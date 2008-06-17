package ca.couchware.wezzle2d.ui.group;

import ca.couchware.wezzle2d.*;
import ca.couchware.wezzle2d.ui.button.*;

/**
 *
 * @author cdmckay
 */
public class HighScoreGroup extends Group
{
    /**
     * The header label.
     */
    private Label headerLabel;
    
    /**
     * The high score list.
     */
    public HighScore[] scoreList;
    
    /**
     * The header label.
     */
    private Label[] labels; 
    
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
    public HighScoreGroup(final GameWindow window, final LayerManager layerMan,
            HighScoreManager highScoreMan)
    {
        // Invoke super.
        super(window, layerMan);
        
        scoreList = highScoreMan.getList();
        
        // Create the options header.
        headerLabel = ResourceFactory.get().getLabel(400, 161);        
        headerLabel.setSize(26);
        headerLabel.setAlignment(Label.VCENTER | Label.HCENTER);
        headerLabel.setColor(Game.TEXT_COLOR);
        headerLabel.setText("High Scores");
        headerLabel.setVisible(false);
        layerMan.add(headerLabel, Game.LAYER_UI);
        entityList.add(headerLabel);
        
        labels = new Label[scoreList.length];
       
        for (int i = 0; i < labels.length; i++)
        {            
            labels[i] = ResourceFactory.get().getLabel(400, 191+(20*i));        
            labels[i].setSize(14);
            labels[i].setAlignment(Label.VCENTER | Label.HCENTER);
            labels[i].setColor(Game.TEXT_COLOR);
            labels[i].setText((i + 1) + ". " + scoreList[i].getKey() + " " 
                    + scoreList[i].getScore());
            labels[i].setVisible(false);
            layerMan.add(labels[i], Game.LAYER_UI);
            entityList.add(labels[i]);
            
        }

        // Create close button.
        closeButton = new RectangularBooleanButton(window, 400, 424);
        closeButton.setNormalOpacity(70);
        closeButton.setText("Close");
        closeButton.getLabel().setSize(18);
        closeButton.setAlignment(Button.VCENTER | Button.HCENTER);
        closeButton.setVisible(false);
        layerMan.add(closeButton, Game.LAYER_UI);
        entityList.add(closeButton);
    }
    
    @Override
    public void setVisible(boolean visible)
    {
        // Invoke super.
        super.setVisible(visible);
        
        // Adjust listeners.
        if (visible == true)
        {            
            window.addMouseListener(closeButton);
        }
        else
        {
            window.removeMouseListener(closeButton);
        }
    }
    
    public void update()
    {
         for(int i = 0; i < labels.length; i++)
        {
            labels[i].setText((i + 1) + ". " + scoreList[i].getKey() + " " 
                    + scoreList[i].getScore());   
        }
    }
    
    public boolean isCloseButtonActivated()
    {
        return this.closeButton.isActivated();
    }
    
    @Override
    public void setActivated(final boolean activated)
    {
        // Invoke super.
        super.setActivated(activated);
        
        // Make sure the pause button is activated.
        closeButton.setActivated(activated);
    }        
}
