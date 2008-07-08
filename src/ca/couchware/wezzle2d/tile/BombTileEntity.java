package ca.couchware.wezzle2d.tile;

import ca.couchware.wezzle2d.graphics.Sprite;
import ca.couchware.wezzle2d.*;

/**
 * A bomb tile.
 * 
 * @author cdmckay
 */

public class BombTileEntity extends TileEntity
{
    /**
     * Path to the piece selector sprite.
     */
    final private String PATH = Game.SPRITES_PATH + "/ItemBomb.png";
    
    /**
     * The sprite representing the bomb graphic.
     */
    final private Sprite bombSprite;
    
    /**
     * The constructor.
     * @param boardMan
     * @param color
     * @param x
     * @param y
     */    
    public BombTileEntity(final BoardManager boardMan, final int color, 
            final int x, final int y)
    {
        // Invoke super.
        super(boardMan, color, x, y);
        
        // Load bomb sprite.
        bombSprite = ResourceFactory.get().getSprite(PATH);
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
        bombSprite.draw((int) x2, (int) y2, width, height, theta, opacity);
    }
}
