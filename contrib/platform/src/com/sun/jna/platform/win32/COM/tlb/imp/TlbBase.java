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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sun.jna.platform.win32.COM.TypeInfoUtil;
import com.sun.jna.platform.win32.COM.TypeLibUtil;

// TODO: Auto-generated Javadoc
/**
 * The Class TlbBase.
 * 
 * @author Tobias Wolf, wolf.tobias@gmx.net
 */
public abstract class TlbBase {

    /** The Constant CR. */
    public final static String CR = "\n";

    /** The Constant CRCR. */
    public final static String CRCR = "\n\n";

    /** The Constant TAB. */
    public final static String TAB = "\t";

    /** The Constant TABTAB. */
    public final static String TABTAB = "\t\t";

    /** The type lib util. */
    protected TypeLibUtil typeLibUtil;

    protected TypeInfoUtil typeInfoUtil;
    
    /** The index. */
    protected int index;

    /** The template buffer. */
    protected StringBuffer templateBuffer;

    /** The class buffer. */
    protected StringBuffer classBuffer;

    /** The content. */
    protected String content = "";

    protected String filename = "DefaultFilename";

    protected String name = "DefaultName";

    /** The iunknown methods. */
    public static String[] IUNKNOWN_METHODS = { "QueryInterface", "AddRef",
            "Release" };

    /** The idispatch methods. */
    public static String[] IDISPATCH_METHODS = { "GetTypeInfoCount",
            "GetTypeInfo", "GetIDsOfNames", "Invoke" };

    protected String bindingMode = TlbConst.BINDING_MODE_DISPID;
    
    public TlbBase(int index, TypeLibUtil typeLibUtil, TypeInfoUtil typeInfoUtil) {
        this(index, typeLibUtil, typeInfoUtil, TlbConst.BINDING_MODE_DISPID);
    }

    public TlbBase(int index, TypeLibUtil typeLibUtil, TypeInfoUtil typeInfoUtil, String bindingMode) {
        this.index = index;
        this.typeLibUtil = typeLibUtil;
        this.typeInfoUtil = typeInfoUtil;
        this.bindingMode = bindingMode;
        
        String filename = this.getClassTemplate();
        try {
            this.readTemplateFile(filename);
            this.classBuffer = templateBuffer;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Log error.
     * 
     * @param msg
     *            the msg
     */
    public void logError(String msg) {
        this.log("ERROR", msg);
    }

    /**
     * Log info.
     * 
     * @param msg
     *            the msg
     */
    public void logInfo(String msg) {
        this.log("INFO", msg);
    }

    /**
     * Gets the class buffer.
     * 
     * @return the class buffer
     */
    public StringBuffer getClassBuffer() {
        return classBuffer;
    }

    /**
     * Creates the content.
     * 
     * @param content
     *            the content
     */
    public void createContent(String content) {
        this.replaceVariable("content", content);
    }

    public void setFilename(String filename) {
        if (!filename.endsWith("java"))
            filename += ".java";
        this.filename = filename;
    }

    public String getFilename() {
        return this.filename;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Log.
     * 
     * @param level
     *            the level
     * @param msg
     *            the msg
     */
    protected void log(String level, String msg) {
        String _msg = level + " " + this.getTime() + " : " + msg;
        System.out.println(_msg);
    }

    /**
     * Gets the time.
     * 
     * @return the time
     */
    private String getTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        return sdf.format(new Date());
    }

    /**
     * Gets the class template.
     * 
     * @return the class template
     */
    abstract protected String getClassTemplate();

    /**
     * Read template file.
     * 
     * @param filename
     *            the filename
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    protected void readTemplateFile(String filename) throws IOException {
        this.templateBuffer = new StringBuffer();
        BufferedReader reader = null;
        try {
            InputStream is = this.getClass().getClassLoader()
                    .getResourceAsStream(filename);
            reader = new BufferedReader(new InputStreamReader(is));
            String line = null;
            while ((line = reader.readLine()) != null)
                this.templateBuffer.append(line + "\n");
        } finally {
            if (reader != null)
                reader.close();
        }
    }

    /**
     * Replace variable.
     * 
     * @param name
     *            the name
     * @param value
     *            the value
     */
    protected void replaceVariable(String name, String value) {
        if (value == null)
            value = "";

        Pattern pattern = Pattern.compile("\\$\\{" + name + "\\}");
        Matcher matcher = pattern.matcher(this.classBuffer);
        String replacement = value;
        String result = "";

        while (matcher.find()) {
            result = matcher.replaceAll(replacement);
        }

        if (result.length() > 0)
            this.classBuffer = new StringBuffer(result);
    }

    protected void createPackageName(String packagename) {
        this.replaceVariable("packagename", packagename);
    }

    /**
     * Creates the class name.
     * 
     * @param name
     *            the name
     */
    protected void createClassName(String name) {
        this.replaceVariable("classname", name);
    }

    /**
     * Checks if is reserved method.
     * 
     * @param method
     *            the method
     * @return true, if is reserved method
     */
    protected boolean isReservedMethod(String method) {
        for (int i = 0; i < IUNKNOWN_METHODS.length; i++) {
            if (IUNKNOWN_METHODS[i].equalsIgnoreCase(method))
                return true;
        }

        for (int i = 0; i < IDISPATCH_METHODS.length; i++) {
            if (IDISPATCH_METHODS[i].equalsIgnoreCase(method))
                return true;
        }

        return false;
    }
    
    protected boolean isVTableMode() {
        if(this.bindingMode.equalsIgnoreCase(TlbConst.BINDING_MODE_VTABLE))
            return true;
        else
            return false;
    }
    
    protected boolean isDispIdMode() {
        if(this.bindingMode.equalsIgnoreCase(TlbConst.BINDING_MODE_DISPID))
            return true;
        else
            return false;
    }
}
