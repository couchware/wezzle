package ca.couchware.wezzle2d.piece;

import ca.couchware.wezzle2d.graphics.*;
import ca.couchware.wezzle2d.*;
import ca.couchware.wezzle2d.manager.Settings;
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

public class PieceGrid extends AbstractEntity
{
    /**
     * Path to the piece selector sprite.
     */
    final private String PATH = Settings.getSpriteResourcesPath() + "/Selector.png";        
    
	/**
	 * The current structure of the piece, representing by a 2D 
	 * boolean array.  Where the array is true, there is a selector.
	 * Where it is false, there is not.
	 */
	private Boolean[][] structure;
	
    /** The width of one of the cells in the grid. */
    final private int cellWidth;
    
    /** The height of one of the cells in the grid. */
    final private int cellHeight;
    
	/**
	 * A 2D array of sprites.  Each cell corresponds to a cell in the
	 * structure.  Each cell is only drawn if the corresponding structure
	 * cell is true.
	 */
	private ISprite[][] spriteArray;    
    
	/**
	 * The constructor.  Initializes the structure and sprites arrays.
     * 
	 * @param path
	 * @param x
	 * @param y
	 */
	public PieceGrid(int x, int y)
	{
        // Grid is initially visible.
        this.visible = true;
       
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
		spriteArray = new ISprite[Piece.MAX_COLUMNS][Piece.MAX_ROWS];
		
		for (int i = 0; i < spriteArray.length; i++)
			for (int j = 0; j < spriteArray[0].length; j++)
				spriteArray[i][j] = ResourceFactory.get().getSprite(PATH);
        
        this.cellWidth  = spriteArray[0][0].getWidth();
        this.cellHeight = spriteArray[0][0].getHeight();
        
        // Set the width and height.
        width =  cellWidth * Piece.MAX_COLUMNS;
        height =  cellHeight * Piece.MAX_ROWS;                              
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
	public boolean draw()
	{
        // Make current (x,y) the old one.
        x_ = x;
        y_ = y;
        
        // Don't draw if we're not visible.
        if (isVisible() == false)
            return false;
        
		// Cycle through, drawing only the sprites that should be shown.
        for (int i = 0; i < structure.length; i++)
        {
            for (int j = 0; j < structure[0].length; j++)
            {
				if (structure[i][j] == true)
                {
                    spriteArray[i][j].draw(x + (i - 1) * cellWidth,
                            y + (j - 1) * cellHeight).end();
                } // end if
            } // end for
        } // end for	
        
        return true;
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

            rect.translate(-cellWidth, -cellHeight);
            
            drawRect = rect;
        }

        return drawRect;
    }    

    @Override
    public void setWidth(int width)
    {
        throw new UnsupportedOperationException("Not supported..");
    }

    @Override
    public void setHeight(int height)
    {
        throw new UnsupportedOperationException("Not supported..");
    }

    @Override
    public EnumSet<Alignment> getAlignment()
    {
        throw new UnsupportedOperationException("Not supported.");
    }
    
}
