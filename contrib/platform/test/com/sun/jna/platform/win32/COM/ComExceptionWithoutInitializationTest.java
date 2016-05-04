package com.sun.jna.platform.win32.COM;

import com.sun.jna.platform.win32.Guid;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class ComExceptionWithoutInitializationTest {

    @Test
    public void testCorrectExceptionOnFailedInitialization() {
        String message = null;
        try {
            InternetExplorer ie = new InternetExplorer();
        } catch (COMException ex) {
            message = ex.getMessage();
        }
        
        // This invocation must raise an exception, as the COM thread is not
        // initialized, in the message it is expected, that the HRESULT is reported
        // and the HRESULT resulting from calling into COM with it being initialized
        // is 800401f0. The message is also expected to point the to correct
        // initialization via CoInitialize
        assertNotNull(message);
        assertTrue(message.contains("HRESULT"));
        assertTrue(message.contains("800401f0"));
        assertTrue(message.contains("CoInitialize"));
    }

    /**
     * InternetExplorer / IWebBrowser2 - see
     * http://msdn.microsoft.com/en-us/library/aa752127(v=vs.85).aspx
     */
    private static class InternetExplorer extends COMLateBindingObject {

        public InternetExplorer(IDispatch iDispatch) {
            super(iDispatch);
        }

        public InternetExplorer() {
            super(new Guid.CLSID("{0002DF01-0000-0000-C000-000000000046}"), true);
        }
        
        /**
         * IWebBrowser2::get_LocationURL<br>
         * Read-only COM property.<br>
         *
         * @return the URL of the resource that is currently displayed.
         */
        public String getURL() {
            return getStringProperty("LocationURL");
        }
    }
}
