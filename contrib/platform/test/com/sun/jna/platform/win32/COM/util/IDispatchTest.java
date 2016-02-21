package com.sun.jna.platform.win32.COM.util;

import com.sun.jna.platform.win32.COM.util.annotation.ComObject;
import junit.framework.TestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class IDispatchTest {

    Factory factory;

    @Before
    public void before() {
        this.factory = new Factory();
    }

    @After
    public void after() {
        this.factory.disposeAll();
        this.factory.getComThread().terminate(1000);
    }

    @Test
    public void testIDispatch() throws InterruptedException {
        ComInternetExplorer ieApp = factory.createObject(ComInternetExplorer.class);
        IDispatch appDispatch = ieApp;

        // Test getting property
        TestCase.assertFalse(appDispatch.getProperty(Boolean.class, "Visible"));

        // Test setting property
        appDispatch.setProperty("Visible", Boolean.TRUE);
        TestCase.assertTrue(appDispatch.getProperty(Boolean.class, "Visible"));

        // Check navigate function and with that the method invocation
        assert appDispatch.getProperty(String.class, "LocationURL").isEmpty();

        appDispatch.invokeMethod(Void.class, "Navigate2", "http://www.heise.de");

        // Check max. 2s if Navigation happend
        boolean navigationHappend = false;
        for (int i = 0; i < 10; i++) {
            String url = appDispatch.getProperty(String.class, "LocationURL");
            if (!url.isEmpty()) {
                navigationHappend = true;
                break;
            } else {
                Thread.sleep(200);
            }
        }

        TestCase.assertTrue(navigationHappend);
     
        appDispatch.invokeMethod(Void.class, "Quit");
    }

    @ComObject(progId = "Internet.Explorer.1", clsId = "{0002DF01-0000-0000-C000-000000000046}")
    interface ComInternetExplorer extends IUnknown, IDispatch {
    }

}
