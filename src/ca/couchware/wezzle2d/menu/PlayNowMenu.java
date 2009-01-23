/*
 *  Wezzle
 *  Copyright (c) 2007-2009 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.menu;

import ca.couchware.wezzle2d.Game;
import ca.couchware.wezzle2d.ManagerHub;
import ca.couchware.wezzle2d.ResourceFactory.LabelBuilder;
import ca.couchware.wezzle2d.audio.Music;
import ca.couchware.wezzle2d.audio.MusicPlayer;
import ca.couchware.wezzle2d.event.GameEvent;
import ca.couchware.wezzle2d.graphics.IEntity;
import ca.couchware.wezzle2d.manager.IResettable;
import ca.couchware.wezzle2d.manager.LayerManager;
import ca.couchware.wezzle2d.manager.LayerManager.Layer;
import ca.couchware.wezzle2d.util.CouchLogger;
import ca.couchware.wezzle2d.manager.MusicManager;
import ca.couchware.wezzle2d.manager.MusicManager.Theme;
import ca.couchware.wezzle2d.manager.Settings;
import ca.couchware.wezzle2d.manager.Settings.Key;
import ca.couchware.wezzle2d.manager.SettingsManager;
import ca.couchware.wezzle2d.ui.IButton;
import ca.couchware.wezzle2d.ui.ITextLabel;
import ca.couchware.wezzle2d.ui.RadioGroup;
import ca.couchware.wezzle2d.ui.RadioItem;
import ca.couchware.wezzle2d.ui.Button;
import ca.couchware.wezzle2d.ui.Box;
import ca.couchware.wezzle2d.ui.SliderBar;
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
public class PlayNowMenu extends AbstractMenu
{
    
    /** The minimum level the user can select. */
    final private static int MIN_LEVEL = 1;
    
    /** The maximum level the user can select. */
    final private static int MAX_LEVEL = 15;             
    
    /** The level label. */
    private final ITextLabel levelNumberLabel;    
    
    /** The level. */
    private int levelNumber = 1;
    
    /** The level slider. */
    private final SliderBar levelSlider;
    
    /** The possibilies for the tutorial. */
    //private enum Tutorial { ON, OFF }
    final private static int TUTORIAL_ON = 0;
    final private static int TUTORIAL_OFF = 1;
    
    /** The tutorial radio group. */
    final private RadioGroup tutorialRadio;
    
    /** The music radio group. */
    final private RadioGroup themeRadio;        
    
    final private static int THEME_TRON = 0;
    final private static int THEME_ELECTRONIC = 1;
    final private static int THEME_HIPPOP = 2;
    
    /**
     * The music player map.
     */
    private List<MusicPlayer> playerMap;   
    
    /**
     * The start button.
     */
    final private IButton startButton;       
    
    public PlayNowMenu(IMenu parentMenu, ManagerHub hub, LayerManager menuLayerMan)
    {                
        // Invoke the super.
        super(parentMenu, hub, menuLayerMan);
               
        // The colors.
        final Color LABEL_COLOR  = hub.settingsMan.getColor(Key.GAME_COLOR_PRIMARY);        
        final Color OPTION_COLOR = hub.settingsMan.getColor(Key.GAME_COLOR_SECONDARY);                    
               
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
                .color(hub.settingsMan.getColor(Key.GAME_COLOR_SECONDARY))
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
        
        this.tutorialRadio = new RadioGroup.Builder(
                    268,
                    tutorialLabel.getY() + 35)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))                
                .add(tutorialOn,  true)
                .add(tutorialOff, false)
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
        themeItem1.setMouseOnRunnable(createFadeInRunnable(THEME_TRON));        
        themeItem1.setMouseOffRunnable(createFadeOutRunnable(THEME_TRON));
        
        RadioItem themeItem2 = new RadioItem.Builder().color(OPTION_COLOR)
                .text("Elec").end();
        themeItem2.setMouseOnRunnable(createFadeInRunnable(THEME_ELECTRONIC));        
        themeItem2.setMouseOffRunnable(createFadeOutRunnable(THEME_ELECTRONIC));
        
        RadioItem themeItem3 = new RadioItem.Builder().color(OPTION_COLOR)
                .text("HipPop").end();
        themeItem3.setMouseOnRunnable(createFadeInRunnable(THEME_HIPPOP));        
        themeItem3.setMouseOffRunnable(createFadeOutRunnable(THEME_HIPPOP));
        
        Map<Theme, Boolean> themeMap = new EnumMap<Theme, Boolean>(Theme.class);
        themeMap.put(Theme.TRON, false);
        themeMap.put(Theme.ELECTRONIC, false);
        themeMap.put(Theme.HIPPOP, false);
        
        List<Theme> themeList = new ArrayList<Theme>(themeMap.keySet());
        Collections.shuffle(themeList);
        themeMap.put(themeList.get(0), true);
               
        this.themeRadio = new RadioGroup.Builder(
                    268, 
                    themeLabel.getY() + 35)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))                
                .add(themeItem1, themeMap.get(Theme.TRON))
                .add(themeItem2, themeMap.get(Theme.ELECTRONIC))
                .add(themeItem3, themeMap.get(Theme.HIPPOP))              
                .itemSpacing(20).visible(false).end();
        this.entityList.add(themeRadio);    
               
        // Create the start button.
        this.startButton = new Button.Builder(268, 450)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                .color(hub.settingsMan.getColor(Key.GAME_COLOR_PRIMARY))
                .normalOpacity(90)                
                .visible(false)                
                .text("Start")
                .end();
        this.entityList.add(this.startButton);
                
        // Add them all to the layer manager.
        for (IEntity entity : this.entityList)
        {
            this.menuLayerMan.add(entity, Layer.UI);
        }
    }
    
    private void createPlayers()
    {
        // Create the music player map.
        this.playerMap = new ArrayList<MusicPlayer>();
       
        // Create three players, 1 for each theme.
        this.playerMap.add(MusicManager.createPlayer(Music.TRON2));
        this.playerMap.add(MusicManager.createPlayer(Music.ELECTRONIC1));
        this.playerMap.add(MusicManager.createPlayer(Music.HIPPOP1));        
        
        try 
        {            
            for (MusicPlayer p : playerMap)
            {
                p.setLoop(true);
                p.play();        
                p.setNormalizedGain(0.0);
            }            
        }
        catch (BasicPlayerException e)
        {
            // TODO Should try to do more than this, but this is OK for now.
            CouchLogger.get().recordException(this.getClass(), e);
        }             
    }
    
    private Runnable createFadeInRunnable(final int playerIndex)
    {
        return new Runnable()
        {
            public void run()
            { 
                playerMap.get(playerIndex).fadeToGain(
                        SettingsManager.get().getDouble(Settings.Key.USER_MUSIC_VOLUME));
            }
        };
    }
    
    private Runnable createFadeOutRunnable(final int playerIndex)
    {
        return new Runnable()
        {
            public void run()
            { 
                playerMap.get(playerIndex).fadeToGain(0.0);
            }
        };
    }                   
        
    public void updateLogic(Game game, ManagerHub hub)
    {      
        if (this.levelSlider.changed() == true)
        {
            levelNumber = (int) levelSlider.getVirtualValue();
            this.levelNumberLabel.setText("" + levelNumber);
        }
        // See if the start button has been pressed.
        else if (this.startButton.clicked() == true)
        {
            // Make sure no groups are showing if we've come back to the menu
            // from a previous game.
            hub.groupMan.hideAllGroups();
            
            // Reset the core managers.
            IResettable coreArray[] = new IResettable[]
            {
                game.getRefactorer(),
                game.getTileDropper(),
                game.getTileRemover()
            };
            
            for (IResettable core : coreArray)
            {
                core.resetState();
            }
                       
            // Reset the board manager and remove any tiles (and their effects)             
            // from the layer manager.          
            hub.boardMan.resetState(); 
            hub.boardMan.setVisible(false); // This is done for the fade in.
            hub.layerMan.clearLayer(Layer.TILE);
            hub.layerMan.clearLayer(Layer.EFFECT);
            
            // Reset the various other managers.
            IResettable[] manArray = new IResettable[]
            {
                hub.levelMan,
                hub.pieceMan,
                hub.scoreMan,
                hub.statMan,
                hub.timerMan,
                hub.tutorialMan
            };
            
            for (IResettable man : manArray)
            {
                man.resetState();
            }
                        
            // Set the music.
            Theme theme = null;
            switch (themeRadio.getSelectedIndex())
            {
                case THEME_TRON:
                    theme = Theme.TRON;
                    break;
                case THEME_ELECTRONIC:
                    theme = Theme.ELECTRONIC;
                    break;
                case THEME_HIPPOP:
                    theme = Theme.HIPPOP;
                    break;
                    
                default: throw new AssertionError();
            }
            
            hub.musicMan.setTheme(theme);
            
            // Set the target score.
            hub.levelMan.setLevel(levelNumber, false);
            //hub.timerMan.resetCurrentTime();
            hub.scoreMan.setTargetLevelScore(hub.scoreMan.generateTargetLevelScore(levelNumber));
            hub.scoreMan.setTargetTotalScore(hub.scoreMan.generateTargetLevelScore(levelNumber)); 
            
            // Notify that the game started.
            hub.listenerMan.notifyGameStarted(new GameEvent(this, 
                    hub.levelMan.getLevel(),
                    hub.scoreMan.getTotalScore()));
            
            // Turn off the tutorials if necessary.
            if (this.tutorialRadio.getSelectedIndex() == TUTORIAL_ON)
            {
                game.initializeTutorials();
            }
                                      
            // Stop all the player.
            for (MusicPlayer p : playerMap)
            {                
                p.stopAtGain(0.0);
            }        
            
            game.startBoard();  
            hub.pieceMan.loadPiece();
            
            // Notify the main menu.
            this.parent.setActivated(false);
        }
    }
    
}
