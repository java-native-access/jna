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
    private Thread cleanerThread;
    private CleanerRef firstCleanable;

    private Cleaner() {
        referenceQueue = new ReferenceQueue<Object>();
    }

    public synchronized Cleanable register(Object obj, Runnable cleanupTask) {
        // The important side effect is the PhantomReference, that is yielded
        // after the referent is GCed
        return add(new CleanerRef(this, obj, referenceQueue, cleanupTask));
    }

    private synchronized CleanerRef add(CleanerRef ref) {
        synchronized (referenceQueue) {
            if (firstCleanable == null) {
                firstCleanable = ref;
            } else {
                ref.setNext(firstCleanable);
                firstCleanable.setPrevious(ref);
                firstCleanable = ref;
            }
            if (cleanerThread == null) {
                Logger.getLogger(Cleaner.class.getName()).log(Level.FINE, "Starting CleanerThread");
                cleanerThread = new CleanerThread();
                cleanerThread.start();
            }
            return ref;
        }
    }

    private synchronized boolean remove(CleanerRef ref) {
        synchronized (referenceQueue) {
            boolean inChain = false;
            if (ref == firstCleanable) {
                firstCleanable = ref.getNext();
                inChain = true;
            }
            if (ref.getPrevious() != null) {
                ref.getPrevious().setNext(ref.getNext());
            }
            if (ref.getNext() != null) {
                ref.getNext().setPrevious(ref.getPrevious());
            }
            if (ref.getPrevious() != null || ref.getNext() != null) {
                inChain = true;
            }
            ref.setNext(null);
            ref.setPrevious(null);
            return inChain;
        }
    }

    private static class CleanerRef extends PhantomReference<Object> implements Cleanable {
        private final Cleaner cleaner;
        private final Runnable cleanupTask;
        private CleanerRef previous;
        private CleanerRef next;

        public CleanerRef(Cleaner cleaner, Object referent, ReferenceQueue<? super Object> q, Runnable cleanupTask) {
            super(referent, q);
            this.cleaner = cleaner;
            this.cleanupTask = cleanupTask;
        }

        @Override
        public void clean() {
            if(cleaner.remove(this)) {
                cleanupTask.run();
            }
        }

        CleanerRef getPrevious() {
            return previous;
        }

        void setPrevious(CleanerRef previous) {
            this.previous = previous;
        }

        CleanerRef getNext() {
            return next;
        }

        void setNext(CleanerRef next) {
            this.next = next;
        }
    }

    public static interface Cleanable {
        public void clean();
    }

    private class CleanerThread extends Thread {

        private static final long CLEANER_LINGER_TIME = 30000;

        public CleanerThread() {
            super("JNA Cleaner");
            setDaemon(true);
        }

        @Override
        public void run() {
            while (true) {
                try {
                    Reference<? extends Object> ref = referenceQueue.remove(CLEANER_LINGER_TIME);
                    if (ref instanceof CleanerRef) {
                        ((CleanerRef) ref).clean();
                    } else if (ref == null) {
                        synchronized (referenceQueue) {
                            Logger logger = Logger.getLogger(Cleaner.class.getName());
                            if (firstCleanable == null) {
                                cleanerThread = null;
                                logger.log(Level.FINE, "Shutting down CleanerThread");
                                break;
                            } else if (logger.isLoggable(Level.FINER)) {
                                StringBuilder registeredCleaners = new StringBuilder();
                                for(CleanerRef cleanerRef = firstCleanable; cleanerRef != null; cleanerRef = cleanerRef.next) {
                                    if(registeredCleaners.length() != 0) {
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
                    break;
                } catch (Exception ex) {
                    Logger.getLogger(Cleaner.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
}
