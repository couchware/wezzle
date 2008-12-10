/*
 *  Wezzle
 *  Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d;

import ca.couchware.wezzle2d.ResourceFactory.LabelBuilder;
import ca.couchware.wezzle2d.animation.*;
import ca.couchware.wezzle2d.audio.*;
import ca.couchware.wezzle2d.event.GameEvent;
import ca.couchware.wezzle2d.graphics.IPositionable.Alignment;
import ca.couchware.wezzle2d.graphics.*;
import ca.couchware.wezzle2d.manager.Achievement;
import ca.couchware.wezzle2d.manager.AchievementManager;
import ca.couchware.wezzle2d.manager.AnimationManager;
import ca.couchware.wezzle2d.manager.BoardManager;
import ca.couchware.wezzle2d.manager.SettingsManager;
import ca.couchware.wezzle2d.manager.BoardManager.AnimationType;
import ca.couchware.wezzle2d.manager.GroupManager;
import ca.couchware.wezzle2d.manager.HighScoreManager;
import ca.couchware.wezzle2d.manager.ItemManager;
import ca.couchware.wezzle2d.manager.LayerManager;
import ca.couchware.wezzle2d.manager.LayerManager.Layer;
import ca.couchware.wezzle2d.manager.ListenerManager;
import ca.couchware.wezzle2d.manager.ListenerManager.Listener;
import ca.couchware.wezzle2d.manager.LogManager;
import ca.couchware.wezzle2d.manager.MusicManager;
import ca.couchware.wezzle2d.manager.PieceManager;
import ca.couchware.wezzle2d.manager.ScoreManager;
import ca.couchware.wezzle2d.manager.Settings;
import ca.couchware.wezzle2d.manager.Settings.Key;
import ca.couchware.wezzle2d.manager.SoundManager;
import ca.couchware.wezzle2d.manager.StatManager;
import ca.couchware.wezzle2d.manager.TimerManager;
import ca.couchware.wezzle2d.manager.TutorialManager;
import ca.couchware.wezzle2d.manager.LevelManager;
import ca.couchware.wezzle2d.menu.Loader;
import ca.couchware.wezzle2d.menu.MainMenuGroup;
import ca.couchware.wezzle2d.tile.TileType;
import ca.couchware.wezzle2d.transition.CircularTransition;
import ca.couchware.wezzle2d.transition.ITransition;
import ca.couchware.wezzle2d.tutorial.BasicTutorial;
import ca.couchware.wezzle2d.tutorial.BombTutorial;
import ca.couchware.wezzle2d.tutorial.GravityTutorial;
import ca.couchware.wezzle2d.tutorial.RocketTutorial;
import ca.couchware.wezzle2d.tutorial.StarTutorial;
import ca.couchware.wezzle2d.ui.ILabel;
import ca.couchware.wezzle2d.ui.ProgressBar;
import ca.couchware.wezzle2d.ui.RadioItem;
import ca.couchware.wezzle2d.ui.SpeechBubble;
import ca.couchware.wezzle2d.ui.Button;
import ca.couchware.wezzle2d.util.ImmutableRectangle;
import java.awt.Canvas;
import java.util.Date;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;
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
 * @author Cameron McKay
 * @author Kevin Grad 
 * @author Kevin Glass
 */
public class Game extends Canvas implements IGameWindowCallback
{	  
    //--------------------------------------------------------------------------
    // Static Members
    //--------------------------------------------------------------------------                            
    
    /** An enumeration of the manager types. */
    public static enum ManagerType
    {
        ACHIEVEMENT, 
        ANIMATION,
        BOARD, 
        GROUP, 
        HIGHSCORE,
        ITEM,
        LAYER, 
        LISTENER,
        MUSIC, 
        PIECE,         
        SCORE, 
        SOUND,
        STAT, 
        TIMER, 
        TUTORIAL, 
        LEVEL                      
                          
    }       
          
    /** The width of the screen. */
    final public static int SCREEN_WIDTH = 800;
    
    /** The height of the screen  */
    final public static int SCREEN_HEIGHT = 600;
    
    /** A rectangle the size of the screen. */
    final public static ImmutableRectangle SCREEN_RECTANGLE = 
            new ImmutableRectangle(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);   
    
    /** The name of the application. */
    final public static String APPLICATION_NAME = "Wezzle";
    
    /** The version of the application. */
    final public static String APPLICATION_VERSION = "Test 7";     
    
    /** The full title of the game. */
    final public static String TITLE = APPLICATION_NAME + " " + APPLICATION_VERSION;
    
    /** The copyright. */
    final public static String COPYRIGHT = "\u00A9 2008 Couchware Inc.";
    
    //--------------------------------------------------------------------------
    // Public Members
    //--------------------------------------------------------------------------                  
    
    /** The loader. */
    public Loader loader;
    
    /** The main menu. */
    public MainMenuGroup mainMenu;
    
    /** The game UI. */
    public GameUI ui = GameUI.get();
    
    /**
     * The main menu transition.  This is the transition animation that is used
     * to transition from the menu to the game.
     */
    public ITransition menuTransition;             
    
    /** The refactorer. */
    public Refactorer refactorer = Refactorer.get();
    
    /** The tile remover. */
    public TileRemover tileRemover = TileRemover.get();    
    
    /** The animation manager in charge of animations. */
    public AnimationManager animationMan;
    
    /** The manager in charge of maintaining the board. */
    public BoardManager boardMan;
    
    /** The menu manager. */
    public GroupManager groupMan;
       
    /** The high score manager. */    
    public HighScoreManager highScoreMan;
    
    /** The item manager. */
    public ItemManager itemMan;
    	
    /** The game layer manager. */
    public LayerManager layerMan;
        
    /** The manager in charge of the moves. */
    public StatManager statMan;	    
    
    /** The manager in charge of music. */
    public MusicManager musicMan;  

    /**
     * The manager in charge of moving the piece around with the
     * pointer and drawing the piece to the board.
     */
    public PieceManager pieceMan;	   
    
    /** The settings manager. */
    public SettingsManager settingsMan = SettingsManager.get();
    
    /** The manager in charge of score. */
    public ScoreManager scoreMan;        
    
    /** The manager in charge of sound. */
    public SoundManager soundMan;             
    
    /** The manager in charge of keeping track of the time. */
    public TimerManager timerMan = TimerManager.get();
    
    /** The manager in charge of running tutorials. */
    public TutorialManager tutorialMan;   
    
    /** The Manager in charge of the world. */
    public LevelManager levelMan;    	
    
    /** The Manager in charge of Listeners. */
    public ListenerManager listenerMan = ListenerManager.get();
    
    /** The manager in charge of achievements */
    public AchievementManager achievementMan;	
    
    //--------------------------------------------------------------------------
    // Private Members
    //--------------------------------------------------------------------------                                 
    
    /** The current build number. */
    final private String BUILD_NUMBER = "N/A";        
    
    /** The current drawer. */
    private IDrawer drawer;
    
    /** The normal title of the window. */
    private String windowTitle = APPLICATION_NAME;	              
    
    /** The executor used by certain managers. */
    private Executor executor;                   

    /** If true */
    private boolean activateBoardShowAnimation = false;
    
    /** If true */
    private boolean activateBoardHideAnimation = false;
   
    /** The board animation type to use. */
    private AnimationType boardAnimationType;
    
    /**
     * The animation that will indicate whether the board animation is 
     * complete.
     */
    private IAnimation boardAnimation = null;
    
    /** If true, the game will end next loop. */
    private boolean activateGameOver = false;   
    
    /** If true, a game over has been activated. */
    private boolean gameOverInProgress = false;
	
	/** The window that is being used to render the game. */
    private IGameWindow window;                  
                
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
	public Game(ResourceFactory.Renderer renderer) 
	{
       
        // Print the build number.
        LogManager.recordMessage("Date: " + (new Date()));   
        LogManager.recordMessage("Wezzle Build: " + BUILD_NUMBER);
        LogManager.recordMessage("Wezzle Version: " + APPLICATION_VERSION);
        LogManager.recordMessage("Java Version: " + System.getProperty("java.version"));
        LogManager.recordMessage("OS Name: " + System.getProperty("os.name"));
        LogManager.recordMessage("OS Architecture: " + System.getProperty("os.arch"));
        LogManager.recordMessage("OS Version: " + System.getProperty("os.version"));
        
		// Create a window based on a chosen rendering method.
		ResourceFactory.get().setRenderer(renderer);		        
        		                      
        window = ResourceFactory.get().getGameWindow();
        window.setResolution(SCREEN_WIDTH, SCREEN_HEIGHT);
        window.setGameWindowCallback(Game.this);
        window.setTitle(windowTitle);
	}

	public void start()
	{
		window.start();
	}
            
    public void startBoard()
    {
        boardMan.generateBoard(itemMan.getItemList(), levelMan.getLevel());          
        startBoardShowAnimation(AnimationType.ROW_FADE);
    }
        
    /**
     * Initializes all the managers (except for the layer manager).
     */    
    private void initializeManagers(EnumSet<ManagerType> managerSet)
    {                
        if (managerSet.contains(ManagerType.SOUND))
        {
            // Create the sound manager.
            soundMan = SoundManager.newInstance(executor);
        }
        
        if (managerSet.contains(ManagerType.MUSIC))
        {
            // Create the music manager.            
            musicMan = MusicManager.newInstance(executor);  
        }   
        
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
        
        if (managerSet.contains(ManagerType.HIGHSCORE))
        {
            // Create the high score manager.
            highScoreMan = HighScoreManager.newInstance();  
        }      
        
        if (managerSet.contains(ManagerType.ITEM))
        {
            // Create the manager.
            itemMan = ItemManager.newInstance();  
            
            listenerMan.registerListener(Listener.LEVEL, itemMan);
        } 
        
        if (managerSet.contains(ManagerType.STAT))
        {
            // Create the move manager.
            statMan = StatManager.newInstance();
            listenerMan.registerListener(Listener.MOVE, statMan);
            listenerMan.registerListener(Listener.LINE, statMan);
        }
        
        if (managerSet.contains(ManagerType.TUTORIAL))
        {
            // Create the tutorial manager.
            tutorialMan = TutorialManager.newInstance();

            // Add the tutorials to it.
            tutorialMan.add(new BasicTutorial(refactorer));
            tutorialMan.add(new GravityTutorial(refactorer));
            tutorialMan.add(new RocketTutorial(refactorer));
            tutorialMan.add(new BombTutorial(refactorer));
            tutorialMan.add(new StarTutorial(refactorer));
        }                           
        
        if (managerSet.contains(ManagerType.BOARD))
        {
            // Create the board manager.
            boardMan = BoardManager.newInstance(
                    animationMan, layerMan, itemMan,
                    272, 139, 8, 10);    
            boardMan.setVisible(false);
            
            // Listen for key presses.
            window.addKeyListener(boardMan);
        }
        
        if (managerSet.contains(ManagerType.PIECE))
        {
            // Create the piece manager.
            pieceMan = PieceManager.newInstance(refactorer, animationMan, boardMan);        
            pieceMan.getPieceGrid().setVisible(false);
            layerMan.add(pieceMan.getPieceGrid(), Layer.EFFECT);  
            
            // Listen for the mouse.
            window.addMouseListener(pieceMan);
        }
        
        if (managerSet.contains(ManagerType.GROUP))
        {
            // Create group manager.
            groupMan = GroupManager.newInstance(layerMan, pieceMan);
        }
	        
        if (managerSet.contains(ManagerType.SCORE))
        {
            // Create the score manager.
            scoreMan = ScoreManager.newInstance(
                    boardMan, 
                    highScoreMan, 
                    listenerMan);
            
            //scoreMan.setTargetLevelScore(levelMan.generateTargetLevelScore());
            listenerMan.registerListener(Listener.GAME,  scoreMan);
            listenerMan.registerListener(Listener.LEVEL, scoreMan);
            //listenerMan.registerListener(Listener.SCORE, scoreMan);            
        }
        
        if (managerSet.contains(ManagerType.LEVEL))
        {
            // Create the world manager.
            levelMan = LevelManager.newInstance(listenerMan, scoreMan);                  
        }                           
        
        if (managerSet.contains(ManagerType.TIMER))
        {
            // Create the time manager.
            if (levelMan == null)
                throw new IllegalStateException("World Manager has not been initalized yet!");
                           
            timerMan.resetTimer(); 
            listenerMan.registerListener(Listener.LEVEL, timerMan);
        }
        
        if (managerSet.contains(ManagerType.ACHIEVEMENT))
        {
            // Create the achievement manager.
            achievementMan = AchievementManager.newInstance();
            listenerMan.registerListener(Listener.COLLISION, achievementMan);
        
            // Load the test achievements.        
            List<Rule> rules1 = new LinkedList<Rule>();
            List<Rule> rules2 = new LinkedList<Rule>();
            List<Rule> rules3 = new LinkedList<Rule>();
            List<Rule> rules4 = new LinkedList<Rule>();
            List<Rule> rules5 = new LinkedList<Rule>();

            rules1.add(new Rule(Rule.Type.SCORE, Rule.Operation.GT, 2000));

            achievementMan.add(new Achievement(rules1, 
                     "Scorelord I",
                     "Score greater than 2000", 
                     Achievement.Difficulty.BRONZE));        

            rules2.add(new Rule(Rule.Type.SCORE, 
                    Rule.Operation.GT, 5000));

            achievementMan.add(new Achievement(rules2, 
                     "Scorelord II",
                     "Score greater than 5000", 
                     Achievement.Difficulty.BRONZE));

            rules3.add(new Rule(Rule.Type.SCORE, Rule.Operation.GT, 1000));        
            rules3.add(new Rule(Rule.Type.MOVES, Rule.Operation.LTEQ, 3));

            achievementMan.add(new Achievement(rules3, 
                     "Dextrous Scorelord",
                     "Score greater than 1000, moves less than or equal to 3", 
                     Achievement.Difficulty.BRONZE));

            rules4.add(new Rule(Rule.Type.LEVEL, Rule.Operation.GT, 2));

            achievementMan.add(new Achievement(rules4, 
                     "Levelator",
                     "Level greater than 2", 
                     Achievement.Difficulty.BRONZE));
            
            rules5.add(new Rule(
                    Rule.Type.COLLISION, 
                    Rule.Operation.BETWEEN,
                    TileType.ROCKET,
                    TileType.ROCKET));
            
            achievementMan.add(new Achievement(rules5,
                    "Rocketeer II",
                    "Hit a rocket with a rocket",
                    Achievement.Difficulty.SILVER));
        }              
    }        
    
    /**
     * Initialize various members.
     */
    private void initializeMembers()
    {
        // Make the tile remover listen for level events.
        listenerMan.registerListener(Listener.LEVEL, tileRemover);
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
        
        // Create the loader.        
        loader = new Loader(settingsMan);        
        setDrawer(loader);
                
        // Initialize managers.
        loader.addRunnable(new Runnable()
        {
           public void run() 
           { 
               initializeManagers(EnumSet.allOf(ManagerType.class)); 
               layerMan.setDisabled(true);              
           }
        });
                               
        // Initialize UI.       
        ui.initialize(loader, this);                                            
	}                   
    
    public void update()
    {                
        // If the loader is running, bypass all the rendering to show it.        
        if (this.getDrawer() == loader)
        {   
            // Animate all animations.
            if (animationMan != null) animationMan.animate();            
            
            // Update the logic.
            loader.updateLogic(this);
            
            if (loader.getState() == Loader.State.FINISHED)
            {
                // Remove the loader.
                loader = null;
                
                // Empty the mouse events.
                window.clearMouseEvents();
                               
                // Create the main menu.
                mainMenu = new MainMenuGroup(
                        settingsMan, 
                        animationMan, 
                        musicMan);
                
                setDrawer(mainMenu);
            }   
            else return;                        
        }
        
        // If the main menu is running, bypass all rendering to show it.
        if (this.getDrawer() == mainMenu)
        {
            // Animate all animations.
            if (animationMan != null) animationMan.animate();
            
            // Update the main menu logic.
            mainMenu.updateLogic(this);
            
            if (mainMenu.getState() == MainMenuGroup.State.FINISHED)
            {                                
                // Empty the mouse events.
                window.clearMouseEvents();   
                
                // Remove the loader.
                mainMenu = null;   
                
                // Create the layer manager transition animation.
                this.menuTransition = new CircularTransition.Builder(layerMan)
                        .minRadius(10).speed(400).end();
                setDrawer(menuTransition);
                
                // When the transition is done, enable the game controls.
                this.menuTransition.setFinishRunnable(new Runnable()
                {
                    public void run()
                    { layerMan.setDisabled(false); }
                });
                
                // Queue in the animation manager.
                this.animationMan.add(menuTransition);                
                
                // Start the music.
                musicMan.play();                
                
                // See if the music is off.
                if (SettingsManager.get().getBoolean(Key.USER_MUSIC) == false)
                    musicMan.setPaused(true);
            }   
            else
            {      
                // Fire the mouse events.
                window.fireMouseEvents();
                
                // Draw using the loader.
                return;
            }
        } // end if
        
        // See if the main menu transition is in progress
        if (this.getDrawer() == menuTransition)
        {
            // Animate all animations.
            if (animationMan != null) animationMan.animate();
            
            // Otherwise see if the transition is over.
            if (menuTransition.isFinished() == false) return;           
            else 
            {
                setDrawer(this.layerMan);
                menuTransition = null;
            }            
        } // end if
                     
        // Update UI.
        ui.updateLogic(this);
        
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
            if (gameOverInProgress == true)
            {
                // Show the game over screen.
                ui.showGameOverGroup(groupMan);      
                
                // Clear the flag.
                gameOverInProgress = false;
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
            updateBoard();
                        
        // Fire all the queued mouse events.
        window.fireMouseEvents();                
        window.updateKeyPresses();
                        
        // The keys.      
        if (window.isKeyPressed('R'))
        {
            SettingsManager.get().loadUserSettings();
            LogManager.recordMessage("Reloaded user settings from " + Settings.getUserSettingsFilePath());
        }
        
        // Check the achievements.
        achievementMan.evaluate(this);
        if (achievementMan.isAchievementCompleted() == true)
        {
            achievementMan.reportCompleted();
        }
    }
    
	/**
	 * Notification that a frame is being rendered. Responsible for running game
	 * logic and rendering the scene.
     * 
     * @return True if the frame has been updated, false if nothing has been
     * updated.
	 */
	public boolean draw()
	{		      
        // If the background is dirty, then redraw everything.
        if (getDrawer() != null) return getDrawer().draw();
        else return false;
	}  
    
    /**
     * Handles the logic and rendering of the game scene.
     * 
     * @param delta The amount of time that has passed since the last
     * board update.
     */
    private void updateBoard()
    {
        // See if it's time to level-up.
        if (pieceMan.isTileDropInProgress() == false
                && isBusy() == false)
        {
            // Handle Level up.
            if (scoreMan.getLevelScore() >= scoreMan.getTargetLevelScore())
            {    
                // Hide piece.                    
                pieceMan.getPieceGrid().setVisible(false);
                pieceMan.stopAnimation();
                timerMan.setPaused(true);

                LogManager.recordMessage("Level up!", "Game#frameRendering");
                //levelMan.levelUp(this);
               
                levelMan.incrementLevel();                                                           

                soundMan.play(Sound.LEVEL_UP);

                int x = pieceMan.getPieceGrid().getX() 
                        + boardMan.getCellWidth() / 2;

                int y = pieceMan.getPieceGrid().getY() 
                        + boardMan.getCellHeight() / 2;
                                                
                final ILabel label = new LabelBuilder(x, y)
                        .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.LEFT))
                        .color(settingsMan.getColor(Key.GAME_COLOR_PRIMARY))
                        .size(settingsMan.getInt(Key.SCT_LEVELUP_TEXT_SIZE))
                        .text(settingsMan.getString(Key.SCT_LEVELUP_TEXT)).end();

                IAnimation a1 = new FadeAnimation.Builder(FadeAnimation.Type.OUT, label)
                        .wait(settingsMan.getInt(Key.SCT_SCORE_FADE_WAIT))
                        .duration(settingsMan.getInt(Key.SCT_SCORE_FADE_DURATION))
                        .minOpacity(settingsMan.getInt(Key.SCT_SCORE_FADE_MIN_OPACITY))
                        .maxOpacity(settingsMan.getInt(Key.SCT_SCORE_FADE_MAX_OPACITY))
                        .end();                 
                
                IAnimation a2 = new MoveAnimation.Builder(label)
                        .duration(settingsMan.getInt(Key.SCT_LEVELUP_MOVE_DURATION))
                        .speed(settingsMan.getInt(Key.SCT_LEVELUP_MOVE_SPEED))
                        .theta(settingsMan.getInt(Key.SCT_LEVELUP_MOVE_THETA))                       
                        .end(); 

                a2.setStartRunnable(new Runnable()
                {
                    public void run()
                    { layerMan.add(label, Layer.EFFECT); }
                });

                a2.setFinishRunnable(new Runnable()
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
            activateGameOver = false;                            

            // Set in progress flag.
            gameOverInProgress = true;

            // Hide the board.
            startBoardHideAnimation(AnimationType.ROW_FADE);                
        }                                                                  

        // Run the refactorer.
        refactorer.updateLogic(this);
      
        // Update the tile remover.
        tileRemover.updateLogic(this);
      
        // See if we should clear the cascade count.
        if (refactorer.isRefactoring() == false 
                && tileRemover.isTileRemoving() == false
                && pieceMan.isTileDropInProgress() == false)
            statMan.resetChainCount(); 

        // Animation all animations.
        animationMan.animate();

        // Handle the timer.
        if (boardMan.isVisible() == true)
        {
            timerMan.updateLogic(this);
        }

        // Check to see if we should force a piece commit.
        if (timerMan.getTime() < 0)
        {
            // Commit the piece.
            timerMan.setTime(0);
            pieceMan.initiateCommit(this);            
        }

        // Update piece manager logic and then draw it.
        pieceMan.updateLogic(this);

         // Update the item manager logic.
        itemMan.updateLogic(this);        
        
         // Update the tutorial manager logic. This must be done after the world
        // manager because it relies on the proper items being in the item list.
        tutorialMan.updateLogic(this);
    
        // Reset the line count.
        //statMan.incrementLineCount(statMan.getCycleLineCount());       
        
        statMan.resetCycleLineCount();
    }       
      
    /**
     * A method to check whether the board is busy.
     * 
     * @return True if it is, false otherwise.
     */
    public boolean isBusy()
    {
       return (refactorer.isRefactoring()               
               || tileRemover.isTileRemoving()
               || gameOverInProgress == true
               || activateBoardShowAnimation == true
               || activateBoardHideAnimation == true
               || this.boardAnimation != null);
    }       
    
    /**
     * Checks whether tiles are, or are about to be, removed.
     */

    
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
        highScoreMan.offerScore("TEST", scoreMan.getTotalScore(), 
                levelMan.getLevel());
        listenerMan.notifyGameOver(new GameEvent(this, levelMan.getLevel()));
        //highScoreGroup.updateLabels();
        
        // Activate the game over process.
        activateGameOver = true;
    }       
    
    //--------------------------------------------------------------------------
    // Getters and Setters
    //--------------------------------------------------------------------------       

    public IDrawer getDrawer()
    {
        return drawer;
    }

    public void setDrawer(IDrawer drawer)
    {
        this.drawer = drawer;
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
            if (SettingsManager.get() != null)        
                SettingsManager.get().saveSettings();

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
            ui.showPauseGroup(groupMan);
        }
                        
        if (layerMan != null) layerMan.forceRedraw();                
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
        
        // Make sure the setting manager is loaded.       
        SettingsManager settingsMan = SettingsManager.get();      
        
        // Set the default color scheme.
        ResourceFactory.setDefaultLabelColor(settingsMan.getColor(Key.GAME_COLOR_PRIMARY));
        ProgressBar.setDefaultColor(settingsMan.getColor(Key.GAME_COLOR_PRIMARY));
        RadioItem.setDefaultColor(settingsMan.getColor(Key.GAME_COLOR_PRIMARY));
        SpeechBubble.setDefaultColor(settingsMan.getColor(Key.GAME_COLOR_PRIMARY));
        Button.setDefaultColor(settingsMan.getColor(Key.GAME_COLOR_PRIMARY));           
        
        try
        {
            //Game game = new Game(ResourceFactory.Renderer.JAVA2D);
            Game game = new Game(ResourceFactory.Renderer.LWJGL);
            game.start();		
        }
        catch (Exception e)
        {
            LogManager.recordException(e);
            LogManager.write();
        }
	}
  
}
