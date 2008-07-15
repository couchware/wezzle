/*
 *  Wezzle
 *  Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d;

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
           
    /**
     * The type of rule.
     */
    public static enum Type
    {
        SCORE, LEVEL, MOVES, LINES
    };       
    
    /**
     * The operation being performed on the value.
     */
    public static enum Operation
    {
        LT, LTEQ, EQ, GTEQ, GT
    }
    
    /** 
     * An achievement tuple contains a value and an associated test. 
     */
    protected final Type type;
    protected final Operation operation;
    protected final int value;    
    
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
        this.type = type;
        this.operation = operation;
        this.value = value;
    }
    
    public void performAction(Game game)
    {
        // Optionally overridden.
    }
    
    //--------------------------------------------------------------------------
    // Static Methods
    //--------------------------------------------------------------------------
    
    /**
     * A private method that compares the achievement tuple with the actual 
     * value.
     * 
     * @param val The value of the field.
     * @param rule The achievement rule.
     */
    public static boolean evaluate(Rule rule, Game game)
    {        
        // Find the appropriate field value from the type.
        int value = -1;          
        
        // Check the type.
        switch (rule.getType())
        {
            case SCORE:
                value = game.scoreMan.getTotalScore();
                break;
                
            case LEVEL:
                value = game.worldMan.getLevel();
                break;
                
            case MOVES:
                value = game.moveMan.getMoveCount();
                break;
                
            case LINES:
                value = game.getTotalLineCount();
                break;
                
            default:
                throw new IllegalArgumentException("Unknown type.");                
        }
                                  
        // If the test is successful, return true, otherwise, return false         
        switch (rule.getOperation())
        {
            case GT:
                if (value > rule.getValue())
                    return true;
                break;
                
            case LT:
                 if (value < rule.getValue())
                    return true;
                break;
                
            case EQ:
                 if (value == rule.getValue())
                    return true;
                break;
                
            case GTEQ:
                if (value >= rule.getValue())
                    return true;
                break;
                
            case LTEQ:
                 if (value <= rule.getValue())
                    return true;
                break;
                
            default:
                throw new IllegalArgumentException("Unknown test.");
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

    public Type getType() 
    {
        return type;
    }   
    
}