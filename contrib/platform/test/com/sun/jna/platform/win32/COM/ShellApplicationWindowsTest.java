package com.sun.jna.platform.win32.COM;

import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Ole32;
import java.util.Iterator;
import java.util.NoSuchElementException;

import com.sun.jna.platform.win32.Variant;
import com.sun.jna.platform.win32.Variant.VARIANT;
import com.sun.jna.platform.win32.WinDef.LONG;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ShellApplicationWindowsTest {

    static {
        ClassLoader.getSystemClassLoader().setDefaultAssertionStatus(true);
    }

    @Before
    public void setUp() throws Exception
    {
        Ole32.INSTANCE.CoInitializeEx(Pointer.NULL, Ole32.COINIT_MULTITHREADED);
        
        // Launch IE in a manner that should ensure it opens even if the system default browser is Chrome, Firefox, or something else.
    	Runtime.getRuntime().exec("cmd /c start iexplore.exe -nohome \"about:blank\"");

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
