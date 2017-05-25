/* Copyright (c) 2017 Matthias Bläsing, All Rights Reserved
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
import com.sun.jna.platform.win32.Guid.IID;
import com.sun.jna.platform.win32.Guid.REFIID;
import com.sun.jna.platform.win32.Variant;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

public class EnumVariant extends Unknown implements IEnumVariant {
    
    public static final IID IID = new IID("{00020404-0000-0000-C000-000000000046}");
    public static final REFIID REFIID = new REFIID(IID);

    public EnumVariant() {
    }

    public EnumVariant(Pointer p) {
        this.setPointer(p);
    }

    @Override
    public Variant.VARIANT[] Next(int count) {
        Variant.VARIANT[] resultStaging = new Variant.VARIANT[count];
        IntByReference resultCount = new IntByReference();
        WinNT.HRESULT hresult = (WinNT.HRESULT) this._invokeNativeObject(3, new Object[]{getPointer(), resultStaging.length, resultStaging, resultCount}, WinNT.HRESULT.class);
        COMUtils.checkRC(hresult);
        Variant.VARIANT[] result = new Variant.VARIANT[resultCount.getValue()];
        System.arraycopy(resultStaging, 0, result, 0, resultCount.getValue());
        return result;
    }

    @Override
    public void Skip(int count) {
        WinNT.HRESULT hresult = (WinNT.HRESULT) this._invokeNativeObject(4, new Object[]{getPointer(), count}, WinNT.HRESULT.class);
        COMUtils.checkRC(hresult);
    }

    @Override
    public void Reset() {
        WinNT.HRESULT hresult = (WinNT.HRESULT) this._invokeNativeObject(5, new Object[]{getPointer()}, WinNT.HRESULT.class);
        COMUtils.checkRC(hresult);
    }

    @Override
    public EnumVariant Clone() {
        PointerByReference pbr = new PointerByReference();
        WinNT.HRESULT hresult = (WinNT.HRESULT) this._invokeNativeObject(6, new Object[]{getPointer(), pbr}, WinNT.HRESULT.class);
        COMUtils.checkRC(hresult);
        return new EnumVariant(pbr.getValue());
    }
}
