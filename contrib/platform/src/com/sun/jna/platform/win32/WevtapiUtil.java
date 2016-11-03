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

import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Winevt.EVT_HANDLE;
import com.sun.jna.platform.win32.Winevt.EVT_VARIANT;
import com.sun.jna.ptr.IntByReference;

/**
 * Wevtapi Utilities Class
 *
 * @author Minoru Sakamoto
 */
public abstract class WevtapiUtil {

    /**
     * Gets a text message that contains the extended error information for the current error.
     *
     * @return error information text
     */
    public static String EvtGetExtendedStatus() {
        int errorCode;
        IntByReference buffUsed = new IntByReference();
        errorCode = Wevtapi.INSTANCE.EvtGetExtendedStatus(0, null, buffUsed);
        if (errorCode != WinError.ERROR_SUCCESS && errorCode != WinError.ERROR_INSUFFICIENT_BUFFER) {
            throw new Win32Exception(errorCode);
        }
        if (buffUsed.getValue() == 0) {
            return "";
        }
        Memory mem = new Memory(buffUsed.getValue() * 2);
        errorCode = Wevtapi.INSTANCE.EvtGetExtendedStatus((int) mem.size(), mem, buffUsed);
        if (errorCode != WinError.ERROR_SUCCESS) {
            throw new Win32Exception(errorCode);
        }
        return mem.getWideString(0);
    }


    /**
     * Renders an XML fragment based on the rendering context that you specify.
     *
     * @param context       [in] A handle to the rendering context that the {@link Wevtapi#EvtCreateRenderContext}
     *                      function returns. This parameter must be set to NULL if the Flags parameter is set to
     *                      {@link Winevt.EVT_RENDER_FLAGS#EvtRenderEventXml} or
     *                      {@link Winevt.EVT_RENDER_FLAGS#EvtRenderBookmark}.
     * @param fragment      [in] A handle to an event or to a bookmark. Set this parameter to a bookmark handle
     *                      if the Flags parameter is set to {@link Winevt.EVT_RENDER_FLAGS#EvtRenderEventXml};
     *                      otherwise, set to an event handle.
     * @param flags         [in] A flag that identifies what to render. For example, the entire event or specific
     *                      properties of the event. For possible values,see the {@link Winevt.EVT_RENDER_FLAGS}
     *                      enumeration.
     * @param propertyCount [out] The number of the properties in the Buffer parameter if the Flags parameter is set
     *                      to {@link Winevt.EVT_RENDER_FLAGS#EvtRenderEventValues}; otherwise, zero.
     * @return A caller-allocated buffer that will receive the rendered output. The contents is a null-terminated
     * Unicode string if the Flags parameter is set to {@link Winevt.EVT_RENDER_FLAGS#EvtRenderEventXml} or
     * {@link Winevt.EVT_RENDER_FLAGS#EvtRenderBookmark}. Otherwise, if Flags is set to {@link Winevt.EVT_RENDER_FLAGS#EvtRenderEventValues},
     * the buffer contains an array of EVT_VARIANT structures; one for each property specified by the rendering context.
     * The PropertyCount parameter contains the number of elements in the array. the {@link Kernel32#GetLastError} function.
     */
    public static Memory EvtRender(EVT_HANDLE context, EVT_HANDLE fragment, int flags, IntByReference propertyCount) {
        boolean result;
        IntByReference buffUsed = new IntByReference();
        result = Wevtapi.INSTANCE.EvtRender(context, fragment, flags, 0, null, buffUsed, propertyCount);
        int errorCode = Kernel32.INSTANCE.GetLastError();
        if ((!result) && errorCode != Kernel32.ERROR_INSUFFICIENT_BUFFER) {
            throw new Win32Exception(errorCode);
        }
        Memory mem = new Memory(buffUsed.getValue());
        result = Wevtapi.INSTANCE.EvtRender(context, fragment, flags, (int) mem.size(), mem, buffUsed, propertyCount);
        if (!result) {
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
        }
        return mem;
    }

    /**
     * Formats a message string.
     *
     * @param publisherMetadata [in] A handle to the provider's metadata that
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
     * @param event             [in] A handle to an event. The Flags parameter specifies the message string in
     *                          the event that you want to format. This parameter must be NULL if the Flags parameter
     *                          is set to EvtFormatMessageId.
     * @param messageId         [in] The resource identifier of the message string that you want to format.
     *                          To get the resource identifier for a message string, call
     *                          the {@link Wevtapi#EvtGetPublisherMetadataProperty} function. Set this parameter only
     *                          if the Flags parameter is set to EvtFormatMessageId.
     * @param valueCount        [in] The number of values in the Values parameter.
     * @param values            [in] An array of insertion values to use when formatting the event's message string.
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
     * @param flags             [in] A flag that specifies the message string in the event to format. For possible
     *                          values, see the {@link Winevt.EVT_FORMAT_MESSAGE_FLAGS} enumeration.
     * @return A caller-allocated buffer that will receive the formatted message string.
     */
    public static Memory EvtFormatMessage(EVT_HANDLE publisherMetadata, EVT_HANDLE event, int messageId, int valueCount,
                                          EVT_VARIANT[] values, int flags) {
        boolean result;
        IntByReference bufferUsed = new IntByReference();
        result = Wevtapi.INSTANCE.EvtFormatMessage(publisherMetadata, event, messageId, valueCount, values, flags, 0, null, bufferUsed);
        int errorCode = Kernel32.INSTANCE.GetLastError();
        if ((!result) && errorCode != Kernel32.ERROR_INSUFFICIENT_BUFFER) {
            throw new Win32Exception(errorCode);
        }

        Memory buffer = new Memory(bufferUsed.getValue());
        result = Wevtapi.INSTANCE.EvtFormatMessage(publisherMetadata, event, messageId, valueCount, values, flags,
                (int) buffer.size(), buffer, bufferUsed);
        if (!result) {
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
        }
        return buffer;
    }

    /**
     * Gets the specified channel configuration property.
     *
     * @param channelHandle [in] A handle to the channel's configuration properties that
     *                      the {@link Wevtapi#EvtOpenChannelConfig} function returns.
     * @param propertyId    [in] The identifier of the channel property to retrieve. For a list of property
     *                      identifiers, see the {@link Winevt.EVT_CHANNEL_CONFIG_PROPERTY_ID} enumeration.
     * @return EVT_VARIANT(already reading from native memory)
     */
    public static EVT_VARIANT EvtGetChannelConfigProperty(EVT_HANDLE channelHandle, int propertyId) {
        IntByReference propertyValueBufferUsed = new IntByReference();
        boolean result = Wevtapi.INSTANCE.EvtGetChannelConfigProperty(channelHandle, propertyId, 0, 0, null, propertyValueBufferUsed);
        int errorCode = Kernel32.INSTANCE.GetLastError();
        if ((!result) && errorCode != Kernel32.ERROR_INSUFFICIENT_BUFFER) {
            throw new Win32Exception(errorCode);
        }

        Memory propertyValueBuffer = new Memory(propertyValueBufferUsed.getValue());
        result = Wevtapi.INSTANCE.EvtGetChannelConfigProperty(channelHandle, propertyId, 0, (int) propertyValueBuffer.size(),
                propertyValueBuffer, propertyValueBufferUsed);
        if (!result) {
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
        }
        EVT_VARIANT resultEvt = new EVT_VARIANT(propertyValueBuffer);
        resultEvt.read();
        return resultEvt;
    }

    /**
     * Gets the identifier of a provider from the enumerator.
     *
     * @param publisherEnum [in] A handle to the registered providers enumerator that
     *                      the {@link Wevtapi#EvtOpenPublisherEnum} function returns.
     * @return A caller-allocated buffer that will receive the name of the registered provider.
     */
    public static Memory EvtNextPublisherId(EVT_HANDLE publisherEnum) {
        IntByReference publisherIdBufferUsed = new IntByReference();
        boolean result = Wevtapi.INSTANCE.EvtNextPublisherId(publisherEnum, 0, null, publisherIdBufferUsed);
        int errorCode = Kernel32.INSTANCE.GetLastError();
        if ((!result) && errorCode != Kernel32.ERROR_INSUFFICIENT_BUFFER) {
            throw new Win32Exception(errorCode);
        }

        Memory publisherIdBuffer = new Memory(publisherIdBufferUsed.getValue() * 2);
        result = Wevtapi.INSTANCE.EvtNextPublisherId(publisherEnum, (int) publisherIdBuffer.size(), publisherIdBuffer, publisherIdBufferUsed);
        if (!result) {
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
        }
        return publisherIdBuffer;
    }

    /**
     * Gets the specified provider metadata property.
     *
     * @param PublisherMetadata [in] A handle to the metadata that
     *                          the {@link Wevtapi#EvtOpenPublisherMetadata} function returns.
     * @param PropertyId        [in] The identifier of the metadata property to retrieve.
     *                          For a list of property identifiers, see
     *                          the {@link Winevt.EVT_PUBLISHER_METADATA_PROPERTY_ID} enumeration.
     * @param Flags             [in] Reserved. Must be zero.
     * @return A caller-allocated buffer that will receive the metadata property. The buffer contains an EVT_VARIANT object.
     */
    public static Memory EvtGetPublisherMetadataProperty(EVT_HANDLE PublisherMetadata, int PropertyId, int Flags) {
        IntByReference publisherMetadataPropertyBufferUsed = new IntByReference();
        boolean result = Wevtapi.INSTANCE.EvtGetPublisherMetadataProperty(PublisherMetadata, PropertyId, Flags, 0, null,
                publisherMetadataPropertyBufferUsed);
        int errorCode = Kernel32.INSTANCE.GetLastError();
        if ((!result) && errorCode != Kernel32.ERROR_INSUFFICIENT_BUFFER) {
            throw new Win32Exception(errorCode);
        }
        Memory publisherMetadataPropertyBuffer = new Memory(publisherMetadataPropertyBufferUsed.getValue());
        result = Wevtapi.INSTANCE.EvtGetPublisherMetadataProperty(PublisherMetadata, PropertyId, Flags,
                (int) publisherMetadataPropertyBuffer.size(), publisherMetadataPropertyBuffer, publisherMetadataPropertyBufferUsed);
        if (!result) {
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
        }
        return publisherMetadataPropertyBuffer;
    }

}