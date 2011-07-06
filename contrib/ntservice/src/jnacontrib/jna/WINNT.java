/*
 * WINNT.java
 *
 * Created on 8. August 2007, 13:41
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jnacontrib.jna;

/**
 *
 * @author TB
 */
public interface WINNT {
  public final static int DELETE       = 0x00010000;
  public final static int READ_CONTROL = 0x00020000;
  public final static int WRITE_DAC    = 0x00040000;
  public final static int WRITE_OWNER  = 0x00080000;
  public final static int SYNCHRONIZE  = 0x00100000;

  public final static int STANDARD_RIGHTS_REQUIRED = 0x000F0000;

  public final static int STANDARD_RIGHTS_READ    = READ_CONTROL;
  public final static int STANDARD_RIGHTS_WRITE   = READ_CONTROL;
  public final static int STANDARD_RIGHTS_EXECUTE = READ_CONTROL;

  public final static int STANDARD_RIGHTS_ALL = 0x001F0000;

  public final static int SPECIFIC_RIGHTS_ALL = 0x0000FFFF;

  public final static int GENERIC_EXECUTE = 0x20000000;
  
  public final static int SERVICE_WIN32_OWN_PROCESS = 0x00000010;
}
