/*
 *  Wezzle
 *  Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d;

import ca.couchware.wezzle2d.ResourceFactory.LabelBuilder;
import ca.couchware.wezzle2d.event.ILevelListener;
import ca.couchware.wezzle2d.event.IScoreListener;
import ca.couchware.wezzle2d.event.LevelEvent;
import ca.couchware.wezzle2d.event.ScoreEvent;
import ca.couchware.wezzle2d.graphics.GraphicEntity;
import ca.couchware.wezzle2d.graphics.IPositionable.Alignment;
import ca.couchware.wezzle2d.manager.GroupManager;
import ca.couchware.wezzle2d.manager.HighScoreManager;
import ca.couchware.wezzle2d.manager.LayerManager;
import ca.couchware.wezzle2d.manager.LayerManager.Layer;
import ca.couchware.wezzle2d.manager.ListenerManager;
import ca.couchware.wezzle2d.manager.ListenerManager.Listener;
import ca.couchware.wezzle2d.manager.ScoreManager;
import ca.couchware.wezzle2d.manager.Settings;
import ca.couchware.wezzle2d.manager.Settings.Key;
import ca.couchware.wezzle2d.manager.SettingsManager;
import ca.couchware.wezzle2d.manager.StatManager;
import ca.couchware.wezzle2d.manager.TimerManager;
import ca.couchware.wezzle2d.manager.TutorialManager;
import ca.couchware.wezzle2d.manager.WorldManager;
import ca.couchware.wezzle2d.menu.Loader;
import ca.couchware.wezzle2d.ui.IButton;
import ca.couchware.wezzle2d.ui.ILabel;
import ca.couchware.wezzle2d.ui.ProgressBar;
import ca.couchware.wezzle2d.ui.SpriteButton;
import ca.couchware.wezzle2d.ui.group.GameOverGroup;
import ca.couchware.wezzle2d.ui.group.HighScoreGroup;
import ca.couchware.wezzle2d.ui.group.OptionsGroup;
import ca.couchware.wezzle2d.ui.group.PauseGroup;
import java.awt.Color;
import java.util.EnumSet;

/**
 * A class for handling the Wezzle UI.
 * 
 * @author cdmckay
 */
public class GameUI implements ILevelListener, IScoreListener
{       
    
    /** The single instance of this class. */
    final private static GameUI single = new GameUI();
    
    /** The level header path. */
    final private static String LEVEL_HEADER_PATH = Settings.getSpriteResourcesPath()
            + "/Header_Level.png";
    
    /** The score header path. */
    final private static String SCORE_HEADER_PATH = Settings.getSpriteResourcesPath()
            + "/Header_Score.png";
    
    /** The high score header path. */
    final private static String HIGH_SCORE_HEADER_PATH = Settings.getSpriteResourcesPath()
            + "/Header_HighScore.png";     
    
    /** A reference to the score manager. */
    
    
    /** The background sprite. */
    private GraphicEntity background;    
    
    /** The pause button. */
    public IButton pauseButton;
       
    /** The options button. */
    public IButton optionsButton;
    
    /** The help button. */
    public IButton helpButton;
    
    /** The progress bar. */
    public ProgressBar progressBar; 
    
    /** The timer text. */
    private ILabel timerLabel;
    
    /** The wezzle timer text. */
    private ILabel wezzleTimerLabel;  
    
    /** The score header graphic. */
    private GraphicEntity scoreHeaderLabel;
        
    /** The score text. */
    private ILabel scoreLabel;
    
    /** The high score header graphic. */
    private GraphicEntity highScoreHeaderLabel;
            
    /** The high score text. */
    private ILabel highScoreLabel;
    
    /** The high score header button. */
    private IButton highScoreButton;
    
    /** The level header graphic. */
    private GraphicEntity levelHeader;
    
    /** The level text. */
    private ILabel levelLabel;            
    
    /** The version label. */
    private ILabel versionLabel;     
    
    /** The copyright label. */
    private ILabel copyrightLabel;
    
    /** The pause group. */
    private PauseGroup pauseGroup;
    
    /** The game over group. */
    private GameOverGroup gameOverGroup;    
    
    /** The options group. */
    private OptionsGroup optionsGroup;
    
     /** The high score group. */
    private HighScoreGroup highScoreGroup;        
    
    /**
     * Private constructor to ensure singletonness.
     */
    private GameUI()
    { }
    
    /**
     * Get the only instance of this clas.
     * 
     * @return
     */
    static GameUI get()
    {
        return single;
    }
    
    /**
     * Adds UI-specific initialization tasks into the loader.
     * 
     * @param loader
     */
    public void initialize(final Loader loader, final Game game)
    {
        // Initialize buttons.    
        loader.addRunnable(new Runnable()
        {
           public void run() { initializeButtons(game.layerMan); }
        });                                 
                
        // Initialize labels.  
        loader.addRunnable(new Runnable()
        {
           public void run() { initializeLabels(game.layerMan); }
        });        
        
        // Initialize miscellaneous components.
        loader.addRunnable(new Runnable()
        {
           public void run() { initializeComponents(game.layerMan); }
        });        
             
        // Initialize the groups.   
        loader.addRunnable(new Runnable()
        {
           public void run() { 
               initializeGroups(
                   game.settingsMan,
                   game.layerMan, 
                   game.scoreMan,
                   game.groupMan, 
                   game.highScoreMan, 
                   game.statMan,  
                   game.listenerMan); 
           }
        });     
        
        // Add the listeners.
        game.listenerMan.registerListener(Listener.LEVEL, this);
        game.listenerMan.registerListener(Listener.SCORE, this);
    }
    
    /**
     * Initializes all the buttons that appear on the main game screen.
     */
    private void initializeButtons(LayerManager layerMan)
    {        
        // The high score button.
        highScoreButton = new SpriteButton.Builder(128, 299)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                .type(SpriteButton.Type.HUGE).text("")
                .offOpacity(0).hoverOpacity(70).onOpacity(95).end();
        layerMan.add(highScoreButton, Layer.UI);
                
        // Create pause button.        
        pauseButton = new SpriteButton.Builder(668, 211)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                .type(SpriteButton.Type.NORMAL).text("Pause").activeText("Resume")
                .offOpacity(70).end();
        layerMan.add(pauseButton, Layer.UI);    
        
        // Create the options button, using pause button as a template.
        optionsButton = new SpriteButton.Builder((SpriteButton) pauseButton)
                .y(299).text("Options").end();
        layerMan.add(optionsButton, Layer.UI);                
        
        // Create the help buttton, using pause button as a template.
        helpButton = new SpriteButton.Builder((SpriteButton) optionsButton)
                .y(387).text("Help").end();               
        layerMan.add(helpButton, Layer.UI);     
    }
    
    /**
     * Initializes all the labesl that appear on the main game screen.
     */
    private void initializeLabels(LayerManager layerMan)
    {          
        // Shortcut to the primary color.
        final Color PRIMARY_COLOR = 
                SettingsManager.get().getColor(Key.GAME_COLOR_PRIMARY);
        
        // Set up the copyright label.
        copyrightLabel = new LabelBuilder(10, 600 - 10)
                .alignment(EnumSet.of(Alignment.BOTTOM, Alignment.LEFT))
                .cached(false).color(PRIMARY_COLOR).size(12)                
                .text(Game.COPYRIGHT).end();
        layerMan.add(copyrightLabel, Layer.UI);
        
        // Set up the version label.	
        versionLabel = new LabelBuilder(800 - 10, 600 - 10)
                .alignment(EnumSet.of(Alignment.BOTTOM, Alignment.RIGHT))
                .cached(false).color(PRIMARY_COLOR).size(12)                
                .text(Game.TITLE)
                .end();                        
        layerMan.add(versionLabel, Layer.UI);
        
		// Set up the timer text.
        timerLabel = new LabelBuilder(400, 70)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                .color(PRIMARY_COLOR).size(50).text("").end();
        layerMan.add(timerLabel, Layer.UI);
        
        // Set up the Wezzle timer text.
        wezzleTimerLabel = new LabelBuilder(470, 70)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                .color(PRIMARY_COLOR).size(25).text("8").end();
        layerMan.add(wezzleTimerLabel, Layer.UI);
        
        // Set up the level header.
        levelHeader = new GraphicEntity.Builder(126, 153, LEVEL_HEADER_PATH)                
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER)).end();        
        layerMan.add(levelHeader, Layer.UI);
        
        // Set up the level text.
        levelLabel = new LabelBuilder(126, 210)
                .alignment(EnumSet.of(Alignment.BOTTOM, Alignment.CENTER))
                .cached(false)
                .color(PRIMARY_COLOR).size(20).text("--").end();                
        layerMan.add(levelLabel, Layer.UI);        
        
        // Set up the score header.
        highScoreHeaderLabel = 
                new GraphicEntity.Builder(127, 278, HIGH_SCORE_HEADER_PATH)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER)).end();
        layerMan.add(highScoreHeaderLabel, Layer.UI);
                        
        // Set up the high score text.
        highScoreLabel = new LabelBuilder(126, 337)
                .alignment(EnumSet.of(Alignment.BOTTOM, Alignment.CENTER))
                .cached(false)
                .color(PRIMARY_COLOR).size(20).text("--").end();
        layerMan.add(highScoreLabel, Layer.UI);
        
        // Set up the score header.
        scoreHeaderLabel = new GraphicEntity.Builder(128, 403, SCORE_HEADER_PATH)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER)).end();
        layerMan.add(scoreHeaderLabel, Layer.UI);
        
        // Set up the score text.
        scoreLabel = new LabelBuilder(126, 460)
                .alignment(EnumSet.of(Alignment.BOTTOM, Alignment.CENTER))
                .cached(false)
                .color(PRIMARY_COLOR).size(20).text("--").end();
        layerMan.add(scoreLabel, Layer.UI);
    }
    
    /**
     * Initializes miscellaneous components.
     */
    private void initializeComponents(LayerManager layerMan)
    {
        // Create the background.
		background = new GraphicEntity
                .Builder(0, 0, Settings.getSpriteResourcesPath() + "/Background2.png")
                .end();
        
        layerMan.add(background, Layer.BACKGROUND);   
        layerMan.toBack(background, Layer.BACKGROUND);
        
        // Create the progress bar.
        progressBar = new ProgressBar.Builder(393, 501)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                //.progressMax(scoreMan.getTargetLevelScore())
                .end();
        layerMan.add(progressBar, Layer.UI);
    }
    
    /**
     * Initialize the various groups.
     */
    private void initializeGroups(
            SettingsManager  settingsMan,
            LayerManager     layerMan,
            ScoreManager     scoreMan,
            GroupManager     groupMan,
            HighScoreManager highScoreMan,            
            StatManager      statMan,
            ListenerManager  listenerMan)
    {        
        // Initialize pause group.                
        this.pauseGroup = new PauseGroup(settingsMan, layerMan, statMan);
        groupMan.register(pauseGroup);
        
        listenerMan.registerListener(Listener.MOVE, this.pauseGroup);
        listenerMan.registerListener(Listener.LINE, this.pauseGroup);
        listenerMan.registerListener(Listener.GAME, this.pauseGroup);
             
        // Initialize game over group.
        this.gameOverGroup = new GameOverGroup(settingsMan, layerMan, scoreMan);    
        groupMan.register(this.gameOverGroup);
        
        listenerMan.registerListener(Listener.GAME, this.gameOverGroup);
        
        // Initialize options group.
        this.optionsGroup = new OptionsGroup(settingsMan, layerMan, groupMan);
        groupMan.register(this.optionsGroup);
        
        // Initialize high score group.
        this.highScoreGroup = new HighScoreGroup(settingsMan, layerMan, highScoreMan); 
        groupMan.register(this.highScoreGroup);
        
        listenerMan.registerListener(Listener.GAME, this.highScoreGroup);                
    }
    
    public void showGameOverGroup(GroupManager groupMan)
    {
        // Draw game over screen.
        //gameOverGroup.setScore(scoreMan.getTotalScore());
        groupMan.showGroup(null, gameOverGroup, 
                GroupManager.Class.GAME_OVER,
                GroupManager.Layer.BOTTOM);  
    }
    
    public void showPauseGroup(GroupManager groupMan)
    {
        groupMan.showGroup(pauseButton, pauseGroup, 
                GroupManager.Class.PAUSE,
                GroupManager.Layer.MIDDLE);
    }      
    
    /**
     * Update the UI logic.
     * 
     * @param game The current game state.
     */
    public void updateLogic(Game game)
    {
        // Make shortcuts to the managers.
        GroupManager groupMan       = game.groupMan;
        LayerManager layerMan       = game.layerMan;
        ScoreManager scoreMan       = game.scoreMan;
        TimerManager timerMan       = game.timerMan;
        TutorialManager tutorialMan = game.tutorialMan;
        WorldManager worldMan       = game.worldMan;
        
        // If the high score button was just clicked.
        if (highScoreButton.clicked() == true)
        {
            if (highScoreButton.isActivated() == true)            
            {                           
                groupMan.showGroup(highScoreButton, highScoreGroup, 
                        GroupManager.Class.HIGH_SCORE,
                        GroupManager.Layer.MIDDLE);            
            }
            else
            {
                groupMan.hideGroup(
                        GroupManager.Class.HIGH_SCORE,
                        GroupManager.Layer.MIDDLE);
            }
        } // end if
        
        // If the pause button was just clicked.
        if (pauseButton.clicked() == true)
        {            
            if (pauseButton.isActivated() == true)            
            {                
                groupMan.showGroup(pauseButton, pauseGroup, 
                        GroupManager.Class.PAUSE,
                        GroupManager.Layer.MIDDLE);            
            }
            else
            {
                groupMan.hideGroup(
                        GroupManager.Class.PAUSE,
                        GroupManager.Layer.MIDDLE);            
            }
        } // end if
        
        // If the options button was just clicked.
        if (optionsButton.clicked() == true)
        {                           
            if (optionsButton.isActivated() == true)  
            {                
                groupMan.showGroup(optionsButton, optionsGroup,
                        GroupManager.Class.OPTIONS,
                        GroupManager.Layer.MIDDLE);            
            }
            else     
            {
                groupMan.hideGroup(
                        GroupManager.Class.OPTIONS,
                        GroupManager.Layer.MIDDLE);
            }
        } // end if  
        
        // Draw the timer text.
        if (!timerLabel.getText().equals(String.valueOf(timerMan.getTime())))            
        {
            layerMan.remove(timerLabel, Layer.UI);
            timerLabel = new LabelBuilder(timerLabel)                        
                    .text(String.valueOf(timerMan.getTime())).end();
            layerMan.add(timerLabel, Layer.UI);
            timerLabel.getDrawRect();
        }

        // Draw the high score text.
        if (!highScoreLabel.getText().equals(String.valueOf(scoreMan.getHighScore())))
        {
            //LogManager.recordMessage("New high score label created.");
            layerMan.remove(highScoreLabel, Layer.UI);
            highScoreLabel = new LabelBuilder(highScoreLabel)
                    .text(String.valueOf(scoreMan.getHighScore())).end();
            layerMan.add(highScoreLabel, Layer.UI);
        }                        

        if (tutorialMan.isTutorialInProgress() == false)
        {
            // Set the level text.
            if (!levelLabel.getText().equals(String.valueOf(worldMan.getLevel())))
            {
                layerMan.remove(levelLabel, Layer.UI);
                levelLabel = new LabelBuilder(levelLabel)
                        .text(String.valueOf(worldMan.getLevel())).end();
                layerMan.add(levelLabel, Layer.UI);
            }

            // Set the score text.
            if (!scoreLabel.getText().equals(String.valueOf(scoreMan.getTotalScore())))
            {
                layerMan.remove(scoreLabel, Layer.UI);
                scoreLabel = new LabelBuilder(scoreLabel)
                        .text(String.valueOf(scoreMan.getTotalScore()))
                        .end();
                layerMan.add(scoreLabel, Layer.UI);
            }
        }
        else
        {
            // Set the level text.
            if (!levelLabel.getText()
                    .equals(tutorialMan.getTutorialInProgress().getName()))
            {
                layerMan.remove(levelLabel, Layer.UI);
                levelLabel = new LabelBuilder(levelLabel)
                        .text(tutorialMan.getTutorialInProgress().getName())
                        .end();
                layerMan.add(levelLabel, Layer.UI);
            }

            // Set the score text.
            if (!scoreLabel.getText().equals("--"))
            {
                layerMan.remove(scoreLabel, Layer.UI);
                scoreLabel = new LabelBuilder(scoreLabel).text("--").end();
                layerMan.add(scoreLabel, Layer.UI);
            }
        }       
    }

    public void levelChanged(LevelEvent event)
    {
        progressBar.setProgressMax(event.getTargetLevelScore());
    }

    public void scoreIncreased(ScoreEvent event)
    {
        // Ignore this, use the scoreChanged event instead.
    }
    
    public void scoreChanged(ScoreEvent event)
    {       
        // Update the progress bar.
        progressBar.setProgress(event.getScore());       
    }

    public void targetScoreChanged(ScoreEvent event)
    {
        // Update the progress bar.
        progressBar.setProgressMax(event.getScore());
    }   
    
}