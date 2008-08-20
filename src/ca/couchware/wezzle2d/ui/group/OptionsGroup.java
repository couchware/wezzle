package ca.couchware.wezzle2d.ui.group;

import ca.couchware.wezzle2d.ui.IButton;
import ca.couchware.wezzle2d.ui.SpriteButton;
import ca.couchware.wezzle2d.LayerManager;
import ca.couchware.wezzle2d.*;
import ca.couchware.wezzle2d.LayerManager.Layer;
import ca.couchware.wezzle2d.ResourceFactory.LabelBuilder;
import ca.couchware.wezzle2d.ui.*;
import ca.couchware.wezzle2d.ui.group.options.*;
import java.util.EnumSet;

/**
 *
 * @author cdmckay
 */
public class OptionsGroup extends Group
{
    /**
     * The header label.
     */
    private ILabel headerLabel;
    
    /**
     * The help button.
     */
    private IButton helpButton;
    
    /**
     * The audio button.
     */
    private IButton audioButton;
    
    /**
     * The audio group.
     */
    private SoundMusicGroup audioGroup;

    /**
     * The main menu button.
     */
    private IButton mainMenuButton;

    /**
     * The back button.
     */
    private IButton backButton;    
    
    /**
     * The constructor.
     * 
     * @param window
     * @param layerMan
     */    
    public OptionsGroup(final LayerManager layerMan,
            final GroupManager groupMan,
            final PropertyManager propertyMan)
    {
        // Invoke super.
        super(layerMan, groupMan);                
        
        // Create the options header.
        headerLabel = new LabelBuilder(400, 171)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                .color(Game.TEXT_COLOR1).size(26).text("Options")
                .visible(false).end();
        layerMan.add(headerLabel, Layer.UI);
        entityList.add(headerLabel);
        
        // Create help button.
        helpButton = new SpriteButton.Builder(400, 246)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                .text("Help").offOpacity(70).visible(false).end();        
        layerMan.add(helpButton, Layer.UI);
        entityList.add(helpButton);
        
        // Create audio button.
        audioButton = new SpriteButton.Builder((SpriteButton) helpButton).y(300)
            .text("Sound/Music").end();
        layerMan.add(audioButton, Layer.UI);
        entityList.add(audioButton);
        
        // Create the audio group.
        audioGroup = new SoundMusicGroup(layerMan, groupMan, 
                propertyMan);        
        groupMan.register(audioGroup);
        
        // Create main menu button.
        mainMenuButton = new SpriteButton.Builder((SpriteButton) helpButton).y(354)
            .text("Main Menu").end();
        layerMan.add(mainMenuButton, Layer.UI);
        entityList.add(mainMenuButton);
        
        // Create back button.
        backButton = new SpriteButton.Builder((SpriteButton) helpButton).y(408)
            .text("Back").end();
        layerMan.add(backButton, Layer.UI);     
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
