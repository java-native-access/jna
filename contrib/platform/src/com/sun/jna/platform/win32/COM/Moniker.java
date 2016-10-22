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
package com.sun.jna.platform.win32.COM;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.platform.win32.Guid.CLSID;
import com.sun.jna.platform.win32.Ole32;
import com.sun.jna.platform.win32.WTypes;
import com.sun.jna.ptr.PointerByReference;

public class Moniker extends Unknown implements IMoniker {

	public static class ByReference extends Moniker implements Structure.ByReference {
	}

	public Moniker() {
	}

	public Moniker(Pointer pointer) {
		super(pointer);
	}

	// There are 8 virtual methods in the ancestors of this class/interfaces
	static final int vTableIdStart = 7;

	@Override
	public void BindToObject() {
		final int vTableId = vTableIdStart + 1;

		throw new UnsupportedOperationException();
	}

	@Override
	public void BindToStorage() {
		final int vTableId = vTableIdStart + 2;

		throw new UnsupportedOperationException();
	}

	@Override
	public void Reduce() {
		final int vTableId = vTableIdStart + 3;

		throw new UnsupportedOperationException();
	}

	@Override
	public void ComposeWith() {
		final int vTableId = vTableIdStart + 4;

		throw new UnsupportedOperationException();
	}

	@Override
	public void Enum() {
		final int vTableId = vTableIdStart + 5;

		throw new UnsupportedOperationException();
	}

	@Override
	public void IsEqual() {
		final int vTableId = vTableIdStart + 6;

		throw new UnsupportedOperationException();
	}

	@Override
	public void Hash() {
		final int vTableId = vTableIdStart + 7;

		throw new UnsupportedOperationException();
	}

	@Override
	public void IsRunning() {
		final int vTableId = vTableIdStart + 8;

		throw new UnsupportedOperationException();
	}

	@Override
	public void GetTimeOfLastChange() {
		final int vTableId = vTableIdStart + 9;

		throw new UnsupportedOperationException();
	}

	@Override
	public void Inverse() {
		final int vTableId = vTableIdStart + 10;

		throw new UnsupportedOperationException();
	}

	@Override
	public void CommonPrefixWith() {
		final int vTableId = vTableIdStart + 11;

		throw new UnsupportedOperationException();
	}

	@Override
	public void RelativePathTo() {
		final int vTableId = vTableIdStart + 12;

		throw new UnsupportedOperationException();
	}

	@Override
	public String GetDisplayName(Pointer pbc, Pointer pmkToLeft) {
		final int vTableId = vTableIdStart + 13;

                PointerByReference ppszDisplayNameRef = new PointerByReference();
                
		WinNT.HRESULT hr = (WinNT.HRESULT) this._invokeNativeObject(vTableId, new Object[] { this.getPointer(), pbc,
				pmkToLeft, ppszDisplayNameRef }, WinNT.HRESULT.class);

                COMUtils.checkRC(hr);

                Pointer ppszDisplayName = ppszDisplayNameRef.getValue();
                if(ppszDisplayName == null) {
                    return null;
                }
                
                WTypes.LPOLESTR oleStr = new WTypes.LPOLESTR(ppszDisplayName);
                String name = oleStr.getValue();
                Ole32.INSTANCE.CoTaskMemFree(ppszDisplayName);

		return name;
	}

	@Override
	public void ParseDisplayName() {
		final int vTableId = vTableIdStart + 14;

		throw new UnsupportedOperationException();
	}

	@Override
	public void IsSystemMoniker() {
		final int vTableId = vTableIdStart + 15;

		throw new UnsupportedOperationException();
	}

	// ------------------------ IPersistStream ----------------------------
	@Override
	public boolean IsDirty() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void Load(IStream stm) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void Save(IStream stm) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void GetSizeMax() {
		throw new UnsupportedOperationException();
	}

	@Override
	public CLSID GetClassID() {
		throw new UnsupportedOperationException();
	}

}
