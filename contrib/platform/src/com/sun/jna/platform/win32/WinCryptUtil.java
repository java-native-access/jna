/* Copyright (c) 2018 Roshan Muralidharan, All Rights Reserved
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

import com.sun.jna.platform.win32.WTypes.LPSTR;
import com.sun.jna.platform.win32.WinCrypt.*;
import com.sun.jna.platform.win32.WinDef.DWORD;
import com.sun.jna.TypeMapper;
import com.sun.jna.StringArray;
import com.sun.jna.win32.W32APITypeMapper;

/**
 * Utility classes and methods for WinCrypt
 */
public class WinCryptUtil {
	
	/**
	 * The CTL_USAGE structure contains an array of object identifiers (OIDs) for
	 * Certificate Trust List (CTL) extensions.
     * 
     * <p>
     * MANAGED_CTL_USAGE is a convenience binding, that makes dealing with
     * CTL_USAGE easier by providing direct, bound access, to the contained
     * LPSTRs.
     * </p>
     */
	public static class MANAGED_CTL_USAGE extends CTL_USAGE {

		private final String[] usageIdentifiers;

		/**
         * Create a new CTL_USAGE with initial data.
         * @param usage Object Identifier of CTL_EXTENSION.
         */
		public MANAGED_CTL_USAGE(String usage) {
			super(W32APITypeMapper.ASCII);

			usageIdentifiers = new String[] { usage };
			StringArray sArray = new StringArray(new String[] { usage });
			rgpszUsageIdentifier = sArray.getPointer(0);
			cUsageIdentifier = usageIdentifiers.length;
		}

		/**
         * Create a new CTL_USAGE with an array of identifiers.
         * @param usages Object Identifiers of CTL_EXTENSIONs.
         */
		public MANAGED_CTL_USAGE(String[] usages) {
			super(W32APITypeMapper.ASCII);

			usageIdentifiers = usages;
			StringArray sArray = new StringArray(usages);
			rgpszUsageIdentifier = sArray.getPointer(0);
			cUsageIdentifier = usageIdentifiers.length;
		}

		/**
         * Retrieve an identifier at the provided index.
         * @param idx index of the object identifier to be retrieved.
         */
		public String getUsageIdentifier(int idx) {
			return usageIdentifiers[idx];
		}
	}
}
