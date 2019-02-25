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
import com.sun.jna.platform.win32.COM.util.annotation.ComMethod;
import com.sun.jna.platform.win32.COM.util.annotation.ComObject;
import com.sun.jna.platform.win32.Ole32;
import com.sun.jna.platform.win32.Variant;
import org.junit.After;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

public class ComEventCallbacks2_Test {

    static {
        ClassLoader.getSystemClassLoader().setDefaultAssertionStatus(true);
    }

    private boolean initialized = false;
    private Factory factory;

    @Before
    public void before() {
        // Check if Word is registered in the registry
        Assume.assumeTrue("Could not find registration", checkCOMRegistered("{000209FF-0000-0000-C000-000000000046}"));
        COMUtils.checkRC(Ole32.INSTANCE.CoInitializeEx(Pointer.NULL, Ole32.COINIT_MULTITHREADED));
        initialized = true;
        this.factory = new Factory();
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
    public void testFireCloseHandlerMatching() throws InterruptedException {

        class ApplicatonEventsHandler extends AbstractComEventCallbackListener implements ApplicationEvents4ListenerMatching {

            public volatile boolean changed = false;
            public volatile boolean beforeClose = false;
            public volatile boolean error = false;

            @Override
            public void errorReceivingCallbackEvent(String string, Exception excptn) {
                if(string.startsWith("No method found with")) {
                    return; // Normal case
                }
                System.out.println("Error: " + string);
                error = true;
            }

            public void DocumentChange() {
                changed = true;
            }

            public void DocumentBeforeClose(IDispatch Doc, Variant.VARIANT Cancel) {
                beforeClose = true;
            }
        }

        ApplicatonEventsHandler handler = new ApplicatonEventsHandler();

        Application appX = factory.createObject(Application.class);

        Thread.sleep(500);

        IComEventCallbackCookie cookie = appX.advise(ApplicationEvents4ListenerMatching.class, handler);

        IDispatch doc = appX.getProperty(IDispatch.class, "Documents").invokeMethod(IDispatch.class, "Add");

        Thread.sleep(500);

        doc.getProperty(IDispatch.class, "Paragraphs")
                .invokeMethod(IDispatch.class, "Item", 1)
                .getProperty(IDispatch.class, "Range")
                .setProperty("Text", "Test text");

        Thread.sleep(500);

        doc.invokeMethod(Void.class, "Close", Boolean.FALSE);

        Thread.sleep(500);

        appX.unadvise(ApplicationEvents4ListenerMatching.class, cookie);

        appX.invokeMethod(Void.class, "Quit", Boolean.FALSE);

        Assert.assertTrue(handler.changed);
        Assert.assertTrue(handler.beforeClose);
        Assert.assertFalse(handler.error);
    }

    @Test
    public void testFireCloseHandlerLessArguments() throws InterruptedException {

        class ApplicatonEventsHandler extends AbstractComEventCallbackListener implements ApplicationEvents4ListenerLessArguments {

            public volatile boolean changed = false;
            public volatile boolean beforeClose = false;
            public volatile boolean error = false;

            @Override
            public void errorReceivingCallbackEvent(String string, Exception excptn) {
                if(string.startsWith("No method found with")) {
                    return; // Normal case
                }
                System.out.println("Error: " + string);
                if(excptn != null) {
                    System.out.println(excptn.getMessage());
                    excptn.printStackTrace(System.out);
                }
                error = true;
            }

            public void DocumentChange() {
                changed = true;
            }

            public void DocumentBeforeClose() {
                beforeClose = true;
            }
        }

        ApplicatonEventsHandler handler = new ApplicatonEventsHandler();

        Application appX = factory.createObject(Application.class);

        Thread.sleep(500);

        IComEventCallbackCookie cookie = appX.advise(ApplicationEvents4ListenerLessArguments.class, handler);

        IDispatch doc = appX.getProperty(IDispatch.class, "Documents").invokeMethod(IDispatch.class, "Add");

        Thread.sleep(500);

        doc.getProperty(IDispatch.class, "Paragraphs")
                .invokeMethod(IDispatch.class, "Item", 1)
                .getProperty(IDispatch.class, "Range")
                .setProperty("Text", "Test text");

        Thread.sleep(500);

        doc.invokeMethod(Void.class, "Close", Boolean.FALSE);

        Thread.sleep(500);

        appX.unadvise(ApplicationEvents4ListenerMatching.class, cookie);

        appX.invokeMethod(Void.class, "Quit", Boolean.FALSE);

        Assert.assertTrue(handler.changed);
        Assert.assertTrue(handler.beforeClose);
        Assert.assertFalse(handler.error);
    }

    @Test
    public void testFireCloseHandlerMoreArguments() throws InterruptedException {

        class ApplicatonEventsHandler extends AbstractComEventCallbackListener implements ApplicationEvents4ListenerMoreArguments {

            public volatile boolean changed = false;
            public volatile boolean beforeClose = false;
            public volatile boolean error = false;
            public volatile boolean fakeArgumentObjectWasNull = false;
            public volatile boolean fakeArgumentIntWas0 = false;

            @Override
            public void errorReceivingCallbackEvent(String string, Exception excptn) {
                if(string.startsWith("No method found with")) {
                    return; // Normal case
                }
                System.out.println("Error: " + string);
                if(excptn != null) {
                    System.out.println(excptn.getMessage());
                    excptn.printStackTrace(System.out);
                }
                error = true;
            }

            public void DocumentChange() {
                changed = true;
            }

            public void DocumentBeforeClose(IDispatch Doc, Variant.VARIANT Cancel, Boolean fakeArgumentObject, int fakeArgumentInt) {
                beforeClose = true;
                fakeArgumentObjectWasNull = fakeArgumentObject == null;
                fakeArgumentIntWas0 = fakeArgumentInt == 0;
            }
        }

        ApplicatonEventsHandler handler = new ApplicatonEventsHandler();

        Application appX = factory.createObject(Application.class);

        Thread.sleep(500);

        IComEventCallbackCookie cookie = appX.advise(ApplicationEvents4ListenerMoreArguments.class, handler);

        IDispatch doc = appX.getProperty(IDispatch.class, "Documents").invokeMethod(IDispatch.class, "Add");

        Thread.sleep(500);

        doc.getProperty(IDispatch.class, "Paragraphs")
                .invokeMethod(IDispatch.class, "Item", 1)
                .getProperty(IDispatch.class, "Range")
                .setProperty("Text", "Test text");

        Thread.sleep(500);

        doc.invokeMethod(Void.class, "Close", Boolean.FALSE);

        Thread.sleep(500);

        appX.unadvise(ApplicationEvents4ListenerMatching.class, cookie);

        appX.invokeMethod(Void.class, "Quit", Boolean.FALSE);

        Assert.assertTrue(handler.changed);
        Assert.assertTrue(handler.beforeClose);
        Assert.assertFalse(handler.error);
        Assert.assertTrue(handler.fakeArgumentIntWas0);
        Assert.assertTrue(handler.fakeArgumentObjectWasNull);
    }


    @ComInterface(iid="{00020A01-0000-0000-C000-000000000046}")
    interface ApplicationEvents4ListenerMatching {

        /**
         * <p>
         * id(0x3)</p>
         */
        @ComMethod(dispId = 0x3)
        void DocumentChange();

        /**
         * <p>
         * id(0x6)</p>
         */
        @ComMethod(dispId = 0x6)
        void DocumentBeforeClose(IDispatch Doc, Variant.VARIANT Cancel);
    }

    @ComInterface(iid="{00020A01-0000-0000-C000-000000000046}")
    interface ApplicationEvents4ListenerLessArguments {

        /**
         * <p>
         * id(0x3)</p>
         */
        @ComMethod(dispId = 0x3)
        void DocumentChange();

        /**
         * <p>
         * id(0x6)</p>
         */
        @ComMethod(dispId = 0x6)
        void DocumentBeforeClose();
    }

    @ComInterface(iid="{00020A01-0000-0000-C000-000000000046}")
    interface ApplicationEvents4ListenerMoreArguments {

        /**
         * <p>
         * id(0x3)</p>
         */
        @ComMethod(dispId = 0x3)
        void DocumentChange();

        /**
         * <p>
         * id(0x6)</p>
         */
        @ComMethod(dispId = 0x6)
        void DocumentBeforeClose(IDispatch Doc, Variant.VARIANT Cancel, Boolean fakeArgumentObject, int fakeArgumentInt);
    }

    @ComObject(clsId = "{000209FF-0000-0000-C000-000000000046}")
    public interface Application extends
            IDispatch,
            IConnectionPoint,
            IUnknown {

    }
}
