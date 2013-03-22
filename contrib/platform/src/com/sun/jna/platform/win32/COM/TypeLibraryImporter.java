package com.sun.jna.platform.win32.COM;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

import com.sun.jna.platform.win32.OaIdl.FUNCDESC;
import com.sun.jna.platform.win32.OaIdl.MEMBERID;
import com.sun.jna.platform.win32.OaIdl.TYPEATTR;
import com.sun.jna.platform.win32.OaIdl.TYPEKIND;
import com.sun.jna.platform.win32.OaIdl.VARDESC;
import com.sun.jna.platform.win32.WTypes.BSTR;

public class TypeLibraryImporter {

	public final static String CR = "\n";

	public final static String CRCR = "\n\n";

	private ITypeLibUtil typeLibUtil;

	private PrintStream out;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new TypeLibraryImporter().startCOM2Java();
	}

	public void startCOM2Java() {
		try {
			// Microsoft Shell Controls And Automation
			// this.typeLibUtil = new
			// ITypeLibUtil("{50A7E9B0-70EF-11D1-B75A-00A0C90564FE}");
			// MS Word
			this.typeLibUtil = new ITypeLibUtil(
					"{50A7E9B0-70EF-11D1-B75A-00A0C90564FE}");

			this.initPrintStream();
			this.createJavaFileHeader();
			this.createJavaDocHeader();

			for (int i = 0; i < typeLibUtil.getTypeInfoCount(); ++i) {
				TYPEKIND typekind = typeLibUtil.getTypeInfoType(i);
				ITypeInfoUtil typeInfoUtil = typeLibUtil.getTypeInfoUtil(i);
				Object[] typeLibDoc = this.typeLibUtil.getDocumentation(i);

				if (typekind.value == TYPEKIND.TKIND_ENUM) {
					this.createCOMEnum(typeLibDoc, typeInfoUtil);
				} else if (typekind.value == TYPEKIND.TKIND_RECORD) {

				} else if (typekind.value == TYPEKIND.TKIND_MODULE) {

				} else if (typekind.value == TYPEKIND.TKIND_INTERFACE) {

				} else if (typekind.value == TYPEKIND.TKIND_DISPATCH) {

				} else if (typekind.value == TYPEKIND.TKIND_COCLASS) {
				} else if (typekind.value == TYPEKIND.TKIND_ALIAS) {

				} else if (typekind.value == TYPEKIND.TKIND_UNION) {

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void initPrintStream() throws FileNotFoundException {
		String tmp = System.getProperty("java.io.tmpdir");
		File javaDir = new File(tmp + "_jnaCOM_" + System.currentTimeMillis()
				+ "\\" + this.typeLibUtil.getName().toLowerCase() + "\\");

		if (javaDir.exists())
			javaDir.delete();

		File javaFile = new File(javaDir, this.typeLibUtil.getName() + ".java");
		if (javaDir.mkdirs()) {
			logInfo("Output directory sucessfully created to: "
					+ javaDir.toString());
			this.out = new PrintStream(javaFile);
		} else {
			throw new FileNotFoundException(
					"Output directory NOT sucessfully created to: "
							+ javaDir.toString());
		}
	}

	private void createJavaFileHeader() {
		this.out.println("package myPackage." + this.typeLibUtil.getName()
				+ ";" + CR);
		this.out.println("import com.sun.jna.platform.win32.*;");
		this.out.println("import com.sun.jna.platform.win32.COM.*;" + CR);
	}

	private void createJavaDocHeader() {
		String guidStr = this.typeLibUtil.getLibAttr().guid.toGuidString();
		int majorVerNum = this.typeLibUtil.getLibAttr().wMajorVerNum.intValue();
		int minorVerNum = this.typeLibUtil.getLibAttr().wMinorVerNum.intValue();

		this.out.println("/**");
		this.out.println("* uuid(" + guidStr + ")");
		this.out.println("* version(" + majorVerNum + "." + minorVerNum + ");");
		this.out.println("* helpstring(" + this.typeLibUtil.getDocString()
				+ ")");
		this.out.println("*/");
	}

	private void createCOMEnum(Object[] typeLibDoc, ITypeInfoUtil typeInfoUtil) {
		 String enumName = (String) typeLibDoc[0];
		 System.out.println(enumName);
		//
		//
		// this.logInfo("Type of kind 'enum' found: " + enumName);
		// this.out.print("	public static class TYPEKIND extends Structure {"
		// + CR
		// +
		// "	public static class ByReference extends TYPEKIND implements	Structure.ByReference {}"
		// + CRCR + "			public int value;" + CRCR + "			public "
		// + enumName + "() {}" + CRCR
		// + "            public static final int " + enumName
		// + "_ENUM = 0;" + CRCR);
		//
		// System.out.println(documentation[0]);

		MEMBERID memberID;
		FUNCDESC pFuncDesc;
		VARDESC pVarDesc;
		BSTR bstrMethod;
		BSTR bstrProperty;

		// Get the TypeAttributes
		TYPEATTR typeAttr = typeInfoUtil.getTypeAttr();

		// Lets get all the methods for this Type Info
		for (int i = 0; i < typeAttr.cFuncs.intValue(); i++) {
			// Get the function description
			FUNCDESC funcDesc = typeInfoUtil.getFuncDesc(i);

			// Get the member ID
			memberID = funcDesc.memid;

			// Get the name of the method
			Object[] typeInfoDoc = typeInfoUtil.getDocumentation(memberID);
		}

		for (int i = 0; i < typeAttr.cVars.intValue(); i++) {
			// Get the property description
			VARDESC varDesc = typeInfoUtil.getVarDesc(i);

			// Get the member ID
			memberID = varDesc.memid;

			// Get the name of the property
			Object[] typeInfoDoc2 = typeInfoUtil.getDocumentation(memberID);
		}

	}

	public static void logInfo(String msg) {
		System.out.println(msg);
	}
}
