/*
 * Copyright 2014 Martin Steiger
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
