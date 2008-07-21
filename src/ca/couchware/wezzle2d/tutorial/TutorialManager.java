/*
 *  Wezzle
 *  Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.tutorial;

import ca.couchware.wezzle2d.*;
import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 * @author cdmckay
 */
public class TutorialManager 
{

    /**
     * The tutorial list.
     */
    private ArrayList<Tutorial> tutorialList;
    
    /**
     * The currently running tutorial.
     */    
    private Tutorial currentTutorial;
    
    public TutorialManager()
    {
        // Initialize animation list.
        tutorialList = new ArrayList<Tutorial>();                
    }
    
    /**
     * Add a tutorial to the manager's list.
     * 
     * @param t
     */
    public void add(Tutorial t)
    {
        tutorialList.add(t);       
    }
    
    /**
     * Remove the tutorial to the manager's list.
     * 
     * @param t
     */
    public void remove(Tutorial t)
    {
        if (contains(t) == true)
            tutorialList.remove(t);
    }
    
    /**
     * Check if the given tutorial is in the manager's list?
     * 
     * @param t
     * @return
     */
    public boolean contains(Tutorial t)
    {
        return tutorialList.contains(t);
    }   
    
    /**
     * Is a tutorial currently running?
     * 
     * @return True if a tutorial is running, false otherwise.
     */
    public boolean isTutorialInProgress()
    {
        return currentTutorial != null;
    }

    /**
     * Get the currently running tutorial.
     * 
     * @return The currently running tutorial, or null, if no tutorial is running.
     */
    public Tutorial getTutorialInProgress()
    {
        return currentTutorial;
    }
    
    public void updateLogic(Game game)
    {
        // If the board is refactoring, do not logicify.
        if (game.isBusy() == true)
             return;
        
        // See if there is a tutorial running.
        if (currentTutorial != null)
        {
            currentTutorial.updateLogic(game);
            if (currentTutorial.isDone() == true)
                currentTutorial = null;
        }
        
        // If no tutorial is running, look for a new one to run.
        if (currentTutorial == null && tutorialList.isEmpty() == false)
        {
            for (Iterator<Tutorial> it = tutorialList.iterator(); it.hasNext(); ) 
            {
                Tutorial t = it.next();
                t.updateLogic(game);
                
                // Check to see if this tutorial activated.
                if (t.isActivated() == true)
                {
                    currentTutorial = t;
                    it.remove();
                    break;
                }
            } // end for
        }        
        
        //Util.handleMessage(animationList.size() + "", "AnimationManager#animate");
    }
    
}
