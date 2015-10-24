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

    HRESULT Next(
            int celt,
            PointerByReference rgelt,
            IntByReference pceltFetched);

    HRESULT Skip(
            int celt);

    HRESULT Reset();

    HRESULT Clone(
            PointerByReference ppenum);


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
