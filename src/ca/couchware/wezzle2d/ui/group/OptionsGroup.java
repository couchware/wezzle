package ca.couchware.wezzle2d.ui.group;

import ca.couchware.wezzle2d.LayerManager;
import ca.couchware.wezzle2d.*;
import ca.couchware.wezzle2d.ui.*;
import ca.couchware.wezzle2d.ui.button.*;
import ca.couchware.wezzle2d.ui.group.options.*;
import ca.couchware.wezzle2d.util.Util;
import java.util.EnumSet;

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
     * The audio group.
     */
    private SoundMusicGroup audioGroup;

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
    public OptionsGroup(final GameWindow window, 
            final LayerManager layerMan, final GroupManager groupMan,
            final PropertyManager propertyMan)
    {
        // Invoke super.
        super(window, layerMan, groupMan);                
        
        // Create the options header.
        headerLabel = ResourceFactory.get().getLabel(400, 171);        
        headerLabel.setSize(26);
        headerLabel.setAlignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER));
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
        helpButton.setAlignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER));
        helpButton.setVisible(false);
        layerMan.add(helpButton, Game.LAYER_UI);
        entityList.add(helpButton);
        
        // Create audio button.
        audioButton = new RectangularBooleanButton(window, 400, 300);
        audioButton.setNormalOpacity(70);
        audioButton.setText("Sound/Music");
        audioButton.getLabel().setSize(18);
        audioButton.setAlignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER));
        audioButton.setVisible(false);
        layerMan.add(audioButton, Game.LAYER_UI);
        entityList.add(audioButton);
        
        // Create the audio group.
        audioGroup = new SoundMusicGroup(window, layerMan, groupMan, 
                propertyMan);        
        groupMan.register(audioGroup);
        
        // Create main menu button.
        mainMenuButton = new RectangularBooleanButton(window, 400, 354);        
        mainMenuButton.setNormalOpacity(70);
        mainMenuButton.setText("Main Menu");
        mainMenuButton.getLabel().setSize(18);
        mainMenuButton.setAlignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER));
        mainMenuButton.setVisible(false);
        layerMan.add(mainMenuButton, Game.LAYER_UI);
        entityList.add(mainMenuButton);
        
        // Create back button.
        backButton = new RectangularBooleanButton(window, 400, 408);
        backButton.setNormalOpacity(70);
        backButton.setText("Back");
        backButton.getLabel().setSize(18);
        backButton.setAlignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER));
        backButton.setVisible(false);
        layerMan.add(backButton, Game.LAYER_UI);     
        entityList.add(backButton);
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
        if (backButton.isActivated() == true)
        {            
            // Hide all side triggered menues.
            backButton.setActivated(false);
            game.groupMan.hideGroup(this);
        }
        // Check if the sound/music button was pressed.
        else if (audioButton.isActivated() == true)
        {                        
            // Show the sound/music group.            
            game.groupMan.showGroup(audioButton, audioGroup, 
                    GroupManager.CLASS_OPTIONS,
                    GroupManager.LAYER_MIDDLE);
        }       
    }
    
}
