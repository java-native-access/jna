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

import com.sun.jna.Memory;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.Union;
import com.sun.jna.platform.win32.WinNT.LARGE_INTEGER;
import com.sun.jna.platform.win32.WinNT.PSID;
import com.sun.jna.win32.StdCallLibrary;

/**
 * Ported from NTSecApi.h
 * Windows SDK 6.0A.
 * @author dblock[at]dblock.org
 */
public interface NTSecApi extends StdCallLibrary {
	
    /**
     * The LSA_UNICODE_STRING structure is used by various Local Security Authority (LSA) 
     * functions to specify a Unicode string.
     */
    public static class LSA_UNICODE_STRING extends Structure {
        public static class ByReference extends LSA_UNICODE_STRING implements Structure.ByReference {

        }
	
        /**
         * Specifies the length, in bytes, of the string pointed to by the Buffer member, 
         * not including the terminating null character, if any.
         */
        public short Length;
        /**
         * Specifies the total size, in bytes, of the memory allocated for Buffer. Up to 
         * MaximumLength bytes can be written into the buffer without trampling memory.
         */
        public short MaximumLength;
        /**
         * Pointer to a wide character string. Note that the strings returned by the 
         * various LSA functions might not be null terminated.
         */
        public Pointer Buffer;
		
        /**
         * String representation of the buffer.
         * @return
         *  Unicode string.
         */
        public String getString() {
            byte[] data = Buffer.getByteArray(0, Length);
            if (data.length < 2 || data[data.length - 1] != 0) {
                Memory newdata = new Memory(data.length + 2);
                newdata.write(0, data, 0, data.length);
                return newdata.getString(0, true);
            }
            return Buffer.getString(0, true);
        }
    }

    /**
     * Pointer to an LSA_UNICODE_STRING.
     */
    public static class PLSA_UNICODE_STRING {
        public static class ByReference extends PLSA_UNICODE_STRING 
            implements Structure.ByReference {

        }
		
        public LSA_UNICODE_STRING.ByReference s;
    }
	
    /**
     * Record contains an included top-level name.
     */
    int ForestTrustTopLevelName = 0;
    /**
     * Record contains an excluded top-level name.
     */
    int ForestTrustTopLevelNameEx = 1;
    /**
     * Record contains an LSA_FOREST_TRUST_DOMAIN_INFO structure.
     */
    int ForestTrustDomainInfo = 2;

    public static class LSA_FOREST_TRUST_DOMAIN_INFO extends Structure {
        public PSID.ByReference Sid;
        public LSA_UNICODE_STRING DnsName;
        public LSA_UNICODE_STRING NetbiosName;
    }
	
    public static class LSA_FOREST_TRUST_BINARY_DATA extends Structure {
        public NativeLong Length;
        public Pointer Buffer;
    }
	
    public static class LSA_FOREST_TRUST_RECORD extends Structure {    
		
        public static class ByReference extends LSA_FOREST_TRUST_RECORD  implements Structure.ByReference {

        }

        public static class UNION extends Union {
            public static class ByReference extends UNION  implements Structure.ByReference {

            }
			
            public LSA_UNICODE_STRING TopLevelName;
            public LSA_FOREST_TRUST_DOMAIN_INFO DomainInfo;
            public LSA_FOREST_TRUST_BINARY_DATA Data;
        }
		
        /**
         * Flags that control the behavior of the operation.
         */
        public NativeLong Flags;
		
        /**
         * LSA_FOREST_TRUST_RECORD_TYPE enumeration that indicates the type of the record. 
         * The following table shows the possible values.
         * ForestTrustTopLevelName
         *  Record contains an included top-level name.
         * ForestTrustTopLevelNameEx
         *  Record contains an excluded top-level name.
         * ForestTrustDomainInfo
         *  Record contains an LSA_FOREST_TRUST_DOMAIN_INFO structure.
         * ForestTrustRecordTypeLast
         *  Marks the end of an enumeration.
         */
        public int ForestTrustType;
        public LARGE_INTEGER Time;
		
        /**
         * Data type depending on ForestTrustType. 
         */
        public UNION u;
		
        public void read() {
            super.read();
			
            switch(ForestTrustType) {
            case NTSecApi.ForestTrustTopLevelName:
            case NTSecApi.ForestTrustTopLevelNameEx:
                u.setType(LSA_UNICODE_STRING.class);
                break;
            case NTSecApi.ForestTrustDomainInfo:
                u.setType(LSA_FOREST_TRUST_DOMAIN_INFO.class);
                break;
            default:
                u.setType(LSA_FOREST_TRUST_BINARY_DATA.class);
                break;
            }
			
            u.read();
        }
    }
	
    public static class PLSA_FOREST_TRUST_RECORD extends Structure {
        public static class ByReference extends PLSA_FOREST_TRUST_RECORD implements Structure.ByReference {
			
        }
		
        public LSA_FOREST_TRUST_RECORD.ByReference tr;	
    }
	
    public static class LSA_FOREST_TRUST_INFORMATION extends Structure {
		
        public static class ByReference extends LSA_FOREST_TRUST_INFORMATION implements Structure.ByReference {
			
        }
		
        /**
         * Number of LSA_FOREST_TRUST_RECORD structures in the array pointed to by the 
         * Entries member.
         */
        public NativeLong RecordCount;
        /**
         * Pointer to a pointer to an array of LSA_FOREST_TRUST_RECORD structures, 
         * each of which contains one piece of forest trust information.
         */
        public PLSA_FOREST_TRUST_RECORD.ByReference Entries;

        /**
         * Get an array of LSA_FOREST_TRUST_RECORD entries.
         * @return
         *  An array of forest trust records.
         */
        public PLSA_FOREST_TRUST_RECORD[] getEntries() {
            return (PLSA_FOREST_TRUST_RECORD[]) Entries.toArray(RecordCount.intValue());
        }
    }
    /**
     * The LSA_FOREST_TRUST_INFORMATION structure contains Local Security Authority 
     * forest trust information.
     */
    public static class PLSA_FOREST_TRUST_INFORMATION extends Structure {
		
        public static class ByReference extends PLSA_FOREST_TRUST_INFORMATION implements Structure.ByReference {
			
        }

        public LSA_FOREST_TRUST_INFORMATION.ByReference fti;		
    }
}
