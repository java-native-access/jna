package com.sun.jna.different_package;

import com.sun.jna.Callback;
import com.sun.jna.Library;
import com.sun.jna.Native;
import junit.framework.TestCase;

public class PrivateLibraryInfoTest extends TestCase {
    private interface TestLibrary extends Library {
        @SuppressWarnings("unused")
        TestLibrary INSTANCE = new TestLibrary() {
        };

        interface VoidCallback extends Callback {
            void callback();
        }
    }

    public void testLibraryInfo() {
        assertTrue(Native.getLibraryOptions(TestLibrary.class).containsKey(Library.OPTION_TYPE_MAPPER));
    }

    public void testCallbackLibraryInfo() {
        TestLibrary.VoidCallback cb = new TestLibrary.VoidCallback() {
            @Override
            public void callback() {
            }
        };
        assertTrue(Native.getLibraryOptions(cb.getClass()).containsKey(Library.OPTION_TYPE_MAPPER));
    }
}
