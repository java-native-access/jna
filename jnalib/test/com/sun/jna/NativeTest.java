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
package com.sun.jna;

import java.util.Properties;
import junit.framework.TestCase;

public class NativeTest extends TestCase {
    
    public void testDefaultStringEncoding() throws Exception {
        String encoding = System.getProperty("file.encoding");
        // Keep stuff within the extended ASCII range so we work with more
        // limited native encodings
        String unicode = "Un \u00e9l\u00e9ment gr\u00e2ce \u00e0 l'index";
        String unicodez = "Un \u00e9l\u00e9ment gr\u00e2ce \u00e0 l'index\0more stuff";
        byte[] defaultEncoded = Native.getBytes(unicode);
        byte[] expected = unicode.getBytes();
        for (int i=0;i < Math.min(defaultEncoded.length, expected.length);i++) {
            assertEquals("Improperly encoded (" + encoding + ") from Java at " + i, 
                         expected[i], defaultEncoded[i]);
        }
        assertEquals("Wrong number of encoded characters (" + encoding + ")", 
                     expected.length, defaultEncoded.length);
        String result = Native.toString(defaultEncoded);
        assertEquals("Improperly decoded from native bytes (" + encoding + ")", 
                     unicode, result);
        
        assertEquals("Should truncate bytes at NUL terminator",
                     unicode, Native.toString(unicodez.getBytes()));
    }
    
    public void testCustomStringEncoding() throws Exception {
        Properties oldprops = (Properties)System.getProperties().clone();
        try {
            String encoding = "UTF8";
            System.setProperty("jna.encoding", encoding);
            String unicode = "\u81ff\u81fe\u4404\u3804\u4104\u3204\u4304";
            String unicodez = "\u81ff\u81fe\u4404\u3804\u4104\u3204\u4304\0more stuff";
            byte[] utf8 = Native.getBytes(unicode);
            byte[] expected = unicode.getBytes(encoding);
            for (int i=0;i < Math.min(utf8.length, expected.length);i++) {
                assertEquals("Improperly encoded at " + i, 
                             expected[i], utf8[i]);
            }
            assertEquals("Wrong number of encoded characters", expected.length, utf8.length);
            String result = Native.toString(utf8);
            assertEquals("Improperly decoded", unicode, result);
            
            assertEquals("Should truncate bytes at NUL terminator",
                         unicode, Native.toString(unicodez.getBytes(encoding)));
        }
        finally {
            System.setProperties(oldprops);
        }
    }
    
    public static interface TestLib extends Library {
        interface VoidCallback extends Callback {
            void callback();
        }
        void callVoidCallback(VoidCallback callback);
    }
    public void testSynchronizedAccess() throws Exception {
        final boolean[] lockHeld = { false };
        TestLib base = (TestLib)Native.loadLibrary("testlib", TestLib.class);
        final TestLib lib = (TestLib)Native.synchronizedLibrary(base); 
        final TestLib.VoidCallback cb = new TestLib.VoidCallback() {
            public void callback() {
                lockHeld[0] = Thread.holdsLock(NativeLibrary.getInstance("testlib"));
            }
        };
        Thread t1 = new Thread() {
            public void run() {
                lib.callVoidCallback(cb);
            }
        };
        t1.start();
        t1.join();
        assertTrue("NativeLibrary lock should be held during native call",
                   lockHeld[0]);
    }
    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(NativeTest.class);
    }
}
