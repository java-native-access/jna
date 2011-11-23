/* Copyright (c) 2010 Daniel Doubrovkine, All Rights Reserved
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

/**
 * Ported from Guid.h.
 * Microsoft Windows SDK 6.0A.
 * @author dblock[at]dblock.org
 */
public interface Guid {
    
    public static class GUID extends Structure {
        
        public static class ByReference extends GUID implements Structure.ByReference {
            public ByReference() {                
            }

            public ByReference(GUID guid) {
                super(guid.getPointer());

                Data1 = guid.Data1;
                Data2 = guid.Data2;
                Data3 = guid.Data3;
                Data4 = guid.Data4;
            }
            
            public ByReference(Pointer memory) {
                super(memory);
            }
        }
        
        public GUID() {
            
        }
            
        public GUID(Pointer memory) {
            super(memory);
            read();
        }

        public GUID(byte[] data) {
            if (data.length != 16) {
                throw new IllegalArgumentException("Invalid data length: " + data.length);
            }

            long data1Temp = data[3] & 0xff;
            data1Temp <<= 8;
            data1Temp |= data[2] & 0xff;
            data1Temp <<= 8;
            data1Temp |= data[1] & 0xff;
            data1Temp <<= 8;
            data1Temp |= data[0] & 0xff;
            Data1 = (int) data1Temp;

            int data2Temp = data[5] & 0xff;
            data2Temp <<= 8;
            data2Temp |= data[4] & 0xff;
            Data2 = (short) data2Temp;

            int data3Temp = data[7] & 0xff;
            data3Temp <<= 8;
            data3Temp |= data[6] & 0xff;
            Data3 = (short) data3Temp;

            Data4[0] = data[8];
            Data4[1] = data[9];
            Data4[2] = data[10];
            Data4[3] = data[11];
            Data4[4] = data[12];
            Data4[5] = data[13];
            Data4[6] = data[14];
            Data4[7] = data[15];
        }

        public int Data1;
        public short Data2;
        public short Data3;
        public byte[] Data4 = new byte[8];
    }    
}
