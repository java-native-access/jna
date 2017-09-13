/* Copyright (c) 2013 Tobias Wolf, All Rights Reserved
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

import com.sun.jna.Function;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.PointerType;

public abstract class COMInvoker extends PointerType {

    protected int _invokeNativeInt(int vtableId, Object[] args) {
        Pointer vptr = this.getPointer().getPointer(0);
        // we take the vtable id and multiply with the pointer size (4 bytes on
        // 32bit OS)
        Function func = Function.getFunction(vptr.getPointer(vtableId
                * Native.POINTER_SIZE));
        return func.invokeInt(args);
    }

    protected Object _invokeNativeObject(int vtableId, Object[] args, Class<?> returnType) {
        Pointer vptr = this.getPointer().getPointer(0);
        // we take the vtable id and multiply with the pointer size (4 bytes on
        // 32bit OS)
        Function func = Function.getFunction(vptr.getPointer(vtableId
                * Native.POINTER_SIZE));
        return func.invoke(returnType, args);
    }

    protected void _invokeNativeVoid(int vtableId, Object[] args) {
        Pointer vptr = this.getPointer().getPointer(0);
        // we take the vtable id and multiply with the pointer size (4 bytes on
        // 32bit OS)
        Function func = Function.getFunction(vptr.getPointer(vtableId
                * Native.POINTER_SIZE));
        func.invokeVoid(args);
    }

}
