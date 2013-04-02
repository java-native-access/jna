package com.sun.jna.platform.win32.COM.tlb.imp;

import java.io.PrintStream;

import com.sun.jna.platform.win32.COM.ITypeLibUtil;
import com.sun.jna.platform.win32.COM.ITypeLibUtil.TypeLibDoc;

public class TlbClass extends TlbBase {

	public TlbClass(int index, ITypeLibUtil typeLibUtil) {
		super(index, typeLibUtil);

		TypeLibDoc typeLibDoc = this.typeLibUtil.getDocumentation(index);
		String enumName = typeLibDoc.getName();
		String docString = typeLibDoc.getDocString();

		this.createClassName(enumName);
		
		String guidStr = this.typeLibUtil.getLibAttr().guid.toGuidString();
		int majorVerNum = this.typeLibUtil.getLibAttr().wMajorVerNum.intValue();
		int minorVerNum = this.typeLibUtil.getLibAttr().wMinorVerNum.intValue();
		String version = majorVerNum + "." + minorVerNum;
		
		this.createJavaDocHeader(guidStr, version, docString);
	}

	protected void createJavaDocHeader(String guid, String version,
			String helpstring) {
		this.replaceVariable("uuid", guid);
		this.replaceVariable("version", version);
		this.replaceVariable("helpstring", helpstring);
	}

	public void createPackage(String packageName) {
		this.replaceVariable("packagename", packageName);
	}

	@Override
	protected String getClassTemplate() {
		return "com/sun/jna/platform/win32/COM/tlb/imp/TlbClass.template";
	}
}
