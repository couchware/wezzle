package ca.couchware.wezzle2d.ui.group;

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
        assert hub != null;
        this.hub = hub;
                 
        // Create the high score header.
        this.headerLabel = new LabelBuilder(400, 171)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                .color(hub.settingsMan.getColor(Key.GAME_COLOR_PRIMARY)).size(26).text("High Scores")
                .visible(false).end();        
        this.entityList.add(this.headerLabel);     
        
        // Create the no high score label.
        this.noHighScore = new ITextLabel[2];
        
        this.noHighScore[0] = new LabelBuilder(400, 270)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                .color(hub.settingsMan.getColor(Key.GAME_COLOR_PRIMARY))
                .size(20).text("There are no")
                .visible(false).end();                
                
        this.noHighScore[1] = new LabelBuilder(400, 300)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                .color(hub.settingsMan.getColor(Key.GAME_COLOR_PRIMARY))
                .size(20).text("high scores yet.")
                .visible(false).end();                
        
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
                    .visible(false).end();
            this.scoreLabelList.add(label);            
            this.entityList.add(label);
        }       
        
        // Update the labels.
        this.updateScoreLabels();
        
        // Create close button.
        this.closeButton = new Button.Builder(400, 420)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))                
                .text("Close").normalOpacity(70).visible(false).end();        
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
    
    private void updateScoreLabels()
    {
        // Get the high score list.
        List<HighScore> list = hub.highScoreMan.getScoreList();
        
        for (int i = 0; i < scoreLabelList.size(); i++)
        {                        
            ITextLabel label = scoreLabelList.get(i);
            HighScore highScore = list.get(i);
                        
            // Change the text.
            label.setText(format(i, highScore));
            label.setOpacity(100);
        }                       
        
        // If no high scores exist, tell the user.
        final int op = list.isEmpty() ? 100 : 0;
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
        // Check if the back button was pressed.
        if (closeButton.isActivated())
        {            
            // Hide all side triggered menues.
            closeButton.setActivated(false);
            hub.groupMan.hideGroup(this);
        }       
    }
    
    private String format(int rank, HighScore highScore)
    {
        return (rank + 1) + ". " 
                //+ highScore.getName() + "  " 
                + highScore.getScore() + "  "
                + "L" + highScore.getLevel() + "";
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
        updateScoreLabels();
    }
    
}
