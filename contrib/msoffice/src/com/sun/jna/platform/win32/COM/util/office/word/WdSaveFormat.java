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

public enum WdSaveFormat implements IComEnum {
	 wdFormatDocument(0),          // Microsoft Office Word 97 - 2003 binary file format.
	 wdFormatDOSText(4),           // Microsoft DOS text format.
	 wdFormatDOSTextLineBreaks(5), // Microsoft DOS text with line breaks preserved.
	 wdFormatEncodedText(7),       // Encoded text format.
	 wdFormatFilteredHTML(10),     // Filtered HTML format.
	 wdFormatFlatXML(19),	       // Open XML file format saved as a single XML file.
	 wdFormatFlatXMLMacroEnabled(20),         // Open XML file format with macros enabled saved as a single XML file.
	 wdFormatFlatXMLTemplate(21),             // Open XML template format saved as a XML single file.
	 wdFormatFlatXMLTemplateMacroEnabled(22), // Open XML template format with macros enabled saved as a single XML file.
	 wdFormatOpenDocumentText(23),       // OpenDocument Text format.
	 wdFormatHTML(8),                    // Standard HTML format.
	 wdFormatRTF(6),                     // Rich text format (RTF).
	 wdFormatStrictOpenXMLDocument(24),  // Strict Open XML document format.
	 wdFormatTemplate(1),                // Word template format.
	 wdFormatText(2),                    //  Microsoft Windows text format.
	 wdFormatTextLineBreaks(3), //Windows text format with line breaks preserved.
	 wdFormatUnicodeText( 7), //Unicode text format.
	 wdFormatWebArchive(9), //Web archive format.
	 wdFormatXML(11), //Extensible Markup Language (XML) format.
	 wdFormatDocument97( 0), // Microsoft Word 97 document format.
	 wdFormatDocumentDefault(16), // Word default document file format. For Word 2010, this is the DOCX format.
	 wdFormatPDF( 17), //PDF format.
	 wdFormatTemplate97( 1), // Word 97 template format.
	 wdFormatXMLDocument( 12), //XML document format.
	 wdFormatXMLDocumentMacroEnabled(13), //XML document format with macros enabled.
	 wdFormatXMLTemplate(14), //XML template format.
	 wdFormatXMLTemplateMacroEnabled(15), //XML template format with macros enabled.
	 wdFormatXPS(18);
	 
	 private WdSaveFormat(long value) {
		 this.value = value;
	 }
	 private long value;
	 public long getValue() {
		 return this.value;
	 }
}
