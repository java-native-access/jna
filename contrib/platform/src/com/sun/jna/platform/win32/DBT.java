/* Copyright (c) 2012 Tobias Wolf, All Rights Reserved
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

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.platform.win32.Guid.GUID;
import com.sun.jna.platform.win32.WinDef.LONG;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.platform.win32.WinUser.HDEVNOTIFY;

/**
 * Based on dbt.h (various types)
 *
 * @author Tobias Wolf, wolf.tobias@gmx.net
 */
public interface DBT {

    /** The dbt no disk space. */
    int DBT_NO_DISK_SPACE = 0x0047;

    /** The dbt low disk space. */
    int DBT_LOW_DISK_SPACE = 0x0048;

    /** The dbt configmgprivate. */
    int DBT_CONFIGMGPRIVATE = 0x7FFF;

    /** The dbt devicearrival. */
    int DBT_DEVICEARRIVAL = 0x8000;

    /** The dbt devicequeryremove. */
    int DBT_DEVICEQUERYREMOVE = 0x8001;

    /** The dbt devicequeryremovefailed. */
    int DBT_DEVICEQUERYREMOVEFAILED = 0x8002;

    /** The dbt deviceremovepending. */
    int DBT_DEVICEREMOVEPENDING = 0x8003;

    /** The dbt deviceremovecomplete. */
    int DBT_DEVICEREMOVECOMPLETE = 0x8004;

    /** A device has been added to or removed from the system. */
    int DBT_DEVNODES_CHANGED = 0x0007;

    /** The dbt devicetypespecific. */
    int DBT_DEVICETYPESPECIFIC = 0x8005;

    /** The dbt customevent. */
    int DBT_CUSTOMEVENT = 0x8006;

    /** The guid devinterface usb device. */
    GUID GUID_DEVINTERFACE_USB_DEVICE = new GUID(
            "{A5DCBF10-6530-11D2-901F-00C04FB951ED}");

    /** The guid devinterface hid. */
    GUID GUID_DEVINTERFACE_HID = new GUID("{4D1E55B2-F16F-11CF-88CB-001111000030}");

    /** The guid devinterface volume. */
    GUID GUID_DEVINTERFACE_VOLUME = new GUID("{53F5630D-B6BF-11D0-94F2-00A0C91EFB8B}");

    /** The guid devinterface keyboard. */
    GUID GUID_DEVINTERFACE_KEYBOARD = new GUID("{884b96c3-56ef-11d1-bc8c-00a0c91405dd}");

    /** The guid devinterface mouse. */
    GUID GUID_DEVINTERFACE_MOUSE = new GUID("{378DE44C-56EF-11D1-BC8C-00A0C91405DD}");

    /**
     * The Class DEV_BROADCAST_HDR.
     */
    public class DEV_BROADCAST_HDR extends Structure {
        public static final List<String> FIELDS = createFieldsOrder("dbch_size", "dbch_devicetype", "dbch_reserved");

        /** The dbch_size. */
        public int dbch_size = size();

        /** The dbch_devicetype. */
        public int dbch_devicetype;

        /** The dbch_reserved. */
        public int dbch_reserved;

        /**
         * Instantiates a new dev broadcast hdr.
         */
        public DEV_BROADCAST_HDR() {
            super();
        }

        /**
         * Instantiates a new dev broadcast hdr.
         *
         * @param pointer
         *            the pointer
         */
        public DEV_BROADCAST_HDR(long pointer) {
            this(new Pointer(pointer));
        }

        /**
         * Instantiates a new dev broadcast hdr.
         *
         * @param memory
         *            the memory
         */
        public DEV_BROADCAST_HDR(Pointer memory) {
            super(memory);
            read();
        }

        @Override
        protected List<String> getFieldOrder() {
            return FIELDS;
        }
    }

    /** The dbt devtyp oem. */
    int DBT_DEVTYP_OEM = 0x00000000;

    /** The dbt devtyp devnode. */
    int DBT_DEVTYP_DEVNODE = 0x00000001;

    /** The dbt devtyp volume. */
    int DBT_DEVTYP_VOLUME = 0x00000002;

    /** The dbt devtyp port. */
    int DBT_DEVTYP_PORT = 0x00000003;

    /** The dbt devtyp net. */
    int DBT_DEVTYP_NET = 0x00000004;

    /** The dbt devtyp deviceinterface. */
    int DBT_DEVTYP_DEVICEINTERFACE = 0x00000005;

    /** The dbt devtyp handle. */
    int DBT_DEVTYP_HANDLE = 0x00000006;

    /**
     * The Class DEV_BROADCAST_OEM.
     */
    public class DEV_BROADCAST_OEM extends Structure {
        public static final List<String> FIELDS = createFieldsOrder("dbco_size", "dbco_devicetype",
                "dbco_reserved", "dbco_identifier", "dbco_suppfunc");

        /** The dbco_size. */
        public int dbco_size = size();

        /** The dbco_devicetype. */
        public int dbco_devicetype;

        /** The dbco_reserved. */
        public int dbco_reserved;

        /** The dbco_identifier. */
        public int dbco_identifier;

        /** The dbco_suppfunc. */
        public int dbco_suppfunc;

        /**
         * Instantiates a new dev broadcast oem.
         */
        public DEV_BROADCAST_OEM() {
            super();
        }

        /**
         * Instantiates a new dev broadcast oem.
         *
         * @param memory
         *            the memory
         */
        public DEV_BROADCAST_OEM(Pointer memory) {
            super(memory);
            read();
        }

        @Override
        protected List<String> getFieldOrder() {
            return FIELDS;
        }
    }

    /**
     * The Class DEV_BROADCAST_DEVNODE.
     */
    public class DEV_BROADCAST_DEVNODE extends Structure {
        public static final List<String> FIELDS = createFieldsOrder("dbcd_size", "dbcd_devicetype",
                "dbcd_reserved", "dbcd_devnode");
        /** The dbcd_size. */
        public int dbcd_size = size();

        /** The dbcd_devicetype. */
        public int dbcd_devicetype;

        /** The dbcd_reserved. */
        public int dbcd_reserved;

        /** The dbcd_devnode. */
        public int dbcd_devnode;

        /**
         * Instantiates a new dev broadcast devnode.
         */
        public DEV_BROADCAST_DEVNODE() {
            super();
        }

        /**
         * Instantiates a new dev broadcast devnode.
         *
         * @param memory
         *            the memory
         */
        public DEV_BROADCAST_DEVNODE(Pointer memory) {
            super(memory);
            read();
        }

        @Override
        protected List<String> getFieldOrder() {
            return FIELDS;
        }
    }

    /**
     * The Class DEV_BROADCAST_VOLUME.
     */
    public class DEV_BROADCAST_VOLUME extends Structure {
        public static final List<String> FIELDS = createFieldsOrder("dbcv_size", "dbcv_devicetype",
                "dbcv_reserved", "dbcv_unitmask", "dbcv_flags");

        /** The dbcv_size. */
        public int dbcv_size = size();

        /** The dbcv_devicetype. */
        public int dbcv_devicetype;

        /** The dbcv_reserved. */
        public int dbcv_reserved;

        /** The dbcv_unitmask. */
        public int dbcv_unitmask;

        /** The dbcv_flags. */
        public short dbcv_flags;

        /**
         * Instantiates a new dev broadcast volume.
         */
        public DEV_BROADCAST_VOLUME() {
            super();
        }

        /**
         * Instantiates a new dev broadcast volume.
         *
         * @param memory
         *            the memory
         */
        public DEV_BROADCAST_VOLUME(Pointer memory) {
            super(memory);
            read();
        }

        @Override
        protected List<String> getFieldOrder() {
            return FIELDS;
        }
    }

    /** The dbt change affects media in drive, not physical device or drive. */
    int DBTF_MEDIA = 0x0001;

    /** The dbt indicated logical volume is a network volume. */
    int DBTF_NET = 0x0002;

    /**
     * The Class DEV_BROADCAST_PORT.
     */
    public class DEV_BROADCAST_PORT extends Structure {
        public static final List<String> FIELDS = createFieldsOrder("dbcp_size", "dbcp_devicetype", "dbcp_reserved", "dbcp_name");

        /** The dbcp_size. */
        public int dbcp_size = size();

        /** The dbcp_devicetype. */
        public int dbcp_devicetype;

        /** The dbcp_reserved. */
        public int dbcp_reserved;

        /** The dbcp_name. */
        public char[] dbcp_name = new char[1];

        /**
         * Instantiates a new dev broadcast port.
         */
        public DEV_BROADCAST_PORT() {
            super();
        }

        /**
         * Instantiates a new dev broadcast port.
         *
         * @param memory
         *            the memory
         */
        public DEV_BROADCAST_PORT(Pointer memory) {
            super(memory);
            read();
        }

        @Override
        protected List<String> getFieldOrder() {
            return FIELDS;
        }
    }

    /**
     * The Class DEV_BROADCAST_NET.
     */
    public class DEV_BROADCAST_NET extends Structure {
        public static final List<String> FIELDS = createFieldsOrder("dbcn_size", "dbcn_devicetype",
                "dbcn_reserved", "dbcn_resource", "dbcn_flags");

        /** The dbcn_size. */
        public int dbcn_size = size();

        /** The dbcn_devicetype. */
        public int dbcn_devicetype;

        /** The dbcn_reserved. */
        public int dbcn_reserved;

        /** The dbcn_resource. */
        public int dbcn_resource;

        /** The dbcn_flags. */
        public int dbcn_flags;

        /**
         * Instantiates a new dev broadcast net.
         */
        public DEV_BROADCAST_NET() {
            super();
        }

        /**
         * Instantiates a new dev broadcast net.
         *
         * @param memory
         *            the memory
         */
        public DEV_BROADCAST_NET(Pointer memory) {
            super(memory);
            read();
        }

        @Override
        protected List<String> getFieldOrder() {
            return FIELDS;
        }
    }

    /**
     * The Class DEV_BROADCAST_DEVICEINTERFACE.
     */
    public class DEV_BROADCAST_DEVICEINTERFACE extends Structure {
        public static final List<String> FIELDS = createFieldsOrder("dbcc_size", "dbcc_devicetype",
                "dbcc_reserved", "dbcc_classguid", "dbcc_name");

        /** The dbcc_size. */
        public int dbcc_size;

        /** The dbcc_devicetype. */
        public int dbcc_devicetype;

        /** The dbcc_reserved. */
        public int dbcc_reserved;

        /** The dbcc_classguid. */
        public GUID dbcc_classguid;

        /** The dbcc_name. */
        public char[] dbcc_name = new char[1];

        /**
         * Instantiates a new dev broadcast deviceinterface.
         */
        public DEV_BROADCAST_DEVICEINTERFACE() {
            super();
        }

        /**
         * Dev broadcast hdr.
         *
         * @param pointer
         *            the pointer
         */
        public DEV_BROADCAST_DEVICEINTERFACE(long pointer) {
            this(new Pointer(pointer));
        }

        /**
         * Instantiates a new dev broadcast deviceinterface.
         *
         * @param memory
         *            the memory
         */
        public DEV_BROADCAST_DEVICEINTERFACE(Pointer memory) {
            super(memory);
            this.dbcc_size = (Integer) this.readField("dbcc_size");
            // figure out how long dbcc_name should be based on the size
            int len = 1 + this.dbcc_size - size();
            this.dbcc_name = new char[len];
            read();
        }

        /**
         * Gets the dbcc_name.
         *
         * @return the dbcc_name
         */
        public String getDbcc_name() {
            return Native.toString(this.dbcc_name);
        }

        @Override
        protected List<String> getFieldOrder() {
            return FIELDS;
        }
    }

    /**
     * The Class DEV_BROADCAST_HANDLE.
     */
    public class DEV_BROADCAST_HANDLE extends Structure {
        public static final List<String> FIELDS = createFieldsOrder("dbch_size", "dbch_devicetype",
                "dbch_reserved", "dbch_handle", "dbch_hdevnotify",
                "dbch_eventguid", "dbch_nameoffset", "dbch_data");

        /** The dbch_size. */
        public int dbch_size = size();

        /** The dbch_devicetype. */
        public int dbch_devicetype;

        /** The dbch_reserved. */
        public int dbch_reserved;

        /** The dbch_handle. */
        public HANDLE dbch_handle;

        /** The dbch_hdevnotify. */
        public HDEVNOTIFY dbch_hdevnotify;

        /** The dbch_eventguid. */
        public GUID dbch_eventguid;

        /** The dbch_nameoffset. */
        public LONG dbch_nameoffset;

        /** The dbch_data. */
        public byte[] dbch_data;

        /**
         * Instantiates a new dev broadcast handle.
         */
        public DEV_BROADCAST_HANDLE() {
            super();
        }

        /**
         * Instantiates a new dev broadcast handle.
         *
         * @param memory
         *            the memory
         */
        public DEV_BROADCAST_HANDLE(Pointer memory) {
            super(memory);
            read();
        }

        @Override
        protected List<String> getFieldOrder() {
            return FIELDS;
        }
    }
}
