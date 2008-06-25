
package ca.couchware.wezzle2d;

import ca.couchware.wezzle2d.util.Util;
import java.util.ArrayList;

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
    public static final int DIFFICULTY_BRONZE = 0;
    public static final int DIFFICULTY_SILVER = 1;
    public static final int DIFFICULTY_GOLD = 2;
    public static final int DIFFICULTY_PLATINUM = 3;
    
    public static final int TYPE_SCORE = 0;
    public static final int TYPE_LEVEL = 1;
    public static final int TYPE_MOVES = 2;
    public static final int TYPE_LINES = 3;
    
    
    private ArrayList<AchievementRule> rules;
    private String description;
    private int difficulty;
    

    /**
     * The achievement is a list of rules which all have to be true for an
     * achievement to be achieved. As well as an associated text description 
     * and a difficulty type.
     * 
     * @param rules
     * @param description
     * @param difficulty
     */
    public Achievement(ArrayList<AchievementRule> rules, String description, 
            int difficulty)
    {
        this.rules = rules;
        this.description = description;
        this.difficulty = difficulty;
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
        
        for (int i = 0; i < rules.size(); i++)
        {
           if (this.evaluateField(rules.get(i), gameState) == false)
               return false;
        }
       
        return true;
       
    }
    
    /**
     * A private method that compares the achievement tuple with the actual value
     * 
     * @param fieldVal The value of the field
     * @param comparator The achievement tuple comparator
     */
    private boolean evaluateField(AchievementRule comparator, Game gameState)
    {
        
        // Find the appropriate field value from the type.
        int fieldVal = -1;
  
        switch(comparator.getType())
        {
            case TYPE_SCORE :
                fieldVal = gameState.scoreMan.getTotalScore();
                break;
                
            case TYPE_LEVEL:
                fieldVal = gameState.worldMan.getLevel();
                break;
            case TYPE_MOVES:
                fieldVal = gameState.moveMan.getMoveCount();
                break;
            case TYPE_LINES:
                fieldVal = gameState.getTotalLineCount();
                break;
                
            default:
                Util.handleWarning("Error:Invalid Type", Thread.currentThread());
                System.exit(0);
                break;
        }
      
        
        // sanity check.
        
        if (fieldVal == -1)
        {
             Util.handleMessage("Error: fieldVal -1", Thread.currentThread());
                System.exit(0);
        }
            
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
                
            case AchievementRule.GREATER_THAN_OR_EQUAL_TO:
                if (fieldVal >= comparator.getValue())
                    return true;
                break;
                
            case AchievementRule.LESS_THAN_OR_EQUAL_TO:
                 if (fieldVal <= comparator.getValue())
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
