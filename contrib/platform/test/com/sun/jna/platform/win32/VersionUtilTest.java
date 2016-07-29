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

import com.sun.jna.platform.win32.VerRsrc.VS_FIXEDFILEINFO;

import junit.framework.TestCase;

public class VersionUtilTest extends TestCase {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(VersionUtilTest.class);
    }

    public void testGetFileVersionNumbers() {
        File file = new File(System.getenv("SystemRoot"), "regedit.exe");
        assertTrue("Test file with version info in it should exist: " + file, file.exists());

        VS_FIXEDFILEINFO version = VersionUtil.getFileVersionInfo(file.getAbsolutePath());
        assertNotNull("Version info should have been returned.", version);

        assertTrue("The major file version number should be greater than 0 when pulling version from \"" + file + "\"", version.getFileVersionMajor() > 0);
        assertTrue("The minor file version number should be greater than or equal to 0 when pulling version from \"" + file + "\"", version.getFileVersionMinor() >= 0);
        assertTrue("The revision file version number should be greater than or equal to 0 when pulling version from \"" + file + "\"", version.getFileVersionRevision() >= 0);
        assertTrue("The build file version number should be greater than or equal to 0  when pulling version from \"" + file + "\"", version.getFileVersionBuild() >= 0);

        assertTrue("The major product version number should be greater than 0 when pulling version from \"" + file + "\"", version.getProductVersionMajor() > 0);
        assertTrue("The minor product version number should be greater than or equal to 0 when pulling version from \"" + file + "\"", version.getProductVersionMinor() >= 0);
        assertTrue("The revision product version number should be greater than or equal to 0 when pulling version from \"" + file + "\"", version.getProductVersionRevision() >= 0);
        assertTrue("The build product version number should be greater than or equal to 0  when pulling version from \"" + file + "\"", version.getProductVersionBuild() >= 0);
    }
}
