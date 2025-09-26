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
import java.util.concurrent.TimeUnit;
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
    private static final Cleaner INSTANCE = new Cleaner();
    private static final Logger logger = Logger.getLogger(Cleaner.class.getName());
    private static final long CLEANER_LINGER_TIME = TimeUnit.SECONDS.toMillis(30);

    public static Cleaner getCleaner() {
        return INSTANCE;
    }

    private final ReferenceQueue<Object> referenceQueue;
    private boolean cleanerRunning;
    private CleanerRef firstCleanable;

    private Cleaner() {
        referenceQueue = new ReferenceQueue<>();
    }

    public synchronized Cleanable register(final Object obj, final Runnable cleanupTask) {
        // The important side effect is the PhantomReference, that is yielded after the referent is GCed
        final CleanerRef ref = new CleanerRef(obj, referenceQueue, cleanupTask);

        if (firstCleanable != null) {
            ref.setNext(firstCleanable);
            firstCleanable.setPrevious(ref);
        }
        firstCleanable = ref;

        if (!cleanerRunning) {
            logger.log(Level.FINE, "Starting CleanerThread");
            Thread cleanerThread = new CleanerThread();
            cleanerThread.start();
            cleanerRunning = true;
        }

        return ref;
    }

    private synchronized boolean remove(final CleanerRef ref) {
        final CleanerRef prev = ref.getPrevious();
        final CleanerRef next = ref.getNext();
        boolean inChain = false;

        if (ref == firstCleanable) {
            firstCleanable = next;
            inChain = true;
        }
        if (prev != null) {
            prev.setNext(next);
            inChain = true;
        }
        if (next != null) {
            next.setPrevious(prev);
            inChain = true;
        }
        return inChain;
    }

    private static class CleanerRef extends PhantomReference<Object> implements Cleanable {
        private final Runnable cleanupTask;
        private CleanerRef previous;
        private CleanerRef next;

        public CleanerRef(final Object referent, final ReferenceQueue<? super Object> queue, final Runnable cleanupTask) {
            super(referent, queue);
            this.cleanupTask = cleanupTask;
        }

        @Override
        public void clean() {
            if (INSTANCE.remove(this)) {
                previous = null;
                next = null;
                cleanupTask.run();
            }
        }

        CleanerRef getPrevious() {
            return previous;
        }

        void setPrevious(final CleanerRef previous) {
            this.previous = previous;
        }

        CleanerRef getNext() {
            return next;
        }

        void setNext(final CleanerRef next) {
            this.next = next;
        }
    }

    public interface Cleanable {
        void clean();
    }

    private class CleanerThread extends Thread {

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
                        synchronized (INSTANCE) {
                            if (firstCleanable == null) {
                                logger.log(Level.FINE, "Shutting down CleanerThread");
                                cleanerRunning = false;
                                break;
                            } else if (logger.isLoggable(Level.FINER)) {
                                StringBuilder registeredCleaners = new StringBuilder();
                                for (CleanerRef cleanerRef = firstCleanable; cleanerRef != null; cleanerRef = cleanerRef.next) {
                                    if (registeredCleaners.length() != 0) {
                                        registeredCleaners.append(", ");
                                    }
                                    registeredCleaners.append(cleanerRef.cleanupTask.toString());
                                }
                                logger.log(Level.FINER, "Registered Cleaners: {0}", registeredCleaners.toString());
                            }
                        }
                    }
                } catch (InterruptedException ex) {
                    // Can be raised on shutdown. If anyone else messes with
                    // our reference queue, well, there is no way to separate
                    // the two cases.
                    // https://groups.google.com/g/jna-users/c/j0fw96PlOpM/m/vbwNIb2pBQAJ
                    synchronized (INSTANCE) {
                        cleanerRunning = false;
                    }
                    break;
                } catch (Exception ex) {
                    Logger.getLogger(Cleaner.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
}
