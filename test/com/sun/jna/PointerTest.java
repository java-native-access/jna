/* Copyright (c) 2007 Wayne Meissner, All Rights Reserved
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

import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;

import junit.framework.TestCase;


public class PointerTest extends TestCase {
    
    public void testGetNativeLong() {
        Memory m = new Memory(8);
        if (NativeLong.SIZE == 4) {
            final int MAGIC = 0xABEDCF23;
            m.setInt(0, MAGIC);
            NativeLong l = m.getNativeLong(0);
            assertEquals("Native long mismatch", MAGIC, l.intValue());
        } else {
            final long MAGIC = 0x1234567887654321L;
            m.setLong(0, MAGIC);
            NativeLong l = m.getNativeLong(0);
            assertEquals("Native long mismatch", MAGIC, l.longValue());
        }
    }
    public void testSetNativeLong() {
        Memory m = new Memory(8);
        if (NativeLong.SIZE == 4) {
            final int MAGIC = 0xABEDCF23;
            m.setNativeLong(0, new NativeLong(MAGIC));
            assertEquals("Native long mismatch", MAGIC, m.getInt(0));
        } else {
            final long MAGIC = 0x1234567887654321L;
            m.setNativeLong(0, new NativeLong(MAGIC));
            assertEquals("Native long mismatch", MAGIC, m.getLong(0));
        }
    }
    public void testSetStringWithEncoding() throws Exception {
        String old = System.getProperty("jna.encoding");
        String VALUE = "\u0444\u0438\u0441\u0432\u0443";
        System.setProperty("jna.encoding", "UTF8");
        try {
            int size = VALUE.getBytes("UTF8").length+1;
            Memory m = new Memory(size);
            m.setString(0, VALUE);
            assertEquals("UTF8 encoding should be double", 
                         VALUE.length() * 2 + 1, size);
            assertEquals("Wrong decoded value", VALUE, m.getString(0));
        }
        finally {
            if (old != null) {
                System.setProperty("jna.encoding", old);
            }
            else {
                Map props = System.getProperties();
                props.remove("jna.encoding");
                Properties newProps = new Properties();
                for (Iterator i = props.entrySet().iterator();i.hasNext();) {
                    Entry e = (Entry)i.next();
                    newProps.setProperty(e.getKey().toString(), e.getValue().toString());
                }
                System.setProperties(newProps);
            }
        }
    }
    
    public static class TestPointerType extends PointerType {
        public TestPointerType() { }
        public TestPointerType(Pointer p) { super(p); }
    }

    public void testSetNativeMapped() {
        Pointer p = new Memory(Pointer.SIZE);
        TestPointerType tp = new TestPointerType(p);

        p.setValue(0, tp, tp.getClass());

        assertEquals("Wrong value written", p, p.getPointer(0));
    }

    public void testGetNativeMapped() {
        Pointer p = new Memory(Pointer.SIZE);
        p.setPointer(0, null);
        Object o = p.getValue(0, TestPointerType.class, null);
        assertNull("Wrong empty value: " + o, o);
        p.setPointer(0, p);
        TestPointerType tp = new TestPointerType(p);
        assertEquals("Wrong value", tp, p.getValue(0, TestPointerType.class, null));
    }

    public void testGetStringArray() {
        Pointer p = new Memory(Pointer.SIZE*3);
        String VALUE1 = getName();
        String VALUE2 = getName() + "2";

        p.setPointer(0, new NativeString(VALUE1).getPointer());
        p.setPointer(Pointer.SIZE, new NativeString(VALUE2).getPointer());
        p.setPointer(Pointer.SIZE*2, null);

        assertEquals("Wrong null-terminated String array",
                     Arrays.asList(new String[] { VALUE1, VALUE2 }),
                     Arrays.asList(p.getStringArray(0)));

        assertEquals("Wrong length-specified String array (1)",
                     Arrays.asList(new String[] { VALUE1 }),
                     Arrays.asList(p.getStringArray(0, 1)));
        assertEquals("Wrong length-specified String array (2)",
                     Arrays.asList(new String[] { VALUE1, VALUE2 }),
                     Arrays.asList(p.getStringArray(0, 2)));
    }

    public void testReadPointerArray() {
        Pointer mem = new Memory(Pointer.SIZE * 2);
        Pointer[] p = new Pointer[2];
        String VALUE1 = getName();

        p[0] = new NativeString(VALUE1).getPointer();
        p[1] = new Memory(1024);
        Pointer[] orig = new Pointer[p.length];
        System.arraycopy(p, 0, orig, 0, p.length);

        mem.write(0, p, 0, p.length);
        mem.read(0, p, 0, p.length);

        assertSame("Pointer object not preserved[0]", orig[0], p[0]);
        assertSame("Pointer object not preserved[1]", orig[1], p[1]);

        mem.setPointer(0, null);
        mem.setPointer(Pointer.SIZE, new Memory(1024));
        mem.read(0, p, 0, p.length);
        assertNull("Pointer element not updated[0]", p[0]);
        assertNotSame("Pointer element not updated[1]", orig[1], p[1]);
    }

    public void testCreateConstantPointer() {
        Pointer p = Pointer.createConstant(0xFFFFFFFF);
        assertEquals("Wrong peer value", p.peer, 0xFFFFFFFF);

        p = Pointer.createConstant(-1);
        assertEquals("Wrong peer value", p.peer, -1);
    }

    public void testReadStringArrayNULLElement() {
        Memory m = new Memory(Pointer.SIZE);
        m.clear();
        String[] arr = m.getStringArray(0, 1);
        assertEquals("Wrong array size", 1, arr.length);
        assertNull("Array element should be null", arr[0]);
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(PointerTest.class);
    }
}
