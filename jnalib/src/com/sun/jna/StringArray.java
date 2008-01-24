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
            String s = getPointer(si * Pointer.SIZE).getString(0, wide);
            original[si] = returnWide ? new WString(s) : (Object)s; 
        }
    }
}