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
import ca.couchware.wezzle2d.piece.PieceGrid;
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
import ca.couchware.wezzle2d.manager.LevelManager;
import ca.couchware.wezzle2d.menu.Loader;
import ca.couchware.wezzle2d.piece.PieceLine;
import ca.couchware.wezzle2d.ui.Box;
import ca.couchware.wezzle2d.ui.Box.Border;
import ca.couchware.wezzle2d.ui.IButton;
import ca.couchware.wezzle2d.ui.ITextLabel;
import ca.couchware.wezzle2d.ui.ProgressBar;
import ca.couchware.wezzle2d.ui.Button;
import ca.couchware.wezzle2d.ui.MammothButton;
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
    private ITextLabel timerLabel;
    
    /** The piece preview. */
    private Box piecePreviewBox;
    
    /** The score header graphic. */
    private GraphicEntity scoreHeaderLabel;
        
    /** The score text. */
    private ITextLabel scoreLabel;
    
    /** The high score header graphic. */
    private GraphicEntity highScoreHeaderLabel;
            
    /** The high score text. */
    private ITextLabel highScoreLabel;
    
    /** The high score header button. */
    private IButton highScoreButton;
    
    /** The level header graphic. */
    private GraphicEntity levelHeader;
    
    /** The level text. */
    private ITextLabel levelLabel;            
    
    /** The version label. */
    private ITextLabel versionLabel;     
    
    /** The copyright label. */
    private ITextLabel copyrightLabel;
    
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
        loader.addTask(new Runnable()
        {
           public void run() { initializeButtons(game.layerMan); }
        });                                 
                
        // Initialize labels.  
        loader.addTask(new Runnable()
        {
           public void run() { initializeLabels(game.layerMan); }
        });        
        
        // Initialize miscellaneous components.
        loader.addTask(new Runnable()
        {
           public void run() { initializeComponents(game.layerMan); }
        });        
             
        // Initialize the groups.   
        loader.addTask(new Runnable()
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
        game.listenerMan.registerListener(Listener.LEVEL,  this);
        game.listenerMan.registerListener(Listener.SCORE,  this);       
    }
    
    /**
     * Initializes all the buttons that appear on the main game screen.
     */
    private void initializeButtons(LayerManager layerMan)
    {        
        // The high score button.
        highScoreButton = new MammothButton.Builder(128, 299)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))                
                .text("")
                .normalOpacity(0).hoverOpacity(70).activeOpacity(95).end();
        layerMan.add(highScoreButton, Layer.UI);
                
        // Create pause button.        
        pauseButton = new Button.Builder(668, 211)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                //.type(SpriteButton.Type.NORMAL)
                .width(170)
                .text("Pause").activeText("Resume")
                .normalOpacity(70).end();
        layerMan.add(pauseButton, Layer.UI);    
        
        // Create the options button, using pause button as a template.
        optionsButton = new Button.Builder((Button) pauseButton)
                .y(299).text("Options").end();
        layerMan.add(optionsButton, Layer.UI);                
        
        // Create the help buttton, using pause button as a template.
        helpButton = new Button.Builder((Button) optionsButton)
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
        timerLabel = new LabelBuilder(400, 100)
                .alignment(EnumSet.of(Alignment.BOTTOM, Alignment.CENTER))
                .color(PRIMARY_COLOR).size(50).text("").end();
        layerMan.add(timerLabel, Layer.UI);        
        
        // Set up the level header.
        levelHeader = new GraphicEntity.Builder(126, 153, LEVEL_HEADER_PATH)                
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER)).end();        
        layerMan.add(levelHeader, Layer.UI);
        
        // Set up the level text.
        levelLabel = new LabelBuilder(126, 210)
                .alignment(EnumSet.of(Alignment.BOTTOM, Alignment.CENTER))
                .cached(false)
                .color(PRIMARY_COLOR).size(20).text("").end();                
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
                .color(PRIMARY_COLOR).size(20).text("").end();
        layerMan.add(highScoreLabel, Layer.UI);
        
        // Set up the score header.
        scoreHeaderLabel = new GraphicEntity.Builder(128, 403, SCORE_HEADER_PATH)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER)).end();
        layerMan.add(scoreHeaderLabel, Layer.UI);
        
        // Set up the score text.
        scoreLabel = new LabelBuilder(126, 460)
                .alignment(EnumSet.of(Alignment.BOTTOM, Alignment.CENTER))
                .cached(false)
                .color(PRIMARY_COLOR).size(20).text("").end();
        layerMan.add(scoreLabel, Layer.UI);
    }
    
    /**
     * Initializes miscellaneous components.
     */
    private void initializeComponents(LayerManager layerMan)
    {
        // Create the background.
		this.background = new GraphicEntity
                .Builder(0, 0, Settings.getSpriteResourcesPath() + "/Background2.png")
                .end();
        
        layerMan.add(this.background, Layer.BACKGROUND);   
        layerMan.toBack(this.background, Layer.BACKGROUND);     
        
        // Create the piece preview window.
        this.piecePreviewBox = new Box.Builder(670, 120)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                .opacity(90)
                .border(Border.MEDIUM)
                .width(90).height(90).end();
        layerMan.add(this.piecePreviewBox, Layer.UI);
        
        PieceGrid grid = new PieceGrid.Builder(
                    this.piecePreviewBox.getX() - 11,
                    this.piecePreviewBox.getY() - 11,
                    PieceGrid.RenderMode.VECTOR
                )
                .cellWidth(22)
                .cellHeight(22)
                .end();
        
        grid.loadStructure(new PieceLine().getStructure());
        layerMan.add(grid, Layer.UI);
        
        // Create the progress bar.
        this.progressBar = new ProgressBar.Builder(393, 501)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))                
                .end();
        layerMan.add(this.progressBar, Layer.UI);
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
        //LayerManager layerMan       = game.layerMan;
        ScoreManager scoreMan       = game.scoreMan;
        TimerManager timerMan       = game.timerMan;
        TutorialManager tutorialMan = game.tutorialMan;
        LevelManager levelMan       = game.levelMan;
        
        // If the high score button was just clicked.
        if (highScoreButton.clicked())
        {
            if (highScoreButton.isActivated())            
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
        if (pauseButton.clicked())
        {            
            if (pauseButton.isActivated())            
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
        if (optionsButton.clicked())
        {                           
            if (optionsButton.isActivated())  
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
            timerLabel.setText(String.valueOf(timerMan.getTime()));            
        }       

        // Draw the high score text.
        if (!highScoreLabel.getText().equals(String.valueOf(scoreMan.getHighScore())))
        {
            highScoreLabel.setText(String.valueOf(scoreMan.getHighScore()));            
        }                        

        if (!tutorialMan.isTutorialRunning())
        {
            // Set the level text.
            if (!levelLabel.getText().equals(String.valueOf(levelMan.getLevel())))
            {
                levelLabel.setText(String.valueOf(levelMan.getLevel()));                
            }

            // Set the score text.
            if (!scoreLabel.getText().equals(String.valueOf(scoreMan.getTotalScore())))
            {
                scoreLabel.setText(String.valueOf(scoreMan.getTotalScore()));                
            }
        }
        else
        {
            // Set the level text.
            if (!levelLabel.getText().equals(tutorialMan.getRunningTutorial().getName()))
            {                
                levelLabel.setText(tutorialMan.getRunningTutorial().getName());
            }

            // Set the score text.
            if (!scoreLabel.getText().equals(""))
            {
                scoreLabel.setText("");                
            }
        } // end if      
    }

    public void levelChanged(LevelEvent event)
    {
        progressBar.setProgressMax(event.getNextTargetLevelScore());
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
