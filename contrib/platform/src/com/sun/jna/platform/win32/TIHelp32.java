/* Copyright (c) 2015 Michael Freeman, All Rights Reserved
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

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.platform.win32.WinDef.DWORD;
import com.sun.jna.platform.win32.WinDef.HMODULE;

/**
 * TIHelp32.h interface
 * 
 * @author mlfreeman[at]gmail.com
 */
public interface TIHelp32 {

	/**
	 * Describes an entry from a list of the modules belonging to the specified
	 * process.
	 * 
	 * @see https://msdn.microsoft.com/en-us/library/windows/desktop/ms684225(v=
	 *      vs.85).aspx
	 */
	public class MODULEENTRY32 extends Structure {

		/**
		 * A representation of a MODULEENTRY32 structure as a reference
		 */
		public static class ByReference extends MODULEENTRY32 implements Structure.ByReference {
			public ByReference() {
			}

			public ByReference(Pointer memory) {
				super(memory);
			}
		}

		public MODULEENTRY32() {
			dwSize = new WinDef.DWORD(size());
		}

		public MODULEENTRY32(Pointer memory) {
			super(memory);
			read();
		}

		/**
		 * The size of the structure, in bytes. Before calling the Module32First
		 * function, set this member to sizeof(MODULEENTRY32). If you do not
		 * initialize dwSize, Module32First fails.
		 */
		public DWORD dwSize;

		/**
		 * This member is no longer used, and is always set to one.
		 */
		public DWORD th32ModuleID;

		/**
		 * The identifier of the process whose modules are to be examined.
		 */
		public DWORD th32ProcessID;

		/**
		 * The load count of the module, which is not generally meaningful, and
		 * usually equal to 0xFFFF.
		 */
		public DWORD GlblcntUsage;

		/**
		 * The load count of the module (same as GlblcntUsage), which is not
		 * generally meaningful, and usually equal to 0xFFFF.
		 */
		public DWORD ProccntUsage;

		/**
		 * The base address of the module in the context of the owning process.
		 */
		public Pointer modBaseAddr;

		/**
		 * The size of the module, in bytes.
		 */
		public DWORD modBaseSize;

		/**
		 * A handle to the module in the context of the owning process.
		 */
		public HMODULE hModule;

		/**
		 * The module name.
		 */
		public char[] szModule = new char[256]; // MAX_MODULE_NAME32 (255) + 1

		/**
		 * The module path.
		 */
		public char[] szExePath = new char[Kernel32.MAX_PATH];

		/**
		 * @return The module name.
		 */
		public String szModule() {
			return Native.toString(this.szModule);
		}

		/**
		 * @return The module path.
		 */
		public String szExePath() {
			return Native.toString(this.szExePath);
		}

		@Override
		protected List<String> getFieldOrder() {
			return Arrays.asList(new String[] { "dwSize", "th32ModuleID", "th32ProcessID", "GlblcntUsage",
					"ProccntUsage", "modBaseAddr", "modBaseSize", "hModule", "szModule", "szExePath" });
		}
	}

}
