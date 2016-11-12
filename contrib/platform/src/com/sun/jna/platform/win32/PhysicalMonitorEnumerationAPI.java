/*
 * Copyright 2014 Martin Steiger
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

import java.util.List;

import com.sun.jna.Structure;
import com.sun.jna.platform.win32.WinNT.HANDLE;

/**
 * Conversion of PhysicalMonitorEnumerationAPI.h
 * @author Martin Steiger
 */
public interface PhysicalMonitorEnumerationAPI
{

    /******************************************************************************
      Physical Monitor Constants
    ******************************************************************************/

    /**
     * A physical monitor description is always an array of 128 characters.  Some
     * of the characters may not be used.
     */
    final int PHYSICAL_MONITOR_DESCRIPTION_SIZE =                   128;

    /******************************************************************************
      Physical Monitor Structures
    ******************************************************************************/

    /**
     * Contains a handle and text description corresponding to a physical monitor.
     */
    public class PHYSICAL_MONITOR extends Structure {
        public static final List<String> FIELDS = createFieldsOrder("hPhysicalMonitor", "szPhysicalMonitorDescription");
        /**
         * Handle to the physical monitor.
         */
        public HANDLE hPhysicalMonitor;

        /**
         * Text description of the physical monitor (always 128 chars)
         */
        public char[] szPhysicalMonitorDescription = new char[PHYSICAL_MONITOR_DESCRIPTION_SIZE];

        @Override
        protected List<String> getFieldOrder() {
            return FIELDS;
        }
    }
}
