/*
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
package com.sun.jna.platform.win32;

import com.sun.jna.platform.win32.WinDef.DWORD;
import com.sun.jna.platform.win32.WinDef.WORD;
import org.junit.Test;
import static org.junit.Assert.*;

public class WinDefTest {

    @Test
    public void testWordExtractionFromDword() {
        DWORD dword = new DWORD(0x12345678);

        assertEquals(new WORD(0x5678), dword.getLow());
        assertEquals(new WORD(0x1234), dword.getHigh());

        DWORD dword2 = new DWORD(0xFFFFFFFF);

        assertEquals(new WORD(0xFFFF), dword2.getLow());
        assertEquals(new WORD(0xFFFF), dword2.getHigh());

        DWORD dword3 = new DWORD(0x00000001);

        assertEquals(new WORD(0x0001), dword3.getLow());
        assertEquals(new WORD(0x0000), dword3.getHigh());

        DWORD dword4 = new DWORD(0x00010000);

        assertEquals(new WORD(0x0000), dword4.getLow());
        assertEquals(new WORD(0x0001), dword4.getHigh());

        DWORD dword5 = new DWORD(0x0000FFFF);

        assertEquals(new WORD(0xFFFF), dword5.getLow());
        assertEquals(new WORD(0x0000), dword5.getHigh());

        DWORD dword6 = new DWORD(0xFFFF0000);

        assertEquals(new WORD(0x0000), dword6.getLow());
        assertEquals(new WORD(0xFFFF), dword6.getHigh());
    }

}
