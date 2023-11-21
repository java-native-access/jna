/* Copyright (c) 2016 Minoru Sakamoto, All Rights Reserved
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

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.platform.win32.WinDef.BOOL;
import com.sun.jna.platform.win32.Winevt.EVT_CHANNEL_CONFIG_PROPERTY_ID;
import com.sun.jna.platform.win32.Winevt.EVT_HANDLE;
import com.sun.jna.ptr.IntByReference;
import junit.framework.TestCase;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * @author Minoru Sakamoto
 */
public class WevtapiTest extends TestCase {

    public void testEvtGetExtendedStatus() throws Exception {
        assertThat(_evtGetExtendedStatus(null).length(), is(0));
        assertThat(_evtGetExtendedStatus(">><").length() > 0, is(true)); // illegal query
    }

    private String _evtGetExtendedStatus(String query) {
        EVT_HANDLE handle = null;
        String result;
        try {
            handle = Wevtapi.INSTANCE.EvtQuery(null, "Application", query,
                    Winevt.EVT_QUERY_FLAGS.EvtQueryChannelPath);
            result = WevtapiUtil.EvtGetExtendedStatus();
        } finally {
            if (handle != null) {
                Wevtapi.INSTANCE.EvtClose(handle);
            }
        }
        return result;
    }

    public void testReadEvents() throws Exception {
        EVT_HANDLE queryHandle = null;
        EVT_HANDLE contextHandle = null;
        File testEvtx = new File(getClass().getResource("/res/WevtapiTest.sample1.evtx").toURI());
        StringBuilder sb = new StringBuilder();
        try {
            // test EvtQuery
            queryHandle = Wevtapi.INSTANCE.EvtQuery(null, testEvtx.getPath(), null,
                    Winevt.EVT_QUERY_FLAGS.EvtQueryFilePath);

            // test EvtCreateRenderContext
            String[] targets = {"Event/System/Provider/@Name", "Event/System/EventRecordID", "Event/System/EventID", "Event/EventData/Data", "Event/System/TimeCreated/@SystemTime"};
            contextHandle = Wevtapi.INSTANCE.EvtCreateRenderContext(targets.length, targets,
                    Winevt.EVT_RENDER_CONTEXT_FLAGS.EvtRenderContextValues);

            // test EvtNext
            int eventArraySize = 10;
            int evtNextTimeout = 1000;
            int arrayIndex = 0;
            EVT_HANDLE[] eventArray = new EVT_HANDLE[eventArraySize];
            IntByReference returned = new IntByReference();

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

            while (Wevtapi.INSTANCE.EvtNext(queryHandle, eventArraySize, eventArray, evtNextTimeout, 0, returned)) {

                // test EvtRender
                Memory buff;
                IntByReference propertyCount = new IntByReference();
                Winevt.EVT_VARIANT evtVariant = new Winevt.EVT_VARIANT();
                for (int i = 0; i < returned.getValue(); i++) {
                    buff = WevtapiUtil.EvtRender(contextHandle, eventArray[i], Winevt.EVT_RENDER_FLAGS.EvtRenderEventValues, propertyCount);
                    assertThat("PropertyCount", propertyCount.getValue(), is(5));
                    useMemory(evtVariant, buff, 0);
                    assertThat("Provider Name", (String) evtVariant.getValue(), is("testSource"));
                    sb.append((String) evtVariant.getValue());
                    useMemory(evtVariant, buff, 1);
                    assertThat("EventRecordID", (Long) evtVariant.getValue(), is((long) arrayIndex * eventArraySize + i + 1));
                    useMemory(evtVariant, buff, 2);
                    assertThat("EventID", (Short) evtVariant.getValue(), is((short) (5000 + (arrayIndex * eventArraySize + i + 1))));
                    useMemory(evtVariant, buff, 3);
                    String[] args = (String[]) evtVariant.getValue();
                    assertThat("Data#length", args.length, is(1));
                    assertThat("Data#value", args[0], is("testMessage" + (arrayIndex * eventArraySize + i + 1)));
                    useMemory(evtVariant, buff, 4);
                    Date systemtime = ((WinBase.FILETIME) evtVariant.getValue()).toDate();
                    assertThat("TimeCreated", dateFormat.format(systemtime), is("2016-08-17"));
                }
                arrayIndex++;
            }
            if (Kernel32.INSTANCE.GetLastError() != WinError.ERROR_SUCCESS &&
                    Kernel32.INSTANCE.GetLastError() != WinError.ERROR_NO_MORE_ITEMS) {
                throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
            }
            assertThat(sb.length() > 0, is(true));
        } finally {
            // test EvtClose
            if (queryHandle != null) {
                Wevtapi.INSTANCE.EvtClose(queryHandle);
            }
            if (contextHandle != null) {
                Wevtapi.INSTANCE.EvtClose(contextHandle);
            }
        }

        // =========== Test accessing binary data and empty value ================

        queryHandle = null;
        contextHandle = null;
        testEvtx = new File(getClass().getResource("/res/WevtapiTest.sample2.evtx").toURI());
        try {
            queryHandle = Wevtapi.INSTANCE.EvtQuery(null, testEvtx.getPath(), null,
                    Winevt.EVT_QUERY_FLAGS.EvtQueryFilePath);

            String[] targets = {"Event/EventData/Binary", "Event/System/Correlation"};
            contextHandle = Wevtapi.INSTANCE.EvtCreateRenderContext(targets.length, targets,
                    Winevt.EVT_RENDER_CONTEXT_FLAGS.EvtRenderContextValues);

            int read = 0;
            int eventArraySize = 1;
            int evtNextTimeout = 1000;
            EVT_HANDLE[] eventArray = new EVT_HANDLE[eventArraySize];
            IntByReference returned = new IntByReference();

            while (Wevtapi.INSTANCE.EvtNext(queryHandle, eventArraySize, eventArray, evtNextTimeout, 0, returned)) {
                Memory buff;
                IntByReference propertyCount = new IntByReference();
                Winevt.EVT_VARIANT evtVariant = new Winevt.EVT_VARIANT();
                for (int i = 0; i < returned.getValue(); i++) {
                    read++;
                    buff = WevtapiUtil.EvtRender(contextHandle, eventArray[i], Winevt.EVT_RENDER_FLAGS.EvtRenderEventValues, propertyCount);
                    assertThat("PropertyCount", propertyCount.getValue(), is(2));
                    useMemory(evtVariant, buff, 0);
                    assertThat("Binary", (byte[]) evtVariant.getValue(), is(new byte[]{(byte) 0xD9, (byte) 0x06, 0, 0}));
                    useMemory(evtVariant, buff, 1);
                    assertThat("Correlation", evtVariant.getValue(), nullValue());
                }
            }

            assertThat(read, is(1));
        } finally {
            // test EvtClose
            if (queryHandle != null) {
                Wevtapi.INSTANCE.EvtClose(queryHandle);
            }
            if (contextHandle != null) {
                Wevtapi.INSTANCE.EvtClose(contextHandle);
            }
        }

        // =========== Test accessing GUID + SID data ================

        queryHandle = null;
        contextHandle = null;
        testEvtx = new File(getClass().getResource("/res/WevtapiTest.sample3.evtx").toURI());
        try {
            queryHandle = Wevtapi.INSTANCE.EvtQuery(null, testEvtx.getPath(), null,
                    Winevt.EVT_QUERY_FLAGS.EvtQueryFilePath);

            String[] targets = {"Event/System/Security/@UserID", "Event/System/Provider/@Guid"};
            contextHandle = Wevtapi.INSTANCE.EvtCreateRenderContext(targets.length, targets,
                    Winevt.EVT_RENDER_CONTEXT_FLAGS.EvtRenderContextValues);

            int read = 0;
            int eventArraySize = 1;
            int evtNextTimeout = 1000;
            EVT_HANDLE[] eventArray = new EVT_HANDLE[eventArraySize];
            IntByReference returned = new IntByReference();

            while (Wevtapi.INSTANCE.EvtNext(queryHandle, eventArraySize, eventArray, evtNextTimeout, 0, returned)) {
                Memory buff;
                IntByReference propertyCount = new IntByReference();
                Winevt.EVT_VARIANT evtVariant = new Winevt.EVT_VARIANT();
                for (int i = 0; i < returned.getValue(); i++) {
                    read++;
                    buff = WevtapiUtil.EvtRender(contextHandle, eventArray[i], Winevt.EVT_RENDER_FLAGS.EvtRenderEventValues, propertyCount);
                    assertThat("PropertyCount", propertyCount.getValue(), is(2));
                    useMemory(evtVariant, buff, 0);
                    assertThat("Security#UserID", ((WinNT.PSID) evtVariant.getValue()).getSidString(), is("S-1-5-21-3178902164-3053647283-518304804-1001"));
                    useMemory(evtVariant, buff, 1);
                    assertThat("Provider#GUID", ((Guid.GUID) evtVariant.getValue()).toGuidString(), is("{B0AA8734-56F7-41CC-B2F4-DE228E98B946}"));
                }
            }

            assertThat(read, is(1));
        } finally {
            // test EvtClose
            if (queryHandle != null) {
                Wevtapi.INSTANCE.EvtClose(queryHandle);
            }
            if (contextHandle != null) {
                Wevtapi.INSTANCE.EvtClose(contextHandle);
            }
        }
    }

    private void useMemory(Winevt.EVT_VARIANT evtVariant, Memory buff, int index) {
        evtVariant.use(buff.share(evtVariant.size() * index));
        evtVariant.read();
    }


    public void testEvtOpenLog() throws Exception {
        File testEvtx = new File(getClass().getResource("/res/WevtapiTest.sample1.evtx").toURI());
        EVT_HANDLE logHandle = Wevtapi.INSTANCE.EvtOpenLog(null, testEvtx.getAbsolutePath(),
                Winevt.EVT_OPEN_LOG_FLAGS.EvtOpenFilePath);
        if (logHandle == null) {
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
        }
        Memory buff = new Memory(1024);
        IntByReference buffUsed = new IntByReference();
        if (!Wevtapi.INSTANCE.EvtGetLogInfo(logHandle, Winevt.EVT_LOG_PROPERTY_ID.EvtLogFileSize, (int) buff.size(), buff, buffUsed)) {
            if (Kernel32.INSTANCE.GetLastError() == WinError.ERROR_INSUFFICIENT_BUFFER) {
                buff = new Memory(buffUsed.getValue());
                if (!Wevtapi.INSTANCE.EvtGetLogInfo(logHandle, Winevt.EVT_LOG_PROPERTY_ID.EvtLogFileSize, (int) buff.size(), buff, buffUsed)) {
                    throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
                }
            } else {
                throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
            }
        }
        assertThat(buff.getLong(0), is(69632L));
    }


    public void testEvtOpenChannelEnum() throws Exception {

        EVT_HANDLE channelHandle = null;
        List<String> channelList = new ArrayList<>();
        try {
            channelHandle = Wevtapi.INSTANCE.EvtOpenChannelEnum(null, 0);
            if (channelHandle == null) {
                throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
            }

            char[] buff = new char[1024];
            IntByReference buffUsed = new IntByReference();
            while (true) {
                if (!Wevtapi.INSTANCE.EvtNextChannelPath(channelHandle, buff.length, buff, buffUsed)) {
                    if (Kernel32.INSTANCE.GetLastError() == WinError.ERROR_NO_MORE_ITEMS) {
                        break;
                    } else if (Kernel32.INSTANCE.GetLastError() == WinError.ERROR_INSUFFICIENT_BUFFER) {
                        buff = new char[buffUsed.getValue()];
                        if (!Wevtapi.INSTANCE.EvtNextChannelPath(channelHandle, buff.length, buff, buffUsed)) {
                            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
                        }
                    }
                }
                channelList.add(Native.toString(buff));
            }
            assertThat(channelList.size() > 0, is(true));

        } finally {
            if (channelHandle != null) {
                Wevtapi.INSTANCE.EvtClose(channelHandle);
            }
        }
    }

    public void testEvtOpenChannelConfig() throws Exception {
        EVT_HANDLE channelHandle = null;
        try {
            channelHandle = Wevtapi.INSTANCE.EvtOpenChannelConfig(null, "Application", 0);
            assertNotNull(channelHandle);
            Winevt.EVT_VARIANT evtVariant = WevtapiUtil.EvtGetChannelConfigProperty(channelHandle,
                    Winevt.EVT_CHANNEL_CONFIG_PROPERTY_ID.EvtChannelConfigClassicEventlog);
            assertThat(((WinDef.BOOL) evtVariant.getValue()).booleanValue(), is(true));
        } finally {
            if (channelHandle != null) {
                Wevtapi.INSTANCE.EvtClose(channelHandle);
            }
        }
    }

    public void testModifyChannelConfig() throws Exception {
        EVT_HANDLE channelHandle = null;
        try {
            channelHandle = Wevtapi.INSTANCE.EvtOpenChannelConfig(null, "Application", 0);
            assertNotNull(channelHandle);

            Winevt.EVT_VARIANT evtVariant = WevtapiUtil.EvtGetChannelConfigProperty(channelHandle, EVT_CHANNEL_CONFIG_PROPERTY_ID.EvtChannelConfigClassicEventlog);
            assertThat(((WinDef.BOOL) evtVariant.getValue()).booleanValue(), is(true));

            Winevt.EVT_VARIANT setter = new Winevt.EVT_VARIANT();
            setter.setValue(Winevt.EVT_VARIANT_TYPE.EvtVarTypeBoolean, new BOOL(false));
            Wevtapi.INSTANCE.EvtSetChannelConfigProperty(channelHandle, EVT_CHANNEL_CONFIG_PROPERTY_ID.EvtChannelConfigClassicEventlog, 0, setter);

            evtVariant = WevtapiUtil.EvtGetChannelConfigProperty(channelHandle, EVT_CHANNEL_CONFIG_PROPERTY_ID.EvtChannelConfigClassicEventlog);
            assertThat(((WinDef.BOOL) evtVariant.getValue()).booleanValue(), is(false));

            setter.setValue(Winevt.EVT_VARIANT_TYPE.EvtVarTypeBoolean, new BOOL(true));
            Wevtapi.INSTANCE.EvtSetChannelConfigProperty(channelHandle, EVT_CHANNEL_CONFIG_PROPERTY_ID.EvtChannelConfigClassicEventlog, 0, setter);

            evtVariant = WevtapiUtil.EvtGetChannelConfigProperty(channelHandle, EVT_CHANNEL_CONFIG_PROPERTY_ID.EvtChannelConfigClassicEventlog);
            assertThat(((WinDef.BOOL) evtVariant.getValue()).booleanValue(), is(true));

            // Writing back is skipped neighter is EvtChannelConfigClassicEventlog
            // writable, nor is it a good idea to mess with the log of the developer machine
        } finally {
            if (channelHandle != null) {
                Wevtapi.INSTANCE.EvtClose(channelHandle);
            }
        }
    }

    public void testEvtOpenPublisherEnum() throws Exception {
        Winevt.EVT_RPC_LOGIN login = new Winevt.EVT_RPC_LOGIN("localhost", null, null, null,
                Winevt.EVT_RPC_LOGIN_FLAGS.EvtRpcLoginAuthDefault);
        EVT_HANDLE session = null;
        EVT_HANDLE publisherEnumHandle = null;
        List<String> publisherList = new ArrayList<>();
        try {
            session = Wevtapi.INSTANCE.EvtOpenSession(Winevt.EVT_LOGIN_CLASS.EvtRpcLogin, login, 0, 0);
            if (session == null) {
                throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
            }
            publisherEnumHandle = Wevtapi.INSTANCE.EvtOpenPublisherEnum(session, 0);
            if (publisherEnumHandle == null) {
                throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
            }

            String providerName;
            while (true) {
                try {
                    providerName = WevtapiUtil.EvtNextPublisherId(publisherEnumHandle);
                } catch (Win32Exception e) {
                    if (e.getErrorCode() == WinError.ERROR_NO_MORE_ITEMS) {
                        break;
                    } else {
                        throw e;
                    }
                }
                publisherList.add(providerName);
            }
            assertThat(publisherList.size() > 0, is(true));
        } finally {
            if (publisherEnumHandle != null) {
                Wevtapi.INSTANCE.EvtClose(publisherEnumHandle);
            }

            if (session != null) {
                Wevtapi.INSTANCE.EvtClose(session);
            }

        }
    }

    public void testEvtGetQueryInfo() throws Exception {
        EVT_HANDLE queryHandle = null;
        try {
            queryHandle = Wevtapi.INSTANCE.EvtQuery(null, "Application", null,
                    Winevt.EVT_QUERY_FLAGS.EvtQueryChannelPath);

            Memory buff = new Memory(1024);
            IntByReference bufferUsed = new IntByReference();
            if (!Wevtapi.INSTANCE.EvtGetQueryInfo(queryHandle, Winevt.EVT_QUERY_PROPERTY_ID.EvtQueryNames, (int) buff.size(), buff, bufferUsed)) {
                if (Kernel32.INSTANCE.GetLastError() == WinError.ERROR_INSUFFICIENT_BUFFER) {
                    buff = new Memory(bufferUsed.getValue());
                    if (!Wevtapi.INSTANCE.EvtGetQueryInfo(queryHandle, Winevt.EVT_QUERY_PROPERTY_ID.EvtQueryNames, (int) buff.size(), buff, bufferUsed)) {
                        throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
                    }
                }
            }
            Winevt.EVT_VARIANT evtVariant = new Winevt.EVT_VARIANT(buff.share(0));
            evtVariant.readField("Type");
            StringBuilder sb = new StringBuilder();

            evtVariant.readField("Count");
            int count = evtVariant.Count;
            useMemory(evtVariant, buff, 0);
            String[] queryNames = (String[]) evtVariant.getValue();
            for (int i = 0; i < count; i++) {
                sb.append(queryNames[i]);
            }
            assertThat(sb.toString(), is("Application"));
        } finally {
            if (queryHandle != null) {
                Wevtapi.INSTANCE.EvtClose(queryHandle);
            }
        }
    }

    public void testEvtCreateBookmark() throws Exception {
        EVT_HANDLE queryHandle = null;
        EVT_HANDLE contextHandle = null;
        File testEvtx = new File(getClass().getResource("/res/WevtapiTest.sample1.evtx").toURI());
        StringBuilder sb = new StringBuilder();
        try {
            queryHandle = Wevtapi.INSTANCE.EvtQuery(null, testEvtx.getPath(), null,
                    Winevt.EVT_QUERY_FLAGS.EvtQueryFilePath);

            // test EvtCreateBookmark
            EVT_HANDLE hBookmark = Wevtapi.INSTANCE.EvtCreateBookmark(
                    "<BookmarkList><Bookmark Channel='" + testEvtx.getAbsolutePath() + "' RecordId='" + 11 + "' IsCurrent='true'/></BookmarkList>"
            );
            if (hBookmark == null) {
                throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
            }
            // test EvtSeek
            if (!Wevtapi.INSTANCE.EvtSeek(queryHandle, 0L, hBookmark, 0, Winevt.EVT_SEEK_FLAGS.EvtSeekRelativeToBookmark)) {
                throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
            }

            String[] targets = {"Event/System/EventRecordID"};
            contextHandle = Wevtapi.INSTANCE.EvtCreateRenderContext(targets.length, targets,
                    Winevt.EVT_RENDER_CONTEXT_FLAGS.EvtRenderContextValues);

            int eventArraySize = 10;
            int evtNextTimeout = 1000;
            int arrayIndex = 1;
            Memory buff;
            IntByReference propertyCount = new IntByReference();
            Winevt.EVT_VARIANT evtVariant = new Winevt.EVT_VARIANT();
            EVT_HANDLE[] eventArray = new EVT_HANDLE[eventArraySize];
            IntByReference returned = new IntByReference();
            while (Wevtapi.INSTANCE.EvtNext(queryHandle, eventArraySize, eventArray, evtNextTimeout, 0, returned)) {
                for (int i = 0; i < returned.getValue(); i++) {
                    EVT_HANDLE evtHandle = eventArray[i];
                    try {
                        buff = WevtapiUtil.EvtRender(contextHandle, eventArray[i], Winevt.EVT_RENDER_FLAGS.EvtRenderEventValues, propertyCount);
                        useMemory(evtVariant, buff, 0);
                        assertThat("EventRecordID", (Long) evtVariant.getValue(), is((long) arrayIndex * eventArraySize + i + 1));
                        sb.append(evtVariant.getValue());

                        // test EvtUpdateBookmark
                        if (!Wevtapi.INSTANCE.EvtUpdateBookmark(hBookmark, eventArray[i])) {
                            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
                        }
                    } finally {
                        if (eventArray[i] != null) {
                            Wevtapi.INSTANCE.EvtClose(eventArray[i]);
                        }
                    }
                }
                arrayIndex++;
            }
            if (Kernel32.INSTANCE.GetLastError() != WinError.ERROR_SUCCESS &&
                    Kernel32.INSTANCE.GetLastError() != WinError.ERROR_NO_MORE_ITEMS) {
                throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
            }
            buff = WevtapiUtil.EvtRender(null, hBookmark, Winevt.EVT_RENDER_FLAGS.EvtRenderBookmark, propertyCount);
            assertThat(buff.getWideString(0), is("<BookmarkList>\r\n  <Bookmark Channel='" + testEvtx.getAbsolutePath() +
                    "' RecordId='" + 20 + "' IsCurrent='true'/>\r\n</BookmarkList>"));
            assertThat(sb.length() > 0, is(true));
        } finally {
            if (queryHandle != null) {
                Wevtapi.INSTANCE.EvtClose(queryHandle);
            }
            if (contextHandle != null) {
                Wevtapi.INSTANCE.EvtClose(contextHandle);
            }
        }
    }

    public void testEvtGetEventInfo() throws Exception {
        EVT_HANDLE queryHandle = null;
        EVT_HANDLE contextHandle = null;
        File testEvtx = new File(getClass().getResource("/res/WevtapiTest.sample1.evtx").toURI());
        StringBuilder sb = new StringBuilder();
        try {
            queryHandle = Wevtapi.INSTANCE.EvtQuery(null, testEvtx.getPath(), null,
                    Winevt.EVT_QUERY_FLAGS.EvtQueryFilePath);


            String[] targets = {"Event/System/EventRecordID"};
            contextHandle = Wevtapi.INSTANCE.EvtCreateRenderContext(targets.length, targets,
                    Winevt.EVT_RENDER_CONTEXT_FLAGS.EvtRenderContextValues);

            int eventArraySize = 10;
            int evtNextTimeout = 1000;
            Memory buff = new Memory(1024);
            Winevt.EVT_VARIANT evtVariant = new Winevt.EVT_VARIANT();
            EVT_HANDLE[] eventArray = new EVT_HANDLE[eventArraySize];
            IntByReference buffUsed = new IntByReference();
            IntByReference returned = new IntByReference();
            while (Wevtapi.INSTANCE.EvtNext(queryHandle, eventArraySize, eventArray, evtNextTimeout, 0, returned)) {
                for (int i = 0; i < returned.getValue(); i++) {
                    try {
                        if (!Wevtapi.INSTANCE.EvtGetEventInfo(eventArray[i],
                                Winevt.EVT_EVENT_PROPERTY_ID.EvtEventPath, (int) buff.size(), buff, buffUsed)) {
                            if (Kernel32.INSTANCE.GetLastError() == WinError.ERROR_INSUFFICIENT_BUFFER) {
                                buff = new Memory(buffUsed.getValue());
                                if (!Wevtapi.INSTANCE.EvtGetEventInfo(eventArray[i],
                                        Winevt.EVT_EVENT_PROPERTY_ID.EvtEventPath, (int) buff.size(), buff, buffUsed)) {
                                    throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
                                }
                            }
                        }
                        useMemory(evtVariant, buff, 0);
                        assertThat("Evtx Path", (String) evtVariant.getValue(), is(testEvtx.getAbsolutePath()));
                        sb.append((String) evtVariant.getValue());
                    } finally {
                        if (eventArray[i] != null) {
                            Wevtapi.INSTANCE.EvtClose(eventArray[i]);
                        }
                    }

                }

            }
            if (Kernel32.INSTANCE.GetLastError() != WinError.ERROR_SUCCESS &&
                    Kernel32.INSTANCE.GetLastError() != WinError.ERROR_NO_MORE_ITEMS) {
                throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
            }
            assertThat(sb.length() > 0, is(true));
        } finally {
            if (queryHandle != null) {
                Wevtapi.INSTANCE.EvtClose(queryHandle);
            }
            if (contextHandle != null) {
                Wevtapi.INSTANCE.EvtClose(contextHandle);
            }
        }
    }

    public void testEvtVariantType() throws Exception {
        assertThat(Winevt.EVT_VARIANT_TYPE.EvtVarTypeNull.getField(), is(""));
        assertThat(Winevt.EVT_VARIANT_TYPE.EvtVarTypeString.getField(), is("StringVal"));
        assertThat(Winevt.EVT_VARIANT_TYPE.EvtVarTypeAnsiString.getField(), is("AnsiStringVal"));
        assertThat(Winevt.EVT_VARIANT_TYPE.EvtVarTypeSByte.getField(), is("SByteVal"));
        assertThat(Winevt.EVT_VARIANT_TYPE.EvtVarTypeByte.getField(), is("ByteVal"));
        assertThat(Winevt.EVT_VARIANT_TYPE.EvtVarTypeInt16.getField(), is("Int16Val"));
        assertThat(Winevt.EVT_VARIANT_TYPE.EvtVarTypeUInt16.getField(), is("UInt16Val"));
        assertThat(Winevt.EVT_VARIANT_TYPE.EvtVarTypeInt32.getField(), is("Int32Val"));
        assertThat(Winevt.EVT_VARIANT_TYPE.EvtVarTypeUInt32.getField(), is("UInt32Val"));
        assertThat(Winevt.EVT_VARIANT_TYPE.EvtVarTypeInt64.getField(), is("Int64Val"));
        assertThat(Winevt.EVT_VARIANT_TYPE.EvtVarTypeUInt64.getField(), is("UInt64Val"));
        assertThat(Winevt.EVT_VARIANT_TYPE.EvtVarTypeSingle.getField(), is("SingleVal"));
        assertThat(Winevt.EVT_VARIANT_TYPE.EvtVarTypeDouble.getField(), is("DoubleVal"));
        assertThat(Winevt.EVT_VARIANT_TYPE.EvtVarTypeBoolean.getField(), is("BooleanVal"));
        assertThat(Winevt.EVT_VARIANT_TYPE.EvtVarTypeBinary.getField(), is("BinaryVal"));
        assertThat(Winevt.EVT_VARIANT_TYPE.EvtVarTypeGuid.getField(), is("GuidVal"));
        assertThat(Winevt.EVT_VARIANT_TYPE.EvtVarTypeSizeT.getField(), is("SizeTVal"));
        assertThat(Winevt.EVT_VARIANT_TYPE.EvtVarTypeFileTime.getField(), is("FileTimeVal"));
        assertThat(Winevt.EVT_VARIANT_TYPE.EvtVarTypeSysTime.getField(), is("SysTimeVal"));
        assertThat(Winevt.EVT_VARIANT_TYPE.EvtVarTypeSid.getField(), is("SidVal"));
        assertThat(Winevt.EVT_VARIANT_TYPE.EvtVarTypeHexInt32.getField(), is("Int32Val"));
        assertThat(Winevt.EVT_VARIANT_TYPE.EvtVarTypeHexInt64.getField(), is("Int64Val"));
        assertThat(Winevt.EVT_VARIANT_TYPE.EvtVarTypeEvtHandle.getField(), is("EvtHandleVal"));
        assertThat(Winevt.EVT_VARIANT_TYPE.EvtVarTypeEvtXml.getField(), is("XmlVal"));

        assertThat(Winevt.EVT_VARIANT_TYPE.EvtVarTypeNull.getArrField(), is(""));
        assertThat(Winevt.EVT_VARIANT_TYPE.EvtVarTypeString.getArrField(), is("StringArr"));
        assertThat(Winevt.EVT_VARIANT_TYPE.EvtVarTypeAnsiString.getArrField(), is("AnsiStringArr"));
        assertThat(Winevt.EVT_VARIANT_TYPE.EvtVarTypeSByte.getArrField(), is("SByteArr"));
        assertThat(Winevt.EVT_VARIANT_TYPE.EvtVarTypeByte.getArrField(), is("ByteArr"));
        assertThat(Winevt.EVT_VARIANT_TYPE.EvtVarTypeInt16.getArrField(), is("Int16Arr"));
        assertThat(Winevt.EVT_VARIANT_TYPE.EvtVarTypeUInt16.getArrField(), is("UInt16Arr"));
        assertThat(Winevt.EVT_VARIANT_TYPE.EvtVarTypeInt32.getArrField(), is("Int32Arr"));
        assertThat(Winevt.EVT_VARIANT_TYPE.EvtVarTypeUInt32.getArrField(), is("UInt32Arr"));
        assertThat(Winevt.EVT_VARIANT_TYPE.EvtVarTypeInt64.getArrField(), is("Int64Arr"));
        assertThat(Winevt.EVT_VARIANT_TYPE.EvtVarTypeUInt64.getArrField(), is("UInt64Arr"));
        assertThat(Winevt.EVT_VARIANT_TYPE.EvtVarTypeSingle.getArrField(), is("SingleArr"));
        assertThat(Winevt.EVT_VARIANT_TYPE.EvtVarTypeDouble.getArrField(), is("DoubleArr"));
        assertThat(Winevt.EVT_VARIANT_TYPE.EvtVarTypeBoolean.getArrField(), is("BooleanArr"));
        assertThat(Winevt.EVT_VARIANT_TYPE.EvtVarTypeBinary.getArrField(), is("BinaryArr"));
        assertThat(Winevt.EVT_VARIANT_TYPE.EvtVarTypeGuid.getArrField(), is("GuidArr"));
        assertThat(Winevt.EVT_VARIANT_TYPE.EvtVarTypeSizeT.getArrField(), is("SizeTArr"));
        assertThat(Winevt.EVT_VARIANT_TYPE.EvtVarTypeFileTime.getArrField(), is("FileTimeArr"));
        assertThat(Winevt.EVT_VARIANT_TYPE.EvtVarTypeSysTime.getArrField(), is("SysTimeArr"));
        assertThat(Winevt.EVT_VARIANT_TYPE.EvtVarTypeSid.getArrField(), is("SidArr"));
        assertThat(Winevt.EVT_VARIANT_TYPE.EvtVarTypeHexInt32.getArrField(), is("Int32Arr"));
        assertThat(Winevt.EVT_VARIANT_TYPE.EvtVarTypeHexInt64.getArrField(), is("Int64Arr"));
        assertThat(Winevt.EVT_VARIANT_TYPE.EvtVarTypeEvtHandle.getArrField(), is("EvtHandleArr"));
        assertThat(Winevt.EVT_VARIANT_TYPE.EvtVarTypeEvtXml.getArrField(), is("XmlArr"));

    }
}