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
package com.sun.jna.platform.win32.COM.tlb.imp;

import java.util.Hashtable;

public class TlbCmdlineArgs extends Hashtable<String, String> implements TlbConst {
    private static final long serialVersionUID = 1L;

    public TlbCmdlineArgs(String[] args) {
        this.readCmdArgs(args);
    }

    public int getIntParam(String key) {
        String param = this.getRequiredParam(key);
        return Integer.parseInt(param);
    }

    public String getParam(String key) {
        return this.get(key);
    }

    public String getRequiredParam(String key) {
        String param = this.getParam(key);
        if (param == null)
            throw new TlbParameterNotFoundException(
                    "Commandline parameter not found: " + key);

        return param;
    }

    private void readCmdArgs(String[] args) {
        if (args.length < 2)
            this.showCmdHelp();

        for (int i = 0; i < args.length;) {
            String cmdName = args[i];
            String cmdValue = args[i+1];
            if (cmdName.startsWith("-") && !cmdValue.startsWith("-")) {
                this.put(cmdName.substring(1), cmdValue);
                i+=2;
            }else {
                this.showCmdHelp();
                break;
            }
        }
    }

    public boolean isTlbFile() {
        return this.containsKey(CMD_ARG_TYPELIB_FILE);
    }

    public boolean isTlbId() {
        return this.containsKey(CMD_ARG_TYPELIB_ID);
    }

    public String getBindingMode() {
        if(this.containsKey(CMD_ARG_BINDING_MODE))
            return this.getParam(CMD_ARG_BINDING_MODE);
        else
            return BINDING_MODE_VTABLE;
    }

    public void showCmdHelp() {
        String helpStr = "usage: TlbImp [-tlb.id -tlb.major.version -tlb.minor.version] [-tlb.file] [-bind.mode vTable, dispId] [-output.dir]"
                + CRCR
                + "options:"
                + CR
                + "-tlb.id               The guid of the type library."
                + CR
                + "-tlb.major.version    The major version of the type library."
                + CR
                + "-tlb.minor.version    The minor version of the type library."
                + CR
                + "-tlb.file             The file name containing the type library."
                + CR
                + "-bind.mode            The binding mode used to create the Java code."
                + CR
                + "-output.dir           The optional output directory, default is the user temp directory."
                + CRCR
                + "samples:"
                + CR
                + "Microsoft Shell Controls And Automation:"
                + CR
                + "-tlb.file shell32.dll"
                + CR
                + "-tlb.id {50A7E9B0-70EF-11D1-B75A-00A0C90564FE} -tlb.major.version 1 -tlb.minor.version 0"
                + CRCR
                + "Microsoft Word 12.0 Object Library:"
                + CR
                + "-tlb.id {00020905-0000-0000-C000-000000000046} -tlb.major.version 8 -tlb.minor.version 4"
                + CRCR;

        System.out.println(helpStr);
        System.exit(0);
    }
}
