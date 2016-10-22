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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sun.jna.Pointer;
import com.sun.jna.WString;
import com.sun.jna.platform.win32.Guid.IID;
import com.sun.jna.platform.win32.Guid.REFIID;
import com.sun.jna.platform.win32.OaIdl.DISPID;
import com.sun.jna.platform.win32.OaIdl.DISPIDByReference;
import com.sun.jna.platform.win32.OaIdl.EXCEPINFO;
import com.sun.jna.platform.win32.OleAuto.DISPPARAMS;
import com.sun.jna.platform.win32.Variant;
import com.sun.jna.platform.win32.Variant.VARIANT;
import com.sun.jna.platform.win32.WinDef.LCID;
import com.sun.jna.platform.win32.WinDef.UINT;
import com.sun.jna.platform.win32.WinDef.UINTByReference;
import com.sun.jna.platform.win32.WinDef.WORD;
import com.sun.jna.platform.win32.WinError;
import com.sun.jna.platform.win32.WinNT.HRESULT;
import com.sun.jna.platform.win32.COM.COMException;
import com.sun.jna.platform.win32.COM.COMUtils;
import com.sun.jna.platform.win32.COM.Dispatch;
import com.sun.jna.platform.win32.COM.DispatchListener;
import com.sun.jna.platform.win32.COM.IDispatchCallback;
import com.sun.jna.platform.win32.COM.Unknown;
import com.sun.jna.platform.win32.COM.util.annotation.ComEventCallback;
import com.sun.jna.platform.win32.COM.util.annotation.ComInterface;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

public class CallbackProxy implements IDispatchCallback {
        // Helper declarations, initialized to default values by jvm
        private static boolean DEFAULT_BOOLEAN;
        private static byte DEFAULT_BYTE;
        private static short DEFAULT_SHORT;
        private static int DEFAULT_INT;
        private static long DEFAULT_LONG;
        private static float DEFAULT_FLOAT;
        private static double DEFAULT_DOUBLE;
    
	public CallbackProxy(ObjectFactory factory, Class<?> comEventCallbackInterface,
			IComEventCallbackListener comEventCallbackListener) {
		this.factory = factory;
		this.comEventCallbackInterface = comEventCallbackInterface;
		this.comEventCallbackListener = comEventCallbackListener;
		this.listenedToRiid = this.createRIID(comEventCallbackInterface);
		this.dsipIdMap = this.createDispIdMap(comEventCallbackInterface);
		this.dispatchListener = new DispatchListener(this);
	}

	ObjectFactory factory;
	Class<?> comEventCallbackInterface;
	IComEventCallbackListener comEventCallbackListener;
	REFIID listenedToRiid;
	public DispatchListener dispatchListener;
	Map<DISPID, Method> dsipIdMap;

	REFIID createRIID(Class<?> comEventCallbackInterface) {
		ComInterface comInterfaceAnnotation = comEventCallbackInterface.getAnnotation(ComInterface.class);
		if (null == comInterfaceAnnotation) {
			throw new COMException(
					"advise: Interface must define a value for either iid via the ComInterface annotation");
		}
		String iidStr = comInterfaceAnnotation.iid();
		if (null == iidStr || iidStr.isEmpty()) {
			throw new COMException("ComInterface must define a value for iid");
		}
		return new REFIID(new IID(iidStr).getPointer());
	}

	Map<DISPID, Method> createDispIdMap(Class<?> comEventCallbackInterface) {
		Map<DISPID, Method> map = new HashMap<DISPID, Method>();

		for (Method meth : comEventCallbackInterface.getMethods()) {
			ComEventCallback annotation = meth.getAnnotation(ComEventCallback.class);
			if (null != annotation) {
				int dispId = annotation.dispid();
				if (-1 == dispId) {
					dispId = this.fetchDispIdFromName(annotation);
				}
                                if(dispId == -1) {
                                        CallbackProxy.this.comEventCallbackListener.errorReceivingCallbackEvent(
                                            "DISPID for " + meth.getName() + " not found",
                                            null);
                                }
				map.put(new DISPID(dispId), meth);
			}
		}

		return map;
	}

	int fetchDispIdFromName(ComEventCallback annotation) {
		// TODO
		return -1;
	}

	void invokeOnThread(final DISPID dispIdMember, final REFIID riid, LCID lcid, WORD wFlags,
            final DISPPARAMS.ByReference pDispParams) {

            VARIANT[] arguments = pDispParams.getArgs();
            
            final Method eventMethod = CallbackProxy.this.dsipIdMap.get(dispIdMember);
            if (eventMethod == null) {
                CallbackProxy.this.comEventCallbackListener.errorReceivingCallbackEvent(
                        "No method found with dispId = " + dispIdMember, null);
                return;
            }
            
            /**
             * DISPPARAMs provides two different ways to pass arguments.
             *
             * Arguments can be passed as a linear list with all arguments
             * specified to a certain position (positional) or the position of
             * an argument can be passed via the rgdispidNamedArgs array
             * (named).
             *
             * pDispParams.rgvarg (length in pDispParams.cArgs) contains all
             * arguments (named + position based)
             *
             * pDispParams.rgdispidNamedArgs (length in pDispParams.cNamedArgs)
             * contains the named parameters as DISPIDs - the DISPIDs are the
             * target index in the method signature (zero based).
             *
             * Each entry in pDispParams.rgvarg is either position based or name
             * based and the position bases arguments are passed in reverse
             * order, so getting this:
             *
             * rgvarg = ["arg1", "arg2", "arg3", "arg4", "arg5"]
             * rgdispidNamedArgs = [3, 4]
             *
             * Would lead to this paramater array in the handler:
             *
             * ["arg5", "arg4", "arg3", "arg1", "arg2"]
             *
             * See also:
             * https://msdn.microsoft.com/de-de/library/windows/desktop/ms221653%28v=vs.85%29.aspx
             */   
            
            // Arguments are converted to the JAVA side and IDispatch Interfaces
            // are wrapped into an ProxyObject if so requested.
            //
            // Out-Parameter need to be specified as VARIANT, VARIANT args are
            // not converted, so COM memory allocation rules apply.
            
            DISPID[] positionMap = pDispParams.getRgdispidNamedArgs();
            
            final Class<?>[] paramTypes = eventMethod.getParameterTypes();
            final Object[] params = new Object[paramTypes.length];

            // Handle position based parameters first
            for ( int i = 0; i < params.length && (arguments.length - positionMap.length - i) > 0; i++) {
                Class targetClass = paramTypes[i];
                Variant.VARIANT varg = arguments[arguments.length - i - 1];
                params[i] = Convert.toJavaObject(varg, targetClass, factory, true, false);
            }
            
            for ( int i = 0; i < positionMap.length; i++) {
                int targetPosition = positionMap[i].intValue();
                if(targetPosition >= params.length) {
                    // If less parameters are mapped then supplied, ignore
                    continue;
                }
                Class targetClass = paramTypes[targetPosition];
                Variant.VARIANT varg = arguments[i];
                params[targetPosition] = Convert.toJavaObject(varg, targetClass, factory, true, false);
            }

            
            // Make sure the parameters are correctly initialized -- primitives
            // are initialized to their default value, else a NullPointer
            // exception occurs while doing the call into the target method
            for(int i = 0; i < params.length; i++) {
                if(params[i] == null && paramTypes[i].isPrimitive()) {
                    if (paramTypes[i].equals(boolean.class)) {
                        params[i] = DEFAULT_BOOLEAN;
                    } else if (paramTypes[i].equals(byte.class)) {
                        params[i] = DEFAULT_BYTE;
                    } else if (paramTypes[i].equals(short.class)) {
                        params[i] = DEFAULT_SHORT;
                    } else if (paramTypes[i].equals(int.class)) {
                        params[i] = DEFAULT_INT;
                    } else if (paramTypes[i].equals(long.class)) {
                        params[i] = DEFAULT_LONG;
                    } else if (paramTypes[i].equals(float.class)) {
                        params[i] = DEFAULT_FLOAT;
                    } else if (paramTypes[i].equals(double.class)) {
                        params[i] = DEFAULT_DOUBLE;
                    } else {
                        throw new IllegalArgumentException("Class type " + paramTypes[i].getName() + " not mapped to primitive default value.");
                    }
                }
            }

            try {
                eventMethod.invoke(comEventCallbackListener, params);
            } catch (Exception e) {
                List<String> decodedClassNames = new ArrayList<String>(params.length);
                for(Object o: params) {
                    if(o == null) {
                        decodedClassNames.add("NULL");
                    } else {
                        decodedClassNames.add(o.getClass().getName());
                    }
                }
                CallbackProxy.this.comEventCallbackListener.errorReceivingCallbackEvent(
                        "Exception invoking method " + eventMethod + " supplied: " + decodedClassNames.toString(), e);
            }
        }

	@Override
	public Pointer getPointer() {
		return this.dispatchListener.getPointer();
	}

	// ------------------------ IDispatch ------------------------------
	@Override
	public HRESULT GetTypeInfoCount(UINTByReference pctinfo) {
		return new HRESULT(WinError.E_NOTIMPL);
	}

	@Override
	public HRESULT GetTypeInfo(UINT iTInfo, LCID lcid, PointerByReference ppTInfo) {
		return new HRESULT(WinError.E_NOTIMPL);
	}

	@Override
	public HRESULT GetIDsOfNames(REFIID riid, WString[] rgszNames, int cNames, LCID lcid,
			DISPIDByReference rgDispId) {
		return new HRESULT(WinError.E_NOTIMPL);
	}

	@Override
	public HRESULT Invoke(DISPID dispIdMember, REFIID riid, LCID lcid, WORD wFlags,
			DISPPARAMS.ByReference pDispParams, VARIANT.ByReference pVarResult, EXCEPINFO.ByReference pExcepInfo,
			IntByReference puArgErr) {

                assert COMUtils.comIsInitialized() : "Assumption about COM threading broken.";
                
                this.invokeOnThread(dispIdMember, riid, lcid, wFlags, pDispParams);

		return WinError.S_OK;
	}

	// ------------------------ IUnknown ------------------------------
	@Override
	public HRESULT QueryInterface(REFIID refid, PointerByReference ppvObject) {
		if (null == ppvObject) {
			return new HRESULT(WinError.E_POINTER);
		} else if (refid.equals(this.listenedToRiid)) {
			ppvObject.setValue(this.getPointer());
			return WinError.S_OK;
		} else if (refid.getValue().equals(Unknown.IID_IUNKNOWN)) {
			ppvObject.setValue(this.getPointer());
			return WinError.S_OK;
		} else if (refid.getValue().equals(Dispatch.IID_IDISPATCH)) {
			ppvObject.setValue(this.getPointer());
			return WinError.S_OK;
		}

		return new HRESULT(WinError.E_NOINTERFACE);
	}

	public int AddRef() {
		return 0;
	}

	public int Release() {
		return 0;
	}

}
