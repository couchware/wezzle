/*
 *  Wezzle
 *  Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.menu;

import ca.couchware.wezzle2d.Game;
import ca.couchware.wezzle2d.LayerManager;
import ca.couchware.wezzle2d.LayerManager.Layer;
import ca.couchware.wezzle2d.animation.FinishedAnimation;
import ca.couchware.wezzle2d.animation.IAnimation;
import ca.couchware.wezzle2d.animation.MoveAnimation;
import ca.couchware.wezzle2d.ui.Window;
import ca.couchware.wezzle2d.ui.group.AbstractGroup;
import java.util.EnumSet;

/**
 * The play now group, which holds all the configuration options for playing
 * a Wezzle game.
 * 
 * @author cdmckay
 */
public class PlayNowGroup extends AbstractGroup
{

    /**
     * The background window.
     */
    Window win;
    
    public PlayNowGroup(LayerManager layerMan)
    {
        // Invoke super.
        super(layerMan);
        
        // Create the window.
        win = new Window.Builder(268, 300).width(430).height(470)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                .opacity(90).visible(false).end();
        layerMan.add(win, Layer.UI);
    }
    
    @Override
    public IAnimation animateShow()
    {       
        win.setXYPosition(268, -300);
        win.setVisible(true);        
        
        IAnimation a = new MoveAnimation.Builder(win).theta(-90).maxY(300)
                .v(1.0).end();
        
        
        return a;
    }
    
    @Override
    public IAnimation animateHide()
    {        
        IAnimation a = new MoveAnimation.Builder(win).theta(90).minY(-300)
                .v(1.0).end();
        
        return a;
    }
        
    public void updateLogic(Game game)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
