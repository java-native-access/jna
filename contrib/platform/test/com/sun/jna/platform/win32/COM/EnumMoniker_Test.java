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
package com.sun.jna.platform.win32.COM;

import com.sun.jna.Pointer;
import static com.sun.jna.platform.win32.AbstractWin32TestSupport.checkCOMRegistered;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.sun.jna.platform.win32.Ole32;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.platform.win32.WinDef.DWORD;
import com.sun.jna.platform.win32.WinDef.ULONG;
import com.sun.jna.platform.win32.WinDef.ULONGByReference;
import com.sun.jna.platform.win32.WinNT.HRESULT;
import com.sun.jna.platform.win32.COM.util.ObjectFactory;
import com.sun.jna.platform.win32.COM.util.annotation.ComInterface;
import com.sun.jna.platform.win32.COM.util.annotation.ComMethod;
import com.sun.jna.platform.win32.COM.util.annotation.ComObject;
import com.sun.jna.platform.win32.COM.util.annotation.ComProperty;
import com.sun.jna.ptr.PointerByReference;
import org.junit.Assume;

public class EnumMoniker_Test {

    static {
        ClassLoader.getSystemClassLoader().setDefaultAssertionStatus(true);
    }

    @ComInterface(iid = "{00020970-0000-0000-C000-000000000046}")
    interface Application {

        @ComProperty
        boolean getVisible();

        @ComProperty
        void setVisible(boolean value);

        @ComMethod
        void Quit();
    }

    @ComObject(progId = "Word.Application")
    interface MsWordApp extends Application {
    }

    private ObjectFactory factory;
    private MsWordApp ob1;
    private MsWordApp ob2;
    private boolean initialized = false;

    @Before
    public void before() {
        // Check Existence of Word Application
        Assume.assumeTrue("Could not find registration", checkCOMRegistered("{00020970-0000-0000-C000-000000000046}"));
        COMUtils.checkRC(Ole32.INSTANCE.CoInitializeEx(Pointer.NULL, Ole32.COINIT_MULTITHREADED));
        initialized = true;
        this.factory = new ObjectFactory();
        // Two COM objects are require to be running for these tests to work
        this.ob1 = this.factory.createObject(MsWordApp.class);
        this.ob2 = this.factory.createObject(MsWordApp.class);
    }

    @After
    public void after() {
        if (ob1 != null) {
            ob1.Quit();
        }
        if (ob2 != null) {
            ob2.Quit();
        }
        if (factory != null) {
            factory.disposeAll();
        }
        if (initialized) {
            Ole32.INSTANCE.CoUninitialize();
        }
    }

    @Test
    public void Reset() {
        // GetRunningObjectTable
        PointerByReference pprot = new PointerByReference();
        HRESULT hr = Ole32.INSTANCE.GetRunningObjectTable(new DWORD(0), pprot);
        COMUtils.checkRC(hr);
        IRunningObjectTable rot = new RunningObjectTable(pprot.getValue());

        // EnumRunning
        PointerByReference ppenumMoniker = new PointerByReference();
        hr = rot.EnumRunning(ppenumMoniker);
        COMUtils.checkRC(hr);
        IEnumMoniker iterator = new EnumMoniker(ppenumMoniker.getValue());

        // Reset
        hr = iterator.Reset();
        COMUtils.checkRC(hr);

        // Next
        PointerByReference rgelt1 = new PointerByReference();
        ULONGByReference pceltFetched1 = new ULONGByReference();
        hr = iterator.Next(new ULONG(1), rgelt1, pceltFetched1);
        COMUtils.checkRC(hr);

        // Reset
        hr = iterator.Reset();
        COMUtils.checkRC(hr);

        // Next
        PointerByReference rgelt2 = new PointerByReference();
        ULONGByReference pceltFetched2 = new ULONGByReference();
        hr = iterator.Next(new ULONG(1), rgelt2, pceltFetched2);
        COMUtils.checkRC(hr);

        assertEquals(rgelt1.getValue(), rgelt2.getValue());
    }

    @Test
    public void Next() {
        // GetRunningObjectTable
        PointerByReference pprot = new PointerByReference();
        HRESULT hr = Ole32.INSTANCE.GetRunningObjectTable(new DWORD(0), pprot);
        COMUtils.checkRC(hr);
        IRunningObjectTable rot = new RunningObjectTable(pprot.getValue());

        // EnumRunning
        PointerByReference ppenumMoniker = new PointerByReference();
        hr = rot.EnumRunning(ppenumMoniker);
        COMUtils.checkRC(hr);
        IEnumMoniker iterator = new EnumMoniker(ppenumMoniker.getValue());

        // Reset
        hr = iterator.Reset();
        COMUtils.checkRC(hr);

        // Next
        PointerByReference rgelt1 = new PointerByReference();
        ULONGByReference pceltFetched1 = new ULONGByReference();
        hr = iterator.Next(new ULONG(1), rgelt1, pceltFetched1);
        COMUtils.checkRC(hr);

        // Next
        PointerByReference rgelt2 = new PointerByReference();
        ULONGByReference pceltFetched2 = new ULONGByReference();
        hr = iterator.Next(new ULONG(1), rgelt2, pceltFetched2);
        COMUtils.checkRC(hr);

        assertNotEquals(rgelt1.getValue(), rgelt2.getValue());
    }

    @Test
    public void Skip() {
        // GetRunningObjectTable
        PointerByReference pprot = new PointerByReference();
        HRESULT hr = Ole32.INSTANCE.GetRunningObjectTable(new DWORD(0), pprot);
        COMUtils.checkRC(hr);
        IRunningObjectTable rot = new RunningObjectTable(pprot.getValue());

        // EnumRunning
        PointerByReference ppenumMoniker = new PointerByReference();
        hr = rot.EnumRunning(ppenumMoniker);
        COMUtils.checkRC(hr);
        IEnumMoniker iterator = new EnumMoniker(ppenumMoniker.getValue());

        // Reset
        hr = iterator.Reset();
        COMUtils.checkRC(hr);

        // Next
        PointerByReference rgelt1 = new PointerByReference();
        ULONGByReference pceltFetched1 = new ULONGByReference();
        hr = iterator.Next(new ULONG(1), rgelt1, pceltFetched1);
        COMUtils.checkRC(hr);

        // Reset
        hr = iterator.Reset();
        COMUtils.checkRC(hr);

        // Skip
        hr = iterator.Skip(new ULONG(1));
        COMUtils.checkRC(hr);

        // Next
        PointerByReference rgelt2 = new PointerByReference();
        ULONGByReference pceltFetched2 = new ULONGByReference();
        hr = iterator.Next(new ULONG(1), rgelt2, pceltFetched2);
        COMUtils.checkRC(hr);

        assertNotEquals(rgelt1.getValue(), rgelt2.getValue());
    }

    @Test
    public void Clone() {
        // GetRunningObjectTable
        PointerByReference pprot = new PointerByReference();
        HRESULT hr = Ole32.INSTANCE.GetRunningObjectTable(new DWORD(0), pprot);
        COMUtils.checkRC(hr);
        IRunningObjectTable rot = new RunningObjectTable(pprot.getValue());

        // EnumRunning
        PointerByReference ppenumMoniker = new PointerByReference();
        hr = rot.EnumRunning(ppenumMoniker);
        COMUtils.checkRC(hr);
        IEnumMoniker iterator1 = new EnumMoniker(ppenumMoniker.getValue());

        // iterator1.Reset
        hr = iterator1.Reset();
        COMUtils.checkRC(hr);

        // iterator1.Next
        PointerByReference rgelt1 = new PointerByReference();
        ULONGByReference pceltFetched1 = new ULONGByReference();
        hr = iterator1.Next(new ULONG(1), rgelt1, pceltFetched1);
        COMUtils.checkRC(hr);

        // iterator1.Clone
        PointerByReference ppenum = new PointerByReference();
        hr = iterator1.Clone(ppenum);
        COMUtils.checkRC(hr);
        IEnumMoniker iterator2 = new EnumMoniker(ppenum.getValue());

        // iterator2.Next
        PointerByReference rgelt2 = new PointerByReference();
        ULONGByReference pceltFetched2 = new ULONGByReference();
        hr = iterator2.Next(new ULONG(1), rgelt2, pceltFetched2);
        COMUtils.checkRC(hr);

        assertNotEquals(rgelt1.getValue(), rgelt2.getValue());

        // iterator1.Next
        rgelt1 = new PointerByReference();
        pceltFetched1 = new ULONGByReference();
        hr = iterator1.Next(new ULONG(1), rgelt1, pceltFetched1);
        COMUtils.checkRC(hr);

        assertEquals(rgelt1.getValue(), rgelt2.getValue());
    }

}
