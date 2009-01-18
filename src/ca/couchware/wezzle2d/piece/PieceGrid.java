package ca.couchware.wezzle2d.piece;

import ca.couchware.wezzle2d.util.IBuilder;
import ca.couchware.wezzle2d.IGraphics;
import ca.couchware.wezzle2d.IWindow;
import ca.couchware.wezzle2d.ResourceFactory;
import ca.couchware.wezzle2d.graphics.AbstractEntity;
import ca.couchware.wezzle2d.graphics.IPositionable.Alignment;
import ca.couchware.wezzle2d.graphics.ISprite;
import ca.couchware.wezzle2d.manager.Settings;
import ca.couchware.wezzle2d.util.NumUtil;
import ca.couchware.wezzle2d.util.SuperColor;
import ca.couchware.wezzle2d.util.Util;
import java.awt.Color;
import java.awt.Rectangle;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;


/**
 * The piece grid is a 3x3 matrix that has each cell selectively
 * activated depending on what piece is currently loaded into it.
 * 
 * @author cdmckay
 *
 */

public class PieceGrid extends AbstractEntity
{       
    /** Path to the piece selector sprite. */
    final private String PATH = Settings.getSpriteResourcesPath() + "/Selector.png";        
    
    /** The render options. */
    public enum RenderMode
    {
        /** Use the selector sprite to draw the grid. */
        SPRITE,
        
        /** Draw the grid using the drawing methods of the underlying renderer. */
        VECTOR            
    }
    
    /** The current render mode */
    private RenderMode renderMode;
    
    public enum AlignmentMode
    {
        /** 
         * Align the piece grid to the full size of the grid.  For example,
         * if we had:
         * <pre>
         * ---
         * -XX
         * -X-
         * </pre>
         * ...and we wanted to align to the MIDDLE | CENTER then the alignment
         * point would be right in the centre of the top-left X.
         */
        TO_FULL_GRID,
        
        /**
         * Align the piece grid to the piece, ignoring empty cells.  For example,
         * if we had:
         * <pre>
         * ---
         * -XX
         * -X-
         * </pre> 
         * ... and we wanted to align to the MIDDLE | CENTER then the alignment
         * point would be in the centre of:
         * <pre>
         * XX
         * X-
         * </pre>
         */
        TO_PIECE
    }
    
    /** The current alignment mode. */
    private AlignmentMode alignmentMode;
    
    /** The game window. */
    private IGraphics gfx;
    
    /** The color of the piece grid, if we're using vector rendering. */
    private Color color;
    
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
	private PieceGrid(Builder builder)
	{
        // Shortcut to the resource factory.
        ResourceFactory factory = ResourceFactory.get();
        
        // Grab the game window.
        this.gfx = factory.getGraphics();
        
        // Grid is initially visible.
        this.visible = true;
       
        // Set x and y.
        this.x  = builder.x;
        this.y  = builder.y;        
        this.x_ = this.x;
        this.y_ = this.y;
        
        // Set the color.
        this.color = builder.color;               
        
        // Set the render mode.
        this.renderMode = builder.renderMode;
                
        // Load in all the sprites.
        if (renderMode == RenderMode.SPRITE)
        {
            spriteArray = new ISprite[Piece.MAX_COLUMNS][Piece.MAX_ROWS];
		
            for (int i = 0; i < spriteArray.length; i++)
                for (int j = 0; j < spriteArray[0].length; j++)
                    spriteArray[i][j] = factory.getSprite(PATH);
        }		
        
		// Create an blank out the structure.
		structure = new Boolean[Piece.MAX_COLUMNS][Piece.MAX_ROWS];
		
		for (int i = 0; i < structure.length; i++)
			Arrays.fill(structure[i], false);				
        
        this.cellWidth  = builder.cellWidth;
        this.cellHeight = builder.cellHeight;
        
        // Set the width and height.
        this.width  = cellWidth  * Piece.MAX_COLUMNS;
        this.height = cellHeight * Piece.MAX_ROWS;           
        
        // Set the alignment mode.
        this.alignment = builder.alignment;
        this.alignmentMode = builder.alignmentMode;
        
        // Calculate the offsets right now if we're aligning to the full grid.
        if (alignmentMode == AlignmentMode.TO_FULL_GRID)
        {
            this.offsetX = determineOffsetX(this.alignment, width);
            this.offsetY = determineOffsetY(this.alignment, height);
        }
	}	
    
    public static class Builder implements IBuilder<PieceGrid>
    {

        final int x;
        final int y;
        final RenderMode renderMode;        
        
        AlignmentMode alignmentMode = AlignmentMode.TO_FULL_GRID;
        EnumSet<Alignment> alignment = EnumSet.of(Alignment.TOP, Alignment.LEFT);
        
        int cellWidth  = 32;
        int cellHeight = 32;       
        Color color = Color.ORANGE;        
        
        public Builder(int x, int y, RenderMode renderMode)
        {
            this.x = x;
            this.y = y;
            this.renderMode = renderMode;
        }
        
        public Builder cellWidth(int val)
        { cellWidth = val; return this; }
        
        public Builder cellHeight(int val)
        { cellHeight = val; return this; }
        
        public Builder color(Color val)
        { color = val; return this; }
        
        public Builder alignmentMode(AlignmentMode val)
        { alignmentMode = val; return this; }
        
        public Builder alignment(EnumSet<Alignment> val)
        { alignment = val; return this; }
        
        public PieceGrid end()
        {
            return new PieceGrid(this);
        }
        
    }
	
    /**
     * Load in a piece structure.
     * @param structure
     */
	public void loadStructure(final Boolean[][] structure)
	{
		// Save the new array.
		this.structure = structure;
        
        // If the alignment mode is relative to the piece, then
        // calculate the new offsets.
        if (this.alignmentMode == AlignmentMode.TO_PIECE)
        {
            int[] wx = new int[structure.length];
            int[] hx = new int[structure[0].length];
            
            for (int i = 0; i < structure.length; i++)
            {
                for (int j = 0; j < structure[0].length; j++)
                {
                    if (structure[i][j])
                    {
                        wx[i] = 1;
                        hx[j] = 1;
                    }
                }
            }                        
                        
            int w = NumUtil.sumIntArray(wx) * cellWidth;
            int h = NumUtil.sumIntArray(hx) * cellHeight;
            
            if (alignment.contains(Alignment.LEFT)
                    || alignment.contains(Alignment.RIGHT)
                    || alignment.contains(Alignment.CENTER))
            {
                this.offsetX = 0;
                
                for (int wi : wx)
                {
                    if (wi == 1) break;
                    this.offsetX -= this.cellWidth;
                }
                
                if (alignment.contains(Alignment.RIGHT))
                {
                    this.offsetX -= w;
                }
                else if (alignment.contains(Alignment.CENTER))
                {
                    this.offsetX -= w / 2;
                }
            }                       
            else
            {
                throw new IllegalStateException("No horizontal alignment assigned!");
            }
            
            if (alignment.contains(Alignment.TOP)
                    || alignment.contains(Alignment.BOTTOM)
                    || alignment.contains(Alignment.MIDDLE))
            {
                this.offsetY = 0;
                
                for (int hi : hx)
                {
                    if (hi == 1) break;
                    this.offsetY -= this.cellHeight;
                }
                
                if (alignment.contains(Alignment.BOTTOM))
                {
                    this.offsetY -= h;
                }
                else if (alignment.contains(Alignment.MIDDLE))
                {
                    this.offsetY -= h / 2;
                }
            }                       
            else
            {
                throw new IllegalStateException("No vertical alignment assigned!");
            }
            
            
        }
        
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
        
		switch (renderMode)
        {
            case SPRITE:
                renderSprite();
                break;
                
            case VECTOR:
                renderVector();
                break;
                
            default: throw new AssertionError();
        }
        
        return true;
	}   
    
    /**
     * Render the piece grid using a sprite.
     */
    private void renderSprite()
    {
        // Cycle through, drawing only the sprites that should be shown.
        for (int i = 0; i < structure.length; i++)
        {
            for (int j = 0; j < structure[0].length; j++)
            {
				if (structure[i][j] == true)
                {
                    spriteArray[i][j].draw(
                                x + offsetX + (i) * cellWidth,
                                y + offsetY + (j) * cellHeight
                            ).end();
                } // end if
            } // end for
        } // end for	
    }

    /**
     * Render the piece grid using vectors.
     */
    private void renderVector()
    {
        // Save the old color and set the new one.
        SuperColor oldColor = gfx.getColor();
        gfx.setColor(this.color);
        
        // Cycle through, drawing only the sprites that should be shown.
        for (int i = 0; i < structure.length; i++)
        {
            for (int j = 0; j < structure[0].length; j++)
            {
				if (structure[i][j] == true)
                {
                    gfx.drawRoundRect(
                            x + offsetX + i * cellWidth, 
                            y + offsetY + j * cellHeight + 1, 
                            cellWidth  - 1, cellHeight - 1,
                            5, 50);
                } // end if
            } // end for
        } // end for	
        
        // Restore the old color.
        gfx.setColor(oldColor);
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
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public void setHeight(int height)
    {
        throw new UnsupportedOperationException("Not supported.");
    }

    public Color getColor()
    {
        return color;
    }

    public void setColor(Color color)
    {
        assert color != null;
        this.color = color;
    }  
    
}
