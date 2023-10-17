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
package com.sun.jna.platform.win32.COM.util;

import com.sun.jna.Pointer;
import static com.sun.jna.platform.win32.AbstractWin32TestSupport.checkCOMRegistered;
import com.sun.jna.platform.win32.COM.COMUtils;
import com.sun.jna.platform.win32.COM.util.annotation.ComInterface;
import com.sun.jna.platform.win32.COM.util.annotation.ComMethod;
import com.sun.jna.platform.win32.COM.util.annotation.ComObject;
import com.sun.jna.platform.win32.OaIdl.DATE;
import com.sun.jna.platform.win32.OaIdl.VARIANT_BOOL;
import com.sun.jna.platform.win32.Ole32;
import com.sun.jna.platform.win32.OleAuto;
import com.sun.jna.platform.win32.Variant;
import com.sun.jna.platform.win32.Variant.VARIANT;
import com.sun.jna.platform.win32.WTypes.BSTR;
import com.sun.jna.platform.win32.WinDef.BOOL;
import com.sun.jna.platform.win32.WinDef.BYTE;
import com.sun.jna.platform.win32.WinDef.CHAR;
import com.sun.jna.platform.win32.WinDef.LONG;
import com.sun.jna.platform.win32.WinDef.SHORT;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.junit.AfterClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Assume;
import org.junit.BeforeClass;

// Untested: IDispatch
// Untested: Proxy
public class ConvertTest {

    private static boolean initialized = false;
    private static ObjectFactory fact;

    @BeforeClass
    public static void init() {
        // Check that FileSystemObject is registered in the registry
        Assume.assumeTrue("Could not find registration", checkCOMRegistered("{0D43FE01-F093-11CF-8940-00A0C9054228}"));
        COMUtils.checkRC(Ole32.INSTANCE.CoInitializeEx(Pointer.NULL, Ole32.COINIT_MULTITHREADED));
        initialized = true;
        fact = new ObjectFactory();
    }

    @AfterClass
    public static void destruct() {
        if(fact != null) {
            fact.disposeAll();
        }
        fact = null;
        if(initialized) {
            Ole32.INSTANCE.CoUninitialize();
        }
    }

    @Test
    public void testConvertVariant() {
        VARIANT testValue = new Variant.VARIANT(42);
        VARIANT resultVariant = Convert.toVariant(testValue);
        assertSame(testValue, resultVariant);
        assertSame(testValue, Convert.toJavaObject(resultVariant, VARIANT.class, fact, false, false));
        assertSame(42, Convert.toJavaObject(testValue, Object.class, fact, false, false));
    }

    @Test
    public void testConvertString() {
        String testString = "Hallo";
        BSTR testValue = OleAuto.INSTANCE.SysAllocString(testString);
        VARIANT resultVariant = Convert.toVariant(testValue);
        assertEquals(testString, resultVariant.stringValue());
        assertEquals(testString, Convert.toJavaObject(resultVariant, Object.class, fact, false, false));
        assertEquals(testString, Convert.toJavaObject(resultVariant, String.class, fact, false, true));

        resultVariant = Convert.toVariant(testString);
        assertEquals(testString, resultVariant.stringValue());
        assertEquals(testString, Convert.toJavaObject(resultVariant, Object.class, fact, false, false));
        assertEquals(testString, Convert.toJavaObject(resultVariant, String.class, fact, false, true));
    }

    @Test
    public void testConvertBoolean() {
        VARIANT_BOOL testVariantBOOL = new VARIANT_BOOL(true);
        VARIANT resultVariantBOOL = Convert.toVariant(testVariantBOOL);
        assertEquals(true, resultVariantBOOL.booleanValue());
        assertEquals(true, Convert.toJavaObject(resultVariantBOOL, Object.class, fact, false, false));
        assertEquals(true, Convert.toJavaObject(resultVariantBOOL, Boolean.class, fact, false, false));
        assertEquals(true, Convert.toJavaObject(resultVariantBOOL, boolean.class, fact, false, false));

        BOOL testBOOL = new BOOL(true);
        VARIANT resultBOOL = Convert.toVariant(testBOOL);
        assertEquals(true, resultBOOL.booleanValue());
        assertEquals(true, Convert.toJavaObject(resultBOOL, Object.class, fact, false, false));
        assertEquals(true, Convert.toJavaObject(resultBOOL, Boolean.class, fact, false, false));
        assertEquals(true, Convert.toJavaObject(resultBOOL, boolean.class, fact, false, false));

        Boolean testBooleanObj = true;
        VARIANT resultBooleanObj = Convert.toVariant(testBooleanObj);
        boolean testBoolean = true;
        VARIANT resultBoolean = Convert.toVariant(testBoolean);

        assertEquals(true, resultBooleanObj.booleanValue());
        assertEquals(true, resultBoolean.booleanValue());
        assertEquals(true, Convert.toJavaObject(resultBooleanObj, Object.class, fact, false, false));
        assertEquals(true, Convert.toJavaObject(resultBoolean, Object.class, fact, false, false));
        assertEquals(true, Convert.toJavaObject(resultBooleanObj, boolean.class, fact, false, false));
        assertEquals(true, Convert.toJavaObject(resultBoolean, boolean.class, fact, false, false));
        assertEquals(true, Convert.toJavaObject(resultBooleanObj, Boolean.class, fact, false, false));
        assertEquals(true, Convert.toJavaObject(resultBoolean, Boolean.class, fact, false, false));
    }

    @Test
    public void testConvertIntTypes() {
        LONG testLONG = new LONG(42);
        VARIANT resultLONG = Convert.toVariant(testLONG);
        assertEquals(42, resultLONG.longValue());
        assertEquals(Integer.class, Convert.toJavaObject(resultLONG, Object.class, fact, false, false).getClass());
        assertEquals(42, Convert.toJavaObject(resultLONG, int.class, fact, false, false));
        assertEquals(42, Convert.toJavaObject(resultLONG, Integer.class, fact, false, false));

        SHORT testSHORT = new SHORT(42);
        VARIANT resultSHORT = Convert.toVariant(testSHORT);
        assertEquals(42, resultSHORT.longValue());
        assertEquals(Short.class, Convert.toJavaObject(resultSHORT, Object.class, fact, false, false).getClass());
        assertEquals((short) 42, Convert.toJavaObject(resultSHORT, short.class, fact, false, false));
        assertEquals((short) 42, Convert.toJavaObject(resultSHORT, Short.class, fact, false, false));

        BYTE testBYTE = new BYTE(42);
        VARIANT resultBYTE = Convert.toVariant(testBYTE);
        Byte testByteObj = 42;
        VARIANT resultByteObj = Convert.toVariant(testByteObj);
        byte testByte = 42;
        VARIANT resultByte = Convert.toVariant(testByte);

        assertEquals(42, resultBYTE.longValue());
        assertEquals(42, resultByteObj.longValue());
        assertEquals(42, resultByte.longValue());
        assertEquals(Byte.class, Convert.toJavaObject(resultBYTE, Object.class, fact, false, false).getClass());
        assertEquals(Byte.class, Convert.toJavaObject(resultByteObj, Object.class, fact, false, false).getClass());
        assertEquals(Byte.class, Convert.toJavaObject(resultByte, Object.class, fact, false, false).getClass());
        assertEquals((byte) 42, Convert.toJavaObject(resultBYTE, byte.class, fact, false, false));
        assertEquals((byte) 42, Convert.toJavaObject(resultByteObj, byte.class, fact, false, false));
        assertEquals((byte) 42, Convert.toJavaObject(resultByte, byte.class, fact, false, false));
        assertEquals((byte) 42, Convert.toJavaObject(resultBYTE, Byte.class, fact, false, false));
        assertEquals((byte) 42, Convert.toJavaObject(resultByteObj, Byte.class, fact, false, false));
        assertEquals((byte) 42, Convert.toJavaObject(resultByte, Byte.class, fact, false, false));

        Character testCharObj = 42;
        VARIANT resultCharObj = Convert.toVariant(testCharObj);
        char testChar = 42;
        VARIANT resultChar = Convert.toVariant(testChar);

        assertEquals(42, resultCharObj.longValue());
        assertEquals(42, resultChar.longValue());
        assertEquals(testCharObj, (Character) Convert.toJavaObject(resultCharObj, Object.class, fact, false, false));
        assertEquals(testCharObj, (Character) Convert.toJavaObject(resultChar, Object.class, fact, false, false));
        assertEquals(testCharObj, (Character) Convert.toJavaObject(resultCharObj, char.class, fact, false, false));
        assertEquals(testCharObj, (Character) Convert.toJavaObject(resultChar, char.class, fact, false, false));
        assertEquals(testCharObj, (Character) Convert.toJavaObject(resultCharObj, Character.class, fact, false, false));
        assertEquals(testCharObj, (Character) Convert.toJavaObject(resultChar, Character.class, fact, false, false));

        CHAR testCHAR = new CHAR(42);
        VARIANT resultCHAR = Convert.toVariant(testCHAR);

        assertEquals(42, resultCHAR.longValue());
        assertEquals((byte) 42, Convert.toJavaObject(resultCHAR, Object.class, fact, false, false));
        assertEquals((byte) 42, Convert.toJavaObject(resultCHAR, byte.class, fact, false, false));
        assertEquals((byte) 42, Convert.toJavaObject(resultCHAR, Byte.class, fact, false, false));

        Short testShortObj = 42;
        VARIANT resultShortObj = Convert.toVariant(testShortObj);
        short testShort = 42;
        VARIANT resultShort = Convert.toVariant(testShort);

        assertEquals(42, resultShortObj.longValue());
        assertEquals(42, resultShort.longValue());
        assertEquals((short) 42, Convert.toJavaObject(resultShortObj, Object.class, fact, false, false));
        assertEquals((short) 42, Convert.toJavaObject(resultShort, Object.class, fact, false, false));
        assertEquals((short) 42, Convert.toJavaObject(resultShortObj, short.class, fact, false, false));
        assertEquals((short) 42, Convert.toJavaObject(resultShort, short.class, fact, false, false));
        assertEquals((short) 42, Convert.toJavaObject(resultShortObj, Short.class, fact, false, false));
        assertEquals((short) 42, Convert.toJavaObject(resultShort, Short.class, fact, false, false));

        Integer testIntegerObj = 42;
        VARIANT resultIntegerObj = Convert.toVariant(testIntegerObj);
        int testInteger = 42;
        VARIANT resultInteger = Convert.toVariant(testInteger);

        assertEquals(42, resultIntegerObj.longValue());
        assertEquals(42, resultInteger.longValue());
        assertEquals((int) 42, Convert.toJavaObject(resultIntegerObj, Object.class, fact, false, false));
        assertEquals((int) 42, Convert.toJavaObject(resultInteger, Object.class, fact, false, false));
        assertEquals((int) 42, Convert.toJavaObject(resultIntegerObj, int.class, fact, false, false));
        assertEquals((int) 42, Convert.toJavaObject(resultInteger, int.class, fact, false, false));
        assertEquals((int) 42, Convert.toJavaObject(resultIntegerObj, Integer.class, fact, false, false));
        assertEquals((int) 42, Convert.toJavaObject(resultInteger, Integer.class, fact, false, false));

        Long testLongObj = 42L;
        VARIANT resultLongObj = Convert.toVariant(testLongObj);
        long testLong = 42;
        VARIANT resultLong = Convert.toVariant(testLong);

        assertEquals(42, resultLongObj.longValue());
        assertEquals(42, resultLong.longValue());
        assertEquals((long) 42, Convert.toJavaObject(resultLongObj, Object.class, fact, false, false));
        assertEquals((long) 42, Convert.toJavaObject(resultLong, Object.class, fact, false, false));
        assertEquals((long) 42, Convert.toJavaObject(resultLongObj, long.class, fact, false, false));
        assertEquals((long) 42, Convert.toJavaObject(resultLong, long.class, fact, false, false));
        assertEquals((long) 42, Convert.toJavaObject(resultLongObj, Long.class, fact, false, false));
        assertEquals((long) 42, Convert.toJavaObject(resultLong, Long.class, fact, false, false));
    }

    @Test
    public void testConvertFloat() {
        Float testFloatObj = 42.23f;
        VARIANT resultFloatObj = Convert.toVariant(testFloatObj);
        float testFloat = 42.23f;
        VARIANT resultFloat = Convert.toVariant(testFloat);

        assertEquals(42.23f, resultFloatObj.floatValue(), 0.01);
        assertEquals(42.23f, resultFloat.floatValue(), 0.01);
        assertEquals(42.23d, resultFloat.doubleValue(), 0.01);
        assertEquals(42.23f, (Float) Convert.toJavaObject(resultFloatObj, Object.class, fact, false, false), 0.01);
        assertEquals(42.23f, (Float) Convert.toJavaObject(resultFloat, Object.class, fact, false, false), 0.01);
        assertEquals(42.23f, (Float) Convert.toJavaObject(resultFloatObj, float.class, fact, false, false), 0.01);
        assertEquals(42.23f, (Float) Convert.toJavaObject(resultFloat, float.class, fact, false, false), 0.01);
        assertEquals(42.23f, (Float) Convert.toJavaObject(resultFloatObj, Float.class, fact, false, false), 0.01);
        assertEquals(42.23f, (Float) Convert.toJavaObject(resultFloat, Float.class, fact, false, false), 0.01);
        assertEquals(42.23d, (Double) Convert.toJavaObject(resultFloat, double.class, fact, false, false), 0.01);

        Double testDoubleObj = 42.23;
        VARIANT resultDoubleObj = Convert.toVariant(testDoubleObj);
        double testDouble = 42.23;
        VARIANT resultDouble = Convert.toVariant(testDouble);

        assertEquals(42.23, resultDoubleObj.doubleValue(), 0.01);
        assertEquals(42.23, resultDouble.doubleValue(), 0.01);
        assertEquals(42.23f, resultDouble.floatValue(), 0.01);
        assertEquals(42.23, (Double) Convert.toJavaObject(resultDoubleObj, Object.class, fact, false, false), 0.01);
        assertEquals(42.23, (Double) Convert.toJavaObject(resultDouble, Object.class, fact, false, false), 0.01);
        assertEquals(42.23, (Double) Convert.toJavaObject(resultDoubleObj, double.class, fact, false, false), 0.01);
        assertEquals(42.23, (Double) Convert.toJavaObject(resultDouble, double.class, fact, false, false), 0.01);
        assertEquals(42.23, (Double) Convert.toJavaObject(resultDoubleObj, Double.class, fact, false, false), 0.01);
        assertEquals(42.23, (Double) Convert.toJavaObject(resultDouble, Double.class, fact, false, false), 0.01);
        assertEquals(42.23f, (Float) Convert.toJavaObject(resultDouble, float.class, fact, false, false), 0.01);
    }

    @Test
    @SuppressWarnings("deprecation")
    public void testConvertDate() {
        testConvertDate(new Date(2015 - 1900, 1, 1, 9, 0, 0));
    }

    @Test
    @SuppressWarnings("deprecation")
    public void testConvertDstOffsetTime() {
        TimeZone timeZone = TimeZone.getDefault();
        try {
            // Use a timezone with a DST offset
            TimeZone.setDefault(TimeZone.getTimeZone("PST"));
            // Use a date in the DST period, and a time in the DST offset window
            testConvertDate(new Date(2015 - 1900, 8 - 1, 1, 0, 30, 0));
        } finally {
            TimeZone.setDefault(timeZone);
        }
    }

    @Test
    @SuppressWarnings("deprecation")
    public void testConvertMillisecondTime() {
        testConvertDate(new Date(2015 - 1900, 1, 1, 0, 0, 0), 1);
        testConvertDate(new Date(2015 - 1900, 1, 1, 0, 0, 0), 499);
        testConvertDate(new Date(2015 - 1900, 1, 1, 0, 0, 0), 500);
        testConvertDate(new Date(2015 - 1900, 1, 1, 0, 0, 0), 999);
        testConvertDate(new Date(1815 - 1900, 1, 1, 0, 0, 0), 1);
        testConvertDate(new Date(1815 - 1900, 1, 1, 0, 0, 0), 499);
        testConvertDate(new Date(1815 - 1900, 1, 1, 0, 0, 0), 500);
        testConvertDate(new Date(1815 - 1900, 1, 1, 0, 0, 0), 999);
        testConvertDate(new Date(2015 - 1900, 1, 1, 23, 59, 59), 1);
        testConvertDate(new Date(2015 - 1900, 1, 1, 23, 59, 59), 499);
        testConvertDate(new Date(2015 - 1900, 1, 1, 23, 59, 59), 500);
        testConvertDate(new Date(2015 - 1900, 1, 1, 23, 59, 59), 999);
        testConvertDate(new Date(1815 - 1900, 1, 1, 23, 59, 59), 1);
        testConvertDate(new Date(1815 - 1900, 1, 1, 23, 59, 59), 499);
        testConvertDate(new Date(1815 - 1900, 1, 1, 23, 59, 59), 500);
        testConvertDate(new Date(1815 - 1900, 1, 1, 23, 59, 59), 999);
    }

    private static void testConvertDate(Date date, int milliseconds) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.MILLISECOND, milliseconds);
        testConvertDate(calendar.getTime());
    }

    private static void testConvertDate(Date testDate) {
        VARIANT resultDate = Convert.toVariant(testDate);
        DATE testDATE = new DATE(testDate);
        VARIANT resultDATE = Convert.toVariant(testDATE);

        assertEquals(testDate, resultDate.dateValue());
        assertEquals(testDate, resultDATE.dateValue());
        assertEquals(testDate, Convert.toJavaObject(resultDate, Object.class, fact, false, false));
        assertEquals(testDate, Convert.toJavaObject(resultDATE, Object.class, fact, false, false));
        assertEquals(testDate, Convert.toJavaObject(resultDate, Date.class, fact, false, false));
        assertEquals(testDate, Convert.toJavaObject(resultDATE, Date.class, fact, false, false));
    }

    @Test
    public void testConvertEnum() {
        TestEnum testEnum = TestEnum.Val2;
        VARIANT resultEnum = Convert.toVariant(testEnum);
        assertEquals((int) testEnum.getValue(), resultEnum.intValue());
        assertEquals((int) testEnum.getValue(), Convert.toJavaObject(resultEnum, Object.class, fact, false, false));
        assertEquals(testEnum, Convert.toJavaObject(resultEnum, TestEnum.class, fact, false, false));
    }

    @Test
    public void testReturnPrimitiveVoid() {
        FileSystemObject app = fact.createObject(FileSystemObject.class);
        // It is assumed that "C" is the holy drive letter, that will
        // always be present
        assertTrue(app.DriveExistsPrimitive("C:"));
        assertTrue(app.DriveExistsObject("C:"));
        app.DriveExistsVoid("C:");
    }
}

@ComObject(clsId = "{0D43FE01-F093-11CF-8940-00A0C9054228}")
interface FileSystemObject extends IFileSystem3 {
}

@ComInterface(iid = "{2A0B9D10-4B87-11D3-A97A-00104B365C9F}")
interface IFileSystem3 extends IUnknown, IConnectionPoint {

    @ComMethod(dispId = 0x0000271f)
    boolean DriveExistsPrimitive(String driveName);

    @ComMethod(dispId = 0x0000271f)
    Boolean DriveExistsObject(String driveName);

    @ComMethod(dispId = 0x0000271f)
    void DriveExistsVoid(String driveName);
}

enum TestEnum implements IComEnum {
    Val1(1),
    Val2(2),
    Val3(3),;

    long value;

    private TestEnum(long val) {
        this.value = val;
    }

    @Override
    public long getValue() {
        return this.value;
    }

}
