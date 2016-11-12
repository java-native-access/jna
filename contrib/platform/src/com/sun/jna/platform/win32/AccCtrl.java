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

import com.sun.jna.win32.StdCallLibrary;

/**
 * Ported from AccCtrl.h.
 * Microsoft Windows SDK 7.1
 * @author amarcionek[at]gmail.com
 */

public abstract class AccCtrl implements StdCallLibrary {

	/**
    * The SE_OBJECT_TYPE enumeration contains values that correspond to the 
    * types of Windows objects that support security. The functions, such as 
    * GetSecurityInfo and SetSecurityInfo, that set and retrieve the security 
    * information of an object, use these values to indicate the type of object.
    */
   public abstract class SE_OBJECT_TYPE {
       /**
        * Unknown object type.
        */
       public static final int SE_UNKNOWN_OBJECT_TYPE = 0;

       /**
        * Indicates a file or directory. The name string that identifies a 
        * file or directory object can be in one of the following formats:
        * 
        * A relative path, such as FileName.dat or ..\FileName
        * An absolute path, such as FileName.dat, C:\DirectoryName\FileName.dat,
        * or G:\RemoteDirectoryName\FileName.dat.
        * A UNC name, such as \\ComputerName\ShareName\FileName.dat.
        */
       public static final int SE_FILE_OBJECT = 1;

       /**
        * Indicates a Windows service. A service object can be a local service,
        * such as ServiceName, or a remote service, such as
        * \\ComputerName\ServiceName.
        */
       public static final int SE_SERVICE = 2;

       /**
        * Indicates a printer. A printer object can be a local printer, such as
        * PrinterName, or a remote printer, such as \\ComputerName\PrinterName.
        */
       public static final int SE_PRINTER = 3;

       /**
        * Indicates a registry key. A registry key object can be in the local
        * registry, such as CLASSES_ROOT\SomePath or in a remote registry,
        * such as \\ComputerName\CLASSES_ROOT\SomePath.
        * 
        * The names of registry keys must use the following literal strings to
        * identify the predefined registry keys: "CLASSES_ROOT", "CURRENT_USER",
        * "MACHINE", and "USERS".
        */
       public static final int SE_REGISTRY_KEY = 4;
       
       /**
        * Indicates a network share. A share object can be local, such as ShareName,
        * or remote, such as \\ComputerName\ShareName.
        */
       public static final int SE_LMSHARE = 5;
       
       /**
        * Indicates a local kernel object.  The GetSecurityInfo and 
        * SetSecurityInfo functions support all types of kernel objects.
        * The GetNamedSecurityInfo and SetNamedSecurityInfo functions work
        * only with the following kernel objects: semaphore, event, mutex,
        * waitable timer, and file mapping.
        */
       public static final int SE_KERNEL_OBJECT = 6;
       
       /**
        * Indicates a window station or desktop object on the local computer.
        * You cannot use GetNamedSecurityInfo and SetNamedSecurityInfo with
        * these objects because the names of window stations or desktops are
        * not unique.
        */
       public static final int SE_WINDOW_OBJECT = 7;
       
       /**
        * Indicates a directory service object or a property set or property
        * of a directory service object. The name string for a directory service
        * object must be in X.500 form, for example:
        * CN=SomeObject,OU=ou2,OU=ou1,DC=DomainName,DC=CompanyName,DC=com,O=internet
        */
       public static final int SE_DS_OBJECT = 8;
       
       /**
        * The server process can impersonate the client's security context on
        * remote systems.
        */
       public static final int SE_DS_OBJECT_ALL = 9;
              
       /**
        * Indicates a provider-defined object.
        */
       public static final int SE_PROVIDER_DEFINED_OBJECT = 10;
       
       /**
        * Indicates a WMI object.
        */
       public static final int SE_WMIGUID_OBJECT = 11;
       
       /**
        * Indicates an object for a registry entry under WOW64.
        */
       public static final int SE_REGISTRY_WOW64_32KEY = 12;
   }
}
