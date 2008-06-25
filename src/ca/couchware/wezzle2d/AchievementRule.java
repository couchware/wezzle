/*
 * A class that holds two associated values.
 * 
 * The Achievement tuple will be used by achievements to set values and tests
 * for those values.
 * 
 * For example. if we have an achievement which requires a player to achieve
 * a score of greater than 2500 points. The value would be 2500 and the 
 * test would be GREATER_THAN.
 */

package ca.couchware.wezzle2d;

/**
 *
 * @author Kevin
 */
public class AchievementRule 
{

    public static final int GREATER_THAN = 0;
    public static final int LESS_THAN = 1;
    public static final int EQUAL_TO = 2;
    public static final int GREATER_THAN_OR_EQUAL_TO = 3;
    public static final int LESS_THAN_OR_EQUAL_TO = 4;
    
    
    /** An achievement tuple contains a value and an associated test */
    private int test;
    private int value;
    
    /**
     * The constructor constructs a tuple with a given value and test
     * @param value The value of the achievement data
     * @param test The test to be performed on the data.
     */
    public AchievementRule(int value, int test)
    {
        this.value = value;
        this.test = test;
    }

    /** Getters and Setters */
            
    public int getTest() {
        return test;
    }

    public void setTest(int test) {
        this.test = test;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
    
    
    
}
