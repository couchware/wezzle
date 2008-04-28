package ca.couchware.wezzle2d;

import ca.couchware.wezzle2d.button.*;

/**
 *
 * @author cdmckay
 */
public class GameOverGroup extends Group
{              
    /**
     * The header label.
     */
    private Label headerLabel;
    
    /**
     * The final score header label.
     */
    private Label scoreHeaderLabel;
    
    /**
     * The final score label.
     */
    private Label scoreLabel;
    
    /**
     * The restart button.
     */
    private RectangularBooleanButton restartButton;
    
    /**
     * The continue button.
     */
    private RectangularBooleanButton continueButton;
    
    /**
     * The constructor.
     * 
     * @param layerMan
     */    
    public GameOverGroup(final GameWindow window, final LayerManager layerMan)
    {        
        // Invoke super.
        super(window, layerMan);
        
        // Create the game over header.
        headerLabel = ResourceFactory.get().getLabel(400, 181);        
        headerLabel.setSize(26);
        headerLabel.setAlignment(Label.VCENTER | Label.HCENTER);
        headerLabel.setColor(Game.TEXT_COLOR);
        headerLabel.setText("Game over :(");
        headerLabel.setVisible(false);
        layerMan.add(headerLabel, Game.LAYER_UI);
        entityList.add(headerLabel);
        
        // Create the final score header.
        scoreHeaderLabel = ResourceFactory.get().getLabel(400, 234);        
        scoreHeaderLabel.setSize(14);
        scoreHeaderLabel.setAlignment(Label.VCENTER | Label.HCENTER);
        scoreHeaderLabel.setColor(Game.TEXT_COLOR);
        scoreHeaderLabel.setText("Your final score was");
        scoreHeaderLabel.setVisible(false);
        layerMan.add(scoreHeaderLabel, Game.LAYER_UI); 
        entityList.add(scoreHeaderLabel);
        
        // Create the final score label.
        scoreLabel = ResourceFactory.get().getLabel(400, 270);        
        scoreLabel.setSize(30);
        scoreLabel.setAlignment(Label.VCENTER | Label.HCENTER);
        scoreLabel.setColor(Game.TEXT_COLOR);
        scoreLabel.setText("0");
        scoreLabel.setVisible(false);
        layerMan.add(scoreLabel, Game.LAYER_UI); 
        entityList.add(scoreLabel);
        
        // Create restart button.
        restartButton = new RectangularBooleanButton(400, 345);        
        restartButton.setNormalOpacity(70);
        restartButton.setText("Restart");
        restartButton.getLabel().setSize(18);
        restartButton.setAlignment(Button.VCENTER | Button.HCENTER);
        restartButton.setVisible(false);
        layerMan.add(restartButton, Game.LAYER_UI);
        entityList.add(restartButton);
        
        // Create continue button.
        continueButton = new RectangularBooleanButton(400, 406);
        continueButton.setNormalOpacity(70);
        continueButton.setText("Continue");
        continueButton.getLabel().setSize(18);
        continueButton.setAlignment(Button.VCENTER | Button.HCENTER);
        continueButton.setVisible(false);
        layerMan.add(continueButton, Game.LAYER_UI);
        entityList.add(continueButton);
    } 
    
    @Override
    public void setVisible(boolean visible)
    {
        // Invoke super.
        super.setVisible(visible);
        
        // Adjust listeners.
        if (visible == true)
        {            
            window.addMouseListener(restartButton);
            window.addMouseMotionListener(restartButton);
            window.addMouseListener(continueButton);
            window.addMouseMotionListener(continueButton);
        }
        else
        {
            window.removeMouseListener(restartButton);
            window.removeMouseMotionListener(restartButton);
            window.removeMouseListener(continueButton);
            window.removeMouseMotionListener(continueButton);
        }
    }
    
    public void setScore(final int score)
    {
        scoreLabel.setText(String.valueOf(score));
    }
    
    public int getScore()
    {
        return Integer.valueOf(scoreLabel.getText());
    }

    public boolean buttonClicked()
    {
        return restartButton.clicked() || continueButton.clicked();
    }
    
    public boolean isRestartActivated()
    {
        return restartButton.isActivated();
    }
    
    public boolean isContinueActiavted()
    {
        return continueButton.isActivated();
    }
    
    public void resetButtons()
    {
        restartButton.setActivated(false);
        continueButton.setActivated(false);
    }
            
}
