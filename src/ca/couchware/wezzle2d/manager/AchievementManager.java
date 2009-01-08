/*
 *  Wezzle
 *  Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.manager;

import ca.couchware.wezzle2d.Game;
import ca.couchware.wezzle2d.event.CollisionEvent;
import ca.couchware.wezzle2d.event.ICollisionListener;
import ca.couchware.wezzle2d.manager.Settings.Key;
import ca.couchware.wezzle2d.tile.Tile;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

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
public class AchievementManager implements ICollisionListener
{

    /** The settings manager. */
    final private SettingsManager settingsMan;
    
    /** A flag that is set to true if an achievement has been completed. */
    private boolean achievementCompleted = false;
    
    /** The unachieved achievements. */
    private List<Achievement> incompleteList;
    
    /** 
     * The newly achieved achievements.  This list holds achievements until
     * they are asked for by <pre>getNewlyCompletedAchievements()</pre>.
     */
    private List<Achievement> newlyCompletedList;
    
    /** The master list */
    private List<Achievement> masterList;
        
    /**
     * The constructor.
     */
    private AchievementManager(SettingsManager settingsMan)
    {
        this.settingsMan = settingsMan;
        
        this.incompleteList     = new ArrayList<Achievement>();
        this.newlyCompletedList = new ArrayList<Achievement>();
        this.masterList         = new ArrayList<Achievement>();
        
        this.importAchievements();
    }
    
    /**
     * Return a new AchiementManager instance.
     * 
     * @return
     */
    public static AchievementManager newInstance(SettingsManager settingsMan)
    {
        return new AchievementManager(settingsMan);
    }
    
    /**
     * Import the achievements from the settings amanager.
     */
    final private void importAchievements()
    {        
        // Get the list from the settings manager. 
        List<Object> list = (List<Object>) settingsMan.getObject(Key.USER_ACHIEVEMENT);
        
        for (Object object : list)     
        {
            Achievement ach = (Achievement) object;
            
            // If the achievement has no completed date, then it is 
            // incomplete.
            if (ach.getDateCompleted() == null)
            {
                this.incompleteList.add(ach);
            }
            
            // Add to master list here so we dont have to rebuild it every 
            // time we click go to achievements potentially.
            this.masterList.add(ach);
        }
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
        
        for (Iterator<Achievement> it = incompleteList.iterator(); it.hasNext(); )
        {
            Achievement a = it.next();
            
            if (a.evaluate(game) == true)
            {
                // set the date.
                a.setCompleted();
                this.newlyCompletedList.add(a);
                it.remove();
                achieved = true;
                this.achievementCompleted = true;
            }
        }
        
        return achieved;
    }

    /**
     * Returns true if one or more achievement has recently been completed.
     * 
     * @return
     */
    public boolean isNewAchievementCompleted()
    {
        return achievementCompleted;
    }

    /**
     * Clears the achievement completed flag.
     * 
     * @param achievementCompleted
     */
    public void clearNewAchievementCompleted()
    {
        this.achievementCompleted = false;
    }        
    
    public List<Achievement> getNewlyCompletedAchievementList()
    {
        List<Achievement> list = new ArrayList<Achievement>(this.newlyCompletedList);
        this.newlyCompletedList.clear();
        return list;
    }
    
    /**
     * Report the completed descriptions to the console.
     */
    public void reportNewlyCompleted()
    {
        // Clear the achievement completed flag.
        this.achievementCompleted = false;
        
        for (int i = 0; i < newlyCompletedList.size(); i++)
            LogManager.recordMessage(newlyCompletedList.get(i).toString(),
                    "AcheivementManager#reportCompleted");
    }     
    
    /**
     * Listens for collision events.
     * 
     * @param e
     */
    public void collisionOccured(CollisionEvent e)
    {
        List<Tile> collisionList =  e.getCollisionList();

        StringBuffer buffer = new StringBuffer();

        for (Tile t : collisionList)
        {
           buffer.append(t.getType().toString() + " -> ");
        }

        buffer.append("END");

        LogManager.recordMessage(buffer.toString());
        
        for (Iterator<Achievement> it = incompleteList.iterator(); it.hasNext(); )
        {
            Achievement a = it.next();

            if (a.evaluateCollision(collisionList) == true)
            {
                a.setCompleted();
                this.newlyCompletedList.add(a);
                this.achievementCompleted = true;      
                it.remove();
            }
        } // end for   
    }        
    
    /**
     * Get the master list.
     * @return The master list.
     * 
     * Note: returns an unmodifiable list.
     */
    public List<Achievement> getMasterList()
    {
        return Collections.unmodifiableList(this.masterList);
    }    
    
    public int getNumberOfAchievements()
    {
        return this.masterList.size();
    }
    
    public int getNumberOfCompletedAchievements()
    {
        return this.masterList.size() - this.incompleteList.size();
    }

}
