/*
 * Copyright (c) 2011 Denis Tulskiy
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

package com.sun.jna.platform.mac;

import java.nio.IntBuffer;
import java.util.List;

import com.sun.jna.Library;
import com.sun.jna.Callback;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.ptr.PointerByReference;

/**
 * Author: Denis Tulskiy
 * Date: 7/25/11
 */
public interface Carbon extends Library {
    Carbon INSTANCE = Native.loadLibrary("Carbon", Carbon.class);

    int cmdKey = 0x0100;
    int shiftKey = 0x0200;
    int optionKey = 0x0800;
    int controlKey = 0x1000;

    /**
     * Obtains the event target reference for the standard toolbox dispatcher
     * @return event dispatcher reference
     */
    Pointer GetEventDispatcherTarget();

    /**
     * Installs an event handler on a specified event target.
     */
    int InstallEventHandler(Pointer inTarget, EventHandlerProcPtr inHandler, int inNumTypes, EventTypeSpec[] inList, Pointer inUserData, PointerByReference outRef);

    /**
     * Registers a global hot key.
     */
    int RegisterEventHotKey(int inHotKeyCode, int inHotKeyModifiers, EventHotKeyID.ByValue inHotKeyID, Pointer inTarget, int inOptions, PointerByReference outRef);

    /**
     * Obtains a parameter from the specified event.
     */
    int GetEventParameter(Pointer inEvent, int inName, int inDesiredType, Pointer outActualType, int inBufferSize, IntBuffer outActualSize, EventHotKeyID outData);

    /**
     * Removes the specified event handler
     */
    int RemoveEventHandler(Pointer inHandlerRef);

    /**
     * Unregisters a global hot key.
     */
    int UnregisterEventHotKey(Pointer inHotKey);

    public class EventTypeSpec extends Structure {
        public static final List<String> FIELDS = createFieldsOrder("eventClass", "eventKind");

        public int eventClass;
        public int eventKind;

        @Override
        protected List<String> getFieldOrder() {
            return FIELDS;
        }
    }

    public static class EventHotKeyID extends Structure {
        public static final List<String> FIELDS = createFieldsOrder("signature", "id");

        public int signature;
        public int id;

        public static class ByValue extends EventHotKeyID implements Structure.ByValue { }

        @Override
        protected List<String> getFieldOrder() {
            return FIELDS;
        }
    }

    public static interface EventHandlerProcPtr extends Callback {
        public int callback(Pointer inHandlerCallRef, Pointer inEvent, Pointer inUserData);
    }
}
