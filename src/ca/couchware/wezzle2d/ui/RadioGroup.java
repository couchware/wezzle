/*
 *  Wezzle
 *  Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.ui;

import ca.couchware.wezzle2d.graphics.AbstractEntity;
import java.util.List;

/**
 * A UI element for representing a group of RadioItems.
 * Typically used for allowing the user to select among mutually exclusive
 * choices.
 * 
 * For exampe:
 * 
 *   Music Theme:
 *   (*) Type A   ( ) Type B   ( ) Type C
 * 
 * @author cdmckay
 */
public class RadioGroup extends AbstractEntity
{
    /** The list of radio items. */
    private List<RadioItem> radioItemList;

    
    
    @Override
    public void draw()
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }        
}
