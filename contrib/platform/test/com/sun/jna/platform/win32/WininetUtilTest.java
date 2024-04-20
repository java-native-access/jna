/* Copyright (c) 2015 Michael Freeman, All Rights Reserved
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

import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.COM.COMLateBindingObject;
import com.sun.jna.platform.win32.COM.COMUtils;
import com.sun.jna.platform.win32.COM.Dispatch;
import com.sun.jna.platform.win32.COM.IDispatch;
import com.sun.jna.ptr.PointerByReference;
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

    private static final Guid.CLSID CLSID_InternetExplorer = new Guid.CLSID("{0002DF01-0000-0000-C000-000000000046}");

    private PointerByReference ieApp;
    private Dispatch ieDispatch;

    @Before
    public void setUp() throws Exception {
        // Launch IE in a manner that should ensure it opens even if the system
        // default browser is Chrome, Firefox, or something else.
        // Launching IE to a page ensures there will be content in the WinInet
        // cache.
        WinNT.HRESULT hr;

        hr = Ole32.INSTANCE.CoInitializeEx(Pointer.NULL, Ole32.COINIT_MULTITHREADED);
        COMUtils.checkRC(hr);

        // IE can not be launched directly anymore - so load it via COM

        ieApp = new PointerByReference();
        hr = Ole32.INSTANCE
                .CoCreateInstance(CLSID_InternetExplorer, null, WTypes.CLSCTX_SERVER, IDispatch.IID_IDISPATCH, ieApp);
        COMUtils.checkRC(hr);

        ieDispatch = new Dispatch(ieApp.getValue());
        LocalLateBinding ie = new LocalLateBinding(ieDispatch);

        ie.setProperty("Visible", true);

        Variant.VARIANT url = new Variant.VARIANT("http://www.google.com");
        Variant.VARIANT result = ie.invoke("Navigate", url);
        OleAuto.INSTANCE.VariantClear(url);
        OleAuto.INSTANCE.VariantClear(result);

        ieDispatch.Release();
        Ole32.INSTANCE.CoUninitialize();

        // There's no easy way to monitor IE and see when it's done loading
        // google.com, so just give it 10 seconds.
        // Google keeps the homepage simple so 10 seconds should be enough time
        // to get something into a cache.
        Thread.sleep(10000);
    }

    @Test
    public void testGetCache() throws Exception {

        Map<String, String> ieCache = WininetUtil.getCache();
        if (ieCache.isEmpty()) {
            return; // Disable test since the cache is empty, policy restriction?
        }

        boolean historyEntryFound = false;
        boolean googleLogoOrOtherImageFound = false;
        for (String URL : ieCache.keySet()) {
            if (URL.startsWith("Visited:") && URL.contains("www.google.com")) {
                historyEntryFound = true;
            } else if (URL.contains("google.com") && (URL.endsWith("png") || URL.endsWith("jpg") || URL.endsWith("ico"))) {
                googleLogoOrOtherImageFound = true;
            }
        }

        assertTrue("Google logo (or other image) should have been found in the browser cache.", googleLogoOrOtherImageFound);
        assertTrue("History entry for google.com should have been found in the browser cache.", historyEntryFound);
    }

    @After
    public void tearDown() throws Exception {
        // only kill the freshly opened Google window, unless someone has two IE windows open to Google.
        Runtime.getRuntime().exec("taskkill.exe /f /im iexplore.exe");
    }

    private static class LocalLateBinding extends COMLateBindingObject {

        public LocalLateBinding(IDispatch iDispatch) {
            super(iDispatch);
        }

        @Override
        public void setProperty(String propertyName, boolean value) {
            super.setProperty(propertyName, value);
        }

        @Override
        public Variant.VARIANT invoke(String methodName, Variant.VARIANT arg) {
            return super.invoke(methodName, arg);
        }

    }
}
