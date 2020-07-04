/* Copyright (c) 2020 Torbjörn Svensson, All Rights Reserved
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
package com.sun.jna.win32;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;

/**
 * Native string methods for Windows.
 *
 * @author Torbj&ouml;rn Svensson, azoff[at]svenskalinuxforeningen.se
 */
public abstract class W32StringUtil {
    /**
     * Returns true for unicode API.
     *
     * @return True for unicode API
     */
    public static boolean isAPITypeWide() {
        return W32APITypeMapper.DEFAULT == W32APITypeMapper.UNICODE;
    }

    /**
     * Return the width of TCHAR in C
     *
     * @return The width of TCHAR in C
     */
    public static int getCharWidth() {
        if (isAPITypeWide()) {
            return Native.WCHAR_SIZE;
        }
        return 1;
    }

    /**
     * Convert buffer to string
     *
     * @param pointer The buffer
     * @return The string
     * @see #toString(Pointer)
     * @see #toString(Pointer, int)
     * @see #toString(Pointer, int, int)
     */
    public static String toString(Pointer pointer) {
        return toString(pointer, 0);
    }

    /**
     * Convert buffer to string
     *
     * @param pointer The buffer
     * @param offsetInCharacters The offset in characters
     * @return The string
     * @see #toString(Pointer)
     * @see #toString(Pointer, int, int)
     */
    public static String toString(Pointer pointer, int offsetInCharacters) {
        if (isAPITypeWide()) {
            return pointer.getWideString(offsetInCharacters * getCharWidth());
        } else {
            return pointer.getString(offsetInCharacters);
        }
    }

    /**
     * Convert buffer to string
     *
     * @param pointer The buffer
     * @param offsetInCharacters The offset in characters
     * @param lengthInCharacters The length of the string in characters
     * @return The string
     * @see #toString(Pointer)
     * @see #toString(Pointer, int)
     */
    public static String toString(Pointer pointer, int offsetInCharacters, int lengthInCharacters) {
        byte[] bytes = pointer.getByteArray(offsetInCharacters * getCharWidth(), lengthInCharacters * getCharWidth());
        return toString(bytes, 0);
    }

    /**
     * Convert buffer to string
     *
     * @param buffer The buffer
     * @return The string
     * @see #toString(byte[], long)
     */
    public static String toString(byte[] buffer) {
        return toString(buffer, 0);
    }

    /**
     * Convert buffer to string
     *
     * @param buffer The buffer
     * @param offsetInCharacters The offset in characters
     * @return The string
     * @see #toString(byte[])
     */
    public static String toString(byte[] buffer, long offsetInCharacters) {
        Memory pointer = new Memory(buffer.length + 1 * getCharWidth());
        pointer.write(0, buffer, 0, buffer.length);

        // Ensure null-termination
        byte[] zeros = new byte[getCharWidth()];
        Arrays.fill(zeros, (byte)0);
        pointer.write(buffer.length, zeros, 0, zeros.length);

        if (isAPITypeWide()) {
            return pointer.getWideString(offsetInCharacters * getCharWidth());
        } else {
            return pointer.getString(offsetInCharacters);
        }
    }

    /**
     * Convert buffer to string
     *
     * @param buffer The buffer
     * @return The string
     * @see #toString(char[], int)
     * @see #toString(char[], int, int)
     */
    public static String toString(char[] buffer) {
        return toString(buffer, 0);
    }

    /**
     * Convert buffer to string
     *
     * @param buffer The buffer
     * @param offsetInCharacters The offset in characters
     * @return The string
     * @see #toString(char[])
     * @see #toString(char[], int, int)
     */
    public static String toString(char[] buffer, int offsetInCharacters) {
        Memory pointer = new Memory((buffer.length + 1) * Native.WCHAR_SIZE); // Always allocate for wide
        pointer.write(0, buffer, 0, buffer.length);

        // Ensure null-termination
        byte[] zeros = new byte[getCharWidth()];
        Arrays.fill(zeros, (byte)0);
        pointer.write(buffer.length * getCharWidth(), zeros, 0, zeros.length);

        if (isAPITypeWide()) {
            return pointer.getWideString(offsetInCharacters * getCharWidth());
        } else {
            return pointer.getString(offsetInCharacters);
        }
    }

    /**
     * Convert buffer to string
     *
     * @param buffer The buffer
     * @param offsetInCharacters The offset in characters
     * @param lengthInCharacters The max length in characters of the string
     * @return The string
     * @see #toString(char[])
     * @see #toString(char[], int)
     */
    public static String toString(char[] buffer, int offsetInCharacters, int lengthInCharacters) {
        return toString(buffer, offsetInCharacters).substring(0, lengthInCharacters);
    }

    /**
     * Convert buffer to a raw string
     *
     * @param biffer The buffer
     * @return The string. Note that the string can contain embedded \0.
     * @see #toRawString(char[])
     */
    public static String toRawString(char[] buffer) {
        if (isAPITypeWide()) {
            return new String(buffer);
        } else {
            Memory pointer = new Memory(buffer.length * Native.WCHAR_SIZE); // Always allocate for wide
            pointer.write(0, buffer, 0, buffer.length);
            byte[] bytes = pointer.getByteArray(0, (int)pointer.size());
            return new String(bytes, 0, buffer.length); // Only the first part of the buffer contains data
        }
    }

    /**
     * Convert buffer to a raw string
     *
     * @param pointer The buffer
     * @param sizeInBytes The size of the string to fetch from <code>pointer</code>
     * @return The string. Note that the string can contain embedded \0.
     * @see #toRawString(char[])
     */
    public static String toRawString(Pointer pointer, int sizeInBytes) {
        int sizeInCharacters = sizeInBytes / getCharWidth();
        if (isAPITypeWide()) {
            char[] buffer = pointer.getCharArray(0, sizeInCharacters);
            return new String(buffer);
        } else {
            byte[] buffer = pointer.getByteArray(0, sizeInCharacters);
            return new String(buffer);
        }
    }

    /**
     * Set string in buffer <code>mem</code> at offset <code>offsetInCharacters</code>
     *
     * @param str The string to set
     * @param mem The destination buffer
     * @param offsetInCharacters The offset to the start of the string
     * @see #setString(String, byte[], int)
     * @see #setString(String, char[], int)
     */
    public static void setString(String str, Memory mem, int offsetInCharacters) {
        if (isAPITypeWide()) {
            mem.setWideString(offsetInCharacters * getCharWidth(), str);
        } else {
            mem.setString(offsetInCharacters, str);
        }
    }

    /**
     * Set string in buffer <code>dest</code> at position <code>destPos</code>
     * String will be null-terminated.
     *
     * @param src The string to set
     * @param dest The destination buffer
     * @param destPos The index in <code>dest</code> to write string to
     * @see #setString(String, char[], int)
     * @see #setString(String, Memory, int)
     */
    public static void setString(String src, byte[] dest, int destPos) {
        Memory mem = allocateBuffer(src);
        byte[] buf = mem.getByteArray(0, (int)mem.size());
        System.arraycopy(buf, 0, dest, destPos, buf.length);

        // Null termination
        for (int i = 0; i < getCharWidth(); i++) {
            dest[destPos + buf.length + i] = 0;
        }
    }

    /**
     * Set string in buffer <code>dest</code> at position <code>destPos</code>
     * String will be null-terminated.
     *
     * @param src The string to set
     * @param dest The destination buffer
     * @param destPos The index in <code>dest</code> to write string to
     * @see #setString(String, byte[], int)
     * @see #setString(String, Memory, int)
     */
    public static void setString(String src, char[] dest, int destPos) {
        Memory mem = allocateBuffer(src);
        char[] buf = mem.getCharArray(0, toSizeInCharacters(mem));
        System.arraycopy(buf, 0, dest, destPos, buf.length);

        // Null termination
        for (int i = 0; i < getCharWidth(); i++) {
            dest[destPos + buf.length + i] = 0;
        }
    }

    /**
     * Allocate buffer for string <code>str</code>
     *
     * @param str The string
     * @return Memory object containing string
     * @see #allocateBuffer(String[])
     * @see #allocateBuffer(int)
     */
    public static Memory allocateBuffer(String str) {
        Memory mem = allocateBuffer(str.length() + 1);
        setString(str, mem, 0);
        return mem;
    }

    /**
     * Allocate buffer and join array elements by padding \0
     * Example in memory: <code>str1\0str2\0str3\0\0</code>
     *
     * @param arr The string array
     * @return Memory object containing string array
     * @see #allocateBuffer(String)
     * @see #allocateBuffer(int)
     */
    public static Memory allocateBuffer(String[] arr) {
        int size = 0;
        for (String s : arr) {
            size += s.length() + 1; // Null termination
        }
        size += 1; // Null termination

        Memory data = W32StringUtil.allocateBuffer(size);
        data.clear();
        int offset = 0;
        for (String s : arr) {
            setString(s, data, offset);
            offset += s.length() + 1; // Null termination
        }
        return data;
    }

    /**
     * Allocate buffer for <code>sizeInCharacters</code> number of characters
     *
     * @param sizeInCharacters The number of characters
     * @return Memory object with desired size
     * @see #allocateBuffer(String)
     * @see #allocateBuffer(String[])
     */
    public static Memory allocateBuffer(int sizeInCharacters) {
        return new Memory(sizeInCharacters * getCharWidth());
    }

    /**
     * Return the number of characters <code>mem</code> can contain
     *
     * @param mem The memory pointer
     * @return Number of characters that <code>mem</code> contains
     */
    public static int toSizeInCharacters(Memory mem) {
        return (int)mem.size() / getCharWidth();
    }

    /**
     * Unpack a list of null terminated string to an array of strings.
     * The list ends with an empty string.
     * Example in memory: <code>str1\0str2\0str3\0\0</code>
     *
     * @param ptr Pointer to memory containing the list
     * @return Array of strings
     * @see #fromJoinedStringArray(char[])
     */
    public static String[] fromJoinedStringArray(Pointer ptr) {
        List<String> result = new ArrayList<String>();
        int offsetInCharacters = 0;
        while (true) {
            String str = toString(ptr, offsetInCharacters);
            if (str.isEmpty()) { // found the ending '\0'
                break;
            }

            result.add(str);
            offsetInCharacters += str.length() + 1;
        }
        return result.toArray(new String[result.size()]);
    }

    /**
     * Unpack a list of null terminated string to an array of strings.
     * The list ends with an empty string.
     * Example in memory: <code>str1\0str2\0str3\0\0</code>
     *
     * @param buffer The buffer
     * @return Array of strings
     * @see #fromJoinedStringArray(Pointer)
     */
    public static String[] fromJoinedStringArray(char[] buffer) {
        Memory mem = new Memory((buffer.length + 1) * Native.WCHAR_SIZE); // Always allocate for wide
        mem.clear();
        mem.write(0, buffer, 0, buffer.length);

        return fromJoinedStringArray(mem);
    }
}
