/* Copyright (c) 2007 Timothy Wall, All Rights Reserved
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.  
 */
package com.sun.jna.examples.win32;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Structure;
import com.sun.jna.examples.win32.W32API;
import junit.framework.TestCase;

public class W32APIMapperTest extends TestCase {

    final String MAGIC = "magic";
    
    public interface UnicodeLibrary extends Library {
        public static class TestStructure extends Structure {
            public String string;
            public String string2;
            public boolean bool;
            public boolean bool2;
        }
        String returnWStringArgument(String arg);
        boolean returnInt32Argument(boolean arg);
    }
    public interface ASCIILibrary extends Library {
        public static class TestStructure extends Structure {
            public String string;
            public String string2;
            public boolean bool;
            public boolean bool2;
        }
        String returnStringArgument(String arg);
        boolean returnInt32Argument(boolean arg);
    }
    
    UnicodeLibrary unicode;
    ASCIILibrary ascii;
    
    protected void setUp() {
        unicode = (UnicodeLibrary)
            Native.loadLibrary("testlib", UnicodeLibrary.class, 
                               W32API.UNICODE_OPTIONS);
        ascii = (ASCIILibrary)
            Native.loadLibrary("testlib", ASCIILibrary.class, 
                               W32API.ASCII_OPTIONS);
    }
    
    protected void tearDown() {
        unicode = null;
        ascii = null;
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
    }
    
    public void testASCIIMapping() {
        assertEquals("Strings should correspond to C strings",
                     MAGIC, ascii.returnStringArgument(MAGIC));
    }
    
    public void testUnicodeStructureSize() {
        UnicodeLibrary.TestStructure s = new UnicodeLibrary.TestStructure();
        assertEquals("Wrong structure size", 16, s.size());
    }
    
    public void testASCIIStructureSize() {
        ASCIILibrary.TestStructure s = new ASCIILibrary.TestStructure();
        assertEquals("Wrong structure size", 16, s.size());
    }

    public void testUnicodeStructureWriteBoolean() {
        UnicodeLibrary.TestStructure s = new UnicodeLibrary.TestStructure();
        s.bool2 = true;
        s.write();
        assertEquals("Wrong value written for FALSE", 0, s.getPointer().getInt(8));
        assertEquals("Wrong value written for TRUE", 1, s.getPointer().getInt(12));
    }        
    public void testASCIIStructureWriteBoolean() {
        ASCIILibrary.TestStructure s = new ASCIILibrary.TestStructure();
        s.bool2 = true;
        s.write();
        assertEquals("Wrong value written for FALSE", 0, s.getPointer().getInt(8));
        assertEquals("Wrong value written for TRUE", 1, s.getPointer().getInt(12));
    }        
    public void testUnicodeStructureReadBoolean() {
        UnicodeLibrary.TestStructure s = new UnicodeLibrary.TestStructure();
        s.getPointer().setInt(8, 1);
        s.getPointer().setInt(12, 0);
        s.read();
        assertTrue("Wrong value read for TRUE", s.bool);
        assertFalse("Wrong value read for FALSE", s.bool2);
    }    
    public void testASCIIStructureReadBoolean() {
        ASCIILibrary.TestStructure s = new ASCIILibrary.TestStructure();
        s.getPointer().setInt(8, 1);
        s.getPointer().setInt(12, 0);
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
        assertEquals("Improper string write", MAGIC, s.getPointer().getPointer(4).getString(0, true));
    }
    public void testASCIIStructureWriteString() {
        ASCIILibrary.TestStructure s = new ASCIILibrary.TestStructure();
        s.string = null;
        s.string2 = MAGIC;
        s.write();
        assertEquals("Improper null write", null, s.getPointer().getPointer(0));
        assertEquals("Improper string write", MAGIC, s.getPointer().getPointer(4).getString(0, false));
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
        assertEquals("Improper null string read", null, s.string2);
    }
}
