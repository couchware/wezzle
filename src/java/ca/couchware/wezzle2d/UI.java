/*
 *  Wezzle
 *  Copyright (c) 2007-2009 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d;

import ca.couchware.wezzle2d.ResourceFactory.LabelBuilder;
import ca.couchware.wezzle2d.animation.AnimationAdapter;
import ca.couchware.wezzle2d.animation.FadeAnimation;
import ca.couchware.wezzle2d.animation.FinishedAnimation;
import ca.couchware.wezzle2d.animation.IAnimation;
import ca.couchware.wezzle2d.animation.MetaAnimation;
import ca.couchware.wezzle2d.event.ILevelListener;
import ca.couchware.wezzle2d.event.IPieceListener;
import ca.couchware.wezzle2d.event.IScoreListener;
import ca.couchware.wezzle2d.event.ITimerListener;
import ca.couchware.wezzle2d.event.LevelEvent;
import ca.couchware.wezzle2d.event.PieceEvent;
import ca.couchware.wezzle2d.event.ScoreEvent;
import ca.couchware.wezzle2d.event.TimerEvent;
import ca.couchware.wezzle2d.graphics.EntityGroup;
import ca.couchware.wezzle2d.graphics.GraphicEntity;
import ca.couchware.wezzle2d.graphics.IEntity;
import ca.couchware.wezzle2d.graphics.IPositionable.Alignment;
import ca.couchware.wezzle2d.piece.PieceGrid;
import ca.couchware.wezzle2d.manager.GroupManager;
import ca.couchware.wezzle2d.manager.LayerManager.Layer;
import ca.couchware.wezzle2d.manager.ListenerManager.Listener;
import ca.couchware.wezzle2d.manager.Settings;
import ca.couchware.wezzle2d.manager.Settings.Key;
import ca.couchware.wezzle2d.manager.SettingsManager;
import ca.couchware.wezzle2d.piece.Piece;
import ca.couchware.wezzle2d.util.CouchLogger;
import ca.couchware.wezzle2d.ui.Box;
import ca.couchware.wezzle2d.ui.Box.Border;
import ca.couchware.wezzle2d.ui.IButton;
import ca.couchware.wezzle2d.ui.ITextLabel;
import ca.couchware.wezzle2d.ui.ProgressBar;
import ca.couchware.wezzle2d.ui.Button;
import ca.couchware.wezzle2d.ui.MammothButton;
import ca.couchware.wezzle2d.group.GameOverGroup;
import ca.couchware.wezzle2d.group.HelpGroup;
import ca.couchware.wezzle2d.group.HighScoreGroup;
import ca.couchware.wezzle2d.group.OptionsGroup;
import ca.couchware.wezzle2d.group.PauseGroup;
import ca.couchware.wezzle2d.manager.AnimationManager;
import ca.couchware.wezzle2d.manager.LayerManager;
import ca.couchware.wezzle2d.util.CouchColor;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;

/**
 * A class for handling the Wezzle UI.
 * 
 * @author cdmckay
 */
public class UI implements 
        ILevelListener, IPieceListener, IScoreListener, ITimerListener
{       
     
    /** The graphics file extension. */
    final private static String FILE_EXT = ".png";

    final private static String BLUE_BACKGROUND_PATH =
            Settings.getSpriteResourcesPath()
            + "/Background_Circles_Blue" + FILE_EXT;

    final private static String PURPLE_BACKGROUND_PATH =
            Settings.getSpriteResourcesPath()
            + "/Background_Circles_Purple" + FILE_EXT;

    final private static String GREEN_BACKGROUND_PATH =
            Settings.getSpriteResourcesPath()
            + "/Background_Circles_Green" + FILE_EXT;

    final private static String GOLD_BACKGROUND_PATH =
            Settings.getSpriteResourcesPath()
            + "/Background_Circles_Gold" + FILE_EXT;

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

    /** The game board. */
    final private String BOARD_PATH =
            Settings.getSpriteResourcesPath()
            + "/Board" + FILE_EXT;

    private AnimationManager animationMan;

    private enum Background
    {
        Blue,
        Purple,
        Green,
        Gold
    }

    private EnumMap<Background, GraphicEntity> backgroundMap
            = new EnumMap<Background, GraphicEntity>(Background.class);

    private GraphicEntity background;
    private GraphicEntity board;
        
    private ProgressBar timerBar;
        
    private ProgressBar progressBar; 
           
    private IEntity traditionalPieceBox;
    private IEntity traditionalPieceBoxLabel;
    private PieceGrid traditionalPieceBoxGrid;
    
    private PieceGrid overlayPieceBoxGrid;    

    private GraphicEntity scoreHeaderLabel;
    private ITextLabel scoreLabel;

    private GraphicEntity highScoreHeaderLabel;
    private ITextLabel highScoreLabel;
    
    private GraphicEntity levelHeader;
    private ITextLabel levelLabel;            
    
    private ITextLabel versionLabel;     
    private ITextLabel copyrightLabel;

    private IButton pauseButton;
    private IButton optionsButton;
    private IButton helpButton;
    private IButton highScoreButton;

    private PauseGroup pauseGroup;      
    private OptionsGroup optionsGroup;
    private HelpGroup helpGroup;
    private HighScoreGroup highScoreGroup;
    private GameOverGroup gameOverGroup;

    /**
     * The master rule list.  Contains all the rules that should exist
     * at the start of a new game.
     */
    private List<Rule> masterRuleList;

    /**
     * The current rule list.  Contains all the rules that have not yet
     * been realized for the current game.
     */
    private List<Rule> ruleList;

    /**
     * Private constructor to ensure singletonness.
     */
    private UI(Game game, ManagerHub hub)
    {
        this.animationMan = hub.uiAnimationMan;

        initializeButtons(hub);        
        initializeLabels(hub);       
        initializeBackgrounds(hub);
        initializeBoard(hub);
        initializeTraditionalPieceBox(hub);
        initializeOverlayPieceBox(hub);
        initializeBars(hub);
        initializeGroups(game, hub);

        initalizeMasterList( hub );
        this.ruleList = new ArrayList<Rule>(this.masterRuleList);
    }        
    
    /**
     * Create a new UI instance.
     * 
     * @param game
     * @param hub
     * @return
     */
    public static UI newInstance(Game game, ManagerHub hub)
    {
        return new UI(game, hub);
    }       

    /**
     * Create the master rule list.
     *
     * @param hub
     */
    private void initalizeMasterList(ManagerHub hub)
    {
        final AnimationManager animMan = hub.gameAnimationMan;
        final LayerManager layerMan = hub.layerMan;
        List<Rule> list = new ArrayList<Rule>();

        // Make it so the rocket block is added.
        list.add(new Rule(Rule.Type.LEVEL, Rule.Operation.EQ, 5)
        {
            @Override
            public void onMatch()
            {
                IAnimation animation
                        = animateBackgroundTo( Background.Purple, layerMan );

                animMan.add( animation );
            }
        });

        // Make it so the rocket block is added.
        list.add(new Rule(Rule.Type.LEVEL, Rule.Operation.EQ, 10)
        {
            @Override
            public void onMatch()
            {
                IAnimation animation
                        = animateBackgroundTo( Background.Green, layerMan );

                animMan.add( animation );
            }
        });

        // Make it so the rocket block is added.
        list.add(new Rule(Rule.Type.LEVEL, Rule.Operation.EQ, 15)
        {
            @Override
            public void onMatch()
            {
                IAnimation animation
                        = animateBackgroundTo( Background.Gold, layerMan );

                animMan.add( animation );
            }
        });

        this.masterRuleList = Collections.unmodifiableList( list );
    }    

    /**
     * Initializes all the buttons that appear on the main game screen.
     *
     * @param hub
     */
    private void initializeButtons(ManagerHub hub)
    {        
        // The high score button.
        highScoreButton = new MammothButton.Builder(128, 299)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))                
                .text("")
                .normalOpacity(0).hoverOpacity(70).activeOpacity(95).build();
        hub.layerMan.add(highScoreButton, Layer.UI);
                
        // Create pause button.        
        pauseButton = new Button.Builder(668, 211)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                //.type(SpriteButton.Type.NORMAL)
                .width(170)
                .text("Pause").activeText("Resume")
                .normalOpacity(70).build();
        hub.layerMan.add(pauseButton, Layer.UI);    
        
        // Create the options button, using pause button as a template.
        optionsButton = new Button.Builder((Button) pauseButton)
                .y(299).text("Options").build();
        hub.layerMan.add(optionsButton, Layer.UI);                
        
        // Create the help buttton, using pause button as a template.
        helpButton = new Button.Builder((Button) optionsButton)
                .y(387).text("Help").build();
        hub.layerMan.add(helpButton, Layer.UI);     
    }
    
    /**
     * Initializes all the labels.
     *
     * @param hub
     */
    private void initializeLabels(ManagerHub hub)
    {          
        // Shortcut to the primary color.
        final Color PRIMARY_COLOR = 
                SettingsManager.get().getColor(Key.GAME_COLOR_PRIMARY);
               
        // Set up the copyright label.
        copyrightLabel = new LabelBuilder(10, 600 - 10)
                .alignment(EnumSet.of(Alignment.BOTTOM, Alignment.LEFT))
                .color(PRIMARY_COLOR).size(12)                
                .text(Game.COPYRIGHT).build();
        hub.layerMan.add(copyrightLabel, Layer.INFORMATION);
        
        // Set up the version label.	
        versionLabel = new LabelBuilder(800 - 10, 600 - 10)
                .alignment(EnumSet.of(Alignment.BOTTOM, Alignment.RIGHT))
                .color(PRIMARY_COLOR).size(12)                
                .text(Game.TITLE)
                .build();
        hub.layerMan.add(versionLabel, Layer.INFORMATION);
        		  
        
        // Set up the level header.
        levelHeader = new GraphicEntity.Builder(126, 153, LEVEL_HEADER_PATH)                
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER)).build();
        hub.layerMan.add(levelHeader, Layer.UI);
        
        // Set up the level text.
        levelLabel = new LabelBuilder(126, 210)
                .alignment(EnumSet.of(Alignment.BOTTOM, Alignment.CENTER))                
                .color(PRIMARY_COLOR).size(20).text("").build();
        hub.layerMan.add(levelLabel, Layer.UI);        
        
        // Set up the score header.
        highScoreHeaderLabel = 
                new GraphicEntity.Builder(127, 278, HIGH_SCORE_HEADER_PATH)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER)).build();
        hub.layerMan.add(highScoreHeaderLabel, Layer.UI);
                        
        // Set up the high score text.
        highScoreLabel = new LabelBuilder(126, 337)
                .alignment(EnumSet.of(Alignment.BOTTOM, Alignment.CENTER))                
                .color(PRIMARY_COLOR).size(20).text("").build();
        hub.layerMan.add(highScoreLabel, Layer.UI);
        
        // Set up the score header.
        scoreHeaderLabel = new GraphicEntity.Builder(128, 403, SCORE_HEADER_PATH)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER)).build();
        hub.layerMan.add(scoreHeaderLabel, Layer.UI);
        
        // Set up the score text.
        scoreLabel = new LabelBuilder(126, 460)
                .alignment(EnumSet.of(Alignment.BOTTOM, Alignment.CENTER))                
                .color(PRIMARY_COLOR).size(20).text("").build();
        hub.layerMan.add(scoreLabel, Layer.UI);
    }
    
    /**
     * Initializes the background.
     *
     * @param hub
     */
    private void initializeBackgrounds(ManagerHub hub)
    {
        this.backgroundMap.put( Background.Blue,
                new GraphicEntity.Builder( 0, 0, BLUE_BACKGROUND_PATH ).build());

        this.backgroundMap.put( Background.Purple,
                new GraphicEntity.Builder( 0, 0, PURPLE_BACKGROUND_PATH ).build());

        this.backgroundMap.put( Background.Green,
                new GraphicEntity.Builder( 0, 0, GREEN_BACKGROUND_PATH ).build());

        this.backgroundMap.put( Background.Gold,
                new GraphicEntity.Builder( 0, 0, GOLD_BACKGROUND_PATH ).build());
        
        this.background = this.backgroundMap.get( Background.Blue );
        
        hub.layerMan.add(this.background, Layer.BACKGROUND);           
    }

    /**
     * Initializes the board layer.
     *
     * @param hub
     */
    private void initializeBoard(ManagerHub hub)
    {
        // Create the background.
		this.board = new GraphicEntity
                .Builder(
                        hub.boardMan.getX() - 12,
                        hub.boardMan.getY() - 12,
                        BOARD_PATH)
                .opacity( 90 )
                .build();

        hub.layerMan.add(this.board, Layer.BOARD);
    }

    /**
     * Initialize the traditional piece box
     *
     * @param hub
     */
    private void initializeTraditionalPieceBox(ManagerHub hub)
    {        
        this.traditionalPieceBoxLabel = new LabelBuilder(668, 65)
                .alignment(EnumSet.of(Alignment.BOTTOM, Alignment.CENTER))
                .text("Next")
                .build();
        hub.layerMan.add(this.traditionalPieceBoxLabel, Layer.UI);

        this.traditionalPieceBox = new Box.Builder(668, 110)
                .border(Border.MEDIUM)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                .opacity(90)
                .width(80).height(80).build();

        hub.layerMan.add(this.traditionalPieceBox, Layer.UI);        

        Color c = hub.settingsMan.getColor(Key.GAME_COLOR_PRIMARY);
        Color color = CouchColor.newInstance(c, 255).toColor();

        this.traditionalPieceBoxGrid = new PieceGrid.Builder(
                    this.traditionalPieceBox.getX(),
                    this.traditionalPieceBox.getY(),                   
                    PieceGrid.RenderMode.VECTOR
                )
                .color(color)
                .alignmentMode(PieceGrid.AlignmentMode.TO_PIECE)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                .cellWidth(16)
                .cellHeight(16)
                .build();
        
        hub.layerMan.add(this.traditionalPieceBoxGrid, Layer.UI);

        // Set the visibility based on the settings.
        boolean visible = hub.settingsMan.getBool(Key.USER_PIECE_PREVIEW_TRADITIONAL);
        this.setTraditionalPiecePreviewVisible(visible);
    }

    /**
     * Initialize the overlay piece box.
     *
     * @param hub
     */
    private void initializeOverlayPieceBox(ManagerHub hub)
    {
        Color color = CouchColor.newInstance(Color.WHITE, 80).toColor();      
        
        this.overlayPieceBoxGrid = new PieceGrid.Builder(
                    400,
                    300 - 1,
                    PieceGrid.RenderMode.VECTOR
                )
                .color(color)
                .alignmentMode(PieceGrid.AlignmentMode.TO_PIECE)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                .cellWidth(50)
                .cellHeight(50)
                .build();
        
        hub.layerMan.add(this.overlayPieceBoxGrid, Layer.PIECE_GRID);

        // Set the visibility based on the settings.
        boolean visible = hub.settingsMan.getBool(Key.USER_PIECE_PREVIEW_OVERLAY);
        this.setOverlayPiecePreviewVisible(visible);
    }
    
    /**
     * Initialize the bars.
     *
     * @param hub
     */
    private void initializeBars(ManagerHub hub)
    {        
        // Create the timer bar.
        this.timerBar = new ProgressBar.Builder(400, 98)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))               
                .textPosition(ProgressBar.TextPosition.NONE)
                .barColor(ProgressBar.BarColor.BLUE)
                .build();
        hub.layerMan.add(this.timerBar, Layer.UI);        
                
         // Create the progress bar.
        this.progressBar = new ProgressBar.Builder(400, 500)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER)) 
                .textPosition(ProgressBar.TextPosition.BOTTOM)
                .build();
        hub.layerMan.add(this.progressBar, Layer.UI);           
    }
    
    /**
     * Initialize the various groups.
     *
     * @param game
     * @param hub
     */
    private void initializeGroups(Game game, ManagerHub hub)
    {        
        // Initialize pause group.                
        this.pauseGroup = new PauseGroup(hub);
        hub.groupMan.register(pauseGroup);
        
        hub.listenerMan.registerListener(Listener.MOVE, this.pauseGroup);
        hub.listenerMan.registerListener(Listener.LINE, this.pauseGroup);
        hub.listenerMan.registerListener(Listener.GAME, this.pauseGroup);                     
        
        // Initialize options group.
        this.optionsGroup = new OptionsGroup(hub);
        hub.groupMan.register(this.optionsGroup);

        // Initialize help group.
        this.helpGroup = new HelpGroup(game, hub);
        hub.groupMan.register(this.helpGroup);
        
        // Initialize high score group.
        this.highScoreGroup = new HighScoreGroup(hub); 
        hub.groupMan.register(this.highScoreGroup);
        hub.listenerMan.registerListener(Listener.GAME, this.highScoreGroup);

        // Initialize game over group.
        this.gameOverGroup = new GameOverGroup(hub);
        hub.groupMan.register(this.gameOverGroup);
        hub.listenerMan.registerListener(Listener.GAME, this.gameOverGroup);
    }

    /**
     * Creates an animation that will fade the current background into
     * the specified background.
     *
     * @param bg
     * @param layerMan
     * @return
     */
    private IAnimation animateBackgroundTo(
            final Background bg,
            final LayerManager layerMan)
    {
        final GraphicEntity oldBg = background;
        final GraphicEntity newBg
                = backgroundMap.get( bg );

        background = newBg;

        newBg.setOpacity( 0 );
        layerMan.add( newBg, Layer.BACKGROUND );

        IAnimation fadeOutOldBg = new FadeAnimation
                .Builder( FadeAnimation.Type.OUT, oldBg)
                .duration( 5000 )
                .build();

        fadeOutOldBg.addAnimationListener( new AnimationAdapter()
        {

            @Override
            public void animationFinished()
            {
                layerMan.remove( oldBg, Layer.BACKGROUND );
            }

        });

        IAnimation fadeInNewBg = new FadeAnimation
                .Builder( FadeAnimation.Type.IN, newBg )
                .duration( 5000 )
                .build();

        IAnimation meta = new MetaAnimation
                .Builder()
                .add( fadeOutOldBg )
                .add( fadeInNewBg )
                .runRule( MetaAnimation.RunRule.SIMULTANEOUS )
                .finishRule( MetaAnimation.FinishRule.ALL )
                .build();

        return meta;
    }

    public void showGameOverGroup(GroupManager groupMan)
    {
        // Draw game over screen.
        //gameOverGroup.setScore(scoreMan.getTotalScore());
        groupMan.showGroup(null, gameOverGroup, 
                GroupManager.Type.GAME_OVER,
                GroupManager.Layer.BOTTOM);  
    }
    
    public void showPauseGroup(GroupManager groupMan)
    {
        groupMan.showGroup(pauseButton, pauseGroup, 
                GroupManager.Type.PAUSE,
                GroupManager.Layer.MIDDLE);
    }      

    /** Whether or not the traditional piece preview is visible. */
    private boolean traditionalPiecePreviewVisible = true;

    /**
     * Gets the visibility of the traditional piece preview.
     * @return
     */
    public boolean isTraditionalPiecePreviewVisible()
    {
        return this.traditionalPiecePreviewVisible;
    }

    /**
     * Sets the visibility of the traditional piece preview.
     * @param visible
     */
    public void setTraditionalPiecePreviewVisible(boolean visible)
    {
        if (this.traditionalPiecePreviewVisible == visible) return;

        this.traditionalPiecePreviewVisible = visible;        
        this.traditionalPieceBoxGrid.setVisible(visible);
    }

    /** Whether or not the overlay piece preview is visible. */
    private boolean overlayPiecePreviewVisible = true;

    /**
     * Gets the visibility of the overlay piece preview.
     * @return
     */
    public boolean isOverlayPiecePreviewVisible()
    {
        return overlayPiecePreviewVisible;
    }

    /**
     * Sets the visibility of the overlay piece preview.
     * @param visible
     */
    public void setOverlayPiecePreviewVisible(boolean visible)
    {
        if (this.overlayPiecePreviewVisible == visible) return;

        this.overlayPiecePreviewVisible = visible;
        this.overlayPieceBoxGrid.setVisible(visible);
    }

    /**
     * Set the piece preview piece.
     * @param piece
     */
    public void setPiecePreviewPiece(Piece piece)
    {
        this.traditionalPieceBoxGrid.loadStructure(piece.getStructure());
        this.overlayPieceBoxGrid.loadStructure(piece.getStructure());
    }

    /** This is used to fade in and out the progress bars. */
    private IAnimation barFadeAnimation = FinishedAnimation.get();

    public void showBarsUsingFade()
    {
        if (this.timerBar.getOpacity() == 100 && this.progressBar.getOpacity() == 100)
            return;

        animationMan.remove( this.barFadeAnimation );

        EntityGroup bars = new EntityGroup( this.timerBar, this.progressBar );
        bars.setOpacity( 0 );

        this.barFadeAnimation = new FadeAnimation
                .Builder( FadeAnimation.Type.IN, bars )
                .duration( 250 )
                .build();

        animationMan.add( this.barFadeAnimation );
    }

    public void hideBarsUsingFade()
    {
        if (this.timerBar.getOpacity() == 0 && this.progressBar.getOpacity() == 0)
            return;

        animationMan.remove( this.barFadeAnimation );

        EntityGroup bars = new EntityGroup( this.timerBar, this.progressBar );
        bars.setOpacity( 100 );

        this.barFadeAnimation = new FadeAnimation
                .Builder( FadeAnimation.Type.OUT, bars )
                .duration( 250 )
                .build();

        animationMan.add( this.barFadeAnimation );
    }

    /** This is used to fade in and out the progress bars. */
    private IAnimation boardFadeAnimation = FinishedAnimation.get();

    public void showBoardUsingFade()
    {
        if (this.board.getOpacity() == 90)
            return;

        animationMan.remove( this.boardFadeAnimation );

        this.boardFadeAnimation = new FadeAnimation
                .Builder(
                    FadeAnimation.Type.IN,
                    this.board)
                .duration( 250 )
                .maxOpacity( 90 )
                .build();

        animationMan.add( this.boardFadeAnimation );
    }

    public void hideBoardUsingFade()
    {
        if (this.board.getOpacity() == 0)
            return;

        animationMan.remove( this.boardFadeAnimation );

        this.boardFadeAnimation = new FadeAnimation
                .Builder(
                    FadeAnimation.Type.OUT,
                    this.board)
                .duration( 250 )
                .build();

        animationMan.add( this.boardFadeAnimation );
    }
    
    /** This is used to fade in and out the progress bars. */
    private IAnimation boxFadeAnimation = FinishedAnimation.get();

    public void showTraditionalPieceBoxUsingFade()
    {
        if (!this.traditionalPiecePreviewVisible
                || this.traditionalPieceBox.getOpacity() == 100)
            return;

        animationMan.remove( this.boxFadeAnimation );

        EntityGroup pieceBoxEntities = new EntityGroup(
                this.traditionalPieceBoxLabel,
                this.traditionalPieceBox );

        pieceBoxEntities.setOpacity( 0 );

        this.boxFadeAnimation = new FadeAnimation
                .Builder( FadeAnimation.Type.IN, pieceBoxEntities )
                .duration( 250 )
                .build();

        this.traditionalPieceBoxGrid.setVisible( true );

        animationMan.add( this.boxFadeAnimation );
    }

    public void hideTraditionalPieceBoxUsingFade()
    {
        if (!this.traditionalPiecePreviewVisible
                || this.traditionalPieceBox.getOpacity() == 0)
            return;

        animationMan.remove( this.boxFadeAnimation );

        EntityGroup pieceBoxEntities = new EntityGroup(
                this.traditionalPieceBoxLabel,
                this.traditionalPieceBox );

        pieceBoxEntities.setOpacity( 100 );

        this.boxFadeAnimation = new FadeAnimation
                .Builder( FadeAnimation.Type.OUT, pieceBoxEntities )
                .duration( 250 )
                .build();

        this.traditionalPieceBoxGrid.setVisible( false );

        animationMan.add( this.boxFadeAnimation );
    }

    /**
     * Evalutes all rules in the current rule list and runs any
     * matching rules.
     *
     * @param game
     * @param hub
     */
    public void evaluateRules(Game game, ManagerHub hub)
    {
        for (Iterator<Rule> it = ruleList.iterator(); it.hasNext(); )
        {
            Rule rule = it.next();

            if (rule.evaluate(game, hub) == true)
            {
                rule.onMatch();
                it.remove();
            }
        } // end for
    }

    /**
     * Update the UI logic.
     * 
     * @param game The current game state.
     */
    public void updateLogic(Game game, ManagerHub hub)
    {
        evaluateRules( game, hub );
        
        if (highScoreButton.clicked())
        {
            if (highScoreButton.isActivated())            
            {                           
                hub.groupMan.showGroup(highScoreButton, highScoreGroup, 
                        GroupManager.Type.HIGH_SCORE,
                        GroupManager.Layer.MIDDLE);            
            }
            else
            {
                hub.groupMan.hideGroup(
                        GroupManager.Type.HIGH_SCORE,
                        GroupManager.Layer.MIDDLE,
                        !game.isCompletelyBusy());
            }
        } // end if
                
        if (pauseButton.clicked())
        {            
            if (pauseButton.isActivated())            
            {                
                hub.groupMan.showGroup(pauseButton, pauseGroup, 
                        GroupManager.Type.PAUSE,
                        GroupManager.Layer.MIDDLE);            
            }
            else
            {
                hub.groupMan.hideGroup(
                        GroupManager.Type.PAUSE,
                        GroupManager.Layer.MIDDLE,
                        !game.isCompletelyBusy());
            }
        } // end if
                
        if (optionsButton.clicked())
        {                           
            if (optionsButton.isActivated())  
            {                
                hub.groupMan.showGroup(optionsButton, optionsGroup,
                        GroupManager.Type.OPTIONS,
                        GroupManager.Layer.MIDDLE);            
            }
            else     
            {
                hub.groupMan.hideGroup(
                        GroupManager.Type.OPTIONS,
                        GroupManager.Layer.MIDDLE,
                        !game.isCompletelyBusy());
            }
        } // end if

        if (helpButton.clicked())
        {
            if (helpButton.isActivated())
            {
                hub.groupMan.showGroup(helpButton, helpGroup,
                        GroupManager.Type.HELP,
                        GroupManager.Layer.MIDDLE);
            }
            else
            {
                hub.groupMan.hideGroup(
                        GroupManager.Type.HELP,
                        GroupManager.Layer.MIDDLE,
                        !game.isCompletelyBusy());
            }
        } // end if

        if (!highScoreLabel.getText().equals(String.valueOf(hub.scoreMan.getHighScore())))        
            highScoreLabel.setText(String.format("%,d", hub.scoreMan.getHighScore()));
                                
        if ( !hub.tutorialMan.isTutorialRunning() )
        {
            if ( !levelLabel.getText().equals(
                    String.valueOf( hub.levelMan.getLevel() ) ) )
            {
                levelLabel.setText( String.valueOf( hub.levelMan.getLevel() ) );
            }

            if ( !scoreLabel.getText().equals(
                    String.valueOf( hub.scoreMan.getTotalScore() ) ) )
            {
                scoreLabel.setText( String.format( "%,d", hub.scoreMan.
                        getTotalScore() ) );
            }
        }
        else
        {
            if ( !levelLabel.getText().equals(
                    hub.tutorialMan.getRunningTutorial().getName() ) )
            {
                levelLabel.setText( hub.tutorialMan.getRunningTutorial().getName() );
            }

            if ( !scoreLabel.getText().equals( "" ) )
            {
                scoreLabel.setText( "" );
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
        Piece piece = event.getNextPiece();
        this.overlayPieceBoxGrid.loadStructure(piece.getStructure());
        this.traditionalPieceBoxGrid.loadStructure(piece.getStructure());
        //this.nextPieceGrid.setColor(event.getNextPiece().getType().getColor());
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
        this.timerBar.setProgressUpper(event.getStartTime());
    }
    
}
