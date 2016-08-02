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

import com.sun.jna.platform.win32.COM.IDispatch;
import com.sun.jna.platform.win32.COM.IDispatchCallback;
import com.sun.jna.platform.win32.COM.util.annotation.ComObject;
import com.sun.jna.platform.win32.Guid;
import com.sun.jna.platform.win32.OaIdl;
import com.sun.jna.platform.win32.OleAuto;
import com.sun.jna.platform.win32.Variant;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.IntByReference;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

/**
 * Factory is intended as a simpler to use version of ObjectFactory.
 * 
 * <p>The Factory abstracts the necessity to handle COM threading by introducing
 * a dispatching thread, that is correctly COM initialized and is used to handle
 * all outgoing calls.</p>
 * 
 * <p><b>NOTE:</b> Remember to call factory.getComThread().terminate() at some
 * appropriate point, when the factory is not used anymore</p>
 */
public class Factory extends ObjectFactory {

    private ComThread comThread;

    public Factory() {
        this(new ComThread("Default Factory COM Thread", 5000, new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                //ignore
            }
        }));
    }

    public Factory(ComThread comThread) {
        this.comThread = comThread;
    }

    private class ProxyObject2 implements InvocationHandler {

        private final Object delegate;

        public ProxyObject2(Object delegate) {
            this.delegate = delegate;
        }

        @Override
        public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
            if (args != null) {
                for (int i = 0; i < args.length; i++) {
                    if (args[i] != null
                            && Proxy.isProxyClass(args[i].getClass())) {
                        InvocationHandler ih = Proxy.getInvocationHandler(args[i]);
                        if (ih instanceof ProxyObject2) {
                            args[i] = ((ProxyObject2) ih).delegate;
                        }
                    }
                }
            }

            return comThread.execute(new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    return method.invoke(delegate, args);
                }
            });
        }
    }

    private class CallbackProxy2 extends CallbackProxy {

        public CallbackProxy2(ObjectFactory factory, Class<?> comEventCallbackInterface, IComEventCallbackListener comEventCallbackListener) {
            super(factory, comEventCallbackInterface, comEventCallbackListener);
        }

        @Override
        public WinNT.HRESULT Invoke(OaIdl.DISPID dispIdMember, Guid.REFIID riid, WinDef.LCID lcid, WinDef.WORD wFlags, OleAuto.DISPPARAMS.ByReference pDispParams, Variant.VARIANT.ByReference pVarResult, OaIdl.EXCEPINFO.ByReference pExcepInfo, IntByReference puArgErr) {
            // Mark callbacks as COM initialized - so normal inline call
            // invocation can be used -- see ComThread#
            ComThread.setComThread(true);
            try {
                return super.Invoke(dispIdMember, riid, lcid, wFlags, pDispParams, pVarResult, pExcepInfo, puArgErr);
            } finally {
                ComThread.setComThread(false);
            }
        }
    }
    
    @Override
    public <T> T createProxy(Class<T> comInterface, IDispatch dispatch) {
        T result = super.createProxy(comInterface, dispatch);
        ProxyObject2 po2 = new ProxyObject2(result);
        Object proxy = Proxy.newProxyInstance(comInterface.getClassLoader(), new Class<?>[]{comInterface}, po2);
        return (T) proxy;
    }

    @Override
    Guid.GUID discoverClsId(final ComObject annotation) {
        return runInComThread(new Callable<Guid.GUID>() {
            public Guid.GUID call() throws Exception {
                return Factory.super.discoverClsId(annotation);
            }
        });
    }

    @Override
    public <T> T fetchObject(final Class<T> comInterface) {
        // Proxy2 is added by createProxy inside fetch Object
        return runInComThread(new Callable<T>() {
            public T call() throws Exception {
                return Factory.super.fetchObject(comInterface);
            }
        });
    }

    @Override
    public <T> T createObject(final Class<T> comInterface) {
        // Proxy2 is added by createProxy inside fetch Object
        return runInComThread(new Callable<T>() {
            public T call() throws Exception {
                return Factory.super.createObject(comInterface);
            }
        });
    }

    @Override
    IDispatchCallback createDispatchCallback(Class<?> comEventCallbackInterface, IComEventCallbackListener comEventCallbackListener) {
        return new CallbackProxy2(this, comEventCallbackInterface, comEventCallbackListener);
    }
    
    @Override
    public IRunningObjectTable getRunningObjectTable() {
        return super.getRunningObjectTable();
    }
    
    private <T> T runInComThread(Callable<T> callable) {
        try {
            return comThread.execute(callable);
        } catch (TimeoutException ex) {
            throw new RuntimeException(ex);
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
        } catch (ExecutionException ex) {
            throw new RuntimeException(ex);
        }
    }

    public ComThread getComThread() {
        return comThread;
    }
}
