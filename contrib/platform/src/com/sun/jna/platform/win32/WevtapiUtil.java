
package com.sun.jna.platform.win32;

import com.sun.jna.Memory;
import com.sun.jna.platform.win32.Winevt.EVT_HANDLE;
import com.sun.jna.platform.win32.Winevt.EVT_VARIANT;
import com.sun.jna.ptr.IntByReference;

public abstract class WevtapiUtil {
    public static EVT_VARIANT EvtGetChannelConfigProperty(EVT_HANDLE channelHandle, int propertyId) {
        // Determine buffer size needed, then allocate matching buffer and
        // return value
        IntByReference buffUsed = new IntByReference();
        boolean result = Wevtapi.INSTANCE.EvtGetChannelConfigProperty(channelHandle, propertyId,  0, 0, null, buffUsed);
        int errorCode = Kernel32.INSTANCE.GetLastError();
        if((! result) && errorCode != Kernel32.ERROR_INSUFFICIENT_BUFFER) {
            throw new IllegalStateException(String.format(
                    "Failed to read property (determine size): %s (%d)",
                    Kernel32Util.getLastErrorMessage(),
                    errorCode
            ));
        }
        
        Memory mem = new Memory(buffUsed.getValue());
        result = Wevtapi.INSTANCE.EvtGetChannelConfigProperty(channelHandle, propertyId,  0, (int) mem.size(), mem, buffUsed);
        if(! result) {
            throw new IllegalStateException("Failed to read property: " + Kernel32Util.getLastErrorMessage());
        }
        
        EVT_VARIANT resultEvt = new EVT_VARIANT(mem);
        resultEvt.read();
        return resultEvt;
    }
}
