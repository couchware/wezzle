/*
 *  Wezzle
 *  Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.menu;

import ca.couchware.wezzle2d.Game;
import ca.couchware.wezzle2d.ManagerHub;
import ca.couchware.wezzle2d.ResourceFactory.LabelBuilder;
import ca.couchware.wezzle2d.animation.AnimationAdapter;
import ca.couchware.wezzle2d.manager.LayerManager;
import ca.couchware.wezzle2d.manager.LayerManager.Layer;
import ca.couchware.wezzle2d.animation.IAnimation;
import ca.couchware.wezzle2d.animation.MoveAnimation;
import ca.couchware.wezzle2d.graphics.IEntity;
import ca.couchware.wezzle2d.manager.Settings.Key;
import ca.couchware.wezzle2d.manager.SettingsManager;
import ca.couchware.wezzle2d.ui.IButton;
import ca.couchware.wezzle2d.ui.ITextLabel;
import ca.couchware.wezzle2d.ui.Button;
import ca.couchware.wezzle2d.ui.Box;
import ca.couchware.wezzle2d.group.AbstractGroup;
import java.awt.Color;
import java.util.EnumSet;

/**
 * The exit group shows a dialog asking whether the user really wants to exit.
 * 
 * @author cdmckay
 */
public class ExitGameMenu extends AbstractMenu
{
      
    /** The "Yes" button. */
    final private IButton yesButton;
    
    /** The "No" button. */
    final private IButton noButton;
    
    /**
     * The constructor.
     * @param layerMan
     */    
    public ExitGameMenu(IMenu parent, ManagerHub hub, LayerManager menuLayerMan)
    {
        // Invoke super.
        super(parent, hub, menuLayerMan);

        // The colors.
        final Color LABEL_COLOR  = hub.settingsMan.getColor(Key.GAME_COLOR_PRIMARY);
        final Color OPTION_COLOR = hub.settingsMan.getColor(Key.GAME_COLOR_SECONDARY);

        // The title label.
        ITextLabel titleLabel = new LabelBuilder(74, 97)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.LEFT))
                .color(LABEL_COLOR)
                .text("Confirmation").size(20)
                .visible( false ).build();

        this.entityList.add(titleLabel);

        // The box.
        Box optionBox = new Box.Builder(68, 122)
                .width(400).height(398)
                .border(Box.Border.MEDIUM)
                .opacity(80)
                .visible(false)
                .build();

        this.entityList.add(optionBox);

        final int labelStartY = 205;
        final int labelSpacing = 30;

        // Line 1.
        ITextLabel label1 = new LabelBuilder(266, labelStartY)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                .color(OPTION_COLOR)
                .size(22)
                .visible(false)
                .text("Are you sure")
                .build();

        this.entityList.add(label1);
        
        // Line 2.
        ITextLabel label2 =  new LabelBuilder(266, labelStartY + labelSpacing)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                .color(OPTION_COLOR)
                .size(22)
                .visible(false)
                .text("you want to exit?")
                .build();

        this.entityList.add(label2);

        final int buttonStartY  = 360;
        final int buttonSpacing = 60;

        Button templateButton = new Button.Builder(266, 400)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                .color(LABEL_COLOR)
                .normalOpacity(90)
                .visible( false )
                .text("")
                .width( 150 )
                .build();

        this.yesButton = new Button.Builder( templateButton )
                .y(buttonStartY)
                .text("Yes")
                .build();

        this.entityList.add(this.yesButton);
        
        // Add the "No" button.
        this.noButton = new Button.Builder( templateButton )
                .y(buttonStartY + buttonSpacing)
                .text("No")
                .build();

        this.entityList.add(this.noButton);
        
        // Add them all to the layer manager.
        for (IEntity e : this.entityList)
        {
            this.menuLayerMan.add(e, Layer.UI);
        }
    }        
        
    public void updateLogic(Game game, ManagerHub hub)
    {
        // See if any control was touched.
        // See if the "Yes" button was pressed.
        if (yesButton.isActivated() == true)
        {
            // Close the game.
            game.windowClosed();
        }
        // See if the "No" button was pressed.
        else if (noButton.isActivated() == true)
        {
            // Deactivate this group.
            setActivated(false);
        }
    }       

    @Override
    public void resetControls()
    {
        clearChanged();
        this.yesButton.setActivated(false);
        this.noButton.setActivated(false);
    }
};
