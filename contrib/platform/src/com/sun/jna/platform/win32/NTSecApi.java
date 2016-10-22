/* Copyright (c) 2010 Daniel Doubrovkine, All Rights Reserved
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

import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.Union;
import com.sun.jna.platform.win32.WinNT.LARGE_INTEGER;
import com.sun.jna.platform.win32.WinNT.PSID;

/**
 * Ported from NTSecApi.h
 * Windows SDK 6.0A.
 * @author dblock[at]dblock.org
 */
public interface NTSecApi {

    /**
     * The LSA_UNICODE_STRING structure is used by various Local Security Authority (LSA)
     * functions to specify a Unicode string.
     */
    public static class LSA_UNICODE_STRING extends Structure {
        public static class ByReference extends LSA_UNICODE_STRING implements Structure.ByReference {

        }

        public static final List<String> FIELDS = createFieldsOrder("Length", "MaximumLength", "Buffer");
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

        @Override
        protected List<String> getFieldOrder() {
            return FIELDS;
        }

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
                return newdata.getWideString(0);
            }
            return Buffer.getWideString(0);
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
        public static final List<String> FIELDS = createFieldsOrder("Sid", "DnsName", "NetbiosName");

        public PSID.ByReference Sid;
        public LSA_UNICODE_STRING DnsName;
        public LSA_UNICODE_STRING NetbiosName;

        @Override
        protected List<String> getFieldOrder() {
            return FIELDS;
        }
    }

    public static class LSA_FOREST_TRUST_BINARY_DATA extends Structure {
        public static final List<String> FIELDS = createFieldsOrder("Length", "Buffer");

        public int Length;
        public Pointer Buffer;

        @Override
        protected List<String> getFieldOrder() {
            return FIELDS;
        }
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

        public static final List<String> FIELDS = createFieldsOrder("Flags", "ForestTrustType", "Time", "u");
        /**
         * Flags that control the behavior of the operation.
         */
        public int Flags;

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

        @Override
        protected List<String> getFieldOrder() {
            return FIELDS;
        }

        @Override
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

        public static final List<String> FIELDS = createFieldsOrder("tr");

        public LSA_FOREST_TRUST_RECORD.ByReference tr;

        @Override
        protected List<String> getFieldOrder() {
            return FIELDS;
        }
    }

    public static class LSA_FOREST_TRUST_INFORMATION extends Structure {

        public static class ByReference extends LSA_FOREST_TRUST_INFORMATION implements Structure.ByReference {

        }

        public static final List<String> FIELDS = createFieldsOrder("RecordCount", "Entries");
        /**
         * Number of LSA_FOREST_TRUST_RECORD structures in the array pointed to by the
         * Entries member.
         */
        public int RecordCount;
        /**
         * Pointer to a pointer to an array of LSA_FOREST_TRUST_RECORD structures,
         * each of which contains one piece of forest trust information.
         */
        public PLSA_FOREST_TRUST_RECORD.ByReference Entries;

        @Override
        protected List<String> getFieldOrder() {
            return FIELDS;
        }

        /**
         * Get an array of LSA_FOREST_TRUST_RECORD entries.
         * @return
         *  An array of forest trust records.
         */
        public PLSA_FOREST_TRUST_RECORD[] getEntries() {
            return (PLSA_FOREST_TRUST_RECORD[]) Entries.toArray(RecordCount);
        }
    }
    /**
     * The LSA_FOREST_TRUST_INFORMATION structure contains Local Security Authority
     * forest trust information.
     */
    public static class PLSA_FOREST_TRUST_INFORMATION extends Structure {

        public static class ByReference extends PLSA_FOREST_TRUST_INFORMATION implements Structure.ByReference {

        }

        public static final List<String> FIELDS = createFieldsOrder("fti");
        public LSA_FOREST_TRUST_INFORMATION.ByReference fti;

        @Override
        protected List<String> getFieldOrder() {
            return FIELDS;
        }
    }
}
