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
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implement ReferenceQueue based cleanup of resources associated with GCed
 * objects. It replaces the {@code Object#finalize} based resource deallocation
 * that is deprecated for removal from the JDK.
 *
 * <p><strong>This class is intented to be used only be JNA itself.</strong></p>
 */
public class Cleaner {
    private static final Cleaner INSTANCE = new Cleaner();

    public static Cleaner getCleaner() {
        return INSTANCE;
    }

    private final ReferenceQueue<Object> referenceQueue;
    private final Set<CleanerRef> uncleaned;
    private final AtomicBoolean cleanerRunning;

    private Cleaner() {
        referenceQueue = new ReferenceQueue<>();
        uncleaned = ConcurrentHashMap.newKeySet();
        cleanerRunning = new AtomicBoolean(false);
    }

    public Cleanable register(Object referent, Runnable cleanupTask) {
        // The important side effect is the PhantomReference, that is yielded
        // after the referent is GCed
        Cleanable cleanable = add(new CleanerRef(referent, referenceQueue, cleanupTask));

        if (cleanerRunning.compareAndSet(false, true)) {
            Logger.getLogger(Cleaner.class.getName()).log(Level.FINE, "Starting CleanerThread");
            Thread cleanerThread = new CleanerThread();
            cleanerThread.start();
        }

        // NOTE: This is a "pointless" check in the conventional sense, however it serves to guarantee that the
        // referent is not garbage collected before the CleanerRef is fully constructed which can happen due
        // to reordering of instructions by the compiler or the CPU. In Java 9+ Reference.reachabilityFence() was
        // introduced to provide this guarantee, but we want to stay compatible with Java 8, so this is the common
        // idiom to achieve the same effect, by ensuring that the referent is still strongly reachable at
        // this point.
        if (referent == null) {
            throw new IllegalArgumentException("The referent object must not be null");
        }

        return cleanable;
    }

    private CleanerRef add(final CleanerRef toAdd) {
        uncleaned.add(toAdd);
        return toAdd;
    }

    // Remove by node reference
    private boolean remove(final CleanerRef node) {
        return uncleaned.remove(node);
    }

    private static class CleanerRef extends PhantomReference<Object> implements Cleanable {
        private volatile Runnable cleanupTask;

        CleanerRef(Object referent, ReferenceQueue<? super Object> q, Runnable cleanupTask) {
            super(referent, q);
            this.cleanupTask = cleanupTask;
        }

        @Override
        public void clean() {
            if (INSTANCE.remove(this)) {
                cleanupTask.run();
            }
        }
    }

    public interface Cleanable {
        void clean();
    }

    private class CleanerThread extends Thread {

        private final long CLEANER_LINGER_TIME = TimeUnit.SECONDS.toMillis(30L);

        public CleanerThread() {
            super("JNA Cleaner");
            setDaemon(true);
        }

        @Override
        public void run() {
            while (true) {
                try {
                    Reference<?> ref = referenceQueue.remove(CLEANER_LINGER_TIME);
                    if (ref instanceof CleanerRef) {
                        ((CleanerRef) ref).clean();
                    } else if (ref == null) {
                        Logger logger = Logger.getLogger(Cleaner.class.getName());
                        if (cleanerRunning.compareAndSet(uncleaned.isEmpty(), false)) {
                            logger.log(Level.FINE, "Shutting down CleanerThread");
                            break;
                        } else if (logger.isLoggable(Level.FINER)) {
                            StringBuilder registeredCleaners = new StringBuilder();
                            uncleaned.forEach((cleanerRef) -> {
                                if (registeredCleaners.length() != 0) {
                                    registeredCleaners.append(", ");
                                }
                                registeredCleaners.append(cleanerRef.cleanupTask.toString());
                            });
                            logger.log(Level.FINER, "Registered Cleaners: {0}", registeredCleaners.toString());
                        }
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
        }
    }
}
