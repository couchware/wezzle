package ca.couchware.wezzle2d.manager;

import ca.couchware.wezzle2d.*;
import ca.couchware.wezzle2d.event.ILevelListener;
import ca.couchware.wezzle2d.event.LevelEvent;
import ca.couchware.wezzle2d.tile.*;
import ca.couchware.wezzle2d.util.Util;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Manages the world.
 * 
 * @author kgrad
 *
 */

public class WorldManager implements IManager, ILevelListener
{       
	//--------------------------------------------------------------------------
	// Instance Members
	//--------------------------------------------------------------------------	   
    
	/**
	 * The current level
	 */
	private int level;
    
	/**
	 * The item list.
	 */
	private ArrayList<Item> itemList;
        
    /**
     * The multiplier list
     */
    private List<Item> multiplierList;        
    
    /**
     * The master rule list.  Contains all the rules that should exist
     * at the start of a new game.
     */
    private List<Rule> masterRuleList;
    
    /**
     * The current rule list.  Contains all the rules that have not yet 
     * been realized for the current game.
     */
    private List<Rule> currentRuleList;
    
    /**
     * Is a game in progress?
     */
    private boolean gameInProgress = false;   
    
    /**
     * The maximum items on the screen at once.
     */
    private int maxItems;
    
    /**
     * The maximum multipliers on the screen at once.
     */
    private int maxMultipliers;
    
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
    
    //--------------------------------------------------------------------------
	// Constructor
	//--------------------------------------------------------------------------
    
	/**
	 * The constructor.
	 * @param fragment
	 * @param board
	 * @param scoreManager
	 */
	private WorldManager()
	{										
        // Set the max items and mults.
        this.maxItems       = 3;
        this.maxMultipliers = 3;
        
        // Create the item list.
        itemList = new ArrayList<Item>();                
        
        // Set the multipliers.
        multiplierList = new ArrayList<Item>();        
        multiplierList.add(new Item.Builder(TileType.X2)
                .initialAmount(2).weight(50).maxOnBoard(3).end());
        multiplierList.add(new Item.Builder(TileType.X3)
                .initialAmount(0).weight(20).maxOnBoard(1).end());
        multiplierList.add(new Item.Builder(TileType.X4)
                .initialAmount(0).weight(10).maxOnBoard(1).end());                
                          
        // Make the mutable list the master list.
        masterRuleList = createMasterRuleList();
                        
        // Create the rule list.
        currentRuleList = new LinkedList<Rule>();        
        
        // Reset the manager to finish the initalization.
        resetState();
	}
	        
    /**
     * Returns a new world manager instance.
     * 
     * @param settingsMan
     * @return
     */       
    public static WorldManager newInstance()
    {
        return new WorldManager();
    }        
    
    //--------------------------------------------------------------------------
	// Instance Methods
	//--------------------------------------------------------------------------   
    
    private List<Rule> createMasterRuleList()
    {
        // Set the rules.
        List<Rule> mutableList = new ArrayList<Rule>();
        
        
        // Make it so the rocket block is added.
        mutableList.add(new Rule(Rule.Type.LEVEL, Rule.Operation.GTEQ, 3)
        {
            @Override
            public void onMatch()
            {
                // Add the rocket.
                itemList.add(new Item.Builder(TileType.ROCKET)
                        .initialAmount(1).weight(55).maxOnBoard(3).end());                
            }            
        });  
        
        // Make it so the bomb block is added.
        mutableList.add(new Rule(Rule.Type.LEVEL, Rule.Operation.GTEQ, 6)
        {
            @Override
            public void onMatch()
            {
                // Add the bomb.
                itemList.add(new Item.Builder(TileType.GRAVITY)
                        .initialAmount(1).weight(50).maxOnBoard(1).end());                
            }            
        });  
        
        // Make it so the bomb block is added.
        mutableList.add(new Rule(Rule.Type.LEVEL, Rule.Operation.GTEQ, 9)
        {
            @Override
            public void onMatch()
            {
                // Add the bomb.
                itemList.add(new Item.Builder(TileType.BOMB)
                        .initialAmount(1).weight(10).maxOnBoard(1).end());                
            }            
        }); 
        
        // Make it so the star block is added.
        mutableList.add(new Rule(Rule.Type.LEVEL, Rule.Operation.GTEQ, 12)
        {
            @Override
            public void onMatch()
            {
                // Add the star.
                itemList.add(new Item.Builder(TileType.STAR)
                        .initialAmount(0).weight(5).maxOnBoard(1).end());                
            }            
        });   
        
        // Make it so a new block color is added on level 5.
//        mutableList.add(new Rule(Rule.Type.LEVEL, Rule.Operation.EQ, 5)
//        {            
//            @Override
//            public void performAction(Game game)
//            {
//                // Increase the number of colours.
//                game.boardMan.setNumberOfColors(6);
//            }            
//        }); 
        
        return Collections.unmodifiableList(mutableList);
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
        return generateTargetLevelScore(level);
    }     
    
    public void levelUp(final Game game)
    {        
        this.incrementLevel();
        
        int currentLevelScore = game.scoreMan.getLevelScore() - 
                game.scoreMan.getTargetLevelScore();
        int targetLevelScore = generateTargetLevelScore(level);
        
        if(currentLevelScore > targetLevelScore / 2)
            currentLevelScore = targetLevelScore / 2;
        
        game.scoreMan.setLevelScore(currentLevelScore);        
		game.scoreMan.setTargetLevelScore(targetLevelScore);
        
        game.progressBar.setProgressMax(game.scoreMan.getTargetLevelScore());
               
        // Change the timer.
        int time = game.timerMan.getInitialTime();
        
        if(time > this.timerMin)
            time--;
        
        game.timerMan.setInitialTime(time);
    }
    
    /**
     * Calculates the number of tiles to drop.
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
        int levelDrop = (this.level / this.levelDifficultySpeed);
        
        // Check for difficulty ramp up.
        if (this.level > this.difficultyIncreaseLevel)
            levelDrop = (this.difficultyIncreaseLevel / this.levelDifficultySpeed);
        
        // The percent of the board to readd.
        int  boardPercentage = (int)((totalSpots - tiles) * 0.1f); 
        
        // We are low. drop in a percentage of the tiles, increasing if there 
        // are fewer tiles.
        if ((tiles / totalSpots) * 100 < this.tileRatio)
        {
            // If we are past the level ramp up point, drop in more.
            if (this.level > this.difficultyIncreaseLevel)
            {
                  return pieceSize +  levelDrop 
                    + (this.level - this.difficultyIncreaseLevel) 
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
            if (this.level > this.difficultyIncreaseLevel)
            {
                return pieceSize + levelDrop
                    + (this.level - this.difficultyIncreaseLevel) 
                    + this.minimumDrop;
            }
            else
                return pieceSize + levelDrop + this.minimumDrop;
        }
    }          
    
    //--------------------------------------------------------------------------
    // Logic
    //--------------------------------------------------------------------------
    
    public void updateLogic(final Game game)
    {       
        // If the board is refactoring, do not logicify.
        if (game.isBusy() == true)
             return;
        
        // See if we need to create a new board.
        if (isGameInProgress() == false 
                && game.tutorialMan.isTutorialInProgress() == false)
        {
            // Game is now in progress.
            //setGameInProgress(true);
            
            // Generate the game board.            
            //game.boardMan.clearBoard();            
            //game.startBoardShowAnimation();
        }                
        
        for (Iterator<Rule> it = currentRuleList.iterator(); it.hasNext(); )
        {
            Rule rule = it.next();
            
            if (rule.evaluate(game) == true)
            {
                rule.onMatch();
                it.remove();
            }
        }            
    }
   
    //--------------------------------------------------------------------------
	// Getters and Setters
	//--------------------------------------------------------------------------
    
    /**
     * Is a game currently in progress?
     * 
     * @return True if a game is in progress, false otherwise.
     */
    public boolean isGameInProgress()
    {
        return gameInProgress;
    }

    public void setGameInProgress(boolean gameInProgress)
    {
        this.gameInProgress = gameInProgress;
    }        
    
	/**
     * Get the current level.
     * 
	 * @return the currentLevel
	 */
	public int getLevel()
	{
		return level;
	}	
	
	/**
	 * @param currentLevel the currentLevel to set
	 */
	public void setLevel(int level)
	{
		// Set the level.
		this.level = level;								
	}	
	
    /**
     * @return the maximum number of items.
     */
    public int getMaxItems()
    {
        return maxItems;
    }
    
    /**
     * set the maximum number of items.
     */
    public void setMaxItems(int maxItems)
    {
        this.maxItems = maxItems;
    }    
    
    
     /**
     * @return the maximum number of mults.
     */
    public int getMaxMultipliers()
    {
        return maxMultipliers;
    }
    
    /**
     * set the maximum number of mults.
     */
    public void setMaxMultipliers(int maxMults)
    {
        this.maxMultipliers = maxMults;
    }    
    
    
    /**
     * Get the initial timer value.
     * @return The initial timer.
     */
    public int getInitialTimer()
    {
        return initialTimer;
    }        
    
	/**
	 * Increment the level.
	 */
	public void incrementLevel()
	{
		// Increment initial amount of normal tiles.		
		getItem(0).incrementInitialAmount();
		
		// Increment the level.
		setLevel(level + 1);
	}	
	            
    /**
     * Get the parallel drop in amount.
     * @return The amount.
     */
    public int getParallelTileDropInAmount()
    {
        return this.parallelDropInAmount;
    }
    
	/**
	 * Get the descriptor for the item at the specified index.
	 * 
	 * @param index The index.
	 * @return The ItemDescriptor.
	 */
	public Item getItem(int index)
	{
		return itemList.get(index);
	}
	
	/**
	 * Get the descriptor for the item with the specified class.
	 * Returns null if the class does not exist.
	 * 
	 * @param type
	 * @return 
	 */
	public Item getItem(TileType type)
	{
		for (Item item : itemList)
        {
			if (item.getTileType() == type)
				return item;
        }
		
		return null;
	}
       
    /**
     * Gets a random item.
     * 
     * @return 
     */
    public Item getItem(int numItems, int numMultipliers)
	{	
        
        // Check if we do not return  a normal tile. There is
        // A flat 5% chance of this.
        int test = Util.random.nextInt(100);
        if( test <= 5)
        {
            return itemList.get(0);
        }

        // Build the list of items. This does not include items 
        // with probability 0 (if the max number exist) and includes
        // Mults as well.

        boolean useItems = false;
        boolean useMultipliers = false;
        int constant = 15;

        // calculate the probability of an item. The probability of a mult
        // is 1 - probability of item.
        int probItems = (numMultipliers-numItems) * constant + 50;
        //int probMults = (numItems-numMults) * constant + 50;

        // If there are no items in the game yet.
        if(itemList.size() == 1)
            probItems = 0;

        // Select a number from 1 - 100.
        int pick = Util.random.nextInt(100);


        if(numMultipliers < maxMultipliers && numItems < maxItems)
        {
            if(pick < probItems)
            {
                useItems = true;
            }
            else
            {
                useMultipliers = true;
            }
        }
        else if (numMultipliers < maxMultipliers)
        {
            useMultipliers = true;
        }
        else if (numItems < maxItems)
        {
            useItems = true;
        }


        ArrayList<Item> items = new ArrayList<Item>();

        if (useItems == true)
        {
            for (Item item : itemList)
            {
                // Skip the normal tile.
                if (item.getTileType() == TileType.NORMAL)
                    continue;

                if (item.getWeight() > 0)
                    items.add(item);
            }
        }

        if (useMultipliers == true)
        {  
            for (Item item : multiplierList)
            {
                if (item.getWeight() > 0)
                    items.add(item);
            }
        }

        // If the list is empty, return a normal tile.
        if(items.size() <= 0)
        {
           return itemList.get(0);
        }
                        
		// Create an array representing the item distribution 
            
		int dist[] = new int[items.size() + 1];
		dist[0] = 0;
		
		// Determine the distribution.
		int i = 1;
		for (Item item : items)
		{            
			if (item.getWeight() == -1)
				dist[i] = dist[i - 1];
			else			
				dist[i] = dist[i - 1] + item.getWeight();
			
			i++;
		}
		
               
		// Pick a random number between 0 and dist[dist.length - 1].
		int randomNumber = Util.random.nextInt(dist[dist.length - 1]);
		
		for (int j = 1; j < dist.length; j++)
		{
			if (randomNumber < dist[j])
            {
				return items.get(j - 1);
            }
		}
		
		// We should never get here.
		LogManager.recordWarning(
                "Random number out of range! (" + randomNumber + ").", 
                "WorldManager#getItem");
        
		return items.get(0);
	}
    
    /**
	 * @return The items and mults
	 */
	public LinkedList<Item> getItemList()
	{
            LinkedList<Item> items = new LinkedList<Item>();
            
            for (Item item : itemList)
                items.add(item);
            for(Item item: multiplierList)
                items.add(item);
                    
		return items;
	}               	
                
    public void levelChanged(LevelEvent e)
    {
        for (int i = 0; i < e.getLevelChange(); i++)
            this.levelUp(e.getGame());
    }

    public void saveState()
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void loadState()
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }
	
    /**
     * Resets the board manager to appropriate settings for the first level.
     */
    public void resetState()
    {
        // Reset to level 1.
        this.setLevel(1);
        
        // !IMPORTANT! Ensure that the first item is a normal tile. 
        // This is so that we can use the first item when returning a
        // normal tile whenever we want.
        itemList.clear();
        itemList.add(new Item.Builder(TileType.NORMAL)
                .initialAmount(28).weight(5).maxOnBoard(100).end());
//        itemList.add(new Item.Builder(TileType.ROCKET)
//                .initialAmount(1).weight(55).maxOnBoard(3).end());
//        itemList.add(new Item.Builder(TileType.BOMB)
//                .initialAmount(1).weight(10).maxOnBoard(1).end());   
//        itemList.add(new Item.Builder(TileType.STAR)
//                .initialAmount(1).weight(5).maxOnBoard(1).end());
//        itemList.add(new Item.Builder(TileType.GRAVITY)
//                .initialAmount(5).weight(50).maxOnBoard(1).end());
        
        // Reset the rules.
        currentRuleList.clear();
        currentRuleList.addAll(masterRuleList);
    }
    
}
