/* Copyright (c) 2007-2013 Timothy Wall, All Rights Reserved
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
package com.sun.jna.win32;

import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;

public class W32APIMapperTest extends TestCase {

    final String UNICODE = "[\u0444]";
    final String MAGIC = "magic" + UNICODE;

    public static void main(String[] args) {
        junit.textui.TestRunner.run(W32APIMapperTest.class);
    }

    public interface UnicodeLibrary extends Library {
        public static class TestStructure extends Structure {
            public static final List<String> FIELDS = createFieldsOrder("string", "string2", "bool", "bool2");
            public String string;
            public String string2;
            public boolean bool;
            public boolean bool2;
            @Override
            protected List<String> getFieldOrder() {
                return FIELDS;
            }
        }
        String returnWStringArgument(String arg);
        boolean returnInt32Argument(boolean arg);
        String returnWideStringArrayElement(String[] args, int which);
    }
    public interface ASCIILibrary extends Library {
        public static class TestStructure extends Structure {
            public static final List<String> FIELDS = Arrays.asList("string", "string2", "bool", "bool2");
            public String string;
            public String string2;
            public boolean bool;
            public boolean bool2;
            @Override
            protected List<String> getFieldOrder() {
                return FIELDS;
            }
        }
        String returnStringArgument(String arg);
        boolean returnInt32Argument(boolean arg);
    }

    UnicodeLibrary unicode;
    ASCIILibrary ascii;

    @Override
    protected void setUp() {
        unicode = Native.loadLibrary("testlib", UnicodeLibrary.class, W32APIOptions.UNICODE_OPTIONS);
        ascii = Native.loadLibrary("testlib", ASCIILibrary.class, W32APIOptions.ASCII_OPTIONS);
    }

    @Override
    protected void tearDown() {
        unicode = null;
        ascii = null;
    }

    public void testInvalidHandleValue() {
        String EXPECTED = "@0xffffffff";
        if (Pointer.SIZE == 8) {
            EXPECTED += "ffffffff";
        }
        Pointer p = Pointer.createConstant(Pointer.SIZE == 8 ? -1 : 0xFFFFFFFFL);
        assertTrue("Wrong value: " + p, p.toString().endsWith(EXPECTED));

    }

    public void testBooleanArgumentConversion() {
        assertTrue("Wrong boolean TRUE argument conversion (unicode)",
                   unicode.returnInt32Argument(true));
        assertFalse("Wrong boolean FALSE argument conversion (unicode)",
                   unicode.returnInt32Argument(false));

        assertTrue("Wrong boolean TRUE argument conversion (ASCII)",
                   ascii.returnInt32Argument(true));
        assertFalse("Wrong boolean FALSE argument conversion (ASCII)",
                    ascii.returnInt32Argument(false));
    }

    public void testUnicodeMapping() {
        assertEquals("Strings should correspond to wide strings",
                     MAGIC, unicode.returnWStringArgument(MAGIC));
        String[] args = { "one", "two" };
        assertEquals("String arrays should be converted to wchar_t*[] and back",
                     args[0],
                     unicode.returnWideStringArrayElement(args, 0));
    }

    public void testASCIIMapping() {
        assertEquals("Strings should correspond to C strings",
                     MAGIC, ascii.returnStringArgument(MAGIC));
    }

    public void testUnicodeStructureSize() {
        UnicodeLibrary.TestStructure s = new UnicodeLibrary.TestStructure();
        assertEquals("Wrong structure size",
                     Pointer.SIZE*2+8, s.size());
    }

    public void testASCIIStructureSize() {
        ASCIILibrary.TestStructure s = new ASCIILibrary.TestStructure();
        assertEquals("Wrong structure size",
                     Pointer.SIZE*2+8, s.size());
    }

    public void testUnicodeStructureWriteBoolean() {
        UnicodeLibrary.TestStructure s = new UnicodeLibrary.TestStructure();
        s.bool2 = true;
        s.write();
        assertEquals("Wrong value written for FALSE", 0, s.getPointer().getInt(Pointer.SIZE*2));
        assertEquals("Wrong value written for TRUE", 1, s.getPointer().getInt(Pointer.SIZE*2+4));
    }
    public void testASCIIStructureWriteBoolean() {
        ASCIILibrary.TestStructure s = new ASCIILibrary.TestStructure();
        s.bool2 = true;
        s.write();
        assertEquals("Wrong value written for FALSE", 0, s.getPointer().getInt(Pointer.SIZE*2));
        assertEquals("Wrong value written for TRUE", 1, s.getPointer().getInt(Pointer.SIZE*2+4));
    }
    public void testUnicodeStructureReadBoolean() {
        UnicodeLibrary.TestStructure s = new UnicodeLibrary.TestStructure();
        s.getPointer().setInt(Pointer.SIZE*2, 1);
        s.getPointer().setInt(Pointer.SIZE*2+4, 0);
        s.read();
        assertTrue("Wrong value read for TRUE", s.bool);
        assertFalse("Wrong value read for FALSE", s.bool2);
    }
    public void testASCIIStructureReadBoolean() {
        ASCIILibrary.TestStructure s = new ASCIILibrary.TestStructure();
        s.getPointer().setInt(Pointer.SIZE*2, 1);
        s.getPointer().setInt(Pointer.SIZE*2+4, 0);
        s.read();
        assertTrue("Wrong value read for TRUE", s.bool);
        assertFalse("Wrong value read for FALSE", s.bool2);
    }
    public void testUnicodeStructureWriteString() {
        UnicodeLibrary.TestStructure s = new UnicodeLibrary.TestStructure();
        s.string = null;
        s.string2 = MAGIC;
        s.write();
        assertEquals("Improper null write", null, s.getPointer().getPointer(0));
        assertEquals("Improper string write", MAGIC, s.getPointer().getPointer(Pointer.SIZE).getWideString(0));
    }
    public void testASCIIStructureWriteString() {
        ASCIILibrary.TestStructure s = new ASCIILibrary.TestStructure();
        s.string = null;
        s.string2 = MAGIC;
        s.write();
        assertEquals("Improper null write", null, s.getPointer().getPointer(0));
        assertEquals("Improper string write", MAGIC, s.getPointer().getPointer(Pointer.SIZE).getString(0));
    }
    public void testUnicodeStructureReadString() {
        UnicodeLibrary.TestStructure s = new UnicodeLibrary.TestStructure();
        s.string = MAGIC;
        s.string2 = null;
        s.write();
        s.read();
        assertEquals("Improper string read", MAGIC, s.string);
        assertEquals("Improper null string read", null, s.string2);
    }
    public void testASCIIStructureReadString() {
        ASCIILibrary.TestStructure s = new ASCIILibrary.TestStructure();
        s.string = MAGIC;
        s.string2 = null;
        s.write();
        s.read();
        assertEquals("Improper string read", MAGIC, s.string);
        assertEquals("Improper null string read: " + s, null, s.string2);
    }
}
