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
		VARIANT[] variantArr = new VARIANT[1];
		VARIANT variant = new VARIANT(Variant.VT_I1);
		variant._variant.__variant.iVal = 1;
		variantArr[0] = variant;
		
		
	}
	
}
