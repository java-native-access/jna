/* Copyright (c) 2010, 2013 Daniel Doubrovkine, Markus Karg, All Rights Reserved
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
package com.sun.jna.platform.win32;

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
        assertNotNull(Shell32Util.getKnownFolderPath(KnownFolders.FOLDERID_UserProgramFiles));
        assertNotNull(Shell32Util.getKnownFolderPath(KnownFolders.FOLDERID_UserProgramFilesCommon));
    }
}
