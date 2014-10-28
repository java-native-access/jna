package com.sun.jna.platform.win32.COM.util;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

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
					//something has gone wrong!
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

	@Override
	protected void finalize() throws Throwable {
		executor.submit(new Runnable() {
			@Override
			public void run() {
				Ole32.INSTANCE.CoUninitialize();
			}
		});
		executor.shutdown();
		super.finalize();
	}

	public <T> T execute(Callable<T> task) throws InterruptedException, ExecutionException {
		if (this.requiresInitialisation) {
			executor.execute(firstTask);
		}
		return executor.submit(task).get();
	}

}
