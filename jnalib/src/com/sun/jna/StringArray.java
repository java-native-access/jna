/* Copyright (c) 2007-2008 Timothy Wall, All Rights Reserved
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * <p/>
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.  
 */
package com.sun.jna;

import java.util.ArrayList;
import java.util.List;

/** Handle native array of <code>char*</code> or <code>wchar_t*</code> type 
 * by managing allocation/disposal of native strings within an array of 
 * pointers.  An extra NULL pointer is always added to the end of the native
 * pointer array for convenience. 
 */
public class StringArray extends Memory implements Function.PostCallRead {
    private boolean wide;
    private List natives = new ArrayList();
    private Object[] original;
    /** Create a native array of strings. */
    public StringArray(String[] strings) {
        this(strings, false);
    }
    /** Create a native array of wide strings. */
    public StringArray(String[] strings, boolean wide) {
        this((Object[])strings, wide);
    }
    /** Create a native array of wide strings. */
    public StringArray(WString[] strings) {
        this(strings, true);
    }
    private StringArray(Object[] strings, boolean wide) { 
        super((strings.length + 1) * Pointer.SIZE);
        this.original = strings;
        this.wide = wide;
        for (int i=0;i < strings.length;i++) {
            Pointer p = null;
            if (strings[i] != null) {
                NativeString ns = new NativeString(strings[i].toString(), wide);
                natives.add(ns);
                p = ns.getPointer();
            }
            setPointer(Pointer.SIZE * i, p);
        }
        setPointer(Pointer.SIZE * strings.length, null);
    }
    /** Read back from native memory. */
    public void read() {
        boolean returnWide = original instanceof WString[];
        for (int si=0;si < original.length;si++) {
            Pointer p = getPointer(si * Pointer.SIZE);
            Object s = null;
            if (p != null) {
                s = p.getString(0, wide);
                if (returnWide) s = new WString((String)s);
            }
            original[si] = s;
        }
    }
}