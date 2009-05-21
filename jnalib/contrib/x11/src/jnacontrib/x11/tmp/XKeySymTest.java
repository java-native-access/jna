package jnacontrib.x11.tmp;

import jnacontrib.x11.api.X;
import com.sun.jna.examples.unix.X11;

/**
 * X KeySym test.
 */
public class XKeySymTest {
  public static void main(String[] args) {
    new XKeySymTest();
  }

  private X.Display display = new X.Display();
  private X11.Xevie xevie = X11.Xevie.INSTANCE;

  public XKeySymTest() {
    xevie.XevieStart(display.getX11Display());
  }
}
