/*
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
package com.sun.jna;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.IdentityHashMap;

/**
 * Helper to hold a memory object based on the lifetime of another object.
 *
 * The intended use is to assoziate a ByteBuffer with its backing Memory object.
 *
 * The ByteBuffer is held by a WeakReference and a ReferenceQueue is used to
 * track GC of the ByteBuffer.
 *
 * The references to the memory objects are released on access of WeakMemoryHolder.
 */
public class WeakMemoryHolder {
    ReferenceQueue<Object> referenceQueue = new ReferenceQueue<Object>();
    IdentityHashMap<Reference<Object>, Memory> backingMap = new IdentityHashMap<Reference<Object>, Memory>();

    public synchronized void put(Object o, Memory m) {
        clean();
        Reference<Object> reference = new WeakReference<Object>(o, referenceQueue);
        backingMap.put(reference, m);
    }

    public synchronized void clean() {
        for(Reference ref = referenceQueue.poll(); ref != null; ref = referenceQueue.poll()) {
            backingMap.remove(ref);
        }
    }
}
