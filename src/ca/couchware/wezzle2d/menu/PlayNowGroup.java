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
import ca.couchware.wezzle2d.manager.MusicManager;
import ca.couchware.wezzle2d.manager.Settings;
import ca.couchware.wezzle2d.manager.Settings.Key;
import ca.couchware.wezzle2d.manager.SettingsManager;
import ca.couchware.wezzle2d.ui.IButton;
import ca.couchware.wezzle2d.ui.ITextLabel;
import ca.couchware.wezzle2d.ui.RadioGroup;
import ca.couchware.wezzle2d.ui.RadioItem;
import ca.couchware.wezzle2d.ui.Button;
import ca.couchware.wezzle2d.ui.Box;
import ca.couchware.wezzle2d.ui.ITextField;
import ca.couchware.wezzle2d.ui.SliderBar;
import ca.couchware.wezzle2d.ui.TallButton;
import ca.couchware.wezzle2d.ui.TextField;
import ca.couchware.wezzle2d.ui.group.AbstractGroup;
import ca.couchware.wezzle2d.ui.group.IGroup;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
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
    
    /** The level label. */
    private final ITextLabel levelNumberLabel;    
    
    /** The level. */
    private int levelNumber = 1;
    
    /** The level slider. */
    private final SliderBar levelSlider;
    
    /** The possibilies for the tutorial. */
    private enum Tutorial { ON, OFF }
    
    /** The tutorial radio group. */
    final private RadioGroup<Tutorial> tutorialRadio;
    
    /** The music radio group. */
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
    private Box box;
    
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
        box = new Box.Builder(268, 300).width(430).height(470)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                .opacity(SettingsManager.get().getInt(Key.MAIN_MENU_WINDOW_OPACITY))
                .visible(false).end();
        this.layerMan.add(box, Layer.UI);         
        
        // The label spacing.
        final int SPACING = 60;
        
        // The title label.
        ITextLabel titleLabel = new LabelBuilder(74, 97)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.LEFT))
                .color(LABEL_COLOR).text("Play Now").size(20)                
                .visible(false).end();
        this.entityList.add(titleLabel);
        
        // The first box.
        Box optionBox = new Box.Builder(68, 122)
                .width(400).height(398)
                .border(Box.Border.MEDIUM)
                .opacity(80)
                .visible(false)
                .end();
        this.entityList.add(optionBox);
        
        ITextLabel levelLabel = new LabelBuilder(110, 180)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.LEFT))
                .color(LABEL_COLOR).text("Level").size(20)
                .visible(false)
                .end();
        this.entityList.add(levelLabel);
        
        this.levelNumberLabel = new LabelBuilder(
                    levelLabel.getX() + levelLabel.getWidth() + 20, 
                    levelLabel.getY())                
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.LEFT))
                .color(settingsMan.getColor(Key.GAME_COLOR_SECONDARY))
                .size(20).visible(false)
                .text(String.valueOf(levelNumber))
                .end();
        this.entityList.add(this.levelNumberLabel);
        
        this.levelSlider = new SliderBar.Builder(
                    268, 
                    levelLabel.getY() + 35)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))                
                .width(340)
                .virtualRange(MIN_LEVEL, MAX_LEVEL)
                .virtualValue(MIN_LEVEL)
                .visible(false)
                .end();
        this.entityList.add(levelSlider);
        
        ITextLabel tutorialLabel = new LabelBuilder(
                    levelLabel.getX(), 
                    levelLabel.getY() + 80)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.LEFT))
                .color(LABEL_COLOR).text("Tutorial").size(20)
                .visible(false)
                .end();
        this.entityList.add(tutorialLabel);
        
        RadioItem tutorialOn = new RadioItem.Builder()
                .color(OPTION_COLOR)
                .text("On").end();        
         
        RadioItem tutorialOff = new RadioItem.Builder()
                .color(OPTION_COLOR)
                .text("Off").end();
        
        this.tutorialRadio = new RadioGroup.Builder<Tutorial>(
                    268,
                    tutorialLabel.getY() + 35,
                    Tutorial.class)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))                
                .add(Tutorial.ON,  tutorialOn,  true)
                .add(Tutorial.OFF, tutorialOff, false)
                .visible(false)
                .end();
        this.entityList.add(tutorialRadio);                
        
        ITextLabel themeLabel = new LabelBuilder(
                    tutorialLabel.getX(), 
                    tutorialLabel.getY() + 80)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.LEFT))
                .color(LABEL_COLOR).text("Music").size(20)
                .visible(false)
                .end();
        this.entityList.add(themeLabel);
        
        // Create the music players.
        createPlayers();
        
        // Creat the level limit radio group.        
        RadioItem themeItem1 = new RadioItem.Builder().color(OPTION_COLOR)
                .text("Tron").end();
        themeItem1.setMouseOnRunnable(createFadeInRunnable(Theme.TRON));        
        themeItem1.setMouseOffRunnable(createFadeOutRunnable(Theme.TRON));
        
        RadioItem themeItem2 = new RadioItem.Builder().color(OPTION_COLOR)
                .text("Elec").end();
        themeItem2.setMouseOnRunnable(createFadeInRunnable(Theme.ELECTRONIC));        
        themeItem2.setMouseOffRunnable(createFadeOutRunnable(Theme.ELECTRONIC));
        
        RadioItem themeItem3 = new RadioItem.Builder().color(OPTION_COLOR)
                .text("HipPop").end();
        themeItem3.setMouseOnRunnable(createFadeInRunnable(Theme.HIPPOP));        
        themeItem3.setMouseOffRunnable(createFadeOutRunnable(Theme.HIPPOP));
        
        Map<Theme, Boolean> themeMap = new EnumMap<Theme, Boolean>(Theme.class);
        themeMap.put(Theme.TRON, false);
        themeMap.put(Theme.ELECTRONIC, false);
        themeMap.put(Theme.HIPPOP, false);
        
        List<Theme> themeList = new ArrayList<Theme>(themeMap.keySet());
        Collections.shuffle(themeList);
        themeMap.put(themeList.get(0), true);
        
        RadioItem themeItem4 = new RadioItem.Builder().color(OPTION_COLOR)
                .text("All").end();
        RadioItem themeItem5 = new RadioItem.Builder().color(OPTION_COLOR)
                .text("?").end();
        this.themeRadio = new RadioGroup.Builder<Theme>(
                    268, 
                    themeLabel.getY() + 35,
                    Theme.class)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))                
                .add(Theme.TRON, themeItem1, themeMap.get(Theme.TRON))
                .add(Theme.ELECTRONIC, themeItem2, themeMap.get(Theme.ELECTRONIC))
                .add(Theme.HIPPOP, themeItem3, themeMap.get(Theme.HIPPOP))              
                .pad(20).visible(false).end();
        this.entityList.add(themeRadio);    
               
        // Create the start button.
        this.startButton = new Button.Builder(268, 459)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                .color(settingsMan.getColor(Key.GAME_COLOR_PRIMARY))
                .normalOpacity(90)                
                .visible(false)                
                .text("Start")
                .end();
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
        this.playerMap.put(Theme.TRON, MusicManager.createPlayer(Music.TRON2));
        this.playerMap.put(Theme.ELECTRONIC, MusicManager.createPlayer(Music.ELECTRONIC1));
        this.playerMap.put(Theme.HIPPOP, MusicManager.createPlayer(Music.HIPPOP1));        
        
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
        box.setPosition(268, -300);
        box.setVisible(true);        
        
        IAnimation a = new MoveAnimation.Builder(box).theta(-90).maxY(300)
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
        IAnimation a = new MoveAnimation.Builder(box).theta(-90)
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
//        if (this.levelDownButton.clicked() == true)
//        {
//            // Reset the level down button.
//            this.levelDownButton.setActivated(false);
//            
//            // Extract the current setting.            
//            levelNumber = levelNumber - 1 < MIN_LEVEL 
//                    ? MIN_LEVEL 
//                    : levelNumber - 1;
//            
//            // Remove old label.
//            this.entityList.remove(this.levelNumberLabel);
//            this.layerMan.remove(this.levelNumberLabel, Layer.UI);
//            
//            // Add new label.
//            this.levelNumberLabel = new LabelBuilder(this.levelNumberLabel)
//                    .text(String.valueOf(levelNumber)).end();
//            
//            // Add it.
//            this.entityList.add(this.levelNumberLabel);
//            this.layerMan.add(this.levelNumberLabel, Layer.UI);                       
//        }
//        // See if the level up button was clicked.
//        else if (this.levelUpButton.clicked() == true)
//        {
//            // Reset the level up button.
//            this.levelUpButton.setActivated(false);
//            
//            // Determine new setting.
//            levelNumber = levelNumber + 1 > MAX_LEVEL 
//                    ? MAX_LEVEL 
//                    : levelNumber + 1;
//            
//            // Remove old label.
//            this.entityList.remove(this.levelNumberLabel);
//            this.layerMan.remove(this.levelNumberLabel, Layer.UI);
//            
//            // Add new label.
//            this.levelNumberLabel = new LabelBuilder(this.levelNumberLabel)
//                    .text(String.valueOf(levelNumber)).end();
//            
//            // Add it.
//            this.entityList.add(this.levelNumberLabel);
//            this.layerMan.add(this.levelNumberLabel, Layer.UI);           
//        }       
        if (this.levelSlider.changed() == true)
        {
            levelNumber = (int) levelSlider.getVirtualValue();
            this.levelNumberLabel.setText("" + levelNumber);
        }
        // See if the start button has been pressed.
        else if (this.startButton.clicked() == true)
        {
            // Set the music.
            this.musicMan.setTheme(themeRadio.getSelectedKey());
            
            // Set the target score.
            game.levelMan.setLevel(levelNumber, false);
            game.timerMan.resetTimer();
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
