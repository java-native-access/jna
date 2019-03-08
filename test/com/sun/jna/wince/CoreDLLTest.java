/* Copyright (c) 2011 Timothy Wall, All Rights Reserved
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
        CoreDLL INSTANCE = Native.load("coredll", CoreDLL.class, W32APIOptions.UNICODE_OPTIONS);

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