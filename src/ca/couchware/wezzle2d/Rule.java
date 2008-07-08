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
    protected int test;
    protected int value;
    protected int type;
    
    //--------------------------------------------------------------------------
    // Constructor
    //--------------------------------------------------------------------------
    
    /**
     * The constructor constructs a tuple with a given value and test
     * 
     * @param type The type of rule this refers to.
     * @param test The test to be performed on the data.
     * @param value The value of the achievement data.     
     */
    public Rule(int type, int test, int value)
    {        
        this.type = type;
        this.test = test;
        this.value = value;
    }

    //--------------------------------------------------------------------------
    // Getters and Setters
    //--------------------------------------------------------------------------
            
    public int getTest() 
    {
        return test;
    }

    public void setTest(int test)
    {
        this.test = test;
    }

    public int getValue() 
    {
        return value;
    }

    public void setValue(int value) 
    {
        this.value = value;
    }

    public int getType() 
    {
        return type;
    }

    public void setType(int type) 
    {
        this.type = type;
    }
    
}
