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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import com.sun.jna.Pointer;
import com.sun.jna.WString;
import com.sun.jna.platform.win32.Guid;
import com.sun.jna.platform.win32.Guid.IID;
import com.sun.jna.platform.win32.Guid.REFIID;
import com.sun.jna.platform.win32.Kernel32Util;
import com.sun.jna.platform.win32.OaIdl;
import com.sun.jna.platform.win32.OaIdl.DISPID;
import com.sun.jna.platform.win32.OaIdl.DISPIDByReference;
import com.sun.jna.platform.win32.OaIdl.EXCEPINFO;
import com.sun.jna.platform.win32.OleAuto;
import com.sun.jna.platform.win32.OleAuto.DISPPARAMS;
import com.sun.jna.platform.win32.Variant;
import com.sun.jna.platform.win32.Variant.VARIANT;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinDef.DWORDByReference;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.platform.win32.WinNT.HRESULT;
import com.sun.jna.platform.win32.COM.COMException;
import com.sun.jna.platform.win32.COM.COMUtils;
import com.sun.jna.platform.win32.COM.ConnectionPoint;
import com.sun.jna.platform.win32.COM.ConnectionPointContainer;
import com.sun.jna.platform.win32.COM.Dispatch;
import com.sun.jna.platform.win32.COM.IDispatch;
import com.sun.jna.platform.win32.COM.IDispatchCallback;
import com.sun.jna.platform.win32.COM.util.annotation.ComInterface;
import com.sun.jna.platform.win32.COM.util.annotation.ComMethod;
import com.sun.jna.platform.win32.COM.util.annotation.ComProperty;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

/**
 * This object acts as the invocation handler for interfaces annotated with
 * ComInterface. It wraps all (necessary) low level COM calls and dispatches
 * them through the COM runtime.
 * 
 * <p>The caller of the methods is responsible for correct initialization of the
 * COM runtime and appropriate thread-handling - depending on the choosen
 * handling model.</p>
 * 
 * @see <a href="https://msdn.microsoft.com/de-de/library/windows/desktop/ms693344%28v=vs.85%29.aspx">MSDN - Processes, Threads, and Apartments</a>
 * @see <a href="https://msdn.microsoft.com/en-us/library/ms809971.aspx">MSDN - Understanding and Using COM Threading Models</a>
 */
public class ProxyObject implements InvocationHandler, com.sun.jna.platform.win32.COM.util.IDispatch,
		IRawDispatchHandle, IConnectionPoint {
        
	// cached value of the IUnknown interface pointer
	// Rules of COM state that querying for the IUnknown interface must return
	// an identical pointer value
	private long unknownId;
	private final Class<?> theInterface;
	private final ObjectFactory factory;
	private final com.sun.jna.platform.win32.COM.IDispatch rawDispatch;
    
	public ProxyObject(Class<?> theInterface, IDispatch rawDispatch, ObjectFactory factory) {
		this.unknownId = -1;
		this.rawDispatch = rawDispatch;
		this.theInterface = theInterface;
		this.factory = factory;
		// make sure dispatch object knows we have a reference to it
		// (for debug it is usefult to be able to see how many refs are present
		int n = this.rawDispatch.AddRef();
		this.getUnknownId(); // pre cache/calculate it
		factory.register(this);
	}

	private long getUnknownId() {
                assert COMUtils.comIsInitialized() : "COM not initialized";
            
		if (-1 == this.unknownId) {
			try {
				final PointerByReference ppvObject = new PointerByReference();

				Thread current = Thread.currentThread();
				String tn = current.getName();

                                IID iid = com.sun.jna.platform.win32.COM.IUnknown.IID_IUNKNOWN;
				HRESULT hr = ProxyObject.this.getRawDispatch().QueryInterface(new REFIID(iid), ppvObject);

				if (WinNT.S_OK.equals(hr)) {
					Dispatch dispatch = new Dispatch(ppvObject.getValue());
					this.unknownId = Pointer.nativeValue(dispatch.getPointer());
					// QueryInterface returns a COM object pointer with a +1
					// reference, we must drop one,
					// Note: createProxy adds one;
					int n = dispatch.Release();
				} else {
					String formatMessageFromHR = Kernel32Util.formatMessage(hr);
					throw new COMException("getUnknownId: " + formatMessageFromHR, hr);
				}
			} catch (RuntimeException e) {
                            // Do not rewrap COMException
                            if(e instanceof COMException) {
                                throw e;
                            } else {
				throw new COMException("Error occured when trying get Unknown Id ", e);
                            }
                        }
		}
		return this.unknownId;
	}

	@Override
	protected void finalize() throws Throwable {
		this.dispose();
	}

	public synchronized void dispose() {
		if (((Dispatch) this.rawDispatch).getPointer() != Pointer.NULL) {
			this.rawDispatch.Release();
                        ((Dispatch) this.rawDispatch).setPointer(Pointer.NULL);
                        factory.unregister(this);
		}
	}

	@Override
        public com.sun.jna.platform.win32.COM.IDispatch getRawDispatch() {
		return this.rawDispatch;
	}

	// -------------------- Object -------------------------

	/*
	 * The QueryInterface rule state that 'a call to QueryInterface with
	 * IID_IUnknown must always return the same physical pointer value.'
	 *
	 * [http://msdn.microsoft.com/en-us/library/ms686590%28VS.85%29.aspx]
	 *
	 * therefore we can compare the pointers
	 */
	@Override
        public boolean equals(Object arg) {
		if (null == arg) {
			return false;
		} else if (arg instanceof ProxyObject) {
			ProxyObject other = (ProxyObject) arg;
			return this.getUnknownId() == other.getUnknownId();
		} else if (Proxy.isProxyClass(arg.getClass())) {
			InvocationHandler handler = Proxy.getInvocationHandler(arg);
			if (handler instanceof ProxyObject) {
				try {
					ProxyObject other = (ProxyObject) handler;
					return this.getUnknownId() == other.getUnknownId();
				} catch (Exception e) {
					// if can't do this comparison, return false
					// (queryInterface may throw if COM objects become invalid)
					return false;
				}
			} else {
				return false;
			}
		} else {
			return false;
		}
	};

	@Override
	public int hashCode() {
	    long id = this.getUnknownId();
		return (int) ((id >>> 32) & 0xFFFFFFFF) + (int) (id & 0xFFFFFFFF);
	}

	@Override
	public String toString() {
		return this.theInterface.getName() + "{unk=" + this.hashCode() + "}";
	}

	// --------------------- InvocationHandler -----------------------------
	@Override
	public Object invoke(final Object proxy, final java.lang.reflect.Method method, final Object[] args)
			throws Throwable {
                boolean declaredAsInterface = 
                        (method.getAnnotation(ComMethod.class) != null)
                        ||(method.getAnnotation(ComProperty.class) != null);
            
		if ((! declaredAsInterface) && (method.getDeclaringClass().equals(Object.class)
                        || method.getDeclaringClass().equals(IRawDispatchHandle.class)
                        || method.getDeclaringClass().equals(com.sun.jna.platform.win32.COM.util.IUnknown.class)
                        || method.getDeclaringClass().equals(com.sun.jna.platform.win32.COM.util.IDispatch.class)
                        || method.getDeclaringClass().equals(IConnectionPoint.class)
                        )) {
                        try {
                            return method.invoke(this, args);
                        } catch (InvocationTargetException ex) {
                            throw ex.getCause();
                        }
		}

		Class<?> returnType = method.getReturnType();
		boolean isVoid = Void.TYPE.equals(returnType);

		ComProperty prop = method.getAnnotation(ComProperty.class);
		if (null != prop) {
                        int dispId = prop.dispId();
			if (isVoid) {
                                if(dispId != -1) {
                                    this.setProperty(new DISPID(dispId), args[0]);
                                    return null;
                                } else {
                                    String propName = this.getMutatorName(method, prop);
                                    this.setProperty(propName, args[0]);
                                    return null;
                                }
			} else {
                                if(dispId != -1) {
                                    return this.getProperty(returnType, new DISPID(dispId), args);
                                } else {
                                    String propName = this.getAccessorName(method, prop);
                                    return this.getProperty(returnType, propName, args);
                                }
			}
		}

		ComMethod meth = method.getAnnotation(ComMethod.class);
		if (null != meth) {
                        Object[] fullLengthArgs = unfoldWhenVarargs(method, args);
                        int dispId = meth.dispId();
                        if(dispId != -1) {
                            return this.invokeMethod(returnType, new DISPID(dispId), fullLengthArgs);
                        } else {
                            String methName = this.getMethodName(method, meth);
                            return this.invokeMethod(returnType, methName, fullLengthArgs);
                        }
		}

		return null;
	}

	// ---------------------- IConnectionPoint ----------------------
	private ConnectionPoint fetchRawConnectionPoint(IID iid) {
                assert COMUtils.comIsInitialized() : "COM not initialized";
            
		// query for ConnectionPointContainer
		IConnectionPointContainer cpc = this.queryInterface(IConnectionPointContainer.class);
		Dispatch rawCpcDispatch = (Dispatch) cpc.getRawDispatch();
		final ConnectionPointContainer rawCpc = new ConnectionPointContainer(rawCpcDispatch.getPointer());

		// find connection point for comEventCallback interface
		final REFIID adviseRiid = new REFIID(iid.getPointer());
		final PointerByReference ppCp = new PointerByReference();
		HRESULT hr = rawCpc.FindConnectionPoint(adviseRiid, ppCp);
		COMUtils.checkRC(hr);
		final ConnectionPoint rawCp = new ConnectionPoint(ppCp.getValue());
		return rawCp;
	}

	public IComEventCallbackCookie advise(Class<?> comEventCallbackInterface,
			final IComEventCallbackListener comEventCallbackListener)
                        throws COMException {
                 assert COMUtils.comIsInitialized() : "COM not initialized";
            
		try {
			ComInterface comInterfaceAnnotation = comEventCallbackInterface.getAnnotation(ComInterface.class);
			if (null == comInterfaceAnnotation) {
				throw new COMException(
						"advise: Interface must define a value for either iid via the ComInterface annotation");
			}
			final IID iid = this.getIID(comInterfaceAnnotation);

			final ConnectionPoint rawCp = this.fetchRawConnectionPoint(iid);

			// create the dispatch listener
			final IDispatchCallback rawListener = factory.createDispatchCallback(comEventCallbackInterface, comEventCallbackListener);
			// store it the comEventCallback argument, so it is not garbage
			// collected.
			comEventCallbackListener.setDispatchCallbackListener(rawListener);
			// set the dispatch listener to listen to events from the connection
			// point
			final DWORDByReference pdwCookie = new DWORDByReference();
			HRESULT hr = rawCp.Advise(rawListener, pdwCookie);
			int n = rawCp.Release(); // release before check in case check
										// throws exception
			COMUtils.checkRC(hr);

			// return the cookie so that a call to stop listening can be made
			return new ComEventCallbackCookie(pdwCookie.getValue());

		} catch (RuntimeException e) {
                        // Do not rewrap COMException
                        if(e instanceof COMException) {
                            throw e;
                        } else {
                            throw new COMException("Error occured in advise when trying to connect the listener " + comEventCallbackListener, e);
                        }
		}
	}

	public void unadvise(Class<?> comEventCallbackInterface, final IComEventCallbackCookie cookie) throws COMException {
                assert COMUtils.comIsInitialized() : "COM not initialized";
            
		try {
			ComInterface comInterfaceAnnotation = comEventCallbackInterface.getAnnotation(ComInterface.class);
			if (null == comInterfaceAnnotation) {
				throw new COMException(
						"unadvise: Interface must define a value for iid via the ComInterface annotation");
			}
			IID iid = this.getIID(comInterfaceAnnotation);

			final ConnectionPoint rawCp = this.fetchRawConnectionPoint(iid);

			HRESULT hr = rawCp.Unadvise(((ComEventCallbackCookie) cookie).getValue());

			rawCp.Release();
			COMUtils.checkRC(hr);

		} catch (RuntimeException e) {
                        // Do not rewrap COMException
                        if(e instanceof COMException) {
                            throw e;
                        } else {
                            throw new COMException("Error occured in unadvise when trying to disconnect the listener from " + this, e);
                        }
		}
	}

	// --------------------- IDispatch ------------------------------
	@Override
	public <T> void setProperty(String name, T value) {
            DISPID dispID = resolveDispId(this.getRawDispatch(), name);
            setProperty(dispID, value);
	}

        @Override
	public <T> void setProperty(DISPID dispId, T value) {
            assert COMUtils.comIsInitialized() : "COM not initialized";
            
            VARIANT v = Convert.toVariant(value);
            WinNT.HRESULT hr = this.oleMethod(OleAuto.DISPATCH_PROPERTYPUT, null, this.getRawDispatch(), dispId, v);
            Convert.free(v, value); // Free value allocated by Convert#toVariant
            COMUtils.checkRC(hr);
	}
        
	@Override
	public <T> T getProperty(Class<T> returnType, String name, Object... args) {
            DISPID dispID = resolveDispId(this.getRawDispatch(), name);
            return getProperty(returnType, dispID, args);
	}

        @Override
        public <T> T getProperty(Class<T> returnType, DISPID dispID, Object... args) {
            VARIANT[] vargs;
            if (null == args) {
                    vargs = new VARIANT[0];
            } else {
                    vargs = new VARIANT[args.length];
            }
            for (int i = 0; i < vargs.length; ++i) {
                    vargs[i] = Convert.toVariant(args[i]);
            }
            Variant.VARIANT.ByReference result = new Variant.VARIANT.ByReference();
            WinNT.HRESULT hr = this.oleMethod(OleAuto.DISPATCH_PROPERTYGET, result, this.getRawDispatch(), dispID, vargs);

            for (int i = 0; i < vargs.length; i++) {
                    // Free value allocated by Convert#toVariant
                    Convert.free(vargs[i], args[i]);
            }

            COMUtils.checkRC(hr);

            return (T) Convert.toJavaObject(result, returnType, factory, false, true);
        }
        
	@Override
	public <T> T invokeMethod(Class<T> returnType, String name, Object... args) {
                DISPID dispID = resolveDispId(this.getRawDispatch(), name);
                return invokeMethod(returnType, dispID, args);
        }
        
        @Override
        public <T> T invokeMethod(Class<T> returnType, DISPID dispID, Object... args) {
                assert COMUtils.comIsInitialized() : "COM not initialized";
            
		VARIANT[] vargs;
		if (null == args) {
			vargs = new VARIANT[0];
		} else {
			vargs = new VARIANT[args.length];
		}
		for (int i = 0; i < vargs.length; ++i) {
			vargs[i] = Convert.toVariant(args[i]);
		}
		Variant.VARIANT.ByReference result = new Variant.VARIANT.ByReference();
		WinNT.HRESULT hr = this.oleMethod(OleAuto.DISPATCH_METHOD, result, this.getRawDispatch(), dispID, vargs);
                
                for (int i = 0; i < vargs.length; i++) {
                        // Free value allocated by Convert#toVariant
                        Convert.free(vargs[i], args[i]);
                }
                
		COMUtils.checkRC(hr);

                return (T) Convert.toJavaObject(result, returnType, factory, false, true);
	}

	private Object[] unfoldWhenVarargs(java.lang.reflect.Method method, Object[] argParams) {
        if (null == argParams) {
            return null;
        }
        if (argParams.length == 0 || !method.isVarArgs() || !(argParams[argParams.length - 1] instanceof Object[])) {
            return argParams;
        }
        // when last parameter is Object[] -> unfold the ellipsis:
        Object[] varargs = (Object[]) argParams[argParams.length - 1];
        Object[] args = new Object[argParams.length - 1 + varargs.length];
        System.arraycopy(argParams, 0, args, 0, argParams.length - 1);
        System.arraycopy(varargs, 0, args, argParams.length - 1, varargs.length);
        return args;
    }

	@Override
	public <T> T queryInterface(Class<T> comInterface) throws COMException {
                assert COMUtils.comIsInitialized() : "COM not initialized";
            
		try {
			ComInterface comInterfaceAnnotation = comInterface.getAnnotation(ComInterface.class);
			if (null == comInterfaceAnnotation) {
				throw new COMException(
						"queryInterface: Interface must define a value for iid via the ComInterface annotation");
			}
			final IID iid = this.getIID(comInterfaceAnnotation);
			final PointerByReference ppvObject = new PointerByReference();

			HRESULT hr = ProxyObject.this.getRawDispatch().QueryInterface(new REFIID(iid), ppvObject);

			if (WinNT.S_OK.equals(hr)) {
				Dispatch dispatch = new Dispatch(ppvObject.getValue());
				T t = this.factory.createProxy(comInterface, dispatch);
				// QueryInterface returns a COM object pointer with a +1
				// reference, we must drop one,
				// Note: createProxy adds one;
				int n = dispatch.Release();
				return t;
			} else {
				String formatMessageFromHR = Kernel32Util.formatMessage(hr);
				throw new COMException("queryInterface: " + formatMessageFromHR, hr);
			}
		} catch (RuntimeException e) {
                        // Do not rewrap COMException
                        if(e instanceof COMException) {
                            throw e;
                        } else {
                            throw new COMException("Error occured when trying to query for interface " + comInterface.getName(), e);
                        }
		}
	}

	private IID getIID(ComInterface annotation) {
		String iidStr = annotation.iid();
		if (null != iidStr && !iidStr.isEmpty()) {
			return new IID(iidStr);
		} else {
			throw new COMException("ComInterface must define a value for iid");
		}
	}

	// --------------------- ProxyObject ---------------------

	private String getAccessorName(java.lang.reflect.Method method, ComProperty prop) {
		if (prop.name().isEmpty()) {
			String methName = method.getName();
			if (methName.startsWith("get")) {
				return methName.replaceFirst("get", "");
			} else {
				throw new RuntimeException(
						"Property Accessor name must start with 'get', or set the anotation 'name' value");
			}
		} else {
			return prop.name();
		}
	}

	private String getMutatorName(java.lang.reflect.Method method, ComProperty prop) {
		if (prop.name().isEmpty()) {
			String methName = method.getName();
			if (methName.startsWith("set")) {
				return methName.replaceFirst("set", "");
			} else {
				throw new RuntimeException(
						"Property Mutator name must start with 'set', or set the anotation 'name' value");
			}
		} else {
			return prop.name();
		}
	}

	private String getMethodName(java.lang.reflect.Method method, ComMethod meth) {
		if (meth.name().isEmpty()) {
			String methName = method.getName();
			return methName;
		} else {
			return meth.name();
		}
	}

	/*
	 * @see com.sun.jna.platform.win32.COM.COMBindingBaseObject#oleMethod
	 */
	protected HRESULT oleMethod(int nType, VARIANT.ByReference pvResult, IDispatch pDisp, String name, VARIANT pArg)
			throws COMException {
		return this.oleMethod(nType, pvResult, pDisp, name, new VARIANT[] { pArg });
	}

	/*
	 * @see com.sun.jna.platform.win32.COM.COMBindingBaseObject#oleMethod
	 */
	protected HRESULT oleMethod(int nType, VARIANT.ByReference pvResult, IDispatch pDisp, DISPID dispId, VARIANT pArg)
			throws COMException {
		return this.oleMethod(nType, pvResult, pDisp, dispId, new VARIANT[] { pArg });
	}

	/*
	 * @see com.sun.jna.platform.win32.COM.COMBindingBaseObject#oleMethod
	 */
	protected HRESULT oleMethod(int nType, VARIANT.ByReference pvResult, IDispatch pDisp, String name)
			throws COMException {
		return this.oleMethod(nType, pvResult, pDisp, name, (VARIANT[]) null);
	}

	/*
	 * @see com.sun.jna.platform.win32.COM.COMBindingBaseObject#oleMethod
	 */
	protected HRESULT oleMethod(int nType, VARIANT.ByReference pvResult, IDispatch pDisp, DISPID dispId)
			throws COMException {

		return this.oleMethod(nType, pvResult, pDisp, dispId, (VARIANT[]) null);
	}

        protected DISPID resolveDispId(final IDispatch pDisp, String name) {
                assert COMUtils.comIsInitialized() : "COM not initialized";
            
                if (pDisp == null)
                        throw new COMException("pDisp (IDispatch) parameter is null!");

                // variable declaration
                final WString[] ptName = new WString[] { new WString(name) };
                final DISPIDByReference pdispID = new DISPIDByReference();

                // Get DISPID for name passed...
                HRESULT hr = pDisp.GetIDsOfNames(
                        new REFIID(Guid.IID_NULL), 
                        ptName, 
                        1, 
                        factory.getLCID(), 
                        pdispID);

                COMUtils.checkRC(hr);
                
                return pdispID.getValue();
        }
        
	/*
	 * @see com.sun.jna.platform.win32.COM.COMBindingBaseObject#oleMethod
	 */
	protected HRESULT oleMethod(int nType, VARIANT.ByReference pvResult, final IDispatch pDisp, String name,
			VARIANT[] pArgs) throws COMException {

                return this.oleMethod(nType, pvResult, pDisp, resolveDispId(pDisp, name), pArgs);
	}

	/*
	 * @see com.sun.jna.platform.win32.COM.COMBindingBaseObject#oleMethod
	 */
	protected HRESULT oleMethod(final int nType, final VARIANT.ByReference pvResult, final IDispatch pDisp,
			final DISPID dispId, VARIANT[] pArgs) throws COMException {

               assert COMUtils.comIsInitialized() : "COM not initialized";
            
		if (pDisp == null)
			throw new COMException("pDisp (IDispatch) parameter is null!");

		// variable declaration
		int _argsLen = 0;
		VARIANT[] _args = null;
		final DISPPARAMS.ByReference dp = new DISPPARAMS.ByReference();
		final EXCEPINFO.ByReference pExcepInfo = new EXCEPINFO.ByReference();
		final IntByReference puArgErr = new IntByReference();

		// make parameter reverse ordering as expected by COM runtime
		if ((pArgs != null) && (pArgs.length > 0)) {
			_argsLen = pArgs.length;
			_args = new VARIANT[_argsLen];

			int revCount = _argsLen;
			for (int i = 0; i < _argsLen; i++) {
				_args[i] = pArgs[--revCount];
			}
		}

		// Handle special-case for property-puts!
		if (nType == OleAuto.DISPATCH_PROPERTYPUT) {
			dp.setRgdispidNamedArgs(new DISPID[] {OaIdl.DISPID_PROPERTYPUT});
		}
                
                // Apply "fix" according to
                // https://www.delphitools.info/2013/04/30/gaining-visual-basic-ole-super-powers/
                // https://msdn.microsoft.com/en-us/library/windows/desktop/ms221486(v=vs.85).aspx
                //
                // Summary: there are methods in the word typelibrary that require both
                // PROPERTYGET _and_ METHOD to be set. With only one of these set the call
                // fails.
                //
                // The article from delphitools argues, that automation compatible libraries
                // need to be compatible with VisualBasic which does not distingish methods
                // and property getters and will set both flags always.
                //
                // The MSDN article advises this behaviour: "[...] Some languages cannot 
                // distinguish between retrieving a property and calling a method. In this 
                //case, you should set the flags DISPATCH_PROPERTYGET and DISPATCH_METHOD.
                // [...]"))
                //
                // This was found when trying to bind InchesToPoints from the _Application 
                // dispatch interface of the MS Word 15 type library
                //
                // The signature according the ITypeLib Viewer (OLE/COM Object Viewer):
                // [id(0x00000172), helpcontext(0x09700172)]
                // single InchesToPoints([in] single Inches);
                final int finalNType;
                if(nType == OleAuto.DISPATCH_METHOD || nType == OleAuto.DISPATCH_PROPERTYGET) {
                    finalNType = OleAuto.DISPATCH_METHOD | OleAuto.DISPATCH_PROPERTYGET;
                } else {
                    finalNType = nType;
                }

		// Build DISPPARAMS
		if (_argsLen > 0) {
			dp.setArgs(_args);

			// write 'DISPPARAMS' structure to memory
			dp.write();
		}


                HRESULT hr = pDisp.Invoke(
                        dispId, 
                        new REFIID(Guid.IID_NULL), 
                        factory.getLCID(),
                        new WinDef.WORD(finalNType), 
                        dp, 
                        pvResult, 
                        pExcepInfo, 
                        puArgErr);


                COMUtils.checkRC(hr, pExcepInfo, puArgErr);
                return hr;
	}
}
