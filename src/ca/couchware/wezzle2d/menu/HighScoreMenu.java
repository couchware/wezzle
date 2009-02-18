/*
 *  Wezzle
 *  Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.menu;

import ca.couchware.wezzle2d.Game;
import ca.couchware.wezzle2d.ManagerHub;
import ca.couchware.wezzle2d.ResourceFactory.LabelBuilder;
import ca.couchware.wezzle2d.graphics.IEntity;
import ca.couchware.wezzle2d.manager.LayerManager;
import ca.couchware.wezzle2d.manager.LayerManager.Layer;
import ca.couchware.wezzle2d.manager.Settings.Key;
import ca.couchware.wezzle2d.ui.Box;
import ca.couchware.wezzle2d.ui.ITextLabel;
import java.awt.Color;
import java.util.EnumSet;

/**
 * The high score menu, which holds all the high scores for the user to peruse
 * at his/her leisureee... so... leisurely.
 * 
 * @author cdmckay
 */
public class HighScoreMenu extends AbstractMenu
{

    /**
     * The "no high score" array.
     */
    private ITextLabel[] noHighScoreArray;

    public HighScoreMenu(IMenu parent, ManagerHub hub, LayerManager menuLayerMan)
    {
        // Invoke super.
        super(parent, hub, menuLayerMan);

        // The colors.
        final Color LABEL_COLOR  = hub.settingsMan.getColor(Key.GAME_COLOR_PRIMARY);
        final Color OPTION_COLOR = hub.settingsMan.getColor(Key.GAME_COLOR_SECONDARY);

        // The title label.
        ITextLabel titleLabel = new LabelBuilder(74, 97)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.LEFT))
                .color(LABEL_COLOR).text("High Scores").size(20)
                .visible(false).end();
        this.entityList.add(titleLabel);

         // The box.
        Box optionBox = new Box.Builder(68, 122)
                .width(400).height(398)
                .border(Box.Border.MEDIUM)
                .opacity(80)
                .visible(false)
                .end();
        this.entityList.add(optionBox);

        // Create the no high score label.
        this.noHighScoreArray = new ITextLabel[2];

        this.noHighScoreArray[0] = new LabelBuilder(268, 306)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                .color(hub.settingsMan.getColor(Key.GAME_COLOR_PRIMARY))
                .size(20).text("There are no")
                .visible(false).end();

        this.noHighScoreArray[1] = new LabelBuilder(268, 336)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                .color(hub.settingsMan.getColor(Key.GAME_COLOR_PRIMARY))
                .size(20).text("high scores yet.")
                .visible(false).end();

        for (ITextLabel label : noHighScoreArray)
            this.entityList.add(label);

        // Add them all to the layer manager.
        for (IEntity entity : this.entityList)
        {
            this.menuLayerMan.add(entity, Layer.UI);
        }
    }       
        
    public void updateLogic(Game game, ManagerHub hub)
    { }

}
