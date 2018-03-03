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

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.List;

import com.sun.jna.Pointer;
import com.sun.jna.PointerType;
import com.sun.jna.Structure;

/**
 * Ported from Guid.h. Microsoft Windows SDK 6.0A.
 *
 * @author dblock[at]dblock.org
 */
public interface Guid {

    /** The Constant IID_NULL. */
    public final static IID IID_NULL = new IID();

    /**
     * The Class GUID.
     *
     * @author Tobias Wolf, wolf.tobias@gmx.net
     */
    public static class GUID extends Structure {

    	public static class ByValue extends GUID implements Structure.ByValue {
    		public ByValue() {
                super();
            }
            public ByValue(GUID guid) {
                super(guid.getPointer());

                Data1 = guid.Data1;
                Data2 = guid.Data2;
                Data3 = guid.Data3;
                Data4 = guid.Data4;
            }
            public ByValue(Pointer memory) {
                super(memory);
            }
    	}

        /**
         * The Class ByReference.
         *
         * @author Tobias Wolf, wolf.tobias@gmx.net
         */
        public static class ByReference extends GUID implements Structure.ByReference {

            /**
             * Instantiates a new by reference.
             */
            public ByReference() {
                super();
            }

            /**
             * Instantiates a new by reference.
             *
             * @param guid
             *            the guid
             */
            public ByReference(GUID guid) {
                super(guid.getPointer());

                Data1 = guid.Data1;
                Data2 = guid.Data2;
                Data3 = guid.Data3;
                Data4 = guid.Data4;
            }

            /**
             * Instantiates a new by reference.
             *
             * @param memory
             *            the memory
             */
            public ByReference(Pointer memory) {
                super(memory);
            }
        }

        public static final List<String> FIELDS = createFieldsOrder("Data1", "Data2", "Data3", "Data4");

        /** The Data1. */
        public int Data1;

        /** The Data2. */
        public short Data2;

        /** The Data3. */
        public short Data3;

        /** The Data4. */
        public byte[] Data4 = new byte[8];

        /**
         * Instantiates a new guid.
         */
        public GUID() {
            super();
        }

        /**
         * Instantiates a new guid.
         *
         * @param guid the guid
         */
        public GUID(GUID guid) {
            this.Data1 = guid.Data1;
            this.Data2 = guid.Data2;
            this.Data3 = guid.Data3;
            this.Data4 = guid.Data4;

            this.writeFieldsToMemory();
        }

        /**
         * Instantiates a new guid.
         *
         * @param guid
         *            the guid
         */
        public GUID(String guid) {
            this(fromString(guid));
        }

        /**
         * Instantiates a new guid.
         *
         * @param data
         *            the data
         */
        public GUID(byte[] data) {
            this(fromBinary(data));
        }

        /**
         * Instantiates a new guid.
         *
         * @param memory
         *            the memory
         */
        public GUID(Pointer memory) {
            super(memory);
            read();
        }

        @Override
        public boolean equals(Object o) {
            if (o == null) {
                return false;
            }
            if (this == o) {
                return true;
            }
            if (getClass() != o.getClass()) {
                return false;
            }

            GUID other = (GUID) o;
            return (this.Data1 == other.Data1)
                && (this.Data2 == other.Data2)
                && (this.Data3 == other.Data3)
                && Arrays.equals(this.Data4, other.Data4);
        }

        @Override
        public int hashCode() {
            return this.Data1 + this.Data2 & 0xFFFF + this.Data3 & 0xFFFF + Arrays.hashCode(this.Data4);
        }

        /**
         * From binary.
         *
         * @param data
         *            the data
         * @return the guid
         */
        public static GUID fromBinary(byte[] data) {
            if (data.length != 16) {
                throw new IllegalArgumentException("Invalid data length: "
                        + data.length);
            }

            GUID newGuid = new GUID();
            long data1Temp = data[0] & 0xff;
            data1Temp <<= 8;
            data1Temp |= data[1] & 0xff;
            data1Temp <<= 8;
            data1Temp |= data[2] & 0xff;
            data1Temp <<= 8;
            data1Temp |= data[3] & 0xff;
            newGuid.Data1 = (int) data1Temp;

            int data2Temp = data[4] & 0xff;
            data2Temp <<= 8;
            data2Temp |= data[5] & 0xff;
            newGuid.Data2 = (short) data2Temp;

            int data3Temp = data[6] & 0xff;
            data3Temp <<= 8;
            data3Temp |= data[7] & 0xff;
            newGuid.Data3 = (short) data3Temp;

            newGuid.Data4[0] = data[8];
            newGuid.Data4[1] = data[9];
            newGuid.Data4[2] = data[10];
            newGuid.Data4[3] = data[11];
            newGuid.Data4[4] = data[12];
            newGuid.Data4[5] = data[13];
            newGuid.Data4[6] = data[14];
            newGuid.Data4[7] = data[15];

            newGuid.writeFieldsToMemory();

            return newGuid;
        }

        /**
         * From string.
         *
         * @param guid
         *            the guid
         * @return the guid
         */
        public static GUID fromString(String guid) {
            int y = 0;
            char[] _cnewguid = new char[32];
            char[] _cguid = guid.toCharArray();
            byte[] bdata = new byte[16];
            GUID newGuid = new GUID();

            // we not accept a string longer than 38 chars
            if (guid.length() > 38) {
                throw new IllegalArgumentException("Invalid guid length: "
                        + guid.length());
            }

            // remove '{', '}' and '-' from guid string
            for (int i = 0; i < _cguid.length; i++) {
                if ((_cguid[i] != '{') && (_cguid[i] != '-')
                        && (_cguid[i] != '}'))
                    _cnewguid[y++] = _cguid[i];
            }

            // convert char to byte
            for (int i = 0; i < 32; i += 2) {
                bdata[i / 2] = (byte) ((Character.digit(_cnewguid[i], 16) << 4)
                        + Character.digit(_cnewguid[i + 1], 16) & 0xff);
            }

            if (bdata.length != 16) {
                throw new IllegalArgumentException("Invalid data length: "
                        + bdata.length);
            }

            long data1Temp = bdata[0] & 0xff;
            data1Temp <<= 8;
            data1Temp |= bdata[1] & 0xff;
            data1Temp <<= 8;
            data1Temp |= bdata[2] & 0xff;
            data1Temp <<= 8;
            data1Temp |= bdata[3] & 0xff;
            newGuid.Data1 = (int) data1Temp;

            int data2Temp = bdata[4] & 0xff;
            data2Temp <<= 8;
            data2Temp |= bdata[5] & 0xff;
            newGuid.Data2 = (short) data2Temp;

            int data3Temp = bdata[6] & 0xff;
            data3Temp <<= 8;
            data3Temp |= bdata[7] & 0xff;
            newGuid.Data3 = (short) data3Temp;

            newGuid.Data4[0] = bdata[8];
            newGuid.Data4[1] = bdata[9];
            newGuid.Data4[2] = bdata[10];
            newGuid.Data4[3] = bdata[11];
            newGuid.Data4[4] = bdata[12];
            newGuid.Data4[5] = bdata[13];
            newGuid.Data4[6] = bdata[14];
            newGuid.Data4[7] = bdata[15];

            newGuid.writeFieldsToMemory();

            return newGuid;
        }

        /**
         * Generates a new guid. Code taken from the standard jdk implementation
         * (see UUID class).
         *
         * @return the guid
         */
        public static GUID newGuid() {
            SecureRandom ng = new SecureRandom();
            byte[] randomBytes = new byte[16];

            ng.nextBytes(randomBytes);
            randomBytes[6] &= 0x0f;
            randomBytes[6] |= 0x40;
            randomBytes[8] &= 0x3f;
            randomBytes[8] |= 0x80;

            return new GUID(randomBytes);
        }

        /**
         * To byte array.
         *
         * @return the byte[]
         */
        public byte[] toByteArray() {
            byte[] guid = new byte[16];

            byte[] bytes1 = new byte[4];
            bytes1[0] = (byte) (Data1 >> 24);
            bytes1[1] = (byte) (Data1 >> 16);
            bytes1[2] = (byte) (Data1 >> 8);
            bytes1[3] = (byte) (Data1 >> 0);

            byte[] bytes2 = new byte[4];
            bytes2[0] = (byte) (Data2 >> 24);
            bytes2[1] = (byte) (Data2 >> 16);
            bytes2[2] = (byte) (Data2 >> 8);
            bytes2[3] = (byte) (Data2 >> 0);

            byte[] bytes3 = new byte[4];
            bytes3[0] = (byte) (Data3 >> 24);
            bytes3[1] = (byte) (Data3 >> 16);
            bytes3[2] = (byte) (Data3 >> 8);
            bytes3[3] = (byte) (Data3 >> 0);

            System.arraycopy(bytes1, 0, guid, 0, 4);
            System.arraycopy(bytes2, 2, guid, 4, 2);
            System.arraycopy(bytes3, 2, guid, 6, 2);
            System.arraycopy(Data4, 0, guid, 8, 8);

            return guid;
        }

        /**
         * The value of this Guid, formatted as follows:
         * xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx.
         *
         * @return the string
         */
        public String toGuidString() {
            final String HEXES = "0123456789ABCDEF";
            byte[] bGuid = toByteArray();

            final StringBuilder hexStr = new StringBuilder(2 * bGuid.length);
            hexStr.append("{");

            for (int i = 0; i < bGuid.length; i++) {
                char ch1 = HEXES.charAt((bGuid[i] & 0xF0) >> 4);
                char ch2 = HEXES.charAt(bGuid[i] & 0x0F);
                hexStr.append(ch1).append(ch2);

                if ((i == 3) || (i == 5) || (i == 7) || (i == 9))
                    hexStr.append("-");
            }

            hexStr.append("}");
            return hexStr.toString();
        }

        /**
         * Write fields to backing memory.
         */
        protected void writeFieldsToMemory() {
            for (String name : FIELDS) {
                this.writeField(name);
            }
        }

        @Override
        protected List<String> getFieldOrder() {
            return FIELDS;
        }
    }

    /**
     * The Class CLSID.
     *
     * @author Tobias Wolf, wolf.tobias@gmx.net
     */
    public static class CLSID extends GUID {

        /**
         * The Class ByReference.
         *
         * @author Tobias Wolf, wolf.tobias@gmx.net
         */
        public static class ByReference extends GUID {

            /**
             * Instantiates a new by reference.
             */
            public ByReference() {
                super();
            }

            /**
             * Instantiates a new by reference.
             *
             * @param guid
             *            the guid
             */
            public ByReference(GUID guid) {
                super(guid);
            }

            /**
             * Instantiates a new by reference.
             *
             * @param memory
             *            the memory
             */
            public ByReference(Pointer memory) {
                super(memory);
            }
        }

        /**
         * Instantiates a new clsid.
         */
        public CLSID() {
            super();
        }

        /**
         * Instantiates a new clsid.
         *
         * @param guid the guid
         */
        public CLSID(String guid) {
            super(guid);
        }

        /**
         * Instantiates a new clsid.
         *
         * @param guid the guid
         */
        public CLSID(GUID guid) {
            super(guid);
        }
    }

    /**
     * REFIID is a pointer to an IID.
     *
     * <p>
     * This type needs to be seperate from IID, as the REFIID can be passed in
     * from external code, that does not allow writes to memory.</p>
     *
     * <p>
     * With the normal JNA behaviour a structure, that crosses the
     * native&lt;-%gt;Java border will be autowritten, which causes a fault when
     * written. Observed was this behaviour in COM-Callbacks, which get the
     * REFIID passed into Invoke-method.</p>
     *
     * <p>
     * So a IID can't be used directly, although the typedef of REFIID (from
     * MSDN):</p>
     *
     * <p>
     * {@code typedef IID* REFIID;}</p>
     *
     * <p>
     * and the jna behaviour is described as:</p>
     *
     * <p>
     * "When a function requires a pointer to a struct, a Java Structure should
     * be used."</p>
     */
    public class REFIID extends PointerType {

        /**
         * Instantiates a new refiid.
         */
        public REFIID() {
        }

        /**
         * Instantiates a new refiid.
         *
         * @param memory
         *            the memory
         */
        public REFIID(Pointer memory) {
            super(memory);
        }

        public REFIID(IID guid) {
            super(guid.getPointer());
        }
        
        public void setValue(IID value) {
            setPointer(value.getPointer());
        }

        public IID getValue() {
            return new IID(getPointer());
        }

        @Override
        public boolean equals(Object o) {
            if (o == null) {
                return false;
            }
            if (this == o) {
                return true;
            }
            if (getClass() != o.getClass()) {
                return false;
            }

            REFIID other = (REFIID) o;
            return getValue().equals(other.getValue());
        }

        @Override
        public int hashCode() {
            return getValue().hashCode();
        }
    }
    
    /**
     * The Class IID.
     *
     * @author Tobias Wolf, wolf.tobias@gmx.net
     */
    public class IID extends GUID {

        /**
         * Instantiates a new iid.
         */
        public IID() {
            super();
        }

        /**
         * Instantiates a new iid.
         *
         * @param memory
         *            the memory
         */
        public IID(Pointer memory) {
            super(memory);
        }

        /**
         * Instantiates a new iid.
         *
         * @param iid the iid
         */
        public IID(String iid) {
            super(iid);
        }

        /**
         * Instantiates a new iid.
         *
         * @param data
         *            the data
         */
        public IID(byte[] data) {
            super(data);
        }

        public IID(GUID guid) {
            this(guid.toGuidString());
        }

    }
}