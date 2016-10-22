/* Copyright (c) 2015 Adam Marcionek, All Rights Reserved
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
import com.sun.jna.win32.W32APITypeMapper;

/**
 * Ported from AccCtrl.h. Microsoft Windows SDK 7.1
 *
 * @author amarcionek[at]gmail.com
 */

public abstract class Winnetwk {

    /**
     * The scope of the enumeration. This member can be one of the following
     * values defined in the Winnetwk.h header file. NOTE: This are for the
     * dwScope member of NetResource. NOTE: Certain functions allow different
     * values of the scope parameter. Consult MSDN for more info.
     */
    public class RESOURCESCOPE {

        /**
         * Enumerate currently connected resources. The dwUsage member cannot be
         * specified.
         */
        public static final int RESOURCE_CONNECTED = 1;

        /**
         * Enumerate all resources on the network. The dwUsage member is
         * specified.
         */
        public static final int RESOURCE_GLOBALNET = 2;

        /**
         * Enumerate remembered (persistent) connections. The dwUsage member
         * cannot be specified.
         */
        public static final int RESOURCE_REMEMBERED = 3;

        /**
         * NOTE: Definition for this is not defined in Windows Kits nor on MSDN
         */
        public static final int RESOURCE_RECENT = 4;

        /**
         * Enumerate only resources in the network context of the caller.
         * Specify this value for a Network Neighborhood view. The function
         * ignores the dwUsage parameter.
         */
        public static final int RESOURCE_CONTEXT = 5;
    }

    /**
     * The type of resource. This member can be one of the following values
     * defined in the Winnetwk.h header file. NOTE: This are for the dwType
     * member of NetResource
     */
    public class RESOURCETYPE {

        /**
         * All resources
         */
        public static final int RESOURCETYPE_ANY = 0;

        /**
         * Disk resources
         */
        public static final int RESOURCETYPE_DISK = 1;

        /**
         * Print resources
         */
        public static final int RESOURCETYPE_PRINT = 2;

        /**
         * NOTE: Definition for this is not defined in Windows Kits nor on MSDN
         */
        public static final int RESOURCETYPE_RESERVED = 8;

        /**
         * The WNetEnumResource function can also return the value
         * RESOURCETYPE_UNKNOWN if a resource is neither a disk nor a print
         * resource.
         */
        public static final int RESOURCETYPE_UNKNOWN = 0xFFFFFFFF;
    }

    /**
     * The type of resource. This member can be one of the following values
     * defined in the Winnetwk.h header file. NOTE: This are for the dwScope
     * member of NetResource
     */
    public class RESOURCEDISPLAYTYPE {

        /**
         * The method used to display the object does not matter.
         */
        public static final int RESOURCEDISPLAYTYPE_GENERIC = 0;

        /**
         * The object should be displayed as a domain.
         */
        public static final int RESOURCEDISPLAYTYPE_DOMAIN = 1;

        /**
         * The object should be displayed as a server.
         */
        public static final int RESOURCEDISPLAYTYPE_SERVER = 2;

        /**
         * The object should be displayed as a share.
         */
        public static final int RESOURCEDISPLAYTYPE_SHARE = 3;

        /**
         * The object should be displayed as a file.
         */
        public static final int RESOURCEDISPLAYTYPE_FILE = 4;

        // TODO: Add the others
    }

    /**
     * A set of bit flags describing how the resource can be used. Note that
     * this member can be specified only if the dwScope member is equal to
     * RESOURCE_GLOBALNET. This member can be one of the following values
     * defined in the Winnetwk.h header file. NOTE: This are for the dwUsage
     * member of NetResource
     */
    public class RESOURCEUSAGE {
        /**
         * The resource is a connectable resource; the name pointed to by the
         * lpRemoteName member can be passed to the WNetAddConnection function
         * to make a network connection.
         */
        public static final int RESOURCEUSAGE_CONNECTABLE = 0x00000001;

        /**
         * The resource is a container resource; the name pointed to by the
         * lpRemoteName member can be passed to the WNetOpenEnum function to
         * enumerate the resources in the container.
         */
        public static final int RESOURCEUSAGE_CONTAINER = 0x00000002;

        /**
         * The resource is not a local device.
         */
        public static final int RESOURCEUSAGE_NOLOCALDEVICE = 0x00000004;

        /**
         * The resource is a sibling. This value is not used by Windows.
         */
        public static final int RESOURCEUSAGE_SIBLING = 0x00000008;

        /**
         * The resource must be attached. This value specifies that a function
         * to enumerate resource this should fail if the caller is not
         * authenticated, even if the network permits enumeration without
         * authentication.
         */
        public static final int RESOURCEUSAGE_ATTACHED = 0x00000010;

        /**
         * Setting this value is equivalent to setting
         * RESOURCEUSAGE_CONNECTABLE, RESOURCEUSAGE_CONTAINER, and
         * RESOURCEUSAGE_ATTACHED.
         */
        public static final int RESOURCEUSAGE_ALL = RESOURCEUSAGE_CONNECTABLE | RESOURCEUSAGE_CONTAINER | RESOURCEUSAGE_ATTACHED;
    }

    /**
     * A set of bit flags describing how the resource can be used. Note that
     * this member can be specified only if the dwScope member is equal to
     * RESOURCE_GLOBALNET. This member can be one of the following values
     * defined in the Winnetwk.h header file. NOTE: This are for the dwUsage
     * member of NetResource
     */
    public class ConnectFlag {

        /**
         * This flag instructs the operating system to store the network
         * resource connection. If this bit flag is set, the operating system
         * automatically attempts to restore the connection when the user logs
         * on. The system remembers only successful connections that redirect
         * local devices. It does not remember connections that are unsuccessful
         * or deviceless connections. (A deviceless connection occurs when
         * lpLocalName is NULL or when it points to an empty string.) If this
         * bit flag is clear, the operating system does not automatically
         * restore the connection at logon.
         */
        public static final int CONNECT_UPDATE_PROFILE = 0x00000001;

        /**
         * If this flag is set, the operating system may interact with the user
         * for authentication purposes.
         */
        public static final int CONNECT_INTERACTIVE = 0x00000008;

        /**
         * This flag instructs the system not to use any default settings for
         * user names or passwords without offering the user the opportunity to
         * supply an alternative. This flag is ignored unless
         * CONNECT_INTERACTIVE is also set.
         */
        public static final int CONNECT_PROMPT = 0x00000010;

        /**
         * This flag forces the redirection of a local device when making the
         * connection. If the lpLocalName member of NETRESOURCE specifies a
         * local device to redirect, this flag has no effect, because the
         * operating system still attempts to redirect the specified device.
         * When the operating system automatically chooses a local device, the
         * dwType member must not be equal to RESOURCETYPE_ANY. If this flag is
         * not set, a local device is automatically chosen for redirection only
         * if the network requires a local device to be redirected. Windows XP:
         * When the system automatically assigns network drive letters, letters
         * are assigned beginning with Z:, then Y:, and ending with C:. This
         * reduces collision between per-logon drive letters (such as network
         * drive letters) and global drive letters (such as disk drives). Note
         * that previous releases assigned drive letters beginning with C: and
         * ending with Z:.
         */
        public static final int CONNECT_REDIRECT = 0x00000080;

        /**
         * If this flag is set, the connection was made using a local device
         * redirection. If the lpAccessName parameter points to a buffer, the
         * local device name is copied to the buffer.
         */
        public static final int CONNECT_LOCALDRIVE = 0x00000100;

        /**
         * If this flag is set, the operating system prompts the user for
         * authentication using the command line instead of a graphical user
         * interface (GUI). This flag is ignored unless CONNECT_INTERACTIVE is
         * also set. Windows 2000/NT and Windows Me/98/95: This value is not
         * supported.
         */
        public static final int CONNECT_COMMANDLINE = 0x00000800;

        /**
         * If this flag is set, and the operating system prompts for a
         * credential, the credential should be saved by the credential manager.
         * If the credential manager is disabled for the caller's logon session,
         * or if the network provider does not support saving credentials, this
         * flag is ignored. This flag is also ignored unless you set the
         * CONNECT_COMMANDLINE flag. Windows 2000/NT and Windows Me/98/95: This
         * value is not supported.
         */
        public static final int CONNECT_CMD_SAVECRED = 0x00001000;
    }

    /**
     * The NETRESOURCE structure contains information about a network resource.
     */
    public static class NETRESOURCE extends Structure {

        public static class ByReference extends NETRESOURCE implements Structure.ByReference {

            public ByReference() {

            }

            public ByReference(Pointer memory) {
                super(memory);
            }
        }

        public static final List<String> FIELDS = createFieldsOrder(
                "dwScope", "dwType", "dwDisplayType", "dwUsage", "lpLocalName", "lpRemoteName", "lpComment", "lpProvider");

        /**
         * The scope of the enumeration. This member can be one of the values
         * defined in class NetResourceSope.
         */
        public int dwScope;

        /**
         * The type of resource. This member can be one of first 3 values
         * defined in the NetResourceType.
         */
        public int dwType;

        /**
         * The display options for the network object in a network browsing user
         * interface. This member can be one of the values defined in the
         * NetResourceDisplayType.
         */
        public int dwDisplayType;

        /**
         * A set of bit flags describing how the resource can be used.
         */
        public int dwUsage;

        /**
         * If the dwScope member is equal to RESOURCE_CONNECTED or
         * RESOURCE_REMEMBERED, this member is a pointer to a null-terminated
         * character string that specifies the name of a local device. This
         * member is NULL if the connection does not use a device.
         */
        public String lpLocalName;

        /**
         * If the entry is a network resource, this member is a pointer to a
         * null-terminated character string that specifies the remote network
         * name.
         *
         * If the entry is a current or persistent connection, lpRemoteName
         * member points to the network name associated with the name pointed to
         * by the lpLocalName member.
         *
         * The string can be MAX_PATH characters in length, and it must follow
         * the network provider's naming conventions
         */
        public String lpRemoteName;

        /**
         * A pointer to a NULL-terminated string that contains a comment
         * supplied by the network provider.
         */
        public String lpComment;

        /**
         * A pointer to a NULL-terminated string that contains the name of the
         * provider that owns the resource. This member can be NULL if the
         * provider name is unknown. To retrieve the provider name, you can call
         * the WNetGetProviderName function.
         */
        public String lpProvider;

        public NETRESOURCE() {
            super(W32APITypeMapper.DEFAULT);
        }

        public NETRESOURCE(Pointer address) {
            super(address, Structure.ALIGN_DEFAULT, W32APITypeMapper.DEFAULT);
            read();
        }

        @Override
        protected List<String> getFieldOrder() {
            return FIELDS;
        }
    }

    //
    // Universal Naming.
    //
    public static int UNIVERSAL_NAME_INFO_LEVEL = 0x00000001;
    public static int REMOTE_NAME_INFO_LEVEL = 0x00000002;

    /**
     * The UNIVERSAL_NAME_INFO structure contains a pointer to a Universal
     * Naming Convention (UNC) name string for a network resource.
     */
    public static class UNIVERSAL_NAME_INFO extends Structure {

        public static class ByReference extends REMOTE_NAME_INFO implements Structure.ByReference {

            public ByReference() {
                super();
            }

            public ByReference(Pointer memory) {
                super(memory);
            }
        }

        public static final List<String> FIELDS = createFieldsOrder("lpUniversalName");
        /**
         * Pointer to the null-terminated UNC name string that identifies a
         * network resource.
         */
        public String lpUniversalName;

        public UNIVERSAL_NAME_INFO() {
            super(W32APITypeMapper.DEFAULT);
        }

        public UNIVERSAL_NAME_INFO(Pointer address) {
            super(address, Structure.ALIGN_DEFAULT, W32APITypeMapper.DEFAULT);
            read();
        }

        @Override
        protected List<String> getFieldOrder() {
            return FIELDS;
        }
    }

    /**
     * The REMOTE_NAME_INFO structure contains path and name information for a
     * network resource. The structure contains a member that points to a
     * Universal Naming Convention (UNC) name string for the resource, and two
     * members that point to additional network connection information strings.
     */
    public static class REMOTE_NAME_INFO extends Structure {

        public static class ByReference extends REMOTE_NAME_INFO implements Structure.ByReference {

            public ByReference() {

            }

            public ByReference(Pointer memory) {
                super(memory);
            }
        }

        public static final List<String> FIELDS = createFieldsOrder("lpUniversalName", "lpConnectionName", "lpRemainingPath");

        /**
         * Pointer to the null-terminated UNC name string that identifies a
         * network resource.
         */
        public String lpUniversalName;

        /**
         * Pointer to a null-terminated string that is the name of a network
         * connection.
         */
        public String lpConnectionName;

        /**
         * Pointer to a null-terminated name string.
         */
        public String lpRemainingPath;

        public REMOTE_NAME_INFO() {
            super(W32APITypeMapper.DEFAULT);
        }

        public REMOTE_NAME_INFO(Pointer address) {
            super(address, Structure.ALIGN_DEFAULT, W32APITypeMapper.DEFAULT);
            read();
        }

        @Override
        protected List<String> getFieldOrder() {
            return FIELDS;
        }
    }
}
