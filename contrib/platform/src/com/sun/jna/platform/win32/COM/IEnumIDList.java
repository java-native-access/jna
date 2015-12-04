package com.sun.jna.platform.win32.COM;

/*
 * @author L W Ahonen, lwahonen@iki.fi
 */

import com.sun.jna.Function;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Guid;
import com.sun.jna.platform.win32.Guid.IID;
import com.sun.jna.platform.win32.Guid.REFIID;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.platform.win32.WinNT.HRESULT;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

public interface IEnumIDList {

    /**
     * The Constant IID_IDispatch.
     */
    public final static IID IID_IEnumIDList = new IID(
            "{000214F2-0000-0000-C000-000000000046}");

    HRESULT QueryInterface(
            Guid.GUID.ByReference riid,
            PointerByReference ppvObject);

    int AddRef();

    int Release();

    /*
    Retrieves the specified number of item identifiers in the enumeration sequence and advances the current position by the number of items retrieved.
     * @param celt
     *            The number of elements in the array referenced by the rgelt parameter.
     * @param rgelt
     *            The address of a pointer to an array of ITEMIDLIST pointers that receive the item identifiers.
     *            The implementation must allocate these item identifiers using CoTaskMemAlloc.
     *            The calling application is responsible for freeing the item identifiers using CoTaskMemFree.
     *            The ITEMIDLIST structures returned in the array are relative to the IShellFolder being enumerated.
     * @param pceltFetched
     *            A pointer to a value that receives a count of the item identifiers actually returned in rgelt.
     *            The count can be smaller than the value specified in the celt parameter. This parameter can be NULL on entry only if celt = 1,
     *            because in that case the method can only retrieve one (S_OK) or zero (S_FALSE) items.
     *
     * @return HRESULT
     *            Returns S_OK if the method successfully retrieved the requested celt elements.
     *            This method only returns S_OK if the full count of requested items are successfully retrieved.
     *            S_FALSE indicates that more items were requested than remained in the enumeration.
     *            The value pointed to by the pceltFetched parameter specifies the actual number of items retrieved.
     *            Note that the value will be 0 if there are no more items to retrieve.
     *            Returns a COM-defined error value otherwise.
     *
     *            If this method returns a Component Object Model (COM) error code (as determined by the COMUtils.FAILED macro),
     *            then no entries in the rgelt array are valid on exit. If this method returns a success code (such as S_OK or S_FALSE),
     *            then the ULONG pointed to by the pceltFetched parameter determines how many entries in the rgelt array are valid on exit.
     *
     *            The distinction is important in the case where celt > 1. For example, if you pass celt=10 and there are only 3 elements left,
     *            *pceltFetched will be 3 and the method will return S_FALSE meaning that you reached the end of the file.
     *            The three fetched elements will be stored into rgelt and are valid.
     */
    HRESULT Next(
            int celt,
            PointerByReference rgelt,
            IntByReference pceltFetched);

    /**
     * Skips the specified number of elements in the enumeration sequence.
     * @param celt
     *            The number of item identifiers to skip.
     * @return HRESULT
     *            Returns S_OK if successful, or a COM-defined error value otherwise.
     */
    HRESULT Skip(
            int celt);

    /**
     * Returns to the beginning of the enumeration sequence.
     * @return HRESULT
     *            Returns S_OK if successful, or a COM-defined error value otherwise.
     */

    HRESULT Reset();

    /**
     * Creates a new item enumeration object with the same contents and state as the current one.
     * @param ppenum
     *                The address of a pointer to the new enumeration object. The calling application must eventually free the new object by calling its Release member function.
     * @return HRESULT
     *            Returns S_OK if successful, or a COM-defined error value otherwise.
     */
    HRESULT Clone(
            PointerByReference ppenum);


    /*
    Use this like:

    PointerByReference pbr=new PointerByReference();
    HRESULT result=SomeCOMObject.QueryInterface(IID_IEnumIDList, pbr);
    if(COMUtils.SUCCEEDED(result)) IENumIDList eil=IEnumIDList.Converter.PointerToIEnumIDList(pbr);

     */
    public static class Converter {
        public static IEnumIDList PointerToIEnumIDList(final PointerByReference ptr) {
            final Pointer interfacePointer = ptr.getValue();
            final Pointer vTablePointer = interfacePointer.getPointer(0);
            final Pointer[] vTable = new Pointer[7];
            vTablePointer.read(0, vTable, 0, 7);
            return new IEnumIDList() {

                @Override
                public WinNT.HRESULT QueryInterface(Guid.GUID.ByReference byValue, PointerByReference pointerByReference) {
                    Function f = Function.getFunction(vTable[0], Function.ALT_CONVENTION);
                    return new WinNT.HRESULT(f.invokeInt(new Object[]{interfacePointer, byValue, pointerByReference}));
                }

                @Override
                public int AddRef() {
                    Function f = Function.getFunction(vTable[1], Function.ALT_CONVENTION);
                    return f.invokeInt(new Object[]{interfacePointer});
                }

                public int Release() {
                    Function f = Function.getFunction(vTable[2], Function.ALT_CONVENTION);
                    return f.invokeInt(new Object[]{interfacePointer});
                }

                @Override
                public HRESULT Next(int celt, PointerByReference rgelt, IntByReference pceltFetched) {
                    Function f = Function.getFunction(vTable[3], Function.ALT_CONVENTION);
                    return new HRESULT(f.invokeInt(new Object[]{interfacePointer, celt, rgelt, pceltFetched}));
                }

                @Override
                public HRESULT Skip(int celt) {
                    Function f = Function.getFunction(vTable[4], Function.ALT_CONVENTION);
                    return new HRESULT(f.invokeInt(new Object[]{interfacePointer, celt}));
                }

                @Override
                public HRESULT Reset() {
                    Function f = Function.getFunction(vTable[5], Function.ALT_CONVENTION);
                    return new HRESULT(f.invokeInt(new Object[]{interfacePointer}));
                }

                @Override
                public HRESULT Clone(PointerByReference ppenum) {
                    Function f = Function.getFunction(vTable[6], Function.ALT_CONVENTION);
                    return new HRESULT(f.invokeInt(new Object[]{interfacePointer, ppenum}));
                }
            };
        }
    }
}
