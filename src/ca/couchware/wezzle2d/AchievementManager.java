/*
 *  Wezzle
 *  Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d;

import ca.couchware.wezzle2d.util.Util;
import java.util.Iterator;
import java.util.LinkedList;

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
    private LinkedList<Achievement> incompleteList;
    
    /** The achieved achievements. */
    private LinkedList<Achievement> completeList;
        
    /**
     * The constructor.
     */
    private AchievementManager()
    {
        this.incompleteList = new LinkedList<Achievement>();
        this.completeList = new LinkedList<Achievement>();
    }
    
    // Public API.
    public static AchievementManager newInstance()
    {
        return new AchievementManager();
    }
    
    /**
     * Add an achievement to the manager.
     * @param achieve The achievement.
     */
    public void add(Achievement achievement)
    {
        this.incompleteList.add(achievement);
    }
    
    /**
     * Evaluate each achievement. 
     * If the achievement is completed transfer from the incomplete to 
     * the completed lists.
     * 
     * @param game The state of the game.
     * @return True if an achievement was completed, false otherwise.
     */
    public boolean evaluate(Game game)
    {
        boolean achieved = false;
        
        for (Iterator<Achievement> it = incompleteList.iterator(); 
                it.hasNext(); )
        {
            Achievement a = it.next();
            
            if (a.evaluate(game) == true)
            {
                this.completeList.add(a);
                it.remove();
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
        for (int i = 0; i < completeList.size(); i++)
            LogManager.recordMessage(completeList.get(i).getDescription(),
                    "AcheivementManager#reportCompleted");
    }        
}
