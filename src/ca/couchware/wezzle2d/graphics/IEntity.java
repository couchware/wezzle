/*
 *  Wezzle
 *  Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.graphics;

import ca.couchware.wezzle2d.util.ImmutablePosition;

/**
 * An interface for describing any entity that appears in the game.
 * 
 * @author cdmckay
 */
public interface IEntity extends IDisposable, IDrawable, IPositionable
{
    
    /**
     * Sets the opacity of the sprite (in percent).
     * 
     * @param opacity The opacity.
     */
    public void setOpacity(final int opacity);
    
    /**
     * Gets the opacity of the sprite.
     * 
     * @return The opacity.
     */
    public int getOpacity();
    
    /**
     * Rotates the entity by theta.
     */
    public void setRotation(double theta);
    
    /**
     * Gets the current theta.
     */
    public double getRotation();
    
    /**
     * Sets the point that the entity should be rotated about.
     * 
     * @param tx
     * @param ty
     */
    public void setRotationAnchor(int tx, int ty);    
    
    /**
     * Gets the point that entity will be rotated about.
     * 
     * @return
     */
    public ImmutablePosition getRotationAnchor();
    
    /**
     * Sets the disabled status of the entity.
     */
    public void setDisabled(boolean disabled);
    
    /**
     * Gets the disabled status of the entity.
     */
    public boolean isDisabled();  
    
    /**
     * Dispose of any resources the entity has.  Call just before 
     * de-referencing.
     */
    public void dispose();
    
}
