/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ca.couchware.wezzle2d;

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
	private final PropertyManager propertyManager;
	
	/**
	 * The score manager.
	 */
	private final ScoreManager scoreManager;
	

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
	public WorldManager( PropertyManager pm, ScoreManager sm)
	{		
		// Store a reference to the score manager.
		this.scoreManager = sm;
		
		// Store a reference to the property manager.
		this.propertyManager = pm;
		
		// Load the properties;
		this.difficulty = propertyManager.getIntegerProperty(PropertyManager.DIFFICULTY);
		
		// Ensure that we are atleast level 1.
		if (this.difficulty < 1)
			this.difficulty = 1;
		
		this.displayName = propertyManager.getStringProperty(PropertyManager.DISPLAY_NAME);
		this.totalLevels = propertyManager.getIntegerProperty(PropertyManager.NUM_LEVELS);
		
		
		// Set the starting level.
		setCurrentLevel(this.difficulty);
				
		itemList = new LinkedList();
//		itemList.add(new ItemDescriptor(TileNormal.class, 28, -1));
//		itemList.add(new ItemDescriptor(TileBomb.class, 1, 50));
//		itemList.add(new ItemDescriptor(TileMultiplier.class, 2, 50));		
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
		scoreManager.setTargetLevelScore(generateTargetLevelScore(currentLevel));				
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
			if (((ItemDescriptor)it).getItemClass().equals(c))
				return (ItemDescriptor)it;
        }
		
		return null;
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
