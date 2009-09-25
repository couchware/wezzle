package ca.couchware.wezzle2d.piece;

import ca.couchware.wezzle2d.util.ArrayUtil;

public abstract class Piece
{
    // ---------------------------------------------------------------------------
    // Constants
    // ---------------------------------------------------------------------------
    public static final int MAX_COLUMNS = 3;
    public static final int MAX_ROWS = 3;

    // ---------------------------------------------------------------------------
    // Instance Attributes
    // ---------------------------------------------------------------------------
    /**
     * The internal name for the piece.
     */
    final protected String name;

    /**
     * The type of the piece.
     */
    final protected PieceType type;

    /**
     * The 3x3 array of what the piece fills up.
     */
    protected Boolean[][] structure;

    /**
     * The number of columns in the piece.
     */
    protected int columns;

    /**
     * The number of rows in the piece.
     */
    protected int rows;

    /**
     * The size of the piece, in tiles.
     */
    final protected int size;

    // ---------------------------------------------------------------------------
    // Constructor
    // ---------------------------------------------------------------------------
    Piece(String name, PieceType type, int size)
    {
        this.name = name;
        this.type = type;
        this.size = size;
    }

    // ---------------------------------------------------------------------------
    // Instance Methods
    // ---------------------------------------------------------------------------
    /**
     * Rotates the piece to the right.
     */
    public void rotateRight()
    {
        // Intentionally blank.  Meant to be overridden by subclass.
    }

    /**
     * Rotate the piece to the left.
     */
    public void rotateLeft()
    {
        // Intentionally blank.  Meant to be overridden by subclass.
    }

    /**
     * Rotate the piece randomly.
     */
    public void rotateRandomly()
    {
        int numberOfRotations = ArrayUtil.random.nextInt( 4 );
        for ( int i = 0; i <= numberOfRotations; i++ )
        {
            this.rotateRight();
        }
    }

    // ---------------------------------------------------------------------------
    // Getters and Setters
    // ---------------------------------------------------------------------------
    /**
     * @return the name
     */
    public String getName()
    {
        return name;
    }

    /**
     * @return the structure
     */
    public Boolean[][] getStructure()
    {
        return structure;
    }

    /**
     * @return the columns
     */
    public int getColumns()
    {
        return columns;
    }

    /**
     * @return the rows
     */
    public int getRows()
    {
        return rows;
    }

    /**
     * @return the size
     */
    public int getSize()
    {
        return size;
    }

    public PieceType getType()
    {
        return type;
    }

}
