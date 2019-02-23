/* Copyright (c) 2007 Timothy Wall, All Rights Reserved
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
package com.sun.jna.contrib.demo;

import java.awt.Image;
import java.awt.Point;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.io.IOException;
import java.net.URL;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.UIManager;

import com.sun.jna.platform.dnd.DragHandler;
import com.sun.jna.platform.dnd.DropHandler;


/** Demonstrate ghosted drag images.  Unfortunately, Swing drag support hides
 * the hooks we need to move the drag image around, so we don't use it and
 * roll our own.
 */
public class GhostedDragImageDemo {

    public static class ImageSelection implements Transferable, ClipboardOwner {
        public static final DataFlavor IMAGE_FLAVOR = DataFlavor.imageFlavor;
        private static final DataFlavor[] FLAVORS = { IMAGE_FLAVOR };

        private Image image;
        public ImageSelection(Image image) {
            this.image = image;
        }
        public void lostOwnership(Clipboard clipboard, Transferable transferable) {
            // don't care
        }
        public Object getTransferData(DataFlavor flavor) {
            return isDataFlavorSupported(flavor) ? image : null;
        }
        public DataFlavor[] getTransferDataFlavors() { return FLAVORS; }
        public boolean isDataFlavorSupported(DataFlavor flavor) {
            return flavor.equals(IMAGE_FLAVOR);
        }
    }

    public static class DragLabel extends JLabel {
        private static final long serialVersionUID = 1L;
        private boolean dragging;
        public DragLabel(Icon icon) {
            super(icon);
            new DragHandler(this, DnDConstants.ACTION_COPY_OR_MOVE) {
                protected Icon getDragIcon(DragGestureEvent e, Point imageOffset) {
                    dragging = true;
                    return getIcon();
                }
                public void dragDropEnd(DragSourceDropEvent e) {
                    super.dragDropEnd(e);
                    if (e.getDropSuccess() && getDropAction(e) == MOVE) {
                        if (dragging) {
                            setIcon(null);
                            dragging = false;
                        }
                    }
                }
                protected Transferable getTransferable(DragGestureEvent e) {
                    ImageIcon icon = (ImageIcon)getIcon();
                    if (icon != null) {
                        return new ImageSelection(icon.getImage());
                    }
                    return null;
                }
            };
            DataFlavor[] flavors = new DataFlavor[] { DataFlavor.imageFlavor };
            new DropHandler(this, DnDConstants.ACTION_COPY_OR_MOVE, flavors) {
                protected void drop(DropTargetDropEvent e, int action) throws UnsupportedFlavorException, IOException {
                    final Image image = (Image)e.getTransferable().getTransferData(DataFlavor.imageFlavor);
                    dragging = false;
                    setIcon(new ImageIcon(image));
                }
            };
        }
        public void setIcon(Icon icon) {
            super.setIcon(icon);
            if (icon == null) {
                setText("Empty");
            }
            else {
                setText(null);
            }
        }
    }

    public static void main(String[] args) {
        try {
            System.setProperty("sun.java2d.noddraw", "true");
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            JFrame f1 = new JFrame("Drag this");
            JFrame f2 = new JFrame("Over here");
            URL url = GhostedDragImageDemo.class.getResource("toucan.png");
            if (url == null)
                throw new RuntimeException("Icon not found");
            Icon icon = new ImageIcon(url);
            f1.getContentPane().add(new DragLabel(icon));
            JLabel label2 = new DragLabel(icon);
            f2.getContentPane().add(label2);
            f1.setLocation(100, 100);
            f1.pack();
            f1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            f2.setLocation(300, 100);
            f2.pack();
            label2.setIcon(null);
            f2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            f2.setVisible(true);
            f1.setVisible(true);
        }
        catch(Throwable t){
            t.printStackTrace();
            System.exit(1);
        }
    }
}
