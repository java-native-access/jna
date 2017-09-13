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
package com.sun.jna.platform.win32.COM;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.WString;
import com.sun.jna.platform.win32.Guid.CLSID;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.OaIdl.MEMBERID;
import com.sun.jna.platform.win32.OaIdl.TLIBATTR;
import com.sun.jna.platform.win32.OaIdl.TYPEKIND;
import com.sun.jna.platform.win32.Ole32;
import com.sun.jna.platform.win32.OleAuto;
import com.sun.jna.platform.win32.WTypes;
import com.sun.jna.platform.win32.WTypes.BSTRByReference;
import com.sun.jna.platform.win32.WTypes.LPOLESTR;
import com.sun.jna.platform.win32.WinDef.BOOLByReference;
import com.sun.jna.platform.win32.WinDef.DWORDByReference;
import com.sun.jna.platform.win32.WinDef.LCID;
import com.sun.jna.platform.win32.WinDef.UINT;
import com.sun.jna.platform.win32.WinDef.ULONG;
import com.sun.jna.platform.win32.WinDef.USHORTByReference;
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
        COMUtils.checkRC(hr);

        // load typelib
        PointerByReference pTypeLib = new PointerByReference();
        hr = OleAuto.INSTANCE.LoadRegTypeLib(clsid, wVerMajor, wVerMinor, lcid,
                pTypeLib);
        COMUtils.checkRC(hr);

        // init type lib class
        this.typelib = new TypeLib(pTypeLib.getValue());

        this.initTypeLibInfo();
    }

    public TypeLibUtil(String file) {
        // load typelib
        PointerByReference pTypeLib = new PointerByReference();
        HRESULT hr = OleAuto.INSTANCE.LoadTypeLib(file, pTypeLib);
        COMUtils.checkRC(hr);

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
        COMUtils.checkRC(hr);
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
        COMUtils.checkRC(hr);
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
        COMUtils.checkRC(hr);

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
        COMUtils.checkRC(hr);

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
        DWORDByReference pdwHelpContext = new DWORDByReference();
        BSTRByReference pBstrHelpFile = new BSTRByReference();

        HRESULT hr = typelib.GetDocumentation(index, pBstrName, pBstrDocString,
                pdwHelpContext, pBstrHelpFile);
        COMUtils.checkRC(hr);

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
        BOOLByReference pfName = new BOOLByReference();

        HRESULT hr = this.typelib.IsName(szNameBuf, lHashVal, pfName);
        COMUtils.checkRC(hr);

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
     *            the hash val or 0 if unknown
     * @param maxResult
     *            maximum number of items to search
     * @return the find name
     */
    public FindName FindName(String name, int hashVal, short maxResult) {
        Pointer p = Ole32.INSTANCE.CoTaskMemAlloc((name.length() + 1L) * Native.WCHAR_SIZE);
        WTypes.LPOLESTR olestr = new WTypes.LPOLESTR(p);
        olestr.setValue(name);

        ULONG lHashVal = new ULONG(hashVal);
        USHORTByReference pcFound = new USHORTByReference(maxResult);

        Pointer[] ppTInfo = new Pointer[maxResult];
        MEMBERID[] rgMemId = new MEMBERID[maxResult];
        HRESULT hr = this.typelib.FindName(olestr, lHashVal, ppTInfo, rgMemId,
                pcFound);
        COMUtils.checkRC(hr);

        FindName findName = new FindName(olestr.getValue(), ppTInfo,
                rgMemId, pcFound.getValue().shortValue());
        
        Ole32.INSTANCE.CoTaskMemFree(p);

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
        private Pointer[] pTInfo;

        /** The rg mem id. */
        private MEMBERID[] rgMemId;

        /** The pc found. */
        private short pcFound;

        /**
         * Instantiates a new find name.
         *  @param nameBuf
         *            the name buf
         * @param pTInfo
         *            the t info
         * @param rgMemId
 *            the rg mem id
         * @param pcFound
         */
        FindName(String nameBuf, Pointer[] pTInfo, MEMBERID[] rgMemId,
                        short pcFound) {
            this.nameBuf = nameBuf;
            this.pTInfo = new Pointer[pcFound];
            this.rgMemId = new MEMBERID[pcFound];
            this.pcFound = pcFound;
            System.arraycopy(pTInfo, 0, this.pTInfo, 0, pcFound);
            System.arraycopy(rgMemId, 0, this.rgMemId, 0, pcFound);
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
            ITypeInfo[] values=new ITypeInfo[pcFound];
            for(int i=0;i<pcFound;i++)
            {
                values[i]=new TypeInfo(pTInfo[i]);
            }
            return values;
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
