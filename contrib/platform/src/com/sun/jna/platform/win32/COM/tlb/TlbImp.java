package com.sun.jna.platform.win32.COM.tlb;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

import com.sun.jna.platform.win32.COM.ITypeInfoUtil;
import com.sun.jna.platform.win32.COM.ITypeLibUtil;
import com.sun.jna.platform.win32.COM.tlb.imp.TlbEnumImp;
import com.sun.jna.platform.win32.OaIdl.FUNCDESC;
import com.sun.jna.platform.win32.OaIdl.MEMBERID;
import com.sun.jna.platform.win32.OaIdl.TYPEATTR;
import com.sun.jna.platform.win32.OaIdl.TYPEDESC;
import com.sun.jna.platform.win32.OaIdl.TYPEKIND;
import com.sun.jna.platform.win32.OaIdl.VARDESC;
import com.sun.jna.platform.win32.WTypes.BSTR;

public class TlbImp {

	public final static String CR = "\n";

	public final static String CRCR = "\n\n";

	private ITypeLibUtil typeLibUtil;

	private PrintStream out;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new TlbImp().startCOM2Java();
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

				if (typekind.value == TYPEKIND.TKIND_ENUM) {
					this.createCOMEnum(i, out, typeLibUtil);
				} else if (typekind.value == TYPEKIND.TKIND_RECORD) {

				} else if (typekind.value == TYPEKIND.TKIND_MODULE) {

				} else if (typekind.value == TYPEKIND.TKIND_INTERFACE) {
					this.createCOMInterface(i);
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

	private void createCOMEnum(int index, PrintStream out, ITypeLibUtil typeLibUtil) {
		new TlbEnumImp(index, out, typeLibUtil);
	}

	private void createCOMInterface(int index) {
		
	}

	public static void logInfo(String msg) {
		System.out.println(msg);
	}
}
