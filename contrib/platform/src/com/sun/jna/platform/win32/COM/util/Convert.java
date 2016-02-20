/* Copyright (c) 2014 Dr David H. Akehurst (itemis), All Rights Reserved
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package com.sun.jna.platform.win32.COM.util;

import com.sun.jna.platform.win32.OleAuto;
import com.sun.jna.platform.win32.Variant;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Date;

import com.sun.jna.platform.win32.WTypes;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.Variant.VARIANT;
import com.sun.jna.platform.win32.WTypes.BSTR;

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
         *  The BSTR needs to be freed by {@see com.sun.jna.platform.win32.OleAuto#SysFreeString}.</li>
         * </ul>
         * 
         * @see com.sun.jna.platform.win32.Variant.VARIANT#VARIANT(java.lang.String)
         * @param value to be wrapped
         * @return wrapped VARIANT
         */
	public static VARIANT toVariant(Object value) {
                if (value instanceof VARIANT) {
                        return (VARIANT) value;
                } else if (value instanceof Boolean) {
			return new VARIANT((Boolean) value);
		} else if (value instanceof Long) {
			return new VARIANT(new WinDef.LONG((Long) value));
		} else if (value instanceof Integer) {
			return new VARIANT((Integer) value);
		} else if (value instanceof Short) {
			return new VARIANT(new WinDef.SHORT((Short) value));
		} else if (value instanceof Float) {
			return new VARIANT((Float) value);
		} else if (value instanceof Double) {
			return new VARIANT((Double) value);
		} else if (value instanceof String) {
			return new VARIANT((String) value);
		} else if (value instanceof Date) {
			return new VARIANT((Date) value);
		} else if (value instanceof Proxy) {
			InvocationHandler ih = Proxy.getInvocationHandler(value);
			ProxyObject pobj = (ProxyObject) ih;
			return new VARIANT(pobj.getRawDispatch());
		} else if (value instanceof IComEnum) {
			IComEnum enm = (IComEnum) value;
			return new VARIANT(new WinDef.LONG(enm.getValue()));
		} else {
			return null;
		}
	}
	
	public static Object toJavaObject(VARIANT value, Class targetClass) {
		if (null==value) {
                    return null;
                }
                
                // Passing null or Object.class as targetClass switch to default
                // handling
                boolean concreteClassRequested = targetClass != null
                        && (! targetClass.isAssignableFrom(Object.class));
                
                if (concreteClassRequested && targetClass.isAssignableFrom(value.getClass())) {
                        return value;
                }
                Object vobj = value.getValue();
                if (vobj != null && concreteClassRequested && targetClass.isAssignableFrom(vobj.getClass())) {
                        return vobj;
                }
                // Handle VARIANTByRef
                if(vobj instanceof VARIANT) {
                    vobj = ((VARIANT) vobj).getValue();
                }
                if (vobj instanceof WinDef.BOOL) {
			return ((WinDef.BOOL) vobj).booleanValue();
		} else if (vobj instanceof WinDef.LONG) {
			return ((WinDef.LONG) vobj).longValue();
		} else if (vobj instanceof WinDef.SHORT) {
			return ((WinDef.SHORT) vobj).shortValue();
		} else if (vobj instanceof WinDef.UINT) {
			return ((WinDef.UINT) vobj).intValue();
		} else if (vobj instanceof WinDef.WORD) {
			return ((WinDef.WORD) vobj).intValue();
		} else if (vobj instanceof WTypes.BSTR) {
			return ((WTypes.BSTR) vobj).getValue();
		}
		return vobj;
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
         * <p>This method is a companion to {@see #toVariant}. Primary usage is
         * to free BSTRs contained in VARIANTs.</p>
         * 
         * @param variant to be cleared
         * @param javaType type before/after conversion
         */
        public static void free(VARIANT variant, Class<?> javaType) {
            if(javaType == null) {
                return;
            }
            if(javaType.isAssignableFrom(String.class) 
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
         * <p>This method is a companion to {@see #toVariant}. Primary usage is
         * to free BSTRs contained in VARIANTs.</p>
         * 
         * @param variant to be cleared
         * @param value value before/after conversion
         */
        public static void free(VARIANT variant, Object value) {
            free(variant, value == null ? null : value.getClass());
        }
}
