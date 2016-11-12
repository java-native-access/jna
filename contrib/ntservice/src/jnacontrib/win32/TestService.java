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

package jnacontrib.win32;

/**
 * TestService.
 */
public class TestService extends Win32Service {
  
  /**
   * main.
   * @param args arguments
   */
  public static void main(String[] args) {
    TestService service = new TestService();
    
    if(args.length == 1) {
      
      if(args[0].equalsIgnoreCase("install")) {
        System.out.println(service.install("TestService DisplayName", "TestService Description",
                null, null, null));
        
      } else if(args[0].equalsIgnoreCase("uninstall")) {
        System.out.println(service.uninstall());
        
      } else {
        System.out.println("Arguments:");
        System.out.println("install   = install service");
        System.out.println("uninstall = uninstall service");
        System.out.println("<none>    = run service");
        System.exit(0);
      }
      
    } else {
      service.init();
    }
  }
  
  /** 
   * Creates a new instance of TestService.
   */
  public TestService() {
    super("TestService");
  }

  /**
   * Will be called on start.
   */
  public void onStart() {
    
  }

  /**
   * Will be called on stop.
   */
  public void onStop() {
  }
  
}
