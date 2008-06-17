package ca.couchware.wezzle2d.ui.group;

import ca.couchware.wezzle2d.*;
import ca.couchware.wezzle2d.ui.*;
import ca.couchware.wezzle2d.ui.button.*;

/**
 *
 * @author cdmckay
 */
public class OptionsGroup extends Group
{
    /**
     * The options button. 
     */
    private RectangularBooleanButton optionsButton;    
    
    /**
     * The header label.
     */
    private Label headerLabel;
    
    /**
     * The help button.
     */
    private RectangularBooleanButton helpButton;
    
    /**
     * The audio button.
     */
    private RectangularBooleanButton audioButton;

    /**
     * The main menu button.
     */
    private RectangularBooleanButton mainMenuButton;

    /**
     * The back button.
     */
    private RectangularBooleanButton backButton;    
    
    /**
     * The constructor.
     * 
     * @param window
     * @param layerMan
     */    
    public OptionsGroup(final GameWindow window, final LayerManager layerMan)
    {
        // Invoke super.
        super(window, layerMan);
        
        // Create the options button.
        optionsButton = new RectangularBooleanButton(window, 668, 299);
        optionsButton.setNormalOpacity(70);
        optionsButton.setText("Options");
        optionsButton.getLabel().setSize(18);
        optionsButton.setAlignment(Button.VCENTER | Button.HCENTER);
        layerMan.add(optionsButton, Game.LAYER_UI);
        entityList.add(optionsButton);
        
        // Create the options header.
        headerLabel = ResourceFactory.get().getLabel(400, 171);        
        headerLabel.setSize(26);
        headerLabel.setAlignment(Label.VCENTER | Label.HCENTER);
        headerLabel.setColor(Game.TEXT_COLOR);
        headerLabel.setText("Options");
        headerLabel.setVisible(false);
        layerMan.add(headerLabel, Game.LAYER_UI);
        entityList.add(headerLabel);
        
        // Create help button.
        helpButton = new RectangularBooleanButton(window, 400, 246);
        helpButton.setNormalOpacity(70);
        helpButton.setText("Help");
        helpButton.getLabel().setSize(18);
        helpButton.setAlignment(Button.VCENTER | Button.HCENTER);
        helpButton.setVisible(false);
        layerMan.add(helpButton, Game.LAYER_UI);
        entityList.add(helpButton);
        
        // Create audio button.
        audioButton = new RectangularBooleanButton(window, 400, 300);
        audioButton.setNormalOpacity(70);
        audioButton.setText("Sound/Music");
        audioButton.getLabel().setSize(18);
        audioButton.setAlignment(Button.VCENTER | Button.HCENTER);
        audioButton.setVisible(false);
        layerMan.add(audioButton, Game.LAYER_UI);
        entityList.add(audioButton);
        
        // Create main menu button.
        mainMenuButton = new RectangularBooleanButton(window, 400, 354);        
        mainMenuButton.setNormalOpacity(70);
        mainMenuButton.setText("Main Menu");
        mainMenuButton.getLabel().setSize(18);
        mainMenuButton.setAlignment(Button.VCENTER | Button.HCENTER);
        mainMenuButton.setVisible(false);
        layerMan.add(mainMenuButton, Game.LAYER_UI);
        entityList.add(mainMenuButton);
        
        // Create back button.
        backButton = new RectangularBooleanButton(window, 400, 408);
        backButton.setNormalOpacity(70);
        backButton.setText("Back");
        backButton.getLabel().setSize(18);
        backButton.setAlignment(Button.VCENTER | Button.HCENTER);
        backButton.setVisible(false);
        layerMan.add(backButton, Game.LAYER_UI);     
        entityList.add(backButton);
    }
    
    public boolean isOptionsButtonClicked()
    {
        return optionsButton.clicked();
    }
    
    public boolean isOptionButtonActivated()
    {
        return optionsButton.isActivated();
    }
    
    public boolean isBackButtonClicked()
    {
        return backButton.clicked();
    }  
    
     public boolean isBackButtonActivated()
    {
        return backButton.isActivated();
    }
    
     @Override
    public void setVisible(final boolean visible)
    {
        // This is more important than you think.  Basically, since we might
        // be adding or removing listeners, we want to make sure we only add
        // a listener once, and that we only remove it once.  This ensures that.
        if (isVisible() == visible)
            return;            
        
        // Invoke super.  This will remove the listener from pause which
        // we will re-add below.
        super.setVisible(visible);
        
        // Make it so pause button still shows when the rest of the group is
        // not visible.
        if (visible == true)
        {
            optionsButton.setVisible(true);            
        }
        else
        {
            optionsButton.setVisible(true);                                                
        }
    }
    
    @Override
    public void setActivated(final boolean activated)
    {
        // Invoke super.
        super.setActivated(activated);
        
        // Make sure the pause button is activated.
        optionsButton.setActivated(activated);
    } 
     
}
