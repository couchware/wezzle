/*
 *  Wezzle
 *  Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d;

import ca.couchware.wezzle2d.manager.Achievement;
import ca.couchware.wezzle2d.tile.Tile;
import ca.couchware.wezzle2d.tile.TileType;
import ca.couchware.wezzle2d.util.CouchLogger;
import java.util.ArrayList;
import java.util.List;

/*
 * A class that holds two associated values.
 * 
 * The achievement tuple will be used by achievements to set values and tests
 * for those values.
 * 
 * For example. if we have an achievement which requires a player to achieve
 * a score of greater than 2500 points. The value would be 2500 and the 
 * test would be GREATER_THAN.
 * 
 * @author Kevin
 */
public class Rule 
{
           
    /** The type of rule. */
    public static enum Type
    {
        SCORE, 
        LEVEL,         
        MOVES, 
        LINES,
        START_LEVEL,
        COLLISION,
        META
    };       
    
    /**
     * The operation being performed on the value.
     */
    public static enum Operation
    {
        LT, 
        LTEQ, 
        EQ, 
        GTEQ, 
        GT,
        BETWEEN
    }

     /**
     * The meta types.
     */
    public static enum MetaType
    {
      COMPLETE,
      INCOMPLETE
    }
    
    /** 
     * An achievement tuple contains a value and an associated test. 
     */
    protected final Type type;
    protected final Operation operation;
    protected final int value;    
    protected final TileType[] tileTypes;
    protected final List<String> achievementNames;
    protected final MetaType mType;
    
    //--------------------------------------------------------------------------
    // Constructor
    //--------------------------------------------------------------------------
    
    /**
     * The constructor constructs a tuple with a given value and test
     * 
     * @param type The type of rule, i.e. a rule that triggers based on score,
     *             level, etc.
     * @param operation The operation to perform, i.e. less than, equal to, etc.
     * @param value The value we are testing against, i.e. less than 2.
     */
    public Rule(Type type, Operation operation, int value)
    {   
        assert type      != null;
        assert operation != null;
        assert value > 0;
        
        this.type      = type;
        this.operation = operation;
        this.value     = value;
        this.tileTypes = new TileType[0];
        this.achievementNames = null;
        mType = MetaType.COMPLETE;
    }
    
    public Rule(Type type, Operation operation, TileType ... tileTypes)
    {
        assert type      != null;
        assert operation != null;
        assert tileTypes.length > 0;
        
        this.type      = type;
        this.operation = operation;
        this.tileTypes = tileTypes;
        this.value     = -1;
        this.achievementNames = null;
        mType = MetaType.COMPLETE;
    }
    
     public Rule(Type type, Operation operation, List<TileType> tileTypes)
    {
        assert type      != null;
        assert operation != null;
        assert tileTypes.size() > 0;
        
        this.type      = type;
        this.operation = operation;
        this.tileTypes = tileTypes.toArray(new TileType[0]);
        this.value     = -1;
        this.achievementNames = null;
        mType = MetaType.COMPLETE;
    }

     public Rule (Type type, Operation operation, int value, List<String> achievementNames, MetaType mType)
     {
        assert type      != null;
        assert operation != null;
        assert value > 0;

        this.type      = type;
        this.operation = operation;
        this.value     = value;
        this.tileTypes = new TileType[0];
        this.achievementNames = achievementNames;
        this.mType = mType;
     }
    
    public void onMatch()
    {
        // Optionally overridden.
    }
    
    //--------------------------------------------------------------------------
    // Static Methods
    //--------------------------------------------------------------------------
    
    /**
     * A public method that compares the achievement tuple with the actual 
     * value.  This will only give a reliable answer for non-COLLISION 
     * achievements.  It will always return false for collision type achievements.
     * 
     * @param game The game state.
     */
    public boolean evaluate(Game game, ManagerHub hub)
    {        
        // Sanity check.
        assert game != null;
        assert hub  != null;
        
        // Make sure we're not a COLLISION-type.
        if (this.type == Type.COLLISION)
            return false;

        // Make sure we're not a META-type.
        if (this.type == Type.META)
            return false;
        
        // Find the appropriate field value from the type.
        int x = -1;          
        // Check the type.
        switch (getType())
        {
            case SCORE:
                x = hub.scoreMan.getTotalScore();
                break;
                
            case LEVEL:
                x = hub.levelMan.getLevel();
                break;
                
            case MOVES:
                x = hub.statMan.getMoveCount();
                break;
                
            case LINES:
                x = hub.statMan.getLineCount();
                break;
                
            case START_LEVEL:
                x = hub.statMan.getStartLevel();
                break;
                
            default:
                throw new IllegalArgumentException("Unknown type.");                
        }
                                  
        // If the test is successful, return true, otherwise, return false         
        switch (getOperation())
        {
            case GT:
                if (x > getValue())
                    return true;
                break;
                
            case LT:
                 if (x < getValue())
                    return true;
                break;
                
            case EQ:
                 if (x == getValue())
                    return true;
                break;
                
            case GTEQ:
                if (x >= getValue())
                    return true;
                break;
                
            case LTEQ:
                 if (x <= getValue())
                    return true;
                break;
                
            default:
                throw new IllegalArgumentException("Unknown operation.");
        }
        
        return false;
    }
    
    /**
     * A special evaluate for collisions.  Will always return false for
     * non-collision achivements.
     * 
     * @param tileTypeList
     * @return
     */
    public boolean evaluateCollision(List<Tile> tileList)
    {
        if (this.type != Type.COLLISION)
            return false;
        
        if (tileList.size() < tileTypes.length)
            return false;
        
        assert tileList.size() >= tileTypes.length;
        
        for (int i = 0; i < tileTypes.length; i++)
        {
            if (tileTypes[i] != tileList.get(i).getType())
                return false;
        }
        
        return true;
    }

    /**
     * A special evaluate for meta acheivements. Will always return false for
     * non-collision achievements.
     *
     * @param achieveMentNameList The list of the achievementNames.
     * @return true or false.
     */
    public boolean evaluateMeta()
    {
        if(this.type != Type.META)
            return false;
        List<Achievement> achieveList = new ArrayList<Achievement>();
        ManagerHub hub = ManagerHub.get();

        if(getMetaType() == MetaType.COMPLETE)
            achieveList = hub.achievementMan.getCompletedAchievementList();
        else if(getMetaType() == MetaType.INCOMPLETE)
            achieveList = hub.achievementMan.getIncompletedAchievementList();
        else
        {
            CouchLogger.get().recordMessage(this.getClass(), "undefined meta type");
            System.exit(-1);
        }
        // Count the achievements in the achievement list that match the
        // names list.

         int x = 0;
         if (this.getAchievementNamesList() != null)
         {
            for(Achievement a : achieveList)
            {
                if(this.getAchievementNamesList().contains(a.getTitle()))
                    x++;
            }
         }
         // If there is no list, implicitly count the number of achievements.
         else
             x = achieveList.size();

        switch (getOperation())
        {
            case GT:
               if(x > this.getValue())
                   return true;
                break;

            case LT:
                 if (x < getValue())
                    return true;
                break;

            case EQ:
                 if (x == getValue())
                    return true;
                break;

            case GTEQ:
                if (x >= getValue())
                    return true;
                break;

            case LTEQ:
                 if (x <= getValue())
                    return true;
                break;

            default:
                throw new IllegalArgumentException("Unknown operation.");
        }

        return false;

    }

    //--------------------------------------------------------------------------
    // Getters and Setters
    //--------------------------------------------------------------------------
            
    public Operation getOperation() 
    {
        return operation;
    }   

    public int getValue() 
    {
        return value;
    }  
    
    public TileType[] getItemList()
    {
        return this.tileTypes;
    }

    public List<String> getAchievementNamesList()
    {
        return this.achievementNames;
    }

    public Type getType() 
    {
        return type;
    }

    public MetaType getMetaType()
    {
        return this.mType;
    }
    
}