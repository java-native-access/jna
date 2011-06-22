package com.sun.jna.contrib.demo;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Area;
import java.awt.geom.RoundRectangle2D;

import javax.swing.AbstractAction;
import javax.swing.JLabel;
import javax.swing.JWindow;
import javax.swing.Popup;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import com.sun.jna.platform.WindowUtils;

/**
 * The BalloonTipManager class handles creation and disposal of balloon style 
 * tips that are typically used to display warning/error information based on
 * results from input validation.  The balloon tip location and direction are
 * computed based on the owner component location on the screen.  The balloon
 * tip is only displayed for a limited amount of time that is configurable.  
 * The balloon tip is also disposed when the owner component loses focus or the
 * mouse is pressed.
 */
public class BalloonTipManager {
  public static final Color DEFAULT_BORDER_COLOR = Color.BLACK;
  public static final Color DEFAULT_BACKGROUND_COLOR = new Color(255, 255, 225);
  public static final Color DEFAULT_TEXT_COLOR = Color.BLACK;
  
  private static final Integer VPOS_ABOVE = 0; // Positioned above component.
  private static final Integer VPOS_BELOW = 1; // Positioned below component.
  private static final Integer HPOS_LEFT = 0; // Arrow is on the left side.
  private static final Integer HPOS_RIGHT = 1; // Arros is on the right side.

  private static Integer vpos = null;
  private static Integer hpos = null;
  
  private static Timer hidePopupTimer = null;
  private static boolean isShowing = false;
  
  
  /*
   * The BalloonTip class defines the look of the BalloonTip object.
   */
  private static final class BalloonTip extends JWindow {
		private static final long serialVersionUID = 1L;
    private static final Integer HMARGIN = 10;
    private static final Integer VMARGIN = 6;
    private static final Integer VSPACER = 4;
    private static final int ARC_D = 16;
    
    private Area mask = null;
    private Dimension maskSize = null;
    private String[] textList = null;
    
    private Color backgroundColor = null;
    private Color borderColor = null;
    private Color textColor = null;
    
    /**
     * Create a BalloonTip object.
     * @param owner the parent window for the components
     * @param content the string for the balloon tip
     * @param position the position for the balloon; either above or below the
     * owner component
     * @param origin the origin point for the balloon tip
     * @param bordercolor the background color for the balloon tip
     * @param backgroundcolor the border color for the balloon tip
     * @param textcolor the text color for the balloon tip
     */
    public BalloonTip (
        Window owner, String content, Point origin, Color bordercolor,
        Color backgroundcolor, Color textcolor)
    {
      super(owner);
      textList = content.split("\n");
      borderColor = bordercolor;
      backgroundColor = backgroundcolor;
      textColor = textcolor;
      setFocusableWindowState(false);
      setName("###overrideRedirect###");
      setSize(getPreferredSize());
    }
    
    /*
     * Sets the mask for the Balloon Tip.
     */
    private void setWindowMask () {
      mask = new Area(getMask());
      maskSize = getSize();
      WindowUtils.setWindowMask(BalloonTip.this, mask);
    }
    
    /*
     * (non-Javadoc)
     * @see java.awt.Container#paint(java.awt.Graphics)
     */
    public void paint (Graphics g) {
      super.paint(g);
      Dimension d = getMinimumWindowSize();
      int width = d.width + 2 * HMARGIN;
      int height = d.height + 2 * VMARGIN;
      int x = 0;
      int y = 0;
      if (vpos == VPOS_BELOW) {
        y += 15;
      }
      // Draw the filled rounded rectangle and clean up the missed pixels.
      g.setColor(backgroundColor);
      g.fillRoundRect(x, y, width, height, ARC_D, ARC_D);
      g.drawLine(x + 6, y + height - 1, x + 6, y + height - 1);
      g.drawLine(x + width - 1, y + 6, x + width - 1, y + 6);
      g.drawLine(x + width - 1, y + height - 6, x + width - 1, y + height - 7);
      g.drawLine(x + width - 2, y + height - 4, x + width - 4, y + height - 2);
      g.drawLine(x + width - 6, y + height - 1, x + width - 7, y + height - 1);
      g.clearRect(x + 2, y + 2, 1, 1);
      // Draw the border of the rounded rectangle.
      g.setColor(borderColor);
      g.drawRoundRect(x, y, width, height, ARC_D, ARC_D);
      // Draw the external triangle for the balloon.
      if (vpos == VPOS_BELOW) {
        if (hpos == HPOS_LEFT) {
          g.setColor(backgroundColor);
          int[] xPts = {16, 16, 31};
          int[] yPts = {0, 16, 16};
          g.fillPolygon(xPts, yPts, 3);
          g.setColor(borderColor);
          g.drawLine(16, 0, 16, 15);
          g.drawLine(16, 0, 31, 15);
          g.drawLine(16, 1, 30, 15);
        }
        else {
          g.setColor(backgroundColor);
          int[] xPts = {width - 16, width - 16, width - 31};
          int[] yPts = {0, 16, 16};
          g.fillPolygon(xPts, yPts, 3);
          g.setColor(borderColor);
          g.drawLine(width - 16, 0, width - 16, 15);
          g.drawLine(width - 16, 0, width - 31, 15);
          g.drawLine(width - 16, 1, width - 30, 15);
        }
      }
      else {
        if (hpos == HPOS_LEFT) {
          g.setColor(backgroundColor);
          int[] xPts = {16, 16, 31};
          int[] yPts = {height, height + 16, height};
          g.fillPolygon(xPts, yPts, 3);
          g.setColor(borderColor);
          g.drawLine(16, height, 16, height + 15);
          g.drawLine(16, height + 15, 31, height);
          g.drawLine(16, height + 14, 30, height);
        }
        else {
          g.setColor(backgroundColor);
          int[] xPts = {width - 16, width - 16, width - 31};
          int[] yPts = {height, height + 16, height};
          g.fillPolygon(xPts, yPts, 3);
          g.setColor(borderColor);
          g.drawLine(width - 16, height, width - 16, height + 15);
          g.drawLine(width - 16, height + 15, width - 31, height);
          g.drawLine(width - 16, height + 14, width - 30, height);
        }
      }
      // Draw the inner component for the balloon.
      g.setColor(textColor);
      g.setFont(new Font("Tahoma", Font.PLAIN, 11));
      int stringY = y + VMARGIN / 2;
      for (int i = 0; i < textList.length; i++) {
        stringY += new JLabel(textList[i]).getPreferredSize().height;
        if (i > 0) {
          stringY += VSPACER;
        }
        g.drawString(textList[i], HMARGIN, stringY);
      }
    }
    
    /*
     * Returns the mask for the balloon tip window.
     */
    private Shape getMask () {
      Dimension d = getMinimumWindowSize();
      int width = d.width + 2 * HMARGIN;
      int height = d.height + 2 * VMARGIN;
      int x = 0;
      int y = 0;
      if (vpos == VPOS_BELOW) {
        y += 15;
      }
      // Start by creating the area of the main rounded rectangle.
      Area area = new Area(
        new RoundRectangle2D.Float(x, y, width + 1, height + 1, ARC_D, ARC_D));
      // Add in the remaining pixels that are not included by default due to
      // the differences between Graphics drawing and Shape creation.
      area.add(new Area(new Rectangle(0, y + 6, 1, 2)));
      area.add(new Area(new Rectangle(0, y + height - 7, 1, 2)));
      area.add(new Area(new Rectangle(1, y + 4, 1, 1)));
      area.add(new Area(new Rectangle(1, y + height - 5, 1, 2)));
      area.add(new Area(new Rectangle(4, y + 1, 1, 1)));
      area.add(new Area(new Rectangle(6, y, 2, 1)));
      area.add(new Area(new Rectangle(width - 7, y, 2, 1)));
      // Subtract the extra pixels that are not included by default due to the
      // differences between Graphics drawing and Shape creation.
      area.subtract(new Area(new Rectangle(2, y + height - 2, 1, 1)));
      area.subtract(new Area(new Rectangle(3, y + height - 1, 1, 1)));
      area.subtract(new Area(new Rectangle(5, y + height, 1, 1)));
      area.subtract(new Area(new Rectangle(width - 5, y + height, 2, 1)));
      area.subtract(new Area(new Rectangle(width - 3, y + height - 1, 2, 1)));
      area.subtract(new Area(new Rectangle(width - 2, y + 2, 1, 1)));
      area.subtract(new Area(new Rectangle(width - 2, y + height - 2, 2, 1)));
      area.subtract(new Area(new Rectangle(width - 1, y + 3, 1, 1)));
      area.subtract(new Area(new Rectangle(width - 1, y + height - 3, 1, 1)));
      area.subtract(new Area(new Rectangle(width, y + 5, 1, 1)));
      area.subtract(new Area(new Rectangle(width, y + height - 5, 1, 2)));
      // Add in the triangle piece for the balloon.
      if (vpos == VPOS_BELOW) {
        if (hpos == HPOS_LEFT) {
          int[] xPts = {16, 16, 32};
          int[] yPts = {-1, 16, 16};
          area.add(new Area(new Polygon(xPts, yPts, 3)));
        }
        else {
          int[] xPts = {width - 15, width - 15, width - 32};
          int[] yPts = {-1, 16, 16};
          area.add(new Area(new Polygon(xPts, yPts, 3)));
        }
      }
      else {
        if (hpos == HPOS_LEFT) {
          int[] xPts = {16, 16, 31};
          int[] yPts = {height, height + 16, height};
          area.add(new Area(new Polygon(xPts, yPts, 3)));
        }
        else {
          int[] xPts = {width - 15, width - 15, width - 32};
          int[] yPts = {height, height + 16, height};
          area.add(new Area(new Polygon(xPts, yPts, 3)));
        }
      }
      return area;
    }
    
    /*
     * Returns the dimension of the window based on the preferred component
     * sizes.
     */
    private Dimension getMinimumWindowSize () {
      int maxWidth = 0;
      int textHeight = 0;
      JLabel tempLabel = null;
      for (int i = 0; i < textList.length; i++) {
        tempLabel = new JLabel(textList[i]);
        maxWidth = Math.max(maxWidth, tempLabel.getPreferredSize().width);
        textHeight += tempLabel.getPreferredSize().height;
      }
      int w = Math.max(maxWidth, 32);
      int h = Math.max(textHeight + (textList.length - 1) * VSPACER, 8);
      return new Dimension(w, h);
    }
    
    /*
     * (non-Javadoc)
     * @see java.awt.Window#setBounds(int, int, int, int)
     */
    public void setBounds (int x, int y, int w, int h) {
      super.setBounds(x, y, w, h);
      Dimension size = new Dimension(w, h);
      if (mask != null && !size.equals(maskSize)) {
        mask.subtract(mask);
        mask.add(new Area(getMask()));
        maskSize = size;
      }
    }
    
    /*
     * (non-Javadoc)
     * @see java.awt.Container#getPreferredSize()
     */
    public Dimension getPreferredSize () {
      Dimension d = getMinimumWindowSize();
      int w = d.width + 2 * HMARGIN + 1;
      int h = d.height + 2 * VMARGIN + 16;
      return new Dimension(w, h);
    }
  }
  

  /**
   * Returns the popup window of the balloon tip.
   * @param owner the owner component for the balloon tip
   * @param content the text string to display in the balloon tip
   * @param x the x coordinate for the origin for the balloon tip in relation
   * to the owner component
   * @param y the y coordinate for the origin for the balloon tip in relation
   * to the owner component
   * @param position the position for the balloon; either above or below the
     * owner component
   * @param duration the duration in milliseconds to display balloon tip
   * @return the popup window of the balloon tip
   */
  public static Popup getBalloonTip (final Component owner,
      final String content, int x, int y, final int duration)
  {
    return getBalloonTip(owner, content, x, y, duration, DEFAULT_BORDER_COLOR,
      DEFAULT_BACKGROUND_COLOR, DEFAULT_TEXT_COLOR);
  }
  
  /**
   * Returns whether the popup is showing or not.
   * @return true if the popup is showing, else false
   */
  public static boolean isShowing () {
    return isShowing;
  }
  
  /**
   * Restarts the popup timer.
   */
  public static void restartTimer () {
    hidePopupTimer.restart();
  }
  
  /**
   * Returns the popup window of the balloon tip.
   * @param owner the owner component for the balloon tip
   * @param content the text string to display in the balloon tip
   * @param x the x coordinate for the origin for the balloon tip in relation
   * to the owner component
   * @param y the y coordinate for the origin for the balloon tip in relation
   * to the owner component
   * @param position the position for the balloon; either above or below the
     * owner component
   * @param duration the duration in milliseconds to display balloon tip
   * @param bordercolor the background color for the balloon tip
   * @param backgroundcolor the border color for the balloon tip
   * @param textcolor the text color for the balloon tip
   * @return the popup window of the balloon tip
   */
  public static Popup getBalloonTip (final Component owner,
      final String content, int x, int y, final Integer duration,
      final Color bordercolor, final Color backgroundcolor,
      final Color textcolor)
  {
    final Point origin =
      owner == null ? new Point(0, 0) : owner.getLocationOnScreen();
    final Window parent =
      owner != null ? SwingUtilities.getWindowAncestor(owner) : null;
    final String text =
      content != null ? content : "";
    final Integer timerDuration =
      duration != null ? duration : 10000;
    origin.translate(x, y);
    vpos = VPOS_BELOW;
    hpos = HPOS_LEFT;
    return new Popup () {
      private BalloonTip bt = null;
      final ComponentEar componentEar = new ComponentEar();
      final MouseEar mouseEar = new MouseEar();
      final FocusEar focusEar = new FocusEar();
      
      /*
       * (non-Javadoc)
       * @see javax.swing.Popup#show()
       */
      public void show () {
        hidePopupTimer = new Timer(timerDuration, new TimerAction());
        bt = new BalloonTip(parent, text, origin, bordercolor,
          backgroundcolor, textcolor);
        bt.pack();
        Point pt = new Point(origin);
        pt.translate(10, owner.getHeight());
        bt.setLocation(getAdjustedOrigin(pt));
        bt.setWindowMask();
        bt.setVisible(true);
        owner.addFocusListener(focusEar);
        owner.addMouseListener(mouseEar);
        parent.addMouseListener(mouseEar);
        parent.addComponentListener(componentEar);
        hidePopupTimer.start();
        isShowing = true;
      }
      
      /*
       * (non-Javadoc)
       * @see javax.swing.Popup#hide()
       */
      public void hide () {
        if (bt != null) {
          isShowing = false;
          hidePopupTimer.stop();
          parent.removeComponentListener(componentEar);
          parent.removeMouseListener(mouseEar);
          owner.removeMouseListener(mouseEar);
          owner.removeFocusListener(focusEar);
          bt.setVisible(false);
          bt.dispose();
        }
      }
      
      /*
       * Adjust the location of the balloon popup so that is drawn completely
       * on the screen and specify the orientation.
       */
      private Point getAdjustedOrigin (Point pt) {
        Point ret = new Point(pt.x, pt.y);
        GraphicsConfiguration gc = owner.getGraphicsConfiguration();
        Rectangle sBounds = gc.getBounds();
        Insets sInsets = Toolkit.getDefaultToolkit().getScreenInsets(gc);
        sBounds.x += sInsets.left;
        sBounds.y += sInsets.top;
        sBounds.width -= (sInsets.left + sInsets.right);
        sBounds.height -= (sInsets.top + sInsets.bottom);
        
        if (ret.x < sBounds.x) {
          ret.x = sBounds.x;
        }
        else if (ret.x - sBounds.x + bt.getWidth() > sBounds.width) {
          ret.x = owner.getLocationOnScreen().x - bt.getWidth() + 43;
        }
        if (ret.x >= pt.x) {
          hpos = HPOS_LEFT;
        }
        else {
          hpos = HPOS_RIGHT;
        }

        if (ret.y < sBounds.y) {
          ret.y = sBounds.y;
        }
        else if (ret.y - sBounds.y + bt.getHeight() > sBounds.height) {
          ret.y = owner.getLocationOnScreen().y - bt.getHeight();
        }
        if (ret.y >= pt.y) {
          vpos = VPOS_BELOW;
        }
        else {
          vpos = VPOS_ABOVE;
        }
        return ret;
      }
      
      /*
       * This class handles actions from the balloon tip timer.
       */
      @SuppressWarnings("serial")
      final class TimerAction extends AbstractAction {
        /*
         * (non-Javadoc)
         * @see java.awt.event.ActionListener#actionPerformed(
         * java.awt.event.ActionEvent)
         */
        public void actionPerformed (ActionEvent e) {
          hide();
        }
      }
      
      /*
       * This class handles events spawned from moving the component.
       */
      final class ComponentEar extends ComponentAdapter {
        /*
         * (non-Javadoc)
         * @see java.awt.event.ComponentAdapter#componentMoved(
         * java.awt.event.ComponentEvent)
         */
        public void componentMoved (ComponentEvent e) {
          hide();
        }
      }
      
      /*
       * This class handles events spawned when a mouse button is pressed.
       */
      final class MouseEar extends MouseAdapter {
        /*
         * (non-Javadoc)
         * @see java.awt.event.MouseAdapter#mousePressed(
         * java.awt.event.MouseEvent)
         */
        public void mousePressed (MouseEvent e) {
          hide();
        }
      }

      /*
       * This class handles events spawned when the component loses focus.
       */
      final class FocusEar extends FocusAdapter {
        /*
         * (non-Javadoc)
         * @see java.awt.event.FocusAdapter#focusLost(
         * java.awt.event.FocusEvent)
         */
        public void focusLost (FocusEvent e) {
          hide();
        }
      }
    };
  }
}
