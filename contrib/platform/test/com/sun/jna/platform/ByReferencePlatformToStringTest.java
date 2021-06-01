/* Copyright (c) 2020 Daniel Widdis, All Rights Reserved
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
package com.sun.jna.platform;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.sun.jna.Platform;
import com.sun.jna.Pointer;
import com.sun.jna.platform.unix.LibCAPI.size_t;
import com.sun.jna.platform.unix.X11.AtomByReference;
import com.sun.jna.platform.unix.X11.WindowByReference;
import com.sun.jna.platform.win32.BaseTSD.ULONG_PTR;
import com.sun.jna.platform.win32.BaseTSD.ULONG_PTRByReference;
import com.sun.jna.platform.win32.HighLevelMonitorConfigurationAPI.MC_COLOR_TEMPERATURE;
import com.sun.jna.platform.win32.HighLevelMonitorConfigurationAPI.MC_DISPLAY_TECHNOLOGY_TYPE;
import com.sun.jna.platform.win32.LowLevelMonitorConfigurationAPI.MC_VCP_CODE_TYPE;
import com.sun.jna.platform.win32.OaIdl.DISPID;
import com.sun.jna.platform.win32.OaIdl.DISPIDByReference;
import com.sun.jna.platform.win32.OaIdl.MEMBERID;
import com.sun.jna.platform.win32.OaIdl.MEMBERIDByReference;
import com.sun.jna.platform.win32.OaIdl.VARIANT_BOOL;
import com.sun.jna.platform.win32.OaIdl.VARIANT_BOOLByReference;
import com.sun.jna.platform.win32.OaIdl._VARIANT_BOOLByReference;
import com.sun.jna.platform.win32.OleAuto;
import com.sun.jna.platform.win32.WTypes.BSTR;
import com.sun.jna.platform.win32.WTypes.BSTRByReference;
import com.sun.jna.platform.win32.WTypes.VARTYPE;
import com.sun.jna.platform.win32.WTypes.VARTYPEByReference;
import com.sun.jna.platform.win32.WinDef.BOOL;
import com.sun.jna.platform.win32.WinDef.BOOLByReference;
import com.sun.jna.platform.win32.WinDef.CHAR;
import com.sun.jna.platform.win32.WinDef.CHARByReference;
import com.sun.jna.platform.win32.WinDef.DWORD;
import com.sun.jna.platform.win32.WinDef.DWORDByReference;
import com.sun.jna.platform.win32.WinDef.LONG;
import com.sun.jna.platform.win32.WinDef.LONGByReference;
import com.sun.jna.platform.win32.WinDef.LONGLONG;
import com.sun.jna.platform.win32.WinDef.LONGLONGByReference;
import com.sun.jna.platform.win32.WinDef.SCODE;
import com.sun.jna.platform.win32.WinDef.SCODEByReference;
import com.sun.jna.platform.win32.WinDef.UINT;
import com.sun.jna.platform.win32.WinDef.UINTByReference;
import com.sun.jna.platform.win32.WinDef.ULONG;
import com.sun.jna.platform.win32.WinDef.ULONGByReference;
import com.sun.jna.platform.win32.WinDef.ULONGLONG;
import com.sun.jna.platform.win32.WinDef.ULONGLONGByReference;
import com.sun.jna.platform.win32.WinDef.USHORT;
import com.sun.jna.platform.win32.WinDef.USHORTByReference;
import com.sun.jna.platform.win32.WinDef.WORD;
import com.sun.jna.platform.win32.WinDef.WORDByReference;
import com.sun.jna.platform.win32.WinNT.ACL;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.platform.win32.WinNT.HANDLEByReference;
import com.sun.jna.platform.win32.WinNT.PACLByReference;
import com.sun.jna.platform.win32.WinNT.PSID;
import com.sun.jna.platform.win32.WinNT.PSIDByReference;
import com.sun.jna.platform.win32.WinReg.HKEY;
import com.sun.jna.platform.win32.WinReg.HKEYByReference;

public class ByReferencePlatformToStringTest {
    @Test
    public void testPlatformToStrings() {
        BOOLByReference boolbr = new BOOLByReference(new BOOL(true));
        parseAndTest(boolbr.toString(), "BOOL", "true");

        if (Platform.isWindows()) {
            BSTR b = OleAuto.INSTANCE.SysAllocString("bstr");
            BSTRByReference bstrbr = new BSTRByReference(b);
            parseAndTest(bstrbr.toString(), "BSTR", "bstr");
            OleAuto.INSTANCE.SysFreeString(b);
        }

        CHARByReference cbr = new CHARByReference(new CHAR(42));
        parseAndTest(cbr.toString(), "CHAR", "42");

        DISPIDByReference dispidbr = new DISPIDByReference(new DISPID(42));
        parseAndTest(dispidbr.toString(), "DISPID", "42");

        DWORDByReference dwbr = new DWORDByReference(new DWORD(42));
        parseAndTest(dwbr.toString(), "DWORD", "42");

        HANDLEByReference handlebr = new HANDLEByReference(new HANDLE(new Pointer(42)));
        parseAndTest(handlebr.toString(), "HANDLE", "native");

        HKEYByReference hkeybr = new HKEYByReference(new HKEY(42));
        parseAndTest(hkeybr.toString(), "HKEY", "native");

        LONGByReference longbr = new LONGByReference(new LONG(42));
        parseAndTest(longbr.toString(), "LONG", "42");

        LONGLONGByReference longlongbr = new LONGLONGByReference(new LONGLONG(42));
        parseAndTest(longlongbr.toString(), "LONGLONG", "42");

        MC_COLOR_TEMPERATURE.ByReference mccbr = new MC_COLOR_TEMPERATURE.ByReference(
                MC_COLOR_TEMPERATURE.MC_COLOR_TEMPERATURE_UNKNOWN);
        parseAndTest(mccbr.toString(), "MC_COLOR_TEMPERATURE", "MC_COLOR_TEMPERATURE_UNKNOWN");

        MC_DISPLAY_TECHNOLOGY_TYPE.ByReference mcdbr = new MC_DISPLAY_TECHNOLOGY_TYPE.ByReference(
                MC_DISPLAY_TECHNOLOGY_TYPE.MC_SHADOW_MASK_CATHODE_RAY_TUBE);
        parseAndTest(mcdbr.toString(), "MC_DISPLAY_TECHNOLOGY_TYPE", "MC_SHADOW_MASK_CATHODE_RAY_TUBE");

        MC_VCP_CODE_TYPE.ByReference mcvbr = new MC_VCP_CODE_TYPE.ByReference(MC_VCP_CODE_TYPE.MC_MOMENTARY);
        parseAndTest(mcvbr.toString(), "MC_VCP_CODE_TYPE", "MC_MOMENTARY");

        MEMBERIDByReference memberidbr = new MEMBERIDByReference(new MEMBERID(42));
        parseAndTest(memberidbr.toString(), "MEMBERID", "42");

        PACLByReference paclbr = new PACLByReference(new ACL());
        parseAndTest(paclbr.toString(), "ACL", "WinNT$ACL(native");

        PSIDByReference psidbr = new PSIDByReference(new PSID());
        parseAndTest(psidbr.toString(), "PSID", "WinNT$PSID(native");

        SCODEByReference scodebr = new SCODEByReference(new SCODE(42));
        parseAndTest(scodebr.toString(), "SCODE", "42");

        size_t.ByReference sizetbr = new size_t.ByReference(42);
        parseAndTest(sizetbr.toString(), "size_t", "42");

        UINTByReference uibr = new UINTByReference(new UINT(42));
        parseAndTest(uibr.toString(), "UINT", "42");

        ULONG_PTRByReference ulpbr = new ULONG_PTRByReference(new ULONG_PTR(42));
        parseAndTest(ulpbr.toString(), "ULONG_PTR", "42");

        ULONGByReference ulbr = new ULONGByReference(new ULONG(42));
        parseAndTest(ulbr.toString(), "ULONG", "42");

        ULONGLONGByReference ullbr = new ULONGLONGByReference(new ULONGLONG(42));
        parseAndTest(ullbr.toString(), "ULONGLONG", "42");

        USHORTByReference usbr = new USHORTByReference(new USHORT(42));
        parseAndTest(usbr.toString(), "USHORT", "42");

        VARIANT_BOOLByReference vboolbr = new VARIANT_BOOLByReference(new VARIANT_BOOL(1));
        parseAndTest(vboolbr.toString(), "VARIANT_BOOL", "1");

        _VARIANT_BOOLByReference vboolbr2 = new _VARIANT_BOOLByReference(new VARIANT_BOOL(1));
        parseAndTest(vboolbr2.toString(), "VARIANT_BOOL", "1");

        VARTYPEByReference varbr = new VARTYPEByReference(new VARTYPE(42));
        parseAndTest(varbr.toString(), "VARTYPE", "42");

        WORDByReference wbr = new WORDByReference(new WORD(42));
        parseAndTest(wbr.toString(), "WORD", "42");

        // No way to set value on these without native code. Both methods read a random
        // NativeLong and return null if 0 or a random hex string otherwise
        AtomByReference abr = new AtomByReference();
        String atomStr = abr.toString();
        if (abr.getValue() == null) {
            assertTrue(abr.toString().startsWith("null@0x"));
        } else {
            assertTrue(atomStr.startsWith("Atom@0x"));
            assertTrue(atomStr.contains("=0x"));
        }

        WindowByReference windowbr = new WindowByReference();
        String windowStr = windowbr.toString();
        if (windowbr.getValue() == null) {
            assertTrue(windowStr.startsWith("null@0x"));
        } else {
            assertTrue(windowStr.startsWith("Window@0x"));
            assertTrue(windowStr.contains("=0x"));
        }
    }

    /**
     * Parses a string "foo@0x123=bar" testing equality of fixed parts of the string
     *
     * @param s
     *            The string to test
     * @param beforeAt
     *            The string which should match the portion before the first
     *            {@code @}
     * @param afterEquals
     *            The string which should match the portion after the {@code =}
     *            sign, before any additional {@code @}
     */
    private void parseAndTest(String s, String beforeAt, String afterEquals) {
        String[] atSplit = s.split("@");
        assertEquals("Incorrect type prefix", beforeAt, atSplit[0]);
        String[] equalsSplit = atSplit[1].split("=");
        assertEquals("Incorrect value string", afterEquals, equalsSplit[1]);
    }
}
