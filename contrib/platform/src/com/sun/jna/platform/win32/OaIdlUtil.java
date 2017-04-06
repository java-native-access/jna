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

import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.OaIdl.DATE;
import com.sun.jna.platform.win32.OaIdl.SAFEARRAY;
import com.sun.jna.platform.win32.Variant.VARIANT;
import static com.sun.jna.platform.win32.Variant.VT_BOOL;
import static com.sun.jna.platform.win32.Variant.VT_BSTR;
import static com.sun.jna.platform.win32.Variant.VT_CY;
import static com.sun.jna.platform.win32.Variant.VT_DATE;
import static com.sun.jna.platform.win32.Variant.VT_DECIMAL;
import static com.sun.jna.platform.win32.Variant.VT_DISPATCH;
import static com.sun.jna.platform.win32.Variant.VT_EMPTY;
import static com.sun.jna.platform.win32.Variant.VT_ERROR;
import static com.sun.jna.platform.win32.Variant.VT_I1;
import static com.sun.jna.platform.win32.Variant.VT_I2;
import static com.sun.jna.platform.win32.Variant.VT_I4;
import static com.sun.jna.platform.win32.Variant.VT_INT;
import static com.sun.jna.platform.win32.Variant.VT_NULL;
import static com.sun.jna.platform.win32.Variant.VT_R4;
import static com.sun.jna.platform.win32.Variant.VT_R8;
import static com.sun.jna.platform.win32.Variant.VT_RECORD;
import static com.sun.jna.platform.win32.Variant.VT_UI1;
import static com.sun.jna.platform.win32.Variant.VT_UI2;
import static com.sun.jna.platform.win32.Variant.VT_UI4;
import static com.sun.jna.platform.win32.Variant.VT_UINT;
import static com.sun.jna.platform.win32.Variant.VT_UNKNOWN;
import static com.sun.jna.platform.win32.Variant.VT_VARIANT;
import com.sun.jna.platform.win32.WTypes.BSTR;
import com.sun.jna.platform.win32.WinDef.SCODE;
import java.lang.reflect.Array;

public abstract class OaIdlUtil {

    /**
     * Read SAFEARRAY into a java array. Not all VARTYPEs are supported!
     *
     * <p>
     * Supported types:</p>
     * <ul>
     * <li>VT_BOOL</li>
     * <li>VT_UI1</li>
     * <li>VT_I1</li>
     * <li>VT_UI2</li>
     * <li>VT_I2</li>
     * <li>VT_UI4</li>
     * <li>VT_UINT</li>
     * <li>VT_I4</li>
     * <li>VT_INT</li>
     * <li>VT_ERROR</li>
     * <li>VT_R4</li>
     * <li>VT_R8</li>
     * <li>VT_DATE</li>
     * <li>VT_BSTR</li>
     * <li>VT_VARIANT (Onle the following VARTYPES):
     * <ul>
     * <li>VT_EMPTY (converted to NULL)</li>
     * <li>VT_NULL</li>
     * <li>VT_BOOL</li>
     * <li>VT_UI1</li>
     * <li>VT_I1</li>
     * <li>VT_UI2</li>
     * <li>VT_I2</li>
     * <li>VT_UI4</li>
     * <li>VT_UINT</li>
     * <li>VT_I4</li>
     * <li>VT_INT</li>
     * <li>VT_ERROR</li>
     * <li>VT_R4</li>
     * <li>VT_R8</li>
     * <li>VT_DATE</li>
     * <li>VT_BSTR</li>
     * </ul>
     * </li>
     * </ul>
     *
     * @param sa SAFEARRAY to convert
     * @param destruct if true the supplied SAFEARRAY is destroyed, there must
     * not be additional locks on the array!
     * @return Java array corresponding to the given SAFEARRAY
     */
    public static Object toPrimitiveArray(SAFEARRAY sa, boolean destruct) {
        Pointer dataPointer = sa.accessData();
        try {
            int dimensions = sa.getDimensionCount();
            int[] elements = new int[dimensions];
            int[] cumElements = new int[dimensions];
            int varType = sa.getVarType().intValue();

            for (int i = 0; i < dimensions; i++) {
                elements[i] = sa.getUBound(i) - sa.getLBound(i) + 1;
            }

            for (int i = dimensions - 1; i >= 0; i--) {
                if (i == (dimensions - 1)) {
                    cumElements[i] = 1;
                } else {
                    cumElements[i] = cumElements[i + 1] * elements[i + 1];
                }
            }

            if (dimensions == 0) {
                throw new IllegalArgumentException("Supplied Array has no dimensions.");
            }

            int elementCount = cumElements[0] * elements[0];

            Object sourceArray;
            switch (varType) {
                case VT_UI1:
                case VT_I1:
                    sourceArray = dataPointer.getByteArray(0, elementCount);
                    break;
                case VT_BOOL:
                case VT_UI2:
                case VT_I2:
                    sourceArray = dataPointer.getShortArray(0, elementCount);
                    break;
                case VT_UI4:
                case VT_UINT:
                case VT_I4:
                case VT_INT:
                case VT_ERROR:
                    sourceArray = dataPointer.getIntArray(0, elementCount);
                    break;
                case VT_R4:
                    sourceArray = dataPointer.getFloatArray(0, elementCount);
                    break;
                case VT_R8:
                case VT_DATE:
                    sourceArray = dataPointer.getDoubleArray(0, elementCount);
                    break;
                case VT_BSTR:
                    sourceArray = dataPointer.getPointerArray(0, elementCount);
                    break;
                case VT_VARIANT:
                    VARIANT variant = new VARIANT(dataPointer);
                    sourceArray = variant.toArray(elementCount);
                    break;
                case VT_UNKNOWN:
                case VT_DISPATCH:
                case VT_CY:
                case VT_DECIMAL:
                case VT_RECORD:
                default:
                    throw new IllegalStateException("Type not supported: " + varType);
            }

            Object targetArray = Array.newInstance(Object.class, elements);
            toPrimitiveArray(sourceArray, targetArray, elements, cumElements, varType, new int[0]);
            return targetArray;
        } finally {
            sa.unaccessData();
            if (destruct) {
                sa.destroy();
            }
        }
    }

    private static void toPrimitiveArray(Object dataArray, Object targetArray, int[] elements, int[] cumElements, int varType, int[] currentIdx) {
        int dimIdx = currentIdx.length;
        int[] subIdx = new int[currentIdx.length + 1];
        System.arraycopy(currentIdx, 0, subIdx, 0, dimIdx);
        for (int i = 0; i < elements[dimIdx]; i++) {
            subIdx[dimIdx] = i;
            if (dimIdx == (elements.length - 1)) {
                int offset = 0;
                for (int j = 0; j < dimIdx; j++) {
                    offset += cumElements[j] * currentIdx[j];
                }
                offset += subIdx[dimIdx];
                int targetPos = subIdx[dimIdx];
                switch (varType) {
                    case VT_BOOL:
                        Array.set(targetArray, targetPos, Array.getShort(dataArray, offset) != 0);
                        break;
                    case VT_UI1:
                    case VT_I1:
                        Array.set(targetArray, targetPos, Array.getByte(dataArray, offset));
                        break;
                    case VT_UI2:
                    case VT_I2:
                        Array.set(targetArray, targetPos, Array.getShort(dataArray, offset));
                        break;
                    case VT_UI4:
                    case VT_UINT:
                    case VT_I4:
                    case VT_INT:
                        Array.set(targetArray, targetPos, Array.getInt(dataArray, offset));
                        break;
                    case VT_ERROR:
                        Array.set(targetArray, targetPos, new SCODE(Array.getInt(dataArray, offset)));
                        break;
                    case VT_R4:
                        Array.set(targetArray, targetPos, Array.getFloat(dataArray, offset));
                        break;
                    case VT_R8:
                        Array.set(targetArray, targetPos, Array.getDouble(dataArray, offset));
                        break;
                    case VT_DATE:
                        Array.set(targetArray, targetPos, new DATE(Array.getDouble(dataArray, offset)).getAsJavaDate());
                        break;
                    case VT_BSTR:
                        Array.set(targetArray, targetPos, new BSTR((Pointer) Array.get(dataArray, offset)).getValue());
                        break;
                    case VT_VARIANT:
                        VARIANT holder = (VARIANT) Array.get(dataArray, offset);
                        switch (holder.getVarType().intValue()) {
                            case VT_NULL:
                            case VT_EMPTY:
                                Array.set(targetArray, targetPos, null);
                                break;
                            case VT_BOOL:
                                Array.set(targetArray, targetPos, holder.booleanValue());
                                break;
                            case VT_UI1:
                            case VT_I1:
                                Array.set(targetArray, targetPos, holder.byteValue());
                                break;
                            case VT_UI2:
                            case VT_I2:
                                Array.set(targetArray, targetPos, holder.shortValue());
                                break;
                            case VT_UI4:
                            case VT_UINT:
                            case VT_I4:
                            case VT_INT:
                                Array.set(targetArray, targetPos, holder.intValue());
                                break;
                            case VT_ERROR:
                                Array.set(targetArray, targetPos, new SCODE(holder.intValue()));
                                break;
                            case VT_R4:
                                Array.set(targetArray, targetPos, holder.floatValue());
                                break;
                            case VT_R8:
                                Array.set(targetArray, targetPos, holder.doubleValue());
                                break;
                            case VT_DATE:
                                Array.set(targetArray, targetPos, holder.dateValue());
                                break;
                            case VT_BSTR:
                                Array.set(targetArray, targetPos, holder.stringValue());
                                break;
                            default:
                                throw new IllegalStateException("Type not supported: " + holder.getVarType().intValue());
                        }
                        break;
                    case VT_UNKNOWN:
                    case VT_DISPATCH:
                    case VT_CY:
                    case VT_DECIMAL:
                    case VT_RECORD:
                    default:
                        throw new IllegalStateException("Type not supported: " + varType);
                }
            } else {
                toPrimitiveArray(dataArray, Array.get(targetArray, i), elements, cumElements, varType, subIdx);
            }
        }
    }
}
