
package ca.couchware.wezzle2d;

import ca.couchware.wezzle2d.util.Util;

/**
 * An achievement will hold all the state information required for that 
 * achievement to be successfully completed. Each field will also hold whether
 * it is greater than, less than, or equal to the required value.
 * 
 * for example, an achievement can hold a value such as:  level < 10, 
 * lines = 100, moves < 15.
 *
 * achievemtns will be held within the achievement manager and take a game state
 * as a parameter to their update function. Every iteration of the game loop
 * will have the achievement compare its internal requirements to the game state
 * and evaluate whether or not it has been achieved. If it has been achieved,
 * the evaluate(gamestate) function will return true.
 * 
 * The achievement manager will simply iterate through the list of achievements
 * passing in the current game state and waiting for a response.
 * 
 * If the achievement has been successfully completed, it will be removed from
 * the achievement list and added to the completed list. This will dynamically
 * shrink the amount of searching necessary to check the achievements.
 * 
 * @author Kevin
 */
public class Achievement
{
    public static final int BRONZE = 0;
    public static final int SILVER = 1;
    public static final int GOLD = 2;
    public static final int PLATINUM = 3;
    
    private AchievementRule score;
    private AchievementRule level;
    private AchievementRule moves;
    private AchievementRule lines;
    private String description;
    private int type;
    

    
    public Achievement(AchievementRule score, AchievementRule level,  
           AchievementRule moves, AchievementRule lines, int type, 
           String description)
    {
        this.score = score;
        this.level = level;
        this.moves = moves;
        this.lines = lines;
        this.type = type;
        this.description = description;
    }
    
    /**
     * A method to evaluate an achievement to check if it has been completed.
     * 
     * @param gameState The state of the game.
     * @return Whether or not the achievement has been completed.
     */
    public boolean evaluate(Game gameState)
    {
        /**
         * use the private helper method to test if all of the fields
         * meet the requirements. any null values are automatically
         * accepted.
         */
        if (evaluateField(gameState.scoreMan.getTotalScore(), score)
                && evaluateField(gameState.worldMan.getLevel(), level)
                && evaluateField(gameState.moveMan.getMoveCount(), moves)
                && evaluateField(gameState.getTotalLineCount(), lines)
                )
        {
            return true;
        }
        else
        {
            return false;
        }
       
    }
    
    /**
     * A private method that compares the achievement tuple with the actual value
     * 
     * @param fieldVal The value of the field
     * @param comparator The achievement tuple comparator
     */
    private boolean evaluateField(int fieldVal, AchievementRule comparator)
    {
        if(comparator == null)
            return true;
        /**
         * If the test is successful, return true, otherwise, return false
         */
        switch(comparator.getTest())
        {
            case AchievementRule.GREATER_THAN:
                if (fieldVal > comparator.getValue())
                    return true;
                break;
            case AchievementRule.LESS_THAN:
                 if (fieldVal < comparator.getValue())
                    return true;
                break;
            case AchievementRule.EQUAL_TO:
                 if (fieldVal == comparator.getValue())
                    return true;
                break;
            default:
                Util.handleMessage("Error: Deafault reached", Thread.currentThread());
                System.exit(0);
                break;   
        }
        
        return false;
    }
    
    /**
     * Get the description of the achievement.
     * @return The description.
     */
    public String getDescription()
    {
        return this.description;
    }
}
