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

/** Provides a temporary allocation of an immutable C string 
 * (<code>const char*</code> or <code>const wchar_t*</code>) for use when 
 * converting a Java String into a native memory function argument.  
 *
 * @author  Todd Fast, todd.fast@sun.com
 * @author twall@users.sf.net
 */
class NativeString implements CharSequence, Comparable {

    private Pointer pointer;
    private boolean wide;

    protected NativeString(Pointer pointer) {
        this(pointer, false);
    }
    
    protected NativeString(Pointer pointer, boolean wide) {
        this.pointer = pointer;
        this.wide = wide;
    }

    public NativeString(String string) {
        this(string, false);
    }
    
    public NativeString(String string, boolean wide) {
        this.wide = wide;
        if (string == null) {
            pointer = null;
        }
        else {
            // Allocate the memory to hold the string.  Note, we have to
            // make this 1 byte longer in order to accomodate the terminating 
            // NUL (which is generated in Pointer.setString()).
            pointer = new Memory((string.length() + 1) * (wide ? Pointer.WCHAR_SIZE : 1));
            pointer.setString(0, string, wide);
        }
    }

    public boolean isValid() {
        return getPointer() != null && getPointer().peer != 0;
    }

    public int hashCode() {
        if (isValid())
            return toString().hashCode();
        else
            return super.hashCode();
    }

    public boolean equals(Object other) {

        if (!isValid())
            return false;

        String s1 = null;
        if (other instanceof NativeString) {
            NativeString cs = (NativeString)other;
            if (!cs.isValid() || cs.wide != wide)
                return false;
            else
                s1 = cs.toString();
        }
        else if (other instanceof String) {
            s1 = (String)other;
        }
        else {
            return false;
        }

        return s1.equals(toString());
    }

    public String toString() {
        if (!isValid()) {
            throw new IllegalStateException("String memory has already been freed");
        }

        return getPointer().getString(0, wide);
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
        return toString().subSequence(start, end);
    }

    public int compareTo(Object other) {
        if (!isValid()) {
            if (other instanceof NativeString) {
                if (!((NativeString)other).isValid())
                    return 0;
            }
            else {
                return -1;
            }
        }

        if (other == null)
            return 1;

        String s1=null;
        if (other instanceof NativeString) {
            if (!((NativeString)other).isValid())
                return 1;
            else
                s1 = ((NativeString)other).toString();
        }
        else if (other instanceof String) {
            s1 = (String)other;
        }
        else {
            s1 = other.toString();
        }

        return toString().compareTo(s1);
    }
}
