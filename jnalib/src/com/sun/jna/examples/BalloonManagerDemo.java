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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Rectangle2D;
import javax.swing.Icon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.Popup;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

/** Demonstration of BalloonManager. */
public class BalloonManagerDemo {
    private static final int ICON_SIZE = 48;
    private static class InfoIcon implements Icon {
        public int getIconHeight() {
            return ICON_SIZE;
        }
        public int getIconWidth() {
            return ICON_SIZE;
        }
        public void paintIcon(Component c, Graphics graphics, int x, int y) {
            Font font = UIManager.getFont("TextField.font");
            Graphics2D g = (Graphics2D)graphics.create(x, y, getIconWidth(), getIconHeight());
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g.setFont(font.deriveFont(Font.BOLD, getIconWidth()*3/4));
            g.setColor(Color.green.darker());
            final int SW = Math.max(getIconWidth()/10, 4);
            g.setStroke(new BasicStroke(SW));
            g.drawArc(SW/2, SW/2, getIconWidth()-SW-1, getIconHeight()-SW-1, 0, 360);
            Rectangle2D bounds = 
                font.getStringBounds("i", g.getFontRenderContext());
            g.drawString("i", Math.round((getIconWidth() - bounds.getWidth())/2 - getIconWidth()/12), 
                         SW/2 + Math.round((getIconHeight()-bounds.getHeight())/2 - bounds.getY() + getIconHeight()/8));
            g.dispose();
        }
    }
    public static void main(String[] args) {
        try {
            System.setProperty("sun.java2d.noddraw", "true");
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch(Exception e) {
        }
        JFrame f = new JFrame("Balloon Test");
        final String BALLOON_TEXT = "<html><center>"
            + "This is some sample balloon text<br>"
            + "which has been formatted with html.<br>"
            + "Click to dismiss.</center></html>";
        final JLabel content = new JLabel(BALLOON_TEXT);
        content.setIconTextGap(10);
        content.setBorder(new EmptyBorder(0, 8, 0, 8));
        content.setSize(content.getPreferredSize());
        content.setIcon(new InfoIcon());
        JLabel label = new JLabel("Click anywhere for more information");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.addMouseListener(new MouseAdapter() {
            private MouseListener listener = new MouseAdapter() {
                public void mousePressed(MouseEvent e) {
                    hidePopup(e);
                }
            }; 
            private Popup popup;
            private void hidePopup(MouseEvent e) {
                e.getComponent().removeMouseListener(listener);
                if (popup != null)
                    popup.hide();
            }
            public void mousePressed(MouseEvent e) {
                hidePopup(e);
                popup = BalloonManager.getBalloon(e.getComponent(), content, e.getX(), e.getY());
                popup.show();
                content.getParent().addMouseListener(listener);
            }
        });
        f.getContentPane().add(label);
        f.pack();
        f.setSize(new Dimension(300, 300));
        f.setLocation(100, 100);
        try {
            // Force a load of JNA
            WindowUtils.setWindowMask(f, WindowUtils.MASK_NONE);
            f.setVisible(true);
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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
            JOptionPane.showMessageDialog(null, new JScrollPane(area), 
                                          "Library Load Error: "
                                          + System.getProperty("os.name")
                                          + "/" + System.getProperty("os.arch"),
                                          JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }    
}
