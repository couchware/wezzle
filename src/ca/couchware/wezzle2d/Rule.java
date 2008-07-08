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
     * Is less than.
     */
    public static final int LESS_THAN = -2;
    
    /**
     * Is less than or equal to.
     */
    public static final int LESS_THAN_OR_EQUAL_TO = -1;
    
    /**
     * Is equal to.
     */
    public static final int EQUAL_TO = 0;
    
    /**
     * Is greater than or equal to.
     */
    public static final int GREATER_THAN_OR_EQUAL_TO = 1;
    
    /**
     * Is greater than.
     */
    public static final int GREATER_THAN = 2;   
    
    /**
     * Is this a score rule?
     */
    public static final int TYPE_SCORE = 0;
    
    /**
     * Is this a level rule?
     */
    public static final int TYPE_LEVEL = 1;
    
    /**
     * Is this a moves rule?
     */
    public static final int TYPE_MOVES = 2;
    
    /**
     * Is this a lines rule?
     */
    public static final int TYPE_LINES = 3;   
    
    /** An achievement tuple contains a value and an associated test */
    protected final int operation;
    protected final int value;
    protected final int type;
    
    //--------------------------------------------------------------------------
    // Constructor
    //--------------------------------------------------------------------------
    
    /**
     * The constructor constructs a tuple with a given value and test
     * 
     * @param type The type of rule, i.e. a rule that triggers based on score,
     *             level, etc.
     * @param op The operation to perform, i.e. less than, equal to, etc.
     * @param value The value we are testing against, i.e. less than 2.
     */
    public Rule(int type, int operation, int value)
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
            case Rule.TYPE_SCORE:
                value = game.scoreMan.getTotalScore();
                break;
                
            case Rule.TYPE_LEVEL:
                value = game.worldMan.getLevel();
                break;
                
            case Rule.TYPE_MOVES:
                value = game.moveMan.getMoveCount();
                break;
                
            case Rule.TYPE_LINES:
                value = game.getTotalLineCount();
                break;
                
            default:
                throw new IllegalArgumentException("Unknown type.");                
        }
                                  
        // If the test is successful, return true, otherwise, return false         
        switch (rule.getOperation())
        {
            case Rule.GREATER_THAN:
                if (value > rule.getValue())
                    return true;
                break;
                
            case Rule.LESS_THAN:
                 if (value < rule.getValue())
                    return true;
                break;
                
            case Rule.EQUAL_TO:
                 if (value == rule.getValue())
                    return true;
                break;
                
            case Rule.GREATER_THAN_OR_EQUAL_TO:
                if (value >= rule.getValue())
                    return true;
                break;
                
            case Rule.LESS_THAN_OR_EQUAL_TO:
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
            
    public int getOperation() 
    {
        return operation;
    }   

    public int getValue() 
    {
        return value;
    }  

    public int getType() 
    {
        return type;
    }   
    
}