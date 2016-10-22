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
package com.sun.jna.platform.win32.COM.util;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.sun.jna.platform.win32.Ole32;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.platform.win32.COM.COMUtils;

public class ComThread {
        private static ThreadLocal<Boolean> isCOMThread = new ThreadLocal<Boolean>();
    
	ExecutorService executor;
	Runnable firstTask;
	boolean requiresInitialisation;
	long timeoutMilliseconds;
	UncaughtExceptionHandler uncaughtExceptionHandler;
	
	public ComThread(final String threadName, long timeoutMilliseconds, UncaughtExceptionHandler uncaughtExceptionHandler) {
		this(threadName, timeoutMilliseconds, uncaughtExceptionHandler, Ole32.COINIT_MULTITHREADED);
	}
	
	public ComThread(final String threadName, long timeoutMilliseconds, UncaughtExceptionHandler uncaughtExceptionHandler, final int coinitialiseExFlag) {
		this.requiresInitialisation = true;
		this.timeoutMilliseconds = timeoutMilliseconds;
		this.uncaughtExceptionHandler = uncaughtExceptionHandler;
		this.firstTask = new Runnable() {
			@Override
			public void run() {
				try {
					//If we do not use COINIT_MULTITHREADED, it is necessary to have
					// a message loop see -
					// [http://www.codeguru.com/cpp/com-tech/activex/apts/article.php/c5529/Understanding-COM-Apartments-Part-I.htm]
					// [http://www.codeguru.com/cpp/com-tech/activex/apts/article.php/c5533/Understanding-COM-Apartments-Part-II.htm]
					WinNT.HRESULT hr = Ole32.INSTANCE.CoInitializeEx(null, coinitialiseExFlag);
                                        isCOMThread.set(true);
					COMUtils.checkRC(hr);
					ComThread.this.requiresInitialisation = false;
				} catch (Throwable t) {
					ComThread.this.uncaughtExceptionHandler.uncaughtException(Thread.currentThread(), t);
				}
			}
		};
		executor = Executors.newSingleThreadExecutor(new ThreadFactory() {

			@Override
			public Thread newThread(Runnable r) {
				if (!ComThread.this.requiresInitialisation) {
					// something has gone wrong!
					throw new RuntimeException("ComThread executor has a problem.");
				}
				Thread thread = new Thread(r, threadName);
				//make sure this is a daemon thread, or it will stop JVM existing
				// if program does not call terminate(); 
				thread.setDaemon(true);

				thread.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
					@Override
					public void uncaughtException(Thread t, Throwable e) {
						ComThread.this.requiresInitialisation = true;
						ComThread.this.uncaughtExceptionHandler.uncaughtException(t, e);
					}
				});

				return thread;
			}
		});

	}

	/**
	 * Stop the COM Thread.
	 * 
	 * @param timeoutMilliseconds
	 *            number of milliseconds to wait for a clean shutdown before a
	 *            forced shutdown is attempted
	 */
	public void terminate(long timeoutMilliseconds) {
		try {

			executor.submit(new Runnable() {
				@Override
				public void run() {
					Ole32.INSTANCE.CoUninitialize();
				}
			}).get(timeoutMilliseconds, TimeUnit.MILLISECONDS);

			executor.shutdown();

		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		} catch (TimeoutException e) {
			executor.shutdownNow();
		}
	}

	@Override
	protected void finalize() throws Throwable {
		if (!executor.isShutdown()) {
			this.terminate(100);
		}
	}

        static void setComThread(boolean value) {
            isCOMThread.set(value);
        }
        
	public <T> T execute(Callable<T> task) throws TimeoutException, InterruptedException, ExecutionException {
                // If the call is done on a COM thread, invoke directly
                // if the call comes from outside the invokation is dispatched
                // into the Dispatch Thread.
                Boolean comThread = isCOMThread.get();
                if(comThread == null) {
                        comThread = false;
                }
                if(comThread) {
                        try {
                                return task.call();
                        } catch (Exception ex) {
                                throw new ExecutionException(ex);
                        }
                } else {
                        if (this.requiresInitialisation) {
                                executor.execute(firstTask);
                        }
                        return executor.submit(task).get(this.timeoutMilliseconds, TimeUnit.MILLISECONDS);
                }
	}

}