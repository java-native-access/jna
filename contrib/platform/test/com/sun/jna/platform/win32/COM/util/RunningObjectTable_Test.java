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

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.sun.jna.platform.win32.COM.COMException;
import com.sun.jna.platform.win32.COM.util.annotation.ComInterface;
import com.sun.jna.platform.win32.COM.util.annotation.ComObject;
import com.sun.jna.platform.win32.COM.util.annotation.ComMethod;
import com.sun.jna.platform.win32.COM.util.annotation.ComProperty;
import com.sun.jna.platform.win32.Ole32;

public class RunningObjectTable_Test {

        static {
                ClassLoader.getSystemClassLoader().setDefaultAssertionStatus(true);
        }
    
	@ComInterface(iid="{00020970-0000-0000-C000-000000000046}")
	interface Application extends IUnknown {
		@ComProperty
		Boolean getVisible();
		
		@ComProperty
		void setVisible(Boolean value);
		
		@ComMethod
		void Quit(boolean SaveChanges, Object OriginalFormat, Boolean RouteDocument);
	}	
	
	@ComObject(progId="Word.Application")
	interface MsWordApp extends Application {
	}
	
	ObjectFactory factory;
	MsWordApp msWord;

	@Before
	public void before() {
                Ole32.INSTANCE.CoInitializeEx(Pointer.NULL, Ole32.COINIT_MULTITHREADED);
		this.factory = new ObjectFactory();
		//ensure there is only one word application running.
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
		
		
		this.msWord = this.factory.createObject(MsWordApp.class);
		msWord.setVisible(true);
	}
	
	@After
	public void after() {
		this.msWord.Quit(true, null, null);
		try {
			//wait for it to quit
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
                factory.disposeAll();
                Ole32.INSTANCE.CoUninitialize();
	}
	
	@Test
	public void getRunningObjectTable() {
		IRunningObjectTable rot = this.factory.getRunningObjectTable();

		assertNotNull(rot);
	}

	@Test
	public void enumRunning() {
		IRunningObjectTable rot = this.factory.getRunningObjectTable();

		for(IUnknown obj: rot.enumRunning()) {
			try {
				Application msw = obj.queryInterface(Application.class);
			} catch(COMException ex) {
				int i= 0;
			}
		}
	}
	
	@Test
	public void getActiveObjectsByInterface() {
		IRunningObjectTable rot = this.factory.getRunningObjectTable();
		
		List<Application> objs = rot.getActiveObjectsByInterface(Application.class);
		assertTrue(objs.size() > 0);
		
		for(Application dobj: objs) {
			msWord.setVisible(true);
			boolean v2 = dobj.getVisible();
			assertEquals(true, v2);
		}
		
	}
}
