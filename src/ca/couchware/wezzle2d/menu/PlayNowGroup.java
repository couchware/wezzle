/*
 *  Wezzle
 *  Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.menu;

import ca.couchware.wezzle2d.Conf;
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
import ca.couchware.wezzle2d.manager.MusicManager;
import ca.couchware.wezzle2d.manager.Settings;
import ca.couchware.wezzle2d.manager.SettingsManager;
import ca.couchware.wezzle2d.ui.IButton;
import ca.couchware.wezzle2d.ui.ILabel;
import ca.couchware.wezzle2d.ui.RadioGroup;
import ca.couchware.wezzle2d.ui.RadioItem;
import ca.couchware.wezzle2d.ui.SpriteButton;
import ca.couchware.wezzle2d.ui.Window;
import ca.couchware.wezzle2d.ui.group.AbstractGroup;
import ca.couchware.wezzle2d.ui.group.IGroup;
import java.awt.Color;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
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
     * The property manager.  This is needed to access the volume settings.
     */
    final private SettingsManager settingsMan;
    
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
    
    /**
     * The level label.
     */
    private ILabel levelNumberLabel;    
    
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
    private Window win;
    
    public PlayNowGroup(IGroup parent, 
            final SettingsManager settingsMan,
            final LayerManager layerMan, 
            final MusicManager musicMan)
    {
        // Invoke the super.
        super(parent);
        
        // Set the property manager.
        this.settingsMan = settingsMan;
        
        // Set the layer manager.
        this.layerMan = layerMan;
        
        // Set the music manager.
        this.musicMan = musicMan;
        
        // Create the window.
        win = new Window.Builder(268, 300).width(430).height(470)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                .opacity(Conf.MAIN_MENU_WINDOW_OPACITY).visible(false).end();
        this.layerMan.add(win, Layer.UI);         
        
        // The label spacing.
        final int SPACING = 60;
        
        // Create the name label.
        ILabel nl = new LabelBuilder(85, 125)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.LEFT))
                .color(LABELS_COLOR).text("Name").size(20)
                .visible(false).end();
        this.entityList.add(nl);
        
        // Create the temporary test name.
        this.nameButton = new SpriteButton.Builder(355, nl.getY())
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                .type(SpriteButton.Type.NORMAL).visible(false).offOpacity(90)
                .text("TEST").end();
        this.entityList.add(this.nameButton);
        
        // Create the level label.
        ILabel ll = new LabelBuilder(nl).y(nl.getY() + SPACING * 1).text("Level").end();
        this.entityList.add(ll);
        
        // Create the level number label.
        this.levelNumberLabel = new LabelBuilder(nameButton.getX(), ll.getY())                
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                .size(20).visible(false).text("1").end();
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
              
        // Create the music theme label.
        ILabel tl = new LabelBuilder(nl).y(nl.getY() + SPACING * 2).text("Music Theme").end();
        this.entityList.add(tl);
        
        // Create a window background for this option.
        Window w = new Window.Builder(268, tl.getY() + SPACING).width(380).height(70)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                .border(Window.Border.MEDIUM).opacity(80).visible(false).end();                    
        
        this.entityList.add(w);        
        
        // Create the music players.
        createPlayers();
        
        // Creat the level limit radio group.        
        RadioItem themeItem1 = new RadioItem.Builder().color(OPTIONS_COLOR)
                .text("A").end();
        themeItem1.setMouseOnRunnable(createFadeInRunnable(Theme.A));        
        themeItem1.setMouseOffRunnable(createFadeOutRunnable(Theme.A));
        
        RadioItem themeItem2 = new RadioItem.Builder().color(OPTIONS_COLOR)
                .text("B").end();
        themeItem2.setMouseOnRunnable(createFadeInRunnable(Theme.B));        
        themeItem2.setMouseOffRunnable(createFadeOutRunnable(Theme.B));
        
        RadioItem themeItem3 = new RadioItem.Builder().color(OPTIONS_COLOR)
                .text("C").end();
        themeItem3.setMouseOnRunnable(createFadeInRunnable(Theme.C));        
        themeItem3.setMouseOffRunnable(createFadeOutRunnable(Theme.C));
        
        RadioItem themeItem4 = new RadioItem.Builder().color(OPTIONS_COLOR)
                .text("All").end();
        RadioItem themeItem5 = new RadioItem.Builder().color(OPTIONS_COLOR)
                .text("?").end();
        this.themeRadio = new RadioGroup.Builder<Theme>(268, tl.getY() + SPACING, Theme.class)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))                
                .add(Theme.A, themeItem1, true)
                .add(Theme.B, themeItem2)
                .add(Theme.C, themeItem3)
                .add(Theme.ALL, themeItem4)
                .add(Theme.RANDOM, themeItem5)
                .pad(20).visible(false).end();
        this.entityList.add(themeRadio);    
               
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
                        settingsMan.getDoubleProperty(Settings.Key.GAME_MUSIC_VOLUME));
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
                .speed(Conf.MAIN_MENU_WINDOW_SPEED).end();   
        
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
                .speed(Conf.MAIN_MENU_WINDOW_SPEED).end();
        
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
            
            // Update the world manager.
            game.worldMan.setLevel(level);
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
            
            // Update the world manager.
            game.worldMan.setLevel(level);
        }       
        // See if the start button has been pressed.
        else if (this.startButton.clicked() == true)
        {
            // Set the music.
            this.musicMan.setTheme(themeRadio.getSelectedKey());
                                      
            // Stop all the player.
            for (MusicPlayer p : playerMap.values())
            {                
                p.stopAtGain(0.0);
            }                           
            
            // Notify the main menu.
            this.parent.setActivated(false);
        }
    }
    
}
