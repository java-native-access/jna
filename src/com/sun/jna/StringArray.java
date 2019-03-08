/* Copyright (c) 2007-2008 Timothy Wall, All Rights Reserved
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
package com.sun.jna;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** Handle native array of <code>char*</code> or <code>wchar_t*</code> type
 * by managing allocation/disposal of native strings within an array of
 * pointers.  An extra NULL pointer is always added to the end of the native
 * pointer array for convenience.
 */
public class StringArray extends Memory implements Function.PostCallRead {
    private String encoding;
    private List<NativeString> natives = new ArrayList<NativeString>();
    private Object[] original;
    /** Create a native array of strings. */
    public StringArray(String[] strings) {
        this(strings, false);
    }
    /** Create a native array of strings. */
    public StringArray(String[] strings, boolean wide) {
        this((Object[])strings, wide ? NativeString.WIDE_STRING : Native.getDefaultStringEncoding());
    }
    /** Create a native array of strings using the given encoding. */
    public StringArray(String[] strings, String encoding) {
        this((Object[])strings, encoding);
    }
    /** Create a native array of wide strings. */
    public StringArray(WString[] strings) {
        this(strings, NativeString.WIDE_STRING);
    }
    private StringArray(Object[] strings, String encoding) {
        super((strings.length + 1) * Native.POINTER_SIZE);
        this.original = strings;
        this.encoding = encoding;
        for (int i=0;i < strings.length;i++) {
            Pointer p = null;
            if (strings[i] != null) {
                NativeString ns = new NativeString(strings[i].toString(), encoding);
                natives.add(ns);
                p = ns.getPointer();
            }
            setPointer(Native.POINTER_SIZE * i, p);
        }
        setPointer(Native.POINTER_SIZE * strings.length, null);
    }
    /** Read back from native memory. */
    @Override
    public void read() {
        boolean returnWide = original instanceof WString[];
        boolean wide = NativeString.WIDE_STRING.equals(encoding);
        for (int si=0;si < original.length;si++) {
            Pointer p = getPointer(si * Native.POINTER_SIZE);
            Object s = null;
            if (p != null) {
                s = wide ? p.getWideString(0) : p.getString(0, encoding);
                if (returnWide) s = new WString((String)s);
            }
            original[si] = s;
        }
    }

    @Override
    public String toString() {
        boolean wide = NativeString.WIDE_STRING.equals(encoding);
        String s = wide ? "const wchar_t*[]" : "const char*[]";
        s += Arrays.asList(original);
        return s;
    }
}