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
package com.sun.jna.platform.win32.COM;

import com.sun.jna.WString;
import com.sun.jna.platform.win32.Guid.CLSID;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.OaIdl.MEMBERID;
import com.sun.jna.platform.win32.OaIdl.TLIBATTR;
import com.sun.jna.platform.win32.OaIdl.TYPEKIND;
import com.sun.jna.platform.win32.Ole32;
import com.sun.jna.platform.win32.OleAuto;
import com.sun.jna.platform.win32.WTypes.BSTRByReference;
import com.sun.jna.platform.win32.WTypes.LPOLESTR;
import com.sun.jna.platform.win32.WinDef.BOOLbyReference;
import com.sun.jna.platform.win32.WinDef.DWORDbyReference;
import com.sun.jna.platform.win32.WinDef.LCID;
import com.sun.jna.platform.win32.WinDef.UINT;
import com.sun.jna.platform.win32.WinDef.ULONG;
import com.sun.jna.platform.win32.WinDef.USHORTbyReference;
import com.sun.jna.platform.win32.WinNT.HRESULT;
import com.sun.jna.ptr.PointerByReference;

// TODO: Auto-generated Javadoc
/**
 * Wrapper class for the class ITypeLibUtil.
 * 
 * @author wolf.tobias@gmx.net The Class ITypeLibUtil.
 */
public class TypeLibUtil {

    /** The Constant OLEAUTO. */
    public final static OleAuto OLEAUTO = OleAuto.INSTANCE;

    /** The typelib. */
    private ITypeLib typelib;
    // get user default lcid
    /** The lcid. */
    private LCID lcid = Kernel32.INSTANCE.GetUserDefaultLCID();

    /** The name. */
    private String name;

    /** The doc string. */
    private String docString;

    /** The help context. */
    private int helpContext;

    /** The help file. */
    private String helpFile;

    /**
     * Instantiates a new i type lib util.
     * 
     * @param clsidStr
     *            the clsid str
     * @param wVerMajor
     *            the w ver major
     * @param wVerMinor
     *            the w ver minor
     */
    public TypeLibUtil(String clsidStr, int wVerMajor, int wVerMinor) {
        CLSID.ByReference clsid = new CLSID.ByReference();
        // get CLSID from string
        HRESULT hr = Ole32.INSTANCE.CLSIDFromString(new WString(clsidStr),
                clsid);
        COMUtils.checkTypeLibRC(hr);

        // load typelib
        PointerByReference pTypeLib = new PointerByReference();
        hr = OleAuto.INSTANCE.LoadRegTypeLib(clsid, wVerMajor, wVerMinor, lcid,
                pTypeLib);
        COMUtils.checkTypeLibRC(hr);

        // init type lib class
        this.typelib = new TypeLib(pTypeLib.getValue());

        this.initTypeLibInfo();
    }

    /**
     * Inits the type lib info.
     */
    private void initTypeLibInfo() {
        TypeLibDoc documentation = this.getDocumentation(-1);
        this.name = documentation.getName();
        this.docString = documentation.getDocString();
        this.helpContext = documentation.getHelpContext();
        this.helpFile = documentation.getHelpFile();
    }

    /**
     * Gets the type info count.
     * 
     * @return the type info count
     */
    public int getTypeInfoCount() {
        return this.typelib.GetTypeInfoCount().intValue();
    }

    /**
     * Gets the type info type.
     * 
     * @param index
     *            the index
     * @return the type info type
     */
    public TYPEKIND getTypeInfoType(int index) {
        TYPEKIND.ByReference typekind = new TYPEKIND.ByReference();
        HRESULT hr = this.typelib.GetTypeInfoType(new UINT(index), typekind);
        COMUtils.checkTypeLibRC(hr);
        return typekind;
    }

    /**
     * Gets the type info.
     * 
     * @param index
     *            the index
     * @return the type info
     */
    public ITypeInfo getTypeInfo(int index) {
        PointerByReference ppTInfo = new PointerByReference();
        HRESULT hr = this.typelib.GetTypeInfo(new UINT(index), ppTInfo);
        COMUtils.checkTypeLibRC(hr);
        return new TypeInfo(ppTInfo.getValue());
    }

    /**
     * Gets the type info util.
     * 
     * @param index
     *            the index
     * @return the type info util
     */
    public TypeInfoUtil getTypeInfoUtil(int index) {
        return new TypeInfoUtil(this.getTypeInfo(index));
    }

    /**
     * Gets the lib attr.
     * 
     * @return the lib attr
     */
    public TLIBATTR getLibAttr() {
        PointerByReference ppTLibAttr = new PointerByReference();
        HRESULT hr = typelib.GetLibAttr(ppTLibAttr);
        COMUtils.checkTypeLibRC(hr);

        return new TLIBATTR(ppTLibAttr.getValue());
    }

    /**
     * Gets the type comp.
     * 
     * @return the i type comp. by reference
     */
    public TypeComp GetTypeComp() {
        PointerByReference ppTComp = new PointerByReference();
        HRESULT hr = this.typelib.GetTypeComp(ppTComp);
        COMUtils.checkTypeLibRC(hr);

        return new TypeComp(ppTComp.getValue());
    }

    /**
     * Gets the documentation.
     * 
     * @param index
     *            the index
     * @return the documentation
     */
    public TypeLibDoc getDocumentation(int index) {
        BSTRByReference pBstrName = new BSTRByReference();
        BSTRByReference pBstrDocString = new BSTRByReference();
        DWORDbyReference pdwHelpContext = new DWORDbyReference();
        BSTRByReference pBstrHelpFile = new BSTRByReference();

        HRESULT hr = typelib.GetDocumentation(index, pBstrName, pBstrDocString,
                pdwHelpContext, pBstrHelpFile);
        COMUtils.checkTypeLibRC(hr);

        TypeLibDoc typeLibDoc = new TypeLibDoc(pBstrName.getString(),
                pBstrDocString.getString(), pdwHelpContext.getValue()
                        .intValue(), pBstrHelpFile.getString());

        OLEAUTO.SysFreeString(pBstrName.getValue());
        OLEAUTO.SysFreeString(pBstrDocString.getValue());
        OLEAUTO.SysFreeString(pBstrHelpFile.getValue());

        return typeLibDoc;
    }

    /**
     * The Class TypeLibDoc.
     * 
     * @author wolf.tobias@gmx.net The Class TypeLibDoc.
     */
    public static class TypeLibDoc {

        /** The name. */
        private String name;

        /** The doc string. */
        private String docString;

        /** The help context. */
        private int helpContext;

        /** The help file. */
        private String helpFile;

        /**
         * Instantiates a new type lib doc.
         * 
         * @param name
         *            the name
         * @param docString
         *            the doc string
         * @param helpContext
         *            the help context
         * @param helpFile
         *            the help file
         */
        public TypeLibDoc(String name, String docString, int helpContext,
                String helpFile) {
            this.name = name;
            this.docString = docString;
            this.helpContext = helpContext;
            this.helpFile = helpFile;
        }

        /**
         * Gets the name.
         * 
         * @return the name
         */
        public String getName() {
            return name;
        }

        /**
         * Gets the doc string.
         * 
         * @return the doc string
         */
        public String getDocString() {
            return docString;
        }

        /**
         * Gets the help context.
         * 
         * @return the help context
         */
        public int getHelpContext() {
            return helpContext;
        }

        /**
         * Gets the help file.
         * 
         * @return the help file
         */
        public String getHelpFile() {
            return helpFile;
        }
    }

    /**
     * Checks if is name.
     * 
     * @param nameBuf
     *            the name buf
     * @param hashVal
     *            the hash val
     * @return the checks if is name
     */
    public IsName IsName(String nameBuf, int hashVal) {

        LPOLESTR szNameBuf = new LPOLESTR(nameBuf);
        ULONG lHashVal = new ULONG(hashVal);
        BOOLbyReference pfName = new BOOLbyReference();

        HRESULT hr = this.typelib.IsName(szNameBuf, lHashVal, pfName);
        COMUtils.checkTypeLibRC(hr);

        return new IsName(szNameBuf.getValue(), pfName.getValue()
                .booleanValue());
    }

    /**
     * The Class IsName.
     * 
     * @author wolf.tobias@gmx.net The Class IsName.
     */
    public static class IsName {

        /** The name buf. */
        private String nameBuf;

        /** The name. */
        private boolean name;

        /**
         * Instantiates a new checks if is name.
         * 
         * @param nameBuf
         *            the name buf
         * @param name
         *            the name
         */
        public IsName(String nameBuf, boolean name) {
            this.nameBuf = nameBuf;
            this.name = name;
        }

        /**
         * Gets the name buf.
         * 
         * @return the name buf
         */
        public String getNameBuf() {
            return nameBuf;
        }

        /**
         * Checks if is name.
         * 
         * @return true, if is name
         */
        public boolean isName() {
            return name;
        }
    }

    /**
     * Find name.
     * 
     * @param name
     *            the name
     * @param hashVal
     *            the hash val
     * @param found
     *            the found
     * @return the find name
     */
    public FindName FindName(String name, int hashVal, short found) {
        /* [annotation][out][in] */
        BSTRByReference szNameBuf = new BSTRByReference(
                OleAuto.INSTANCE.SysAllocString(name));
        /* [in] */ULONG lHashVal = new ULONG(hashVal);
        /* [out][in] */USHORTbyReference pcFound = new USHORTbyReference(found);

        HRESULT hr = this.typelib.FindName(szNameBuf, lHashVal, null, null,
                pcFound);
        COMUtils.checkTypeLibRC(hr);

        found = pcFound.getValue().shortValue();
        /* [length_is][size_is][out] */ITypeInfo[] ppTInfo = new ITypeInfo[found];
        /* [length_is][size_is][out] */MEMBERID[] rgMemId = new MEMBERID[found];
        hr = this.typelib.FindName(szNameBuf, lHashVal, ppTInfo, rgMemId,
                pcFound);
        COMUtils.checkTypeLibRC(hr);

        FindName findName = new FindName(szNameBuf.getString(), ppTInfo,
                rgMemId, found);
        OLEAUTO.SysFreeString(szNameBuf.getValue());

        return findName;
    }

    /**
     * The Class FindName.
     * 
     * @author wolf.tobias@gmx.net The Class FindName.
     */
    public static class FindName {

        /** The name buf. */
        private String nameBuf;

        /** The p t info. */
        private ITypeInfo[] pTInfo;

        /** The rg mem id. */
        private MEMBERID[] rgMemId;

        /** The pc found. */
        private short pcFound;

        /**
         * Instantiates a new find name.
         * 
         * @param nameBuf
         *            the name buf
         * @param pTInfo
         *            the t info
         * @param rgMemId
         *            the rg mem id
         * @param pcFound
         *            the pc found
         */
        public FindName(String nameBuf, ITypeInfo[] pTInfo, MEMBERID[] rgMemId,
                short pcFound) {
            this.nameBuf = nameBuf;
            this.pTInfo = pTInfo;
            this.rgMemId = rgMemId;
            this.pcFound = pcFound;
        }

        /**
         * Gets the name buf.
         * 
         * @return the name buf
         */
        public String getNameBuf() {
            return nameBuf;
        }

        /**
         * Gets the t info.
         * 
         * @return the t info
         */
        public ITypeInfo[] getTInfo() {
            return pTInfo;
        }

        /**
         * Gets the mem id.
         * 
         * @return the mem id
         */
        public MEMBERID[] getMemId() {
            return rgMemId;
        }

        /**
         * Gets the found.
         * 
         * @return the found
         */
        public short getFound() {
            return pcFound;
        }
    }

    /**
     * Release t lib attr.
     * 
     * @param pTLibAttr
     *            the t lib attr
     */
    public void ReleaseTLibAttr(/* [in] */TLIBATTR pTLibAttr) {
        this.typelib.ReleaseTLibAttr(pTLibAttr);
    }

    /**
     * Gets the lcid.
     * 
     * @return the lcid
     */
    public LCID getLcid() {
        return lcid;
    }

    /**
     * Gets the typelib.
     * 
     * @return the typelib
     */
    public ITypeLib getTypelib() {
        return typelib;
    }

    /**
     * Gets the name.
     * 
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the doc string.
     * 
     * @return the doc string
     */
    public String getDocString() {
        return docString;
    }

    /**
     * Gets the help context.
     * 
     * @return the help context
     */
    public long getHelpContext() {
        return helpContext;
    }

    /**
     * Gets the help file.
     * 
     * @return the help file
     */
    public String getHelpFile() {
        return helpFile;
    }

}
