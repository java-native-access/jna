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

import static java.util.Collections.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import com.sun.jna.Native;
import com.sun.jna.platform.unix.LibCAPI.size_t;

public class ExtAttrUtil {

    public static List<String> list(String path) throws IOException {
        // get required buffer size
        long bufferLength = ExtAttr.INSTANCE.extattr_list_file(path, ExtAttr.EXTATTR_NAMESPACE_USER, null, new size_t(0)).longValue();

        if (bufferLength < 0) {
            throw new IOException("errno: " + Native.getLastError());
        }

        if (bufferLength == 0) {
            return emptyList();
        }

        ByteBuffer buffer = ByteBuffer.allocate((int) bufferLength);
        long valueLength = ExtAttr.INSTANCE.extattr_list_file(path, ExtAttr.EXTATTR_NAMESPACE_USER, buffer, new size_t(bufferLength)).longValue();

        if (valueLength < 0) {
            throw new IOException("errno: " + Native.getLastError());
        }

        return decodeStringList(buffer);
    }

    public static ByteBuffer get(String path, String name) throws IOException {
        // get required buffer size
        long bufferLength = ExtAttr.INSTANCE.extattr_get_file(path, ExtAttr.EXTATTR_NAMESPACE_USER, name, null, new size_t(0)).longValue();

        if (bufferLength < 0) {
            throw new IOException("errno: " + Native.getLastError());
        }

        if (bufferLength == 0) {
            return ByteBuffer.allocate(0);
        }

        ByteBuffer buffer = ByteBuffer.allocate((int) bufferLength);
        long valueLength = ExtAttr.INSTANCE.extattr_get_file(path, ExtAttr.EXTATTR_NAMESPACE_USER, name, buffer, new size_t(bufferLength)).longValue();

        if (valueLength < 0) {
            throw new IOException("errno: " + Native.getLastError());
        }

        return buffer;
    }

    public static void set(String path, String name, ByteBuffer value) throws IOException {
        long r = ExtAttr.INSTANCE.extattr_set_file(path, ExtAttr.EXTATTR_NAMESPACE_USER, name, value, new size_t(value.remaining())).longValue();
        if (r < 0) {
            throw new IOException("errno: " + Native.getLastError());
        }
    }

    public static void delete(String path, String name) throws IOException {
        int r = ExtAttr.INSTANCE.extattr_delete_file(path, ExtAttr.EXTATTR_NAMESPACE_USER, name);
        if (r < 0) {
            throw new IOException("errno: " + Native.getLastError());
        }
    }

    private static List<String> decodeStringList(ByteBuffer buffer) {
        List<String> list = new ArrayList<>();

        while (buffer.hasRemaining()) {
            int length = buffer.get() & 0xFF;
            byte[] value = new byte[length];
            buffer.get(value);

            try {
                list.add(new String(value, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }

        return list;
    }

}
