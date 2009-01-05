package ca.couchware.wezzle2d.manager;

import ca.couchware.wezzle2d.Game;
import ca.couchware.wezzle2d.IGameWindow;
import ca.couchware.wezzle2d.Refactorer;
import ca.couchware.wezzle2d.ResourceFactory;
import ca.couchware.wezzle2d.ResourceFactory.LabelBuilder;
import ca.couchware.wezzle2d.animation.FadeAnimation;
import ca.couchware.wezzle2d.animation.FinishedAnimation;
import ca.couchware.wezzle2d.animation.IAnimation;
import ca.couchware.wezzle2d.animation.MetaAnimation;
import ca.couchware.wezzle2d.animation.MoveAnimation;
import ca.couchware.wezzle2d.animation.ZoomAnimation;
import ca.couchware.wezzle2d.audio.Sound;
import ca.couchware.wezzle2d.event.IMouseListener;
import ca.couchware.wezzle2d.event.MouseEvent;
import ca.couchware.wezzle2d.event.MoveEvent;
import ca.couchware.wezzle2d.graphics.IPositionable.Alignment;
import ca.couchware.wezzle2d.graphics.PieceGrid;
import ca.couchware.wezzle2d.manager.LayerManager.Layer;
import ca.couchware.wezzle2d.manager.Settings.Key;
import ca.couchware.wezzle2d.piece.Piece;
import ca.couchware.wezzle2d.piece.PieceType;
import ca.couchware.wezzle2d.tile.Tile;
import ca.couchware.wezzle2d.tile.TileType;
import ca.couchware.wezzle2d.ui.ITextLabel;
import ca.couchware.wezzle2d.util.ImmutablePosition;
import ca.couchware.wezzle2d.util.ImmutableRectangle;
import ca.couchware.wezzle2d.util.Util;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * The piece manager keeps track of where the mouse pointer is on the board
 * and places the piece selector accordingly when its draw method is called.
 * 
 * @author cdmckay
 *
 */

public class PieceManager implements IResettable, IMouseListener
{	
    private static int SLOW_SPEED = SettingsManager.get().getInt(Key.ANIMATION_PIECE_PULSE_SPEED_SLOW);
    private static int FAST_SPEED = SettingsManager.get().getInt(Key.ANIMATION_PIECE_PULSE_SPEED_FAST);
    
    // -------------------------------------------------------------------------
    // Private Members
    // -------------------------------------------------------------------------       
    
    /** A reference to the game window. */
    private IGameWindow window;
    
    /** A reference to the refactorer. */
    private Refactorer refactorer;
    
    /** The possible buttons that may be clicked. */
    private static enum MouseButton
    {
        LEFT, RIGHT
    }            
    
    /** A set of buttons that were clicked. */
    private Set<MouseButton> mouseButtonSet = EnumSet.noneOf(MouseButton.class);
        
    /** The animation manager that animations are run with. */
	private AnimationManager animationMan;
    
	/** The board manager the piece manager to attached to. */
	private BoardManager boardMan;    
    
    /** Was the board recently refactored? */
    private boolean refactored = false;	                  
    
    /** The current piece. */
    private Piece piece;
    
	/** The piece grid. */
	private PieceGrid pieceGrid;       
	
    /**
     * The restriction board.  All the entries that are true are clickable,
     * all the ones that are false are not.
     */
    private boolean[] restrictionBoard;
    
    /** This is set to true everytime the restriction board is clicked. */
    private volatile boolean restrictionBoardClicked = false;

    //--------------------------------------------------------------------------
    // Constructor
    //--------------------------------------------------------------------------
    
	/**
	 * The constructor.
	 * 
	 * @param boardMan The board manager.
	 */
	private PieceManager(
            Refactorer refactorer,
            AnimationManager animationMan, 
            BoardManager boardMan,
            LayerManager layerMan)
	{       
		// Set the reference.
        this.window       = ResourceFactory.get().getGameWindow();
        this.refactorer   = refactorer;
        this.animationMan = animationMan;
		this.boardMan     = boardMan;                
        
        // Create new piece entity at the origin of the board.
		pieceGrid = new PieceGrid(boardMan, 
                boardMan.getX() + boardMan.getCellWidth(),
                boardMan.getY() + boardMan.getCellHeight());        
        
        // Load a random piece.
        this.loadPiece();
        this.pieceGrid.setPosition(limitPosition(window.getMouseImmutablePosition()));
        layerMan.add(pieceGrid, Layer.PIECE_GRID);
        
        // Create the restriction board and fill it with trues.
        restrictionBoard = new boolean[boardMan.getCells()];
        this.clearRestrictionBoard();
	}	
        
    public void resetState()
    {
        // Clear the restriction board.
        this.clearRestrictionBoard();
        
        // Clear the mouse button set.
        this.clearMouseButtonSet();
        
        // Clear the refactored flag.
        this.refactored = false;                
    }
    
    /**
     * Create a new piece manager instance.
     * 
     * @param refactorer
     * @param animationMan
     * @param boardMan
     * @return
     */
    public static PieceManager newInstance(
            Refactorer refactorer,
            AnimationManager animationMan, 
            BoardManager boardMan,
            LayerManager layerMan)
    {
        return new PieceManager(refactorer, animationMan, boardMan, layerMan);
    }
    
    //--------------------------------------------------------------------------
    // Instance Methods
    //--------------------------------------------------------------------------    
  
    /**
     * Load a piece into the piece grid.
     * 
	 * @param piece The piece to set.
	 */
	public void loadPiece(final PieceType type)
	{
        // Remember which piece it is for rotating.
        this.piece = type.getPiece();
        
        // Load the piece into the piece grid.
		pieceGrid.loadStructure(piece.getStructure());
	}	
    
    /**
     * Loads a random piece into the piece grid.
     */
    public void loadPiece()
    {
        // Get an array of all the types.
        PieceType[] pt = PieceType.values();
        
        // Load a random one.
		loadPiece(pt[Util.random.nextInt(pt.length)]);		
		        
        // Rotate it up to 3 times.
        int numberOfRotations = Util.random.nextInt(4);        
        for (int i = 0; i <= numberOfRotations; i++)        
            piece.rotate();                    
        
        // Adjust the piece grid.
        pieceGrid.setPosition(limitPosition(pieceGrid.getPosition()));
        pieceGrid.setDirty(true);
	}
    
    /**
     * Adjusts the position of the piece grid so that it is within the board's
     * boundaries.
     * 
     * @param p The position to adjust.
     * @return The adjusted position.
     */
    public ImmutablePosition limitPosition(ImmutablePosition p)
	{
        int x = p.getX();
        int y = p.getY();
        
        ImmutableRectangle shape = boardMan.getShape();
        
        if (shape.contains(p) == false)        
        {            
            if (x < shape.getX()) x = shape.getX();
            else if (x > shape.getMaxX()) x = shape.getMaxX();
            
            if (y < shape.getY()) y = shape.getY();
            else if (y > shape.getMaxY()) y = shape.getMaxY();
        }
        
		int column = convertXToColumn(x);
		int row = convertYToRow(y);
		
		if (column >= boardMan.getColumns())
			column = boardMan.getColumns() - 1;
		
		if (row >= boardMan.getRows())
			row = boardMan.getRows() - 1;
		
		// Get the piece structure.
		Boolean[][] structure = piece.getStructure();
		
		// Cycle through the structure.
		for (int j = 0; j < structure[0].length; j++)
		{
			for (int i = 0; i < structure.length; i++)
			{	
				if (structure[i][j] == true)
				{					
					if (column - 1 + i < 0)
						column++;					
					else if (column - 1 + i >= boardMan.getColumns())
						column--;
						
					if (row - 1 + j < 0)
						row++;
					else if (row - 1 + j >= boardMan.getRows())
						row--;										
				}										
			} // end for				
		} // end for
		
		return new ImmutablePosition(
                boardMan.getX() + (column * boardMan.getCellWidth()), 
                boardMan.getY() + (row * boardMan.getCellHeight()));
	}
    
    /**
     * Gets the set of indices covered by the current piece.  All of the
     * indices that are over tiles are stored in the tileSet, while all
     * remaining indices are stored in the blankSet.
     * 
     * @param p
     * @param tileSet
     * @param emptySet
     */
    public void getSelectedIndexSet(ImmutablePosition p, 
            Set<Integer> tileSet, 
            Set<Integer> blankSet)
    {                
        // Convert to rows and columns.
        ImmutablePosition ap = limitPosition(p);
        int column = convertXToColumn(ap.getX());
		int row = convertYToRow(ap.getY());
        
        // Get the piece struture.
        Boolean[][] structure = piece.getStructure();
        
        for (int j = 0; j < structure[0].length; j++)
        {
            for (int i = 0; i < structure.length; i++)
            {	
                if (structure[i][j] == true)
                {      
                    int t = (column - 1 + i) 
                           + (row - 1 + j) * boardMan.getColumns();

                    if (boardMan.getTile(column - 1 + i, row - 1 + j) != null)                   
                    {
                       tileSet.add(t);                                      
                    }
                    else if (blankSet != null)
                    {
                       blankSet.add(t);
                    }
                } // end if	                
            } // end for				
        } // end for               
    }
    
    public void notifyRefactored()
    {
        refactored = true;
        
    }      
    
  
    
    private int convertXToColumn(final int x)
    {
        return (x - boardMan.getX()) / boardMan.getCellWidth();
    }    
    
    private int convertYToRow(final int y)
    {
       return (y - boardMan.getY()) / boardMan.getCellHeight(); 
    }
    
    private void startAnimationAt(final ImmutablePosition p, int speed)
    {
        // Add new animations.
        Set<Integer> indexSet = new HashSet<Integer>();
        getSelectedIndexSet(p, indexSet, null);
        
        for (Iterator it = indexSet.iterator(); it.hasNext(); )
        {
            final Tile t = boardMan.getTile((Integer) it.next());

            // Skip if this is not a tile.
            if (t == null)
                continue;

            // Make sure they have a pulse animation.                   
            t.setAnimation(new ZoomAnimation.Builder(ZoomAnimation.Type.LOOP_IN, t)
                    .minWidth(t.getWidth() - 8).speed(speed).end());
            animationMan.add(t.getAnimation());
        }
    }
    
    public void startAnimation(TimerManager timerMan)
    {
        startAnimationAt(pieceGrid.getPosition(), 
                getPulseSpeed(timerMan.getInitialTime(), timerMan.getTime()));
    }
    
    private void adjustAnimationAt(final ImmutablePosition p, int speed)
    {
        Set<Integer> indexSet = new HashSet<Integer>();
        getSelectedIndexSet(p, indexSet, null);
        
        for (Iterator it = indexSet.iterator(); it.hasNext(); )
        {                    
            final Tile t = boardMan.getTile((Integer) it.next());

            // Skip if this is not a tile.
            if (t == null)                    
                continue;
                        
            IAnimation a = t.getAnimation();
            
            if (a == null || a instanceof ZoomAnimation == false)
                continue;
            
            ((ZoomAnimation) a).speed(speed);            
        }
    }
    
    public void stopAnimation()
    {
        stopAnimationAt(pieceGrid.getPosition());
    }
    
    private void stopAnimationAt(final ImmutablePosition p)
    {
        // Remove old animations.
        Set<Integer> indexSet = new HashSet<Integer>();
        getSelectedIndexSet(p, indexSet, null);
        
        for (Iterator it = indexSet.iterator(); it.hasNext(); )
        {                    
            final Tile t = boardMan.getTile((Integer) it.next());

            // Skip if this is not a tile.
            if (t == null)
                continue;

            IAnimation a = t.getAnimation();            

            if (a != null) a.cleanUp();

            animationMan.remove(a);
            t.setAnimation(null);
        }
    }
    
    //--------------------------------------------------------------------------
    // Logic
    //--------------------------------------------------------------------------
    
    public void updateLogic(final Game game)
    {                
        final SettingsManager settingsMan = game.settingsMan;
        final ItemManager     itemMan     = game.itemMan;
        
        // If the board is refactoring, do not logicify.
        if (game.isBusy() == true) return;                 

        // In this case, the tile drop is not activated, so proceed normally
        // and handle mouse clicks and such.
        if (!game.tileDropper.isTileDropping())
        {      
            // Grab the current mouse position.
            final ImmutablePosition p = window.getMouseImmutablePosition();             
            
            if (mouseButtonSet.contains(MouseButton.LEFT) == true)
            {          
               mouseButtonSet.remove(MouseButton.LEFT);
               initiateCommit(game);
            }
            else if (mouseButtonSet.contains(MouseButton.RIGHT) == true)
            {                                
                // Rotate the piece.            
                stopAnimation();
                
                piece.rotate();
                pieceGrid.setPosition(limitPosition(pieceGrid.getPosition()));
                pieceGrid.setDirty(true);                                
                
                if (pieceGrid.isVisible() == true)
                    startAnimationAt(pieceGrid.getPosition(), SLOW_SPEED);

                // Reset released buttons.
                mouseButtonSet = EnumSet.noneOf(MouseButton.class);
            }
            // Animate selected pieces.
            else
            {
                // Filter the current position.
                ImmutablePosition pos = limitPosition(p);

                int speed = getPulseSpeed(game.timerMan.getInitialTime(), 
                        game.timerMan.getTime());                             
                
                // If the position changed, or the board was refactored.
                if (pos.getX() != pieceGrid.getX()
                        || pos.getY() != pieceGrid.getY()
                        || refactored == true)                    
                {
                    // Clear refactored flag.
                    refactored = false;

                    // Stop the old animations.
                    stopAnimationAt(pieceGrid.getPosition());

                    // Update piece grid position.
                    pieceGrid.setPosition(pos);             
                                        
                    // Start new animation.   
                    if (pieceGrid.isVisible() == true)
                        startAnimationAt(pos, speed);
                } 
                else
                {                                        
                    adjustAnimationAt(pieceGrid.getPosition(), speed);
                }
            } // end if
        } // end if
    }
    
    /**
     * Determines the pulse speed based on the initial time and the current time.
     * 
     * @param initialTime
     * @param time
     * @return
     */
    private int getPulseSpeed(int initialTime, int time)
    {
        return Util.scaleInt(0, initialTime, SLOW_SPEED, FAST_SPEED, initialTime - time);
    }
    
    public void initiateCommit(final Game game)
    {
        // If a tile drop is already in progress, then return.
        if (game.tileDropper.isTileDropping() == true)
            return;
        
        // Get the indices of the committed pieces.
        Set<Integer> indexSet = new LinkedHashSet<Integer>();
        Set<Integer> blankSet = new LinkedHashSet<Integer>();
        getSelectedIndexSet(this.pieceGrid.getPosition(), indexSet, blankSet);                                
        
        // See if any of the indices are restricted.
        for (Integer index : indexSet)
        {
            if (restrictionBoard[index] == false)
            {
                setRestrictionBoardClicked(true);
                mouseButtonSet = EnumSet.noneOf(MouseButton.class);
                return;
            }          
        } // end for
        
        for (Integer index : blankSet)
        {
            if (restrictionBoard[index] == false)
            {
                setRestrictionBoardClicked(true);
                mouseButtonSet = EnumSet.noneOf(MouseButton.class);
                return;
            }
        } // end for
        
        // Play the sound.
        game.soundMan.play(Sound.CLICK);
        
        // The wezzle fade animation.
        List<IAnimation> wezzleAnimationList = new ArrayList<IAnimation>();
        
        Tile tile = null;
        for (Integer index : indexSet)
        {
            tile = boardMan.getTile(index);
            
            // Invoke the on-click behaviour.            
            tile.fireTileClickedEvent();
                         
        } // end for                 
        
        // Remove and score the piece.
        int deltaScore = game.scoreMan.calculatePieceScore(indexSet);                    
        
        // Increment the score.
        if (game.tutorialMan.isTutorialRunning() == false)       
        {
            game.scoreMan.incrementScore(deltaScore);        
        }
        
        // Add score SCT.
        ImmutablePosition p = boardMan.determineCenterPoint(indexSet);
        
        final ITextLabel label = new LabelBuilder(p.getX(), p.getY())
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                .color(game.settingsMan.getColor(Key.SCT_COLOR_PIECE))
                .size(game.scoreMan.determineFontSize(deltaScore))
                .text(String.valueOf(deltaScore))
                .end();
        
        SettingsManager settingsMan = SettingsManager.get();
        
        IAnimation a1 = new FadeAnimation.Builder(FadeAnimation.Type.OUT, label)
                .wait(settingsMan.getInt(Key.SCT_SCORE_FADE_WAIT))
                .duration(settingsMan.getInt(Key.SCT_SCORE_FADE_DURATION))
                .minOpacity(settingsMan.getInt(Key.SCT_SCORE_FADE_MIN_OPACITY))
                .maxOpacity(settingsMan.getInt(Key.SCT_SCORE_FADE_MAX_OPACITY))
                .end();
        
        IAnimation a2 = new MoveAnimation.Builder(label)
                .duration(settingsMan.getInt(Key.SCT_SCORE_MOVE_DURATION))
                .speed(settingsMan.getInt(Key.SCT_SCORE_MOVE_SPEED))
                .theta(settingsMan.getInt(Key.SCT_SCORE_MOVE_THETA))
                .end();
                    
        a2.setStartRunnable(new Runnable()
        {
            public void run()
            { game.layerMan.add(label, Layer.EFFECT); }
        });

        a2.setFinishRunnable(new Runnable()
        {
            public void run()
            { game.layerMan.remove(label, Layer.EFFECT); }
        });          
        
        game.animationMan.add(a1);
        game.animationMan.add(a2);
        a1 = null;
        a2 = null;
        
        // Release references.
        p = null;       
        
        // Remove the tiles.
        game.boardMan.removeTiles(indexSet);

        // Set the count to the piece size.
        game.tileDropper.updateDropAmount(game, this.piece.getSize());

        // Increment the moves.
        if (game.tutorialMan.isTutorialRunning() == true)
        {
            game.listenerMan.notifyMoveCommitted(new MoveEvent(this, 1), 
                    ListenerManager.GameType.TUTORIAL); 
        }
        else
        {
             game.listenerMan.notifyMoveCommitted(new MoveEvent(this, 1), 
                    ListenerManager.GameType.GAME); 
        }
                
        // Start a tile drop.
        if (game.tileDropper.isDropOnCommit()) game.tileDropper.startDrop();        

        // Make visible.
        pieceGrid.setVisible(false);        
        
        // Run a refactor.       
        refactorer.startRefactor();

        // Reset mouse buttons.
        mouseButtonSet = EnumSet.noneOf(MouseButton.class);

        // Pause timer.
        game.timerMan.setPaused(true);
    }              

    /**
     * Gets the current restriction board.
     * 
     * @return
     */
    public boolean[] getRestrictionBoard()
    {
        return restrictionBoard.clone();
    }

    /**
     * Sets the restriction board to the passed board.
     * 
     * @param restrictionBoard
     */
    public void setRestrictionBoard(boolean[] restrictionBoard)
    {
        this.restrictionBoard = restrictionBoard.clone();
    }        
    
    /**
     * Resets the restriction board to only have true values.
     */
    final public void clearRestrictionBoard()
    {
        // Fill restriction board with true values.
        Arrays.fill(restrictionBoard, true);
    }
    
    /**
     * Reverses the restriction board, turning all true values into false
     * values and vice versa.
     */
    public void reverseRestrictionBoard()
    {
        for (int i = 0; i < restrictionBoard.length; i++)
            restrictionBoard[i] = !restrictionBoard[i];
    }
           
    public boolean getRestrictionCell(int index)
    {
        return restrictionBoard[index];
    }
    
    public boolean getRestrictionCell(int column, int row)
    {
        return getRestrictionCell(row * boardMan.getColumns() + column);
    }
    
    public void setRestrictionCell(int index, boolean value)
    {
        restrictionBoard[index] = value;
    }
    
    public void setRestrictionCell(int column, int row, boolean value)
    {
        setRestrictionCell(row * boardMan.getColumns() + column, value);
    }
            
    //--------------------------------------------------------------------------
    // Getters and Setters
    //--------------------------------------------------------------------------     

//    public PieceGrid getPieceGrid()
//    {
//        return pieceGrid;
//    }
    
    public void showPieceGrid()
    {
        this.pieceGrid.setVisible(true);
    }
    
    public void hidePieceGrid()
    {
        this.pieceGrid.setVisible(false);
    }
    
    public ImmutablePosition getPieceGridPosition()
    {
        return this.pieceGrid.getPosition();
    }

    /**
     * Has the restriction board been clicked?
     * 
     * @return True if it has been clicked, false otherwise.
     */
    public boolean isRestrictionBoardClicked()
    {
        return restrictionBoardClicked;
    }

    /**
     * Set the click status of the restriction board.  This is mainly used
     * to set it to false.
     * 
     * @param restrictionBoardClicked
     */
    public void setRestrictionBoardClicked(boolean clicked)
    {
        this.restrictionBoardClicked = clicked;
    }        
    
    /**
     * Clears all mouse button set flags.     
     */
    public void clearMouseButtonSet()
    {
        mouseButtonSet = EnumSet.noneOf(MouseButton.class);
    }
    
    //--------------------------------------------------------------------------
    // Events
    //--------------------------------------------------------------------------
    
    public void mouseClicked(MouseEvent e)
	{
		// TODO Auto-generated method stub
		
	}

	public void mouseEntered(MouseEvent e)
	{
		// TODO Auto-generated method stub
		
	}

	public void mouseExited(MouseEvent e)
	{
		// TODO Auto-generated method stub
		
	}

	public void mousePressed(MouseEvent e)
	{
		// TODO Auto-generated method stub
		
	}

	public void mouseReleased(MouseEvent e)
	{
        // Debug.
        //Util.handleMessage("Button clicked.", Thread.currentThread());                
        
        // Retrieve the mouse position.
        final ImmutablePosition p = window.getMouseImmutablePosition();
        
        // Ignore click if we're outside the board.
        if (boardMan.getShape().contains(p) == false)
            return;                    
            
		// Check which button.
        switch (e.getButton())
        {
            // Left mouse clicked.
            case LEFT:
                mouseButtonSet.add(MouseButton.LEFT);
                break;
              
            // Right mouse clicked.
            case RIGHT:
                mouseButtonSet.add(MouseButton.RIGHT);
                break;
                
            //temp.
            case MIDDLE:
                boardMan.insertItemRandomly(TileType.BOMB);
                break;
                
            default:
                LogManager.recordMessage("No recognized button pressed.", 
                        "PieceManager#mouseReleased");
        }
	}

    public void mouseDragged(MouseEvent e)
    {
        // Intentionally blank.
    }

    public void mouseMoved(MouseEvent e)
    {
        // Intentionally blank.
    }

    public void mouseWheel(MouseEvent e)
    {
        //LogManager.recordMessage("Wheeled by: " + e.getDeltaWheel());
    }   
    
}
