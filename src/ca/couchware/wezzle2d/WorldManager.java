package ca.couchware.wezzle2d;

import ca.couchware.wezzle2d.tile.*;
import ca.couchware.wezzle2d.util.Util;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * Manages the world.
 * @author kgrad
 *
 */

public class WorldManager
{
	/**
	 * The property manager.
	 */
	private final PropertyManager propertyMan;
    
	/**
	 * The current level
	 */
	private int currentLevel;
		
	// ---------------------------------------------------------------------------
	// XML Instance Attributes
	// ---------------------------------------------------------------------------
	
	/**
	 * The item list
	 */
	private LinkedList itemList;
		
	/**
	 * The difficulty level
	 */
	private int difficulty;	
    
    /**
     * The maximum items available for the level.
     */
    private int maxItems;
    
    /**
     * The percentage of tiles to maintain.
     */
    private int tileRatio = 80;
    
    /**
     * The minimum drop.
     */
    private int minimumDrop = 1;
    
    /**
     * The number of pieces to drop in concurrently.
     */
    private int parallelDropInAmount = 4;
    
    /**
     * The minimum timer value.
     */
    private int timerMin = 5;
    
    /**
     * The initial timer value.
     */
	
    private int initialTimer = 15;
    
    
    /**
     * The level at which the difficulty begins to increase.
     */
    private int difficultyIncreaseLevel = 9;
    
    /**
     * The number of levels before the difficulty level increases.
     */
    private int levelDifficultySpeed = 3;
    
	/**
	 * The constructor.
	 * @param fragment
	 * @param board
	 * @param scoreManager
	 */
	public WorldManager(final PropertyManager propertyMan)
	{						
		// Store a reference to the property manager.
		this.propertyMan = propertyMan;
		
		// Load the properties;
		this.difficulty = propertyMan
                .getIntegerProperty(PropertyManager.KEY_DIFFICULTY);
		
		// Ensure that we are at least level 1.
		if (this.difficulty < 1)
			this.difficulty = 1;
				
		// Set the starting level.
		setCurrentLevel(1);
        
        // Set the max items.
        this.maxItems = 5;
        
		itemList = new LinkedList();
		itemList.add(new ItemDescriptor(TileEntity.class, 28, 20));
		itemList.add(new ItemDescriptor(BombTileEntity.class, 2, 50));
		itemList.add(new ItemDescriptor(Multiply2xTileEntity.class, 2, 50));
        itemList.add(new ItemDescriptor(Multiply3xTileEntity.class, 0, 20));
        itemList.add(new ItemDescriptor(Multiply4xTileEntity.class, 0, 10));
	}
		
	/**
	 * @return the currentLevel
	 */
	public int getLevel()
	{
		return currentLevel;
	}	
	
	/**
	 * @param currentLevel the currentLevel to set
	 */
	public void setCurrentLevel(int currentLevel)
	{
		// Set the level.
		this.currentLevel = currentLevel;								
	}	
	
    /**
     * @return the maximum number of items.
     */
    public int getNumMaxItems()
    {
        return this.maxItems;
    }
    
    /**
     * Get the initial timer value.
     * @return The initial timer.
     */
    public int getInitialTimer()
    {
        return this.initialTimer;
    }
    
     /**
     * set the maximum number of items.
     */
    public void setNumMaxItems(int max)
    {
        this.maxItems = max;
    }
    
	/**
	 * Increment the level.
	 */
	public void incrementCurrentLevel()
	{
		// Increment initial amount of normal tiles.		
		getItem(0).incrementInitialAmount();
		
		// Increment the level.
		this.setCurrentLevel(currentLevel + 1);
	}	
	    
    public void levelUp(final Game game)
    {        
        this.incrementCurrentLevel();
        
        int currentLevelScore = game.scoreMan.getLevelScore() - 
                game.scoreMan.getTargetLevelScore();
        int targetLevelScore = generateTargetLevelScore(currentLevel);
        
        if(currentLevelScore > targetLevelScore / 2)
            currentLevelScore = targetLevelScore / 2;
        
        game.scoreMan.setLevelScore(currentLevelScore);        
		game.scoreMan.setTargetLevelScore(targetLevelScore);
        
        game.progressBar.setProgressMax(game.scoreMan.getTargetLevelScore());
       
//        game.boardMan.generateBoard(this.getItemList());
        
        // Change the timer.
        int time = game.timerMan.getInitialTime();
        
        if(time > this.timerMin)
            time --;
        
        game.timerMan.setInitialTime(time);
    }
    
    /**
     * Get the parallel drop in amount.
     * @return The amount.
     */
    public int getParallelDropInAmount()
    {
        return this.parallelDropInAmount;
    }
    
	/**
	 * Get the descriptor for the item at the specified index.
	 * 
	 * @param index The index.
	 * @return The ItemDescriptor.
	 */
	public ItemDescriptor getItem(int index)
	{
		return (ItemDescriptor) this.itemList.get(index);
	}
	
	/**
	 * Get the descriptor for the item with the specified class.
	 * Returns null if the class does not exist.
	 * 
	 * @param c The item class.
	 * @return The ItemDescriptor.
	 */
	public ItemDescriptor getItem(Class c)
	{
		for (Iterator it = itemList.iterator(); it.hasNext(); )
        {
			if (((ItemDescriptor) it).getItemClass().equals(c))
				return (ItemDescriptor) it;
        }
		
		return null;
	}
       
    /**
     * 
     * @param game The game.
     * @param pieceSize The size of the piece consumed.
     * @return The number of tiles do drop.
     */
    public int calculateDropNumber(final Game game, int pieceSize)
    {
        float tiles = game.boardMan.getNumberOfTiles();
        float totalSpots = game.boardMan.getColumns() * game.boardMan.getRows();
        
        // The number of tiles for the current level.
        int levelDrop = (this.currentLevel / this.levelDifficultySpeed);
        
        // Check for difficulty ramp up.
        if(this.currentLevel > this.difficultyIncreaseLevel)
            levelDrop = (this.difficultyIncreaseLevel / this.levelDifficultySpeed);
        
        // The percent of the board to readd.
        int  boardPercentage = (int)((totalSpots - tiles) * 0.1f); 
        
        // We are low. drop in a percentage of the tiles, increasing if there 
        // are fewer tiles.
        if( (tiles / totalSpots) * 100 < this.tileRatio)
        {
            // If we are past the level ramp up point, drop in more.
            if(this.currentLevel > this.difficultyIncreaseLevel)
            {
                  return pieceSize +  levelDrop 
                    + (this.currentLevel - this.difficultyIncreaseLevel) 
                    + boardPercentage + this.minimumDrop;
            }
            else
            {
                return pieceSize + levelDrop + boardPercentage 
                        + this.minimumDrop;
            }
        }
        else
        {
            // If we are past the level ramp up point, drop in more.
            if(this.currentLevel > this.difficultyIncreaseLevel)
            {
                return pieceSize + levelDrop
                    + (this.currentLevel - this.difficultyIncreaseLevel) 
                    + this.minimumDrop;
            }
            else
                return pieceSize + levelDrop + this.minimumDrop;
        }
    }
     
    public Class pickRandomItem()
	{	
		// Create an array representing the item distribution.
		int dist[] = new int[itemList.size() + 1];
		dist[0] = 0;
		
		// Determine the distribution.
		int i = 1;
		for (Iterator it = itemList.iterator(); it.hasNext();)
		{
            ItemDescriptor item = (ItemDescriptor) it.next();
            
			if (item.getProbability() == -1)
				dist[i] = dist[i - 1];
			else			
				dist[i] = dist[i - 1] + item.getProbability();
			
			i++;
		}
		
		// Pick a random number between 0 and dist[dist.length - 1].
		int randomNumber = Util.random.nextInt(dist[dist.length - 1]);
		
		for (int j = 1; j < dist.length; j++)
		{
			if (randomNumber < dist[j])
				return ((ItemDescriptor) itemList.get(j - 1)).getItemClass();
		}
		
		// We should never get here.
		Util.handleWarning(
                "Random number out of range! (" + randomNumber + ").", 
                Thread.currentThread());
        
		return ((ItemDescriptor) itemList.get(0)).getItemClass();
	}
			
	/**
	 * @return The items.
	 */
	public LinkedList getItemList()
	{
		return itemList;
	}
		
	/**
	 * A method to generate a target score given the level. 
	 * 
	 * @param currentLevel The level to generate the score for.
	 * @return The score.
	 */
	public int generateTargetLevelScore(int currentLevel)
	{
		return currentLevel * 1200;
	}
        
    public int generateTargetLevelScore()
    {
        return generateTargetLevelScore(currentLevel);
    }
	
}
