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

import com.sun.jna.platform.win32.Guid.REFIID;
import com.sun.jna.platform.win32.OaIdl.EXCEPINFO;
import com.sun.jna.platform.win32.OaIdl.FUNCDESC;
import com.sun.jna.platform.win32.OaIdl.HREFTYPE;
import com.sun.jna.platform.win32.OaIdl.HREFTYPEByReference;
import com.sun.jna.platform.win32.OaIdl.INVOKEKIND;
import com.sun.jna.platform.win32.OaIdl.MEMBERID;
import com.sun.jna.platform.win32.OaIdl.TYPEATTR;
import com.sun.jna.platform.win32.OaIdl.VARDESC;
import com.sun.jna.platform.win32.OleAuto;
import com.sun.jna.platform.win32.OleAuto.DISPPARAMS;
import com.sun.jna.platform.win32.Variant.VARIANT;
import com.sun.jna.platform.win32.WTypes.BSTR;
import com.sun.jna.platform.win32.WTypes.BSTRByReference;
import com.sun.jna.platform.win32.WTypes.LPOLESTR;
import com.sun.jna.platform.win32.WinDef.DWORDByReference;
import com.sun.jna.platform.win32.WinDef.PVOID;
import com.sun.jna.platform.win32.WinDef.UINT;
import com.sun.jna.platform.win32.WinDef.UINTByReference;
import com.sun.jna.platform.win32.WinDef.WORD;
import com.sun.jna.platform.win32.WinDef.WORDByReference;
import com.sun.jna.platform.win32.WinNT.HRESULT;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

// TODO: Auto-generated Javadoc
/**
 * The Class ITypeInfoUtil.
 * 
 * @author wolf.tobias@gmx.net The Class ITypeInfoUtil.
 */
public class TypeInfoUtil {

    /** The Constant OLEAUTO. */
    public final static OleAuto OLEAUTO = OleAuto.INSTANCE;

    /** The type info. */
    private ITypeInfo typeInfo;

    /**
     * Instantiates a new i type info util.
     * 
     * @param typeInfo
     *            the type info
     */
    public TypeInfoUtil(ITypeInfo typeInfo) {
        this.typeInfo = typeInfo;
    }

    /**
     * Gets the type attr.
     * 
     * @return the type attr
     */
    public TYPEATTR getTypeAttr() {
        PointerByReference ppTypeAttr = new PointerByReference();
        HRESULT hr = this.typeInfo.GetTypeAttr(ppTypeAttr);
        COMUtils.checkRC(hr);

        return new TYPEATTR(ppTypeAttr.getValue());
    }

    /**
     * Gets the type comp.
     * 
     * @return the type comp
     */
    public TypeComp getTypeComp() {
        PointerByReference ppTypeAttr = new PointerByReference();
        HRESULT hr = this.typeInfo.GetTypeComp(ppTypeAttr);
        COMUtils.checkRC(hr);

        return new TypeComp(ppTypeAttr.getValue());
    }

    /**
     * Gets the func desc.
     * 
     * @param index
     *            the index
     * @return the func desc
     */
    public FUNCDESC getFuncDesc(int index) {
        PointerByReference ppFuncDesc = new PointerByReference();
        HRESULT hr = this.typeInfo.GetFuncDesc(new UINT(index), ppFuncDesc);
        COMUtils.checkRC(hr);

        return new FUNCDESC(ppFuncDesc.getValue());
    }

    /**
     * Gets the var desc.
     * 
     * @param index
     *            the index
     * @return the var desc
     */
    public VARDESC getVarDesc(int index) {
        PointerByReference ppVarDesc = new PointerByReference();
        HRESULT hr = this.typeInfo.GetVarDesc(new UINT(index), ppVarDesc);
        COMUtils.checkRC(hr);

        return new VARDESC(ppVarDesc.getValue());
    }

    /**
     * Gets the names.
     * 
     * @param memid
     *            the memid
     * @param maxNames
     *            the max names
     * @return the names
     */
    public String[] getNames(MEMBERID memid, int maxNames) {
        BSTR[] rgBstrNames = new BSTR[maxNames];
        UINTByReference pcNames = new UINTByReference();
        HRESULT hr = this.typeInfo.GetNames(memid, rgBstrNames, new UINT(
                maxNames), pcNames);
        COMUtils.checkRC(hr);

        int cNames = pcNames.getValue().intValue();
        String[] result = new String[cNames];

        for (int i = 0; i < result.length; i++) {
            result[i] = rgBstrNames[i].getValue();
            OLEAUTO.SysFreeString(rgBstrNames[i]);
        }

        return result;
    }

    /**
     * Gets the ref type of impl type.
     * 
     * @param index
     *            the index
     * @return the ref type of impl type
     */
    public HREFTYPE getRefTypeOfImplType(int index) {
        HREFTYPEByReference ppTInfo = new HREFTYPEByReference();
        HRESULT hr = this.typeInfo.GetRefTypeOfImplType(new UINT(index),
                ppTInfo);
        COMUtils.checkRC(hr);

        return ppTInfo.getValue();
    }

    /**
     * Gets the impl type flags.
     * 
     * @param index
     *            the index
     * @return the impl type flags
     */
    public int getImplTypeFlags(int index) {
        IntByReference pImplTypeFlags = new IntByReference();
        HRESULT hr = this.typeInfo.GetImplTypeFlags(new UINT(index),
                pImplTypeFlags);
        COMUtils.checkRC(hr);

        return pImplTypeFlags.getValue();
    }

    /**
     * Gets the i ds of names.
     * 
     * @param rgszNames
     *            the rgsz names
     * @param cNames
     *            the c names
     * @return the i ds of names
     */
    public MEMBERID[] getIDsOfNames(LPOLESTR[] rgszNames, int cNames) {
        MEMBERID[] pMemId = new MEMBERID[cNames];
        HRESULT hr = this.typeInfo.GetIDsOfNames(rgszNames, new UINT(cNames),
                pMemId);
        COMUtils.checkRC(hr);

        return pMemId;
    }

    /**
     * Invoke.
     * 
     * @param pvInstance
     *            the pv instance
     * @param memid
     *            the memid
     * @param wFlags
     *            the w flags
     * @param pDispParams
     *            the disp params
     * @return the invoke
     */
    public Invoke Invoke(PVOID pvInstance, MEMBERID memid, WORD wFlags,
            DISPPARAMS.ByReference pDispParams) {

        VARIANT.ByReference pVarResult = new VARIANT.ByReference();
        EXCEPINFO.ByReference pExcepInfo = new EXCEPINFO.ByReference();
        UINTByReference puArgErr = new UINTByReference();

        HRESULT hr = this.typeInfo.Invoke(pvInstance, memid, wFlags,
                pDispParams, pVarResult, pExcepInfo, puArgErr);
        COMUtils.checkRC(hr);

        return new Invoke(pVarResult, pExcepInfo, puArgErr.getValue()
                .intValue());
    }

    /**
     * The Class Invoke.
     * 
     * @author wolf.tobias@gmx.net The Class Invoke.
     */
    public static class Invoke {

        /** The p var result. */
        private VARIANT.ByReference pVarResult;

        /** The p excep info. */
        private EXCEPINFO.ByReference pExcepInfo;

        /** The pu arg err. */
        private int puArgErr;

        /**
         * Instantiates a new invoke.
         * 
         * @param pVarResult
         *            the var result
         * @param pExcepInfo
         *            the excep info
         * @param puArgErr
         *            the pu arg err
         */
        public Invoke(VARIANT.ByReference pVarResult,
                EXCEPINFO.ByReference pExcepInfo, int puArgErr) {
            this.pVarResult = pVarResult;
            this.pExcepInfo = pExcepInfo;
            this.puArgErr = puArgErr;
        }

        /**
         * Gets the p var result.
         * 
         * @return the p var result
         */
        public VARIANT.ByReference getpVarResult() {
            return pVarResult;
        }

        /**
         * Gets the p excep info.
         * 
         * @return the p excep info
         */
        public EXCEPINFO.ByReference getpExcepInfo() {
            return pExcepInfo;
        }

        /**
         * Gets the pu arg err.
         * 
         * @return the pu arg err
         */
        public int getPuArgErr() {
            return puArgErr;
        }
    }

    /**
     * Gets the documentation.
     * 
     * @param memid
     *            the memid
     * @return the documentation
     */
    public TypeInfoDoc getDocumentation(MEMBERID memid) {
        BSTRByReference pBstrName = new BSTRByReference();
        BSTRByReference pBstrDocString = new BSTRByReference();
        DWORDByReference pdwHelpContext = new DWORDByReference();
        BSTRByReference pBstrHelpFile = new BSTRByReference();

        HRESULT hr = this.typeInfo.GetDocumentation(memid, pBstrName,
                pBstrDocString, pdwHelpContext, pBstrHelpFile);
        COMUtils.checkRC(hr);

        TypeInfoDoc TypeInfoDoc = new TypeInfoDoc(pBstrName.getString(),
                pBstrDocString.getString(), pdwHelpContext.getValue()
                        .intValue(), pBstrHelpFile.getString());

        OLEAUTO.SysFreeString(pBstrName.getValue());
        OLEAUTO.SysFreeString(pBstrDocString.getValue());
        OLEAUTO.SysFreeString(pBstrHelpFile.getValue());

        return TypeInfoDoc;
    }

    /**
     * The Class TypeInfoDoc.
     * 
     * @author wolf.tobias@gmx.net The Class TypeInfoDoc.
     */
    public static class TypeInfoDoc {

        /** The name. */
        private String name;

        /** The doc string. */
        private String docString;

        /** The help context. */
        private int helpContext;

        /** The help file. */
        private String helpFile;

        /**
         * Instantiates a new type info doc.
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
        public TypeInfoDoc(String name, String docString, int helpContext,
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
     * Gets the dll entry.
     * 
     * @param memid
     *            the memid
     * @param invKind
     *            the inv kind
     * @return the dll entry
     */
    public DllEntry GetDllEntry(MEMBERID memid, INVOKEKIND invKind) {
        BSTRByReference pBstrDllName = new BSTRByReference();
        BSTRByReference pBstrName = new BSTRByReference();
        WORDByReference pwOrdinal = new WORDByReference();

        HRESULT hr = this.typeInfo.GetDllEntry(memid, invKind, pBstrDllName,
                pBstrName, pwOrdinal);
        COMUtils.checkRC(hr);

        OLEAUTO.SysFreeString(pBstrDllName.getValue());
        OLEAUTO.SysFreeString(pBstrName.getValue());

        return new DllEntry(pBstrDllName.getString(), pBstrName.getString(),
                pwOrdinal.getValue().intValue());
    }

    /**
     * The Class DllEntry.
     * 
     * @author wolf.tobias@gmx.net The Class DllEntry.
     */
    public static class DllEntry {

        /** The dll name. */
        private String dllName;

        /** The name. */
        private String name;

        /** The ordinal. */
        private int ordinal;

        /**
         * Instantiates a new dll entry.
         * 
         * @param dllName
         *            the dll name
         * @param name
         *            the name
         * @param ordinal
         *            the ordinal
         */
        public DllEntry(String dllName, String name, int ordinal) {
            this.dllName = dllName;
            this.name = name;
            this.ordinal = ordinal;
        }

        /**
         * Gets the dll name.
         * 
         * @return the dll name
         */
        public String getDllName() {
            return dllName;
        }

        /**
         * Sets the dll name.
         * 
         * @param dllName
         *            the new dll name
         */
        public void setDllName(String dllName) {
            this.dllName = dllName;
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
         * Sets the name.
         * 
         * @param name
         *            the new name
         */
        public void setName(String name) {
            this.name = name;
        }

        /**
         * Gets the ordinal.
         * 
         * @return the ordinal
         */
        public int getOrdinal() {
            return ordinal;
        }

        /**
         * Sets the ordinal.
         * 
         * @param ordinal
         *            the new ordinal
         */
        public void setOrdinal(int ordinal) {
            this.ordinal = ordinal;
        }
    }

    /**
     * Gets the ref type info.
     * 
     * @param hreftype
     *            the hreftype
     * @return the ref type info
     */
    public ITypeInfo getRefTypeInfo(HREFTYPE hreftype) {
        PointerByReference ppTInfo = new PointerByReference();
        HRESULT hr = this.typeInfo.GetRefTypeInfo(hreftype, ppTInfo);
        COMUtils.checkRC(hr);

        return new TypeInfo(ppTInfo.getValue());
    }

    /**
     * Address of member.
     * 
     * @param memid
     *            the memid
     * @param invKind
     *            the inv kind
     * @return the pointer by reference
     */
    public PointerByReference AddressOfMember(MEMBERID memid, INVOKEKIND invKind) {
        PointerByReference ppv = new PointerByReference();
        HRESULT hr = this.typeInfo.AddressOfMember(memid, invKind, ppv);
        COMUtils.checkRC(hr);

        return ppv;
    }

    /**
     * Creates the instance.
     * 
     * @param pUnkOuter
     *            the unk outer
     * @param riid
     *            the riid
     * @return the pointer by reference
     */
    public PointerByReference CreateInstance(IUnknown pUnkOuter, REFIID riid) {
        PointerByReference ppvObj = new PointerByReference();
        HRESULT hr = this.typeInfo.CreateInstance(pUnkOuter, riid, ppvObj);
        COMUtils.checkRC(hr);

        return ppvObj;
    }

    /**
     * Gets the mops.
     * 
     * @param memid
     *            the memid
     * @return the string
     */
    public String GetMops(MEMBERID memid) {

        BSTRByReference pBstrMops = new BSTRByReference();
        HRESULT hr = this.typeInfo.GetMops(memid, pBstrMops);
        COMUtils.checkRC(hr);

        return pBstrMops.getString();
    }

    /**
     * Gets the containing type lib.
     * 
     * @return the containing type lib
     */
    public ContainingTypeLib GetContainingTypeLib() {

        PointerByReference ppTLib = new PointerByReference();
        UINTByReference pIndex = new UINTByReference();

        HRESULT hr = this.typeInfo.GetContainingTypeLib(ppTLib, pIndex);
        COMUtils.checkRC(hr);

        return new ContainingTypeLib(new TypeLib(ppTLib.getValue()), pIndex
                .getValue().intValue());
    }

    /**
     * The Class ContainingTypeLib.
     * 
     * @author wolf.tobias@gmx.net The Class ContainingTypeLib.
     */
    public static class ContainingTypeLib {

        /** The type lib. */
        private ITypeLib typeLib;

        /** The index. */
        private int index;

        /**
         * Instantiates a new containing type lib.
         * 
         * @param typeLib
         *            the type lib
         * @param index
         *            the index
         */
        public ContainingTypeLib(ITypeLib typeLib, int index) {
            this.typeLib = typeLib;
            this.index = index;
        }

        /**
         * Gets the type lib.
         * 
         * @return the type lib
         */
        public ITypeLib getTypeLib() {
            return typeLib;
        }

        /**
         * Sets the type lib.
         * 
         * @param typeLib
         *            the new type lib
         */
        public void setTypeLib(ITypeLib typeLib) {
            this.typeLib = typeLib;
        }

        /**
         * Gets the index.
         * 
         * @return the index
         */
        public int getIndex() {
            return index;
        }

        /**
         * Sets the index.
         * 
         * @param index
         *            the new index
         */
        public void setIndex(int index) {
            this.index = index;
        }
    }

    /**
     * Release type attr.
     * 
     * @param pTypeAttr
     *            the type attr
     */
    public void ReleaseTypeAttr(TYPEATTR pTypeAttr) {
        this.typeInfo.ReleaseTypeAttr(pTypeAttr);
    }

    /**
     * Release func desc.
     * 
     * @param pFuncDesc
     *            the func desc
     */
    public void ReleaseFuncDesc(FUNCDESC pFuncDesc) {
        this.typeInfo.ReleaseFuncDesc(pFuncDesc);
    }

    /**
     * Release var desc.
     * 
     * @param pVarDesc
     *            the var desc
     */
    public void ReleaseVarDesc(VARDESC pVarDesc) {
        this.typeInfo.ReleaseVarDesc(pVarDesc);
    }
}
