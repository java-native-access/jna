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

import junit.framework.TestCase;

import com.sun.jna.platform.win32.OaIdl.DATE;
import com.sun.jna.platform.win32.OaIdl.SAFEARRAY;
import com.sun.jna.platform.win32.OaIdl.VARIANT_BOOL;
import com.sun.jna.platform.win32.Variant.VARIANT;
import com.sun.jna.platform.win32.WTypes.BSTR;
import com.sun.jna.platform.win32.WTypes.VARTYPE;
import com.sun.jna.platform.win32.WinBase.SYSTEMTIME;
import com.sun.jna.platform.win32.WinDef.BOOL;
import com.sun.jna.platform.win32.WinDef.BYTE;
import com.sun.jna.platform.win32.WinDef.CHAR;
import com.sun.jna.platform.win32.WinDef.LONG;
import com.sun.jna.platform.win32.WinDef.LONGLONG;
import com.sun.jna.platform.win32.WinDef.SHORT;
import com.sun.jna.platform.win32.WinDef.USHORT;
import com.sun.jna.platform.win32.WinNT.HRESULT;
import com.sun.jna.ptr.DoubleByReference;
import java.util.Date;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class VariantTest extends TestCase {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(VariantTest.class);
    }

    public VariantTest() {
        super();
    }

    public void testVariantClear() {
        VARIANT variant = new VARIANT(new SHORT(33333));
        HRESULT hr = OleAuto.INSTANCE.VariantClear(variant);

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
        SYSTEMTIME lpTestSystemTime = new SYSTEMTIME();
        Kernel32.INSTANCE.GetLocalTime(lpTestSystemTime);

        // SystemTimeToVariantTime and VariantTimeToSystemTime truncate/round off millis
        lpTestSystemTime.wMilliseconds = 0;

        DoubleByReference pvtime = new DoubleByReference();
        OleAuto.INSTANCE.SystemTimeToVariantTime(lpTestSystemTime, pvtime);

        VARIANT variantDate = new VARIANT(new DATE(pvtime.getValue()));

        SYSTEMTIME lpResultSystemTime = new SYSTEMTIME();
        OleAuto.INSTANCE.VariantTimeToSystemTime(pvtime.getValue(), lpResultSystemTime);
        assertEquals(lpTestSystemTime.toCalendar(), lpResultSystemTime.toCalendar());
    }

    public void testVariantRecord() {
        VARIANT._VARIANT.__VARIANT.BRECORD pvRecord = new VARIANT._VARIANT.__VARIANT.BRECORD();
        VARIANT._VARIANT.__VARIANT.BRECORD pvRecord2;

        VARIANT variant = new VARIANT();
        variant.setValue(Variant.VT_RECORD, pvRecord);

        pvRecord2 = (VARIANT._VARIANT.__VARIANT.BRECORD)variant.getValue();
    }

    @SuppressWarnings("deprecation")
    public void testDATECalculation() {
        // Samples from MSDN to ensure correct implementation
        // Definition is to be found here: https://msdn.microsoft.com/de-de/library/82ab7w69.aspx

        // From Date to DATE
        assertThat(new DATE(new Date(1899 - 1900, 12 - 1, 27, 0, 0, 0)).date, equalTo(-3.00d));
        assertThat(new DATE(new Date(1899 - 1900, 12 - 1, 28, 12, 0, 0)).date, equalTo(-2.50d));
        assertThat(new DATE(new Date(1899 - 1900, 12 - 1, 28, 0, 0, 0)).date, equalTo(-2.00d));
        assertThat(new DATE(new Date(1899 - 1900, 12 - 1, 29, 0, 0, 0)).date, equalTo(-1.00d));
        assertThat(new DATE(new Date(1899 - 1900, 12 - 1, 30, 0, 0, 0)).date, equalTo(0.00d));
        assertThat(new DATE(new Date(1899 - 1900, 12 - 1, 30, 6, 0, 0)).date, equalTo(0.25d));
        assertThat(new DATE(new Date(1899 - 1900, 12 - 1, 30, 12, 0, 0)).date, equalTo(0.50d));
        assertThat(new DATE(new Date(1899 - 1900, 12 - 1, 30, 18, 0, 0)).date, equalTo(0.75d));
        assertThat(new DATE(new Date(1899 - 1900, 12 - 1, 31, 0, 0, 0)).date, equalTo(1.0d));
        assertThat(new DATE(new Date(1900 - 1900, 1 - 1, 1, 0, 0, 0)).date, equalTo(2.00d));
        assertThat(new DATE(new Date(1900 - 1900, 1 - 1, 1, 12, 0, 0)).date, equalTo(2.50d));
        assertThat(new DATE(new Date(1900 - 1900, 1 - 1, 2, 0, 0, 0)).date, equalTo(3.00d));
        assertThat(new DATE(new Date(1900 - 1900, 1 - 1, 4, 0, 0, 0)).date, equalTo(5.00d));
        assertThat(new DATE(new Date(1900 - 1900, 1 - 1, 4, 6, 0, 0)).date, equalTo(5.25d));
        assertThat(new DATE(new Date(1900 - 1900, 1 - 1, 4, 12, 0, 0)).date, equalTo(5.50d));
        assertThat(new DATE(new Date(1900 - 1900, 1 - 1, 4, 21, 0, 0)).date, equalTo(5.875d));

        // From DATE to Date
        assertThat(new DATE(-3.00d).getAsJavaDate(), equalTo(new Date(1899 - 1900, 12 - 1, 27, 0, 0, 0)));
        assertThat(new DATE(-2.50d).getAsJavaDate(), equalTo(new Date(1899 - 1900, 12 - 1, 28, 12, 0, 0)));
        assertThat(new DATE(-2.00d).getAsJavaDate(), equalTo(new Date(1899 - 1900, 12 - 1, 28, 0, 0, 0)));
        assertThat(new DATE(-1.00d).getAsJavaDate(), equalTo(new Date(1899 - 1900, 12 - 1, 29, 0, 0, 0)));
        assertThat(new DATE(-0.75d).getAsJavaDate(), equalTo(new Date(1899 - 1900, 12 - 1, 30, 18, 0, 0)));
        assertThat(new DATE(-0.50d).getAsJavaDate(), equalTo(new Date(1899 - 1900, 12 - 1, 30, 12, 0, 0)));
        assertThat(new DATE(-0.25d).getAsJavaDate(), equalTo(new Date(1899 - 1900, 12 - 1, 30, 6, 0, 0)));
        assertThat(new DATE(0.00d).getAsJavaDate(), equalTo(new Date(1899 - 1900, 12 - 1, 30, 0, 0, 0)));
        assertThat(new DATE(0.25d).getAsJavaDate(), equalTo(new Date(1899 - 1900, 12 - 1, 30, 6, 0, 0)));
        assertThat(new DATE(0.50d).getAsJavaDate(), equalTo(new Date(1899 - 1900, 12 - 1, 30, 12, 0, 0)));
        assertThat(new DATE(0.75d).getAsJavaDate(), equalTo(new Date(1899 - 1900, 12 - 1, 30, 18, 0, 0)));
        assertThat(new DATE(1.0d).getAsJavaDate(), equalTo(new Date(1899 - 1900, 12 - 1, 31, 0, 0, 0)));
        assertThat(new DATE(2.00d).getAsJavaDate(), equalTo(new Date(1900 - 1900, 1 - 1, 1, 0, 0, 0)));
        assertThat(new DATE(2.50d).getAsJavaDate(), equalTo(new Date(1900 - 1900, 1 - 1, 1, 12, 0, 0)));
        assertThat(new DATE(3.00d).getAsJavaDate(), equalTo(new Date(1900 - 1900, 1 - 1, 2, 0, 0, 0)));
        assertThat(new DATE(5.00d).getAsJavaDate(), equalTo(new Date(1900 - 1900, 1 - 1, 4, 0, 0, 0)));
        assertThat(new DATE(5.25d).getAsJavaDate(), equalTo(new Date(1900 - 1900, 1 - 1, 4, 6, 0, 0)));
        assertThat(new DATE(5.50d).getAsJavaDate(), equalTo(new Date(1900 - 1900, 1 - 1, 4, 12, 0, 0)));
        assertThat(new DATE(5.875d).getAsJavaDate(), equalTo(new Date(1900 - 1900, 1 - 1, 4, 21, 0, 0)));

        // Test roundtripping with sub-hour resolution
        // This test requires equality, in spite of this MSDN:
        // https://msdn.microsoft.com/en-us/library/aa393691.aspx
        // Date was chosen from the example that made the problem visible in testing
        Date testDate = new Date(2016 - 1900, 10 - 1, 12, 2, 59, 19);
        assertEquals("java.util.Date -> com.sun.jna.platform.win32.OaIdl.DATE -> java.util.Date roundtrip failed",
                     testDate, new DATE(testDate).getAsJavaDate());
    }

    @SuppressWarnings("deprecation")
    public void testVariantConstructors() {
        VARIANT variant;

        // skipped: BSTRByReference constructor
        // skipped: empty constructor
        // skipped: pointer constructor
        // skipped: IDispatch constructor
        String testString = "TeST$ö";
        BSTR bstr = OleAuto.INSTANCE.SysAllocString(testString);

        variant = new VARIANT(bstr);
        assertThat(variant.getValue(), instanceOf(BSTR.class));
        assertThat(((BSTR)variant.getValue()).getValue(), equalTo(testString));
        assertThat(variant.stringValue(), equalTo(testString));

        variant = new VARIANT(testString);
        assertThat(variant.getValue(), instanceOf(BSTR.class));
        assertThat(((BSTR)variant.getValue()).getValue(), equalTo(testString));
        assertThat(variant.stringValue(), equalTo(testString));

        OleAuto.INSTANCE.SysFreeString(bstr);
        OleAuto.INSTANCE.SysFreeString((BSTR) variant.getValue());

        BOOL boolTrue = new WinDef.BOOL(true);

        variant = new VARIANT(Variant.VARIANT_TRUE);
        assertThat(variant.getValue(), instanceOf(VARIANT_BOOL.class));
        assertThat(((VARIANT_BOOL) variant.getValue()).shortValue(), equalTo((short) 0xFFFF));
        assertThat(variant.booleanValue(), equalTo(true));

        variant = new VARIANT(boolTrue);
        assertThat(variant.getValue(), instanceOf(VARIANT_BOOL.class));
        assertThat(((VARIANT_BOOL) variant.getValue()).shortValue(), equalTo((short) 0xFFFF));
        assertThat(variant.booleanValue(), equalTo(true));

        int testInt = 4223;
        LONG testIntWin = new LONG(testInt);
        variant = new VARIANT(testIntWin);
        assertThat(variant.getValue(), instanceOf(LONG.class));
        assertThat(((LONG) variant.getValue()).intValue(), equalTo(testInt));
        assertThat(variant.intValue(), equalTo(testInt));

        variant = new VARIANT(testInt);
        assertThat(variant.getValue(), instanceOf(LONG.class));
        assertThat(((LONG) variant.getValue()).intValue(), equalTo(testInt));
        assertThat(variant.intValue(), equalTo(testInt));

        short testShort = 23;
        SHORT testShortWin = new SHORT(testShort);
        variant = new VARIANT(testShortWin);
        assertThat(variant.getValue(), instanceOf(SHORT.class));
        assertThat(((SHORT) variant.getValue()).shortValue(), equalTo(testShort));
        assertThat(variant.shortValue(), equalTo(testShort));

        variant = new VARIANT(testShort);
        assertThat(variant.getValue(), instanceOf(SHORT.class));
        assertThat(((SHORT) variant.getValue()).shortValue(), equalTo(testShort));
        assertThat(variant.shortValue(), equalTo(testShort));

        long testLong = 4223L + Integer.MAX_VALUE;

        variant = new VARIANT(testLong);
        assertThat(variant.getValue(), instanceOf(LONGLONG.class));
        assertThat(((LONGLONG) variant.getValue()).longValue(), equalTo(testLong));
        assertThat(variant.longValue(), equalTo(testLong));

        Date testDate = new Date(2042 - 1900, 2, 3, 23, 0, 0);
        variant = new VARIANT(testDate);
        assertThat(variant.getValue(), instanceOf(DATE.class));
        assertThat(variant.dateValue(), equalTo(testDate));

        byte testByte = 42;
        BYTE testByteWin = new BYTE(testByte);
        CHAR testByteWin2 = new CHAR(testByte);
        variant = new VARIANT(testByte);
        assertThat(variant.getValue(), instanceOf(BYTE.class));
        assertThat(((BYTE) variant.getValue()).byteValue(), equalTo(testByte));
        assertThat(variant.byteValue(), equalTo(testByte));

        variant = new VARIANT(testByteWin);
        assertThat(variant.getValue(), instanceOf(BYTE.class));
        assertThat(((BYTE) variant.getValue()).byteValue(), equalTo(testByte));
        assertThat(variant.byteValue(), equalTo(testByte));

        variant = new VARIANT(testByteWin2);
        assertThat(variant.getValue(), instanceOf(CHAR.class));
        assertThat(((CHAR) variant.getValue()).byteValue(), equalTo(testByte));
        assertThat(variant.byteValue(), equalTo(testByte));

        variant = new VARIANT(testByteWin2);
        assertThat(variant.getValue(), instanceOf(CHAR.class));
        assertThat(((CHAR) variant.getValue()).byteValue(), equalTo(testByte));
        assertThat(variant.byteValue(), equalTo(testByte));

        double testDouble = 42.23;
        variant = new VARIANT(testDouble);
        assertThat(variant.getValue(), instanceOf(Double.class));
        // If this fails introduce comparison with range
        assertThat(variant.doubleValue(), equalTo(testDouble));

        float testFloat = 42.23f;
        variant = new VARIANT(testFloat);
        assertThat(variant.getValue(), instanceOf(Float.class));
        // If this fails introduce comparison with range
        assertThat(variant.floatValue(), equalTo(testFloat));

        char testChar = 42 + Short.MAX_VALUE;

        variant = new VARIANT(testChar);
        assertThat(variant.getValue(), instanceOf(USHORT.class));
        assertThat(((USHORT) variant.getValue()).intValue(), equalTo((int) testChar));
        assertThat(variant.intValue(), equalTo((int) testChar));
    }

    public void testVariantSafearrayWrapping() {
        SAFEARRAY safearray = OaIdl.SAFEARRAY.createSafeArray(new VARTYPE(Variant.VT_I1), 5);
        try {
            VARIANT variant = new VARIANT(safearray);
            assertThat(variant.getVarType().intValue(), equalTo((int) (Variant.VT_I1 | Variant.VT_ARRAY)));
            Object wrappedValue = variant.getValue();
            assertThat(wrappedValue, instanceOf(SAFEARRAY.class));
            assertThat(safearray.getUBound(0), is(4));
        } finally {
            safearray.destroy();
        }
    }
}
