/* Copyright (c) 2013 Tobias Wolf, All Rights Reserved
 *
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

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.platform.win32.COM.COMException;
import com.sun.jna.platform.win32.COM.COMUtils;
import com.sun.jna.platform.win32.COM.util.ObjectFactory;
import com.sun.jna.platform.win32.COM.util.IComEnum;
import com.sun.jna.platform.win32.COM.util.IConnectionPoint;
import com.sun.jna.platform.win32.COM.util.IUnknown;
import com.sun.jna.platform.win32.COM.util.annotation.ComInterface;
import com.sun.jna.platform.win32.COM.util.annotation.ComMethod;
import com.sun.jna.platform.win32.COM.util.annotation.ComObject;
import com.sun.jna.platform.win32.COM.util.annotation.ComProperty;
import com.sun.jna.platform.win32.OaIdl.DATE;
import com.sun.jna.platform.win32.OaIdl.SAFEARRAY;
import com.sun.jna.platform.win32.Variant.VARIANT;
import static com.sun.jna.platform.win32.Variant.VT_BOOL;
import static com.sun.jna.platform.win32.Variant.VT_BSTR;
import static com.sun.jna.platform.win32.Variant.VT_DATE;
import static com.sun.jna.platform.win32.Variant.VT_ERROR;
import static com.sun.jna.platform.win32.Variant.VT_I1;
import static com.sun.jna.platform.win32.Variant.VT_I2;
import static com.sun.jna.platform.win32.Variant.VT_I4;
import static com.sun.jna.platform.win32.Variant.VT_INT;
import static com.sun.jna.platform.win32.Variant.VT_R4;
import static com.sun.jna.platform.win32.Variant.VT_R8;
import static com.sun.jna.platform.win32.Variant.VT_UI1;
import static com.sun.jna.platform.win32.Variant.VT_UI2;
import static com.sun.jna.platform.win32.Variant.VT_UI4;
import static com.sun.jna.platform.win32.Variant.VT_UINT;
import com.sun.jna.platform.win32.WTypes.BSTR;
import com.sun.jna.platform.win32.WinDef.LONGByReference;
import com.sun.jna.platform.win32.WinDef.SCODE;
import com.sun.jna.platform.win32.WinDef.UINT;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assert.assertTrue;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static com.sun.jna.platform.win32.OaIdlUtil.toPrimitiveArray;
import com.sun.jna.platform.win32.WTypes.VARTYPE;
import com.sun.jna.platform.win32.WinDef.LONG;
import java.lang.reflect.Field;

public class SAFEARRAYTest {
    static {
        ClassLoader.getSystemClassLoader().setDefaultAssertionStatus(true);
    }

    @Before
    public void setup() {
        Ole32.INSTANCE.CoInitializeEx(Pointer.NULL, Ole32.COINIT_MULTITHREADED);
    }

    @After
    public void teardown() {
        Ole32.INSTANCE.CoUninitialize();
    }

    @Test
    public void testCreateVarArray() {
        SAFEARRAY varArray = SAFEARRAY.createSafeArray(1);
        Assert.assertTrue(varArray != null);
        varArray.destroy();
    }

    @Test
    public void testCreateEmpty() throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        Field f = Structure.class.getDeclaredField("PLACEHOLDER_MEMORY");
        f.setAccessible(true);
        Pointer PLACEHOLDER_MEMORY = (Pointer) f.get(null);
        SAFEARRAY sa = Structure.newInstance(SAFEARRAY.class, PLACEHOLDER_MEMORY);
        Assert.assertTrue(sa != null);
    }

    @Test
    public void testSafeArrayPutGetElement() throws Exception {
        int rowCount = 2;
        int colCount = 10;

        SAFEARRAY varArray = SAFEARRAY.createSafeArray(rowCount, colCount);

        assertThat(varArray.getDimensionCount(), is(2));

        assertThat(varArray.getUBound(0), equalTo(rowCount - 1));
        assertThat(varArray.getUBound(1), equalTo(colCount - 1));

        for (int rowIdx = 0; rowIdx < rowCount; rowIdx++) {
            for (int colIdx = 0; colIdx < colCount; colIdx++) {
                VARIANT variant = new VARIANT(rowIdx + "#" + colIdx);
                varArray.putElement(variant, rowIdx, colIdx);
            }
        }

        for (int rowIdx = 0; rowIdx < rowCount; rowIdx++) {
            for (int colIdx = 0; colIdx < colCount; colIdx++) {
                VARIANT element = (VARIANT) varArray.getElement(rowIdx, colIdx);
                assertEquals(rowIdx + "#" + colIdx, element.stringValue());
                OleAuto.INSTANCE.VariantClear(element);
            }
        }
        varArray.destroy();
    }

    @Ignore("Only for live testing")
    @Test
    public void testPerformance() {
        ObjectFactory fact = new ObjectFactory();

        // Open a record set with a sample search (basicly get the first five
        // entries from the search index
        Connection conn = fact.createObject(Connection.class);
        conn.Open("Provider=Search.CollatorDSO;Extended Properties='Application=Windows';", "", "", -1);

        Recordset recordset = fact.createObject(Recordset.class);
        recordset.Open("SELECT TOP 500 System.ItemPathDisplay, System.ItemName, System.ItemUrl, System.DateCreated FROM SYSTEMINDEX ORDER BY System.ItemUrl", conn, CursorTypeEnum.adOpenUnspecified, LockTypeEnum.adLockUnspecified, -1);

        SAFEARRAY wrap = recordset.GetRows();

        assertThat(wrap.getDimensionCount(), is(2));

        long timeDirect = 0;
        long timeGetElement = 0;
        long timePointer = 0;
        long timeHelper = 0;

        long start, end;

        for (int i = 0; i < 4 * 10; i++) {
            if (i % 4 == 0) {
                start = System.currentTimeMillis();
                toArrayPtrToElement(wrap);
                end = System.currentTimeMillis();
                timePointer += (end - start);
            } else if (i % 4 == 1) {
                start = System.currentTimeMillis();
                toArrayGetElement(wrap);
                end = System.currentTimeMillis();
                timeGetElement += (end - start);
            } else if (i % 4 == 2) {
                start = System.currentTimeMillis();
                toArrayDirect(wrap);
                end = System.currentTimeMillis();
                timeDirect += (end - start);
            } else if (i % 4 == 3) {
                start = System.currentTimeMillis();
                OaIdlUtil.toPrimitiveArray(wrap, false);
                end = System.currentTimeMillis();
                timeHelper += (end - start);
            }
        }

        System.out.println("Direct: " + timeDirect + " ms");
        System.out.println("GetElement: " + timeGetElement + " ms");
        System.out.println("Pointer: " + timePointer + " ms");
        System.out.println("Helper: " + timeHelper + " ms");

        recordset.Close();
        conn.Close();

        fact.disposeAll();
    }

    private Object[] toArrayGetElement(SAFEARRAY wrap) {
        wrap.lock();
        int rowMax = wrap.getUBound(2);
        int columnMax = wrap.getUBound(1);
        Object[][] result = new Object[rowMax + 1][columnMax + 1];
        for(int i = 0; i <= rowMax; i++) {
            for(int j = 0; j <= columnMax; j++) {
                VARIANT cell = (VARIANT) wrap.getElement(i, j);
                result[i][j] = cell.getValue();
                OleAuto.INSTANCE.VariantClear(cell);
            }
        }
        wrap.unlock();
        return result;
    }

    private Object[] toArrayPtrToElement(SAFEARRAY wrap) {
        wrap.lock();
        int rowMax = wrap.getUBound(2);
        int columnMax = wrap.getUBound(1);
        Object[][] result = new Object[rowMax + 1][columnMax + 1];
        for(int i = 0; i <= rowMax; i++) {
            for(int j = 0; j <= columnMax; j++) {
                VARIANT cell = new VARIANT(wrap.ptrOfIndex(i, j));
                result[i][j] = cell.getValue();
            }
        }
        wrap.unlock();
        return result;
    }

    private Object[] toArrayDirect(SAFEARRAY wrap) {
        Pointer dataPointer = wrap.accessData();
        long rowMax = wrap.getUBound(2);
        long columnMax = wrap.getUBound(1);
        VARIANT[] variantData = (VARIANT[]) new VARIANT(dataPointer).toArray((int) ((rowMax + 1) * (columnMax + 1)));
        Object[][] result = new Object[(int) (rowMax + 1)][(int) (columnMax + 1)];
        for(long i = 0; i <= rowMax; i++) {
            long rowOffset = i * columnMax;
            for(long j = 0; j <= columnMax; j++) {
                VARIANT cell = variantData[(int) (rowOffset + j)];
                result[(int)i][(int) j] = cell.getValue();
            }
        }
        wrap.unaccessData();
        return result;
    }

    @Test
    public void testMultidimensionalNotNullBased() {
        // create a basic SAFEARRAY
        SAFEARRAY sa = SAFEARRAY.createSafeArray(new VARTYPE(VT_I4), 2, 2);
        sa.putElement(1, 0, 0);
        sa.putElement(2, 0, 1);
        sa.putElement(3, 1, 0);
        sa.putElement(4, 1, 1);

        // query the plain SAFEARRAY
        Object[][] basic = (Object[][]) OaIdlUtil.toPrimitiveArray(sa, false);

        // Virtually move the bounds
        sa.rgsabound[0].lLbound = new LONG(2);
        sa.rgsabound[1].lLbound = new LONG(5);
        sa.write();

        // Validate new bounds
        Assert.assertEquals(2, sa.getLBound(0));
        Assert.assertEquals(3, sa.getUBound(0));
        Assert.assertEquals(5, sa.getLBound(1));
        Assert.assertEquals(6, sa.getUBound(1));

        // requery the moved array and compare with basic array
        Object[][] relocated = (Object[][]) OaIdlUtil.toPrimitiveArray(sa, true);
        Assert.assertArrayEquals( basic, relocated);
    }

    @Test
    @SuppressWarnings("deprecation")
    public void testDataTypes() {
        int idx = 1;
        Pointer dataPointer;
        SAFEARRAY sa;
        long elementSize;

        Object[] objectResult;

        sa = SAFEARRAY.createSafeArray(new WTypes.VARTYPE(VT_BOOL), 2);
        elementSize = sa.getElemsize();
        assertThat(elementSize, equalTo(2L));
        dataPointer = sa.accessData();
        sa.putElement(true, idx);
        short[] shortResult = dataPointer.getShortArray(0, 2);
        objectResult = (Object[]) toPrimitiveArray(sa, false);
        assertThat((Boolean) sa.getElement(idx), equalTo(true));
        assertThat(shortResult[idx], equalTo((short) 0xFFFF));
        assertThat((Short) dataPointer.getShort(idx * elementSize), equalTo((short) 0xFFFF));
        assertThat((Boolean) objectResult[idx], equalTo(true));
        sa.unaccessData();
        sa.destroy();

        byte testByte = 67;
        sa = SAFEARRAY.createSafeArray(new WTypes.VARTYPE(VT_UI1), 2);
        elementSize = sa.getElemsize();
        assertThat(elementSize, equalTo(1L));
        dataPointer = sa.accessData();
        sa.putElement(testByte, idx);
        byte[] byteResult = dataPointer.getByteArray(0, 2);
        objectResult = (Object[]) toPrimitiveArray(sa, false);
        assertThat((Byte) sa.getElement(idx), equalTo(testByte));
        assertThat(dataPointer.getByte(idx * elementSize), equalTo(testByte));
        assertThat(byteResult[idx], equalTo(testByte));
        assertThat((Byte) objectResult[idx], equalTo(testByte));
        sa.unaccessData();
        sa.destroy();

        sa = SAFEARRAY.createSafeArray(new WTypes.VARTYPE(VT_I1), 2);
        elementSize = sa.getElemsize();
        assertThat(elementSize, equalTo(1L));
        dataPointer = sa.accessData();
        sa.putElement(testByte, idx);
        byteResult = dataPointer.getByteArray(0, 2);
        objectResult = (Object[]) toPrimitiveArray(sa, false);
        assertThat((Byte) sa.getElement(idx), equalTo(testByte));
        assertThat(dataPointer.getByte(idx * elementSize), equalTo(testByte));
        assertThat(byteResult[idx], equalTo(testByte));
        assertThat((Byte) objectResult[idx], equalTo(testByte));
        sa.unaccessData();
        sa.destroy();

        short testShort = Short.MAX_VALUE - 1;
        sa = SAFEARRAY.createSafeArray(new WTypes.VARTYPE(VT_UI2), 2);
        elementSize = sa.getElemsize();
        assertThat(elementSize, equalTo(2L));
        dataPointer = sa.accessData();
        sa.putElement(testShort, idx);
        shortResult = dataPointer.getShortArray(0, 2);
        objectResult = (Object[]) toPrimitiveArray(sa, false);
        assertThat((Short) sa.getElement(idx), equalTo(testShort));
        assertThat(dataPointer.getShort(idx * elementSize), equalTo(testShort));
        assertThat(shortResult[idx], equalTo(testShort));
        assertThat((Short) objectResult[idx], equalTo(testShort));
        sa.unaccessData();
        sa.destroy();

        sa = SAFEARRAY.createSafeArray(new WTypes.VARTYPE(VT_I2), 2);
        elementSize = sa.getElemsize();
        assertThat(elementSize, equalTo(2L));
        dataPointer = sa.accessData();
        sa.putElement(testShort, idx);
        shortResult = dataPointer.getShortArray(0, 2);
        objectResult = (Object[]) toPrimitiveArray(sa, false);
        assertThat((Short) sa.getElement(idx), equalTo(testShort));
        assertThat(dataPointer.getShort(idx * elementSize), equalTo(testShort));
        assertThat(shortResult[idx], equalTo(testShort));
        assertThat((Short) objectResult[idx], equalTo(testShort));
        sa.unaccessData();
        sa.destroy();

        int testInt = Integer.MAX_VALUE - 1;
        sa = SAFEARRAY.createSafeArray(new WTypes.VARTYPE(VT_UI4), 2);
        elementSize = sa.getElemsize();
        assertThat(elementSize, equalTo(4L));
        dataPointer = sa.accessData();
        sa.putElement(testInt, idx);
        int[] intResult = dataPointer.getIntArray(0, 2);
        objectResult = (Object[]) toPrimitiveArray(sa, false);
        assertThat((Integer) sa.getElement(idx), equalTo(testInt));
        assertThat(dataPointer.getInt(idx * elementSize), equalTo(testInt));
        assertThat(intResult[idx], equalTo(testInt));
        assertThat((Integer) objectResult[idx], equalTo(testInt));
        sa.unaccessData();
        sa.destroy();

        sa = SAFEARRAY.createSafeArray(new WTypes.VARTYPE(VT_I4), 2);
        elementSize = sa.getElemsize();
        assertThat(elementSize, equalTo(4L));
        dataPointer = sa.accessData();
        sa.putElement(testInt, idx);
        intResult = dataPointer.getIntArray(0, 2);
        objectResult = (Object[]) toPrimitiveArray(sa, false);
        assertThat((Integer) sa.getElement(idx), equalTo(testInt));
        assertThat(dataPointer.getInt(idx * elementSize), equalTo(testInt));
        assertThat(intResult[idx], equalTo(testInt));
        assertThat((Integer) objectResult[idx], equalTo(testInt));
        sa.unaccessData();
        sa.destroy();

        sa = SAFEARRAY.createSafeArray(new WTypes.VARTYPE(VT_UINT), 2);
        elementSize = sa.getElemsize();
        assertThat(elementSize, equalTo(4L));
        dataPointer = sa.accessData();
        sa.putElement(testInt, idx);
        intResult = dataPointer.getIntArray(0, 2);
        objectResult = (Object[]) toPrimitiveArray(sa, false);
        assertThat((Integer) sa.getElement(idx), equalTo(testInt));
        assertThat(dataPointer.getInt(idx * elementSize), equalTo(testInt));
        assertThat(intResult[idx], equalTo(testInt));
        assertThat((Integer) objectResult[idx], equalTo(testInt));
        sa.unaccessData();
        sa.destroy();

        sa = SAFEARRAY.createSafeArray(new WTypes.VARTYPE(VT_INT), 2);
        elementSize = sa.getElemsize();
        assertThat(elementSize, equalTo(4L));
        dataPointer = sa.accessData();
        sa.putElement(testInt, idx);
        intResult = dataPointer.getIntArray(0, 2);
        objectResult = (Object[]) toPrimitiveArray(sa, false);
        assertThat((Integer) sa.getElement(idx), equalTo(testInt));
        assertThat(dataPointer.getInt(idx * elementSize), equalTo(testInt));
        assertThat(intResult[idx], equalTo(testInt));
        assertThat((Integer) objectResult[idx], equalTo(testInt));
        sa.unaccessData();
        sa.destroy();

        SCODE testSCODE = new SCODE(47);
        sa = SAFEARRAY.createSafeArray(new WTypes.VARTYPE(VT_ERROR), 2);
        elementSize = sa.getElemsize();
        assertThat(elementSize, equalTo(4L));
        dataPointer = sa.accessData();
        sa.putElement(testSCODE, idx);
        intResult = dataPointer.getIntArray(0, 2);
        objectResult = (Object[]) toPrimitiveArray(sa, false);
        assertThat((SCODE) sa.getElement(idx), equalTo(testSCODE));
        assertThat(dataPointer.getInt(idx * elementSize), equalTo(47));
        assertThat(intResult[idx], equalTo(47));
        assertThat((SCODE) objectResult[idx], equalTo(testSCODE));
        sa.unaccessData();
        sa.destroy();

        float testFloat = 42.23f;
        sa = SAFEARRAY.createSafeArray(new WTypes.VARTYPE(VT_R4), 2);
        elementSize = sa.getElemsize();
        assertThat(elementSize, equalTo(4L));
        dataPointer = sa.accessData();
        sa.putElement(testFloat, idx);
        float[] floatResult = dataPointer.getFloatArray(0, 2);
        objectResult = (Object[]) toPrimitiveArray(sa, false);
        assertThat((Float) sa.getElement(idx), equalTo(testFloat));
        assertThat((Float) dataPointer.getFloat(idx * elementSize), equalTo(testFloat));
        assertThat(floatResult[idx], equalTo(testFloat));
        assertThat((Float) objectResult[idx], equalTo(testFloat));
        sa.unaccessData();
        sa.destroy();

        double testDouble = 42.23d;
        sa = SAFEARRAY.createSafeArray(new WTypes.VARTYPE(VT_R8), 2);
        elementSize = sa.getElemsize();
        assertThat(elementSize, equalTo(8L));
        dataPointer = sa.accessData();
        sa.putElement(testDouble, idx);
        double[] doubleResult = dataPointer.getDoubleArray(0, 2);
        objectResult = (Object[]) toPrimitiveArray(sa, false);
        assertThat((Double) sa.getElement(idx), equalTo(testDouble));
        assertThat((Double) dataPointer.getDouble(idx * elementSize), equalTo(testDouble));
        assertThat(doubleResult[idx], equalTo(testDouble));
        assertThat((Double) objectResult[idx], equalTo(testDouble));
        sa.unaccessData();
        sa.destroy();

        Date testDate = new Date(1923, 1, 1, 5, 0, 0);
        DATE testDATE = new DATE(testDate);
        sa = SAFEARRAY.createSafeArray(new WTypes.VARTYPE(VT_DATE), 2);
        elementSize = sa.getElemsize();
        assertThat(elementSize, equalTo(8L));
        dataPointer = sa.accessData();
        sa.putElement(testDATE, idx);
        doubleResult = dataPointer.getDoubleArray(0, 2);
        objectResult = (Object[]) toPrimitiveArray(sa, false);
        assertThat(((DATE) sa.getElement(idx)).date, equalTo(testDATE.date));
        assertThat((Double) dataPointer.getDouble(idx * elementSize), equalTo(testDATE.date));
        assertThat(((DATE) sa.getElement(idx)).getAsJavaDate(), equalTo(testDate));
        assertThat(doubleResult[idx], equalTo(testDATE.date));
        assertThat((Date) objectResult[idx], equalTo(testDate));
        sa.unaccessData();
        sa.destroy();

        String testString = "äöüßAE!";
        sa = SAFEARRAY.createSafeArray(new WTypes.VARTYPE(VT_BSTR), 2);
        elementSize = sa.getElemsize();
        dataPointer = sa.accessData();
        sa.putElement(testString, idx);
        Pointer[] pointerResult = dataPointer.getPointerArray(0, 2);
        objectResult = (Object[]) toPrimitiveArray(sa, false);
        assertThat(((String) sa.getElement(idx)), equalTo(testString));
        assertThat(new BSTR(dataPointer.getPointer(idx * elementSize)).getValue(), equalTo(testString));
        assertThat(new BSTR(pointerResult[idx]).getValue(), equalTo(testString));
        assertThat((String) objectResult[idx], equalTo(testString));
        sa.unaccessData();
        sa.destroy();

        // VT_VARIANT is tested in testADODB

        // untested: VT_UNKNOWN
        // untested: VT_DISPATCH
        // untested: VT_CY
        // untested: VT_DECIMAL
        // unsupported: VT_RECORD
    }


    /**
     * Test assumption: The windows search provider is present and holds at least
     * five entries. If this assumption is not met, this test fails.
     */
    @Ignore("Assumes windows search provider present with five entries")
    @Test
    public void testADODB() {
        ObjectFactory fact = new ObjectFactory();

        // Open a record set with a sample search (basicly get the first five
        // entries from the search index
        Connection conn = fact.createObject(Connection.class);
        try {
            conn.Open("Provider=Search.CollatorDSO;Extended Properties='Application=Windows';", "", "", -1);
        } catch (COMException ex) {
            Assume.assumeNoException(ex);
        }

        Recordset recordset = fact.createObject(Recordset.class);
        recordset.Open("SELECT TOP 5 System.ItemPathDisplay, System.ItemName, System.ItemUrl, System.DateCreated FROM SYSTEMINDEX ORDER BY System.ItemUrl", conn, CursorTypeEnum.adOpenUnspecified, LockTypeEnum.adLockUnspecified, -1);

        // Save complete list for comparison with subscript list
        List<String> urls = new ArrayList<>(5);
        List<String> names = new ArrayList<>(5);

        while (!recordset.getEOF()) {
            WinNT.HRESULT hr;

             // Fetch (all) five rows and extract SAFEARRAY
            SAFEARRAY sa = recordset.GetRows(5);

            assertThat(sa.getDimensionCount(), is(2));

            // Test getting bounds via automation functions SafeArrayGetLBound
            // and SafeArrayGetUBound

            // 5 rows (dimension 1) and 4 (dimension 2) columns should be
            // returned, the indices are zero-based, the lower bounds are
            // always zero
            //
            // Dimensions are inverted between SafeArray and java, so
            // in this case row dimension 2 retrieves row count,
            // dimension 1 retrieves column count
            WinDef.LONGByReference res = new WinDef.LONGByReference();

            hr = OleAuto.INSTANCE.SafeArrayGetLBound(sa, new UINT(2), res);
            assert COMUtils.SUCCEEDED(hr);
            assertThat(res.getValue().intValue(), is(0));

            hr = OleAuto.INSTANCE.SafeArrayGetUBound(sa, new UINT(2), res);
            assert COMUtils.SUCCEEDED(hr);
            assertThat(res.getValue().intValue(), is(4));

            hr = OleAuto.INSTANCE.SafeArrayGetLBound(sa, new UINT(1), res);
            assert COMUtils.SUCCEEDED(hr);
            assertThat(res.getValue().intValue(), is(0));

            hr = OleAuto.INSTANCE.SafeArrayGetUBound(sa, new UINT(1), res);
            assert COMUtils.SUCCEEDED(hr);
            assertThat(res.getValue().intValue(), is(3));

            // Get dimensions directly from structure
            // lLbound contains lowerBound (first index)
            // cElements contains count of elements
            int row_lower = sa.rgsabound[0].lLbound.intValue();
            int row_count = sa.rgsabound[0].cElements.intValue();
            int column_lower = sa.rgsabound[1].lLbound.intValue();
            int column_count = sa.rgsabound[1].cElements.intValue();
            assertThat(row_lower, is(0));
            assertThat(row_count, is(5));
            assertThat(column_lower, is(0));
            assertThat(column_count, is(4));

            // Use Wrapper methods
            assertThat(sa.getLBound(0), is(0));
            assertThat(sa.getUBound(0), is(4));
            assertThat(sa.getLBound(1), is(0));
            assertThat(sa.getUBound(1), is(3));

            // Iterate over resultset and fetch via SafeArrayGetElement
            // Columns 1 - 3 return Strings, Column 4 returns a date
            for (int rowIdx = row_lower; rowIdx < row_lower + row_count; rowIdx++) {
                for (int colIdx = column_lower; colIdx < column_lower + column_count; colIdx++) {
                    VARIANT result = (VARIANT) sa.getElement(rowIdx, colIdx);
                    Pointer pv = sa.ptrOfIndex(rowIdx, colIdx);
                    VARIANT result2 = new VARIANT(pv);
                    COMUtils.checkRC(hr);
                    if(colIdx == 3) {
                        assert (result.getVarType().intValue() & Variant.VT_DATE) > 0;
                        assert (result2.getVarType().intValue() & Variant.VT_DATE) > 0;
                    } else {
                        assert (result.getVarType().intValue() & Variant.VT_BSTR) > 0;
                        assert (result2.getVarType().intValue() & Variant.VT_BSTR) > 0;
                    }
                    // Only clear result, as SafeArrayGetElement creates a copy
                    // result2 is a view into the SafeArray
                    OleAuto.INSTANCE.VariantClear(result);
                }
            }

            // Access SafeArray directly
            sa.lock();
            try {
                // Map the returned array to java
                VARIANT[] variantArray = (VARIANT[]) (new VARIANT(sa.pvData.getPointer()).toArray(row_count * column_count));

                for (int rowIdx = 0; rowIdx < row_count; rowIdx++) {
                    for (int colIdx = 0; colIdx < column_count; colIdx++) {
                        int index = rowIdx * column_count + colIdx;
                        VARIANT result = variantArray[index];
                        if (colIdx == 3) {
                            assert (result.getVarType().intValue() & Variant.VT_DATE) > 0;
                        } else {
                            assert (result.getVarType().intValue() & Variant.VT_BSTR) > 0;
                        }
                        // see comment for urls
                        if(colIdx == 2) {
                            urls.add(result.stringValue());
                        } else if (colIdx == 1) {
                            names.add(result.stringValue());
                        }
                    }
                }
            } finally {
                sa.unlock();
            }

            // Access SafeArray directly - Variant 2
            Pointer data = sa.accessData();
            try {
                // Map the returned array to java
                VARIANT[] variantArray = (VARIANT[]) (new VARIANT(data).toArray(row_count * column_count));

                for (int rowIdx = 0; rowIdx < row_count; rowIdx++) {
                    for (int colIdx = 0; colIdx < column_count; colIdx++) {
                        int index = rowIdx * column_count + colIdx;
                        VARIANT result = variantArray[index];
                        if (colIdx == 3) {
                            assert (result.getVarType().intValue() & Variant.VT_DATE) > 0;
                        } else {
                            assert (result.getVarType().intValue() & Variant.VT_BSTR) > 0;
                        }
                        // see comment for urls
                        if(colIdx == 2) {
                            urls.add(result.stringValue());
                        } else if (colIdx == 1) {
                            names.add(result.stringValue());
                        }
                    }
                }
            } finally {
                sa.unaccessData();
            }

            sa.destroy();
        }

        recordset.Close();

        // Requery and fetch only columns "System.ItemUrl", "System.ItemName" and "System.ItemUrl"
        recordset = fact.createObject(Recordset.class);
        recordset.Open("SELECT TOP 5 System.ItemPathDisplay, System.ItemName, System.ItemUrl FROM SYSTEMINDEX ORDER BY System.ItemUrl", conn, CursorTypeEnum.adOpenUnspecified, LockTypeEnum.adLockUnspecified, -1);


        // Create SAFEARRAY and wrap it into a VARIANT
        // Array is initialized to one element and then redimmed. This is done
        // to test SafeArrayRedim, in normal usage it is more efficient to
        // intitialize directly to the correct size
        SAFEARRAY arr = SAFEARRAY.createSafeArray(1);
        arr.putElement(new VARIANT("System.ItemUrl"), 0);
        boolean exceptionCaught = false;
        VARIANT itemName = new VARIANT("System.ItemName");
        try {
            arr.putElement(itemName, 1);
        } catch (COMException ex) {
            exceptionCaught = true;
        } finally {
            OleAuto.INSTANCE.VariantClear(itemName);
        }
        assertTrue("Array is initialized to a size of one - it can't hold a second item.", exceptionCaught);
        arr.redim(2, 0);
        arr.putElement(new VARIANT("System.ItemName"), 1);

        assertThat(arr.getDimensionCount(), is(1));

        VARIANT columnList = new VARIANT();
        columnList.setValue(Variant.VT_ARRAY | Variant.VT_VARIANT, arr);

        assert !(recordset.getEOF());

        while (!recordset.getEOF()) {
            SAFEARRAY sa = recordset.GetRows(5, VARIANT.VARIANT_MISSING, columnList);

            assertThat(sa.getDimensionCount(), is(2));

            assertThat(sa.getVarType().intValue(), is(Variant.VT_VARIANT));
            LONGByReference longRef = new LONGByReference();

            OleAuto.INSTANCE.SafeArrayGetLBound(sa, new UINT(2), longRef);
            int lowerBound = longRef.getValue().intValue();
            assertThat(sa.getLBound(0), equalTo(lowerBound));

            OleAuto.INSTANCE.SafeArrayGetUBound(sa, new UINT(2), longRef);
            int upperBound = longRef.getValue().intValue();
            assertThat(sa.getUBound(0), equalTo(upperBound));

            // 5 rows are expected
            assertThat(upperBound - lowerBound + 1, is(5));

            for (int rowIdx = lowerBound; rowIdx <= upperBound; rowIdx++) {
                VARIANT variantItemUrl = (VARIANT) sa.getElement(rowIdx, 0);
                VARIANT variantItemName = (VARIANT) sa.getElement(rowIdx, 1);
                assertThat(variantItemUrl.stringValue(), is(urls.get(rowIdx)));
                assertThat(variantItemName.stringValue(), is(names.get(rowIdx)));
                OleAuto.INSTANCE.VariantClear(variantItemUrl);
                OleAuto.INSTANCE.VariantClear(variantItemName);
            }

            sa.destroy();
        }

        recordset.Close();

        // Requery and fetch only columns "System.ItemUrl", "System.ItemName" and "System.ItemUrl"
        recordset = fact.createObject(Recordset.class);
        recordset.Open("SELECT TOP 5 System.ItemPathDisplay, System.ItemName, System.ItemUrl FROM SYSTEMINDEX ORDER BY System.ItemUrl", conn, CursorTypeEnum.adOpenUnspecified, LockTypeEnum.adLockUnspecified, -1);

        assert !(recordset.getEOF());

        while (!recordset.getEOF()) {
            Object[][] data = (Object[][]) OaIdlUtil.toPrimitiveArray(recordset.GetRows(5, VARIANT.VARIANT_MISSING, columnList), true);

            assertThat(data.length, is(5));
            assertThat(data[0].length, is(2));

            for (int rowIdx = 0; rowIdx < data.length; rowIdx++) {
                assertThat((String) data[rowIdx][0], is(urls.get(rowIdx)));
                assertThat((String) data[rowIdx][1], is(names.get(rowIdx)));
            }
        }

        recordset.Close();

        conn.Close();

        fact.disposeAll();
    }

    // -------------  Helper classes / interfaces

    /**
     * <p>
     * guid({00000514-0000-0010-8000-00AA006D2EA4})</p>
     * <p>
     * source(ConnectionEvents)</p>
     */
    @ComObject(clsId = "{00000514-0000-0010-8000-00AA006D2EA4}", progId = "{B691E011-1797-432E-907A-4D8C69339129}")
    public static interface Connection extends
            _Connection,
            IConnectionPoint,
            IUnknown {

    }

    /**
     * <p>
     * guid({0000051B-0000-0010-8000-00AA006D2EA4})</p>
     */
    public static enum CursorTypeEnum implements IComEnum {
        adOpenUnspecified(-1),
        adOpenForwardOnly(0),
        adOpenKeyset(1),
        adOpenDynamic(2),
        adOpenStatic(3),;

        private CursorTypeEnum(long value) {
            this.value = value;
        }
        private final long value;

        @Override
        public long getValue() {
            return this.value;
        }
    }

    /**
     * <p>
     * guid({0000051D-0000-0010-8000-00AA006D2EA4})</p>
     */
    public static enum LockTypeEnum implements IComEnum {
        adLockUnspecified(-1),
        adLockReadOnly(1),
        adLockPessimistic(2),
        adLockOptimistic(3),
        adLockBatchOptimistic(4),;

        private LockTypeEnum(long value) {
            this.value = value;
        }
        private final long value;

        @Override
        public long getValue() {
            return this.value;
        }
    }

    /**
     * <p>
     * guid({00000535-0000-0010-8000-00AA006D2EA4})</p>
     */
    @ComObject(clsId = "{00000535-0000-0010-8000-00AA006D2EA4}", progId = "{00000300-0000-0010-8000-00AA006D2EA4}")
    public static interface Recordset extends
            _Recordset {

    }

    /**
     * <p>
     * guid({00001550-0000-0010-8000-00AA006D2EA4})</p>
     */
    @ComInterface(iid = "{00001550-0000-0010-8000-00AA006D2EA4}")
    public static interface _Connection {

        /**
         * <p>
         * memberId(5)</p>
         */
        @ComMethod(name = "Close")
        void Close();

        /**
         * <p>
         * memberId(10)</p>
         * @param ConnectionString
         * @param UserID
         * @param Password
         * @param Options
         */
        @ComMethod(name = "Open")
        void Open(String ConnectionString,
                String UserID,
                String Password,
                int Options);
    }

    /**
     *
     *
     * <p>
     * guid({00000556-0000-0010-8000-00AA006D2EA4})</p>
     */
    @ComInterface(iid = "{00000556-0000-0010-8000-00AA006D2EA4}")
    public static interface _Recordset {

        /**
         * <p>
         * memberId(1006)</p>
         * @return
         */
        @ComProperty(name = "EOF")
        Boolean getEOF();

        /**
         * <p>
         * memberId(1016)</p>
         * @param Rows
         * @param Start
         * @param Fields
         * @return
         */
        @ComMethod(name = "GetRows")
        SAFEARRAY GetRows(int Rows,
                Object Start,
                Object Fields);

        /**
         * <p>
         * memberId(1016)</p>
         * @param Rows
         * @return
         */
        @ComMethod(name = "GetRows")
        SAFEARRAY GetRows(int Rows);

        /**
         * <p>
         * memberId(1016)</p>
         * @return
         */
        @ComMethod(name = "GetRows")
        SAFEARRAY GetRows();

        /**
         * <p>
         * memberId(1014)</p>
         */
        @ComMethod(name = "Close")
        void Close();

        /**
         * <p>
         * memberId(1022)</p>
         * @param Source
         * @param ActiveConnection
         * @param CursorType
         * @param LockType
         * @param Options
         */
        @ComMethod(name = "Open")
        void Open(Object Source,
                Object ActiveConnection,
                CursorTypeEnum CursorType,
                LockTypeEnum LockType,
                int Options);
    }
}
