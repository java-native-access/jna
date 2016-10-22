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
package com.sun.jna.platform.win32.COM.util.office;

import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.COM.Helper;
import java.io.File;

import com.sun.jna.platform.win32.COM.util.AbstractComEventCallbackListener;
import com.sun.jna.platform.win32.COM.util.Factory;
import com.sun.jna.platform.win32.COM.util.IComEventCallbackCookie;
import com.sun.jna.platform.win32.COM.util.office.excel.ComExcel_Application;
import com.sun.jna.platform.win32.COM.util.office.excel.ComIAppEvents;
import com.sun.jna.platform.win32.COM.util.office.excel.ComIApplication;
import com.sun.jna.platform.win32.COM.util.office.excel.ComIRange;
import com.sun.jna.platform.win32.COM.util.office.excel.ComIWorkbook;
import com.sun.jna.platform.win32.COM.util.office.excel.ComIWorksheet;
import com.sun.jna.platform.win32.Ole32;
import java.io.IOException;

public class MSOfficeExcelDemo {
        private static final String currentWorkingDir = new File("").getAbsolutePath() + File.separator;

	public static void main(String[] argv) throws IOException {
            Ole32.INSTANCE.CoInitializeEx(Pointer.NULL, Ole32.COINIT_MULTITHREADED);
            try {
                testExcel();
            } finally {
                Ole32.INSTANCE.CoUninitialize();
            }
        }
        
        public static void testExcel() throws IOException {
                File demoDocument = null;
                ComIApplication msExcel = null;
		Factory factory = new Factory();
		try {
                        System.out.println("Files in temp dir: " + Helper.tempDir.getAbsolutePath());
                        
			ComExcel_Application excelObject = factory.createObject(ComExcel_Application.class);
			msExcel = excelObject.queryInterface(ComIApplication.class);
                        
			System.out.println("MSExcel version: " + msExcel.getVersion());
                        
			msExcel.setVisible(true);
                        
                        Helper.sleep(5);
                        
                        demoDocument = Helper.createNotExistingFile("jnatest", ".xls");
                        Helper.extractClasspathFileToReal("/com/sun/jna/platform/win32/COM/util/office/resources/jnatest.xls", demoDocument);
                        
                        ComIWorkbook workbook = msExcel.getWorkbooks().Open(demoDocument.getAbsolutePath());
			msExcel.getActiveSheet().getRange("A1").setValue("Hello from JNA!");
			// wait 1sec. before closing
			Helper.sleep(1);
			// Save document into temp and close
                        File output = new File(Helper.tempDir, "jnatest.xls");
                        output.delete();
                        workbook.SaveAs(output.getAbsolutePath());
			msExcel.getActiveWorkbook().Close(false);

//			// msExcel.newExcelBook();
			msExcel.getWorkbooks().Open(output.getAbsolutePath());
			msExcel.getActiveSheet().getRange("A2").setValue("Hello again from JNA!");

			class Listener extends AbstractComEventCallbackListener implements ComIAppEvents {
				volatile boolean SheetSelectionChange_called;
				
				@Override
				public void errorReceivingCallbackEvent(String message, Exception exception) {
				}

				@Override
				public void SheetSelectionChange(ComIWorksheet sheet, ComIRange target) {
					SheetSelectionChange_called = true;
				}
				
			};
			Listener listener = new Listener();
			IComEventCallbackCookie cookie = msExcel.advise(ComIAppEvents.class, listener);
			
                        Helper.sleep(1);
                        
			msExcel.getActiveSheet().getRange("A5").Activate();
			
			Helper.sleep(1);
                        
                        msExcel.unadvise(ComIAppEvents.class, cookie);
			
                        System.out.println("Listener was fired: " + listener.SheetSelectionChange_called);
                        
			// close and discard changes
			msExcel.getActiveWorkbook().Close(false);
		} finally {
                        // Make sure the excel instance is shut down
			if (null != msExcel) {
				msExcel.Quit();
			}
                        
                        // Release all objects acquired by the factory
                        factory.disposeAll();
                        
                        if (demoDocument != null && demoDocument.exists()) {
                            demoDocument.delete();
                        }
		}
	}
}
