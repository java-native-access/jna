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
import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetContext;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Provides simplified drop handling for a component.
 * Usage:<br>    
 * <pre><code>
 * int actions = DnDConstants.MOVE_OR_COPY;
 * Component component = ...;
 * DropHandler handler = new DropHandler(component, actions);
 * </code></pre>
 * <p>
 * <ul>
 * <li>Accept drops where the action is the default (i.e. no modifiers) but
 * the intersection of source and target actions is <i>not</i> the default.
 * Doing so allows the source to adjust the cursor appropriately.
 * <li>Refuse drops where the user modifiers request an action that is not
 * supported (this works for all cases except when the drag source is not
 * a {@link DragHandler} and the user explicitly requests a MOVE operation;
 * this is indistinguishable from a drag with no modifiers unless we have
 * access to the key modifiers, which {@link DragHandler} provides).
 * <li>Drops may be refused based on data flavor, location, intended drop
 * action, or any combination of those, by overriding {@link #canDrop}.
 * <li>Custom decoration of the drop area may be performed in 
 * {@link #paintDropTarget(DropTargetEvent, int, Point)} or by providing
 * a {@link DropTargetPainter}.
 * </ul>
 * 
 * The method {@link #getDropAction(DropTargetEvent)} follows these steps to
 * determine the appropriate action (if any).  
 * <ul>
 * <li>{@link #isSupported(DataFlavor[])} determines if there are any supported 
 * flavors
 * <li>{@link #getDropActionsForFlavors(DataFlavor[])} reduces the supported 
 * actions based on available flavors.  For instance, a text field for file 
 * paths might support {@link DnDConstants#ACTION_COPY_OR_MOVE} on a plain 
 * string, but {@link DnDConstants#ACTION_LINK} might be the only action 
 * supported on a file.
 * <li>{@link #getDropAction(DropTargetEvent, int, int, int)} relax the action
 * if it's the default, or restrict it for user requested actions.
 * <li>{@link #canDrop(DropTargetEvent, int, Point)} change the action based on 
 * the location in the drop target component, or any other criteria.
 * </ul>
 * 
 * Override {@link #drop(DropTargetDropEvent, int)} to handle the drop.  
 * You should invoke {@link DropTargetDropEvent#dropComplete} as soon
 * as the {@link Transferable} data is obtained, to avoid making the DnD
 * operation look suspended.
 * 
 * @see DragHandler
 * @author twall
 */
// NOTE: you could probably make one of these handlers serve several targets, 
// but for simplicity, keep the mapping 1-1-1 handler/droptarget/component
// TODO: look into making use of the existing 
// Transferable.SwingDropTarget on JComponent instances instead of
// creating a new DropTarget; we can add self as a listener; probably would
// want to remove the default TransferHandler.DropHandler, which uses
// the TransferHandler to drop
public abstract class DropHandler implements DropTargetListener {

    private int acceptedActions;
    private List acceptedFlavors;
    private DropTarget dropTarget;
    private boolean active = true;
    private DropTargetPainter painter;
    
    /** Create a handler that allows the given set of actions.  If using
     * this constructor, you will need to override {@link #isSupported} to
     * indicate which data flavors are allowed.
     */
    public DropHandler(Component c, int acceptedActions) {
        this(c, acceptedActions, new DataFlavor[0]);
    }
    
    /** Enable handling of drops, indicating what actions and flavors are
     * acceptable.  
     * @param c The component to receive drops
     * @param acceptedActions Allowed actions for drops
     * @param acceptedFlavors Allowed data flavors for drops
     * @see #isSupported
     */
    public DropHandler(final Component c, int acceptedActions, DataFlavor[] acceptedFlavors) {
        this(c, acceptedActions, acceptedFlavors, null);
    }
    
    /** Enable handling of drops, indicating what actions and flavors are
     * acceptable, and providing a painter for drop target feedback.
     * @param c The component to receive drops
     * @param acceptedActions Allowed actions for drops
     * @param acceptedFlavors Allowed data flavors for drops
     * @param painter Painter to handle drop target feedback
     * @see #paintDropTarget
     */
    public DropHandler(final Component c, int acceptedActions, 
                       DataFlavor[] acceptedFlavors, DropTargetPainter painter) {
        this.acceptedActions = acceptedActions;
        this.acceptedFlavors = Arrays.asList(acceptedFlavors);
        this.painter = painter;
        dropTarget = new DropTarget(c, acceptedActions, this, active);
    }
    
    protected DropTarget getDropTarget() {
        return dropTarget;
    }

    /** Whether this drop target is active. */
    public boolean isActive() { return active; }
    
    /** Set whether this handler (and thus its drop target) will accept
     * any drops.
     */ 
    public void setActive(boolean active) {
        this.active = active;
        if (dropTarget != null) {
            dropTarget.setActive(active);
        }
    }

    /** Indicate the actions available for the given list of data flavors.
     * Override this method if the acceptable drop actions depend
     * on the currently available {@link DataFlavor}.  The default returns
     * the accepted actions passed into the constructor.
     * @param dataFlavors currently available flavors
     * @see #getDropAction(DropTargetEvent, int, int, int)
     * @see #canDrop(DropTargetEvent, int, Point)
     */
    protected int getDropActionsForFlavors(DataFlavor[] dataFlavors) {
        return acceptedActions;
    }
    
    /** Calculate the effective action.  The default implementation 
     * checks whether any {@link DataFlavor}s are supported, and if so,
     * will change the current action from {@link DnDConstants#ACTION_NONE} to 
     * something in common between the source and destination.  Refuse 
     * user-requested actions if they are not supported (rather than silently 
     * accepting a non-user-requested action, which is the Java's DnD default 
     * behavior).  The drop action is forced to {@link DnDConstants#ACTION_NONE} 
     * if there is no supported data flavor.<p>
     * @see #isSupported(DataFlavor[])
     * @see #getDropActionsForFlavors
     * @see #getDropAction(DropTargetEvent, int, int, int)
     * @see #canDrop(DropTargetEvent, int, Point)
     */
    protected int getDropAction(DropTargetEvent e) {
        int currentAction = DragHandler.NONE;
        int sourceActions = DragHandler.NONE;
        Point location = null;
        DataFlavor[] flavors = new DataFlavor[0];
        if (e instanceof DropTargetDragEvent) {
            DropTargetDragEvent ev = (DropTargetDragEvent)e;
            currentAction = ev.getDropAction();
            sourceActions = ev.getSourceActions();
            flavors = ev.getCurrentDataFlavors();
            location = ev.getLocation();
        }
        else if (e instanceof DropTargetDropEvent) {
            DropTargetDropEvent ev = (DropTargetDropEvent)e;
            currentAction = ev.getDropAction();
            sourceActions = ev.getSourceActions();
            flavors = ev.getCurrentDataFlavors();
            location = ev.getLocation();
        }
        if (isSupported(flavors)) {
            int availableActions = getDropActionsForFlavors(flavors);
            currentAction = getDropAction(e, currentAction, sourceActions, availableActions);
            if (currentAction != DragHandler.NONE) {
                if (canDrop(e, currentAction, location)) {
                    return currentAction;
                }
            }
        }
        return DragHandler.NONE;
    }

    /* Adjust the drop action depending on whether the
     * current action is the default or a specific user-requested action.
     * The default implementation will change the current action from 
     * {@link DnDConstants#ACTION_NONE} if there are actions in 
     * common between the source and destination.  It will refuse user-requested
     * actions if they are not supported (rather than silently accepting
     * a non-user-requested action, which is the behavior of Swing's default
     * drop handlers).<p>
     * You can override this method if you wish to adjust the action based
     * on the the drag location; if you wish to deny drops based on location,
     * override {@link #canDrop} instead.  If you wish to adjust
     * the action based on the available data flavors, override
     * {@link #getDropActionsForFlavor} instead.
     * @see #getDropActionsForFlavor
     * @see #canDrop(DropTargetEvent, int, Point)
     */
    protected int getDropAction(DropTargetEvent e, int currentAction, 
                                int sourceActions, int acceptedActions) {
        boolean modifiersActive = modifiersActive(currentAction);
        if ((currentAction & acceptedActions) == DragHandler.NONE
            && !modifiersActive) {
            int action = acceptedActions & sourceActions;
            currentAction = action;
        }
        else if (modifiersActive) {
            int action = currentAction & acceptedActions & sourceActions;
            if (action != currentAction) {
                currentAction = action;
            }
        }
        return currentAction;
    }
    
    /** Returns whether there are key modifiers active , 
     * or false if they can't be determined.
     * We use the DragHandler hint, if available, or fall back to whether
     * the drop action is other than the default (move).
     */
    protected boolean modifiersActive(int dropAction) {
        int mods = DragHandler.getModifiers();
        if (mods == DragHandler.UNKNOWN_MODIFIERS) {
            if (dropAction == DragHandler.LINK
                || dropAction == DragHandler.COPY) {
                return true;
            }
            // Can't (yet) distinguish between a forced and a default move
            // without help from DragHandler
            return false;
        }
        return mods != 0;
    }

    private String lastAction;
    private void describe(String type, DropTargetEvent e) {
        if (false) {
            String msg = "drop: " + type;
            if (e instanceof DropTargetDragEvent) {
                DropTargetContext dtc = e.getDropTargetContext();
                DropTarget dt = dtc.getDropTarget();
                DropTargetDragEvent ev = (DropTargetDragEvent)e;
                msg += ": src=" + DragHandler.actionString(ev.getSourceActions())
                    + " tgt=" + DragHandler.actionString(dt.getDefaultActions())
                    + " act=" + DragHandler.actionString(ev.getDropAction());
            }
            else if (e instanceof DropTargetDropEvent) {
                DropTargetContext dtc = e.getDropTargetContext();
                DropTarget dt = dtc.getDropTarget();
                DropTargetDropEvent ev = (DropTargetDropEvent)e;
                msg += ": src=" + DragHandler.actionString(ev.getSourceActions())
                + " tgt=" + DragHandler.actionString(dt.getDefaultActions())
                + " act=" + DragHandler.actionString(ev.getDropAction());
            }
            if (!msg.equals(lastAction)) {
                System.out.println(lastAction = msg);
            }
        }
    }
    
    /** Accept or reject the drag represented by the given event.  Returns
     * the action determined by {@link #getDropAction(DropTargetEvent)}.
     */
    protected int acceptOrReject(DropTargetDragEvent e) {
        int action = getDropAction(e);
        if (action != DragHandler.NONE) {
            // NOTE: the action argument (as of 1.5+) is only passed
            // to the DropTargetContextPeer, *not* the drag source
            e.acceptDrag(action);
        }
        else {
            e.rejectDrag();
        }
        return action;
    }

    public void dragEnter(DropTargetDragEvent e) {
        describe("enter(tgt)", e);
        int action = acceptOrReject(e);
        paintDropTarget(e, action, e.getLocation());
    }

    public void dragOver(DropTargetDragEvent e) {
        describe("over(tgt)", e);
        int action = acceptOrReject(e);
        paintDropTarget(e, action, e.getLocation());
    }

    public void dragExit(DropTargetEvent e) {
        describe("exit(tgt)", e);
        paintDropTarget(e, DragHandler.NONE, null);
    }

    public void dropActionChanged(DropTargetDragEvent e) {
        describe("change(tgt)", e);
        int action = acceptOrReject(e);
        paintDropTarget(e, action, e.getLocation());
    }

    /** Indicates the user has initiated a drop.  The default performs all
     * standard drop validity checking and handling, then invokes
     * {@link #drop(DropTargetDropEvent,int)} if the drop looks acceptable.
     */
    public void drop(DropTargetDropEvent e) {
        describe("drop(tgt)", e);
        int action = getDropAction(e);
        if (action != DragHandler.NONE) {
            e.acceptDrop(action);
            try {
                drop(e, action);
                // Just in case this hasn't been done yet
                e.dropComplete(true);
            }
            catch (Exception ex) {
                e.dropComplete(false);
            }
        }
        else {
            e.rejectDrop();
        }
        paintDropTarget(e, DragHandler.NONE, e.getLocation());
    }
    
    /** Return whether any of the flavors in the given list are accepted. 
     * The list is compared against the accepted list provided in the
     * constructor.
     */
    protected boolean isSupported(DataFlavor[] flavors) {
        Set set = new HashSet(Arrays.asList(flavors));
        set.retainAll(acceptedFlavors);
        return !set.isEmpty();
    }

    /** Update the appearance of the target component.  Normally the decoration
     * should be painted only if the event is an instance of 
     * {@link DropTargetDragEvent} with an action that is not 
     * {@link DragHandler#NONE}.  Otherwise the decoration should be removed
     * or hidden.
     * <p>
     * For an easy way to highlight the drop target, consider using a single
     * instance of <code>AbstractComponentDecorator</code> and moving it 
     * according to the intended drop location.
     * @param e The drop target event
     * @param action The action for the drop  
     * @param location The intended drop location, or null if there is none
     */
    protected void paintDropTarget(DropTargetEvent e, int action, Point location) { 
        if (painter != null) {
            painter.paintDropTarget(e, action, location);
        }
    } 

    /** Indicate whether the given drop action is acceptable at the given
     * location.  This method is the last check performed by 
     * {@link #getDropAction(DropTargetEvent)}.
     * You may override this method to refuse drops on certain areas
     * within the drop target component.  The default always returns true.
     */ 
    protected boolean canDrop(DropTargetEvent e, int action, Point location) { 
        return true;
    }
    
    /** Handle an incoming drop with the given action.  The action passed in
     * might be different from {@link DropTargetDropEvent#getDropAction}, 
     * for instance, if there are no modifiers and the default action is not 
     * supported.  Calling {@link DropTargetDropEvent#dropComplete} is
     * recommended as soon as the {@link Transferable} data is obtained; this
     * allows the drag source to reset the cursor and any drag images which
     * may be in effect.
     */
    protected abstract void drop(DropTargetDropEvent e, int action) throws UnsupportedFlavorException, IOException;
}