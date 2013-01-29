package com.sun.jna.platform.win32;

import junit.framework.TestCase;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.Variant.VARIANT;
import com.sun.jna.platform.win32.WinDef.SHORT;
import com.sun.jna.platform.win32.WinNT.HRESULT;

public class VariantTest extends TestCase {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(VariantTest.class);
	}

	public VariantTest() {
		Native.setProtected(true);
	}

	public void testVariantClear() {
		System.out.println("------------------------------------------");

		VARIANT variant = new VARIANT(new SHORT(33333));
		HRESULT hr = OleAuto.INSTANCE.VariantClear(variant.getPointer());
		
		assertTrue("hr: " + hr.intValue(), hr.intValue() == 0);
		
		System.out.println(variant.toString(true));
		System.out.println("------------------------------------------");
	}
	
	public void testVariantCopyShort() {
		System.out.println("------------------------------------------");

		VARIANT variantSource = new VARIANT(new SHORT(33333));
		VARIANT variantDest = new VARIANT();
		
		System.out.println(variantSource.toString(true));
		HRESULT hr = OleAuto.INSTANCE.VariantCopy(variantDest.getPointer(), variantSource);
		
		assertTrue("hr: " + hr.intValue(), hr.intValue() == 0);

		System.out.println(variantDest.toString(true));
		System.out.println("variant type  :" + variantDest.getVarType());
		System.out.println("variant value :" + variantDest.getValue());
		System.out.println("------------------------------------------");
	}
	
	public void testVariantCopyBoolean() {
		System.out.println("------------------------------------------");

		VARIANT variantSource = new VARIANT(Variant.VARIANT_TRUE);
		VARIANT variantDest = new VARIANT();
		
		System.out.println(variantSource.toString(true));
		HRESULT hr = OleAuto.INSTANCE.VariantCopy(variantDest.getPointer(), variantSource);
		
		assertTrue("hr: " + hr.intValue(), hr.intValue() == 0);
		
		System.out.println(variantDest.toString(true));
		System.out.println("variant type  :" + variantDest.getVarType());
		System.out.println("variant value :" + variantDest.getValue());
		System.out.println("------------------------------------------");
	}
}
