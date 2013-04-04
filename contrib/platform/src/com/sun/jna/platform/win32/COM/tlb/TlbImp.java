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
package com.sun.jna.platform.win32.COM.tlb;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

import com.sun.jna.platform.win32.COM.ITypeInfoUtil;
import com.sun.jna.platform.win32.COM.ITypeLibUtil;
import com.sun.jna.platform.win32.COM.tlb.imp.TlbClass;
import com.sun.jna.platform.win32.COM.tlb.imp.TlbDispatch;
import com.sun.jna.platform.win32.COM.tlb.imp.TlbEnum;
import com.sun.jna.platform.win32.COM.tlb.imp.TlbInterface;
import com.sun.jna.platform.win32.OaIdl.FUNCDESC;
import com.sun.jna.platform.win32.OaIdl.MEMBERID;
import com.sun.jna.platform.win32.OaIdl.TYPEATTR;
import com.sun.jna.platform.win32.OaIdl.TYPEDESC;
import com.sun.jna.platform.win32.OaIdl.TYPEKIND;
import com.sun.jna.platform.win32.OaIdl.VARDESC;
import com.sun.jna.platform.win32.WTypes.BSTR;

// TODO: Auto-generated Javadoc
/**
 * The Class TlbImp.
 * 
 * @author Tobias Wolf, wolf.tobias@gmx.net
 */
public class TlbImp {

	/** The Constant CR. */
	public final static String CR = "\n";

	/** The Constant CRCR. */
	public final static String CRCR = "\n\n";

	/** The Constant TYPELIB_ID_SHELL. */
	public final static String TYPELIB_ID_SHELL = "{50A7E9B0-70EF-11D1-B75A-00A0C90564FE}";

	/** The Constant TYPELIB_ID_WORD. */
	public final static String TYPELIB_ID_WORD = "{00020905-0000-0000-C000-000000000046}";

	/** The type lib util. */
	private ITypeLibUtil typeLibUtil;

	/** The out. */
	private PrintStream out;

	/** The content buffer. */
	private StringBuffer contentBuffer = new StringBuffer();

	/**
	 * The main method.
	 * 
	 * @param args
	 *            the arguments
	 */
	public static void main(String[] args) {
		new TlbImp().startCOM2Java();
	}

	/**
	 * Start co m2 java.
	 */
	public void startCOM2Java() {
		try {
			this.typeLibUtil = new ITypeLibUtil(TYPELIB_ID_SHELL, 1, 0);

			this.initPrintStream();

			for (int i = 0; i < typeLibUtil.getTypeInfoCount(); ++i) {
				TYPEKIND typekind = typeLibUtil.getTypeInfoType(i);

				if (typekind.value == TYPEKIND.TKIND_ENUM) {
					StringBuffer buffer = this.createCOMEnum(i, typeLibUtil);
					contentBuffer.append(buffer + CR);
				} else if (typekind.value == TYPEKIND.TKIND_RECORD) {
					System.out.println("TKIND_RECORD");
				} else if (typekind.value == TYPEKIND.TKIND_MODULE) {
					System.out.println("TKIND_MODULE");
				} else if (typekind.value == TYPEKIND.TKIND_INTERFACE) {
					StringBuffer buffer = this.createCOMInterface(i,
							typeLibUtil);
					contentBuffer.append(buffer + CR);
				} else if (typekind.value == TYPEKIND.TKIND_DISPATCH) {
					StringBuffer buffer = this
							.createCOMDispatch(i, typeLibUtil);
					contentBuffer.append(buffer + CR);
				} else if (typekind.value == TYPEKIND.TKIND_COCLASS) {
					System.out.println("TKIND_COCLASS");
				} else if (typekind.value == TYPEKIND.TKIND_ALIAS) {
					System.out.println("TKIND_ALIAS");
				} else if (typekind.value == TYPEKIND.TKIND_UNION) {

				}
			}

			String packageName = "myPackage."
					+ this.typeLibUtil.getName().toLowerCase();
			TlbClass tlbClass = new TlbClass(-1, typeLibUtil);
			tlbClass.createPackage(packageName);
			tlbClass.createContent(contentBuffer.toString());
			this.out.print(tlbClass.getClassBuffer());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Inits the print stream.
	 * 
	 * @throws FileNotFoundException
	 *             the file not found exception
	 */
	private void initPrintStream() throws FileNotFoundException {
		String tmp = System.getProperty("java.io.tmpdir");
		File javaDir = new File(tmp + "_jnaCOM_" + System.currentTimeMillis()
				+ "\\myPackage\\" + this.typeLibUtil.getName().toLowerCase()
				+ "\\");

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

	/**
	 * Creates the com enum.
	 * 
	 * @param index
	 *            the index
	 * @param typeLibUtil
	 *            the type lib util
	 * @return the string buffer
	 */
	private StringBuffer createCOMEnum(int index, ITypeLibUtil typeLibUtil) {
		TlbEnum tlbEnum = new TlbEnum(index, typeLibUtil);
		return tlbEnum.getClassBuffer();
	}

	/**
	 * Creates the com interface.
	 * 
	 * @param index
	 *            the index
	 * @param typeLibUtil
	 *            the type lib util
	 * @return the string buffer
	 */
	private StringBuffer createCOMInterface(int index, ITypeLibUtil typeLibUtil) {
		TlbInterface tlbInterface = new TlbInterface(index, typeLibUtil);
		return tlbInterface.getClassBuffer();
	}

	/**
	 * Creates the com dispatch.
	 * 
	 * @param index
	 *            the index
	 * @param typeLibUtil
	 *            the type lib util
	 * @return the string buffer
	 */
	private StringBuffer createCOMDispatch(int index, ITypeLibUtil typeLibUtil) {
		TlbDispatch tlbDispatch = new TlbDispatch(index, typeLibUtil);
		return tlbDispatch.getClassBuffer();
	}

	/**
	 * Log info.
	 * 
	 * @param msg
	 *            the msg
	 */
	public static void logInfo(String msg) {
		System.out.println(msg);
	}
}
