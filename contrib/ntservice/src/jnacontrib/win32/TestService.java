/*
 * TestService.java
 *
 * Created on 12. September 2007, 12:49
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
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
