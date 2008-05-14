package ca.couchware.wezzle2d;

import ca.couchware.wezzle2d.button.*;

/**
 *
 * @author cdmckay
 */
public class OptionsGroup extends Group
{
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
        helpButton = new RectangularBooleanButton(400, 214);
        helpButton.setNormalOpacity(70);
        helpButton.setText("Help");
        helpButton.getLabel().setSize(18);
        helpButton.setAlignment(Button.VCENTER | Button.HCENTER);
        helpButton.setVisible(false);
        layerMan.add(helpButton, Game.LAYER_UI);
        entityList.add(helpButton);
        
        // Create audio button.
        audioButton = new RectangularBooleanButton(400, 278);
        audioButton.setNormalOpacity(70);
        audioButton.setText("Sound/Music");
        audioButton.getLabel().setSize(18);
        audioButton.setAlignment(Button.VCENTER | Button.HCENTER);
        audioButton.setVisible(false);
        layerMan.add(audioButton, Game.LAYER_UI);
        entityList.add(audioButton);
        
        // Create main menu button.
        mainMenuButton = new RectangularBooleanButton(400, 342);        
        mainMenuButton.setNormalOpacity(70);
        mainMenuButton.setText("Main Menu");
        mainMenuButton.getLabel().setSize(18);
        mainMenuButton.setAlignment(Button.VCENTER | Button.HCENTER);
        mainMenuButton.setVisible(false);
        layerMan.add(mainMenuButton, Game.LAYER_UI);
        entityList.add(mainMenuButton);
        
        // Create back button.
        backButton = new RectangularBooleanButton(400, 406);
        backButton.setNormalOpacity(70);
        backButton.setText("Back");
        backButton.getLabel().setSize(18);
        backButton.setAlignment(Button.VCENTER | Button.HCENTER);
        backButton.setVisible(false);
        layerMan.add(backButton, Game.LAYER_UI);
        entityList.add(backButton);
    }
}
