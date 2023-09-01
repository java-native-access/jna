/* Copyright (c) 2023 Reinhard Pointner, All Rights Reserved
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
package com.sun.jna.platform.bsd;

import java.nio.ByteBuffer;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.platform.unix.LibCAPI.size_t;
import com.sun.jna.platform.unix.LibCAPI.ssize_t;

public interface ExtAttr extends Library {

    ExtAttr INSTANCE = Native.load(null, ExtAttr.class);

    int EXTATTR_NAMESPACE_USER = 0x1;

    ssize_t extattr_get_file(String path, int attrnamespace, String attrname, ByteBuffer data, size_t nbytes);

    ssize_t extattr_set_file(String path, int attrnamespace, String attrname, ByteBuffer data, size_t nbytes);

    int extattr_delete_file(String path, int attrnamespace, String attrname);

    ssize_t extattr_list_file(String path, int attrnamespace, ByteBuffer data, size_t nbytes);

}
