/*
 *  Wezzle
 *  Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.manager;

import ca.couchware.wezzle2d.Game;
import ca.couchware.wezzle2d.ManagerHub;
import ca.couchware.wezzle2d.manager.BoardManager.AnimationType;
import ca.couchware.wezzle2d.tutorial.ITutorial;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author cdmckay
 */
public class TutorialManager implements IResettable
{

    /** The tutorial list. */
    private List<ITutorial> tutorialList;
       
    /** The currently running tutorial. */    
    private ITutorial tutorial;
    
    private TutorialManager()
    {
        // Initialize tutorial list.
        tutorialList  = new ArrayList<ITutorial>();        
    }
        
    /**
     * Returns a new tutorial manager instance.
     * 
     * @return
     */
    public static TutorialManager newInstance()
    {
        return new TutorialManager();
    }
    
    /**
     * Add a tutorial to the manager's list.
     * 
     * @param t
     */
    public void add(ITutorial t)
    {
        tutorialList.add(t);       
    }
    
    /**
     * Remove the tutorial to the manager's list.
     * 
     * @param t
     */
    public void remove(ITutorial t)
    {
        if (contains(t) == true)
            tutorialList.remove(t);
    }
    
    /**
     * Remove all tutorials from the manager.
     */
    public void clear()
    {
        tutorialList.clear();
    }
    
    /**
     * Check if the given tutorial is in the manager's list?
     * 
     * @param t
     * @return
     */
    public boolean contains(ITutorial t)
    {
        return tutorialList.contains(t);
    }   
    
    /**
     * Is a tutorial currently running?
     * 
     * @return True if a tutorial is running, false otherwise.
     */
    public boolean isTutorialRunning()
    {
        return tutorial != null;
    }

    /**
     * Get the currently running tutorial.
     * 
     * @return The currently running tutorial, or null, if no tutorial is running.
     */
    public ITutorial getRunningTutorial()
    {
        return tutorial;
    }
    
    /**
     * Finishes the currently running tutorial.
     * 
     * @param game
     */
    public void finishTutorial(Game game, ManagerHub hub)
    {
       if (this.tutorial != null)
       {
           tutorial.finish(game, hub);
           tutorial = null;
       }
    }
    
    public void updateLogic(Game game, ManagerHub hub)
    {
        // If the board is refactoring, do not logicify.
        if (game.isCompletelyBusy()) return;
        
        // If no tutorial is running, look for a new one to run.
        if (tutorial == null && !tutorialList.isEmpty())
        {
            for (Iterator<ITutorial> it = tutorialList.iterator(); it.hasNext(); ) 
            {
                ITutorial t = it.next();
                
                // Check to see if the tutorial is activated.                
                if (t.evaluateRules(game, hub))
                {       
                    if (hub.boardMan.isVisible())                    
                    {
                        // Hide the board nicely.
                        // Make sure the grid doesn't flicker on for a second.
                        game.startBoardHideAnimation(AnimationType.SLIDE_FADE);
                        hub.pieceMan.stopAnimation();
                        hub.pieceMan.hidePieceGrid();
                        hub.pieceMan.hideShadowPieceGrid();
                        break;
                    }
                    
                    tutorial = t;
                    tutorial.initialize(game, hub);
                    it.remove();                    
                    break;
                }                                                                    
            } // end for
        }    
        
        // See if there is a tutorial running.
        if (tutorial != null)
        {
            tutorial.updateLogic(game, hub);
            if (tutorial.isDone())
            {
                hub.boardMan.setVisible(false);
                game.startBoardShowAnimation(AnimationType.SLIDE_FADE);
                hub.pieceMan.hidePieceGrid();
                hub.pieceMan.hideShadowPieceGrid();
                tutorial = null;
            }
        } // end if       
    }                 
    
    public void resetState()
    {
        // Check to see if a tutorial is running, if it is, then we're
        // trying to reset the tutorial manager while it is running.
        if (tutorial != null)
            throw new IllegalStateException("Cannot close tutorial manager while a tutorial is running.");
        
        // Clear the tutorial list.
        this.tutorialList.clear();
    }
    
}
