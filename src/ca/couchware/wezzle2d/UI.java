/*
 *  Wezzle
 *  Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d;

import ca.couchware.wezzle2d.ResourceFactory.LabelBuilder;
import ca.couchware.wezzle2d.event.ILevelListener;
import ca.couchware.wezzle2d.event.IPieceListener;
import ca.couchware.wezzle2d.event.IScoreListener;
import ca.couchware.wezzle2d.event.ITimerListener;
import ca.couchware.wezzle2d.event.LevelEvent;
import ca.couchware.wezzle2d.event.PieceEvent;
import ca.couchware.wezzle2d.event.ScoreEvent;
import ca.couchware.wezzle2d.event.TimerEvent;
import ca.couchware.wezzle2d.graphics.GraphicEntity;
import ca.couchware.wezzle2d.graphics.IPositionable.Alignment;
import ca.couchware.wezzle2d.piece.PieceGrid;
import ca.couchware.wezzle2d.manager.GroupManager;
import ca.couchware.wezzle2d.manager.LayerManager.Layer;
import ca.couchware.wezzle2d.manager.ListenerManager.Listener;
import ca.couchware.wezzle2d.manager.Settings;
import ca.couchware.wezzle2d.manager.Settings.Key;
import ca.couchware.wezzle2d.manager.SettingsManager;
import ca.couchware.wezzle2d.util.CouchLogger;
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
public class UI implements 
        ILevelListener, 
        IPieceListener, 
        IScoreListener,
        ITimerListener
{       
     
    /** The graphics file extension. */
    final private static String FILE_EXT = ".png";
    
    /** The level header path. */
    final private static String LEVEL_HEADER_PATH = 
            Settings.getSpriteResourcesPath()
            + "/Header_Level" + FILE_EXT;
    
    /** The score header path. */
    final private static String SCORE_HEADER_PATH = 
            Settings.getSpriteResourcesPath()
            + "/Header_Score" + FILE_EXT;
    
    /** The high score header path. */
    final private static String HIGH_SCORE_HEADER_PATH = 
            Settings.getSpriteResourcesPath()
            + "/Header_HighScore" + FILE_EXT;          
    
    /** The background sprite. */
    private GraphicEntity background;    
    
    /** The pause button. */
    private IButton pauseButton;
       
    /** The options button. */
    private IButton optionsButton;
    
    /** The help button. */
    private IButton helpButton;
    
    /** The timer bar. */
    private ProgressBar timerBar;
    
    /** The progress bar. */
    private ProgressBar progressBar; 
       
    /** The next piece preview. */
    private Box nextPieceBox;
    
    /** The next piece preview grid. */
    private PieceGrid nextPieceGrid;
    
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
    private UI(ManagerHub hub)
    {        
        // Initialize the buttons.
        initializeButtons(hub);
        
        // Initialize the labels.
        initializeLabels(hub);
        
        // Initialize the background.
        initializeBackground(hub);
        
        // Initialize piece box.
        initializePieceBox(hub);
        
        // Initialize the progress bars.
        initializeBars(hub);
        
        // Initialize the groups.
        initializeGroups(hub);        
    }        
    
    /**
     * Create a new UI instance.
     */
    public static UI newInstance(ManagerHub hub)
    {
        return new UI(hub);
    }       
    
    /**
     * Initializes all the buttons that appear on the main game screen.
     */
    private void initializeButtons(ManagerHub hub)
    {        
        // The high score button.
        highScoreButton = new MammothButton.Builder(128, 299)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))                
                .text("")
                .normalOpacity(0).hoverOpacity(70).activeOpacity(95).end();
        hub.layerMan.add(highScoreButton, Layer.UI);
                
        // Create pause button.        
        pauseButton = new Button.Builder(668, 211)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                //.type(SpriteButton.Type.NORMAL)
                .width(170)
                .text("Pause").activeText("Resume")
                .normalOpacity(70).end();
        hub.layerMan.add(pauseButton, Layer.UI);    
        
        // Create the options button, using pause button as a template.
        optionsButton = new Button.Builder((Button) pauseButton)
                .y(299).text("Options").end();
        hub.layerMan.add(optionsButton, Layer.UI);                
        
        // Create the help buttton, using pause button as a template.
        helpButton = new Button.Builder((Button) optionsButton)
                .y(387).text("Help").end();               
        hub.layerMan.add(helpButton, Layer.UI);     
    }
    
    /**
     * Initializes all the labels.
     */
    private void initializeLabels(ManagerHub hub)
    {          
        // Shortcut to the primary color.
        final Color PRIMARY_COLOR = 
                SettingsManager.get().getColor(Key.GAME_COLOR_PRIMARY);
               
        // Set up the copyright label.
        copyrightLabel = new LabelBuilder(10, 600 - 10)
                .alignment(EnumSet.of(Alignment.BOTTOM, Alignment.LEFT))
                .cached(false).color(PRIMARY_COLOR).size(12)                
                .text(Game.COPYRIGHT).end();
        hub.layerMan.add(copyrightLabel, Layer.UI);
        
        // Set up the version label.	
        versionLabel = new LabelBuilder(800 - 10, 600 - 10)
                .alignment(EnumSet.of(Alignment.BOTTOM, Alignment.RIGHT))
                .cached(false).color(PRIMARY_COLOR).size(12)                
                .text(Game.TITLE)
                .end();                        
        hub.layerMan.add(versionLabel, Layer.UI);
        		  
        
        // Set up the level header.
        levelHeader = new GraphicEntity.Builder(126, 153, LEVEL_HEADER_PATH)                
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER)).end();        
        hub.layerMan.add(levelHeader, Layer.UI);
        
        // Set up the level text.
        levelLabel = new LabelBuilder(126, 210)
                .alignment(EnumSet.of(Alignment.BOTTOM, Alignment.CENTER))
                .cached(false)
                .color(PRIMARY_COLOR).size(20).text("").end();                
        hub.layerMan.add(levelLabel, Layer.UI);        
        
        // Set up the score header.
        highScoreHeaderLabel = 
                new GraphicEntity.Builder(127, 278, HIGH_SCORE_HEADER_PATH)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER)).end();
        hub.layerMan.add(highScoreHeaderLabel, Layer.UI);
                        
        // Set up the high score text.
        highScoreLabel = new LabelBuilder(126, 337)
                .alignment(EnumSet.of(Alignment.BOTTOM, Alignment.CENTER))
                .cached(false)
                .color(PRIMARY_COLOR).size(20).text("").end();
        hub.layerMan.add(highScoreLabel, Layer.UI);
        
        // Set up the score header.
        scoreHeaderLabel = new GraphicEntity.Builder(128, 403, SCORE_HEADER_PATH)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER)).end();
        hub.layerMan.add(scoreHeaderLabel, Layer.UI);
        
        // Set up the score text.
        scoreLabel = new LabelBuilder(126, 460)
                .alignment(EnumSet.of(Alignment.BOTTOM, Alignment.CENTER))
                .cached(false)
                .color(PRIMARY_COLOR).size(20).text("").end();
        hub.layerMan.add(scoreLabel, Layer.UI);
    }
    
    /**
     * Initializes the background.
     */
    private void initializeBackground(ManagerHub hub)
    {
        // Create the background.
		this.background = new GraphicEntity
                .Builder(0, 0, Settings.getSpriteResourcesPath() + "/Background2.png")
                .end();
        
        hub.layerMan.add(this.background, Layer.BACKGROUND);   
        hub.layerMan.toBack(this.background, Layer.BACKGROUND);     
    }
    
    /**
     * Initialize the piece box.
     * @param hub
     */
    private void initializePieceBox(ManagerHub hub)
    {                        
        // Create the piece preview window.
        this.nextPieceBox = new Box.Builder(670, 120)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                .opacity(90)
                .border(Border.MEDIUM)
                .width(90).height(90).end();
        hub.layerMan.add(this.nextPieceBox, Layer.UI);
        
        this.nextPieceGrid = new PieceGrid.Builder(
                    this.nextPieceBox.getX(),
                    this.nextPieceBox.getY() - 1,
                    PieceGrid.RenderMode.VECTOR
                )
                .alignmentMode(PieceGrid.AlignmentMode.TO_PIECE)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                .cellWidth(22)
                .cellHeight(22)
                .end();                
        hub.layerMan.add(this.nextPieceGrid, Layer.UI);               
    }
    
    /**
     * Initialize the bars.
     * @param hub
     */
    private void initializeBars(ManagerHub hub)
    {        
        // Create the timer bar.
        this.timerBar = new ProgressBar.Builder(400, 98)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))               
                .textPosition(ProgressBar.TextPosition.NONE)
                .barColor(ProgressBar.BarColor.BLUE)
                .end();
        hub.layerMan.add(this.timerBar, Layer.UI);        
                
         // Create the progress bar.
        this.progressBar = new ProgressBar.Builder(400, 501)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER)) 
                .textPosition(ProgressBar.TextPosition.BOTTOM)
                .end();
        hub.layerMan.add(this.progressBar, Layer.UI);           
    }
    
    /**
     * Initialize the various groups.
     */
    private void initializeGroups(ManagerHub hub)
    {        
        // Initialize pause group.                
        this.pauseGroup = new PauseGroup(hub);
        hub.groupMan.register(pauseGroup);
        
        hub.listenerMan.registerListener(Listener.MOVE, this.pauseGroup);
        hub.listenerMan.registerListener(Listener.LINE, this.pauseGroup);
        hub.listenerMan.registerListener(Listener.GAME, this.pauseGroup);
             
        // Initialize game over group.
        this.gameOverGroup = new GameOverGroup(hub);    
        hub.groupMan.register(this.gameOverGroup);
        
        hub.listenerMan.registerListener(Listener.GAME, this.gameOverGroup);
        
        // Initialize options group.
        this.optionsGroup = new OptionsGroup(hub);
        hub.groupMan.register(this.optionsGroup);
        
        // Initialize high score group.
        this.highScoreGroup = new HighScoreGroup(hub); 
        hub.groupMan.register(this.highScoreGroup);
        
        hub.listenerMan.registerListener(Listener.GAME, this.highScoreGroup);                
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
    public void updateLogic(Game game, ManagerHub hub)
    {              
        // If the high score button was just clicked.
        if (highScoreButton.clicked())
        {
            if (highScoreButton.isActivated())            
            {                           
                hub.groupMan.showGroup(highScoreButton, highScoreGroup, 
                        GroupManager.Class.HIGH_SCORE,
                        GroupManager.Layer.MIDDLE);            
            }
            else
            {
                hub.groupMan.hideGroup(
                        GroupManager.Class.HIGH_SCORE,
                        GroupManager.Layer.MIDDLE);
            }
        } // end if
        
        // If the pause button was just clicked.
        if (pauseButton.clicked())
        {            
            if (pauseButton.isActivated())            
            {                
                hub.groupMan.showGroup(pauseButton, pauseGroup, 
                        GroupManager.Class.PAUSE,
                        GroupManager.Layer.MIDDLE);            
            }
            else
            {
                hub.groupMan.hideGroup(
                        GroupManager.Class.PAUSE,
                        GroupManager.Layer.MIDDLE);            
            }
        } // end if
        
        // If the options button was just clicked.
        if (optionsButton.clicked())
        {                           
            if (optionsButton.isActivated())  
            {                
                hub.groupMan.showGroup(optionsButton, optionsGroup,
                        GroupManager.Class.OPTIONS,
                        GroupManager.Layer.MIDDLE);            
            }
            else     
            {
                hub.groupMan.hideGroup(
                        GroupManager.Class.OPTIONS,
                        GroupManager.Layer.MIDDLE);
            }
        } // end if  
        
        // Update the timer bar.
        //this.timerBar.setProgressValue(timerMan.getCurrrentTime());

        // Draw the high score text.
        if (!highScoreLabel.getText().equals(String.valueOf(hub.scoreMan.getHighScore())))
        {
            highScoreLabel.setText(String.format("%,d", hub.scoreMan.getHighScore()));
        }                        

        if (!hub.tutorialMan.isTutorialRunning())
        {
            // Set the level text.
            if (!levelLabel.getText().equals(String.valueOf(hub.levelMan.getLevel())))
            {
                levelLabel.setText(String.valueOf(hub.levelMan.getLevel()));                
            }

            // Set the score text.
            if (!scoreLabel.getText().equals(String.valueOf(hub.scoreMan.getTotalScore())))
            {
                scoreLabel.setText(String.format("%,d", hub.scoreMan.getTotalScore()));
            }
        }
        else
        {
            // Set the level text.
            if (!levelLabel.getText().equals(hub.tutorialMan.getRunningTutorial().getName()))
            {                
                levelLabel.setText(hub.tutorialMan.getRunningTutorial().getName());
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
        this.progressBar.setProgressUpper(event.getNextTargetLevelScore());
    }

    public void scoreIncreased(ScoreEvent event)
    {
        // Ignore this, use the scoreChanged event instead.
    }
    
    public void scoreChanged(ScoreEvent event)
    {       
        // Update the progress bar.
        this.progressBar.setProgressValue(event.getScore());       
    }

    public void targetScoreChanged(ScoreEvent event)
    {
        // Update the progress bar.
        this.progressBar.setProgressUpper(event.getScore());
    }

    public void pieceAdded(PieceEvent event)
    {
        // Update the piece preview.
        this.nextPieceGrid.loadStructure(event.getNextPiece().getStructure());
        this.nextPieceGrid.setColor(event.getNextPiece().getType().getColor());
    }

    public void tickOccurred(TimerEvent event)
    {
        //LogManager.recordMessage("Tick! Current time is " + event.getCurrentTime() + ".");
        this.timerBar.setProgressValue(event.getCurrentTime());
    }

    public void currentTimeReset(TimerEvent event)
    {
        CouchLogger.get().recordMessage(this.getClass(),
                "Time Reset! Current time is " + event.getCurrentTime() + ".");
        this.timerBar.setProgressValue(event.getCurrentTime());
    }

    public void startTimeChanged(TimerEvent event)
    {
        CouchLogger.get().recordMessage(this.getClass(),
                "Start time changed! Start time is " + event.getStartTime() + ".");
        this.timerBar.setProgressValue(event.getStartTime());
        this.timerBar.setProgressUpper(event.getStartTime());        
    }
    
}