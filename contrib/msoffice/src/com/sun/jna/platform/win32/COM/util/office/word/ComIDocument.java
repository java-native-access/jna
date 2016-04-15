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

import com.sun.jna.platform.win32.COM.util.annotation.ComMethod;
import com.sun.jna.platform.win32.COM.util.annotation.ComProperty;

public interface ComIDocument {

	@ComMethod
	void SaveAs(String string, WdSaveFormat wdFormatDocument);

	@ComMethod
	void Close(boolean saveChanges);
        
        /**
         * <p>id(0x451)</p>
         */
        @ComMethod(name = "Close", dispId = 0x451)
        void Close(Object SaveChanges,
                Object OriginalFormat,
                Object RouteDocument);
        
        /**
         * <p>
         * id(0x29)</p>
         */
        @ComProperty(name = "Content", dispId = 0x29)
        Range getContent();
        
        /**
         * <p>id(0x4)</p>
         */
        @ComProperty(name = "Bookmarks", dispId = 0x4)
        Bookmarks getBookmarks();

        /**
         * <p>
         * id(0x6)</p>
         */
        @ComProperty(name = "Tables", dispId = 0x6)
        Tables getTables();
        
        /**
         * <p>id(0x228)</p>
         */
        @ComMethod(name = "ExportAsFixedFormat", dispId = 0x228)
        void ExportAsFixedFormat(String OutputFileName,
                WdExportFormat ExportFormat,
                Boolean OpenAfterExport,
                WdExportOptimizeFor OptimizeFor,
                WdExportRange Range,
                Integer From,
                Integer To,
                WdExportItem Item,
                Boolean IncludeDocProps,
                Boolean KeepIRM,
                WdExportCreateBookmarks CreateBookmarks,
                Boolean DocStructureTags,
                Boolean BitmapMissingFonts,
                Boolean UseISO19005_1,
                Object FixedFormatExtClassPtr);
}
