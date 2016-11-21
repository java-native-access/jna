/*
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

package com.sun.jna.platform.win32.COM.util.office;

import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.COM.Helper;
import com.sun.jna.platform.win32.COM.util.Factory;
import com.sun.jna.platform.win32.COM.util.IDispatch;
import com.sun.jna.platform.win32.COM.util.office.office.XlChartType;
import com.sun.jna.platform.win32.COM.util.office.word.ComIApplication;
import com.sun.jna.platform.win32.COM.util.office.word.ComIDocument;
import com.sun.jna.platform.win32.COM.util.office.word.ComWord_Application;
import com.sun.jna.platform.win32.COM.util.office.word.InlineShape;
import com.sun.jna.platform.win32.COM.util.office.word.Paragraph;
import com.sun.jna.platform.win32.COM.util.office.word.Range;
import com.sun.jna.platform.win32.COM.util.office.word.Table;
import com.sun.jna.platform.win32.COM.util.office.word.WdBreakType;
import com.sun.jna.platform.win32.COM.util.office.word.WdCollapseDirection;
import com.sun.jna.platform.win32.COM.util.office.word.WdExportCreateBookmarks;
import com.sun.jna.platform.win32.COM.util.office.word.WdExportFormat;
import com.sun.jna.platform.win32.COM.util.office.word.WdExportItem;
import com.sun.jna.platform.win32.COM.util.office.word.WdExportOptimizeFor;
import com.sun.jna.platform.win32.COM.util.office.word.WdExportRange;
import com.sun.jna.platform.win32.COM.util.office.word.WdInformation;
import com.sun.jna.platform.win32.COM.util.office.word.WdSaveOptions;
import com.sun.jna.platform.win32.Ole32;
import static com.sun.jna.platform.win32.Variant.VARIANT.VARIANT_MISSING;
import java.io.File;


import java.io.IOException;


/**
 * Based on VB sample: https://support.microsoft.com/de-de/kb/313193
 * 
 * <p>This version of the sample runs without a visible word instance and in the
 * end shuts down word. The process creates a PDF document, that is written to a
 * temporary file, which name is printed.</p>
 * 
 * <p>Please note: The contained type-bindings are far from complete and only
 * included as sample - please use one of the generators to generate complete
 * bindings or enhance the coverage yourself.</p>
 */
public class Wordautomation_KB_313193_Mod {
    public static void main(String[] args) throws IOException {
        // Initialize COM Subsystem
        Ole32.INSTANCE.CoInitializeEx(Pointer.NULL, Ole32.COINIT_MULTITHREADED);
        // Initialize Factory for COM object creation
        Factory fact = new Factory();
        
        try {
            // oEndOfDoc is a predefined bookmark
            final String oEndOfDoc = "\\endofdoc"; /* \endofdoc is a predefined bookmark */ 

            // Start word application
            ComWord_Application word = fact.createObject(ComWord_Application.class);
            ComIApplication wordApp = word.queryInterface(ComIApplication.class);

            // Make word visible/invisible (invisible is default)
            wordApp.setVisible(true);
            
            // Create an empty document (signiture of depends on bindings)
            ComIDocument doc = wordApp.getDocuments().Add();

            Helper.sleep(5);
            
            //Insert a paragraph at the beginning of the document.
            Paragraph para1 = doc.getContent().getParagraphs().Add(VARIANT_MISSING);
            para1.getRange().setText("Heading 1");
            para1.getRange().getFont().setBold(1);
            //24 pt spacing after paragraph.
            para1.getFormat().setSpaceAfter(24F);
            para1.getRange().InsertParagraphAfter();

            //Insert a paragraph at the end of the document.
            Paragraph para2 = doc.getContent().getParagraphs().Add(doc.getBookmarks().Item(oEndOfDoc).getRange());
            para2.getRange().setText("Heading 2");
            para2.getFormat().setSpaceAfter(6F);
            para2.getRange().InsertParagraphAfter();

            //Insert another paragraph.
            Paragraph para3 = doc.getContent().getParagraphs().Add(doc.getBookmarks().Item(oEndOfDoc).getRange());
            para3.getRange().setText("This is a sentence of normal text. Now here is a table:");
            para3.getRange().getFont().setBold(0);
            para3.getFormat().setSpaceAfter(24F);
            para3.getRange().InsertParagraphAfter();

            //Insert a 3 x 5 table, fill it with data, and make the first row
            //bold and italic.
            Table table = doc.getTables().Add(doc.getBookmarks().Item(oEndOfDoc).getRange(),
                    3, 5, VARIANT_MISSING, VARIANT_MISSING);
            table.getRange().getParagraphFormat().setSpaceAfter(6F);
            for(int r = 1; r <= 3; r++) {
                    for(int c = 1; c <= 5; c++) {
                            String strText = "r" + r + "c" + c;
                            table.Cell(r, c).getRange().setText(strText);
                    }
            }
            table.getRows().Item(1).getRange().getFont().setBold(1);
            table.getRows().Item(1).getRange().getFont().setItalic(1);

            //Add some text after the table.
            Paragraph para4 = doc.getContent().getParagraphs().Add(doc.getBookmarks().Item(oEndOfDoc).getRange());
            para4.getRange().InsertParagraphBefore();
            para4.getRange().setText("And here's another table:");
            para4.getFormat().setSpaceAfter(24F);
            para4.getRange().InsertParagraphAfter();

            //Insert a 5 x 2 table, fill it with data, and change the column widths.
            table = doc.getTables().Add(doc.getBookmarks().Item(oEndOfDoc).getRange(), 5, 2, VARIANT_MISSING, VARIANT_MISSING);
            table.getRange().getParagraphFormat().setSpaceAfter(6F);

            for(int r = 1; r <= 5; r++) {
                    for(int c = 1; c <= 2; c++) {
                            String strText = "r" + r + "c" + c;
                            table.Cell(r, c).getRange().setText(strText);
                    }
            }

            //Change width of columns 1 & 2
            table.getColumns().Item(1).setWidth(wordApp.InchesToPoints(2F));
            table.getColumns().Item(2).setWidth(wordApp.InchesToPoints(3F));

            //Keep inserting text. When you get to 7 inches from top of the
            //document, insert a hard page break.
            Range wrdRng;
            float dPos = wordApp.InchesToPoints(7F);
            doc.getBookmarks().Item(oEndOfDoc).getRange().InsertParagraphAfter();
            do {
                    wrdRng = doc.getBookmarks().Item(oEndOfDoc).getRange();
                    wrdRng.getParagraphFormat().setSpaceAfter(6F);
                    wrdRng.InsertAfter("A line of text");
                    wrdRng.InsertParagraphAfter();
            } while(dPos >= (Float) wrdRng.getInformation(WdInformation.wdVerticalPositionRelativeToPage));

            wrdRng.Collapse(WdCollapseDirection.wdCollapseEnd);
            wrdRng.InsertBreak(WdBreakType.wdPageBreak);
            wrdRng.Collapse(WdCollapseDirection.wdCollapseEnd);
            wrdRng.InsertAfter("We're now on page 2. Here's my chart:");
            wrdRng.InsertParagraphAfter();

            //Insert a chart and change the chart.
            InlineShape oShape = doc.getBookmarks().Item(oEndOfDoc).getRange()
                    .getInlineShapes().AddOLEObject(
                            "MSGraph.Chart.8", "",
                            Boolean.FALSE, Boolean.FALSE, VARIANT_MISSING,
                            VARIANT_MISSING, VARIANT_MISSING, VARIANT_MISSING);

            //Demonstrate use of late bound oChart and oChartApp objects to
            //manipulate the chart object with MSGraph.
            IDispatch oChart = oShape.getOLEFormat().getObject();
            IDispatch oChartApp = oChart.getProperty(IDispatch.class, "Application");

            //Change the chart type to Line
            oChart.setProperty("ChartType", XlChartType.xlLine.getValue());

            //Update the chart image and quit MSGraph.
            oChartApp.invokeMethod(Void.class, "Update");
            oChartApp.invokeMethod(Void.class, "Quit");

            //... If desired, you can proceed from here using the Microsoft Graph 
            //Object model on the oChart and oChartApp objects to make additional
            //changes to the chart.

            //Set the width of the chart.
            oShape.setWidth(wordApp.InchesToPoints(6.25f));
            oShape.setHeight(wordApp.InchesToPoints(3.57f));

            //Add text after the chart.
            wrdRng = doc.getBookmarks().Item(oEndOfDoc).getRange();
            wrdRng.InsertParagraphAfter();
            wrdRng.InsertAfter("THE END.");

            File tempFile = Helper.createNotExistingFile("KB_313193_", ".pdf");

            doc.ExportAsFixedFormat(
                    tempFile.getAbsolutePath(),
                    WdExportFormat.wdExportFormatPDF,
                    Boolean.FALSE, 
                    WdExportOptimizeFor.wdExportOptimizeForOnScreen, 
                    WdExportRange.wdExportAllDocument, 
                    null, 
                    null, 
                    WdExportItem.wdExportDocumentContent,
                    Boolean.FALSE,
                    Boolean.TRUE, 
                    WdExportCreateBookmarks.wdExportCreateNoBookmarks,
                    Boolean.TRUE,
                    Boolean.FALSE, 
                    Boolean.TRUE,
                    VARIANT_MISSING);

            System.out.println("Output written to: " + tempFile.getAbsolutePath());

            doc.Close(WdSaveOptions.wdDoNotSaveChanges, VARIANT_MISSING, VARIANT_MISSING);
            
            wordApp.Quit();
        } finally {
            fact.disposeAll();
            Ole32.INSTANCE.CoUninitialize();
        }
    }
    
}
