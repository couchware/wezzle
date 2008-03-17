package ca.couchware.wezzle2d;

import ca.couchware.wezzle2d.animation.ZoomAnimation;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

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
 * @author Kevin Glass
 */
public class Game extends Canvas implements GameWindowCallback
{	
    
    /**
	 * The manager in charge of maintaining the board.
	 */
	private BoardManager boardMan;
	
	/**
	 * The manager in charge of moving the piece around with the
	 * pointer and drawing the piece to the board.
	 */
	private PieceManager pieceMan;
	
	/** 
	 * The manager in charge of keeping track of the time. 
	 */
	private TimeManager timeMan;
        
     /** The manager in charge of score */
    public ScoreManager scoreMan;

    /** The manager in charge of sound */
    public SoundManager soundMan;
    
    /** The manager in charge of loading and saving properties */
    public PropertyManager propertyMan;
    
    /** The manager in charge of the moves. */
    public MoveManager moveMan;	
	
    /**
     * If true, refactor will be activated next loop.
     */
	private boolean activateRefactor = false;
    
    /**
     * If true, the board is currently being refactored downwards.
     */
	private boolean refactorDownInProgress = false;
    
    /**
     * If true, the board is currently being refactored leftward.
     */
	private boolean refactorLeftInProgress = false;
    
    /**
     * If true, a line removal will be activated next loop.
     */
    private boolean activateLineRemoval = false;
    
    /**
     * If true, a line removal is in progress.
     */
    private boolean lineRemovalInProgress = false;
    
    /**
     * The set of tile indices that will be removed.
     */
    private Set lineRemovalSet;
    
    /**
     * If true, a bomb removal will be activated next loop.
     */
    private boolean activateBombRemoval = false;
    
    /**
     * If true, a bomb removal is in progress.
     */
    private boolean bombRemovalInProgress = false;
    
    /**
     * The set of bomb tile indices that will be removed.
     */
    private Set bombRemovalSet;
    
	/**
	 * The time at which the last rendering looped started from the point of
	 * view of the game logic.
	 */
	private long lastLoopTime = SystemTimer.getTime();
	
	/** The window that is being used to render the game. */
	private GameWindow window;

	/** The time since the last record of FPS. */
	private long lastFramesPerSecondTime = 0;
	
	/** The recorded FPS. */
	private int framesPerSecond;

	/** The normal title of the window. */
	private String windowTitle = "Wezzle";
	
    /**
     * The text color.
     */
    private Color textColor = new Color(252, 233, 45);
    
	/** The timer text. */
	private Text timerText;
        
    /** The score text. */
    private Text scoreText;
    
    /** The high score text. */
    private Text highScoreText;
    
    /** The move count text */
    private Text moveCountText;
        
	/**
	 * Construct our game and set it running.
	 * 
	 * @param renderingType
	 *            The type of rendering to use (should be one of the contansts
	 *            from ResourceFactory)
	 */
	public Game(int renderingType) 
	{
		// Create a window based on a chosen rendering method.
		ResourceFactory.get().setRenderingType(renderingType);		

		final Runnable r = new Runnable()
		{
			public void run()
			{
				window = ResourceFactory.get().getGameWindow();
				window.setResolution(800, 600);
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
			Util.handleException(e);
		}
		catch (InvocationTargetException e)
		{
			Util.handleException(e);
		}		
	}

	public void startRendering()
	{
		window.startRendering();
	}
    
	/**
	 * Initialize the common elements for the game
	 */
	public void initialize()
	{
        // Initialize line index set.
        lineRemovalSet = new HashSet();
        
        // Initialize bomb index set.
        bombRemovalSet = new HashSet();
        
		// Create the board manager.
		boardMan = new BoardManager(272, 139, 8, 10);
		boardMan.generateBoard(40, 4, 6);
		
		// Create the piece manager.
		pieceMan = new PieceManager(boardMan);
		window.addMouseListener(pieceMan);
		window.addMouseMotionListener(pieceMan);	
	
        // Create the property manager. Must be done before Score manager.
        this.propertyMan = new PropertyManager();
        
        // Create the score manager.
        scoreMan = new ScoreManager(boardMan, propertyMan);

        // Create the sound manager.
        soundMan = new SoundManager();
        
        // Create the move manager.
        moveMan = new MoveManager();
                
		// Set up the timer text.
		timerText = ResourceFactory.get().getText();
		timerText.setSize(50);
		timerText.setAnchor(Text.BOTTOM | Text.HCENTER);
		timerText.setColor(textColor);
                
        // Set up the score text.
        scoreText = ResourceFactory.get().getText();
        scoreText.setSize(20);
        scoreText.setAnchor(Text.BOTTOM | Text.HCENTER);
        scoreText.setColor(textColor);
        
        // Set up the high score text.
        highScoreText = ResourceFactory.get().getText();
        highScoreText.setSize(20);
        highScoreText.setAnchor(Text.BOTTOM | Text.HCENTER);
        highScoreText.setColor(textColor);
                
         // Set up the move count text.
        moveCountText = ResourceFactory.get().getText();
        moveCountText.setSize(20);
        moveCountText.setAnchor(Text.BOTTOM | Text.HCENTER);
        moveCountText.setColor(textColor);
                        		
		// Create the time manager.
		timeMan = new TimeManager();

		// Setup the initial game state.
		startGame();
	}

	/**
	 * Start a fresh game, this should clear out any old data and create a new
	 * set.
	 */
	private void startGame()
	{		
		// Nothing here yet.
	}
    
    public void runRefactor()
    {
        // Set the refactor flag.
        this.activateRefactor = true;
    }
    
   public  void clearRefactor()
   {
       // Set the refactor flag.
       this.activateRefactor = false;
   }

	/**
	 * Notification that a frame is being rendered. Responsible for running game
	 * logic and rendering the scene.
	 */
	public void frameRendering()
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
		
		// See if we need to activate the refactor.
		if (activateRefactor == true)
		{            
            // Hide piece.
            pieceMan.setVisible(false);
            
			// Start down refactor.
			boardMan.startShiftDown();
			refactorDownInProgress = true;
			
			// Clear flag.
			clearRefactor();
		}
		
		// See if we're down refactoring.
		if (refactorDownInProgress == true)
		{
			if (boardMan.moveAll(delta) == false)
			{			
                // Clear down flag.
				refactorDownInProgress = false;
                
				// Synchronize board.
				boardMan.synchronize();							
				
				// Start left refactor.
				boardMan.startShiftLeft();
				refactorLeftInProgress = true;								
			}
		} // end if
		
		// See if we're left refactoring.
		if (refactorLeftInProgress == true)
		{
			if (boardMan.moveAll(delta) == false)
			{
                // Clear left flag.
				refactorLeftInProgress = false;
                
				// Synchronize board.
				boardMan.synchronize();		
                
                // Look for matches.
                lineRemovalSet.clear();
                boardMan.findXMatch(lineRemovalSet);
                boardMan.findYMatch(lineRemovalSet);
                
                // If there are matches, score them, remove them and then
                // refactor again.
                if (lineRemovalSet.size() > 0)
                {                                       
                    // Activate the line removal.
                    activateLineRemoval = true;                 
                }
                else
                {
                    pieceMan.loadRandomPiece();
                    pieceMan.setVisible(true);
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

            // Calculate score and play the sound.
            scoreMan.calculateLineScore(lineRemovalSet, 
                    ScoreManager.TYPE_LINE, 1);
            soundMan.play(SoundManager.LINE);

            // Make sure bombs aren't removed (they get removed
            // in a different step).
            bombRemovalSet = boardMan.scanBombs(lineRemovalSet);
            lineRemovalSet.removeAll(bombRemovalSet);

            // Start the line removal animations if there are any
            // non-bomb tiles.
            if (lineRemovalSet.size() > 0)
            {
                for (Iterator it = lineRemovalSet.iterator(); it.hasNext(); )
                {
                    TileEntity t = boardMan.getTile((Integer) it.next());
                    t.setAnimation(new ZoomAnimation(t));
                }

                // Set the flag.
                lineRemovalInProgress = true;
            }
            // Otherwise, start the bomb processing.
            else
            {
                activateBombRemoval = true;
            }
        }

        // If a line removal is in progress.
        if (lineRemovalInProgress == true)
        {
            // Check to see if they're all done.
            if (boardMan.getTile((Integer) lineRemovalSet.iterator().next())
                    .getAnimation().isDone() == true)
            {
                // Remove the tiles from the board.
                boardMan.removeTiles(lineRemovalSet);

                // Line removal is completed.
                lineRemovalInProgress = false;

                // See if there are any bombs in the bomb set.
                // If there are, activate the bomb removal.
                if (bombRemovalSet.size() > 0)
                    activateBombRemoval = true;
                // Otherwise, start a new refactor.
                else                        
                    runRefactor();
            }                        
        } // end if
                        
        // If a bomb removal is in progress.
        if (activateBombRemoval == true)
        {
            // Clear the flag.
            activateBombRemoval = false;
            
            // Get the tiles the bombs would affect.
            lineRemovalSet = boardMan.processBombs(bombRemovalSet);
            scoreMan.calculateLineScore(lineRemovalSet, 
                    ScoreManager.TYPE_BOMB, 1);

            // Extract all the new bombs.
            Set newBombRemovalSet = boardMan.scanBombs(lineRemovalSet);                                                
            newBombRemovalSet.removeAll(bombRemovalSet);

            // Remove all tiles that aren't new bombs.
            lineRemovalSet.removeAll(newBombRemovalSet);
            
            // Start the line removal animations.
            for (Iterator it = lineRemovalSet.iterator(); it.hasNext(); )
            {
                TileEntity t = boardMan.getTile((Integer) it.next());
                t.setAnimation(new ZoomAnimation(t));
            }

            // If other bombs were hit, they will be dealt with in another
            // bomb removal cycle.
            bombRemovalSet = newBombRemovalSet;
            
            // Set the flag.
            bombRemovalInProgress = true;
        }
        
        // If a line removal is in progress.
        if (bombRemovalInProgress == true)
        {
            // Check to see if they're all done.
            if (boardMan.getTile((Integer) lineRemovalSet.iterator().next())
                    .getAnimation().isDone() == true)
            {
                // Remove the tiles from the board.
                boardMan.removeTiles(lineRemovalSet);
                
                // Bomb removal is completed.
                bombRemovalInProgress = false;
                
                // See if there are any bombs in the bomb set.
                // If there are, activate the bomb removal.
                if (bombRemovalSet.size() > 0)
                    activateBombRemoval = true;
                // Otherwise, start a new refactor.
                else                
                    runRefactor();
            }  
        }
        
        // Animate all the pieces.
        boardMan.animateAll(delta);
        
		// Draw the board.
		boardMan.draw();
		
        // Update piece manager logic and then draw it.
        pieceMan.updateLogic(this);
        pieceMan.draw();
		
		// Draw the timer text.
		timerText.setText(String.valueOf(timeMan.getTime()));		
		timerText.draw(400, 100);                                
                
        // Draw the score text.
        scoreText.setText(String.valueOf(this.scoreMan.getTotalScore()));
        scoreText.draw(126, 400); 
        
        // Draw the high score text.
        highScoreText.setText(String.valueOf(this.scoreMan.getHighScore()));
        highScoreText.draw(126, 270);
        
        // Draw the move count text.
        moveCountText.setText(String.valueOf(this.moveMan.getCurrentMoveCount()));
        moveCountText.draw(669, 400);       
		
		// Handle the timer.
		timeMan.incrementInternalTime(delta);
		
		// if escape has been pressed, stop the game
		if (window.isKeyPressed(KeyEvent.VK_ESCAPE))
		{
			System.exit(0);
		}
	}

	/**
	 * Notification that the game window has been closed
	 */
	public void windowClosed()
	{
		System.exit(0);
	}
    
	/**
	 * The entry point into the game. We'll simply create an instance of class
	 * which will start the display and game loop.
	 * 
	 * @param argv
	 *            The arguments that are passed into our game
	 */
	public static void main(String argv[])
	{		
		Game g = new Game(ResourceFactory.JAVA2D);
		g.startRendering();		
	}
}
