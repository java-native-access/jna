
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
