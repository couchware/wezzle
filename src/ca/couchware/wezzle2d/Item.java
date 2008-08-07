package ca.couchware.wezzle2d;

import ca.couchware.wezzle2d.tile.TileType;

/**
 * A class to describe an item on the board.
 * @author Kevin
 *
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
	 * The probability this item will be created.
	 */
	private int probability;
	
	// ---------------------------------------------------------------------------
	// Constructors
	// ---------------------------------------------------------------------------
	
	/**
	 * A constructor to construct an item descriptor for the passed in class.
	 * @param itemClass The class we are describing.
	 */
	public Item(TileType tileType, int initialAmount, int probability)
	{
		this.tileType = tileType;
		this.initialAmount = initialAmount;
		this.currentAmount = 0;
		this.probability = probability;
	}
	
	/**
	 * The overloaded constructor. Parses the data from an XML file.
	 * @param file The file to parse
	 */
	public Item(String path)
	{
		
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
	public int getMaxAmount(int currentLevel)
	{
		return this.initialAmount + (currentLevel / 4);
	}

	/**
	 * @return The probability.
	 */
	public int getProbability()
	{
		return probability;
	}

	/**
	 * @param probability The probability to set.
	 */
	public void setProbability(int probability)
	{
		this.probability = probability;
	}	
}

