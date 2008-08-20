/*
 *  Wezzle
 *  Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d;

import ca.couchware.wezzle2d.BoardManager.AnimationType;
import ca.couchware.wezzle2d.ResourceFactory.LabelBuilder;
import ca.couchware.wezzle2d.BoardManager.Direction;
import ca.couchware.wezzle2d.LayerManager.Layer;
import ca.couchware.wezzle2d.ResourceFactory.LabelBuilder;
import ca.couchware.wezzle2d.ScoreManager.ScoreType;
import ca.couchware.wezzle2d.animation.*;
import ca.couchware.wezzle2d.audio.*;
import ca.couchware.wezzle2d.graphics.IPositionable.Alignment;
import ca.couchware.wezzle2d.graphics.*;
import ca.couchware.wezzle2d.event.LevelEvent;
import ca.couchware.wezzle2d.event.LineEvent;
import ca.couchware.wezzle2d.event.ScoreEvent;
import ca.couchware.wezzle2d.menu2.Loader;
import ca.couchware.wezzle2d.menu2.MainMenu;
import ca.couchware.wezzle2d.tile.*;
import ca.couchware.wezzle2d.tutorial.*;
import ca.couchware.wezzle2d.ui.*;
import ca.couchware.wezzle2d.ui.group.*;
import ca.couchware.wezzle2d.util.*;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * The main hook of our game. This class with both act as a manager for the
 * display and central mediator for the game logic.
 * 
 * Display management will consist of a loop that cycles round all entities in
 * the game asking them to move and then drawing them in the appropriate place.
 * With the help of an inner class it will also allow the player to control the
 * main ship.
 * 
 * As a mediator it will be informed when entities within our game detect events
 * (e.g. alien killed, played died) and will take appropriate game actions.
 * 
 * @author Cameron, Kevin Grad (based on code by Kevin Glass)
 */
public class Game extends Canvas implements IGameWindowCallback
{	  
    //--------------------------------------------------------------------------
    // Static Members
    //--------------------------------------------------------------------------        
                
    /**
     * The refator types.
     */
    public static enum RefactorType
    {
        NORMAL, DROP
    }
    
    /**
     * The Manager Types.
     */
    public static enum ManagerType
    {
        ACHIEVEMENT, BOARD, GROUP, HIGHSCORE, LAYER, LISTENER, PIECE, PROPERTY,
        SCORE, STAT, TIMER, WORLD, ANIMATION, TUTORIAL, MUSIC, SOUND
    }
    
    /**
     * The default refactor speed.
     */
    final public static int DEFAULT_REFACTOR_SPEED = 180;
    
    /**
     * The default drop speed.
     */
    final public static int DEFAULT_DROP_SPEED = 250;
    
    /**
     * The width of the screen.
     */
    final public static int SCREEN_WIDTH = 800;
    
    /**
     * The height of the screen .
     */
    final public static int SCREEN_HEIGHT = 600;
    
    /**
     * The path to the resources.
     */
    final public static String RESOURCES_PATH = "resources";        

    /**
     * The path to the fonts.
     */
    final public static String FONTS_PATH = RESOURCES_PATH + "/fonts";
    
    /**
     * The path to the sprites.
     */
    final public static String SPRITES_PATH = RESOURCES_PATH + "/sprites";
    
    /**
     * The path to the sounds.
     */
    final public static String SOUNDS_PATH = RESOURCES_PATH + "/sounds";
    
    /**
     * The path to the music.
     */
    final public static String MUSIC_PATH = RESOURCES_PATH + "/music";
    
    /**
     * The level header path.
     */
    final private static String LEVEL_HEADER_PATH = Game.SPRITES_PATH 
            + "/Header_Level.png";
    
    /**
     * The score header path.
     */
    final private static String SCORE_HEADER_PATH = Game.SPRITES_PATH 
            + "/Header_Score.png";
    
    /**
     * The high score header path.
     */
    final private static String HIGH_SCORE_HEADER_PATH = Game.SPRITES_PATH 
            + "/Header_HighScore.png";       
    
    /**
     * The text color.
     */
    final public static Color TEXT_COLOR1 = new Color(252, 233, 45);
    
    /**
     * The secondary colour.
     */
    final public static Color TEXT_COLOR2 = new Color(178, 178, 178);
        
    /**
     * The line score color.
     */
    final public static Color SCORE_LINE_COLOR = new Color(252, 233, 45);
    
    /**
     * The piece score color.
     */
    final public static Color SCORE_PIECE_COLOR = new Color(240, 240, 240);
    
    /**
     * The bomb score color.
     */ 
    final public static Color SCORE_BOMB_COLOR = new Color(255, 127, 0);
  
//    /**
//     * The background layer.
//     */
//    final public static int Layer.BACKGROUND = 0;
//    
//    /**
//     * The board layer.
//     */
//    final public static int Layer.TILE = 1;
//    
//    /**
//     * The effects layer.
//     */
//    final public static int Layer.EFFECT = 2;
//    
//    /**
//     * The UI layer.
//     */
//    final public static int Layer.UI = 3;     
    
    /**
     * The name of the application.
     */
    final private static String APPLICATION_NAME = "Wezzle";
    
    /**
     * The version of the application.
     */
    final private static String APPLICATION_VERSION = "Test 6";     
    
    /**
     * The full title of the game.
     */
    final public static String TITLE = APPLICATION_NAME + " " + APPLICATION_VERSION;
    
    /**
     * The copyright.
     */
    final public static String COPYRIGHT = "\u00A9 2008 Couchware Inc.";
    
    //--------------------------------------------------------------------------
    // Public Members
    //--------------------------------------------------------------------------
    
    /**
     * The current build number.
     */
    public String buildNumber;        
    
    /**
     * The loader.
     */
    public Loader loader;
    
    /**
     * The main menu.
     */
    public MainMenu mainMenu;
    
    /**
     * The animation manager in charge of animations.
     */
    public AnimationManager animationMan;
    
    /**
	 * The manager in charge of maintaining the board.
	 */
    public BoardManager boardMan;
    
    /**
     * The menu manager.
     */
    public GroupManager groupMan;
       
    /**
     * The high score manager.
     */    
    public HighScoreManager highScoreMan;
    	
    /**
     * The game layer manager.
     */
    public LayerManager layerMan;
        
    /** 
     * The manager in charge of the moves. 
     */
    public StatManager statMan;	
    
    /**
     * The menu manager.
     */
    //public MenuManager menuMan;
    
    /**
     * The manager in charge of music.
     */
    public MusicManager musicMan;  

    /**
     * The manager in charge of moving the piece around with the
     * pointer and drawing the piece to the board.
     */
    public PieceManager pieceMan;
	
    /** 
     * The manager in charge of loading and saving properties.
     */
    public PropertyManager propertyMan;
    
    /** 
     * The manager in charge of score.
     */
    public ScoreManager scoreMan;        
    
    /** 
     * The manager in charge of sound.
     */
    public SoundManager soundMan;             
    
    /** 
     * The manager in charge of keeping track of the time. 
     */
    public TimerManager timerMan;
    
    /**
     * The manager in charge of running tutorials.
     */
    public TutorialManager tutorialMan;   
    
    /**
     * The Manager in charge of the world.
     */
    public WorldManager worldMan;    	
    
    /**
     * The Manager in charge of Listeners.
     */
    public ListenerManager listenerMan = ListenerManager.get();
    
    /**
     * The manager in charge of achievements
     */
    public AchievementManager achievementMan;
	
    /**
     * The pause button.
     */
    public IButton pauseButton;
       
    /**
     * The options button.
     */
    public IButton optionsButton;
    
    /**
     * The help button.
     */
    public IButton helpButton;
    
    /**
     * The progress bar.
     */
    public ProgressBar progressBar;        
    
    //--------------------------------------------------------------------------
    // Private Members
    //--------------------------------------------------------------------------
    
    /**
     * The build nunber path.
     */
    final private static String BUILD_NUMBER_PATH = 
            RESOURCES_PATH + "/build.number";                             
    
    /** 
     * The normal title of the window. 
     */
    private String windowTitle = APPLICATION_NAME;	              
    
    /**
     * The executor used by certain managers.
     */
    private Executor executor;            
    
    /**
     * If true, refactor will be activated next loop.
     */
    private boolean activateRefactor = false;
    
    /**
     * If true, the board is currently being refactored downwards.
     */
    private boolean refactorVerticalInProgress = false;
    
    /**
     * If true, the board is currently being refactored leftward.
     */
    private boolean refactorHorizontalInProgress = false;
    
    /**
     * The current refactor animations.
     */
    private List<IAnimation> refactorAnimationList;
    
    /**
     * The refactor type.
     */
    private RefactorType refactorType;
    
    /**
     * The speed of the upcoming refactor.
     */
    private int refactorSpeed;
    
    /**
     * The speed of the upcoming drop.
     */
    private int dropSpeed;
    
    /**
     * If true, a line removal will be activated next loop.
     */
    private boolean activateLineRemoval = false;       
    
    /**
     * The last line that was matched.  This is used by the "star" item
     * to determine whether a bomb is "in the wild" (i.e. should be removed)
     * or part of the line (i.e. should be left to explode).
     */
    private Set<Integer> lastMatchSet;
    
    /**
     * If true, a line removal is in progress.
     */
    private boolean tileRemovalInProgress = false;
    
    /**
     * The set of tile indices that will be removed.
     */
    private Set<Integer> tileRemovalSet;
    
    /**
     * If true, uses jump animation instead of zoom.
     */
    private boolean tileRemovalUseJumpAnimation = false;
    
    /**
     * If true, award no points for this tile removal.
     */
    private boolean tileRemovalNoScore = false;
    
    /**
     * If true, do not activate items on this removal.
     */
    private boolean tileRemovalNoItems = false;
    
    /**
     * If true, a bomb removal will be activated next loop.
     */
    private boolean activateBombRemoval = false;
    
    /**
     * The set of bomb tile indices that will be removed.
     */
    private Set<Integer> bombRemovalSet;       
    
    /**
     * If true, a star removal will be activated next loop.
     */
    private boolean activateStarRemoval = false;
    
    /**
     * The set of star tile indices that will be removed.
     */
    private Set<Integer> starRemovalSet;     
    
    /**
     * If true, a rocket removal will be activated next loop.
     */
    private boolean activateRocketRemoval = false;
    
    /**
     * The set of star tile indices that will be removed.
     */
    private Set<Integer> rocketRemovalSet;           
    
    /**
     * If true, the board show animation will be activated next loop.
     */
    private boolean activateBoardShowAnimation = false;
    
    /**
     * If true, the board hide animation will be activated next loop.
     */
    private boolean activateBoardHideAnimation = false;
    
    /**
     * The board animation type to use.
     */
    private AnimationType boardAnimationType;
    
    /**
     * The animation that will indicate whether the board animation is 
     * complete.
     */
    private IAnimation boardAnimation = null;
    
    /**
     * If true, the game will end next loop.
     */
    private boolean activateGameOver = false;                        
        
    /**
     * The time at which the last rendering looped started from the point of
     * view of the game logic.
     */
    private long lastLoopTime;
	
	/** 
     * The window that is being used to render the game. 
     */
    private IGameWindow window;

	/** 
     * The time since the last record of FPS. 
     */
    private long lastFramesPerSecondTime = 0;
	
	/** 
     * The recorded FPS. 
     */
    private int framesPerSecond;   
    
    /**
     * The background sprite.
     */
    private AbstractEntity background;
    
	/** 
     * The timer text. 
     */
    private ILabel timerLabel;
      
    /**
     * The score header graphic.
     */
    private GraphicEntity scoreHeaderLabel;
        
    /** 
     * The score text.
     */
    private ILabel scoreLabel;
    
    /**
     * The high score header graphic.
     */
    private GraphicEntity highScoreHeaderLabel;
            
    /** 
     * The high score text. 
     */
    private ILabel highScoreLabel;
    
    /**
     * The high score header button.
     */
    private IButton highScoreButton;

    
    /**
     * The level header graphic.
     */
    private GraphicEntity levelHeader;
    
    /**
     * The level text.
     */
    private ILabel levelLabel;            
    
    /**
     * The version label.
     */
    private ILabel versionLabel;     
    
    /**
     * The copyright label.
     */
    private ILabel copyrightLabel;
    
    /**
     * The pause group.
     */
    private PauseGroup pauseGroup;
    
    /**
     * The game over group.
     */
    private GameOverGroup gameOverGroup;    
    
    /**
     * The options group.
     */
    private OptionsGroup optionsGroup;
    
     /**
     * The high score group.
     */
    private HighScoreGroup highScoreGroup;
    
    //--------------------------------------------------------------------------
    // Constructor
    //--------------------------------------------------------------------------
    
	/**
	 * Construct our game and set it running.
	 * 
	 * @param renderingType
	 *            The type of rendering to use (should be one of the contansts
	 *            from ResourceFactory)
	 */
	public Game(ResourceFactory.RenderType renderType) 
	{
        // Get the build number.
        Properties buildProperties = new Properties();            
               
        try
        {
            URL url = this.getClass().getClassLoader()
                .getResource(BUILD_NUMBER_PATH);  
            
            InputStream in = url.openStream();
            buildProperties.load(in);
            in.close();
            
            buildNumber = 
                    (String) buildProperties.getProperty("build.number");
        }
        catch (Exception e)
        {
            LogManager.recordException(e);
            LogManager.recordWarning("Could not find build number at: "
                    + BUILD_NUMBER_PATH + "!",
                    "Game#this");
            buildNumber = "???";
        }
        finally
        {
            if (buildNumber == null)
                buildNumber = "???";
        }               
        
        // Print the build number.
        LogManager.recordMessage("Date: " + (new Date()));   
        LogManager.recordMessage("Wezzle Build: " + buildNumber);
        LogManager.recordMessage("Wezzle Version: " + APPLICATION_VERSION);
        LogManager.recordMessage("Java Version: " + System.getProperty("java.version"));
        LogManager.recordMessage("OS Name: " + System.getProperty("os.name"));
        LogManager.recordMessage("OS Architecture: " + System.getProperty("os.arch"));
        LogManager.recordMessage("OS Version: " + System.getProperty("os.version"));
        
		// Create a window based on a chosen rendering method.
		ResourceFactory.get().setRenderingType(renderType);		        
        
		final Runnable r = new Runnable()
		{
			public void run()
			{                             
				window = ResourceFactory.get().getGameWindow();
				window.setResolution(SCREEN_WIDTH, SCREEN_HEIGHT);
				window.setGameWindowCallback(Game.this);
				window.setTitle(windowTitle);
			}		
		};
		
		try
		{
			javax.swing.SwingUtilities.invokeAndWait(r);
		}
		catch (InterruptedException e)
		{
			LogManager.recordException(e);
		}
		catch (InvocationTargetException e)
		{
			LogManager.recordException(e);
		}		
	}

	public void startRendering()
	{
		window.startRendering();
	}
    
    /**
     * Initializes all the managers (except for the layer manager).
     */
    private void initializeManagers(EnumSet<ManagerType> managerSet)
    {        
        if (managerSet.contains(ManagerType.LAYER))
        {
            // Create the layer manager.   
            layerMan = LayerManager.newInstance();  
        }
        
        if (managerSet.contains(ManagerType.ANIMATION))
        {
            // Create the animation manager.
            animationMan = AnimationManager.newInstance();                
         }
        
        if (managerSet.contains(ManagerType.TUTORIAL))
        {
            // Create the tutorial manager.
            tutorialMan = TutorialManager.newInstance();

            // Add the tutorials to it.
            tutorialMan.add(new BasicTutorial());
            tutorialMan.add(new RocketTutorial());
            tutorialMan.add(new BombTutorial());
            tutorialMan.add(new StarTutorial());
        }
    
        if (managerSet.contains(ManagerType.PROPERTY))
        {
            // Create the property manager. Must be done before Score manager.
            propertyMan = PropertyManager.newInstance();
        }
        
        if (managerSet.contains(ManagerType.HIGHSCORE))
        {
            // Create the high score manager.
            highScoreMan = HighScoreManager.newInstance(propertyMan);  
        }
        
        if (managerSet.contains(ManagerType.WORLD))
        {
            // Create the world manager.
            worldMan = WorldManager.newInstance(propertyMan);  
            worldMan.setGameInProgress(true);
        }
        
        if (managerSet.contains(ManagerType.BOARD))
        {
            // Create the board manager.
            boardMan = BoardManager.newInstance(animationMan, layerMan, 272, 139, 8, 10);    
            boardMan.setVisible(false);
            boardMan.generateBoard(worldMan.getItemList());          
            startBoardShowAnimation(AnimationType.ROW_FADE);  
        }
        
        if (managerSet.contains(ManagerType.PIECE))
        {
            // Create the piece manager.
            pieceMan = PieceManager.newInstance(window, animationMan, boardMan);        
            layerMan.add(pieceMan.getPieceGrid(), Layer.EFFECT);
        }
        
        if (managerSet.contains(ManagerType.GROUP))
        {
            // Create group manager.
            groupMan = GroupManager.newInstance(layerMan, pieceMan);
        }
	        
        if (managerSet.contains(ManagerType.SCORE))
        {
            // Create the score manager.
            scoreMan = ScoreManager.newInstance(boardMan, propertyMan, highScoreMan);
            scoreMan.setTargetLevelScore(worldMan.generateTargetLevelScore());
        }
        
        if (managerSet.contains(ManagerType.SOUND))
        {
            // Create the sound manager.
            soundMan = SoundManager.newInstance(executor, propertyMan);
        }
        
        if (managerSet.contains(ManagerType.MUSIC))
        {
            // Create the music manager.
            musicMan = MusicManager.newInstance(executor, propertyMan);  
        }
        
        if (managerSet.contains(ManagerType.STAT))
        {
            // Create the move manager.
            statMan = StatManager.newInstance();
        }
        
        if (managerSet.contains(ManagerType.TIMER))
        {
            // Create the time manager.
            timerMan = TimerManager.newInstance(worldMan.getInitialTimer()); 
        }
        
        if (managerSet.contains(ManagerType.ACHIEVEMENT))
        {
            // Create the achievement manager.
            achievementMan = AchievementManager.newInstance();
        
            // Load the test achievements.        
            List<Rule> rules1 = new LinkedList<Rule>();
            List<Rule> rules2 = new LinkedList<Rule>();
            List<Rule> rules3 = new LinkedList<Rule>();
            List<Rule> rules4 = new LinkedList<Rule>();

            rules1.add(new Rule(Rule.Type.SCORE, Rule.Operation.GT, 2000));

            achievementMan.add(new Achievement(rules1, 
                     "Score greater than 2000", Achievement.Difficulty.BRONZE));        

            rules2.add(new Rule(Rule.Type.SCORE, 
                    Rule.Operation.GT, 5000));

            achievementMan.add(new Achievement(rules2, 
                     "Score greater than 5000", Achievement.Difficulty.BRONZE));

            rules3.add(new Rule(Rule.Type.SCORE, Rule.Operation.GT, 1000));        
            rules3.add(new Rule(Rule.Type.MOVES, Rule.Operation.LTEQ, 3));

            achievementMan.add(new Achievement(rules3, 
                     "Score greater than 1000, Moves less than or equal to 3", 
                     Achievement.Difficulty.BRONZE));

            rules4.add(new Rule(Rule.Type.LEVEL, Rule.Operation.GT, 2));

            achievementMan.add(new Achievement(rules4, 
                     "Level greater than 2", Achievement.Difficulty.BRONZE));
        }
        
        // Register the listeners. 
        if (managerSet.contains(ManagerType.SCORE))
        {
            listenerMan.registerScoreListener(scoreMan);
        }
        
        if (managerSet.contains(ManagerType.STAT))
        {
            listenerMan.registerMoveListener(statMan);
            listenerMan.registerLineListener(statMan);
        }
        if (managerSet.contains(ManagerType.WORLD))
        {
            listenerMan.registerLevelListener(worldMan);
        }
    }
    
    /**
     * Initializes all the buttons that appear on the main game screen.
     */
    private void initializeButtons()
    {        
        // The high score button.
        highScoreButton = new SpriteButton.Builder(128, 299)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                .type(SpriteButton.Type.LARGE).text("")
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
    private void initializeLabels()
    {                     
        // Set up the copyright label.
        copyrightLabel = new LabelBuilder(10, 600 - 10)
                .alignment(EnumSet.of(Alignment.BOTTOM, Alignment.LEFT))
                .cached(false).color(TEXT_COLOR1).size(12)                
                .text(COPYRIGHT).end();
        layerMan.add(copyrightLabel, Layer.UI);
        
        // Set up the version label.	
        versionLabel = new LabelBuilder(800 - 10, 600 - 10)
                .alignment(EnumSet.of(Alignment.BOTTOM, Alignment.RIGHT))
                .cached(false).color(TEXT_COLOR1).size(12)                
                .text(TITLE)
                .end();                        
        layerMan.add(versionLabel, Layer.UI);
        
		// Set up the timer text.
        timerLabel = new LabelBuilder(400, 70)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                .color(TEXT_COLOR1).size(50).text("--").end();
        layerMan.add(timerLabel, Layer.UI);
             
        // Set up the level header.
        levelHeader = new GraphicEntity.Builder(126, 153, LEVEL_HEADER_PATH)                
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER)).end();        
        layerMan.add(levelHeader, Layer.UI);
        
        // Set up the level text.
        levelLabel = new LabelBuilder(126, 210)
                .alignment(EnumSet.of(Alignment.BOTTOM, Alignment.CENTER))
                .cached(false)
                .color(TEXT_COLOR1).size(20).text("--").end();                
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
                .color(TEXT_COLOR1).size(20).text("--").end();
        layerMan.add(highScoreLabel, Layer.UI);
        
        // Set up the score header.
        scoreHeaderLabel = new GraphicEntity.Builder(128, 403, SCORE_HEADER_PATH)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER)).end();
        layerMan.add(scoreHeaderLabel, Layer.UI);
        
        // Set up the score text.
        scoreLabel = new LabelBuilder(126, 460)
                .alignment(EnumSet.of(Alignment.BOTTOM, Alignment.CENTER))
                .cached(false)
                .color(TEXT_COLOR1).size(20).text("--").end();
        layerMan.add(scoreLabel, Layer.UI);
    }
    
    /**
     * Initializes miscellaneous components.
     */
    private void initializeComponents()
    {
        // Create the background.
		background = new GraphicEntity
                .Builder(0, 0, SPRITES_PATH + "/Background2.png").end();
        layerMan.add(background, Layer.BACKGROUND);   
        layerMan.toBack(background, Layer.BACKGROUND);
        
        // Create the progress bar.
        progressBar = new ProgressBar.Builder(393, 501)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                .progressMax(scoreMan.getTargetLevelScore()).end();
        layerMan.add(progressBar, Layer.UI);
    }
    
    /**
     * Initialize the various groups.
     */
    private void initializeGroups()
    {        
        // Initialize pause group.                
        pauseGroup = new PauseGroup(layerMan, groupMan);
        groupMan.register(pauseGroup);
             
        // Initialize game over group.
        gameOverGroup = new GameOverGroup(layerMan, groupMan);    
        groupMan.register(gameOverGroup);
        
        // Initialize options group.
        optionsGroup = new OptionsGroup(layerMan, groupMan, propertyMan);
        groupMan.register(optionsGroup);
        
        // Initialize high score group.
        highScoreGroup = new HighScoreGroup(layerMan, groupMan, highScoreMan); 
        groupMan.register(highScoreGroup);
    }
    
    /**
     * Initialize various members.
     */
    private void initializeMembers()
    {
        // Initialize the last line match.
        lastMatchSet = new HashSet<Integer>();
        
        // Initialize line index set.
        tileRemovalSet = new HashSet<Integer>();
        
        // Initialize bomb index set.
        bombRemovalSet = new HashSet<Integer>();
        
        // Initialize star index set.
        starRemovalSet = new HashSet<Integer>();
        
        // Initialize rocket index set.
        rocketRemovalSet = new HashSet<Integer>();               
    }
    
	/**
	 * Initialize the common elements for the game.
	 */
	public void initialize()
	{                
        // Initialize the executor.        
        executor = Executors.newCachedThreadPool();
                
        // Initialize various members.
        initializeMembers();            
        
        // Set the refactor speeds.
        resetRefactorSpeed();
        resetDropSpeed();                                               
        
        // Create the loader.        
        loader = new Loader();               
                
        // Initialize managers.
        loader.addRunnable(new Runnable()
        {
           public void run() { initializeManagers(EnumSet.allOf(ManagerType.class)); }
        });
                               
        // Initialize buttons.    
        loader.addRunnable(new Runnable()
        {
           public void run() { initializeButtons(); }
        });                                 
                
        // Initialize labels.  
        loader.addRunnable(new Runnable()
        {
           public void run() { initializeLabels(); }
        });        
        
        // Initialize miscellaneous components.
        loader.addRunnable(new Runnable()
        {
           public void run() { initializeComponents(); }
        });        
             
        // Initialize the groups.   
        loader.addRunnable(new Runnable()
        {
           public void run() { initializeGroups(); }
        });                                        
                
        // Start the loop counter.
		lastLoopTime = SystemTimer.getTime();
	}        
        
    /**
     * This method is used to update the values shown on the pause screen.
     */
    private void updatePauseGroup()
    {               
        // Update the pause screen date.
        pauseGroup.setMoves(statMan.getMoveCount());
        pauseGroup.setLines(statMan.getLineCount());
        pauseGroup.setLinesPerMove(statMan.getLinesPerMove());         
    }
    
    /**
     * Start a refactor with the given speed.
     * 
     * @param speed
     */
    public void startRefactor(RefactorType type)
    {
        // Set the refactor flag.
        this.activateRefactor = true;   
        
        // Set the type.
        this.refactorType = type;
        
        //Util.handleWarning("Refactor speed is " + refactorSpeed + ".");
    }
    
    /**
     * Clear the refactor flag.
     */
    public void clearRefactor()
    {
       // Set the refactor flag.
       this.activateRefactor = false;
    }

    /**
     * Get the refactor speed, in pixels per second.
     * 
     * @return
     */
    public int getRefactorSpeed()
    {
        return refactorSpeed;
    }

    /**
     * Set the refactor speed, in pixels per second.
     * 
     * @param refactorSpeed
     */
    public void setRefactorSpeed(int refactorSpeed)
    {
        this.refactorSpeed = refactorSpeed;
    }        
    
    /**
     * Resets the refactor speed to it's default value.
     */
    public void resetRefactorSpeed()
    {
        this.refactorSpeed = DEFAULT_REFACTOR_SPEED;
    }

    public int getDropSpeed()
    {
        return dropSpeed;
    }

    public void setDropSpeed(int dropSpeed)
    {
        this.dropSpeed = dropSpeed;
    }
    
    public void resetDropSpeed()
    {
        this.dropSpeed = DEFAULT_DROP_SPEED;
    }
   
    /**
     * A method to check whether the board is busy.
     * 
     * @return True if it is, false otherwise.
     */
    public boolean isBusy()
    {
       return (isRefactoring()               
               || isTileRemoving()
               || gameOverGroup.isActivated() == true
               || activateBoardShowAnimation == true
               || activateBoardHideAnimation == true
               || this.boardAnimation != null);
    }
    
    /**
     * Checks whether a refactor is, or is about to be, in progress.
     */
    public boolean isRefactoring()
    {
        return this.activateRefactor 
                || this.refactorVerticalInProgress 
                || this.refactorHorizontalInProgress;
    }
    
    /**
     * Checks whether tiles are, or are about to be, removed.
     */
    public boolean isTileRemoving()
    {
        return this.activateLineRemoval               
               || this.activateBombRemoval
               || this.activateStarRemoval
               || this.activateRocketRemoval
               || this.tileRemovalInProgress;
    }
    
    public void startBoardShowAnimation(AnimationType type)
    {
        // Set the flag.
        if (activateBoardHideAnimation == true)
            throw new IllegalStateException(
                    "Attempted to show board while it is being hidden.");
        
        if (activateBoardShowAnimation == true)
            throw new IllegalStateException(
                    "Attempted to show board while it is already being shown.");
        
        activateBoardShowAnimation = true;
        boardAnimationType = type;
    }
    
    public void clearBoardShowAnimation()
    {
        // Clear the flag.
        activateBoardShowAnimation = false;        
    }
    
    public void startBoardHideAnimation(AnimationType type)
    {
        // Set the flag.
        if (activateBoardShowAnimation == true)
            throw new IllegalStateException(
                    "Attempted to hide board while it is being shown.");
        
        if (activateBoardHideAnimation == true)
            throw new IllegalStateException(
                    "Attempted to hide board while it is already being hidden.");
        
        activateBoardHideAnimation = true;
        boardAnimationType = type;
    }
    
    public void clearBoardHideAnimation()
    {
        // Clear the flag.
        activateBoardHideAnimation = false;
    }
    
    public void startGameOver()
    {
        LogManager.recordMessage("Game over!", "Game#frameRendering");

        // Add the new score.
        highScoreMan.addScore("TEST", scoreMan.getTotalScore(), 
                worldMan.getLevel());
        highScoreGroup.updateLabels();
        
        // Activate the game over process.
        activateGameOver = true;
    }
    
    public void clearGameOver()
    {
        activateGameOver = false;
    }  

	/**
	 * Notification that a frame is being rendered. Responsible for running game
	 * logic and rendering the scene.
     * 
     * @return True if the frame has been updated, false if nothing has been
     * updated.
	 */
	public boolean frameRendering()
	{
		SystemTimer.sleep(lastLoopTime + 10 - SystemTimer.getTime());

		// Work out how long its been since the last update, this
		// will be used to calculate how far the entities should
		// move this loop.
		long delta = SystemTimer.getTime() - lastLoopTime;
		lastLoopTime = SystemTimer.getTime();
		lastFramesPerSecondTime += delta;
		framesPerSecond++;

		// Update our FPS counter if a second has passed.
		if (lastFramesPerSecondTime >= 1000)
		{
			window.setTitle(windowTitle + " (FPS: " + framesPerSecond + ")");
			lastFramesPerSecondTime = 0;
			framesPerSecond = 0;
		}
        
        // If the loader is running, bypass all the rendering to show it.        
        if (loader != null)
        {   
            // Animate all animations.
            if (animationMan != null)
                animationMan.animate(delta);
            
            if (loader.updateLogic(this) == Loader.State.FINISHED)
            {
                // Remove the loader.
                loader = null;
                
                // Empty the mouse events.
                window.clearMouseEvents();
                
                // Draw normally.
                return layerMan.draw(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);                
                
                // Create the main menu.
                //mainMenu = new MainMenu();
            }   
            else
            {      
                // Draw using the loader.
                return loader.draw();
            }                       
        }
        
        // If the main menu is running, bypass all rendering to show it.
        if (mainMenu != null)
        {
            // Animate all animations.
            if (animationMan != null)
                animationMan.animate(delta);
            
            if (mainMenu.updateLogic(this) == MainMenu.State.FINISHED)
            {
                // Remove the loader.
                mainMenu = null;
                
                // Empty the mouse events.
                window.clearMouseEvents();                                                                    
            }   
            else
            {      
                // Fire the mouse events.
                window.fireMouseEvents();
                
                // Draw using the loader.
                return mainMenu.draw();
            }         
        }
                               
        // If the high score button was just clicked.
        if (highScoreButton.clicked() == true)
        {
            if (highScoreButton.isActivated() == true)            
            {                           
                groupMan.showGroup(highScoreButton, highScoreGroup, 
                        GroupManager.CLASS_HIGH_SCORE,
                        GroupManager.LAYER_MIDDLE);            
            }
            else
                groupMan.hideGroup(GroupManager.CLASS_HIGH_SCORE,
                        GroupManager.LAYER_MIDDLE);
        }
        
        // If the pause button was just clicked.
        if (pauseButton.clicked() == true)
        {            
            if (pauseButton.isActivated() == true)            
            {
                updatePauseGroup();                
                groupMan.showGroup(pauseButton, pauseGroup, 
                        GroupManager.CLASS_PAUSE,
                        GroupManager.LAYER_MIDDLE);            
            }
            else
                groupMan.hideGroup(GroupManager.CLASS_PAUSE,
                        GroupManager.LAYER_MIDDLE);            
        }
        
        // If the options button was just clicked.
        if (optionsButton.clicked() == true)
        {                           
            if (optionsButton.isActivated() == true)  
            {                
                groupMan.showGroup(optionsButton, optionsGroup,
                        GroupManager.CLASS_OPTIONS,
                        GroupManager.LAYER_MIDDLE);            
            }
            else            
                groupMan.hideGroup(GroupManager.CLASS_OPTIONS,
                        GroupManager.LAYER_MIDDLE);
        }    
        
        // Check on board animation.
        if (boardAnimation != null && boardAnimation.isFinished() == true)
        {
            // Set animation visible depending on what animation
            // was just performed.
            if (boardAnimation instanceof FadeAnimation)   
            {
                // Cast it to a fade animation.
                FadeAnimation f = (FadeAnimation) boardAnimation;
                
                switch (f.getType())
                {
                    case IN:
                        
                        boardMan.setVisible(true);
                        pieceMan.getPieceGrid().setVisible(true);
                        break;
                        
                    case OUT:
                        
                        boardMan.setVisible(false);
                        pieceMan.getPieceGrid().setVisible(false);
                        break;
                        
                    default:
                        
                        throw new IllegalStateException(
                                "Unrecogonized fade animation type.");
                }                                               
            }            
            else
            {
                throw new RuntimeException(
                        "Unrecognized board animation class.");
            }

            // Clear the board animation.
            boardAnimation = null;

            // Clear mouse button presses.
            pieceMan.clearMouseButtonSet();

            // If game over is in progress, make a new board and start.
            if (gameOverGroup.isActivated() == true)
            {
                // Draw game over screen.
                gameOverGroup.setScore(scoreMan.getTotalScore());
                groupMan.showGroup(null, gameOverGroup, 
                        GroupManager.CLASS_GAME_OVER,
                        GroupManager.LAYER_BOTTOM);                  
            }
        }
        else if (boardAnimation != null && boardAnimation.isFinished() == false)
        {
            // Board is still dirty due to animation.
            boardMan.setDirty(true);
        }
        
        // Update all the group logic.
        groupMan.updateLogic(this);
        
        // Uphdate the music manager logic.
        musicMan.updateLogic(this);

        // Check to see if we should be showing the board.
        if (activateBoardShowAnimation == true)
        {
            // Hide the piece.            
            pieceMan.getPieceGrid().setVisible(false);                               
            pieceMan.stopAnimation();
            
            // Start board show animation.            
            boardAnimation = boardMan.animateShow(boardAnimationType);                 
            boardMan.setDirty(true);            

            // Clear flag.
            clearBoardShowAnimation();                                
        }

        // Check to see if we should be hiding the board.
        if (activateBoardHideAnimation == true)
        {
            // Hide the piece.
            pieceMan.getPieceGrid().setVisible(false); 
            pieceMan.stopAnimation();
            
            // Start board hide animation.            
            boardAnimation = boardMan.animateHide(boardAnimationType); 
            boardMan.setDirty(true);
                                          
            // Clear flag.
            clearBoardHideAnimation();                                
        }      
        
        // If the pause button is not on, then we proceed with the
        // normal game loop.
        if (groupMan.isActivated() == false)
            updateBoard(delta);
                
        // Whether or not the frame was updated.
        boolean updated = false;
        
        // Fire all the queued mouse events.
        window.fireMouseEvents();
        
        // If the background is dirty, then redraw everything.
        if (background.isDirty() == true)
        {            
            layerMan.draw();
            background.setDirty(false);
            updated = true;
        }
        // Otherwise, only draw what needs to be redrawn.
        else
        {                       
            updated = layerMan.draw(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);         
        }        
        
        //Util.handleMessage(layerMan.toString());
		
		// if escape has been pressed, stop the game
		if (window.isKeyPressed(KeyEvent.VK_ESCAPE))
		{
			System.exit(0);
		}
        
        // If up or down have been pressed
        if (window.isKeyPressed(KeyEvent.VK_UP))
		{
			musicMan.increaseVolume();
		}
        
        if (window.isKeyPressed(KeyEvent.VK_DOWN))
		{
			musicMan.decreaseVolume();
		}
        
         // If right or left have been pressed
        if (window.isKeyPressed(KeyEvent.VK_RIGHT))
		{
			soundMan.increaseVolume();
		}
        
        if (window.isKeyPressed(KeyEvent.VK_LEFT))
		{
			soundMan.decreaseVolume();
		}
        
        // Check the achievements.
        if (achievementMan.evaluate(this) == true)
            achievementMan.reportCompleted();
        
        return updated;
	}  
    
    /**
     * Handles the logic and rendering of the game scene.
     * 
     * @param delta The amount of time that has passed since the last
     * board update.
     */
    private void updateBoard(long delta)
    {
        // See if it's time to level-up.
        if (pieceMan.isTileDropInProgress() == false
                && isBusy() == false)
        {
            // Handle Level up.
            if (scoreMan.getLevelScore() 
                    >= scoreMan.getTargetLevelScore())
            {    
                // Hide piece.                    
                pieceMan.getPieceGrid().setVisible(false);
                pieceMan.stopAnimation();

                LogManager.recordMessage("Level up!", "Game#frameRendering");
                //worldMan.levelUp(this);

                listenerMan.notifyLevelListener(new LevelEvent(1, this, this));
                
                this.activateLineRemoval = true;
                this.tileRemovalUseJumpAnimation = true;
                this.tileRemovalNoScore = true;
                this.tileRemovalNoItems = true;
                tileRemovalSet.clear();

                int j;
                if (boardMan.getGravity().contains(Direction.UP))                        
                    j = 0;
                else
                    j = boardMan.getRows() - 1;

                for (int i = 0; i < boardMan.getColumns(); i++)
                {                         
                    int index = i + (j * boardMan.getColumns());
                    if (boardMan.getTile(index) != null)
                        tileRemovalSet.add(new Integer(index));
                }                                        

                soundMan.play(AudioTrack.SOUND_LEVEL_UP);

                int x = pieceMan.getPieceGrid().getX() 
                        + boardMan.getCellWidth() / 2;

                int y = pieceMan.getPieceGrid().getY() 
                        + boardMan.getCellHeight() / 2;

                final ILabel label = new LabelBuilder(x, y)
                        .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.LEFT))
                        .color(TEXT_COLOR1).size(26).text("Level Up!").end();

                IAnimation a1 = new FadeAnimation.Builder(FadeAnimation.Type.OUT, label).end();                 
                IAnimation a2 = new MoveAnimation.Builder(label)
                        .duration(1150).theta(0).v(0.03).end();

                a2.setStartAction(new Runnable()
                {
                    public void run()
                    { layerMan.add(label, Layer.EFFECT); }
                });

                a2.setFinishAction(new Runnable()
                {
                    public void run()
                    { layerMan.remove(label, Layer.EFFECT); }
                });

                animationMan.add(a1);
                animationMan.add(a2);
                a1 = null;
                a2 = null;                    
            }
        } // end if

        // See if it's game ovaries.
        if (activateGameOver == true)
        {
            // Clear flag.
            clearGameOver();                                

            // Set in progress flag.
            gameOverGroup.setActivated(true);

            // Hide the board.
            startBoardHideAnimation(AnimationType.ROW_FADE);                
        }                                                                  

        // See if we need to activate the refactor.
        if (activateRefactor == true)
        {            
            // Hide piece.
            pieceMan.getPieceGrid().setVisible(false);

            // Start down refactor.                
            switch (refactorType)
            {
                case NORMAL:
                    refactorAnimationList = 
                            boardMan.startVerticalShift(refactorSpeed);
                    break;

                case DROP:
                    refactorAnimationList =
                            boardMan.startVerticalShift(dropSpeed);
                    break;                    

                default: throw new AssertionError();
            }

            // Add to the animation manager.
            // No need to worry about removing them, that'll happen
            // automatically when they are done.
            animationMan.addAll(refactorAnimationList);

            refactorVerticalInProgress = true;

            // Clear flag.
            clearRefactor();
        }

        // See if we're down refactoring.
        if (refactorVerticalInProgress == true)
        {
            boolean done = true;
            for (IAnimation a : refactorAnimationList)
                if (a.isFinished() == false)
                    done = false;

            if (done == true)
            {		
                // Clear the animation list.
                refactorAnimationList = null;

                // Clear down flag.
                refactorVerticalInProgress = false;

                // Synchronize board.
                boardMan.synchronize();							

                // Start left refactor.

                switch (refactorType)
                {
                    case NORMAL:
                        refactorAnimationList = 
                                boardMan.startHorizontalShift(refactorSpeed);
                        break;

                    case DROP:
                        refactorAnimationList = 
                                boardMan.startHorizontalShift(dropSpeed);
                        break; 

                    default: throw new AssertionError();
                }

                // Add to the animation manager.
                // No need to worry about removing them, that'll happen
                // automatically when they are done.
                animationMan.addAll(refactorAnimationList);

                refactorHorizontalInProgress = true;								
            }
        } // end if

        // See if we're left refactoring.
        if (refactorHorizontalInProgress == true)
        {
            boolean done = true;
            for (IAnimation a : refactorAnimationList)
                if (a.isFinished() == false)
                    done = false;

            if (done == true)
            {		
                // Clear the animation list.
                refactorAnimationList = null;

                // Clear left flag.
                refactorHorizontalInProgress = false;

                // Synchronize board.
                boardMan.synchronize();		

                // Look for matches.
                tileRemovalSet.clear();

                statMan.incrementCycleLineCount(
                        boardMan.findXMatch(tileRemovalSet));

                statMan.incrementCycleLineCount(
                        boardMan.findYMatch(tileRemovalSet));

                // Copy the match into the last line match holder.
                lastMatchSet.clear();
                lastMatchSet.addAll(tileRemovalSet);

                // If there are matches, score them, remove 
                // them and then refactor again.
                if (tileRemovalSet.size() > 0)
                {                                       
                    // Activate the line removal.
                    activateLineRemoval = true;                 
                }
                else
                {
                   // Make sure the tiles are not still dropping.
                    if (pieceMan.isTileDropInProgress() == false)
                    {                            
                        pieceMan.loadPiece();   
                        pieceMan.getPieceGrid().setVisible(true);

                        // Unpause the timer.
                        timerMan.resetTimer();
                        timerMan.setPaused(false);

                        // Reset the mouse.
                        pieceMan.clearMouseButtonSet();
                    }
                }
            } // end if

            // Notify piece manager.
            pieceMan.notifyRefactored();
        } // end if

        // If a line removal was activated.
        if (activateLineRemoval == true)
        {                
            // Clear flag.
            activateLineRemoval = false;

            // Increment cascade.
            statMan.incrementChainCount();

            // Calculate score, unless no-score flag is set.
            if (tileRemovalNoScore == false)
            {
                final int deltaScore = scoreMan.calculateLineScore(
                        tileRemovalSet, 
                        ScoreType.LINE,
                        statMan.getChainCount());                               


                // Fire a score event.
                listenerMan.notifyScoreListener(new ScoreEvent(deltaScore, this));

                // Show the SCT.
                ImmutablePosition p = boardMan.determineCenterPoint(tileRemovalSet);

                final ILabel label = new LabelBuilder(p.getX(), p.getY())
                        .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                        .color(SCORE_LINE_COLOR)
                        .size(scoreMan.determineFontSize(deltaScore))
                        .text(String.valueOf(deltaScore)).end();

                IAnimation a1 = new FadeAnimation.Builder(FadeAnimation.Type.OUT, label).end();
                //IAnimation a2 = new FloatAnimation(0, -1, layerMan, label);                    
                IAnimation a2 = new MoveAnimation.Builder(label)
                        .duration(1150).v(0.03).theta(90).end();

                a2.setStartAction(new Runnable()
                {
                    public void run()
                    { layerMan.add(label, Layer.EFFECT); }
                });

                a2.setFinishAction(new Runnable()
                {
                    public void run()
                    { layerMan.remove(label, Layer.EFFECT); }
                });

                animationMan.add(a1);
                animationMan.add(a2);
                a1 = null;
                a2 = null;

                // Release references.
                p = null;                                                       
            }                  
            else
            {
                // Turn off the flag now that it has been used.
                tileRemovalNoScore = false;
            }

            // Play the sound.
            soundMan.play(AudioTrack.SOUND_LINE);

            // Make sure bombs aren't removed (they get removed
            // in a different step).  However, if the no-items
            // flag is set, then ignore bombs.
            if (tileRemovalNoItems == false)
            {
                //Set<Integer> allSet = new HashSet<Integer>();

                bombRemovalSet.clear();
                boardMan.scanFor(BombTileEntity.class, tileRemovalSet, 
                        bombRemovalSet);

                starRemovalSet.clear();
                boardMan.scanFor(StarTileEntity.class, tileRemovalSet, 
                        starRemovalSet);

                rocketRemovalSet.clear();
                boardMan.scanFor(RocketTileEntity.class, tileRemovalSet, 
                        rocketRemovalSet);                                       

                tileRemovalSet.removeAll(bombRemovalSet);
                tileRemovalSet.removeAll(starRemovalSet);
                tileRemovalSet.removeAll(rocketRemovalSet);                                            
            }
            else
            {
                // Turn off the flag now that it has been used.
                tileRemovalNoItems = false;
            }

            // Start the line removal animations if there are any
            // non-bomb tiles.
            if (tileRemovalSet.size() > 0)
            {
                int i = 0;
                for (Iterator it = tileRemovalSet.iterator(); it.hasNext(); )
                {
                    TileEntity t = boardMan.getTile((Integer) it.next());

                    if (tileRemovalUseJumpAnimation == true)
                    {
                        i++;
                        int angle = i % 2 == 0 ? 70 : 180 - 70;                             

                        // Bring this tile to the top.
                        layerMan.toFront(t, Layer.TILE);

                        IAnimation a1 = new MoveAnimation.Builder(t)
                                .duration(750).theta(angle).v(0.3)
                                .g(0.001).end();                                    
                        IAnimation a2 = new FadeAnimation.Builder(FadeAnimation.Type.OUT, t)
                                .wait(0).duration(750).end();
                        t.setAnimation(a1);
                        animationMan.add(a1);                            
                        animationMan.add(a2);      
                        a1 = null;
                        a2 = null;
                    }
                    else                        
                    {
                        t.setAnimation(new ZoomAnimation.Builder(ZoomAnimation.Type.IN, t)
                                .v(0.05).end());
                        animationMan.add(t.getAnimation());
                    }
                }

                // Clear the animation flag.
                tileRemovalUseJumpAnimation = false;

                // Set the flag.
                tileRemovalInProgress = true;
            }
            // Otherwise, start the bomb processing.
            else
            {
                //activateBombRemoval = true;
                activateStarRemoval = true;
            }
        }

        // If the star removal is in progress.
        if (activateRocketRemoval == true)
        {
            // Clear the flag.
            activateRocketRemoval = false;

            // Increment cascade.
            statMan.incrementChainCount();

            // Used below.
            int deltaScore = 0;

            // Also used below.
            IAnimation a1, a2;

            // Get the tiles the bombs would affect.
            boardMan.processRockets(rocketRemovalSet, tileRemovalSet);

            for (Integer index : lastMatchSet)
            {
                if (boardMan.getTile(index) == null)
                    continue;

                if (boardMan.getTile(index).getClass() 
                    != RocketTileEntity.class)
                {
                    tileRemovalSet.remove(index);
                }
            }       

            deltaScore = scoreMan.calculateLineScore(
                    tileRemovalSet, 
                    ScoreType.STAR, 
                    statMan.getChainCount());

              // Fire a score event.
              listenerMan.notifyScoreListener(new ScoreEvent(deltaScore, this));


            // Show the SCT.
            ImmutablePosition p = boardMan.determineCenterPoint(tileRemovalSet);

            final ILabel label = new LabelBuilder(p.getX(), p.getY())
                    .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                    .color(SCORE_BOMB_COLOR)
                    .size(scoreMan.determineFontSize(deltaScore))
                    .text(String.valueOf(deltaScore)).end();                        

            //a1 = new FadeAnimation(FadeType.OUT, label);
            a1 = new FadeAnimation.Builder(FadeAnimation.Type.OUT, label).end();
            //a2 = new FloatAnimation(0, -1, layerMan, label);    
            a2 = new MoveAnimation.Builder(label)
                        .duration(1150).v(0.03).theta(90).end();

            a2.setStartAction(new Runnable()
            {
                public void run()
                { layerMan.add(label, Layer.EFFECT); }
            });

            a2.setFinishAction(new Runnable()
            {
                public void run()
                { layerMan.remove(label, Layer.EFFECT); }
            });

            animationMan.add(a1);
            animationMan.add(a2);
            a1 = null;
            a2 = null;

            // Release references.
            p = null;                             

            // Play the sound.
            soundMan.play(AudioTrack.SOUND_ROCKET);

            // Find all the new rockets.
            Set<Integer> newRocketRemovalSet = new HashSet<Integer>();
            boardMan.scanFor(RocketTileEntity.class, tileRemovalSet,
                    newRocketRemovalSet);                                                
            newRocketRemovalSet.removeAll(rocketRemovalSet);

            // Remove all new rockets from the tile removal set.
            // They will be processed separately.
            tileRemovalSet.removeAll(newRocketRemovalSet);

            // Find all the bombs.
            boardMan.scanFor(BombTileEntity.class, tileRemovalSet,
                    bombRemovalSet);

            // Remove all bombs from the tile removal set.
            // They will be processed separately.
            tileRemovalSet.removeAll(bombRemovalSet);

            // Start the line removal animations.
            int i = 0;
            for (Iterator it = tileRemovalSet.iterator(); it.hasNext(); )
            {
                TileEntity t = boardMan.getTile((Integer) it.next());

                // Bring the tile to the front.
                layerMan.toFront(t, Layer.TILE);

                if (t.getClass() == RocketTileEntity.class)
                {
                    // Cast it.
                    RocketTileEntity r = (RocketTileEntity) t;                                                                                           

                    //a1 = new JumpAnimation(0.3, r.getDirection() + 90, 0, 750, r);
                    //a2 = new FadeAnimation(FadeType.OUT, 0, 750, t); 
                    a1 = new MoveAnimation.Builder(r).duration(750)
                            .theta(r.getDirection().toDegrees())
                            .v(0.3).g(0).end();
                    a2 = new FadeAnimation.Builder(FadeAnimation.Type.OUT, t)
                            .wait(0).duration(750).end();
                    t.setAnimation(a1);
                    animationMan.add(a1);                            
                    animationMan.add(a2);      
                    a1 = null;
                    a2 = null;
                }
                else
                {
                    i++;
                    int angle = i % 2 == 0 ? 70 : 180 - 70;                        
                    //int angle = 360 - 180 + 70;
                    a1 = new MoveAnimation.Builder(t).duration(750)
                            .theta(angle).v(0.3).g(0.001).end();     
                    //a2 = new FadeAnimation(FadeType.OUT, 0, 750, t);                                        
                    a2 = new FadeAnimation.Builder(FadeAnimation.Type.OUT, t)
                            .wait(0).duration(750).end();
                    t.setAnimation(a1);
                    animationMan.add(a1);                            
                    animationMan.add(a2);      
                    a1 = null;
                    a2 = null;
                }                    
            }

            // If other bombs were hit, they will be dealt with in another
            // bomb removal cycle.
            rocketRemovalSet = newRocketRemovalSet;

            // Set the flag.
            tileRemovalInProgress = true;
        }

        // If the star removal is in progress.
        if (activateStarRemoval == true)
        {
            // Clear the flag.
            activateStarRemoval = false;

            // Increment cascade.
            statMan.incrementChainCount();

            // Used below.
            int deltaScore = 0;

            // Also used below.
            IAnimation a1, a2;

            // Get the tiles the bombs would affect.
            boardMan.processStars(starRemovalSet, tileRemovalSet);

            for (Integer index : lastMatchSet)
            {
                if (boardMan.getTile(index) == null)
                    continue;

                if (boardMan.getTile(index).getClass() 
                    != StarTileEntity.class)
                {
                    tileRemovalSet.remove(index);
                }
            }       

            deltaScore = scoreMan.calculateLineScore(
                    tileRemovalSet, 
                    ScoreType.STAR, 
                    statMan.getChainCount());

              // Fire a score event.
              listenerMan.notifyScoreListener(new ScoreEvent(deltaScore, this));


            // Show the SCT.
            ImmutablePosition p = boardMan.determineCenterPoint(tileRemovalSet);

            final ILabel label = new LabelBuilder(p.getX(), p.getY())
                    .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                    .color(SCORE_BOMB_COLOR)
                    .size(scoreMan.determineFontSize(deltaScore))
                    .text(String.valueOf(deltaScore)).end();                        

            a1 = new FadeAnimation.Builder(FadeAnimation.Type.OUT, label).end();
            //a2 = new FloatAnimation(0, -1, layerMan, label);                    
            a2 = new MoveAnimation.Builder(label)
                    .duration(1150).v(0.03).theta(90).end();

            a2.setStartAction(new Runnable()
            {
                public void run()
                { layerMan.add(label, Layer.EFFECT); }
            });

            a2.setFinishAction(new Runnable()
            {
                public void run()
                { layerMan.remove(label, Layer.EFFECT); }
            });

            animationMan.add(a1);
            animationMan.add(a2);
            a1 = null;
            a2 = null;

            // Release references.
            p = null;

            // Play the sound.
            soundMan.play(AudioTrack.SOUND_STAR);

            // Start the line removal animations.
            int i = 0;
            for (Iterator it = tileRemovalSet.iterator(); it.hasNext(); )
            {
                TileEntity t = boardMan.getTile((Integer) it.next());

                // Bring the entity to the front.
                layerMan.toFront(t, Layer.TILE);

                // Increment counter.
                i++;

                int angle = i % 2 == 0 ? 70 : 180 - 70;                                                                
                //a1 = new JumpAnimation(0.3, angle, 0.001, 750, t);
                //a2 = new FadeAnimation(FadeType.OUT, 0, 750, t);                                        
                a1 = new MoveAnimation.Builder(t).duration(750)
                        .theta(angle).v(0.3).g(0.001).end();                            
                a2 = new FadeAnimation.Builder(FadeAnimation.Type.OUT, t)
                        .wait(0).duration(750).end();
                t.setAnimation(a1);
                animationMan.add(a1);                            
                animationMan.add(a2);      
                a1 = null;
                a2 = null;
            }

            // Clear the star removal set.
            starRemovalSet.clear();

            // Set the flag.
            tileRemovalInProgress = true;
        }

        // If a bomb removal is in progress.
        if (activateBombRemoval == true)
        {
            // Clear the flag.
            activateBombRemoval = false;

            // Increment cascade.
            statMan.incrementChainCount();

            // Used below.
            int deltaScore = 0;

            // Also used below.
            IAnimation a1, a2;

            // Get the tiles the bombs would affect.
            boardMan.processBombs(bombRemovalSet, tileRemovalSet);
            deltaScore = scoreMan.calculateLineScore(
                    tileRemovalSet, 
                    ScoreType.BOMB, 
                    statMan.getChainCount());

              // Fire a score event.
              listenerMan.notifyScoreListener(new ScoreEvent(deltaScore, this));


            // Show the SCT.
            ImmutablePosition p = boardMan.determineCenterPoint(tileRemovalSet);

            final ILabel label = new LabelBuilder(p.getX(), p.getY())
                    .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                    .color(SCORE_BOMB_COLOR)
                    .size(scoreMan.determineFontSize(deltaScore))
                    .text(String.valueOf(deltaScore)).end();

            a1 = new FadeAnimation.Builder(FadeAnimation.Type.OUT, label).end();
            a2 = new MoveAnimation.Builder(label)
                    .duration(1150).v(0.03).theta(90).end();

            a2.setStartAction(new Runnable()
            {
                public void run()
                { layerMan.add(label, Layer.EFFECT); }
            });

            a2.setFinishAction(new Runnable()
            {
                public void run()
                { layerMan.remove(label, Layer.EFFECT); }
            });

            animationMan.add(a1);
            animationMan.add(a2);
            a1 = null;
            a2 = null;

            // Release references.
            p = null;                

            // Play the sound.
            soundMan.play(AudioTrack.SOUND_BOMB);

            // Find all the new bombs.
            Set<Integer> newBombRemovalSet = new HashSet<Integer>();
            boardMan.scanFor(BombTileEntity.class, tileRemovalSet,
                    newBombRemovalSet);                                                
            newBombRemovalSet.removeAll(bombRemovalSet);

            // Remove all new bombs from the tile removal set.
            // They will be processed separately.
            tileRemovalSet.removeAll(newBombRemovalSet);

            // Find all rockets.                
            boardMan.scanFor(RocketTileEntity.class, tileRemovalSet,
                    rocketRemovalSet);                

            // Remove all rockets from the tile removal set.
            // They will be processed separately.
            tileRemovalSet.removeAll(rocketRemovalSet);

            // Start the line removal animations.
            for (Iterator it = tileRemovalSet.iterator(); it.hasNext(); )
            {
                TileEntity t = boardMan.getTile((Integer) it.next());

                if (t instanceof BombTileEntity)                    
                {
                    t.setAnimation(new ExplosionAnimation(t, layerMan));                                            
                    animationMan.add(t.getAnimation());
                }                    
                else
                {          
                    a1 = new JiggleAnimation(600, 50, t);
                    //a2 = new FadeAnimation(FadeType.OUT, 0, 600, t);                                        
                    a2 = new FadeAnimation.Builder(FadeAnimation.Type.OUT, t)
                            .wait(0).duration(600).end();

                    t.setAnimation(a1);
                    animationMan.add(a1);                            
                    animationMan.add(a2);      
                    a1 = null;
                    a2 = null;                                               
                }
            }

            // If other bombs were hit, they will be dealt with in another
            // bomb removal cycle.
            bombRemovalSet = newBombRemovalSet;

            // Set the flag.
            tileRemovalInProgress = true;
        }

        // If a line removal is in progress.
        if (tileRemovalInProgress == true)
        {
            // Animation completed flag.
            boolean animationInProgress = false;

            // Check to see if they're all done.
            for (Iterator it = tileRemovalSet.iterator(); it.hasNext(); )
            {
                if (boardMan.getTile((Integer) it.next()).getAnimation()
                        .isFinished() == false)
                {
                    animationInProgress = true;
                }
            }

            if (animationInProgress == false)
            {
                // Remove the tiles from the board.
                boardMan.removeTiles(tileRemovalSet);

                // Bomb removal is completed.
                tileRemovalInProgress = false;

                // See if there are any bombs in the bomb set.
                // If there are, activate the bomb removal.
                if (rocketRemovalSet.size() > 0)
                    activateRocketRemoval = true;
                else if (starRemovalSet.size() > 0)
                    activateStarRemoval = true;
                else if (bombRemovalSet.size() > 0)
                    activateBombRemoval = true;
                // Otherwise, start a new refactor.
                else                
                    startRefactor(RefactorType.NORMAL);
            }  
        }

        // See if we should clear the cascade count.
        if (isRefactoring() == false 
                && isTileRemoving() == false
                && pieceMan.isTileDropInProgress() == false)
            statMan.resetChainCount(); 

        // Animation all animations.
        animationMan.animate(delta);

        // Handle the timer.
        if (boardMan.isVisible() == true)
            timerMan.incrementInternalTime(delta);

        // Check to see if we should force a piece commit.
        if (timerMan.getTime() < 0)
        {
            // Commit the piece.
            timerMan.setTime(0);
            pieceMan.initiateCommit(this);            
        }

        // Update piece manager logic and then draw it.
        pieceMan.updateLogic(this);

         // Update the tutorial manager logic.
        tutorialMan.updateLogic(this);

        // Update the world manager logic.
        worldMan.updateLogic(this);                       

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
            LogManager.recordMessage("New high score label created.");
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

        // Update the progress bar.
        progressBar.setProgress(scoreMan.getLevelScore());

        // Reset the line count.
        //statMan.incrementLineCount(statMan.getCycleLineCount());
        listenerMan.notifyLineListener(new LineEvent(statMan.getCycleLineCount(), this));
        statMan.resetCycleLineCount();
    }
    
    //--------------------------------------------------------------------------
    // Getters and Setters
    //--------------------------------------------------------------------------       

    /**
     * Get a reference to the game window.
     * 
     * @return A reference to the current game window.
     */
    public IGameWindow getGameWindow()
    {
        return window;
    }
    
    //--------------------------------------------------------------------------
    // Window Methods
    //--------------------------------------------------------------------------
    
	/**
	 * Notification that the game window has been closed
	 */
	public void windowClosed()
	{                    
        try
        {
            // Save the properites.
            if (propertyMan != null)        
                propertyMan.saveProperties();

            // Save the log data.            
            LogManager.write();
        }
        catch(Exception e)
        {
            LogManager.recordException(e);
        }        
        
     	System.exit(0);
	}        
    
    /**
     * Notification that the game window has been deactivated in some way.
     */
    public void windowDeactivated()
    {
        // Don't pause game if we're showing the game over screen.
        if (groupMan != null && groupMan.isActivated() == false)
        {
            updatePauseGroup();
            groupMan.showGroup(pauseButton, pauseGroup, 
                    GroupManager.CLASS_PAUSE,
                    GroupManager.LAYER_MIDDLE);
        }
                        
        if (layerMan != null)
            layerMan.forceRedraw();                
    }
    
     /**
     * Notification that the game window has been reactivated in some way.
     */
    public void windowActivated()
    {                
        // Force redraw.
        if (loader != null)
            loader.forceRedraw();
        
        if (layerMan != null)
            layerMan.forceRedraw();        
    }        
    
    //--------------------------------------------------------------------------
    // Main method
    //--------------------------------------------------------------------------
    
	/**
	 * The entry point into the game. We'll simply create an instance of class
	 * which will start the display and game loop.
	 * 
	 * @param argv
	 *            The arguments that are passed into our game
	 */
	public static void main(String argv[])
	{		        
//        if (System.getProperty("os.name").toLowerCase().contains("windows"))
//        {
//            Util.handleMessage("Windows detected, enabling translaccel.");
//            System.setProperty("sun.java2d.translaccel", "true");
//        }
        
        // Enable OpenGL.
        // Can cause the JVM to crash.
        //System.setProperty("sun.java2d.opengl", "True");
        
        try
        {
            Game g = new Game(ResourceFactory.RenderType.JAVA2D);
            g.startRendering();		
        }
        catch (Exception e)
        {
            LogManager.recordException(e);
        }
	}    
  
}