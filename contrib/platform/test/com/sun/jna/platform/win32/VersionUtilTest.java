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
