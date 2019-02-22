/*
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
        @Override
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
        if (WIDE_STRING.equals(this.encoding)) {
            int len = (string.length() + 1 ) * Native.WCHAR_SIZE;
            pointer = new StringMemory(len);
            pointer.setWideString(0, string);
        } else {
            byte[] data = Native.getBytes(string, encoding);
            pointer = new StringMemory(data.length + 1);
            pointer.write(0, data, 0, data.length);
            pointer.setByte(data.length, (byte)0);
        }
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof CharSequence) {
            return compareTo(other) == 0;
        }
        return false;
    }

    @Override
    public String toString() {
        boolean wide = WIDE_STRING.equals(encoding);
        return (wide ? pointer.getWideString(0) : pointer.getString(0, encoding));
    }

    public Pointer getPointer() {
        return pointer;
    }

    @Override
    public char charAt(int index) {
        return toString().charAt(index);
    }

    @Override
    public int length() {
        return toString().length();
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        return toString().subSequence(start, end);
    }

    @Override
    public int compareTo(Object other) {
        if (other == null)
            return 1;

        return toString().compareTo(other.toString());
    }
}
