/* Copyright (c) 2020 Florian Kistner, All Rights Reserved
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
