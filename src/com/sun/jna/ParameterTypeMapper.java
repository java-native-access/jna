/* Copyright (c) 2020 Torbjörn Svensson, All Rights Reserved
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

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Map a function parameter to a new type
 *
 * @author Torbj&ouml;rn Svensson, azoff[at]svenskalinuxforeninen.se
 */
@Documented
@Retention(RUNTIME)
@Target(METHOD)
public @interface ParameterTypeMapper {
    /**
     * The type of each paramter to convert to
     * WARNING: The length of the array must same as for {@link #indexes()}
     *
     * @return Array of new type(s) for parameter(s)
     */
    Class<?>[] types();

    /**
     * The index of the parameter(s) to convert
     * WARNING: The length of the array must same as for {@link #types()}
     *
     * @return Array of index(es) of the parameter(s) to convert
     */
    int[] indexes();
}
