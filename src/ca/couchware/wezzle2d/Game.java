package ca.couchware.wezzle2d;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;


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
	
	/** The list of entities that need to be removed from the game this loop. */
	private ArrayList removeList = new ArrayList();
	
	private boolean activateRefactor = false;
	private boolean refactorDownInProgress = false;
	private boolean refactorLeftInProgress = false;

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
	
	/** The timer text. */
	Text timerText;
        
        /** The score text. */
        Text scoreText;
        
       

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
		// Create the board manager.
		boardMan = new BoardManager(272, 139, 8, 10);
		boardMan.generateBoard(40, 6);
		
		// Create the piece manager.
		pieceMan = new PieceManager(boardMan);
		window.addMouseListener(pieceMan);
		window.addMouseMotionListener(pieceMan);	
	
                // Create the score manager.
                scoreMan = new ScoreManager();
                
                // Create the sound manager.
                soundMan = new SoundManager();
                
		// Set up the timer text.
		timerText = ResourceFactory.get().getText();
		timerText.setSize(50);
		timerText.setAnchor(Text.BOTTOM | Text.HCENTER);
		timerText.setColor(new Color(252, 233, 45 ));
                
                // Set up the score text.
                scoreText = ResourceFactory.get().getText();
                scoreText.setSize(20);
                scoreText.setAnchor(Text.BOTTOM | Text.HCENTER);
                scoreText.setColor(new Color(252, 233, 45 ));
                
		
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

	/**
	 * Remove an entity from the game. The entity removed will no longer move or
	 * be drawn.
	 * 
	 * @param entity
	 *            The entity that should be removed
	 */
	public void removeEntity(Entity entity)
	{
		removeList.add(entity);
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
                HashSet set = new HashSet();
                boardMan.findXMatch(set);
                boardMan.findYMatch(set);
                
                // If there are matches, score them, remove them and then
                // refactor again.
                if (set.size() > 0)
                {
                    scoreMan.calculateLineScore(set, 1, 1);
                    soundMan.play(SoundManager.LINE);
                    boardMan.removeTiles(set);
                    runRefactor();
                }
                else
                {
                    pieceMan.loadRandomPiece();
                    pieceMan.setVisible(true);
                }
			} // end if
		} // end if
		
		// Draw the board.
		boardMan.draw();
		
		// Update piece manager logic and then draw it.
        pieceMan.logic(this);
		pieceMan.draw();
		
		// Draw the timer text.
		timerText.setText(String.valueOf(timeMan.getTime()));		
		timerText.draw(400, 100);                                
                
        // Draw the score text.
        scoreText.setText(String.valueOf(this.scoreMan.getTotalScore()));
        scoreText.draw(126, 400);               
		
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
