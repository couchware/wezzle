/*
 *  Wezzle
 *  Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.manager;

import ca.couchware.wezzle2d.manager.Settings.Key;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * TODO Describe how class works.
 * 
 * @author Kevin, Cameron
 */
public class HighScoreManager 
{        
    
    /**
     * The number of high scores to keep track of.
     */
    public final static int NUMBER_OF_SCORES = 5;
    
    /**
     * The symbol indicating an empty name.
     */
    public final static String EMPTY_NAME = "";             
    
    /**
     * The list of high scores.
     */
    private List<HighScore> scoreList;       
    
    /**
     * Create a high score manager and fill it with 0's.
     * 
     * @param properytMan
     */
    private HighScoreManager()
    {
        // Initialize.
        this.scoreList = new ArrayList<HighScore>(NUMBER_OF_SCORES);
        this.importSettings();
    }
    
    
    /**
     * Returns a new instance of the high score manager.
     * 
     * @param propertyMan
     * @return
     */
    public static HighScoreManager newInstance()
    {
        return new HighScoreManager();
    }
    
    /**
     * Get the highest score. Return position 0 in the list.
     * @return the high score.
     */
    public int getHighestScore()
    {
        return this.scoreList.get(0).getScore();
    }
    
    /**
     * A method to check the lowest score in the list. Since the list is sorted
     * it returns the bottom score. This is used to see if a new score belongs
     * in the list.
     * 
     * @return the lowest score.
     */
    public int getLowestScore()
    {
        return this.scoreList.get(scoreList.size() - 1).getScore();
    }       
    
    /**
     * Add a score to the list. If the list has 10 values, 
     * the bottom value will be removed to
     * accommodate this value. This is because if it belongs in the list, and
     * the list is sorted, the bottom value will be the lowest value.
     * 
     * The insert into the list sorts the list.
     * 
     * @param name
     * @param score
     * @param level
     * @param export
     */    
    public void offerScore(String name, int score, int level, boolean export)
    {
        // See if the score belongs on the list.
        if (score < getLowestScore())
            return;
        
        HighScore newScore = HighScore.newInstance(name, score, level);       
        
        // Add the score.
        this.scoreList.set(NUMBER_OF_SCORES - 1, newScore);
        
        // Sort.
        this.bubbleUp();
        
        // Export if requested.
        if (export == true) this.exportSettings();
    }
    
    public void offerScore(String name, int score, int level)
    {
        offerScore(name, score, level, true);
    }
    
    public void offerScore(HighScore score, boolean export)
    {
        offerScore(score.getName(), score.getScore(), score.getLevel(), export);                
    }
    
    public void offerScore(HighScore score)
    {
        offerScore(score, true);
    }
    
    /**
     * A method to bubble the bottom value in the array to the proper spot.
     * Will only move the bottom value up to the correct spot.
     * 
     * Kevin is very proud of this method.
     */
    private void bubbleUp()
    {
        for (int i = scoreList.size() - 1; i > 0; i--)
        {
            // Swap.
            if (scoreList.get(i).getScore() > scoreList.get(i - 1).getScore())
            {
                HighScore swap = scoreList.get(i - 1);
                scoreList.set(i - 1, scoreList.get(i));
                scoreList.set(i, swap);
            }
            else
            {
                // If we have found the right spot. break.
                break;
            }
        }
    }
        
    /**
     * Write the list to properties.
     */
    private void exportSettings()
    {
        SettingsManager settingsMan = SettingsManager.get();
        
        settingsMan.setObject(Key.USER_HIGHSCORE, 
                Collections.unmodifiableList(scoreList));
    }
        
    /**
     * Read the list from properties.
     * 
     * @return Whether the list was read or not.
     */
    private void importSettings()
    {
        SettingsManager settingsMan = SettingsManager.get();
        
        // Reset the score list.
        resetScoreList();
        
        // Get the list from the settings manager.
        //List list = new ArrayList((List) settingsMan.getObject(Key.USER_HIGHSCORE));
        List list = (List) settingsMan.getObject(Key.USER_HIGHSCORE);
        
        for (Object object : list)     
        {
            HighScore score = (HighScore) object;            
            offerScore(score, false);                
        }
    }
    
    /**
     * Reset the list.
     */
    public void resetScoreList()
    {
        HighScore score = HighScore.newInstance("", 0, 0);
        scoreList.clear();
        
        // Load with dummy scores.
        for (int i = 0; i < NUMBER_OF_SCORES; i++)        
            scoreList.add(score);               
    }
    
    /**
     * Returns a copy of the high score list.
     */
    public List<HighScore> getScoreList()
    {
        return new ArrayList<HighScore>(scoreList);
    }         
    
}
