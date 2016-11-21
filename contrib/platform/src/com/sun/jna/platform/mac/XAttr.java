/* Copyright (c) 2014 Reinhard Pointner, All Rights Reserved
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
package com.sun.jna.platform.mac;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;

/**
 * JNA wrapper for &lt;sys/xattr.h&gt;
 * 
 */
interface XAttr extends Library {

	// load from current image
	XAttr INSTANCE = Native.loadLibrary(null, XAttr.class);

	// see /usr/include/sys/xattr.h
	int XATTR_NOFOLLOW = 0x0001;
	int XATTR_CREATE = 0x0002;
	int XATTR_REPLACE = 0x0004;
	int XATTR_NOSECURITY = 0x0008;
	int XATTR_NODEFAULT = 0x0010;
	int XATTR_SHOWCOMPRESSION = 0x0020;
	int XATTR_MAXNAMELEN = 127;
	String XATTR_FINDERINFO_NAME = "com.apple.FinderInfo";
	String XATTR_RESOURCEFORK_NAME = "com.apple.ResourceFork";

	// see https://developer.apple.com/library/mac/documentation/Darwin/Reference/ManPages/man2/getxattr.2.html
	long getxattr(String path, String name, Pointer value, long size, int position, int options);

	// see https://developer.apple.com/library/mac/documentation/Darwin/Reference/ManPages/man2/setxattr.2.html
	int setxattr(String path, String name, Pointer value, long size, int position, int options);

	// see https://developer.apple.com/library/mac/documentation/Darwin/Reference/ManPages/man2/removexattr.2.html
	int removexattr(String path, String name, int options);

	// see https://developer.apple.com/library/mac/documentation/Darwin/Reference/ManPages/man2/listxattr.2.html
	long listxattr(String path, Pointer namebuff, long size, int options);

}
