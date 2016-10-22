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

import java.util.Iterator;

import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.platform.win32.COM.COMUtils;
import com.sun.jna.platform.win32.COM.Dispatch;
import com.sun.jna.platform.win32.COM.IEnumMoniker;
import com.sun.jna.platform.win32.COM.Moniker;
import com.sun.jna.ptr.PointerByReference;

/**
 * Enumerates the components of a moniker or the monikers in a table of
 * monikers.
 * 
 * @see <a
 *      href="http://msdn.microsoft.com/en-us/library/windows/desktop/ms692852%28v=vs.85%29.aspx">MSDN</a>
 * 
 */
public class EnumMoniker implements Iterable<IDispatch> {

	protected EnumMoniker(IEnumMoniker raw, com.sun.jna.platform.win32.COM.IRunningObjectTable rawRot,
			ObjectFactory factory) {
            
                assert COMUtils.comIsInitialized() : "COM not initialized";
                
		this.rawRot = rawRot;
		this.raw = raw;
		this.factory = factory;

                WinNT.HRESULT hr = raw.Reset();
                COMUtils.checkRC(hr);

		this.cacheNext();
	}

	ObjectFactory factory;
	com.sun.jna.platform.win32.COM.IRunningObjectTable rawRot;
	IEnumMoniker raw;
	Moniker rawNext;

	protected void cacheNext() {
            assert COMUtils.comIsInitialized() : "COM not initialized";
            final PointerByReference rgelt = new PointerByReference();
            final WinDef.ULONGByReference pceltFetched = new WinDef.ULONGByReference();

            WinNT.HRESULT hr = this.raw.Next(new WinDef.ULONG(1), rgelt, pceltFetched);

            if (WinNT.S_OK.equals(hr) && pceltFetched.getValue().intValue() > 0) {
                    this.rawNext = new Moniker(rgelt.getValue());
            } else {
                    if (!WinNT.S_FALSE.equals(hr)) {
                            COMUtils.checkRC(hr);
                    }
                    this.rawNext = null;
            }
	}

	@Override
	public Iterator<IDispatch> iterator() {
		return new Iterator<IDispatch>() {

			@Override
			public boolean hasNext() {
				return null != EnumMoniker.this.rawNext;
			}

			@Override
			public IDispatch next() {
                                assert COMUtils.comIsInitialized() : "COM not initialized";
                                
                                final Moniker moniker = EnumMoniker.this.rawNext;
                                final PointerByReference ppunkObject = new PointerByReference();
                                WinNT.HRESULT hr = EnumMoniker.this.rawRot.GetObject(moniker.getPointer(), ppunkObject);
                                COMUtils.checkRC(hr);

                                // To assist debug, can use the following code
                                // PointerByReference ppbc = new
                                // PointerByReference();
                                // Ole32.INSTANCE.CreateBindCtx(new DWORD(), ppbc);
                                //
                                // BSTRByReference ppszDisplayName = new
                                // BSTRByReference();
                                // hr = moniker.GetDisplayName(ppbc.getValue(),
                                // moniker.getPointer(), ppszDisplayName);
                                // COMUtils.checkRC(hr);
                                // String name = ppszDisplayName.getString();
                                // Ole32.INSTANCE.CoTaskMemFree(ppszDisplayName.getPointer().getPointer(0));

                                // TODO: Can we assume that the object is an
                                // IDispatch ?
                                // Unknown unk = new
                                // Unknown(ppunkObject.getValue());

                                Dispatch dispatch = new Dispatch(ppunkObject.getValue());
                                EnumMoniker.this.cacheNext();
                                IDispatch d = EnumMoniker.this.factory.createProxy(IDispatch.class, dispatch);
                                //must release a COM Ref, GetObject returns a pointer with +1
                                int n = dispatch.Release();
                                return d;
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException("remove");
			}
			
		};
	}

}
