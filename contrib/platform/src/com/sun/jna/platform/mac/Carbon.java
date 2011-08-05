/*
 * Copyright (c) 2011 Denis Tulskiy
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * version 3 along with this work.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.sun.jna.platform.mac;

import com.sun.jna.Callback;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.ptr.PointerByReference;

import java.nio.IntBuffer;

/**
 * Author: Denis Tulskiy
 * Date: 7/25/11
 */
public interface Carbon {
    public static Carbon INSTANCE = (Carbon) Native.loadLibrary("Carbon", Carbon.class);

    public static final int cmdKey = 0x0100;
    public static final int shiftKey = 0x0200;
    public static final int optionKey = 0x0800;
    public static final int controlKey = 0x1000;

    /**
     * Obtains the event target reference for the standard toolbox dispatcher
     */
    public Pointer GetEventDispatcherTarget();

    /**
     * Installs an event handler on a specified event target.
     */
    public int InstallEventHandler(Pointer inTarget, EventHandlerProcPtr inHandler, int inNumTypes, EventTypeSpec[] inList, Pointer inUserData, PointerByReference outRef);

    /**
     * Registers a global hot key.
     */
    public int RegisterEventHotKey(int inHotKeyCode, int inHotKeyModifiers, EventHotKeyID.ByValue inHotKeyID, Pointer inTarget, int inOptions, PointerByReference outRef);

    /**
     * Obtains a parameter from the specified event.
     */
    public int GetEventParameter(Pointer inEvent, int inName, int inDesiredType, Pointer outActualType, int inBufferSize, IntBuffer outActualSize, EventHotKeyID outData);

    /**
     * Removes the specified event handler
     */
    public int RemoveEventHandler(Pointer inHandlerRef);

    /**
     * Unregisters a global hot key.
     */
    public int UnregisterEventHotKey(Pointer inHotKey);

    public class EventTypeSpec extends Structure {
        public int eventClass;
        public int eventKind;
    }

    public static class EventHotKeyID extends Structure {
        public int signature;
        public int id;

        public static class ByValue extends EventHotKeyID implements Structure.ByValue {

        }
    }

    public static interface EventHandlerProcPtr extends Callback {
        public int callback(Pointer inHandlerCallRef, Pointer inEvent, Pointer inUserData);
    }
}
