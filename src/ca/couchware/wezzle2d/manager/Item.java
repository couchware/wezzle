package ca.couchware.wezzle2d.manager;

import ca.couchware.wezzle2d.*;
import ca.couchware.wezzle2d.tile.TileType;

/**
 * A class to describe an item on the board.
 * 
 * @author Cameron
 * @author Kevin
 */
public class Item 
{
	// ---------------------------------------------------------------------------
	// Instance Attributes
	// ---------------------------------------------------------------------------
	
	/**
	 * The class of the tile we are describing.
	 */
	private TileType tileType;
	
	/**
	 * The initial number of this item.
	 */
	private int initialAmount; 
	
	/**
	 * The current number of this item.
	 */
	private int currentAmount;
	
	/**
	 * The chance weight that this item will be created.
	 */
	private int weight;
        
    /**
     * The maximum number of these items on the screen at once.
     */
    private int maxOnScreen;
	
	// ---------------------------------------------------------------------------
	// Constructors
	// ---------------------------------------------------------------------------
	
	/**
	 * A constructor to construct an item descriptor for the passed in class.
	 * @param itemClass The class we are describing.
	 */
	private Item(TileType tileType, int initialAmount, int probability, int maxOnScreen)
	{
		this.tileType = tileType;
		this.initialAmount = initialAmount;
		this.currentAmount = 0;
		this.weight = probability;
                this.maxOnScreen = maxOnScreen;
	}
    
    public static class Builder implements IBuilder<Item>
    {
        // Required values.       
        final private TileType tileType;        
        
        // Optional values.
        private int initialAmount = 0;        
        private int weight = 10;
        private int maxOnScreen = 1;
        
        public Builder(TileType tileType)
        {            
            this.tileType = tileType;
        }                             
        
        public Builder initialAmount(int val) 
        { initialAmount = val; return this; }
                
        public Builder weight(int val) 
        { weight = val; return this; }
        
        public Builder maximumOnBoard(int val) 
        { maxOnScreen = val; return this; }
        
        public Item end()
        {            
            return new Item(tileType, initialAmount, weight, maxOnScreen);
        }                
    } 
	
	// ---------------------------------------------------------------------------
	// Getters and Setters
	// ---------------------------------------------------------------------------
	
	/**
	 * Get the Item class of this descriptor.
	 * @return The itemClass.
	 */
	public TileType getTileType()
	{
		return tileType;
	}
	
	public void setTileType(TileType tileType)
	{
		this.tileType = tileType;
	}

	/**
	 * @return The initialAmount.
	 */
	public int getInitialAmount()
	{
		return initialAmount;
	}

	/**
	 * @param initialAmount The initialAmount to set.
	 */
	public void setInitialAmount(int initialAmount)
	{
		this.initialAmount = initialAmount;
	}
	
	public void incrementInitialAmount()
	{
		initialAmount++;
	}
	
	public void decrementInitialAmount()
	{
		initialAmount--;
	}

	/**
	 * @return The currentAmount.
	 */
	public int getCurrentAmount()
	{
		return currentAmount;
	}

	/**
	 * @param currentAmount The currentAmount to set.
	 */
	public void setCurrentAmount(int currentAmount)
	{
		this.currentAmount = currentAmount;
	}
	
	/**
	 * Increment the current amount.
	 */
	public void incrementCurrentAmount()
	{
		this.currentAmount++;
	}
	
	/**
	 * Decrement the current amount.
	 */
	public void decrementCurrentAmount()
	{
		this.currentAmount--;
	}

	/**
	 * @return The maxAmount.
	 */
	public int getMaxOnScreen()
	{
		return this.maxOnScreen;
	}
        
        public void setMaxOnScreen(int maxOnScreen)
	{
		this.maxOnScreen = maxOnScreen;
	}

	/**
	 * @return The probability.
     */
    public int getWeight()
    {
        if (this.currentAmount < this.maxOnScreen)
        {
            return weight;
        }
        else
        {
            return 0;
        }
    }

	/**
	 * @param probability The probability to set.
	 */
	public void setWeight(int weight)
	{
		this.weight = weight;
	}	
}

