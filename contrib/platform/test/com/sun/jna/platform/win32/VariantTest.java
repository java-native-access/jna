package com.sun.jna.platform.win32;

import junit.framework.TestCase;

import com.sun.jna.platform.win32.Guid.GUID;
import com.sun.jna.platform.win32.Variant.VARIANT;
import com.sun.jna.platform.win32.WinDef.HWND;

public class VariantTest extends TestCase {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(VariantTest.class);
	}

	public VariantTest() {
	}

	public void testVariant() {
		VARIANT variant = new VARIANT(33333);
		System.out.println(variant.toString(false));

		VARIANT variant2 = new VARIANT(variant.getPointer());
		System.out.println(variant2.toString(false));
	}
}
