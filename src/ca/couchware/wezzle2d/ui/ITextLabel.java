/*
 *  Wezzle
 *  Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.ui;

import ca.couchware.wezzle2d.graphics.IEntity;
import java.awt.Color;

/**
 *
 * @author cdmckay
 */
public interface ITextLabel extends IEntity 
{

    /**
     * Get the color of the label.
     */
    public Color getColor();
    
    /**
     * Set the color of the label.
     */
    public void setColor(Color color);
    
    /**
     * Get the size of the label.
     */
    public int getSize();
    
    /**
     * Get the text contained in the label.
     */
    public String getText(); 
    
    /**
     * Set the text contained in the label.
     */
    public void setText(String text);
    
    /**
     * Is the label cached in some way?
     */
    public boolean isCached();
    
}
