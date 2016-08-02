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
package com.sun.jna.platform.win32.COM.util;

import com.sun.jna.Pointer;
import static org.junit.Assert.*;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.sun.jna.platform.win32.COM.util.annotation.ComInterface;
import com.sun.jna.platform.win32.COM.util.annotation.ComObject;
import com.sun.jna.platform.win32.COM.util.annotation.ComMethod;
import com.sun.jna.platform.win32.COM.util.annotation.ComProperty;
import com.sun.jna.platform.win32.Ole32;

public class ProxyObjectFactory_Test {

        static {
                ClassLoader.getSystemClassLoader().setDefaultAssertionStatus(true);
        }
    
	@ComInterface(iid="{00020970-0000-0000-C000-000000000046}")
	interface Application extends IUnknown {
		@ComProperty
		boolean getVisible();
		
		@ComProperty
		void setVisible(boolean value);
		
		@ComMethod
		void Quit(boolean SaveChanges, Object OriginalFormat, Boolean RouteDocument);

		@ComMethod
		public void Quit(Object... someArgs);

		@ComMethod(dispId = 0x00000183)
		public float PointsToPixels(float points, Object... someArgs);

		@ComProperty(dispId = 0x00000006)
		public Documents getDocuments();
	}	

	@ComInterface(iid = "{0002096C-0000-0000-C000-000000000046}")
	public interface Documents extends IDispatch {
		@ComMethod
		public _Document Add(Object template, Object newTemplate, Object documentType, Object visible);

		@ComMethod
		public _Document Add(Object... someArgs);
	}

	@ComInterface(iid = "{0002096B-0000-0000-C000-000000000046}")
	public interface _Document extends IDispatch {
		@ComMethod
		public void SaveAs(Object fileName, Object fileFormat, Object lockComments, Object password,
		        Object addToRecentFiles, Object writePassword, Object readOnlyRecommended, Object embedTrueTypeFonts,
		        Object saveNativePictureFormat, Object saveFormsData, Object saveAsAOCELetter, Object encoding,
		        Object insertLineBreaks, Object allowSubstitutions, Object lineEnding, Object addBiDiMarks);

		@ComMethod
		public void SaveAs(Object... someArgs);
	}

	public enum WdSaveFormat implements IComEnum {
		wdFormatDocument(0), wdFormatText(2), wdFormatRTF(6), wdFormatHTML(8), wdFormatPDF(17);

		private long _value;

		private WdSaveFormat(long value) {
			_value = value;
		}

		@Override
		public long getValue() {
			return _value;
		}
	}

	@ComObject(progId="Word.Application")
	interface MsWordApp extends Application {
	}
	
	Factory factory;
        
	@Before
	public void before() {
		this.factory = new Factory();
		//ensure there are no word applications running.
		while(true) {
			try {
				MsWordApp ao = this.factory.fetchObject(MsWordApp.class);
				Application a = ao.queryInterface(Application.class);
				try {
					a.Quit(true, null, null);
					try {
						//wait for it to quit
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				} catch (Exception e) {
					e.printStackTrace();e.getCause().printStackTrace();
				}
			} catch(Exception e) {
				break;
			}
		}
	}
	
	@After
	public void after() {
                factory.disposeAll();
                factory.getComThread().terminate(10000);
	}
	
	
	@Test
	public void equals() {
		MsWordApp comObj1 = this.factory.createObject(MsWordApp.class);
		MsWordApp comObj2 = this.factory.fetchObject(MsWordApp.class);

		boolean res = comObj1.equals(comObj2);
		
		assertTrue(res);
		
		comObj1.Quit(false, null,null);
	}
	
	@Test
	public void notEquals() {
		MsWordApp comObj1 = this.factory.createObject(MsWordApp.class);
		MsWordApp comObj2 = this.factory.createObject(MsWordApp.class);

		boolean res = comObj1.equals(comObj2);
		
		assertFalse(res);
		
		comObj1.Quit(false, null,null);
	}
	
	@Test
	public void accessWhilstDisposing() {
		MsWordApp comObj1 = this.factory.createObject(MsWordApp.class);
		
		//TODO: how to test this?
		
		this.factory.disposeAll();
		
	}
	
	@Test
	public void testVarargsCallWithoutVarargParameter() {
		MsWordApp comObj = this.factory.createObject(MsWordApp.class);

		// call must work without exception:
		float f = comObj.PointsToPixels(25.3f);
		comObj.Quit();
	}

	@Test
	public void testVarargsCallWithParameter() {
		MsWordApp comObj = this.factory.createObject(MsWordApp.class);

		Documents documents = comObj.getDocuments();
		_Document myDocument = documents.Add();

		String path = new File(".").getAbsolutePath();
		myDocument.SaveAs(path + "\\abcdefg", WdSaveFormat.wdFormatPDF);
		comObj.Quit();

		boolean wasDeleted = new File("abcdefg.pdf").delete();
		assertTrue(wasDeleted);
	}
}
