package com.sun.jna.different_package;

import com.sun.jna.*;
import junit.framework.TestCase;

public class PrivateDirectCallbacksTest extends TestCase {
    private interface PrivateWithCallbackArgumentTestLibrary extends Library {
        PrivateWithCallbackArgumentTestLibrary INSTANCE = new DirectPrivateWithCallbackArgumentTestLibrary();

        interface VoidCallback extends Callback {
            void callback();
        }

        void callVoidCallback(VoidCallback c);
    }

    private static class DirectPrivateWithCallbackArgumentTestLibrary implements PrivateWithCallbackArgumentTestLibrary {
        @Override
        public native void callVoidCallback(VoidCallback c);

        static {
            Native.register("testlib");
        }
    }

    public void testCallVoidCallback() {
        final boolean[] called = {false};
        PrivateWithCallbackArgumentTestLibrary.VoidCallback cb = new PrivateWithCallbackArgumentTestLibrary.VoidCallback() {
            @Override
            public void callback() {
                called[0] = true;
            }
        };
        PrivateWithCallbackArgumentTestLibrary.INSTANCE.callVoidCallback(cb);
        assertTrue("Callback not called", called[0]);
    }

    private interface PrivateWithCallbackReturnTestLibrary extends Library {
        PrivateWithCallbackReturnTestLibrary INSTANCE = new DirectPrivateWithCallbackReturnTestLibrary();

        interface Int32CallbackX extends Callback {
            int callback(int arg);
        }

        Int32CallbackX returnCallback();

        Int32CallbackX returnCallbackArgument(Int32CallbackX cb);
    }

    private static class DirectPrivateWithCallbackReturnTestLibrary implements PrivateWithCallbackReturnTestLibrary {
        @Override
        public native Int32CallbackX returnCallback();

        @Override
        public native Int32CallbackX returnCallbackArgument(Int32CallbackX cb);

        static {
            Native.register("testlib");
        }
    }

    public void testInvokeCallback() {
        PrivateWithCallbackReturnTestLibrary.Int32CallbackX cb = PrivateWithCallbackReturnTestLibrary.INSTANCE.returnCallback();
        assertNotNull("Callback should not be null", cb);
        assertEquals("Callback should be callable", 1, cb.callback(1));

        PrivateWithCallbackReturnTestLibrary.Int32CallbackX cb2 = new PrivateWithCallbackReturnTestLibrary.Int32CallbackX() {
            @Override
            public int callback(int arg) {
                return 0;
            }
        };
        assertSame("Java callback should be looked up",
                cb2, PrivateWithCallbackReturnTestLibrary.INSTANCE.returnCallbackArgument(cb2));
        assertSame("Existing native function wrapper should be reused",
                cb, PrivateWithCallbackReturnTestLibrary.INSTANCE.returnCallbackArgument(cb));
    }

    public static void main(java.lang.String[] argList) {
        junit.textui.TestRunner.run(PrivateDirectCallbacksTest.class);
    }
}
