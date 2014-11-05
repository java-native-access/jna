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

	ExecutorService executor;
	Runnable firstTask;
	boolean requiresInitialisation;

	public ComThread(final String threadName) {
		this.requiresInitialisation = true;
		this.firstTask = new Runnable() {
			@Override
			public void run() {
				WinNT.HRESULT hr = Ole32.INSTANCE.CoInitialize(null);
				COMUtils.checkRC(hr);
				ComThread.this.requiresInitialisation = false;
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

				thread.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
					@Override
					public void uncaughtException(Thread t, Throwable e) {
						ComThread.this.requiresInitialisation = true;
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

	public <T> T execute(Callable<T> task) throws InterruptedException, ExecutionException {
		if (this.requiresInitialisation) {
			executor.execute(firstTask);
		}
		return executor.submit(task).get();
	}

}
