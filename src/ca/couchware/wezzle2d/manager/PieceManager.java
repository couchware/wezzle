package ca.couchware.wezzle2d.manager;

import ca.couchware.wezzle2d.*;
import ca.couchware.wezzle2d.Refactorer.RefactorSpeed;
import ca.couchware.wezzle2d.manager.AnimationManager;
import ca.couchware.wezzle2d.ResourceFactory.LabelBuilder;
import ca.couchware.wezzle2d.manager.BoardManager.Direction;
import ca.couchware.wezzle2d.manager.LayerManager.Layer;
import ca.couchware.wezzle2d.animation.FadeAnimation.Type;
import ca.couchware.wezzle2d.graphics.IPositionable.Alignment;
import ca.couchware.wezzle2d.graphics.PieceGrid;
import ca.couchware.wezzle2d.util.*;
import ca.couchware.wezzle2d.tile.*;
import ca.couchware.wezzle2d.animation.*;
import ca.couchware.wezzle2d.audio.Music;
import ca.couchware.wezzle2d.audio.Sound;
import ca.couchware.wezzle2d.event.IListenerComponent;
import ca.couchware.wezzle2d.event.IMouseListener;
import ca.couchware.wezzle2d.event.MouseEvent;
import ca.couchware.wezzle2d.event.MoveEvent;
import ca.couchware.wezzle2d.event.ScoreEvent;
import ca.couchware.wezzle2d.piece.*;
import ca.couchware.wezzle2d.ui.ILabel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * The piece manager keeps track of where the mouse pointer is on the board
 * and places the piece selector accordingly when its draw method is called.
 * 
 * @author cdmckay
 *
 */

public class PieceManager implements IMouseListener
{	
    private static int SLOW_SPEED_INVERSE = 125;
    private static int FAST_SPEED_INVERSE = 20;
    private static double SLOW_SPEED = 1.0 / (double) SLOW_SPEED_INVERSE;
    private static double FAST_SPEED = 1.0 / (double) FAST_SPEED_INVERSE;
    
    // -------------------------------------------------------------------------
    // Private Members
    // -------------------------------------------------------------------------       
    
    /**
     * A reference to the game window.
     */
    private IGameWindow window;
    
    /**
     * The possible buttons that may be clicked.
     */
    private static enum MouseButton
    {
        LEFT, RIGHT
    }            
    
    /**
     * A set of buttons that were clicked.
     */
    private EnumSet<MouseButton> mouseButtonSet = EnumSet.noneOf(MouseButton.class);
    
    /**
     * Should the piece manager drop automatically drop tiles after a 
     * commit?
     */
    private boolean tileDropOnCommit = true;
    
    /**
     * Is the board dropping in a tile?
     */
    private boolean tileDropInProgress = false;
    
    /**
     * Is the board animating the tile dropped?
     */
    private boolean tileDropAnimationInProgress = false;        
    
    /**
     * The number of tiles to drop this turn.
     */
    private int totalTileDropInAmount = 0;
    
    /**
     * The index list.
     */
    private ArrayList<Integer> openIndexList;
    
    /**
     * The tile currently being dropped.
     */
    private List<TileEntity> tileDropList = new ArrayList<TileEntity>();
    
     /**
     * Was the board recently refactored?
     */
    private boolean refactored = false;    	
	
    /**
     * Was the left mouse button clicked?
     */
    private volatile boolean mouseLeftReleased;
    
    /**
     * Was the right mouse button clicked?
     */
    private volatile boolean mouseRightReleased;        
    
    /**
     * The piece map.
     */
    //private EnumMap<PieceType, Piece> pieceMap;
    
    /**
     * The current piece.
     */
    private Piece piece;
    
	/**
	 * The piece grid.  The graphical representation of the piece.
	 */
	private PieceGrid pieceGrid;       
	
    /**
	 * The animation manager that animations are run with.
	 */
	private AnimationManager animationMan;
    
	/**
	 * The board manager the piece manager to attached to.
	 */
	private BoardManager boardMan;
    
    /**
     * The restriction board.  All the entries that are true are clickable,
     * all the ones that are false are not.
     */
    private boolean[] restrictionBoard;
    
    /**
     * This is set to true everytime the restriction board is clicked.
     */
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
            AnimationManager animationMan, 
            BoardManager boardMan)
	{       
		// Set the reference.
        this.window = ResourceFactory.get().getGameWindow();
        this.animationMan = animationMan;
		this.boardMan = boardMan;
        
        // Add the mouse listener.
        window.addMouseListener(this);
        
        // Default the mouse buttons to not clicked.
        mouseLeftReleased = false;
        mouseRightReleased = false;
        
        // Create the piece map.
//        pieceMap = new EnumMap<PieceType, Piece>(PieceType.class);
//        pieceMap.put(PieceType.DASH, new PieceDash());
//        pieceMap.put(PieceType.DIAGONAL, new PieceDiagonal());
//        pieceMap.put(PieceType.DOT, new PieceDot());
//        pieceMap.put(PieceType.L, new PieceL());
//        pieceMap.put(PieceType.LINE, new PieceLine());
        
        // Create new piece entity at the origin of the board.
		pieceGrid = new PieceGrid(boardMan, 
                boardMan.getX() + boardMan.getCellWidth(),
                boardMan.getY() + boardMan.getCellHeight());        
        
        // Load a random piece.
        loadPiece();
        pieceGrid.setXYPosition(limitPosition(window.getMouseImmutablePosition()));				                
        
        // Create the index list.
        this.openIndexList = new ArrayList<Integer>();    
        
        // Create the restriction board and fill it with trues.
        restrictionBoard = new boolean[boardMan.getCells()];
        clearRestrictionBoard();
	}	
        
        // Public API.
        public static PieceManager newInstance(AnimationManager animationMan, 
            BoardManager boardMan)
        {
            return new PieceManager(animationMan, boardMan);
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
        pieceGrid.setXYPosition(limitPosition(pieceGrid.getXYPosition()));
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
    
    private void startAnimationAt(final ImmutablePosition p, double speed)
    {
        // Add new animations.
        Set<Integer> indexSet = new HashSet<Integer>();
        getSelectedIndexSet(p, indexSet, null);
        
        for (Iterator it = indexSet.iterator(); it.hasNext(); )
        {
            final TileEntity t = boardMan.getTile((Integer) it.next());

            // Skip if this is not a tile.
            if (t == null)
                continue;

            // Make sure they have a pulse animation.                   
            t.setAnimation(new ZoomAnimation.Builder(ZoomAnimation.Type.LOOP_IN, t)
                    .minWidth(t.getWidth() - 8).v(speed).end());
            animationMan.add(t.getAnimation());
        }
    }
    
    private void adjustAnimationAt(final ImmutablePosition p, double speed)
    {
        Set<Integer> indexSet = new HashSet<Integer>();
        getSelectedIndexSet(p, indexSet, null);
        
        for (Iterator it = indexSet.iterator(); it.hasNext(); )
        {                    
            final TileEntity t = boardMan.getTile((Integer) it.next());

            // Skip if this is not a tile.
            if (t == null)                    
                continue;
                        
            IAnimation a = t.getAnimation();
            
            if (a == null || a instanceof ZoomAnimation == false)
                continue;
            
            ((ZoomAnimation) a).v(speed);            
        }
    }
    
    public void stopAnimation()
    {
        stopAnimationAt(pieceGrid.getXYPosition());
    }
    
    private void stopAnimationAt(final ImmutablePosition p)
    {
        // Remove old animations.
        Set<Integer> indexSet = new HashSet<Integer>();
        getSelectedIndexSet(p, indexSet, null);
        
        for (Iterator it = indexSet.iterator(); it.hasNext(); )
        {                    
            final TileEntity t = boardMan.getTile((Integer) it.next());

            // Skip if this is not a tile.
            if (t == null)
                continue;

            IAnimation a = t.getAnimation();            

            if (a != null)
                a.cleanUp();

            animationMan.remove(a);
            t.setAnimation(null);
        }
    }
    
    //--------------------------------------------------------------------------
    // Logic
    //--------------------------------------------------------------------------
    
    public void updateLogic(final Game game)
    {                     
        // If the board is refactoring, do not logicify.
        if (game.isBusy() == true)
             return;                 
                 
        // Which row should we be dropping tiles into?
        int dropRow = 0;
        if (boardMan.getGravity().contains(Direction.UP))
            dropRow = boardMan.getRows() - 1;
        else
            dropRow = 0;
        
        // Drop in any tiles that need to be dropped, one at a time. 
        //
        // The if statement encompasses the entire function in order to ensure
        // that the board is locked while tiles are dropping.
        if (tileDropInProgress == true)
        {          
            // Is the tile dropped currently being animated?
            // If not, that means we need to drop a new one.            
            if (tileDropAnimationInProgress == false)
            {
                // The number of pieces to drop in.
                int parallelTileDropInAmount = game.worldMan.getParallelTileDropInAmount();                                
                
                // Adjust for the pieces left to drop in.
                if (parallelTileDropInAmount > totalTileDropInAmount)
                    parallelTileDropInAmount = totalTileDropInAmount;                              
                
                // Count the open columns and build a list of all open indices.
                openIndexList.clear();
                int openColumnCount = 0;                                                
                for (int i = 0; i < boardMan.getColumns(); i++)
                {
                    if (boardMan.getTile(i, dropRow) == null)
                    {
                        openColumnCount++;
                        openIndexList.add(new Integer(i));
                    }
                }                         
                
                // At this point these must be equal.
                assert openColumnCount == openIndexList.size();
                
                // Automatically adjust the number of pieces to fall in based
                // on the number of open columns.
                if (parallelTileDropInAmount > openColumnCount)
                    parallelTileDropInAmount = openColumnCount;                              
                
                // Create a queue holding all the open columns in a randomized
                // order.
                LinkedList<Integer> randomIndexQueue = new LinkedList<Integer>();
                randomIndexQueue.addAll(openIndexList);
                Collections.shuffle(randomIndexQueue, Util.random);
                
                // Generate the indices. pick a random index from the available
                // indices in the indexList.
//                for (int i = 0; i < openIndexList.size(); i++)
//                {
//                    int r = Util.random.nextInt(openIndexList.size());
//                    Integer randomIndex = openIndexList.get(r);
//                    randomIndexQueue.add(randomIndex);
//                    openIndexList.remove(openIndexList.indexOf(randomIndex));
//                }                                                                   
                
                // If there are less items left (to ensure only 1 drop per drop 
                // in) and we have less than the max number of items...
                // drop an item in. Otherwise drop a normal.
                if (openColumnCount == 0 && totalTileDropInAmount > 0)                
                {                    
                    // The tile drop is no longer in progress.
                    tileDropInProgress = false;
                    
                    // Start the game over routine.
                    game.startGameOver();
                }
                else if (totalTileDropInAmount == 1 
                        && ( game.boardMan.getNumberOfItems() < game.worldMan.getMaxItems() 
                        || game.boardMan.getNumberOfMults() < game.worldMan.getMaxMults()))
                {
                    // The tile is an item.
                    tileDropList.add(boardMan.createTile(randomIndexQueue.remove(), 
                            dropRow, game.worldMan.getItem(game.boardMan.getNumberOfItems(),
                            game.boardMan.getNumberOfMults()).getTileType()));
                                  
                }
                else if (totalTileDropInAmount <= parallelTileDropInAmount
                       && (game.boardMan.getNumberOfItems() < game.worldMan.getMaxItems()
                       || game.boardMan.getNumberOfMults() < game.worldMan.getMaxMults()))
                {
                    // This must be true.
                    assert totalTileDropInAmount <= randomIndexQueue.size();
                    
                    // Drop in the first amount.
                    for (int i = 0; i < totalTileDropInAmount - 1; i++)
                    {
                        tileDropList.add(boardMan.createTile(randomIndexQueue.remove(), 
                                dropRow, TileType.NORMAL)); 
                    }
                    
                    // Drop in the item tile.
                    tileDropList.add(boardMan.createTile(randomIndexQueue.remove(),
                            dropRow, game.worldMan.getItem(game.boardMan.getNumberOfItems(),
                            game.boardMan.getNumberOfMults()).getTileType()));                     
                }
                else
                {
                    // They are all normals.
                    for (int i = 0; i < parallelTileDropInAmount; i++)
                    {
                        tileDropList.add(boardMan.createTile(randomIndexQueue.remove(), 
                                dropRow, TileType.NORMAL));
                    }                 
                }                
                          
                // See if the tile drop is still in progress.  If it's not
                // (which would be in the case of a game over), don't play
                // the sound or set the animation flag.
                if (tileDropInProgress == true)
                {        
                    // Start the animation.
                    game.soundMan.play(Sound.BLEEP);
                    
                    for (TileEntity tile : tileDropList)
                    {
                        if (tile == null)
                        {
                            throw new NullPointerException(
                                    "A tile was null in the tile drop list.");
                        }
                        else
                        {           
                            //IAnimation a = new ZoomInAnimation(tileDropped[i]);
                            IAnimation a = new ZoomAnimation.Builder(ZoomAnimation.Type.OUT, tile)
                                    .v(0.05).end();
                            tile.setAnimation(a);
                            animationMan.add(a);
                        }
                    }
                 
                    // Set the animation flag.
                    tileDropAnimationInProgress = true;
                }                                                     
            } 
            // If we got here, the animating is in progress, so we need to check
            // if it's done.  If it is, de-reference it and refactor.
            else if (isAnimationDone(tileDropList) == true)
            {
                // Clear the flag.
                tileDropAnimationInProgress = false;
                               
                // Run refactor.
                Refactorer.get()
                        .setRefactorSpeed(RefactorSpeed.DROP)
                        .startRefactor();
                
                // Remove the amount just removed from the total.
                totalTileDropInAmount -= tileDropList.size();
                
                // De-reference the tile dropped.
                tileDropList.clear();
                
                // Check to see if we have more tiles to drop. 
                // If not, stop tile dropping.
                if (totalTileDropInAmount == 0)  
                {
                    tileDropInProgress = false;
                }
                // Defensive.
                else if (totalTileDropInAmount < 0)
                {
                    throw new IllegalStateException("Tile drop count is: "
                            + totalTileDropInAmount);
                }                
            }
        }
        // In this case, the tile drop is not activated, so proceed normally
        // and handle mouse clicks and such.
        else
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
                pieceGrid.setXYPosition(limitPosition(pieceGrid.getXYPosition()));
                pieceGrid.setDirty(true);                                
                
                if (pieceGrid.isVisible() == true)
                    startAnimationAt(pieceGrid.getXYPosition(), SLOW_SPEED);

                // Reset released buttons.
                mouseButtonSet = EnumSet.noneOf(MouseButton.class);
            }
            // Animate selected pieces.
            else
            {
                // Filter the current position.
                ImmutablePosition pos = limitPosition(p);

                double speed = 1.0 / (double) 
                        Util.scaleInt(0, game.timerMan.getInitialTime(), 
                            FAST_SPEED_INVERSE, SLOW_SPEED_INVERSE, 
                            game.timerMan.getTime());
                
                // If the position changed, or the board was refactored.
                if (pos.getX() != pieceGrid.getX()
                        || pos.getY() != pieceGrid.getY()
                        || refactored == true)                    
                {
                    // Clear refactored flag.
                    refactored = false;

                    // Stop the old animations.
                    stopAnimationAt(pieceGrid.getXYPosition());

                    // Update piece grid position.
                    pieceGrid.setXYPosition(pos);             
                                        
                    // Start new animation.   
                    if (pieceGrid.isVisible() == true)
                        startAnimationAt(pos, speed);
                } 
                else
                {                                        
                    adjustAnimationAt(pieceGrid.getXYPosition(), speed);
                }
            } // end if
        } // end if
    }
    
    public void initiateCommit(final Game game)
    {
        // If a tile drop is already in progress, then return.
        if (tileDropInProgress == true)
            return;
        
        // Get the indices of the committed pieces.
        Set<Integer> indexSet = new LinkedHashSet<Integer>();
        Set<Integer> blankSet = new LinkedHashSet<Integer>();
        getSelectedIndexSet(this.pieceGrid.getXYPosition(), indexSet, blankSet);                                
        
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
        
        for (Integer index : indexSet)
            boardMan.getTile(index).onClick();
        
        // Remove and score the piece.
        int deltaScore = game.scoreMan.calculatePieceScore(indexSet);    
        
        // Notify the listener manager.
        if (game.tutorialMan.isTutorialInProgress() == true)
        {
            game.listenerMan.notifyScoreListener(new ScoreEvent(deltaScore, this), 
                IListenerComponent.GameType.TUTORIAL);
        }
        else
        {
             game.listenerMan.notifyScoreListener(new ScoreEvent(deltaScore, this), 
                IListenerComponent.GameType.GAME);
        }
        // Add score SCT.
        ImmutablePosition p = boardMan.determineCenterPoint(indexSet);
        
        final ILabel label = new LabelBuilder(p.getX(), p.getY())
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                .color(Game.SCORE_PIECE_COLOR)
                .size(game.scoreMan.determineFontSize(deltaScore))
                .text(String.valueOf(deltaScore))
                .end();
        
        IAnimation a1 = new FadeAnimation.Builder(Type.OUT, label).end();
        IAnimation a2 = new MoveAnimation.Builder(label)
                        .duration(1150).v(0.03).theta(90).end();
                    
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
        this.totalTileDropInAmount = 
                game.worldMan.calculateDropNumber(game, this.piece.getSize());

        // Increment the moves.
        if (game.tutorialMan.isTutorialInProgress() == true)
        {
            game.listenerMan.notifyMoveListener(new MoveEvent(1, this), 
                    IListenerComponent.GameType.TUTORIAL); 
        }
        else
        {
             game.listenerMan.notifyMoveListener(new MoveEvent(1, this), 
                    IListenerComponent.GameType.GAME); 
        }
        
        
        // Start a tile drop.
        if (tileDropOnCommit == true)
            tileDropInProgress = true;

        // Make visible.
        pieceGrid.setVisible(false);        
        
        // Run a refactor.       
        Refactorer.get().startRefactor();

        // Reset mouse buttons.
        mouseButtonSet = EnumSet.noneOf(MouseButton.class);

        // Pause timer.
        game.timerMan.setPaused(true);
    }
    
    /**
     * Check if an array of animations is done.
     * @param tiles
     * @return
     */
    private boolean isAnimationDone(List<TileEntity> tiles)
    {   
        for (TileEntity tile : tiles)
        {            
            if (tile != null && tile.getAnimation().isFinished() == false)
            {
                return false;  
            }
        } // end for
                        
        return true;
    }        

    /**
     * Gets the current restriction board.
     * 
     * @return
     */
    public boolean[] getRestrictionBoard()
    {
        return restrictionBoard;
    }

    /**
     * Sets the restriction board to the passed board.
     * 
     * @param restrictionBoard
     */
    public void setRestrictionBoard(boolean[] restrictionBoard)
    {
        this.restrictionBoard = restrictionBoard;
    }        
    
    /**
     * Resets the restriction board to only have true values.
     */
    public void clearRestrictionBoard()
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
     
    public boolean isTileDropInProgress()
    {
        return tileDropInProgress;
    }

    public PieceGrid getPieceGrid()
    {
        return pieceGrid;
    }

    public boolean isTileDropOnCommit()
    {
        return tileDropOnCommit;
    }

    public void setTileDropOnCommit(boolean tileDropOnCommit)
    {
        this.tileDropOnCommit = tileDropOnCommit;
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
    
}
