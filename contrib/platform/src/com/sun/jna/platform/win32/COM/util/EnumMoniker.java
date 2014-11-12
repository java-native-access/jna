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

import java.util.Iterator;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

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
			Factory factory) {
		this.rawRot = rawRot;
		this.raw = raw;
		this.factory = factory;
		this.comThread = factory.getComThread();

		try {
			WinNT.HRESULT hr = this.comThread.execute(new Callable<WinNT.HRESULT>() {
				@Override
				public WinNT.HRESULT call() throws Exception {
					return EnumMoniker.this.raw.Reset();
				}
			});
			COMUtils.checkRC(hr);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		} catch (ExecutionException e) {
			throw new RuntimeException(e);
		}

		this.cacheNext();
	}

	ComThread comThread;
	Factory factory;
	com.sun.jna.platform.win32.COM.IRunningObjectTable rawRot;
	IEnumMoniker raw;
	Moniker rawNext;

	protected void cacheNext() {
		try {
			final PointerByReference rgelt = new PointerByReference();
			final WinDef.ULONGByReference pceltFetched = new WinDef.ULONGByReference();

			WinNT.HRESULT hr = EnumMoniker.this.comThread.execute(new Callable<WinNT.HRESULT>() {
				@Override
				public WinNT.HRESULT call() throws Exception {
					return EnumMoniker.this.raw.Next(new WinDef.ULONG(1), rgelt, pceltFetched);
				}
			});

			if (WinNT.S_OK.equals(hr) && pceltFetched.getValue().intValue() > 0) {
				this.rawNext = new Moniker(rgelt.getValue());
			} else {
				if (!WinNT.S_FALSE.equals(hr)) {
					COMUtils.checkRC(hr);
				}
				this.rawNext = null;
			}

		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		} catch (ExecutionException e) {
			throw new RuntimeException(e);
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
				try {

					final Moniker moniker = EnumMoniker.this.rawNext;
					final PointerByReference ppunkObject = new PointerByReference();
					WinNT.HRESULT hr = EnumMoniker.this.comThread.execute(new Callable<WinNT.HRESULT>() {
						@Override
						public WinNT.HRESULT call() throws Exception {
							return EnumMoniker.this.rawRot.GetObject(moniker.getPointer(), ppunkObject);
						}
					});
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
					
					return new ProxyObject(IUnknown.class, dispatch, EnumMoniker.this.factory);
					
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				} catch (ExecutionException e) {
					throw new RuntimeException(e);
				}

			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException("remove");
			}
			
		};
	}

}
