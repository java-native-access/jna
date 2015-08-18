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
package com.sun.jna.platform.win32.COM.util.office;

import java.io.File;

import com.sun.jna.platform.win32.COM.office.MSExcel;
import com.sun.jna.platform.win32.COM.util.AbstractComEventCallbackListener;
import com.sun.jna.platform.win32.COM.util.Factory;
import com.sun.jna.platform.win32.COM.util.office.excel.ComExcel_Application;
import com.sun.jna.platform.win32.COM.util.office.excel.ComIAppEvents;
import com.sun.jna.platform.win32.COM.util.office.excel.ComIApplication;
import com.sun.jna.platform.win32.COM.util.office.excel.ComIRange;
import com.sun.jna.platform.win32.COM.util.office.excel.ComIWorksheet;
import com.sun.jna.platform.win32.COM.util.office.word.ComWord_Application;

public class MSOfficeExcelDemo {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new MSOfficeExcelDemo();
	}

	private String currentWorkingDir = new File("").getAbsolutePath() + File.separator;

	public MSOfficeExcelDemo() {
		this.testMSExcel();
	}

	public void testMSExcel() {
		ComExcel_Application excelObject = null;
		ComIApplication msExcel = null;
		Factory factory = null;
		try {
			factory = new Factory();
			excelObject = factory.createObject(ComExcel_Application.class);
			msExcel = excelObject.queryInterface(ComIApplication.class);
			System.out.println("MSExcel version: " + msExcel.getVersion());
			msExcel.setVisible(true);
			// msExcel.newExcelBook();
			msExcel.getWorkbooks().Open(currentWorkingDir + "jnatest.xls");
			msExcel.getActiveSheet().getRange("A1").setValue("Hello from JNA!");
			// wait 1sec. before closing
			Thread.currentThread().sleep(1000);
//			// close and save the active sheet
//			msExcel.getActiveWorkbook().Close(true);
//			msExcel.setVisible(true);
//			// msExcel.newExcelBook();
//			msExcel.getWorkbooks().Open(currentWorkingDir + "jnatest.xls");
//			msExcel.getActiveSheet().getRange("A2").setValue("Hello again from JNA!");

			class Listener extends AbstractComEventCallbackListener implements ComIAppEvents {
				boolean SheetSelectionChange_called;
				
				@Override
				public void errorReceivingCallbackEvent(String message, Exception exception) {
				}

				@Override
				public void SheetSelectionChange(ComIWorksheet sheet, ComIRange target) {
					SheetSelectionChange_called = true;
				}
				
			};
			Listener listener = new Listener();
			msExcel.advise(ComIAppEvents.class, listener);
//			
//			msExcel.getActiveSheet().getRange("A5").Activate();
//			
//			Thread.currentThread().sleep(500);
			
			// close and save the active sheet
			msExcel.getActiveWorkbook().Close(true);
			
			msExcel.Quit();
			msExcel = null;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != msExcel) {
				msExcel.Quit();
			}
			if (null != factory) {
				factory.disposeAll();
			}
		}
	}
}
