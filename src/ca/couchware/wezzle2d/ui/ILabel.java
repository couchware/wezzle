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
public interface ILabel extends IEntity 
{

    /**
     * Get the color of the label.
     */
    public Color getColor();
    
    /**
     * Get the size of the label.
     */
    public int getSize();
    
    /**
     * Get the text contained in the label.
     */
    public String getText();     
    
    /**
     * Is the label cached in some way?
     */
    public boolean isCached();
    
}
