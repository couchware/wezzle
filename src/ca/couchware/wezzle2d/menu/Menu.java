/*
 *  Wezzle
 *  Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.menu;

import ca.couchware.wezzle2d.Game;
import ca.couchware.wezzle2d.IGameWindow;
import ca.couchware.wezzle2d.LayerManager;
import ca.couchware.wezzle2d.LayerManager.Layer;
import ca.couchware.wezzle2d.ResourceFactory.LabelBuilder;
import ca.couchware.wezzle2d.graphics.GraphicEntity;
import ca.couchware.wezzle2d.graphics.IEntity;
import ca.couchware.wezzle2d.graphics.IPositionable.Alignment;
import ca.couchware.wezzle2d.ui.ILabel;
import java.util.EnumSet;
import java.util.List;

/**
 * An interface for creating menu hierarchies.
 * 
 * @author cdmckay
 */
public class Menu
{
    
    /** 
     * The standard menu background used by the loader. 
     */
    final private static String BACKGROUND_PATH = Game.SPRITES_PATH 
            + "/MenuBackground.png";
    
    /**
     * The list of menu options, in order, associated with this menu.
     */
    protected List<MenuOption> optionList;    
    
    /**
     * The list of entities in this menu.
     */
    protected List<IEntity> entityList;

    /**
     * The layer manager for this menu.
     */
    protected LayerManager layerMan;    
    
    /**
     * The graphic entity representing the background.
     */
    private GraphicEntity backgroundGraphic;
    
    public Menu(IGameWindow window)
    {
        // Create the menu layer manager.
        this.layerMan = new LayerManager(window);
        
        // Add the default background.
        backgroundGraphic = new GraphicEntity.Builder(0, 0, BACKGROUND_PATH).end();
        layerMan.add(backgroundGraphic, Layer.BACKGROUND);
        
        // A temporary label.
        ILabel label;                
        
        // Set up the copyright label.               
        label = new LabelBuilder(10, 600 - 10)
                .alignment(EnumSet.of(Alignment.BOTTOM, Alignment.LEFT))
                .color(Game.TEXT_COLOR).size(12)                
                .text(Game.COPYRIGHT).end();
        layerMan.add(label, Layer.UI);
        
        // Set up the version label.	
        label = new LabelBuilder(800 - 10, 600 - 10)
                .alignment(EnumSet.of(Alignment.BOTTOM, Alignment.RIGHT))
                .color(Game.TEXT_COLOR).size(12)                
                .text(Game.TITLE).end();                        
        layerMan.add(label, Layer.UI);        
    }
    
    /**
     * Returns the options the user has selected, or null for no selection.
     * 
     * @return
     */
    public MenuOption getSelectedOption()
    {
        return null;
    }
    
}
