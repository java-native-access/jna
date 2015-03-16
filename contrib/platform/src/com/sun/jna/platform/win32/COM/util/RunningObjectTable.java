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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.platform.win32.COM.COMException;
import com.sun.jna.platform.win32.COM.COMUtils;
import com.sun.jna.ptr.PointerByReference;

public class RunningObjectTable implements IRunningObjectTable {

	protected RunningObjectTable(com.sun.jna.platform.win32.COM.RunningObjectTable raw, Factory factory) {
		this.raw = raw;
		this.factory = factory;
		this.comThread = factory.getComThread();
	}

	Factory factory;
	ComThread comThread;
	com.sun.jna.platform.win32.COM.RunningObjectTable raw;

	@Override
	public Iterable<IDispatch> enumRunning() {

		try {

			final PointerByReference ppenumMoniker = new PointerByReference();

			WinNT.HRESULT hr = this.comThread.execute(new Callable<WinNT.HRESULT>() {
				@Override
				public WinNT.HRESULT call() throws Exception {
					return RunningObjectTable.this.raw.EnumRunning(ppenumMoniker);
				}
			});
			COMUtils.checkRC(hr);
			com.sun.jna.platform.win32.COM.EnumMoniker raw = new com.sun.jna.platform.win32.COM.EnumMoniker(
					ppenumMoniker.getValue());

			return new EnumMoniker(raw, this.raw, this.factory);

		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		} catch (ExecutionException e) {
			throw new RuntimeException(e);
		} catch (TimeoutException e) {
			throw new RuntimeException(e);
		}

	}

	@Override
	public <T> List<T> getActiveObjectsByInterface(Class<T> comInterface) {
		List<T> result = new ArrayList<T>();

		for (IDispatch obj : this.enumRunning()) {
			try {
				T dobj = obj.queryInterface(comInterface);

				result.add(dobj);
			} catch (COMException ex) {

			}
		}

		return result;
	}
}
