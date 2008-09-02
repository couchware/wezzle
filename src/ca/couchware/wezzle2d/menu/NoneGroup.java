/*
 *  Wezzle
 *  Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.menu;

import ca.couchware.wezzle2d.Game;
import ca.couchware.wezzle2d.manager.LayerManager;
import ca.couchware.wezzle2d.ui.group.AbstractGroup;

/**
 * This group is used to represent when the menu has no option selected.
 * 
 * @author cdmckay
 */
public class NoneGroup extends AbstractGroup
{   
    
    public NoneGroup(LayerManager layerMan)
    {
        // Invoke super.
        super(layerMan);      
    }       
        
    public void updateLogic(Game game)
    {
        // Intentionally left blank.
    }

}
