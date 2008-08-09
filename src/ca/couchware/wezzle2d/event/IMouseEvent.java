/*
 *  Wezzle
 *  Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.event;

import ca.couchware.wezzle2d.util.ImmutablePosition;
import java.util.EnumSet;

/**
 * An event for describing mouse events.
 * 
 * @author cdmckay
 */
public interface IMouseEvent
{
    public enum Type
    {
        MOUSE_CLICKED,
        MOUSE_DRAGGED,
        MOUSE_ENTERED,
        MOUSE_EXITED,
        MOUSE_MOVED,
        MOUSE_PRESSED,
        MOUSE_RELEASED,
        MOUSE_WHEEL
    }
    
    public enum Button
    {
        NONE,
        LEFT,
        MIDDLE,
        RIGHT
    }
    
    public enum Modifier
    {
        ALT,
        CTRL,
        SHIFT,
        META
    }
    
    /**
     * Gets which buttons were were pressed at the time of the event.
     * 
     * @return
     */
    public Button getButton();
    
    /**
     * Gets the modifier keys that were pressed at the time of the event.
     * 
     * @return
     */
    public EnumSet<Modifier> getModifierState();
    
    /**
     * Gets the the (x,y) position of the pointer at the time of the event.
     * 
     * @return
     */
    public ImmutablePosition getPosition();
    
    /**
     * Get the x-coordinate at the time of the event.
     * 
     * @return
     */
    public int getX();
    
    /**
     * Get the y-coordinate at the time of the event.
     * 
     * @return
     */
    public int getY();
    
    /**
     * Get the source that generated the event.
     */
    public Object getSource();
    
    /**
     * Get the type of the mouse event.
     */
    public Type getType();
}
