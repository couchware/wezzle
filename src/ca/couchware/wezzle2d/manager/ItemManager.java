/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ca.couchware.wezzle2d.manager;

import ca.couchware.wezzle2d.Game;
import ca.couchware.wezzle2d.Rule;
import ca.couchware.wezzle2d.event.ILevelListener;
import ca.couchware.wezzle2d.event.LevelEvent;
import ca.couchware.wezzle2d.tile.TileType;
import ca.couchware.wezzle2d.util.Util;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author cdmckay
 */
public class ItemManager implements IManager, ILevelListener
{

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
     * The maximum items on the screen at once.
     */
    private int maximumItems;
    
    /**
     * The maximum multipliers on the screen at once.
     */
    private int maximumMultipliers;           
    
    private ItemManager()
    {
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
    
    public static ItemManager newInstance()
    {
        return new ItemManager();
    }
    
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
        mutableList.add(new Rule(Rule.Type.LEVEL, Rule.Operation.GTEQ, 4)
        {
            @Override
            public void onMatch()
            {
                LogManager.recordMessage("Gravity item enabled.");
                
                // Add the bomb.
                itemMap.put(TileType.GRAVITY,
                        new Item.Builder(TileType.GRAVITY)
                        .initialAmount(1).weight(50).maximumOnBoard(1).end());                
            }            
        });  
        
        // Make it so the bomb block is added.
        mutableList.add(new Rule(Rule.Type.LEVEL, Rule.Operation.GTEQ, 5)
        {
            @Override
            public void onMatch()
            {
                LogManager.recordMessage("Bomb item enabled.");
                
                // Add the bomb.
                itemMap.put(TileType.BOMB,
                        new Item.Builder(TileType.BOMB)
                        .initialAmount(1).weight(10).maximumOnBoard(1).end());                
            }            
        }); 
        
        // Make it so the star block is added.
        mutableList.add(new Rule(Rule.Type.LEVEL, Rule.Operation.GTEQ, 6)
        {
            @Override
            public void onMatch()
            {
                // Add the star.
                itemMap.put(TileType.STAR,
                        new Item.Builder(TileType.STAR)
                        .initialAmount(0).weight(10).maximumOnBoard(1).end());                
            }            
        });           
        
        return Collections.unmodifiableList(mutableList);
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
    
    //--------------------------------------------------------------------------
    // Logic
    //--------------------------------------------------------------------------
    
    public void evaluateRules(Game game)
    {
        for (Iterator<Rule> it = currentRuleList.iterator(); it.hasNext(); )
        {
            Rule rule = it.next();
            
            if (rule.evaluate(game) == true)
            {                
                rule.onMatch();
                it.remove();
            }
        } // end for
    }
    
    public void updateLogic(final Game game)
    {       
        // If the board is refactoring, do not logicify.
        if (game.isBusy() == true)
             return;                    
        
        evaluateRules(game);
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
        itemMap.clear();
        
        itemMap.put(TileType.NORMAL, new Item.Builder(TileType.NORMAL)
                .initialAmount(28).weight(5).maximumOnBoard(100).end());
        
        itemMap.put(TileType.WEZZLE, new Item.Builder(TileType.WEZZLE)
                .initialAmount(0).weight(100).maximumOnBoard(4).end());
        
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

    public void levelChanged(LevelEvent event)
    {
        int newLevel = event.getNewLevel();
        
        // Increment initial amount of normal tiles.
        if (newLevel != 1)
        {
            Item normalTiles = getItemOrMultiplier(TileType.NORMAL);
            normalTiles.setCurrentAmount((getItemOrMultiplier(TileType.NORMAL).getInitialAmount() + newLevel) - 1);
        }
    }
    
}
