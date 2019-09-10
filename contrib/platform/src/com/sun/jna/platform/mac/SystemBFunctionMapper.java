/*
 * Copyright (c) 2019 Daniel Widdis
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
package com.sun.jna.platform.mac;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;

import com.sun.jna.FunctionMapper;
import com.sun.jna.NativeLibrary;

public class SystemBFunctionMapper implements FunctionMapper {
    private static Collection<String> mappedFunctions = Arrays.asList("mach_host_self_ptr", "mach_task_self_ptr",
            "host_page_size_ptr", "host_statistics_ptr", "host_statistics64_ptr", "host_processor_info_ptr");

    /**
     * Removes the _ptr suffix from methods which use the properly sized pointer
     * rather than 32-bit int.
     */
    @Override
    public String getFunctionName(NativeLibrary library, Method method) {
        String name = method.getName();
        if (mappedFunctions.contains(name)) {
            return name.substring(0, name.length() - 4);
        }
        return name;
    }
}
