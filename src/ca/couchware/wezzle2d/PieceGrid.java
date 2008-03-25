package ca.couchware.wezzle2d;

import ca.couchware.wezzle2d.util.XYPosition;
import java.util.Arrays;

import ca.couchware.wezzle2d.piece.Piece;

/**
 * The piece grid is a 3x3 matrix that has each cell selectively
 * activated depending on what piece is currently loaded into it.
 * 
 * @author cdmckay
 *
 */

public class PieceGrid implements Drawable
{
    /**
     * Path to the piece selector sprite.
     */
    final private String PATH = Game.SPRITES_PATH + "/Selector.png";
    
	/**
	 * The current structure of the piece, representing by a 2D 
	 * boolean array.  Where the array is true, there is a selector.
	 * Where it is false, there is not.
	 */
	private Boolean[][] structure;
	
	/**
	 * A 2D array of sprites.  Each cell corresponds to a cell in the
	 * structure.  Each cell is only drawn if the corresponding structure
	 * cell is true.
	 */
	private Sprite[][] sprites;
    
    /**
     * Whether or not this is visible.
     */
    private boolean visible;
    
    /**
     * The board manager reference.
     */
    final private BoardManager boardMan;
	
    /**
     * The X-coordinate of the top left corner of the piece grid.
     */
    private int x;
    
    /**
     * The Y-coordinate of the top let corner of the piece grid.
     */
    private int y;
    
	/**
	 * The constructor.  Initializes the structure and sprites arrays.
	 * @param path
	 * @param x
	 * @param y
	 */
	public PieceGrid(BoardManager boardMan, int x, int y)
	{
        // Grid is initially visible.
        this.visible = true;
        
        // Set board manager reference.
        this.boardMan = boardMan;
        
        // Set x and y.
        this.x = x;
        this.y = y;
        
		// Create an blank out the structure.
		structure = new Boolean[Piece.MAX_COLUMNS][Piece.MAX_ROWS];
		
		for (int i = 0; i < structure.length; i++)
			Arrays.fill(structure[i], new Boolean(false));
		
		// Load in all the sprites.
		sprites = new Sprite[Piece.MAX_COLUMNS][Piece.MAX_ROWS];
		
		for (int i = 0; i < sprites.length; i++)
			for (int j = 0; j < sprites[0].length; j++)
				sprites[i][j] = ResourceFactory.get().getSprite(PATH);
	}	
	
    /**
     * Load in a piece structure.
     * @param structure
     */
	public void loadStructure(final Boolean[][] structure)
	{
		// Save the new array.
		this.structure = structure;
	}

    public int getX()
    {
        return x;
    }

    public void setX(int x) 
    {
        this.x = x;
    }

    public int getY() 
    {
        return y;
    }

    public void setY(int y) 
    {
        this.y = y;
    }
    
    public XYPosition getXYPosition()
    {
        return new XYPosition(x, y);
    }
    
    /**
     * Draw the piece grid at the predefined location x,y.
     */
	public void draw()
	{
        // Don't draw if we're not visible.
        if (isVisible() == false)
            return;
        
		// Cycle through, drawing only the sprites that should be shown.
        for (int i = 0; i < structure.length; i++)
        {
            for (int j = 0; j < structure[0].length; j++)
            {
				if (structure[i][j] == true)
                {
                    sprites[i][j].draw(x + (i - 1) * boardMan.getCellWidth(),
                            y + (j - 1) * boardMan.getCellHeight());
                } // end if
            } // end for
        } // end for			
	}

    public void setVisible(boolean visible)
    {
        this.visible = visible;
    }

    public boolean isVisible()
    {
        return visible;
    }
}
