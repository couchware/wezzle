package ca.couchware.wezzle2d.group;

import ca.couchware.wezzle2d.Game;
import ca.couchware.wezzle2d.ManagerHub;
import ca.couchware.wezzle2d.event.GameEvent;
import ca.couchware.wezzle2d.manager.HighScoreManager;
import ca.couchware.wezzle2d.manager.HighScore;
import ca.couchware.wezzle2d.manager.LayerManager.Layer;
import ca.couchware.wezzle2d.ResourceFactory.LabelBuilder;
import ca.couchware.wezzle2d.event.IGameListener;
import ca.couchware.wezzle2d.graphics.IEntity;
import ca.couchware.wezzle2d.manager.Settings.Key;
import ca.couchware.wezzle2d.ui.Button;
import ca.couchware.wezzle2d.ui.IButton;
import ca.couchware.wezzle2d.ui.ITextLabel;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

/**
 *
 * @author cdmckay
 */
public class HighScoreGroup extends AbstractGroup implements IGameListener
{
    
    /**
     * A reference to the layer manager.  This is used by groups to add
     * and remove things like buttons and sliders.
     */
    final private ManagerHub hub;          
    
    /** The header label. */
    private ITextLabel headerLabel;
    
    /** The "no high score" array... contains text labels saying that. */
    private ITextLabel[] noHighScore;
    
    /** The player score labels. */
    private List<ITextLabel> scoreLabelList;
    
    /** The close button. */
    private IButton closeButton;
    
    /**
     * The constructor.
     * @param window
     * @param layerMan
     */    
    public HighScoreGroup(ManagerHub hub)
    {
        // Save a reference to the hub.
        if(hub == null)
            throw new IllegalArgumentException("hub must not be null.");
        this.hub = hub;
                 
        // Create the high score header.
        this.headerLabel = new LabelBuilder(400, 171)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                .color(hub.settingsMan.getColor(Key.GAME_COLOR_PRIMARY)).size(26).text("High Scores")
                .visible(false).build();
        this.entityList.add(this.headerLabel);
        
        // Create the no high score label.
        this.noHighScore = new ITextLabel[2];
        
        this.noHighScore[0] = new LabelBuilder(400, 270)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                .color(hub.settingsMan.getColor(Key.GAME_COLOR_PRIMARY))
                .size(20).text("There are no")
                .visible(false).build();
                
        this.noHighScore[1] = new LabelBuilder(400, 300)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                .color(hub.settingsMan.getColor(Key.GAME_COLOR_PRIMARY))
                .size(20).text("high scores yet.")
                .visible(false).build();
        
        for (ITextLabel label : noHighScore)
        {
            this.entityList.add(label);
        }
        
        // Create the score labels.
        this.scoreLabelList = new ArrayList<ITextLabel>(HighScoreManager.NUMBER_OF_SCORES);
        
        // Create all the labels.
        for (int i = 0; i < HighScoreManager.NUMBER_OF_SCORES; i++)
        {                      
            ITextLabel label = new LabelBuilder(400, 225 + (35 * i))
                    .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                    .color(hub.settingsMan.getColor(Key.GAME_COLOR_PRIMARY)).opacity(0).size(16).text(" ")
                    .visible(false).build();
            this.scoreLabelList.add(label);            
            this.entityList.add(label);
        }       
        
        // Update the labels.
        this.updateScoreLabels(this.scoreLabelList, hub.highScoreMan.getScoreList());
        
        // Create close button.
        this.closeButton = new Button.Builder(400, 420)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))                
                .text("Close").normalOpacity(70).visible(false).build();
        this.entityList.add(this.closeButton);
        
        // Add them all to the layer man.
        for (IEntity e : this.entityList)
        {
            hub.layerMan.add(e, Layer.UI);
        }
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
    
    /**
     * Update a label list using a high score list.
     * @param labelList
     * @param scoreList
     */
    private void updateScoreLabels(List<ITextLabel> labelList, List<HighScore> scoreList)
    {
        for (int i = 0; i < labelList.size(); i++)
        {
            ITextLabel label = labelList.get(i);

            if (i < scoreList.size())
            {
                HighScore highScore = scoreList.get(i);

                // Change the text.
                label.setText(format(i, highScore));
                label.setOpacity(100);
            }
            else
            {
                label.setText("");
            }
        } // end for

        // If no high scores exist, tell the user.
        final int op = scoreList.isEmpty() ? 100 : 0;
        for ( ITextLabel label : this.noHighScore )
        {
            label.setOpacity(op);
        }
    }
    
    /**
     * Controls the group's logic.
     * @param game The game state.
     */    
    public void updateLogic(Game game, ManagerHub hub)
    {
        // Sanity check.
        if (game == null)
            throw new IllegalArgumentException("Game must not be null");

        if (hub == null)
            throw new IllegalArgumentException("Hub must not be null");

        // Make sure something changed.
        if ( !this.controlChanged() ) return;

        // Check if the back button was pressed.
        if (closeButton.isActivated())
        {            
            // Hide all side triggered menues.
            closeButton.setActivated(false);
            hub.groupMan.hideGroup(this, !game.isCompletelyBusy());
        }

        // Clear the change setting.
        this.clearChanged();
    }
    
    private String format(int rank, HighScore highScore)
    {
        return String.format("%d. %,d L%,d %s",
                rank + 1, 
                highScore.getScore(),
                highScore.getLevel(),
                highScore.getDifficulty().getDescription());
    }

    public void gameStarted(GameEvent event)
    {
        // Intentionally blank.
    }

    public void gameReset(GameEvent event)
    {
        // Intentionally blank.
    }

    public void gameOver(GameEvent event)
    {
        // Update all the labels.
        this.updateScoreLabels(this.scoreLabelList, hub.highScoreMan.getScoreList());
    }
    
}
