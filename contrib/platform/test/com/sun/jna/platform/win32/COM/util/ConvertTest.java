package com.sun.jna.platform.win32.COM.util;

import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.OaIdl.DATE;
import com.sun.jna.platform.win32.OaIdl.VARIANT_BOOL;
import com.sun.jna.platform.win32.Ole32;
import com.sun.jna.platform.win32.Variant;
import com.sun.jna.platform.win32.Variant.VARIANT;
import com.sun.jna.platform.win32.WTypes.BSTR;
import com.sun.jna.platform.win32.WinDef.BOOL;
import com.sun.jna.platform.win32.WinDef.BYTE;
import com.sun.jna.platform.win32.WinDef.CHAR;
import com.sun.jna.platform.win32.WinDef.LONG;
import com.sun.jna.platform.win32.WinDef.SHORT;
import java.util.Date;
import org.junit.AfterClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.BeforeClass;

// Untested: IDispatch
// Untested: Proxy
public class ConvertTest {

    private static Factory fact;

    @BeforeClass
    public static void init() {
        Ole32.INSTANCE.CoInitializeEx(Pointer.NULL, Ole32.COINIT_MULTITHREADED);
        fact = new Factory();
    }

    @AfterClass
    public static void destruct() {
        fact.disposeAll();
        fact = null;
        Ole32.INSTANCE.CoUninitialize();
    }

    @Test
    public void convertVariant() {
        VARIANT testValue = new Variant.VARIANT(42);
        VARIANT resultVariant = Convert.toVariant(testValue);
        assertSame(testValue, resultVariant);
        assertSame(testValue, Convert.toJavaObject(resultVariant, VARIANT.class, fact, true));
        assertSame(42, Convert.toJavaObject(testValue, Object.class, fact, true));
    }

    @Test
    public void convertString() {
        // This test leaks the allocated BSTR -- this is tollerated here, as memory usage is minimal
        String testString = "Hallo";
        BSTR testValue = new BSTR(testString);
        VARIANT resultVariant = Convert.toVariant(testValue);
        assertEquals(testString, resultVariant.stringValue());
        assertEquals(testString, Convert.toJavaObject(resultVariant, Object.class, fact, true));
        assertEquals(testString, Convert.toJavaObject(resultVariant, String.class, fact, true));
        
        resultVariant = Convert.toVariant(testString);
        assertEquals(testString, resultVariant.stringValue());
        assertEquals(testString, Convert.toJavaObject(resultVariant, Object.class, fact, true));
        assertEquals(testString, Convert.toJavaObject(resultVariant, String.class, fact, true));
    }

    @Test
    public void convertBoolean() {
        VARIANT_BOOL testVariantBOOL = new VARIANT_BOOL(true);
        VARIANT resultVariantBOOL = Convert.toVariant(testVariantBOOL);
        assertEquals(true, resultVariantBOOL.booleanValue());
        assertEquals(true, Convert.toJavaObject(resultVariantBOOL, Object.class, fact, true));
        assertEquals(true, Convert.toJavaObject(resultVariantBOOL, Boolean.class, fact, true));
        assertEquals(true, Convert.toJavaObject(resultVariantBOOL, boolean.class, fact, true));
     
        BOOL testBOOL = new BOOL(true);
        VARIANT resultBOOL = Convert.toVariant(testBOOL);
        assertEquals(true, resultBOOL.booleanValue());
        assertEquals(true, Convert.toJavaObject(resultBOOL, Object.class, fact, true));
        assertEquals(true, Convert.toJavaObject(resultBOOL, Boolean.class, fact, true));
        assertEquals(true, Convert.toJavaObject(resultBOOL, boolean.class, fact, true));

        Boolean testBooleanObj = true;
        VARIANT resultBooleanObj = Convert.toVariant(testBooleanObj);
        boolean testBoolean = true;
        VARIANT resultBoolean = Convert.toVariant(testBoolean);
        
        assertEquals(true, resultBooleanObj.booleanValue());
        assertEquals(true, resultBoolean.booleanValue());
        assertEquals(true, Convert.toJavaObject(resultBooleanObj, Object.class, fact, true));
        assertEquals(true, Convert.toJavaObject(resultBoolean, Object.class, fact, true));
        assertEquals(true, Convert.toJavaObject(resultBooleanObj, boolean.class, fact, true));
        assertEquals(true, Convert.toJavaObject(resultBoolean, boolean.class, fact, true));
        assertEquals(true, Convert.toJavaObject(resultBooleanObj, Boolean.class, fact, true));
        assertEquals(true, Convert.toJavaObject(resultBoolean, Boolean.class, fact, true));
    }
    
    @Test
    public void convertIntTypes() {
        LONG testLONG = new LONG(42);
        VARIANT resultLONG = Convert.toVariant(testLONG);
        assertEquals(42, resultLONG.longValue());
        assertEquals(Integer.class, Convert.toJavaObject(resultLONG, Object.class, fact, true).getClass());
        assertEquals(42, Convert.toJavaObject(resultLONG, int.class, fact, true));
        assertEquals(42, Convert.toJavaObject(resultLONG, Integer.class, fact, true));

        SHORT testSHORT = new SHORT(42);
        VARIANT resultSHORT = Convert.toVariant(testSHORT);
        assertEquals(42, resultSHORT.longValue());
        assertEquals(Short.class, Convert.toJavaObject(resultSHORT, Object.class, fact, true).getClass());
        assertEquals((short) 42, Convert.toJavaObject(resultSHORT, short.class, fact, true));
        assertEquals((short) 42, Convert.toJavaObject(resultSHORT, Short.class, fact, true));
        
        BYTE testBYTE = new BYTE(42);
        VARIANT resultBYTE = Convert.toVariant(testBYTE);
        Byte testByteObj = 42;
        VARIANT resultByteObj = Convert.toVariant(testByteObj);
        byte testByte = 42;
        VARIANT resultByte = Convert.toVariant(testByte);

        assertEquals(42, resultBYTE.longValue());
        assertEquals(42, resultByteObj.longValue());
        assertEquals(42, resultByte.longValue());
        assertEquals(Byte.class, Convert.toJavaObject(resultBYTE, Object.class, fact, true).getClass());
        assertEquals(Byte.class, Convert.toJavaObject(resultByteObj, Object.class, fact, true).getClass());
        assertEquals(Byte.class, Convert.toJavaObject(resultByte, Object.class, fact, true).getClass());
        assertEquals((byte) 42, Convert.toJavaObject(resultBYTE, byte.class, fact, true));
        assertEquals((byte) 42, Convert.toJavaObject(resultByteObj, byte.class, fact, true));
        assertEquals((byte) 42, Convert.toJavaObject(resultByte, byte.class, fact, true));
        assertEquals((byte) 42, Convert.toJavaObject(resultBYTE, Byte.class, fact, true));
        assertEquals((byte) 42, Convert.toJavaObject(resultByteObj, Byte.class, fact, true));
        assertEquals((byte) 42, Convert.toJavaObject(resultByte, Byte.class, fact, true));

        Character testCharObj = 42;
        VARIANT resultCharObj = Convert.toVariant(testCharObj);
        char testChar = 42;
        VARIANT resultChar = Convert.toVariant(testChar);
        
        assertEquals(42, resultCharObj.longValue());
        assertEquals(42, resultChar.longValue());
        assertEquals(testCharObj, (Character) Convert.toJavaObject(resultCharObj, Object.class, fact, true));
        assertEquals(testCharObj, (Character) Convert.toJavaObject(resultChar, Object.class, fact, true));
        assertEquals(testCharObj, (Character) Convert.toJavaObject(resultCharObj, char.class, fact, true));
        assertEquals(testCharObj, (Character) Convert.toJavaObject(resultChar, char.class, fact, true));
        assertEquals(testCharObj, (Character) Convert.toJavaObject(resultCharObj, Character.class, fact, true));
        assertEquals(testCharObj, (Character) Convert.toJavaObject(resultChar, Character.class, fact, true));


        CHAR testCHAR = new CHAR(42);
        VARIANT resultCHAR = Convert.toVariant(testCHAR);

        assertEquals(42, resultCHAR.longValue());
        assertEquals((byte) 42, Convert.toJavaObject(resultCHAR, Object.class, fact, true));
        assertEquals((byte) 42, Convert.toJavaObject(resultCHAR, byte.class, fact, true));
        assertEquals((byte) 42, Convert.toJavaObject(resultCHAR, Byte.class, fact, true));

        Short testShortObj = 42;
        VARIANT resultShortObj = Convert.toVariant(testShortObj);
        short testShort = 42;
        VARIANT resultShort = Convert.toVariant(testShort);

        assertEquals(42, resultShortObj.longValue());
        assertEquals(42, resultShort.longValue());
        assertEquals((short) 42, Convert.toJavaObject(resultShortObj, Object.class, fact, true));
        assertEquals((short) 42, Convert.toJavaObject(resultShort, Object.class, fact, true));
        assertEquals((short) 42, Convert.toJavaObject(resultShortObj, short.class, fact, true));
        assertEquals((short) 42, Convert.toJavaObject(resultShort, short.class, fact, true));
        assertEquals((short) 42, Convert.toJavaObject(resultShortObj, Short.class, fact, true));
        assertEquals((short) 42, Convert.toJavaObject(resultShort, Short.class, fact, true));

        Integer testIntegerObj = 42;
        VARIANT resultIntegerObj = Convert.toVariant(testIntegerObj);
        int testInteger = 42;
        VARIANT resultInteger = Convert.toVariant(testInteger);

        assertEquals(42, resultIntegerObj.longValue());
        assertEquals(42, resultInteger.longValue());
        assertEquals((int) 42, Convert.toJavaObject(resultIntegerObj, Object.class, fact, true));
        assertEquals((int) 42, Convert.toJavaObject(resultInteger, Object.class, fact, true));
        assertEquals((int) 42, Convert.toJavaObject(resultIntegerObj, int.class, fact, true));
        assertEquals((int) 42, Convert.toJavaObject(resultInteger, int.class, fact, true));
        assertEquals((int) 42, Convert.toJavaObject(resultIntegerObj, Integer.class, fact, true));
        assertEquals((int) 42, Convert.toJavaObject(resultInteger, Integer.class, fact, true));

        Long testLongObj = 42L;
        VARIANT resultLongObj = Convert.toVariant(testLongObj);
        long testLong = 42;
        VARIANT resultLong = Convert.toVariant(testLong);

        assertEquals(42, resultLongObj.longValue());
        assertEquals(42, resultLong.longValue());
        assertEquals((long) 42, Convert.toJavaObject(resultLongObj, Object.class, fact, true));
        assertEquals((long) 42, Convert.toJavaObject(resultLong, Object.class, fact, true));
        assertEquals((long) 42, Convert.toJavaObject(resultLongObj, long.class, fact, true));
        assertEquals((long) 42, Convert.toJavaObject(resultLong, long.class, fact, true));
        assertEquals((long) 42, Convert.toJavaObject(resultLongObj, Long.class, fact, true));
        assertEquals((long) 42, Convert.toJavaObject(resultLong, Long.class, fact, true));
    }
    
    @Test
    public void convertFloat() {
        Float testFloatObj = 42.23f;
        VARIANT resultFloatObj = Convert.toVariant(testFloatObj);
        float testFloat = 42.23f;
        VARIANT resultFloat = Convert.toVariant(testFloat);
        
        assertEquals(42.23f, resultFloatObj.floatValue(), 0.01);
        assertEquals(42.23f, resultFloat.floatValue(), 0.01);
        assertEquals(42.23d, resultFloat.doubleValue(), 0.01);
        assertEquals(42.23f, (Float) Convert.toJavaObject(resultFloatObj, Object.class, fact, true), 0.01);
        assertEquals(42.23f, (Float) Convert.toJavaObject(resultFloat, Object.class, fact, true), 0.01);
        assertEquals(42.23f, (Float) Convert.toJavaObject(resultFloatObj, float.class, fact, true), 0.01);
        assertEquals(42.23f, (Float) Convert.toJavaObject(resultFloat, float.class, fact, true), 0.01);
        assertEquals(42.23f, (Float) Convert.toJavaObject(resultFloatObj, Float.class, fact, true), 0.01);
        assertEquals(42.23f, (Float) Convert.toJavaObject(resultFloat, Float.class, fact, true), 0.01);
        assertEquals(42.23d, (Double) Convert.toJavaObject(resultFloat, double.class, fact, true), 0.01);

        Double testDoubleObj = 42.23;
        VARIANT resultDoubleObj = Convert.toVariant(testDoubleObj);
        double testDouble = 42.23;
        VARIANT resultDouble = Convert.toVariant(testDouble);

        assertEquals(42.23, resultDoubleObj.doubleValue(), 0.01);
        assertEquals(42.23, resultDouble.doubleValue(), 0.01);
        assertEquals(42.23f, resultDouble.floatValue(), 0.01);
        assertEquals(42.23, (Double) Convert.toJavaObject(resultDoubleObj, Object.class, fact, true), 0.01);
        assertEquals(42.23, (Double) Convert.toJavaObject(resultDouble, Object.class, fact, true), 0.01);
        assertEquals(42.23, (Double) Convert.toJavaObject(resultDoubleObj, double.class, fact, true), 0.01);
        assertEquals(42.23, (Double) Convert.toJavaObject(resultDouble, double.class, fact, true), 0.01);
        assertEquals(42.23, (Double) Convert.toJavaObject(resultDoubleObj, Double.class, fact, true), 0.01);
        assertEquals(42.23, (Double) Convert.toJavaObject(resultDouble, Double.class, fact, true), 0.01);
        assertEquals(42.23f, (Float) Convert.toJavaObject(resultDouble, float.class, fact, true), 0.01);
    }
    
    @Test
    public void convertDate() {
        Date testDate = new Date(2015 - 1900, 1, 1, 9, 0, 0);
        VARIANT resultDate = Convert.toVariant(testDate);
        DATE testDATE = new DATE(testDate);
        VARIANT resultDATE = Convert.toVariant(testDATE);

        assertEquals(testDate, resultDate.dateValue());
        assertEquals(testDate, resultDATE.dateValue());
        assertEquals(testDate, Convert.toJavaObject(resultDate, Object.class, fact, true));
        assertEquals(testDate, Convert.toJavaObject(resultDATE, Object.class, fact, true));
        assertEquals(testDate, Convert.toJavaObject(resultDate, Date.class, fact, true));
        assertEquals(testDate, Convert.toJavaObject(resultDATE, Date.class, fact, true));
    }
        
    @Test
    public void convertEnum() {
        TestEnum testEnum = TestEnum.Val2;
        VARIANT resultEnum = Convert.toVariant(testEnum);
        assertEquals((int) testEnum.getValue(), resultEnum.intValue());
        assertEquals((int) testEnum.getValue(), Convert.toJavaObject(resultEnum, Object.class, fact, true));
        assertEquals(testEnum, Convert.toJavaObject(resultEnum, TestEnum.class, fact, true));
    }

}

enum TestEnum implements IComEnum {
    Val1(1),
    Val2(2),
    Val3(3),;

    long value;

    private TestEnum(long val) {
        this.value = val;
    }

    public long getValue() {
        return this.value;
    }

}
