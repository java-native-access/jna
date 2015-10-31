package com.sun.jna.platform.win32.COM;

import java.util.Iterator;
import java.util.NoSuchElementException;

import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;
import com.sun.jna.platform.win32.OleAuto;
import com.sun.jna.platform.win32.Variant;
import com.sun.jna.platform.win32.Variant.VARIANT;
import com.sun.jna.platform.win32.WinDef.LONG;
import com.sun.jna.platform.win32.WinDef.LONGLONG;

import junit.framework.TestCase;

public class ShellApplicationWindowsTest extends TestCase {

	@Override
	protected void setUp() throws Exception {
		String[] commands = {"cmd", "/c", "start", "iexplore.exe","-nohome", "\"http://www.srh.noaa.gov/jax\""};
		Runtime.getRuntime().exec(commands);
		Thread.sleep(30000);
	}

	public void testWindowsCount() {
        System.out.println("java.vendor: " + System.getProperty("java.vendor"));
        System.out.println("java.version: " + System.getProperty("java.version"));
        System.out.println("os.version: " + System.getProperty("os.version"));
        System.out.println("os.arch: " + System.getProperty("os.arch"));
        System.out.println("os.name: " + System.getProperty("os.name"));
        
        ShellApplication sa = new ShellApplication();
        System.out.println("Shell.Application.Windows found: " + sa.Windows().Count());
        assertTrue(sa.Windows().Count() > 0);
        for (InternetExplorer ie : sa.Windows())
        {            
            System.out.println();
            System.out.println("============================================");
            if (ie != null)
            {
                System.out.println("Title: " + ie.getTitle());
                System.out.println("URL: " + ie.getURL());
                System.out.println("App Name: " + ie.getName());
                System.out.println("Window Handle: " + ie.getWindowHandle());
            }
            else
            {
                System.out.println("Null encountered.");
            }
            System.out.println("============================================");
            System.out.println();
        }
	}
	
	@Override
	protected void tearDown() throws Exception {
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
         * @return the title of the browser window. This may not always be the title of the main document
         */
        public String getTitle()
        {
            return getStringProperty("LocationName");
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
        
        /**
         * IWebBrowser2::get_Name<br>
         * Read-only COM property.<br>
         * 
         * @return the frame name or application name of the object.
         */
        public String getName()
        {
            return getStringProperty("Name");
        }
        
        /**
         * IWebBrowser2::HWND<br>
         * Read-only COM property.<br>
         * 
         * @return the handle of the Internet Explorer main window.
         */
        public long getWindowHandle()
        {
            VARIANT.ByReference result = new VARIANT.ByReference();
            this.oleMethod(OleAuto.DISPATCH_PROPERTYGET, result, this.getIDispatch(), "HWND");
            
            if (result.getVarType().intValue() == Variant.VT_I8)
            {
                return ((LONGLONG) result.getValue()).longValue();
            }
            return ((LONG) result.getValue()).longValue();
            
        }
    }

	
}
