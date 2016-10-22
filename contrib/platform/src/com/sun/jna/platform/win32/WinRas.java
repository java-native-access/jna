/* Copyright (c) 2011 Timothy Wall, All Rights Reserved
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

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.Union;
import com.sun.jna.platform.win32.BaseTSD.ULONG_PTR;
import com.sun.jna.platform.win32.Guid.GUID;
import com.sun.jna.platform.win32.WinDef.BOOL;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.platform.win32.WinNT.LUID;
import com.sun.jna.win32.StdCallLibrary.StdCallCallback;

/**
 * Definitions for RASAPI32
 */
public interface WinRas {
	public static final int ERROR_BUFFER_TOO_SMALL = 603;
	public static final int ERROR_CANNOT_FIND_PHONEBOOK_ENTRY = 623;

	public static final int MAX_PATH = 260;
	public static final int UNLEN = 256;
	public static final int PWLEN = 256;
	public static final int DNLEN = 15;

	public static final int RAS_MaxEntryName = 256;
	public static final int RAS_MaxPhoneNumber = 128;
	public static final int RAS_MaxCallbackNumber = 128;
	public static final int RAS_MaxDeviceType = 16;
	public static final int RAS_MaxDeviceName = 128;
	public static final int RAS_MaxDnsSuffix = 256;
	public static final int RAS_MaxAreaCode = 10;
	public static final int RAS_MaxX25Address = 200;
	public static final int RAS_MaxIpAddress = 15;
	public static final int RAS_MaxFacilities = 200;
	public static final int RAS_MaxUserData = 200;
	public static final int RAS_MaxPadType = 32;

	public static final int RASCS_Connected = 0x2000;
	public static final int RASCS_Disconnected = 0x2001;

	public static final int RASCM_UserName = 0x00000001;
	public static final int RASCM_Password = 0x00000002;
	public static final int RASCM_Domain = 0x00000004;

	public static final int RASTUNNELENDPOINT_IPv4 = 1;
	public static final int RASTUNNELENDPOINT_IPv6 = 2;

	public static final String RASDT_Modem = "modem";

	/**
	 * The RASEAPINFO structure contains user-specific Extensible Authentication Protocol (EAP) information.
	 * Use RASEAPINFO to pass this information to the RasDial function.
	 */
	public static class RASEAPINFO extends Structure {
		public RASEAPINFO() {
			super();
		}

		public RASEAPINFO(Pointer memory) {
			super(memory);
			read();
		}

		public RASEAPINFO(byte [] data) {
			pbEapInfo = new Memory(data.length);
			pbEapInfo.write(0, data, 0, data.length);
			dwSizeofEapInfo = data.length;
			allocateMemory();
		}

		public RASEAPINFO(String s) {
			this(Native.toByteArray(s));
		}

		/**
		 * Specifies the size of the binary information pointed to by the pbEapInfo member.
		 */
		public int dwSizeofEapInfo;
		/**
		 * Pointer to binary EAP information. RasDial uses this information for authentication.
		 */
		public Pointer pbEapInfo;

		@Override
		protected List<String> getFieldOrder() {
			return Arrays.asList(new String[] { "dwSizeofEapInfo", "pbEapInfo", });
		}
		/**
		 * Get byte data.
		 * @return
		 *  Byte data or null.
		 */
		public byte[] getData() {
			return pbEapInfo == null ? null : pbEapInfo.getByteArray(0, dwSizeofEapInfo);
		}
	}

	/**
	 * The RASDEVSPECIFICINFO structure is used to send a cookie for server validation and bypass
	 * point-to-point (PPP) authentication.
	 */
	public static class RASDEVSPECIFICINFO extends Structure {
		public RASDEVSPECIFICINFO() {
			super();
		}

		public RASDEVSPECIFICINFO(Pointer memory) {
			super(memory);
			read();
		}

		public RASDEVSPECIFICINFO(byte [] data) {
			pbDevSpecificInfo = new Memory(data.length);
			pbDevSpecificInfo.write(0, data, 0, data.length);
			dwSize = data.length;
			allocateMemory();
		}

		public RASDEVSPECIFICINFO(String s) {
			this(Native.toByteArray(s));
		}

		/**
		 * The size, in bytes, of the cookie in pbDevSpecificInfo.
		 */
		public int dwSize;
		/**
		 * A pointer to a BLOB that contains the authentication cookie.
		 */
		public Pointer pbDevSpecificInfo;

		@Override
		protected List<String> getFieldOrder() {
			return Arrays.asList(new String[] { "dwSize", "pbDevSpecificInfo", });
		}
		/**
		 * Get byte data.
		 * @return
		 *  Byte data or null.
		 */
		public byte[] getData() {
			return pbDevSpecificInfo == null ? null : pbDevSpecificInfo.getByteArray(0, dwSize);
		}
	}

	/**
	 * The RASDIALEXTENSIONS structure contains information about extended features of the RasDial function.
	 * Enable one or more of these extensions by passing a pointer to a RASDIALEXTENSIONS structure when you call RasDial.
	 * If you do not pass a pointer to a RASDIALEXTENSIONS structure to RasDial, RasDial uses the default settings that are
	 * noted in the following descriptions.
	 */
	public static class RASDIALEXTENSIONS extends Structure {
		public RASDIALEXTENSIONS() {
			super();
			dwSize = size();
		}

		public RASDIALEXTENSIONS(Pointer memory) {
			super(memory);
			read();
		}

		public static class ByReference extends RASDIALEXTENSIONS implements Structure.ByReference {
		}

		/**
		 * Specifies the size of this structure, in bytes. Set this member to sizeof(RASDIALEXTENSIONS). This indicates the version of the structure.
		 */
		public int dwSize;
		/**
		 * A set of bit flags that specify RasDial extensions. The following bit flags are defined; set all undefined bits to zero.
		 */
		public int dwfOptions;
		/**
		 * Handle to a parent window that a security DLL can use for dialog box creation and centering.
		 */
		public HWND hwndParent;
		/**
		 * This member is reserved for future use. It must be set to zero.
		 */
		public ULONG_PTR reserved;
		/**
		 * This member is reserved for future use. It must be set to zero.
		 */
		public ULONG_PTR reserved1;
		/**
		 * A RASEAPINFO structure that contains user-specific Extensible Authentication Protocol (EAP) information.
		 */
		public RASEAPINFO RasEapInfo;
		/**
		 * Windows 7 or later: If this member is TRUE, then Point to Point Protocol (PPP) authentication is skipped and
		 * the value in RasDevSpecificInfo will be passed to the server for validation. Otherwise, this member is
		 * FALSE and PPP authentication proceeds normally.
		 */
		public BOOL fSkipPppAuth;
		/**
		 * Windows 7 or later: A RASDEVSPECIFICINFO structure that contains a cookie to be used for PPP authentication.
		 * This cookie is only valid if fSkipPppAuth is TRUE.
		 */
		public RASDEVSPECIFICINFO RasDevSpecificInfo;

		@Override
		protected List<String> getFieldOrder() {
			return Arrays.asList(new String[] { "dwSize", "dwfOptions", "hwndParent", "reserved", "reserved1", "RasEapInfo", "fSkipPppAuth", "RasDevSpecificInfo", });
		}
	}

	/**
	 * The RASDIALPARAMS structure contains parameters that are used by RasDial to establish a remote access connection.
	 */
	public static class RASDIALPARAMS extends Structure {
		public RASDIALPARAMS() {
			super();
			dwSize = size();
		}

		public RASDIALPARAMS(Pointer memory) {
			super(memory);
			read();
		}

		public static class ByReference extends RASDIALPARAMS implements Structure.ByReference {
		}

		/**
		 * A value that specifies the size, in bytes, of the structure.
		 */
		public int dwSize;
		/**
		 * A null-terminated string that contains the phone-book entry to use to establish the connection. An empty string ("")
		 * specifies a simple modem connection on the first available modem port, in which case a non-empty szPhoneNumber must be provided.
		 */
		public char[] szEntryName = new char[RAS_MaxEntryName + 1];
		/**
		 * A null-terminated string that contains an overriding phone number. An empty string ("") indicates that the phone-book
		 * entry's phone number should be used. If szEntryName is "", szPhoneNumber cannot be "".
		 */
		public char[] szPhoneNumber = new char[RAS_MaxPhoneNumber + 1];
		/**
		 * A null-terminated string that contains a callback phone number. An empty string ("") indicates that callback should not be used.
		 * This string is ignored unless the user has "Set By Caller" callback permission on the RAS server.
		 * An asterisk indicates that the number stored in the phone book should be used for callback.
		 */
		public char[] szCallbackNumber = new char[RAS_MaxCallbackNumber + 1];
		/**
		 * A null-terminated string that contains the user's user name. This string is used to authenticate the user's access to the remote access server.
		 */
		public char[] szUserName = new char[UNLEN + 1];
		/**
		 * A null-terminated string that contains the user's password. This string is used to authenticate the user's access to the remote access server.
		 */
		public char[] szPassword = new char[PWLEN + 1];
		/**
		 * A null-terminated string that contains the domain on which authentication is to occur. An empty string ("") specifies the domain in
		 * which the remote access server is a member. An asterisk specifies the domain stored in the phone book for the entry.
		 */
		public char[] szDomain = new char[DNLEN + 1];

		@Override
		protected List<String> getFieldOrder() {
			return Arrays.asList(new String[] { "dwSize", "szEntryName", "szPhoneNumber", "szCallbackNumber", "szUserName", "szPassword", "szDomain",  });
		}
	}

	/**
	 * The RASCONN structure provides information about a remote access connection. The RasEnumConnections function returns an array of RASCONN structures.
	 */
	public static class RASCONN extends Structure {
		public RASCONN() {
			super();
			dwSize = size();
		}

		public RASCONN(Pointer memory) {
			super(memory);
			read();
		}

		public static class ByReference extends RASCONN implements Structure.ByReference {
		}

		/**
		 * Specifies the size, in bytes, of the RASCONN structure.
		 */
		public int dwSize;
		/**
		 * A HRASCONN handle that defines the remote access connection. This handle is used in other remote access API calls.
		 */
		public HANDLE hrasconn;
		/**
		 * A null-terminated string that specifies the phone-book entry used to establish the remote access connection.
		 * If the connection was established using an empty entry name, this string consists of a PERIOD followed by the connection phone number.
		 */
		public char[] szEntryName = new char[RAS_MaxEntryName + 1];
		/**
		 * A null-terminated string that contains the device type through which the connection is made. See RASENTRY for a list of possible device types.
		 */
		public char[] szDeviceType = new char[RAS_MaxDeviceType + 1];
		/**
		 * A null-terminated string that contains the device name through which the connection is made.
		 */
		public char[] szDeviceName = new char[RAS_MaxDeviceName + 1];
		/**
		 * A null-terminated string that specifies the full path and file name of a phone-book (PBK) file.
		 */
		public char[] szPhonebook = new char[MAX_PATH];
		/**
		 * For multilink connections, a value that specifies the subentry one-based index of a connected link.
		 */
		public int dwSubEntry;
		/**
		 * A GUID (Globally Unique IDentifier) that represents the phone-book entry. The value of this member corresponds to that of the guidId member in the RASENTRY structure.
		 */
		public GUID guidEntry;
		/**
		 * A value that specifies zero or more of the following flags.
		 */
		public int dwFlags;
		/**
		 * A locally unique identifier (LUID) that specifies the logon session of the RAS connection.
		 */
		public LUID luid;
		/**
		 * A GUID that specifies the RAS connection correlation ID. The correlation ID is logged with the RAS connection setup, disconnect and setup failure events
		 * and identifies the RAS connection event logs on the client and server.
		 */
		public GUID guidCorrelationId;

		@Override
		protected List<String> getFieldOrder() {
			return Arrays.asList(new String[] { "dwSize", "hrasconn", "szEntryName", "szDeviceType", "szDeviceName", "szPhonebook", "dwSubEntry", "guidEntry", "dwFlags", "luid", "guidCorrelationId" });
		}
	}

	/**
	 * The RAS_STATS structure stores the statistics for a single-link RAS connection, or for one of the links in a multilink RAS connection.
	 */
	public static class RAS_STATS extends Structure {
		public RAS_STATS() {
			super();
			dwSize = size();
		}

		public RAS_STATS(Pointer memory) {
			super(memory);
			read();
		}

		/**
		 * Specifies the version of the structure. Set this member to sizeof(RAS_STATS) before using the structure in a function call.
		 */
		public int dwSize;
		/**
		 * The number of bytes transmitted through this connection or link.
		 */
		public int dwBytesXmited;
		/**
		 * The number of bytes received through this connection or link.
		 */
		public int dwBytesRcved;
		/**
		 * The number frames transmitted through this connection or link.
		 */
		public int dwFramesXmited;
		/**
		 * The number of frames received through this connection or link.
		 */
		public int dwFramesRcved;
		/**
		 * The number of cyclic redundancy check (CRC) errors on this connection or link.
		 */
		public int dwCrcErr;
		/**
		 * The number of timeout errors on this connection or link.
		 */
		public int dwTimeoutErr;
		/**
		 * The number of alignment errors on this connection or link.
		 */
		public int dwAlignmentErr;
		/**
		 * The number of hardware overrun errors on this connection or link.
		 */
		public int dwHardwareOverrunErr;
		/**
		 * The number of framing errors on this connection or link.
		 */
		public int dwFramingErr;
		/**
		 * The number of buffer overrun errors on this connection or link.
		 */
		public int dwBufferOverrunErr;
		/**
		 * The compression ratio for the data being received on this connection or link.
		 */
		public int dwCompressionRatioIn;
		/**
		 * The compression ratio for the data being transmitted on this connection or link.
		 */
		public int dwCompressionRatioOut;
		/**
		 * The speed of the connection or link, in bits per second.
		 */
		public int dwBps;
		/**
		 * The amount of time, in milliseconds, that the connection or link has been connected.
		 */
		public int dwConnectDuration;

		@Override
		protected List<String> getFieldOrder() {
			return Arrays.asList(new String[] { "dwSize", "dwBytesXmited", "dwBytesRcved", "dwFramesXmited", "dwFramesRcved", "dwCrcErr", "dwTimeoutErr", "dwAlignmentErr", "dwHardwareOverrunErr", "dwFramingErr", "dwBufferOverrunErr", "dwCompressionRatioIn", "dwCompressionRatioOut", "dwBps", "dwConnectDuration", });
		}
	}

	/**
	 * This RASTUNNELENDPOINT structure is used to define the end-point of a virtual private network (VPN) tunnel.
	 */
	public static class RASIPV4ADDR extends Structure {
		public RASIPV4ADDR() {
			super();
		}

		public RASIPV4ADDR(Pointer memory) {
			super(memory);
			read();
		}

		/**
		 * A value that determines endpoint type
		 */
		public byte[] addr = new byte[8];

		@Override
		protected List<String> getFieldOrder() {
			return Arrays.asList(new String[] { "addr",	});
		}
	}

	/**
	 * This RASTUNNELENDPOINT structure is used to define the end-point of a virtual private network (VPN) tunnel.
	 */
	public static class RASIPV6ADDR extends Structure {
		public RASIPV6ADDR() {
			super();
		}

		public RASIPV6ADDR(Pointer memory) {
			super(memory);
			read();
		}

		/**
		 * A value that determines endpoint type
		 */
		public byte[] addr = new byte[16];

		@Override
		protected List<String> getFieldOrder() {
			return Arrays.asList(new String[] { "addr",	});
		}
	}

	/**
	 * The RASPPPIP structure contains the result of a PPP IP projection operation.
	 */
	public static class RASPPPIP extends Structure {
		public RASPPPIP() {
			super();
			dwSize = size();
		}

		public RASPPPIP(Pointer memory) {
			super(memory);
			read();
		}

		public static class ByReference extends RASPPPIP implements Structure.ByReference {
		}

		/**
		 * A value that specifies the size, in bytes, of the structure. This member must be set before it is used in a function call.
		 */
		public int dwSize;
		/**
		 * A value that specifies the result of the PPP control protocol negotiation. A value of zero indicates success.
		 * A non-zero value indicates failure, and indicates the fatal error that occurred during the control protocol negotiation.
		 */
		public int dwError;
		/**
		 * An array that contains a null-terminated string that is the client's IP address on the RAS connection.
		 * This address string has the form a.b.c.d.
		 */
		public char[] szIpAddress = new char[RAS_MaxIpAddress + 1];
		/**
		 * An array that contains a null-terminated string that is the server's IP address on the RAS connection.
		 * This string is in a.b.c.d form.
		 * PPP does not require that servers provide this address, but servers will consistently return the address anyway.
		 * Other PPP vendors may not provide the address. If the address is not available, this member returns an empty string, "".
		 */
		public char[] szServerIpAddress = new char[RAS_MaxIpAddress + 1];
		/**
		 * A value that specifies IPCP options for the local client.
		 */
		public int dwOptions;
		/**
		 * A value that specifies IPCP options for the remote server.
		 */
		public int dwServerOptions;

		@Override
		protected List<String> getFieldOrder() {
			return Arrays.asList(new String[] { "dwSize", "dwError", "szIpAddress",	"szServerIpAddress", "dwOptions", "dwServerOptions",});
		}
	}

	/**
	 * This RASTUNNELENDPOINT structure is used to define the end-point of a virtual private network (VPN) tunnel.
	 */
	public static class RASTUNNELENDPOINT extends Structure {
		public RASTUNNELENDPOINT() {
			super();
		}

		public RASTUNNELENDPOINT(Pointer memory) {
			super(memory);
			read();
		}

		public static class UNION extends Union {
			public static class ByReference extends UNION  implements Structure.ByReference {

			}

			public RASIPV4ADDR ipv4;
			public RASIPV6ADDR ipv6;
		}

		/**
		 * A value that determines endpoint type
		 */
		public int dwType;
		/**
		 * The union structure
		 */
		public UNION u;

		@Override
		protected List<String> getFieldOrder() {
			return Arrays.asList(new String[] { "dwType", "u",	});
		}

		@Override
		public void read() {
			super.read();

			switch(dwType) {
			case RASTUNNELENDPOINT_IPv4:
				u.setType(RASIPV4ADDR.class);
				break;
			case RASTUNNELENDPOINT_IPv6:
				u.setType(RASIPV6ADDR.class);
				break;
			default:
				u.setType(RASIPV4ADDR.class);
				break;
			}

			u.read();
		}
	}

	/**
	 * The RASCONNSTATUS structure describes the current status of a remote access connection. It is returned by the RasGetConnectStatus function.
	 */
	public static class RASCONNSTATUS extends Structure {
		public RASCONNSTATUS() {
			super();
			dwSize = size();
		}

		public RASCONNSTATUS(Pointer memory) {
			super(memory);
			read();
		}

		/**
		 * Specifies the structure size, in bytes.
		 */
		public int dwSize;
		/**
		 * Specifies a RASCONNSTATE enumerator value that indicates the current state of the RasDial connection process;
		 * that is, the piece of the RasDial process that is currently executing.
		 */
		public int rasconnstate;
		/**
		 * If nonzero, indicates the reason for failure. The value is one of the error values from the RasError.h
		 * header file or one of ERROR_NOT_ENOUGH_MEMORY or ERROR_INVALID_HANDLE.
		 */
		public int dwError;
		/**
		 * A string that specifies the type of the current device, if available. For example, common device types supported by
		 * RAS are "modem", "pad", "switch", "ISDN", or "null". See RASENTRY for a complete list of possible device types.
		 */
		public char[] szDeviceType = new char[RAS_MaxDeviceType + 1];
		/**
		 * A string that specifies the name of the current device, if available. This would be the name of the modem -
		 * for example, "Hayes SmartModem 2400"; the name of the PAD, for example "US Sprint"; or the name of a
		 * switch device, for example "Racal-Guardata".
		 */
		public char[] szDeviceName = new char[RAS_MaxDeviceName + 1];
		/**
		 * A string that indicates the phone number dialed for this specific connection.
		 */
		public char[] szPhoneNumber = new char[RAS_MaxPhoneNumber + 1];
		/**
		 * A RASTUNNELENDPOINT structure that contains the local client endpoint information of a virtual private network (VPN) endpoint.
		 */
		public RASTUNNELENDPOINT localEndPoint;
		/**
		 * A RASTUNNELENDPOINT structure that contains the remote server endpoint information of a virtual private network (VPN) endpoint.
		 */
		public RASTUNNELENDPOINT remoteEndPoint;
		/**
		 * A RASCONNSUBSTATE enumeration that specifies state information of an Internet Key Exchange version 2 (IKEv2) VPN tunnel.
		 */
		public int rasconnsubstate;

		@Override
		protected List<String> getFieldOrder() {
			return Arrays.asList(new String[] { "dwSize", "rasconnstate", "dwError", "szDeviceType", "szDeviceName", "szPhoneNumber", "localEndPoint", "remoteEndPoint", "rasconnsubstate" });
		}
	}

	/**
	 * The RASCREDENTIALS structure is used with the RasGetCredentials and RasSetCredentials functions to specify the user credentials associated with a RAS phone-book entry.
	 */
	public static class RASCREDENTIALS extends Structure {
		public RASCREDENTIALS() {
			super();
			dwSize = size();
		}

		public RASCREDENTIALS(Pointer memory) {
			super(memory);
			read();
		}

		public static class ByReference extends RASCREDENTIALS implements Structure.ByReference {
		}

		/**
		 * Specifies the size, in bytes, of the RASCREDENTIALS structure.
		 */
		public int dwSize;
		/**
		 * Specifies a set of bit flags. These flags indicate the members of this structure that are valid. On input, set the flags to indicate the members of interest.
		 */
		public int dwMask;
		/**
		 * Specifies a null-terminated string that contains a user name.
		 */
		public char[] szUserName = new char[UNLEN + 1];
		/**
		 * Specifies a null-terminated string that contains a password.
		 */
		public char[] szPassword = new char[PWLEN + 1];
		/**
		 * A null-terminated string that contains a domain name.
		 */
		public char[] szDomain = new char[DNLEN + 1];

		@Override
		protected List<String> getFieldOrder() {
			return Arrays.asList(new String[] { "dwSize", "dwMask", "szUserName", "szPassword", "szDomain",  });
		}
	}

	/**
	 * The RASIPADDR structure contains an IP address of the form "a.b.c.d".
	 * The RASENTRY structure uses this structure to specify the IP addresses of various servers associated with an entry in a RAS phone book.
	 */
	public static class RASIPADDR extends Structure {
		public RASIPADDR() {
			super();
		}

		public RASIPADDR(Pointer memory) {
			super(memory);
			read();
		}

		public byte[] addr = new byte[4];

		@Override
		protected List<String> getFieldOrder() {
			return Arrays.asList(new String[] { "addr",  });
		}
	}

	/**
	 * The RASENTRY structure describes a phone-book entry. The RasSetEntryProperties and RasGetEntryProperties
	 * functions use this structure to set and retrieve the properties of a phone-book entry.
	 */
	public static class RASENTRY extends Structure {
		public RASENTRY() {
			super();
			dwSize = size();
		}

		public RASENTRY(Pointer memory) {
			super(memory);
			read();
		}

		public static class ByReference extends RASENTRY implements Structure.ByReference {
		}

		/**
		 * Specifies the size, in bytes, of the RASENTRY structure. Before calling RasSetEntryProperties or
		 * RasGetEntryProperties, set dwSize to sizeof(RASENTRY) to identify the version of the structure.
		 */
		public int dwSize;
		/**
		 * A set of bit flags that specify connection options. Set one or more of the following flags.
		 */
		public int dwfOptions;
		/**
		 * Specifies the TAPI country/region identifier. Use the RasGetCountryInfo function to enumerate
		 * country/region identifiers. This member is ignored unless the dwfOptions member specifies the RASEO_UseCountryAndAreaCodes flag.
		 */
		public int dwCountryID;
		/**
		 * Specifies the country/region code portion of the phone number. The country/region code must
		 * correspond to the country/region identifier specified by dwCountryID. If dwCountryCode is zero, the
		 * country/region code is based on the country/region identifier specified by dwCountryID.
		 * This member is ignored unless dwfOptions specifies the RASEO_UseCountryAndAreaCodes flag.
		 */
		public int dwCountryCode;
		/**
		 * Specifies the area code as a null-terminated string. If the dialing location does not have an area code,
		 * specify an empty string (""). Do not include parentheses or other delimiters in the area code string.
		 * For example, "206" is a valid area code; "(206)" is not. This member is ignored unless the dwfOptions member
		 * specifies the RASEO_UseCountryAndAreaCodes flag.
		 */
		public char[] szAreaCode = new char[RAS_MaxAreaCode + 1];
		/**
		 * Specifies a null-terminated device-type specific destination string.
		 * The following table describes the contents of the szLocalPhoneNumber member for various device types.
		 */
		public char[] szLocalPhoneNumber = new char[RAS_MaxPhoneNumber + 1];
		/**
		 * Specifies the offset, in bytes, from the beginning of the structure to a list of consecutive null-terminated strings.
		 * The last string is terminated by two consecutive null characters. The strings are alternate phone numbers that RAS
		 * dials in the order listed if the primary number (see szLocalPhoneNumber) fails to connect.
		 * The alternate phone number strings are ANSI or Unicode, depending on whether you use the ANSI or Unicode version of the structure.
		 */
		public int dwAlternateOffset;
		/**
		 * Specifies the IP address to be used while this connection is active. This member is ignored unless dwfOptions specifies the RASEO_SpecificIpAddr flag.
		 */
		public RASIPADDR ipaddr;
		/**
		 * Specifies the IP address of the DNS server to be used while this connection is active.
		 * This member is ignored unless dwfOptions specifies the RASEO_SpecificNameServers flag.
		 */
		public RASIPADDR ipaddrDns;
		/**
		 * Specifies the IP address of a secondary or backup DNS server to be used while this connection is active.
		 * This member is ignored unless dwfOptions specifies the RASEO_SpecificNameServers flag.
		 */
		public RASIPADDR ipaddrDnsAlt;
		/**
		 * Specifies the IP address of the WINS server to be used while this connection is active.
		 * This member is ignored unless dwfOptions specifies the RASEO_SpecificNameServers flag.
		 */
		public RASIPADDR ipaddrWins;
		/**
		 * Specifies the IP address of a secondary WINS server to be used while this connection is active.
		 * This member is ignored unless dwfOptions specifies the RASEO_SpecificNameServers flag.
		 */
		public RASIPADDR ipaddrWinsAlt;
		/**
		 * Specifies the IP address of a secondary WINS server to be used while this connection is active.
		 * This member is ignored unless dwfOptions specifies the RASEO_SpecificNameServers flag.
		 */
		public int dwFrameSize;
		/**
		 * Specifies the network protocols to negotiate. This member can be a combination of the following flags.
		 */
		public int dwfNetProtocols;
		/**
		 * Specifies the framing protocol used by the server. PPP is the emerging standard. SLIP is used mainly in UNIX environments.
		 * This member can be one of the following flags.
		 */
		public int dwFramingProtocol;
		/**
		 * Specifies a null-terminated string that contains the name of the script file.
		 * The file name should be a full path. This field is only used for analog dial-up connections.
		 */
		public char[] szScript = new char[MAX_PATH];
		/**
		 * Windows 2000 or later: This member is no longer supported. The szCustomDialDll member of the
		 * RASENTRY structure specifies the path to the custom-dial DLL. For more information on custom dialers, see RAS Custom Dialers.
		 */
		public char[] szAutodialDll = new char[MAX_PATH];
		/**
		 * Windows 2000 or later: This member is no longer supported. See RAS Custom Dialers for more information on custom dialers.
		 */
		public char[] szAutodialFunc = new char[MAX_PATH];
		/**
		 * Specifies a null-terminated string that indicates the RAS device type referenced by szDeviceName. This member can be one of the following string constants.
		 */
		public char[] szDeviceType = new char[RAS_MaxDeviceType + 1];
		/**
		 * Contains a null-terminated string that contains the name of a TAPI device to use with this phone-book entry, for example,
		 * "XYZ Corp 28800 External". To enumerate all available RAS-capable devices, use the RasEnumDevices function.
		 */
		public char[] szDeviceName = new char[RAS_MaxDeviceName + 1];
		/**
		 * Contains a null-terminated string that identifies the X.25 PAD type. Set this member to "" unless the entry
		 * should dial using an X.25 PAD. The szX25PadType string maps to a section name in PAD.INF.
		 */
		public char[] szX25PadType = new char[RAS_MaxPadType + 1];
		/**
		 * Contains a null-terminated string that identifies the X.25 address to which to connect .
		 * Set this member to "" unless the entry should dial using an X.25 PAD or native X.25 device.
		 */
		public char[] szX25Address = new char[RAS_MaxX25Address + 1];
		/**
		 * Contains a null-terminated string that specifies the facilities to request from the X.25 host at connection.
		 * This member is ignored if szX25Address is an empty string ("").
		 */
		public char[] szX25Facilities = new char[RAS_MaxFacilities + 1];
		/**
		 * Contains a null-terminated string that specifies additional connection information supplied to the X.25
		 * host at connection. This member is ignored if szX25Address is an empty string ("").
		 */
		public char[] szX25UserData = new char[RAS_MaxUserData + 1];
		/**
		 * Reserved for future use
		 */
		public int dwChannels;
		/**
		 * Reserved. Must be zero.
		 */
		public int dwReserved1;
		/**
		 * Reserved. Must be zero.
		 */
		public int dwReserved2;
		/**
		 * Specifies the number of multilink subentries associated with this entry. When calling RasSetEntryProperties,
		 * set this member to zero. To add subentries to a phone-book entry, use the RasSetSubEntryProperties function.
		 */
		public int dwSubEntries;
		/**
		 * Specifies whether RAS should dial all of this entry's multilink subentries when the entry is first connected.
		 * This member can be one of the following values.
		 */
		public int dwDialMode;
		/**
		 * Specifies a percent of the total bandwidth available from the currently connected subentries.
		 * RAS dials an additional subentry when the total bandwidth used exceeds dwDialExtraPercent percent of the available
		 * bandwidth for at least dwDialExtraSampleSeconds seconds.
		 */
		public int dwDialExtraPercent;
		/**
		 * Specifies the number of seconds that current bandwidth usage must exceed the threshold specified by dwDialExtraPercent
		 * before RAS dials an additional subentry.
		 */
		public int dwDialExtraSampleSeconds;
		/**
		 * Specifies a percent of the total bandwidth available from the currently connected subentries.
		 * RAS terminates (hangs up) an existing subentry connection when total bandwidth used is less than dwHangUpExtraPercent
		 * percent of the available bandwidth for at least dwHangUpExtraSampleSeconds seconds.
		 */
		public int dwHangUpExtraPercent;
		/**
		 * Specifies the number of seconds that current bandwidth usage must be less than the threshold specified by
		 * dwHangUpExtraPercent before RAS terminates an existing subentry connection.
		 */
		public int dwHangUpExtraSampleSeconds;
		/**
		 * Specifies the number of seconds after which the connection is terminated due to inactivity.
		 * Note that unless the idle time out is disabled, the entire connection is terminated if the connection is
		 * idle for the specified interval. This member can specify a number of seconds, or one of the following values.
		 */
		public int dwIdleDisconnectSeconds;
		/**
		 * The type of phone-book entry. This member can be one of the following types.
		 */
		public int dwType;
		/**
		 * The type of encryption to use with the connection. The encryption is either provided by
		 * IPSec (for L2TP/IPSec connections) or by Microsoft Point-to-Point Encryption (MPPE). This member can be one of the following values.
		 */
		public int dwEncryptionType;
		/**
		 * This member is used for Extensible Authentication Protocol (EAP). This member contains the authentication key provided to the EAP vendor.
		 */
		public int dwCustomAuthKey;
		/**
		 * The GUID (Globally Unique Identifier) that represents this phone-book entry. This member is read-only.
		 */
		public GUID guidId;
		/**
		 * A null-terminated string that contains the full path and file name for the dynamic link library (DLL) that implements the custom-dialing functions.
		 * This DLL should export Unicode versions of functions named RasCustomDial, RasCustomHangup, RasCustomEntryDlg, and
		 * RasCustomDialDlg. These functions should have prototypes RasCustomDialFn and RasCustomHangUpFn as defined in Ras.h,
		 * and RasCustomDialDlgFn and RasCustomEntryDlgFn as defined in Rasdlg.h.
		 */
		public char[] szCustomDialDll = new char[MAX_PATH];
		/**
		 * The VPN strategy to use when dialing a VPN connection. This member can have one of the following values.
		 */
		public int dwVpnStrategy;
		/**
		 * A set of bits that specify connection options. This member is a continuation of the bits specified
		 * in the dwfOptions member. Set one or more of the following bit flags.
		 */
		public int dwfOptions2;
		/**
		 * This parameter is reserved for future use.
		 */
		public int dwfOptions3;
		/**
		 * Pointer to a string that specifies the Domain Name Service (DNS) suffix for the connection.
		 * This string can be Unicode depending on the version of the structure you are using.
		 */
		public char[] szDnsSuffix = new char[RAS_MaxDnsSuffix];
		/**
		 * Specifies the TCP window size for all TCP sessions that run over this connection.
		 * Setting this value can increase the throughput of high-latency devices such as cellular phones.
		 */
		public int dwTcpWindowSize;
		/**
		 * Pointer to a null-terminated string that specifies the full path and file name of a phone-book (PBK) file.
		 * This phone-book file contains the entry specified by the szPrerequisiteEntry member. This member is used only for VPN connections.
		 */
		public char[] szPrerequisitePbk = new char[MAX_PATH];
		/**
		 * Pointer to a null-terminated string that specifies a phone-book entry.
		 * This entry should exist in the phone-book file specified by the szPrerequisitePbk member.
		 * The szPrerequisteEntry member specifies an entry that RAS dials prior to establishing the connection
		 * specified by this RASENTRY structure. This member is used only for VPN connections.
		 */
		public char[] szPrerequisiteEntry = new char[RAS_MaxEntryName + 1];
		/**
		 * Specifies the number of times RAS attempts to redial a connection.
		 */
		public int dwRedialCount;
		/**
		 * Specifies the number of seconds to wait between redial attempts.
		 */
		public int dwRedialPause;
		/**
		 * Contains the IPv6 address of the preferred DNS. A RASIPV6ADDR type is identical to a in6_addr structure.
		 */
		public RASIPV6ADDR ipv6addrDns;
		/**
		 * Contains the IPv6 address of the alternate DNS. A RASIPV6ADDR type is identical to a in6_addr structure.
		 */
		public RASIPV6ADDR ipv6addrDnsAlt;
		/**
		 * Sets the metric of the IPv4 stack for this interface.
		 */
		public int dwIPv4InterfaceMetric;
		/**
		 * Sets the metric of the IPv6 stack for this interface.
		 */
		public int dwIPv6InterfaceMetric;
		/**
		 * Specifies the client's IPv6 address negotiated by the server and the client.
		 */
		public RASIPV6ADDR ipv6addr;
		/**
		 * The length of the IPv6 address prefix in ipv6addr.
		 */
		public int dwIPv6PrefixLength;
		/**
		 * Specifies the amount of time, in minutes, that IKEv2 packets will be
		 * retransmitted without a response before the connection is considered lost.
		 * Increase this value to support connection persistence during network outages.
		 */
		public int dwNetworkOutageTime;

		@Override
		protected List<String> getFieldOrder() {
			return Arrays.asList(new String[] { "dwSize", "dwfOptions", "dwCountryID", "dwCountryCode", "szAreaCode", "szLocalPhoneNumber", "dwAlternateOffset",
					"ipaddr", "ipaddrDns", "ipaddrDnsAlt", "ipaddrWins", "ipaddrWinsAlt", "dwFrameSize", "dwfNetProtocols", "dwFramingProtocol",
					"szScript", "szAutodialDll", "szAutodialFunc", "szDeviceType", "szDeviceName", "szX25PadType", "szX25Address", "szX25Facilities", "szX25UserData" ,
					"dwChannels", "dwReserved1", "dwReserved2", "dwSubEntries", "dwDialMode", "dwDialExtraPercent", "dwDialExtraSampleSeconds", "dwHangUpExtraPercent",
					"dwHangUpExtraSampleSeconds", "dwIdleDisconnectSeconds", "dwType", "dwEncryptionType", "dwCustomAuthKey", "guidId", "szCustomDialDll",
					"dwVpnStrategy", "dwfOptions2", "dwfOptions3", "szDnsSuffix", "dwTcpWindowSize", "szPrerequisitePbk", "szPrerequisiteEntry", "dwRedialCount",
					"dwRedialPause", "ipv6addrDns", "ipv6addrDnsAlt", "dwIPv4InterfaceMetric", "dwIPv6InterfaceMetric", "ipv6addr", "dwIPv6PrefixLength", "dwNetworkOutageTime",
			});
		}
	}

	/**
	 * The asynchronous dial calback interface
	 */
	public interface RasDialFunc2 extends StdCallCallback {
		public int dialNotification(int dwCallbackId, int dwSubEntry, HANDLE hrasconn, int unMsg, int rascs, int dwError, int dwExtendedError);
	}
}
