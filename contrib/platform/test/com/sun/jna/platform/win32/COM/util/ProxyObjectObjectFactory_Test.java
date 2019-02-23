/* Copyright (c) 2014 Dr David H. Akehurst (itemis), All Rights Reserved
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
package com.sun.jna.platform.win32.COM.util;

import com.sun.jna.Pointer;
import static com.sun.jna.platform.win32.AbstractWin32TestSupport.checkCOMRegistered;
import com.sun.jna.platform.win32.COM.COMException;
import com.sun.jna.platform.win32.COM.COMInvokeException;
import com.sun.jna.platform.win32.COM.COMUtils;
import static org.junit.Assert.*;

import java.lang.reflect.Proxy;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.sun.jna.platform.win32.COM.util.annotation.ComInterface;
import com.sun.jna.platform.win32.COM.util.annotation.ComObject;
import com.sun.jna.platform.win32.COM.util.annotation.ComMethod;
import com.sun.jna.platform.win32.COM.util.annotation.ComProperty;
import com.sun.jna.platform.win32.Ole32;
import com.sun.jna.platform.win32.WinError;
import org.junit.Assume;

public class ProxyObjectObjectFactory_Test {

    private static final Logger LOG = Logger.getLogger(ProxyObjectObjectFactory_Test.class.getName());

    static {
        ClassLoader.getSystemClassLoader().setDefaultAssertionStatus(true);
    }

    @ComInterface(iid = "{00020970-0000-0000-C000-000000000046}")
    interface Application extends IUnknown {

        @ComProperty
        boolean getVisible();

        @ComProperty
        void setVisible(boolean value);

        @ComMethod
        void Quit(boolean SaveChanges, Object OriginalFormat, Boolean RouteDocument);

        @ComMethod
        public void Quit(Object... someArgs);

        @ComMethod(dispId = 0x00000183)
        public float PointsToPixels(float points, Object... someArgs);

        @ComProperty(dispId = 0x00000006)
        public Documents getDocuments();
    }

    @ComInterface(iid = "{0002096C-0000-0000-C000-000000000046}")
    public interface Documents extends IDispatch {

        @ComMethod
        public _Document Add(Object template, Object newTemplate, Object documentType, Object visible);

        @ComMethod
        public _Document Add(Object... someArgs);
    }

    @ComInterface(iid = "{0002096B-0000-0000-C000-000000000046}")
    public interface _Document extends IDispatch {

        @ComMethod
        public void SaveAs(Object fileName, Object fileFormat, Object lockComments, Object password,
                Object addToRecentFiles, Object writePassword, Object readOnlyRecommended, Object embedTrueTypeFonts,
                Object saveNativePictureFormat, Object saveFormsData, Object saveAsAOCELetter, Object encoding,
                Object insertLineBreaks, Object allowSubstitutions, Object lineEnding, Object addBiDiMarks);

        @ComMethod
        public void SaveAs(Object... someArgs);
    }

    public enum WdSaveFormat implements IComEnum {
        wdFormatDocument(0), wdFormatText(2), wdFormatRTF(6), wdFormatHTML(8), wdFormatPDF(17);

        private long _value;

        private WdSaveFormat(long value) {
            _value = value;
        }

        @Override
        public long getValue() {
            return _value;
        }
    }

    @ComObject(progId = "Word.Application")
    interface MsWordApp extends Application {
    }

    private ObjectFactory factory;
    private boolean initialized = false;

    @Before
    public void before() {
        // Check Existence of Word Application
        Assume.assumeTrue("Could not find registration", checkCOMRegistered("{00020970-0000-0000-C000-000000000046}"));
        COMUtils.checkRC(Ole32.INSTANCE.CoInitializeEx(Pointer.NULL, Ole32.COINIT_MULTITHREADED));
        initialized = true;
        this.factory = new ObjectFactory();
        //ensure there are no word applications running.
        while (true) {
            try {
                MsWordApp ao = this.factory.fetchObject(MsWordApp.class);
                Application a = ao.queryInterface(Application.class);
                try {
                    a.Quit(true, null, null);
                    try {
                        //wait for it to quit
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        LOG.log(Level.INFO, null, e);
                    }
                } catch (COMException e) {
                    LOG.log(Level.INFO, null, e);
                    LOG.log(Level.INFO, null, e.getCause());
                }
            } catch (COMException e) {
                if (e.getHresult() != null) {
                    if (e.matchesErrorCode(WinError.MK_E_UNAVAILABLE)) {
                        break;
                    } else if (e.matchesErrorCode(WinError.RPC_E_DISCONNECTED)) {
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException ex) {
                        }
                    }
                } else {
                    throw e;
                }
            }
        }
    }

    @After
    public void after() {
        if (factory != null) {
            factory.disposeAll();
        }
        if (initialized) {
            Ole32.INSTANCE.CoUninitialize();
        }
    }

    @Test
    public void testFetchNotExistingObject() {
        COMException exceptionRaised = null;
        try {
            MsWordApp comObj2 = this.factory.fetchObject(MsWordApp.class);
        } catch (COMException ex) {
            exceptionRaised = ex;
        }
        assertNotNull("fetchObject on a non-running Object must raise an exception", exceptionRaised);
        assertEquals("Unexpected error code", exceptionRaised.getHresult().intValue(), WinError.MK_E_UNAVAILABLE);
        assertTrue("Error code not matched", exceptionRaised.matchesErrorCode(WinError.MK_E_UNAVAILABLE));
        boolean callingMethodPartOfStackTrace = false;
        for (StackTraceElement ste : exceptionRaised.getStackTrace()) {
            if ("testFetchNotExistingObject".equals(ste.getMethodName())
                    && getClass().getName().equals(ste.getClassName())) {
                callingMethodPartOfStackTrace = true;
                break;
            }
        }
        assertTrue("The calling method must be part of the reported stack trace", callingMethodPartOfStackTrace);
    }

    @Test
    public void equals() {
        MsWordApp comObj1 = this.factory.createObject(MsWordApp.class);
        MsWordApp comObj2 = this.factory.fetchObject(MsWordApp.class);

        boolean res = comObj1.equals(comObj2);

        assertTrue(res);

        comObj1.Quit(false, null, null);
    }

    @Test
    public void notEquals() {
        MsWordApp comObj1 = this.factory.createObject(MsWordApp.class);
        MsWordApp comObj2 = this.factory.createObject(MsWordApp.class);

        boolean res = comObj1.equals(comObj2);

        assertFalse(res);

        comObj1.Quit(false, null, null);
    }

    @Test
    public void accessWhilstDisposing() {
        MsWordApp comObj1 = this.factory.createObject(MsWordApp.class);
        comObj1.Quit();

        //TODO: how to test this?
        this.factory.disposeAll();

    }

    @Test
    public void testVarargsCallWithoutVarargParameter() {
        MsWordApp comObj = this.factory.createObject(MsWordApp.class);

        // call must work without exception:
        float f = comObj.PointsToPixels(25.3f);
        comObj.Quit();
    }

    @Test
    public void testVarargsCallWithParameter() {
        MsWordApp comObj = this.factory.createObject(MsWordApp.class);

        Documents documents = comObj.getDocuments();
        _Document myDocument = documents.Add();

        String path = new File(".").getAbsolutePath();
        myDocument.SaveAs(path + "\\abcdefg", WdSaveFormat.wdFormatPDF);
        comObj.Quit();

        boolean wasDeleted = new File("abcdefg.pdf").delete();
        assertTrue(wasDeleted);
    }

    @Test
    public void testDisposeMustBeCallableMultipleTimes() {
        MsWordApp comObj = this.factory.createObject(MsWordApp.class);
        comObj.Quit();
        ((ProxyObject) Proxy.getInvocationHandler(comObj)).dispose();
        ((ProxyObject) Proxy.getInvocationHandler(comObj)).dispose();
    }

    @Test
    public void testVarargsCallWithInvalidParameter() {
        MsWordApp comObj = this.factory.createObject(MsWordApp.class);

        Documents documents = comObj.getDocuments();

        COMInvokeException invokeException = null;

        try {
            documents.Add("Not_existing_template");
        } catch (COMInvokeException ex) {
            invokeException = ex;
        }

        assertNotNull(invokeException);
        assertEquals("Wrong hresult", WinError.DISP_E_EXCEPTION, invokeException.getHresult().intValue());
        assertTrue("hresult was not matched", invokeException.matchesErrorCode(WinError.DISP_E_EXCEPTION));
        assertEquals("Wrong scode", (long) 0x800a1436, (long) invokeException.getScode());
    }
}
