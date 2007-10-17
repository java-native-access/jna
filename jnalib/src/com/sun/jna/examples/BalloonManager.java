/*
 * Copyright (c) 2007 Timothy Wall, All Rights Reserved 
 * 
 * This library is free software; you can redistribute it and/or modify it 
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation; either version 2.1 of the License, or (at
 * your option) any later version. <p/> This library is distributed in
 * the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for
 * more details.
 */
package com.sun.jna.examples;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Shape;
import java.awt.Window;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.awt.geom.RoundRectangle2D;
import javax.swing.Box;
import javax.swing.JWindow;
import javax.swing.Popup;
import javax.swing.SwingUtilities;

/**
 * Provides a popup balloon containing an arbitrary component.  This provides
 * a form of content-specific decoration less transient than a tooltip, and less
 * heavyweight and more adaptable to changing content than a dedicated window. 
 * Clients are responsible for invoking show and hide on the provided popup.
 */ 
// TODO: anchor balloon point
// TODO: connect drop shadow and parent masks
// TODO: proper preferred size for html
// TODO: lightweight popups
public class BalloonManager {

    // Avoid using drop shadow in some instances
    private static boolean useDropShadow() {
        return WindowUtils.isWindowAlphaSupported();
    }
    
    private static class DropShadow extends JWindow {
        private static final float SHADOW_ALPHA = .25f;
        private static final float YSCALE = .80f;
        private static final double ANGLE = 2*Math.PI/24;
        private static final Point OFFSET = new Point(8, 8);
        private static final Color COLOR = new Color(0, 0, 0, 50);

        private Shape parentMask;
        private ComponentListener listener;
        public DropShadow(final Window parent, Shape mask) {
            super(parent);
            setFocusableWindowState(false);
            setName("###overrideRedirect###");

            Point where = parent.isShowing()
                ? parent.getLocationOnScreen() : parent.getLocation();
            setLocation(where.x + OFFSET.x, where.y + OFFSET.y);
            setBackground(COLOR);
            getContentPane().setBackground(COLOR);

            parentMask = mask;
            parent.addComponentListener(listener = new ComponentAdapter() {
                public void componentMoved(ComponentEvent e) {
                    Point where = getOwner().isShowing()
                        ? getOwner().getLocationOnScreen()
                        : getOwner().getLocation();
                    setLocation(where.x + OFFSET.x, where.y + OFFSET.y);
                }
                public void componentResized(ComponentEvent e) {
                    Component c = e.getComponent();
                    int extra = c.getWidth() + (int)Math.sin(ANGLE)*c.getHeight();
                    setSize(c.getWidth() + extra, c.getHeight());
                    WindowUtils.setWindowMask(DropShadow.this, getMask());
                }
                public void componentShown(ComponentEvent e) {
                    if (!isVisible()) {
                        pack();
                        setVisible(true);
                    }
                }
            });
            addWindowListener(new WindowAdapter() {
                public void windowClosed(WindowEvent e) {
                    if (listener != null) {
                        parent.removeComponentListener(listener);
                        listener = null;
                    }
                }
            });
            WindowUtils.setWindowMask(DropShadow.this, getMask());
            WindowUtils.setWindowAlpha(DropShadow.this, SHADOW_ALPHA);
            if (parent.isVisible()) {
                pack();
                setVisible(true);
            }
        }
        
        public void paint(Graphics graphics) {
            Graphics2D g = (Graphics2D)graphics.create();
            // Workaround for OSX since we only get automatic clipping
            // on the content pane and below
            g.setClip(getMask());
            g.setPaint(new GradientPaint(0, getHeight()/2, new Color(0,0,0,0), getWidth(), getHeight()/2, new Color(0,0,0,255)));
            g.fillRect(0, 0, getWidth(), getHeight());
            g.dispose();
        }

        public Dimension getPreferredSize() {
            Dimension size = getOwner().getPreferredSize();
            size.width += 100;
            size.height += 100;
            return size;
        }
        
        private Shape getMask() {
            Area area = new Area(parentMask);
            Area clip = new Area(parentMask);

            AffineTransform tx = new AffineTransform();
            tx.translate(Math.sin(ANGLE)*getOwner().getHeight(), 0);
            tx.shear(-Math.tan(ANGLE), 0);
            tx.scale(1, YSCALE);
            tx.translate(0, (1-YSCALE)*getOwner().getHeight());
            area.transform(tx);
            tx = new AffineTransform();
            tx.translate(-OFFSET.x, -OFFSET.y);
            clip.transform(tx);
            area.subtract(clip);
            return area;
        }
    }
    
    private static final class BubbleWindow extends JWindow {
        private static final int Y_OFFSET = 50;
        private static final int ARC = 25;

        private Point offset;
        private Area mask;
        private Dimension maskSize;
        private ComponentListener moveTracker = new ComponentAdapter() {
            public void componentMoved(ComponentEvent e) {
                Point where = 
                    e.getComponent().isShowing() 
                    ? e.getComponent().getLocationOnScreen()
                    : e.getComponent().getLocation();
                setLocation(where.x - offset.x, where.y - offset.y);
                // TODO preserve stacking order (linux)
            }
        };
        
        public BubbleWindow(Window owner, Component content) {
            super(owner);
            setFocusableWindowState(false);
            setName("###overrideRedirect###");
            getContentPane().setBackground(Color.white);
            getContentPane().add(content, BorderLayout.CENTER);
            getContentPane().add(Box.createVerticalStrut(Y_OFFSET), BorderLayout.SOUTH);
            owner.addComponentListener(moveTracker);
            setSize(getPreferredSize());
            mask = new Area(getMask(getWidth(), getHeight()));
            maskSize = getSize();
            WindowUtils.setWindowMask(BubbleWindow.this, mask);
            if (useDropShadow()) {
                new DropShadow(this, mask);
            }
        }
        
        public void setBounds(int x, int y, int w, int h) {
            super.setBounds(x, y, w, h);
            Dimension size = new Dimension(w, h);
            if (mask != null && !size.equals(maskSize)) {
                mask.subtract(mask);
                mask.add(new Area(getMask(w, h)));
                maskSize = size;
            }
        }
        
        public void setAnchorLocation(int x, int y) {
            super.setLocation(x, y);
            Window owner = getOwner();
            if (owner != null) {
                Point ref = owner.isShowing()
                    ? owner.getLocationOnScreen() : owner.getLocation();
                offset = new Point(ref.x - x, ref.y - y);
            }
        }
        
        public void dispose() {
            super.dispose();
            getOwner().removeComponentListener(moveTracker);
        }

        private Shape getMask(int w, int h) {
            Shape shape = new RoundRectangle2D.Float(0, 0, w, h-Y_OFFSET, 
                                                     ARC, ARC);
            Area area = new Area(shape);
            GeneralPath path = new GeneralPath();
            path.moveTo(w/3, h-1);
            path.lineTo(w/2, h-1-Y_OFFSET);
            path.lineTo(w*2/3, h-1-Y_OFFSET);
            path.closePath();
            area.add(new Area(path));
            return area;
        }
        
        public Dimension getPreferredSize() {
            Dimension size = super.getPreferredSize();
            size.height += Y_OFFSET;
            return size;
        }
    }

    /** Get a balloon pointing to the given location.  The coordinates are 
     * relative to <code>owner</code>, which if null, indicates the coordinates
     * are absolute.
     */
    public static Popup getBalloon(final Component owner, final Component content, int x, int y) {

        // Simulate PopupFactory, ensuring we get a heavyweight "popup"
        final Point origin = 
            owner == null ? new Point(0, 0)
                : (owner.isShowing()
                   ? owner.getLocationOnScreen() : owner.getLocation());
        final Window parent = owner != null 
            ? SwingUtilities.getWindowAncestor(owner) : null;
        origin.translate(x, y);
        return new Popup() {
            private BubbleWindow w;
            public void show() {
                w = new BubbleWindow(parent, content);
                w.pack();
                Point where = new Point(origin);
                where.translate(-w.getWidth()/3, -w.getHeight());
                w.setAnchorLocation(where.x, where.y);
                w.setVisible(true);
            }
            public void hide() {
                w.setVisible(false);
                w.dispose();
            }
        };
    }  
}
