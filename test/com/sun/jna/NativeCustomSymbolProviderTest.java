/* Copyright (c) 2022 Carlos Ballesteros, All Rights Reserved
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
package com.sun.jna;

import junit.framework.TestCase;

import java.util.Collections;

public class NativeCustomSymbolProviderTest extends TestCase implements Paths {
    public static void main(java.lang.String[] argList) {
        junit.textui.TestRunner.run(NativeCustomSymbolProviderTest.class);
    }

    interface MathInterfaceWithSymbolProvider extends Library {

        double sin(double x);
        double cos(double x);

    }

    static class MathLibraryWithSymbolProvider {

        public static native double sin(double x);
        public static native double cos(double x);

        static {
            Native.register(MathLibraryWithSymbolProvider.class, NativeLibrary.getInstance(Platform.MATH_LIBRARY_NAME, Collections.singletonMap(
                    Library.OPTION_SYMBOL_PROVIDER,
                    new SymbolProvider() {
                        @Override
                        public long getSymbolAddress(long handle, String name, SymbolProvider parent) {
                            if (name.equals("sin")) {
                                return parent.getSymbolAddress(handle, "cos", null);
                            } else {
                                return parent.getSymbolAddress(handle, "sin", null);
                            }
                        }
                    }
            )));
        }
    }

    MathInterfaceWithSymbolProvider lib;
    MathInterfaceWithSymbolProvider libCustom;

    @Override
    protected void setUp() {
        lib = Native.load(Platform.MATH_LIBRARY_NAME, MathInterfaceWithSymbolProvider.class);
        libCustom = Native.load(
                Platform.MATH_LIBRARY_NAME,
                MathInterfaceWithSymbolProvider.class, Collections.singletonMap(
                        Library.OPTION_SYMBOL_PROVIDER,
                        new SymbolProvider() {
                            @Override
                            public long getSymbolAddress(long handle, String name, SymbolProvider parent) {
                                if (name.equals("sin")) {
                                    return parent.getSymbolAddress(handle, "cos", null);
                                } else {
                                    return parent.getSymbolAddress(handle, "sin", null);
                                }
                            }
                        }
                )
        );
    }

    @Override
    protected void tearDown() {
        lib = null;
        libCustom = null;
    }


    public void testDirectMappingSymbolProvider() {
        assertEquals(lib.cos(0.0), MathLibraryWithSymbolProvider.sin(0.0));
        assertEquals(lib.sin(0.0), MathLibraryWithSymbolProvider.cos(0.0));
    }

    public void testInterfaceCustomSymbolProvider() {
        assertEquals(lib.cos(0.0), libCustom.sin(0.0));
        assertEquals(lib.sin(0.0), libCustom.cos(0.0));
    }
}
