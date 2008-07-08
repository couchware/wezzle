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
    public Rule(int type, int op, int value)
    {        
        this.type = type;
        this.operation = op;
        this.value = value;
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