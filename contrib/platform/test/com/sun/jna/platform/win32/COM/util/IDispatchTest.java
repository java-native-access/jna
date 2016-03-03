package com.sun.jna.platform.win32.COM.util;

import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.COM.util.annotation.ComMethod;
import com.sun.jna.platform.win32.COM.util.annotation.ComObject;
import com.sun.jna.platform.win32.COM.util.annotation.ComProperty;
import com.sun.jna.platform.win32.OaIdl.DISPID;
import com.sun.jna.platform.win32.Ole32;
import junit.framework.TestCase;
import static junit.framework.TestCase.assertTrue;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class IDispatchTest {

    Factory factory;

    @Before
    public void before() {
        Ole32.INSTANCE.CoInitializeEx(Pointer.NULL, Ole32.COINIT_MULTITHREADED);
        this.factory = new Factory();
    }

    @After
    public void after() {
        this.factory.disposeAll();
        Ole32.INSTANCE.CoUninitialize();
    }
    
    @Test
    public void testDispatchBaseOnMethodName() throws InterruptedException {
        ComInternetExplorerMethodname ieApp = factory.createObject(ComInternetExplorerMethodname.class);

        // Test getting property
        TestCase.assertFalse(ieApp.getVisible());

        // Test setting property
        ieApp.setVisible(Boolean.TRUE);
        TestCase.assertTrue(ieApp.getVisible());

        // Check navigate function and with that the method invocation
        assertTrue(ieApp.getLocationURL().isEmpty());
        
        ieApp.Navigate2("https://github.com/java-native-access/");

        // Check max. 2s if Navigation happend
        boolean navigationHappend = false;
        for (int i = 0; i < 10; i++) {
            String url = ieApp.getLocationURL();
            if (!url.isEmpty()) {
                navigationHappend = true;
                break;
            } else {
                Thread.sleep(200);
            }
        }

        TestCase.assertTrue(navigationHappend);
        
        ieApp.Quit();
    }
    
    @ComObject(progId = "Internet.Explorer.1", clsId = "{0002DF01-0000-0000-C000-000000000046}")
    interface ComInternetExplorerMethodname {
        @ComProperty
        String getLocationURL();
        
        @ComMethod
        void Navigate2(String url);
        
        @ComProperty
        Boolean getVisible();
        
        @ComProperty
        void setVisible(Boolean visible);
        
        @ComMethod
        void Quit();
    }

    @Test
    public void testDispatchBaseOnNamed() throws InterruptedException {
        ComInternetExplorerNamed ieApp = factory.createObject(ComInternetExplorerNamed.class);

        // Test getting property
        TestCase.assertFalse(ieApp.getVisible_MOD());

        // Test setting property
        ieApp.setVisible_MOD(Boolean.TRUE);
        TestCase.assertTrue(ieApp.getVisible_MOD());

        // Check navigate function and with that the method invocation
        assertTrue(ieApp.getLocationURL_MOD().isEmpty());
        
        ieApp.Navigate2_MOD("https://github.com/java-native-access/");

        // Check max. 2s if Navigation happend
        boolean navigationHappend = false;
        for (int i = 0; i < 10; i++) {
            String url = ieApp.getLocationURL_MOD();
            if (!url.isEmpty()) {
                navigationHappend = true;
                break;
            } else {
                Thread.sleep(200);
            }
        }

        TestCase.assertTrue(navigationHappend);
        
        ieApp.Quit_MOD();
    }
    
    @ComObject(progId = "Internet.Explorer.1", clsId = "{0002DF01-0000-0000-C000-000000000046}")
    interface ComInternetExplorerNamed {
        @ComProperty(name="LocationURL")
        String getLocationURL_MOD();
        
        @ComMethod(name="Navigate2")
        void Navigate2_MOD(String url);
        
        @ComProperty(name="Visible")
        Boolean getVisible_MOD();
        
        @ComProperty(name="Visible")
        void setVisible_MOD(Boolean visible);
        
        @ComMethod(name="Quit")
        void Quit_MOD();
    }
    
    @Test
    public void testDispatchBaseOnDISPID() throws InterruptedException {
        ComInternetExplorerDISPID ieApp = factory.createObject(ComInternetExplorerDISPID.class);

        // Test getting property
        TestCase.assertFalse(ieApp.getVisible_MOD());

        // Test setting property
        ieApp.setVisible_MOD(Boolean.TRUE);
        TestCase.assertTrue(ieApp.getVisible_MOD());

        // Check navigate function and with that the method invocation
        assertTrue(ieApp.getLocationURL_MOD().isEmpty());
        
        ieApp.Navigate2_MOD("https://github.com/java-native-access/");

        // Check max. 2s if Navigation happend
        boolean navigationHappend = false;
        for (int i = 0; i < 10; i++) {
            String url = ieApp.getLocationURL_MOD();
            if (!url.isEmpty()) {
                navigationHappend = true;
                break;
            } else {
                Thread.sleep(200);
            }
        }

        TestCase.assertTrue(navigationHappend);
        
        ieApp.Quit_MOD();
    }
    
    @ComObject(progId = "Internet.Explorer.1", clsId = "{0002DF01-0000-0000-C000-000000000046}")
    interface ComInternetExplorerDISPID {
        @ComProperty(dispId = 0x000000d3)
        String getLocationURL_MOD();
        
        @ComMethod(dispId = 0x000001f4)
        void Navigate2_MOD(String url);
        
        @ComProperty(dispId = 0x00000192)
        Boolean getVisible_MOD();
        
        @ComProperty(dispId = 0x00000192)
        void setVisible_MOD(Boolean visible);
        
        @ComMethod(dispId = 0x0000012c)
        void Quit_MOD();
    }
    
    @Test
    public void testIDispatchName() throws InterruptedException {
        ComInternetExplorerIDispatch ieApp = factory.createObject(ComInternetExplorerIDispatch.class);

        // Test getting property
        TestCase.assertFalse(ieApp.getProperty(Boolean.class, "Visible"));

        // Test setting property
        ieApp.setProperty("Visible", Boolean.TRUE);
        TestCase.assertTrue(ieApp.getProperty(Boolean.class, "Visible"));

        // Check navigate function and with that the method invocation
        assertTrue(ieApp.getProperty(String.class, "LocationURL").isEmpty());

        ieApp.invokeMethod(Void.class, "Navigate2", "https://github.com/java-native-access/");

        // Check max. 2s if Navigation happend
        boolean navigationHappend = false;
        for (int i = 0; i < 10; i++) {
            String url = ieApp.getProperty(String.class, "LocationURL");
            if (!url.isEmpty()) {
                navigationHappend = true;
                break;
            } else {
                Thread.sleep(200);
            }
        }

        TestCase.assertTrue(navigationHappend);
        
        ieApp.invokeMethod(Void.class, "Quit");
    }
    
    @Test
    public void testIDispatchDISPID() throws InterruptedException {
        DISPID locationURL = new DISPID(0x000000d3);
        DISPID visible = new DISPID(0x00000192);
        DISPID quit = new DISPID(0x0000012c);
        DISPID navigate2 = new DISPID(0x000001f4);
        
        ComInternetExplorerIDispatch ieApp = factory.createObject(ComInternetExplorerIDispatch.class);

        // Test getting property
        TestCase.assertFalse(ieApp.getProperty(Boolean.class, visible));

        // Test setting property
        ieApp.setProperty(visible, Boolean.TRUE);
        TestCase.assertTrue(ieApp.getProperty(Boolean.class, visible));

        // Check navigate function and with that the method invocation
        assertTrue(ieApp.getProperty(String.class, locationURL).isEmpty());

        ieApp.invokeMethod(Void.class, navigate2, "https://github.com/java-native-access/");

        // Check max. 2s if Navigation happend
        boolean navigationHappend = false;
        for (int i = 0; i < 10; i++) {
            String url = ieApp.getProperty(String.class, locationURL);
            if (!url.isEmpty()) {
                navigationHappend = true;
                break;
            } else {
                Thread.sleep(200);
            }
        }

        TestCase.assertTrue(navigationHappend);
        
        ieApp.invokeMethod(Void.class, quit);
    }
    
    @ComObject(progId = "Internet.Explorer.1", clsId = "{0002DF01-0000-0000-C000-000000000046}")
    interface ComInternetExplorerIDispatch extends IDispatch {
    }
}
