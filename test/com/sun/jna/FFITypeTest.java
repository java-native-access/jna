/* Copyright (c) 2026 arimu1, All Rights Reserved
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
package com.sun.jna;

import java.lang.reflect.Field;

import com.sun.jna.Structure.FFIType;
import com.sun.jna.Structure.FieldOrder;

import junit.framework.TestCase;

/**
 * {@code FFIType} descriptors are cached by Class in {@code typeInfoMap}.
 * That cache is intentional (native cif pointers must remain valid), but the
 * backing native memory must not be registered with the JNA Cleaner, or the
 * cleaner thread never exits for ordinary application classes. See
 * https://github.com/java-native-access/jna/issues/1633
 */
public class FFITypeTest extends TestCase {

    @FieldOrder({ "field" })
    public static class NativeWrapper extends Structure {
        public boolean field;
    }

    public void testStructureFfiTypeDoesNotRegisterCleaner() throws Exception {
        NativeWrapper nw = new NativeWrapper();
        FFIType ffi = Structure.getTypeInfo(nw);

        Field cleanable = Memory.class.getDeclaredField("cleanable");
        cleanable.setAccessible(true);

        Pointer backing = ffi.getPointer();
        assertTrue("FFIType should allocate Memory", backing instanceof Memory);
        assertTrue("Class-cached FFIType should use TypeInfoMemory",
            backing instanceof FFIType.TypeInfoMemory);
        assertNull("Class-cached FFIType memory must not register with Cleaner",
            cleanable.get(backing));

        assertNotNull("FFIType.elements should be allocated", ffi.elements);
        assertTrue("FFIType.elements should allocate Memory",
            ffi.elements instanceof Memory);
        assertTrue("Class-cached FFIType.elements should use TypeInfoMemory",
            ffi.elements instanceof FFIType.TypeInfoMemory);
        assertNull("Class-cached FFIType.elements must not register with Cleaner",
            cleanable.get(ffi.elements));
    }

    public void testFfiTypeSizeMatchesStructure() {
        NativeWrapper nw = new NativeWrapper();
        Pointer p = Structure.getTypeInfo(nw).getPointer();
        assertEquals("libffi should accept the cached FFIType",
            nw.size(), Native.initialize_ffi_type(p.peer));
    }

    public void testStructureFfiTypeRemainsCachedAfterInstanceGc() {
        NativeWrapper nw = new NativeWrapper();
        Pointer typeInfo = Structure.getTypeInfo(nw).getPointer();
        nw = null;
        System.gc();
        Memory.purge();

        FFIType again = Structure.getTypeInfo(new NativeWrapper());
        assertSame("typeInfoMap should keep the Class-cached FFIType",
            typeInfo, again.getPointer());
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(FFITypeTest.class);
    }
}
