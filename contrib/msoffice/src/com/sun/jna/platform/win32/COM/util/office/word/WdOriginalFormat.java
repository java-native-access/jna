/* Copyright (c) 2014 Dr David H. Akehurst (itemis), All Rights Reserved
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
package com.sun.jna.platform.win32.COM.util.office.word;

import com.sun.jna.platform.win32.COM.util.IComEnum;

public enum WdOriginalFormat implements IComEnum {
	wdOriginalDocumentFormat(1),  // Original document format.
	wdPromptUser(2),              // Prompt user to select a document format.
	wdWordDocument(0);            // Microsoft Word document format.
	
	 private WdOriginalFormat(long value) {
		 this.value = value;
	 }
	 private long value;
	 public long getValue() {
		 return this.value;
	 }
}
