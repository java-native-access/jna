package com.sun.jna.platform.win32;

import junit.framework.TestCase;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.OaIdl.DATE;
import com.sun.jna.platform.win32.Variant.VARIANT;
import com.sun.jna.platform.win32.WinBase.SYSTEMTIME;
import com.sun.jna.platform.win32.WinDef.SHORT;
import com.sun.jna.platform.win32.WinNT.HRESULT;
import com.sun.jna.ptr.DoubleByReference;

public class VariantTest extends TestCase {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(VariantTest.class);
    }

    public VariantTest() {
    }

    public void testVariantClear() {
        VARIANT variant = new VARIANT(new SHORT(33333));
        HRESULT hr = OleAuto.INSTANCE.VariantClear(variant.getPointer());

        assertTrue("hr: " + hr.intValue(), hr.intValue() == 0);
    }

    public void testVariantCopyShort() {
        VARIANT variantSource = new VARIANT(new SHORT(33333));
        VARIANT variantDest = new VARIANT();

        //System.out.println(variantSource.toString(true));
        HRESULT hr = OleAuto.INSTANCE.VariantCopy(variantDest.getPointer(),
                                                  variantSource);

        assertTrue("hr: " + hr.intValue(), hr.intValue() == 0);
    }

    public void testVariantCopyBoolean() {
        VARIANT variantSource = new VARIANT(Variant.VARIANT_TRUE);
        VARIANT variantDest = new VARIANT();

        HRESULT hr = OleAuto.INSTANCE.VariantCopy(variantDest.getPointer(),
                                                  variantSource);

        assertTrue("hr: " + hr.intValue(), hr.intValue() == 0);
    }

    public void testVariantDate() {
        SYSTEMTIME lpSystemTime = new SYSTEMTIME();
        Kernel32.INSTANCE.GetLocalTime(lpSystemTime);

        DoubleByReference pvtime = new DoubleByReference();
        OleAuto.INSTANCE.SystemTimeToVariantTime(lpSystemTime, pvtime);

        VARIANT variantDate = new VARIANT(new DATE(pvtime.getValue()));
    }
    
    public void testVariantRecord() {
        VARIANT._VARIANT.__VARIANT.BRECORD pvRecord = new VARIANT._VARIANT.__VARIANT.BRECORD(); 
        VARIANT._VARIANT.__VARIANT.BRECORD pvRecord2;
        
        VARIANT variant = new VARIANT();
        variant.setValue(Variant.VT_RECORD, pvRecord);
        
        pvRecord2 = (VARIANT._VARIANT.__VARIANT.BRECORD)variant.getValue();
    }
    
}
