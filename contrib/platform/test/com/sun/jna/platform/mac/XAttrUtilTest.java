/* Copyright (c) 2014 Reinhard Pointner, All Rights Reserved
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
package com.sun.jna.platform.mac;

import com.sun.jna.Pointer;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import junit.framework.TestCase;

public class XAttrUtilTest extends TestCase {

    String testPath;

    protected void setUp() throws Exception {
        testPath = File.createTempFile("xattr-test", ".txt").getAbsolutePath();
        assertTrue(new File(testPath).exists());
    }

    protected void tearDown() throws Exception {
        new File(testPath).delete();
        assertFalse(new File(testPath).exists());
    }

    public void testListXAttr() {
        // no xattr initially
        List<String> keys = XAttrUtil.listXAttr(testPath);
        assertEquals(0, keys.size());

        // set multiple xattr
        String[] names = new String[]{"Java", "Native", "Access"};
        for (int i = 0; i < names.length; i++) {
            // set xattr
            XAttrUtil.setXAttr(testPath, names[i], names[i]);

            // check if new xattr is listed
            keys = XAttrUtil.listXAttr(testPath);
            assertEquals(i + 1, keys.size());
            assertTrue(keys.contains(names[i]));
        }
    }

    public void testGetXAttr() {
        String value = XAttrUtil.getXAttr(testPath, "JNA");
        assertNull(value);

        XAttrUtil.setXAttr(testPath, "JNA", "Java Native Access");
        value = XAttrUtil.getXAttr(testPath, "JNA");

        assertEquals(Arrays.toString("Java Native Access".getBytes()), Arrays.toString(value.getBytes()));

        XAttr.INSTANCE.setxattr(testPath, "JNA.empty", Pointer.NULL, 0, 0, 0);
        value = XAttrUtil.getXAttr(testPath, "JNA.empty");
        assertEquals("", value);
    }

    public void testSetXAttr() {
        String value = XAttrUtil.getXAttr(testPath, "JNA");
        assertNull(value);

        XAttrUtil.setXAttr(testPath, "JNA", "Java Native Access");
        value = XAttrUtil.getXAttr(testPath, "JNA");
        assertEquals("Java Native Access", value);

        XAttrUtil.setXAttr(testPath, "JNA", "is nice");
        value = XAttrUtil.getXAttr(testPath, "JNA");
        assertEquals("is nice", value);
    }

    public void testRemoveXAttr() {
        XAttrUtil.setXAttr(testPath, "JNA", "Java Native Access");
        assertEquals("[JNA]", XAttrUtil.listXAttr(testPath).toString());

        // remove xattr
        XAttrUtil.removeXAttr(testPath, "JNA");

        assertEquals("[]", XAttrUtil.listXAttr(testPath).toString());
    }

    public void testUnicode() {
        String[] names = new String[]{
            "\u4E2D\u6587",
            "\u306B\u307B\u3093\u3054",
            "\u00D6sterreichisch",
            "Fran\u00E7aise",
            "Portugu\u00EAs",};
        for (int i = 0; i < names.length; i++) {
            // set xattr
            XAttrUtil.setXAttr(testPath, names[i], names[i]);

            // check if new xattr is listed
            List<String> keys = XAttrUtil.listXAttr(testPath);
            assertEquals(i + 1, keys.size());
            assertTrue(keys.contains(names[i]));

            String value = XAttrUtil.getXAttr(testPath, names[i]);
            assertEquals(names[i], value);
        }
    }

    public void testLargeData() {
        StringBuilder name = new StringBuilder();
        while (name.length() < XAttr.XATTR_MAXNAMELEN) {
            name.append('X');
        }

        StringBuilder data = new StringBuilder();
        while (data.length() < 4 * 1024 * 1024) {
            data.append('X');
        }

        XAttrUtil.setXAttr(testPath, name.toString(), data.toString());
        String value = XAttrUtil.getXAttr(testPath, name.toString());
        assertEquals(data.toString(), value.toString());
    }

    public void testReadAlignedCliTool() throws IOException {
        XAttrUtil.setXAttr(testPath, "JNA", "Java Native Access");
        Process p = Runtime.getRuntime().exec(new String[] {"xattr", "-p", "JNA", testPath});
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int read;
        InputStream is = p.getInputStream();
        while(((read = is.read(buffer))) > 0) {
            baos.write(buffer, 0, read);
        }
        String resultString = baos.toString("UTF-8");
        // Trailing new line is added by command
        assertEquals("Java Native Access\n", resultString);
    }

    public void testWriteAlignedCliTool() throws IOException, InterruptedException {
        Process p = Runtime.getRuntime().exec(new String[] {"xattr", "-w", "JNA", "Java Native Access", testPath});
        assertTrue("Wait for CLI xattr call timed out", p.waitFor(60, TimeUnit.SECONDS));
        String resultString = XAttrUtil.getXAttr(testPath, "JNA");
        assertEquals("Java Native Access", resultString);
    }
}
