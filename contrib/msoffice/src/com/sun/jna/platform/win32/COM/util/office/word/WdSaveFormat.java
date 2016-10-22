/* Copyright (c) 2014 Dr David H. Akehurst (itemis), All Rights Reserved
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
