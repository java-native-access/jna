/* Copyright (c) 2013 Tobias Wolf, All Rights Reserved
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
package com.sun.jna.platform.win32.COM;

import com.sun.jna.Function;
import com.sun.jna.Pointer;
import com.sun.jna.PointerType;

public abstract class COMInvoker extends PointerType {

    protected int _invokeNativeInt(int vtableId, Object[] args) {
        Pointer vptr = this.getPointer().getPointer(0);
        // we take the vtable id and multiply with the pointer size (4 bytes on
        // 32bit OS)
        Function func = Function.getFunction(vptr.getPointer(vtableId
                * Pointer.SIZE));
        return func.invokeInt(args);
    }

    protected Object _invokeNativeObject(int vtableId, Object[] args, Class<?> returnType) {
        Pointer vptr = this.getPointer().getPointer(0);
        // we take the vtable id and multiply with the pointer size (4 bytes on
        // 32bit OS)
        Function func = Function.getFunction(vptr.getPointer(vtableId
                * Pointer.SIZE));
        return func.invoke(returnType, args);
    }

    protected void _invokeNativeVoid(int vtableId, Object[] args) {
        Pointer vptr = this.getPointer().getPointer(0);
        // we take the vtable id and multiply with the pointer size (4 bytes on
        // 32bit OS)
        Function func = Function.getFunction(vptr.getPointer(vtableId
                * Pointer.SIZE));
        func.invokeVoid(args);
    }

}
