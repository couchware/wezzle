package ca.couchware.wezzle2d.manager;

import ca.couchware.wezzle2d.tracker.Line;
import ca.couchware.wezzle2d.animation.AnimationAdapter;
import ca.couchware.wezzle2d.animation.FadeAnimation;
import ca.couchware.wezzle2d.animation.IAnimation;
import ca.couchware.wezzle2d.animation.MoveAnimation;
import ca.couchware.wezzle2d.event.KeyEvent;
import ca.couchware.wezzle2d.manager.LayerManager.Layer;
import ca.couchware.wezzle2d.graphics.GraphicEntity;
import ca.couchware.wezzle2d.event.IKeyListener;
import ca.couchware.wezzle2d.graphics.AbstractEntity;
import ca.couchware.wezzle2d.graphics.EntityGroup;
import ca.couchware.wezzle2d.manager.Settings.Key;
import ca.couchware.wezzle2d.tile.RocketTile;
import ca.couchware.wezzle2d.tile.StarTile;
import ca.couchware.wezzle2d.tile.Tile;
import ca.couchware.wezzle2d.tile.TileColor;
import ca.couchware.wezzle2d.tile.TileHelper;
import ca.couchware.wezzle2d.tile.TileType;
import ca.couchware.wezzle2d.tracker.TileEffect;
import ca.couchware.wezzle2d.util.CouchLogger;
import ca.couchware.wezzle2d.util.ImmutablePosition;
import ca.couchware.wezzle2d.util.ImmutableRectangle;
import ca.couchware.wezzle2d.util.ArrayUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Manages the game board.  A replacement for the GameBoard class from
 * the SVG-based Wezzle.
 *  
 * @author cdmckay
 * 
 */
public class BoardManager implements IResettable, ISaveable, IKeyListener
{
    //--------------------------------------------------------------------------
    // Static Members
    //--------------------------------------------------------------------------
    
    /**
     * An enumeration representing the four directions.
     * 
     * @author cdmckay
     */
    public static enum Direction
    {
        NONE( 0 ),
        UP( -1 ),
        DOWN( 1 ),
        LEFT( -1 ),
        RIGHT( 1 );

        private int dir;

        Direction(int dir)
        {
            this.dir = dir;
        }

        public int asInteger()
        {
            return dir;
        }

    }

    /**
     * The types of board animations.
     */
    public static enum AnimationType
    {
        ROW_FADE,
        SLIDE_FADE

    }
    final private static int BOARD_X = 272;

    final private static int BOARD_Y = 139;

    final private static int BOARD_COLUMNS = 8;

    final private static int BOARD_ROWS = 10;

    /** The width of a grid cell. */
    final private int cellWidth;

    /** The height of a grid cell. */
    final private int cellHeight;

    /**
     * The default number of colours.
     */
    final private int DEFAULT_NUMBER_OF_COLORS = 5;

    /**
     * The animation manager.
     */
    final private AnimationManager animationMan;

    /**
     * The item manager.
     */
    final private ItemManager itemMan;

    /**
     * The layer manager.
     */
    final private LayerManager layerMan;

    /**
     * The x-coordiante of the top left corner of the board.
     */
    final private int x;

    /**
     * The y-coordinate of the top left corner of the board.
     */
    final private int y;

    /**
     * The number of columns in the game board.
     */
    final private int columns;

    /**
     * The number of rows in the game board.
     */
    final private int rows;

    /**
     * The total number of cells.
     */
    final private int cells;

    /**
     * The minimum number of tiles in a match.
     */
    final private int minimumMatch = 3;

    /**
     * The width of the board.
     */
    final private int width;

    /**
     * The height of the board.
     */
    final private int height;

    /**
     * The shape of the board.
     */
    final private ImmutableRectangle shape;

    /** 
     * The colour locked map.  If the key in the map has a true value, that
     * means it is locked and cannot form lines.
     */
    final private Map<TileColor, Boolean> lockedColorMap =
            new EnumMap<TileColor, Boolean>( TileColor.class );

    /**
     * The hash map keys for storing the score manager state.
     */
    private static enum Keys
    {
        NUMBER_OF_COLORS,
        NUMBER_OF_TILES,
        NUMBER_OF_ITEMS,
        NUMBER_OF_MULTIPLIERS,
        GRAVITY,
        BOARD,
        SCRATCH_BOARD

    }
    /**
     * The hash map used to save the score manager's state.
     */
    final private Map<Keys, Object> managerState =
            new EnumMap<Keys, Object>( Keys.class );

    //--------------------------------------------------------------------------
    // Instance Members
    //--------------------------------------------------------------------------               
    /**
     * Whether or not this is visible.
     */
    private boolean visible;

    /**
     * Whether or not the board needs to be drawn.
     */
    private boolean dirty;

    /**
     * The number of colours.
     */
    private int numberOfColors;

    /**
     * The number of tiles.
     */
    private int numberOfTiles;

    /**
     * The number of items.
     */
    private int numberOfItems;

    /**
     * The number of mults.
     */
    private int numberOfMultipliers;

    /**
     * The gravity corner.
     */
    private EnumSet<Direction> gravityDirection;

    /**
     * The array representing the game board.
     */
    private Tile[] board;

    /**
     * An array representing the scratch board.
     */
    private Tile[] scratchBoard;

    //--------------------------------------------------------------------------
    // Constructor
    //--------------------------------------------------------------------------
    /**
     * The constructor.
     */
    private BoardManager(
            final AnimationManager animationMan,
            final LayerManager layerMan,
            final ItemManager itemMan)
    {
        // Board is initially visible.
        this.visible = true;

        // Keep reference to managers.
        this.animationMan = animationMan;
        this.layerMan = layerMan;
        this.itemMan = itemMan;

        // Set the cell width and height. Hard-coded to 32x32 for now.
        this.cellWidth = 32;
        this.cellHeight = 32;

        // Set the x and y coordinates.
        this.x = BOARD_X;
        this.y = BOARD_Y;

        // Set columns and rows.
        this.columns = BOARD_COLUMNS;
        this.rows = BOARD_ROWS;
        this.cells = columns * rows;

        // Set the board width and height.
        this.width = columns * cellWidth;
        this.height = rows * cellHeight;

        // Create the shape.
        this.shape = new ImmutableRectangle( x, y, width, height );

        // Initialize board.
        board = new Tile[cells];
        scratchBoard = new Tile[cells];

        // Reset the state.
        this.resetState();
    }

    /**
     * Reset the board manager to its starting state.
     */
    public void resetState()
    {
        // Set the gravity to be to the bottom left by default.
        this.gravityDirection = EnumSet.of( Direction.DOWN, Direction.LEFT );

        // Set the number of various things.
        this.numberOfColors = DEFAULT_NUMBER_OF_COLORS;
        this.numberOfTiles = 0;
        this.numberOfMultipliers = 0;
        this.numberOfItems = 0;

        // Reset the color locks.
        for ( TileColor color : TileColor.values() )
        {
            lockedColorMap.put( color, false );
        }

        // Stop all the animations on all the tiles.
        for ( Tile tile : board )
        {
            if ( tile == null )
            {
                continue;
            }

            IAnimation anim = tile.getAnimation();
            if ( anim != null )
            {
                anim.cleanUp();
            }
            animationMan.remove( anim );
        }

        // Clear the board and scratch board.
        Arrays.fill( board, null );
        Arrays.fill( scratchBoard, null );

        // Reset visibility.
        this.visible = true;
    }

    /**
     * Saves the current state of the board.
     */
    public void saveState()
    {
        managerState.put( Keys.NUMBER_OF_COLORS, numberOfColors );
        managerState.put( Keys.NUMBER_OF_ITEMS, numberOfItems );
        managerState.put( Keys.NUMBER_OF_MULTIPLIERS, numberOfMultipliers );
        managerState.put( Keys.NUMBER_OF_TILES, numberOfTiles );
        managerState.put( Keys.GRAVITY, gravityDirection );
        managerState.put( Keys.BOARD, board.clone() );
        managerState.put( Keys.SCRATCH_BOARD, scratchBoard.clone() );

        CouchLogger.get().recordMessage( this.getClass(), "Saved " + numberOfTiles + " tiles." );
        CouchLogger.get().recordMessage( this.getClass(), "Saved " + numberOfItems + " items." );
        CouchLogger.get().recordMessage( this.getClass(), "Saved " + numberOfMultipliers + " mults." );
    }

    /**
     * Loads a previously saved state of the board.
     */
    @SuppressWarnings("unchecked")
    public void loadState()
    {
        // Clear the board.
        clearBoard();

        numberOfColors = (Integer) managerState.get( Keys.NUMBER_OF_COLORS );
        numberOfItems = (Integer) managerState.get( Keys.NUMBER_OF_ITEMS );
        numberOfTiles = (Integer) managerState.get( Keys.NUMBER_OF_TILES );
        numberOfMultipliers = (Integer) managerState.get( Keys.NUMBER_OF_MULTIPLIERS );
        gravityDirection = (EnumSet<Direction>) managerState.get( Keys.GRAVITY );
        scratchBoard = (Tile[]) managerState.get( Keys.SCRATCH_BOARD );
        board = (Tile[]) managerState.get( Keys.BOARD );

        // Make sure that this board is in the layer manager.
        layerize();

        // readd the item counts.
        for ( Tile t : board )
        {
            if ( t == null )
            {
                continue;
            }

            if ( t.getType() != TileType.NORMAL )
            {
                for ( Item item : itemMan.getItemList() )
                {
                    if ( item.getTileType() == t.getType() )
                    {
                        item.incrementCurrentAmount();
                        CouchLogger.get().recordMessage( this.getClass(), item.
                                getTileType() + " has " + item.getCurrentAmount() + " instances." );
                        break;
                    }
                } // end for
            } // end if
        }

        CouchLogger.get().recordMessage( this.getClass(), "Loaded " + numberOfTiles + " tiles." );
        CouchLogger.get().recordMessage( this.getClass(), "Loaded " + numberOfItems + " items." );
        CouchLogger.get().recordMessage( this.getClass(), "Loaded " + numberOfMultipliers + " multipliers." );
    }

    /**
     * Create a new board manager instance.
     * 
     * @param animationMan
     * @param layerMan
     * @param x
     * @param y
     * @param columns
     * @param rows
     * @return
     */
    public static BoardManager newInstance(final AnimationManager animationMan,
            final LayerManager layerMan,
            final ItemManager itemMan)
    {
        return new BoardManager( animationMan, layerMan, itemMan );
    }

    //--------------------------------------------------------------------------
    // Instance Methods
    //--------------------------------------------------------------------------
    /**
     * Set the board to the passed in array of tile entities.
     * 
     * @param newBoard
     */
    public void loadBoard(Tile[] newBoard)
    {
        // Make sure the array is the right size.        
        assert newBoard.length == cells;

        // Set the current board to the passed board.
        board = newBoard;
    }

    /**
     * Generates a random game board with a linked list of item descriptors.
     * 
     * @param items A linked list of Item Descriptors.
     */
    public void generateBoard(List<Item> itemList, int level)
    {
        // Make sure the board is clean.
        this.clearBoard();
        assert (itemList.get( 0 ) instanceof Item);

        int count = 0;
        for ( int i = 0; i < itemList.size(); i++ )
        {
            int offset = 0;

            // Handle the case where the normal tiles are different. This occurs
            // When players start on a level other than level 1.
            if ( i == 0 )
            {
                offset = level - 1;
            }

            for ( int j = 0;
                    j < itemList.get( i ).getInitialAmount() + offset; j++ )
            {
                this.createTile( count, itemList.get( i ).getTileType() );
                count++;
            }
        }

        shuffleBoard();
        instantRefactorBoard();

        HashSet<Integer> set = new HashSet<Integer>();



        while ( true )
        {

            findXMatch( set, null );
            findYMatch( set, null );



            if ( set.size() > 0 )
            {
                for ( Iterator it = set.iterator(); it.hasNext(); )
                {
                    Integer n = (Integer) it.next();
                    this.createTile( n.intValue(),
                            getTile( n.intValue() ).getType() );
                }

                set.clear();
            }
            else
            {
                break;
            }
        } // end while


    }

    /**
     * Clear the board of all tiles.
     */
    public void clearBoard()
    {
        for ( int i = 0; i < cells; i++ )
        {
            if ( this.getTile( i ) != null )
            {
                this.removeTile( i );
            }
        }

        // Ensure the item counts are set to 0
        for ( Item item : itemMan.getItemList() )
        {
            item.setCurrentAmount( 0 );
        }
    }

    /**
     * Shuffles the board randomly.
     */
    private void shuffleBoard()
    {
        for ( int i = 0; i < cells; i++ )
        {
            swapTile( i, ArrayUtil.random.nextInt( cells ) );
        }
    }

    /**
     * An instant refactor used for generating boards.
     */
    public void instantRefactorBoard()
    {
        // Check the vertical direction.
        if ( gravityDirection.contains( Direction.DOWN ) == true )
        {
            for ( int i = 0; i < cells; i++ )
            {
                Tile tile = board[i];
                if ( tile == null )
                {
                    continue;
                }

                int tiles = countTilesInDirection( Direction.DOWN, i );
                int bound = calculateBound( Direction.DOWN, tiles );

                if ( bound != board[i].getY() )
                {
                    board[i].setY( bound );
                    //synchronizeTile(board[i]);
                    //board[i] = null;
                }
            }
        }
        else
        {
            for ( int i = 0; i < cells; i++ )
            {
                Tile tile = board[i];
                if ( tile == null )
                {
                    continue;
                }

                int tiles = countTilesInDirection( Direction.UP, i );
                int bound = calculateBound( Direction.UP, tiles );

                if ( bound != board[i].getY() )
                {
                    board[i].setY( bound );
                    //synchronizeTile(board[i]);
                    //board[i] = null;
                }
            }
        }

        synchronize();

        // Check the horizontal direction.
        if ( gravityDirection.contains( Direction.LEFT ) == true )
        {
            for ( int i = 0; i < cells; i++ )
            {
                Tile tile = board[i];
                if ( tile == null )
                {
                    continue;
                }

                int tiles = countTilesInDirection( Direction.LEFT, i );
                int bound = calculateBound( Direction.LEFT, tiles );

                if ( bound != board[i].getX() )
                {
                    board[i].setX( bound );
                    //synchronizeTile(board[i]);
                    //board[i] = null;
                }
            }
        }
        else
        {
            for ( int i = 0; i < cells; i++ )
            {
                Tile tile = board[i];
                if ( tile == null )
                {
                    continue;
                }

                int tiles = countTilesInDirection( Direction.RIGHT, i );
                int bound = calculateBound( Direction.RIGHT, tiles );
                if ( bound != board[i].getX() )
                {
                    board[i].setX( bound );
                    //synchronizeTile(board[i]);
                    //board[i] = null;
                }
            }
        }

        synchronize();
    }

    /**
     * Searches for all matches in the X-direction and returns a linked list
     * with the indices of the matches.
     * 
     * @param set The linked list that will be filled with indices.
     * @param chain The chain that is passed through
     * @return The number of matches found.
     */
    public int findXMatch(Set<Integer> set, List<Line> chainList)
    {
        // The line count.
        int lineCount = 0;

        // Cycle through the board looking for a match in the X-direction.
        for ( int i = 0; i < cells; i++ )
        {
            // Build up the tiles to build up lines.
            List<Tile> tileList = new ArrayList<Tile>();

            // Check to see if there's even enough room for an X-match.
            if ( columns - (i % columns) < minimumMatch )
            {
                continue;
            }

            // Make sure there's a tile here.
            if ( board[i] == null )
            {
                continue;
            }

            // Get the color of this tile.
            TileColor color = board[i].getColor();

            // Make sure the tile's colour is not locked.
            // if (lockedColorMap.get(color) == true)
            //    continue;

            // See how long we have a match for.
            int j;
            for ( j = 1; j < (columns - (i % columns)); j++ )
            {
                if ( board[i + j] == null || board[i + j].getColor() != color )
                {
                    break;
                }
            }

            // Check if we have a match.
            if ( j >= minimumMatch )
            {
                CouchLogger.get().recordMessage( this.getClass(), "XMatch of length " + j + " found." );

                lineCount++;

                // Copy all matched locations to the linked list.
                for ( int k = i; k < i + j; k++ )
                {
                    set.add( new Integer( k ) );

                    // Build up the line of tiles.
                    if ( chainList != null )
                    {
                        tileList.add( board[k] );
                    }
                }

                i += j - 1;

                // If the set is non-empty, add the line to the chain.
                if ( chainList != null )
                {
                    chainList.add( Line.newInstance( tileList ) );
                }
            }

        } // end for

        // Return the line count.
        return lineCount;
    }

    /**
     * Searches for all matches in the Y-direction and returns a set
     * with the indices of the matches.
     * 
     * @param set The linked list that will be filled with indices.
     * @return The number of matches found.
     */
    public int findYMatch(Set<Integer> set, List<Line> chain)
    {
        // The number of matches found.
        int lineCount = 0;

        // Cycle through the board looking for a match in the Y-direction.
        for ( int i = 0; i < cells; i++ )
        {
            // Build up the tiles to build up lines.
            ArrayList<Tile> tiles = new ArrayList<Tile>();

            // Transpose i.
            int ti = ArrayUtil.pseudoTranspose( i, columns, rows );

            // Check to see if there's even enough room for an Y-match.
            if ( rows - (ti / columns) < minimumMatch )
            {
                continue;
            }

            // Make sure there's a tile here.
            if ( board[ti] == null )
            {
                continue;
            }

            // Get the color of this tile.
            TileColor color = board[ti].getColor();

            // Make sure the tile's colour is not locked.
            //if (lockedColorMap.get(color) == true)
            //  continue;

            // See how long we have a match for.
            int j;
            for ( j = 1; j < (rows - (ti / columns)); j++ )
            {
                // Transpose i + j.
                int tij = ArrayUtil.pseudoTranspose( i + j, columns, rows );

                if ( board[tij] == null || board[tij].getColor() != color )
                {
                    break;
                }
            }

            // Check if we have a match.
            if ( j >= minimumMatch )
            {
                CouchLogger.get().recordMessage( this.getClass(), "YMatch of length " + j + " found." );

                lineCount++;

                // Copy all matched locations to the linked list.
                for ( int k = i; k < i + j; k++ )
                {
                    int index = ArrayUtil.pseudoTranspose( k, columns, rows );
                    set.add( new Integer( index ) );

                    // Build up the line of tiles.
                    if ( chain != null )
                    {
                        tiles.add( board[index] );
                    }
                }

                // If the set is non-empty, add the line to the chain.
                if ( chain != null )
                {
                    chain.add( Line.newInstance( tiles ) );
                }

                i += j - 1;
            }




        }

        // Return the number of matches found.
        return lineCount;
    }

    /**
     * Synchronizes the current board array with where the tiles are current
     * are on the board.  Usually called after a refactor so that the board
     * array will accurately reflect the board.
     */
    public void synchronize()
    {
        Arrays.fill( scratchBoard, null );

        for ( int i = 0; i < cells; i++ )
        {
            if ( board[i] != null )
            {
                Tile t = board[i];
                int column = (t.getX() - x) / cellWidth;
                int row = (t.getY() - y) / cellHeight;

                scratchBoard[column + (row * columns)] = board[i];
            }
        }

        // The new number of tiles.
        int newNumberOfTiles = 0;

        // Count the number of tiles on the new board.
        for ( int i = 0; i < cells; i++ )
        {
            if ( scratchBoard[i] != null )
            {
                newNumberOfTiles++;
            }
        }

        // Make sure the tile count hasn't changed.
        if ( newNumberOfTiles != numberOfTiles )
        {
            throw new IllegalStateException( "Expected " + numberOfTiles + ", " + "Found " + newNumberOfTiles + "." );
        }

        // Trade-sies!
        Tile[] swapBoard = board;
        board = scratchBoard;
        scratchBoard = swapBoard;
    }

    /**
     * This method is used to re-add all the tiles to the layer manager if they
     * are not already there.  It is principally used for restoring an old board
     * in the manager <pre>loadState()</pre> method.
     */
    private void layerize()
    {
        for ( Tile tile : board )
        {
            if ( tile != null && layerMan.contains( tile, Layer.TILE ) == false )
            {
                tile.setVisible( visible );
                layerMan.add( tile, Layer.TILE );
            }
        }
    }

    /**
     * TODO Documentation.
     * 
     * @param direction
     * @param speed
     * @param gravity
     */
    public List<IAnimation> startShift(final Direction direction,
            final int speed, final int gravity)
    {
        // The list of new animations made.
        List<IAnimation> animationList = new ArrayList<IAnimation>();

        // The new animation.
        IAnimation a;
        int bound;

        // The v.
        int v = speed;

        switch ( direction )
        {
            case UP:

                for ( int i = 0; i < cells; i++ )
                {
                    if ( board[i] != null )
                    {
//                        board[i].setYMovement(-speed);
//                        board[i].calculateBound(direction,
//                                countTilesInDirection(direction, i));
                        bound = calculateBound( direction,
                                countTilesInDirection( direction, i ) );

                        a = new MoveAnimation.Builder( board[i] ).speed( v ).
                                minY( bound ).theta( 90 ).build();
                        animationList.add( a );
                    }
                }

                break;

            case DOWN:

                for ( int i = 0; i < cells; i++ )
                {
                    if ( board[i] != null )
                    {
//                        board[i].setYMovement(+speed);
//                        board[i].calculateBound(direction,
//                                countTilesInDirection(direction, i));
                        bound = calculateBound( direction,
                                countTilesInDirection( direction, i ) );

                        a = new MoveAnimation.Builder( board[i] ).speed( v ).
                                gravity( 1000 ).maxY( bound ).theta( -90 ).build();
                        animationList.add( a );
                    }
                }

                break;

            case LEFT:

                // Start them moving left.
                for ( int i = 0; i < cells; i++ )
                {
                    if ( board[i] != null )
                    {
//                        board[i].setXMovement(-speed);
//                        board[i].calculateBound(direction,
//                                countTilesInDirection(direction, i));
                        bound = calculateBound( direction,
                                countTilesInDirection( direction, i ) );

                        a = new MoveAnimation.Builder( board[i] ).speed( v ).
                                minX( bound ).theta( 180 ).build();
                        animationList.add( a );
                    }
                }

                break;

            case RIGHT:

                // Start them moving left.
                for ( int i = 0; i < cells; i++ )
                {
                    if ( board[i] != null )
                    {
//                        board[i].setXMovement(+speed);
//                        board[i].calculateBound(direction,
//                                countTilesInDirection(direction, i));
                        bound = calculateBound( direction,
                                countTilesInDirection( direction, i ) );

                        a = new MoveAnimation.Builder( board[i] ).speed( v ).
                                maxX( bound ).theta( 0 ).build();
                        animationList.add( a );
                    }
                }

                break;

            default:
                throw new AssertionError();
        }

        return animationList;
    }

    /**
     * A convience method for starting a shift in the vertical direction of the
     * currently set gravity.
     * 
     * @param speed
     * @param gravity
     */
    public List<IAnimation> startVerticalShift(int speed, int gravity)
    {
        if ( speed == 0 )
        {
            throw new IllegalArgumentException( "Speed must be > 0" );
        }

        if ( gravity < 0 )
        {
            throw new IllegalArgumentException( "Gravity must be >= 0" );
        }

        if ( this.gravityDirection.contains( Direction.DOWN ) )
        {
            return startShift( Direction.DOWN, speed, gravity );
        }
        else
        {
            return startShift( Direction.UP, speed, gravity );
        }
    }

    /**
     * A convience method for starting a horizontal shift in the direction of 
     * the currently set gravity.
     * 
     * @param speed
     */
    public List<IAnimation> startHorizontalShift(final int speed)
    {
        if ( speed == 0 )
        {
            throw new IllegalArgumentException( "Speed must be > 0" );
        }


        if ( gravityDirection.contains( Direction.LEFT ) )
        {
            return startShift( Direction.LEFT, speed, 0 );
        }
        else
        {
            return startShift( Direction.RIGHT, speed, 0 );
        }
    }

    /**
     * Moves all currently moving tiles.
     * @returns True if there is still more moving to happen.
     */
//	public boolean moveAll(long delta)
//	{
//		// Set to true if there are more movement to happen.
//		boolean moreMovement = false;
//		
//		for (int i = 0; i < cells; i++)		
//			if (board[i] != null)
//			{
//				board[i].move(delta);
//				if (board[i].getXMovement() != 0 
//						|| board[i].getYMovement() != 0)
//					moreMovement = true;
//			}
//		
//        // Dirty board.
//        setDirty(true);
//        
//		return moreMovement;
//	}    
    /**
     * Counts all the tiles that are under the tile at the specified
     * index.
     *
     * For example, if we had a 3x3 board like this:
     *
     * 012 .X.
     * 345 XX.
     * 678 .XX
     *
     * where "X" is a tile and "." is an empty space, then calling
     * this method on index 1 would return 2.
     *
     * @param index
     * @return
     */
    public int countTilesInDirection(Direction direction, int index)
    {
        // Sanity check.
        assert (index >= 0 && index < cells);

        // The current column and row.
        int column = index % columns;
        int row = index / columns;

        // The tile count.
        int count = 0;

        switch ( direction )
        {
            case UP:

                // If we're at the top row, return 0.
                if ( row == 0 )
                {
                    return 0;
                }

                // Cycle through the column rows, counting tiles.
                for ( int j = row - 1; j >= 0; j-- )
                {
                    if ( getTile( column, j ) != null )
                    {
                        count++;
                    }
                }

                break;

            case DOWN:

                // If we're at the bottom row, return 0.
                if ( row == rows - 1 )
                {
                    return 0;
                }

                // Cycle through the column rows, counting tiles.
                for ( int j = row + 1; j < rows; j++ )
                {
                    if ( getTile( column, j ) != null )
                    {
                        count++;
                    }
                }

                break;

            case LEFT:

                // If we're at the bottom row, return 0.
                if ( column == 0 )
                {
                    return 0;
                }

                // Cycle through the column rows, counting tiles.
                for ( int i = column - 1; i >= 0; i-- )
                {
                    if ( getTile( i, row ) != null )
                    {
                        count++;
                    }
                }

                break;

            case RIGHT:

                // If we're at the bottom row, return 0.
                if ( column == columns - 1 )
                {
                    return 0;
                }

                // Cycle through the column rows, counting tiles.
                for ( int i = column + 1; i < columns; i++ )
                {
                    if ( getTile( i, row ) != null )
                    {
                        count++;
                    }
                }

                break;

            default:
                throw new IllegalStateException( "Unknown direction." );
        }

        // Return the count.
        return count;
    }

    public int calculateBound(Direction direction, int tileCount)
    {
        switch ( direction )
        {
            case UP:

                return tileCount * getCellHeight();

            case DOWN:

                return getY() + getHeight() - ((tileCount + 1) * getCellHeight());

            case LEFT:

                return getX() + (tileCount * getCellWidth());

            case RIGHT:

                return getX() + getWidth() - ((tileCount + 1) * getCellWidth());

            default:
                throw new AssertionError();
        }
    }

    public void addTile(final int index, final Tile t)
    {
        // Sanity check.
        assert index >= 0 && index < cells;
        assert t != null;

        // Make sure the tile is located properly.
        t.setPosition( x + (index % columns) * cellWidth,
                y + (index / columns) * cellHeight );

        // If we're overwriting a tile, remove it first.
        if ( getTile( index ) != null )
        {
            removeTile( index );
        }

        // Increment the item count.
        if ( t.getType() != TileType.NORMAL )
        {
            Item item = itemMan.getItemOrMultiplier( t.getType() );

            if ( item == null )
            {
                CouchLogger.get().recordMessage( this.getClass(), "Missing type was " + t.
                        getType() );
            }

            item.incrementCurrentAmount();
            CouchLogger.get().recordMessage( this.getClass(), item.getTileType() + " has " + item.
                    getCurrentAmount() + " instances." );
        }

        // Set the tile.
        board[index] = t;

        switch ( t.getType() )
        {
            case NORMAL:
                break;

            case X2:
            case X3:
            case X4:

                CouchLogger.get().recordMessage( this.getClass(), "Multiplier added." );
                this.incrementNumberOfMultipliers();

                break;

            case ROCKET:
            case GRAVITY:
            case BOMB:
            case STAR:

                CouchLogger.get().recordMessage( this.getClass(), "Item added." );
                this.incrementNumberOfItems();

                break;

            default:
                throw new IllegalStateException( "Unhandled tile type." );
        }

        // Increment tile count.
        numberOfTiles++;

        // Set the tile visibility to that of the board.
        t.setVisible( visible );

        // Add the tile to the bottom layer too.        
        layerMan.add( t, Layer.TILE );

        // Dirty board.
        setDirty( true );
    }

    public void addTile(final int column, final int row, final Tile t)
    {
        addTile( row * columns + column, t );
    }

    /**
     * Create a new a tile at the specified index using the given class and
     * color.  The new tile is also returned.
     * 
     * @param index
     * @param type
     * @param color
     * @return
     */
    public Tile createTile(final int index, final TileType type,
            final TileColor color)
    {
        // Sanity check.
        assert (index >= 0 && index < cells);
        assert (type != null);

        // The new tile.
        int tx = x + (index % columns) * cellWidth;
        int ty = y + (index / columns) * cellHeight;
        Tile t = TileHelper.makeTile( type, color, tx, ty );

        // Add the tile.
        addTile( index, t );

        // Return the tile.
        return t;
    }

    public Tile createTile(final int column, final int row,
            final TileType type, final TileColor color)
    {
        return createTile( row * columns + column, type, color );
    }

    public Tile createTile(final int index, final TileType type)
    {
        return createTile( index, type,
                TileColor.getRandomColor( getNumberOfColors() ) );
    }

    public Tile createTile(final int column, final int row,
            final TileType type)
    {
        return createTile( row * columns + column, type );
    }

    /**
     * Replace a tile with a new tile of the same colour.
     * 
     * @param index
     * @param type
     * @return The new tile.
     */
    public Tile replaceTile(int index, TileType type)
    {
        // Remove the old, insert the new.       
        TileColor color = getTile( index ).getColor();
        this.removeTile( index );
        return this.createTile( index, type, color );
    }

    /**
     * Replace a tile with a new tile of the same colour.
     * 
     * @param index
     * @param type
     * @return The new tile.
     */
    public Tile replaceTile(int index, TileColor color)
    {
        // Remove the old, insert the new.       
        TileType type = this.getTile( index ).getType();
        this.removeTile( index );
        return this.createTile( index, type, color );
    }

    public Tile replaceTile(Tile t, TileColor color)
    {
        // Remove the old, insert the new.       
        TileType type = t.getType();
        int index = getIndex( t );
        this.removeTile( index );
        return this.createTile( index, type, color );
    }

    /**
     * Replace a tile with a pre-made tile.
     * 
     * @param index
     * @param tile
     */
    public void replaceTile(int index, Tile tile)
    {
        this.removeTile( index );
        this.addTile( index, tile );
    }

    public int getIndex(Tile t)
    {
        for ( int i = 0; i < board.length; i++ )
        {
            Tile testTile = getTile( i );

            if ( testTile != null )
            {
                if ( t.equals( testTile ) )
                {
                    return i;
                }
            } // end if
        } // end for

        return -1;
    }

    public int getIndexFromPosition(ImmutablePosition pos)
    {
        int col = (pos.getX() - this.x) / this.cellWidth;
        int row = (pos.getY() - this.y) / this.cellHeight;

        return row * this.columns + col;
    }

    public void removeTile(Tile t)
    {
        int i = getIndex( t );
        assert (i != -1);

        removeTile( i );
    }

    public void removeTile(final int index)
    {
        // Sanity check.
        assert (index >= 0 && index < cells);

        // Get the tile.
        Tile t = getTile( index );

        // If the tile does not exist, throw an exception.
        if ( t == null )
        {
            throw new NullPointerException( "No tile at that index." );
        }

        // Decrement the item count.
        // Increment the item count.
        if ( t.getType() != TileType.NORMAL )
        {
            Item item = itemMan.getItemOrMultiplier( t.getType() );
            item.decrementCurrentAmount();
            CouchLogger.get().recordMessage( this.getClass(), item.getTileType() + " has " + item.
                    getCurrentAmount() + " instances." );
        }

        switch ( t.getType() )
        {
            case NORMAL:
                break;

            case X2:
            case X3:
            case X4:

                CouchLogger.get().recordMessage( this.getClass(), "Multiplier removed." );
                this.decrementNumberOfMultipliers();

                break;

            case ROCKET:
            case GRAVITY:
            case BOMB:
            case STAR:

                CouchLogger.get().recordMessage( this.getClass(), "Item removed." );
                this.decrementNumberOfItems();

                break;

            default:
                throw new IllegalStateException( "Unhandled tile type." );
        }

        // Remove from layer manager.
        if ( layerMan.contains( t, Layer.TILE ) )
        {
            layerMan.remove( t, Layer.TILE );
        }

        // Remove the tile.
        board[index] = null;

        // Remove the animation.        
        animationMan.remove( t.getAnimation() );

        // Decrement tile counter.
        numberOfTiles--;

        // Dirty board.
        setDirty( true );
    }

    public void removeTile(final int column, final int row)
    {
        // Sanity check.
        assert (column >= 0 && column < columns);
        assert (row >= 0 && row < rows);

        // Passthrough.
        removeTile( column + (row * columns) );
    }

    public void removeTiles(final Set indexSet)
    {
        for ( Iterator it = indexSet.iterator(); it.hasNext(); )
        {
            removeTile( (Integer) it.next() );
        }
    }

    public Tile getTile(int index)
    {
        // Sanity check.
        assert (index >= 0 && index < cells);

        // Set the tile.
        return board[index];
    }

    public Tile getTile(int column, int row)
    {
        // Make sure we're within parameters.
        if ( column < 0 || column >= columns )
        {
            throw new IndexOutOfBoundsException(
                    "Column out of bounds: " + column + "." );
        }

        if ( row < 0 || row >= rows )
        {
            throw new IndexOutOfBoundsException(
                    "Row out of bounds: " + row + "." );
        }

        return getTile( column + (row * columns) );
    }

    public EntityGroup getTileRange(int index1, int index2)
    {
        assert index1 >= 0 && index1 < cells;
        assert index2 >= index1 && index2 < cells;

        // Calculate the number of possible tiles in the range.
        int length = index2 - index1 + 1;

        // Count the number of tiles in that range.
        int count = 0;

        for ( int i = 0; i < length; i++ )
        {
            if ( getTile( index1 + i ) != null )
            {
                count++;
            }
        }

        AbstractEntity[] entities = new AbstractEntity[count];

        // The array index.
        int index = 0;

        for ( int i = 0; i < length; i++ )
        {
            AbstractEntity e = getTile( index1 + i );

            if ( e != null )
            {
                entities[index++] = e;
            }
        }

        return new EntityGroup( entities );
    }

    public EntityGroup getTiles(List<Integer> indexList)
    {
        assert indexList != null;

        List<Tile> tileList = new ArrayList<Tile>();

        Tile t;
        for ( Integer i : indexList )
        {
            t = getTile( i );
            if ( t != null )
            {
                tileList.add( t );
            }
        }

        return new EntityGroup( tileList );
    }

    public void swapTile(int index1, int index2)
    {
        // Validate parameters.
        assert (index1 >= 0 && index1 < cells);
        assert (index2 >= 0 && index2 < cells);

        Tile t = board[index1];
        board[index1] = board[index2];
        board[index2] = t;

        if ( board[index1] != null )
        {
            board[index1].setX( x + (index1 % columns) * cellWidth );
            board[index1].setY( y + (index1 / columns) * cellHeight );
        }

        if ( board[index2] != null )
        {
            board[index2].setX( x + (index2 % columns) * cellWidth );
            board[index2].setY( y + (index2 / columns) * cellHeight );
        }
    }

    /**
     * Finds all the tiles between the rocket and the wall that is in the
     * direction the rocket is pointing.
     * @param rocketSet
     * @param affectedSet
     * @param trackerList
     */
    public void processRockets(
            Set<Integer> rocketSet,
            Set<Integer> affectedSet,
            List<TileEffect> trackerList)
    {
        // Clear the set.
        affectedSet.clear();

        int column;
        int row;

        // The list to store the effect tiles in.
        List<Tile> list = new ArrayList<Tile>();

        for ( Integer rocketIndex : rocketSet )
        {
            // Extract column and row.
            column = rocketIndex % columns;
            row = rocketIndex / columns;

            // Depending on the direction, collect the appropriate tiles.            
            final Tile t = getTile( rocketIndex );
            if ( t.getType() != TileType.ROCKET )
            {
                throw new IllegalArgumentException( "Rocket set contained non-rocket" );
            }
            final RocketTile rocket = (RocketTile) t;
            final RocketTile.Direction dir = rocket.getDirection();

            int index;

            switch ( dir )
            {
                case UP:

                    //Util.handleWarning("Dir is up!", Thread.currentThread());

                    for ( int j = 0; j <= row; j++ )
                    {
                        index = column + j * columns;

                        if ( getTile( index ) != null )
                        {
                            affectedSet.add( index );
                            list.add( getTile( index ) );
                        }
                    }

                    break;

                case DOWN:

                    //Util.handleWarning("Dir is down!", Thread.currentThread());

                    for ( int j = row; j < rows; j++ )
                    {
                        index = column + j * columns;

                        if ( getTile( index ) != null )
                        {
                            affectedSet.add( index );
                            list.add( getTile( index ) );
                        }
                    }

                    break;

                case LEFT:

                    //Util.handleWarning("Dir is left!", Thread.currentThread());

                    for ( int j = 0; j <= column; j++ )
                    {
                        index = j + row * columns;

                        if ( getTile( index ) != null )
                        {
                            affectedSet.add( index );
                            list.add( getTile( index ) );
                        }
                    }

                    break;

                case RIGHT:

                    //Util.handleWarning("Dir is right!", Thread.currentThread());

                    for ( int j = column; j < columns; j++ )
                    {
                        index = j + row * columns;

                        if ( getTile( index ) != null )
                        {
                            affectedSet.add( index );
                            list.add( getTile( index ) );
                        }
                    }

                    break;
            } // end switch

            // Remove the rocket's instance.
            list.remove( rocket );
            trackerList.add( TileEffect.newInstance( rocket, list ) );
            list.clear();

        } // end for
    }

    /**
     * Finds all the tiles that are the same color as the star tile.
     * 
     * @param starSet
     * @param affectedSet
     * @param trackerList
     */
    public void processStars(
            Set<Integer> starSet,
            Set<Integer> affectedSet,
            List<TileEffect> trackerList)
    {
        // Clear the set.
        affectedSet.clear();

        // The list to store the effect tiles in.
        List<Tile> list = new ArrayList<Tile>();

        for ( Integer starIndex : starSet )
        {
            // Determine the colour of the star.
            if ( getTile( starIndex ).getType() != TileType.STAR )
            {
                throw new IllegalArgumentException( "Non-star tile passed" );
            }

            StarTile starTile = (StarTile) getTile( starIndex );

            // Look for that colour and add it to the affected set.
            for ( int i = 0; i < cells; i++ )
            {
                Tile tile = getTile( i );

                if ( tile == null )
                {
                    continue;
                }

                if ( tile.getColor() == starTile.getColor() )
                {
                    affectedSet.add( i );
                    list.add( tile );
                }
            }

            // Remove the star's instance.
            list.remove( starTile );
            trackerList.add( TileEffect.newInstance( starTile, list ) );
            list.clear();
        }
    }

    /**
     * Feeds all the bombs in the bomb processor and then returns those results
     * in the cleared out affected set parameter.
     * @param bombTileSet
     * @param affectedSet
     * @param trackerList
     */
    public void processBombs(
            Set<Integer> bombSet,
            Set<Integer> affectedSet,
            List<TileEffect> trackerList)
    {
        // A list of tiles affected by the blast.
        affectedSet.clear();

        // Gather affected tiles.
        for ( int bombIndex : bombSet )
        {
            affectedSet.addAll( this.processBomb( bombIndex, trackerList ) );
        }
    }

    /**
     * Determines where tiles are affected by the bomb explosion.
     * @param bombIndex
     * @param trackerList
     * @return The set of indices (including the bomb) affected by the bomb.
     */
    private Set<Integer> processBomb(final int bombIndex, List<TileEffect> trackerList)
    {
        // List of additional bomb tiles.
        Set<Integer> affectedSet = new HashSet<Integer>();

        // The list of tiles to be tracked.
        List<Tile> list = new ArrayList<Tile>();

        // Return if bomb is null.
        Tile bombTile = getTile( bombIndex );
        if ( bombTile == null )
        {
            return null;
        }

        // Determine affected tiles.
        for ( int j = -1; j < 2; j++ )
        {
            for ( int i = -1; i < 2; i++ )
            {
                if ( (bombIndex % columns) + i >= 0 && (bombIndex % columns) + i < this.
                        getColumns() && (bombIndex / columns) + j >= 0 && (bombIndex / columns) + j < this.
                        getRows() )
                {
                    if ( getTile( bombIndex % columns + i, bombIndex / columns + j ) != null )
                    {
                        int index = bombIndex + i + (j * columns);
                        affectedSet.add( index );
                        list.add( getTile( index ) );
                    }
                }
            } // end for i
        } // end for j

        // Add all affected tiles to the tracker list.
        list.remove( bombTile );
        trackerList.add( TileEffect.newInstance( bombTile, list ) );

        // Pass back affected tiles.
        return affectedSet;
    }

    /**
     * Scans the tile set for specified tile and places them in a passed item 
     * set.
     * 
     * @param tileType The type of tile to scan for.
     * @param tileSet  The tile set to scan in.
     * @param foundSet The set to store the found tiles in (may be null).
     * @return The number of tiles of that type found.
     */
    public int scanFor(TileType tileType,
            Set<Integer> tileSet,
            Set<Integer> foundSet)
    {
        if ( tileType == null )
        {
            throw new NullPointerException( "Tile type is null" );
        }

        if ( tileSet == null )
        {
            throw new NullPointerException( "Tile set is null" );
        }

        // The number of items found.
        int count = 0;

        for ( Integer index : tileSet )
        {
            if ( getTile( index ).getType() == tileType )
            {
                count++;
                if ( foundSet != null )
                {
                    foundSet.add( index );
                }
            }
        } // end for

        return count;
    }

    /**
     * Animates the showing of the board.
     *      
     * @param type The type of animation to use.
     * @return An animation that can be checked for doneness.
     */
    public IAnimation animateShow(AnimationType type)
    {
        // Animate based on the type.
        switch ( type )
        {
            case ROW_FADE:
                return animateRowFadeIn();

            case SLIDE_FADE:
                return animateSlideFadeIn();

            default:
                throw new AssertionError();
        }
    }

    private IAnimation animateRowFadeIn()
    {
        // The amount of delay between each row.
        int wait = 0;
        int deltaWait = SettingsManager.get().getInt( Key.ANIMATION_ROWFADE_WAIT );
        int duration = SettingsManager.get().getInt( Key.ANIMATION_ROWFADE_DURATION );

        // True if a tile was found this row.
        boolean tileFound = false;

        // Count the number of tiles.
        int tileCount = 0;

        // Add the animations.
        for ( int i = 0; i < cells; i++ )
        {
            Tile t = getTile( i );

            if ( t != null )
            {
                t.setOpacity( 0 );
                IAnimation a = new FadeAnimation.Builder( FadeAnimation.Type.IN, t ).
                        wait( wait ).duration( duration ).build();

                t.setAnimation( a );
                animationMan.add( a );

                tileFound = true;
                tileCount++;
            }

            if ( tileFound == true && (i + 1) % columns == 0 )
            {
                tileFound = false;
                wait += deltaWait;
            }
        }

        // If there are any tiles, there at least must be a tile in the bottom
        // left corner.                
        if ( tileCount > 0 )
        {
            for ( int i = cells - 1; i >= 0; i-- )
            {
                if ( getTile( i ) != null )
                {
                    return getTile( i ).getAnimation();
                }
            }
            throw new IllegalStateException( "There are no tiles on the board." );
        }
        else
        {
            return null;
        }
    }

    /**
     * Slides the board on the screen and fades it in as it does so.
     * 
     * @return An animation object that can be tested for doneness.
     */
    private IAnimation animateSlideFadeIn()
    {
        // The settings manager.
        SettingsManager settingsMan = SettingsManager.get();

        // Count the number of tiles.
        int tileCount = 0;

        // The animation variables that will be used.
        IAnimation a1 = null;
        IAnimation a2 = null;

        // Get all the tiles on the board.
        for ( int i = 0; i < cells; i++ )
        {
            // Get the tile.
            final Tile tile = getTile( i );

            // Get the row.
            int row = i / columns;

            if ( tile != null )
            {
                // Make a copy and hide the original.                
                final Tile t = new Tile( tile );
                tile.setVisible( false );
                layerMan.add( t, Layer.TILE );

                // Count it.
                tileCount++;

                int fadeWait = settingsMan.getInt( Key.ANIMATION_SLIDEFADE_FADE_WAIT );
                int fadeDuration = settingsMan.getInt( Key.ANIMATION_SLIDEFADE_FADE_DURATION );

                // Create the animation.
                t.setOpacity( 0 );
                a1 = new FadeAnimation.Builder( FadeAnimation.Type.IN, t ).wait( fadeWait ).
                        duration( fadeDuration ).build();

                a1.addAnimationListener( new AnimationAdapter()
                {
                    @Override
                    public void animationFinished()
                    {
                        layerMan.remove( t, Layer.TILE );
                        tile.setVisible( true );
                    }

                } );

                // Determine the theta.
                int theta = 180 * ((row + 1) % 2);

                // The min and max x values.
                int minX = Integer.MIN_VALUE;
                int maxX = Integer.MAX_VALUE;

                // If the theta is facing right, move the copy that direction.
                if ( theta == 0 )
                {
                    t.translate( -(int) (500.0 * 0.15), 0 );
                    maxX = tile.getX();
                }
                else if ( theta == 180 )
                {
                    t.translate( (int) (500.0 * 0.15), 0 );
                    minX = tile.getX();
                }
                else
                {
                    throw new AssertionError( "Angle should only be 0 or 180." );
                }

                int moveWait = settingsMan.getInt( Key.ANIMATION_SLIDEFADE_MOVE_WAIT );
                int moveSpeed = settingsMan.getInt( Key.ANIMATION_SLIDEFADE_MOVE_SPEED );

                a2 = new MoveAnimation.Builder( t ).minX( minX ).maxX( maxX ).
                        wait( moveWait ).theta( theta ).speed( moveSpeed ).build();

                // Add them to the animation manager.
                t.setAnimation( a1 );
                animationMan.add( a1 );
                animationMan.add( a2 );
            }
        }

        // If there are any tiles, there at least must be a tile in the bottom
        // left corner.
        if ( tileCount > 0 )
        {
            return a1;
        }
        else
        {
            return null;
        }
    }

    /**
     * Animates the hiding of the board.
     *      
     * @param type The type of animation to use.
     * @return An animation that can be checked for doneness.
     */
    public IAnimation animateHide(AnimationType type)
    {
        // Animate based on the type.
        switch ( type )
        {
            case ROW_FADE:
                return animateRowFadeOut();

            case SLIDE_FADE:
                return animateSlideFadeOut();

            default:
                throw new AssertionError();
        }
    }

    /**
     * Create an animation that fades each row slowly, starting from the
     * top to the bottom.
     * 
     * @return An animation object that can be tested for doneness.
     */
    private IAnimation animateRowFadeOut()
    {
        // The amount of delay between each row.
        int wait = 0;
        int deltaWait = SettingsManager.get().getInt( Key.ANIMATION_ROWFADE_WAIT );
        int duration = SettingsManager.get().getInt( Key.ANIMATION_ROWFADE_DURATION );

        // True if a tile was found this row.
        boolean tileFound = false;

        // Count the number of tiles.
        int tileCount = 0;

        // Add the animations.
        for ( int i = 0; i < cells; i++ )
        {
            Tile t = getTile( i );

            if ( t != null )
            {
                IAnimation a = new FadeAnimation.Builder( FadeAnimation.Type.OUT, t ).
                        wait( wait ).duration( duration ).build();
                t.setAnimation( a );
                animationMan.add( a );

                tileFound = true;
                tileCount++;
            }

            if ( tileFound == true && (i + 1) % columns == 0 )
            {
                tileFound = false;
                wait += deltaWait;
            }
        }

        // If there are any tiles, there at least must be a tile in the bottom
        // left corner.
        if ( tileCount > 0 )
        {
            for ( int i = cells - 1; i >= 0; i-- )
            {
                if ( getTile( i ) != null )
                {
                    return getTile( i ).getAnimation();
                }
            }
            throw new IllegalStateException( "There are no tiles on the board." );
        }
        else
        {
            return null;
        }
    }

    /**
     * Slides the board off the screen and fades it as it does so.
     * 
     * ---->
     * <----
     * ---->
     * <----
     * 
     * @return An animation object that can be tested for doneness.
     */
    private IAnimation animateSlideFadeOut()
    {
        // Count the number of tiles.
        int tileCount = 0;

        // The animation variables that will be used.
        IAnimation a1 = null;
        IAnimation a2 = null;

        // Get all the tiles on the board.
        for ( int i = 0; i < cells; i++ )
        {
            // Get the tile.
            Tile tile = getTile( i );

            // Get the row.
            int row = i / columns;

            if ( tile != null )
            {
                // Make a copy and hide the original.                
                final Tile t = new Tile( tile );
                tile.setVisible( false );
                layerMan.add( t, Layer.TILE );

                // Count it.
                tileCount++;

                // The settings manager.
                SettingsManager settingsMan = SettingsManager.get();

                int fadeWait = settingsMan.getInt( Key.ANIMATION_SLIDEFADE_FADE_WAIT );
                int fadeDuration = settingsMan.getInt( Key.ANIMATION_SLIDEFADE_FADE_DURATION );
                int moveWait = settingsMan.getInt( Key.ANIMATION_SLIDEFADE_MOVE_WAIT );
                int moveDuration = settingsMan.getInt( Key.ANIMATION_SLIDEFADE_MOVE_DURATION );
                int moveSpeed = settingsMan.getInt( Key.ANIMATION_SLIDEFADE_MOVE_SPEED );

                // Create the animation.
                a1 = new FadeAnimation.Builder( FadeAnimation.Type.OUT, t ).wait( fadeWait ).
                        duration( fadeDuration ).build();

                a2 = new MoveAnimation.Builder( t ).wait( moveWait ).duration( moveDuration ).
                        theta( 180 * (row % 2) ).speed( moveSpeed ).build();

                a1.addAnimationListener( new AnimationAdapter()
                {
                    @Override
                    public void animationFinished()
                    {
                        layerMan.remove( t, Layer.TILE );
                    }

                } );

                // Add them to the animation manager.
                t.setAnimation( a1 );
                animationMan.add( a1 );
                animationMan.add( a2 );
            }
        }

        // If there are any tiles, there at least must be a tile in the bottom
        // left corner.
        if ( tileCount > 0 )
        {
            return a1;
        }
        else
        {
            return null;
        }
    }

    /**
     * Determines the centrepoint of a group of tiles.  Used for determine
     * position of SCT.
     * 
     * @param indexSet
     * @return
     */
    public ImmutablePosition determineCenterPoint(final Set<Integer> indexSet)
    {
        // The furthest left, right, up and down locations.
        int l = Integer.MAX_VALUE;
        int r = 0;
        int u = Integer.MAX_VALUE;
        int d = 0;

        // The x and y coordinate of the centre of the tiles.
        int cx, cy;

        // Determine centre of tiles.
        for ( Integer index : indexSet )
        {
            Tile t = getTile( index );

            if ( t == null )
            {
                CouchLogger.get().recordWarning( this.getClass(), "Tile was null: " + index );
            }

            if ( t.getX() < l )
            {
                l = t.getX();
            }

            if ( t.getX() + t.getWidth() > r )
            {
                r = t.getX() + t.getWidth();
            }

            if ( t.getY() < u )
            {
                u = t.getY();
            }

            if ( t.getY() + t.getHeight() > d )
            {
                d = t.getY() + t.getHeight();
            }
        }

        // Assigned centre.
        cx = l + (r - l) / 2;
        cy = u + (d - u) / 2;

        // Return centerpoint.
        return new ImmutablePosition( cx, cy );
    }

    /**
     * Prints board to console (for debugging purposes).
     */
    public void print()
    {
        for ( int i = 0; i < board.length; i++ )
        {
            if ( board[i] == null )
            {
                System.out.print( "." );
            }
            else
            {
                System.out.print( "X" );
            }

            if ( i % columns == columns - 1 )
            {
                System.out.println();
            }
        }

        System.out.println();
    }

    //--------------------------------------------------------------------------
    // Getters and Setters
    //--------------------------------------------------------------------------
    /**
     * @return The cellWidth.
     */
    public int getCellWidth()
    {
        return cellWidth;
    }

    /**
     * @return The cellHeight.
     */
    public int getCellHeight()
    {
        return cellHeight;
    }

    /**
     * Gets the width.
     * @return The width.
     */
    public int getWidth()
    {
        return width;
    }

    /**
     * Gets the height.
     * @return The height.
     */
    public int getHeight()
    {
        return height;
    }

    /**
     * Gets the x.
     * @return The x.
     */
    public int getX()
    {
        return x;
    }

    /**
     * Gets the y.
     * @return The y.
     */
    public int getY()
    {
        return y;
    }

    public int getColumns()
    {
        return columns;
    }

    public int getRows()
    {
        return rows;
    }

    public int getNumberOfCells()
    {
        return cells;
    }

    public void insertItemRandomly(TileType type)
    {
        Set<TileType> typeSet = EnumSet.of( TileType.NORMAL );
        Set<TileColor> colorSet = EnumSet.allOf( TileColor.class );

        // Get a random tile location.
        List<Integer> indexSet = this.getTileIndices( typeSet, colorSet );

        // If there are no available locations, do nothing.
        if ( indexSet.size() == 0 )
        {
            return;
        }

        // Get a random index.
        Collections.shuffle( indexSet, ArrayUtil.random );
        int index = indexSet.get( 0 );

        // Replace the tile.
        replaceTile( index, type );
    }

    public int getNumberOfItems()
    {
        return this.numberOfItems;
    }

    public void setNumberOfItems(final int numberOfItems)
    {
        this.numberOfItems = numberOfItems;
    }

    public void decrementNumberOfItems()
    {
        this.numberOfItems--;
    }

    public void incrementNumberOfItems()
    {
        this.numberOfItems++;
    }

    public int getNumberOfMultipliers()
    {
        return this.numberOfMultipliers;
    }

    public void setNumberOfMultipliers(final int numberOfMultipliers)
    {
        this.numberOfMultipliers = numberOfMultipliers;
    }

    public void decrementNumberOfMultipliers()
    {
        this.numberOfMultipliers--;
    }

    public void incrementNumberOfMultipliers()
    {
        this.numberOfMultipliers++;
    }

    public int getNumberOfTiles()
    {
        int counter = 0;
        for ( int i = 0; i < this.cells; i++ )
        {
            if ( this.getTile( i ) != null )
            {
                counter++;
            }
        }

        return counter;
    }

    public List<Integer> getTileIndices(
            Set<TileType> typeFilter,
            Set<TileColor> colorFilter)
    {
        assert typeFilter != null;
        assert colorFilter != null;

        List<Integer> indexList = new ArrayList<Integer>();
        Tile tile;

        for ( int i = 0; i < this.cells; i++ )
        {
            tile = this.getTile( i );
            if ( tile != null && typeFilter.contains( tile.getType() ) && colorFilter.
                    contains( tile.getColor() ) )
            {
                indexList.add( i );
            }
        }

        return indexList;
    }

    public int getNumberOfColors()
    {
        return numberOfColors;
    }

    public void setNumberOfColors(int numberOfColors)
    {
        this.numberOfColors = numberOfColors;
    }

    public EnumSet<Direction> getGravity()
    {
        return gravityDirection;
    }

    public void setGravity(EnumSet<Direction> gravity)
    {
        this.gravityDirection = gravity;
    }

    public boolean isVisible()
    {
        return visible;
    }

    public void setVisible(boolean visible)
    {
        CouchLogger.get().recordMessage( this.getClass(), "Board visible: " + visible + "." );

        for ( Tile tile : board )
        {
            if ( tile != null )
            {
                tile.setVisible( visible );
            }
        }

        this.visible = visible;
    }

    public void setDirty(boolean dirty)
    {
        this.dirty = dirty;
    }

    public boolean isDirty()
    {
        return dirty;
    }

    public ImmutableRectangle getShape()
    {
        return shape;
    }

    public int asColumn(int index)
    {
        return (index % columns);
    }

    public int asRow(int index)
    {
        return (index / columns);
    }

    /**
     * Set the locked status of a tile color.
     * 
     * @param color
     * @param locked
     */
    public void setColorLocked(TileColor color, boolean locked)
    {
        lockedColorMap.put( color, locked );
    }

    /**
     * Checks to see if a tile color is locked or not.
     * 
     * @param color
     * @return
     */
    public boolean isColorLocked(TileColor color)
    {
        return lockedColorMap.get( color );
    }

    /**
     * Returns the relative position of the two tiles, relative to a.
     * For instance, if Direction.LEFT was returned, then that would mean
     * that "a is left of b".
     * 
     * @param a
     * @param b
     * @return
     */
    public Direction relativeColumnPosition(int a, int b)
    {
        if ( asColumn( a ) > asColumn( b ) )
        {
            return Direction.RIGHT;
        }
        else if ( asColumn( a ) < asColumn( b ) )
        {
            return Direction.LEFT;
        }
        else
        {
            return Direction.NONE;
        }
    }

    /**
     * Returns the relative position of the two tiles, relative to a.
     * For instance, if Direction.UP was returned, then that would mean
     * that "a is above b".
     * 
     * @param a
     * @param b
     * @return
     */
    public Direction relativeRowPosition(int a, int b)
    {
        if ( asRow( a ) > asRow( b ) )
        {
            return Direction.DOWN;
        }
        else if ( asRow( a ) < asRow( b ) )
        {
            return Direction.UP;
        }
        else
        {
            return Direction.NONE;
        }
    }

    public void keyPressed(KeyEvent event)
    {
        switch ( event.getChar() )
        {
//            case 'r':
//                insertItemRandomly(TileType.ROCKET);
//                break;
//
//            case 'g':
//                insertItemRandomly(TileType.GRAVITY);
//                break;
//
//            case 'b':
//                insertItemRandomly(TileType.BOMB);
//                break;
//
//            case 's':
//                insertItemRandomly(TileType.STAR);
//                break;      
        }
    }

    public void keyReleased(KeyEvent event)
    {
        // Ignore this.
    }

}
