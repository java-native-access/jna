/* Copyright (c) 2007 Timothy Wall, All Rights Reserved
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * <p/>
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.  
 */
package com.sun.jna;

import java.util.Map;
import junit.framework.TestCase;

/** TODO: need more alignment tests, especially platform-specific behavior
 * @author twall@users.sf.net
 */
public class StructureTest extends TestCase {
    
    public static void main(java.lang.String[] argList) {
        junit.textui.TestRunner.run(StructureTest.class);
    }

    public void testSize() throws Exception {
        class TestStructure extends Structure {
            public int field;
        }
        Structure s = new TestStructure();
        assertEquals("Wrong size", 4, s.size());
    }

    public void testAlign() throws Exception {
        class TestStructure extends Structure {
            public byte b;
            public short s;
            public int i;
            public long j;
        }
        Structure s = new TestStructure();
        assertEquals("Wrong size", 16, s.size());
    }
    
    public static class InnerStructure extends Structure {
        public int x, y;
    }
    public void testSizeWithNestedStructure() {
        class TestStructure extends Structure {
            public InnerStructure s1, s2;
            public int after;
        }
        TestStructure s = new TestStructure();
        assertNotNull("Inner structure should be initialized", s.s1);
        assertEquals("Wrong aggregate size", 
                     s.s1.size() + s.s2.size() + 4, s.size());
    }
    
    public void testStructureSizePadding() {
        class TestStructure extends Structure {
            public long bigbits;
            public byte b;
        }
        TestStructure s = new TestStructure();
        assertEquals("Structure should be padded to longest element",
                     16, s.size());
    }
    
    public void testNestArray() throws Exception {
        class TestStructure extends Structure {
            public byte[] buffer = new byte[1024];
        }
        TestStructure s = new TestStructure();
        assertEquals("Wrong size for nested array", 1024, s.size());
        assertNotNull("Array should be initialized", s.buffer);
    }

    public void testReadWriteStructure() {
        class TestStructure extends Structure {
            public InnerStructure s1;
            public byte[] buffer = new byte[8];
        }
        TestStructure s = new TestStructure();
        s.s1.x = 1;
        s.s1.y = 2;
        s.buffer[0] = 3;
        s.write();
        s.s1.x = s.s1.y = 0;
        s.buffer[0] = 0;
        s.read();
        assertEquals("Wrong nested struct field value after write/read (x)",
                     1, s.s1.x);
        assertEquals("Wrong nested struct field value after write/read (y)",
                     2, s.s1.y);
        assertEquals("Wrong nested array element value after write/read",
                     3, s.buffer[0]);
    }
    
    public static class TestStructure extends Structure {
        public int value;
    }
    public void testToArray() {
        TestStructure s = new TestStructure();
        TestStructure[] array = (TestStructure[])s.toArray(new TestStructure[1]);
        assertEquals("Array should consist of a single element",
                     1, array.length);
        assertEquals("First element should be original", s, array[0]);
        try {
            s.toArray(2);
            fail("Should throw exception on attempts to exceed allocated bounds");
        }
        catch(IndexOutOfBoundsException e) {
        }
    }
    
    static class CbStruct extends Structure {
        public Callback cb;
    }
    static interface CbTest extends Library {
        CbTest INSTANCE = (CbTest)
            Native.loadLibrary("testlib", CbTest.class);
        public void callCallbackInStruct(CbStruct cbstruct);
    }
    public void testCallbackWrite() {
        final CbStruct s = new CbStruct();
        s.cb = new Callback() {
            public void callback() {
            }
        };
        s.write();
        Pointer func = s.getPointer().getPointer(0);
        assertNotNull("Callback trampoline not set", func);
        Map refs = CallbackReference.callbackMap;
        assertTrue("Callback not cached", refs.containsKey(s.cb));
        CallbackReference ref = (CallbackReference)refs.get(s.cb);
        assertEquals("Wrong trampoline", ref.getTrampoline(), func);
    }
    
    public void testCallCallbackInStructure() {
        final boolean[] flag = {false};
        final CbStruct s = new CbStruct();
        s.cb = new Callback() {
            public void callback() {
                flag[0] = true;
            }
        };
        CbTest.INSTANCE.callCallbackInStruct(s);
        assertTrue("Callback not invoked", flag[0]);
    }
}