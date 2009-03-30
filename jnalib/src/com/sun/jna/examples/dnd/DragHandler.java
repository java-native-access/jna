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

import java.awt.AlphaComposite;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Point;
import java.awt.Transparency;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceContext;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.dnd.DragSourceMotionListener;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.InvalidDnDOperationException;
import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;
import javax.swing.Icon;
import javax.swing.JColorChooser;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.text.JTextComponent;

/** Provides simplified drag handling for a component.  
 * Usage:<br>
 * <pre><code>
 * int actions = DnDConstants.MOVE_OR_COPY;
 * Component component = ...;
 * DragHandler handler = new DragHandler(component, actions);
 * </code></pre>
 * <ul>
 * <li>Supports painting an arbitrary {@link Icon} with transparency to
 * represent the item being dragged (restricted to the {@link java.awt.Window} 
 * of the drag source if the platform doesn't support drag images).
 * <li>Disallow starting a drag if the user requests an unsupported action.
 * <li>Adjusts the cursor on drags with no modifier for which the default action
 * is disallowed but where one or more non-default actions are allowed, e.g. a 
 * drag (with no modifiers) to a target which supports "link" should change the 
 * cursor to "link" (prior to 1.6, the JRE behavior is to display a 
 * "not allowed" cursor, even though the action actually depends on how the 
 * drop target responds).  
 * <a href="http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4869264">
 * The bug</a> is fixed in java 1.6.
 * <li>Disallow drops to targets if the non-default (user-requested) action 
 * is not supported by the target, e.g. the user requests a "copy" when the 
 * target only supports "move".  This is generally the responsibility of the
 * drop handler, which decides whether or not to accept a drag.  The DragHandler
 * provides static modifier state information since the drop handler doesn't
 * have access to it.
 * </ul>
 * NOTE: Fundamentally, the active action is determined by the drop handler
 * in {@link DropTargetDragEvent#acceptDrag}, but often client code
 * simply relies on {@link DropTargetDragEvent#getDropAction}.
 */
//TODO: separate into the the following drag pieces:
// * default cursor changing behavior fix (fixed in jre1.6) (global DSL)
// * drag unselected item (fixed in 1.6, flag in 1.5.0.05+) bug not an issue w/DragHandler
// * multi-selection drag workaround (if not using swing drag gesture recognizer)
//   proper multi-selection drag is provided for swing dnd as of 1.4
// (use swing drag gesture recognizer if possible?) DSL+mouse listener
//TODO: identify overlap with default Swing support + TransferHandler
// * TransferHandler doesn't have drop location information (included in 1.6)
// * SwingDropTarget allows listeners, so target highlighting is possible

//NOTE: acceptDrag(int) is simply indicates the drag is accepted; the action
// value doesn't get propagated anywhere, it's certainly not communicated to 
// the drag source
//MAYBE: should Transferable/Icon provision be a separate interface
//(i.e. TransferHandler)?  only if the rest of the drag handler is constant and 
// only the image needs to change (for standard components, e.g. tree cells, 
// table cells, etc.)

public abstract class DragHandler 
    implements DragSourceListener, DragSourceMotionListener, DragGestureListener {

    /** Default maximum size for ghosted images. */
    public static final Dimension MAX_GHOST_SIZE = new Dimension(250, 250);

    /** Default transparency for ghosting. */
    public static final float DEFAULT_GHOST_ALPHA = 0.5f;

    /** {@link #getModifiers} returns this value when the current
     * modifiers state is unknown.
     */
    public static final int UNKNOWN_MODIFIERS = -1;
    
    /** {@link #getTransferable} returns this value when 
     * the current {@link Transferable} is unknown.
     */
    public static final Transferable UNKNOWN_TRANSFERABLE = null;
    /** Convenience to reference {@link DnDConstants#ACTION_MOVE}. */
    protected static final int MOVE = DnDConstants.ACTION_MOVE;
    /** Convenience to reference {@link DnDConstants#ACTION_COPY}. */
    protected static final int COPY = DnDConstants.ACTION_COPY;
    /** Convenience to reference {@link DnDConstants#ACTION_LINK}. */
    protected static final int LINK = DnDConstants.ACTION_LINK;
    /** Convenience to reference {@link DnDConstants#ACTION_NONE}. */
    protected static final int NONE = DnDConstants.ACTION_NONE;
    
    // TODO: w32 explorer: link=alt or ctrl+shift, copy=ctrl or shift
    // w32 others: copy=ctrl
    /** Modifier mask for a user-requested move. */
    static final int MOVE_MASK = InputEvent.SHIFT_DOWN_MASK;
    static final boolean OSX = 
        System.getProperty("os.name").toLowerCase().indexOf("mac") != -1;
    /** Modifier mask for a user-requested copy. */
    static final int COPY_MASK = 
        OSX ? InputEvent.ALT_DOWN_MASK : InputEvent.CTRL_DOWN_MASK;
    /** Modifier mask for a user-requested link. */
    static final int LINK_MASK =
        OSX ? InputEvent.ALT_DOWN_MASK|InputEvent.META_DOWN_MASK 
            : InputEvent.CTRL_DOWN_MASK|InputEvent.SHIFT_DOWN_MASK;
    /** Modifier mask for any user-requested action. */
    static final int KEY_MASK =
        InputEvent.ALT_DOWN_MASK|InputEvent.META_DOWN_MASK
        |InputEvent.CTRL_DOWN_MASK|InputEvent.SHIFT_DOWN_MASK
        |InputEvent.ALT_GRAPH_DOWN_MASK;

    private static int modifiers = UNKNOWN_MODIFIERS;
    private static Transferable transferable = UNKNOWN_TRANSFERABLE;
    
    /** Used to communicate modifier state to {@link DropHandler}.  Note that
     * this field will only be accurate when a {@link DragHandler} in
     * the same VM started the drag.  Otherwise, {@link #UNKNOWN_MODIFIERS}
     * will be returned.
     */
    static int getModifiers() {
        return modifiers;
    }
    
    /** Used to communicate the current {@link Transferable} during a drag, 
     * if available.  Work around absence of access to the data when dragging 
     * pre-1.5. 
     */
    public static Transferable getTransferable(DropTargetEvent e) {
        if (e instanceof DropTargetDragEvent) {
            try {
                // getTransferable is available during drag only on 1.5+
                return (Transferable)
                    e.getClass().getMethod("getTransferable", (Class[])null).invoke(e, (Object[])null);
            }
            catch(Exception ex) {
                // Method not available
            }
        }
        else if (e instanceof DropTargetDropEvent) {
            return ((DropTargetDropEvent)e).getTransferable();
        }
        return transferable;
    }
    
    private int supportedActions;
    private boolean fixCursor = true;
    private Component dragSource;
    private GhostedDragImage ghost;
    private Point imageOffset;
    private Dimension maxGhostSize = MAX_GHOST_SIZE;
    private float ghostAlpha = DEFAULT_GHOST_ALPHA;

    /** Enable drags from the given component, supporting the actions in
     * the given action mask.
     */
    protected DragHandler(Component dragSource, int actions) {
        this.dragSource = dragSource;
        this.supportedActions = actions;
        try {
            String alpha = System.getProperty("DragHandler.alpha");
            if (alpha != null) {
                try {
                    ghostAlpha = Float.parseFloat(alpha);
                }
                catch(NumberFormatException e) { }
            }
            String max = System.getProperty("DragHandler.maxDragImageSize");
            if (max != null) {
                String[] size = max.split("x");
                if (size.length == 2) {
                    try {
                        maxGhostSize = new Dimension(Integer.parseInt(size[0]),
                                                     Integer.parseInt(size[1]));
                    }
                    catch(NumberFormatException e) { }
                }
            }
        }
        catch(SecurityException e) { }
        // Avoid having more than one gesture recognizer active
        disableSwingDragSupport(dragSource);
        DragSource src = DragSource.getDefaultDragSource();
        src.createDefaultDragGestureRecognizer(dragSource, supportedActions, this);
    }

    private void disableSwingDragSupport(Component comp) {
        if (comp instanceof JTree) {
            ((JTree)comp).setDragEnabled(false);
        }
        else if (comp instanceof JList) {
            ((JList)comp).setDragEnabled(false);
        }
        else if (comp instanceof JTable) {
            ((JTable)comp).setDragEnabled(false);
        }
        else if (comp instanceof JTextComponent) {
            ((JTextComponent)comp).setDragEnabled(false);
        }
        else if (comp instanceof JColorChooser) {
            ((JColorChooser)comp).setDragEnabled(false);
        }
        else if (comp instanceof JFileChooser) {
            ((JFileChooser)comp).setDragEnabled(false);
        }
    }

    /** Override to control whether a drag is started.  The default 
     * implementation disallows the drag if the user is applying modifiers
     * and the user-requested action is not supported.  
     */
    protected boolean canDrag(DragGestureEvent e) {
        int mods = e.getTriggerEvent().getModifiersEx() & KEY_MASK;
        if (mods == MOVE_MASK)
            return (supportedActions & MOVE) != 0;
        if (mods == COPY_MASK)
            return (supportedActions & COPY) != 0;
        if (mods == LINK_MASK)
            return (supportedActions & LINK) != 0;
        return true;
    }

    /** Update the modifiers hint. */
    protected void setModifiers(int mods) {
        modifiers = mods;
    }
    /** Override to provide an appropriate {@link Transferable} representing
     * the data being dragged.
     */
    protected abstract Transferable getTransferable(DragGestureEvent e);

    /** Override this to provide a custom image.  The {@link Icon}
     * returned by this method by default is <code>null</code>, which results
     * in no drag image.
     * @param srcOffset set this to be the offset from the drag source 
     * component's upper left corner to the image's upper left corner.  
     * For example, when dragging a row from a list, the offset would be the 
     * row's bounding rectangle's (x,y) coordinate.<p>
     * The default value is (0,0), so if unchanged, the image is will
     * use the same origin as the drag source component.  
     */
    protected Icon getDragIcon(DragGestureEvent e, Point srcOffset) {
        return null; 
    }
    
    /** Override to perform any decoration of the target at the start of a drag, 
     * if desired. 
     */
    protected void dragStarted(DragGestureEvent e) { }

    /** Called when a user drag gesture is recognized.  This method is 
     * responsible for initiating the drag operation.
     */
    public void dragGestureRecognized(DragGestureEvent e) {
        if ((e.getDragAction() & supportedActions) != 0
            && canDrag(e)) {
            setModifiers(e.getTriggerEvent().getModifiersEx() & KEY_MASK);
            Transferable transferable = getTransferable(e);
            if (transferable == null)
                return;
            try {
                Point srcOffset = new Point(0, 0);
                Icon icon = getDragIcon(e, srcOffset);
                Point origin = e.getDragOrigin();
                // offset of the image origin from the cursor
                imageOffset = new Point(srcOffset.x - origin.x,
                                        srcOffset.y - origin.y);
                Icon dragIcon = scaleDragIcon(icon, imageOffset);
                Cursor cursor = null;
                if (dragIcon != null && DragSource.isDragImageSupported()) {
                    GraphicsConfiguration gc = e.getComponent().getGraphicsConfiguration();
                    e.startDrag(cursor, createDragImage(gc, dragIcon), 
                                imageOffset, transferable, this);
                }
                else {
                    if (dragIcon != null) {
                        Point screen = dragSource.getLocationOnScreen();
                        screen.translate(origin.x, origin.y);
                        Point cursorOffset = new Point(-imageOffset.x, -imageOffset.y);
                        ghost = new GhostedDragImage(dragSource, dragIcon, 
                                                     getImageLocation(screen), cursorOffset);
                        ghost.setAlpha(ghostAlpha);
                    }
                    e.startDrag(cursor, transferable, this);
                }
                dragStarted(e);
                moved = false;
                e.getDragSource().addDragSourceMotionListener(this);
                DragHandler.transferable = transferable;
            }
            catch (InvalidDnDOperationException ex) {
                if (ghost != null) {
                    ghost.dispose();
                    ghost = null;
                }
            }
        }
    }

    /** Reduce the size of the given drag icon, if appropriate.  When using
     * a smaller drag icon, we also need to adjust the cursor offset within
     * the icon.
     */
    protected Icon scaleDragIcon(Icon icon, Point imageOffset) {
        /*
        if (icon != null && maxGhostSize != null) {
            if (icon.getIconWidth() > maxGhostSize.width
                || icon.getIconHeight() > maxGhostSize.height) {
                Icon scaled = new ScaledIcon(icon, maxGhostSize.width, 
                                             maxGhostSize.height);
                double scale = (double)scaled.getIconWidth()/icon.getIconWidth();
                imageOffset.x *= scale;
                imageOffset.y *= scale;
                return scaled;
            }
        }*/
        return icon;
    }

    /** Create an image from the given icon.  The image is provided to the
     * native handler if drag images are supported natively.
     */
    protected Image createDragImage(GraphicsConfiguration gc, Icon icon) {
        int w = icon.getIconWidth();
        int h = icon.getIconHeight();
        BufferedImage image = gc.createCompatibleImage(w, h, Transparency.TRANSLUCENT);
        Graphics2D g = (Graphics2D)image.getGraphics();
        g.setComposite(AlphaComposite.Clear);
        g.fillRect(0, 0, w, h);
        // Ignore pixels in the buffered image
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC, ghostAlpha));
        icon.paintIcon(dragSource, g, 0, 0);
        g.dispose();
        return image;
    }

    /** Reduce a multiply-set bit mask to a single bit. */
    private int reduce(int actions) {
        if ((actions & MOVE) != 0 && actions != MOVE) {
            return MOVE;
        }
        else if ((actions & COPY) != 0 && actions != COPY) {
            return COPY;
        }
        return actions;
    }

    protected Cursor getCursorForAction(int actualAction) {
        switch(actualAction) {
        case MOVE: 
            return DragSource.DefaultMoveDrop; 
        case COPY:
            return DragSource.DefaultCopyDrop;
        case LINK: 
            return DragSource.DefaultLinkDrop;
        default:
            return DragSource.DefaultMoveNoDrop;
        }
    }

    /** Returns the first available action supported by source and target. */
    protected int getAcceptableDropAction(int targetActions) {
        return reduce(supportedActions & targetActions);
    }
    
    /** Get the currently requested drop action. */
    protected int getDropAction(DragSourceEvent ev) {
        if (ev instanceof DragSourceDragEvent) {
            DragSourceDragEvent e = (DragSourceDragEvent)ev;
            return e.getDropAction();
        }
        if (ev instanceof DragSourceDropEvent) {
            return ((DragSourceDropEvent)ev).getDropAction();
        }
        return NONE;
    }

    /** Pick a different drop action if the target doesn't support the current
     * one and there are no modifiers.
     */
    protected int adjustDropAction(DragSourceEvent ev) {
        int action = getDropAction(ev);
        if (ev instanceof DragSourceDragEvent) {
            DragSourceDragEvent e = (DragSourceDragEvent)ev;
            if (action == NONE) {
                int mods = e.getGestureModifiersEx() & KEY_MASK;
                if (mods == 0) {
                    action = getAcceptableDropAction(e.getTargetActions());
                }
            }
        }
        return action;
    }
    
    protected void updateCursor(DragSourceEvent ev) {
        if (!fixCursor)
            return;
        Cursor cursor = getCursorForAction(adjustDropAction(ev));
        ev.getDragSourceContext().setCursor(cursor);
    }
    
    static String actionString(int action) {
        switch(action) {
        case MOVE: return "MOVE";
        case MOVE|COPY: return "MOVE|COPY";
        case MOVE|LINK: return "MOVE|LINK";
        case MOVE|COPY|LINK: return "MOVE|COPY|LINK";
        case COPY: return "COPY";
        case COPY|LINK: return "COPY|LINK";
        case LINK: return "LINK";
        default: return "NONE";
        }
    }
    private String lastAction;
    private void describe(String type, DragSourceEvent e) {
        if (false) {
            DragSourceContext ds = e.getDragSourceContext();
            String msg = "drag: " + type;
            if (e instanceof DragSourceDragEvent) {
                DragSourceDragEvent ev = (DragSourceDragEvent)e;
                msg += ": src=" + actionString(ds.getSourceActions())
                    + " usr=" + actionString(ev.getUserAction())
                    + " tgt=" + actionString(ev.getTargetActions())
                    + " act=" + actionString(ev.getDropAction())
                    + " mods=" + ev.getGestureModifiersEx();
            }
            else {
                msg += ": e=" + e;
            }
            if (!msg.equals(lastAction)) {
                System.out.println(lastAction = msg);
            }
        }
    }

    public void dragDropEnd(DragSourceDropEvent e) {
        describe("end", e);
        setModifiers(UNKNOWN_MODIFIERS);
        transferable = UNKNOWN_TRANSFERABLE;
        if (ghost != null) {
            if (e.getDropSuccess()) { 
                ghost.dispose();
            }
            else {
                ghost.returnToOrigin();
            }
            ghost = null;
        }
        DragSource src = e.getDragSourceContext().getDragSource();
        src.removeDragSourceMotionListener(this);
        moved = false;
    }

    private Point getImageLocation(Point where) {
        where.translate(imageOffset.x, imageOffset.y);
        return where;
    }
    
    public void dragEnter(DragSourceDragEvent e) {
        describe("enter", e);
        if (ghost != null) {
            ghost.move(getImageLocation(e.getLocation()));
        }
        updateCursor(e);
    }

    // bug workaround; need to skip initial dragMouseMoved event,
    // which has reports "0" for the available target actions (1.4+?)
    // filed a bug for this
    private boolean moved;
    public void dragMouseMoved(DragSourceDragEvent e) {
        describe("move", e);
        if (ghost != null) {
            ghost.move(getImageLocation(e.getLocation()));
        }
        if (moved) 
            updateCursor(e);
        moved = true;
    }

    public void dragOver(DragSourceDragEvent e) {
        describe("over", e);
        if (ghost != null) {
            ghost.move(getImageLocation(e.getLocation()));
        }
        updateCursor(e);
    }

    public void dragExit(DragSourceEvent e) {
        describe("exit", e);
    }

    public void dropActionChanged(DragSourceDragEvent e) {
        describe("change", e);
        setModifiers(e.getGestureModifiersEx() & KEY_MASK);
        if (ghost != null) {
            ghost.move(getImageLocation(e.getLocation()));
        }
        updateCursor(e);
    }
}