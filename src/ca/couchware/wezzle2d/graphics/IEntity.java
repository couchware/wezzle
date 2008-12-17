/*
 *  Wezzle
 *  Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.graphics;

import ca.couchware.wezzle2d.util.ImmutablePosition;

/**
 *
 * @author cdmckay
 */
public interface IEntity extends IDrawable, IPositionable
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
     * Rotates the image by theta.
     */
    public void setRotation(double theta);
    
    /**
     * Gets the current theta.
     */
    public double getRotation();
    
    public void setRotationAnchor(int tx, int ty);    
    public ImmutablePosition getRotationAnchor();
    
    /**
     * Sets the disabled status of the entity.
     */
    public void setDisabled(boolean disabled);
    
    /**
     * Gets the disabled status of the entity.
     */
    public boolean isDisabled();
    
}
