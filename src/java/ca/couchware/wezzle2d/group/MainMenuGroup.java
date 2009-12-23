/*
 *  Wezzle
 *  Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.group;

import ca.couchware.wezzle2d.Game;
import ca.couchware.wezzle2d.Game.TransitionTarget;
import ca.couchware.wezzle2d.ManagerHub;
import ca.couchware.wezzle2d.manager.SettingsManager;
import ca.couchware.wezzle2d.manager.LayerManager.Layer;
import ca.couchware.wezzle2d.ResourceFactory.LabelBuilder;
import ca.couchware.wezzle2d.manager.Settings.Key;
import ca.couchware.wezzle2d.ui.Button;
import ca.couchware.wezzle2d.ui.IButton;
import ca.couchware.wezzle2d.ui.ITextLabel;
import ca.couchware.wezzle2d.ui.RadioGroup;
import ca.couchware.wezzle2d.ui.RadioItem;
import ca.couchware.wezzle2d.ui.SliderBar;
import java.awt.Color;
import java.util.EnumSet;

/**
 * The sound and music menu for Wezzle.  This menu consists of two buttons 
 * for turning the music on and off, and two sliders, for adjusting the music
 * volume.
 * 
 * @author cdmckay
 */
public class MainMenuGroup extends AbstractGroup
{           

    private ManagerHub hub;

    private ITextLabel headerLabel;                     

    private IButton yesButton;
    private IButton noButton;
        
    public MainMenuGroup(ManagerHub hub)
    {
        if (hub == null)
            throw new IllegalArgumentException("Hub must not be null");

        this.hub = hub;

        // The colors.
        final Color LABEL_COLOR  = hub.settingsMan.getColor(Key.GAME_COLOR_PRIMARY);
        final Color OPTION_COLOR = hub.settingsMan.getColor(Key.GAME_COLOR_SECONDARY);

        // Create the options header.
        headerLabel = new LabelBuilder(400, 171)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                .color(LABEL_COLOR).size(26)
                .text("Main Menu").visible(false).build();
        hub.layerMan.add(headerLabel, Layer.UI);
        entityList.add(headerLabel);

        final int labelStartY = 233;
        final int labelSpacing = 30;

        // Line 1.
        ITextLabel label1 = new LabelBuilder(400, labelStartY)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                .color(OPTION_COLOR)
                .size(18)
                .visible(false)
                .text("Are you sure")
                .build();

        hub.layerMan.add(label1, Layer.UI);
        this.entityList.add(label1);

        // Line 2.
        ITextLabel label2 =  new LabelBuilder(400, label1.getY() + labelSpacing)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                .color(OPTION_COLOR)
                .size(18)
                .visible(false)
                .text("you want to return")
                .build();

        hub.layerMan.add(label2, Layer.UI);
        this.entityList.add(label2);

        // Line 2.
        ITextLabel label3 =  new LabelBuilder(400, label2.getY() + labelSpacing)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                .color(OPTION_COLOR)
                .size(18)
                .visible(false)
                .text("to the main menu?")
                .build();

        hub.layerMan.add(label3, Layer.UI);
        this.entityList.add(label3);

        Button templateButton = new Button.Builder(400, 0)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                .text("").normalOpacity(80).visible(false).build();

        yesButton = new Button.Builder(templateButton).y(366)
                .text("Yes").build();
        hub.layerMan.add(yesButton, Layer.UI);
        entityList.add(yesButton);
        
        noButton = new Button.Builder(templateButton).y(420)
                .text("No").build();
        hub.layerMan.add(noButton, Layer.UI);
        entityList.add(noButton);
    }
    
    /**
     * Override the update logic method.
     * 
     * @param game The game state.
     */    
    @Override
    public void updateLogic(Game game, ManagerHub hub)
    {
        super.updateLogic( game, hub );
        
        // Sanity check.
        if (game == null)
            throw new IllegalArgumentException("Game must not be null");

        if (hub == null)
            throw new IllegalArgumentException("Hub must not be null");

        // Make sure something changed.
        if ( !this.controlChanged() ) return;
        
        if ( false ) { }
        else if ( noButton.isActivated() )
        {
            noButton.setActivated( false );
            hub.groupMan.hideGroup( this, !game.isCompletelyBusy() );
        }
        else if ( yesButton.isActivated() )
        {
            yesButton.setActivated( false );

            // Stop the tutorial if necessary.
            if (hub.tutorialMan.isTutorialRunning())
            {
                hub.tutorialMan.finishTutorial(game, hub);
            }

            // Disable the layer manager.
            hub.layerMan.setDisabled(true);

            // Start the transition.
            game.startTransitionTo(TransitionTarget.MENU);
        }

        // Clear the change setting.
        this.clearChanged();
    }
}
