package ca.couchware.wezzle2d;

import ca.couchware.wezzle2d.util.XYPosition;
import java.awt.Rectangle;
import java.util.Arrays;

import ca.couchware.wezzle2d.piece.Piece;

/**
 * The piece grid is a 3x3 matrix that has each cell selectively
 * activated depending on what piece is currently loaded into it.
 * 
 * @author cdmckay
 *
 */

public class PieceGrid implements Drawable, Positionable
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
     * Is it dirty (i.e. does it need to be redrawn)?
     */
    private boolean dirty;
    
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
    
    private int x_;
    private int y_;
    
    /**
     * The width.
     */
    private int width;
    
    /**
     * The height.
     */
    private int height;
    
//    /**
//     * The draw rectangle.
//     */
//    private Rectangle drawRect;
    
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
        
        // Create the drawing rectangle.
//        this.drawRect = new Rectangle(x, y, width, height);
//        this.drawRect.translate(
//                -boardMan.getCellWidth(),
//                -boardMan.getCellHeight());
        
        // Set dirty so it will be drawn.        
        setDirty(true);
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

    public int getX()
    {
        return x;
    }

    public void setX(int x) 
    {
//        updateDrawRectX(x);
        
        this.x = x;
        
        // Set dirty so it will be drawn.        
        setDirty(true);
    }
    
//    protected void updateDrawRectX(int x)
//    {
//        // Update draw rectangle.
//        drawRect.setBounds(getX(), getY(), width + 1, height + 1);
//        
//        if (x > getX())
//            drawRect.add(x + width + 1, y);
//        else
//            drawRect.add(x, y);                   
//        
//        drawRect.translate(
//                -boardMan.getCellWidth(),
//                -boardMan.getCellHeight());
//    }

    public int getY() 
    {
        return y;
    }

    public void setY(int y) 
    {
//        updateDrawRectY(y);
        
        this.y = y;
        
        // Set dirty so it will be drawn.        
        setDirty(true);
    }
    
//    protected void updateDrawRectY(int y)
//    {
//         // Update draw rectangle.
//        drawRect.setBounds(getX(), getY(), width + 1, height + 1);
//        
//        if (y > getY())
//            drawRect.add(x, y + height + 1);
//        else
//            drawRect.add(x, y);     
//        
//        drawRect.translate(
//                -boardMan.getCellWidth(),
//                -boardMan.getCellHeight());
//    }
    
    public XYPosition getXYPosition()
    {
        return new XYPosition(x, y);
    }
    
    /**
     * Draw the piece grid at the predefined location x,y.
     */
	public void draw()
	{
        // Make current (x,y) the old onee.
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

    public void setVisible(boolean visible)
    {
        this.visible = visible;
        
        // Set dirty so it will be drawn.        
        setDirty(true);
    }

    public boolean isVisible()
    {
        return visible;
    }
    
    public void setDirty(boolean dirty)
    {
        this.dirty = dirty;
    }

    public boolean isDirty()
    {
        return dirty;
    }

    public Rectangle getDrawRect()
    {
        Rectangle rect = new Rectangle(x_, y_, width + 1, height + 1);
        
        if (x_ != x || y_ != y)
            rect.add(new Rectangle(x, y, width + 1, height + 1));
        
        rect.translate(-boardMan.getCellWidth(), -boardMan.getCellHeight());
        
        return rect;
    }
    
    public void resetDrawRect()
    {
        x_ = x;
        y_ = y;
    }

    public void setXYPosition(int x, int y)
    {
        setX(x);
        setY(y);
    }

    public void setXYPosition(XYPosition p)
    {
        setX(p.x);
        setY(p.y);
    }

    public int getWidth()
    {
        return width;
    }

    public void setWidth(int width)
    {
        throw new UnsupportedOperationException("You may not adjust this property.");
    }

    public int getHeight()
    {
        return height;
    }

    public void setHeight(int height)
    {
        throw new UnsupportedOperationException("You may not adjust this property.");
    }

    public int getAlignment()
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setAlignment(int alignment)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
