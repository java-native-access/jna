/* Copyright (c) 2012 Tobias Wolf, All Rights Reserved
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

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.Structure.FieldOrder;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.platform.win32.WinNT.LARGE_INTEGER;
import com.sun.jna.platform.win32.WinNT.PSID;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;
import com.sun.jna.win32.W32APITypeMapper;

public interface Wtsapi32 extends StdCallLibrary {

    Wtsapi32 INSTANCE = Native.load("Wtsapi32", Wtsapi32.class, W32APIOptions.DEFAULT_OPTIONS);

    int NOTIFY_FOR_ALL_SESSIONS = 1;

    int NOTIFY_FOR_THIS_SESSION = 0;

    /**
     * The session identified by lParam was connected to the console terminal or
     * RemoteFX session.
     */
    int WTS_CONSOLE_CONNECT = 0x1;

    /**
     * The session identified by lParam was disconnected from the console
     * terminal or RemoteFX session.
     */
    int WTS_CONSOLE_DISCONNECT = 0x2;

    /**
     * The session identified by lParam was connected to the remote terminal.
     */
    int WTS_REMOTE_CONNECT = 0x3;

    /**
     * The session identified by lParam was disconnected from the remote
     * terminal.
     */
    int WTS_REMOTE_DISCONNECT = 0x4;

    /**
     * A user has logged on to the session identified by lParam.
     */
    int WTS_SESSION_LOGON = 0x5;

    /**
     * A user has logged off the session identified by lParam.
     */
    int WTS_SESSION_LOGOFF = 0x6;

    /**
     * The session identified by lParam has been locked.
     */
    int WTS_SESSION_LOCK = 0x7;

    /**
     * The session identified by lParam has been unlocked.
     */
    int WTS_SESSION_UNLOCK = 0x8;

    /**
     * The session identified by lParam has changed its remote controlled
     * status. To determine the status, call GetSystemMetrics and check the
     * SM_REMOTECONTROL metric.
     */
    int WTS_SESSION_REMOTE_CONTROL = 0x9;

    /**
     * Specifies the current server
     */
    HANDLE WTS_CURRENT_SERVER_HANDLE = new HANDLE(null);

    /**
     * Specifies the current session (SessionId)
     */
    int WTS_CURRENT_SESSION = -1;

    /**
     * Specifies any-session (SessionId)
     */
    int WTS_ANY_SESSION = -2;

    int WTS_PROCESS_INFO_LEVEL_0 = 0;
    int WTS_PROCESS_INFO_LEVEL_1 = 1;

    /**
     * Defined in {@code winsta.h} and present in this interface to properly size
     * the {@link WTSINFO} structure.
     */
    int DOMAIN_LENGTH = 17;

    /**
     * Defined in {@code winsta.h} and present in this interface to properly size
     * the {@link WTSINFO} structure.
     */
    int USERNAME_LENGTH = 20;

    /**
     * Defined in {@code winsta.h} and present in this interface to properly size
     * the {@link WTSINFO} structure.
     */
    int WINSTATIONNAME_LENGTH = 32;

    /**
     * Specifies the connection state of a Remote Desktop Services session.
     */
    public interface WTS_CONNECTSTATE_CLASS {
        int WTSActive = 0;
        int WTSConnected = 1;
        int WTSConnectQuery = 2;
        int WTSShadow = 3;
        int WTSDisconnected = 4;
        int WTSIdle = 5;
        int WTSListen = 6;
        int WTSReset = 7;
        int WTSDown = 8;
        int WTSInit = 9;
    }

    /**
     * Contains values that indicate the type of session information to retrieve in
     * a call to the {@link #WTSQuerySessionInformation()} function.
     */
    public interface WTS_INFO_CLASS {
        int WTSInitialProgram = 0;
        int WTSApplicationName = 1;
        int WTSWorkingDirectory = 2;
        int WTSOEMId = 3;
        int WTSSessionId = 4;
        int WTSUserName = 5;
        int WTSWinStationName = 6;
        int WTSDomainName = 7;
        int WTSConnectState = 8;
        int WTSClientBuildNumber = 9;
        int WTSClientName = 10;
        int WTSClientDirectory = 11;
        int WTSClientProductId = 12;
        int WTSClientHardwareId = 13;
        int WTSClientAddress = 14;
        int WTSClientDisplay = 15;
        int WTSClientProtocolType = 16;
        int WTSIdleTime = 17;
        int WTSLogonTime = 18;
        int WTSIncomingBytes = 19;
        int WTSOutgoingBytes = 20;
        int WTSIncomingFrames = 21;
        int WTSOutgoingFrames = 22;
        int WTSClientInfo = 23;
        int WTSSessionInfo = 24;
        int WTSSessionInfoEx = 25;
        int WTSConfigInfo = 26;
        int WTSValidationInfo = 27;
        int WTSSessionAddressV4 = 28;
        int WTSIsRemoteSession = 29;
    }

    /**
     * Contains information about a client session on a Remote Desktop Session Host
     * (RD Session Host) server.
     */
    @FieldOrder({ "SessionId", "pWinStationName", "State" })
    class WTS_SESSION_INFO extends Structure {
        public int SessionId;
        public String pWinStationName; // either LPSTR or LPWSTR
        public int State; // WTS_CONNECTSTATE_CLASS

        public WTS_SESSION_INFO() {
            super(W32APITypeMapper.DEFAULT);
        }

        public WTS_SESSION_INFO(Pointer p) {
            super(p, Structure.ALIGN_DEFAULT, W32APITypeMapper.DEFAULT);
            read();
        }
    }

    /**
     * Contains the client network address of a Remote Desktop Services session.
     */
    @FieldOrder({ "AddressFamily", "Address" })
    class WTS_CLIENT_ADDRESS extends Structure {
        public int AddressFamily;
        public byte[] Address = new byte[20];

        public WTS_CLIENT_ADDRESS() {
            super();
        }

        public WTS_CLIENT_ADDRESS(Pointer p) {
            super(p);
            read();
        }
    }

    /**
     * Contains information about a Remote Desktop Services session.
     */
    @FieldOrder({ "State", "SessionId", "IncomingBytes", "OutgoingBytes", "IncomingFrames", "OutgoingFrames",
            "IncomingCompressedBytes", "OutgoingCompressedBytes", "WinStationName", "Domain", "UserName", "ConnectTime",
            "DisconnectTime", "LastInputTime", "LogonTime", "CurrentTime" })
    class WTSINFO extends Structure {
        private static final int CHAR_WIDTH = Boolean.getBoolean("w32.ascii") ? 1 : 2;

        public int State; // WTS_CONNECTSTATE_CLASS
        public int SessionId;
        public int IncomingBytes;
        public int OutgoingBytes;
        public int IncomingFrames;
        public int OutgoingFrames;
        public int IncomingCompressedBytes;
        public int OutgoingCompressedBytes;
        public final byte[] WinStationName = new byte[WINSTATIONNAME_LENGTH * CHAR_WIDTH];
        public final byte[] Domain = new byte[DOMAIN_LENGTH * CHAR_WIDTH];
        public final byte[] UserName = new byte[(USERNAME_LENGTH + 1) * CHAR_WIDTH];
        public LARGE_INTEGER ConnectTime;
        public LARGE_INTEGER DisconnectTime;
        public LARGE_INTEGER LastInputTime;
        public LARGE_INTEGER LogonTime;
        public LARGE_INTEGER CurrentTime;

        public WTSINFO() {
            super();
        }

        public WTSINFO(Pointer p) {
            super(p);
            read();
        }

        /**
         * Convenience method to return the null-terminated string in the
         * {@link #WinStationName} member, accounting for {@code CHAR} or {@code WCHAR}
         * byte width.
         *
         * @return The {@code WinStationName} as a string.
         */
        public String getWinStationName() {
            return getStringAtOffset(fieldOffset("WinStationName"));
        }

        /**
         * Convenience method to return the null-terminated string in the
         * {@link #Domain} member, accounting for {@code CHAR} or {@code WCHAR} byte
         * width.
         *
         * @return The {@code Domain} as a string.
         */
        public String getDomain() {
            return getStringAtOffset(fieldOffset("Domain"));
        }

        /**
         * Convenience method to return the null-terminated string in the
         * {@link #UserName} member, accounting for {@code CHAR} or {@code WCHAR} byte
         * width.
         *
         * @return The {@code UserName} as a string.
         */
        public String getUserName() {
            return getStringAtOffset(fieldOffset("UserName"));
        }

        private String getStringAtOffset(int offset) {
            return CHAR_WIDTH == 1 ? getPointer().getString(offset) : getPointer().getWideString(offset);
        }
    }

    /**
     * Contains extended information about a process running on a Remote Desktop
     * Session Host (RD Session Host) server. This structure is returned by the
     * WTSEnumerateProcessesEx function when you set the pLevel parameter to one.
     *
     * @see <A HREF=
     *      "https://docs.microsoft.com/en-us/windows/desktop/api/wtsapi32/ns-wtsapi32-_wts_process_info_exa">WTS_PROCESS_INFO_EXA</A>
     * @see <A HREF=
     *      "https://docs.microsoft.com/en-us/windows/desktop/api/wtsapi32/ns-wtsapi32-_wts_process_info_exw">WTS_PROCESS_INFO_EXW</A>
     */
    @FieldOrder({ "SessionId", "ProcessId", "pProcessName", "pUserSid", "NumberOfThreads", "HandleCount",
            "PagefileUsage", "PeakPagefileUsage", "WorkingSetSize", "PeakWorkingSetSize", "UserTime", "KernelTime" })
    class WTS_PROCESS_INFO_EX extends Structure {
        public int SessionId;
        public int ProcessId;
        public String pProcessName; // Either LPSTR or LPWSTR
        public PSID pUserSid;
        public int NumberOfThreads;
        public int HandleCount;
        public int PagefileUsage;
        public int PeakPagefileUsage;
        public int WorkingSetSize;
        public int PeakWorkingSetSize;
        public LARGE_INTEGER UserTime;
        public LARGE_INTEGER KernelTime;

        public WTS_PROCESS_INFO_EX() {
            super(W32APITypeMapper.DEFAULT);
        }

        public WTS_PROCESS_INFO_EX(Pointer p) {
            super(p, Structure.ALIGN_DEFAULT, W32APITypeMapper.DEFAULT);
            read();
        }
    }

    /**
     * Retrieves a list of sessions on a Remote Desktop Session Host (RD Session
     * Host) server.
     *
     * @param hServer
     *            A handle to the RD Session Host server.
     *            <p>
     *            You can use the {@code WTSOpenServer} or {@code WTSOpenServerEx}
     *            functions to retrieve a handle to a specific server, or
     *            {@link #WTS_CURRENT_SERVER_HANDLE} to use the RD Session Host
     *            server that hosts your application.
     * @param Reserved
     *            This parameter is reserved. It must be zero.
     * @param Version
     *            The version of the enumeration request. This parameter must be 1.
     * @param ppSessionInfo
     *            A pointer to an array of {@link WTS_SESSION_INFO} structures that
     *            represent the retrieved sessions. To free the returned buffer,
     *            call the {@link Wtsapi32#WTSFreeMemory} function.
     * @param pCount
     *            A pointer to the number of {@code WTS_SESSION_INFO} structures
     *            returned in the {@code ppSessionInfo} parameter.
     * @return Returns {@code false} if this function fails. If this function
     *         succeeds, returns {@code true}.
     *         <p>
     *         To get extended error information, call
     *         {@link Kernel32#GetLastError()}.
     */
    boolean WTSEnumerateSessions(HANDLE hServer, int Reserved, int Version, PointerByReference ppSessionInfo,
            IntByReference pCount);

    /**
     * Retrieves session information for the specified session on the specified
     * Remote Desktop Session Host (RD Session Host) server. It can be used to query
     * session information on local and remote RD Session Host servers.
     *
     * @param hServer
     *            A handle to an RD Session Host server. Specify a handle opened by
     *            the {@code WTSOpenServer} function, or specify
     *            {@link #WTS_CURRENT_SERVER_HANDLE} to indicate the RD Session Host
     *            server on which your application is running.
     * @param SessionId
     *            A Remote Desktop Services session identifier. To indicate the
     *            session in which the calling application is running (or the
     *            current session) specify {@link #WTS_CURRENT_SESSION}. Only
     *            specify {@code WTS_CURRENT_SESSION} when obtaining session
     *            information on the local server. If {@code WTS_CURRENT_SESSION} is
     *            specified when querying session information on a remote server,
     *            the returned session information will be inconsistent. Do not use
     *            the returned data.
     *            <p>
     *            You can use the {@code WTSEnumerateSessionsEx} function to
     *            retrieve the identifiers of all sessions on a specified RD Session
     *            Host server.
     *            <p>
     *            To query information for another user's session, you must have
     *            Query Information permission.
     * @param WTSInfoClass
     *            A value of the {@link WTS_INFO_CLASS} enumeration that indicates
     *            the type of session information to retrieve in a call to the
     *            {@code WTSQuerySessionInformation} function.
     * @param ppBuffer
     *            A pointer to a variable that receives a pointer to the requested
     *            information. The format and contents of the data depend on the
     *            information class specified in the {@code WTSInfoClass} parameter.
     *            To free the returned buffer, call the {@link #WTSFreeMemory}
     *            function.
     * @param pBytesReturned
     *            A pointer to a variable that receives the size, in bytes, of the
     *            data returned in ppBuffer.
     * @return If the function succeeds, returns {@code true}.
     *         <p>
     *         If the function fails, returns {@code false}. To get extended error
     *         information, call {@link Kernel32#GetLastError()}.
     */
    boolean WTSQuerySessionInformation(HANDLE hServer, int SessionId, int WTSInfoClass, PointerByReference ppBuffer,
            IntByReference pBytesReturned);

    /**
     * Frees memory allocated by a Remote Desktop Services function.
     *
     * @param pMemory
     *            Pointer to the memory to free.
     */
    void WTSFreeMemory(Pointer pMemory);

    /**
     * Registers the specified window to receive session change notifications.
     *
     * @param hWnd
     *            [in] Handle of the window to receive session change
     *            notifications.
     *
     * @param dwFlags
     *            [in] Specifies which session notifications are to be received.
     *            This parameter can be one of the following values.
     *
     * @return If the function succeeds, the return value is TRUE. Otherwise, it
     *         is FALSE. To get extended error information, call GetLastError.
     */
    boolean WTSRegisterSessionNotification(HWND hWnd, int dwFlags);

    /**
     * Unregisters the specified window so that it receives no further session
     * change notifications.
     *
     * @param hWnd
     *            [in] Handle of the window to be unregistered from receiving
     *            session notifications.
     *
     * @return If the function succeeds, the return value is TRUE. Otherwise, it
     *         is FALSE. To get extended error information, call GetLastError.
     */
    boolean WTSUnRegisterSessionNotification(HWND hWnd);

    /**
     * Retrieves information about the active processes on the specified Remote
     * Desktop Session Host (RD Session Host) server or Remote Desktop
     * Virtualization Host (RD Virtualization Host) server.
     *
     * @param hServer
     *            A handle to an RD Session Host server. Specify a handle opened
     *            by the WTSOpenServer function, or specify
     *            WTS_CURRENT_SERVER_HANDLE to indicate the server on which your
     *            application is running.
     * @param pLevel
     *            A pointer to a DWORD variable that, on input, specifies the
     *            type of information to return. To return an array of
     *            WTS_PROCESS_INFO structures, specify zero. To return an array
     *            of WTS_PROCESS_INFO_EX structures, specify one. If you do not
     *            specify a valid value for this parameter, on output,
     *            WTSEnumerateProcessesEx sets this parameter to one and returns
     *            an error. Otherwise, on output, WTSEnumerateProcessesEx does
     *            not change the value of this parameter.
     * @param SessionID
     *            The session for which to enumerate processes. To enumerate
     *            processes for all sessions on the server, specify
     *            WTS_ANY_SESSION.
     * @param ppProcessInfo
     *            A pointer to a variable that receives a pointer to an array of
     *            WTS_PROCESS_INFO or WTS_PROCESS_INFO_EX structures. The type
     *            of structure is determined by the value passed to the pLevel
     *            parameter. Each structure in the array contains information
     *            about an active process. When you have finished using the
     *            array, free it by calling the WTSFreeMemoryEx function. You
     *            should also set the pointer to NULL.
     * @param pCount
     *            A pointer to a variable that receives the number of structures
     *            returned in the buffer referenced by the ppProcessInfo
     *            parameter.
     * @return If the function succeeds, the return value is a nonzero value. If
     *         the function fails, the return value is zero. To get extended
     *         error information, call the GetLastError function.
     */
    boolean WTSEnumerateProcessesEx(HANDLE hServer, IntByReference pLevel, int SessionID,
            PointerByReference ppProcessInfo, IntByReference pCount);

    /**
     * Frees memory that contains WTS_PROCESS_INFO_EX or WTS_SESSION_INFO_1
     * structures allocated by a Remote Desktop Services function.
     *
     * @param WTSTypeClass
     *            A value of the WTS_TYPE_CLASS enumeration type that specifies
     *            the type of structures contained in the buffer referenced by
     *            the pMemory parameter.
     * @param pMemory
     *            A pointer to the buffer to free.
     * @param NumberOfEntries
     *            The number of elements in the buffer referenced by the pMemory
     *            parameter.
     * @return If the function succeeds, the return value is a nonzero value. If
     *         the function fails, the return value is zero. To get extended
     *         error information, call the GetLastError function.
     */
    boolean WTSFreeMemoryEx(int WTSTypeClass, Pointer pMemory, int NumberOfEntries);
}
