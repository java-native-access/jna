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
import static org.junit.Assert.*;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.sun.jna.platform.win32.COM.COMException;
import com.sun.jna.platform.win32.COM.COMUtils;
import com.sun.jna.platform.win32.COM.util.annotation.ComInterface;
import com.sun.jna.platform.win32.COM.util.annotation.ComObject;
import com.sun.jna.platform.win32.COM.util.annotation.ComMethod;
import com.sun.jna.platform.win32.COM.util.annotation.ComProperty;
import com.sun.jna.platform.win32.Ole32;
import org.junit.Assume;

public class RunningObjectTable_Test {

    static {
        ClassLoader.getSystemClassLoader().setDefaultAssertionStatus(true);
    }

    @ComInterface(iid = "{00020970-0000-0000-C000-000000000046}")
    interface Application extends IUnknown {

        @ComProperty
        Boolean getVisible();

        @ComProperty
        void setVisible(Boolean value);

        @ComMethod
        void Quit(boolean SaveChanges, Object OriginalFormat, Boolean RouteDocument);
    }

    @ComObject(progId = "Word.Application")
    interface MsWordApp extends Application {
    }

    private ObjectFactory factory;
    private MsWordApp msWord;
    private boolean initialized = false;

    @Before
    public void before() {
        // Check Existence of Word Application
        Assume.assumeTrue("Could not find registration", checkCOMRegistered("{00020970-0000-0000-C000-000000000046}"));

        COMUtils.checkRC(Ole32.INSTANCE.CoInitializeEx(Pointer.NULL, Ole32.COINIT_MULTITHREADED));
        initialized = true;

        this.factory = new ObjectFactory();
        //ensure there is only one word application running.
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
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    e.getCause().printStackTrace();
                }
            } catch (Exception e) {
                break;
            }
        }

        this.msWord = this.factory.createObject(MsWordApp.class);
        msWord.setVisible(true);
    }

    @After
    public void after() {
        if (this.msWord != null) {
            this.msWord.Quit(true, null, null);
        }
        try {
            //wait for it to quit
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (factory != null) {
            factory.disposeAll();
        }
        if (initialized) {
            Ole32.INSTANCE.CoUninitialize();
        }
    }

    @Test
    public void getRunningObjectTable() {
        IRunningObjectTable rot = this.factory.getRunningObjectTable();

        assertNotNull(rot);
    }

    @Test
    public void enumRunning() {
        IRunningObjectTable rot = this.factory.getRunningObjectTable();

        for (IUnknown obj : rot.enumRunning()) {
            try {
                Application msw = obj.queryInterface(Application.class);
            } catch (COMException ex) {
                int i = 0;
            }
        }
    }

    @Test
    public void getActiveObjectsByInterface() {
        IRunningObjectTable rot = this.factory.getRunningObjectTable();

        List<Application> objs = rot.getActiveObjectsByInterface(Application.class);
        assertTrue(objs.size() > 0);

        for (Application dobj : objs) {
            msWord.setVisible(true);
            boolean v2 = dobj.getVisible();
            assertEquals(true, v2);
        }

    }
}
