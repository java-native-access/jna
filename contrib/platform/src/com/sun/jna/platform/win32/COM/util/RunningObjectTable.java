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

import java.util.ArrayList;
import java.util.List;

import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.platform.win32.COM.COMException;
import com.sun.jna.platform.win32.COM.COMUtils;
import com.sun.jna.ptr.PointerByReference;

public class RunningObjectTable implements IRunningObjectTable {

	protected RunningObjectTable(com.sun.jna.platform.win32.COM.RunningObjectTable raw, ObjectFactory factory) {
		this.raw = raw;
		this.factory = factory;
	}

	ObjectFactory factory;
	com.sun.jna.platform.win32.COM.RunningObjectTable raw;

	@Override
	public Iterable<IDispatch> enumRunning() {
                assert COMUtils.comIsInitialized() : "COM not initialized";
            
                final PointerByReference ppenumMoniker = new PointerByReference();

                WinNT.HRESULT hr = this.raw.EnumRunning(ppenumMoniker);

                COMUtils.checkRC(hr);
                com.sun.jna.platform.win32.COM.EnumMoniker raw = new com.sun.jna.platform.win32.COM.EnumMoniker(
                                ppenumMoniker.getValue());

                return new EnumMoniker(raw, this.raw, this.factory);
	}

	@Override
	public <T> List<T> getActiveObjectsByInterface(Class<T> comInterface) {
                assert COMUtils.comIsInitialized() : "COM not initialized";
            
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
