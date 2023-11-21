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

/**
 * Utility functions class for handling file extended attributes on Linux.
 */
public abstract class XAttrUtil {

    private XAttrUtil() {
    }

    /**
     * Set or replace value of extended attribute.
     *
     * @param path  file path
     * @param name  extended attribute name
     * @param value value to set
     * @throws IOException on any error
     */
    public static void setXAttr(String path, String name, String value) throws IOException {
        setXAttr(path, name, value, Native.getDefaultStringEncoding());
    }

    /**
     * Set or replace value of extended attribute.
     *
     * @param path     file path
     * @param name     extended attribute name
     * @param value    value to set
     * @param encoding character encoding to be used for stored value
     * @throws IOException on any error
     */
    public static void setXAttr(String path, String name, String value, String encoding)
        throws IOException {
        setXAttr(path, name, value.getBytes(encoding));
    }

    /**
     * Set or replace value of extended attribute.
     *
     * @param path  file path
     * @param name  extended attribute name
     * @param value value to set
     * @throws IOException on any error
     */
    public static void setXAttr(String path, String name, byte[] value) throws IOException {
        int retval = XAttr.INSTANCE.setxattr(path, name, value, new size_t(value.length), 0);
        if (retval != 0) {
            final int eno = Native.getLastError();
            throw new IOException("errno: " + eno);
        }
    }


    /**
     * Set or replace value of extended attribute but in case of symbolic link set the extended
     * attribute on the link itself instead linked file.
     *
     * @param path  file path
     * @param name  extended attribute name
     * @param value value to set
     * @throws IOException on any error
     */
    public static void lSetXAttr(String path, String name, String value) throws IOException {
        lSetXAttr(path, name, value, Native.getDefaultStringEncoding());
    }

    /**
     * Set or replace value of extended attribute but in case of symbolic link set the extended
     * attribute on the link itself instead linked file.
     *
     * @param path     file path
     * @param name     extended attribute name
     * @param value    value to set
     * @param encoding character encoding to be used for stored value
     * @throws IOException on any error
     */
    public static void lSetXAttr(String path, String name, String value, String encoding)
        throws IOException {
        lSetXAttr(path, name, value.getBytes(encoding));
    }

    /**
     * Set or replace value of extended attribute but in case of symbolic link set the extended
     * attribute on the link itself instead linked file.
     *
     * @param path  file path
     * @param name  extended attribute name
     * @param value value to set
     * @throws IOException on any error
     */
    public static void lSetXAttr(String path, String name, byte[] value) throws IOException {
        final int retval = XAttr.INSTANCE.lsetxattr(path, name, value, new size_t(value.length), 0);
        if (retval != 0) {
            final int eno = Native.getLastError();
            throw new IOException("errno: " + eno);
        }
    }


    /**
     * Set or replace value of extended attribute.
     *
     * @param fd    file handle
     * @param name  extended attribute name
     * @param value value to set
     * @throws IOException on any error
     */
    public static void fSetXAttr(int fd, String name, String value) throws IOException {
        fSetXAttr(fd, name, value, Native.getDefaultStringEncoding());
    }

    /**
     * Set or replace value of extended attribute.
     *
     * @param fd       file handle
     * @param name     extended attribute name
     * @param value    value to set
     * @param encoding character encoding to be used for stored value
     * @throws IOException on any error
     */
    public static void fSetXAttr(int fd, String name, String value, String encoding)
        throws IOException {
        fSetXAttr(fd, name, value.getBytes(encoding));
    }

    /**
     * Set or replace value of extended attribute.
     *
     * @param fd    file handle
     * @param name  extended attribute name
     * @param value value to set
     * @throws IOException on any error
     */
    public static void fSetXAttr(int fd, String name, byte[] value) throws IOException {
        final int retval = XAttr.INSTANCE.fsetxattr(fd, name, value, new size_t(value.length), 0);
        if (retval != 0) {
            final int eno = Native.getLastError();
            throw new IOException("errno: " + eno);
        }
    }


    /**
     * Get extended attribute value.
     *
     * @param path file path
     * @param name extended attribute name
     * @return extended attribute value
     * @throws IOException on any error except <code>ERANGE</code> which handled internally
     */
    public static String getXAttr(String path, String name) throws IOException {
        return getXAttr(path, name, Native.getDefaultStringEncoding());
    }

    /**
     * Get extended attribute value.
     *
     * @param path     file path
     * @param name     extended attribute name
     * @param encoding character encoding to be used to decode stored extended attribute value
     * @return extended attribute value
     * @throws IOException on any error except <code>ERANGE</code> which handled internally
     */
    public static String getXAttr(String path, String name, String encoding) throws IOException {
        byte[] valueMem = getXAttrBytes(path, name);
        return new String(valueMem, Charset.forName(encoding));
    }

    /**
     * Get extended attribute value.
     *
     * @param path file path
     * @param name extended attribute name
     * @return extended attribute value
     * @throws IOException on any error except <code>ERANGE</code> which handled internally
     */
    public static byte[] getXAttrBytes(String path, String name) throws IOException {
        ssize_t retval;
        byte[] valueMem;
        int eno = 0;

        do {
            retval = XAttr.INSTANCE.getxattr(path, name, (byte[]) null, size_t.ZERO);
            if (retval.longValue() < 0) {
                eno = Native.getLastError();
                throw new IOException("errno: " + eno);
            }

            valueMem = new byte[retval.intValue()];
            retval = XAttr.INSTANCE.getxattr(path, name, valueMem, new size_t(valueMem.length));
            if (retval.longValue() < 0) {
                eno = Native.getLastError();
                if (eno != XAttr.ERANGE) {
                    throw new IOException("errno: " + eno);
                }
            }
        } while (retval.longValue() < 0 && eno == XAttr.ERANGE);

        return valueMem;
    }

    /**
     * Get extended attribute value.
     *
     * @param path file path
     * @param name extended attribute name
     * @return extended attribute value
     * @throws IOException on any error except <code>ERANGE</code> which handled internally
     */
    public static Memory getXAttrAsMemory(String path, String name) throws IOException {
        ssize_t retval;
        Memory valueMem;
        int eno = 0;

        do {
            retval = XAttr.INSTANCE.getxattr(path, name, (Memory) null, size_t.ZERO);
            if (retval.longValue() < 0) {
                eno = Native.getLastError();
                throw new IOException("errno: " + eno);
            }

            if (retval.longValue() == 0) {
                return null;
            }

            valueMem = new Memory(retval.longValue());
            retval = XAttr.INSTANCE.getxattr(path, name, valueMem, new size_t(valueMem.size()));
            if (retval.longValue() < 0) {
                eno = Native.getLastError();
                if (eno != XAttr.ERANGE) {
                    throw new IOException("errno: " + eno);
                }
            }
        } while (retval.longValue() < 0 && eno == XAttr.ERANGE);

        return valueMem;
    }


    /**
     * Get extended attribute value but in case of symbolic link get the value from the link
     * itself instead of linked file.
     *
     * @param path file path
     * @param name extended attribute name
     * @return extended attribute value
     * @throws IOException on any error except <code>ERANGE</code> which handled internally
     */
    public static String lGetXAttr(String path, String name) throws IOException {
        return lGetXAttr(path, name, Native.getDefaultStringEncoding());
    }

    /**
     * Get extended attribute value but in case of symbolic link get the value from the link
     * itself instead of linked file.
     *
     * @param path     file path
     * @param name     extended attribute name
     * @param encoding character encoding to be used to decode stored extended attribute value
     * @return extended attribute value
     * @throws IOException on any error except <code>ERANGE</code> which handled internally
     */
    public static String lGetXAttr(String path, String name, String encoding) throws IOException {
        byte[] valueMem = lGetXAttrBytes(path, name);
        return new String(valueMem, Charset.forName(encoding));
    }

    /**
     * Get extended attribute value but in case of symbolic link get the value from the link
     * itself instead of linked file.
     *
     * @param path file path
     * @param name extended attribute name
     * @return extended attribute value
     * @throws IOException on any error except <code>ERANGE</code> which handled internally
     */
    public static byte[] lGetXAttrBytes(String path, String name) throws IOException {
        ssize_t retval;
        byte[] valueMem;
        int eno = 0;

        do {
            retval = XAttr.INSTANCE.lgetxattr(path, name, (byte[]) null, size_t.ZERO);
            if (retval.longValue() < 0) {
                eno = Native.getLastError();
                throw new IOException("errno: " + eno);
            }

            valueMem = new byte[retval.intValue()];
            retval = XAttr.INSTANCE.lgetxattr(path, name, valueMem, new size_t(valueMem.length));
            if (retval.longValue() < 0) {
                eno = Native.getLastError();
                if (eno != XAttr.ERANGE) {
                    throw new IOException("errno: " + eno);
                }
            }
        } while (retval.longValue() < 0 && eno == XAttr.ERANGE);

        return valueMem;
    }

    /**
     * Get extended attribute value but in case of symbolic link get the value from the link
     * itself instead of linked file.
     *
     * @param path file path
     * @param name extended attribute name
     * @return extended attribute value
     * @throws IOException on any error except <code>ERANGE</code> which handled internally
     */
    public static Memory lGetXAttrAsMemory(String path, String name) throws IOException {
        ssize_t retval;
        Memory valueMem;
        int eno = 0;

        do {
            retval = XAttr.INSTANCE.lgetxattr(path, name, (Memory) null, size_t.ZERO);
            if (retval.longValue() < 0) {
                eno = Native.getLastError();
                throw new IOException("errno: " + eno);
            }

            if (retval.longValue() == 0) {
                return null;
            }

            valueMem = new Memory(retval.longValue());
            retval = XAttr.INSTANCE.lgetxattr(path, name, valueMem, new size_t(valueMem.size()));
            if (retval.longValue() < 0) {
                eno = Native.getLastError();
                if (eno != XAttr.ERANGE) {
                    throw new IOException("errno: " + eno);
                }
            }
        } while (retval.longValue() < 0 && eno == XAttr.ERANGE);

        return valueMem;
    }


    /**
     * Get extended attribute value.
     *
     * @param fd   file handle
     * @param name extended attribute name
     * @return extended attribute value
     * @throws IOException on any error except <code>ERANGE</code> which handled internally
     */
    public static String fGetXAttr(int fd, String name) throws IOException {
        return fGetXAttr(fd, name, Native.getDefaultStringEncoding());
    }

    /**
     * Get extended attribute value.
     *
     * @param fd       file handle
     * @param name     extended attribute name
     * @param encoding character encoding to be used to decode stored extended attribute value
     * @return extended attribute value
     * @throws IOException on any error except <code>ERANGE</code> which handled internally
     */
    public static String fGetXAttr(int fd, String name, String encoding) throws IOException {
        byte[] valueMem = fGetXAttrBytes(fd, name);
        return new String(valueMem, Charset.forName(encoding));
    }

    /**
     * Get extended attribute value.
     *
     * @param fd   file handle
     * @param name extended attribute name
     * @return extended attribute value
     * @throws IOException on any error except <code>ERANGE</code> which handled internally
     */
    public static byte[] fGetXAttrBytes(int fd, String name) throws IOException {
        ssize_t retval;
        byte[] valueMem;
        int eno = 0;

        do {
            retval = XAttr.INSTANCE.fgetxattr(fd, name, (byte[]) null, size_t.ZERO);
            if (retval.longValue() < 0) {
                eno = Native.getLastError();
                throw new IOException("errno: " + eno);
            }

            valueMem = new byte[retval.intValue()];
            retval = XAttr.INSTANCE.fgetxattr(fd, name, valueMem, new size_t(valueMem.length));
            if (retval.longValue() < 0) {
                eno = Native.getLastError();
                if (eno != XAttr.ERANGE) {
                    throw new IOException("errno: " + eno);
                }
            }
        } while (retval.longValue() < 0 && eno == XAttr.ERANGE);

        return valueMem;
    }

    /**
     * Get extended attribute value.
     *
     * @param fd   file handle
     * @param name extended attribute name
     * @return extended attribute value
     * @throws IOException on any error except <code>ERANGE</code> which handled internally
     */
    public static Memory fGetXAttrAsMemory(int fd, String name) throws IOException {
        ssize_t retval;
        Memory valueMem;
        int eno = 0;

        do {
            retval = XAttr.INSTANCE.fgetxattr(fd, name, (Memory) null, size_t.ZERO);
            if (retval.longValue() < 0) {
                eno = Native.getLastError();
                throw new IOException("errno: " + eno);
            }

            if (retval.longValue() == 0) {
                return null;
            }

            valueMem = new Memory(retval.longValue());
            retval = XAttr.INSTANCE.fgetxattr(fd, name, valueMem, new size_t(valueMem.size()));
            if (retval.longValue() < 0) {
                eno = Native.getLastError();
                if (eno != XAttr.ERANGE) {
                    throw new IOException("errno: " + eno);
                }
            }
        } while (retval.longValue() < 0 && eno == XAttr.ERANGE);

        return valueMem;
    }


    /**
     * List extended attributes on file.
     *
     * @param path file path
     * @return collection of extended attributes' names
     * @throws IOException on any error except <code>ERANGE</code> which handled internally
     */
    public static Collection<String> listXAttr(String path) throws IOException {
        return listXAttr(path, Native.getDefaultStringEncoding());
    }

    /**
     * List extended attributes on file.
     *
     * @param path     file path
     * @param encoding character encoding use to decode extended attributes' names
     * @return collection of extended attributes' names
     * @throws IOException on any error except <code>ERANGE</code> which handled internally
     */
    public static Collection<String> listXAttr(String path, String encoding) throws IOException {
        ssize_t retval;
        byte[] listMem;
        int eno = 0;

        do {
            retval = XAttr.INSTANCE.listxattr(path, (byte[]) null, size_t.ZERO);
            if (retval.longValue() < 0) {
                eno = Native.getLastError();
                throw new IOException("errno: " + eno);
            }

            listMem = new byte[retval.intValue()];
            retval = XAttr.INSTANCE.listxattr(path, listMem, new size_t(listMem.length));
            if (retval.longValue() < 0) {
                eno = Native.getLastError();
                if (eno != XAttr.ERANGE) {
                    throw new IOException("errno: " + eno);
                }
            }
        } while (retval.longValue() < 0 && eno == XAttr.ERANGE);

        return splitBufferToStrings(listMem, encoding);
    }


    /**
     * List extended attributes on file but in case of symbolic link get extended attributes of
     * the link itself instead of linked file.
     *
     * @param path file path
     * @return collection of extended attributes' names
     * @throws IOException on any error except <code>ERANGE</code> which handled internally
     */
    public static Collection<String> lListXAttr(String path) throws IOException {
        return lListXAttr(path, Native.getDefaultStringEncoding());
    }

    /**
     * List extended attributes on file but in case of symbolic link get extended attributes of
     * the link itself instead of linked file.
     *
     * @param path     file path
     * @param encoding character encoding use to decode extended attributes' names
     * @return collection of extended attributes' names
     * @throws IOException on any error except <code>ERANGE</code> which handled internally
     */
    public static Collection<String> lListXAttr(String path, String encoding) throws IOException {
        ssize_t retval;
        byte[] listMem;
        int eno = 0;

        do {
            retval = XAttr.INSTANCE.llistxattr(path, (byte[]) null, size_t.ZERO);
            if (retval.longValue() < 0) {
                eno = Native.getLastError();
                throw new IOException("errno: " + eno);
            }

            listMem = new byte[retval.intValue()];
            retval = XAttr.INSTANCE.llistxattr(path, listMem, new size_t(listMem.length));
            if (retval.longValue() < 0) {
                eno = Native.getLastError();
                if (eno != XAttr.ERANGE) {
                    throw new IOException("errno: " + eno);
                }
            }
        } while (retval.longValue() < 0 && eno == XAttr.ERANGE);

        return splitBufferToStrings(listMem, encoding);
    }


    /**
     * List extended attributes on file.
     *
     * @param fd file handle
     * @return collection of extended attributes' names
     * @throws IOException on any error except <code>ERANGE</code> which handled internally
     */
    public static Collection<String> fListXAttr(int fd) throws IOException {
        return fListXAttr(fd, Native.getDefaultStringEncoding());
    }

    /**
     * List extended attributes on file.
     *
     * @param fd       file handle
     * @param encoding character encoding use to decode extended attributes' names
     * @return collection of extended attributes' names
     * @throws IOException on any error except <code>ERANGE</code> which handled internally
     */
    public static Collection<String> fListXAttr(int fd, String encoding) throws IOException {
        ssize_t retval;
        byte[] listMem;
        int eno = 0;

        do {
            retval = XAttr.INSTANCE.flistxattr(fd, (byte[]) null, size_t.ZERO);
            if (retval.longValue() < 0) {
                eno = Native.getLastError();
                throw new IOException("errno: " + eno);
            }

            listMem = new byte[retval.intValue()];
            retval = XAttr.INSTANCE.flistxattr(fd, listMem, new size_t(listMem.length));
            if (retval.longValue() < 0) {
                eno = Native.getLastError();
                if (eno != XAttr.ERANGE) {
                    throw new IOException("errno: " + eno);
                }
            }
        } while (retval.longValue() < 0 && eno == XAttr.ERANGE);

        return splitBufferToStrings(listMem, encoding);
    }


    /**
     * Remove extended attribute from file.
     *
     * @param path file path
     * @param name extended attribute name
     * @throws IOException on any error
     */
    public static void removeXAttr(String path, String name) throws IOException {
        final int retval = XAttr.INSTANCE.removexattr(path, name);
        if (retval != 0) {
            final int eno = Native.getLastError();
            throw new IOException("errno: " + eno);
        }
    }

    /**
     * Remove extended attribute from file but in case of symbolic link remove extended attribute
     * from the link itself instead of linked file.
     *
     * @param path file path
     * @param name extended attribute name
     * @throws IOException on any error
     */
    public static void lRemoveXAttr(String path, String name) throws IOException {
        final int retval = XAttr.INSTANCE.lremovexattr(path, name);
        if (retval != 0) {
            final int eno = Native.getLastError();
            throw new IOException("errno: " + eno);
        }
    }

    /**
     * Remove extended attribute from file.
     *
     * @param fd   file handle
     * @param name extended attribute name
     * @throws IOException on any error
     */
    public static void fRemoveXAttr(int fd, String name) throws IOException {
        final int retval = XAttr.INSTANCE.fremovexattr(fd, name);
        if (retval != 0) {
            final int eno = Native.getLastError();
            throw new IOException("errno: " + eno);
        }
    }

    private static Collection<String> splitBufferToStrings(byte[] valueMem, String encoding)
        throws IOException {
        final Charset charset = Charset.forName(encoding);
        final Set<String> attributesList = new LinkedHashSet<>(1);
        int offset = 0;
        for(int i = 0; i < valueMem.length; i++) {
            // each entry is terminated by a single \0 byte
            if(valueMem[i] == 0) {
                // Convert bytes of the name to String.
                final String name = new String(valueMem, offset, i - offset, charset);
                attributesList.add(name);
                offset = i + 1;
            }
        }
        return attributesList;
    }
}
