/*
 *  Wezzle
 *  Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d;

import ca.couchware.wezzle2d.util.Util;
import java.util.StringTokenizer;

/**
 * TODO Describe how class works.
 * 
 * @author Kevin, Cameron
 */
public class HighScoreManager 
{        
    
    /**
     * The symbol indicating an empty name.
     */
    public final static String EMPTY_NAME = "-";        
    
    /**
     * The prefix to use in the properties file.
     */
    private final static String PREFIX = "wezzle.highScore";       
    
    /**
     * The number of high scores to keep track of.
     */
    private final static int NUMBER_OF_SCORES = 5;

    /**
     * The property manager.
     */
    private PropertyManager propertyMan;
    
    /**
     * The list of high scores.
     */
    private HighScore[] highScoreList;
    
    /**
     * Create a high score manager and fill it with 0's.
     * 
     * @param properytMan
     */
    public HighScoreManager(PropertyManager propertyMan)
    {
        // Initialize 
        this.highScoreList = new HighScore[NUMBER_OF_SCORES];
        
        // Set the property manager reference.
        this.propertyMan = propertyMan;
        
        // If this is the first time running the game, we have no built list.
        // Every other time it will load the list from file.
        if (readProperties() == false)
            resetScoreList();
    }
    
    /**
     * Get the highest score. Return position 0 in the list.
     * @return the high score.
     */
    public int getHighestScore()
    {
        return this.highScoreList[0].getScore();
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
        return this.highScoreList[highScoreList.length - 1].getScore();
    }       
    
    /**
     * Add a score to the list. If the list has 10 values, 
     * the bottom value will be removed to
     * accommodate this value. This is because if it belongs in the list, and
     * the list is sorted, the bottom value will be the lowest value.
     * 
     * The insert into the list sorts the list.
     * 
     * @param key The key associated with the score.
     * @param score The score associated with the key.
     */
    public void addScore(String name, int score, int level)
    {
        // See if the score belongs on the list.
        if (score < getLowestScore())
            return;
        
        HighScore newScore = HighScore.newInstance(name, score, level);       
        
        // Add the score.
        this.highScoreList[this.highScoreList.length - 1] = newScore;
        
        // Sort.
        this.bubbleUp();
        
        // Write to properties.
        writeProperties();
    }
    
    /**
     * A method to bubble the bottom value in the array to the proper spot.
     * Will only move the bottom value up to the correct spot.
     * 
     * Kevin is very proud of this method.
     */
    private void bubbleUp()
    {
        for (int i = this.highScoreList.length - 1; i > 0; i--)
        {
            // Swap.
            if (highScoreList[i].getScore() > highScoreList[i - 1].getScore())
            {
                HighScore temp = highScoreList[i - 1];
                highScoreList [i - 1] = highScoreList[i];
                highScoreList[i] = temp;
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
    private void writeProperties()
    {
        for (int i = 0; i < highScoreList.length; i++)
        {
            this.propertyMan.setProperty(PREFIX + i, 
                    highScoreList[i].getName() 
                    + " " + highScoreList[i].getScore()
                    + " " + highScoreList[i].getLevel());
        }
    }
    
    
    /**
     * Read the list from properties.
     * 
     * @return Whether the list was read or not.
     */
    private boolean readProperties()
    {
        for (int i = 0; i < highScoreList.length; i++)
        {
            String property = this.propertyMan.getStringProperty(PREFIX + i);
            
            // If the properties aren't set, return false.
            if (property == null)
                return false;
            
            // Otherwise, add to the high score list.
            StringTokenizer tokenizer = new StringTokenizer(property); 
            String key = tokenizer.nextToken();
            int score = Integer.parseInt(tokenizer.nextToken());
            int level = Integer.parseInt(tokenizer.nextToken());
            
            highScoreList[i] = HighScore.newInstance(key, score, level);           
        }
        
        return true;
    }
    
    /**
     * Reset the list.
     */
    public void resetScoreList()
    {
        HighScore dummyScore = HighScore.newInstance("-", 0, 0);
        
        // Load with dummy scores.
        for (int i = 0; i < highScoreList.length; i++)        
            highScoreList[i] = dummyScore;        
        
        // Save the properties.
        writeProperties();
    }
    
    /**
     * Returns a copy of the high score list.
     */
    public HighScore[] getScoreList()
    {
        return highScoreList.clone();
    }

    public int getNumberOfScores()
    {
        return NUMBER_OF_SCORES;
    }                
    
}
