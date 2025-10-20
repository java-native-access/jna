package com.sun.jna.platform.win32;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

import java.util.Arrays;
import java.util.List;

/**
 * Module Name:
 *     wlanapi.h
 * Abstract:
 *     Definitions and data structures for wlan auto config client side API.
 */
public interface WlanApi extends Library {
    WlanApi INSTANCE = Native.load("wlanapi", WlanApi.class);
    int WLAN_MAX_NAME_LENGTH = 256;
    int DOT11_SSID_MAX_LENGTH = 32; // 32 bytes

    class WLAN_INTERFACE_INFO_LIST extends Structure {
        public int dwNumberOfItems;
        public int dwIndex;
        public WLAN_INTERFACE_INFO[] InterfaceInfo = new WLAN_INTERFACE_INFO[1];

        public WLAN_INTERFACE_INFO_LIST() {
          setAutoSynch(false);
        }

        public WLAN_INTERFACE_INFO_LIST(Pointer p) {
            super(p);
        }

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList("dwNumberOfItems", "dwIndex", "InterfaceInfo");
        }

        public void read() {
            // First element contains array size
            dwNumberOfItems = getPointer().getInt(0);
            if (dwNumberOfItems > 0) {
                InterfaceInfo = (WLAN_INTERFACE_INFO[]) new WLAN_INTERFACE_INFO().toArray(dwNumberOfItems);
                super.read();
            } else {
                InterfaceInfo = new WLAN_INTERFACE_INFO[0];
            }
        }
    }

    class WLAN_INTERFACE_INFO extends Structure {
        public GUID InterfaceGuid;
        public char[] strInterfaceDescription = new char[WLAN_MAX_NAME_LENGTH];
        public int isState; // WLAN_INTERFACE_STATE

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList("InterfaceGuid", "strInterfaceDescription", "isState");
        }
    }


    class WLAN_CONNECTION_ATTRIBUTES extends Structure {
        public int isState; // WLAN_INTERFACE_STATE
        public int wlanConnectionMode; // WLAN_CONNECTION_MODE
        public char[] strProfileName = new char[WLAN_MAX_NAME_LENGTH];
        public WLAN_ASSOCIATION_ATTRIBUTES wlanAssociationAttributes;
        // Other fields omitted

        public WLAN_CONNECTION_ATTRIBUTES(Pointer p) {
            super(p);
        }

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList("isState", "wlanConnectionMode", "strProfileName", "wlanAssociationAttributes");
        }
    }

    interface WLAN_INTERFACE_STATE {
        int wlan_interface_state_not_ready = 0;
        int wlan_interface_state_connected = 1;
        int wlan_interface_state_ad_hoc_network_formed = 2;
        int wlan_interface_state_disconnecting = 3;
        int wlan_interface_state_disconnected = 4;
        int wlan_interface_state_associating = 5;
        int wlan_interface_state_discovering = 6;
        int wlan_interface_state_authenticating = 7;
    }

    interface WLAN_CONNECTION_MODE {
        int wlan_connection_mode_profile = 0;
        int wlan_connection_mode_temporary_profile = 1;
        int wlan_connection_mode_discovery_secure = 2;
        int wlan_connection_mode_discovery_unsecure = 3;
        int wlan_connection_mode_auto = 4;
        int wlan_connection_mode_invalid = 5;
    }

    class WLAN_ASSOCIATION_ATTRIBUTES extends Structure {
        public DOT11_SSID dot11Ssid;
        public int dot11BssType;    // DOT11_BSS_TYPE
        // Other fields omitted

        public WLAN_ASSOCIATION_ATTRIBUTES(Pointer p) {
            super(p);
        }

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList("dot11Ssid", "dot11BssType");
        }
    }

    interface DOT11_BSS_TYPE {
        int dot11_BSS_type_infrastructure = 1;
        int dot11_BSS_type_independent = 2;
        int dot11_BSS_type_any = 3;
    }

    class DOT11_SSID extends Structure {
        public WinDef.ULONG uSSIDLength;
        public byte[] ucSSID = new byte[DOT11_SSID_MAX_LENGTH];

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList("uSSIDLength", "ucSSID");
        }
    }

    class HANDLE extends WinNT.HANDLE {
        public HANDLE() {
        }

        public HANDLE(Pointer p) {
            super(p);
        }
    }

    class GUID extends Guid.GUID {}

    interface WLAN_INTF_OPCODE {
        int wlan_intf_opcode_autoconf_start = 0x000000000;
        int wlan_intf_opcode_autoconf_enabled = 1;
        int wlan_intf_opcode_background_scan_enabled = 2;
        int wlan_intf_opcode_media_streaming_mode = 3;
        int wlan_intf_opcode_radio_state = 4;
        int wlan_intf_opcode_bss_type = 5;
        int wlan_intf_opcode_interface_state = 6;
        int wlan_intf_opcode_current_connection = 7;
        int wlan_intf_opcode_channel_number = 8;
        int wlan_intf_opcode_supported_infrastructure_auth_cipher_pairs = 9;
        int wlan_intf_opcode_supported_adhoc_auth_cipher_pairs = 10;
        int wlan_intf_opcode_supported_country_or_region_string_list = 11;
        int wlan_intf_opcode_current_operation_mode = 12;
        int wlan_intf_opcode_supported_safe_mode = 13;
        int wlan_intf_opcode_certified_safe_mode = 14;
        int wlan_intf_opcode_hosted_network_capable = 15;
        int wlan_intf_opcode_autoconf_end = 0x0fffffff;
        int wlan_intf_opcode_msm_start = 0x10000100;
        int wlan_intf_opcode_statistics = 0x10000101;
        int wlan_intf_opcode_rssi = 0x10000102;
        int wlan_intf_opcode_msm_end = 0x1fffffff;
        int wlan_intf_opcode_security_start = 0x20010000;
        int wlan_intf_opcode_security_end = 0x2fffffff;
        int wlan_intf_opcode_ihv_start = 0x30000000;
        int wlan_intf_opcode_ihv_end = 0x3fffffff;
    }

    interface WLAN_OPCODE_VALUE_TYPE {
        int wlan_opcode_value_type_query_only = 0;
        int wlan_opcode_value_type_set_by_group_policy = 1;
        int wlan_opcode_value_type_set_by_user = 2;
        int wlan_opcode_value_type_invalid = 3;
    }

    int WlanOpenHandle(int dwClientVersion, Pointer pReserved, IntByReference pdwNegotiatedVersion,
                       PointerByReference phClientHandle);
    int WlanCloseHandle(HANDLE hClientHandle, Pointer pReserved);
    int WlanEnumInterfaces(HANDLE hClientHandle, Pointer pReserved, PointerByReference ppInterfaceList);
    int WlanQueryInterface(HANDLE hClientHandle, GUID pInterfaceGuid, int OpCode /* WLAN_INTF_OPCODE */,
                           Pointer pReserved, IntByReference pDataSize, PointerByReference ppData,
                           IntByReference pWlanOpcodeValueType /* WLAN_OPCODE_VALUE_TYPE */);
    void WlanFreeMemory(Pointer pMemory);
}
