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

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.platform.win32.Guid.GUID;
import com.sun.jna.platform.win32.WinNT.PSID;
import com.sun.jna.win32.W32APITypeMapper;

/**
 * Ported from DsGetDC.h. Windows SDK 6.0a
 *
 * @author dblock[at]dblock.org
 */
public interface DsGetDC {

    /**
     * The DOMAIN_CONTROLLER_INFO structure is used with the DsGetDcName
     * function to receive data about a domain controller.
     */
    public static class DOMAIN_CONTROLLER_INFO extends Structure {

        public static class ByReference extends DOMAIN_CONTROLLER_INFO
                implements Structure.ByReference {
        }

        public static final List<String> FIELDS = createFieldsOrder("DomainControllerName",
                "DomainControllerAddress", "DomainControllerAddressType",
                "DomainGuid", "DomainName", "DnsForestName", "Flags",
                "DcSiteName", "ClientSiteName");

        /**
         * Pointer to a null-terminated string that specifies the computer name
         * of the discovered domain controller. The returned computer name is
         * prefixed with "\\". The DNS-style name, for example,
         * "\\phoenix.fabrikam.com", is returned, if available. If the DNS-style
         * name is not available, the flat-style name (for example, "\\phoenix")
         * is returned. This example would apply if the domain is a Windows NT
         * 4.0 domain or if the domain does not support the IP family of
         * protocols.
         */
        public String DomainControllerName;
        /**
         * Pointer to a null-terminated string that specifies the address of
         * the discovered domain controller. The address is prefixed with "\\".
         * This string is one of the types defined by the
         * DomainControllerAddressType member.
         */
        public String DomainControllerAddress;
        /**
         * Indicates the type of string that is contained in the
         * DomainControllerAddress member.
         */
        public int DomainControllerAddressType;
        /**
         * The GUID of the domain. This member is zero if the domain controller
         * does not have a Domain GUID; for example, the domain controller is
         * not a Windows 2000 domain controller.
         */
        public GUID DomainGuid;
        /**
         * Pointer to a null-terminated string that specifies the name of the
         * domain. The DNS-style name, for example, "fabrikam.com", is returned
         * if available. Otherwise, the flat-style name, for example,
         * "fabrikam", is returned. This name may be different than the
         * requested domain name if the domain has been renamed.
         */
        public String DomainName;
        /**
         * Pointer to a null-terminated string that specifies the name of the
         * domain at the root of the DS tree. The DNS-style name, for example,
         * "fabrikam.com", is returned if available. Otherwise, the flat-style
         * name, for example, "fabrikam" is returned.
         */
        public String DnsForestName;
        /**
         * Contains a set of flags that describe the domain controller.
         */
        public int Flags;
        /**
         * Pointer to a null-terminated string that specifies the name of the
         * site where the domain controller is located. This member may be NULL
         * if the domain controller is not in a site; for example, the domain
         * controller is a Windows NT 4.0 domain controller.
         */
        public String DcSiteName;
        /**
         * Pointer to a null-terminated string that specifies the name of the
         * site that the computer belongs to. The computer is specified in the
         * ComputerName parameter passed to DsGetDcName. This member may be NULL
         * if the site that contains the computer cannot be found; for example,
         * if the DS administrator has not associated the subnet that the
         * computer is in with a valid site.
         */
        public String ClientSiteName;

        public DOMAIN_CONTROLLER_INFO() {
            super(W32APITypeMapper.DEFAULT);
        }

        public DOMAIN_CONTROLLER_INFO(Pointer memory) {
            super(memory, Structure.ALIGN_DEFAULT, W32APITypeMapper.DEFAULT);
            read();
        }

        @Override
        protected List<String> getFieldOrder() {
            return FIELDS;
        }
    }

    /**
     * Pointer to DOMAIN_CONTROLLER_INFO.
     */
    public static class PDOMAIN_CONTROLLER_INFO extends Structure {

        public static class ByReference extends PDOMAIN_CONTROLLER_INFO
                implements Structure.ByReference {

        }

        public static final List<String> FIELDS = createFieldsOrder("dci");

        public DOMAIN_CONTROLLER_INFO.ByReference dci;

        @Override
        protected List<String> getFieldOrder() {
            return FIELDS;
        }
    }

    /**
     * Domain is a member of the forest.
     */
    int DS_DOMAIN_IN_FOREST = 0x0001;
    /**
     * Domain is directly trusted.
     */
    int DS_DOMAIN_DIRECT_OUTBOUND = 0x0002;
    /**
     * Domain is root of a tree in the forest.
     */
    int DS_DOMAIN_TREE_ROOT = 0x0004;
    /**
     * Domain is the primary domain of queried server.
     */
    int DS_DOMAIN_PRIMARY = 0x0008;
    /**
     * Primary domain is running in native mode.
     */
    int DS_DOMAIN_NATIVE_MODE = 0x0010;
    /**
     * Domain is directly trusting.
     */
    int DS_DOMAIN_DIRECT_INBOUND = 0x0020;
    /**
     * Valid domain flags.
     */
    int DS_DOMAIN_VALID_FLAGS = DS_DOMAIN_IN_FOREST | DS_DOMAIN_DIRECT_OUTBOUND
            | DS_DOMAIN_TREE_ROOT | DS_DOMAIN_PRIMARY | DS_DOMAIN_NATIVE_MODE
            | DS_DOMAIN_DIRECT_INBOUND;

    /**
     * The DS_DOMAIN_TRUSTS structure is used with the DsEnumerateDomainTrusts
     * function to contain trust data for a domain.
     */
    public static class DS_DOMAIN_TRUSTS extends Structure {

        public static class ByReference extends DS_DOMAIN_TRUSTS implements
                Structure.ByReference {
        }

        public static final List<String> FIELDS = createFieldsOrder("NetbiosDomainName",
                "DnsDomainName", "Flags", "ParentIndex", "TrustType",
                "TrustAttributes", "DomainSid", "DomainGuid");

        /**
         * Pointer to a null-terminated string that contains the NetBIOS name of
         * the domain.
         */
        public String NetbiosDomainName;
        /**
         * Pointer to a null-terminated string that contains the DNS name of the
         * domain. This member may be NULL.
         */
        public String DnsDomainName;
        /**
         * Contains a set of flags that specify more data about the domain
         * trust.
         */
        public int Flags;
        /**
         * Contains the index in the Domains array returned by the
         * DsEnumerateDomainTrusts function that corresponds to the parent
         * domain of the domain represented by this structure.
         */
        public int ParentIndex;
        /**
         * Contains a value that indicates the type of trust represented by this
         * structure.
         */
        public int TrustType;
        /**
         * Contains a value that indicates the attributes of the trust
         * represented by this structure.
         */
        public int TrustAttributes;

        /**
         * Contains the security identifier of the domain represented by this
         * structure.
         */
        public PSID.ByReference DomainSid;

        /**
         * Contains the GUID of the domain represented by this structure.
         */
        public GUID DomainGuid;

        @Override
        protected List<String> getFieldOrder() {
            return FIELDS;
        }

        public DS_DOMAIN_TRUSTS() {
            super(W32APITypeMapper.DEFAULT);
        }

        public DS_DOMAIN_TRUSTS(Pointer p) {
            super(p, Structure.ALIGN_DEFAULT, W32APITypeMapper.DEFAULT);
            read();
        }
    };
}
