/*
 * Kernel32b.java
 *
 * Created on 6. August 2007, 14:43
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jnacontrib.jna;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.win32.StdCallLibrary;

/**
 *
 * @author TB
 */
public interface Kernel32b extends StdCallLibrary {
  Kernel32b INSTANCE = (Kernel32b) Native.loadLibrary("Kernel32", Kernel32b.class, Options.UNICODE_OPTIONS);
  
/*  
HLOCAL WINAPI LocalFree(
  HLOCAL hMem
);*/
  public Pointer LocalFree(Pointer hMem);
/*

DWORD WINAPI GetLastError(void);*/
  public int GetLastError();
}
