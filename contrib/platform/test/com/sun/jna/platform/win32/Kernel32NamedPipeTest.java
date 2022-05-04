/* Copyright (c) 2007 Timothy Wall, All Rights Reserved
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
package com.sun.jna.platform.win32;

import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Test;

import com.sun.jna.platform.win32.WinDef.ULONGByReference;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.ptr.IntByReference;

/**
 * @author lgoldstein
 */
public class Kernel32NamedPipeTest extends AbstractWin32TestSupport {
    public Kernel32NamedPipeTest() {
        super();
    }

    @Test
    public void testNamedPipeServerAPI() {
        String    pipeName="\\\\.\\pipe\\" + getCurrentTestName();
        HANDLE    hNamedPipe=Kernel32.INSTANCE.CreateNamedPipe(pipeName,
                WinBase.PIPE_ACCESS_DUPLEX,        // dwOpenMode
                WinBase.PIPE_TYPE_BYTE | WinBase.PIPE_READMODE_BYTE | WinBase.PIPE_WAIT,    // dwPipeMode
                1,    // nMaxInstances,
                Byte.MAX_VALUE,    // nOutBufferSize,
                Byte.MAX_VALUE,    // nInBufferSize,
                1000,    // nDefaultTimeOut,
                null);    // lpSecurityAttributes
        assertCallSucceeded("CreateNamedPipe", !WinBase.INVALID_HANDLE_VALUE.equals(hNamedPipe));

        // NOTE: we don't really care what the returned values are only that the call succeeds
        try {
            IntByReference lpFlags = new IntByReference(0);
            IntByReference lpOutBuffferSize = new IntByReference(0);
            IntByReference lpInBufferSize = new IntByReference(0);
            IntByReference lpMaxInstances = new IntByReference(0);
            assertCallSucceeded("GetNamedPipeInfo",
                Kernel32.INSTANCE.GetNamedPipeInfo(hNamedPipe, lpFlags,
                    lpOutBuffferSize, lpInBufferSize, lpMaxInstances));

            ULONGByReference ServerProcessId = new ULONGByReference();
            assertCallSucceeded("GetNamedPipeServerProcessId", Kernel32.INSTANCE.GetNamedPipeServerProcessId(hNamedPipe, ServerProcessId));

            ULONGByReference ServerSessionId = new ULONGByReference();
            assertCallSucceeded("GetNamedPipeServerSessionId", Kernel32.INSTANCE.GetNamedPipeServerSessionId(hNamedPipe, ServerSessionId));

            assertCallSucceeded("DisconnectNamedPipe", Kernel32.INSTANCE.DisconnectNamedPipe(hNamedPipe));
        } finally {    // clean up
            assertCallSucceeded("Named pipe handle close", Kernel32.INSTANCE.CloseHandle(hNamedPipe));
        }
    }

    @Test
    public void testMultiThreadedNamedPipe() {
        final String pipeName = "\\\\.\\pipe\\" + getCurrentTestName();
        final Logger logger = Logger.getLogger(getClass().getName());
        final int MAX_BUFFER_SIZE=1024;
        ExecutorService executors = Executors.newFixedThreadPool(2);
        try {
            Future<?> server = executors.submit(new Runnable() {
                    @Override
                    public void run() {
                        // based on https://msdn.microsoft.com/en-us/library/windows/desktop/aa365588(v=vs.85).aspx
                        HANDLE hNamedPipe = assertValidHandle("CreateNamedPipe", Kernel32.INSTANCE.CreateNamedPipe(pipeName,
                                WinBase.PIPE_ACCESS_DUPLEX,        // dwOpenMode
                                WinBase.PIPE_TYPE_MESSAGE | WinBase.PIPE_READMODE_MESSAGE | WinBase.PIPE_WAIT,    // dwPipeMode
                                1,    // nMaxInstances,
                                MAX_BUFFER_SIZE,    // nOutBufferSize,
                                MAX_BUFFER_SIZE,    // nInBufferSize,
                                (int) TimeUnit.SECONDS.toMillis(30L),    // nDefaultTimeOut,
                                null    // lpSecurityAttributes
                              ));

                        try {
                            logger.info("Await client connection");
                            assertCallSucceeded("ConnectNamedPipe", Kernel32.INSTANCE.ConnectNamedPipe(hNamedPipe, null));
                            logger.info("Client connected");

                            byte[] readBuffer = new byte[MAX_BUFFER_SIZE];
                            IntByReference lpNumberOfBytesRead = new IntByReference(0);
                            assertCallSucceeded("ReadFile", Kernel32.INSTANCE.ReadFile(hNamedPipe, readBuffer, readBuffer.length, lpNumberOfBytesRead, null));

                            int readSize = lpNumberOfBytesRead.getValue();
                            logger.info("Received client data - length=" + readSize);
                            assertTrue("No data receieved from client", readSize > 0);

                            IntByReference lpNumberOfBytesWritten = new IntByReference(0);
                            assertCallSucceeded("WriteFile", Kernel32.INSTANCE.WriteFile(hNamedPipe, readBuffer, readSize, lpNumberOfBytesWritten, null));
                            logger.info("Echoed client data - length=" + lpNumberOfBytesWritten.getValue());
                            assertEquals("Mismatched write buffer size", readSize, lpNumberOfBytesWritten.getValue());

                            // Flush the pipe to allow the client to read the pipe's contents before disconnecting
                            assertCallSucceeded("FlushFileBuffers", Kernel32.INSTANCE.FlushFileBuffers(hNamedPipe));
                            logger.info("Disconnecting");
                            assertCallSucceeded("DisconnectNamedPipe", Kernel32.INSTANCE.DisconnectNamedPipe(hNamedPipe));
                            logger.info("Disconnected");
                        } finally {    // clean up
                            assertCallSucceeded("Named pipe handle close", Kernel32.INSTANCE.CloseHandle(hNamedPipe));
                        }
                    }
                });
            logger.info("Started server - handle=" + server);

            Future<?> client = executors.submit(new Runnable() {
                    @Override
                    public void run() {
                        // based on https://msdn.microsoft.com/en-us/library/windows/desktop/aa365592(v=vs.85).aspx
                        assertCallSucceeded("WaitNamedPipe", Kernel32.INSTANCE.WaitNamedPipe(pipeName, (int) TimeUnit.SECONDS.toMillis(15L)));
                        logger.info("Connected to server");

                        HANDLE hPipe = assertValidHandle("CreateNamedPipe", Kernel32.INSTANCE.CreateFile(pipeName,
                                 WinNT.GENERIC_READ | WinNT.GENERIC_WRITE,
                                 0,                      // no sharing
                                 null,                   // default security attributes
                                 WinNT.OPEN_EXISTING,      // opens existing pipe
                                 0,                      // default attributes
                                 null                  // no template file
                               ));

                        try {
                            IntByReference lpMode = new IntByReference(WinBase.PIPE_READMODE_MESSAGE);
                            assertCallSucceeded("SetNamedPipeHandleState", Kernel32.INSTANCE.SetNamedPipeHandleState(hPipe, lpMode, null, null));

                            String expMessage = Thread.currentThread().getName() + " says hello";
                            byte[] expData = expMessage.getBytes();
                            IntByReference lpNumberOfBytesWritten = new IntByReference(0);
                            assertCallSucceeded("WriteFile", Kernel32.INSTANCE.WriteFile(hPipe, expData, expData.length, lpNumberOfBytesWritten, null));
                            logger.info("Sent hello message");
                            assertEquals("Mismatched write buffer size", expData.length, lpNumberOfBytesWritten.getValue());

                            byte[] readBuffer = new byte[MAX_BUFFER_SIZE];
                            IntByReference lpNumberOfBytesRead = new IntByReference(0);
                            assertCallSucceeded("ReadFile", Kernel32.INSTANCE.ReadFile(hPipe, readBuffer, readBuffer.length, lpNumberOfBytesRead, null));

                            int readSize = lpNumberOfBytesRead.getValue();
                            logger.info("Received server data - length=" + readSize);
                            assertTrue("No data receieved from server", readSize > 0);

                            String actMessage = new String(readBuffer, 0, readSize);
                            assertEquals("Mismatched server data", expMessage, actMessage);
                        } finally {    // clean up
                            assertCallSucceeded("Named pipe handle close", Kernel32.INSTANCE.CloseHandle(hPipe));
                        }
                    }
                });
            logger.info("Started client - handle=" + client);

            boolean exceptionReported = false;
            for (Future<?> f : Arrays.asList(client, server)) {
                try {
                    f.get(30L, TimeUnit.SECONDS);
                    logger.log(Level.INFO, "Finished {0}", f);
                } catch(Exception e) {
                    exceptionReported = true;
                    logger.log(Level.WARNING, e.getClass().getSimpleName() + " while await completion of " + f, e);
                }
            }

            assertFalse(exceptionReported);
        } finally {
            executors.shutdownNow();
        }
    }
}
