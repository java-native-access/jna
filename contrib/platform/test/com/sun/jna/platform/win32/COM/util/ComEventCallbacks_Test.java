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

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.COM.util.annotation.ComEventCallback;
import com.sun.jna.platform.win32.COM.util.annotation.ComInterface;
import com.sun.jna.platform.win32.COM.util.annotation.ComMethod;
import com.sun.jna.platform.win32.COM.util.annotation.ComObject;
import com.sun.jna.platform.win32.COM.util.annotation.ComProperty;

public class ComEventCallbacks_Test {

	Factory factory;
	
	@Before
	public void before() {
		this.factory = new Factory();
	}

	@After
	public void after() {
		this.factory.disposeAll();
		this.factory.getComThread().terminate(100);
	}
	
	
	@ComObject(progId="Word.Application")
	interface ComIMsWordApp extends ComIApplication {
	}
	
	@ComInterface(iid="{00020970-0000-0000-C000-000000000046}")
	interface ComIApplication extends IUnknown, IConnectionPoint {
		@ComProperty
		boolean getVisible();
		
		@ComProperty
		void setVisible(boolean value);
		
		@ComMethod
		void Quit(boolean SaveChanges, Object OriginalFormat, Boolean RouteDocument);
		
		@ComProperty
		ComIDocuments getDocuments();
		
	}	
	
	@ComInterface(iid="{0002096C-0000-0000-C000-000000000046}")
	interface ComIDocuments {
		@ComMethod
		ComIDocument Open(String fileName);
		@ComMethod
		ComIDocument Add();
	}
	
	@ComInterface(iid="{0002096B-0000-0000-C000-000000000046}")
	interface ComIDocument {
		@ComProperty
		String getFullName();
		
		@ComMethod
		void Select();
	}
	
	@ComInterface(iid="{00020962-0000-0000-C000-000000000046}")
	interface ComIWindow {}
	
	@ComInterface(iid="{00020975-0000-0000-C000-000000000046}")
	public interface ComISelection {
		@ComProperty
		String getText();
	}
	
	@ComInterface(iid="{00020A01-0000-0000-C000-000000000046}")
	interface ApplicationEvents4_Event {
		@ComEventCallback(dispid=10)
		void WindowActivate(ComIDocument doc, ComIWindow win);
		
		@ComEventCallback(dispid=2)
		void Quit();
		
		@ComEventCallback(dispid=12)
		void WindowSelectionChange(ComISelection sel);
	}
	
	class ApplicationEvents4_EventListener extends AbstractComEventCallbackListener implements ApplicationEvents4_Event {

		@Override
		public void errorReceivingCallbackEvent(String message, Exception exception) {
			
		}
		
		Boolean WindowActivate_called = null;
		@Override
		public void WindowActivate(ComIDocument doc, ComIWindow win) {
			if (null!=doc && null !=win) {
				String docName = doc.getFullName();
				WindowActivate_called = true;
			}
		}
		
		Boolean Quit_called = null;
		@Override
		public void Quit() {
			Quit_called = true;
		}

		Boolean WindowSelectionChange_called = null;
		@Override
		public void WindowSelectionChange(ComISelection sel) {
			if (null!=sel) {
				String t = sel.getText();
				WindowSelectionChange_called = true;
			}
		}
		
	}

	@Test
	public void advise_Quit() {
		// Create word object
		ComIMsWordApp wordObj = factory.createObject(ComIMsWordApp.class);
		ComIApplication wordApp = wordObj.queryInterface(ComIApplication.class);
		wordApp.setVisible(true);
		ApplicationEvents4_EventListener listener = new ApplicationEvents4_EventListener();
		wordApp.advise(ApplicationEvents4_Event.class, listener);
		
		wordApp.Quit(false, null, null);
		
		//Wait for event to happen
		try {
			Thread.sleep(200);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		Assert.assertNotNull(listener.Quit_called);
		Assert.assertTrue(listener.Quit_called);
	}

	@Test
	public void unadvise_Quit() {
		// Create word object
		ComIMsWordApp wordObj = factory.createObject(ComIMsWordApp.class);
		ComIApplication wordApp = wordObj.queryInterface(ComIApplication.class);
		
		ApplicationEvents4_EventListener listener = new ApplicationEvents4_EventListener();
		IComEventCallbackCookie cookie = wordApp.advise(ApplicationEvents4_Event.class, listener);
		
		wordApp.unadvise(ApplicationEvents4_Event.class, cookie);
		listener.Quit_called=false;
		wordApp.Quit(false, null, null);
		
		//Wait for event to happen
		try {
			Thread.sleep(200);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		Assert.assertNotNull(listener.Quit_called);
		Assert.assertFalse(listener.Quit_called);
	}
	
	@Test
	public void WindowActivate() {
		// Create word object
		ComIMsWordApp wordObj = factory.createObject(ComIMsWordApp.class);
		ComIApplication wordApp = wordObj.queryInterface(ComIApplication.class);
		wordApp.setVisible(true);
		ApplicationEvents4_EventListener listener = new ApplicationEvents4_EventListener();
		wordApp.advise(ApplicationEvents4_Event.class, listener);
		wordApp.getDocuments().Add();
		
		//bring word doc to front
		HWND h = User32.INSTANCE.FindWindow("OpusApp", null);
		if (h == null)
			h = User32.INSTANCE.FindWindow("NetUIHWND", null);
		User32.INSTANCE.ShowWindow(h, User32.SW_RESTORE);
		User32.INSTANCE.SetForegroundWindow(h);
				
		//Wait for event to happen
		try {
			Thread.sleep(500);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		Assert.assertNotNull(listener.WindowActivate_called);
		Assert.assertTrue(listener.WindowActivate_called);
		
		wordApp.Quit(false, null, null);

	}

	@Test
	public void WindowSelectionChanged() {
		// Create word object
		ComIMsWordApp wordObj = factory.createObject(ComIMsWordApp.class);
		ComIApplication wordApp = wordObj.queryInterface(ComIApplication.class);
		wordApp.setVisible(true);
		ApplicationEvents4_EventListener listener = new ApplicationEvents4_EventListener();
		wordApp.advise(ApplicationEvents4_Event.class, listener);
		
		ComIDocument doc = wordApp.getDocuments().Add();

		doc.Select();
				
		//Wait for event to happen
		try {
			Thread.sleep(200);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		Assert.assertNotNull(listener.WindowSelectionChange_called);
		Assert.assertTrue(listener.WindowSelectionChange_called);
		
		wordApp.Quit(false, null, null);

	}

//	@Test
//	public void WindowSelectionChanged_jvmCrash() {
//		// Create word object
//		ComIMsWordApp wordObj = factory.createObject(ComIMsWordApp.class);
//		ComIApplication wordApp = wordObj.queryInterface(ComIApplication.class);
//		wordApp.setVisible(true);
//		ApplicationEvents4_EventListener listener = new ApplicationEvents4_EventListener();
//		wordApp.advise(ApplicationEvents4_Event.class, listener);
//		
//
//		
//		ComIDocument doc = wordApp.getDocuments().Add();
//
//		doc.Select();
//				
//		//Wait for event to happen
//		try {
//			Thread.sleep(2000000);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		
//		Assert.assertNotNull(listener.WindowSelectionChange_called);
//		Assert.assertTrue(listener.WindowSelectionChange_called);
//		
//		wordApp.Quit(false, null, null);
//
//	}
	
}
