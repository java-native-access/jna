package com.sun.jna.platform.win32.COM.util;

import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.COM.util.annotation.ComInterface;
import com.sun.jna.platform.win32.COM.util.annotation.ComObject;
import com.sun.jna.platform.win32.Ole32;
import com.sun.jna.platform.win32.WinDef.LCID;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;


public class ConfigurateLCID_Test {

    private Factory factory;

    @Before
    public void before() {
        Ole32.INSTANCE.CoInitializeEx(Pointer.NULL, Ole32.COINIT_MULTITHREADED);
        this.factory = new Factory();
        // switch to english locale (the test is only valid if office is
        // installed in a non-english locale
        this.factory.setLCID(new LCID(0x0409)); 
    }

    @After
    public void after() {
        this.factory.disposeAll();
        Ole32.INSTANCE.CoUninitialize();
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
