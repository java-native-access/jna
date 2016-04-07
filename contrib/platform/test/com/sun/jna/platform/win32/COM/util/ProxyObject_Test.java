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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.sun.jna.platform.win32.COM.util.annotation.ComInterface;
import com.sun.jna.platform.win32.COM.util.annotation.ComObject;
import com.sun.jna.platform.win32.COM.util.annotation.ComMethod;
import com.sun.jna.platform.win32.COM.util.annotation.ComProperty;
import com.sun.jna.platform.win32.Ole32;

public class ProxyObject_Test {

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
	}	
	
	@ComObject(progId="Word.Application")
	interface MsWordApp extends Application {
	}
	
	Factory factory;
        
	@Before
	public void before() {
                Ole32.INSTANCE.CoInitializeEx(Pointer.NULL, Ole32.COINIT_MULTITHREADED);
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
		Ole32.INSTANCE.CoUninitialize();
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
	
}
