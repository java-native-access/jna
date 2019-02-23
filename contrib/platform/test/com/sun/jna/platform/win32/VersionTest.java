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
package com.sun.jna.platform.win32;

import java.io.File;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

import junit.framework.TestCase;

public class VersionTest extends TestCase {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(VersionTest.class);
    }

    public void testGetFileVersion() {
        String systemRoot = System.getenv("SystemRoot");
        assertNotNull("Missing system root environment variable", systemRoot);
        File file = new File(systemRoot + File.separator + "regedit.exe");
        if (!file.exists()) {
            fail("Can't obtain file version, file " + file + " is missing");
        }

        String filePath = file.getAbsolutePath();
        int size = Version.INSTANCE.GetFileVersionInfoSize(filePath, null);
        assertTrue("GetFileVersionInfoSize(" + filePath + ")", size > 0);

        Pointer buffer = Kernel32.INSTANCE.LocalAlloc(WinBase.LMEM_ZEROINIT, size);
        assertTrue("LocalAlloc(" + size + ")", !buffer.equals(Pointer.NULL));

        try {
            assertTrue("GetFileVersionInfo(" + filePath + ")",
                    Version.INSTANCE.GetFileVersionInfo(filePath, 0, size, buffer));

            IntByReference outputSize = new IntByReference();
            PointerByReference pointer = new PointerByReference();

            assertTrue("VerQueryValue",
                    Version.INSTANCE.VerQueryValue(buffer, "\\", pointer, outputSize));

            VerRsrc.VS_FIXEDFILEINFO fixedFileInfo =
                    new VerRsrc.VS_FIXEDFILEINFO(pointer.getValue());
            assertTrue("dwFileVersionLS", fixedFileInfo.dwFileVersionLS.longValue() > 0);
            assertTrue("dwFileVersionMS", fixedFileInfo.dwFileVersionMS.longValue() > 0);
        } finally {
            Kernel32Util.freeGlobalMemory(buffer);
        }
    }
}
