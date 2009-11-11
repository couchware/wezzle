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
import java.util.EnumSet;

/**
 * The exit group shows a dialog asking whether the user really wants to exit.
 * 
 * @author cdmckay
 */
public class ExitMenu extends AbstractGroup
{

    /** The settings manager. */
    final private SettingsManager settingsMan;
    
    /** The layer manager. */
    final private LayerManager layerMan;
    
    /** The background window. */
    final private Box box;        
    
    /** The "Yes" button. */
    final private IButton yesButton;
    
    /** The "No" button. */
    final private IButton noButton;
    
    /**
     * The constructor.
     * @param layerMan
     */    
    public ExitMenu(
            SettingsManager settingsMan,
            LayerManager layerMan)
    {
        // Set the layer manager.
        this.settingsMan = settingsMan;
        this.layerMan    = layerMan;        
                        
        // Create the window.
        box = new Box.Builder(268, 300).width(430).height(470)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                .opacity(settingsMan.getInt(Key.MAIN_MENU_WINDOW_OPACITY))
                .visible(false).build();
        this.layerMan.add(box, Layer.UI);               
               
        // Line 1.
        ITextLabel l1 = new LabelBuilder(266, 155)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                .color(settingsMan.getColor(Key.GAME_COLOR_PRIMARY))
                .size(22).visible(false)
                .text("Are you sure").build();
        this.entityList.add(l1);
        
        // Line 2.
        ITextLabel l2 = new LabelBuilder(l1).y(l1.getY() + 30)
                .text("you want to exit?").build();
        this.entityList.add(l2);
        
        // Add the "Yes" button.
//        this.yesButton = new SpriteButton.Builder(174, 405)
//                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
//                .type(SpriteButton.Type.LARGE).visible(false).offOpacity(90)
//                .text("Yes").end();
//        this.entityList.add(this.yesButton);
//        
//        // Add the "No" button.
//        this.noButton = new SpriteButton.Builder((SpriteButton) yesButton).x(357)
//                .text("No").end();
//        this.entityList.add(this.noButton);
        
        this.yesButton = new Button.Builder(266, 400)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                .color(settingsMan.getColor(Key.GAME_COLOR_PRIMARY))
                //.type(SpriteButton.Type.THIN)
                .visible(false).normalOpacity(90)
                .text("Yes").build();
        this.entityList.add(this.yesButton);
        
        // Add the "No" button.
        this.noButton = new Button.Builder((Button) yesButton)
                .y(yesButton.getY() + 60).text("No").build();
        this.entityList.add(this.noButton);
        
        // Add them all to the layer manager.
        for (IEntity e : this.entityList)
            this.layerMan.add(e, Layer.UI);
    }
    
     @Override
    public IAnimation animateShow()
    {       
        box.setPosition(268, -300);
        box.setVisible(true);        
        
        IAnimation anim = new MoveAnimation.Builder(box).theta(-90).maxY(300)
                .speed(SettingsManager.get().getInt(Key.MAIN_MENU_WINDOW_SPEED))
                .build();
        
//        a.setFinishRunnable(new Runnable()
//        {
//           public void run()
//           { setVisible(true); }
//        });
        
        anim.addAnimationListener(new AnimationAdapter()
        {          
            @Override
            public void animationFinished()
            { setVisible(true); }
        });
        
        return anim;
    }
    
    @Override
    public IAnimation animateHide()
    {        
        IAnimation anim = new MoveAnimation.Builder(box).theta(-90)
                .maxY(Game.SCREEN_HEIGHT + 300)
                .speed(SettingsManager.get().getInt(Key.MAIN_MENU_WINDOW_SPEED))
                .build();
        
//        a.setStartRunnable(new Runnable()
//        {
//           public void run()
//           { setVisible(false); }
//        });
        
        anim.addAnimationListener(new AnimationAdapter()
        {
            @Override
            public void animationStarted()
            { setVisible(false); }
        });
        
        return anim;
    }
        
    public void updateLogic(Game game, ManagerHub hub)
    {
        // See if any control was touched.
        if (controlChanged() == true)
        {
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
        } // end if
    }
    
    @Override
    public void resetControls()
    {
        clearChanged();
        this.yesButton.setActivated(false);
        this.noButton.setActivated(false);
    }

};
