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

import com.sun.jna.Callback;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Winevt.EVT_HANDLE;
import com.sun.jna.platform.win32.Winevt.EVT_VARIANT;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;

/**
 * wevtapi.dll Interface
 *
 * @author Minoru Sakamoto
 */
public interface Wevtapi extends StdCallLibrary {
    Wevtapi INSTANCE = (Wevtapi) Native.loadLibrary("wevtapi", Wevtapi.class, W32APIOptions.UNICODE_OPTIONS);

    /**
     * Establishes a connection to a remote computer that you can use when calling the other Windows Event Log functions.
     * https://msdn.microsoft.com/en-us/library/windows/desktop/aa385462(v=vs.85).aspx
     *
     * @param LoginClass [in] The connection method to use to connect to the remote computer. For possible values,
     *                   see the {@link Winevt.EVT_LOGIN_CLASS} enumeration.
     * @param Login      [in] A EVT_RPC_LOGIN structure that identifies the remote computer that you want to connect
     *                   to, the user's credentials, and the type of authentication to use when connecting.
     * @param Timeout    [in] Reserved. Must be zero.
     * @param Flags      [in]Reserved. Must be zero.
     * @return If successful, the function returns a session handle that you can use to access event log information
     * on the remote computer; otherwise, NULL. If NULL, call {@link Kernel32#GetLastError} function to get
     * the error code.
     */
    EVT_HANDLE EvtOpenSession(int LoginClass, Winevt.EVT_RPC_LOGIN Login, int Timeout, int Flags);

    /**
     * Closes an open handle.
     * https://msdn.microsoft.com/en-us/library/windows/desktop/aa385344(v=vs.85).aspx
     *
     * @param Object [in] An open event handle to close.
     * @return True The function succeeded, False The function failed. To get the error code,
     * call the {@link Kernel32#GetLastError} function.
     */
    boolean EvtClose(EVT_HANDLE Object);

    /**
     * Cancels all pending operations on a handle.
     * https://msdn.microsoft.com/en-us/library/windows/desktop/aa385335(v=vs.85).aspx
     *
     * @param Object The handle whose operation you want to cancel. You can cancel the following operations:
     *               <ul>
     *               <li>{@link Wevtapi#EvtClearLog}</li>
     *               <li>{@link Wevtapi#EvtExportLog}</li>
     *               <li>{@link Wevtapi#EvtNext}</li>
     *               <li>{@link Wevtapi#EvtQuery}</li>
     *               <li>{@link Wevtapi#EvtSeek}</li>
     *               <li>{@link Wevtapi#EvtSubscribe}</li>
     *               </ul>
     *               To cancel the {@link Wevtapi#EvtClearLog}, {@link Wevtapi#EvtExportLog}, {@link Wevtapi#EvtQuery},
     *               and EvtSubscribe operations, you must pass the session handle. To specify the default
     *               session (local session), set this parameter to NULL.
     * @return True The function succeeded, False The function failed. To get the error code, call
     * the {@link Kernel32#GetLastError} function.
     */
    boolean EvtCancel(EVT_HANDLE Object);

    /**
     * Gets a text message that contains the extended error information for the current error.
     * https://msdn.microsoft.com/en-us/library/windows/desktop/aa385380(v=vs.85).aspx
     *
     * @param BufferSize [in] The size of the Buffer buffer, in characters.
     * @param Buffer     [in] A caller-allocated string buffer that will receive the extended error information.
     *                   You can set this parameter to NULL to determine the required buffer size.
     * @param BufferUsed [out] The size, in characters, of the caller-allocated buffer that the function used or
     *                   the required buffer size if the function fails with ERROR_INSUFFICIENT_BUFFER.
     * @return The return value is ERROR_SUCCESS if the call succeeded; otherwise, a Win32 error code.
     */
    int EvtGetExtendedStatus(int BufferSize, char[] Buffer, IntByReference BufferUsed);

    /**
     * Runs a query to retrieve events from a channel or log file that match the specified query criteria.
     * https://msdn.microsoft.com/en-us/library/windows/desktop/aa385466(v=vs.85).aspx
     *
     * @param Session [in] A remote session handle that the {@link Wevtapi#EvtOpenSession} function returns.
     *                Set to NULL to query for events on the local computer.
     * @param Path    [in] The name of the channel or the full path to a log file that contains the events that
     *                you want to query. You can specify an .evt, .evtx, or.etl log file. The path is required
     *                if the Query parameter contains an XPath query; the path is ignored if the Query parameter
     *                contains a structured XML query and the query specifies the path.
     * @param Query   [in] A query that specifies the types of events that you want to retrieve. You can specify
     *                an XPath 1.0 query or structured XML query. If your XPath contains more than 20 expressions,
     *                use a structured XML query. To receive all events, set this parameter to NULL or "*".
     * @param Flags   [in] One or more flags that specify the order that you want to receive the events and
     *                whether you are querying against a channel or log file. For possible values,
     *                see the {@link Winevt.EVT_QUERY_FLAGS} enumeration.
     * @return A handle to the query results if successful; otherwise, NULL. If the function returns NULL,
     * call the {@link Kernel32#GetLastError} function to get the error code.
     */
    EVT_HANDLE EvtQuery(EVT_HANDLE Session, String Path, String Query, int Flags);

    /**
     * Gets the next event from the query or subscription results.
     * https://msdn.microsoft.com/en-us/library/windows/desktop/aa385405(v=vs.85).aspx
     *
     * @param ResultSet      [in] The handle to a query or subscription result set that
     *                       the {@link Wevtapi#EvtQuery} function or the {@link Wevtapi#EvtSubscribe} function returns.
     * @param EventArraySize [in] The number of elements in the EventArray array. The function will try to retrieve
     *                       this number of elements from the result set.
     * @param EventArray     [in] A pointer to an array of handles that will be set to the handles to the events from
     *                       the result set.
     * @param Timeout        [in] The number of milliseconds that you are willing to wait for a result.
     *                       Set to INFINITE to indicate no time-out value. If the time-out expires, the last error is
     *                       set to ERROR_TIMEOUT.
     * @param Flags          [in] Reserved. Must be zero.
     * @param Returned       [out] The number of handles in the array that are set.
     * @return True The function succeeded, False The function failed. To get the error code, call
     * the {@link Kernel32#GetLastError} function.
     */
    boolean EvtNext(EVT_HANDLE ResultSet, int EventArraySize, EVT_HANDLE[] EventArray, int Timeout, int Flags,
                    IntByReference Returned);

    /**
     * Seeks to a specific event in a query result set.
     * https://msdn.microsoft.com/en-us/library/windows/desktop/aa385481(v=vs.85).aspx
     *
     * @param ResultSet [in] The handle to a query result set that
     *                  the {@link Wevtapi#EvtQuery} function returns.
     * @param Position  [in] The zero-based offset to an event in the result set. The flag that you specify
     *                  in the Flags parameter indicates the beginning relative position in the result set from
     *                  which to seek. For example, you can seek from the beginning of the results or from the end of
     *                  the results. Set to 0 to move to the relative position specified by the flag.
     * @param Bookmark  [in] A handle to a bookmark that the {@link Wevtapi#EvtCreateBookmark}function returns.
     *                  The bookmark identifies an event in the result set to which you want to seek.
     *                  Set this parameter only if the Flags parameter has the EvtSeekRelativeToBookmark flag set.
     * @param Timeout   [in] Reserved. Must be zero.
     * @param Flags     [in] One or more flags that indicate the relative position in the result set from which to seek.
     *                  For possible values, see the {@link Winevt.EVT_SEEK_FLAGS} enumeration.
     * @return True The function was successful, False The function failed. To get the error code, call
     * the {@link Kernel32#GetLastError} function.
     */
    boolean EvtSeek(EVT_HANDLE ResultSet, long Position, EVT_HANDLE Bookmark, int Timeout, int Flags);

    /**
     * Creates a subscription that will receive current and future events from a channel or log file
     * that match the specified query criteria.
     * https://msdn.microsoft.com/en-us/library/windows/desktop/aa385487(v=vs.85).aspx
     *
     * @param Session     [in] A remote session handle that the {@link Wevtapi#EvtOpenSession} function returns.
     *                    Set to NULL to subscribe to events on the local computer.
     * @param SignalEvent [in] The handle to an event object that the service will signal when new events are
     *                    available that match your query criteria. This parameter must be NULL if the Callback
     *                    parameter is not NULL.
     * @param ChannelPath [in] The name of the Admin or Operational channel that contains the events that you want to
     *                    subscribe to (you cannot subscribe to Analytic or Debug channels). The path is required
     *                    if the Query parameter contains an XPath query; the path is ignored if the Query parameter
     *                    contains a structured XML query.
     * @param Query       [in] A query that specifies the types of events that you want the subscription service to
     *                    return. You can specify an XPath 1.0 query or structured XML query. If your XPath contains
     *                    more than 20 expressions, use a structured XML query. To receive all events, set this
     *                    parameter to NULL or "*".
     * @param Bookmark    [in] A handle to a bookmark that identifies the starting point for the subscription. To get
     *                    a bookmark handle, call the {@link Wevtapi#EvtCreateBookmark} function. You must set
     *                    this parameter if the Flags parameter contains the EvtSubscribeStartAfterBookmark flag;
     *                    otherwise, NULL.
     * @param Context     [in] A caller-defined context value that the subscription service will pass to the specified
     *                    callback each time it delivers an event.
     * @param Callback    [in] Pointer to your EVT_SUBSCRIBE_CALLBACK callback function that will receive
     *                    the subscription events. This parameter must be NULL if the SignalEvent parameter is not NULL.
     * @param Flags       [in] One or more flags that specify when to start subscribing to events. For example, if you
     *                    specify {@link Winevt.EVT_SUBSCRIBE_FLAGS#EvtSubscribeStartAtOldestRecord}, the service will
     *                    retrieve all current and future events that match your query criteria; however, if you specify
     *                    {@link Winevt.EVT_SUBSCRIBE_FLAGS#EvtSubscribeToFutureEvents}, the service returns only
     *                    future events that match your query criteria. For possible values,see
     *                    the {@link Winevt.EVT_SUBSCRIBE_FLAGS} enumeration.
     * @return A handle to the subscription if successful; otherwise, NULL. If the function returns NULL,
     * call the {@link Kernel32#GetLastError} function to get the error code.
     * You must call the EvtClose function with the subscription handle when done.
     */
    EVT_HANDLE EvtSubscribe(EVT_HANDLE Session, EVT_HANDLE SignalEvent, String ChannelPath, String Query, EVT_HANDLE Bookmark,
                            Pointer Context, Callback Callback, int Flags);

    /**
     * Creates a context that specifies the information in the event that you want to render.
     * https://msdn.microsoft.com/en-us/library/windows/desktop/aa385352(v=vs.85).aspx
     *
     * @param ValuePathsCount [in] The number of XPath expressions in the ValuePaths parameter.
     * @param ValuePaths      [in] An array of XPath expressions that uniquely identify a node or attribute in
     *                        the event that you want to render. The expressions must not contain the OR or AND operator.
     *                        Set to NULL if the {@link Winevt.EVT_RENDER_CONTEXT_FLAGS#EvtRenderContextValues} context
     *                        flag is not set in the Flags parameter.
     * @param Flags           [in] One or more flags that identify the information in the event that you want to render.
     *                        For example, the system information, user information, or specific values.
     *                        For possible values, see the {@link Winevt.EVT_RENDER_CONTEXT_FLAGS} enumeration.
     * @return A context handle that you use when calling the {@link Wevtapi#EvtRender}function to render the contents
     * of an event; otherwise, NULL. If NULL, call the {@link Kernel32#GetLastError} function to get the error code.
     */
    EVT_HANDLE EvtCreateRenderContext(int ValuePathsCount, String[] ValuePaths, int Flags);

    /**
     * Renders an XML fragment based on the rendering context that you specify.
     * https://msdn.microsoft.com/en-us/library/windows/desktop/aa385471(v=vs.85).aspx
     *
     * @param Context       [in] A handle to the rendering context that the {@link Wevtapi#EvtCreateRenderContext}
     *                      function returns. This parameter must be set to NULL if the Flags parameter is set to
     *                      {@link Winevt.EVT_RENDER_FLAGS#EvtRenderEventXml} or
     *                      {@link Winevt.EVT_RENDER_FLAGS#EvtRenderBookmark}.
     * @param Fragment      [in] A handle to an event or to a bookmark. Set this parameter to a bookmark handle
     *                      if the Flags parameter is set to {@link Winevt.EVT_RENDER_FLAGS#EvtRenderEventXml};
     *                      otherwise, set to an event handle.
     * @param Flags         [in] A flag that identifies what to render. For example, the entire event or specific
     *                      properties of the event. For possible values,see the {@link Winevt.EVT_RENDER_FLAGS}
     *                      enumeration.
     * @param BufferSize    [in] The size of the Buffer buffer, in bytes.
     * @param Buffer        [in] A caller-allocated buffer that will receive the rendered output. The contents is
     *                      a null-terminated Unicode string if the Flags parameter is set to
     *                      {@link Winevt.EVT_RENDER_FLAGS#EvtRenderEventXml} or
     *                      {@link Winevt.EVT_RENDER_FLAGS#EvtRenderBookmark}. Otherwise, if Flags is set to
     *                      {@link Winevt.EVT_RENDER_FLAGS#EvtRenderEventValues}, the buffer
     *                      contains an array of EVT_VARIANT structures; one for each property specified by
     *                      the rendering context. The PropertyCount parameter contains the number of elements
     *                      in the array.
     * @param BufferUsed    [out] The size, in bytes, of the caller-allocated buffer that the function used or
     *                      the required buffer size if the function fails with ERROR_INSUFFICIENT_BUFFER.
     * @param PropertyCount [out] The number of the properties in the Buffer parameter if the Flags parameter is set
     *                      to {@link Winevt.EVT_RENDER_FLAGS#EvtRenderEventValues}; otherwise, zero.
     * @return True The function was successful, False The function failed. To get the error code, call
     * the {@link Kernel32#GetLastError} function.
     */
    boolean EvtRender(EVT_HANDLE Context, EVT_HANDLE Fragment, int Flags, int BufferSize, Pointer Buffer,
                      IntByReference BufferUsed, IntByReference PropertyCount);

    /**
     * Formats a message string.
     * https://msdn.microsoft.com/en-us/library/windows/desktop/aa385359(v=vs.85).aspx
     *
     * @param PublisherMetadata [in] A handle to the provider's metadata that
     *                          the {@link Wevtapi#EvtOpenPublisherMetadata} function returns. The handle acts as
     *                          a formatting context for the event or message identifier.
     *                          <p>
     *                          You can set this parameter to NULL if the Windows Event Collector service forwarded
     *                          the event. Forwarded events include a RenderingInfo section that contains the rendered
     *                          message strings. You can also set this parameter to NULL if the event property that
     *                          you are formatting is defined in the Winmeta.xml file (for example, if level is set
     *                          to win:Error). In the latter case, the service uses the Winmeta provider as
     *                          the formatting context and will format only those message strings that you reference
     *                          in your event that are defined in the Winmeta.xml file.
     * @param Event             [in] A handle to an event. The Flags parameter specifies the message string in
     *                          the event that you want to format. This parameter must be NULL if the Flags parameter
     *                          is set to EvtFormatMessageId.
     * @param MessageId         [in] The resource identifier of the message string that you want to format.
     *                          To get the resource identifier for a message string, call
     *                          the {@link Wevtapi#EvtGetPublisherMetadataProperty} function. Set this parameter only
     *                          if the Flags parameter is set to EvtFormatMessageId.
     * @param ValueCount        [in] The number of values in the Values parameter.
     * @param Values            [in] An array of insertion values to use when formatting the event's message string.
     *                          Typically, you set this parameter to NULL and the function gets the insertion values
     *                          from the event data itself. You would use this parameter to override the default
     *                          behavior and supply the insertion values to use. For example, you might use this
     *                          parameter if you wanted to resolve a SID to a principal name before inserting the value.
     *                          <p>
     *                          To override the insertion values, the Flags parameter must be set to
     *                          {@link Winevt.EVT_FORMAT_MESSAGE_FLAGS#EvtFormatMessageEvent},
     *                          {@link Winevt.EVT_FORMAT_MESSAGE_FLAGS#EvtFormatMessageXml}, or
     *                          {@link Winevt.EVT_FORMAT_MESSAGE_FLAGS#EvtFormatMessageId}, If Flags is set to
     *                          {@link Winevt.EVT_FORMAT_MESSAGE_FLAGS#EvtFormatMessageId}, the resource identifier
     *                          must identify the event's message string.
     * @param Flags             [in] A flag that specifies the message string in the event to format. For possible
     *                          values, see the {@link Winevt.EVT_FORMAT_MESSAGE_FLAGS} enumeration.
     * @param BufferSize        [in] The size of the Buffer buffer, in characters.
     * @param Buffer            [in] A caller-allocated buffer that will receive the formatted message string.
     *                          You can set this parameter to NULL to determine the required buffer size.
     * @param BufferUsed        [out] The size, in characters of the caller-allocated buffer that the function used
     *                          or the required buffer size if the function fails with ERROR_INSUFFICIENT_BUFFER.
     * @return True The function was successful, False The function failed. To get the error code, call
     * the {@link Kernel32#GetLastError} function.
     */
    boolean EvtFormatMessage(EVT_HANDLE PublisherMetadata, EVT_HANDLE Event, int MessageId, int ValueCount, EVT_VARIANT[] Values,
                             int Flags, int BufferSize, char[] Buffer, IntByReference BufferUsed);

    /**
     * Gets a handle to a channel or log file that you can then use to get information about the channel or log file.
     * https://msdn.microsoft.com/en-us/library/windows/desktop/aa385447(v=vs.85).aspx
     *
     * @param Session [in] A remote session handle that the {@link Wevtapi#EvtOpenSession} function returns.
     *                Set to NULL to open a channel or log on the local computer.
     * @param Path    [in] The name of the channel or the full path to the exported log file.
     * @param Flags   [in] A flag that determines whether the Path parameter points to a log file or channel.
     *                For possible values, see the {@link Winevt.EVT_OPEN_LOG_FLAGS} enumeration.
     * @return If successful, the function returns a handle to the file or channel;
     * otherwise, NULL. If NULL, call {@link Kernel32#GetLastError} function to get the error code.
     */
    EVT_HANDLE EvtOpenLog(EVT_HANDLE Session, String Path, int Flags);

    /**
     * Gets information about a channel or log file.
     * https://msdn.microsoft.com/en-us/library/windows/desktop/aa385385(v=vs.85).aspx
     *
     * @param Log                     [in] A handle to the channel or log file that the {@link Wevtapi#EvtOpenLog}
     *                                function returns.
     * @param PropertyId              [in] The identifier of the property to retrieve. For a list of property
     *                                identifiers, see the {@link Winevt.EVT_LOG_PROPERTY_ID} enumeration.
     * @param PropertyValueBufferSize [in] The size of the PropertyValueBuffer buffer, in bytes.
     * @param PropertyValueBuffer     [in] A caller-allocated buffer that will receive the property value. The buffer
     *                                contains an EVT_VARIANT object. You can set this parameter to NULL to determine
     *                                the required buffer size.
     * @param PropertyValueBufferUsed [out] The size, in bytes, of the caller-allocated buffer that
     *                                the function used or the required buffer size if the function fails
     *                                with ERROR_INSUFFICIENT_BUFFER.
     * @return True The function was successful, False The function failed. To get the error code, call
     * the {@link Kernel32#GetLastError} function.
     */
    boolean EvtGetLogInfo(EVT_HANDLE Log, int PropertyId, int PropertyValueBufferSize, Pointer PropertyValueBuffer,
                          IntByReference PropertyValueBufferUsed);

    /**
     * Removes all events from the specified channel and writes them to the target log file.
     * https://msdn.microsoft.com/en-us/library/windows/desktop/aa385340(v=vs.85).aspx
     *
     * @param Session        [in, optional] A remote session handle that the {@link Wevtapi#EvtOpenSession} function
     *                       returns. Set to NULL for local channels.
     * @param ChannelPath    [in] The name of the channel to clear.
     * @param TargetFilePath [in, optional] The full path to the target log file that will receive the events.
     *                       Set to NULL to clear the log file and not save the events.
     * @param Flags          [in] Reserved. Must be zero.
     * @return True The function was successful, False The function failed. To get the error code, call
     * the {@link Kernel32#GetLastError} function.
     */
    boolean EvtClearLog(EVT_HANDLE Session, String ChannelPath, String TargetFilePath, int Flags);

    /**
     * Copies events from the specified channel or log file and writes them to the target log file.
     * https://msdn.microsoft.com/en-us/library/windows/desktop/aa385355(v=vs.85).aspx
     *
     * @param Session        [in, optional] A remote session handle that the {@link Wevtapi#EvtOpenSession} function
     *                       returns. Set to NULL for local channels.
     * @param Path           [in] The name of the channel or the full path to a log file that contains the events that
     *                       you want to export. If the Query parameter contains an XPath query, you must specify
     *                       the channel or log file. If the Flags parameter contains
     *                       {@link Winevt.EVT_EXPORTLOG_FLAGS#EvtExportLogFilePath}, you must specify the log file.
     *                       If the Query parameter contains a structured XML query, the channel or path that you
     *                       specify here must match the channel or path in the query. If the Flags parameter contains
     *                       {@link Winevt.EVT_EXPORTLOG_FLAGS#EvtExportLogChannelPath}, this parameter can be NULL
     *                       if the query is a structured XML query that specifies the channel.
     * @param Query          [in] A query that specifies the types of events that you want to export. You can specify
     *                       an XPath 1.0 query or structured XML query. If your XPath contains more than 20 expressions,
     *                       use a structured XML query. To export all events, set this parameter to NULL or "*".
     * @param TargetFilePath [in] The full path to the target log file that will receive the events.
     *                       The target log file must not exist.
     * @param Flags          [in] Flags that indicate whether the events come from a channel or log file. For possible
     *                       values, see the {@link Winevt.EVT_EXPORTLOG_FLAGS#EvtExportLogChannelPath} enumeration.
     * @return True The function was successful, False The function failed. To get the error code, call
     * the {@link Kernel32#GetLastError} function.
     */
    boolean EvtExportLog(EVT_HANDLE Session, String Path, String Query, String TargetFilePath, int Flags);

    /**
     * Adds localized strings to the events in the specified log file.
     * https://msdn.microsoft.com/en-us/library/windows/desktop/aa385232(v=vs.85).aspx
     *
     * @param Session     [in] A remote session handle that the {@link Wevtapi#EvtOpenSession} function returns.
     *                    Set to NULL for local channels.
     * @param LogFilePath [in] The full path to the exported log file that contains the events to localize.
     * @param Locale      [in] The locale to use to localize the strings that the service adds to the events in
     *                    the log file. If zero, the function uses the calling thread's locale. If the provider's
     *                    resources does not contain the locale, the string is empty.
     * @param Flags       [in] Reserved. Must be zero.
     * @return True The function succeeded, False The function failed. To get the error code,
     * call the {@link Kernel32#GetLastError} function.
     */
    boolean EvtArchiveExportedLog(EVT_HANDLE Session, String LogFilePath, int Locale, int Flags);

    /**
     * Gets a handle that you use to enumerate the list of channels that are registered on the computer.
     * https://msdn.microsoft.com/en-us/library/windows/desktop/aa385437(v=vs.85).aspx
     *
     * @param Session [in] A remote session handle that the {@link Wevtapi#EvtOpenSession} function returns.
     *                Set to NULL to enumerate the channels on the local computer.
     * @param Flags   [in] Reserved. Must be zero.
     * @return If successful, the function returns a handle to the list of channel names that are registered on
     * the computer; otherwise, NULL. If NULL, call {@link Kernel32#GetLastError} function to get the error code.
     */
    EVT_HANDLE EvtOpenChannelEnum(EVT_HANDLE Session, int Flags);

    /**
     * Gets a channel name from the enumerator.
     * https://msdn.microsoft.com/en-us/library/windows/desktop/aa385412(v=vs.85).aspx
     *
     * @param ChannelEnum           [in] A handle to the enumerator that the {@link Wevtapi#EvtOpenChannelEnum}
     *                              function returns.
     * @param ChannelPathBufferSize [in] The size of the ChannelPathBuffer buffer, in characters.
     * @param ChannelPathBuffer     [in] A caller-allocated buffer that will receive the name of the channel.
     *                              You can set this parameter to NULL to determine the required buffer size.
     * @param ChannelPathBufferUsed [out] The size, in characters, of the caller-allocated buffer that the function
     *                              used or the required buffer size if the function fails with ERROR_INSUFFICIENT_BUFFER.
     * @return True The function succeeded, False The function failed. To get the error code,
     * call the {@link Kernel32#GetLastError} function.
     */
    boolean EvtNextChannelPath(EVT_HANDLE ChannelEnum, int ChannelPathBufferSize, char[] ChannelPathBuffer,
                               IntByReference ChannelPathBufferUsed);

    /**
     * Gets a handle that you use to read or modify a channel's configuration property.
     * https://msdn.microsoft.com/en-us/library/windows/desktop/aa385430(v=vs.85).aspx
     *
     * @param Session     [in] A remote session handle that the {@link Wevtapi#EvtOpenSession} function returns.
     *                    Set to NULL to access a channel on the local computer.
     * @param ChannelPath [in] The name of the channel to access.
     * @param Flags       [in] Reserved. Must be zero.
     * @return If successful, the function returns a handle to the channel's configuration;
     * otherwise, NULL. If NULL, call GetLastError function to get the error code.
     */
    EVT_HANDLE EvtOpenChannelConfig(EVT_HANDLE Session, String ChannelPath, int Flags);

    /**
     * Saves the changes made to a channel's configuration.
     * https://msdn.microsoft.com/en-us/library/windows/desktop/aa385477(v=vs.85).aspx
     *
     * @param ChannelConfig [in] A handle to the channel's configuration properties that
     *                      the {@link Wevtapi#EvtOpenChannelConfig} function returns.
     * @param Flags         [in] Reserved. Must be zero.
     * @return True The function succeeded, False The function failed. To get the error code,
     * call the {@link Kernel32#GetLastError} function.
     */
    boolean EvtSaveChannelConfig(EVT_HANDLE ChannelConfig, int Flags);

    /**
     * Sets the specified configuration property of a channel.
     * https://msdn.microsoft.com/en-us/library/windows/desktop/aa385484(v=vs.85).aspx
     *
     * @param ChannelConfig [in] A handle to the channel's configuration properties that
     *                      the {@link Wevtapi#EvtOpenChannelConfig} function returns.
     * @param PropertyId    [in] The identifier of the channel property to set. For a list of property identifiers,
     *                      see the {@link Winevt.EVT_CHANNEL_CONFIG_PROPERTY_ID} enumeration.
     * @param Flags         [in] Reserved. Must be zero.
     * @param PropertyValue [in] The property value to set.
     *                      A caller-allocated buffer that contains the new configuration property value.
     *                      The buffer contains an EVT_VARIANT object. Be sure to set the configuration value and
     *                      variant type.
     * @return True The function succeeded, False The function failed. To get the error code,
     * call the {@link Kernel32#GetLastError} function.
     */
    boolean EvtSetChannelConfigProperty(EVT_HANDLE ChannelConfig, int PropertyId, int Flags, EVT_VARIANT PropertyValue);

    /**
     * Gets the specified channel configuration property.
     * https://msdn.microsoft.com/en-us/library/windows/desktop/aa385362(v=vs.85).aspx
     *
     * @param ChannelConfig           [in] A handle to the channel's configuration properties that
     *                                the {@link Wevtapi#EvtOpenChannelConfig} function returns.
     * @param PropertyId              [in] The identifier of the channel property to retrieve. For a list of property
     *                                identifiers, see the {@link Winevt.EVT_CHANNEL_CONFIG_PROPERTY_ID} enumeration.
     * @param Flags                   [in] Reserved. Must be zero.
     * @param PropertyValueBufferSize [in] The size of the PropertyValueBuffer buffer, in bytes.
     * @param PropertyValueBuffer     [in] A caller-allocated buffer that will receive the configuration property.
     *                                The buffer contains an EVT_VARIANT object. You can set this parameter to NULL
     *                                to determine the required buffer size.
     * @param PropertyValueBufferUsed [out] The size, in bytes, of the caller-allocated buffer that the function
     *                                used or the required buffer size if the function fails with ERROR_INSUFFICIENT_BUFFER.
     * @return True The function succeeded, False The function failed. To get the error code,
     * call the {@link Kernel32#GetLastError} function.
     */
    boolean EvtGetChannelConfigProperty(EVT_HANDLE ChannelConfig, int PropertyId, int Flags, int PropertyValueBufferSize,
                                        Pointer PropertyValueBuffer, IntByReference PropertyValueBufferUsed);

    /**
     * Gets a handle that you use to enumerate the list of registered providers on the computer.
     * https://msdn.microsoft.com/en-us/library/windows/desktop/aa385451(v=vs.85).aspx
     *
     * @param Session [in] A remote session handle that the {@link Wevtapi#EvtOpenSession} function returns.
     *                Set to NULL to enumerate the registered providers on the local computer.
     * @param Flags   [in] Reserved. Must be zero.
     * @return If successful, the function returns a handle to the list of registered providers;
     * otherwise, NULL. If NULL, call {@link Kernel32#GetLastError} function to get the error code.
     */
    EVT_HANDLE EvtOpenPublisherEnum(EVT_HANDLE Session, int Flags);

    /**
     * Gets the identifier of a provider from the enumerator.
     * https://msdn.microsoft.com/en-us/library/windows/desktop/aa385425(v=vs.85).aspx
     *
     * @param PublisherEnum         [in] A handle to the registered providers enumerator that
     *                              the {@link Wevtapi#EvtOpenPublisherEnum} function returns.
     * @param PublisherIdBufferSize [in] The size of the PublisherIdBuffer buffer, in characters.
     * @param PublisherIdBuffer     [in] A caller-allocated buffer that will receive the name of the registered
     *                              provider. You can set this parameter to NULL to determine the required buffer size.
     * @param PublisherIdBufferUsed [out] The size, in characters, of the caller-allocated buffer that the function
     *                              used or the required buffer size if the function fails with ERROR_INSUFFICIENT_BUFFER.
     * @return If successful, the function returns a handle to the list of registered providers;
     * otherwise, NULL. If NULL, call {@link Kernel32#GetLastError} function to get the error code.
     */
    boolean EvtNextPublisherId(EVT_HANDLE PublisherEnum, int PublisherIdBufferSize, char[] PublisherIdBuffer,
                               IntByReference PublisherIdBufferUsed);

    /**
     * Gets a handle that you use to read the specified provider's metadata.
     * https://msdn.microsoft.com/en-us/library/windows/desktop/aa385458(v=vs.85).aspx
     *
     * @param EvtHandleSession  [in, optional] A remote session handle that the {@link Wevtapi#EvtOpenSession}
     *                          function returns. Set to NULL to get the metadata for a provider on the local computer.
     * @param PublisherIdentity [in] The name of the provider. To enumerate the names of the providers registered on
     *                          the computer, call the {@link Wevtapi#EvtOpenPublisherEnum} function.
     * @param LogFilePath       [in, optional] The full path to an archived log file that contains the events that
     *                          the provider logged. An archived log file also contains the provider's metadata. Use
     *                          this parameter when the provider is not registered on the local computer. Set to NULL
     *                          when reading the metadata from a registered provider..
     * @param Locale            [in] The locale identifier to use when accessing the localized metadata from
     *                          the provider. To create the locale identifier, use the MAKELCID macro. Set to 0 to use
     *                          the locale identifier of the calling thread.
     * @param Flags             [in] Reserved. Must be zero.
     * @return If successful, the function returns a handle to the provider's metadata;
     * otherwise, NULL. If NULL, call {@link Kernel32#GetLastError} function to get the error code.
     */
    EVT_HANDLE EvtOpenPublisherMetadata(EVT_HANDLE EvtHandleSession, String PublisherIdentity, String LogFilePath, int Locale, int Flags);

    /**
     * Gets the specified provider metadata property.
     * https://msdn.microsoft.com/en-us/library/windows/desktop/aa385399(v=vs.85).aspx
     *
     * @param PublisherMetadata                   [in] A handle to the metadata that
     *                                            the {@link Wevtapi#EvtOpenPublisherMetadata} function returns.
     * @param PropertyId                          [in] The identifier of the metadata property to retrieve.
     *                                            For a list of property identifiers, see
     *                                            the {@link Winevt.EVT_PUBLISHER_METADATA_PROPERTY_ID} enumeration.
     * @param Flags                               [in] Reserved. Must be zero.
     * @param PublisherMetadataPropertyBufferSize [in] The size of the PublisherMetadataPropertyBuffer buffer,
     *                                            in bytes.
     * @param PublisherMetadataPropertyBuffer     [in] A caller-allocated buffer that will receive the metadata
     *                                            property. The buffer contains an EVT_VARIANT object. You can set this
     *                                            parameter to NULL to determine the required buffer size.
     * @param PublisherMetadataPropertyBufferUsed [out] The size, in bytes, of the caller-allocated buffer that
     *                                            the function used or the required buffer size if the function fails
     *                                            with ERROR_INSUFFICIENT_BUFFER.
     * @return True The function succeeded, False The function failed. To get the error code,
     * call the {@link Kernel32#GetLastError} function.
     */
    boolean EvtGetPublisherMetadataProperty(EVT_HANDLE PublisherMetadata, int PropertyId, int Flags,
                                            int PublisherMetadataPropertyBufferSize,
                                            Pointer PublisherMetadataPropertyBuffer,
                                            IntByReference PublisherMetadataPropertyBufferUsed);

    /**
     * Gets a handle that you use to enumerate the list of events that the provider defines.
     * https://msdn.microsoft.com/en-us/library/windows/desktop/aa385446(v=vs.85).aspx
     *
     * @param PublisherMetadata [in] A handle to the provider's metadata that
     *                          the {@link Wevtapi#EvtOpenPublisherMetadata} function returns.
     * @param Flags             [in] Reserved. Must be zero.
     * @return If successful, the function returns a handle to the list of events that the provider defines;
     * otherwise, NULL. If NULL, call {@link Kernel32#GetLastError} function to get the error code.
     */
    EVT_HANDLE EvtOpenEventMetadataEnum(EVT_HANDLE PublisherMetadata, int Flags);

    /**
     * Gets an event definition from the enumerator.
     * https://msdn.microsoft.com/en-us/library/windows/desktop/aa385419(v=vs.85).asp
     *
     * @param EventMetadataEnum [in] A handle to the event definition enumerator that
     *                          the {@link Wevtapi#EvtOpenEventMetadataEnum} function returns.
     * @param Flags             [in] Reserved. Must be zero.
     * @return If successful, the function returns a handle to the event's metadata;
     * otherwise, NULL. If NULL, call {@link Kernel32#GetLastError} function to get the error code.
     */
    EVT_HANDLE EvtNextEventMetadata(EVT_HANDLE EventMetadataEnum, int Flags);

    /**
     * Gets the specified event metadata property.
     * https://msdn.microsoft.com/en-us/library/windows/desktop/aa385376(v=vs.85).aspx
     *
     * @param EventMetadata                   [in] A handle to the event metadata that
     *                                        the {@link Wevtapi#EvtNextEventMetadata} function returns.
     * @param PropertyId                      [in] The identifier of the metadata property to retrieve. For a list of
     *                                        property identifiers, see
     *                                        the {@link Winevt.EVT_EVENT_METADATA_PROPERTY_ID} enumeration.
     * @param Flags                           [in] Reserved. Must be zero.
     * @param EventMetadataPropertyBufferSize [in] The size of the EventMetadataPropertyBuffer buffer, in bytes.
     * @param Buffer                          [in] A caller-allocated buffer that will receive the metadata property.
     *                                        The buffer contains an EVT_VARIANT object. You can set this parameter to
     *                                        NULL to determine the required buffer size.
     * @param BufferUsed                      [out] The size, in bytes, of the caller-allocated buffer that
     *                                        the function used or the required buffer size if the function fails
     *                                        with ERROR_INSUFFICIENT_BUFFER.
     * @return True The function succeeded, False The function failed. To get the error code,
     * call the {@link Kernel32#GetLastError} function.
     */
    boolean EvtGetEventMetadataProperty(EVT_HANDLE EventMetadata, int PropertyId, int Flags,
                                        int EventMetadataPropertyBufferSize, Pointer Buffer, IntByReference BufferUsed);

    /**
     * Gets the number of elements in the array of objects.
     * https://msdn.microsoft.com/en-us/library/windows/desktop/aa385394(v=vs.85).aspx
     *
     * @param ObjectArray     [in] A handle to an array of objects that
     *                        the {@link Wevtapi#EvtGetPublisherMetadataProperty} function returns.
     * @param ObjectArraySize [out] The number of elements in the array.
     * @return True The function succeeded, False The function failed. To get the error code,
     * call the {@link Kernel32#GetLastError} function.
     */
    boolean EvtGetObjectArraySize(Pointer ObjectArray, IntByReference ObjectArraySize);

    /**
     * Gets a provider metadata property from the specified object in the array.
     * https://msdn.microsoft.com/en-us/library/windows/desktop/aa385389(v=vs.85).aspx
     *
     * @param ObjectArray             [in] A handle to an array of objects that
     *                                the {@link Wevtapi#EvtGetPublisherMetadataProperty} function returns.
     * @param PropertyId              [in] The property identifier of the metadata property that you want to get from
     *                                the specified object. For possible values, see the Remarks section of
     *                                {@link Winevt.EVT_PUBLISHER_METADATA_PROPERTY_ID}.
     * @param ArrayIndex              [in] The zero-based index of the object in the array.
     * @param Flags                   [in] Reserved. Must be zero.
     * @param PropertyValueBufferSize [in] The size of the PropertyValueBuffer buffer, in bytes.
     * @param PropertyValueBuffer     [in] A caller-allocated buffer that will receive the metadata property.
     *                                The buffer contains an EVT_VARIANT object. You can set this parameter to NULL
     *                                to determine the required buffer size.
     * @param PropertyValueBufferUsed [in] The size, in bytes, of the caller-allocated buffer that the function used
     *                                or the required buffer size if the function fails with ERROR_INSUFFICIENT_BUFFER.
     * @return True The function succeeded, False The function failed. To get the error code,
     * call the {@link Kernel32#GetLastError} function.
     */
    boolean EvtGetObjectArrayProperty(Pointer ObjectArray, int PropertyId, int ArrayIndex, int Flags,
                                      int PropertyValueBufferSize, Pointer PropertyValueBuffer,
                                      IntByReference PropertyValueBufferUsed);

    /**
     * Gets information about a query that you ran that identifies the list of channels or log files that the query
     * attempted to access. The function also gets a list of return codes that indicates the success or failure of each
     * access.
     * https://msdn.microsoft.com/en-us/library/windows/desktop/aa820606(v=vs.85).aspx
     *
     * @param QueryOrSubscription     [in] A handle to the query that the {@link Wevtapi#EvtQuery} or
     *                                {@link Wevtapi#EvtSubscribe} function returns.
     * @param PropertyId              [in] The identifier of the query information to retrieve. For a list of
     *                                identifiers, see the {@link Winevt.EVT_QUERY_PROPERTY_ID} enumeration.
     * @param PropertyValueBufferSize [in] The size of the PropertyValueBuffer buffer, in bytes.
     * @param PropertyValueBuffer     [in] A caller-allocated buffer that will receive the query information.
     *                                The buffer contains an EVT_VARIANT object. You can set this parameter to NULL to
     *                                determine the required buffer size.
     * @param PropertyValueBufferUsed [out] The size, in bytes, of the caller-allocated buffer that the
     *                                function used or the required buffer size if the function fails
     *                                with ERROR_INSUFFICIENT_BUFFER.
     * @return True The function succeeded, False The function failed. To get the error code,
     * call the {@link Kernel32#GetLastError} function.
     */
    boolean EvtGetQueryInfo(EVT_HANDLE QueryOrSubscription, int PropertyId, int PropertyValueBufferSize,
                            Pointer PropertyValueBuffer, IntByReference PropertyValueBufferUsed);

    /**
     * Creates a bookmark that identifies an event in a channel.
     * https://msdn.microsoft.com/en-us/library/windows/desktop/aa385348(v=vs.85).aspx
     *
     * @param BookmarkXml [in, optional] An XML string that contains the bookmark or NULL if creating a bookmark.
     * @return A handle to the bookmark if the call succeeds;
     * otherwise, NULL. If NULL, call the {@link Kernel32#GetLastError} function to get the error code.
     */
    EVT_HANDLE EvtCreateBookmark(String BookmarkXml);

    /**
     * Updates the bookmark with information that identifies the specified event.
     * https://msdn.microsoft.com/en-us/library/windows/desktop/aa385489(v=vs.85).aspx
     *
     * @param Bookmark [in] The handle to the bookmark to be updated. The {@link Wevtapi#EvtCreateBookmark} function
     *                 returns this handle.
     * @param Event    [in] The handle to the event to bookmark.
     * @return True The function succeeded, False The function failed. To get the error code,
     * call the {@link Kernel32#GetLastError} function.
     */
    boolean EvtUpdateBookmark(EVT_HANDLE Bookmark, EVT_HANDLE Event);

    /**
     * Gets information that identifies the structured XML query that selected the event and the channel or log file
     * that contained the event.
     * https://msdn.microsoft.com/en-us/library/windows/desktop/aa385372(v=vs.85).aspx
     *
     * @param Event                   [in] A handle to an event for which you want to retrieve information.
     * @param PropertyId              [in] A flag that identifies the information to retrieve. For example, the query
     *                                identifier or the path. For possible values,
     *                                see the {@link Winevt.EVT_EVENT_PROPERTY_ID} enumeration.
     * @param PropertyValueBufferSize [in] The size of the PropertyValueBuffer buffer, in bytes.
     * @param PropertyValueBuffer     [in] A caller-allocated buffer that will receive the information. The buffer
     *                                contains an EVT_VARIANT object. You can set this parameter to NULL to determine
     *                                the required buffer size.
     * @param PropertyValueBufferUsed [in] The size, in bytes, of the caller-allocated buffer that the function used
     *                                or the required buffer size if the function fails with ERROR_INSUFFICIENT_BUFFER.
     * @return True The function succeeded, False The function failed. To get the error code,
     * call the {@link Kernel32#GetLastError} function.
     */
    boolean EvtGetEventInfo(EVT_HANDLE Event, int PropertyId, int PropertyValueBufferSize, Pointer PropertyValueBuffer,
                            IntByReference PropertyValueBufferUsed);

}
