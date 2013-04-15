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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import com.sun.jna.Function;
import com.sun.jna.Pointer;
import com.sun.jna.PointerType;
import com.sun.jna.platform.win32.WinDef.UINT;
import com.sun.jna.platform.win32.WinNT.HRESULT;

public abstract class COMClass extends PointerType implements InvocationHandler {
	
	public Object createCOMClass(Class _interface) {
		return this.createCOMClass(new Class[] { _interface });
	}

	public Object createCOMClass(Class[] _interfaces) {
		ClassLoader classLoader = this.getClass().getClassLoader();
		return Proxy.newProxyInstance(classLoader, _interfaces, this);
	}

	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {

		VTABLE_ID vtableIdAnno = method.getAnnotation(VTABLE_ID.class);

		if (vtableIdAnno == null)
			throw new COMException(
					"Cannot find 'VTABLE_ID' annotation for method: "
							+ method.getName());

		int vtableId = vtableIdAnno.value();

		// check case for args array, some methods have no parameters
		Object[] argsDest;
		if ((args != null) && (args.length > 0)) {
			// resize args array to add the pointer
			argsDest = new Object[args.length + 1];
			argsDest[0] = this.getPointer();
			System.arraycopy(args, 0, argsDest, 1, args.length);
		} else {
			argsDest = new Object[1];
			argsDest[0] = this.getPointer();
		}

		// make the result
		int result = this._invokeNativeInt(vtableId, argsDest);

		Class returnType = method.getReturnType();

		if (returnType == UINT.class)
			return new UINT(result);
		else if (returnType == Integer.class)
			return new Integer(result);
		else
			return new HRESULT(result);
	}

	protected int _invokeNativeInt(int vtableId, Object[] args) {
		Pointer vptr = this.getPointer().getPointer(0);
		// we take the vtable id and multiply with the pointer size (4 bytes on
		// 32bit OS)
		Function func = Function.getFunction(vptr.getPointer(vtableId
				* Pointer.SIZE));
		return func.invokeInt(args);
	}
}
