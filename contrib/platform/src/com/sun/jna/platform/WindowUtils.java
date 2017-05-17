/*
 * Copyright (c) 2007-2008 Timothy Wall, All Rights Reserved
 * Parts Copyright (c) 2007 Olivier Chafik
 *
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
package com.sun.jna.platform;

import java.awt.AWTEvent;
import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.AWTEventListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.ContainerEvent;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Area;
import java.awt.geom.PathIterator;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.PopupFactory;
import javax.swing.RootPaneContainer;
import javax.swing.SwingUtilities;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.NativeLong;
import com.sun.jna.Platform;
import com.sun.jna.Pointer;
import com.sun.jna.platform.unix.X11;
import com.sun.jna.platform.unix.X11.Display;
import com.sun.jna.platform.unix.X11.GC;
import com.sun.jna.platform.unix.X11.Pixmap;
import com.sun.jna.platform.unix.X11.XVisualInfo;
import com.sun.jna.platform.unix.X11.Xext;
import com.sun.jna.platform.unix.X11.Xrender.XRenderPictFormat;
import com.sun.jna.platform.win32.GDI32;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.Psapi;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.Win32Exception;
import com.sun.jna.platform.win32.WinDef.DWORDByReference;
import com.sun.jna.platform.win32.WinDef.HBITMAP;
import com.sun.jna.platform.win32.WinDef.HDC;
import com.sun.jna.platform.win32.WinDef.HICON;
import com.sun.jna.platform.win32.WinDef.HRGN;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinDef.LPARAM;
import com.sun.jna.platform.win32.WinDef.LRESULT;
import com.sun.jna.platform.win32.WinDef.POINT;
import com.sun.jna.platform.win32.WinDef.RECT;
import com.sun.jna.platform.win32.WinDef.WPARAM;
import com.sun.jna.platform.win32.WinGDI;
import com.sun.jna.platform.win32.WinGDI.BITMAP;
import com.sun.jna.platform.win32.WinGDI.BITMAPINFO;
import com.sun.jna.platform.win32.WinGDI.BITMAPINFOHEADER;
import com.sun.jna.platform.win32.WinGDI.ICONINFO;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.platform.win32.WinUser;
import com.sun.jna.platform.win32.WinUser.BLENDFUNCTION;
import com.sun.jna.platform.win32.WinUser.SIZE;
import com.sun.jna.platform.win32.WinUser.WNDENUMPROC;
import com.sun.jna.ptr.ByteByReference;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

/**
 * Provides additional features on a Java {@link Window}.
 * <ul>
 * <li>Non-rectangular shape (bitmap mask, no antialiasing)
 * <li>Transparency (constant alpha applied to window contents or
 * transparent background)
 * <li>Fully transparent window (the transparency of all painted pixels is
 * applied to the window).
 * </ul>
 * NOTE: since there is no explicit way to force PopupFactory to use a
 * heavyweight popup, and anything but a heavyweight popup will be
 * clipped by a window mask, an additional subwindow is added to all
 * masked windows to implicitly force PopupFactory to use a heavyweight
 * window and avoid clipping.
 * <p>
 * NOTE: Neither shaped windows nor transparency
 * currently works with Java 1.4 under X11. This is at least partly due
 * to 1.4 using multiple X11 windows for a single given Java window. It
 * *might* be possible to remedy by applying the window
 * region/transparency to all descendants, but I haven't tried it. In
 * addition, windows must be both displayable <em>and</em> visible
 * before the corresponding native Drawable may be obtained; in later
 * Java versions, the window need only be displayable.
 * <p>
 * NOTE: If you use {@link #setWindowMask(Window,Shape)} and override {@link
 * Window#paint(Graphics)} on OS X, you'll need to explicitly set the clip
 * mask on the <code>Graphics</code> object with the window mask; only the
 * content pane of the window and below have the window mask automatically
 * applied.<p>
 * NOTE: On OSX, the property
 * <code>apple.awt.draggableWindowBackground</code> is set automatically when
 * a window's background color has an alpha component.  That property must be
 * set to its final value <em>before</em> the heavyweight peer for the Window
 * is created.  Once {@link Component#addNotify} has been called on the
 * component, causing creation of the heavyweight peer, changing this
 * property has no effect.
 * @see <a href="http://developer.apple.com/technotes/tn2007/tn2196.html#APPLE_AWT_DRAGGABLEWINDOWBACKGROUND">Apple Technote 2007</a>
 *
 * @author Andreas "PAX" L&uuml;ck, onkelpax-git[at]yahoo.de
 */
// TODO: setWindowMask() should accept a threshold; some cases want a
// 50% threshold, some might want zero/non-zero
public class WindowUtils {

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
     * is a fairly simple, lightweight workaround.  Note that, at least as of
     * JDK 1.6, the following do not have the desired effect:<br>
     * <pre><code>
     * ToolTipManager.sharedInstance().setLightWeightPopupEnabled(false);
     * JPopupMenu.setDefaultLightWeightPopupEnabled(false);
     * System.setProperty("JPopupMenu.defaultLWPopupEnabledKey", "false");
     * </code></pre>
     */
    private static class HeavyweightForcer extends Window {
		private static final long serialVersionUID = 1L;
        private final boolean packed;

        public HeavyweightForcer(Window parent) {
            super(parent);
            pack();
            packed = true;
        }

        @Override
        public boolean isVisible() {
            // Only want to be 'visible' once the peer is instantiated
            // via pack; this tricks PopupFactory into using a heavyweight
            // popup to avoid being obscured by this window
            return packed;
        }

        @Override
        public Rectangle getBounds() {
            return getOwner().getBounds();
        }
    }
    /**
     * This can be installed over a {@link JLayeredPane} in order to
     * listen for repaint requests. The content's repaint method will be
     * invoked whenever any part of the ancestor window is repainted.
     */
    protected static class RepaintTrigger extends JComponent {
		private static final long serialVersionUID = 1L;

        protected class Listener
            extends WindowAdapter
            implements ComponentListener, HierarchyListener, AWTEventListener {
            @Override
            public void windowOpened(WindowEvent e) {
                repaint();
            }

            @Override
            public void componentHidden(ComponentEvent e) {}

            @Override
            public void componentMoved(ComponentEvent e) {}

            @Override
            public void componentResized(ComponentEvent e) {
                setSize(getParent().getSize());
                repaint();
            }

            @Override
            public void componentShown(ComponentEvent e) {
                repaint();
            }

            @Override
            public void hierarchyChanged(HierarchyEvent e) {
                repaint();
            }

            @Override
            public void eventDispatched(AWTEvent e) {
                if (e instanceof MouseEvent) {
                    Component src = ((MouseEvent)e).getComponent();
                    if (src != null
                        && SwingUtilities.isDescendingFrom(src, content)) {
                        MouseEvent me = SwingUtilities.convertMouseEvent(src, (MouseEvent)e, content);
                        Component c = SwingUtilities.getDeepestComponentAt(content, me.getX(), me.getY());
                        if (c != null) {
                            setCursor(c.getCursor());
                        }
                    }
                }
            }
        }

        private final Listener listener = createListener();
        private final JComponent content;

        public RepaintTrigger(JComponent content) {
            this.content = content;
        }

        @Override
        public void addNotify() {
            super.addNotify();
            Window w = SwingUtilities.getWindowAncestor(this);
            setSize(getParent().getSize());
            w.addComponentListener(listener);
            w.addWindowListener(listener);
            Toolkit.getDefaultToolkit().addAWTEventListener(listener, AWTEvent.MOUSE_EVENT_MASK|AWTEvent.MOUSE_MOTION_EVENT_MASK);
        }

        @Override
        public void removeNotify() {
            Toolkit.getDefaultToolkit().removeAWTEventListener(listener);
            Window w = SwingUtilities.getWindowAncestor(this);
            w.removeComponentListener(listener);
            w.removeWindowListener(listener);
            super.removeNotify();
        }

        private Rectangle dirty;
        @Override
        protected void paintComponent(Graphics g) {
            Rectangle bounds = g.getClipBounds();
            if (dirty == null || !dirty.contains(bounds)) {
                if (dirty == null) {
                    dirty = bounds;
                }
                else {
                    dirty = dirty.union(bounds);
                }
                content.repaint(dirty);
            }
            else {
                dirty = null;
            }
        }

        protected Listener createListener() {
            return new Listener();
        }
    };

    /** Window utilities with differing native implementations. */
    public static abstract class NativeWindowUtils {
        protected abstract class TransparentContentPane
            extends JPanel implements AWTEventListener {
    		private static final long serialVersionUID = 1L;
            private boolean transparent;
            public TransparentContentPane(Container oldContent) {
                super(new BorderLayout());
                add(oldContent, BorderLayout.CENTER);
                setTransparent(true);
                if (oldContent instanceof JPanel) {
                    ((JComponent)oldContent).setOpaque(false);
                }
            }
            @Override
            public void addNotify() {
                super.addNotify();
                Toolkit.getDefaultToolkit().addAWTEventListener(this, AWTEvent.CONTAINER_EVENT_MASK);
            }
            @Override
            public void removeNotify() {
                Toolkit.getDefaultToolkit().removeAWTEventListener(this);
                super.removeNotify();
            }
            public void setTransparent(boolean transparent) {
                this.transparent = transparent;
                setOpaque(!transparent);
                setDoubleBuffered(!transparent);
                repaint();
            }
            @Override
            public void eventDispatched(AWTEvent e) {
                if (e.getID() == ContainerEvent.COMPONENT_ADDED
                    && SwingUtilities.isDescendingFrom(((ContainerEvent)e).getChild(), this)) {
                    Component child = ((ContainerEvent)e).getChild();
                    NativeWindowUtils.this.setDoubleBuffered(child, false);
                }
            }
            @Override
            public void paint(Graphics gr) {
                if (transparent) {
                    Rectangle r = gr.getClipBounds();
                    final int w = r.width;
                    final int h = r.height;
                    if (getWidth() > 0 && getHeight() > 0) {
                        final BufferedImage buf =
                            new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB_PRE);

                        Graphics2D g = buf.createGraphics();
                        g.setComposite(AlphaComposite.Clear);
                        g.fillRect(0, 0, w, h);
                        g.dispose();

                        g = buf.createGraphics();
                        g.translate(-r.x, -r.y);
                        super.paint(g);
                        g.dispose();

                        paintDirect(buf, r);
                    }
                }
                else {
                    super.paint(gr);
                }
            }
            /** Use the contents of the given BufferedImage to paint directly
             * on this component's ancestor window.
             */
            protected abstract void paintDirect(BufferedImage buf, Rectangle bounds);
        }

        protected Window getWindow(Component c) {
            return c instanceof Window
                ? (Window)c : SwingUtilities.getWindowAncestor(c);
        }
        /**
         * Execute the given action when the given window becomes
         * displayable.
         */
        protected void whenDisplayable(Component w, final Runnable action) {
            if (w.isDisplayable() && (!Holder.requiresVisible || w.isVisible())) {
                action.run();
            }
            else if (Holder.requiresVisible) {
                getWindow(w).addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowOpened(WindowEvent e) {
                        e.getWindow().removeWindowListener(this);
                        action.run();
                    }
                    @Override
                    public void windowClosed(WindowEvent e) {
                        e.getWindow().removeWindowListener(this);
                    }
                });
            }
            else {
                // Hierarchy events are fired in direct response to
                // displayability changes
                w.addHierarchyListener(new HierarchyListener() {
                    @Override
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

        protected Raster toRaster(Shape mask) {
            Raster raster = null;
            if (mask != MASK_NONE) {
                Rectangle bounds = mask.getBounds();
                if (bounds.width > 0 && bounds.height > 0) {
                    BufferedImage clip =
                        new BufferedImage(bounds.x + bounds.width,
                                          bounds.y + bounds.height,
                                          BufferedImage.TYPE_BYTE_BINARY);
                    Graphics2D g = clip.createGraphics();
                    g.setColor(Color.black);
                    g.fillRect(0, 0, bounds.x + bounds.width, bounds.y + bounds.height);
                    g.setColor(Color.white);
                    g.fill(mask);
                    raster = clip.getRaster();
                }
            }
            return raster;
        }

        protected Raster toRaster(Component c, Icon mask) {
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
                mask.paintIcon(c, g, 0, 0);
                raster = clip.getAlphaRaster();
            }
            return raster;
        }

        protected Shape toShape(Raster raster) {
            final Area area = new Area(new Rectangle(0, 0, 0, 0));
            RasterRangesUtils.outputOccupiedRanges(raster, new RasterRangesUtils.RangesOutput() {
                @Override
                public boolean outputRange(int x, int y, int w, int h) {
                    area.add(new Area(new Rectangle(x, y, w, h)));
                    return true;
                }
            });
            return area;
        }

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

        protected void setDoubleBuffered(Component root, boolean buffered) {
            if (root instanceof JComponent) {
                ((JComponent)root).setDoubleBuffered(buffered);
            }
            if (root instanceof JRootPane && buffered) {
                ((JRootPane)root).setDoubleBuffered(true);
            }
            else if (root instanceof Container) {
                Component[] kids = ((Container)root).getComponents();
                for (int i=0;i < kids.length;i++) {
                    setDoubleBuffered(kids[i], buffered);
                }
            }
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
                    lp.putClientProperty(TRANSPARENT_OLD_OPAQUE, Boolean.valueOf(lp.isOpaque()));
                    lp.setOpaque(false);
                    root.putClientProperty(TRANSPARENT_OLD_OPAQUE, Boolean.valueOf(root.isOpaque()));
                    root.setOpaque(false);
                    if (content != null) {
                        content.putClientProperty(TRANSPARENT_OLD_OPAQUE, Boolean.valueOf(content.isOpaque()));
                        content.setOpaque(false);
                    }
                    root.putClientProperty(TRANSPARENT_OLD_BG,
                                           root.getParent().getBackground());
                }
                else {
                    lp.setOpaque(Boolean.TRUE.equals(lp.getClientProperty(TRANSPARENT_OLD_OPAQUE)));
                    lp.putClientProperty(TRANSPARENT_OLD_OPAQUE, null);
                    root.setOpaque(Boolean.TRUE.equals(root.getClientProperty(TRANSPARENT_OLD_OPAQUE)));
                    root.putClientProperty(TRANSPARENT_OLD_OPAQUE, null);
                    if (content != null) {
                        content.setOpaque(Boolean.TRUE.equals(content.getClientProperty(TRANSPARENT_OLD_OPAQUE)));
                        content.putClientProperty(TRANSPARENT_OLD_OPAQUE, null);
                    }
                    bg = (Color)root.getClientProperty(TRANSPARENT_OLD_BG);
                    root.putClientProperty(TRANSPARENT_OLD_BG, null);
                }
            }
            w.setBackground(bg);
        }

        /** Override this method to provide bitmap masking of the given
         * heavyweight component.
         */
        protected void setMask(Component c, Raster raster) {
            throw new UnsupportedOperationException("Window masking is not available");
        }

        /**
         * Set the window mask based on the given Raster, which should
         * be treated as a bitmap (zero/nonzero values only). A value of
         * <code>null</code> means to remove the mask.
         */
        protected void setWindowMask(Component w, Raster raster) {
            if (w.isLightweight())
                throw new IllegalArgumentException("Component must be heavyweight: " + w);
            setMask(w, raster);
        }

        /** Set the window mask based on a {@link Shape}. */
        public void setWindowMask(Component w, Shape mask) {
            setWindowMask(w, toRaster(mask));
        }

        /**
         * Set the window mask based on an Icon. All non-transparent
         * pixels will be included in the mask.
         */
        public void setWindowMask(Component w, Icon mask) {
            setWindowMask(w, toRaster(w, mask));
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
                        if (force)
                            return;
                        owned[i].dispose();
                    }
                }
                Boolean b = Boolean.valueOf(System.getProperty("jna.force_hw_popups", "true"));
                if (force && b.booleanValue()) {
                    new HeavyweightForcer(w);
                }
            }
        }

        /**
         * Obtains the set icon for the window associated with the specified
         * window handle.
         *
         * @param hwnd
         *            The concerning window handle.
         * @return Either the window's icon or {@code null} if an error
         *         occurred.
         *
         * @throws UnsupportedOperationException
         *             Thrown if this method wasn't yet implemented for the
         *             current platform.
         */
        protected BufferedImage getWindowIcon(final HWND hwnd) {
            throw new UnsupportedOperationException("This platform is not supported, yet.");
        }

        /**
         * Detects the size of an icon.
         *
         * @param hIcon
         *            The icon handle type.
         * @return Either the requested icon's dimension or an {@link Dimension}
         *         instance of {@code (0, 0)}.
         *
         * @throws UnsupportedOperationException
         *             Thrown if this method wasn't yet implemented for the
         *             current platform.
         */
        protected Dimension getIconSize(final HICON hIcon) {
            throw new UnsupportedOperationException("This platform is not supported, yet.");
        }

        /**
         * Requests a list of all currently available Desktop windows.
         *
         * @param onlyVisibleWindows
         *            Specifies whether only currently visible windows will be
         *            considered ({@code true}). That are windows which are not
         *            minimized. The {@code WS_VISIBLE} flag will be checked
         *            (see: <a href=
         *            "https://msdn.microsoft.com/de-de/library/windows/desktop/ms633530%28v=vs.85%29.aspx"
         *            >User32.IsWindowVisible(HWND)</a>).
         *
         * @return A list with all windows and some detailed information.
         *
         * @throws UnsupportedOperationException
         *             Thrown if this method wasn't yet implemented for the
         *             current platform.
         */
        protected List<DesktopWindow> getAllWindows(final boolean onlyVisibleWindows) {
            throw new UnsupportedOperationException("This platform is not supported, yet.");
        }

        /**
         * Tries to obtain the Window's title which belongs to the specified
         * window handle.
         *
         * @param hwnd
         *            The concerning window handle.
         * @return Either the title or an empty string of no title was found or
         *         an error occurred.
         *
         * @throws UnsupportedOperationException
         *             Thrown if this method wasn't yet implemented for the
         */
        protected String getWindowTitle(final HWND hwnd) {
            throw new UnsupportedOperationException("This platform is not supported, yet.");
        }

        /**
         * Detects the full file path of the process associated with the specified
         * window handle.
         *
         * @param hwnd
         *            The concerning window handle for which the PE file path is
         *            required.
         * @return The full file path of the PE file that is associated with the
         *         specified window handle.
         *
         * @throws UnsupportedOperationException
         *             Thrown if this method wasn't yet implemented for the
         */
        protected  String getProcessFilePath(final HWND hwnd){
            throw new UnsupportedOperationException("This platform is not supported, yet.");
        }

        /**
         * Requests the location and size of the window associated with the
         * specified window handle.
         *
         * @param hwnd
         *            The concerning window handle.
         * @return The location and size of the window.
         *
         * @throws UnsupportedOperationException
         *             Thrown if this method wasn't yet implemented for the
         */
        protected Rectangle getWindowLocationAndSize(final HWND hwnd) {
            throw new UnsupportedOperationException("This platform is not supported, yet.");
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
            if (Platform.isWindows()) {
                INSTANCE = new W32WindowUtils();
            }
            else if (Platform.isMac()) {
                INSTANCE = new MacWindowUtils();
            }
            else if (Platform.isX11()) {
                INSTANCE = new X11WindowUtils();
                requiresVisible = System.getProperty("java.version")
                                        .matches("^1\\.4\\..*");
            }
            else {
                String os = System.getProperty("os.name");
                throw new UnsupportedOperationException("No support for " + os);
            }
        }
    }

    private static NativeWindowUtils getInstance() {
        return Holder.INSTANCE;
    }

    private static class W32WindowUtils extends NativeWindowUtils {
        private HWND getHWnd(Component w) {
            HWND hwnd = new HWND();
            hwnd.setPointer(Native.getComponentPointer(w));
            return hwnd;
        }

        /**
         * W32 alpha will only work if <code>sun.java2d.noddraw</code>
         * is set
         */
        @Override
        public boolean isWindowAlphaSupported() {
            return Boolean.getBoolean("sun.java2d.noddraw");
        }

        /** Indicates whether UpdateLayeredWindow is in use. */
        private boolean usingUpdateLayeredWindow(Window w) {
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
                Byte b = alpha == (byte)0xFF ? null : Byte.valueOf(alpha);
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

        @Override
        public void setWindowAlpha(final Window w, final float alpha) {
            if (!isWindowAlphaSupported()) {
                throw new UnsupportedOperationException("Set sun.java2d.noddraw=true to enable transparent windows");
            }
            whenDisplayable(w, new Runnable() {
                @Override
                public void run() {
                    HWND hWnd = getHWnd(w);
                    User32 user = User32.INSTANCE;
                    int flags = user.GetWindowLong(hWnd, WinUser.GWL_EXSTYLE);
                    byte level = (byte)((int)(255 * alpha) & 0xFF);
                    if (usingUpdateLayeredWindow(w)) {
                        // If already using UpdateLayeredWindow, continue to
                        // do so
                        BLENDFUNCTION blend = new BLENDFUNCTION();
                        blend.SourceConstantAlpha = level;
                        blend.AlphaFormat = WinUser.AC_SRC_ALPHA;
                        user.UpdateLayeredWindow(hWnd, null, null, null, null,
                                                 null, 0, blend,
                                                 WinUser.ULW_ALPHA);
                    }
                    else if (alpha == 1f) {
                        flags &= ~WinUser.WS_EX_LAYERED;
                        user.SetWindowLong(hWnd, WinUser.GWL_EXSTYLE, flags);
                    }
                    else {
                        flags |= WinUser.WS_EX_LAYERED;
                        user.SetWindowLong(hWnd, WinUser.GWL_EXSTYLE, flags);
                        user.SetLayeredWindowAttributes(hWnd, 0, level,
                        		WinUser.LWA_ALPHA);
                    }
                    setForceHeavyweightPopups(w, alpha != 1f);
                    storeAlpha(w, level);
                }
            });
        }

        /** W32 makes the client responsible for repainting the <em>entire</em>
         * window on any change.  It also does not paint window decorations
         * when the window is transparent.
         */
        private class W32TransparentContentPane extends TransparentContentPane {
    		private static final long serialVersionUID = 1L;
            private HDC memDC;
            private HBITMAP hBitmap;
            private Pointer pbits;
            private Dimension bitmapSize;
            public W32TransparentContentPane(Container content) {
                super(content);
            }
            private void disposeBackingStore() {
                GDI32 gdi = GDI32.INSTANCE;
                if (hBitmap != null) {
                    gdi.DeleteObject(hBitmap);
                    hBitmap = null;
                }
                if (memDC != null) {
                    gdi.DeleteDC(memDC);
                    memDC = null;
                }
            }
            @Override
            public void removeNotify() {
                super.removeNotify();
                disposeBackingStore();
            }
            @Override
            public void setTransparent(boolean transparent) {
                super.setTransparent(transparent);
                if (!transparent) {
                    disposeBackingStore();
                }
            }
            @Override
            protected void paintDirect(BufferedImage buf, Rectangle bounds) {
                // TODO: paint frame decoration if window is decorated
                Window win = SwingUtilities.getWindowAncestor(this);
                GDI32 gdi = GDI32.INSTANCE;
                User32 user = User32.INSTANCE;
                int x = bounds.x;
                int y = bounds.y;
                Point origin = SwingUtilities.convertPoint(this, x, y, win);
                int w = bounds.width;
                int h = bounds.height;
                int ww = win.getWidth();
                int wh = win.getHeight();
                HDC screenDC = user.GetDC(null);
                HANDLE oldBitmap = null;
                try {
                    if (memDC == null) {
                        memDC = gdi.CreateCompatibleDC(screenDC);
                    }
                    if (hBitmap == null || !win.getSize().equals(bitmapSize)) {
                        if (hBitmap != null) {
                            gdi.DeleteObject(hBitmap);
                            hBitmap = null;
                        }
                        BITMAPINFO bmi = new BITMAPINFO();
                        bmi.bmiHeader.biWidth = ww;
                        bmi.bmiHeader.biHeight = wh;
                        bmi.bmiHeader.biPlanes = 1;
                        bmi.bmiHeader.biBitCount = 32;
                        bmi.bmiHeader.biCompression = WinGDI.BI_RGB;
                        bmi.bmiHeader.biSizeImage = ww * wh * 4;
                        PointerByReference ppbits = new PointerByReference();
                        hBitmap = gdi.CreateDIBSection(memDC, bmi,
                        		WinGDI.DIB_RGB_COLORS,
                        		ppbits, null, 0);
                        pbits = ppbits.getValue();
                        bitmapSize = new Dimension(ww, wh);
                    }
                    oldBitmap = gdi.SelectObject(memDC, hBitmap);
                    Raster raster = buf.getData();
                    int[] pixel = new int[4];
                    int[] bits = new int[w];
                    for (int row = 0; row < h; row++) {
                        for (int col = 0; col < w; col++) {
                            raster.getPixel(col, row, pixel);
                            int alpha = (pixel[3] & 0xFF) << 24;
                            int red = (pixel[2] & 0xFF);
                            int green = (pixel[1] & 0xFF) << 8;
                            int blue = (pixel[0] & 0xFF) << 16;
                            bits[col] = alpha | red | green | blue;
                        }
                        int v = wh - (origin.y + row) - 1;
                        pbits.write((v*ww+origin.x)*4, bits, 0, bits.length);
                    }
                    SIZE winSize = new SIZE();
                    winSize.cx = win.getWidth();
                    winSize.cy = win.getHeight();
                    POINT winLoc = new POINT();
                    winLoc.x = win.getX();
                    winLoc.y = win.getY();
                    POINT srcLoc = new POINT();
                    BLENDFUNCTION blend = new BLENDFUNCTION();
                    HWND hWnd = getHWnd(win);
                    // extract current constant alpha setting, if possible
                    ByteByReference bref = new ByteByReference();
                    IntByReference iref = new IntByReference();
                    byte level = getAlpha(win);
                    try {
                        // GetLayeredwindowAttributes supported WinXP and later
                        if (user.GetLayeredWindowAttributes(hWnd, null, bref, iref)
                            && (iref.getValue() & WinUser.LWA_ALPHA) != 0) {
                            level = bref.getValue();
                        }
                    }
                    catch(UnsatisfiedLinkError e) {
                    }
                    blend.SourceConstantAlpha = level;
                    blend.AlphaFormat = WinUser.AC_SRC_ALPHA;
                    user.UpdateLayeredWindow(hWnd, screenDC, winLoc, winSize, memDC,
                                             srcLoc, 0, blend, WinUser.ULW_ALPHA);
                } finally {
                    user.ReleaseDC(null, screenDC);
                    if (memDC != null && oldBitmap != null) {
                        gdi.SelectObject(memDC, oldBitmap);
                    }
                }
            }
        }

        /** Note that w32 does <em>not</em> paint window decorations when
         * the window is transparent.
         */
        @Override
        public void setWindowTransparent(final Window w,
                                         final boolean transparent) {
            if (!(w instanceof RootPaneContainer)) {
                throw new IllegalArgumentException("Window must be a RootPaneContainer");
            }
            if (!isWindowAlphaSupported()) {
                throw new UnsupportedOperationException("Set sun.java2d.noddraw=true to enable transparent windows");
            }
            boolean isTransparent = w.getBackground() != null
                && w.getBackground().getAlpha() == 0;
            if (transparent == isTransparent)
                return;
            whenDisplayable(w, new Runnable() {
                @Override
                public void run() {
                    User32 user = User32.INSTANCE;
                    HWND hWnd = getHWnd(w);
                    int flags = user.GetWindowLong(hWnd, WinUser.GWL_EXSTYLE);
                    JRootPane root = ((RootPaneContainer)w).getRootPane();
                    JLayeredPane lp = root.getLayeredPane();
                    Container content = root.getContentPane();
                    if (content instanceof W32TransparentContentPane) {
                        ((W32TransparentContentPane)content).setTransparent(transparent);
                    }
                    else if (transparent) {
                        W32TransparentContentPane w32content =
                            new W32TransparentContentPane(content);
                        root.setContentPane(w32content);
                        lp.add(new RepaintTrigger(w32content),
                               JLayeredPane.DRAG_LAYER);
                    }
                    if (transparent && !usingUpdateLayeredWindow(w)) {
                        flags |= WinUser.WS_EX_LAYERED;
                        user.SetWindowLong(hWnd, WinUser.GWL_EXSTYLE, flags);
                    }
                    else if (!transparent && usingUpdateLayeredWindow(w)) {
                        flags &= ~WinUser.WS_EX_LAYERED;
                        user.SetWindowLong(hWnd, WinUser.GWL_EXSTYLE, flags);
                    }
                    setLayersTransparent(w, transparent);
                    setForceHeavyweightPopups(w, transparent);
                    setDoubleBuffered(w, !transparent);
                }
            });
        }

        @Override
        public void setWindowMask(final Component w, final Shape mask) {
            if (mask instanceof Area && ((Area)mask).isPolygonal()) {
                setMask(w, (Area)mask);
            }
            else {
                super.setWindowMask(w, mask);
            }
        }

        // NOTE: Deletes hrgn after setting the window region
        private void setWindowRegion(final Component w, final HRGN hrgn) {
            whenDisplayable(w, new Runnable() {
                @Override
                public void run() {
                    GDI32 gdi = GDI32.INSTANCE;
                    User32 user = User32.INSTANCE;
                    HWND hWnd = getHWnd(w);
                    try {
                        user.SetWindowRgn(hWnd, hrgn, true);
                        setForceHeavyweightPopups(getWindow(w), hrgn != null);
                    }
                    finally {
                        gdi.DeleteObject(hrgn);
                    }
                }
            });
        }

        // Take advantage of CreatePolyPolygonalRgn on w32
        private void setMask(final Component w, final Area area) {
            GDI32 gdi = GDI32.INSTANCE;
            PathIterator pi = area.getPathIterator(null);
            int mode = pi.getWindingRule() == PathIterator.WIND_NON_ZERO
                ? WinGDI.WINDING: WinGDI.ALTERNATE;
            float[] coords = new float[6];
            List<POINT> points = new ArrayList<POINT>();
            int size = 0;
            List<Integer> sizes = new ArrayList<Integer>();
            while (!pi.isDone()) {
                int type = pi.currentSegment(coords);
                if (type == PathIterator.SEG_MOVETO) {
                    size = 1;
                    points.add(new POINT((int)coords[0], (int)coords[1]));
                }
                else if (type == PathIterator.SEG_LINETO) {
                    ++size;
                    points.add(new POINT((int)coords[0], (int)coords[1]));
                }
                else if (type == PathIterator.SEG_CLOSE) {
                    sizes.add(Integer.valueOf(size));
                }
                else {
                    throw new RuntimeException("Area is not polygonal: " + area);
                }
                pi.next();
            }
            POINT[] lppt = (POINT[])new POINT().toArray(points.size());
            POINT[] pts = points.toArray(new POINT[points.size()]);
            for (int i=0;i < lppt.length;i++) {
                lppt[i].x = pts[i].x;
                lppt[i].y = pts[i].y;
            }
            int[] counts = new int[sizes.size()];
            for (int i=0;i < counts.length;i++) {
                counts[i] = sizes.get(i).intValue();
            }
            HRGN hrgn = gdi.CreatePolyPolygonRgn(lppt, counts, counts.length, mode);
            setWindowRegion(w, hrgn);
        }

        @Override
        protected void setMask(final Component w, final Raster raster) {
            GDI32 gdi = GDI32.INSTANCE;
            final HRGN region = raster != null
                ? gdi.CreateRectRgn(0, 0, 0, 0) : null;
            if (region != null) {
                final HRGN tempRgn = gdi.CreateRectRgn(0, 0, 0, 0);
                try {
                    RasterRangesUtils.outputOccupiedRanges(raster, new RasterRangesUtils.RangesOutput() {
                        @Override
                        public boolean outputRange(int x, int y, int w, int h) {
                            GDI32 gdi = GDI32.INSTANCE;
                            gdi.SetRectRgn(tempRgn, x, y, x + w, y + h);
                            return gdi.CombineRgn(region, region, tempRgn, WinGDI.RGN_OR) != WinGDI.ERROR;
                        }
                    });
                }
                finally {
                    gdi.DeleteObject(tempRgn);
                }
            }
            setWindowRegion(w, region);
        }

        @Override
        public BufferedImage getWindowIcon(final HWND hwnd) {
            // request different kind of icons if any solution fails
            final DWORDByReference hIconNumber = new DWORDByReference();
            LRESULT result = User32.INSTANCE
                .SendMessageTimeout(hwnd,
                                    WinUser.WM_GETICON,
                                    new WPARAM(WinUser.ICON_BIG),
                                    new LPARAM(0),
                                    WinUser.SMTO_ABORTIFHUNG, 500, hIconNumber);
            if (result.intValue() == 0)
                result = User32.INSTANCE
                    .SendMessageTimeout(hwnd,
                                        WinUser.WM_GETICON,
                                        new WPARAM(WinUser.ICON_SMALL),
                                        new LPARAM(0),
                                        WinUser.SMTO_ABORTIFHUNG, 500, hIconNumber);
            if (result.intValue() == 0)
                result = User32.INSTANCE
                    .SendMessageTimeout(hwnd,
                                        WinUser.WM_GETICON,
                                        new WPARAM(WinUser.ICON_SMALL2),
                                        new LPARAM(0),
                                        WinUser.SMTO_ABORTIFHUNG, 500, hIconNumber);
            if (result.intValue() == 0) {
                result = new LRESULT(User32.INSTANCE
                                     .GetClassLongPtr(hwnd,
                                                      WinUser.GCLP_HICON).intValue());
                hIconNumber.getValue().setValue(result.intValue());
            }
            if (result.intValue() == 0) {
                result = new LRESULT(User32.INSTANCE
                                     .GetClassLongPtr(hwnd,
                                                      WinUser.GCLP_HICONSM).intValue());
                hIconNumber.getValue().setValue(result.intValue());
            }
            if (result.intValue() == 0)
                return null;

            // draw native icon into Java image
            final HICON hIcon = new HICON(new Pointer(hIconNumber.getValue()
                                                      .longValue()));
            final Dimension iconSize = getIconSize(hIcon);
            if (iconSize.width == 0 || iconSize.height == 0)
                return null;

            final int width = iconSize.width;
            final int height = iconSize.height;
            final short depth = 24;

            final byte[] lpBitsColor = new byte[width * height * depth / 8];
            final Pointer lpBitsColorPtr = new Memory(lpBitsColor.length);
            final byte[] lpBitsMask = new byte[width * height * depth / 8];
            final Pointer lpBitsMaskPtr = new Memory(lpBitsMask.length);
            final BITMAPINFO bitmapInfo = new BITMAPINFO();
            final BITMAPINFOHEADER hdr = new BITMAPINFOHEADER();

            bitmapInfo.bmiHeader = hdr;
            hdr.biWidth = width;
            hdr.biHeight = height;
            hdr.biPlanes = 1;
            hdr.biBitCount = depth;
            hdr.biCompression = 0;
            hdr.write();
            bitmapInfo.write();

            final HDC hDC = User32.INSTANCE.GetDC(null);
            final ICONINFO iconInfo = new ICONINFO();
            User32.INSTANCE.GetIconInfo(hIcon, iconInfo);
            iconInfo.read();
            GDI32.INSTANCE.GetDIBits(hDC, iconInfo.hbmColor, 0, height,
                                     lpBitsColorPtr, bitmapInfo, 0);
            lpBitsColorPtr.read(0, lpBitsColor, 0, lpBitsColor.length);
            GDI32.INSTANCE.GetDIBits(hDC, iconInfo.hbmMask, 0, height,
                                     lpBitsMaskPtr, bitmapInfo, 0);
            lpBitsMaskPtr.read(0, lpBitsMask, 0, lpBitsMask.length);
            final BufferedImage image = new BufferedImage(width, height,
                                                          BufferedImage.TYPE_INT_ARGB);

            int r, g, b, a, argb;
            int x = 0, y = height - 1;
            for (int i = 0; i < lpBitsColor.length; i = i + 3) {
                b = lpBitsColor[i] & 0xFF;
                g = lpBitsColor[i + 1] & 0xFF;
                r = lpBitsColor[i + 2] & 0xFF;
                a = 0xFF - lpBitsMask[i] & 0xFF;
                argb = (a << 24) | (r << 16) | (g << 8) | b;
                image.setRGB(x, y, argb);
                x = (x + 1) % width;
                if (x == 0)
                    y--;
            }

            User32.INSTANCE.ReleaseDC(null, hDC);

            return image;
        }

        @Override
        public Dimension getIconSize(final HICON hIcon) {
            final ICONINFO iconInfo = new ICONINFO();
            try {
                if (!User32.INSTANCE.GetIconInfo(hIcon, iconInfo))
                    return new Dimension();
                iconInfo.read();

                final BITMAP bmp = new BITMAP();
                if (iconInfo.hbmColor != null
                    && iconInfo.hbmColor.getPointer() != Pointer.NULL) {
                    final int nWrittenBytes = GDI32.INSTANCE.GetObject(
                                                                       iconInfo.hbmColor, bmp.size(), bmp.getPointer());
                    bmp.read();
                    if (nWrittenBytes > 0)
                        return new Dimension(bmp.bmWidth.intValue(),
                                             bmp.bmHeight.intValue());
                } else if (iconInfo.hbmMask != null
                           && iconInfo.hbmMask.getPointer() != Pointer.NULL) {
                    final int nWrittenBytes = GDI32.INSTANCE.GetObject(
                                                                       iconInfo.hbmMask, bmp.size(), bmp.getPointer());
                    bmp.read();
                    if (nWrittenBytes > 0)
                        return new Dimension(bmp.bmWidth.intValue(), bmp.bmHeight.intValue() / 2);
                }
            } finally {
                if (iconInfo.hbmColor != null
                    && iconInfo.hbmColor.getPointer() != Pointer.NULL)
                    GDI32.INSTANCE.DeleteObject(iconInfo.hbmColor);
                if (iconInfo.hbmMask != null
                    && iconInfo.hbmMask.getPointer() != Pointer.NULL)
                    GDI32.INSTANCE.DeleteObject(iconInfo.hbmMask);
            }

            return new Dimension();
        }

        @Override
        public List<DesktopWindow> getAllWindows(final boolean onlyVisibleWindows) {
            final List<DesktopWindow> result = new LinkedList<DesktopWindow>();

            final WNDENUMPROC lpEnumFunc = new WNDENUMPROC() {
                @Override
                public boolean callback(final HWND hwnd, final Pointer arg1) {
                    try {
                        final boolean visible = !onlyVisibleWindows
                            || User32.INSTANCE.IsWindowVisible(hwnd);
                        if (visible) {
                            final String title = getWindowTitle(hwnd);
                            final String filePath = getProcessFilePath(hwnd);
                            final Rectangle locAndSize = getWindowLocationAndSize(hwnd);
                            result.add(new DesktopWindow(hwnd, title, filePath,
                                                         locAndSize));
                        }
                    } catch (final Exception e) {
                        // FIXME properly handle whatever error is raised
                        e.printStackTrace();
                    }

                    return true;
                }
            };

            if (!User32.INSTANCE.EnumWindows(lpEnumFunc, null))
                throw new Win32Exception(Kernel32.INSTANCE.GetLastError());

            return result;
        }

        @Override
        public String getWindowTitle(final HWND hwnd) {
            final int requiredLength = User32.INSTANCE
                .GetWindowTextLength(hwnd) + 1;
            final char[] title = new char[requiredLength];
            final int length = User32.INSTANCE.GetWindowText(hwnd, title,
                                                             title.length);

            return Native.toString(Arrays.copyOfRange(title, 0, length));
        }

        @Override
        public String getProcessFilePath(final HWND hwnd) {
            final char[] filePath = new char[2048];
            final IntByReference pid = new IntByReference();
            User32.INSTANCE.GetWindowThreadProcessId(hwnd, pid);

            final HANDLE process = Kernel32.INSTANCE.OpenProcess(WinNT.PROCESS_QUERY_INFORMATION | WinNT.PROCESS_VM_READ,
                                                                 false, pid.getValue());
            if (process == null
                && Kernel32.INSTANCE.GetLastError() != WinNT.ERROR_ACCESS_DENIED) {
                throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
            }
            final int length = Psapi.INSTANCE.GetModuleFileNameExW(process,
                                                                   null, filePath, filePath.length);
            if (length == 0
                && Kernel32.INSTANCE.GetLastError() != WinNT.ERROR_INVALID_HANDLE) {
                throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
            }
            return Native.toString(filePath).trim();
        }

        @Override
        public Rectangle getWindowLocationAndSize(final HWND hwnd) {
            final RECT lpRect = new RECT();
            if (!User32.INSTANCE.GetWindowRect(hwnd, lpRect))
                throw new Win32Exception(Kernel32.INSTANCE.GetLastError());

            return new Rectangle(lpRect.left, lpRect.top, Math.abs(lpRect.right
                                                                   - lpRect.left), Math.abs(lpRect.bottom - lpRect.top));
        }
    }

    private static class MacWindowUtils extends NativeWindowUtils {
        @Override
        public boolean isWindowAlphaSupported() {
            return true;
        }

        private OSXMaskingContentPane installMaskingPane(Window w) {
            OSXMaskingContentPane content;
            if (w instanceof RootPaneContainer) {
                // TODO: replace layered pane instead?
                final RootPaneContainer rpc = (RootPaneContainer)w;
                Container oldContent = rpc.getContentPane();
                if (oldContent instanceof OSXMaskingContentPane) {
                    content = (OSXMaskingContentPane)oldContent;
                }
                else {
                    content = new OSXMaskingContentPane(oldContent);
                    // TODO: listen for content pane changes
                    rpc.setContentPane(content);
                }
            }
            else {
                Component oldContent = w.getComponentCount() > 0 ? w.getComponent(0) : null;
                if (oldContent instanceof OSXMaskingContentPane) {
                    content = (OSXMaskingContentPane)oldContent;
                }
                else {
                    content = new OSXMaskingContentPane(oldContent);
                    w.add(content);
                }
            }
            return content;
        }

        /** Note that the property
         * <code>apple.awt.draggableWindowBackground</code> must be set to its
         * final value <em>before</em> the heavyweight peer for the Window is
         * created.  Once {@link Component#addNotify} has been called on the
         * component, causing creation of the heavyweight peer, changing this
         * property has no effect.
         * @see <a href="http://developer.apple.com/technotes/tn2007/tn2196.html#APPLE_AWT_DRAGGABLEWINDOWBACKGROUND">Apple Technote 2007</a>
         */
        @Override
        public void setWindowTransparent(Window w, boolean transparent) {
            boolean isTransparent = w.getBackground() != null
                && w.getBackground().getAlpha() == 0;
            if (transparent != isTransparent) {
                setBackgroundTransparent(w, transparent, "setWindowTransparent");
            }
        }

        /** Setting this false restores the original setting. */
        private static final String WDRAG = "apple.awt.draggableWindowBackground";
        private void fixWindowDragging(Window w, String context) {
            if (w instanceof RootPaneContainer) {
                JRootPane p = ((RootPaneContainer)w).getRootPane();
                Boolean oldDraggable = (Boolean)p.getClientProperty(WDRAG);
                if (oldDraggable == null) {
                    p.putClientProperty(WDRAG, Boolean.FALSE);
                    if (w.isDisplayable()) {
                        System.err.println(context + "(): To avoid content dragging, " + context + "() must be called before the window is realized, or " + WDRAG + " must be set to Boolean.FALSE before the window is realized.  If you really want content dragging, set " + WDRAG + " on the window's root pane to Boolean.TRUE before calling " + context + "() to hide this message.");
                    }
                }
            }
        }

        /** Note that the property
         * <code>apple.awt.draggableWindowBackground</code> must be set to its
         * final value <em>before</em> the heavyweight peer for the Window is
         * created.  Once {@link Component#addNotify} has been called on the
         * component, causing creation of the heavyweight peer, changing this
         * property has no effect.
         * @see <a href="http://developer.apple.com/technotes/tn2007/tn2196.html#APPLE_AWT_DRAGGABLEWINDOWBACKGROUND">Apple Technote 2007</a>
         */
        @Override
        public void setWindowAlpha(final Window w, final float alpha) {
            if (w instanceof RootPaneContainer) {
                JRootPane p = ((RootPaneContainer)w).getRootPane();
                p.putClientProperty("Window.alpha", Float.valueOf(alpha));
                fixWindowDragging(w, "setWindowAlpha");
            }
            whenDisplayable(w, new Runnable() {
				@Override
                public void run() {
                    try {
                        // This will work with old Apple AWT implementations and
                        // not with openjdk
                        Method getPeer = w.getClass().getMethod("getPeer");
                        Object peer = getPeer.invoke(w);
                        Method setAlpha = peer.getClass().getMethod("setAlpha", new Class[]{ float.class });
                        setAlpha.invoke(peer, Float.valueOf(alpha));
                    }
                    catch (Exception e) {
                    }
                }
            });
        }

        @Override
        protected void setWindowMask(Component w, Raster raster) {
            if (raster != null) {
                setWindowMask(w, toShape(raster));
            }
            else {
                setWindowMask(w, new Rectangle(0, 0, w.getWidth(),
                                               w.getHeight()));
            }
        }

        @Override
        public void setWindowMask(Component c, final Shape shape) {
            if (c instanceof Window) {
                Window w = (Window)c;
                OSXMaskingContentPane content = installMaskingPane(w);
                content.setMask(shape);
                setBackgroundTransparent(w, shape != MASK_NONE, "setWindowMask");
            }
            else {
                // not yet implemented
            }
        }

        /** Mask out unwanted pixels and ensure background gets cleared.
         * @author Olivier Chafik
         */
        private static class OSXMaskingContentPane extends JPanel {
    		private static final long serialVersionUID = 1L;
            private Shape shape;

            public OSXMaskingContentPane(Component oldContent) {
                super(new BorderLayout());
                if (oldContent != null) {
                    add(oldContent, BorderLayout.CENTER);
                }
            }

            public void setMask(Shape shape) {
                this.shape = shape;
                repaint();
            }

            @Override
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

        private void setBackgroundTransparent(Window w, boolean transparent, String context) {
            JRootPane rp = w instanceof RootPaneContainer
                ? ((RootPaneContainer)w).getRootPane() : null;
            if (transparent) {
                if (rp != null) {
                    rp.putClientProperty(TRANSPARENT_OLD_BG, w.getBackground());
                }
                w.setBackground(new Color(0,0,0,0));
            }
            else {
                if (rp != null) {
                    Color bg = (Color)rp.getClientProperty(TRANSPARENT_OLD_BG);
                    // If the old bg is a
                    // apple.laf.CColorPaintUIResource, the window's
                    // transparent state will not change
                    if (bg != null) {
                        bg = new Color(bg.getRed(), bg.getGreen(), bg.getBlue(), bg.getAlpha());
                    }
                    w.setBackground(bg);
                    rp.putClientProperty(TRANSPARENT_OLD_BG, null);
                }
                else {
                    w.setBackground(null);
                }
            }
            fixWindowDragging(w, context);
        }
    }
    private static class X11WindowUtils extends NativeWindowUtils {
        private static Pixmap createBitmap(final Display dpy,
                                           X11.Window win,
                                           Raster raster) {
            final X11 x11 = X11.INSTANCE;
            Rectangle bounds = raster.getBounds();
            int width = bounds.x + bounds.width;
            int height = bounds.y + bounds.height;
            final Pixmap pm = x11.XCreatePixmap(dpy, win, width, height, 1);
            final GC gc = x11.XCreateGC(dpy, pm, new NativeLong(0), null);
            if (gc == null) {
                return null;
            }
            x11.XSetForeground(dpy, gc, new NativeLong(0));
            x11.XFillRectangle(dpy, pm, gc, 0, 0, width, height);
            final List<Rectangle> rlist = new ArrayList<Rectangle>();
            try {
                RasterRangesUtils.outputOccupiedRanges(raster, new RasterRangesUtils.RangesOutput() {
                    @Override
                    public boolean outputRange(int x, int y, int w, int h) {
                        rlist.add(new Rectangle(x, y, w, h));
                        return true;
                    }
                });
                X11.XRectangle[] rects = (X11.XRectangle[])
                    new X11.XRectangle().toArray(rlist.size());
                for (int i=0;i < rects.length;i++) {
                    Rectangle r = rlist.get(i);
                    rects[i].x = (short)r.x;
                    rects[i].y = (short)r.y;
                    rects[i].width = (short)r.width;
                    rects[i].height = (short)r.height;
                    // Optimization: write directly to native memory
                    Pointer p = rects[i].getPointer();
                    p.setShort(0, (short)r.x);
                    p.setShort(2, (short)r.y);
                    p.setShort(4, (short)r.width);
                    p.setShort(6, (short)r.height);
                    rects[i].setAutoSynch(false);
                    // End optimization
                }
                final int UNMASKED = 1;
                x11.XSetForeground(dpy, gc, new NativeLong(UNMASKED));
                x11.XFillRectangles(dpy, pm, gc, rects, rects.length);
            }
            finally {
                x11.XFreeGC(dpy, gc);
            }
            return pm;
        }

        private boolean didCheck;
        private long[] alphaVisualIDs = {};

        @Override
        public boolean isWindowAlphaSupported() {
            return getAlphaVisualIDs().length > 0;
        }

        private static long getVisualID(GraphicsConfiguration config) {
            // Use reflection to call
            // X11GraphicsConfig.getVisual
            try {
                Object o = config.getClass()
                    .getMethod("getVisual", (Class[])null)
                    .invoke(config, (Object[])null);
                return ((Number)o).longValue();
            }
            catch (Exception e) {
                // FIXME properly handle this error
                e.printStackTrace();
                return -1;
            }
        }

        /** Return the default graphics configuration. */
        @Override
        public GraphicsConfiguration getAlphaCompatibleGraphicsConfiguration() {
            if (isWindowAlphaSupported()) {
                GraphicsEnvironment env =
                    GraphicsEnvironment.getLocalGraphicsEnvironment();
                GraphicsDevice[] devices = env.getScreenDevices();
                for (int i = 0; i < devices.length; i++) {
                    GraphicsConfiguration[] configs =
                        devices[i].getConfigurations();
                    for (int j = 0; j < configs.length; j++) {
                        long visualID = getVisualID(configs[j]);
                        long[] ids = getAlphaVisualIDs();
                        for (int k = 0; k < ids.length; k++) {
                            if (visualID == ids[k]) {
                                return configs[j];
                            }
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
        private synchronized long[] getAlphaVisualIDs() {
            if (didCheck) {
                return alphaVisualIDs;
            }
            didCheck = true;
            X11 x11 = X11.INSTANCE;
            Display dpy = x11.XOpenDisplay(null);
            if (dpy == null)
                return alphaVisualIDs;
            XVisualInfo info = null;
            try {
                int screen = x11.XDefaultScreen(dpy);
                XVisualInfo template = new XVisualInfo();
                template.screen = screen;
                template.depth = 32;
                template.c_class = X11.TrueColor;
                NativeLong mask = new NativeLong(X11.VisualScreenMask
                                                 | X11.VisualDepthMask
                                                 | X11.VisualClassMask);
                IntByReference pcount = new IntByReference();
                info = x11.XGetVisualInfo(dpy, mask, template, pcount);
                if (info != null) {
                    List<X11.VisualID> list = new ArrayList<X11.VisualID>();
                    XVisualInfo[] infos =
                        (XVisualInfo[])info.toArray(pcount.getValue());
                    for (int i = 0; i < infos.length; i++) {
                        XRenderPictFormat format =
                            X11.Xrender.INSTANCE.XRenderFindVisualFormat(dpy,
                                                                         infos[i].visual);
                        if (format.type == X11.Xrender.PictTypeDirect
                            && format.direct.alphaMask != 0) {
                            list.add(infos[i].visualid);
                        }
                    }
                    alphaVisualIDs = new long[list.size()];
                    for (int i=0;i < alphaVisualIDs.length;i++) {
                        alphaVisualIDs[i] = ((Number)list.get(i)).longValue();
                    }
                    return alphaVisualIDs;
                }
            }
            finally {
                if (info != null) {
                    x11.XFree(info.getPointer());
                }
                x11.XCloseDisplay(dpy);
            }
            return alphaVisualIDs;
        }

        private static X11.Window getContentWindow(Window w, X11.Display dpy,
                                                   X11.Window win, Point offset) {
            if ((w instanceof Frame && !((Frame)w).isUndecorated())
                || (w instanceof Dialog && !((Dialog)w).isUndecorated())) {
                X11 x11 = X11.INSTANCE;
                X11.WindowByReference rootp = new X11.WindowByReference();
                X11.WindowByReference parentp = new X11.WindowByReference();
                PointerByReference childrenp = new PointerByReference();
                IntByReference countp = new IntByReference();
                x11.XQueryTree(dpy, win, rootp, parentp, childrenp, countp);
                Pointer p = childrenp.getValue();
                int[] ids = p.getIntArray(0, countp.getValue());
                for (int id : ids) {
                    // TODO: more verification of correct window?
                    X11.Window child = new X11.Window(id);
                    X11.XWindowAttributes xwa = new X11.XWindowAttributes();
                    x11.XGetWindowAttributes(dpy, child, xwa);
                    offset.x = -xwa.x;
                    offset.y = -xwa.y;
                    win = child;
                    break;
                }
                if (p != null) {
                    x11.XFree(p);
                }
            }
            return win;
        }

        private static X11.Window getDrawable(Component w) {
            int id = (int)Native.getComponentID(w);
            if (id == X11.None)
                return null;
            return new X11.Window(id);
        }

        private static final long OPAQUE = 0xFFFFFFFFL;
        private static final String OPACITY = "_NET_WM_WINDOW_OPACITY";

        @Override
        public void setWindowAlpha(final Window w, final float alpha) {
            if (!isWindowAlphaSupported()) {
                throw new UnsupportedOperationException("This X11 display does not provide a 32-bit visual");
            }
            Runnable action = new Runnable() {
                @Override
                public void run() {
                    X11 x11 = X11.INSTANCE;
                    Display dpy = x11.XOpenDisplay(null);
                    if (dpy == null)
                        return;
                    try {
                        X11.Window win = getDrawable(w);
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

        private class X11TransparentContentPane extends TransparentContentPane {
    		private static final long serialVersionUID = 1L;

            public X11TransparentContentPane(Container oldContent) {
                super(oldContent);
            }

            private Memory buffer;
            private int[] pixels;
            private final int[] pixel = new int[4];
            // Painting directly to the original Graphics
            // fails to properly composite unless the destination
            // is pure black.  Too bad.
			@Override
            protected void paintDirect(BufferedImage buf, Rectangle bounds) {
                Window window = SwingUtilities.getWindowAncestor(this);
                X11 x11 = X11.INSTANCE;
                X11.Display dpy = x11.XOpenDisplay(null);
                X11.Window win = getDrawable(window);
                Point offset = new Point();
                win = getContentWindow(window, dpy, win, offset);
                X11.GC gc = x11.XCreateGC(dpy, win, new NativeLong(0), null);

                Raster raster = buf.getData();
                int w = bounds.width;
                int h = bounds.height;
                if (buffer == null || buffer.size() != w*h*4) {
                    buffer = new Memory(w*h*4);
                    pixels = new int[w*h];
                }
                for (int y=0;y<h;y++) {
                    for (int x=0;x < w;x++) {
                        raster.getPixel(x, y, pixel);
                        int alpha = pixel[3]&0xFF;
                        int red = pixel[2]&0xFF;
                        int green = pixel[1]&0xFF;
                        int blue = pixel[0]&0xFF;
                        // TODO: use visual RGB masks to position bits
                        // This layout (ABGR) works empirically
                        pixels[y*w + x] = (alpha<<24)|(blue<<16)|(green<<8)|red;
                    }
                }
                X11.XWindowAttributes xwa = new X11.XWindowAttributes();
                x11.XGetWindowAttributes(dpy, win, xwa);
                X11.XImage image =
                    x11.XCreateImage(dpy, xwa.visual, 32, X11.ZPixmap,
                                     0, buffer, w, h, 32, w * 4);
                buffer.write(0, pixels, 0, pixels.length);
                offset.x += bounds.x;
                offset.y += bounds.y;
                x11.XPutImage(dpy, win, gc, image, 0, 0, offset.x, offset.y, w, h);

                x11.XFree(image.getPointer());
                x11.XFreeGC(dpy, gc);
                x11.XCloseDisplay(dpy);
            }
        }

        @Override
        public void setWindowTransparent(final Window w,
                                         final boolean transparent) {
            if (!(w instanceof RootPaneContainer)) {
                throw new IllegalArgumentException("Window must be a RootPaneContainer");
            }
            if (!isWindowAlphaSupported()) {
                throw new UnsupportedOperationException("This X11 display does not provide a 32-bit visual");
            }
            if (!w.getGraphicsConfiguration()
                .equals(getAlphaCompatibleGraphicsConfiguration())) {
                throw new IllegalArgumentException("Window GraphicsConfiguration '" + w.getGraphicsConfiguration() + "' does not support transparency");
            }
            boolean isTransparent = w.getBackground() != null
                && w.getBackground().getAlpha() == 0;
            if (transparent == isTransparent)
                return;
            whenDisplayable(w, new Runnable() {
                @Override
                public void run() {
                    JRootPane root = ((RootPaneContainer)w).getRootPane();
                    JLayeredPane lp = root.getLayeredPane();
                    Container content = root.getContentPane();
                    if (content instanceof X11TransparentContentPane) {
                        ((X11TransparentContentPane)content).setTransparent(transparent);
                    }
                    else if (transparent) {
                        X11TransparentContentPane x11content =
                            new X11TransparentContentPane(content);
                        root.setContentPane(x11content);
                        lp.add(new RepaintTrigger(x11content),
                               JLayeredPane.DRAG_LAYER);
                    }
                    setLayersTransparent(w, transparent);
                    setForceHeavyweightPopups(w, transparent);
                    setDoubleBuffered(w, !transparent);
                }
            });
        }

        private interface PixmapSource {
            Pixmap getPixmap(Display dpy, X11.Window win);
        }

        private void setWindowShape(final Window w, final PixmapSource src) {
            Runnable action = new Runnable() {
                @Override
                public void run() {
                    X11 x11 = X11.INSTANCE;
                    Display dpy = x11.XOpenDisplay(null);
                    if (dpy == null) {
                        return;
                    }
                    Pixmap pm = null;
                    try {
                        X11.Window win = getDrawable(w);
                        pm = src.getPixmap(dpy, win);
                        Xext ext = Xext.INSTANCE;
                        ext.XShapeCombineMask(dpy, win, X11.Xext.ShapeBounding,
                                              0, 0, pm == null ? Pixmap.None : pm,
                                              X11.Xext.ShapeSet);
                    }
                    finally {
                        if (pm != null) {
                            x11.XFreePixmap(dpy, pm);
                        }
                        x11.XCloseDisplay(dpy);
                    }
                    setForceHeavyweightPopups(getWindow(w), pm != null);
                }
            };
            whenDisplayable(w, action);
        }

        @Override
        protected void setMask(final Component w, final Raster raster) {
            setWindowShape(getWindow(w), new PixmapSource() {
                @Override
                public Pixmap getPixmap(Display dpy, X11.Window win) {
                    return raster != null ? createBitmap(dpy, win, raster) : null;
                }
            });
        }
    }

    /**
     * Applies the given mask to the given window. Does nothing if the
     * operation is not supported.  The mask is treated as a bitmap and
     * ignores transparency.
     */
    public static void setWindowMask(Window w, Shape mask) {
        getInstance().setWindowMask(w, mask);
    }

    /**
     * Applies the given mask to the given heavyweight component. Does nothing
     * if the operation is not supported.  The mask is treated as a bitmap and
     * ignores transparency.
     */
    public static void setComponentMask(Component c, Shape mask) {
        getInstance().setWindowMask(c, mask);
    }

    /**
     * Applies the given mask to the given window. Does nothing if the
     * operation is not supported.  The mask is treated as a bitmap and
     * ignores transparency.
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
     * to all window pixels.<p>
     * NOTE: Windows requires that <code>sun.java2d.noddraw=true</code>
     * in order for alpha to work.<p>
     * NOTE: On OSX, the property
     * <code>apple.awt.draggableWindowBackground</code> must be set to its
     * final value <em>before</em> the heavyweight peer for the Window is
     * created.  Once {@link Component#addNotify} has been called on the
     * component, causing creation of the heavyweight peer, changing this
     * property has no effect.
     * @see <a href="http://developer.apple.com/technotes/tn2007/tn2196.html#APPLE_AWT_DRAGGABLEWINDOWBACKGROUND">Apple Technote 2007</a>
     */
    public static void setWindowAlpha(Window w, float alpha) {
        getInstance().setWindowAlpha(w, Math.max(0f, Math.min(alpha, 1f)));
    }

    /**
     * Set the window to be transparent. Only explicitly painted pixels
     * will be non-transparent. All pixels will be composited with
     * whatever is under the window using their alpha values.
     *
     * On OSX, the property <code>apple.awt.draggableWindowBackground</code>
     * must be set to its final value <em>before</em> the heavyweight peer for
     * the Window is created.  Once {@link Component#addNotify} has been
     * called on the component, causing creation of the heavyweight peer,
     * changing this property has no effect.
     * @see <a href="http://developer.apple.com/technotes/tn2007/tn2196.html#APPLE_AWT_DRAGGABLEWINDOWBACKGROUND">Apple Technote 2007</a>
     */
    public static void setWindowTransparent(Window w, boolean transparent) {
        getInstance().setWindowTransparent(w, transparent);
    }

	/**
	 * Obtains the set icon for the window associated with the specified
	 * window handle.
	 *
	 * @param hwnd
	 *            The concerning window handle.
	 * @return Either the window's icon or {@code null} if an error
	 *         occurred.
	 */
	public  static BufferedImage getWindowIcon(final HWND hwnd) {
		return getInstance().getWindowIcon(hwnd);
	}

	/**
	 * Detects the size of an icon.
	 *
	 * @param hIcon
	 *            The icon handle type.
	 * @return Either the requested icon's dimension or an {@link Dimension}
	 *         instance of {@code (0, 0)}.
	 */
	public static Dimension getIconSize(final HICON hIcon) {
		return getInstance().getIconSize(hIcon);
	}

	/**
	 * Requests a list of all currently available Desktop windows.
	 *
	 * @param onlyVisibleWindows
	 *            Specifies whether only currently visible windows will be
	 *            considered ({@code true}). That are windows which are not
	 *            minimized. The {@code WS_VISIBLE} flag will be checked (see:
	 *            <a href=
	 *            "https://msdn.microsoft.com/de-de/library/windows/desktop/ms633530%28v=vs.85%29.aspx"
	 *            >User32.IsWindowVisible(HWND)</a>).
	 *
	 * @return A list with all windows and some detailed information.
	 */
	public static List<DesktopWindow> getAllWindows(
			final boolean onlyVisibleWindows) {
		return getInstance().getAllWindows(onlyVisibleWindows);
	}

	/**
	 * Tries to obtain the Window's title which belongs to the specified window
	 * handle.
	 *
	 * @param hwnd
	 *            The concerning window handle.
	 * @return Either the title or an empty string of no title was found or an
	 *         error occurred.
	 */
	public static String getWindowTitle(final HWND hwnd) {
		return getInstance().getWindowTitle(hwnd);
	}

	/**
	 * Detects the full file path of the process associated with the specified
	 * window handle.
	 *
	 * @param hwnd
	 *            The concerning window handle for which the PE file path is
	 *            required.
	 * @return The full file path of the PE file that is associated with the
	 *         specified window handle.
	 */
	public static String getProcessFilePath(final HWND hwnd) {
		return getInstance().getProcessFilePath(hwnd);
	}

	/**
	 * Requests the location and size of the window associated with the
	 * specified window handle.
	 *
	 * @param hwnd
	 *            The concerning window handle.
	 * @return The location and size of the window.
	 */
	public static Rectangle getWindowLocationAndSize(final HWND hwnd) {
		return getInstance().getWindowLocationAndSize(hwnd);
	}
}
