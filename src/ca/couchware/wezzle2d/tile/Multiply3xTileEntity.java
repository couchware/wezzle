package ca.couchware.wezzle2d.tile;

import ca.couchware.wezzle2d.*;

/**
 * A bomb tile.
 * 
 * @author cdmckay
 */

public class Multiply3xTileEntity extends TileEntity
{
    /**
     * Path to the piece selector sprite.
     */
    final private String PATH = Game.SPRITES_PATH + "/Item3x.png";
    
    /**
     * The sprite representing the bomb graphic.
     */
    final private Sprite multSprite;
    
    /**
     * The constructor.
     * @param boardMan
     * @param color
     * @param x
     * @param y
     */    
    public Multiply3xTileEntity(final BoardManager boardMan, final int color, 
            final int x, final int y)
    {
        // Invoke super.
        super(boardMan, color, x, y);
        
        // Load bomb sprite.
        multSprite = ResourceFactory.get().getSprite(PATH);
    }
    
    /**
     * Override that draw muthafucka.
     */
    public void draw()
    {
        if (isVisible() == false)
            return;
        
        // Invoke super draw.
        super.draw();
        
        // Draw bomb on top of it.
        multSprite.draw((int) x, (int) y, width, height, theta, opacity);
    }
}
