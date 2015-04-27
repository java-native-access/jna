/* Copyright (c) 2007 Timothy Wall, All Rights Reserved
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package com.sun.jna.platform;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.rules.TestName;

/**
 * @author lgoldstein
 */
public abstract class AbstractPlatformTestSupport extends Assert {
    @Rule public final TestName TEST_NAME_HOLDER=new TestName();

    protected AbstractPlatformTestSupport() {
        super();
    }

    public final String getCurrentTestName() {
        return TEST_NAME_HOLDER.getMethodName();
    }
}
