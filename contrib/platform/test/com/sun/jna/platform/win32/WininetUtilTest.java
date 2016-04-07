/* Copyright (c) 2015 Michael Freeman, All Rights Reserved
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

import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.JUnitCore;

public class WininetUtilTest extends AbstractWin32TestSupport {

    public static void main(String[] args) {
        JUnitCore jUnitCore = new JUnitCore();
        jUnitCore.run(WininetUtilTest.class);
    }

    @Before
    public void setUp() throws Exception {
        // Launch IE in a manner that should ensure it opens even if the system
        // default browser is Chrome, Firefox, or something else.
        // Launching IE to a page ensures there will be content in the WinInet
        // cache.
        Runtime.getRuntime().exec("cmd /c start iexplore.exe -nomerge -nohome \"http://www.google.com\"");

        // There's no easy way to monitor IE and see when it's done loading
        // google.com, so just give it 10 seconds.
        // Google keeps the homepage simple so 10 seconds should be enough time
        // to get something into a cache.
        Thread.sleep(10000);
    }

    @Test
    public void testGetCache() throws Exception {

        Map<String, String> ieCache = WininetUtil.getCache();
        assertNotNull("WinInet cache should have some items in it.", ieCache.size() > 1);

        boolean historyEntryFound = false;
        boolean googleLogoOrOtherImageFound = false;
        for (String URL : ieCache.keySet()) {
            if (URL.startsWith("Visited:") && URL.contains("www.google.com")) {
                historyEntryFound = true;
            } else if (URL.contains("google.com") && (URL.endsWith("png") || URL.endsWith("jpg"))) {
                googleLogoOrOtherImageFound = true;
            }
        }

        assertTrue("Google logo (or other image) should have been found in the browser cache.", googleLogoOrOtherImageFound);
        assertTrue("History entry for google.com should have been found in the browser cache.", historyEntryFound);
    }

    @After
    public void tearDown() throws Exception {
        // only kill the freshly opened Google window, unless someone has two IE windows open to Google.
        Runtime.getRuntime().exec("taskkill.exe /f /im iexplore.exe /fi \"windowtitle eq Google*\"");
    }
}
