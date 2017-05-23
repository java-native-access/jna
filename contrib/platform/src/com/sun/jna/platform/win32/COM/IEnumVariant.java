/* Copyright (c) 2017 Matthias Bläsing, All Rights Reserved
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

import com.sun.jna.platform.win32.Variant.VARIANT;

/**
 * Provides a method for enumerating a collection of variants, including
 * heterogeneous collections of objects and intrinsic types. Callers of this
 * interface do not need to know the specific type (or types) of the elements in
 * the collection.
 */
public interface IEnumVariant extends IUnknown {

    /**
     * Creates a copy of the current state of enumeration. 
     * 
     * @return clone of the backing enumeration
     */
    IEnumVariant Clone();

    /**
     * Retrieves the specified items in the enumeration sequence.
     * 
     * <p>Count is the upper limit and less values can be retrieved.</p>
     * 
     * @param count maximum number of elements to retrieve
     * @return array of VARIANTs
     */
    VARIANT[] Next(int count);

    /**
     * Resets the enumeration sequence to the beginning.
     */
    void Reset();

    /**
     * Attempts to skip over the next celt elements in the enumeration sequence.
     * 
     * @param count  elements to skip
     */
    void Skip(int count);

}
