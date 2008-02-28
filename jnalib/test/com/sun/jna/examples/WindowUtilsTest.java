/* Copyright (c) 2007-2008 Timothy Wall, All Rights Reserved
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
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Shape;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.geom.Area;
import java.lang.ref.WeakReference;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputAdapter;

import com.sun.jna.Platform;

import junit.framework.TestCase;

// NOTE: java.awt.Robot can't properly capture transparent pixels
// Transparency tests are disabled until this can be resolved
// TODO: test method invocations before/after pack, before/after setvisible
// TODO: test RootPaneContainer/non-RootPaneContainer variations
// TODO: use ComponentTestFixture from abbot
public class WindowUtilsTest extends TestCase {

    MouseInputAdapter handler = new MouseInputAdapter() {
        private Point offset;
        public void mousePressed(MouseEvent e) {
            if (SwingUtilities.isLeftMouseButton(e))
                offset = e.getPoint();
        }
        public void mouseClicked(MouseEvent e) {
            if (e.getClickCount() == 2) {
                System.exit(1);
            }
        }
        public void mouseReleased(MouseEvent e) {
            offset = null;
        }
        public void mouseDragged(MouseEvent e) {
            if (offset != null) {
                Window w = (Window)e.getSource();
                Point where = e.getPoint();
                where.translate(-offset.x, -offset.y);
                Point loc = w.getLocationOnScreen();
                loc.translate(where.x, where.y);
                w.setLocation(loc.x, loc.y);
            }
        }
    };
    
    private Robot robot;
    
    protected void setUp() throws Exception {
        if (!GraphicsEnvironment.isHeadless())
            robot = new Robot();
    }
    
    protected void tearDown() {
        robot = null;
        if (!GraphicsEnvironment.isHeadless()) {
            Window[] owned = JOptionPane.getRootFrame().getOwnedWindows();
            for (int i=0;i < owned.length;i++) {
                owned[i].dispose();
            }
        }
    }
    
    private static final int X = 100;
    private static final int Y = 100;
    private static final int W = 100;
    private static final int H = 100;
    
    public void xtestReveal() throws Exception {
        final int SIZE = 200;
        System.setProperty("sun.java2d.noddraw", "true");
        GraphicsConfiguration gconfig =
            WindowUtils.getAlphaCompatibleGraphicsConfiguration();
        Window w;
        Container content;
        if (true) {
            JFrame frame = new JFrame(getName(), gconfig);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            content = frame.getContentPane();
            w = frame;
        }
        else {
            Frame frame = JOptionPane.getRootFrame();
            JWindow window = new JWindow(frame, gconfig);
            content = window.getContentPane();
            w = window;
        }
        final Window f = w;
        WindowUtils.setWindowTransparent(f, true);
        content.add(new JButton("Quit") {
            {
                addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        System.exit(0);
                    }
                });
            }
        }, BorderLayout.SOUTH);
        content.add(new JComponent() {
            public Dimension getPreferredSize() {
                return new Dimension(SIZE, SIZE);
            }
            protected void paintComponent(Graphics graphics) {
                Graphics2D g = (Graphics2D)graphics.create();
                g.setComposite(AlphaComposite.Clear);
                g.fillRect(0,0,SIZE,SIZE);
                g.dispose();

                g = (Graphics2D)graphics.create();
                Color[] colors = {
                    new Color(0,0,0),
                    new Color(0,0,0,128),
                    new Color(128,128,128),
                    new Color(128,128,128,128),
                    new Color(255,255,255),
                    new Color(255,255,255,128),
                };
                for (int i=0;i < colors.length;i++) {
                    g.setColor(colors[i]);
                    g.fillRect((SIZE * i)/colors.length, 0,
                               (SIZE + colors.length-1)/colors.length, SIZE);
                }
                g.setColor(Color.red);
                g.drawRect(0, 0, SIZE-1, SIZE-1);
                g.dispose();
                SwingUtilities.getWindowAncestor(this).toFront();
            }
        });
        f.pack();
        f.addMouseListener(handler);
        f.addMouseMotionListener(handler);
        f.setLocation(100, 100);
        f.setVisible(true);
        while (f.isVisible()) {
            Thread.sleep(1000);
            //f.repaint();
        }
    }

    // Expect failure on windows and x11, since transparent pixels are not 
    // properly captured by java.awt.Robot
    public void xtestWindowTransparency() throws Exception {
        if (GraphicsEnvironment.isHeadless())
            return;
        System.setProperty("sun.java2d.noddraw", "true");
        GraphicsConfiguration gconfig = WindowUtils.getAlphaCompatibleGraphicsConfiguration();
        Frame root = JOptionPane.getRootFrame();
        final Window background = new Window(root);
        background.setBackground(Color.white);
        background.setLocation(X, Y);
        final JWindow transparent = new JWindow(root, gconfig);
        transparent.setLocation(X, Y);
        ((JComponent)transparent.getContentPane()).setOpaque(false);
        transparent.getContentPane().add(new JComponent() {
            public Dimension getPreferredSize() {
                return new Dimension(W, H);
            }
            protected void paintComponent(Graphics g) {
                g = g.create();
                g.setColor(Color.red);
                g.fillRect(getWidth()/4, getHeight()/4, getWidth()/2, getHeight()/2);
                g.drawRect(0, 0, getWidth()-1, getHeight()-1);
                g.dispose();
            }
        });
        transparent.addMouseListener(handler);
        transparent.addMouseMotionListener(handler);
        
        SwingUtilities.invokeAndWait(new Runnable() { public void run() {
            background.pack();
            background.setSize(new Dimension(W, H));
            background.setVisible(true);
            transparent.pack();
            transparent.setSize(new Dimension(W, H));
            transparent.setVisible(true);
            transparent.toFront();
        }});
        
        WindowUtils.setWindowTransparent(transparent, true);
        
        //robot.delay(60000);

        Color sample = robot.getPixelColor(X + W/2, Y + H/2);
        assertEquals("Painted pixel should be opaque", Color.red, sample);
        
        sample = robot.getPixelColor(X + 10, Y + 10);
        assertEquals("Unpainted pixel should be transparent", Color.white, sample);
    }
    
    // Expect failure on windows and x11, since transparent pixels are not 
    // properly captured by java.awt.Robot
    public void xtestWindowAlpha() throws Exception {
        if (GraphicsEnvironment.isHeadless())
            return;
        System.setProperty("sun.java2d.noddraw", "true");
        GraphicsConfiguration gconfig = WindowUtils.getAlphaCompatibleGraphicsConfiguration();
        Frame root = JOptionPane.getRootFrame();
        final Window background = new Window(root);
        background.setBackground(Color.white);
        background.setLocation(X, Y);
        final Window transparent = new Window(root, gconfig);
        transparent.setBackground(Color.black);
        transparent.setLocation(X, Y);
        WindowUtils.setWindowAlpha(transparent, .5f);
        
        transparent.addMouseListener(handler);
        transparent.addMouseMotionListener(handler);

        SwingUtilities.invokeAndWait(new Runnable() { public void run() {
            background.pack();
            background.setSize(new Dimension(W, H));
            background.setVisible(true);
            transparent.pack();
            transparent.setSize(new Dimension(W, H));
            transparent.setVisible(true);
        }});
        
        //robot.delay(60000);

        Point where = new Point(transparent.getX() + W/2, 
                                transparent.getY() + H/2);
        Color sample = robot.getPixelColor(where.x, where.y);
        // NOTE: w32 won't sample non-opaque windows
        if (System.getProperty("os.name").startsWith("Windows")) {
            assertFalse("Sample not transparent (w32)",
                        sample.equals(transparent.getBackground()));
        }
        else {
            assertEquals("Sample should be 50% fg/bg",
                         new Color(128, 128, 128), sample);
        }
        
        SwingUtilities.invokeAndWait(new Runnable() {public void run() {
            WindowUtils.setWindowAlpha(transparent, 1f);
        }});
        sample = robot.getPixelColor(where.x, where.y);
        assertEquals("Window should be opaque with alpha=1f",
                     transparent.getBackground(), sample);
        
        SwingUtilities.invokeAndWait(new Runnable() {public void run() {
            WindowUtils.setWindowAlpha(transparent, 0f);
        }});
        sample = robot.getPixelColor(where.x, where.y);
        assertEquals("Window should be transparent with alpha=0f",
                     transparent.getBackground(), sample);
    }
    
    public void testWindowRegion() throws Exception {
        if (GraphicsEnvironment.isHeadless())
            return;
        Frame root = JOptionPane.getRootFrame();
        final Window back = new Window(root);
        // Avoid display idiosyncrasies by using "standard" colors
        // (Don't use black, since a failed sample is sometimes black)
        final Color BACKGROUND = Color.GREEN;
        final Color FOREGROUND = Color.RED;
        back.setBackground(BACKGROUND);
        back.setLocation(X, Y);
        final JWindow front = new JWindow(root);
        front.getContentPane().setBackground(FOREGROUND);
        front.setLocation(X, Y);
        Area mask = new Area(new Rectangle(0, 0, W, H));
        mask.subtract(new Area(new Rectangle(W/4, H/4, W/2, H/2)));
        WindowUtils.setWindowMask(front, mask);
        
        front.addMouseListener(handler);
        front.addMouseMotionListener(handler);

        SwingUtilities.invokeAndWait(new Runnable() { public void run() {
            back.pack();
            back.setSize(new Dimension(W, H));
            back.setVisible(true);
            front.pack();
            front.setSize(new Dimension(W, H));
            front.setVisible(true);
        }});
        
        Point where = front.getLocationOnScreen();
        where.translate(W/8, H/8);
        Color sample = robot.getPixelColor(where.x, where.y);
        long start = System.currentTimeMillis();
        while (!sample.equals(FOREGROUND)) {
            SwingUtilities.invokeAndWait(new Runnable() { public void run() {
                front.toFront(); 
            }});
            Thread.sleep(10);
            if (System.currentTimeMillis() - start > 5000)
                fail("Timed out waiting for shaped window to appear, "
                     + "expected foreground color (sample="
                     + sample + " vs expected=" + FOREGROUND + ")");
            sample = robot.getPixelColor(where.x, where.y);
        }

        where = front.getLocationOnScreen();
        where.translate(W/2, H/2);
        sample = robot.getPixelColor(where.x, where.y);
        start = System.currentTimeMillis();
        while (!sample.equals(BACKGROUND)) {
            Thread.sleep(10);
            if (System.currentTimeMillis() - start > 1000) 
                assertEquals("Background window should show through (center) "
                             + where, BACKGROUND, sample);
            sample = robot.getPixelColor(where.x, where.y);
        }
    }
    
    public void testDisposeHeavyweightForcer() throws Exception {
		if (GraphicsEnvironment.isHeadless())
			return;
		// Forcer not required on OSX
		if (Platform.isMac())
			return;

		Frame root = JOptionPane.getRootFrame();
		final JWindow w = new JWindow(root);
		w.getContentPane().add(new JLabel(getName()));
		final Rectangle mask = new Rectangle(0, 0, 10, 10);
		SwingUtilities.invokeAndWait(new Runnable() {
			public void run() {
				w.pack();
				WindowUtils.setWindowMask(w, mask);
				w.setVisible(true);
			}
		});
		try {
			Window[] owned = w.getOwnedWindows();
			WeakReference ref = null;
			for (int i = 0; i < owned.length; i++) {
				if (owned[i].getClass().getName().indexOf("Heavy") != -1) {
					ref = new WeakReference(owned[i]);
					break;
				}
			}
			owned = null;
			assertNotNull("Forcer not found", ref);
			SwingUtilities.invokeAndWait(new Runnable() {
				public void run() {
					WindowUtils.setWindowMask(w, WindowUtils.MASK_NONE);
				}
			});
			System.gc();
			long start = System.currentTimeMillis();
			while (ref.get() != null) {
				Thread.sleep(10);
				System.gc();
				if (System.currentTimeMillis() - start > 5000)
					fail("Timed out waiting for forcer to be GC'd");
			}
			assertNull("Forcer not GC'd", ref.get());
		} finally {
			w.dispose();
		}
	}

    // Test for accumulation of windows with repetitive setting of mask
    public void xtestWindowDisposeBug() throws Exception {
        final JFrame w = new JFrame(getName());
        w.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        w.getContentPane().add(new JLabel(getName()));

        WindowUtils.setWindowMask(w, new Area(new Rectangle(600, 20)));
        w.pack();
        // small window, no bug.  big window, bug.
        w.setSize(600, 600);
        w.setResizable(false);
        w.setVisible(true);
        final Shape mask = new Rectangle(0, 0, w.getWidth(), w.getHeight());
        while (true) {
            System.gc();
            Thread.sleep(50);
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    WindowUtils.setWindowMask(w, mask);
                    Window[] owned = w.getOwnedWindows();
                    System.err.println(owned.length + ": " + Arrays.asList(w.getOwnedWindows()));
                }
            });
        }
    }
    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(WindowUtilsTest.class);
    }
}
