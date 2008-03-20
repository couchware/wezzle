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
	 * The score manager.
	 */
	private final ScoreManager scoreMan;
	
    /**
     * The maximum items available for the level.
     */
    private int maxItems;
    
	/**
	 * The current level
	 */
	private int currentLevel;
		
	// ---------------------------------------------------------------------------
	// XML Instance Attributes
	// ---------------------------------------------------------------------------
	
	/** 
	 * The display name of the world.
	 */
	private String displayName;
	
	/**
	 * The number of levels in this world.
	 */
	private int totalLevels;
	
	/**
	 * The item list
	 */
	private LinkedList itemList;
		
	/**
	 * The difficulty level
	 */
	private int difficulty;	
	
	/**
	 * The constructor.
	 * @param fragment
	 * @param board
	 * @param scoreManager
	 */
	public WorldManager(final PropertyManager propertyMan, 
            final ScoreManager scoreMan)
	{		
		// Store a reference to the score manager.
		this.scoreMan = scoreMan;
		
		// Store a reference to the property manager.
		this.propertyMan = propertyMan;
		
		// Load the properties;
		this.difficulty = propertyMan
                .getIntegerProperty(PropertyManager.DIFFICULTY);
		
		// Ensure that we are at least level 1.
		if (this.difficulty < 1)
			this.difficulty = 1;
		
		this.displayName = propertyMan
                .getStringProperty(PropertyManager.DISPLAY_NAME);
        
		this.totalLevels = propertyMan
                .getIntegerProperty(PropertyManager.NUM_LEVELS);
				
		// Set the starting level.
		setCurrentLevel(this.difficulty);
        
        // Set the max items.
        this.maxItems = 5;
				
		itemList = new LinkedList();
		itemList.add(new ItemDescriptor(TileEntity.class, 28, -1));
		itemList.add(new ItemDescriptor(BombTileEntity.class, 5, 50));
		itemList.add(new ItemDescriptor(Multiply2xTileEntity.class, 2, 50));
        itemList.add(new ItemDescriptor(Multiply3xTileEntity.class, 2, 20));
        itemList.add(new ItemDescriptor(Multiply4xTileEntity.class, 2, 10));
	}
		
	/**
	 * @return the currentLevel
	 */
	public int getCurrentLevel()
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
		
		// Set the difference score.
		scoreMan.setTargetLevelScore(generateTargetLevelScore(currentLevel));				
	}	
	
    /**
     * @return the maximum number of items.
     */
    public int getNumMaxItems()
    {
        return this.maxItems;
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
        scoreMan.setLevelScore(0);
        game.boardMan.generateBoard(this.getItemList());
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
		Util.handleWarning("Random number out of range! (" + randomNumber + ").", Thread.currentThread());
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
	private int generateTargetLevelScore(int currentLevel)
	{
		return currentLevel * 2000;
	}

//	/**
//	 * Reset the game board.  This method will remove the old game board
//	 * and replace it with a new one on the currently set level.
//	 * 
//	 * @param line1 The first line of the message shown.
//	 * @param line2 The second line of the message shown.
//	 */
//	private void resetGameBoard(final GameBoard board, final String line1, final String line2)
//	{
//		// Update the board in another thread.
//		Runnable r = new Runnable()
//		{			
//			public void run()
//			{																		
//				// Animate hide.
//				board.hideTileLayerUsingAnimation();
//
//				// Reset the game board.
//				board.reset();
//				
//				// Animate some text.
//				final Animation a = new AnimationTextZoom(board, 
//						board.getWidth() / 2, 
//						board.getHeight() / 2,
//						line1,
//						line2);
//				
//				// Add it to the manager.
//				animationManager.addAnimation(a);
//				
//				// Add to um.
////				board.getComponent().getUpdateManager().getUpdateRunnableQueue().invokeLater(r);
//				
//				// Wait for it to finish.
//				while(a.isDone() == false)
//					Time.waitUntilNextTimeStep();											
//															
//				// Generate a new board.									
//				board.generateGameBoard(itemList);
//				
//				// Animate show.
//				board.showTileLayerUsingAnimation();
//				
//				// Fire level started event.
//				fireLevelStartedEvent(new LevelEvent(this, getCurrentLevel()));								
//			}
//		};
//		
//		// Execute the worker.
//		executor.submit(r);
//	}
	
}
