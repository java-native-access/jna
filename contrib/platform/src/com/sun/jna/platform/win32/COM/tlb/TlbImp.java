/* Copyright (c) 2013 Tobias Wolf, All Rights Reserved
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
package com.sun.jna.platform.win32.COM.tlb;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.sun.jna.platform.win32.OaIdl.TYPEKIND;
import com.sun.jna.platform.win32.COM.TypeLibUtil;
import com.sun.jna.platform.win32.COM.tlb.imp.TlbBase;
import com.sun.jna.platform.win32.COM.tlb.imp.TlbCmdlineArgs;
import com.sun.jna.platform.win32.COM.tlb.imp.TlbCoClass;
import com.sun.jna.platform.win32.COM.tlb.imp.TlbConst;
import com.sun.jna.platform.win32.COM.tlb.imp.TlbDispInterface;
import com.sun.jna.platform.win32.COM.tlb.imp.TlbEnum;
import com.sun.jna.platform.win32.COM.tlb.imp.TlbInterface;

// TODO: Auto-generated Javadoc
/**
 * The Class TlbImp.
 * 
 * @author Tobias Wolf, wolf.tobias@gmx.net
 */
public class TlbImp implements TlbConst {

    /** The type lib util. */
    private TypeLibUtil typeLibUtil;

    /** The out. */
    private File comRootDir;

    private File outputDir;

    private TlbCmdlineArgs cmdlineArgs;

    /**
     * The main method.
     * 
     * @param args
     *            the arguments
     */
    public static void main(String[] args) {
        new TlbImp(args);
    }

    public TlbImp(String[] args) {
        this.cmdlineArgs = new TlbCmdlineArgs(args);

        if (this.cmdlineArgs.isTlbId()) {
            String clsid = this.cmdlineArgs.getRequiredParam(CMD_ARG_TYPELIB_ID);
            int majorVersion = this.cmdlineArgs
                    .getIntParam(CMD_ARG_TYPELIB_MAJOR_VERSION);
            int minorVersion = this.cmdlineArgs
                    .getIntParam(CMD_ARG_TYPELIB_MINOR_VERSION);

            // initialize typelib
            // check version numbers with registry entries!!!
            this.typeLibUtil = new TypeLibUtil(clsid, majorVersion,
                    minorVersion);
            this.startCOM2Java();
        } else if (this.cmdlineArgs.isTlbFile()) {
            String file = this.cmdlineArgs.getRequiredParam(CMD_ARG_TYPELIB_FILE);
            // initialize typelib
            // check version numbers with registry entries!!!
            this.typeLibUtil = new TypeLibUtil(file);
            this.startCOM2Java();
        } else
            this.cmdlineArgs.showCmdHelp();
    }

    /**
     * Start startCOM2Java.
     */
    public void startCOM2Java() {
        try {
            // create output Dir
            this.createDir();

            String bindingMode = this.cmdlineArgs.getBindingMode();

            int typeInfoCount = typeLibUtil.getTypeInfoCount();
            for (int i = 0; i < typeInfoCount; ++i) {
                TYPEKIND typekind = typeLibUtil.getTypeInfoType(i);

                if (typekind.value == TYPEKIND.TKIND_ENUM) {
                    this.createCOMEnum(i, this.getPackageName(), typeLibUtil);
                } else if (typekind.value == TYPEKIND.TKIND_RECORD) {
                    TlbImp.logInfo("'TKIND_RECORD' objects are currently not supported!");
                } else if (typekind.value == TYPEKIND.TKIND_MODULE) {
                    TlbImp.logInfo("'TKIND_MODULE' objects are currently not supported!");
                } else if (typekind.value == TYPEKIND.TKIND_INTERFACE) {
                    this.createCOMInterface(i, this.getPackageName(),
                            typeLibUtil);
                } else if (typekind.value == TYPEKIND.TKIND_DISPATCH) {
                    this.createCOMDispInterface(i, this.getPackageName(),
                            typeLibUtil);
                } else if (typekind.value == TYPEKIND.TKIND_COCLASS) {
                    this.createCOMCoClass(i, this.getPackageName(),
                            typeLibUtil, bindingMode);
                } else if (typekind.value == TYPEKIND.TKIND_ALIAS) {
                    TlbImp.logInfo("'TKIND_ALIAS' objects are currently not supported!");
                } else if (typekind.value == TYPEKIND.TKIND_UNION) {
                    TlbImp.logInfo("'TKIND_UNION' objects are currently not supported!");
                }
            }

            logInfo(typeInfoCount + " files sucessfully written to: "
                    + this.comRootDir.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createDir() throws FileNotFoundException {
        String _outputDir = this.cmdlineArgs.getParam(CMD_ARG_OUTPUT_DIR);
        String path = "_jnaCOM_" + System.currentTimeMillis() + "\\myPackage\\"
                + this.typeLibUtil.getName().toLowerCase() + "\\";

        if (_outputDir != null) {
            this.comRootDir = new File(_outputDir + "\\" + path);
        } else {
            String tmp = System.getProperty("java.io.tmpdir");
            this.comRootDir = new File(tmp + "\\" + path);
        }

        if (this.comRootDir.exists())
            this.comRootDir.delete();

        if (this.comRootDir.mkdirs()) {
            logInfo("Output directory sucessfully created.");
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
        String file = this.comRootDir + File.separator + filename;
        BufferedOutputStream bos = new BufferedOutputStream(
                new FileOutputStream(file));
        bos.write(str.getBytes());
        bos.close();
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
            TypeLibUtil typeLibUtil, String bindingMode) throws IOException {
        TlbCoClass tlbCoClass = new TlbCoClass(index, this.getPackageName(),
                typeLibUtil, bindingMode);
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
