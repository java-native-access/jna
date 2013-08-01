package com.sun.jna.platform.win32.COM.tlb.imp;

import java.util.Hashtable;

public class TlbCmdlineArgs extends Hashtable<String, String> implements
        TlbConst {

    public TlbCmdlineArgs(String[] args) {
        this.scanCmdArgs(args);
    }

    public int getIntParam(String key) {
        String param = this.getParam(key);
        return new Integer(param).intValue();
    }
    
    public String getParam(String key) {
        String param = this.get(key);
        if(param == null)
            throw new TlbParameterNotFoundException("Commandline parameter not found: " + key);
        
        return param;
    }
    
    private void scanCmdArgs(String[] args) {
        for (int i = 0; i < args.length; i++) {
            if (args[i].startsWith("-")) {
                this.put(args[i].substring(1), args[i + 1]);
            }
        }
    }
}
