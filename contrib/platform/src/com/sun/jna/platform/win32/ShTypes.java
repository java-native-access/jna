/* Copyright (c) 2017 Matthias Bläsing, All Rights Reserved
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

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.Union;
import java.util.List;

/**
 * Ported from ShTypes.h. Microsoft Windows SDK.
 */
public interface ShTypes {
    /**
     * structure for returning strings from IShellFolder member functions
     */
    public static class STRRET extends Structure {
        public static final int TYPE_WSTR = 0;
        public static final int TYPE_OFFSET = 0x1;
        public static final int TYPE_CSTR = 0x2;
        
        public static class UNION extends Union {

            public static class ByReference extends UNION implements Structure.ByReference {

            }

            public WTypes.LPWSTR pOleStr;
            public int uOffset;
            public byte[] cStr = new byte[260];
        }
        
        public static final List<String> FIELDS = createFieldsOrder("uType", "u");

        /**
         * indicate which union member to use:
         * 
         * <table>
         * <tr><td>{@link #TYPE_WSTR}</td><td>0x0</td><td>Use STRRET.pOleStr</td><td>must be freed by caller of GetDisplayNameOf</td></tr>
         * <tr><td>{@link #TYPE_OFFSET}</td><td>0x1</td><td>Use STRRET.uOffset</td><td>Offset into SHITEMID for ANSI string</td></tr>
         * <tr><td>{@link #TYPE_CSTR}</td><td>0x0</td><td>Use STRRET.cStr</td><td>ANSI Buffer</td></tr>
         * </table>
         */
        public int uType;
        public UNION u;

        public STRRET() {
            super();
        }

        public STRRET(Pointer p) {
            super(p);
            read();
        }

        @Override
        public void read() {
            super.read();
            switch(uType) {
                default:
                case TYPE_WSTR:
                    u.setType("pOleStr");
                    break;
                case TYPE_OFFSET:
                    u.setType("uOffset");
                    break;
                case TYPE_CSTR:
                    u.setType("cStr");
                    break;
            }
            u.read();
        }
        
        @Override
        protected List<String> getFieldOrder() {
            return FIELDS;
        }
    }
}
