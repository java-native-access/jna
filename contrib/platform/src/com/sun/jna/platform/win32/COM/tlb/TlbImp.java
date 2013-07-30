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
import java.io.FileWriter;
import java.io.IOException;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.OaIdl.TYPEKIND;
import com.sun.jna.platform.win32.COM.TypeLibUtil;
import com.sun.jna.platform.win32.COM.tlb.imp.TlbBase;
import com.sun.jna.platform.win32.COM.tlb.imp.TlbCoClass;
import com.sun.jna.platform.win32.COM.tlb.imp.TlbDispInterface;
import com.sun.jna.platform.win32.COM.tlb.imp.TlbEnum;
import com.sun.jna.platform.win32.COM.tlb.imp.TlbInterface;

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
    private TypeLibUtil typeLibUtil;

    /** The out. */
    private File comRootDir;

    /**
     * The main method.
     * 
     * @param args
     *            the arguments
     */
    public static void main(String[] args) {
        new TlbImp().startCOM2Java();
    }

    public TlbImp() {
        Native.setProtected(true);
    }

    /**
     * Start co m2 java.
     */
    public void startCOM2Java() {
        try {
            this.typeLibUtil = new TypeLibUtil(TYPELIB_ID_SHELL, 1, 0);
            // create output Dir
            this.createDir();

            for (int i = 0; i < typeLibUtil.getTypeInfoCount(); ++i) {
                TYPEKIND typekind = typeLibUtil.getTypeInfoType(i);

                if (typekind.value == TYPEKIND.TKIND_ENUM) {
                    this.createCOMEnum(i, this.getPackageName(), typeLibUtil);
                } else if (typekind.value == TYPEKIND.TKIND_RECORD) {
                    System.out
                            .println("'TKIND_RECORD' objects are currently not supported!");
                } else if (typekind.value == TYPEKIND.TKIND_MODULE) {
                    System.out
                            .println("'TKIND_MODULE' objects are currently not supported!");
                } else if (typekind.value == TYPEKIND.TKIND_INTERFACE) {
                    this.createCOMInterface(i, this.getPackageName(),
                            typeLibUtil);
                } else if (typekind.value == TYPEKIND.TKIND_DISPATCH) {
                    this.createCOMDispInterface(i, this.getPackageName(),
                            typeLibUtil);
                } else if (typekind.value == TYPEKIND.TKIND_COCLASS) {
                    this.createCOMCoClass(i, this.getPackageName(), typeLibUtil);
                } else if (typekind.value == TYPEKIND.TKIND_ALIAS) {
                    System.out
                            .println("'TKIND_ALIAS' objects are currently not supported!");
                } else if (typekind.value == TYPEKIND.TKIND_UNION) {
                    System.out
                            .println("'TKIND_UNION' objects are currently not supported!");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createDir() throws FileNotFoundException {
        String tmp = System.getProperty("java.io.tmpdir");
        this.comRootDir = new File(tmp + "_jnaCOM_"
                + System.currentTimeMillis() + "\\myPackage\\"
                + this.typeLibUtil.getName().toLowerCase() + "\\");

        if (this.comRootDir.exists())
            this.comRootDir.delete();

        if (this.comRootDir.mkdirs()) {
            logInfo("Output directory sucessfully created to: "
                    + this.comRootDir.toString());
        } else {
            throw new FileNotFoundException(
                    "Output directory NOT sucessfully created to: "
                            + this.comRootDir.toString());
        }
    }

    private String getPackageName() {
        return "myPackage." + this.typeLibUtil.getName().toLowerCase();
    }

    private void writeTextFile(String filename, String str) throws IOException {
        File classFile = new File(this.comRootDir, filename);
        FileWriter fileWriter = new FileWriter(classFile);
        fileWriter.write(str);
        fileWriter.close();
    }

    private void writeTlbClass(TlbBase tlbBase) throws IOException {
        StringBuffer classBuffer = tlbBase.getClassBuffer();
        this.writeTextFile(tlbBase.getFilename(), classBuffer.toString());
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
    private void createCOMEnum(int index, String packagename,
            TypeLibUtil typeLibUtil) throws IOException {
        TlbEnum tlbEnum = new TlbEnum(index, packagename, typeLibUtil);
        this.writeTlbClass(tlbEnum);
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
    private void createCOMInterface(int index, String packagename,
            TypeLibUtil typeLibUtil) throws IOException {
        TlbInterface tlbInterface = new TlbInterface(index, packagename,
                typeLibUtil);
        this.writeTlbClass(tlbInterface);
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
    private void createCOMDispInterface(int index, String packagename,
            TypeLibUtil typeLibUtil) throws IOException {
        TlbDispInterface tlbDispatch = new TlbDispInterface(index, packagename,
                typeLibUtil);
        this.writeTlbClass(tlbDispatch);
    }

    private void createCOMCoClass(int index, String packagename,
            TypeLibUtil typeLibUtil) throws IOException {
        TlbCoClass tlbCoClass = new TlbCoClass(index, this.getPackageName(),
                typeLibUtil);
        this.writeTlbClass(tlbCoClass);
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
