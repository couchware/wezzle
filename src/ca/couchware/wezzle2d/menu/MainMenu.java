/*
 *  Wezzle
 *  Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.menu;

import ca.couchware.wezzle2d.Game;
import ca.couchware.wezzle2d.IGameWindow;
import ca.couchware.wezzle2d.LayerManager.Layer;
import ca.couchware.wezzle2d.graphics.GraphicEntity;

/**
 *
 * @author cdmckay
 */
public class MainMenu extends Menu
{

    /**
     * The path to the Wezzle logo graphic.
     */
    final private static String LOGO_PATH = Game.SPRITES_PATH + "/WezzleLogo.png";
    
    /**
     * The Wezzle logo graphic.
     */
    private GraphicEntity logoGraphic;
    
    public MainMenu(IGameWindow window)
    {
        // Invoke super.
        super(window);
        
        // Create the logo graphic.
        logoGraphic = new GraphicEntity.Builder(268, 300, LOGO_PATH).end();
        layerMan.add(logoGraphic, Layer.UI);
        entityList.add(logoGraphic);
    }
    
}
