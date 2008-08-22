/* Copyright (c) 2007 Timothy Wall, All Rights Reserved
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.  
 */
package com.sun.jna.examples;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Calendar;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.event.MouseInputAdapter;

/** Example which uses the {@link WindowUtils} class.  Demonstrates the 
 * definition of a cross-platform library with several platform-specific
 * implementations based on JNA native library definitions.
 */
public class ShapedWindowDemo {
    
    public static final int ICON_SIZE = 64;

    private static class ClockFace extends JComponent {
        private Stroke border;
        private Stroke secondHand;
        private Stroke minuteHand;
        private Stroke hourHand;
        private Stroke ticks;
        public ClockFace(Dimension size) {
            setPreferredSize(size);
            setSize(size);
            setOpaque(false);
            Timer timer = new Timer(1000, new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    repaint();
                    Window w = SwingUtilities.getWindowAncestor(ClockFace.this);
                    while (!(w instanceof Frame)) {
                        w = w.getOwner();
                    }
                    if (w instanceof Frame) {
                        ((Frame)w).setIconImage(getIconImage());
                    }
                }
            });
            timer.setRepeats(true);
            timer.start();
        }
        private String getRomanNumeral(int number) {
            switch(number) {
            case 1: return "I";
            case 2: return "II";
            case 3: return "III";
            case 4: return "IV";
            case 5: return "V";
            case 6: return "VI";
            case 7: return "VII";
            case 8: return "VIII";
            case 9: return "IX";
            case 10: return "X";
            case 11: return "XI";
            case 12:
            default: return "XII";
            }
        }
        protected void paintComponent(Graphics graphics) {
            paintFace(graphics, Math.min(getWidth(), getHeight()));
        }
        
        protected void paintFace(Graphics graphics, int size) {
            Point center = new Point(size/2, size/2);
            int radius = center.x;
            int margin = radius / 20;

            int w = size;
            border = new BasicStroke(Math.max(1f, w/150f), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
            secondHand = new BasicStroke(Math.max(1f, w/75f), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
            minuteHand = new BasicStroke(Math.max(1f, w/38f), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
            hourHand = new BasicStroke(Math.max(1.5f, w/20f), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
            ticks = new BasicStroke(1f);

            Graphics2D g = (Graphics2D)graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
                               RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, 
                               RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_RENDERING,
                               RenderingHints.VALUE_RENDER_QUALITY);
            Color bg = getBackground();
            g.setColor(new Color(bg.getRed(), bg.getGreen(), bg.getBlue()));

            g.fill(new Ellipse2D.Float(0,0,size,size));
            Font font = getFont();
            g.setFont(font.deriveFont(Font.BOLD, size/12));
            g.setColor(new Color(0,0,0,128));
            g.setStroke(border);
            g.draw(new Ellipse2D.Float(0,0,size-1,size-1));
            g.draw(new Ellipse2D.Float(margin,margin,size-margin*2-1,size-margin*2-1));

            Calendar c = Calendar.getInstance();
            int minute = c.get(Calendar.MINUTE);
            int hour = c.get(Calendar.HOUR);
            int second = c.get(Calendar.SECOND);
            g.translate(center.x, center.y);
            g.setColor(getForeground());
            int numbers = radius * 3 / 4;
            for (int i=0;i < 12;i++) {
                double theta = Math.PI*2*i/12;
                String str = getRomanNumeral((i+2)%12+1); 
                Rectangle2D rect = g.getFontMetrics().getStringBounds(str, g);
                g.drawString(str, Math.round(numbers*Math.cos(theta)-rect.getWidth()/2),
                             Math.round(numbers*Math.sin(theta)+margin*2));
            }
            for (int i=0;i < 60;i++) {
                g.setColor(getForeground());
                g.setStroke(ticks);
                g.drawLine(radius-margin*2, 0, radius-margin, 0);
                if ((i % 5) == 0) {
                    g.drawLine(radius-margin*3, 0, radius-margin, 0);
                }
                if ((i + 15) % 60 == minute) {
                    g.setStroke(minuteHand);
                    g.drawLine(0, 0, radius-margin*4, 0);
                }
                if ((i + 15) % 60 == (hour * 5 + minute * 5 / 60)) {
                    g.setStroke(hourHand);
                    g.drawLine(0, 0, radius/2, 0);
                }
                if ((i + 15) % 60 == second) {
                    g.setColor(new Color(255, 0, 0, 128));
                    g.setStroke(secondHand);
                    g.drawLine(0, 0, radius-margin*4, 0);
                }
                g.rotate(Math.PI*2/60);
            }
            g.dispose();
        }
        
        public Image getIconImage() {
            BufferedImage image = new BufferedImage(ICON_SIZE, ICON_SIZE,
                                                    BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = image.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_RENDERING,
                               RenderingHints.VALUE_RENDER_QUALITY);
            Composite old = g.getComposite();
            g.setComposite(AlphaComposite.Clear);
            g.fillRect(0, 0, image.getWidth(), image.getHeight());
            g.setComposite(old);
            paintFace(g, ICON_SIZE);
            return image;
        }
    }
    
    public static void main(String[] args) {
        try {
            System.setProperty("sun.java2d.noddraw", "true");
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch(Exception e) {
        }
        final JFrame frame = new JFrame("Shaped Window Demo");
        MouseInputAdapter handler = new MouseInputAdapter() {
            private Point offset;
            private void showPopup(MouseEvent e) {
                final JPopupMenu m = new JPopupMenu();
                m.add(new AbstractAction("Hide") {
                    public void actionPerformed(ActionEvent e) {
                        frame.setState(JFrame.ICONIFIED);
                    }
                });
                m.add(new AbstractAction("Close") {
                    public void actionPerformed(ActionEvent e) {
                        System.exit(0);
                    }
                });
                m.pack();
                m.show(e.getComponent(), e.getX(), e.getY());
            }
            public void mousePressed(MouseEvent e) {
                offset = e.getPoint();
                if (e.isPopupTrigger()) {
                    showPopup(e);
                }
            }
            public void mouseDragged(MouseEvent e) {
                if (!SwingUtilities.isLeftMouseButton(e))
                    return;
                Point where = e.getPoint();
                where.translate(-offset.x, -offset.y);
                Point loc = frame.getLocationOnScreen();
                loc.translate(where.x, where.y);
                frame.setLocation(loc.x, loc.y);
            }
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    showPopup(e);
                }
            }
        };
        frame.addMouseListener(handler);
        frame.addMouseMotionListener(handler);
        ClockFace face = new ClockFace(new Dimension(150, 150));
        frame.getContentPane().setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
        frame.getContentPane().add(face);
        frame.setUndecorated(true);
        try {
            Shape mask = new Area(new Ellipse2D.Float(0, 0, 150, 150));
            WindowUtils.setWindowMask(frame, mask);
            if (WindowUtils.isWindowAlphaSupported()) {
                WindowUtils.setWindowAlpha(frame, .7f);
            }
            frame.setIconImage(face.getIconImage());
            frame.setResizable(false);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            frame.pack();
            frame.setLocation(100, 100);
            frame.setVisible(true);
        }
        catch(UnsatisfiedLinkError e) {
            e.printStackTrace();
            String msg = e.getMessage() 
                + "\nError loading the JNA library";
            JTextArea area = new JTextArea(msg);
            area.setOpaque(false);
            area.setFont(UIManager.getFont("Label.font"));
            area.setEditable(false);
            area.setColumns(80);
            area.setRows(8);
            area.setWrapStyleWord(true);
            area.setLineWrap(true);
            JOptionPane.showMessageDialog(frame, new JScrollPane(area), 
                                          "Library Load Error: "
                                          + System.getProperty("os.name")
                                          + "/" + System.getProperty("os.arch"),
                                          JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }
}
