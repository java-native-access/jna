/* This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package com.sun.jna.platform.win32;

import com.sun.jna.ptr.IntByReference;
import junit.framework.TestCase;

public class MsiTest extends TestCase {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(MsiTest.class);
    }

    public void testMsiEnumComponents() {
        char[] componentBuffer = new char[40];
        assertTrue(W32Errors.ERROR_SUCCESS == Msi.INSTANCE.MsiEnumComponents(new WinDef.DWORD(0), componentBuffer));
        String component = new String(componentBuffer).trim();
        assertTrue(component.length() > 0);
    }

    public void testMsiGetProductCodeW() {
        char[] componentBuffer = new char[40];
        assertTrue(W32Errors.ERROR_SUCCESS == Msi.INSTANCE.MsiEnumComponents(new WinDef.DWORD(0), componentBuffer));
        String component = new String(componentBuffer).trim();

        char[] productBuffer = new char[40];
        assertTrue(W32Errors.ERROR_SUCCESS == Msi.INSTANCE.MsiGetProductCode(component, productBuffer));

        String product = new String(productBuffer).trim();
        assertTrue(product.length() > 0);
    }

    public void testMsiLocateComponentW() {
        char[] componentBuffer = new char[40];
        assertTrue(W32Errors.ERROR_SUCCESS == Msi.INSTANCE.MsiEnumComponents(new WinDef.DWORD(0), componentBuffer));
        String component = new String(componentBuffer).trim();

        char[] pathBuffer = new char[WinDef.MAX_PATH];
        IntByReference pathBufferSize = new IntByReference(pathBuffer.length);
        Msi.INSTANCE.MsiLocateComponent(component, pathBuffer, pathBufferSize);

        String path = new String(pathBuffer, 0, pathBufferSize.getValue()).trim();
        assertTrue(path.length() > 0);
    }

    public void testMsiGetComponentPathW() {
        char[] componentBuffer = new char[40];
        assertTrue(W32Errors.ERROR_SUCCESS == Msi.INSTANCE.MsiEnumComponents(new WinDef.DWORD(0), componentBuffer));
        String component = new String(componentBuffer).trim();

        char[] productBuffer = new char[40];
        assertTrue(W32Errors.ERROR_SUCCESS == Msi.INSTANCE.MsiGetProductCode(component, productBuffer));

        String product = new String(productBuffer).trim();
        assertTrue(product.length() > 0);

        char[] pathBuffer = new char[WinDef.MAX_PATH];
        IntByReference pathBufferSize = new IntByReference(pathBuffer.length);
        Msi.INSTANCE.MsiGetComponentPath(product, component, pathBuffer, pathBufferSize);

        String path = new String(pathBuffer, 0, pathBufferSize.getValue()).trim();
        assertTrue(path.length() > 0);
    }
}
