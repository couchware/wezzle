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
import ca.couchware.wezzle2d.util.CouchColor;
import java.awt.Color;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.EnumSet;

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
    final private String SHADOW_PATH = Settings.getSpriteResourcesPath() + "/ShadowSelector.png";

    final private EnumMap<RenderMode, String> pathMap =
            new EnumMap<RenderMode, String>(RenderMode.class);

    /** The render options. */
    public enum RenderMode
    {
        /** Use the selector sprite to draw the grid. */
        SPRITE_LIGHT,
        SPRITE_DARK,
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

    private IWindow win;
    private IGraphics graphics;

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
        this.win = builder.win;
        this.graphics = win.getGraphics();

        // Grid is initially visible.
        this.visible = builder.visible;

        // Set x and y.
        this.x = builder.x;
        this.y = builder.y;
        this.x_ = this.x;
        this.y_ = this.y;

        // Set the color.
        this.color = builder.color;
        this.opacity = builder.opacity;

        // Set the render mode.
        this.renderMode = builder.renderMode;

        this.pathMap.put( RenderMode.SPRITE_LIGHT, PATH);
        this.pathMap.put( RenderMode.SPRITE_DARK, SHADOW_PATH);

        // Load in all the sprites.
        ResourceFactory factory = ResourceFactory.get();
        if ( renderMode == RenderMode.SPRITE_LIGHT
                || renderMode == RenderMode.SPRITE_DARK )
        {
            spriteArray = new ISprite[Piece.MAX_COLUMNS][Piece.MAX_ROWS];

            for ( int i = 0; i < spriteArray.length; i++ )
            {
                for ( int j = 0; j < spriteArray[0].length; j++ )
                {
                    spriteArray[i][j] =
                            factory.getSprite( this.pathMap.get(renderMode) );
                } // end for
            } // end for
        }

        // Create an blank out the structure.
        structure = new Boolean[Piece.MAX_COLUMNS][Piece.MAX_ROWS];

        for ( int i = 0; i < structure.length; i++ )
        {
            Arrays.fill( structure[i], false );
        }

        this.cellWidth = builder.cellWidth;
        this.cellHeight = builder.cellHeight;

        // Set the width and height.
        this.width = cellWidth * Piece.MAX_COLUMNS;
        this.height = cellHeight * Piece.MAX_ROWS;

        // Set the alignment mode.
        this.alignment = builder.alignment;
        this.alignmentMode = builder.alignmentMode;

        // Calculate the offsets right now if we're aligning to the full grid.
        if ( alignmentMode == AlignmentMode.TO_FULL_GRID )
        {
            this.offsetX = determineOffsetX( this.alignment, width );
            this.offsetY = determineOffsetY( this.alignment, height );
        }
    }

    public static class Builder implements IBuilder<PieceGrid>
    {
        final IWindow win;
        final int x;
        final int y;
        final RenderMode renderMode;

        AlignmentMode alignmentMode = AlignmentMode.TO_FULL_GRID;
        EnumSet<Alignment> alignment = EnumSet.of( Alignment.TOP, Alignment.LEFT );
        int cellWidth = 32;
        int cellHeight = 32;
        Color color = Color.ORANGE;
        boolean visible = true;
        int opacity = 100;

        public Builder(IWindow win, int x, int y, RenderMode renderMode)
        {
            this.win = win;
            this.x = x;
            this.y = y;
            this.renderMode = renderMode;
        }

        public Builder cellWidth(int val)
        {
            cellWidth = val;
            return this;
        }

        public Builder cellHeight(int val)
        {
            cellHeight = val;
            return this;
        }

        public Builder color(Color val)
        {
            color = val;
            return this;
        }

        public Builder alignmentMode(AlignmentMode val)
        {
            alignmentMode = val;
            return this;
        }

        public Builder alignment(EnumSet<Alignment> val)
        {
            alignment = val;
            return this;
        }

        public Builder visible(boolean val)
        {
            visible = val;
            return this;
        }

        public Builder opacity(int val)
        {
            opacity = val;
            return this;
        }

        public PieceGrid build()
        {
            return new PieceGrid( this );
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
        if ( this.alignmentMode == AlignmentMode.TO_PIECE )
        {
            int[] wx = new int[structure.length];
            int[] hx = new int[structure[0].length];

            for ( int i = 0; i < structure.length; i++ )
            {
                for ( int j = 0; j < structure[0].length; j++ )
                {
                    if ( structure[i][j] )
                    {
                        wx[i] = 1;
                        hx[j] = 1;
                    }
                }
            }

            int w = NumUtil.sumIntArray( wx ) * cellWidth;
            int h = NumUtil.sumIntArray( hx ) * cellHeight;

            if ( alignment.contains( Alignment.LEFT ) || alignment.contains( Alignment.RIGHT ) || alignment.
                    contains( Alignment.CENTER ) )
            {
                this.offsetX = 0;

                for ( int wi : wx )
                {
                    if ( wi == 1 )
                    {
                        break;
                    }
                    this.offsetX -= this.cellWidth;
                }

                if ( alignment.contains( Alignment.RIGHT ) )
                {
                    this.offsetX -= w;
                }
                else if ( alignment.contains( Alignment.CENTER ) )
                {
                    this.offsetX -= w / 2;
                }
            }
            else
            {
                throw new IllegalStateException( "No horizontal alignment assigned!" );
            }

            if ( alignment.contains( Alignment.TOP ) || alignment.contains( Alignment.BOTTOM ) || alignment.
                    contains( Alignment.MIDDLE ) )
            {
                this.offsetY = 0;

                for ( int hi : hx )
                {
                    if ( hi == 1 )
                    {
                        break;
                    }
                    this.offsetY -= this.cellHeight;
                }

                if ( alignment.contains( Alignment.BOTTOM ) )
                {
                    this.offsetY -= h;
                }
                else if ( alignment.contains( Alignment.MIDDLE ) )
                {
                    this.offsetY -= h / 2;
                }
            }
            else
            {
                throw new IllegalStateException( "No vertical alignment assigned!" );
            }


        }

        // Set dirty so it will be drawn.        
        setDirty( true );
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
        if ( !this.visible )
        {
            return false;
        }

        switch ( renderMode )
        {
            case SPRITE_LIGHT:
            case SPRITE_DARK:
                renderSprite();
                break;

            case VECTOR:
                renderVector();
                break;

            default:
                throw new AssertionError();
        }

        return true;
    }

    /**
     * Render the piece grid using a sprite.
     */
    private void renderSprite()
    {
        // Cycle through, drawing only the sprites that should be shown.
        for ( int i = 0; i < structure.length; i++ )
        {
            for ( int j = 0; j < structure[0].length; j++ )
            {
                if ( structure[i][j] )
                {
                    spriteArray[i][j].draw(
                            x + offsetX + (i) * cellWidth,
                            y + offsetY + (j) * cellHeight )
                            .opacity( this.opacity )
                            .theta( this.theta, -32 * i + 48, -32 * j + 48 )
                            .end();
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
        CouchColor oldColor = graphics.getColor();
        graphics.setColor( color );

        // Cycle through, drawing only the sprites that should be shown.
        for ( int i = 0; i < structure.length; i++ )
        {
            for ( int j = 0; j < structure[0].length; j++ )
            {
                if ( structure[i][j] == true )
                {                    
                    graphics.fillEllipse(
                            x + offsetX + i * cellWidth,
                            y + offsetY + j * cellHeight,
                            cellWidth, cellHeight);
                } // end if
            } // end for
        } // end for	

        // Restore the old color.
        graphics.setColor( oldColor );
    }

    @Override
    public void setWidth(int width)
    {
        throw new UnsupportedOperationException( "Not supported" );
    }

    @Override
    public void setHeight(int height)
    {
        throw new UnsupportedOperationException( "Not supported" );
    }

    public Color getColor()
    {
        return color;
    }

    public void setColor(Color color)
    {
        if ( color == null )
        {
            throw new IllegalArgumentException( "Color must not be null" );
        }

        this.color = color;
    }

}
