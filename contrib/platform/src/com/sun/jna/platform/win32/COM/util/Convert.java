/* Copyright (c) 2014 Dr David H. Akehurst (itemis), All Rights Reserved
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
package com.sun.jna.platform.win32.COM.util;

import com.sun.jna.platform.win32.OaIdl.DATE;
import com.sun.jna.platform.win32.OaIdl.VARIANT_BOOL;
import com.sun.jna.platform.win32.OleAuto;
import com.sun.jna.platform.win32.Variant;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Date;

import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.Variant.VARIANT;
import com.sun.jna.platform.win32.WTypes.BSTR;
import com.sun.jna.platform.win32.WinDef.BOOL;
import com.sun.jna.platform.win32.WinDef.BYTE;
import com.sun.jna.platform.win32.WinDef.CHAR;
import com.sun.jna.platform.win32.WinDef.LONG;
import com.sun.jna.platform.win32.WinDef.SHORT;
import com.sun.jna.platform.win32.OaIdl;
import com.sun.jna.platform.win32.OaIdl.SAFEARRAY;
import static com.sun.jna.platform.win32.Variant.VT_ARRAY;
import static com.sun.jna.platform.win32.Variant.VT_BOOL;
import static com.sun.jna.platform.win32.Variant.VT_BSTR;
import static com.sun.jna.platform.win32.Variant.VT_BYREF;
import static com.sun.jna.platform.win32.Variant.VT_CY;
import static com.sun.jna.platform.win32.Variant.VT_DATE;
import static com.sun.jna.platform.win32.Variant.VT_DECIMAL;
import static com.sun.jna.platform.win32.Variant.VT_DISPATCH;
import static com.sun.jna.platform.win32.Variant.VT_EMPTY;
import static com.sun.jna.platform.win32.Variant.VT_ERROR;
import static com.sun.jna.platform.win32.Variant.VT_I1;
import static com.sun.jna.platform.win32.Variant.VT_I2;
import static com.sun.jna.platform.win32.Variant.VT_I4;
import static com.sun.jna.platform.win32.Variant.VT_I8;
import static com.sun.jna.platform.win32.Variant.VT_INT;
import static com.sun.jna.platform.win32.Variant.VT_NULL;
import static com.sun.jna.platform.win32.Variant.VT_R4;
import static com.sun.jna.platform.win32.Variant.VT_R8;
import static com.sun.jna.platform.win32.Variant.VT_RECORD;
import static com.sun.jna.platform.win32.Variant.VT_UI1;
import static com.sun.jna.platform.win32.Variant.VT_UI2;
import static com.sun.jna.platform.win32.Variant.VT_UI4;
import static com.sun.jna.platform.win32.Variant.VT_UI8;
import static com.sun.jna.platform.win32.Variant.VT_UINT;
import static com.sun.jna.platform.win32.Variant.VT_UNKNOWN;
import static com.sun.jna.platform.win32.Variant.VT_VARIANT;
import com.sun.jna.platform.win32.WinDef.PVOID;

/**
 * This class is considered internal to the package.
 */
class Convert {
        /**
         * Convert a java value into a VARIANT suitable for passing in a COM
         * invocation.
         * 
         * <p><i>Implementation notes</i></p>
         * 
         * <ul>
         * <li>VARIANTs are not rewrapped, but passed through unmodified</li>
         * <li>A string is wrapped into a BSTR, that is wrapped into the VARIANT.
         *  The string is allocated as native memory by the VARIANT constructor.
         *  The BSTR needs to be freed by {@link com.sun.jna.platform.win32.OleAuto#SysFreeString}.</li>
         * </ul>
         * 
         * @param value to be wrapped
         * @return wrapped VARIANT
         */
	public static VARIANT toVariant(Object value) {
                if (value instanceof VARIANT) {
                        return (VARIANT) value;
                } else if (value instanceof BSTR) {
                        return new VARIANT((BSTR) value);
                } else if (value instanceof VARIANT_BOOL) {
                        return new VARIANT((VARIANT_BOOL) value);
                } else if (value instanceof BOOL) {
                        return new VARIANT((BOOL) value);
                } else if (value instanceof LONG) {
                        return new VARIANT((LONG) value);
                } else if (value instanceof SHORT) {
                        return new VARIANT((SHORT) value);
                } else if (value instanceof DATE) {
                        return new VARIANT((DATE) value);
                } else if (value instanceof BYTE) {
                        return new VARIANT((BYTE) value);
                } else if (value instanceof Byte) {
                        return new VARIANT((Byte) value);
                } else if (value instanceof Character) {
                        return new VARIANT((Character) value);
                } else if (value instanceof CHAR) {
                        return new VARIANT((CHAR) value);
		} else if (value instanceof Short) {
			return new VARIANT((Short) value);
		} else if (value instanceof Integer) {
			return new VARIANT((Integer) value);
		} else if (value instanceof Long) {
			return new VARIANT((Long) value);
		} else if (value instanceof Float) {
			return new VARIANT((Float) value);
		} else if (value instanceof Double) {
			return new VARIANT((Double) value);
		} else if (value instanceof String) {
			return new VARIANT((String) value);
                } else if (value instanceof Boolean) {
			return new VARIANT((Boolean) value);
                } else if (value instanceof com.sun.jna.platform.win32.COM.IDispatch) {
			return new VARIANT((com.sun.jna.platform.win32.COM.IDispatch) value);
		} else if (value instanceof Date) {
			return new VARIANT((Date) value);
		} else if (value instanceof Proxy) {
			InvocationHandler ih = Proxy.getInvocationHandler(value);
			ProxyObject pobj = (ProxyObject) ih;
			return new VARIANT(pobj.getRawDispatch());
		} else if (value instanceof IComEnum) {
			IComEnum enm = (IComEnum) value;
			return new VARIANT(new WinDef.LONG(enm.getValue()));
                } else if (value instanceof SAFEARRAY) {
                        return new VARIANT((SAFEARRAY) value);
		} else {
			return null;
		}
	}
	
	public static Object toJavaObject(VARIANT value, Class<?> targetClass, ObjectFactory factory, boolean addReference, boolean freeValue) {
		if (null==value 
                        || value.getVarType().intValue() == VT_EMPTY 
                        || value.getVarType().intValue() == VT_NULL) {
                    return null;
                }
                
                if (targetClass != null  && (!targetClass.isAssignableFrom(Object.class))) {
                    if (targetClass.isAssignableFrom(value.getClass())) {
                        return value;
                    }

                    Object vobj = value.getValue();
                    if (vobj != null && (targetClass.isAssignableFrom(vobj.getClass()))) {
                        return vobj;
                    }
                }
                
                VARIANT inputValue = value;
                
                if (value.getVarType().intValue() == (VT_BYREF | VT_VARIANT)) {
                    value = (VARIANT) value.getValue();
                }
                
                // Passing null or Object.class as targetClass switch to default
                // handling
                if (targetClass == null || (targetClass.isAssignableFrom(Object.class))) {
                    
                    targetClass = null;
                    
                    int varType = value.getVarType().intValue();
                    
                    switch (value.getVarType().intValue()) {
                        case VT_UI1:
                        case VT_I1:
                        case VT_BYREF | VT_UI1:
                        case VT_BYREF | VT_I1:
                            targetClass = Byte.class;
                            break;
                        case VT_I2:
                        case VT_BYREF | VT_I2:
                            targetClass = Short.class;
                            break;
                        case VT_UI2:
                        case VT_BYREF | VT_UI2:
                            targetClass = Character.class;
                            break;
                        case VT_INT:
                        case VT_UINT:
                        case VT_UI4:
                        case VT_I4:
                        case VT_BYREF | VT_I4:
                        case VT_BYREF | VT_UI4:
                        case VT_BYREF | VT_INT:
                        case VT_BYREF | VT_UINT:
                            targetClass = Integer.class;
                            break;
                        case VT_UI8:
                        case VT_I8:
                        case VT_BYREF | VT_I8:
                        case VT_BYREF | VT_UI8:
                            targetClass = Long.class;
                            break;
                        case VT_R4:
                        case VT_BYREF | VT_R4:
                            targetClass = Float.class;
                            break;
                        case VT_R8:
                        case VT_BYREF | VT_R8:
                            targetClass = Double.class;
                            break;
                        case VT_BOOL:
                        case VT_BYREF | VT_BOOL:
                            targetClass = Boolean.class;
                            break;
                        case VT_ERROR:
                        case VT_BYREF | VT_ERROR:
                            targetClass = WinDef.SCODE.class;
                            break;
                        case VT_CY:
                        case VT_BYREF | VT_CY:
                            targetClass = OaIdl.CURRENCY.class;
                            break;
                        case VT_DATE:
                        case VT_BYREF | VT_DATE:
                            targetClass = Date.class;
                            break;
                        case VT_BSTR:
                        case VT_BYREF | VT_BSTR:
                            targetClass = String.class;
                            break;
                        case VT_UNKNOWN:
                        case VT_BYREF | VT_UNKNOWN:
                            targetClass = com.sun.jna.platform.win32.COM.IUnknown.class;
                            break;
                        case VT_DISPATCH:
                        case VT_BYREF | VT_DISPATCH:
                            targetClass = IDispatch.class;
                            break;
                        case VT_BYREF | VT_VARIANT:
                            targetClass = Variant.class;
                            break;
                        case VT_BYREF:
                            targetClass = PVOID.class;
                            break;
                        case VT_BYREF | VT_DECIMAL:
                            targetClass = OaIdl.DECIMAL.class;
                            break;
                        case VT_RECORD:
                        default:
                            if ((varType & VT_ARRAY) > 0) {
                                targetClass = OaIdl.SAFEARRAY.class;
                            }
                    }
                }
                
                Object result;
                if(Byte.class.equals(targetClass) || byte.class.equals(targetClass)) {
                    result = value.byteValue();
                } else if (Short.class.equals(targetClass) || short.class.equals(targetClass)) {
                    result = value.shortValue();
                } else if (Character.class.equals(targetClass) || char.class.equals(targetClass)) {
                    result = (char) value.intValue();
                } else if (Integer.class.equals(targetClass) || int.class.equals(targetClass)) {
                    result = value.intValue();
                } else if (Long.class.equals(targetClass) || long.class.equals(targetClass) || IComEnum.class.isAssignableFrom(targetClass)) {
                    result = value.longValue();
                } else if (Float.class.equals(targetClass) || float.class.equals(targetClass)) {
                    result = value.floatValue();
                } else if (Double.class.equals(targetClass) || double.class.equals(targetClass)) {
                    result = value.doubleValue();
                } else if (Boolean.class.equals(targetClass) || boolean.class.equals(targetClass)) {
                    result = value.booleanValue();
                } else if (Date.class.equals(targetClass)) {
                    result = value.dateValue();
                } else if (String.class.equals(targetClass)) {
                    result = value.stringValue();
                } else if (value.getValue() instanceof com.sun.jna.platform.win32.COM.IDispatch) {
                    com.sun.jna.platform.win32.COM.IDispatch d = (com.sun.jna.platform.win32.COM.IDispatch) value.getValue();
                    Object proxy = factory.createProxy(targetClass, d);
                    // must release a COM reference, createProxy adds one, as does the
                    // call
                    if (!addReference) {
                        int n = d.Release();
                    }
                    result = proxy;
                } else {
                    /*
                    WinDef.SCODE.class.equals(targetClass) 
                        || OaIdl.CURRENCY.class.equals(targetClass)
                        || OaIdl.DECIMAL.class.equals(targetClass)
                        || OaIdl.SAFEARRAY.class.equals(targetClass)
                        || com.sun.jna.platform.win32.COM.IUnknown.class.equals(targetClass)
                        || Variant.class.equals(targetClass)
                        || PVOID.class.equals(targetClass
                    */
                    result = value.getValue();
                }

                if (IComEnum.class.isAssignableFrom(targetClass)) {
                    result = targetClass.cast(Convert.toComEnum((Class<? extends IComEnum>) targetClass, result));
                }
                
                if(freeValue) {
                    free(inputValue, result);
                }
                
                return result;
	}
	
	public static <T extends IComEnum> T toComEnum(Class<T> enumType, Object value) {
		try {
			Method m = enumType.getMethod("values");
			T[] values = (T[])m.invoke(null);
			for(T t: values) {
				if (value.equals(t.getValue())) {
					return t;
				}
			}
		} catch (NoSuchMethodException e) {
		} catch (IllegalAccessException e) {
		} catch (IllegalArgumentException e) {
		} catch (InvocationTargetException e) {
		}
		return null;
	}
        
        /**
         * Free the contents of the supplied VARIANT.
         * 
         * <p>This method is a companion to {@link #toVariant}. Primary usage is
         * to free BSTRs contained in VARIANTs.</p>
         * 
         * @param variant to be cleared
         * @param javaType type before/after conversion
         */
        public static void free(VARIANT variant, Class<?> javaType) {
            if((javaType == null || (! BSTR.class.isAssignableFrom(javaType)))
                    && variant != null
                    && variant.getVarType().intValue() == Variant.VT_BSTR) {
                Object value = variant.getValue();
                if(value instanceof BSTR) {
                    OleAuto.INSTANCE.SysFreeString((BSTR) value);
                }
            }
        }
        
        /**
         * Free the contents of the supplied VARIANT.
         * 
         * <p>This method is a companion to {@link #toVariant}. Primary usage is
         * to free BSTRs contained in VARIANTs.</p>
         * 
         * @param variant to be cleared
         * @param value value before/after conversion
         */
        public static void free(VARIANT variant, Object value) {
            free(variant, value == null ? null : value.getClass());
        }
}
