/* Copyright (c) 2011 Timothy Wall, All Rights Reserved
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * <p/>
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package com.sun.jna.wince;

import junit.framework.TestCase;
import com.sun.jna.*;
import com.sun.jna.ptr.*;
import com.sun.jna.win32.*;
import java.util.List;

public class CoreDLLTest extends TestCase {
    public static void main(java.lang.String[] argList) {
        junit.textui.TestRunner.run(CoreDLLTest.class);
    }

    public interface CoreDLL extends StdCallLibrary {
        CoreDLL INSTANCE = Native.loadLibrary("coredll", CoreDLL.class, W32APIOptions.UNICODE_OPTIONS);

        public static class SECURITY_ATTRIBUTES extends Structure {
            public static final List<String> FIELDS = createFieldsOrder("dwLength", "lpSecurityDescriptor", "bInheritHandle");
            public int dwLength;
            public Pointer lpSecurityDescriptor;
            public boolean bInheritHandle;
            public SECURITY_ATTRIBUTES() {
                dwLength = size();
            }
            @Override
            protected List<String> getFieldOrder() {
                return FIELDS;
            }
        }

        public static class STARTUPINFO extends Structure {
            public static final List<String> FIELDS = createFieldsOrder(
                    "cb", "lpReserved", "lpDesktop", "lpTitle", "dwX", "dwY", "dwXSize", "dwYSize",
                    "dwXCountChars", "dwYCountChars", "dwFillAttribute", "dwFlags", "wShowWindow",
                    "cbReserved2", "lpReserved2", "hStdInput", "hStdOutput", "hStdError");
            public int cb;
            public String lpReserved;
            public String lpDesktop;
            public String lpTitle;
            public int dwX;
            public int dwY;
            public int dwXSize;
            public int dwYSize;
            public int dwXCountChars;
            public int dwYCountChars;
            public int dwFillAttribute;
            public int dwFlags;
            public short wShowWindow;
            public short cbReserved2;
            public ByteByReference lpReserved2;
            public Pointer hStdInput;
            public Pointer hStdOutput;
            public Pointer hStdError;
            public STARTUPINFO() {
                cb = size();
            }
            @Override
            protected List<String> getFieldOrder() {
                return FIELDS;
            }
        }
        public static class PROCESS_INFORMATION extends Structure {
            public static final List<String> FIELDS = createFieldsOrder("hProcess", "hThread", "dwProcessId", "dwThreadId");
            public Pointer hProcess;
            public Pointer hThread;
            public int dwProcessId;
            public int dwThreadId;

            public static class ByReference extends PROCESS_INFORMATION implements Structure.ByReference {
                public ByReference() {
                }

                public ByReference(Pointer memory) {
                    super(memory);
                }
            }

            public PROCESS_INFORMATION() {
                super();
            }

            public PROCESS_INFORMATION(Pointer memory) {
                super(memory);
            }
            @Override
            protected List<String> getFieldOrder() {
                return FIELDS;
            }
        }

        boolean CreateProcess(String lpApplicationName, String lpCommandLine,
                              SECURITY_ATTRIBUTES lpProcessAttributes,
                              SECURITY_ATTRIBUTES lpThreadAttributes,
                              boolean bInheritHandles, int dwCreationFlags,
                              Pointer lpEnvironment, String lpCurrentDirectory, STARTUPINFO lpStartupInfo,
                              PROCESS_INFORMATION lpProcessInformation) throws LastErrorException;
    }

    public void testCreateProcess() {
        CoreDLL.PROCESS_INFORMATION processInformation = new CoreDLL.PROCESS_INFORMATION();
        //String cmd = "/storage card/Program files/PHM Tools/regedit";
        String cmd = null;
        boolean status = CoreDLL.INSTANCE.CreateProcess(cmd, null, null, null,
                                                        false, 0x10, Pointer.NULL,
                                                        null, null, processInformation);
        assertTrue("Process launch failed", status);
    }
}