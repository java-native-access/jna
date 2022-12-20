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

/**
 * Interface to define a custom symbol provider.
 *
 * This can be used for method hooking, or special
 * classes like direct mapping the Win32 OpenGL.
 */
public interface SymbolProvider {
    /**
     * Gets the address of a symbol by its name and the handle of the library.
     *
     * @param handle Handle of the original library
     * @param name Name of the symbol to load
     * @param parent Parent symbol provider
     *
     * @return Address of the symbol, typically a function.
     */
    long getSymbolAddress(long handle, String name, SymbolProvider parent);
}
