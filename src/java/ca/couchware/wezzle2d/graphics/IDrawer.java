/*
 *  Wezzle
 *  Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.graphics;

import java.awt.Shape;

/**
 * An interface for describing an entity that can draw IDrawables.
 * 
 * @author cdmckay
 */
public interface IDrawer 
{
    /** 
     * Draws something to the screen. 
     * 
     * @return True if something was drawn, false otherwise.
     */
    public boolean draw();    
    public boolean draw(Shape region, boolean exact);
    public boolean draw(Shape region);
}
