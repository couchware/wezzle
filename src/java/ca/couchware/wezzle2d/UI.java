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
import ca.couchware.wezzle2d.audio.Sound;
import ca.couchware.wezzle2d.event.GameEvent;
import ca.couchware.wezzle2d.event.IGameListener;
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
import ca.couchware.wezzle2d.group.DemoOverGroup;
import ca.couchware.wezzle2d.group.GameOverGroup;
import ca.couchware.wezzle2d.group.HelpGroup;
import ca.couchware.wezzle2d.group.HighScoreGroup;
import ca.couchware.wezzle2d.group.OptionsGroup;
import ca.couchware.wezzle2d.group.PauseGroup;
import ca.couchware.wezzle2d.piece.PieceGrid;
import ca.couchware.wezzle2d.manager.GroupManager;
import ca.couchware.wezzle2d.manager.LayerManager.Layer;
import ca.couchware.wezzle2d.manager.ListenerManager.Listener;
import ca.couchware.wezzle2d.manager.Settings;
import ca.couchware.wezzle2d.manager.Settings.Key;
import ca.couchware.wezzle2d.manager.SettingsManager;
import ca.couchware.wezzle2d.piece.Piece;
import ca.couchware.wezzle2d.ui.Box;
import ca.couchware.wezzle2d.ui.Box.Border;
import ca.couchware.wezzle2d.ui.IButton;
import ca.couchware.wezzle2d.ui.ITextLabel;
import ca.couchware.wezzle2d.ui.ProgressBar;
import ca.couchware.wezzle2d.ui.Button;
import ca.couchware.wezzle2d.ui.MammothButton;
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
        IGameListener, ILevelListener, IPieceListener, IScoreListener, ITimerListener
{       
    final private IWindow win;
    final private ManagerHub hub;
    
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

    private enum Background
    {
        Blue,
        Purple,
        Green,
        Gold
    }

    private EnumMap<Background, GraphicEntity> backgroundMap
            = new EnumMap<Background, GraphicEntity>(Background.class);

    private Background background;
    private GraphicEntity backgroundGraphic;
    private IAnimation backgroundAnimation = FinishedAnimation.get();

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
    private DemoOverGroup demoOverGroup;

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
    private UI(IWindow win, Game game, ManagerHub hub)
    {
        this.win = win;
        this.hub = hub;        

        initializeButtons();        
        initializeLabels();       
        initializeBackgrounds();
        initializeBoard();
        initializeTraditionalPieceBox();
        initializeOverlayPieceBox();
        initializeBars();
        initializeGroups(win, game);

        initalizeMasterList();
        this.ruleList = new ArrayList<Rule>(this.masterRuleList);
    }        
    
    /**
     * Create a new UI instance.
     * 
     * @param game
     * @param hub
     * @return
     */
    public static UI newInstance(IWindow win, Game game, ManagerHub hub)
    {
        return new UI(win, game, hub);
    }       

    /**
     * Create the master rule list.     
     */
    private void initalizeMasterList()
    {        
        List<Rule> list = new ArrayList<Rule>();

        // Make it so the rocket block is added.
        list.add(new Rule(Rule.Type.LEVEL, Rule.Operation.EQ, 5)
        {
            @Override
            public void onMatch()
            {
                if (hub.statMan.getStartLevel() == 5) return;

                Background bg = getBackgroundForLevel( 5 );
                IAnimation animation = animateBackgroundTo( bg );
                enqueueBackgroundAnimation( animation );
            }
        });

        // Make it so the rocket block is added.
        list.add(new Rule(Rule.Type.LEVEL, Rule.Operation.EQ, 10)
        {
            @Override
            public void onMatch()
            {
                if (hub.statMan.getStartLevel() == 10) return;

                Background bg = getBackgroundForLevel( 10 );
                IAnimation animation = animateBackgroundTo( bg );

                enqueueBackgroundAnimation( animation );
            }
        });

        // Make it so the rocket block is added.
        list.add(new Rule(Rule.Type.LEVEL, Rule.Operation.EQ, 15)
        {
            @Override
            public void onMatch()
            {
                if (hub.statMan.getStartLevel() == 15) return;

                Background bg = getBackgroundForLevel( 15 );
                IAnimation animation = animateBackgroundTo( bg );

                enqueueBackgroundAnimation( animation );
            }
        });

        this.masterRuleList = Collections.unmodifiableList( list );
    }    

    /**
     * Initializes all the buttons that appear on the main game screen.
     */
    private void initializeButtons()
    {        
        // The high score button.
        if (!Game.isApplet())
        {
            highScoreButton = new MammothButton.Builder(win, 128, 299)
                    .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                    .text("")
                    .normalOpacity(0).hoverOpacity(70).activeOpacity(95).build();

            hub.layerMan.add(highScoreButton, Layer.UI);
        }
        
        // Create pause button.        
        pauseButton = new Button.Builder(win, 668, 211)
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
     */
    private void initializeLabels()
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
     */
    private void initializeBackgrounds()
    {
        this.backgroundMap.put( Background.Blue,
                new GraphicEntity.Builder( 0, 0, BLUE_BACKGROUND_PATH ).build());

        this.backgroundMap.put( Background.Purple,
                new GraphicEntity.Builder( 0, 0, PURPLE_BACKGROUND_PATH ).build());

        this.backgroundMap.put( Background.Green,
                new GraphicEntity.Builder( 0, 0, GREEN_BACKGROUND_PATH ).build());

        this.backgroundMap.put( Background.Gold,
                new GraphicEntity.Builder( 0, 0, GOLD_BACKGROUND_PATH ).build());
        
        setBackgroundTo( Background.Blue );
    }

    /**
     * Initializes the board layer.
     */
    private void initializeBoard()
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
     */
    private void initializeTraditionalPieceBox()
    {        
        this.traditionalPieceBoxLabel = new LabelBuilder(668, 65)
                .alignment(EnumSet.of(Alignment.BOTTOM, Alignment.CENTER))
                .text("Next")
                .build();
        hub.layerMan.add(this.traditionalPieceBoxLabel, Layer.UI);

        this.traditionalPieceBox = new Box.Builder(win, 668, 110)
                .border(Border.MEDIUM)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                .opacity(90)
                .width(80).height(80).build();

        hub.layerMan.add(this.traditionalPieceBox, Layer.UI);        

        Color c = hub.settingsMan.getColor(Key.GAME_COLOR_PRIMARY);
        Color color = CouchColor.newInstance(c, 255).toColor();

        this.traditionalPieceBoxGrid = new PieceGrid.Builder(
                    win,
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
     */
    private void initializeOverlayPieceBox()
    {
        Color color = CouchColor.newInstance(Color.WHITE, 80).toColor();      
        
        this.overlayPieceBoxGrid = new PieceGrid.Builder(
                    win, 400, 300 - 1,
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
     */
    private void initializeBars()
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
     */
    private void initializeGroups(IWindow win, Game game)
    {        
        // Initialize pause group.                
        this.pauseGroup = new PauseGroup(hub);
        hub.groupMan.register(pauseGroup);
        
        hub.listenerMan.registerListener(Listener.MOVE, this.pauseGroup);
        hub.listenerMan.registerListener(Listener.LINE, this.pauseGroup);
        hub.listenerMan.registerListener(Listener.GAME, this.pauseGroup);                     
        
        // Initialize options group.
        this.optionsGroup = new OptionsGroup(win, hub);
        hub.groupMan.register(this.optionsGroup);

        // Initialize help group.
        this.helpGroup = new HelpGroup(win, game, hub);
        hub.groupMan.register(this.helpGroup);
        
        // Initialize high score group.
        this.highScoreGroup = new HighScoreGroup(win, hub);
        hub.groupMan.register(this.highScoreGroup);
        hub.listenerMan.registerListener(Listener.GAME, this.highScoreGroup);

        // Initialize game over group.
        if (Game.isApplet())
        {
            this.demoOverGroup = new DemoOverGroup(win, hub);
            hub.groupMan.register(this.demoOverGroup);            
        }
        else
        {
            this.gameOverGroup = new GameOverGroup(win, hub);
            hub.groupMan.register(this.gameOverGroup);
            hub.listenerMan.registerListener(Listener.GAME, this.gameOverGroup);
        }
        
    }

    /**
     * Creates an animation that will fade the current background into
     * the specified background.
     *
     * @param bg
     * @param layerMan
     * @return
     */
    private IAnimation animateBackgroundTo(final Background bg)
    {
        if (this.background == bg)
        {
            return FinishedAnimation.get();
        }

        final GraphicEntity oldBgGraphic = backgroundGraphic;
        final GraphicEntity newBgGraphic
                = backgroundMap.get( bg );

        background = bg;
        backgroundGraphic = newBgGraphic;

        newBgGraphic.setOpacity( 0 );
        
        assert( !hub.layerMan.contains( newBgGraphic, Layer.BACKGROUND ) );
        hub.layerMan.add( newBgGraphic, Layer.BACKGROUND );

        IAnimation fadeOutOldBg = new FadeAnimation
                .Builder( FadeAnimation.Type.OUT, oldBgGraphic)
                .duration( 5000 )
                .build();

        fadeOutOldBg.addAnimationListener( new AnimationAdapter()
        {

            @Override
            public void animationFinished()
            {
                hub.layerMan.remove( oldBgGraphic, Layer.BACKGROUND );
            }

        });

        IAnimation fadeInNewBg = new FadeAnimation
                .Builder( FadeAnimation.Type.IN, newBgGraphic )
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

    /**
     * Change the game background instantly to the specified background.
     * 
     * @param bg
     */
    private void setBackgroundTo(Background bg)
    {
        if ( !this.backgroundAnimation.isFinished() )
            this.backgroundAnimation.setFinished();

        if ( this.backgroundGraphic != null )
            hub.layerMan.remove( this.backgroundGraphic, Layer.BACKGROUND );

        this.background = bg;
        this.backgroundGraphic = this.backgroundMap.get( bg );
        this.backgroundGraphic.setOpacity( 100 );

        assert( !hub.layerMan.contains( this.backgroundGraphic, Layer.BACKGROUND ) );
        hub.layerMan.add(this.backgroundGraphic, Layer.BACKGROUND);
    }

    /**
     * Get the appropriate background for a specified level.
     * 
     * @param level
     * @return
     */
    private Background getBackgroundForLevel(int level)
    {
        if (level < 5) return Background.Blue;
        if (level < 10) return Background.Purple;
        if (level < 15) return Background.Green;
        else return Background.Gold;
    }

    private void enqueueBackgroundAnimation(IAnimation animation)
    {
        if (animation == null)
            throw new IllegalArgumentException("Animation cannot be null");

        // Cancel the existing animation, if it's still running.
        if ( !this.backgroundAnimation.isFinished() )        
            this.backgroundAnimation.setFinished();

        this.backgroundAnimation = animation;
        hub.gameAnimationMan.add( this.backgroundAnimation );
    }

    public void showGameOverGroup(GroupManager groupMan)
    {        
        groupMan.showGroup(null, Game.isApplet() ? demoOverGroup : gameOverGroup,
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

        hub.uiAnimationMan.remove( this.barFadeAnimation );

        EntityGroup bars = new EntityGroup( this.timerBar, this.progressBar );
        bars.setOpacity( 0 );

        this.barFadeAnimation = new FadeAnimation
                .Builder( FadeAnimation.Type.IN, bars )
                .duration( 250 )
                .build();

        hub.uiAnimationMan.add( this.barFadeAnimation );
    }

    public void hideBarsUsingFade()
    {
        if (this.timerBar.getOpacity() == 0 && this.progressBar.getOpacity() == 0)
            return;

        hub.uiAnimationMan.remove( this.barFadeAnimation );

        EntityGroup bars = new EntityGroup( this.timerBar, this.progressBar );
        bars.setOpacity( 100 );

        this.barFadeAnimation = new FadeAnimation
                .Builder( FadeAnimation.Type.OUT, bars )
                .duration( 250 )
                .build();

        hub.uiAnimationMan.add( this.barFadeAnimation );
    }

    /** This is used to fade in and out the progress bars. */
    private IAnimation boardFadeAnimation = FinishedAnimation.get();

    public void showBoardUsingFade()
    {
        if (this.board.getOpacity() == 90)
            return;

        hub.uiAnimationMan.remove( this.boardFadeAnimation );

        this.boardFadeAnimation = new FadeAnimation
                .Builder(
                    FadeAnimation.Type.IN,
                    this.board)
                .duration( 250 )
                .maxOpacity( 90 )
                .build();

        hub.uiAnimationMan.add( this.boardFadeAnimation );
    }

    public void hideBoardUsingFade()
    {
        if (this.board.getOpacity() == 0)
            return;

        hub.uiAnimationMan.remove( this.boardFadeAnimation );

        this.boardFadeAnimation = new FadeAnimation
                .Builder(
                    FadeAnimation.Type.OUT,
                    this.board)
                .duration( 250 )
                .build();

        hub.uiAnimationMan.add( this.boardFadeAnimation );
    }
    
    /** This is used to fade in and out the progress bars. */
    private IAnimation boxFadeAnimation = FinishedAnimation.get();

    public void showTraditionalPieceBoxUsingFade()
    {
        if (!this.traditionalPiecePreviewVisible
                || this.traditionalPieceBox.getOpacity() == 100)
            return;

        hub.uiAnimationMan.remove( this.boxFadeAnimation );

        EntityGroup pieceBoxEntities = new EntityGroup(
                this.traditionalPieceBoxLabel,
                this.traditionalPieceBox );

        pieceBoxEntities.setOpacity( 0 );

        this.boxFadeAnimation = new FadeAnimation
                .Builder( FadeAnimation.Type.IN, pieceBoxEntities )
                .duration( 250 )
                .build();

        this.traditionalPieceBoxGrid.setVisible( true );

        hub.uiAnimationMan.add( this.boxFadeAnimation );
    }

    public void hideTraditionalPieceBoxUsingFade()
    {
        if (!this.traditionalPiecePreviewVisible
                || this.traditionalPieceBox.getOpacity() == 0)
            return;

        hub.uiAnimationMan.remove( this.boxFadeAnimation );

        EntityGroup pieceBoxEntities = new EntityGroup(
                this.traditionalPieceBoxLabel,
                this.traditionalPieceBox );

        pieceBoxEntities.setOpacity( 100 );

        this.boxFadeAnimation = new FadeAnimation
                .Builder( FadeAnimation.Type.OUT, pieceBoxEntities )
                .duration( 250 )
                .build();

        this.traditionalPieceBoxGrid.setVisible( false );

        hub.uiAnimationMan.add( this.boxFadeAnimation );
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
        
        if (!Game.isApplet() && highScoreButton.clicked())
        {
            hub.soundMan.play( Sound.CLICK_LIGHT );
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
                        !game.shouldHidePieceGrid());
            }
        } // end if
                
        if (pauseButton.clicked())
        {
            hub.soundMan.play( Sound.CLICK_LIGHT );
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
                        !game.shouldHidePieceGrid());
            }
        } // end if
                
        if (optionsButton.clicked())
        {
            hub.soundMan.play( Sound.CLICK_LIGHT );
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
                        !game.shouldHidePieceGrid());
            }
        } // end if

        if (helpButton.clicked())
        {
            hub.soundMan.play( Sound.CLICK_LIGHT );
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
                        !game.shouldHidePieceGrid());
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
    }

    public void tickOccurred(TimerEvent event)
    {        
        this.timerBar.setProgressValue(event.getCurrentTime());
    }

    public void currentTimeReset(TimerEvent event)
    {       
        this.timerBar.setProgressValue(event.getCurrentTime());
    }

    public void startTimeChanged(TimerEvent event)
    {        
        this.timerBar.setProgressUpper(event.getStartTime());
    }
    
    public void gameStarted(GameEvent event)
    {        
        Background bg = getBackgroundForLevel( event.getLevel() );
        setBackgroundTo( bg );
    }

    public void gameReset(GameEvent event)
    {
        this.ruleList = new ArrayList<Rule>(this.masterRuleList);

        Background bg = getBackgroundForLevel( event.getLevel() );        
        IAnimation animation = animateBackgroundTo( bg );
        enqueueBackgroundAnimation( animation );
    }

    public void gameOver(GameEvent event)
    {

    }
    
}
