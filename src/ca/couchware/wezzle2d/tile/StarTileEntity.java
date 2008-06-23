package ca.couchware.wezzle2d.tile;

import ca.couchware.wezzle2d.*;

/**
 * A bomb tile.
 * 
 * @author cdmckay
 */

public class StarTileEntity extends TileEntity
{
    /**
     * Path to the piece selector sprite.
     */
    final private String PATH = Game.SPRITES_PATH + "/ItemStar.png";
    
    /**
     * The sprite representing the item graphic.
     */
    final private Sprite itemSprite;
    
    /**
     * The constructor.
     * @param boardMan
     * @param color
     * @param x
     * @param y
     */    
    public StarTileEntity(final BoardManager boardMan, final int color, 
            final int x, final int y)
    {
        // Invoke super.
        super(boardMan, color, x, y);
        
        // Load bomb sprite.
        itemSprite = ResourceFactory.get().getSprite(PATH);
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
        itemSprite.draw((int) x2, (int) y2, width, height, theta, opacity);
    }
}
