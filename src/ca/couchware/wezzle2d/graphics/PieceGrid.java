package ca.couchware.wezzle2d.graphics;

import ca.couchware.wezzle2d.*;
import java.awt.Rectangle;
import java.util.Arrays;
import java.util.EnumSet;

import ca.couchware.wezzle2d.piece.Piece;

/**
 * The piece grid is a 3x3 matrix that has each cell selectively
 * activated depending on what piece is currently loaded into it.
 * 
 * @author cdmckay
 *
 */

public class PieceGrid extends Entity
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
     * The board manager reference.
     */
    final private BoardManager boardMan;	    
    
	/**
	 * The constructor.  Initializes the structure and sprites arrays.
     * 
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
        
        this.x_ = x;
        this.y_ = y;
                
		// Create an blank out the structure.
		structure = new Boolean[Piece.MAX_COLUMNS][Piece.MAX_ROWS];
		
		for (int i = 0; i < structure.length; i++)
			Arrays.fill(structure[i], new Boolean(false));
		
		// Load in all the sprites.
		sprites = new Sprite[Piece.MAX_COLUMNS][Piece.MAX_ROWS];
		
		for (int i = 0; i < sprites.length; i++)
			for (int j = 0; j < sprites[0].length; j++)
				sprites[i][j] = ResourceFactory.get().getSprite(PATH);
        
        // Set the width and height.
        width = boardMan.getCellWidth() * Piece.MAX_COLUMNS;
        height = boardMan.getCellHeight() * Piece.MAX_ROWS;                              
	}	
	
    /**
     * Load in a piece structure.
     * @param structure
     */
	public void loadStructure(final Boolean[][] structure)
	{
		// Save the new array.
		this.structure = structure;
        
        // Set dirty so it will be drawn.        
        setDirty(true);
	}
   
    /**
     * Draw the piece grid at the predefined location x,y.
     */
    @Override
	public void draw()
	{
        // Make current (x,y) the old one.
        x_ = x;
        y_ = y;
        
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

    @Override
    public Rectangle getDrawRect()
    {
        // If the draw rect is null, generate it.
        if (drawRect == null)
        {
            Rectangle rect = new Rectangle(x_, y_, width + 1, height + 1);

            if (x_ != x || y_ != y)
                rect.add(new Rectangle(x, y, width + 1, height + 1));

            rect.translate(-boardMan.getCellWidth(), -boardMan.getCellHeight());
            
            drawRect = rect;
        }

        return drawRect;
    }    

    @Override
    public void setWidth(int width)
    {
        throw new UnsupportedOperationException("You may not adjust this property.");
    }

    @Override
    public void setHeight(int height)
    {
        throw new UnsupportedOperationException("You may not adjust this property.");
    }

    @Override
    public EnumSet<Alignment> getAlignment()
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setAlignment(EnumSet<Alignment> alignment)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
