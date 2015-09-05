/* This library is free software; you can redistribute it and/or
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

import java.nio.CharBuffer;


/** Provides a temporary allocation of an immutable C string 
 * (<code>const char*</code> or <code>const wchar_t*</code>) for use when 
 * converting a Java String into a native memory function argument.  
 *
 * @author  Todd Fast, todd.fast@sun.com
 * @author twall@users.sf.net
 */
class NativeString implements CharSequence, Comparable {

    static final String WIDE_STRING = "--WIDE-STRING--";

    private Pointer pointer;
    private String encoding;

    private class StringMemory extends Memory {
        public StringMemory(long size) { super(size); }
        public String toString() {
            return NativeString.this.toString();
        }
    }

    /** Create a native string (NUL-terminated array of <code>char</code>).<p>
     * Uses the encoding returned by {@link Native#getDefaultStringEncoding()}.
     */
    public NativeString(String string) {
        this(string, Native.getDefaultStringEncoding());
    }

    /** Create a native string as a NUL-terminated array of <code>wchar_t</code>
     * (if <code>wide</code> is true) or <code>char</code>.<p>
     * If not <code>wide</code>, the encoding is obtained from {@link
     * Native#getDefaultStringEncoding()}. 
     * 
     * @param string value to write to native memory
     * @param wide whether to store the String as <code>wchar_t</code>
     */
    public NativeString(String string, boolean wide) {
        this(string, wide ? WIDE_STRING : Native.getDefaultStringEncoding());
    }

    /** Create a native string as a NUL-terminated array of
     * <code>wchar_t</code>. 
     */
    public NativeString(WString string) {
        this(string.toString(), WIDE_STRING);
    }

    /** Create a native string (NUL-terminated array of <code>char</code>),
     * using the requested encoding.
     */
    public NativeString(String string, String encoding) {
        if (string == null) {
            throw new NullPointerException("String must not be null");
        }
        // Allocate the memory to hold the string.  Note, we have to
        // make this 1 element longer in order to accommodate the terminating 
        // NUL (which is generated in Pointer.setString()).
        this.encoding = encoding;
        if (this.encoding == WIDE_STRING) {
            int len = (string.length() + 1 ) * Native.WCHAR_SIZE;
            pointer = new StringMemory(len);
            pointer.setWideString(0, string);
        }
        else {
            byte[] data = Native.getBytes(string, encoding);
            pointer = new StringMemory(data.length + 1);
            pointer.write(0, data, 0, data.length);
            pointer.setByte(data.length, (byte)0);
        }
    }

    public int hashCode() {
        return toString().hashCode();
    }

    public boolean equals(Object other) {

        if (other instanceof CharSequence) {
            return compareTo(other) == 0;
        }
        return false;
    }

    public String toString() {
        boolean wide = encoding == WIDE_STRING;
        String s = wide ? "const wchar_t*" : "const char*";
        s += "(" + (wide ? pointer.getWideString(0) : pointer.getString(0, encoding)) + ")";
        return s;
    }

    public Pointer getPointer() {
        return pointer;
    }

    public char charAt(int index) {
        return toString().charAt(index);
    }

    public int length() {
        return toString().length();
    }

    public CharSequence subSequence(int start, int end) {
        return CharBuffer.wrap(toString()).subSequence(start, end);
    }

    public int compareTo(Object other) {

        if (other == null)
            return 1;

        return toString().compareTo(other.toString());
    }
}
