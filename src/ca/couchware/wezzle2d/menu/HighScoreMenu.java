/*
 *  Wezzle
 *  Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.menu;

import ca.couchware.wezzle2d.Game;
import ca.couchware.wezzle2d.ManagerHub;
import ca.couchware.wezzle2d.manager.LayerManager;

/**
 * The play now group, which holds all the options for playing
 * a Wezzle game.
 * 
 * @author cdmckay
 */
public class HighScoreMenu extends AbstractMenu
{
   
    public HighScoreMenu(IMenu parent, ManagerHub hub, LayerManager menuLayerMan)
    {
        // Invoke super.
        super(parent, hub, menuLayerMan);
    }       
        
    public void updateLogic(Game game, ManagerHub hub)
    { }

}
