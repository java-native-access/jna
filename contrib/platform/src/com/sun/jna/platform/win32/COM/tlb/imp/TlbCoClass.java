/* Copyright (c) 2013 Tobias Wolf, All Rights Reserved
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
package com.sun.jna.platform.win32.COM.tlb.imp;

import java.io.PrintStream;

import com.sun.jna.platform.win32.COM.TypeLibUtil;
import com.sun.jna.platform.win32.COM.TypeLibUtil.TypeLibDoc;

// TODO: Auto-generated Javadoc
/**
 * The Class TlbClass.
 * 
 * @author Tobias Wolf, wolf.tobias@gmx.net
 */
public class TlbCoClass extends TlbBase {

	/**
	 * Instantiates a new tlb class.
	 * 
	 * @param index
	 *            the index
	 * @param typeLibUtil
	 *            the type lib util
	 */
	public TlbCoClass(int index, TypeLibUtil typeLibUtil) {
		super(index, typeLibUtil);

		TypeLibDoc typeLibDoc = this.typeLibUtil.getDocumentation(index);
		String coClassName = typeLibDoc.getName();
		String docString = typeLibDoc.getDocString();

		this.createClassName(coClassName);
		this.setFilename(coClassName);
		
		String guidStr = this.typeLibUtil.getLibAttr().guid.toGuidString();
		int majorVerNum = this.typeLibUtil.getLibAttr().wMajorVerNum.intValue();
		int minorVerNum = this.typeLibUtil.getLibAttr().wMinorVerNum.intValue();
		String version = majorVerNum + "." + minorVerNum;

		this.createJavaDocHeader(guidStr, version, docString);
		this.createCLSID(guidStr);
	}

	/**
	 * Creates the java doc header.
	 * 
	 * @param guid
	 *            the guid
	 * @param version
	 *            the version
	 * @param helpstring
	 *            the helpstring
	 */
	protected void createJavaDocHeader(String guid, String version,
			String helpstring) {
		this.replaceVariable("uuid", guid);
		this.replaceVariable("version", version);
		this.replaceVariable("helpstring", helpstring);
	}

	protected void createCLSID(String clsid) {
		this.replaceVariable("clsid", clsid);
	}

	/**
	 * Creates the package.
	 * 
	 * @param packageName
	 *            the package name
	 */
	public void createPackage(String packageName) {
		this.replaceVariable("packagename", packageName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sun.jna.platform.win32.COM.tlb.imp.TlbBase#getClassTemplate()
	 */
	@Override
	protected String getClassTemplate() {
		return "com/sun/jna/platform/win32/COM/tlb/imp/TlbCoClass.template";
	}
}
