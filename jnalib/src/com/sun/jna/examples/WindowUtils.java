/*
 * Copyright (c) 2007 Timothy Wall, All Rights Reserved 
 * Parts Copyright (c) 2007 Olivier Chafik 
 * 
 * This library is free software; you can
 * redistribute it and/or modify it under the terms of the GNU Lesser
 * General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later
 * version. <p/> This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 */
package com.sun.jna.examples;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Window;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.PopupFactory;
import javax.swing.RootPaneContainer;
import javax.swing.SwingUtilities;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.examples.unix.X11;
import com.sun.jna.examples.unix.X11.XVisualInfo;
import com.sun.jna.examples.unix.X11.Xext;
import com.sun.jna.examples.unix.X11.Xrender.XRenderPictFormat;
import com.sun.jna.examples.win32.GDI32;
import com.sun.jna.examples.win32.User32;
import com.sun.jna.examples.win32.GDI32.BITMAPINFO;
import com.sun.jna.examples.win32.User32.BLENDFUNCTION;
import com.sun.jna.examples.win32.User32.POINT;
import com.sun.jna.examples.win32.User32.SIZE;
import com.sun.jna.ptr.ByteByReference;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

/**
 * Provides additional features on a Java {@link Window}.
 * <ul>
 * <li>Non-rectangular shape
 * <li>Transparency (constant alpha applied to window contents or
 * transparent background)
 * </ul>
 * NOTE: since there is no explicit way to force PopupFactory to use a
 * heavyweight popup, and anything but a heavyweight popup will be
 * clipped by a window mask, an additional subwindow is added to all
 * masked windows to implicitly force PopupFactory to use a heavyweight
 * window and avoid clipping.
 * <p>
 * NOTE: {@link #setWindowTransparent} on X11 doesn't seem to composite
 * entirely correctly; depending on what's drawn in the window it mey be
 * more or less noticable. 
 * <p>
 * NOTE: Neither shaped windows nor transparency
 * currently works with Java 1.4 under X11. This is at least partly due
 * to 1.4 using multiple X11 windows for a single given Java window. It
 * *might* be possible to remedy by applying the window
 * region/transparency to all descendants, but I haven't tried it. In
 * addition, windows must be both displayable <em>and</em> visible
 * before the corresponding native Drawable may be obtained; in later
 * Java versions, the window need only be displayable.
 */
// TODO: setWindowMask() should accept a threshold; some cases want a
// 50% threshold, some might want zero/non-zero
public class WindowUtils {
    public static boolean doPaint;
    private static final String TRANSPARENT_OLD_BG = "transparent-old-bg";
    private static final String TRANSPARENT_OLD_OPAQUE = "transparent-old-opaque";
    private static final String TRANSPARENT_ALPHA = "transparent-alpha";
    /** Use this to clear a window mask. */
    public static final Shape MASK_NONE = null;

    /**
     * This class forces a heavyweight popup on the parent
     * {@link Window}. See the implementation of {@link PopupFactory};
     * a heavyweight is forced if there is an occluding subwindow on the
     * target window.
     * <p>
     * Ideally we'd have more control over {@link PopupFactory} but this
     * is a fairly simple, lightweight workaround.
     */
    private static class HeavyweightForcer extends Window {
        private boolean packed;

        public HeavyweightForcer(Window parent) {
            super(parent);
            pack();
            packed = true;
        }

        public boolean isVisible() {
            // Only want to be 'visible' once the peer is instantiated
            // via pack
            return packed;
        }

        public Rectangle getBounds() {
            return getOwner().getBounds();
        }
    }
    /**
     * This can be installed over a {@link JLayeredPane} in order to
     * listen for repaint requests. The {@link #update} method will be
     * invoked whenever any part of the ancestor window is repainted.
     */
    private static abstract class RepaintTrigger extends JComponent {
        protected class Listener extends WindowAdapter implements
            ComponentListener, HierarchyListener {
            public void windowOpened(WindowEvent e) {
                repaint();
            }

            public void componentHidden(ComponentEvent e) {}

            public void componentMoved(ComponentEvent e) {}

            public void componentResized(ComponentEvent e) {
                setSize(getParent().getSize());
                repaint();
            }

            public void componentShown(ComponentEvent e) {
                repaint();
            }

            public void hierarchyChanged(HierarchyEvent e) {
                repaint();
            }
        }

        private Listener listener = createListener();

        public void addNotify() {
            super.addNotify();
            Window w = SwingUtilities.getWindowAncestor(this);
            setSize(getParent().getSize());
            w.addComponentListener(listener);
            w.addWindowListener(listener);
        }

        public void removeNotify() {
            Window w = SwingUtilities.getWindowAncestor(this);
            w.removeComponentListener(listener);
            w.removeWindowListener(listener);
            super.removeNotify();
        }

        private boolean painting;

        protected void paintComponent(Graphics g) {
            if (!painting) {
                painting = true;
                update();
                painting = false;
            }
        }

        protected Listener createListener() {
            return new Listener();
        }

        protected abstract void update();

        public static void remove(Container c) {
            for (int i = 0; i < c.getComponentCount(); i++) {
                if (c.getComponent(i) instanceof RepaintTrigger) {
                    c.remove(i);
                    return;
                }
            }
        }
    };

    /**
     * Execute the given action when the given window becomes
     * displayable.
     */
    public static void whenDisplayable(Window w, final Runnable action) {
        if (w.isDisplayable() && (!Holder.requiresVisible || w.isVisible())) {
            action.run();
        }
        else if (Holder.requiresVisible) {
            w.addWindowListener(new WindowAdapter() {
                public void windowOpened(WindowEvent e) {
                    e.getWindow().removeWindowListener(this);
                    action.run();
                }
                public void windowClosed(WindowEvent e) {
                    e.getWindow().removeWindowListener(this);
                }
            });
        }
        else {
            // Hierarchy events are fired in direct response to
            // displayability
            // changes
            w.addHierarchyListener(new HierarchyListener() {
                public void hierarchyChanged(HierarchyEvent e) {
                    if ((e.getChangeFlags() & HierarchyEvent.DISPLAYABILITY_CHANGED) != 0
                        && e.getComponent().isDisplayable()) {
                        e.getComponent().removeHierarchyListener(this);
                        action.run();
                    }
                }
            });
        }
    }

    /** Window utilities with differing native implementations. */
    public static abstract class NativeWindowUtils {
        /**
         * Set the overall alpha transparency of the window. An alpha of
         * 1.0 is fully opaque, 0.0 is fully transparent.
         */
        public void setWindowAlpha(Window w, float alpha) {
        // do nothing
        }

        /** Default: no support. */
        public boolean isWindowAlphaSupported() {
            return false;
        }

        /** Return the default graphics configuration. */
        public GraphicsConfiguration getAlphaCompatibleGraphicsConfiguration() {
            GraphicsEnvironment env = GraphicsEnvironment
                                                         .getLocalGraphicsEnvironment();
            GraphicsDevice dev = env.getDefaultScreenDevice();
            return dev.getDefaultConfiguration();
        }

        /**
         * Set the window to be transparent. Only explicitly painted
         * pixels will be non-transparent. All pixels will be composited
         * with whatever is under the window using their alpha values.
         */
        public void setWindowTransparent(Window w, boolean transparent) {
        // do nothing
        }

        protected void setLayersTransparent(Window w, boolean transparent) {
            Color bg = transparent ? new Color(0, 0, 0, 0) : null;
            if (w instanceof RootPaneContainer) {
                RootPaneContainer rpc = (RootPaneContainer)w;
                JRootPane root = rpc.getRootPane();
                JLayeredPane lp = root.getLayeredPane();
                Container c = root.getContentPane();
                JComponent content = 
                    (c instanceof JComponent) ? (JComponent)c : null;
                if (transparent) {
                    lp.putClientProperty(TRANSPARENT_OLD_OPAQUE,
                                         Boolean.valueOf(lp.isOpaque()));
                    lp.setOpaque(false);
                    root.putClientProperty(TRANSPARENT_OLD_OPAQUE,
                                           Boolean.valueOf(root.isOpaque()));
                    root.setOpaque(false);
                    if (content != null) {
                        content.putClientProperty(TRANSPARENT_OLD_OPAQUE,
                                                  Boolean.valueOf(content.isOpaque()));
                        content.setOpaque(false);
                    }
                    root.putClientProperty(TRANSPARENT_OLD_BG,
                                           root.getParent().getBackground());
                }
                else {
                    lp.setOpaque(Boolean.TRUE.equals(lp.getClientProperty(TRANSPARENT_OLD_OPAQUE)));
                    root
                        .setOpaque(Boolean.TRUE.equals(root.getClientProperty(TRANSPARENT_OLD_OPAQUE)));
                    if (content != null) {
                        content.setOpaque(Boolean.TRUE.equals(content.getClientProperty(TRANSPARENT_OLD_OPAQUE)));
                    }
                    bg = (Color)root.getClientProperty(TRANSPARENT_OLD_BG);
                }
            }
            w.setBackground(bg);
        }

        /**
         * Set the window mask based on the given Raster, which should
         * be treated as a bitmap (zero/nonzero values only). A value of
         * <code>null</code> means to remove the mask.
         */
        public abstract void setWindowMask(Window w, Raster raster);

        /** Set the window mask based on a {@link Shape}. */
        public void setWindowMask(Window w, Shape mask) {
            Raster raster = null;
            if (mask != MASK_NONE) {
                Rectangle bounds = mask.getBounds();
                if (bounds.width > 0 && bounds.height > 0) {
                    BufferedImage bitmap = 
                        new BufferedImage(bounds.x + bounds.width,
                                          bounds.y + bounds.height,
                                          BufferedImage.TYPE_BYTE_BINARY);
                    Graphics2D g = bitmap.createGraphics();
                    g.setColor(Color.black);
                    g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
                    g.setColor(Color.white);
                    g.fill(mask);
                    raster = bitmap.getData();
                }
            }
            setWindowMask(w, raster);
        }

        /**
         * Set the window mask based on an Icon. All non-transparent
         * pixels will be included in the mask.
         */
        public void setWindowMask(final Window w, Icon mask) {
            Raster raster = null;
            if (mask != null) {
                Rectangle bounds = new Rectangle(0, 0, mask.getIconWidth(),
                                                 mask.getIconHeight());
                BufferedImage clip = new BufferedImage(bounds.width,
                                                       bounds.height,
                                                       BufferedImage.TYPE_INT_ARGB);
                Graphics2D g = clip.createGraphics();
                g.setComposite(AlphaComposite.Clear);
                g.fillRect(0, 0, bounds.width, bounds.height);
                g.setComposite(AlphaComposite.SrcOver);
                mask.paintIcon(w, g, 0, 0);
                raster = clip.getAlphaRaster();
            }
            setWindowMask(w, raster);
        }

        /**
         * Use this method to ensure heavyweight popups are used in
         * conjunction with a given window. This prevents the window's
         * alpha setting or mask region from being applied to the popup.
         */
        protected void setForceHeavyweightPopups(Window w, boolean force) {
            if (!(w instanceof HeavyweightForcer)) {
                Window[] owned = w.getOwnedWindows();
                for (int i = 0; i < owned.length; i++) {
                    if (owned[i] instanceof HeavyweightForcer) {
                        owned[i].dispose();
                    }
                }
                if (force) {
                    new HeavyweightForcer(w);
                }
            }
        }
    }
    /** Canonical lazy loading of a singleton. */
    private static class Holder {
        /**
         * Indicates whether a window must be visible before its native
         * handle can be obtained. This wart is caused by the Java
         * 1.4/X11 implementation.
         */
        public static boolean requiresVisible;
        public static final NativeWindowUtils INSTANCE;
        static {
            String os = System.getProperty("os.name");
            if (os.startsWith("Windows")) {
                INSTANCE = new W32WindowUtils();
            }
            else if (os.startsWith("Mac")) {
                INSTANCE = new MacWindowUtils();
            }
            else if (os.startsWith("Linux") || os.startsWith("SunOS")) {
                INSTANCE = new X11WindowUtils();
                requiresVisible = System.getProperty("java.version")
                                        .matches("^1\\.4\\..*");
            }
            else {
                throw new UnsupportedOperationException("No support for " + os);
            }
        }
    }

    private static NativeWindowUtils getInstance() {
        return Holder.INSTANCE;
    }

    private static class W32WindowUtils extends NativeWindowUtils {
        public Pointer getHWnd(Window w) {
            return Native.getWindowPointer(w);
            /*
            try { 
                WindowPeer peer = (WindowPeer)w.getPeer(); 
                Method m = peer.getClass().getMethod("getHWnd", null); 
                return new Pointer(((Long)m.invoke(peer, null)).longValue()); 
                }
            catch (NoSuchMethodException e) { 
                // Method unavailable, fall back to native lookup 
            } catch (Exception e) { }
            return null;
            */
        }

        /**
         * W32 alpha will only work if <code>sun.java2d.noddraw</code>
         * is set
         */
        public boolean isWindowAlphaSupported() {
            return Boolean.getBoolean("sun.java2d.noddraw");
        }

        /** Indicates whether UpdateLayeredWindow is in use. */
        private boolean isTransparent(Window w) {
            if (w instanceof RootPaneContainer) {
                JRootPane root = ((RootPaneContainer)w).getRootPane();
                return root.getClientProperty(TRANSPARENT_OLD_BG) != null;
            }
            return false;
        }

        /** Keep track of the alpha level, since we can't read it from
         * the window itself.
         */
        private void storeAlpha(Window w, byte alpha) {
            if (w instanceof RootPaneContainer) {
                JRootPane root = ((RootPaneContainer)w).getRootPane();
                Byte b = alpha == (byte)0xFF ? null : new Byte(alpha);
                root.putClientProperty(TRANSPARENT_ALPHA, b);
            }
        }

        /** Return the last alpha level we set on the window. */
        private byte getAlpha(Window w) {
            if (w instanceof RootPaneContainer) {
                JRootPane root = ((RootPaneContainer)w).getRootPane();
                Byte b = (Byte)root.getClientProperty(TRANSPARENT_ALPHA);
                if (b != null) {
                    return b.byteValue();
                }
            }
            return (byte)0xFF;
        }

        public void setWindowAlpha(final Window w, final float alpha) {
            if (!isWindowAlphaSupported()) {
                System.err.println("Window alpha not supported");
                return;
            }
            whenDisplayable(w, new Runnable() {
                public void run() {
                    Pointer hWnd = getHWnd(w);
                    User32 user = User32.INSTANCE;
                    int flags = user.GetWindowLong(hWnd, User32.GWL_EXSTYLE);
                    byte level = (byte)((int)(255 * alpha) & 0xFF);
                    if (isTransparent(w)) {
                        // If already using UpdateLayeredWindow, continue to 
                        // do so
                        BLENDFUNCTION blend = new BLENDFUNCTION();
                        blend.SourceConstantAlpha = level;
                        blend.AlphaFormat = User32.AC_SRC_ALPHA;
                        user.UpdateLayeredWindow(hWnd, null, null, null, null,
                                                 null, 0, blend,
                                                 User32.ULW_ALPHA);
                    }
                    else if (alpha == 1f) {
                        flags &= ~User32.WS_EX_LAYERED;
                        user.SetWindowLong(hWnd, User32.GWL_EXSTYLE, flags);
                    }
                    else {
                        flags |= User32.WS_EX_LAYERED;
                        user.SetWindowLong(hWnd, User32.GWL_EXSTYLE, flags);
                        user.SetLayeredWindowAttributes(hWnd, 0, level,
                                                        User32.LWA_ALPHA);
                    }
                    setForceHeavyweightPopups(w, alpha != 1f);
                    storeAlpha(w, level);
                }
            });
        }

        private class W32RepaintTrigger extends RepaintTrigger {
            public void setBounds(int x, int y, int w, int h) {
                super.setBounds(x, y, w, h);
                // FIXME this is a hack to get the window to properly
                // refresh. figure out what w32 api needs tweaking to
                // do it explicitly
                if (w > 0 && h > 0)
                    SwingUtilities.getWindowAncestor(this).toFront();
            }

            public void update() {
                GDI32 gdi = GDI32.INSTANCE;
                User32 user = User32.INSTANCE;
                Window win = SwingUtilities.getWindowAncestor(this);
                int w = win.getWidth();
                int h = win.getHeight();
                Pointer screenDC = user.GetDC(null);
                Pointer memDC = gdi.CreateCompatibleDC(screenDC);
                Pointer hBitmap = null;
                Pointer oldBitmap = null;
                try {
                    BufferedImage buf = 
                        new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB_PRE);
                    Graphics2D g = buf.createGraphics();
                    g.setComposite(AlphaComposite.Clear);
                    g.fillRect(0, 0, w, h);
                    g.setComposite(AlphaComposite.SrcOver);
                    Point origin = SwingUtilities.convertPoint(getParent(), 0, 0, win);
                    getParent().paint(g.create(origin.x, origin.y,
                                               getWidth(), getHeight()));
                    BITMAPINFO bmi = new BITMAPINFO();
                    bmi.bmiHeader.biWidth = w;
                    bmi.bmiHeader.biHeight = h;
                    bmi.bmiHeader.biPlanes = 1;
                    bmi.bmiHeader.biBitCount = 32;
                    bmi.bmiHeader.biCompression = GDI32.BI_RGB;
                    bmi.bmiHeader.biSizeImage = w * h * 4;
                    PointerByReference ppbits = new PointerByReference();
                    hBitmap = gdi.CreateDIBSection(memDC, bmi,
                                                   GDI32.DIB_RGB_COLORS,
                                                   ppbits, null, 0);
                    oldBitmap = gdi.SelectObject(memDC, hBitmap);
                    Pointer pbits = ppbits.getValue();
                    Raster raster = buf.getData();
                    int[] pixel = new int[4];
                    int[] bits = new int[w * h];
                    for (int row = 0; row < h; row++) {
                        for (int col = 0; col < w; col++) {
                            raster.getPixel(col, h - row - 1, pixel);
                            int alpha = (pixel[3] & 0xFF) << 24;
                            int red = (pixel[2] & 0xFF);
                            int green = (pixel[1] & 0xFF) << 8;
                            int blue = (pixel[0] & 0xFF) << 16;
                            bits[col + row * w] = alpha | red | green | blue;
                        }
                    }
                    pbits.write(0, bits, 0, bits.length);
                    SIZE size = new SIZE();
                    size.cx = w;
                    size.cy = h;
                    POINT srcLoc = new POINT();
                    BLENDFUNCTION blend = new BLENDFUNCTION();
                    POINT loc = new POINT();
                    loc.x = win.getX();
                    loc.y = win.getY();
                    Pointer hWnd = Native.getWindowPointer(win);
                    // extract current constant alpha setting, if possible
                    ByteByReference bref = new ByteByReference();
                    IntByReference iref = new IntByReference();
                    byte level = getAlpha(win);
                    if (user.GetLayeredWindowAttributes(hWnd, null, bref, iref)
                        && (iref.getValue() & User32.LWA_ALPHA) != 0) {
                        level = bref.getValue();
                    }
                    blend.SourceConstantAlpha = level;
                    blend.AlphaFormat = User32.AC_SRC_ALPHA;
                    user.UpdateLayeredWindow(hWnd, screenDC, loc, size, memDC,
                                             srcLoc, 0, blend, User32.ULW_ALPHA);
                }
                finally {
                    user.ReleaseDC(null, screenDC);
                    if (hBitmap != null) {
                        gdi.SelectObject(memDC, oldBitmap);
                        gdi.DeleteObject(hBitmap);
                    }
                    gdi.DeleteDC(memDC);
                }
            }
        }

        public void setWindowTransparent(final Window w,
                                         final boolean transparent) {
            if (!(w instanceof RootPaneContainer)) {
                throw new IllegalArgumentException("Window must be a RootPaneContainer");
            }
            if (!isWindowAlphaSupported()) {
                System.err.println("Window alpha not supported");
                return;
            }
            boolean isTransparent = w.getBackground() != null
                && w.getBackground().getAlpha() == 0;
            if (!(transparent ^ isTransparent))
                return;
            whenDisplayable(w, new Runnable() {
                public void run() {
                    User32 user = User32.INSTANCE;
                    Pointer hWnd = getHWnd(w);
                    int flags = user.GetWindowLong(hWnd, User32.GWL_EXSTYLE);
                    JRootPane root = ((RootPaneContainer)w).getRootPane();
                    JLayeredPane lp = root.getLayeredPane();
                    if (transparent && !isTransparent(w)) {
                        flags |= User32.WS_EX_LAYERED;
                        user.SetWindowLong(hWnd, User32.GWL_EXSTYLE, flags);
                        lp.add(new W32RepaintTrigger(), JLayeredPane.DRAG_LAYER);
                    }
                    else if (!transparent && isTransparent(w)) {
                        flags &= ~User32.WS_EX_LAYERED;
                        user.SetWindowLong(hWnd, User32.GWL_EXSTYLE, flags);
                        RepaintTrigger.remove(lp);
                    }
                    setLayersTransparent(w, transparent);
                    setForceHeavyweightPopups(w, transparent);
                }
            });
        }

        public void setWindowMask(final Window w, final Raster raster) {
            whenDisplayable(w, new Runnable() {
                public void run() {
                    GDI32 gdi = GDI32.INSTANCE;
                    User32 user = User32.INSTANCE;
                    Pointer hWnd = getHWnd(w);
                    final Pointer result = gdi.CreateRectRgn(0, 0, 0, 0);
                    try {
                        if (raster == null) {
                            gdi.SetRectRgn(result, 0, 0, w.getWidth(), w.getHeight());
                        }
                        else {
                            final Pointer tempRgn = gdi.CreateRectRgn(0, 0, 0, 0);
                            try {
                                RasterRangesUtils.outputOccupiedRanges(raster, new RasterRangesUtils.RangesOutput() {
                                    public boolean outputRange(int x, int y, int w, int h) {
                                        GDI32 gdi = GDI32.INSTANCE;
                                        gdi.SetRectRgn(tempRgn, x, y, x + w, y + h);
                                        return gdi.CombineRgn(result, result, tempRgn, GDI32.RGN_OR) != GDI32.ERROR;
                                    }
                                });
                            }
                            finally {
                                gdi.DeleteObject(tempRgn);
                            }
                        }
                        user.SetWindowRgn(hWnd, result, true);
                    }
                    finally {
                        gdi.DeleteObject(result);
                    }
                    setForceHeavyweightPopups(w, raster != null);
                }
            });
        }
    }
    private static class MacWindowUtils extends NativeWindowUtils {
        private Shape shapeFromRaster(Raster raster) {
            final Area area = new Area(new Rectangle(0, 0, 0, 0));
            RasterRangesUtils.outputOccupiedRanges(raster, new RasterRangesUtils.RangesOutput() {
                public boolean outputRange(int x, int y, int w, int h) {
                    area.add(new Area(new Rectangle(x, y, w, h)));
                    return true;
                }
            });
            return area;
        }

        public boolean isWindowAlphaSupported() {
            return true;
        }
        
        private OSXTransparentContent installTransparentContent(Window w) {
            OSXTransparentContent content;
            if (w instanceof RootPaneContainer) {
                // TODO: replace layered pane instead?
                final RootPaneContainer rpc = (RootPaneContainer)w;
                Container oldContent = rpc.getContentPane();
                if (oldContent instanceof OSXTransparentContent) {
                    content = (OSXTransparentContent)oldContent;
                }
                else {
                    content = new OSXTransparentContent(oldContent);
                    // TODO: listen for content pane changes
                    rpc.setContentPane(content);
                }
            }
            else {
                Component oldContent = w.getComponentCount() > 0 ? w.getComponent(0) : null;
                if (oldContent instanceof OSXTransparentContent) {
                    content = (OSXTransparentContent)oldContent;
                }
                else {
                    content = new OSXTransparentContent(oldContent);
                    w.add(content);
                }
            }
            return content;
        }

        public void setWindowTransparent(Window w, boolean transparent) {
            boolean isTransparent = w.getBackground() != null
                && w.getBackground().getAlpha() == 0;
            if (!(transparent ^ isTransparent))
                return;
            installTransparentContent(w);
            setBackgroundTransparent(w, transparent);
            setLayersTransparent(w, transparent);
        }

        public void setWindowAlpha(final Window w, final float alpha) {
            whenDisplayable(w, new Runnable() {
                public void run() {
                    Object peer = w.getPeer();
                    try {
                        peer.getClass().getMethod("setAlpha", new Class[]{
                            float.class
                        }).invoke(peer, new Object[]{
                            new Float(alpha)
                        });
                    }
                    catch (Exception e) {
                    }
                }
            });
        }

        public void setWindowMask(Window w, Raster raster) {
            if (raster != null) {
                setWindowMask(w, shapeFromRaster(raster));
            }
            else {
                setWindowMask(w, new Rectangle(0, 0, w.getWidth(),
                                               w.getHeight()));
            }
        }

        /** Mask out unwanted pixels and ensure background gets cleared.
         * @author Olivier Chafik
         */
        static class OSXTransparentContent extends JPanel {
            private Shape shape;

            public OSXTransparentContent() {
                this(null);
            }
            
            public OSXTransparentContent(Component oldContent) {
                super(new BorderLayout());
                if (oldContent != null) {
                    add(oldContent, BorderLayout.CENTER);
                }
            }
            
            public void setMask(Shape shape) {
                this.shape = shape;
                repaint();
            }

            public void paint(Graphics graphics) {
                Graphics2D g = (Graphics2D)graphics.create();
                g.setComposite(AlphaComposite.Clear);
                g.fillRect(0, 0, getWidth(), getHeight());
                g.dispose();
                if (shape != null) {
                    g = (Graphics2D)graphics.create();
                    g.setClip(shape);
                    super.paint(g);
                    g.dispose();
                }
                else {
                    super.paint(graphics);
                }
            }
        }
        
        private void setBackgroundTransparent(Window w, boolean transparent) {
            if (transparent) {
                w.setBackground(new Color(0,0,0,0));
            }
            else {
                // FIXME restore background
                w.setBackground(new Window(w.getOwner()).getBackground());
            }
        }

        public void setWindowMask(Window w, final Shape shape) {
            OSXTransparentContent content = installTransparentContent(w);
            content.setMask(shape);
            setBackgroundTransparent(w, shape != MASK_NONE);
        }
    }
    private static class X11WindowUtils extends NativeWindowUtils {
        private int createBitmap(final Pointer dpy, int win, Window w, Raster raster) {
            final X11 x11 = X11.INSTANCE;
            Rectangle bounds = raster.getBounds();
            int width = bounds.x + bounds.width;
            int height = bounds.y + bounds.height;
            final int pm = x11.XCreatePixmap(dpy, win, width, height, 1);
            final Pointer gc = x11.XCreateGC(dpy, pm, 0, null);
            if (gc == null) {
                return X11.None;
            }
            x11.XSetForeground(dpy, gc, 0);
            x11.XFillRectangle(dpy, pm, gc, 0, 0, width, height);
            final int UNMASKED = 1;
            x11.XSetForeground(dpy, gc, UNMASKED);
            X11.XWindowAttributes atts = new X11.XWindowAttributes();
            int status = x11.XGetWindowAttributes(dpy, win, atts);
            if (status == 0) {
                return X11.None;
            }
            try {
                RasterRangesUtils.outputOccupiedRanges(raster, new RasterRangesUtils.RangesOutput() {
                    public boolean outputRange(int x, int y, int w, int h) {
                        return x11.XFillRectangle(dpy, pm, gc, x, y, w, h) != 0;
                    }
                });
            }
            finally {
                x11.XFreeGC(dpy, gc);
            }
            return pm;
        }

        private boolean didCheck;
        private int[] alphaVisuals = {};

        public boolean isWindowAlphaSupported() {
            if (!didCheck) {
                didCheck = true;
                alphaVisuals = getAlphaVisuals();
            }
            return alphaVisuals.length > 0;
        }

        /** Return the default graphics configuration. */
        public GraphicsConfiguration getAlphaCompatibleGraphicsConfiguration() {
            if (isWindowAlphaSupported()) {
                GraphicsEnvironment env = GraphicsEnvironment
                                                             .getLocalGraphicsEnvironment();
                GraphicsDevice[] devices = env.getScreenDevices();
                for (int i = 0; i < devices.length; i++) {
                    GraphicsConfiguration[] configs = devices[i]
                                                                .getConfigurations();
                    for (int j = 0; j < configs.length; j++) {
                        // Use reflection to call
                        // X11GraphicsConfig.getVisual
                        try {
                            Object o = configs[j].getClass()
                                                 .getMethod("getVisual", null)
                                                 .invoke(configs[j], null);
                            int visual = ((Integer)o).intValue();
                            for (int k = 0; k < alphaVisuals.length; k++) {
                                if (visual == alphaVisuals[k])
                                    return configs[j];
                            }
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            return super.getAlphaCompatibleGraphicsConfiguration();
        }

        /**
         * Return the visual ID of the visual which supports an alpha
         * channel.
         */
        private int[] getAlphaVisuals() {
            X11 x11 = X11.INSTANCE;
            Pointer dpy = x11.XOpenDisplay(null);
            if (dpy == null)
                return new int[0];
            XVisualInfo info = null;
            try {
                int screen = x11.XDefaultScreen(dpy);
                XVisualInfo template = new XVisualInfo();
                template.screen = screen;
                template.depth = 32;
                template.clazz = X11.TrueColor;
                int mask = X11.VisualScreenMask | X11.VisualDepthMask
                    | X11.VisualClassMask;
                IntByReference pcount = new IntByReference();
                info = x11.XGetVisualInfo(dpy, mask, template, pcount);
                if (info != null) {
                    List list = new ArrayList();
                    XVisualInfo[] infos = (XVisualInfo[])
                        info.toArray(new XVisualInfo[pcount.getValue()]);
                    for (int i = 0; i < infos.length; i++) {
                        XRenderPictFormat format = 
                            X11.Xrender.INSTANCE.XRenderFindVisualFormat(dpy,
                                                                         infos[i].visual);
                        if (format.type == X11.Xrender.PictTypeDirect
                            && format.direct.alphaMask != 0) {
                            list.add(new Integer(infos[i].visualID));
                        }
                    }
                    int[] ids = new int[list.size()];
                    for (int i = 0; i < list.size(); i++) {
                        ids[i] = ((Integer)list.get(i)).intValue();
                    }
                    return ids;
                }
            }
            finally {
                if (info != null) {
                    x11.XFree(info.getPointer());
                }
                x11.XCloseDisplay(dpy);
            }
            return new int[0];
        }

        public int getDrawable(Window w) {
            return (int)Native.getWindowID(w);
            /*
            WindowPeer peer = (WindowPeer)w.getPeer(); 
            try { 
                Method m = peer.getClass().getMethod("getWindow", null); 
                return (int)((Long)m.invoke(peer, null)).longValue(); 
            }
            catch(NoSuchMethodException e) { 
                // Method unavailable, fall back to native lookup 
                e.printStackTrace(); 
            }
            catch(Exception e) { 
            } 
            return 0;
            */
        }

        private static final long OPAQUE = 0xFFFFFFFFL;
        private static final String OPACITY = "_NET_WM_WINDOW_OPACITY";

        public void setWindowAlpha(final Window w, final float alpha) {
            Runnable action = new Runnable() {
                public void run() {
                    X11 x11 = X11.INSTANCE;
                    Pointer dpy = x11.XOpenDisplay(null);
                    if (dpy == null)
                        return;
                    try {
                        int win = getDrawable(w);
                        if (alpha == 1f) {
                            x11.XDeleteProperty(dpy, win,
                                                x11.XInternAtom(dpy, OPACITY,
                                                                false));
                        }
                        else {
                            int opacity = (int)((long)(alpha * OPAQUE) & 0xFFFFFFFF);
                            IntByReference patom = new IntByReference(opacity);
                            x11.XChangeProperty(dpy, win,
                                                x11.XInternAtom(dpy, OPACITY,
                                                                false),
                                                X11.XA_CARDINAL, 32,
                                                X11.PropModeReplace,
                                                patom.getPointer(), 1);
                        }
                    }
                    finally {
                        x11.XCloseDisplay(dpy);
                    }
                }
            };
            whenDisplayable(w, action);
        }

        private class X11TransparentContent extends JPanel {
            private boolean transparent;

            public X11TransparentContent(Container oldContent) {
                super(new BorderLayout());
                add(oldContent, BorderLayout.CENTER);
                setTransparent(true);
                if (oldContent instanceof JPanel) {
                    ((JComponent)oldContent).setOpaque(false);
                }
            }

            public void setTransparent(boolean transparent) {
                this.transparent = transparent;
                setOpaque(!transparent);
                repaint();
            }

            public void paint(Graphics gr) {
                if (transparent) {
                    int w = getWidth();
                    int h = getHeight();
                    if (w > 0 && h > 0) {
                        BufferedImage buf =
                            new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
                        Graphics2D g = buf.createGraphics();
                        g.setComposite(AlphaComposite.Clear);
                        g.fillRect(0, 0, w, h);
                        g.setComposite(AlphaComposite.SrcOver);
                        super.paint(g);
                        g = (Graphics2D)gr.create();
                        g.setComposite(AlphaComposite.Src);
                        g.drawImage(buf, 0, 0, w, h, null);
                        g.dispose();
                    }
                }
                else {
                    super.paint(gr);
                }
            }
        }

        public void setWindowTransparent(final Window w,
                                         final boolean transparent) {
            if (!(w instanceof RootPaneContainer)) {
                throw new IllegalArgumentException(
                                                   "Window must be a RootPaneContainer");
            }
            if (!isWindowAlphaSupported()) {
                System.err.println("Window alpha not supported");
                return;
            }
            if (!w.getGraphicsConfiguration()
                  .equals(getAlphaCompatibleGraphicsConfiguration())) {
                throw new IllegalArgumentException(
                                                   "Window GraphicsConfiguration does not support transparency");
            }
            boolean isTransparent = w.getBackground() != null
                && w.getBackground().getAlpha() == 0;
            if (!(transparent ^ isTransparent))
                return;
            whenDisplayable(w, new Runnable() {
                public void run() {
                    JRootPane root = ((RootPaneContainer)w).getRootPane();
                    Container content = root.getContentPane();
                    if (content instanceof X11TransparentContent) {
                        ((X11TransparentContent)content)
                                                     .setTransparent(transparent);
                    }
                    else if (transparent) {
                        root.setContentPane(new X11TransparentContent(content));
                    }
                    setLayersTransparent(w, transparent);
                    setForceHeavyweightPopups(w, transparent);
                }
            });
        }

        public void setWindowMask(final Window w, final Raster raster) {
            Runnable action = new Runnable() {
                public void run() {
                    X11 x11 = X11.INSTANCE;
                    Xext ext = Xext.INSTANCE;
                    Pointer dpy = x11.XOpenDisplay(null);
                    if (dpy == null)
                        return;
                    int pm = X11.None;
                    try {
                        int win = getDrawable(w);
                        if (raster == null 
                            || ((pm = createBitmap(dpy, win, w, raster))
                                == X11.None)) {
                            ext.XShapeCombineMask(dpy, win, 
                                                  X11.Xext.ShapeBounding,
                                                  0, 0, X11.None,
                                                  X11.Xext.ShapeSet);
                        }
                        else {
                            ext.XShapeCombineMask(dpy, win,
                                                  X11.Xext.ShapeBounding, 0, 0,
                                                  pm, X11.Xext.ShapeSet);
                        }
                    }
                    finally {
                        if (pm != X11.None) {
                            x11.XFreePixmap(dpy, pm);
                        }
                        x11.XCloseDisplay(dpy);
                    }
                    setForceHeavyweightPopups(w, raster != null);
                }
            };
            whenDisplayable(w, action);
        }
    }

    /**
     * Applies the given mask to the given window. Does nothing if the
     * operation is not supported.
     */
    public static void setWindowMask(Window w, Shape mask) {
        getInstance().setWindowMask(w, mask);
    }

    /**
     * Applies the given mask to the given window. Does nothing if the
     * operation is not supported.
     */
    public static void setWindowMask(Window w, Icon mask) {
        getInstance().setWindowMask(w, mask);
    }

    /** Indicate a window can have a global alpha setting. */
    public static boolean isWindowAlphaSupported() {
        return getInstance().isWindowAlphaSupported();
    }

    /**
     * Returns a {@link GraphicsConfiguration} comptible with alpha
     * compositing.
     */
    public static GraphicsConfiguration getAlphaCompatibleGraphicsConfiguration() {
        return getInstance().getAlphaCompatibleGraphicsConfiguration();
    }

    /**
     * Set the overall window transparency. An alpha of 1.0 is fully
     * opaque, 0.0 fully transparent. The alpha level is applied equally
     * to all window pixels.
     */
    public static void setWindowAlpha(Window w, float alpha) {
        getInstance().setWindowAlpha(w, Math.max(0f, Math.min(alpha, 1f)));
    }

    /**
     * Set the window to be transparent. Only explicitly painted pixels
     * will be non-transparent. All pixels will be composited with
     * whatever is under the window using their alpha values.
     */
    public static void setWindowTransparent(Window w, boolean transparent) {
        getInstance().setWindowTransparent(w, transparent);
    }
}
