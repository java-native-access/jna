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
import com.sun.jna.platform.win32.COM.util.office.excel.Borders;
import com.sun.jna.platform.win32.COM.util.office.excel.Chart;
import com.sun.jna.platform.win32.COM.util.office.excel.ComExcel_Application;
import com.sun.jna.platform.win32.COM.util.office.excel.ComIApplication;
import com.sun.jna.platform.win32.COM.util.office.excel.ComIRange;
import com.sun.jna.platform.win32.COM.util.office.excel.ComIWorkbook;
import com.sun.jna.platform.win32.COM.util.office.excel.ComIWorksheet;
import com.sun.jna.platform.win32.COM.util.office.excel.Shape;
import com.sun.jna.platform.win32.COM.util.office.excel.XlBorderWeight;
import com.sun.jna.platform.win32.COM.util.office.excel.XlChartLocation;
import com.sun.jna.platform.win32.COM.util.office.excel.XlLineStyle;
import com.sun.jna.platform.win32.COM.util.office.excel.XlRowCol;
import com.sun.jna.platform.win32.COM.util.office.office.XlChartType;
import com.sun.jna.platform.win32.OaIdl;
import com.sun.jna.platform.win32.OaIdl.SAFEARRAY;
import com.sun.jna.platform.win32.Ole32;
import com.sun.jna.platform.win32.Variant;
import com.sun.jna.platform.win32.Variant.VARIANT;
import static com.sun.jna.platform.win32.Variant.VARIANT.VARIANT_MISSING;
import com.sun.jna.platform.win32.WTypes.BSTR;
import com.sun.jna.platform.win32.WinDef.LCID;
import java.io.File;

import java.io.IOException;
import javax.swing.JDialog;

import javax.swing.JOptionPane;

/**
 * Based on VB sample: https://support.microsoft.com/en-us/kb/219151
 * 
 * <p>Please note: The contained type-bindings are far from complete and only
 * included as sample - please use one of the generators to generate complete
 * bindings or enhance the coverage yourself.</p>
 */
public class Excelautomation_KB_219151_Mod {

    public static void main(String[] args) throws IOException {
        // Initialize COM Subsystem
        Ole32.INSTANCE.CoInitializeEx(Pointer.NULL, Ole32.COINIT_MULTITHREADED);
        // Initialize Factory for COM object creation
        Factory fact = new Factory();
        // Set LCID for calls to english locale. Without this formulas need
        // to be specified in the users locale.
        fact.setLCID(new LCID(0x0409));
        
        try {
            // Start excel application
            ComExcel_Application excel = fact.createObject(ComExcel_Application.class);
            ComIApplication excelApp = excel.queryInterface(ComIApplication.class);
            
            // Set visiblite of application
            excelApp.setVisible(true);
            
            Helper.sleep(5);
            
            // Get a new workbook.
            ComIWorkbook wb = excelApp.getWorkbooks().Add();
            ComIWorksheet sheet = wb.getActiveSheet();
            
            // Add table headers going cell by cell.
            sheet.getCells().getItem(1, 1).setValue("First Name");
            sheet.getCells().getItem(1, 2).setValue("Last Name");
            sheet.getCells().getItem(1, 3).setValue("Full Name");
            sheet.getCells().getItem(1, 4).setValue("Salary");
            
            // Create an array to set multiple values at once.
            SAFEARRAY saNames = safeVariantArrayFromJava(new String[][] {
                {"John", "Smith"},
                {"Tom", "Brown"},
                {"Sue", "Thomas"},
                {"Jane", "Jones"},
                {"Adam", "Johnson"},
            });
           
            // Fill A2:B6 with an array of values (First and Last Names).
            VARIANT valueHolder = new VARIANT();
            valueHolder.setValue(Variant.VT_ARRAY | Variant.VT_VARIANT, saNames);
            sheet.getRange("A2", "B6").setValue(valueHolder);
            saNames.destroy();
            
            // Fill C2:C6 with a relative formula (=A2 & " " & B2).
            sheet.getRange("C2", "C6").setFormula("= A2 & \" \" & B2");
            
            // Fill D2:D6 with a formula(=RAND()*100000) and apply format.
            sheet.getRange("D2", "D6").setFormula("=RAND()*100000");
            sheet.getRange("D2", "D6").setNumberFormat("$0.00");

            // AutoFit columns A:D.
            sheet.getRange("A1", "D2").getEntireColumn().AutoFit();

            displayQuaterlySales(sheet);

            File tempFile = Helper.createNotExistingFile("exceloutput", ".xlsx");
            System.out.println("Writing output to: " + tempFile.getAbsolutePath());
            wb.SaveAs(tempFile.getAbsolutePath());

            excelApp.setUserControl(true);
        } finally {
            fact.disposeAll();
            Ole32.INSTANCE.CoUninitialize();
        }
        
        System.exit(0);
    }
    
    private static void displayQuaterlySales(ComIWorksheet sheet) {
        // Determine how many quarters to display data for.
        int iNumQtrs = 4;
        for(; iNumQtrs >= 2; iNumQtrs--) {
            JOptionPane pane = new JOptionPane(
                    String.format("Enter sales data for %d quarter(s)?", iNumQtrs),
                    JOptionPane.QUESTION_MESSAGE);
            pane.setOptionType(JOptionPane.YES_NO_OPTION);
            JDialog dialog = pane.createDialog("Input...");
            dialog.setAlwaysOnTop(true);
            dialog.show();
            if(((Integer) pane.getValue()) == JOptionPane.YES_OPTION) {
                break;
            }
        }
        
        JOptionPane.showMessageDialog(
                    null, 
                    String.format("Displaying data for %d quarter(s).", iNumQtrs) 
        );
        
        // Starting at E1, fill headers for the number of columns selected.
        ComIRange oResizeRange = sheet.getRange("E1", "E1").getResize(VARIANT_MISSING, iNumQtrs);

        oResizeRange.setFormula("=\"Q\" & COLUMN() - 4 & CHAR(10) & \"Sales\"");
      
        // Change the Orientation and WrapText properties for the headers.
        oResizeRange.setOrientation(38);
        oResizeRange.setWrapText(true);
      
        // Fill the interior color of the headers.
        oResizeRange.getInterior().setColorIndex(36);
      
        // Fill the columns with a formula and apply a number format.
        oResizeRange = sheet.getRange("E2", "E6").getResize(VARIANT_MISSING, iNumQtrs);
        oResizeRange.setFormula("=RAND()*100");
        oResizeRange.setNumberFormat("$0.00");
      
        // Apply borders to the Sales data and headers.
        oResizeRange = sheet.getRange("E1", "E6").getResize(VARIANT_MISSING, iNumQtrs);
        oResizeRange.getBorders().setWeight(XlBorderWeight.xlThin);
      
        // Add a Totals formula for the sales data and apply a border.
        oResizeRange = sheet.getRange("E8", "E8").getResize(VARIANT_MISSING, iNumQtrs);
        oResizeRange.setFormula("=SUM(E2:E6)");
        Borders oResizeRangeBorders = oResizeRange.getBorders();
        oResizeRangeBorders.setLineStyle(XlLineStyle.xlDouble);
        oResizeRangeBorders.setWeight(XlBorderWeight.xlThick);

        // Add a Chart for the selected data
        oResizeRange = sheet.getRange("E2:E6").getResize(VARIANT_MISSING, iNumQtrs);

        Chart chart = sheet.getParent().getCharts().Add(VARIANT_MISSING,VARIANT_MISSING,VARIANT_MISSING,VARIANT_MISSING);
        // Java note: Assumption is, that VARIANT_MISSING is the correct indicator
        // for missing values, it turns out, NULL is correct in this case...
        chart.ChartWizard(oResizeRange, XlChartType.xl3DColumn, VARIANT_MISSING,
                XlRowCol.xlColumns,
                null, null, null,
                null,null,null,
                null
        );
        chart.SeriesCollection(1).setXValues(sheet.getRange("C2", "C6"));
        for(int i = 1; i <= iNumQtrs; i++) {
            chart.SeriesCollection(i).setName("=\"Q" + Integer.toString(i) + "\"");
        }
        chart.Location(XlChartLocation.xlLocationAsObject, sheet.getName());
      
        // Move the chart so as not to cover your data.
        Shape shape = sheet.getShapes().Item(1);
        shape.setTop(sheet.getRows(10).getTop());
        shape.setLeft(sheet.getColumns(2).getLeft());
    }
    
    private static SAFEARRAY safeVariantArrayFromJava(String[][] data) {
        // The data array is defined/stored row major, while excel expects the
        // data column major, so this method also transposes the matrix
        
        OaIdl.SAFEARRAY wrapped = OaIdl.SAFEARRAY.createSafeArray(data[0].length, data.length);
        // VARIANT is java allocated and will be freed by GC
        VARIANT var = new VARIANT();
        for(int i = 0; i < data.length; i++) {
            for(int j = 0; j < data[0].length; j++) {
                // BSTR is allocated by java and will be freed by GC
                var.setValue(Variant.VT_BSTR, new BSTR(data[i][j]));
                wrapped.putElement(var, j, i);
            }
        }
        return wrapped;
    }
}
