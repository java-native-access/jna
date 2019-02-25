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
package com.sun.jna.platform.win32.COM.util;

import com.sun.jna.Pointer;
import static com.sun.jna.platform.win32.AbstractWin32TestSupport.checkCOMRegistered;
import com.sun.jna.platform.win32.COM.COMUtils;
import com.sun.jna.platform.win32.COM.util.annotation.ComInterface;
import com.sun.jna.platform.win32.COM.util.annotation.ComObject;
import com.sun.jna.platform.win32.Ole32;
import com.sun.jna.platform.win32.WinDef.LCID;
import org.junit.After;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;


public class ConfigurateLCID_Test {

    private boolean initialized = false;
    private Factory factory;

    @Before
    public void before() {
        // Check that Excel is registered in the registry
        Assume.assumeTrue("Could not find registration", checkCOMRegistered("{0002DF01-0000-0000-C000-000000000046}"));
        COMUtils.checkRC(Ole32.INSTANCE.CoInitializeEx(Pointer.NULL, Ole32.COINIT_MULTITHREADED));
        initialized= true;
        this.factory = new Factory();
        // switch to english locale (the test is only valid if office is
        // installed in a non-english locale
        this.factory.setLCID(new LCID(0x0409));
    }

    @After
    public void after() {
        if(this.factory != null) {
            this.factory.disposeAll();
        }
        if(initialized) {
            Ole32.INSTANCE.CoUninitialize();
        }
    }

    @Test
    @Ignore("Only valid for a non-english locale - run manually")
    public void testDispatchBaseOnMethodName() throws InterruptedException {
        ComExcel_Application excel = factory.createObject(ComExcel_Application.class);
        ComIApplication excelApp = excel.queryInterface(ComIApplication.class);

        // Set visiblite of application
        excelApp.setProperty("Visible", false);
        excelApp.setProperty("DisplayAlerts", false);

        // Get a new workbook.
        IDispatch wb = excelApp.getProperty(IDispatch.class, "Workbooks").invokeMethod(IDispatch.class, "Add");
        IDispatch sheet = wb.getProperty(IDispatch.class, "ActiveSheet");

        sheet.getProperty(IDispatch.class, "Range", "A1").setProperty("Value", 42);
        sheet.getProperty(IDispatch.class, "Range", "A2").setProperty("Value", 23);
        // Set formula with english command
        sheet.getProperty(IDispatch.class, "Range", "A3").setProperty("Formula", "=SUM(A1:A2)");

        Number result = sheet.getProperty(IDispatch.class, "Range", "A3").getProperty(Number.class, "Value");

        // The formula should report the sum
        Assert.assertEquals(65, result.intValue());

        excelApp.invokeMethod(Void.class, "Quit");
    }

    @ComObject(progId = "Excel.Application")
    public interface ComExcel_Application extends IUnknown {

    }

    @ComInterface(iid = "{000208D5-0000-0000-C000-000000000046}")
    public interface ComIApplication extends IUnknown, IConnectionPoint, IDispatch {
    }
}
