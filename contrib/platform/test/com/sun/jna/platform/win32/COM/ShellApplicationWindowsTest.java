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
package com.sun.jna.platform.win32.COM;

import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Guid;
import com.sun.jna.platform.win32.Ole32;
import com.sun.jna.platform.win32.OleAuto;
import com.sun.jna.platform.win32.Variant;
import com.sun.jna.platform.win32.Variant.VARIANT;
import com.sun.jna.platform.win32.WTypes;
import com.sun.jna.platform.win32.WinDef.LONG;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.PointerByReference;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ShellApplicationWindowsTest {

    private static final Guid.CLSID CLSID_InternetExplorer = new Guid.CLSID("{0002DF01-0000-0000-C000-000000000046}");

    static {
        ClassLoader.getSystemClassLoader().setDefaultAssertionStatus(true);
    }

    private PointerByReference ieApp;
    private Dispatch ieDispatch;

    @Before
    public void setUp() throws Exception {
        WinNT.HRESULT hr;

        hr = Ole32.INSTANCE.CoInitializeEx(Pointer.NULL, Ole32.COINIT_MULTITHREADED);
        COMUtils.checkRC(hr);

        // IE can not be launched directly anymore - so load it via COM

        ieApp = new PointerByReference();
        hr = Ole32.INSTANCE
                .CoCreateInstance(CLSID_InternetExplorer, null, WTypes.CLSCTX_SERVER, IDispatch.IID_IDISPATCH, ieApp);
        COMUtils.checkRC(hr);

        ieDispatch = new Dispatch(ieApp.getValue());
        InternetExplorer ie = new InternetExplorer(ieDispatch);

        ie.setProperty("Visible", true);
        COMUtils.checkRC(hr);

        VARIANT url = new VARIANT("about:blank");
        VARIANT result = ie.invoke("Navigate", url);
        OleAuto.INSTANCE.VariantClear(url);
        OleAuto.INSTANCE.VariantClear(result);

        ieDispatch.Release();

        // Even when going to "about:blank", IE still needs a few seconds to start up and add itself to Shell.Application.Windows
        // Removing this delay will cause the test to fail even on the fastest boxes I can find.
        Thread.sleep(3000);
    }

    @Test
    public void testWindowsCount()
    {
        ShellApplication sa = new ShellApplication();

        // IE is open, so there should be at least one present.
        // More may exist if Windows Explorer windows are open.
        assertTrue("No shell application windows found",
                   sa.Windows().Count() > 0);

        boolean pageFound = false;
        for (InternetExplorer ie : sa.Windows())
        {
            // For reasons unknown, Shell.Application.Windows can have null members inside it.
            // All I care about is whether or not the collection contains the window I opened.
            if (ie != null && "about:blank".equals(ie.getURL()))
            {
                pageFound = true;
            }
        }

        // Finally, did we find our page in the collection?
        assertTrue("No IE page was found", pageFound);

        Ole32.INSTANCE.CoUninitialize();
    }

    @After
    public void tearDown() throws Exception
    {
        Ole32.INSTANCE.CoUninitialize();
        Runtime.getRuntime().exec("taskkill.exe /f /im iexplore.exe");
    }

    /**
     * A COM representation of the Windows shell.
     */
    private static class ShellApplication extends COMLateBindingObject
    {
        public ShellApplication() throws COMException
        {
            super("Shell.Application", false);
        }

        /**
         * @return Creates and returns a ShellWindows object.<br>
         *         This object represents a collection of all of the open windows that belong to the Shell.
         */
        public ShellWindows Windows()
        {
            return new ShellWindows((IDispatch) invoke("Windows").getValue());
        }

        /**
         * Represents a collection of the open windows that belong to the Shell.<br>
         * Methods associated with this objects can control and execute commands within the Shell, and obtain other Shell-related objects.
         */
        public static class ShellWindows extends COMLateBindingObject implements Iterable<InternetExplorer>
        {

            private static class ShellWindowsIterator implements Iterator<InternetExplorer>
            {

                private ShellWindows source;

                private int          count;

                private int          max;

                public ShellWindowsIterator(ShellWindows collection)
                {
                    source = collection;
                    max = source.Count();
                }

                @Override
                public boolean hasNext()
                {
                    return count < max;
                }

                @Override
                public InternetExplorer next()
                {
                    if (!hasNext())
                    {
                        throw new NoSuchElementException();
                    }
                    return source.Item(count++);
                }

                @Override
                public void remove()
                {
                    throw new UnsupportedOperationException();
                }

            }

            public ShellWindows(IDispatch iDispatch)
            {
                super(iDispatch);
            }

            /**
             * Retrieves an InternetExplorer object that represents the Shell window.
             *
             * @param idx
             *            The zero-based index of the item to retrieve.<br>
             *            This value must be less than the value of the Count property.
             * @return an InternetExplorer object that represents the Shell window.
             */
            public InternetExplorer Item(int idx)
            {
                VARIANT arg = new VARIANT();
                arg.setValue(Variant.VT_I4, new LONG(idx));
                IDispatch result = (IDispatch) invoke("Item", arg).getValue();
                if (result == null)
                {
                    return null;
                }
                return new InternetExplorer(result);
            }

            /**
             * @return the number of items in the collection.
             */
            public int Count()
            {
                return getIntProperty("Count");
            }

            @SuppressWarnings({"unchecked", "rawtypes"})
            @Override
            public Iterator iterator()
            {
                return new ShellWindowsIterator(this);
            }
        }

    }

    /**
     * InternetExplorer / IWebBrowser2 - see http://msdn.microsoft.com/en-us/library/aa752127(v=vs.85).aspx
     */
    private static class InternetExplorer extends COMLateBindingObject
    {

        public InternetExplorer(IDispatch iDispatch)
        {
            super(iDispatch);
        }

        /**
         * IWebBrowser2::get_LocationURL<br>
         * Read-only COM property.<br>
         *
         * @return the URL of the resource that is currently displayed.
         */
        public String getURL()
        {
            return getStringProperty("LocationURL");
        }
    }
}
