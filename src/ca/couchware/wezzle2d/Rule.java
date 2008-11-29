/*
 *  Wezzle
 *  Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d;

import ca.couchware.wezzle2d.tile.TileEntity;
import ca.couchware.wezzle2d.tile.TileType;

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
import ca.couchware.wezzle2d.util.Util;
import java.util.List;
public class Rule 
{
           
    /** The type of rule. */
    public static enum Type
    {
        SCORE, 
        LEVEL, 
        MOVES, 
        LINES,
        COLLISION
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
     * An achievement tuple contains a value and an associated test. 
     */
    protected final Type type;
    protected final Operation operation;
    protected final int value;    
    protected final TileType[] tileTypes;
    
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
    public boolean evaluate(Game game)
    {        
        // Make sure we're not a COLLISION-type.
        if (this.type == Type.COLLISION)
            return false;
        
        // Find the appropriate field value from the type.
        int x = -1;          
        
        // Check the type.
        switch (getType())
        {
            case SCORE:
                x = game.scoreMan.getTotalScore();
                break;
                
            case LEVEL:
                x = game.worldMan.getLevel();
                break;
                
            case MOVES:
                x = game.statMan.getMoveCount();
                break;
                
            case LINES:
                x = game.statMan.getLineCount();
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
    public boolean evaluateCollision(List<TileEntity> tileList)
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

    public Type getType() 
    {
        return type;
    }   
    
}