package com.sun.jna.platform.win32.COM;

import com.sun.jna.Pointer;

public interface IUnknownCallback extends IUnknown {
	Pointer getPointer();
}
