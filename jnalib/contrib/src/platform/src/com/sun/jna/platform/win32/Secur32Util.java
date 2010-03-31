package com.sun.jna.platform.win32;

import java.util.ArrayList;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.Sspi.PSecPkgInfo;
import com.sun.jna.platform.win32.Sspi.SecPkgInfo;
import com.sun.jna.ptr.IntByReference;

/**
 * Secur32 Utility API.
 * @author dblock[at]dblock.org
 */
public abstract class Secur32Util {

	/**
	 * An SSPI package.
	 */
	public static class SecurityPackage {
		/**
		 * Package name.
		 */
		public String name;
		/**
		 * Package comment.
		 */
		public String comment;
	}
	
	/**
	 * Retrieves the name of the user or other security principal associated 
	 * with the calling thread.
	 * 
	 * @param format User name format.
	 * @return User name in a given format.
	 */
	public static String getUserNameEx(int format) {
		char[] buffer = new char[128];
		IntByReference len = new IntByReference(buffer.length);
		boolean result = Secur32.INSTANCE.GetUserNameEx(format, buffer, len); 
		
		if (! result) {
			
			int rc = Kernel32.INSTANCE.GetLastError();

			switch(rc) {
			case W32Errors.ERROR_MORE_DATA:
				buffer = new char[len.getValue() + 1];
				break;
			default:
				throw new Win32Exception(Native.getLastError());
			}
			
			result = Secur32.INSTANCE.GetUserNameEx(format, buffer, len);
		}
		
		if (! result) {
			throw new Win32Exception(Native.getLastError());
		}
		
		return Native.toString(buffer);		
	}
	
	/**
	 * Get the security packages installed on the current computer.
	 * @return
	 *  An array of SSPI security packages.
	 */
	public static SecurityPackage[] getSecurityPackages() {
    	IntByReference pcPackages = new IntByReference();
    	PSecPkgInfo.ByReference pPackageInfo = new PSecPkgInfo.ByReference();
    	int rc = Secur32.INSTANCE.EnumerateSecurityPackages(pcPackages, pPackageInfo);
    	if(W32Errors.SEC_E_OK != rc) {
    		throw new Win32Exception(rc);
    	}
    	SecPkgInfo.ByReference[] packagesInfo = pPackageInfo.toArray(pcPackages.getValue());
    	ArrayList<SecurityPackage> packages = new ArrayList<SecurityPackage>(pcPackages.getValue());
    	for(SecPkgInfo.ByReference packageInfo : packagesInfo) {
    		SecurityPackage securityPackage = new SecurityPackage();
    		securityPackage.name = packageInfo.Name.toString();
    		securityPackage.comment = packageInfo.Comment.toString();
    		packages.add(securityPackage);
    	}
    	rc = Secur32.INSTANCE.FreeContextBuffer(pPackageInfo.getPointer());
    	if(W32Errors.SEC_E_OK != rc) {
    		throw new Win32Exception(rc);
    	}
    	return packages.toArray(new SecurityPackage[0]);		
	}
}
