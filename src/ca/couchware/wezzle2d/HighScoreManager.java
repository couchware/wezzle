/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ca.couchware.wezzle2d;

import ca.couchware.wezzle2d.util.Util;
import java.util.StringTokenizer;

/**
 *
 * @author Kevin
 */
public class HighScoreManager 
{
    private final static int NUM_SCORES = 10;

    /**
     * The property manager.
     */
    private PropertyManager propMan;
    
    /**
     * The list of high scores.
     */
    private HighScore[] scoreList;

    
    /**
     * Create a high score manager and fill it with 0's.
     */
    public HighScoreManager(PropertyManager properties)
    {
        this.scoreList = new HighScore[NUM_SCORES];
        
        this.propMan = properties;
        
        // If this is the first time running the game, we have no built list.
        // Every other time it will load the list from file.
        if(readFromProperties() == false)
        {
            HighScore dummyScore = new HighScore("Player", 0);
        
            // Load with dummy scores.
            for(int i = 0; i < scoreList.length; i++)
            {
                scoreList[i] = dummyScore;
            }
        }
    }
    
    /**
     * Get the highest score. Return position 0 in the list.
     * @return the high score.
     */
    public int getHighScore()
    {
        return this.scoreList[0].getScore();
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
        return this.scoreList[this.scoreList.length-1].getScore();
    }
    
    /**
     * Get the score list.
     * @return the score list.
     */
    public HighScore[] getList()
    {
        return this.scoreList;
    }
    
    /**
     * Add a score to the list. This assumes that the score should be in the
     * list. If the list has 10 values, the bottom value will be removed to
     * accommodate this value. This is because if it belongs in the list, and
     * the list is sorted, the bottom value will be the lowest value.
     * 
     * The insert into the list sorts the list.
     * 
     * @param key The key associated with the score.
     * @param score The score associated with the key.
     */
    public void addScore(String key, int score)
    {
        HighScore newScore = new HighScore(key, score);
        
        // Check the lowest value, just incase.
        if(newScore.getScore() < this.getLowestScore())
        {
            Util.handleMessage("Error, adding high score lower than the lowest"
                   , Thread.currentThread());
            
            System.exit(0);
        }
        
        // Add the score.
        this.scoreList[this.scoreList.length-1] = newScore;
        
        // Sort.
        this.bubbleUp();
        
        // Write to properties.
        writeToProperties();
    }
    
    /**
     * A method to bubble the bottom value in the array to the proper spot.
     * Will only move the bottom value up to the correct spot.
     */
    private void bubbleUp()
    {
        for(int i = this.scoreList.length-1; i > 0; i--)
        {
            // Swap.
            if(scoreList[i].getScore() > scoreList[i-1].getScore())
            {
                HighScore temp = scoreList[i-1];
                scoreList [i-1] = scoreList[i];
                scoreList[i] = temp;
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
    private void writeToProperties()
    {
        for (int i = 0; i < scoreList.length; i++)
        {
            this.propMan.setProperty("HIGHSCORE" + i, 
                    scoreList[i].getKey() + " " + scoreList[i].getScore());
        }
    }
    
    
    /**
     * Read the list from properties.
     * @return Whether the list was read or not.
     */
    private boolean readFromProperties()
    {
        for (int i = 0; i < scoreList.length; i++)
        {
            String temp = this.propMan.getStringProperty("HIGHSCORE" + i);
            
            // If the properties arent set, return false;
            if (temp.equals("on"))
                return false;
            
            //otherwise.
            StringTokenizer tokenizer = new StringTokenizer(temp); 
            String key = tokenizer.nextToken();
            int score = Integer.parseInt(tokenizer.nextToken());
            
            scoreList[i] = new HighScore(key, score);
           
        }
        
        return true;
    }
    
    /**
     * reset the list.
     */
    public void resetList()
    {
        HighScore dummyScore = new HighScore("Player", 0);
        
        // Load with dummy scores.
        for(int i = 0; i < scoreList.length; i++)
        {
            scoreList[i] = dummyScore;
        }
        
        // Save the properties.
        writeToProperties();
    }
    
    /**
     * Print the high score list to console.
     */
    public void printToConsole()
    {
        for(int i = 0; i < scoreList.length; i++)
        {
            System.out.println((i+1) +". " + scoreList[i].getKey() + " " + 
                    scoreList[i].getScore());
        }
    }
}
