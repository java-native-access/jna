/* Copyright (c) 2010, 2013 Daniel Doubrovkine, Markus Karg, All Rights Reserved
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
package com.sun.jna.platform.win32;

import static org.junit.Assert.assertArrayEquals;

import junit.framework.TestCase;

/**
 * @author dblock[at]dblock[dot]org
 * @author markus[at]headcrashing[dot]eu
 */
public class Shell32UtilTest extends TestCase {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(Shell32UtilTest.class);
        System.out.println("Windows: " + Shell32Util.getFolderPath(ShlObj.CSIDL_WINDOWS));
        System.out.println(" System: " + Shell32Util.getFolderPath(ShlObj.CSIDL_SYSTEM));
        System.out.println("AppData: " + Shell32Util.getFolderPath(ShlObj.CSIDL_APPDATA));
        System.out.println("AppData: " + Shell32Util.getSpecialFolderPath(ShlObj.CSIDL_APPDATA, false));
    }

    public void testGetFolderPath() {
        assertTrue(Shell32Util.getFolderPath(ShlObj.CSIDL_WINDOWS).length() > 0);
    }

    public final void testGetSpecialFolderPath() {
        assertFalse(Shell32Util.getSpecialFolderPath(ShlObj.CSIDL_APPDATA, false).isEmpty());
    }

    public void testGetKnownFolderPath()
    {
        assertNotNull(Shell32Util.getKnownFolderPath(KnownFolders.FOLDERID_Fonts));
        assertNotNull(Shell32Util.getKnownFolderPath(KnownFolders.FOLDERID_Desktop));
        assertNotNull(Shell32Util.getKnownFolderPath(KnownFolders.FOLDERID_Startup));
        assertNotNull(Shell32Util.getKnownFolderPath(KnownFolders.FOLDERID_Programs));
        assertNotNull(Shell32Util.getKnownFolderPath(KnownFolders.FOLDERID_StartMenu));
        assertNotNull(Shell32Util.getKnownFolderPath(KnownFolders.FOLDERID_Recent));
        assertNotNull(Shell32Util.getKnownFolderPath(KnownFolders.FOLDERID_SendTo));
        assertNotNull(Shell32Util.getKnownFolderPath(KnownFolders.FOLDERID_Documents));
        assertNotNull(Shell32Util.getKnownFolderPath(KnownFolders.FOLDERID_Favorites));
        assertNotNull(Shell32Util.getKnownFolderPath(KnownFolders.FOLDERID_NetHood));
        assertNotNull(Shell32Util.getKnownFolderPath(KnownFolders.FOLDERID_PrintHood));
        assertNotNull(Shell32Util.getKnownFolderPath(KnownFolders.FOLDERID_Templates));
        assertNotNull(Shell32Util.getKnownFolderPath(KnownFolders.FOLDERID_CommonStartup));
        assertNotNull(Shell32Util.getKnownFolderPath(KnownFolders.FOLDERID_CommonAdminTools));
        assertNotNull(Shell32Util.getKnownFolderPath(KnownFolders.FOLDERID_CDBurning));
        assertNotNull(Shell32Util.getKnownFolderPath(KnownFolders.FOLDERID_Music));
        assertNotNull(Shell32Util.getKnownFolderPath(KnownFolders.FOLDERID_SavedGames));
        assertNotNull(Shell32Util.getKnownFolderPath(KnownFolders.FOLDERID_SavedSearches));
        assertNotNull(Shell32Util.getKnownFolderPath(KnownFolders.FOLDERID_AdminTools));
        assertNotNull(Shell32Util.getKnownFolderPath(KnownFolders.FOLDERID_ProgramFiles));
        assertNotNull(Shell32Util.getKnownFolderPath(KnownFolders.FOLDERID_ProgramData));
        assertNotNull(Shell32Util.getKnownFolderPath(KnownFolders.FOLDERID_ProgramFilesCommon));
        assertNotNull(Shell32Util.getKnownFolderPath(KnownFolders.FOLDERID_ProgramFilesCommonX86));
        assertNotNull(Shell32Util.getKnownFolderPath(KnownFolders.FOLDERID_Programs));
        assertNotNull(Shell32Util.getKnownFolderPath(KnownFolders.FOLDERID_Windows));
        assertNotNull(Shell32Util.getKnownFolderPath(KnownFolders.FOLDERID_Public));
        assertNotNull(Shell32Util.getKnownFolderPath(KnownFolders.FOLDERID_PublicDesktop));
        assertNotNull(Shell32Util.getKnownFolderPath(KnownFolders.FOLDERID_Links));
        assertNotNull(Shell32Util.getKnownFolderPath(KnownFolders.FOLDERID_LocalAppData));
        assertNotNull(Shell32Util.getKnownFolderPath(KnownFolders.FOLDERID_Libraries));
        assertNotNull(Shell32Util.getKnownFolderPath(KnownFolders.FOLDERID_RoamingAppData));
        assertNotNull(Shell32Util.getKnownFolderPath(KnownFolders.FOLDERID_UserProfiles));
        // This is unstable:
        // assertNotNull(Shell32Util.getKnownFolderPath(KnownFolders.FOLDERID_UserProgramFiles));
        // assertNotNull(Shell32Util.getKnownFolderPath(KnownFolders.FOLDERID_UserProgramFilesCommon));
    }

    public void testCommandLineToArgv() {
        String cl = "\"foo bar\" baz";
        String[] argv = { "foo bar", "baz" };
        assertArrayEquals(argv, Shell32Util.CommandLineToArgv(cl));
    }
}
