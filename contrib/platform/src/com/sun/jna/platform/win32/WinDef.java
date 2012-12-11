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
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.platform.win32.BaseTSD.LONG_PTR;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.win32.StdCallLibrary;

// TODO: Auto-generated Javadoc
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
		
		/**
		 * Instantiates a new word.
		 */
		public WORD() {
			this(0);
		}

		/**
		 * Instantiates a new word.
		 *
		 * @param value the value
		 */
		public WORD(long value) {
			super(2, value, true);
		}
	}

	/**
	 * 32-bit unsigned integer.
	 */
	public static class DWORD extends IntegerType {
		
		/**
		 * Instantiates a new dword.
		 */
		public DWORD() {
			this(0);
		}

		/**
		 * Instantiates a new dword.
		 *
		 * @param value the value
		 */
		public DWORD(long value) {
			super(4, value, true);
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

	/**
	 * 32-bit signed integer.
	 */
	public static class LONG extends IntegerType {
		
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
			super(Native.LONG_SIZE, value);
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
		 * @param p the p
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
		 * @param p the p
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
		 * @param p the p
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
		 * @param p the p
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
		 * @param p the p
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
		 * @param p the p
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
		 * @param p the p
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
		 * @param p the p
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
		 * @param p the p
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
		 * @param p the p
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
		 * @param p the p
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
		 * @param value the value
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
		 * @param value the value
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
		 * @param value the value
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
		 * @param value the value
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
		 * @param value the value
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

		/* (non-Javadoc)
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

		/* (non-Javadoc)
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
		
		/**
		 * Instantiates a new ulong.
		 */
		public ULONG() {
			this(0);
		}

		/**
		 * Instantiates a new ulong.
		 *
		 * @param value the value
		 */
		public ULONG(long value) {
			super(Native.LONG_SIZE, value, true);
		}

		/**
		 * The Class ByReference.
		 */
		public static class ByReference implements Structure.ByReference {

		}
	}

	/**
	 * 64-bit unsigned integer.
	 */
	public static class ULONGLONG extends IntegerType {
		
		/**
		 * Instantiates a new ulonglong.
		 */
		public ULONGLONG() {
			this(0);
		}

		/**
		 * Instantiates a new ulonglong.
		 *
		 * @param value the value
		 */
		public ULONGLONG(long value) {
			super(8, value, true);
		}
	}

	/**
	 * 64-bit unsigned integer.
	 */
	public static class DWORDLONG extends IntegerType {
		
		/**
		 * Instantiates a new dwordlong.
		 */
		public DWORDLONG() {
			this(0);
		}

		/**
		 * Instantiates a new dwordlong.
		 *
		 * @param value the value
		 */
		public DWORDLONG(long value) {
			super(8, value, true);
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
		 * @param p the p
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
		 * @param value the value
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
		 * @param p the p
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
		 * @param value the value
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
		 * @param memory the memory
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
		 * @param x the x
		 * @param y the y
		 */
		public POINT(int x, int y) {
			this.x = x;
			this.y = y;
		}

		/* (non-Javadoc)
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
		
		/**
		 * Instantiates a new ushort.
		 */
		public USHORT() {
			this(0);
		}

		/**
		 * Instantiates a new ushort.
		 *
		 * @param value the value
		 */
		public USHORT(long value) {
			super(2, value, true);
		}
	}

	/**
	 * 32-bit unsigned short.
	 */
	public static class UINT extends IntegerType {
		
		/**
		 * Instantiates a new uint.
		 */
		public UINT() {
			this(0);
		}

		/**
		 * Instantiates a new uint.
		 *
		 * @param value the value
		 */
		public UINT(long value) {
			super(4, value, true);
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
		 * @param value the value
		 */
		public SCODE(long value) {
			super(value);
		}

		/**
		 * The Class ByReference.
		 */
		public static class ByReference implements Structure.ByReference {

		}
	}

}
