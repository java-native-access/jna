/* Copyright (c) 2021, Matthias Bläsing, All Rights Reserved
 *
 * The contents of this file is dual-licensed under 2
 * alternative Open Source/Free licenses: LGPL 2.1 or later and
 * Apache License 2.0. (starting with JNA version 4.0.0).
 *
 * You can freely decide which license you want to apply to
 * the project.
 *
 * You may obtain a copy of the LGPL License at:
 *
 * http://www.gnu.org/licenses/licenses.html
 *
 * A copy is also included in the downloadable source code package
 * containing JNA, in file "LGPL2.1".
 *
 * You may obtain a copy of the Apache License at:
 *
 * http://www.apache.org/licenses/
 *
 * A copy is also included in the downloadable source code package
 * containing JNA, in file "AL2.0".
 */

package com.sun.jna.internal;

import java.lang.ref.PhantomReference;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implement ReferenceQueue based cleanup of resources associated with GCed
 * objects. It replaces the {@code Object#finalize} based resource deallocation
 * that is deprecated for removal from the JDK.
 *
 * <p><strong>This class is intended to be used only be JNA itself.</strong></p>
 */
public class Cleaner {
    /* General idea:
     *
     * There's one Cleaner per thread, kept in a ThreadLocal static variable.
     * This instance handles all to-be-cleaned objects registered by this
     * thread. Whenever the thread registers another object, it first checks
     * if there are references in the queue and cleans them up, then continues
     * with the registration.
     *
     * This leaves two cases open, for which we employ a "Master Cleaner" and
     * a separate cleaning thread.
     * 1. If a long-lived thread registers some objects in the beginning, but
     *    then stops registering more objects, the previously registered
     *    objects will never be cleared.
     * 2. When a thread exits before all its registered objects have been
     *    cleared, the ThreadLocal instance is lost, and so are the pending
     *    objects.
     *
     * The Master Cleaner handles the first issue by regularly handling the
     * queues of the Cleaners registered with it.
     * The seconds issue is handled by registering the per-thread Cleaner
     * instances with the Master's reference queue.
     */

    public static final long MASTER_CLEANUP_INTERVAL_MS = 5000;
    public static final long MASTER_MAX_LINGER_MS = 30000;

    private static class CleanerImpl {
        protected final ReferenceQueue<Object> referenceQueue = new ReferenceQueue<Object>();
        protected final Map<Long,CleanerRef> cleanables = new ConcurrentHashMap<Long,CleanerRef>();
        private final AtomicBoolean lock = new AtomicBoolean(false);

        private void cleanQueue() {
            if (lock.compareAndSet(false, true)) {
                try {
                    Reference<?> ref;
                    while ((ref = referenceQueue.poll()) != null) {
                        try {
                            if (ref instanceof Cleanable) {
                                ((Cleanable) ref).clean();
                            }
                        } catch (RuntimeException ex) {
                            Logger.getLogger(Cleaner.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                } finally {
                    lock.set(false);
                }
            }
        }

        public Cleanable register(Object obj, Runnable cleanupTask) {
            cleanQueue();
            // The important side effect is the PhantomReference, that is yielded
            // after the referent is GCed
            return new CleanerRef(this, obj, referenceQueue, cleanupTask);
        }

        protected void put(long n, CleanerRef ref) {
            cleanables.put(n, ref);
        }

        protected boolean remove(long n) {
            return cleanables.remove(n) != null;
        }
    }

    static class MasterCleaner extends Cleaner {
        static MasterCleaner INSTANCE;

        public static synchronized void add(Cleaner cleaner) {
            if (INSTANCE == null) {
                INSTANCE = new MasterCleaner();
            }
            final CleanerImpl impl = cleaner.impl;
            INSTANCE.cleanerImpls.put(impl, true);
            INSTANCE.register(cleaner, () -> INSTANCE.cleanerImpls.put(impl, false));
        }

        private static synchronized boolean deleteIfEmpty(MasterCleaner caller) {
            if (INSTANCE == caller && INSTANCE.cleanerImpls.isEmpty()) {
                INSTANCE = null;
            }
            return caller.cleanerImpls.isEmpty();
        }

        final Map<CleanerImpl,Boolean> cleanerImpls = new ConcurrentHashMap<CleanerImpl,Boolean>();
        private long lastNonEmpty = System.currentTimeMillis();

        private MasterCleaner() {
            super(true);
            Thread cleanerThread = new Thread(() -> {
                    long now;
                    long lastMasterRun = 0;
                    while ((now = System.currentTimeMillis()) < lastNonEmpty + MASTER_MAX_LINGER_MS || !deleteIfEmpty(MasterCleaner.this)) {
                        if (!cleanerImpls.isEmpty()) { lastNonEmpty = now; }
                        try {
                            Reference<?> ref = impl.referenceQueue.remove(MASTER_CLEANUP_INTERVAL_MS);
                            if(ref instanceof CleanerRef) {
                                ((CleanerRef) ref).clean();
                            }
                            // "now" is not really *now* at this point, but off by no more than MASTER_CLEANUP_INTERVAL_MS
                            if (lastMasterRun + MASTER_CLEANUP_INTERVAL_MS <= now) {
                                masterCleanup();
                                lastMasterRun = now;
                            }
                        } catch (InterruptedException ex) {
                            // Can be raised on shutdown. If anyone else messes with
                            // our reference queue, well, there is no way to separate
                            // the two cases.
                            // https://groups.google.com/g/jna-users/c/j0fw96PlOpM/m/vbwNIb2pBQAJ
                            break;
                        } catch (Exception ex) {
                            Logger.getLogger(Cleaner.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }, "JNA Cleaner");
            cleanerThread.setDaemon(true);
            cleanerThread.start();
        }

        private void masterCleanup() {
            Iterator<Map.Entry<CleanerImpl,Boolean>> it = cleanerImpls.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<CleanerImpl,Boolean> entry = it.next();
                entry.getKey().cleanQueue();
                if (!entry.getValue() && entry.getKey().cleanables.isEmpty()) {
                    it.remove();
                }
            }
        }
    }

    private static final ThreadLocal<Cleaner> MY_INSTANCE = ThreadLocal.withInitial(() -> new Cleaner(false));

    public static Cleaner getCleaner() {
        return MY_INSTANCE.get();
    }

    protected final CleanerImpl impl;

    private Cleaner(boolean master) {
        impl = new CleanerImpl();
        if (!master) {
            MasterCleaner.add(this);
        }
    }

    public Cleanable register(Object obj, Runnable cleanupTask) {
        return impl.register(obj, cleanupTask);
    }

    private static class CleanerRef extends PhantomReference<Object> implements Cleanable {
        private static final AtomicLong COUNTER = new AtomicLong(Long.MIN_VALUE);

        private final CleanerImpl cleaner;
        private final long number = COUNTER.incrementAndGet();
        private Runnable cleanupTask;

        public CleanerRef(CleanerImpl impl, Object referent, ReferenceQueue<Object> q, Runnable cleanupTask) {
            super(referent, q);
            this.cleaner = impl;
            this.cleanupTask = cleanupTask;
            cleaner.put(number, this);
        }

        @Override
        public void clean() {
            if(cleaner.remove(this.number) && cleanupTask != null) {
                cleanupTask.run();
                cleanupTask = null;
            }
        }
    }

    public interface Cleanable {
        void clean();
    }
}
