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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    /** The index. */
    protected int index;

    /** The template buffer. */
    protected StringBuffer templateBuffer;

    /** The class buffer. */
    protected StringBuffer classBuffer;

    /** The content. */
    protected String content = "";

    protected String filename = "";

    /** The iunknown methods. */
    public static String[] IUNKNOWN_METHODS = { "QueryInterface", "AddRef",
            "Release" };

    /** The idispatch methods. */
    public static String[] IDISPATCH_METHODS = { "GetTypeInfoCount",
            "GetTypeInfo", "GetIDsOfNames", "Invoke" };

    /**
     * Instantiates a new tlb base.
     * 
     * @param index
     *            the index
     * @param typeLibUtil
     *            the type lib util
     */
    public TlbBase(int index, TypeLibUtil typeLibUtil) {
        this.index = index;
        this.typeLibUtil = typeLibUtil;

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

        if(result.length() > 0)
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
}
