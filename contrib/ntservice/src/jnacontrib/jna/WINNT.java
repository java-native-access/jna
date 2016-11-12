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
