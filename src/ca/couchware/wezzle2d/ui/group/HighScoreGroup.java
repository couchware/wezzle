package ca.couchware.wezzle2d.ui.group;

import ca.couchware.wezzle2d.LayerManager;
import ca.couchware.wezzle2d.*;
import ca.couchware.wezzle2d.ResourceFactory.LabelBuilder;
import ca.couchware.wezzle2d.ui.*;
import ca.couchware.wezzle2d.ui.button.*;
import java.util.EnumSet;

/**
 *
 * @author cdmckay
 */
public class HighScoreGroup extends Group
{
    
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
     * The high score list.
     */
    public HighScore[] highScoreList;        
    
    /**
     * The player score labels.
     */
    private ILabel[] scoreLabels;
    
    /**
     * The close button.
     */
    private RectangularBooleanButton closeButton;
    
    /**
     * The constructor.
     * 
     * @param window
     * @param layerMan
     */    
    public HighScoreGroup(final GameWindow window,             
            final LayerManager layerMan, final GroupManager groupMan,
            final HighScoreManager highScoreMan)
    {
        // Invoke super.
        super(window, layerMan, groupMan);
        
        // Get the high score list.
        highScoreList = highScoreMan.getHighScoreList();
        
        // Create the high score header.
//        headerLabel = ResourceFactory.get().getLabel(400, 171);        
//        headerLabel.setSize(26);
//        headerLabel.setAlignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER));
//        headerLabel.setColor(Game.TEXT_COLOR);
//        headerLabel.setText("High Scores");
//        headerLabel.setVisible(false);
        headerLabel = new LabelBuilder(400, 171)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                .color(Game.TEXT_COLOR).size(26).text("High Scores")
                .visible(false).end();
        layerMan.add(headerLabel, Game.LAYER_UI);
        entityList.add(headerLabel);     
        
        // Create the no high score label.
//        noHighScoreLabel1 = ResourceFactory.get().getLabel(400, 270);
//        noHighScoreLabel1.setSize(20);
//        noHighScoreLabel1.setAlignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER));
//        noHighScoreLabel1.setColor(Game.TEXT_COLOR);
//        noHighScoreLabel1.setText("There are no");
//        noHighScoreLabel1.setVisible(false);
        noHighScoreLabel1 = new LabelBuilder(400, 270)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                .color(Game.TEXT_COLOR).size(20).text("There are no")
                .visible(false).end();
        layerMan.add(noHighScoreLabel1, Game.LAYER_UI);
        entityList.add(noHighScoreLabel1);
        
//        noHighScoreLabel2 = ResourceFactory.get().getLabel(400, 300);
//        noHighScoreLabel2.setSize(20);
//        noHighScoreLabel2.setAlignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER));
//        noHighScoreLabel2.setColor(Game.TEXT_COLOR);
//        noHighScoreLabel2.setText("high scores yet.");
//        noHighScoreLabel2.setVisible(false);
        noHighScoreLabel2 = new LabelBuilder(400, 300)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                .color(Game.TEXT_COLOR).size(20).text("high scores yet")
                .visible(false).end();
        layerMan.add(noHighScoreLabel2, Game.LAYER_UI);
        entityList.add(noHighScoreLabel2);                
        
        // Create the score labels.
        scoreLabels = new ILabel[highScoreList.length];                
        
        // Create all the labels.
        for (int i = 0; i < scoreLabels.length; i++)
        {           
//            scoreLabels[i] = ResourceFactory.get()
//                    .getLabel(400, 225 + (30 * i));        
//            scoreLabels[i].setSize(16);
//            scoreLabels[i].setAlignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER));
//            scoreLabels[i].setColor(Game.TEXT_COLOR);
//            scoreLabels[i].setText(" "); // hack
//            scoreLabels[i].setVisible(false);
//            scoreLabels[i].setOpacity(0); // hack
            scoreLabels[i] = new LabelBuilder(400, 225 + (30 * i))
                    .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                    .color(Game.TEXT_COLOR).opacity(0).size(16).text(" ")
                    .visible(false).end();
            layerMan.add(scoreLabels[i], Game.LAYER_UI);
            entityList.add(scoreLabels[i]);
        }       
        
        // Update the labels.
        updateScoreLabels();
        
        // Create close button.
        closeButton = new RectangularBooleanButton(window, 400, 408, "Close");
        closeButton.setNormalOpacity(70);
//        closeButton.setText("Close");
//        closeButton.getLabel().setSize(18);
        closeButton.setAlignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER));
        closeButton.setVisible(false);
        layerMan.add(closeButton, Game.LAYER_UI);
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
    
    public void updateScoreLabels()
    {
        // This variable is set to true if there is a high score.
        boolean highScoreExists = false;
        
        for (int i = 0; i < scoreLabels.length; i++)
        {                        
            // Skip if empty.
            if (highScoreList[i].getKey().equals(HighScoreManager.EMPTY_NAME))
                continue;
            
            // A high score must exist then.
            highScoreExists = true;
              
            scoreLabels[i] = new LabelBuilder(scoreLabels[i])
                    .text((i + 1) + ". " + highScoreList[i].getKey() + " " 
                    + highScoreList[i].getScore()).opacity(100).end();
            
//            scoreLabels[i].setText((i + 1) + ". " 
//                    + highScoreList[i].getKey() + " " 
//                    + highScoreList[i].getScore()); 
//            scoreLabels[i].setOpacity(100);
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
    
}
