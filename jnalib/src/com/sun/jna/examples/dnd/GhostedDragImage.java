/* Copyright (c) 2007 Timothy Wall, All Rights Reserved
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * <p/>
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.  
 */
package com.sun.jna.examples.dnd;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.dnd.DragSource;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Area;

import javax.swing.Icon;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import com.sun.jna.examples.WindowUtils;

/** Provide a ghosted drag image for use during drags where 
 * {@link DragSource#isDragImageSupported} returns false.<p>
 * Its location in screen coordinates may be changed via {@link #move}.<p>  
 * When the image is no longer needed, invoke {@link #dispose}, which
 * hides the graphic immediately, or {@link #returnToOrigin}, which 
 * moves the image to its original location and then disposes it.
 */
public class GhostedDragImage {

    private static final float DEFAULT_ALPHA = .5f;
    private Window dragImage;
    // Initial image position, relative to drag source
    private Point origin;

    /** Create a ghosted drag image, using the given icon.
     * @param icon image to be drawn
     * @param initialScreenLoc initial screen location of the image
     */
    public GhostedDragImage(Component dragSource, final Icon icon, Point initialScreenLoc, 
                            final Point cursorOffset) {
        Window parent = dragSource instanceof Window
            ? (Window)dragSource : SwingUtilities.getWindowAncestor(dragSource);
        // FIXME ensure gc is compatible (X11)
        GraphicsConfiguration gc = parent.getGraphicsConfiguration();
        dragImage = new Window(JOptionPane.getRootFrame(), gc) {
            public void paint(Graphics g) {
                icon.paintIcon(this, g, 0, 0);
            }
            public Dimension getPreferredSize() {
                return new Dimension(icon.getIconWidth(), icon.getIconHeight()); 
            }
            public Dimension getMinimumSize() {
                return getPreferredSize();
            }
            public Dimension getMaximumSize() {
                return getPreferredSize();
            }
        };
        dragImage.setFocusableWindowState(false);
        dragImage.setName("###overrideRedirect###");
        Icon dragIcon = new Icon() {
            public int getIconHeight() {
                return icon.getIconHeight();
            }
            public int getIconWidth() {
                return icon.getIconWidth();
            }
            public void paintIcon(Component c, Graphics g, int x, int y) {
                g = g.create();
                Area area = new Area(new Rectangle(x, y, getIconWidth(), getIconHeight()));
                // X11 needs more of a window due to differences in event processing
                area.subtract(new Area(new Rectangle(x + cursorOffset.x-1, y + cursorOffset.y-1, 3, 3)));
                g.setClip(area);
                icon.paintIcon(c, g, x, y);
                g.dispose();
            }
            
        };
        dragImage.pack();
        WindowUtils.setWindowMask(dragImage, dragIcon);
        WindowUtils.setWindowAlpha(dragImage, DEFAULT_ALPHA);
        move(initialScreenLoc);
        dragImage.setVisible(true);
    }

    /** Set the transparency of the ghosted image. */
    public void setAlpha(float alpha) {
        WindowUtils.setWindowAlpha(dragImage, alpha);
    }
    
    /** Make all ghosted images go away. */
    public void dispose() {
        dragImage.dispose();
        dragImage = null;
    }

    /** Move the ghosted image to the requested location. 
     * @param screenLocation Where to draw the image, in screen coordinates
     */
    public void move(Point screenLocation) {
        if (origin == null) {
            origin = screenLocation;
        }
        dragImage.setLocation(screenLocation.x, screenLocation.y);
    }
    
    private static final int SLIDE_INTERVAL = 1000/30;
    /** Animate the ghosted image returning to its origin. */
    public void returnToOrigin() {
        final Timer timer = new Timer(SLIDE_INTERVAL, null);
        timer.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Point location = dragImage.getLocationOnScreen();
                Point dst = new Point(origin);
                int dx = (dst.x - location.x)/2;
                int dy = (dst.y - location.y)/2;
                if (dx != 0 || dy != 0) {
                    location.translate(dx, dy);
                    move(location);
                }
                else {
                    timer.stop();
                    dispose();
                }
            }
        });
        timer.setInitialDelay(0);
        timer.start();
    }
}