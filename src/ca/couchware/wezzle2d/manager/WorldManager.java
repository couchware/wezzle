package ca.couchware.wezzle2d.manager;

import ca.couchware.wezzle2d.*;
import ca.couchware.wezzle2d.event.ILevelListener;
import ca.couchware.wezzle2d.event.ILineListener;
import ca.couchware.wezzle2d.event.IMoveListener;
import ca.couchware.wezzle2d.event.LevelEvent;
import ca.couchware.wezzle2d.event.LineEvent;
import ca.couchware.wezzle2d.event.MoveEvent;
import ca.couchware.wezzle2d.event.WezzleEvent;
import ca.couchware.wezzle2d.manager.ListenerManager.GameType;
import ca.couchware.wezzle2d.tile.*;
import ca.couchware.wezzle2d.util.Util;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Manages the world.
 * 
 * @author kgrad
 *
 */

public class WorldManager implements IManager, 
        ILineListener, 
        ILevelListener, 
        IMoveListener
{       
	//--------------------------------------------------------------------------
	// Instance Members
	//--------------------------------------------------------------------------	   
    
    /** The listener manager. */
    private ListenerManager listenerMan;
    
	/**
	 * The current level
	 */
	private int level;
    
	/**
	 * The item list.
	 */
	private Map<TileType, Item> itemMap;
        
    /**
     * The multiplier list
     */
    private Map<TileType, Item> multiplierMap;        
    
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
    
    // Timer.
    
    private int minimumTime = 5;
    private int maximumTime = 15;
    
    // Items & Drop.
    
    /**
     * The maximum items on the screen at once.
     */
    private int maximumItems;
    
    /**
     * The maximum multipliers on the screen at once.
     */
    private int maximumMultipliers;       
    
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
     * The level at which the difficulty begins to increase.
     */
    private int difficultyIncreaseLevel = 9;
    
    /**
     * The number of levels before the difficulty level increases.
     */
    private int levelDifficultySpeed = 3;
    
    // Wezzle tile.
        
    private int wezzleLineCount   = 0;
    private int wezzleMaximumTime = 3;
    private int wezzleTime        = wezzleMaximumTime;
    
    //--------------------------------------------------------------------------
	// Constructor
	//--------------------------------------------------------------------------
    
	/**
	 * The constructor.
	 * @param fragment
	 * @param board
	 * @param scoreManager
	 */
	private WorldManager(ListenerManager listenerMan)
	{				
        // Remember the listener manager.
        this.listenerMan = listenerMan;
        
        // Set the max items and mults.
        this.maximumItems       = 3;
        this.maximumMultipliers = 3;
        
        // Create the item list.
        itemMap = new EnumMap<TileType, Item>(TileType.class);                
        
        // Set the multipliers.
        multiplierMap = new EnumMap<TileType, Item>(TileType.class);        
        multiplierMap.put(TileType.X2, new Item.Builder(TileType.X2)
                .initialAmount(2).weight(50).maximumOnBoard(3).end());
        multiplierMap.put(TileType.X3, new Item.Builder(TileType.X3)
                .initialAmount(0).weight(20).maximumOnBoard(1).end());
        multiplierMap.put(TileType.X4, new Item.Builder(TileType.X4)
                .initialAmount(0).weight(10).maximumOnBoard(1).end());                
                          
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
    public static WorldManager newInstance(ListenerManager listenerMan)
    {
        return new WorldManager(listenerMan);
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
                itemMap.put(TileType.ROCKET, 
                        new Item.Builder(TileType.ROCKET)
                        .initialAmount(1).weight(55).maximumOnBoard(3).end());                
            }            
        });  
        
        // Make it so the bomb block is added.
        mutableList.add(new Rule(Rule.Type.LEVEL, Rule.Operation.GTEQ, 6)
        {
            @Override
            public void onMatch()
            {
                // Add the bomb.
                itemMap.put(TileType.GRAVITY,
                        new Item.Builder(TileType.GRAVITY)
                        .initialAmount(1).weight(50).maximumOnBoard(1).end());                
            }            
        });  
        
        // Make it so the bomb block is added.
        mutableList.add(new Rule(Rule.Type.LEVEL, Rule.Operation.GTEQ, 9)
        {
            @Override
            public void onMatch()
            {
                // Add the bomb.
                itemMap.put(TileType.BOMB,
                        new Item.Builder(TileType.BOMB)
                        .initialAmount(1).weight(10).maximumOnBoard(1).end());                
            }            
        }); 
        
        // Make it so the star block is added.
        mutableList.add(new Rule(Rule.Type.LEVEL, Rule.Operation.GTEQ, 12)
        {
            @Override
            public void onMatch()
            {
                // Add the star.
                itemMap.put(TileType.STAR,
                        new Item.Builder(TileType.STAR)
                        .initialAmount(0).weight(5).maximumOnBoard(1).end());                
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
                System.out.println(rule.getType() + "<-----------");
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
        // Increment initial amount of normal tiles.
        if (level != 1)
        {
            Item normalTiles = getItemOrMultiplier(TileType.NORMAL);
            normalTiles.setCurrentAmount(
                    (getItemOrMultiplier(TileType.NORMAL).getInitialAmount() + level) - 1);
        }

        // Set the level.
        this.level = level;								
	}	
	
    /**
     * @return the maximum number of items.
     */
    public int getMaximumItems()
    {
        return maximumItems;
    }
    
    /**
     * set the maximum number of items.
     */
    public void setMaximumItems(int maximum)
    {
        this.maximumItems = maximum;
    }    
    
    
     /**
     * @return the maximum number of mults.
     */
    public int getMaximumMultipliers()
    {
        return maximumMultipliers;
    }
    
    /**
     * set the maximum number of mults.
     */
    public void setMaximumMultipliers(int maximum)
    {
        this.maximumMultipliers = maximum;
    }                
    
	/**
	 * Increment the level.
	 */
	public void incrementLevel()
        {	
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
	
    public void addItem(Item item)
    {
        this.itemMap.put(item.getTileType(), item);
    }
    
    public void addMultiplier(Item multiplier)
    {
        this.multiplierMap.put(multiplier.getTileType(), multiplier);
    }
    
    public void removeItem(TileType type)
    {
        this.itemMap.remove(type);
    }
    
    public void removeItem(Item item)
    {
        this.removeItem(item.getTileType());
    }        
    
    public void removeMultiplier(TileType type)
    {
        this.multiplierMap.remove(type);
    }
    
    public void removeMultiplier(Item multiplier)
    {
        this.removeMultiplier(multiplier.getTileType());
    }
    
	/**
	 * Get the descriptor for the item with the specified class.
	 * Returns null if the class does not exist.
	 * 
	 * @param type
	 * @return 
	 */
	public Item getItemOrMultiplier(TileType type)
	{
		Item item = null;
        
        item = itemMap.get(type);
        if (item != null) return item;
        
        item = multiplierMap.get(type);
        if (item != null) return item;
        
        throw new RuntimeException("Attempted to get an item or multiplier that did not exist.");
	}
       
    /**
     * Gets a random item.
     * 
     * @return 
     */
    public Item getItem(int numberOfItems, int numberOfMultipliers)
	{	        
        // Check if we do not return  a normal tile. There is
        // a flat 5% chance of this.
        int test = Util.random.nextInt(100);
        if (test <= 5)
        {
            return itemMap.get(TileType.NORMAL);
        }

        // Build the list of items. This does not include items 
        // with probability 0 (if the max number exist) and includes
        // Mults as well.

        boolean useItems = false;
        boolean useMultipliers = false;
        final int CONSTANT = 15;        

        // calculate the probability of an item. The probability of a mult
        // is 1 - probability of item.
        int probItems = (numberOfMultipliers-numberOfItems) * CONSTANT + 50;
        //int probMults = (numItems-numMults) * constant + 50;

        // If there are no items in the game yet.
        if(itemMap.size() == 1)
            probItems = 0;

        // Select a number from 1 - 100.
        int pick = Util.random.nextInt(100);


        if (numberOfMultipliers < maximumMultipliers && numberOfItems < maximumItems)
        {
            if (pick < probItems)
            {
                useItems = true;
            }
            else
            {
                useMultipliers = true;
            }
        }
        else if (numberOfMultipliers < maximumMultipliers)
        {
            useMultipliers = true;
        }
        else if (numberOfItems < maximumItems)
        {
            useItems = true;
        }

        List<Item> itemList = new ArrayList<Item>();

        if (useItems == true)
        {
            for (Item item : itemMap.values())
            {
                // Skip the normal tile.
                if (item.getTileType() == TileType.NORMAL)
                    continue;

                if (item.getWeight() > 0)
                    itemList.add(item);
            }
        }

        if (useMultipliers == true)
        {  
            for (Item item : multiplierMap.values())
            {
                if (item.getWeight() > 0)
                    itemList.add(item);
            }
        }

        // If the list is empty, return a normal tile.
        if (itemList.size() <= 0)
        {
           return itemMap.get(TileType.NORMAL);
        }
                        
		// Create an array representing the item distribution 
            
		int dist[] = new int[itemList.size() + 1];
		dist[0] = 0;
		
		// Determine the distribution.
		int i = 1;
		for (Item item : itemList)
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
				return itemList.get(j - 1);
            }
		}
		
		// We should never get here.
		LogManager.recordWarning(
                "Random number out of range! (" + randomNumber + ").", 
                "WorldManager#getItem");
        
		return itemList.get(0);
	}
    
    /**
     * Get a list of all the items and multipliers.
     * 
	 * @return A list of all the items and multipliers.
	 */
	public List<Item> getItemList()
	{
        List<Item> itemList = new ArrayList<Item>();
        itemList.addAll(itemMap.values());
        itemList.addAll(multiplierMap.values());                    
		return itemList;
	}

    public int getMaximumTime()
    {
        return maximumTime;
    }

    public int getMinimumTime()
    {
        return minimumTime;
    }

    public int getWezzleTime()
    {
        return wezzleTime;
    }

    public void setWezzleTime(int wezzleTime)
    {
        this.wezzleTime = wezzleTime;
        
        this.listenerMan.notifyWezzleTimerChanged(
                new WezzleEvent(this, this.wezzleTime)); 
    }
    
    public void resetWezzleTime()
    {
        setWezzleTime(this.wezzleMaximumTime);
    }

    public int getWezzleMaximumTime()
    {
        return wezzleMaximumTime;
    }

    public void setWezzleMaximumTime(int wezzleMaximumTime)
    {
        this.wezzleMaximumTime = wezzleMaximumTime;
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
        itemMap.clear();
        
        itemMap.put(TileType.NORMAL, new Item.Builder(TileType.NORMAL)
                .initialAmount(28).weight(5).maximumOnBoard(100).end());
        
        itemMap.put(TileType.WEZZLE, new Item.Builder(TileType.WEZZLE)
                .initialAmount(0).weight(0).maximumOnBoard(4).end());
        
        itemMap.put(TileType.ROCKET, new Item.Builder(TileType.ROCKET)
                .initialAmount(0).weight(0).maximumOnBoard(1).end());
        
        itemMap.put(TileType.BOMB, new Item.Builder(TileType.BOMB)
                .initialAmount(0).weight(0).maximumOnBoard(1).end());  
        
        itemMap.put(TileType.STAR, new Item.Builder(TileType.STAR)
                .initialAmount(0).weight(0).maximumOnBoard(1).end());
        
        itemMap.put(TileType.GRAVITY, new Item.Builder(TileType.GRAVITY)
                .initialAmount(0).weight(0).maximumOnBoard(1).end());
        
        // Reset the rules.
        currentRuleList.clear();
        currentRuleList.addAll(masterRuleList);
    }

    public void levelChanged(LevelEvent e)
    {
        this.incrementLevel();
//        for (int i = 0; i < e.getLevel(); i++)
//            this.levelUp(e.getGame());
    }
    
    public void lineConsumed(LineEvent event, GameType gameType)
    {
        // Only worry about the Wezzle tile if it's in the item map.
        if (itemMap.containsKey(TileType.WEZZLE) == false)
            return;
         
        // Record how many lines were found.
        //this.wezzleLineCount += event.getLineCount();                                         
    }

    public void moveCommitted(MoveEvent event, GameType gameType)
    {
        // Intentionally left blank.        
    }

    public void moveCompleted(MoveEvent event)
    {
        // Only worry about the Wezzle tile if it's in the item map.
        if (itemMap.containsKey(TileType.WEZZLE) == false)
            return;    
        
//        if (this.wezzleLineCount == 0)
//        {
//            // Check to see if the wezzle was handled.  If not, throw an
//            // exception and just die already.
//            if (this.wezzleTime == 0)
//                throw new IllegalStateException("Wezzle tile not handled!");
//            
//            setWezzleTime(this.wezzleTime - 1);
//            LogManager.recordMessage(
//                    "Wezzle timer is at: " + this.wezzleTime);                                              
//        }
           
//        if (this.wezzleTime == 0)
//        {   
//            this.wezzleTime = this.wezzleMaximumTime;
//            Item wezzleItem = itemMap.get(TileType.WEZZLE);
//            wezzleItem.incrementCurrentAmount();
//            LogManager.recordMessage(
//                    "Wezzle infection increased: " + wezzleItem.getCurrentAmount());
//            
//            this.listenerMan.notifyWezzleTimerChanged(
//                    new WezzleEvent(this, this.wezzleTime));
//        }
        
        // Reset the line count.
        this.wezzleLineCount = 0;
    }
    
}
