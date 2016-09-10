package com.sun.jna.platform.win32;

import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import com.sun.jna.StringArray;
import com.sun.jna.ptr.IntByReference;
import junit.framework.TestCase;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.sun.jna.Native.POINTER_SIZE;
import static com.sun.jna.platform.win32.Winevt.EVT_VARIANT_TYPE_ARRAY;
import static com.sun.jna.platform.win32.Winevt.EVT_VARIANT_TYPE_MASK;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * @author Minoru Sakamoto
 */
public class WevtapiTest extends TestCase {

    public void testEvtGetExtendedStatus() throws Exception {
        Memory buffer = new Memory(1024);
        IntByReference bufferUsed = new IntByReference();

        assertThat(_evtGetExtendedStatus(null, buffer, bufferUsed), is(0));
        assertThat(bufferUsed.getValue(), is(0));

        assertThat(_evtGetExtendedStatus(">><", buffer, bufferUsed), is(0));
        assertThat(bufferUsed.getValue() > 0, is((true)));

    }

    private int _evtGetExtendedStatus(String query, Memory buffer, IntByReference bufferUsed) {
        WinNT.HANDLE handle = null;
        int ret;
        try {
            handle = Wevtapi.INSTANCE.EvtQuery(null, "Application", query,
                    Winevt.EVT_QUERY_FLAGS.EvtQueryChannelPath);
            ret = Wevtapi.INSTANCE.EvtGetExtendedStatus((int) buffer.size(), buffer, bufferUsed);
            if (ret == WinError.ERROR_INSUFFICIENT_BUFFER) {
                buffer = new Memory(bufferUsed.getValue());
                ret = Wevtapi.INSTANCE.EvtGetExtendedStatus((int) buffer.size(), buffer, bufferUsed);
            }
            if (ret != WinError.ERROR_SUCCESS) {
                throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
            }
        } finally {
            if (handle != null) {
                Wevtapi.INSTANCE.EvtClose(handle);
            }
        }
        return ret;
    }

    public void testReadEvents() throws Exception {
        WinNT.HANDLE queryHandle = null;
        WinNT.HANDLE contextHandle = null;
        File testEvtx = new File(getClass().getResource("/res/test.evtx").toURI());
        StringBuilder sb = new StringBuilder();
        try {
            // test EvtQuery
            queryHandle = Wevtapi.INSTANCE.EvtQuery(null, testEvtx.getPath(), null,
                    Winevt.EVT_QUERY_FLAGS.EvtQueryFilePath);

            // test EvtCreateRenderContext
            String[] targets = {"Event/System/Provider/@Name", "Event/System/EventRecordID", "Event/System/EventID"};
            StringArray array = new StringArray(targets, true);
            contextHandle = Wevtapi.INSTANCE.EvtCreateRenderContext(targets.length, array,
                    Winevt.EVT_RENDER_CONTEXT_FLAGS.EvtRenderContextValues);

            // test EvtNext
            int eventArraySize = 10;
            int evtNextTimeout = 1000;
            int arrayIndex = 0;
            Memory eventArray = new Memory(POINTER_SIZE * eventArraySize);
            WinNT.HANDLEByReference evtHandle = new WinNT.HANDLEByReference();
            IntByReference returned = new IntByReference();
            while (Wevtapi.INSTANCE.EvtNext(queryHandle, eventArraySize, eventArray, evtNextTimeout, 0, returned)) {

                // test EvtRender
                Memory buff = new Memory(1024);
                IntByReference propertyCount = new IntByReference();
                Winevt.EVT_VARIANT evtVariant = new Winevt.EVT_VARIANT();
                for (int i = 0; i < returned.getValue(); i++) {
                    try {
                        evtHandle.setPointer(eventArray.share(i * POINTER_SIZE));
                        buff = evtRender(buff, contextHandle, evtHandle.getValue(),
                                Winevt.EVT_RENDER_FLAGS.EvtRenderEventValues, propertyCount);
                        useMemory(evtVariant, buff, 0);
                        assertThat("Provider Name", evtVariant.field1.StringVal, is("testSource"));
                        sb.append(evtVariant.field1.StringVal);
                        useMemory(evtVariant, buff, 1);
                        assertThat("EventRecordID", evtVariant.field1.UInt64Val, is((long) arrayIndex * eventArraySize + i + 1));
                        useMemory(evtVariant, buff, 2);
                        assertThat("EventID", evtVariant.field1.UInt64Val, is((long) arrayIndex * eventArraySize + i + 1));
                    } finally {
                        if (evtHandle.getValue() != null) {
                            Wevtapi.INSTANCE.EvtClose(evtHandle.getValue());
                        }
                    }
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
    }

    private Memory evtRender(Memory buff, WinNT.HANDLE contextHandle, WinNT.HANDLE evtHandle, int flag, IntByReference propertyCount) {
        buff.clear();
        IntByReference dwBufferUsed = new IntByReference();
        if (!Wevtapi.INSTANCE.EvtRender(contextHandle, evtHandle, flag, (int) buff.size(), buff, dwBufferUsed, propertyCount)) {
            if (Kernel32.INSTANCE.GetLastError() == WinError.ERROR_INSUFFICIENT_BUFFER) {
                buff = new Memory(dwBufferUsed.getValue());
                if (!Wevtapi.INSTANCE.EvtRender(contextHandle, evtHandle, flag, (int) buff.size(), buff, dwBufferUsed, propertyCount)) {
                    throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
                }
            } else {
                throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
            }
        }
        return buff;
    }

    private void useMemory(Winevt.EVT_VARIANT evtVariant, Memory buff, int index) {
        evtVariant.use(buff.share(evtVariant.size() * index));
        evtVariant.readField("Type");
        int typeIdx = evtVariant.Type;

        boolean isArray = (typeIdx & EVT_VARIANT_TYPE_ARRAY) == EVT_VARIANT_TYPE_ARRAY;
        int baseTypeIdx = typeIdx & EVT_VARIANT_TYPE_MASK;

        System.out.println("===== " + baseTypeIdx + " ======");

        Winevt.EVT_VARIANT_TYPE type = Winevt.EVT_VARIANT_TYPE.values()[baseTypeIdx];

        evtVariant.field1.use(buff.share(evtVariant.size() * index));
        evtVariant.field1.readField(isArray ? type.getArrField() : type.getField());
    }

    public void testEvtOpenLog() throws Exception {
        File testEvtx = new File(getClass().getResource("/res/test.evtx").toURI());
        WinNT.HANDLE logHandle = Wevtapi.INSTANCE.EvtOpenLog(null, testEvtx.getAbsolutePath(),
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

        WinNT.HANDLE channelHandle = null;
        List<String> channelList = new ArrayList<String>();
        try {
            channelHandle = Wevtapi.INSTANCE.EvtOpenChannelEnum(null, 0);
            if (channelHandle == null) {
                throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
            }

            Memory buff = new Memory(1024);
            IntByReference buffUsed = new IntByReference();
            while (true) {
                buff.clear();
                if (!Wevtapi.INSTANCE.EvtNextChannelPath(channelHandle, (int) buff.size(), buff, buffUsed)) {
                    if (Kernel32.INSTANCE.GetLastError() == WinError.ERROR_NO_MORE_ITEMS) {
                        break;
                    } else if (Kernel32.INSTANCE.GetLastError() == WinError.ERROR_INSUFFICIENT_BUFFER) {
                        buff = new Memory(buffUsed.getValue());
                        if (!Wevtapi.INSTANCE.EvtNextChannelPath(channelHandle, (int) buff.size(), buff, buffUsed)) {
                            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
                        }
                    }
                }
                channelList.add(buff.getWideString(0));
            }
            assertThat(channelList.size() > 0, is(true));

        } finally {
            if (channelHandle != null) {
                Wevtapi.INSTANCE.EvtClose(channelHandle);
            }
        }
    }

    public void testEvtOpenChannelConfig() throws Exception {
        WinNT.HANDLE channelHandle = null;
        try {
            channelHandle = Wevtapi.INSTANCE.EvtOpenChannelConfig(null, "Application", 0);
            if (channelHandle == null) {
                throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
            }

            Memory buff = new Memory(1024);
            IntByReference buffUsed = new IntByReference();
            if (!Wevtapi.INSTANCE.EvtGetChannelConfigProperty(channelHandle, Winevt.EVT_CHANNEL_CONFIG_PROPERTY_ID.EvtChannelConfigClassicEventlog,
                    0, (int) buff.size(), buff, buffUsed)) {
                if (Kernel32.INSTANCE.GetLastError() == WinError.ERROR_INSUFFICIENT_BUFFER) {
                    buff = new Memory(buffUsed.getValue());
                    if (!Wevtapi.INSTANCE.EvtGetChannelConfigProperty(channelHandle, Winevt.EVT_CHANNEL_CONFIG_PROPERTY_ID.EvtChannelConfigClassicEventlog,
                            0, (int) buff.size(), buff, buffUsed)) {
                        throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
                    }
                } else {
                    throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
                }
            }
            Winevt.EVT_VARIANT evtVariant = new Winevt.EVT_VARIANT();
            useMemory(evtVariant, buff, 0);
            assertThat(evtVariant.field1.BooleanVal, is(1));
        } finally {
            if (channelHandle != null) {
                Wevtapi.INSTANCE.EvtClose(channelHandle);
            }
        }
    }

    public void testEvtOpenPublisherEnum() throws Exception {
        Winevt.EVT_RPC_LOGIN login = new Winevt.EVT_RPC_LOGIN("localhost", null, null, null,
                Winevt.EVT_RPC_LOGIN_FLAGS.EvtRpcLoginAuthDefault);
        WinNT.HANDLE session = null;
        WinNT.HANDLE publisherEnumHandle = null;
        List<String> publisherList = new ArrayList<String>();
        try {
            session = Wevtapi.INSTANCE.EvtOpenSession(Winevt.EVT_LOGIN_CLASS.EvtRpcLogin, login, 0, 0);
            if (session == null) {
                throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
            }
            publisherEnumHandle = Wevtapi.INSTANCE.EvtOpenPublisherEnum(session, 0);
            if (publisherEnumHandle == null) {
                throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
            }

            Memory buff = new Memory(1024);
            IntByReference buffUsed = new IntByReference();
            while (true) {
                buff.clear();
                if (!Wevtapi.INSTANCE.EvtNextPublisherId(publisherEnumHandle, (int) buff.size(), buff, buffUsed)) {
                    if (Kernel32.INSTANCE.GetLastError() == WinError.ERROR_NO_MORE_ITEMS) {
                        break;
                    } else if (Kernel32.INSTANCE.GetLastError() == WinError.ERROR_INSUFFICIENT_BUFFER) {
                        buff = new Memory(buffUsed.getValue());
                        if (!Wevtapi.INSTANCE.EvtNextPublisherId(publisherEnumHandle, (int) buff.size(), buff, buffUsed)) {
                            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
                        }
                    }
                }
                publisherList.add(buff.getWideString(0));
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
        WinNT.HANDLE queryHandle = null;
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
            Pointer[] pointers = evtVariant.field1.StringArr.getPointerArray(0);
            for (int i = 0; i < count; i++) {
                sb.append(pointers[i].getWideString(0));
            }
            assertThat(sb.toString(), is("Application"));
        } finally {
            if (queryHandle != null) {
                Wevtapi.INSTANCE.EvtClose(queryHandle);
            }
        }
    }

    public void testEvtCreateBookmark() throws Exception {

        WinNT.HANDLE queryHandle = null;
        WinNT.HANDLE contextHandle = null;
        File testEvtx = new File(getClass().getResource("/res/test.evtx").toURI());
        StringBuilder sb = new StringBuilder();
        try {
            queryHandle = Wevtapi.INSTANCE.EvtQuery(null, testEvtx.getPath(), null,
                    Winevt.EVT_QUERY_FLAGS.EvtQueryFilePath);

            // test EvtCreateBookmark
            WinNT.HANDLE hBookmark = Wevtapi.INSTANCE.EvtCreateBookmark(
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
            StringArray array = new StringArray(targets, true);
            contextHandle = Wevtapi.INSTANCE.EvtCreateRenderContext(targets.length, array,
                    Winevt.EVT_RENDER_CONTEXT_FLAGS.EvtRenderContextValues);

            int eventArraySize = 10;
            int evtNextTimeout = 1000;
            int arrayIndex = 1;
            Memory buff = new Memory(1024);
            IntByReference propertyCount = new IntByReference();
            Winevt.EVT_VARIANT evtVariant = new Winevt.EVT_VARIANT();
            Memory eventArray = new Memory(POINTER_SIZE * eventArraySize);
            WinNT.HANDLEByReference evtHandle = new WinNT.HANDLEByReference();
            IntByReference returned = new IntByReference();
            while (Wevtapi.INSTANCE.EvtNext(queryHandle, eventArraySize, eventArray, evtNextTimeout, 0, returned)) {
                for (int i = 0; i < returned.getValue(); i++) {
                    try {
                        evtHandle.setPointer(eventArray.share(i * POINTER_SIZE));
                        evtRender(buff, contextHandle, evtHandle.getValue(),
                                Winevt.EVT_RENDER_FLAGS.EvtRenderEventValues, propertyCount);
                        useMemory(evtVariant, buff, 0);
                        assertThat("EventRecordID", evtVariant.field1.UInt64Val, is((long) arrayIndex * eventArraySize + i + 1));
                        sb.append(evtVariant.field1.UInt64Val);

                        // test EvtUpdateBookmark
                        if (!Wevtapi.INSTANCE.EvtUpdateBookmark(hBookmark, evtHandle.getValue())) {
                            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
                        }
                    } finally {
                        if (evtHandle.getValue() != null) {
                            Wevtapi.INSTANCE.EvtClose(evtHandle.getValue());
                        }
                    }
                }
                arrayIndex++;
            }
            if (Kernel32.INSTANCE.GetLastError() != WinError.ERROR_SUCCESS &&
                    Kernel32.INSTANCE.GetLastError() != WinError.ERROR_NO_MORE_ITEMS) {
                throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
            }
            buff = evtRender(buff, null, hBookmark, Winevt.EVT_RENDER_FLAGS.EvtRenderBookmark, propertyCount);
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
        WinNT.HANDLE queryHandle = null;
        WinNT.HANDLE contextHandle = null;
        File testEvtx = new File(getClass().getResource("/res/test.evtx").toURI());
        StringBuilder sb = new StringBuilder();
        try {
            queryHandle = Wevtapi.INSTANCE.EvtQuery(null, testEvtx.getPath(), null,
                    Winevt.EVT_QUERY_FLAGS.EvtQueryFilePath);


            String[] targets = {"Event/System/EventRecordID"};
            StringArray array = new StringArray(targets, true);
            contextHandle = Wevtapi.INSTANCE.EvtCreateRenderContext(targets.length, array,
                    Winevt.EVT_RENDER_CONTEXT_FLAGS.EvtRenderContextValues);

            int eventArraySize = 10;
            int evtNextTimeout = 1000;
            Memory buff = new Memory(1024);
            Winevt.EVT_VARIANT evtVariant = new Winevt.EVT_VARIANT();
            Memory eventArray = new Memory(POINTER_SIZE * eventArraySize);
            WinNT.HANDLEByReference evtHandle = new WinNT.HANDLEByReference();
            IntByReference buffUsed = new IntByReference();
            IntByReference returned = new IntByReference();
            while (Wevtapi.INSTANCE.EvtNext(queryHandle, eventArraySize, eventArray, evtNextTimeout, 0, returned)) {
                for (int i = 0; i < returned.getValue(); i++) {
                    try {
                        evtHandle.setPointer(eventArray.share(i * POINTER_SIZE));
                        if (!Wevtapi.INSTANCE.EvtGetEventInfo(evtHandle.getValue(),
                                Winevt.EVT_EVENT_PROPERTY_ID.EvtEventPath, (int) buff.size(), buff, buffUsed)) {
                            if (Kernel32.INSTANCE.GetLastError() == WinError.ERROR_INSUFFICIENT_BUFFER) {
                                buff = new Memory(buffUsed.getValue());
                                if (!Wevtapi.INSTANCE.EvtGetEventInfo(evtHandle.getValue(),
                                        Winevt.EVT_EVENT_PROPERTY_ID.EvtEventPath, (int) buff.size(), buff, buffUsed)) {
                                    throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
                                }
                            }
                        }
                        useMemory(evtVariant, buff, 0);
                        assertThat("Evtx Path", evtVariant.field1.StringVal, is(testEvtx.getAbsolutePath()));
                        sb.append(evtVariant.field1.StringVal);
                    } finally {
                        if (evtHandle.getValue() != null) {
                            Wevtapi.INSTANCE.EvtClose(evtHandle.getValue());
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