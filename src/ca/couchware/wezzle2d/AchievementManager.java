
package ca.couchware.wezzle2d;

import ca.couchware.wezzle2d.util.Util;
import java.util.ArrayList;

/**
 * A class to manage achievements.
 * The manager holds an arraylist of achievements not yet achieved
 * and a list of achievements which have already been achieved
 * 
 * every iteration of the game, the manager passes the gamestate into all of
 * the achievements which check to see if they have been achieved. The 
 * achievments have an evaluate(gamestate) function which returns a boolean.
 * if the achievement has been achieved, the achievement is moved from the
 * arraylist into the achieved array list and displayed to the screen.
 *
 * @author Kevin
 */
public class AchievementManager 
{

    /** The unachieved achievements. */
    private ArrayList<Achievement> incomplete;
    
    /** The achieved achievements. */
    private ArrayList<Achievement> completed;
        
    public AchievementManager()
    {
        this.incomplete = new ArrayList();
        this.completed = new ArrayList();
    }
    
    /**
     * Add an achievement to the manager.
     * @param achieve The achievement.
     */
    public void addAchievement(Achievement achieve)
    {
        this.incomplete.add(achieve);
    }
    
    /**
     * Evaluate each achievement. If the achievement is completed
     * transfer from the incomplete to the completed lists.
     * 
     * @param gameState The state of the game.
     * @return True if an achievement was completed, false otherwise.
     */
    public boolean evaluate(Game gameState)
    {
        boolean achieved = false;
        
        for (int i = 0; i < incomplete.size(); i++)
        {
            Achievement temp = this.incomplete.get(i);
            
            if (temp.evaluate(gameState) == true)
            {
                this.completed.add(temp);
                this.incomplete.remove(i);
                achieved = true;
            }
        }
        
        return achieved;
    }
    
    /**
     * Report the completed descriptions to the console.
     */
    public void reportCompleted()
    {
        for (int i = 0; i < completed.size(); i++)
            Util.handleMessage(completed.get(i).getDescription(),
                    Thread.currentThread());
    }        
}
