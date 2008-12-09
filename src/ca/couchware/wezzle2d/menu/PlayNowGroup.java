/*
 *  Wezzle
 *  Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.menu;

import ca.couchware.wezzle2d.Game;
import ca.couchware.wezzle2d.ResourceFactory.LabelBuilder;
import ca.couchware.wezzle2d.manager.LayerManager;
import ca.couchware.wezzle2d.manager.LayerManager.Layer;
import ca.couchware.wezzle2d.manager.LogManager;
import ca.couchware.wezzle2d.manager.MusicManager.Theme;
import ca.couchware.wezzle2d.animation.IAnimation;
import ca.couchware.wezzle2d.animation.MoveAnimation;
import ca.couchware.wezzle2d.audio.Music;
import ca.couchware.wezzle2d.audio.MusicPlayer;
import ca.couchware.wezzle2d.graphics.IEntity;
import ca.couchware.wezzle2d.manager.BoardManager.AnimationType;
import ca.couchware.wezzle2d.manager.MusicManager;
import ca.couchware.wezzle2d.manager.Settings;
import ca.couchware.wezzle2d.manager.Settings.Key;
import ca.couchware.wezzle2d.manager.SettingsManager;
import ca.couchware.wezzle2d.ui.IButton;
import ca.couchware.wezzle2d.ui.ILabel;
import ca.couchware.wezzle2d.ui.RadioGroup;
import ca.couchware.wezzle2d.ui.RadioItem;
import ca.couchware.wezzle2d.ui.Button;
import ca.couchware.wezzle2d.ui.Box;
import ca.couchware.wezzle2d.ui.TallButton;
import ca.couchware.wezzle2d.ui.group.AbstractGroup;
import ca.couchware.wezzle2d.ui.group.IGroup;
import ca.couchware.wezzle2d.util.Util;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javazoom.jlgui.basicplayer.BasicPlayerException;

/**
 * The play now group, which holds all the configuration options for playing
 * a Wezzle game.
 * 
 * @author cdmckay
 */
public class PlayNowGroup extends AbstractGroup
{
    
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
     * The music manager.
     */
    final private MusicManager musicMan;
    
    /**
     * The name button.
     */
    final private IButton nameButton;
    
    /**
     * The level down button.
     */
    final private IButton levelDownButton;
    
    /**
     * The level up button.
     */
    final private IButton levelUpButton;
    
    /** The level label. */
    private ILabel levelNumberLabel;    
    
    /** The level. */
    private int levelNumber = 1;
    
    /**
     * The music radio group.
     */
    final private RadioGroup<Theme> themeRadio;        
    
    /**
     * The music player map.
     */
    private Map<Theme, MusicPlayer> playerMap;   
    
    /**
     * The start button.
     */
    final private IButton startButton;
    
    /**
     * The background window.
     */
    private Box win;
    
    public PlayNowGroup(IGroup parent, 
            final SettingsManager settingsMan,
            final LayerManager layerMan, 
            final MusicManager musicMan)
    {
        // Invoke the super.
        super(parent);
               
        // Set the managers.
        this.layerMan = layerMan;
        this.musicMan = musicMan;
        
        // The colors.
        final Color LABEL_COLOR  = settingsMan.getColor(Key.GAME_COLOR_PRIMARY);
        final Color OPTION_COLOR = settingsMan.getColor(Key.GAME_COLOR_SECONDARY);
        
        // Create the window.
        win = new Box.Builder(268, 300).width(430).height(470)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                .opacity(SettingsManager.get().getInt(Key.MAIN_MENU_WINDOW_OPACITY))
                .visible(false).end();
        this.layerMan.add(win, Layer.UI);         
        
        // The label spacing.
        final int SPACING = 60;
        
        // Create the name label.
        ILabel nl = new LabelBuilder(85, 125)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.LEFT))
                .color(LABEL_COLOR).text("Name").size(20)
                .visible(false).end();
        this.entityList.add(nl);
        
        // Create the temporary test name.
        this.nameButton = new Button.Builder(355, nl.getY())
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                .color(OPTION_COLOR)
                .width(150)
                //.type(SpriteButton.Type.NORMAL)
                .visible(false).normalOpacity(90)
                .text("TEST").end();
        this.entityList.add(this.nameButton);
        
        // Create the level label.
        ILabel ll = new LabelBuilder(nl).y(nl.getY() + SPACING * 1).text("Level").end();
        this.entityList.add(ll);
        
        // Create the level number label.
        this.levelNumberLabel = new LabelBuilder(nameButton.getX(), ll.getY())                
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                .color(settingsMan.getColor(Key.GAME_COLOR_SECONDARY))
                .size(20).visible(false).text(String.valueOf(levelNumber)).end();
        this.entityList.add(this.levelNumberLabel);
        
        // Create the level down button.
        this.levelDownButton = new Button.Builder(this.levelNumberLabel.getX() - 55, ll.getY())
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.LEFT))                
                //.type(SpriteButton.Type.SMALL_CIRCULAR)
                .width(30)
                .normalOpacity(90)
                .text("-").visible(false).end();
        this.entityList.add(this.levelDownButton);
        
        // Create the level up button.
        this.levelUpButton = new Button.Builder((Button) levelDownButton)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.RIGHT)) 
                .width(30)
                .x(this.levelNumberLabel.getX() + 55).text("+").end();
        this.entityList.add(this.levelUpButton);                       
              
        // Create the music theme label.
        ILabel tl = new LabelBuilder(nl).y(nl.getY() + SPACING * 2).text("Music Theme").end();
        this.entityList.add(tl);
        
        // Create a window background for this option.
        Box w = new Box.Builder(268, tl.getY() + SPACING).width(380).height(70)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                .border(Box.Border.MEDIUM).opacity(80).visible(false).end();                    
        
        this.entityList.add(w);        
        
        // Create the music players.
        createPlayers();
        
        // Creat the level limit radio group.        
        RadioItem themeItem1 = new RadioItem.Builder().color(OPTION_COLOR)
                .text("A").end();
        themeItem1.setMouseOnRunnable(createFadeInRunnable(Theme.A));        
        themeItem1.setMouseOffRunnable(createFadeOutRunnable(Theme.A));
        
        RadioItem themeItem2 = new RadioItem.Builder().color(OPTION_COLOR)
                .text("B").end();
        themeItem2.setMouseOnRunnable(createFadeInRunnable(Theme.B));        
        themeItem2.setMouseOffRunnable(createFadeOutRunnable(Theme.B));
        
        RadioItem themeItem3 = new RadioItem.Builder().color(OPTION_COLOR)
                .text("C").end();
        themeItem3.setMouseOnRunnable(createFadeInRunnable(Theme.C));        
        themeItem3.setMouseOffRunnable(createFadeOutRunnable(Theme.C));
        
        Map<Theme, Boolean> themeMap = new EnumMap<Theme, Boolean>(Theme.class);
        themeMap.put(Theme.A, false);
        themeMap.put(Theme.B, false);
        themeMap.put(Theme.C, false);
        
        List<Theme> themeList = new ArrayList<Theme>(themeMap.keySet());
        Collections.shuffle(themeList);
        themeMap.put(themeList.get(0), true);
        
        RadioItem themeItem4 = new RadioItem.Builder().color(OPTION_COLOR)
                .text("All").end();
        RadioItem themeItem5 = new RadioItem.Builder().color(OPTION_COLOR)
                .text("?").end();
        this.themeRadio = new RadioGroup.Builder<Theme>(268, tl.getY() + SPACING, Theme.class)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))                
                .add(Theme.A, themeItem1, themeMap.get(Theme.A))
                .add(Theme.B, themeItem2, themeMap.get(Theme.B))
                .add(Theme.C, themeItem3, themeMap.get(Theme.C))
                .add(Theme.ALL, themeItem4)
                .add(Theme.RANDOM, themeItem5)
                .pad(20).visible(false).end();
        this.entityList.add(themeRadio);    
               
        // Create the start button.
        this.startButton = new TallButton.Builder(266, 435)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                .color(settingsMan.getColor(Key.GAME_COLOR_PRIMARY))
                //.type(SpriteButton.Type.LARGE)
                .visible(false).normalOpacity(90)
                .text("Start").end();
        this.entityList.add(this.startButton);
                
        // Add them all to the layer manager.
        for (IEntity e : this.entityList)
            this.layerMan.add(e, Layer.UI);        
    }
    
    private void createPlayers()
    {
        // Create the music player map.
        this.playerMap = new EnumMap<Theme, MusicPlayer>(Theme.class);
       
        // Create three players, 1 for each theme.
        this.playerMap.put(Theme.A, MusicManager.createPlayer(Music.TRON2));
        this.playerMap.put(Theme.B, MusicManager.createPlayer(Music.ELECTRONIC1));
        this.playerMap.put(Theme.C, MusicManager.createPlayer(Music.HIPPOP1));        
        
        try 
        {            
            for (MusicPlayer p : playerMap.values())
            {
                p.setLoop(true);
                p.play();        
                p.setNormalizedGain(0.0);
            }            
        }
        catch (BasicPlayerException e)
        {
            // TODO Should try to do more than this, but this is OK for now.
            LogManager.recordException(e);               
        }             
    }
    
    private Runnable createFadeInRunnable(final Theme theme)
    {
        return new Runnable()
        {
            public void run()
            { 
                playerMap.get(theme).fadeToGain(
                        SettingsManager.get().getDouble(Settings.Key.USER_MUSIC_VOLUME));
            }
        };
    }
    
    private Runnable createFadeOutRunnable(final Theme theme)
    {
        return new Runnable()
        {
            public void run()
            { 
                playerMap.get(theme).fadeToGain(0.0);
            }
        };
    }
    
    @Override
    public IAnimation animateShow()
    {       
        win.setXYPosition(268, -300);
        win.setVisible(true);        
        
        IAnimation a = new MoveAnimation.Builder(win).theta(-90).maxY(300)
                .speed(SettingsManager.get().getInt(Key.MAIN_MENU_WINDOW_SPEED))
                .end();   
        
        a.setFinishRunnable(new Runnable()
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
                .maxY(Game.SCREEN_HEIGHT + 300)
                .speed(SettingsManager.get().getInt(Key.MAIN_MENU_WINDOW_SPEED))
                .end();
        
        a.setStartRunnable(new Runnable()
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
            levelNumber = levelNumber - 1 < MIN_LEVEL 
                    ? MIN_LEVEL 
                    : levelNumber - 1;
            
            // Remove old label.
            this.entityList.remove(this.levelNumberLabel);
            this.layerMan.remove(this.levelNumberLabel, Layer.UI);
            
            // Add new label.
            this.levelNumberLabel = new LabelBuilder(this.levelNumberLabel)
                    .text(String.valueOf(levelNumber)).end();
            
            // Add it.
            this.entityList.add(this.levelNumberLabel);
            this.layerMan.add(this.levelNumberLabel, Layer.UI);                       
        }
        // See if the level up button was clicked.
        else if (this.levelUpButton.clicked() == true)
        {
            // Reset the level up button.
            this.levelUpButton.setActivated(false);
            
            // Determine new setting.
            levelNumber = levelNumber + 1 > MAX_LEVEL 
                    ? MAX_LEVEL 
                    : levelNumber + 1;
            
            // Remove old label.
            this.entityList.remove(this.levelNumberLabel);
            this.layerMan.remove(this.levelNumberLabel, Layer.UI);
            
            // Add new label.
            this.levelNumberLabel = new LabelBuilder(this.levelNumberLabel)
                    .text(String.valueOf(levelNumber)).end();
            
            // Add it.
            this.entityList.add(this.levelNumberLabel);
            this.layerMan.add(this.levelNumberLabel, Layer.UI);           
        }       
        // See if the start button has been pressed.
        else if (this.startButton.clicked() == true)
        {
            // Set the music.
            this.musicMan.setTheme(themeRadio.getSelectedKey());
            
            // Set the target score.
            game.worldMan.setLevel(levelNumber);
            game.scoreMan.setTargetLevelScore(game.scoreMan.generateTargetLevelScore(levelNumber));
            game.scoreMan.setTargetTotalScore(game.scoreMan.generateTargetLevelScore(levelNumber));
            //game.progressBar.setProgressMax(game.scoreMan.getTargetLevelScore());
                                      
            // Stop all the player.
            for (MusicPlayer p : playerMap.values())
            {                
                p.stopAtGain(0.0);
            }        
            
            game.startBoard();
            
              
            
            // Notify the main menu.
            this.parent.setActivated(false);
        }
    }
    
}
