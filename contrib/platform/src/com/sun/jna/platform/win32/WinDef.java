/* Copyright (c) 2010 Daniel Doubrovkine, All Rights Reserved
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
package com.sun.jna.platform.win32;

import java.awt.Rectangle;

import com.sun.jna.IntegerType;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.PointerType;
import com.sun.jna.Structure;
import com.sun.jna.Structure.FieldOrder;
import com.sun.jna.platform.win32.BaseTSD.LONG_PTR;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.platform.win32.WinNT.HANDLEByReference;
import com.sun.jna.ptr.ByReference;

/**
 * Ported from Windef.h (various macros and types). Microsoft Windows SDK 6.0A.
 *
 * @author dblock[at]dblock.org
 */
@SuppressWarnings("serial")
public interface WinDef {

    /** The max path. */
    int MAX_PATH = 260;

    /**
     * 16-bit unsigned integer.
     */
    public static class WORD extends IntegerType implements Comparable<WORD> {

        /** The Constant SIZE. */
        public static final int SIZE = 2;

        /**
         * Instantiates a new word.
         */
        public WORD() {
            this(0);
        }

        /**
         * Instantiates a new word.
         *
         * @param value
         *            the value
         */
        public WORD(long value) {
            super(SIZE, value, true);
        }

        @Override
        public int compareTo(WORD other) {
            return compare(this, other);
        }
    }

    /**
     * The Class WORDByReference.
     */
    public class WORDByReference extends ByReference {

        /**
         * Instantiates a new WORD by reference.
         */
        public WORDByReference() {
            this(new WORD(0));
        }

        /**
         * Instantiates a new WORD by reference.
         *
         * @param value the value
         */
        public WORDByReference(WORD value) {
            super(WORD.SIZE);
            setValue(value);
        }

        /**
         * Sets the value.
         *
         * @param value the new value
         */
        public void setValue(WORD value) {
            getPointer().setShort(0, value.shortValue());
        }

        /**
         * Gets the value.
         *
         * @return the value
         */
        public WORD getValue() {
            return new WORD(getPointer().getShort(0));
        }
    }

    /**
     * 32-bit unsigned integer.
     */
    public static class DWORD extends IntegerType implements Comparable<DWORD> {

        /** The Constant SIZE. */
        public static final int SIZE = 4;

        /**
         * Instantiates a new dword.
         */
        public DWORD() {
            this(0);
        }

        /**
         * Instantiates a new dword.
         *
         * @param value
         *            the value
         */
        public DWORD(long value) {
            super(SIZE, value, true);
        }

        /**
         * Low WORD.
         *
         * @return Low WORD.
         */
        public WORD getLow() {
            return new WORD(longValue() & 0xFFFF);
        }

        /**
         * High WORD.
         *
         * @return High WORD.
         */
        public WORD getHigh() {
            return new WORD((longValue() >> 16) & 0xFFFF);
        }

        @Override
        public int compareTo(DWORD other) {
            return compare(this, other);
        }
    }

    /**
     * The Class DWORDByReference.
     */
    public class DWORDByReference extends ByReference {

        /**
         * Instantiates a new dWOR dby reference.
         */
        public DWORDByReference() {
            this(new DWORD(0));
        }

        /**
         * Instantiates a new dWOR dby reference.
         *
         * @param value the value
         */
        public DWORDByReference(DWORD value) {
            super(DWORD.SIZE);
            setValue(value);
        }

        /**
         * Sets the value.
         *
         * @param value the new value
         */
        public void setValue(DWORD value) {
            getPointer().setInt(0, value.intValue());
        }

        /**
         * Gets the value.
         *
         * @return the value
         */
        public DWORD getValue() {
            return new DWORD(getPointer().getInt(0));
        }
    }

    /**
     * The Class LONG.
     */
    public static class LONG extends IntegerType implements Comparable<LONG> {

        /** The Constant SIZE. */
        public static final int SIZE = Native.LONG_SIZE;

        /**
         * Instantiates a new long.
         */
        public LONG() {
            this(0);
        }

        /**
         * Instantiates a new long.
         *
         * @param value the value
         */
        public LONG(long value) {
            super(SIZE, value);
        }

        @Override
        public int compareTo(LONG other) {
            return compare(this, other);
        }
    }

    /**
     * The Class LONGByReference.
     */
    public class LONGByReference extends ByReference {

        /**
         * Instantiates a new LONG by reference.
         */
        public LONGByReference() {
            this(new LONG(0L));
        }

        /**
         * Instantiates a new LONG by reference.
         *
         * @param value the value
         */
        public LONGByReference(LONG value) {
            super(LONG.SIZE);
            setValue(value);
        }

        /**
         * Sets the value.
         *
         * @param value the new value
         */
        public void setValue(LONG value) {
            getPointer().setInt(0, value.intValue());
        }

        /**
         * Gets the value.
         *
         * @return the value
         */
        public LONG getValue() {
            return new LONG(getPointer().getInt(0));
        }
    }

    /**
     * The Class LONGLONG.
     */
    public static class LONGLONG extends IntegerType implements Comparable<LONGLONG> {

        /** The Constant SIZE. */
        public static final int SIZE = Native.LONG_SIZE *2;

        /**
         * Instantiates a new LONGLONG.
         */
        public LONGLONG() {
            this(0);
        }

        /**
         * Instantiates a new LONGLONG.
         *
         * @param value the value
         */
        public LONGLONG(long value) {
            super(8, value, false);
        }

        @Override
        public int compareTo(LONGLONG other) {
            return compare(this, other);
        }
    }

    /**
     * The Class LONGLONGByReference.
     */
    public class LONGLONGByReference extends ByReference {

        /**
         * Instantiates a new LONGLONG by reference.
         */
        public LONGLONGByReference() {
            this(new LONGLONG(0));
        }

        /**
         * Instantiates a new LONGLONG by reference.
         *
         * @param value the value
         */
        public LONGLONGByReference(LONGLONG value) {
            super(LONGLONG.SIZE);
            setValue(value);
        }

        /**
         * Sets the value.
         *
         * @param value the new value
         */
        public void setValue(LONGLONG value) {
            getPointer().setLong(0, value.longValue());
        }

        /**
         * Gets the value.
         *
         * @return the value
         */
        public LONGLONG getValue() {
            return new LONGLONG(getPointer().getLong(0));
        }
    }

    /**
     * Handle to a device context (DC).
     */
    public static class HDC extends HANDLE {

        /**
         * Instantiates a new hdc.
         */
        public HDC() {

        }

        /**
         * Instantiates a new hdc.
         *
         * @param p
         *            the p
         */
        public HDC(Pointer p) {
            super(p);
        }
    }

    /**
     * Handle to an icon.
     */
    public static class HICON extends HANDLE {

        /**
         * Instantiates a new hicon.
         */
        public HICON() {

        }

        /**
         * Instantiates a new hicon from a handle - this is required since
         * in Win32 API HANDLE and HICON are the same, whereas in Java they
         * are not, so in order to &quot;cast&quot; the handle we need this
         * constructor
         *
         * @param handle The {@link HANDLE} to cast
         */
        public HICON(HANDLE handle) {
            this(handle.getPointer());
        }

        /**
         * Instantiates a new hicon.
         *
         * @param p
         *            the p
         */
        public HICON(Pointer p) {
            super(p);
        }
    }

    /**
     * Handle to a cursor.
     */
    public static class HCURSOR extends HICON {

        /**
         * Instantiates a new hcursor.
         */
        public HCURSOR() {

        }

        /**
         * Instantiates a new hcursor.
         *
         * @param p
         *            the p
         */
        public HCURSOR(Pointer p) {
            super(p);
        }
    }

    /**
     * Handle to a cursor.
     */
    public static class HMENU extends HANDLE {

        /**
         * Instantiates a new hmenu.
         */
        public HMENU() {

        }

        /**
         * Instantiates a new hmenu.
         *
         * @param p
         *            the p
         */
        public HMENU(Pointer p) {
            super(p);
        }
    }

    /**
     * Handle to a pen.
     */
    public static class HPEN extends HANDLE {

        /**
         * Instantiates a new hpen.
         */
        public HPEN() {

        }

        /**
         * Instantiates a new hpen.
         *
         * @param p
         *            the p
         */
        public HPEN(Pointer p) {
            super(p);
        }
    }

    /**
     * Handle to a resource.
     */
    public static class HRSRC extends HANDLE {

        /**
         * Instantiates a new hrsrc.
         */
        public HRSRC() {

        }

        /**
         * Instantiates a new hrsrc.
         *
         * @param p
         *            the p
         */
        public HRSRC(Pointer p) {
            super(p);
        }
    }

    /**
     * Handle to a palette.
     */
    public static class HPALETTE extends HANDLE {

        /**
         * Instantiates a new hpalette.
         */
        public HPALETTE() {

        }

        /**
         * Instantiates a new hpalette.
         *
         * @param p
         *            the p
         */
        public HPALETTE(Pointer p) {
            super(p);
        }
    }

    /**
     * Handle to a bitmap.
     */
    public static class HBITMAP extends HANDLE {

        /**
         * Instantiates a new hbitmap.
         */
        public HBITMAP() {

        }

        /**
         * Instantiates a new hbitmap.
         *
         * @param p
         *            the p
         */
        public HBITMAP(Pointer p) {
            super(p);
        }
    }

    /**
     * Handle to a region.
     */
    public static class HRGN extends HANDLE {

        /**
         * Instantiates a new hrgn.
         */
        public HRGN() {

        }

        /**
         * Instantiates a new hrgn.
         *
         * @param p
         *            the p
         */
        public HRGN(Pointer p) {
            super(p);
        }
    }

    /**
     * Handle to a window.
     */
    public static class HWND extends HANDLE {

        /**
         * Instantiates a new hwnd.
         */
        public HWND() {

        }

        /**
         * Instantiates a new hwnd.
         *
         * @param p
         *            the p
         */
        public HWND(Pointer p) {
            super(p);
        }
    }

    /**
     * Handle to an instance.
     */
    public static class HINSTANCE extends HANDLE {

    }

    /**
     * Handle to a module. The value is the base address of the module.
     */
    public static class HMODULE extends HINSTANCE {

    }

    /**
     * Handle to a font.
     */
    public static class HFONT extends HANDLE {

        /**
         * Instantiates a new hfont.
         */
        public HFONT() {

        }

        /**
         * Instantiates a new hfont.
         *
         * @param p
         *            the p
         */
        public HFONT(Pointer p) {
            super(p);
        }
    }

    /**
     * Handle to a input locale identifier (formerly called keyboard layout
     * handle).
     */
    public static class HKL extends HANDLE {

        /**
         * Instantiates a new hkl.
         */
        public HKL() {

        }

        /**
         * Instantiates a new hkl.
         *
         * @param p the p
         */
        public HKL(Pointer p) {
            super(p);
        }

        public HKL(int i) {
            super(Pointer.createConstant(i));
        }

        /**
         * Get the low word (unsigned short).
         */
        public int getLanguageIdentifier() {
            return (int) (Pointer.nativeValue(getPointer()) & 0xFFFF);
        }

        public int getDeviceHandle() {
            return (int) (Pointer.nativeValue(getPointer()) >> 16 & 0xFFFF);
        }

        @Override
        public String toString() {
            return String.format("%08x", Pointer.nativeValue(getPointer()));
        }
    }

    /**
     * Message parameter.
     */
    public static class LPARAM extends LONG_PTR {

        /**
         * Instantiates a new lparam.
         */
        public LPARAM() {
            this(0);
        }

        /**
         * Instantiates a new lparam.
         *
         * @param value
         *            the value
         */
        public LPARAM(long value) {
            super(value);
        }
    }

    /**
     * Signed result of message processing.
     */
    public static class LRESULT extends LONG_PTR {

        /**
         * Instantiates a new lresult.
         */
        public LRESULT() {
            this(0);
        }

        /**
         * Instantiates a new lresult.
         *
         * @param value
         *            the value
         */
        public LRESULT(long value) {
            super(value);
        }
    }

    /** Integer type big enough for a pointer. */
    public static class INT_PTR extends IntegerType {

        /**
         * Instantiates a new int ptr.
         */
        public INT_PTR() {
            super(Native.POINTER_SIZE);
        }

        /**
         * Instantiates a new int ptr.
         *
         * @param value
         *            the value
         */
        public INT_PTR(long value) {
            super(Native.POINTER_SIZE, value);
        }

        /**
         * To pointer.
         *
         * @return the pointer
         */
        public Pointer toPointer() {
            return Pointer.createConstant(longValue());
        }
    }

    /**
     * Unsigned INT_PTR.
     */
    public static class UINT_PTR extends IntegerType {

        /**
         * Instantiates a new uint ptr.
         */
        public UINT_PTR() {
            super(Native.POINTER_SIZE);
        }

        /**
         * Instantiates a new uint ptr.
         *
         * @param value
         *            the value
         */
        public UINT_PTR(long value) {
            super(Native.POINTER_SIZE, value, true);
        }

        /**
         * To pointer.
         *
         * @return the pointer
         */
        public Pointer toPointer() {
            return Pointer.createConstant(longValue());
        }
    }

    /**
     * Message parameter.
     */
    public static class WPARAM extends UINT_PTR {

        /**
         * Instantiates a new wparam.
         */
        public WPARAM() {
            this(0);
        }

        /**
         * Instantiates a new wparam.
         *
         * @param value
         *            the value
         */
        public WPARAM(long value) {
            super(value);
        }
    }

    /**
     * The Class RECT.
     */
    @FieldOrder({"left", "top", "right", "bottom"})
    public class RECT extends Structure {
        /** The left. */
        public int left;

        /** The top. */
        public int top;

        /** The right. */
        public int right;

        /** The bottom. */
        public int bottom;

        /**
         * To rectangle.
         *
         * @return the rectangle
         */
        public Rectangle toRectangle() {
            return new Rectangle(left, top, right - left, bottom - top);
        }

        @Override
        public String toString() {
            return "[(" + left + "," + top + ")(" + right + "," + bottom + ")]";
        }
    }

    /**
     * 32-bit unsigned integer.
     */
    public static class ULONG extends IntegerType implements Comparable<ULONG> {

        /** The Constant SIZE. */
        public static final int SIZE = Native.LONG_SIZE;

        /**
         * Instantiates a new ULONG.
         */
        public ULONG() {
            this(0);
        }

        /**
         * Instantiates a new ULONG.
         *
         * @param value
         *            the value
         */
        public ULONG(long value) {
            super(SIZE, value, true);
        }

        @Override
        public int compareTo(ULONG other) {
            return compare(this, other);
        }
    }

    /**
     * The Class ULONGByReference.
     */
    public class ULONGByReference extends ByReference {

        /**
         * Instantiates a new ULONG by reference.
         */
        public ULONGByReference() {
            this(new ULONG(0));
        }

        /**
         * Instantiates a new ULONG by reference.
         *
         * @param value the value
         */
        public ULONGByReference(ULONG value) {
            super(ULONG.SIZE);
            setValue(value);
        }

        /**
         * Sets the value.
         *
         * @param value the new value
         */
        public void setValue(ULONG value) {
            getPointer().setInt(0, value.intValue());
        }

        /**
         * Gets the value.
         *
         * @return the value
         */
        public ULONG getValue() {
            return new ULONG(getPointer().getInt(0));
        }
    }

    /**
     * The Class ULONGLONG.
     */
    public static class ULONGLONG extends IntegerType implements Comparable<ULONGLONG> {

        /** The Constant SIZE. */
        public static final int SIZE = 8;

        /**
         * Instantiates a new ULONGLONG.
         */
        public ULONGLONG() {
            this(0);
        }

        /**
         * Instantiates a new ULONGLONG.
         *
         * @param value the value
         */
        public ULONGLONG(long value) {
            super(SIZE, value, true);
        }

        @Override
        public int compareTo(ULONGLONG other) {
            return compare(this, other);
        }
    }

    /**
     * The Class ULONGLONGByReference.
     */
    public class ULONGLONGByReference extends ByReference {

        /**
         * Instantiates a new ULONGLONG by reference.
         */
        public ULONGLONGByReference() {
            this(new ULONGLONG(0));
        }

        /**
         * Instantiates a new ULONGLONG by reference.
         *
         * @param value the value
         */
        public ULONGLONGByReference(ULONGLONG value) {
            super(ULONGLONG.SIZE);
            setValue(value);
        }

        /**
         * Sets the value.
         *
         * @param value the new value
         */
        public void setValue(ULONGLONG value) {
            getPointer().setLong(0, value.longValue());
        }

        /**
         * Gets the value.
         *
         * @return the value
         */
        public ULONGLONG getValue() {
            return new ULONGLONG(getPointer().getLong(0));
        }
    }

    /**
     * 64-bit unsigned integer.
     */
    public static class DWORDLONG extends IntegerType implements Comparable<DWORDLONG> {

        /** The Constant SIZE. */
        public static final int SIZE = 8;

        /**
         * Instantiates a new dwordlong.
         */
        public DWORDLONG() {
            this(0);
        }

        /**
         * Instantiates a new dwordlong.
         *
         * @param value
         *            the value
         */
        public DWORDLONG(long value) {
            super(SIZE, value, true);
        }

        @Override
        public int compareTo(DWORDLONG other) {
            return compare(this, other);
        }
    }

    /**
     * Handle to a bitmap.
     */
    public static class HBRUSH extends HANDLE {

        /**
         * Instantiates a new hbrush.
         */
        public HBRUSH() {

        }

        /**
         * Instantiates a new hbrush.
         *
         * @param p
         *            the p
         */
        public HBRUSH(Pointer p) {
            super(p);
        }
    }

    /**
     * 16-bit unsigned integer.
     */
    public static class ATOM extends WORD {

        /**
         * Instantiates a new atom.
         */
        public ATOM() {
            this(0);
        }

        /**
         * Instantiates a new atom.
         *
         * @param value
         *            the value
         */
        public ATOM(long value) {
            super(value);
        }
    }

    /**
     * The Class PVOID.
     */
    public static class PVOID extends PointerType {

        public PVOID() {
            super();
        }

        /**
         * Instantiates a new pvoid.
         *
         * @param pointer the pointer
         */
        public PVOID(Pointer pointer) {
            super(pointer);
        }
    }

    /**
     * LPVOID is simply a Windows API typedef for void* - to pointer to any type so to speak.
     */
    public static class LPVOID extends PointerType {

        /**
         * Instantiates a new instance to NULL.
         */
        public LPVOID() {
            super();
        }

        /**
         * Instantiates a new instance using a given pointer.
         * @param p the pointer
         */
        public LPVOID(Pointer p) {
            super(p);
        }
    }

    /**
     * The Class POINT.
     */
    @FieldOrder({"x", "y"})
    public class POINT extends Structure {

        /**
         * The Class ByReference.
         */
        public static class ByReference extends POINT implements Structure.ByReference {

            public ByReference() {
            }

            public ByReference(Pointer memory) {
                super(memory);
            }

            public ByReference(int x, int y) {
                super(x, y);
            }

        }

        /**
         * The Class ByValue.
         */
        public static class ByValue extends POINT implements Structure.ByValue {

            public ByValue() {
            }

            public ByValue(Pointer memory) {
                super(memory);
            }

            public ByValue(int x, int y) {
                super(x, y);
            }

        }

        /** The x. */
        public int x;
        /** The y. */
        public int y;
        /**
         * Instantiates a new point.
         */
        public POINT() {
            super();
        }

        /**
         * Instantiates a new point.
         *
         * @param memory
         *            the memory
         */
        public POINT(Pointer memory) {
            super(memory);
            read();
        }

        /**
         * Instantiates a new point.
         *
         * @param x
         *            the x
         * @param y
         *            the y
         */
        public POINT(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    /**
     * 16-bit unsigned short.
     */
    public static class USHORT extends IntegerType implements Comparable<USHORT> {

        /** The Constant SIZE. */
        public static final int SIZE = 2;

        /**
         * Instantiates a new USHORT.
         */
        public USHORT() {
            this(0);
        }

        /**
         * Instantiates a new USHORT.
         *
         * @param value
         *            the value
         */
        public USHORT(long value) {
            super(SIZE, value, true);
        }

        @Override
        public int compareTo(USHORT other) {
            return compare(this, other);
        }
    }

    /**
     * The Class USHORTByReference.
     */
    public class USHORTByReference extends ByReference {

        /**
         * Instantiates a new USHORT by reference.
         */
        public USHORTByReference() {
            this(new USHORT(0));
        }

        /**
         * Instantiates a new USHORT by reference.
         *
         * @param value the value
         */
        public USHORTByReference(USHORT value) {
            super(USHORT.SIZE);
            setValue(value);
        }

        /**
         * Instantiates a new USHORT by reference.
         *
         * @param value the value
         */
        public USHORTByReference(short value) {
            super(USHORT.SIZE);
            setValue(new USHORT(value));
        }

        /**
         * Sets the value.
         *
         * @param value the new value
         */
        public void setValue(USHORT value) {
            getPointer().setShort(0, value.shortValue());
        }

        /**
         * Gets the value.
         *
         * @return the value
         */
        public USHORT getValue() {
            return new USHORT(getPointer().getShort(0));
        }
    }

    /**
     * 16-bit short.
     */
    public static class SHORT extends IntegerType implements Comparable<SHORT> {

        /** The Constant SIZE. */
        public static final int SIZE = 2;

        /**
         * Instantiates a new SHORT.
         */
        public SHORT() {
            this(0);
        }

        /**
         * Instantiates a new SHORT.
         *
         * @param value
         *            the value
         */
        public SHORT(long value) {
            super(SIZE, value, false);
        }

        @Override
        public int compareTo(SHORT other) {
            return compare(this, other);
        }
    }

    /**
     * 32-bit unsigned int.
     */
    public static class UINT extends IntegerType implements Comparable<UINT> {

        /** The Constant SIZE. */
        public static final int SIZE = 4;

        /**
         * Instantiates a new UINT.
         */
        public UINT() {
            this(0);
        }

        /**
         * Instantiates a new UINT.
         *
         * @param value
         *            the value
         */
        public UINT(long value) {
            super(SIZE, value, true);
        }

        @Override
        public int compareTo(UINT other) {
            return compare(this, other);
        }
    }

    /**
     * The Class UINTByReference.
     */
    public class UINTByReference extends ByReference {

        /**
         * Instantiates a new UINT by reference.
         */
        public UINTByReference() {
            this(new UINT(0));
        }

        /**
         * Instantiates a new UINT by reference.
         *
         * @param value the value
         */
        public UINTByReference(UINT value) {
            super(UINT.SIZE);
            setValue(value);
        }

        /**
         * Sets the value.
         *
         * @param value the new value
         */
        public void setValue(UINT value) {
            getPointer().setInt(0, value.intValue());
        }

        /**
         * Gets the value.
         *
         * @return the value
         */
        public UINT getValue() {
            return new UINT(getPointer().getInt(0));
        }
    }

    /**
     * The Class SCODE.
     */
    public static class SCODE extends ULONG {

        /**
         * Instantiates a new SCODE.
         */
        public SCODE() {
            this(0);
        }

        /**
         * Instantiates a new SCODE.
         *
         * @param value
         *            the value
         */
        public SCODE(long value) {
            super(value);
        }
    }

    /**
     * The Class SCODEByReference.
     */
    public static class SCODEByReference extends ByReference {

        /**
         * Instantiates a new SCODE by reference.
         */
        public SCODEByReference() {
            this(new SCODE(0));
        }

        /**
         * Instantiates a new SCODE by reference.
         *
         * @param value the value
         */
        public SCODEByReference(SCODE value) {
            super(SCODE.SIZE);
            setValue(value);
        }

        /**
         * Sets the value.
         *
         * @param value the new value
         */
        public void setValue(SCODE value) {
            getPointer().setInt(0, value.intValue());
        }

        /**
         * Gets the value.
         *
         * @return the value
         */
        public SCODE getValue() {
            return new SCODE(getPointer().getInt(0));
        }
    }

    /**
     * The Class LCID.
     */
    public static class LCID extends DWORD {

        /**
         * Instantiates a new lcid.
         */
        public LCID() {
            super(0);
        }

        /**
         * Instantiates a new lcid.
         *
         * @param value the value
         */
        public LCID(long value) {
            super(value);
        }
    }

    /**
     * The Class BOOL.
     */
    public static class BOOL extends IntegerType implements Comparable<BOOL> {

        /** The Constant SIZE. */
        public static final int SIZE = 4;

        /**
         * Instantiates a new bool.
         */
        public BOOL() {
            this(0);
        }

        /**
         * Instantiates a new bool.
         *
         * @param value the value
         */
        public BOOL(boolean value) {
            this(value ? 1L : 0L);
        }

        /**
         * Instantiates a new bool.
         *
         * @param value the value
         */
        public BOOL(long value) {
            super(SIZE, value, false);
            assert value == 0 || value == 1;
        }

        public boolean booleanValue() {
            if (this.intValue() > 0) {
                return true;
            } else {
                return false;
            }
        }

        @Override
        public String toString() {
            return Boolean.toString(booleanValue());
        }

        @Override
        public int compareTo(BOOL other) {
            return compare(this, other);
        }

        /**
         * Compares 2 BOOL values -  - <B>Note:</B> a {@code null}
         * value is considered <U>greater</U> than any non-{@code null} one
         * (i.e., {@code null} values are &quot;pushed&quot; to the end
         * of a sorted array / list of values)
         *
         * @param v1 The 1st value
         * @param v2 The 2nd value
         * @return 0 if values are equal (including if <U>both</U> are {@code null},
         * negative if 1st value less than 2nd one, positive otherwise. <B>Note:</B>
         * the comparison uses the {@link #booleanValue()}.
         * @see #compare(boolean, boolean)
         */
        public static int compare(BOOL v1, BOOL v2) {
            if (v1 == v2) {
                return 0;
            } else if (v1 == null) {
                return 1;   // v2 cannot be null or v1 == v2 would hold
            } else if (v2 == null) {
                return (-1);
            } else {
                return compare(v1.booleanValue(), v2.booleanValue());
            }
        }

        /**
         * Compares a BOOL value with a {@code long} one. <B>Note:</B> if
         * the BOOL value is {@code null} then it is consider <U>greater</U>
         * than any {@code long} value.
         *
         * @param v1 The {@link BOOL} value
         * @param v2 The {@code boolean} value
         * @return 0 if values are equal, negative if 1st value less than 2nd one,
         * positive otherwise. <B>Note:</B> the comparison uses the {@link #longValue()}.
         * @see #compare(boolean, boolean)
         */
        public static int compare(BOOL v1, boolean v2) {
            if (v1 == null) {
                return 1;
            } else {
                return compare(v1.booleanValue(), v2);
            }
        }

        // TODO when JDK 1.7 becomes the min. version, use Boolean.compare(...)
        public static int compare(boolean v1, boolean v2) {
            if (v1 == v2) {
                return 0;
            } else if (v1) {
                return 1;   // v2 cannot be true or v1 == v2
            } else {
                return (-1);
            }
        }
    }

    /**
     * The Class BOOLByReference.
     */
    public static class BOOLByReference extends ByReference {

        /**
         * Instantiates a new BOOL by reference.
         */
        public BOOLByReference() {
            this(new BOOL(0));
        }

        /**
         * Instantiates a new BOOL by reference.
         *
         * @param value the value
         */
        public BOOLByReference(BOOL value) {
            super(BOOL.SIZE);
            setValue(value);
        }

        /**
         * Sets the value.
         *
         * @param value the new value
         */
        public void setValue(BOOL value) {
            getPointer().setInt(0, value.intValue());
        }

        /**
         * Gets the value.
         *
         * @return the value
         */
        public BOOL getValue() {
            return new BOOL(getPointer().getInt(0));
        }
    }

    /**
     * The Class UCHAR.
     */
    public static class UCHAR extends IntegerType implements Comparable<UCHAR> {

        /** The Constant SIZE. */
        public static final int SIZE = 1;

        /**
         * Instantiates a new uchar.
         */
        public UCHAR() {
            this(0);
        }

        public UCHAR(char ch) {
            this(ch & 0xFF);
        }

        /**
         * Instantiates a new uchar.
         *
         * @param value the value
         */
        public UCHAR(long value) {
            super(SIZE, value, true);
        }

        @Override
        public int compareTo(UCHAR other) {
            return compare(this, other);
        }
    }

    /**
     * The Class BYTE.
     */
    public static class BYTE extends UCHAR {

        /**
         * Instantiates a new byte.
         */
        public BYTE() {
            this(0);
        }

        /**
         * Instantiates a new byte.
         *
         * @param value the value
         */
        public BYTE(long value) {
            super(value);
        }
    }

    /**
     * The Class CHAR.
     */
    public static class CHAR extends IntegerType implements Comparable<CHAR> {

        /** The Constant SIZE. */
        public static final int SIZE = 1;

        /**
         * Instantiates a new char.
         */
        public CHAR() {
            this(0);
        }

        /**
         * Instantiates a new char.
         *
         * @param ch The {@code char} value
         */
        public CHAR(byte ch) {
            this(ch & 0xFF);
        }

        /**
         * Instantiates a new char.
         *
         * @param value the value
         */
        public CHAR(long value) {
            super(1, value, false);
        }

        @Override
        public int compareTo(CHAR other) {
            return compare(this, other);
        }
    }

    /**
     * The Class CHARByReference.
     */
    public static class CHARByReference extends ByReference {

        /**
         * Instantiates a new CHAR by reference.
         */
        public CHARByReference() {
            this(new CHAR(0));
        }

        /**
         * Instantiates a new CHAR by reference.
         *
         * @param value the value
         */
        public CHARByReference(CHAR value) {
            super(CHAR.SIZE);
            setValue(value);
        }

        /**
            * Sets the value.
            *
            * @param value the new value
            */
        public void setValue(CHAR value) {
            getPointer().setByte(0, value.byteValue());
        }

        /**
         * Gets the value.
         *
         * @return the value
         */
        public CHAR getValue() {
            return new CHAR(getPointer().getByte(0));
        }
    }

    /**
     * handle to an OpenGL rendering context
     */
    public static class HGLRC extends HANDLE {

        /**
         * Instantiates a new HGLRC .
         */
        public HGLRC() {

        }

        /**
         * Instantiates a new HGLRC .
         *
         * @param p the p
         */
        public HGLRC(Pointer p) {
            super(p);
        }
    }

    /**
     * handle to an OpenGL rendering context
     */
    public static class HGLRCByReference extends HANDLEByReference {

        /**
         * Instantiates a new pointer to an HGLRC.
         */
        public HGLRCByReference() {

        }

        /**
         * Instantiates a new pointer to an HGLRC.
         *
         * @param h
         *            Initial valure for the HGLRC
         */
        public HGLRCByReference(HGLRC h) {
            super(h);
        }
    }
}
