/* This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
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
