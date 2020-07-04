/*
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
package com.sun.jna.platform.win32;

import java.util.Arrays;

import com.sun.jna.ptr.IntByReference;
import com.sun.jna.win32.W32StringUtil;

import junit.framework.TestCase;

public class MsiTest extends TestCase {
    public static void main(String[] args) {
        junit.textui.TestRunner.run(MsiTest.class);
    }

    public void testMsiEnumComponents() {
        char[] componentBuffer = new char[40];
        assertEquals("MsiEnumComponents", W32Errors.ERROR_SUCCESS, Msi.INSTANCE.MsiEnumComponents(new WinDef.DWORD(0), componentBuffer));
        String component = W32StringUtil.toString(componentBuffer);
        assertFalse("Component is empty", component.isEmpty());
    }

    public void testMsiGetProductCodeW() {
        char[] componentBuffer = new char[40];
        assertEquals("MsiEnumComponents", W32Errors.ERROR_SUCCESS, Msi.INSTANCE.MsiEnumComponents(new WinDef.DWORD(0), componentBuffer));
        String component = W32StringUtil.toString(componentBuffer);
        assertFalse("Component is empty", component.isEmpty());

        char[] productBuffer = new char[40];
        assertEquals("MsiGetProductCode", W32Errors.ERROR_SUCCESS, Msi.INSTANCE.MsiGetProductCode(component, productBuffer));

        String product = W32StringUtil.toString(productBuffer);
        assertFalse("Product is empty", product.isEmpty());
    }

    public void testMsiLocateComponentW() {
        char[] componentBuffer = new char[40];
        assertEquals("MsiEnumComponents", W32Errors.ERROR_SUCCESS, Msi.INSTANCE.MsiEnumComponents(new WinDef.DWORD(0), componentBuffer));
        String component = W32StringUtil.toString(componentBuffer);
        assertFalse("Component is empty", component.isEmpty());

        char[] pathBuffer = new char[WinDef.MAX_PATH];
        IntByReference pathBufferSize = new IntByReference(pathBuffer.length);
        int installState = Msi.INSTANCE.MsiLocateComponent(component, pathBuffer, pathBufferSize);
        assertTrue(String.format("MsiLocateComponent returned %d for component %s", installState, component),
                Arrays.asList(Msi.INSTALLSTATE_LOCAL, Msi.INSTALLSTATE_SOURCE).contains(installState));

        String path = new String(pathBuffer, 0, pathBufferSize.getValue()).trim();
        assertFalse("Path is empty", path.isEmpty());
    }

    public void testMsiGetComponentPathW() {
        char[] componentBuffer = new char[40];
        assertEquals("MsiEnumComponents", W32Errors.ERROR_SUCCESS, Msi.INSTANCE.MsiEnumComponents(new WinDef.DWORD(0), componentBuffer));
        String component = W32StringUtil.toString(componentBuffer);
        assertFalse("Component is empty", component.isEmpty());

        char[] productBuffer = new char[40];
        assertEquals("MsiGetProductCode", W32Errors.ERROR_SUCCESS,  Msi.INSTANCE.MsiGetProductCode(component, productBuffer));

        String product = W32StringUtil.toString(productBuffer);
        assertFalse("Product is empty", product.isEmpty());

        char[] pathBuffer = new char[WinDef.MAX_PATH];
        IntByReference pathBufferSize = new IntByReference(pathBuffer.length);
        int installState = Msi.INSTANCE.MsiGetComponentPath(product, component, pathBuffer, pathBufferSize);
        assertTrue(String.format("MsiGetComponentPath returned %d for component %s in product %s", installState, component, product),
                Arrays.asList(Msi.INSTALLSTATE_LOCAL, Msi.INSTALLSTATE_SOURCE).contains(installState));

        String path = new String(pathBuffer, 0, pathBufferSize.getValue()).trim();
        assertFalse("Path is empty", path.isEmpty());
    }
}
