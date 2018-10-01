/*
 * Copyright (c) 2018 Václav Haisman, All Rights Reserved
 *
 * The contents of this file is dual-licensed under 2
 * alternative Open Source/Free licenses: LGPL 2.1 or later and
 * Apache License 2.0.
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
package com.sun.jna.platform.linux;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.platform.linux.XAttr.size_t;
import com.sun.jna.platform.linux.XAttr.ssize_t;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

public final class XAttrUtil {

    public static final String UTF_8 = "UTF-8";

    private XAttrUtil() {
    }

    public static void setXAttr(String path, String name, String value) throws IOException {
        setXAttr(path, name, value, UTF_8);
    }

    public static void setXAttr(String path, String name, String value, String encoding)
        throws IOException {
        setXAttr(path, name, value.getBytes(encoding));
    }

    public static void setXAttr(String path, String name, byte[] value) throws IOException {
        Memory valueMem = bytesToMemory(value);
        int retval = XAttr.INSTANCE.setxattr(path, name, valueMem, new size_t(valueMem.size()), 0);
        final int eno = Native.getLastError();
        if (retval != 0) {
            throw new IOException("errno: " + eno);
        }
    }


    public static void lSetXAttr(String path, String name, String value) throws IOException {
        lSetXAttr(path, name, value, UTF_8);
    }

    public static void lSetXAttr(String path, String name, String value, String encoding)
        throws IOException {
        lSetXAttr(path, name, value.getBytes(encoding));
    }

    public static void lSetXAttr(String path, String name, byte[] value) throws IOException {
        Memory valueMem = bytesToMemory(value);
        final int retval = XAttr.INSTANCE.lsetxattr(path, name, valueMem,
            new size_t(valueMem.size()), 0);
        final int eno = Native.getLastError();
        if (retval != 0) {
            throw new IOException("errno: " + eno);
        }
    }


    public static void fSetXAttr(int fd, String name, String value) throws IOException {
        fSetXAttr(fd, name, value, UTF_8);
    }

    public static void fSetXAttr(int fd, String name, String value, String encoding)
        throws IOException {
        fSetXAttr(fd, name, value.getBytes(encoding));
    }

    public static void fSetXAttr(int fd, String name, byte[] value) throws IOException {
        Memory valueMem = bytesToMemory(value);
        final int retval = XAttr.INSTANCE.fsetxattr(fd, name, valueMem, new size_t(valueMem.size()),
            0);
        final int eno = Native.getLastError();
        if (retval != 0) {
            throw new IOException("errno: " + eno);
        }
    }


    public static String getXAttr(String path, String name) throws IOException {
        return getXAttr(path, name, UTF_8);
    }

    public static String getXAttr(String path, String name, String encoding) throws IOException {
        Memory valueMem = getXAttrAsMemory(path, name);
        return Charset.forName(encoding)
            .decode(valueMem.getByteBuffer(0, valueMem.size()))
            .toString();
    }

    public static byte[] getXAttrBytes(String path, String name) throws IOException {
        Memory valueMem = getXAttrAsMemory(path, name);
        return memoryToBytes(valueMem, (int) valueMem.size());
    }

    public static Memory getXAttrAsMemory(String path, String name) throws IOException {
        ssize_t retval;
        Memory valueMem;
        int eno;

        do {
            retval = XAttr.INSTANCE.getxattr(path, name, null, size_t.ZERO);
            eno = Native.getLastError();
            if (retval.longValue() < 0) {
                throw new IOException("errno: " + eno);
            }

            valueMem = new Memory(retval.longValue());
            retval = XAttr.INSTANCE.getxattr(path, name, valueMem, new size_t(valueMem.size()));
            eno = Native.getLastError();
            if (retval.longValue() < 0 && eno != XAttr.ERANGE) {
                throw new IOException("errno: " + eno);
            }
        } while (retval.longValue() < 0 && eno == XAttr.ERANGE);

        return valueMem;
    }


    public static String lGetXAttr(String path, String name) throws IOException {
        return lGetXAttr(path, name, UTF_8);
    }

    public static String lGetXAttr(String path, String name, String encoding) throws IOException {
        Memory valueMem = lGetXAttrAsMemory(path, name);
        return Charset.forName(encoding)
            .decode(valueMem.getByteBuffer(0, valueMem.size()))
            .toString();
    }

    public static byte[] lGetXAttrBytes(String path, String name) throws IOException {
        Memory valueMem = lGetXAttrAsMemory(path, name);
        return memoryToBytes(valueMem, (int) valueMem.size());
    }

    public static Memory lGetXAttrAsMemory(String path, String name) throws IOException {
        ssize_t retval;
        Memory valueMem;
        int eno;

        do {
            retval = XAttr.INSTANCE.lgetxattr(path, name, null, size_t.ZERO);
            eno = Native.getLastError();
            if (retval.longValue() < 0) {
                throw new IOException("errno: " + eno);
            }

            valueMem = new Memory(retval.longValue());
            retval = XAttr.INSTANCE.lgetxattr(path, name, valueMem, new size_t(valueMem.size()));
            eno = Native.getLastError();
            if (retval.longValue() < 0 && eno != XAttr.ERANGE) {
                throw new IOException("errno: " + eno);
            }
        } while (retval.longValue() < 0 && eno == XAttr.ERANGE);

        return valueMem;
    }


    public static String fGetXAttr(int fd, String name) throws IOException {
        return fGetXAttr(fd, name, UTF_8);
    }

    public static String fGetXAttr(int fd, String name, String encoding) throws IOException {
        Memory valueMem = fGetXAttrAsMemory(fd, name);
        return Charset.forName(encoding)
            .decode(valueMem.getByteBuffer(0, valueMem.size()))
            .toString();
    }

    public static byte[] fGetXAttrBytes(int fd, String name) throws IOException {
        Memory valueMem = fGetXAttrAsMemory(fd, name);
        return memoryToBytes(valueMem, (int) valueMem.size());
    }

    public static Memory fGetXAttrAsMemory(int fd, String name) throws IOException {
        ssize_t retval;
        Memory valueMem;
        int eno;

        do {
            retval = XAttr.INSTANCE.fgetxattr(fd, name, null, size_t.ZERO);
            eno = Native.getLastError();
            if (retval.longValue() < 0) {
                throw new IOException("errno: " + eno);
            }

            valueMem = new Memory(retval.longValue());
            retval = XAttr.INSTANCE.fgetxattr(fd, name, valueMem, new size_t(valueMem.size()));
            eno = Native.getLastError();
            if (retval.longValue() < 0 && eno != XAttr.ERANGE) {
                throw new IOException("errno: " + eno);
            }
        } while (retval.longValue() < 0 && eno == XAttr.ERANGE);

        return valueMem;
    }


    public static Collection<String> listXAttr(String path) throws IOException {
        return listXAttr(path, UTF_8);
    }

    public static Collection<String> listXAttr(String path, String encoding) throws IOException {
        ssize_t retval;
        Memory listMem;
        int eno;

        do {
            retval = XAttr.INSTANCE.listxattr(path, null, size_t.ZERO);
            eno = Native.getLastError();
            if (retval.longValue() < 0) {
                throw new IOException("errno: " + eno);
            }

            listMem = new Memory(retval.longValue());
            retval = XAttr.INSTANCE.listxattr(path, listMem, new size_t(listMem.size()));
            eno = Native.getLastError();
            if (retval.longValue() < 0 && eno != XAttr.ERANGE) {
                throw new IOException("errno: " + eno);
            }
        } while (retval.longValue() < 0 && eno == XAttr.ERANGE);

        return splitBufferToStrings(listMem, encoding);
    }


    public static Collection<String> lListXAttr(String path) throws IOException {
        return lListXAttr(path, UTF_8);
    }

    public static Collection<String> lListXAttr(String path, String encoding) throws IOException {
        ssize_t retval;
        Memory listMem;
        int eno;

        do {
            retval = XAttr.INSTANCE.llistxattr(path, null, size_t.ZERO);
            eno = Native.getLastError();
            if (retval.longValue() < 0) {
                throw new IOException("errno: " + eno);
            }

            listMem = new Memory(retval.longValue());
            retval = XAttr.INSTANCE.llistxattr(path, listMem, new size_t(listMem.size()));
            eno = Native.getLastError();
            if (retval.longValue() < 0 && eno != XAttr.ERANGE) {
                throw new IOException("errno: " + eno);
            }
        } while (retval.longValue() < 0 && eno == XAttr.ERANGE);

        return splitBufferToStrings(listMem, encoding);
    }


    public static Collection<String> fListXAttr(int fd) throws IOException {
        return fListXAttr(fd, UTF_8);
    }

    public static Collection<String> fListXAttr(int fd, String encoding) throws IOException {
        ssize_t retval;
        Memory listMem;
        int eno;

        do {
            retval = XAttr.INSTANCE.flistxattr(fd, null, size_t.ZERO);
            eno = Native.getLastError();
            if (retval.longValue() < 0) {
                throw new IOException("errno: " + eno);
            }

            listMem = new Memory(retval.longValue());
            retval = XAttr.INSTANCE.flistxattr(fd, listMem, new size_t(listMem.size()));
            eno = Native.getLastError();
            if (retval.longValue() < 0 && eno != XAttr.ERANGE) {
                throw new IOException("errno: " + eno);
            }
        } while (retval.longValue() < 0 && eno == XAttr.ERANGE);

        return splitBufferToStrings(listMem, encoding);
    }


    public static void removeXAttr(String path, String name) throws IOException {
        final int retval = XAttr.INSTANCE.removexattr(path, name);
        final int eno = Native.getLastError();
        if (retval != 0) {
            throw new IOException("errno: " + eno);
        }
    }

    public static void lRemoveXAttr(String path, String name) throws IOException {
        final int retval = XAttr.INSTANCE.lremovexattr(path, name);
        final int eno = Native.getLastError();
        if (retval != 0) {
            throw new IOException("errno: " + eno);
        }
    }

    public static void fRemoveXAttr(int fd, String name) throws IOException {
        final int retval = XAttr.INSTANCE.fremovexattr(fd, name);
        final int eno = Native.getLastError();
        if (retval != 0) {
            throw new IOException("errno: " + eno);
        }
    }


    private static byte[] memoryToBytes(Memory mem, int length) {
        final byte[] bytes = new byte[length];
        mem.read(0, bytes, 0, length);
        return bytes;
    }

    private static Memory bytesToMemory(byte[] value) {
        Memory valueMem = new Memory(value.length);
        valueMem.write(0, value, 0, value.length);
        return valueMem;
    }

    private static Collection<String> splitBufferToStrings(Memory valueMem, String encoding)
        throws IOException {
        final Charset charset = Charset.forName(encoding);
        final Set<String> attributesList = new LinkedHashSet<String>(1);
        long offset = 0;
        while (offset != valueMem.size()) {
            // Find terminating NUL character.
            long nulOffset = valueMem.indexOf(offset, (byte) 0);
            if (nulOffset == -1) {
                throw new IOException("Expected NUL character not found.");
            }

            // Duplicate buffer with limit at end of name.
            final ByteBuffer nameBuffer = valueMem.getByteBuffer(offset, nulOffset);

            // Convert bytes of the name to String.
            final String name = charset.decode(nameBuffer).toString();
            attributesList.add(name);

            // Move past NUL.
            offset += nulOffset + 1;
        }
        return attributesList;
    }
}
