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
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.sun.jna.platform.win32.COM.util.annotation.ComEventCallback;
import com.sun.jna.platform.win32.COM.util.annotation.ComInterface;
import com.sun.jna.platform.win32.COM.util.annotation.ComMethod;
import com.sun.jna.platform.win32.COM.util.annotation.ComObject;
import com.sun.jna.platform.win32.COM.util.annotation.ComProperty;
import com.sun.jna.platform.win32.OaIdl;
import com.sun.jna.platform.win32.Ole32;
import com.sun.jna.platform.win32.Variant;
import com.sun.jna.platform.win32.Variant.VARIANT;
import org.hamcrest.CoreMatchers;

public class ComEventCallbacks_Test {

        static {
                ClassLoader.getSystemClassLoader().setDefaultAssertionStatus(true);
        }
    
	Factory factory;
	
	@Before
	public void before() {
                Ole32.INSTANCE.CoInitializeEx(Pointer.NULL, Ole32.COINIT_MULTITHREADED);
		this.factory = new Factory();
	}

	@After
	public void after() {
		this.factory.disposeAll();
                Ole32.INSTANCE.CoUninitialize();
	}
	
	
	@ComObject(progId="Internet.Explorer.1", clsId = "{0002DF01-0000-0000-C000-000000000046}")
	interface ComInternetExplorer extends ComIWebBrowser2 {
	}
	
	@ComInterface(iid="{D30C1661-CDAF-11D0-8A3E-00C04FC9E26E}")
	interface ComIWebBrowser2  extends IUnknown, IConnectionPoint {
		@ComProperty
		boolean getVisible();
		
		@ComProperty
		void setVisible(boolean value);
		
		@ComMethod
		void Quit();
                
		@ComMethod
                /**
                 * navOpenInNewWindow = 1
                 * navNoHistory = 2
                 * navNoReadFromCache = 4
                 * navNoWriteToCache = 8
                 * navAllowAutosearch = 16
                 * navBrowserBar = 32
                 * navHyperlink = 64
                 * navEnforceRestricted = 128
                 * navNewWindowsManaged = 256
                 * navUntrustedForDownload = 512
                 * navTrustedForActiveX = 1024
                 * navOpenInNewTab = 2048
                 * navOpenInBackgroundTab = 4096
                 * navKeepWordWheelText = 8192
                 * navVirtualTab = 16384
                 * navBlockRedirectsXDomain = 32768
                 * navOpenNewForegroundTab = 65536
                 */
		void Navigate(String url, long flags, String targetFrameName, VARIANT postData, String headers);
	}
        
	@ComInterface(iid="{34A715A0-6587-11D0-924A-0020AFC7AC4D}")
	interface DWebBrowserEvents2 {
		@ComEventCallback(dispid=0x000000fd)
		void OnQuit();
		
		@ComEventCallback(dispid=0x000000fc)
		void NavigateComplete2(IUnknown source, Object url);

		@ComEventCallback(dispid=0x000000fa)
		void BeforeNavigate2(IUnknown pDisp, 
                        String URL,
                        long Flags,
                        String TargetFrameName,
                        VARIANT.ByReference PostData,
                        VARIANT.ByReference Headers,
                        OaIdl.VARIANT_BOOLByReference Cancel);
	}
	
	class DWebBrowserEvents2_Listener extends AbstractComEventCallbackListener implements DWebBrowserEvents2 {

		@Override
		public void errorReceivingCallbackEvent(String message, Exception exception) {
//                    System.err.println(message);
//                    if(exception != null) {
//                        System.err.println(exception.getMessage());
//                        exception.printStackTrace(System.err);
//                    }
		}

                volatile boolean blockNavigate = false;

                public void BeforeNavigate2(
                        IUnknown pDisp, 
                        String URL, 
                        long Flags, 
                        String TargetFrameName, 
                        VARIANT.ByReference PostData, 
                        VARIANT.ByReference Headers, 
                        OaIdl.VARIANT_BOOLByReference Cancel) {
                    // The utilizing unittest is adviseBeforeNavigate
                    if(blockNavigate){
                        Cancel.setValue(Variant.VARIANT_TRUE);
                    }
                }
                
                volatile boolean navigateComplete2Called = false;
                volatile String navigateComplete2URL = null;
                @Override
                public void NavigateComplete2( IUnknown source, Object url) {
                    navigateComplete2Called = true;
                    if(url != null) {
                        navigateComplete2URL = url.toString();
                    }
                }

		volatile Boolean Quit_called = null;
		@Override
		public void OnQuit() {
			Quit_called = true;
		}	
	}

	@Test
	public void advise_Quit() throws InterruptedException {
		ComInternetExplorer ieApp = factory.createObject(ComInternetExplorer.class);
		ComIWebBrowser2 iWebBrowser2 = ieApp.queryInterface(ComIWebBrowser2.class);
		iWebBrowser2.setVisible(true);
		DWebBrowserEvents2_Listener listener = new DWebBrowserEvents2_Listener();
		iWebBrowser2.advise(DWebBrowserEvents2.class, listener);
		
		iWebBrowser2.Quit();
		
		//Wait for event to happen
                Thread.sleep(200);
		
		Assert.assertNotNull(listener.Quit_called);
		Assert.assertTrue(listener.Quit_called);
	}

	@Test
	public void unadvise_Quit() throws InterruptedException {
		ComInternetExplorer ieApp = factory.createObject(ComInternetExplorer.class);
		ComIWebBrowser2 iWebBrowser2 = ieApp.queryInterface(ComIWebBrowser2.class);
		iWebBrowser2.setVisible(true);
                
		DWebBrowserEvents2_Listener listener = new DWebBrowserEvents2_Listener();
		IComEventCallbackCookie cookie = iWebBrowser2.advise(DWebBrowserEvents2.class, listener);
		
		iWebBrowser2.unadvise(DWebBrowserEvents2.class, cookie);
		listener.Quit_called=false;
                
		iWebBrowser2.Quit();
		
		Thread.sleep(200);
		
		Assert.assertNotNull(listener.Quit_called);
		Assert.assertFalse(listener.Quit_called);
	}
	
	@Test
	public void adviseNavigateComplete2() throws InterruptedException {
		ComInternetExplorer ieApp = factory.createObject(ComInternetExplorer.class);
		ComIWebBrowser2 iWebBrowser2 = ieApp.queryInterface(ComIWebBrowser2.class);
		iWebBrowser2.setVisible(true);
                
                DWebBrowserEvents2_Listener listener = new DWebBrowserEvents2_Listener();
		IComEventCallbackCookie cookie = iWebBrowser2.advise(DWebBrowserEvents2.class, listener);
                
		iWebBrowser2.Navigate("https://github.com/java-native-access/jna", 0, null, null, null);
                
                for(int i = 0; i < 10; i++) {
                    if(listener.navigateComplete2Called) {
                        break;
                    }
                    Thread.sleep(1000);
                }
                
                iWebBrowser2.Quit();
                
                Assert.assertTrue("NavigateComplete was not called", listener.navigateComplete2Called);
                Assert.assertNotNull("URL passed to NavigateComplete2 was NULL", listener.navigateComplete2URL);
                Assert.assertThat(listener.navigateComplete2URL, CoreMatchers.startsWith("https://github.com/java-native-access/jna"));
	}
        
	@Test
	public void adviseBeforeNavigate() throws InterruptedException {
		ComInternetExplorer ieApp = factory.createObject(ComInternetExplorer.class);
		ComIWebBrowser2 iWebBrowser2 = ieApp.queryInterface(ComIWebBrowser2.class);
		iWebBrowser2.setVisible(true);
                
                DWebBrowserEvents2_Listener listener = new DWebBrowserEvents2_Listener();
		IComEventCallbackCookie cookie = iWebBrowser2.advise(DWebBrowserEvents2.class, listener);
                
                listener.blockNavigate = true;
                
		iWebBrowser2.Navigate("https://github.com/java-native-access/jna", 0, null, null, null);
                
                for(int i = 0; i < 10; i++) {
                    if(listener.navigateComplete2Called) {
                        break;
                    }
                    Thread.sleep(1000);
                }
                
                iWebBrowser2.Quit();
                
                // NavigateComplete can't be called if access is blocked
                Assert.assertFalse("Navigation to https://github.com/java-native-access/jna should be blocked", listener.navigateComplete2Called);
		
	}
}
