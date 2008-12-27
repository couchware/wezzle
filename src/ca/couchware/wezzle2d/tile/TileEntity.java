package ca.couchware.wezzle2d.tile;

import ca.couchware.wezzle2d.graphics.GraphicEntity;
import ca.couchware.wezzle2d.manager.BoardManager;
import ca.couchware.wezzle2d.manager.Settings;
import java.util.ArrayList;
import java.util.List;

/**
 * A class representing a game tile.
 * 
 * @author cdmckay
 *
 */

public class TileEntity extends GraphicEntity
{                                 
	
	/** The associated board manager. */
	protected final BoardManager boardMan;	    
    
	/** The colour of the tile. */
	protected final TileColor color;	
    
    /** The tile type, i.e. NORMAL, ROCKET, etc. */
    protected TileType type;       
   
	/**
	 * Creates a tile at (x,y) with the specified color.
     * 
	 * @param color
	 * @param x
	 * @param y
	 */
	public TileEntity(BoardManager boardMan, TileColor color, int x, int y) 
	{
		// Invoke super.		
		super(new Builder(x, y, 
                Settings.getSpriteResourcesPath() + "/Tile" + color + ".png"));                
        
        // Set the position.
        this.x  = x;
        this.y  = y;
        this.x_ = x;
        this.y_ = y;
        
		// Set board manager and color reference.
		this.boardMan = boardMan;	
		this.color = color;                
        this.type = TileType.NORMAL;
	}
    
    /**
     * Used to make a new tile entity that is roughly the same as the one
     * passed.
     * 
     * @param tile
     */
    public TileEntity(TileEntity tile)
    {
        this(tile.boardMan, tile.color, tile.x, tile.y);
    }    	
    
	/**
	 * Gets the tile color.
     *      
	 * @return The color.
	 */
	public TileColor getColor()
	{
		return color;
	}

    /**
     * Gets the tile type.
     * 
     * @return
     */
    public TileType getType()
    {
        return type;
    }    
    
	@Override
    public boolean draw()
	{          
        this.x_ = x + offsetX;
        this.y_ = y + offsetY;
        
        this.width_ = width;
        this.height_ = height;
        
        if (isVisible() == false)
            return false;
                        
        sprite.draw(x + offsetX, y + offsetY).width(width).height(height)                
                .theta(theta, rotationAnchor).opacity(opacity).end();                          
        
        return true;
	}   
    
//    @Override
//    public String toString()
//    {
//        StringBuffer buffer = new StringBuffer();
//        buffer.append(String.format
//    }
    
//    //--------------------------------------------------------------------------
//    // Clickable
//    //--------------------------------------------------------------------------
//    
//    /**
//     * The stored click action.
//     */
//    Runnable clickHook = null;
//    
//    /**
//     * Sets the click runnable.
//     */
//    public void setClickRunnable(Runnable r)
//    { 
//        this.clickHook = r;
//    }
//    
//    /**
//     * Gets the click runnable.
//     */
//    public Runnable getClickRunnable()
//    {
//        return clickHook;
//    }
//    
//    /**
//     * This method is called when the tile is clicked.
//     */
//    public void onClick()
//    {
//        if (clickHook != null) clickHook.run();
//    }
    
    /** An interface for listening to tile changes. */
    public static interface ITileListener 
    {
        public void tileClicked();
    }
    
    /** The tile listener list. */
    private List<ITileListener> tileListenerList = new ArrayList<ITileListener>();
    
    public void fireTileClickedEvent()
    {
        for (ITileListener listener : tileListenerList)
            listener.tileClicked();
    }
    
    public void addTileListener(ITileListener listener)
    {
        if (this.tileListenerList.contains(listener))
            throw new IllegalArgumentException("Listener already registered!");
        
        this.tileListenerList.add(listener);
    }
    
    public void removeTileListener(ITileListener listener)
    {
        if (!this.tileListenerList.contains(listener))
            throw new IllegalArgumentException("Listener is not registered!");
        
        this.tileListenerList.remove(listener);
    }
    
}
