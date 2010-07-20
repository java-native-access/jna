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

import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.WString;
import com.sun.jna.platform.win32.Guid.GUID;
import com.sun.jna.platform.win32.WinNT.PSID;
import com.sun.jna.win32.StdCallLibrary;


/**
 * Ported from DsGetDC.h.
 * Windows SDK 6.0a
 * @author dblock[at]dblock.org 
 */
public interface DsGetDC extends StdCallLibrary {
	
	/**
	 * The DOMAIN_CONTROLLER_INFO structure is used with the DsGetDcName 
	 * function to receive data about a domain controller.
	 */
	public static class DOMAIN_CONTROLLER_INFO extends Structure {
	    
		public static class ByReference extends DOMAIN_CONTROLLER_INFO implements Structure.ByReference {

		}
		
		public DOMAIN_CONTROLLER_INFO() {
			
		}
		
		public DOMAIN_CONTROLLER_INFO(Pointer memory) {
			useMemory(memory);
			read();
		}

		/**
		 * Pointer to a null-terminated WString that specifies the computer name 
		 * of the discovered domain controller. The returned computer name is 
		 * prefixed with "\\". The DNS-style name, for example, "\\phoenix.fabrikam.com", 
		 * is returned, if available. If the DNS-style name is not available, the 
		 * flat-style name (for example, "\\phoenix") is returned. This example would apply 
		 * if the domain is a Windows NT 4.0 domain or if the domain does not support the 
		 * IP family of protocols.
		 */
	    public WString DomainControllerName;
	    /**
	     * Pointer to a null-terminated WString that specifies the address of the discovered 
	     * domain controller. The address is prefixed with "\\". This WString is one of the
	     * types defined by the DomainControllerAddressType member.
	     */
	    public WString DomainControllerAddress;
	    /**
	     * Indicates the type of WString that is contained in the DomainControllerAddress member.
	     */
	    public int DomainControllerAddressType;
	    /**
	     * The GUID of the domain. This member is zero if the domain controller does not have 
	     * a Domain GUID; for example, the domain controller is not a Windows 2000 domain 
	     * controller.
	     */
	    public GUID DomainGuid;
	    /**
	     * Pointer to a null-terminated WString that specifies the name of the domain. The 
	     * DNS-style name, for example, "fabrikam.com", is returned if available. Otherwise, 
	     * the flat-style name, for example, "fabrikam", is returned. This name may be different 
	     * than the requested domain name if the domain has been renamed.
	     */
	    public WString DomainName;
	    /**
	     * Pointer to a null-terminated WString that specifies the name of the domain at the root 
	     * of the DS tree. The DNS-style name, for example, "fabrikam.com", is returned if 
	     * available. Otherwise, the flat-style name, for example, "fabrikam" is returned.
	     */
	    public WString DnsForestName;
	    /**
	     * Contains a set of flags that describe the domain controller. 
	     */
	    public int Flags;
	    /**
	     * Pointer to a null-terminated WString that specifies the name of the site where the 
	     * domain controller is located. This member may be NULL if the domain controller is 
	     * not in a site; for example, the domain controller is a Windows NT 4.0 domain 
	     * controller.
	     */
	    public WString DcSiteName;
	    /**
	     * Pointer to a null-terminated WString that specifies the name of the site that the 
	     * computer belongs to. The computer is specified in the ComputerName parameter passed 
	     * to DsGetDcName. This member may be NULL if the site that contains the computer 
	     * cannot be found; for example, if the DS administrator has not associated the 
	     * subnet that the computer is in with a valid site.
	     */
	    public WString ClientSiteName;
	}	
	
	/**
	 * Pointer to DOMAIN_CONTROLLER_INFO.
	 */
	public static class PDOMAIN_CONTROLLER_INFO extends Structure {

		public static class ByReference extends PDOMAIN_CONTROLLER_INFO implements Structure.ByReference {

		}

		public DOMAIN_CONTROLLER_INFO.ByReference dci;
	}
	
	/**
	 * Domain is a member of the forest.
	 */
	public static final int DS_DOMAIN_IN_FOREST = 0x0001;
	/**
	 * Domain is directly trusted.
	 */
	public static final int DS_DOMAIN_DIRECT_OUTBOUND = 0x0002;
	/**
	 * Domain is root of a tree in the forest.
	 */
	public static final int DS_DOMAIN_TREE_ROOT = 0x0004; 
	/**
	 * Domain is the primary domain of queried server.
	 */
	public static final int DS_DOMAIN_PRIMARY = 0x0008;
	/**
	 * Primary domain is running in native mode.
	 */
	public static final int DS_DOMAIN_NATIVE_MODE = 0x0010;
	/**
	 * Domain is directly trusting.
	 */
	public static final int DS_DOMAIN_DIRECT_INBOUND = 0x0020;
	/**
	 * Valid domain flags.
	 */
	public static final int DS_DOMAIN_VALID_FLAGS = 
		DS_DOMAIN_IN_FOREST       |
	    DS_DOMAIN_DIRECT_OUTBOUND |
	    DS_DOMAIN_TREE_ROOT       |
	    DS_DOMAIN_PRIMARY         |
	    DS_DOMAIN_NATIVE_MODE     |
	    DS_DOMAIN_DIRECT_INBOUND;

	/**
	 * The DS_DOMAIN_TRUSTS structure is used with the DsEnumerateDomainTrusts function to 
	 * contain trust data for a domain.
	 */
	public static class DS_DOMAIN_TRUSTS extends Structure {
			
		public static class ByReference extends DS_DOMAIN_TRUSTS implements Structure.ByReference {

		}
		
		/**
		 * Pointer to a null-terminated string that contains the NetBIOS name of the domain.
		 */
	    public WString NetbiosDomainName;
	    /**
	     * Pointer to a null-terminated string that contains the DNS name of the domain. This member may be NULL.
	     */
	    public WString DnsDomainName;
	    /**
	     * Contains a set of flags that specify more data about the domain trust.
	     */	    
	    public NativeLong Flags;
	    /**
	     * Contains the index in the Domains array returned by the DsEnumerateDomainTrusts function that 
	     * corresponds to the parent domain of the domain represented by this structure.
	     */
	    public NativeLong ParentIndex;
	    /**
	     * Contains a value that indicates the type of trust represented by this structure.
	     */
	    public NativeLong TrustType;	    
	    /**
	     * Contains a value that indicates the attributes of the trust represented by this structure.
	     */
	    public NativeLong TrustAttributes;
	    
	    /**
	     * Contains the security identifier of the domain represented by this structure.
	     */
	    public PSID.ByReference DomainSid;

	    /**
	     * Contains the GUID of the domain represented by this structure.
	     */
	    public GUID DomainGuid;
	};
	
	/**
	 * A pointer to an array of DS_DOMAIN_TRUSTS.
	 */
	public static class PDS_DOMAIN_TRUSTS extends Structure {
		public static class ByReference extends PDS_DOMAIN_TRUSTS implements Structure.ByReference {

		}
		
		public DS_DOMAIN_TRUSTS.ByReference t;
		
		/**
		 * Returns domain trusts.
		 * @param count
		 *  Number of domain trusts.
		 * @return
		 *  An array of domain trusts.
		 */
		public DS_DOMAIN_TRUSTS[] getTrusts(int count) {
			return (DS_DOMAIN_TRUSTS[]) t.toArray(count);
		}
	}
}
