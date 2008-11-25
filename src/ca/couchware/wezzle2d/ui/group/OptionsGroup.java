package ca.couchware.wezzle2d.ui.group;

import ca.couchware.wezzle2d.manager.GroupManager;
import ca.couchware.wezzle2d.manager.SettingsManager;
import ca.couchware.wezzle2d.ui.IButton;
import ca.couchware.wezzle2d.ui.SpriteButton;
import ca.couchware.wezzle2d.manager.LayerManager;
import ca.couchware.wezzle2d.*;
import ca.couchware.wezzle2d.manager.LayerManager.Layer;
import ca.couchware.wezzle2d.ResourceFactory.LabelBuilder;
import ca.couchware.wezzle2d.ui.*;
import java.util.EnumSet;

/**
 *
 * @author cdmckay
 */
public class OptionsGroup extends AbstractGroup
{
    
    /**
     * The layer manager.
     */
    final private LayerManager layerMan;
    
    /**
     * The header label.
     */
    private ILabel headerLabel;
    
    /**
     * The help button.
     */
    private IButton upgradeButton;
    
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
    public OptionsGroup(
            final LayerManager layerMan,     
            final GroupManager groupMan)
    {
        // Invoke super.
        this.layerMan = layerMan;
        
        // Create the options header.
        headerLabel = new LabelBuilder(400, 171)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                .color(Game.TEXT_COLOR1).size(26).text("Options")
                .visible(false).end();
        layerMan.add(headerLabel, Layer.UI);
        entityList.add(headerLabel);
        
        // Create upgrade button.
        upgradeButton = new SpriteButton.Builder(400, 246)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                .type(SpriteButton.Type.THIN)
                .text("Upgrade").offOpacity(80).visible(false).end();        
        layerMan.add(upgradeButton, Layer.UI);
        entityList.add(upgradeButton);
        
        // Create audio button.
        audioButton = new SpriteButton.Builder((SpriteButton) upgradeButton).y(300)
            .text("Sound/Music").end();
        layerMan.add(audioButton, Layer.UI);
        entityList.add(audioButton);
        
        // Create the audio group.
        audioGroup = new SoundMusicGroup(layerMan);
        groupMan.register(audioGroup);
        
        // Create main menu button.
        mainMenuButton = new SpriteButton.Builder((SpriteButton) upgradeButton).y(354)
            .text("Main Menu").end();
        layerMan.add(mainMenuButton, Layer.UI);
        entityList.add(mainMenuButton);
        
        // Create back button.
        backButton = new SpriteButton.Builder((SpriteButton) upgradeButton).y(408)
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
                GroupManager.Class.OPTIONS,
                GroupManager.Layer.MIDDLE);
        }       
    }
    
}
