/*
 * Copyright (c) 2007-2008 Timothy Wall, All Rights Reserved This library is
 * free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation; either version 2.1 of the License, or (at
 * your option) any later version. This library is distributed in the
 * hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
 * PURPOSE. See the GNU Lesser General Public License for more details.
 */
package com.sun.jna.examples;

import java.awt.Cursor;
import java.awt.BorderLayout;
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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JWindow;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.MouseInputAdapter;

public class AlphaMaskDemo2 implements Runnable {
    private static final DataFlavor URL_FLAVOR =
        new DataFlavor("application/x-java-url; class=java.net.URL", "URL");
    private static final DataFlavor URI_LIST_FLAVOR =
        new DataFlavor("text/uri-list; class=java.lang.String", "URI list");
    private JFrame frame;
    private JWindow alphaWindow;
    private JLabel icon;
    private ImageObserver observer = new ImageObserver() {
        public boolean imageUpdate(final Image img, int infoflags, int x,
                                   int y, int width, int height) {
            if ((infoflags & (ImageObserver.ALLBITS | ImageObserver.FRAMEBITS)) != 0) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        setImage(img);
                    }
                });
                return false;
            }
            else if ((infoflags & (ImageObserver.ERROR | ImageObserver.ABORT)) != 0) {
                System.err.println("Image load error: " + img);
                return false;
            }
            return true;
        }
    };

    private void setImage(final Image image) {
        final int w = image.getWidth(observer);
        final int h = image.getHeight(observer);
        if (w > 0 && h > 0) {
            frame.setIconImage(image);
            icon.setIcon(new ImageIcon(image));
            if (!alphaWindow.isVisible()) {
                alphaWindow.pack();
                alphaWindow.setVisible(true);
            }
            else {
                alphaWindow.setSize(alphaWindow.getPreferredSize());
            }
            icon.getParent().invalidate();
            icon.getParent().repaint();
        }
    }

    public void run() {
        // Must find a graphics configuration with a depth of 32 bits
        GraphicsConfiguration gconfig = WindowUtils.getAlphaCompatibleGraphicsConfiguration();
        frame = new JFrame("Alpha Mask Demo");
        alphaWindow = new JWindow(frame, gconfig);
        icon = new JLabel();
        icon.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        alphaWindow.getContentPane().add(icon);
        JButton quit = new JButton("Quit");
        JLabel label = new JLabel("Drag this window by its image");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        alphaWindow.getContentPane().add(label, BorderLayout.NORTH);
        alphaWindow.getContentPane().add(quit, BorderLayout.SOUTH);
        quit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        MouseInputAdapter handler = new MouseInputAdapter() {
            private Point offset;

            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e))
                    offset = e.getPoint();
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
        alphaWindow.addMouseListener(handler);
        alphaWindow.addMouseMotionListener(handler);
        JPanel p = new JPanel(new BorderLayout(8, 8));
        p.setBorder(new EmptyBorder(8, 8, 8, 8));
        p.setTransferHandler(new TransferHandler() {
            public boolean canImport(JComponent comp,
                                     DataFlavor[] transferFlavors) {
                List list = Arrays.asList(transferFlavors);
                if (list.contains(URL_FLAVOR) || list.contains(URI_LIST_FLAVOR)
                    || list.contains(DataFlavor.imageFlavor)
                    || list.contains(DataFlavor.javaFileListFlavor)) {
                    return true;
                }
                if (DataFlavor.selectBestTextFlavor(transferFlavors) != null) {
                    return true;
                }
                System.err.println("No acceptable flavor found in "
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
                        Image image = (Image)t
                                              .getTransferData(DataFlavor.imageFlavor);
                        setImage(image);
                        return true;
                    }
                    if (t.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                        List files = (List)t
                                            .getTransferData(DataFlavor.javaFileListFlavor);
                        File f = (File)files.get(0);
                        URL url = new URL("file://"
                            + f.toURI().toURL().getPath());
                        Image image = Toolkit.getDefaultToolkit().getImage(url);
                        setImage(image);
                        return true;
                    }
                    DataFlavor flavor = DataFlavor
                                                  .selectBestTextFlavor(t
                                                                         .getTransferDataFlavors());
                    if (flavor != null) {
                        Reader reader = flavor.getReaderForText(t);
                        char[] buf = new char[512];
                        StringBuffer b = new StringBuffer();
                        int count;
                        // excise excess NUL characters (bug in firefox,
                        // java
                        // or my code, not sure which). someone got the
                        // encoding wrong
                        while ((count = reader.read(buf)) > 0) {
                            for (int i = 0; i < count; i++) {
                                if (buf[i] != 0)
                                    b.append(buf, i, 1);
                            }
                        }
                        String html = b.toString();
                        Pattern p = Pattern
                                           .compile(
                                                    "<img.*src=\"([^\\\"\">]+)\"",
                                                    Pattern.CANON_EQ
                                                        | Pattern.UNICODE_CASE);
                        Matcher m = p.matcher(html);
                        if (m.find()) {
                            URL url = new URL(m.group(1));
                            System.out.println("Load image from " + url);
                            Image image = Toolkit.getDefaultToolkit()
                                                 .getImage(url);
                            setImage(image);
                            return true;
                        }
                        System.err.println("Can't parse text: " + html);
                        return false;
                    }
                    System.err.println("No flavor available: "
                        + Arrays.asList(t.getTransferDataFlavors()));
                }
                catch (UnsupportedFlavorException e) {
                    e.printStackTrace();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
                catch (Throwable e) {
                    e.printStackTrace();
                }
                return false;
            }
        });
        p
         .add(
              new JLabel(
                         "<html><center>Drop an image with an alpha channel onto this window<br>"
                             + "You may also adjust the overall transparency with the slider</center></html>"),
              BorderLayout.NORTH);
        p.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        final JSlider slider = new JSlider(0, 255, 255);
        slider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                int value = slider.getValue();
                WindowUtils.setWindowAlpha(alphaWindow, value / 255f);
            }
        });
        p.add(slider, BorderLayout.SOUTH);
        frame.getContentPane().add(p);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        centerOnScreen(frame);
        frame.setVisible(true);
        WindowUtils.setWindowTransparent(alphaWindow, true);
        alphaWindow.setLocation(frame.getX() + frame.getWidth() + 4,
                                frame.getY());
        try {
            URL url = getClass().getResource("tardis.png");
            if (url != null) {
                setImage(Toolkit.getDefaultToolkit().getImage(url));
            }
        }
        catch (Exception e) {
        }
    }

    /** Center the given {@link Window} on the default screen. */
    private static void centerOnScreen(Window window) {
        Point center = GraphicsEnvironment.getLocalGraphicsEnvironment()
                                          .getCenterPoint();
        Rectangle max = GraphicsEnvironment.getLocalGraphicsEnvironment()
                                           .getMaximumWindowBounds();
        int x = Math.max(center.x - Math.round(window.getWidth() / 2f), max.x);
        int y = Math.max(center.y - Math.round(window.getHeight() / 2f), max.y);
        window.setLocation(new Point(x, y));
    }

    public static void main(String[] args) {
        try {
            System.setProperty("sun.java2d.noddraw", "true");
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (Exception e) {
        }
        SwingUtilities.invokeLater(new AlphaMaskDemo2());
    }
}
