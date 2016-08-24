/* Copyright (c) 2016 Minoru Sakamoto, All Rights Reserved
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
package com.sun.jna.platform.win32;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.Union;
import com.sun.jna.WString;
import com.sun.jna.ptr.*;

import java.util.Arrays;
import java.util.List;

/**
 * Ported from winevt.h.
 * Microsoft Windows SDK 10.0.10586
 *
 * @author Minoru Sakamoto
 */
public interface Winevt {

    /**
     * Defines the possible data types of a variant data item.
     * https://msdn.microsoft.com/en-us/library/windows/desktop/aa385616(v=vs.85).aspx
     */
    public static enum EVT_VARIANT_TYPE {

        /** Null content that implies that the element that contains the content does not exist. */
        EvtVarTypeNull(""),

        /** A null-terminated Unicode string. */
        EvtVarTypeString("AnsiStringVal"),

        /** A null-terminated ANSI string. */
        EvtVarTypeAnsiString("AnsiStringVal"),

        /** A signed 8-bit integer value. */
        EvtVarTypeSByte("ByteVal"),

        /** An unsigned 8-bit integer value. */
        EvtVarTypeByte("ByteVal"),

        /** An signed 16-bit integer value. */
        EvtVarTypeInt16("Int16Val"),

        /** An unsigned 16-bit integer value. */
        EvtVarTypeUInt16("UInt16Val"),

        /** A signed 32-bit integer value. */
        EvtVarTypeInt32("Int32Val"),

        /** An unsigned 32-bit integer value. */
        EvtVarTypeUInt32("UInt32Val"),

        /** A signed 64-bit integer value. */
        EvtVarTypeInt64("Int64Val"),

        /** An unsigned 64-bit integer value. */
        EvtVarTypeUInt64("UInt64Val"),

        /** A single-precision real value. */
        EvtVarTypeSingle("SingleVal"),

        /** A double-precision real value. */
        EvtVarTypeDouble("DoubleVal"),

        /** A Boolean value. */
        EvtVarTypeBoolean("BooleanVal"),

        /** A hexadecimal binary value. */
        EvtVarTypeBinary("BinaryVal"),

        /** A GUID value. */
        EvtVarTypeGuid("GuidVal"),

        /** An unsigned 32-bit or 64-bit integer value that contains a pointer address. */
        EvtVarTypeSizeT("SizeTVal"),

        /** A FILETIME value. */
        EvtVarTypeFileTime("FileTimeVal"),

        /** A SYSTEMTIME value. */
        EvtVarTypeSysTime("SysTimeVal"),

        /** A security identifier (SID) structure */
        EvtVarTypeSid("SidVal"),

        /** A 32-bit hexadecimal number. */
        EvtVarTypeHexInt32("Int32Val"),

        /** A 64-bit hexadecimal number. */
        EvtVarTypeHexInt64("Int64Val"),

        /** An EVT_HANDLE value. */
        EvtVarTypeEvtHandle("EvtHandleVal"),

        /** A null-terminated Unicode string that contains XML. */
        EvtVarTypeEvtXml("AnsiStringVal");

        private final String field;

        private EVT_VARIANT_TYPE(String field) {
            this.field = field;
        }

        public String getField() {
            return this.field;
        }
    }

    /**
     * The Type member of the EVT_VARIANT structure has this bit set if the variant contains a pointer to an array of
     * values, rather than the value itself.
     * https://msdn.microsoft.com/en-us/library/windows/desktop/aa385781(v=vs.85).aspx
     */
    public static final int EVT_VARIANT_TYPE_ARRAY = 128;

    /**
     * A bitmask that you use to mask out the array bit of the variant type, so you can determine the data type of
     * the variant value that the EVT_VARIANT structure contains.
     * https://msdn.microsoft.com/en-us/library/windows/desktop/aa385781(v=vs.85).aspx
     */
    public static final int EVT_VARIANT_TYPE_MASK = 0x7f;

    /**
     * Defines the types of connection methods you can use to connect to the remote computer.
     * https://msdn.microsoft.com/en-us/library/windows/desktop/aa385529(v=vs.85).aspx
     */
    public static interface EVT_LOGIN_CLASS {

        /** Use Remote Procedure Call (RPC) login. */
        public static final int EvtRpcLogin = 1;
    }

    /**
     * Defines the types of authentication that you can use to authenticate the user when connecting to a remote
     * computer.
     * https://msdn.microsoft.com/en-us/library/windows/desktop/aa385570(v=vs.85).aspx
     */
    public static interface EVT_RPC_LOGIN_FLAGS {

        /**
         * Use the default authentication method during RPC login. The default authentication method is Negotiate.
         */
        public static final int EvtRpcLoginAuthDefault = 0;

        /**
         * Use the Negotiate authentication method during RPC login. The client and server negotiate whether to use
         * NTLM or Kerberos.
         */
        public static final int EvtRpcLoginAuthNegotiate = 1;

        /** Use Kerberos authentication during RPC login. */
        public static final int EvtRpcLoginAuthKerberos = 2;

        /** Use NTLM authentication during RPC login. */
        public static final int EvtRpcLoginAuthNTLM = 3;
    }

    /**
     * Defines the values that specify how to return the query results and whether you are query against a channel or
     * log file.
     * https://msdn.microsoft.com/en-us/library/windows/desktop/aa385549(v=vs.85).aspx
     */
    public static interface EVT_QUERY_FLAGS {

        /**
         * Specifies that the query is against one or more channels. The Path parameter of the EvtQuery function must
         * specify the name of a channel or NULL.
         */
        public static final int EvtQueryChannelPath = 0x1;

        /**
         * Specifies that the query is against one or more log files. The Path parameter of the EvtQuery function must
         * specify the full path to a log file or NULL.
         */
        public static final int EvtQueryFilePath = 0x2;

        /**
         * Specifies that the events in the query result are ordered from oldest to newest. This is the default.
         */
        public static final int EvtQueryForwardDirection = 0x100;

        /**
         * Specifies that the events in the query result are ordered from newest to oldest.
         */
        public static final int EvtQueryReverseDirection = 0x200;

        /**
         * Specifies that {@link Wevtapi#EvtQuery} should run the query even if the part of the query generates
         * an error (is not well formed). The service validates the syntax of the XPath query to determine if it is
         * well formed. If the validation fails, the service parses the XPath into individual expressions. It builds
         * a new XPath beginning with the left most expression. The service validates the expression and if it is valid,
         * the service adds the next expression to the XPath. The service repeats this process until it finds
         * the expression that is failing. It then uses the valid expressions that it found beginning with the leftmost
         * expression as the XPath query (which means that you may not get the events that you expected). If no part of
         * the XPath is valid, the EvtQuery call fails.
         */
        public static final int EvtQueryTolerateQueryErrors = 0x1000;
    }

    /**
     * Defines the relative position in the result set from which to seek.
     * https://msdn.microsoft.com/en-us/library/windows/desktop/aa385575(v=vs.85).aspx
     */
    public static interface EVT_SEEK_FLAGS {

        /**
         * Seek to the specified offset from the first entry in the result set. The offset must be a positive value.
         */
        public static final int EvtSeekRelativeToFirst = 1;

        /**
         * Seek to the specified offset from the last entry in the result set. The offset must be a negative value.
         */
        public static final int EvtSeekRelativeToLast = 2;

        /**
         * Seek to the specified offset from the current entry in the result set. The offset can be a positive or
         * negative value.
         */
        public static final int EvtSeekRelativeToCurrent = 3;

        /**
         * Seek to the specified offset from the bookmarked entry in the result set. The offset can be a positive or
         * negative value.
         */
        public static final int EvtSeekRelativeToBookmark = 4;

        /**
         * A bitmask that you can use to determine which of the following flags is set:
         * <ul>
         * <li>EvtSeekRelativeToFirst<li/>
         * <li>EvtSeekRelativeToLast<li/>
         * <li>EvtSeekRelativeToBookmark<li/>
         * </ul>
         */
        public static final int EvtSeekOriginMask = 7;

        /**
         * Force the function to fail if the event does not exist.
         */
        public static final int EvtSeekStrict = 0x10000;
    }

    /**
     * Defines the possible values that specify when to start subscribing to events.
     * https://msdn.microsoft.com/en-us/library/windows/desktop/aa385588(v=vs.85).aspx
     */
    public static interface EVT_SUBSCRIBE_FLAGS {

        /**
         * Subscribe to only future events that match the query criteria.
         */
        public static final int EvtSubscribeToFutureEvents = 1;

        /**
         * Subscribe to all existing and future events that match the query criteria.
         */
        public static final int EvtSubscribeStartAtOldestRecord = 2;

        /**
         * Subscribe to all existing and future events that match the query criteria that begin after the bookmarked
         * event. If you include the EvtSubscribeStrict flag, the {@link Wevtapi#EvtSubscribe} function fails if
         * the bookmarked event does not exist. If you do not include the EvtSubscribeStrict flag and the bookmarked
         * event does not exist, the subscription begins with the event that is after the event that is closest to
         * the bookmarked event.
         */
        public static final int EvtSubscribeStartAfterBookmark = 3;

        /**
         * A bitmask that you can use to determine which of the following flags is set:
         * <ul>
         * <li>EvtSubscribeToFutureEvents</li>
         * <li>EvtSubscribeStartAtOldestRecord</li>
         * <li>EvtSubscribeStartAfterBookmark</li>
         * </ul>
         */
        public static final int EvtSubscribeOriginMask = 3;

        /**
         * Complete the subscription even if the part of the query generates an error (is not well formed). The service
         * validates the syntax of the XPath query to determine if it is well formed. If the validation fails,
         * the service parses the XPath into individual expressions. It builds a new XPath beginning with the left most
         * expression. The service validates the expression and if it is valid, the service adds the next expression to
         * the XPath. The service repeats this process until it finds the expression that is failing. It then uses
         * the valid expressions that it found beginning with the leftmost expression as the XPath query (which means
         * that you may not get the events that you expected). If no part of the XPath is valid,
         * the {@link Wevtapi#EvtSubscribe} call fails.
         */
        public static final int EvtSubscribeTolerateQueryErrors = 0x1000;

        /**
         * Forces the {@link Wevtapi#EvtSubscribe} call to fail if you specify EvtSubscribeStartAfterBookmark and
         * the bookmarked event is not found (the return value is ERROR_NOT_FOUND). Also, set this flag if you want to
         * receive notification in your callback when event records are missing.
         */
        public static final int EvtSubscribeStrict = 0x10000;
    }

    /**
     * Defines the possible types of data that the subscription service can deliver to your callback.
     * https://msdn.microsoft.com/en-us/library/windows/desktop/aa385596(v=vs.85).aspx
     */
    public static interface EVT_SUBSCRIBE_NOTIFY_ACTION {

        /**
         * Indicates that the Event parameter contains a Win32 error code.
         */
        public static final int EvtSubscribeActionError = 0;

        /**
         * Indicates that the Event parameter contains an event that matches the subscriber's query.
         */
        public static final int EvtSubscribeActionDeliver = 1;
    }

    /**
     * Defines the identifiers that identify the system-specific properties of an event.
     * https://msdn.microsoft.com/en-us/library/windows/desktop/aa385606(v=vs.85).aspx
     */
    public static interface EVT_SYSTEM_PROPERTY_ID {

        /**
         * Identifies the Name attribute of the provider element. The variant type for this property is EvtVarTypeString.
         */
        public static final int EvtSystemProviderName = 0;

        /**
         * Identifies the Guid attribute of the provider element. The variant type for this property is EvtVarTypeGuid.
         */
        public static final int EvtSystemProviderGuid = 1;

        /** Identifies the EventID element. The variant type for this property is EvtVarTypeUInt16. */
        public static final int EvtSystemEventID = 2;

        /**
         * Identifies the Qualifiers attribute of the EventID element. The variant type for this property is
         * EvtVarTypeUInt16.
         */
        public static final int EvtSystemQualifiers = 3;

        /** Identifies the Level element. The variant type for this property is EvtVarTypeUInt8. */
        public static final int EvtSystemLevel = 4;

        /** Identifies the Task element. The variant type for this property is EvtVarTypeUInt16. */
        public static final int EvtSystemTask = 5;

        /** Identifies the Opcode element. The variant type for this property is EvtVarTypeUInt8. */
        public static final int EvtSystemOpcode = 6;

        /** Identifies the Keywords element. The variant type for this property is EvtVarTypeInt64. */
        public static final int EvtSystemKeywords = 7;

        /**
         * Identifies the SystemTime attribute of the TimeCreated element. The variant type for this property is
         * EvtVarTypeFileTime.
         */
        public static final int EvtSystemTimeCreated = 8;

        /** Identifies the EventRecordID element. The variant type for this property is EvtVarTypeUInt64. */
        public static final int EvtSystemEventRecordId = 9;

        /**
         * Identifies the ActivityID attribute of the Correlation element. The variant type for this property is
         * EvtVarTypeGuid.
         */
        public static final int EvtSystemActivityID = 10;

        /**
         * Identifies the RelatedActivityID attribute of the Correlation element. The variant type for this property is
         * EvtVarTypeGuid.
         */
        public static final int EvtSystemRelatedActivityID = 11;

        /**
         * Identifies the ProcessID attribute of the Execution element. The variant type for this property is
         * EvtVarTypeUInt32.
         */
        public static final int EvtSystemProcessID = 12;

        /**
         * Identifies the ThreadID attribute of the Execution element. The variant type for this property is
         * EvtVarTypeUInt32.
         */
        public static final int EvtSystemThreadID = 13;

        /** Identifies the Channel element. The variant type for this property is EvtVarTypeString. */
        public static final int EvtSystemChannel = 14;

        /** Identifies the Computer element. The variant type for this property is EvtVarTypeString. */
        public static final int EvtSystemComputer = 15;

        /** Identifies the UserID element. The variant type for this property is EvtVarTypeSid. */
        public static final int EvtSystemUserID = 16;

        /** Identifies the Version element. The variant type for this property is EvtVarTypeUInt8. */
        public static final int EvtSystemVersion = 17;

        /** This enumeration value marks the end of the enumeration values. */
        public static final int EvtSystemPropertyIdEND = 18;
    }

    /**
     * Defines the values that specify the type of information to access from the event.
     * https://msdn.microsoft.com/en-us/library/windows/desktop/aa385561(v=vs.85).aspx
     */
    public static interface EVT_RENDER_CONTEXT_FLAGS {

        /** Render specific properties from the event. */
        public static final int EvtRenderContextValues = 0;

        /**
         * Render the system properties under the System element. The properties are returned in the order defined in
         * the {@link EVT_SYSTEM_PROPERTY_ID} enumeration.
         */
        public static final int EvtRenderContextSystem = 1;

        /**
         * Render all user-defined properties under the UserData or EventData element. If the data template associated
         * with the event contains a UserData section, the UserData properties are rendered; otherwise, the EventData
         * properties are rendered.
         */
        public static final int EvtRenderContextUser = 2;
    }

    /**
     * Defines the values that specify what to render.
     * https://msdn.microsoft.com/en-us/library/windows/desktop/aa385563(v=vs.85).aspx
     */
    public static interface EVT_RENDER_FLAGS {

        /** Render the event properties specified in the rendering context. */
        public static final int EvtRenderEventValues = 0;

        /**
         * Render the event as an XML string. For details on the contents of the XML string, see the Event schema.
         */
        public static final int EvtRenderEventXml = 1;

        /**
         * Render the bookmark as an XML string, so that you can easily persist the bookmark for use later.
         */
        public static final int EvtRenderBookmark = 2;
    }

    /**
     * Defines the values that specify the message string from the event to format.
     * https://msdn.microsoft.com/en-us/library/windows/desktop/aa385525(v=vs.85).aspx
     */
    public static interface EVT_FORMAT_MESSAGE_FLAGS {

        /** Format the event's message string. */
        public static final int EvtFormatMessageEvent = 1;

        /** Format the message string of the level specified in the event. */
        public static final int EvtFormatMessageLevel = 2;

        /** Format the message string of the task specified in the event. */
        public static final int EvtFormatMessageTask = 3;

        /** Format the message string of the opcode specified in the event. */
        public static final int EvtFormatMessageOpcode = 4;

        /**
         * Format the message string of the keywords specified in the event. If the event specifies multiple keywords,
         * the formatted string is a list of null-terminated strings. Increment through the strings until your pointer
         * points past the end of the used buffer.
         */
        public static final int EvtFormatMessageKeyword = 5;

        /** Format the message string of the channel specified in the event. */
        public static final int EvtFormatMessageChannel = 6;

        /** Format the provider's message string. */
        public static final int EvtFormatMessageProvider = 7;

        /**
         * Format the message string associated with a resource identifier. The provider's metadata contains
         * the resource identifiers; the message compiler assigns a resource identifier to each string when it compiles
         * the manifest.
         */
        public static final int EvtFormatMessageId = 8;

        /**
         * Format all the message strings in the event. The formatted message is an XML string that contains the event
         * details and the message strings. The message strings are included in the RenderingInfo section of the event
         * details.
         */
        public static final int EvtFormatMessageXml = 9;
    }

    /**
     * Defines the values that specify whether to open a channel or exported log file.
     * https://msdn.microsoft.com/en-us/library/windows/desktop/aa385541(v=vs.85).aspx
     */
    public static interface EVT_OPEN_LOG_FLAGS {

        /** Open a channel. */
        public static final int EvtOpenChannelPath = 0x1;

        /** Open an exported log file. */
        public static final int EvtOpenFilePath = 0x2;
    }

    /**
     * Defines the identifiers that identify the log file metadata properties of a channel or log file.
     * https://msdn.microsoft.com/en-us/library/windows/desktop/aa385536(v=vs.85).aspx
     */
    public static interface EVT_LOG_PROPERTY_ID {

        /**
         * Identifies the property that contains the time that the channel or log file was created. The variant type
         * for this property is EvtVarTypeFileTime.
         */
        public static final int EvtLogCreationTime = 0;

        /**
         * Identifies the property that contains the last time that the channel or log file was accessed. The variant
         * type for this property is EvtVarTypeFileTime.
         */
        public static final int EvtLogLastAccessTime = 1;

        /**
         * Identifies the property that contains the last time that the channel or log file was written to. The variant
         * type for this property is EvtVarTypeFileTime.
         */
        public static final int EvtLogLastWriteTime = 2;

        /**
         * Identifies the property that contains the size of the file, in bytes. The variant type for this property
         * is EvtVarTypeUInt64.
         */
        public static final int EvtLogFileSize = 3;

        /**
         * Identifies the property that contains the file attributes (for details on the file attributes,
         * see the GetFileAttributesEx function). The variant type for this property is EvtVarTypeUInt32.
         */
        public static final int EvtLogAttributes = 4;

        /**
         * Identifies the property that contains the number of records in the channel or log file. The variant type
         * for this property is EvtVarTypeUInt64.
         */
        public static final int EvtLogNumberOfLogRecords = 5;

        /**
         * Identifies the property that contains the record number of the oldest event in the channel or log file.
         * The variant type for this property is EvtVarTypeUInt64.
         */
        public static final int EvtLogOldestRecordNumber = 6;

        /**
         * Identifies the property that you use to determine whether the channel or log file is full. The variant type
         * for this property is EvtVarTypeBoolean. The channel is full if another event cannot be written to the channel
         * (for example, if the channel is sequential and maximum size is reached). The property will always be false if
         * the channel is circular or the sequential log is automatically backed up.
         */
        public static final int EvtLogFull = 7;
    }

    /**
     * Defines values that indicate whether the events come from a channel or log file.
     * https://msdn.microsoft.com/en-us/library/windows/desktop/aa385522(v=vs.85).aspx
     */
    public static interface EVT_EXPORTLOG_FLAGS {

        /** The source of the events is a channel. */
        public static final int EvtExportLogChannelPath = 0x1;

        /** The source of the events is a previously exported log file. */
        public static final int EvtExportLogFilePath = 0x2;

        /**
         * Export events even if part of the query generates an error (is not well formed). The service validates
         * the syntax of the XPath query to determine whether it is well formed. If the validation fails, the service
         * parses the XPath into individual expressions. It builds a new XPath beginning with the leftmost expression.
         * The service validates the expression and if it is valid, the service adds the next expression to the XPath.
         * The service repeats this process until it finds the expression that is failing. It then uses the valid
         * expressions as the XPath query (which means that you may not get the events that you expected). If no part of
         * the XPath is valid, the EvtExportLog call fails.
         */
        public static final int EvtExportLogTolerateQueryErrors = 0x1000;

        /** no document */
        public static final int EvtExportLogOverwrite = 0x2000;
    }

    /**
     * Defines the identifiers that identify the configuration properties of a channel.
     * https://msdn.microsoft.com/en-us/library/windows/desktop/aa385499(v=vs.85).aspx
     */
    public static interface EVT_CHANNEL_CONFIG_PROPERTY_ID {

        /**
         * Identifies the enabled attribute of the channel. The variant type for this property is EvtVarTypeBoolean.
         * You cannot set this property for the Application, System, and Security channels.
         */
        public static final int EvtChannelConfigEnabled = 0;

        /**
         * Identifies the isolation attribute of the channel. The variant type for this property is EvtVarTypeUInt32.
         * For possible isolation values, see the {@link Winevt.EVT_CHANNEL_ISOLATION_TYPE} enumeration.
         * You cannot set this property for the Application, System, and Security channels.
         */
        public static final int EvtChannelConfigIsolation = 1;

        /**
         * Identifies the type attribute of the channel. The variant type for this property is EvtVarTypeUInt32.
         * For possible isolation values, see the {@link Winevt.EVT_CHANNEL_TYPE} enumeration.
         * You cannot set this property.
         */
        public static final int EvtChannelConfigType = 2;

        /**
         * Identifies the name attribute of the provider that defined the channel. The variant type for this property
         * is EvtVarTypeString. You cannot set this property.
         */
        public static final int EvtChannelConfigOwningPublisher = 3;

        /**
         * Identifies the configuration property that indicates whether the channel is a classic event channel
         * (for example the Application or System log). The variant type for this property is EvtVarTypeBoolean.
         * You cannot set this property.
         */
        public static final int EvtChannelConfigClassicEventlog = 4;

        /**
         * Identifies the access attribute of the channel. The variant type for this property is EvtVarTypeString.
         */
        public static final int EvtChannelConfigAccess = 5;

        /**
         * Identifies the retention logging attribute of the channel. The variant type for this property is
         * EvtVarTypeBoolean.
         */
        public static final int EvtChannelLoggingConfigRetention = 6;

        /**
         * Identifies the autoBackup logging attribute of the channel. The variant type for this property is
         * EvtVarTypeBoolean.
         */
        public static final int EvtChannelLoggingConfigAutoBackup = 7;

        /**
         * Identifies the maxSize logging attribute of the channel. The variant type for this property is
         * EvtVarTypeUInt64.
         */
        public static final int EvtChannelLoggingConfigMaxSize = 8;

        /**
         * Identifies the configuration property that contains the path to the file that backs the channel.
         * The variant type for this property is EvtVarTypeString.
         */
        public static final int EvtChannelLoggingConfigLogFilePath = 9;

        /**
         * Identifies the level publishing attribute of the channel. The variant type for this property is
         * EvtVarTypeUInt32.
         * To set this property, you must first disable the debug or analytic channel.
         */
        public static final int EvtChannelPublishingConfigLevel = 10;

        /**
         * Identifies the keywords publishing attribute of the channel. The variant type for this property is
         * EvtVarTypeUInt64.
         * To set this property, you must first disable the debug or analytic channel.
         */
        public static final int EvtChannelPublishingConfigKeywords = 11;

        /**
         * Identifies the controlGuid publishing attribute of the channel. The variant type for this property is
         * EvtVarTypeGuid.
         * You cannot set this property.
         */
        public static final int EvtChannelPublishingConfigControlGuid = 12;

        /**
         * Identifies the bufferSize publishing attribute of the channel. The variant type for this property is
         * EvtVarTypeUInt32.
         * You cannot set this property.
         */
        public static final int EvtChannelPublishingConfigBufferSize = 13;

        /**
         * Identifies the minBuffers publishing attribute of the channel. The variant type for this property is
         * EvtVarTypeUInt32. You cannot set this property.
         */
        public static final int EvtChannelPublishingConfigMinBuffers = 14;

        /**
         * Identifies the maxBuffers publishing attribute of the channel. The variant type for this property is
         * EvtVarTypeUInt32.
         * You cannot set this property.
         */
        public static final int EvtChannelPublishingConfigMaxBuffers = 15;

        /**
         * Identifies the latency publishing attribute of the channel. The variant type for this property is
         * EvtVarTypeUInt32.
         * You cannot set this property.
         */
        public static final int EvtChannelPublishingConfigLatency = 16;

        /**
         * Identifies the clockType publishing attribute of the channel. The variant type for this property is
         * EvtVarTypeUInt32. For possible clock type values, see the {@link Winevt.EVT_CHANNEL_CLOCK_TYPE} enumeration.
         * You cannot set this property.
         */
        public static final int EvtChannelPublishingConfigClockType = 17;

        /**
         * Identifies the sidType publishing attribute of the channel. The variant type for this property is
         * EvtVarTypeUInt32. For possible SID type values, see the {@link Winevt.EVT_CHANNEL_SID_TYPE} enumeration.
         * You cannot set this property.
         */
        public static final int EvtChannelPublishingConfigSidType = 18;

        /**
         * Identifies the configuration property that contains the list of providers that import this channel.
         * The variant type for this property is EvtVarTypeString | EVT_VARIANT_TYPE_ARRAY.
         * You cannot set this property.
         */
        public static final int EvtChannelPublisherList = 19;

        /**
         * Identifies the fileMax publishing attribute of the channel. The variant type for this property is
         * EvtVarTypeUInt32.
         */
        public static final int EvtChannelPublishingConfigFileMax = 20;

        /**
         * This enumeration value marks the end of the enumeration values.
         */
        public static final int EvtChannelConfigPropertyIdEND = 21;
    }

    /**
     * Defines the type of a channel.
     * https://msdn.microsoft.com/en-us/library/windows/desktop/aa385514(v=vs.85).aspx
     */
    public static interface EVT_CHANNEL_TYPE {

        /** The channel's type is Admin. */
        public static final int EvtChannelTypeAdmin = 0;

        /** The channel's type is Operational. */
        public static final int EvtChannelTypeOperational = 1;

        /** The channel's type is Analytic. */
        public static final int EvtChannelTypeAnalytic = 2;

        /** The channel's type is Debug. */
        public static final int EvtChannelTypeDebug = 3;
    }

    /**
     * Defines the default access permissions to apply to the channel.
     * https://msdn.microsoft.com/en-us/library/windows/desktop/aa385502(v=vs.85).aspx
     */
    public static interface EVT_CHANNEL_ISOLATION_TYPE {

        /** Provides open access to the channel. */
        public static final int EvtChannelIsolationTypeApplication = 0;

        /**
         * Provides restricted access to the channel and is used by applications running under system service accounts,
         * drivers, or an application that logs events that relate to the health of the computer.
         */
        public static final int EvtChannelIsolationTypeSystem = 1;

        /** Provides custom access to the channel. */
        public static final int EvtChannelIsolationTypeCustom = 2;
    }

    /**
     * Defines the values that specify the type of time stamp to use when logging events channel.
     * https://msdn.microsoft.com/en-us/library/windows/desktop/aa385493(v=vs.85).aspx
     */
    public static interface EVT_CHANNEL_CLOCK_TYPE {

        /**
         * Uses the system time for the time stamp. The system time provides a low-resolution (10 milliseconds) time
         * stamp but is comparatively less expensive to retrieve. System time is the default. Note that if the volume
         * of events is high, the resolution for system time may not be fine enough to determine the sequence of events.
         * If multiple events contain the same time stamp, the events may be delivered in the wrong order.
         */
        public static final int EvtChannelClockTypeSystemTime = 0;

        /**
         * Uses the query performance counter (QPC) for the time stamp. The QPC time stamp provides a high-resolution
         * (100 nanoseconds) time stamp but is comparatively more expensive to retrieve. You should use this resolution
         * if you have high event rates or if the consumer merges events from different buffers. Note that on older
         * computers, the time stamp may not be accurate because the counter sometimes skips forward due to hardware
         * errors.
         */
        public static final int EvtChannelClockTypeQPC = 1;
    }

    /**
     * Defines the values that determine whether the event includes the security identifier (SID) of the principal
     * that logged the event.
     * https://msdn.microsoft.com/en-us/library/windows/desktop/aa385511(v=vs.85).aspx
     */
    public static interface EVT_CHANNEL_SID_TYPE {

        /** Do not include with the event the SID of the principal that logged the event. */
        public static final int EvtChannelSidTypeNone = 0;

        /** Include with the event the SID of the principal that logged the event. */
        public static final int EvtChannelSidTypePublishing = 1;
    }

    /**
     * Defines the values that specify how a channel is referenced.
     * https://msdn.microsoft.com/en-us/library/windows/desktop/aa385507(v=vs.85).aspx
     */
    public static interface EVT_CHANNEL_REFERENCE_FLAGS {

        /** Specifies that the channel is imported. */
        public static final int EvtChannelReferenceImported = 0x1;
    }

    /**
     * Defines the identifiers that identify the metadata properties of a provider.
     * https://msdn.microsoft.com/en-us/library/windows/desktop/aa385545(v=vs.85).aspx
     */
    public static interface EVT_PUBLISHER_METADATA_PROPERTY_ID {

        /**
         * Identifies the guid attribute of the provider. The variant type for this property is EvtVarTypeGuid.
         */
        public static final int EvtPublisherMetadataPublisherGuid = 0;

        /**
         * Identifies the resourceFilePath attribute of the provider. The variant type for this property is
         * EvtVarTypeString.
         */
        public static final int EvtPublisherMetadataResourceFilePath = 1;

        /**
         * Identifies the parameterFilePath attribute of the provider. The variant type for this property is
         * EvtVarTypeString.
         */
        public static final int EvtPublisherMetadataParameterFilePath = 2;

        /**
         * Identifies the messageFilePath attribute of the provider. The variant type for this property is
         * EvtVarTypeString.
         * https://msdn.microsoft.com/en-us/library/windows/desktop/aa385545(v=vs.85).aspx
         */
        public static final int EvtPublisherMetadataMessageFilePath = 3;

        /**
         * Identifies the helpLink attribute of the provider. The variant type for this property is EvtVarTypeString.
         */
        public static final int EvtPublisherMetadataHelpLink = 4;

        /**
         * Identifies the message attribute of the provider. The metadata is the resource identifier assigned to
         * the message string. To get the message string, call the {@link Wevtapi#EvtFormatMessage} function.
         * The variant type for this property is EvtVarTypeUInt32. If the provider does not specify a message,
         * the value is –1.
         */
        public static final int EvtPublisherMetadataPublisherMessageID = 5;

        /**
         * Identifies the channels child element of the provider. The variant type for this property is
         * EvtVarTypeEvtHandle. To access the metadata of the channels that the provider defines or imports, use this
         * handle when calling the {@link Wevtapi#EvtGetObjectArrayProperty} function. For details, see Remarks.
         * When you are done with the handle, call the {@link Wevtapi#EvtClose} function.
         */
        public static final int EvtPublisherMetadataChannelReferences = 6;

        /**
         * Identifies the name attribute of the channel. Use this identifier when calling
         * the {@link Wevtapi#EvtGetObjectArrayProperty} function. For details, see Remarks. The variant type for
         * this property is EvtVarTypeString.
         */
        public static final int EvtPublisherMetadataChannelReferencePath = 7;

        /**
         * Identifies the zero-based index value of the channel in the list of channels. Use this identifier when
         * calling the {@link Wevtapi#EvtGetObjectArrayProperty} function. For details, see Remarks. The variant type
         * for this property is EvtVarTypeUInt32.
         */
        public static final int EvtPublisherMetadataChannelReferenceIndex = 8;

        /**
         * Identifies the value attribute of the channel. Use this identifier when calling
         * the ${@link Wevtapi#EvtGetObjectArrayProperty} function. For details, see Remarks. The variant type for
         * this property is EvtVarTypeUInt32.
         */
        public static final int EvtPublisherMetadataChannelReferenceID = 9;

        /**
         * Identifies the flags value that indicates whether this channel is imported from another provider. The channel
         * is imported if the EvtChannelReferenceImported flag value is set. Use this identifier when calling
         * the {@link Wevtapi#EvtGetObjectArrayProperty} function. For details, see Remarks.
         * The variant type for this property is EvtVarTypeUInt32.
         */
        public static final int EvtPublisherMetadataChannelReferenceFlags = 10;

        /**
         * Identifies the message attribute of the channel. Use this identifier when calling
         * the {@link Wevtapi#EvtGetObjectArrayProperty} function. For details, see Remarks. The variant type for
         * this property is EvtVarTypeUInt32. The property contains the resource identifier that is assigned to
         * the message string. To get the message string, call the EvtFormatMessage function.
         * If the channel does not specify a message, the value is –1.
         */
        public static final int EvtPublisherMetadataChannelReferenceMessageID = 11;

        /**
         * Identifies the levels child element of the provider. The variant type for this property is
         * EvtVarTypeEvtHandle. To access the metadata of the levels that the provider defines or references,
         * use this handle when calling the {@link Wevtapi#EvtGetObjectArrayProperty} function. For details,
         * see Remarks. When you are done with the handle, call the {@link Wevtapi#EvtClose} function.
         */
        public static final int EvtPublisherMetadataLevels = 12;

        /**
         * Identifies the name attribute of the level. Use this identifier when calling
         * the {@link Wevtapi#EvtGetObjectArrayProperty} function. For details, see Remarks.
         * The variant type for this property is EvtVarTypeString.
         */
        public static final int EvtPublisherMetadataLevelName = 13;

        /**
         * Identifies the value attribute of the level. Use this identifier when calling
         * the {@link Wevtapi#EvtGetObjectArrayProperty} function. For details, see Remarks. The variant type for
         * this property is EvtVarTypeUInt32.
         */
        public static final int EvtPublisherMetadataLevelValue = 14;

        /**
         * Identifies the message attribute of the level. Use this identifier when calling
         * the {@link Wevtapi#EvtGetObjectArrayProperty} function. For details, see Remarks. The variant type for
         * this property is EvtVarTypeUInt32. The property contains the resource identifier that is assigned to
         * the message string. To get the message string, call the {@link Wevtapi#EvtFormatMessage} function.
         * If the level does not specify a message, the value is –1.
         */
        public static final int EvtPublisherMetadataLevelMessageID = 15;

        /**
         * Identifies the tasks child element of the provider. The variant type for this property is
         * EvtVarTypeEvtHandle. To access the metadata of the tasks that the provider defines, use this handle when
         * calling the {@link Wevtapi#EvtGetObjectArrayProperty} function. For details, see Remarks.
         * When you are done with the handle, call the {@link Wevtapi#EvtClose} function.
         */
        public static final int EvtPublisherMetadataTasks = 16;

        /**
         * Identifies the name attribute of the task. Use this identifier when calling
         * the {@link Wevtapi#EvtGetObjectArrayProperty} function. For details, see Remarks.
         * The variant type for this property is EvtVarTypeString.
         */
        public static final int EvtPublisherMetadataTaskName = 17;

        /**
         * Identifies the eventGuid attribute of the task. Use this identifier when calling
         * the {@link Wevtapi#EvtGetObjectArrayProperty} function. For details, see Remarks.
         * The variant type for this property is EvtVarTypeString.
         */
        public static final int EvtPublisherMetadataTaskEventGuid = 18;

        /**
         * Identifies the value attribute of the task. Use this identifier when calling
         * the {@link Wevtapi#EvtGetObjectArrayProperty} function. For details, see Remarks.
         * The variant type for this property is EvtVarTypeUInt32.
         */
        public static final int EvtPublisherMetadataTaskValue = 19;

        /**
         * Identifies the message attribute of the task. Use this identifier when calling
         * the {@link Wevtapi#EvtGetObjectArrayProperty} function. For details, see Remarks.
         * The variant type for this property is EvtVarTypeUInt32. The property contains the resource identifier
         * that is assigned to the message string. To get the message string, call the {@link Wevtapi#EvtFormatMessage}
         * function. If the task does not specify a message, the value is –1.
         */
        public static final int EvtPublisherMetadataTaskMessageID = 20;

        /**
         * Identifies the opcodes child element of the provider. The variant type for this property is
         * EvtVarTypeEvtHandle. To access the metadata of the opcodes that the provider defines or references,
         * use this handle when calling the {@link Wevtapi#EvtGetObjectArrayProperty} function. For details,
         * see Remarks. When you are done with the handle, call the {@link Wevtapi#EvtClose} function.
         */
        public static final int EvtPublisherMetadataOpcodes = 21;

        /**
         * Identifies the name attribute of the opcode. Use this identifier when calling
         * the {@link Wevtapi#EvtGetObjectArrayProperty} function. For details, see Remarks.
         * The variant type for this property is EvtVarTypeString.
         */
        public static final int EvtPublisherMetadataOpcodeName = 22;

        /**
         * Identifies the value attribute of the opcode. Use this identifier when calling
         * the {@link Wevtapi#EvtGetObjectArrayProperty} function. For details, see Remarks. The variant type for
         * this property is EvtVarTypeUInt32. The high word contains the opcode value and the low word contains the task
         * to which it belongs. If the low word is zero, the opcode is defined globally; otherwise, the opcode is task
         * specific. Use the low word value to determine the task that defines the opcode.
         */
        public static final int EvtPublisherMetadataOpcodeValue = 23;

        /**
         * Identifies the message attribute of the opcode. Use this identifier when calling
         * the {@link Wevtapi#EvtGetObjectArrayProperty} function. For details, see Remarks. The variant type for
         * this property is EvtVarTypeUInt32. The property contains the resource identifier that is assigned to
         * the message string. To get the message string, call the {@link Wevtapi#EvtFormatMessage} function.
         * If the opcode does not specify a message, the value is –1.
         */
        public static final int EvtPublisherMetadataOpcodeMessageID = 24;

        /**
         * Identifies the keywords child element of the provider. The variant type for this property is
         * EvtVarTypeEvtHandle. To access the metadata of the keywords that the provider defines, use this handle when
         * calling the {@link Wevtapi#EvtGetObjectArrayProperty} function. For details, see Remarks. When you are done
         * with the handle, call the {@link Wevtapi#EvtClose} function.
         */
        public static final int EvtPublisherMetadataKeywords = 25;

        /**
         * Identifies the name attribute of the keyword. Use this identifier when calling
         * the {@link Wevtapi#EvtGetObjectArrayProperty} function. For details, see Remarks.
         * The variant type for this property is EvtVarTypeString.
         */
        public static final int EvtPublisherMetadataKeywordName = 26;

        /**
         * Identifies the mask attribute of the keyword. Use this identifier when calling
         * the {@link Wevtapi#EvtGetObjectArrayProperty} function. For details, see Remarks.
         * The variant type for this property is EvtVarTypeUInt64.
         */
        public static final int EvtPublisherMetadataKeywordValue = 27;

        /**
         * Identifies the message attribute of the keyword. Use this identifier when calling
         * the {@link Wevtapi#EvtGetObjectArrayProperty} function. For details, see Remarks. The variant type for
         * this property is EvtVarTypeUInt32. The property contains the resource identifier that is assigned to
         * the message string. To get the message string, call the {@link Wevtapi#EvtFormatMessage} function.
         * If the keyword does not specify a message, the value is –1.
         */
        public static final int EvtPublisherMetadataKeywordMessageID = 28;

        /** This enumeration value marks the end of the enumeration values. */
        public static final int EvtPublisherMetadataPropertyIdEND = 29;
    }

    /**
     * Defines the identifiers that identify the metadata properties of an event definition.
     * https://msdn.microsoft.com/en-us/library/windows/desktop/aa385517%28v=vs.85%29.aspx?f=255&MSPPError=-2147217396
     */
    public static interface EVT_EVENT_METADATA_PROPERTY_ID {

        /**
         * Identifies the value attribute of the event definition. The variant type for this property is
         * EvtVarTypeUInt32.
         */
        public static final int EventMetadataEventID = 0;

        /**
         * Identifies the version attribute of the event definition. The variant type for this property is
         * EvtVarTypeUInt32.
         */
        public static final int EventMetadataEventVersion = 1;

        /**
         * Identifies the channel attribute of the event definition. The variant type for this property is
         * EvtVarTypeUInt32. This property does not contain the channel identifier that you specified in the event
         * definition but instead contains the value attribute of the channel. The value is zero if the event
         * definition does not specify a channel.
         */
        public static final int EventMetadataEventChannel = 2;

        /**
         * Identifies the level attribute of the event definition. The variant type for this property is
         * EvtVarTypeUInt32. This property does not contain the level name that you specified in the event definition
         * but instead contains the value attribute of the level. The value is zero if the event definition does not
         * specify a level.
         */
        public static final int EventMetadataEventLevel = 3;

        /**
         * Identifies the opcode attribute of the event definition. The variant type for this property is
         * EvtVarTypeUInt32. This property does not contain the opcode name that you specified in the event definition
         * but instead contains the value attribute of the opcode. The value is zero if the event definition does not
         * specify an opcode.
         */
        public static final int EventMetadataEventOpcode = 4;

        /**
         * dentifies the task attribute of the event definition. The variant type for this property is EvtVarTypeUInt32.
         * This property does not contain the task name that you specified in the event definition but instead contains
         * the value attribute of the task. The value is zero if the event definition does not specify a task.
         */
        public static final int EventMetadataEventTask = 5;

        /**
         * Identifies the keyword attribute of the event definition. The variant type for this property is
         * EvtVarTypeUInt64. This property does not contain the list of keyword names that you specified in the event
         * definition but instead contains a 64-bitmask of all the keywords. The top 16 bits of the mask are reserved
         * for internal use and should be ignored when determining the keyword bits that the event definition set.
         */
        public static final int EventMetadataEventKeyword = 6;

        /**
         * Identifies the message attribute of the event definition. The variant type for this property is
         * EvtVarTypeUInt32. The property contains the resource identifier that is assigned to the message string.
         * To get the message string, call the EvtFormatMessage function. If the event definition does not specify
         * a message, the value is –1.
         */
        public static final int EventMetadataEventMessageID = 7;

        /**
         * Identifies the template attribute of the event definition. The variant type for this property is
         * EvtVarTypeString. This property does not contain the template name that you specified in the event definition
         * but instead contains an XML string that includes the template node and each data node; the string does not
         * include the UserData. The value is an empty string if the event definition does not specify a template.
         */
        public static final int EventMetadataEventTemplate = 8;

        /** This enumeration value marks the end of the enumeration values. */
        public static final int EvtEventMetadataPropertyIdEND = 9;
    }

    /**
     * Defines the identifiers that identify the query information that you can retrieve.
     * https://msdn.microsoft.com/en-us/library/windows/desktop/aa820607(v=vs.85).aspx
     */
    public static interface EVT_QUERY_PROPERTY_ID {

        /**
         * Identifies the property that contains the list of channel or log file names that are specified in the query.
         * The variant type for this property is EvtVarTypeString | EVT_VARIANT_TYPE_ARRAY.
         */
        public static final int EvtQueryNames = 0;

        /**
         * Identifies the property that contains the list of Win32 error codes that correspond directly to the list of
         * channel or log file names that the EvtQueryNames property returns. The error codes indicate the success or
         * failure of the query for the specific channel or log file. The variant type for this property is
         * EvtVarTypeUInt32 | EVT_VARIANT_TYPE_ARRAY.
         */
        public static final int EvtQueryStatuses = 1;

        /** This enumeration value marks the end of the enumeration values. */
        public static final int EvtQueryPropertyIdEND = 2;
    }

    /**
     * Defines the values that determine the query information to retrieve.
     * https://msdn.microsoft.com/en-us/library/windows/desktop/aa385600(v=vs.85).aspx
     */
    public static interface EVT_EVENT_PROPERTY_ID {

        /**
         * Not supported. The identifier of the query that selected the event. The variant type of this property is
         * EvtVarTypeInt32.
         */
        public static final int EvtEventQueryIDs = 0;

        /**
         * The channel or log file from which the event came. The variant type of this property is EvtVarTypeString.
         */
        public static final int EvtEventPath = 1;

        /**
         * This enumeration value marks the end of the enumeration values. It can be used to exit a loop when retrieving
         * all the properties.
         */
        public static final int EvtEventPropertyIdEND = 2;
    }

    /**
     * Read access control permission that allows information to be read from an event log.
     * https://msdn.microsoft.com/en-us/library/windows/desktop/aa385781(v=vs.85).aspx
     */
    public static final int EVT_READ_ACCESS = 0x1;

    /**
     * Write access control permission that allows information to be written to an event log.
     * https://msdn.microsoft.com/en-us/library/windows/desktop/aa385781(v=vs.85).aspx
     */
    public static final int EVT_WRITE_ACCESS = 0x2;

    /**
     * All (read, write, clear, and delete) access control permission.
     * https://msdn.microsoft.com/en-us/library/windows/desktop/aa385781(v=vs.85).aspx
     */
    public static final int EVT_ALL_ACCESS = 0x7;

    /**
     * Clear access control permission that allows all information to be cleared from an event log.
     * https://msdn.microsoft.com/en-us/library/windows/desktop/aa385781(v=vs.85).aspx
     */
    public static final int EVT_CLEAR_ACCESS = 0x4;
}
