/* Copyright (c) 2010 Daniel Doubrovkine, All Rights Reserved
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package com.sun.jna.platform.win32;

import java.awt.Rectangle;
import java.util.Arrays;
import java.util.List;

import com.sun.jna.IntegerType;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.platform.win32.BaseTSD.LONG_PTR;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.ptr.ByReference;
import com.sun.jna.win32.StdCallLibrary;

/**
 * Ported from Windef.h (various macros and types). Microsoft Windows SDK 6.0A.
 *
 * @author dblock[at]dblock.org
 */
@SuppressWarnings("serial")
public interface WinDef extends StdCallLibrary {

	/** The max path. */
	int MAX_PATH = 260;

	/**
	 * 16-bit unsigned integer.
	 */
	public static class WORD extends IntegerType {
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
	}

	public class WORDByReference extends ByReference {
		public WORDByReference() {
			this(new WORD(0));
		}

		public WORDByReference(WORD value) {
			super(WORD.SIZE);
			setValue(value);
		}

		public void setValue(WORD value) {
			getPointer().setShort(0, value.shortValue());
		}

		public WORD getValue() {
			return new WORD(getPointer().getShort(0));
		}
	}

	/**
	 * 32-bit unsigned integer.
	 */
	public static class DWORD extends IntegerType {
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
			return new WORD(longValue() & 0xFF);
		}

		/**
		 * High WORD.
		 *
		 * @return High WORD.
		 */
		public WORD getHigh() {
			return new WORD((longValue() >> 16) & 0xFF);
		}
	}

	public class DWORDByReference extends ByReference {
		public DWORDByReference() {
			this(new DWORD(0));
		}

		public DWORDByReference(DWORD value) {
			super(DWORD.SIZE);
			setValue(value);
		}

		public void setValue(DWORD value) {
			getPointer().setInt(0, value.intValue());
		}

		public DWORD getValue() {
			return new DWORD(getPointer().getInt(0));
		}
	}

	public static class LONG extends IntegerType {
		public static final int SIZE = 4;

		public LONG() {
			this(0);
		}

		public LONG(int value) {
			super(SIZE, value);
		}
	}

	public class LONGByReference extends ByReference {
		public LONGByReference() {
			this(new LONG(0));
		}

		public LONGByReference(LONG value) {
			super(LONG.SIZE);
			setValue(value);
		}

		public void setValue(LONG value) {
			getPointer().setInt(0, value.intValue());
		}

		public LONG getValue() {
			return new LONG(getPointer().getInt(0));
		}
	}

	public static class LONGLONG extends IntegerType {
		public static final int SIZE = 8;

		public LONGLONG() {
			this(0);
		}

		public LONGLONG(long value) {
			super(8, value, false);
		}
	}

	public class LONGLONGByReference extends ByReference {
		public LONGLONGByReference() {
			this(new LONGLONG(0));
		}

		public LONGLONGByReference(LONGLONG value) {
			super(LONGLONG.SIZE);
			setValue(value);
		}

		public void setValue(LONGLONG value) {
			getPointer().setLong(0, value.longValue());
		}

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
			super(Pointer.SIZE);
		}

		/**
		 * Instantiates a new int ptr.
		 *
		 * @param value
		 *            the value
		 */
		public INT_PTR(long value) {
			super(Pointer.SIZE, value);
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
			super(Pointer.SIZE);
		}

		/**
		 * Instantiates a new uint ptr.
		 *
		 * @param value
		 *            the value
		 */
		public UINT_PTR(long value) {
			super(Pointer.SIZE, value, true);
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
	public class RECT extends Structure {

		/** The left. */
		public int left;

		/** The top. */
		public int top;

		/** The right. */
		public int right;

		/** The bottom. */
		public int bottom;

		/*
		 * (non-Javadoc)
		 *
		 * @see com.sun.jna.Structure#getFieldOrder()
		 */
		protected List getFieldOrder() {
			return Arrays.asList(new String[] { "left", "top", "right",
					"bottom" });
		}

		/**
		 * To rectangle.
		 *
		 * @return the rectangle
		 */
		public Rectangle toRectangle() {
			return new Rectangle(left, top, right - left, bottom - top);
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see com.sun.jna.Structure#toString()
		 */
		public String toString() {
			return "[(" + left + "," + top + ")(" + right + "," + bottom + ")]";
		}
	}

	/**
	 * 32-bit unsigned integer.
	 */
	public static class ULONG extends IntegerType {
		public static final int SIZE = 4;

		/**
		 * Instantiates a new ulong.
		 */
		public ULONG() {
			this(0);
		}

		/**
		 * Instantiates a new ulong.
		 *
		 * @param value
		 *            the value
		 */
		public ULONG(int value) {
			super(SIZE, value, true);
		}
	}

	public class ULONGByReference extends ByReference {
		public ULONGByReference() {
			this(new ULONG(0));
		}

		public ULONGByReference(ULONG value) {
			super(ULONG.SIZE);
			setValue(value);
		}

		public void setValue(ULONG value) {
			getPointer().setInt(0, value.intValue());
		}

		public ULONG getValue() {
			return new ULONG(getPointer().getInt(0));
		}
	}

	public static class ULONGLONG extends IntegerType {
		public static final int SIZE = 8;

		public ULONGLONG() {
			this(0);
		}

		public ULONGLONG(long value) {
			super(SIZE, value, true);
		}
	}

	public class ULONGLONGByReference extends ByReference {
		public ULONGLONGByReference() {
			this(new ULONGLONG(0));
		}

		public ULONGLONGByReference(ULONGLONG value) {
			super(ULONGLONG.SIZE);
			setValue(value);
		}

		public void setValue(ULONGLONG value) {
			getPointer().setLong(0, value.longValue());
		}

		public ULONGLONG getValue() {
			return new ULONGLONG(getPointer().getLong(0));
		}
	}

	/**
	 * 64-bit unsigned integer.
	 */
	public static class DWORDLONG extends IntegerType {
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
	public static class PVOID extends HANDLE {

		/**
		 * Instantiates a new pvoid.
		 */
		public PVOID() {

		}

		/**
		 * Instantiates a new pvoid.
		 *
		 * @param p
		 *            the p
		 */
		public PVOID(Pointer p) {
			super(p);
		}
	}

	/**
	 * Message parameter.
	 */
	public static class LPVOID extends LONG_PTR {

		/**
		 * Instantiates a new lpvoid.
		 */
		public LPVOID() {
			this(0);
		}

		/**
		 * Instantiates a new lpvoid.
		 *
		 * @param value
		 *            the value
		 */
		public LPVOID(long value) {
			super(value);
		}
	}

	/**
	 * The Class POINT.
	 */
	public class POINT extends Structure {

		/**
		 * The Class ByReference.
		 */
		public static class ByReference extends POINT implements
				Structure.ByReference {
		}

		/**
		 * Instantiates a new point.
		 */
		public POINT() {
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

		/** The y. */
		public int x, y;

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

		/*
		 * (non-Javadoc)
		 *
		 * @see com.sun.jna.Structure#getFieldOrder()
		 */
		protected List getFieldOrder() {
			return Arrays.asList(new String[] { "x", "y" });
		}
	}

	/**
	 * 16-bit unsigned short.
	 */
	public static class USHORT extends IntegerType {
		public static final int SIZE = 2;

		/**
		 * Instantiates a new ushort.
		 */
		public USHORT() {
			this(0);
		}

		/**
		 * Instantiates a new ushort.
		 *
		 * @param value
		 *            the value
		 */
		public USHORT(long value) {
			super(2, value, true);
		}
	}

	public class USHORTByReference extends ByReference {
		public USHORTByReference() {
			this(new USHORT(0));
		}

		public USHORTByReference(USHORT value) {
			super(USHORT.SIZE);
			setValue(value);
		}

		public void setValue(USHORT value) {
			getPointer().setShort(0, value.shortValue());
		}

		public USHORT getValue() {
			return new USHORT(getPointer().getShort(0));
		}
	}

	/**
	 * 16-bit short.
	 */
	public static class SHORT extends IntegerType {
		public static final int SIZE = 2;

		/**
		 * Instantiates a new ushort.
		 */
		public SHORT() {
			this(0);
		}

		/**
		 * Instantiates a new ushort.
		 *
		 * @param value
		 *            the value
		 */
		public SHORT(long value) {
			super(SIZE, value, false);
		}
	}

	/**
	 * 32-bit unsigned int.
	 */
	public static class UINT extends IntegerType {
		public static final int SIZE = 4;

		/**
		 * Instantiates a new uint.
		 */
		public UINT() {
			this(0);
		}

		/**
		 * Instantiates a new uint.
		 *
		 * @param value
		 *            the value
		 */
		public UINT(long value) {
			super(SIZE, value, true);
		}
	}

	public class UINTByReference extends ByReference {
		public UINTByReference() {
			this(new UINT(0));
		}

		public UINTByReference(UINT value) {
			super(UINT.SIZE);
			setValue(value);
		}

		public void setValue(UINT value) {
			getPointer().setInt(0, value.intValue());
		}

		public UINT getValue() {
			return new UINT(getPointer().getInt(0));
		}
	}

	/**
	 * The Class SCODE.
	 */
	public static class SCODE extends ULONG {

		/**
		 * Instantiates a new scode.
		 */
		public SCODE() {
			this(0);
		}

		/**
		 * Instantiates a new scode.
		 *
		 * @param value
		 *            the value
		 */
		public SCODE(int value) {
			super(value);
		}
	}

	public static class SCODEByReference extends ByReference {
		public SCODEByReference() {
			this(new SCODE(0));
		}

		public SCODEByReference(SCODE value) {
			super(SCODE.SIZE);
			setValue(value);
		}

		public void setValue(SCODE value) {
			getPointer().setInt(0, value.intValue());
		}

		public SCODE getValue() {
			return new SCODE(getPointer().getInt(0));
		}
	}

	public static class LCID extends DWORD {

		public LCID() {
			super(0);
		}

		public LCID(long value) {
			super(value);
		}
	}

	public static class BOOL extends IntegerType {
		public static final int SIZE = 4;

		public BOOL() {
			super(0);
		}

		public BOOL(long value) {
			super(SIZE, value, false);
		}
	}

	public static class BOOLByReference extends ByReference {
		public BOOLByReference() {
			this(new BOOL(0));
		}

		public BOOLByReference(BOOL value) {
			super(BOOL.SIZE);
			setValue(value);
		}

		public void setValue(BOOL value) {
			getPointer().setInt(0, value.intValue());
		}

		public BOOL getValue() {
			return new BOOL(getPointer().getInt(0));
		}
	}

	public static class UCHAR extends IntegerType {
		public static final int SIZE = 1;

		public UCHAR() {
			this(0);
		}

		public UCHAR(long value) {
			super(SIZE, value, true);
		}
	}

	public static class BYTE extends UCHAR {

		public BYTE() {
			this(0);
		}

		public BYTE(long value) {
			super(value);
		}
	}

	public static class CHAR extends IntegerType {
		public static final int SIZE = 1;

		public CHAR() {
			this(0);
		}

		public CHAR(long value) {
			super(1, value, false);
		}
	}

	public static class CHARByReference extends ByReference {
		public CHARByReference() {
			this(new CHAR(0));
		}

		public CHARByReference(CHAR value) {
			super(CHAR.SIZE);
			setValue(value);
		}

		public void setValue(CHAR value) {
			getPointer().setByte(0, value.byteValue());
		}

		public CHAR getValue() {
			return new CHAR(getPointer().getChar(0));
		}
	}
}
