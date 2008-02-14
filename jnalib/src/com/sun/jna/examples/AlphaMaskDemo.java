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

import java.awt.event.*;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.image.Raster;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.MouseInputAdapter;
import com.sun.jna.Native;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.examples.unix.X11;
import com.sun.jna.examples.unix.X11.Display;
import com.sun.jna.examples.unix.X11.GC;
import com.sun.jna.examples.unix.X11.XSetWindowAttributes;
import com.sun.jna.examples.win32.GDI32;
import com.sun.jna.examples.win32.User32;
import com.sun.jna.examples.win32.GDI32.BITMAPINFO;
import com.sun.jna.examples.win32.User32.BLENDFUNCTION;
import com.sun.jna.examples.win32.User32.POINT;
import com.sun.jna.examples.win32.User32.SIZE;
import com.sun.jna.examples.win32.W32API.HANDLE;
import com.sun.jna.examples.win32.W32API.HBITMAP;
import com.sun.jna.examples.win32.W32API.HDC;
import com.sun.jna.examples.win32.W32API.HWND;
import com.sun.jna.ptr.PointerByReference;

// TODO: put this into a reasonable API; right now this is pretty much
// just hard-coded blitting of an image into a window
// Thanks to Rui Lopes for the C# example on which this is based: 
// rui@ruilopes.com 
// http://www.codeproject.com/cs/media/perpxalpha_sharp.asp?df=100&forumid=3270&exp=0&select=773155
public class AlphaMaskDemo implements Runnable {
    
    private static final DataFlavor URL_FLAVOR = 
        new DataFlavor("application/x-java-url; class=java.net.URL", "URL");
    private static final DataFlavor URI_LIST_FLAVOR = 
        new DataFlavor("text/uri-list; class=java.lang.String", "URI list");
    
    private JFrame frame;
    private JWindow alphaWindow;
    private float alpha = 1f;
    private Image image;
    
    private void update() {
        update(false, true);
    }

    private void update(boolean a, boolean i) {
        String os = System.getProperty("os.name");
        if (os.startsWith("Windows"))
            updateW32(a, i);
        else if (os.startsWith("Linux")) 
            updateX11(a, i);
        else if (os.startsWith("Mac"))
            updateMac(a, i);
    }

    private void updateMac(boolean a, boolean i) {
        if (!alphaWindow.isDisplayable()) {
            alphaWindow.pack();
        }
        if (a)
            WindowUtils.setWindowAlpha(alphaWindow, alpha);
        if (i) {
            alphaWindow.setBackground(new Color(0,0,0,0));
            alphaWindow.setContentPane(new JLabel(new ImageIcon(image)));
            alphaWindow.setSize(alphaWindow.getPreferredSize());
        }
        if (!alphaWindow.isVisible()) {
            Window parent = alphaWindow.getOwner();
            Point where = parent.getLocationOnScreen();
            where.translate(parent.getWidth(), 0);
            alphaWindow.setLocation(where);
            alphaWindow.setVisible(true);
        }
    }

    private com.sun.jna.Memory buffer;
    private int[] pixels;
    private void updateX11(boolean a, boolean i) {
        X11 x11 = X11.INSTANCE;
        X11.Window win = X11.Window.None;
        Display dpy = x11.XOpenDisplay(null);
        try {
            if (!alphaWindow.isDisplayable()) {
                alphaWindow.pack();
                if (System.getProperty("java.version").matches("^1\\.4\\..*"))
                    alphaWindow.setVisible(true);
                win = new X11.Window((int)Native.getWindowID(alphaWindow));
                Window parent = alphaWindow.getOwner();
                Point where = parent.getLocationOnScreen();
                where.translate(parent.getWidth(), 0);
                alphaWindow.removeAll();
                alphaWindow.setLocation(where);
                alphaWindow.setBackground(new Color(0,0,0,0));
            }
            else {
                win = new X11.Window((int)Native.getWindowID(alphaWindow));
            }
            
            if (i) {
                int w = image.getWidth(null);
                int h = image.getHeight(null);
                alphaWindow.setSize(w, h);
                if (buffer == null || buffer.getSize() != w*h*4) {
                    buffer = new com.sun.jna.Memory(w*h*4);
                    pixels = new int[w*h];
                }

                BufferedImage buf = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB_PRE);
                Graphics g = buf.getGraphics();
                g.drawImage(image, 0, 0, w, h, null);
                
                long start = System.currentTimeMillis();
                long blitTime, putImageTime, write;
                GC gc = x11.XCreateGC(dpy, win, new NativeLong(0), null);
                long gcTime = System.currentTimeMillis();
                try {
                    Raster raster = buf.getData();
                    int[] pixel = new int[4];
                    for (int y=0;y < h;y++) {
                        for (int x=0;x < w;x++) {
                            raster.getPixel(x, y, pixel);
                            int alpha = (pixel[3]&0xFF)<<24;
                            int red = (pixel[2]&0xFF);
                            int green = (pixel[1]&0xFF)<<8;
                            int blue = (pixel[0]&0xFF)<<16;
                            pixels[y*w+x] = alpha|red|green|blue;
                        }
                    }
                    blitTime = System.currentTimeMillis();
                    X11.XWindowAttributes xwa = new X11.XWindowAttributes();
                    x11.XGetWindowAttributes(dpy, win, xwa);
                    X11.XImage image = x11.XCreateImage(dpy, xwa.visual,
                                                        32, X11.ZPixmap,
                                                        0, buffer, w, h, 32, w*4);
                    buffer.write(0, pixels, 0, pixels.length);
                    write = System.currentTimeMillis();
                    x11.XPutImage(dpy, win, gc, image, 0,0,0,0,w,h);
                    x11.XFree(image.getPointer());
                    putImageTime = System.currentTimeMillis();
                }
                finally {
                    if (gc != null)
                        x11.XFreeGC(dpy, gc);
                }
                long end = System.currentTimeMillis();
                //System.out.println("gc: " + (gcTime-start) + "ms");
                //System.out.println("blit: " + (blitTime-gcTime) + "ms");
                //System.out.println("write: " + (write-blitTime) + "ms");
                //System.out.println("put image: " + (putImageTime-write) + "ms");
                //System.out.println("total: " + (end-start) + "ms");
            }
        }
        finally {
            if (dpy != null)
                x11.XCloseDisplay(dpy);
        }
        if (a)
            WindowUtils.setWindowAlpha(alphaWindow, alpha);
        
        if (!alphaWindow.isVisible()) {
            alphaWindow.setVisible(true);
            // hack for initial refresh (X11)
            update(true, true);
        }
    }

    private HWND getHwnd(Window w) {
        HWND hwnd = new HWND();
        hwnd.setPointer(Native.getWindowPointer(w));
        return hwnd;
    }
    
    private void updateW32(boolean a, boolean i) {
        User32 user = User32.INSTANCE;
        GDI32 gdi = GDI32.INSTANCE;
        HWND hWnd = null;

        if (!alphaWindow.isDisplayable()) {
            alphaWindow.pack();
            hWnd = getHwnd(alphaWindow);
            int flags = user.GetWindowLong(hWnd, User32.GWL_EXSTYLE);
            flags |= User32.WS_EX_LAYERED;
            user.SetWindowLong(hWnd, User32.GWL_EXSTYLE, flags);
            Window parent = alphaWindow.getOwner();
            Point where = parent.getLocationOnScreen();
            where.translate(parent.getWidth(), 0);
            alphaWindow.setLocation(where);
        }
        else {
            hWnd = getHwnd(alphaWindow);
        }
    
        if (i) {
            int w = image.getWidth(null);
            int h = image.getHeight(null);
            HDC screenDC = user.GetDC(null);
            HDC memDC = gdi.CreateCompatibleDC(screenDC);
            HBITMAP hBitmap = null;
            HANDLE oldBitmap = null;
            
            try {
                BufferedImage buf = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB_PRE);
                Graphics g = buf.getGraphics();
                g.drawImage(image, 0, 0, w, h, null);
                
                BITMAPINFO bmi = new BITMAPINFO();
                bmi.bmiHeader.biWidth = w;
                bmi.bmiHeader.biHeight = h;
                bmi.bmiHeader.biPlanes = 1;
                bmi.bmiHeader.biBitCount = 32;
                bmi.bmiHeader.biCompression = GDI32.BI_RGB;
                bmi.bmiHeader.biSizeImage = w * h * 4;
                
                PointerByReference ppbits = new PointerByReference();
                hBitmap = gdi.CreateDIBSection(memDC, bmi, GDI32.DIB_RGB_COLORS,
                                               ppbits, null, 0);
                oldBitmap = gdi.SelectObject(memDC, hBitmap);
                Pointer pbits = ppbits.getValue();
                
                Raster raster = buf.getData();
                int[] pixel = new int[4];
                int[] bits = new int[w*h];
                for (int y=0;y < h;y++) {
                    for (int x=0;x < w;x++) {
                        raster.getPixel(x, h-y-1, pixel);
                        int alpha = (pixel[3]&0xFF)<<24;
                        int red = (pixel[2]&0xFF);
                        int green = (pixel[1]&0xFF)<<8;
                        int blue = (pixel[0]&0xFF)<<16; 
                        bits[x + y * w] = alpha|red|green|blue;
                    }
                }
                pbits.write(0, bits, 0, bits.length);
                
                SIZE size = new SIZE();
                size.cx = w;
                size.cy = h;
                POINT loc = new POINT();
                loc.x = alphaWindow.getX();
                loc.y = alphaWindow.getY();
                POINT srcLoc = new POINT();
                BLENDFUNCTION blend = new BLENDFUNCTION();
                blend.SourceConstantAlpha = (byte)(alpha * 255);
                blend.AlphaFormat = User32.AC_SRC_ALPHA;
                user.UpdateLayeredWindow(hWnd, screenDC, loc, size, memDC, srcLoc, 
                                         0, blend, User32.ULW_ALPHA);
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
        else if (a) {
            BLENDFUNCTION blend = new BLENDFUNCTION();
            blend.SourceConstantAlpha = (byte)(alpha * 255);
            blend.AlphaFormat = User32.AC_SRC_ALPHA;
            user.UpdateLayeredWindow(hWnd, null, null, null, null, null, 
                                     0, blend, User32.ULW_ALPHA);
        }
        
        if (!alphaWindow.isVisible()) {
            alphaWindow.setVisible(true);
        }
    }
    
    private ImageObserver observer = new ImageObserver() {
        public boolean imageUpdate(final Image img, int infoflags, int x, int y, int width, int height) {
            if ((infoflags & (ImageObserver.ALLBITS|ImageObserver.FRAMEBITS)) != 0) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        setImage(img);
                    }
                });
                return false;
            }
            else if ((infoflags & (ImageObserver.ERROR|ImageObserver.ABORT)) != 0) {
                System.out.println("Image load error: " + img);
                return false;
            }
            return true;
        }
        
    };
    private void setImage(final Image image) {
        int w = image.getWidth(observer);
        int h = image.getHeight(observer);
        if (w > 0 && h > 0) {
            this.image = image;
            frame.setIconImage(image);
            update(false, true);
        }
    }
    
    private void setAlpha(float alpha) {
        this.alpha = alpha = Math.min(1f, Math.max(0f, alpha));
        update(true, false);
    }
    
    public void run() {
        // Must find a graphics configuration with a depth of 32 bits
        GraphicsConfiguration gconfig = WindowUtils.getAlphaCompatibleGraphicsConfiguration();
        frame = new JFrame("Alpha Mask Demo");
        alphaWindow = new JWindow(frame, gconfig);
        MouseInputAdapter handler = new MouseInputAdapter() {
            private Point offset;
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e))
                    offset = e.getPoint();
            }
            public void mouseReleased(MouseEvent e) {
                offset = null;
                // hack (w32) calling this has the side effect of re-enabling
                // hit testing; not sure why it gets disabled
                if (System.getProperty("os.name").startsWith("Windows"))
                    update(true, true);
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
        alphaWindow.addMouseListener(handler);
        alphaWindow.addMouseMotionListener(handler);
        JPanel p = new JPanel(new BorderLayout(8, 8));
        p.setBorder(new EmptyBorder(8, 8, 8, 8));
        p.setTransferHandler(new TransferHandler() {
            public boolean canImport(JComponent comp, DataFlavor[] transferFlavors) {
                List list = Arrays.asList(transferFlavors);
                if (list.contains(URL_FLAVOR)
                    || list.contains(URI_LIST_FLAVOR)
                    || list.contains(DataFlavor.imageFlavor)
                    || list.contains(DataFlavor.javaFileListFlavor)) {
                    return true;
                }
                if (DataFlavor.selectBestTextFlavor(transferFlavors) != null) {
                    return true;
                }
                System.out.println("No acceptable flavor found in "
                                   + Arrays.asList(transferFlavors));
                return false;
            }
            public boolean importData(JComponent comp, Transferable t) {
                try {
                    if (t.isDataFlavorSupported(URL_FLAVOR)) {
                        URL url = (URL)t.getTransferData(URL_FLAVOR);
                        setImage(Toolkit.getDefaultToolkit().getImage(url));
                        return true;
                    }
                    if (t.isDataFlavorSupported(URI_LIST_FLAVOR)) {
                        String s = (String)t.getTransferData(URI_LIST_FLAVOR);
                        String[] uris = s.split("[\r\n]");
                        if (uris.length > 0) {
                            URL url = new URL(uris[0]);
                            setImage(Toolkit.getDefaultToolkit().getImage(url));
                            return true;
                        }
                        return false;
                    }
                    if (t.isDataFlavorSupported(DataFlavor.imageFlavor)) {
                        Image image = (Image)t.getTransferData(DataFlavor.imageFlavor);
                        setImage(image);
                        return true;
                    }
                    if (t.isDataFlavorSupported(DataFlavor.javaFileListFlavor)){
                        List files = (List)t.getTransferData(DataFlavor.javaFileListFlavor);
                        File f = (File)files.get(0);
                        URL url = new URL("file://" + f.toURI().toURL().getPath());
                        Image image = Toolkit.getDefaultToolkit().getImage(url);
                        setImage(image);
                        return true;
                    }
                    DataFlavor flavor = DataFlavor.selectBestTextFlavor(t.getTransferDataFlavors());
                    if (flavor != null) {
                        Reader reader = flavor.getReaderForText(t);
                        char[] buf = new char[512];
                        StringBuffer b = new StringBuffer();
                        int count;
                        // excise excess NUL characters (bug in firefox, java
                        // or my code, not sure which).  someone got the 
                        // encoding wrong
                        while ((count = reader.read(buf)) > 0) {
                            for (int i=0;i < count;i++) {
                                if (buf[i] != 0)
                                    b.append(buf, i, 1);
                            }
                        }
                        String html = b.toString();
                        Pattern p = Pattern.compile("<img.*src=\"([^\\\"\">]+)\"",
                                                    Pattern.CANON_EQ|Pattern.UNICODE_CASE);
                        Matcher m = p.matcher(html);
                        if (m.find()) {
                            URL url = new URL(m.group(1));
                            System.out.println("Load image from " + url);
                            Image image = Toolkit.getDefaultToolkit().getImage(url);
                            setImage(image);
                            return true;
                        }
                        System.out.println("Can't parse text: " + html);
                        return false;
                    }
                    System.out.println("No flavor available: " 
                                       + Arrays.asList(t.getTransferDataFlavors()));
                }
                catch (UnsupportedFlavorException e) {
                    e.printStackTrace();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
                catch(Throwable e) {
                    e.printStackTrace();
                }
                return false;
            }
        });
        p.add(new JLabel("<html><center>Drop an image with an alpha channel onto this window<br>" 
                         + "You may also adjust the overall transparency with the slider</center></html>"),
                         BorderLayout.NORTH);
        final JSlider slider = new JSlider(0, 255, 255);
        slider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                int value = slider.getValue();
                setAlpha(value / 255f);
            }
        });
        p.add(slider, BorderLayout.SOUTH);
        
        frame.getContentPane().add(p);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        centerOnScreen(frame);
        frame.setVisible(true);
        p.addMouseListener(new MouseAdapter() {
                public void mousePressed(MouseEvent e) {
                    update();
                }
            });

        try {
            URL url = getClass().getResource("tardis.png");
            if (url != null) {
                setImage(Toolkit.getDefaultToolkit().getImage(url));
            }
        }
        catch(Exception e) { }
    }
    
    /** Center the given {@link Window} on the default screen. */
    private static void centerOnScreen(Window window) {
        Point center = GraphicsEnvironment.getLocalGraphicsEnvironment().getCenterPoint();
        Rectangle max = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
        int x = Math.max(center.x - Math.round(window.getWidth()/2f), max.x);
        int y = Math.max(center.y - Math.round(window.getHeight()/2f), max.y);
        window.setLocation(new Point(x, y));
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (Exception e) {
        }
        SwingUtilities.invokeLater(new AlphaMaskDemo());
    }
}
