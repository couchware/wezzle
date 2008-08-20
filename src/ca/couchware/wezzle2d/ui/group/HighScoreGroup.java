package ca.couchware.wezzle2d.ui.group;

import ca.couchware.wezzle2d.ui.IButton;
import ca.couchware.wezzle2d.ui.SpriteButton;
import ca.couchware.wezzle2d.LayerManager;
import ca.couchware.wezzle2d.*;
import ca.couchware.wezzle2d.LayerManager.Layer;
import ca.couchware.wezzle2d.ResourceFactory.LabelBuilder;
import ca.couchware.wezzle2d.ui.*;
import java.util.EnumSet;

/**
 *
 * @author cdmckay
 */
public class HighScoreGroup extends Group
{
    
    /**
     * A reference to the high score manager.
     */
    private HighScoreManager highScoreMan;
    
    /**
     * The header label.
     */
    private ILabel headerLabel;
    
    /**
     * The "no high score" label, line 1.
     */
    private ILabel noHighScoreLabel1;
    
    /**
     * The "no high score" label, line 2.
     */
    private ILabel noHighScoreLabel2;    
    
    /**
     * The player score labels.
     */
    private ILabel[] scoreLabels;
    
    /**
     * The close button.
     */
    private IButton closeButton;
    
    /**
     * The constructor.
     * 
     * @param window
     * @param layerMan
     */    
    public HighScoreGroup(final LayerManager layerMan, 
            final GroupManager groupMan,
            final HighScoreManager highScoreMan)
    {
        // Invoke super.
        super(layerMan, groupMan);
         
        // Save the reference.
        this.highScoreMan = highScoreMan;
        
        // Create the high score header.
        headerLabel = new LabelBuilder(400, 171)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                .color(Game.TEXT_COLOR1).size(26).text("High Scores")
                .visible(false).end();
        layerMan.add(headerLabel, Layer.UI);
        entityList.add(headerLabel);     
        
        // Create the no high score label.
        noHighScoreLabel1 = new LabelBuilder(400, 270)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                .color(Game.TEXT_COLOR1).size(20).text("There are no")
                .visible(false).end();
        layerMan.add(noHighScoreLabel1, Layer.UI);
        entityList.add(noHighScoreLabel1);
                
        noHighScoreLabel2 = new LabelBuilder(400, 300)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                .color(Game.TEXT_COLOR1).size(20).text("high scores yet.")
                .visible(false).end();
        layerMan.add(noHighScoreLabel2, Layer.UI);
        entityList.add(noHighScoreLabel2);                
        
        // Create the score labels.
        scoreLabels = new ILabel[highScoreMan.getNumberOfScores()];                
        
        // Create all the labels.
        for (int i = 0; i < scoreLabels.length; i++)
        {                      
            scoreLabels[i] = new LabelBuilder(400, 225 + (30 * i))
                    .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                    .color(Game.TEXT_COLOR1).opacity(0).size(16).text(" ")
                    .visible(false).end();
            layerMan.add(scoreLabels[i], Layer.UI);
            entityList.add(scoreLabels[i]);
        }       
        
        // Update the labels.
        updateLabels();
        
        // Create close button.
        closeButton = new SpriteButton.Builder(400, 408)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                .text("Close").offOpacity(70).visible(false).end();
        layerMan.add(closeButton, Layer.UI);
        entityList.add(closeButton);
    }
    
    @Override
    public void setVisible(boolean visible)
    {
        // This is more important than you think.  Basically, since we might
        // be adding or removing listeners, we want to make sure we only add
        // a listener once, and that we only remove it once.  This ensures that.
        if (isVisible() == visible)
            return;            
        
        // Invoke super.  This will remove the listener from pause which
        // we will re-add below.
        super.setVisible(visible);
    }
    
    public void updateLabels()
    {
        // This variable is set to true if there is a high score.
        boolean highScoreExists = false;
        
        // Get the high score list.
        HighScore[] highScoreList = highScoreMan.getScoreList();
        
        for (int i = 0; i < scoreLabels.length; i++)
        {                        
            // Skip if empty.
            if (highScoreList[i].getName().equals(HighScoreManager.EMPTY_NAME))
                continue;
            
            // A high score must exist then.
            highScoreExists = true;
              
            layerMan.remove(scoreLabels[i], Layer.UI);
            entityList.remove(scoreLabels[i]);
            scoreLabels[i] = new LabelBuilder(scoreLabels[i])
                .text(createScoreLabel(i, highScoreList[i])).opacity(100).end();
            layerMan.add(scoreLabels[i], Layer.UI);
            entityList.add(scoreLabels[i]);            
        }                       
        
        // If no high scores exist, tell the user.
        if (highScoreExists == false)
        {
            noHighScoreLabel1.setOpacity(100);
            noHighScoreLabel2.setOpacity(100);
        }
        else
        {
            noHighScoreLabel1.setOpacity(0);
            noHighScoreLabel2.setOpacity(0);
        }
    }    
    
    /**
     * Override the update logic method.
     * 
     * @param game The game state.
     */
    @Override
    public void updateLogic(Game game)
    {
        // Check if the back button was pressed.
        if (closeButton.isActivated() == true)
        {            
            // Hide all side triggered menues.
            closeButton.setActivated(false);
            game.groupMan.hideGroup(this);
        }       
    }
    
    private String createScoreLabel(int rank, HighScore highScore)
    {
        return (rank + 1) + ". " + highScore.getName() + "  " 
            + highScore.getScore() + "  L" + highScore.getLevel() + "";
    }
    
}
