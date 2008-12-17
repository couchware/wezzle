/*
 *  Wezzle
 *  Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.menu;

import ca.couchware.wezzle2d.Game;
import ca.couchware.wezzle2d.manager.LayerManager;
import ca.couchware.wezzle2d.manager.LayerManager.Layer;
import ca.couchware.wezzle2d.animation.IAnimation;
import ca.couchware.wezzle2d.animation.MoveAnimation;
import ca.couchware.wezzle2d.manager.Settings.Key;
import ca.couchware.wezzle2d.manager.SettingsManager;
import ca.couchware.wezzle2d.ui.Box;
import ca.couchware.wezzle2d.ui.group.AbstractGroup;
import java.util.EnumSet;

/**
 * The play now group, which holds all the options for playing
 * a Wezzle game.
 * 
 * @author cdmckay
 */
public class TutorialGroup extends AbstractGroup
{

    /** The settings manager. */
    final private SettingsManager settingsMan;
    
    /** The layer manager. */
    final private LayerManager layerMan;
    
    /** The background window. */
    Box win;
    
    public TutorialGroup(
            SettingsManager settingsMan,
            LayerManager layerMan)
    {
        // Set the layer manager.
        this.settingsMan = settingsMan;
        this.layerMan    = layerMan;
        
         // Create the window.
        win = new Box.Builder(268, 300).width(430).height(470)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                .opacity(settingsMan.getInt(Key.MAIN_MENU_WINDOW_OPACITY)).visible(false).end();
        this.layerMan.add(win, Layer.UI);         
    }
    
    @Override
    public IAnimation animateShow()
    {       
        win.setPosition(268, -300);
        win.setVisible(true);        
        
        IAnimation a = new MoveAnimation.Builder(win).theta(-90).maxY(300)
                .speed(settingsMan.getInt(Key.MAIN_MENU_WINDOW_SPEED)).end();   
                
        return a;
    }
    
    @Override
    public IAnimation animateHide()
    {        
        IAnimation a = new MoveAnimation.Builder(win).theta(-90)
                .maxY(Game.SCREEN_HEIGHT + 300)
                .speed(settingsMan.getInt(Key.MAIN_MENU_WINDOW_SPEED)).end();
        
        return a;
    }
        
    public void updateLogic(Game game)
    {
        
    }

}
