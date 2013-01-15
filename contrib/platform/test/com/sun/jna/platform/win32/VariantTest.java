package com.sun.jna.platform.win32;

import junit.framework.TestCase;

import com.sun.jna.platform.win32.Variant.VARIANT;
import com.sun.jna.platform.win32.WinDef.SHORT;

public class VariantTest extends TestCase {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(VariantTest.class);
	}

	public VariantTest() {
	}

	public void testVariant() {
		VARIANT variantSource = new VARIANT(new SHORT(33333));
		VARIANT.ByReference variantDest = new VARIANT.ByReference();

		System.out.println(variantSource.toString(true));
		OleAut32.INSTANCE.VariantCopy(variantDest, variantSource);
		System.out.println(variantDest.toString(true));
	}
}
