/*
 *  Wezzle
 *  Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.menu;

import ca.couchware.wezzle2d.Game;
import ca.couchware.wezzle2d.ResourceFactory.LabelBuilder;
import ca.couchware.wezzle2d.manager.LayerManager;
import ca.couchware.wezzle2d.manager.LayerManager.Layer;
import ca.couchware.wezzle2d.animation.FinishedAnimation;
import ca.couchware.wezzle2d.animation.IAnimation;
import ca.couchware.wezzle2d.animation.MoveAnimation;
import ca.couchware.wezzle2d.graphics.IEntity;
import ca.couchware.wezzle2d.ui.IButton;
import ca.couchware.wezzle2d.ui.ILabel;
import ca.couchware.wezzle2d.ui.RadioGroup;
import ca.couchware.wezzle2d.ui.RadioItem;
import ca.couchware.wezzle2d.ui.SpriteButton;
import ca.couchware.wezzle2d.ui.Window;
import ca.couchware.wezzle2d.ui.group.AbstractGroup;
import ca.couchware.wezzle2d.ui.group.IGroup;
import java.awt.Color;
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
     * The color of the play now options labels.
     */
    final private static Color LABELS_COLOR = Game.TEXT_COLOR1;
    
    /**
     * The color of the play now options.
     */
    final private static Color OPTIONS_COLOR = Game.TEXT_COLOR2;
    
    /**
     * The minimum level the user can select.
     */
    final private static int MIN_LEVEL = 1;
    
    /*
     * The maximum level the user can select.
     */
    final private static int MAX_LEVEL = 15;
    
    /**
     * The layer manager.
     */
    final private LayerManager layerMan;
    
    /**
     * The level down button.
     */
    final private IButton levelDownButton;
    
    /**
     * The level up button.
     */
    final private IButton levelUpButton;
    
    /**
     * The level label.
     */
    private ILabel levelNumberLabel;
    
//    /**
//     * The level limit radio items.
//     */
//    private enum LevelLimit { LEVEL_20, LEVEL_N }
//    
//    /**
//     * The level limit radio group.
//     */
//    final private RadioGroup<LevelLimit> levelLimitRadio;            
    
    /**
     * The music radio items.
     */
    private enum Music { A, B, C, All, Random }
    
    /**
     * The music radio group.
     */
    final private RadioGroup musicRadio;        
    
    /**
     * The start button.
     */
    final private IButton startButton;
    
    /**
     * The background window.
     */
    private Window win;
    
    public PlayNowGroup(IGroup parent, LayerManager layerMan)
    {
        // Invoke the super.
        super(parent);
        
        // Set the layer manager.
        this.layerMan = layerMan;
        
        // Create the window.
        win = new Window.Builder(268, 300).width(430).height(470)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                .opacity(MainMenu.WINDOW_OPACITY).visible(false).end();
        layerMan.add(win, Layer.UI);
        
        // The label spacing.
        final int SPACING = 60;
        
        // Create the name label.
        ILabel nl = new LabelBuilder(85, 125)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.LEFT))
                .color(LABELS_COLOR).text("Name").size(20f)
                .visible(false).end();
        this.entityList.add(nl);
        
        // Create the temporary test name.
//        Window w1 = new Window.Builder(355, nl.getY()).width(200).height(50)
//                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
//                .opacity(80)
//                .visible(false).end();                        
//        this.entityList.add(w1); 
        
        ILabel tnl = new LabelBuilder(355, nl.getY())
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                .color(OPTIONS_COLOR).text("TEST").size(20f)
                .visible(false).end();
        this.entityList.add(tnl);
        
        // Create the level label.
        ILabel ll = new LabelBuilder(nl).y(nl.getY() + SPACING * 1).text("Level").end();
        this.entityList.add(ll);
        
        // Create the level number label.
//        Window w2 = new Window.Builder(tnl.getX(), ll.getY()).width(200).height(50)
//                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
//                .opacity(80)
//                .visible(false).end();                        
//        this.entityList.add(w2); 
        
        this.levelNumberLabel = new LabelBuilder(tnl).xy(tnl.getX(), ll.getY())
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                .text("1").end();
        this.entityList.add(this.levelNumberLabel);
        
        // Create the level down button.
        this.levelDownButton = new SpriteButton.Builder(this.levelNumberLabel.getX() - 55, ll.getY())
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.LEFT))
                .type(SpriteButton.Type.SMALL_CIRCULAR).offOpacity(90)
                .text("-").visible(false).end();
        this.entityList.add(this.levelDownButton);
        
        // Create the level up button.
        this.levelUpButton = new SpriteButton.Builder((SpriteButton) levelDownButton)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.RIGHT))
                .x(this.levelNumberLabel.getX() + 55).text("+").end();
        this.entityList.add(this.levelUpButton);                       
              
        // Create the music label.
        ILabel ml = new LabelBuilder(nl).y(nl.getY() + SPACING * 2).text("Music Theme").end();
        this.entityList.add(ml);
        
        // Create a window background for this option.
        Window w = new Window.Builder(268, ml.getY() + SPACING).width(380).height(70)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                .opacity(80)
                .visible(false).end();                        
        this.entityList.add(w);        
        
        // Creat the level limit radio group.        
        RadioItem musicItem1 = new RadioItem.Builder().color(OPTIONS_COLOR)
                .text("A").end();
        RadioItem musicItem2 = new RadioItem.Builder().color(OPTIONS_COLOR)
                .text("B").end();
        RadioItem musicItem3 = new RadioItem.Builder().color(OPTIONS_COLOR)
                .text("C").end();
        RadioItem musicItem4 = new RadioItem.Builder().color(OPTIONS_COLOR)
                .text("All").end();
        RadioItem musicItem5 = new RadioItem.Builder().color(OPTIONS_COLOR)
                .text("?").end();
        this.musicRadio = new RadioGroup.Builder<Music>(268, ml.getY() + SPACING, Music.class)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))                
                .add(Music.A, musicItem1, true)
                .add(Music.B, musicItem2)
                .add(Music.C, musicItem3)
                .add(Music.All, musicItem4)
                .add(Music.Random, musicItem5)
                .pad(20).visible(false).end();
        this.entityList.add(musicRadio);       
        
        // Create the start button.
        this.startButton = new SpriteButton.Builder(266, 435)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                .type(SpriteButton.Type.LARGE).visible(false).offOpacity(90)
                .text("Start").end();
        this.entityList.add(this.startButton);
                
        // Add them all to the layer manager.
        for (IEntity e : this.entityList)
            this.layerMan.add(e, Layer.UI);        
    }
    
    @Override
    public IAnimation animateShow()
    {       
        win.setXYPosition(268, -300);
        win.setVisible(true);        
        
        IAnimation a = new MoveAnimation.Builder(win).theta(-90)
                .maxY(300).v(MainMenu.WINDOW_SPEED).end();
        
        a.setFinishAction(new Runnable()
        {
           public void run()
           { setVisible(true); }
        });
        
        return a;
    }
    
    @Override
    public IAnimation animateHide()
    {        
        IAnimation a = new MoveAnimation.Builder(win).theta(-90)
                .maxY(Game.SCREEN_HEIGHT + 300).v(MainMenu.WINDOW_SPEED).end();
        
        a.setStartAction(new Runnable()
        {
           public void run()
           { setVisible(false); }
        });
        
        return a;
    }
        
    public void updateLogic(Game game)
    {
        // See if the level down button was clicked.
        if (this.levelDownButton.clicked() == true)
        {
            // Reset the level down button.
            this.levelDownButton.setActivated(false);
            
            // Extract the current setting.
            int level = Integer.valueOf(this.levelNumberLabel.getText()) - 1;
            level = level < MIN_LEVEL ? MIN_LEVEL : level;
            
            // Remove old label.
            this.entityList.remove(this.levelNumberLabel);
            this.layerMan.remove(this.levelNumberLabel, Layer.UI);
            
            // Add new label.
            this.levelNumberLabel = new LabelBuilder(this.levelNumberLabel)
                    .text(String.valueOf(level)).end();
            
            // Add it.
            this.entityList.add(this.levelNumberLabel);
            this.layerMan.add(this.levelNumberLabel, Layer.UI);
        }
        // See if the level up button was clicked.
        else if (this.levelUpButton.clicked() == true)
        {
            // Reset the level up button.
            this.levelUpButton.setActivated(false);
            
            // Extract the current setting.
            int level = Integer.valueOf(this.levelNumberLabel.getText()) + 1;
            level = level > MAX_LEVEL ? MAX_LEVEL : level;
            
            // Remove old label.
            this.entityList.remove(this.levelNumberLabel);
            this.layerMan.remove(this.levelNumberLabel, Layer.UI);
            
            // Add new label.
            this.levelNumberLabel = new LabelBuilder(this.levelNumberLabel)
                    .text(String.valueOf(level)).end();
            
            // Add it.
            this.entityList.add(this.levelNumberLabel);
            this.layerMan.add(this.levelNumberLabel, Layer.UI);
        }    
        // See if the start button has been pressed.
        else if (this.startButton.clicked() == true)
        {
            // Notify the main menu.
            this.parent.setActivated(false);
        }
    }
    
}
