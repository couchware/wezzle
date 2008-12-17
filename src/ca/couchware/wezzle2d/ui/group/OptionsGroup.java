package ca.couchware.wezzle2d.ui.group;

import ca.couchware.wezzle2d.manager.GroupManager;
import ca.couchware.wezzle2d.manager.SettingsManager;
import ca.couchware.wezzle2d.ui.IButton;
import ca.couchware.wezzle2d.ui.Button;
import ca.couchware.wezzle2d.manager.LayerManager;
import ca.couchware.wezzle2d.*;
import ca.couchware.wezzle2d.manager.LayerManager.Layer;
import ca.couchware.wezzle2d.ResourceFactory.LabelBuilder;
import ca.couchware.wezzle2d.manager.Settings;
import ca.couchware.wezzle2d.manager.Settings.Key;
import ca.couchware.wezzle2d.ui.*;
import edu.stanford.ejalbert.BrowserLauncher;
import edu.stanford.ejalbert.exception.BrowserLaunchingInitializingException;
import edu.stanford.ejalbert.exception.UnsupportedOperatingSystemException;
import java.util.EnumSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author cdmckay
 */
public class OptionsGroup extends AbstractGroup
{
    
    /** The browser launcher. */
    BrowserLauncher launcher;
    
    /** The layer manager. */
    final private LayerManager layerMan;
    
    /** The header label. */
    private ITextLabel headerLabel;
    
    /** The help button. */
    private IButton upgradeButton;
    
    /** The audio button. */
    private IButton audioButton;
    
    /** The audio group. */
    private AudioGroup audioGroup;

    /** The main menu button. */
    private IButton mainMenuButton;

    /** The back button. */
    private IButton closeButton;    
    
    /**
     * The constructor.
     * 
     * @param window
     * @param layerMan
     */    
    public OptionsGroup(
            final SettingsManager settingsMan,
            final LayerManager layerMan,     
            final GroupManager groupMan)
    {
        // Invoke super.
        this.layerMan = layerMan;
        
        // Create the options header.
        headerLabel = new LabelBuilder(400, 171)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                .color(settingsMan.getColor(Key.GAME_COLOR_PRIMARY)).size(26).text("Options")
                .visible(false).end();
        layerMan.add(headerLabel, Layer.UI);
        entityList.add(headerLabel);
        
        // Create upgrade button.
        upgradeButton = new Button.Builder(400, 246)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                //.type(SpriteButton.Type.THIN)
                .text("Buy Now").normalOpacity(80).visible(false).end();        
        layerMan.add(upgradeButton, Layer.UI);
        entityList.add(upgradeButton);
        
        // Create audio button.
        audioButton = new Button.Builder((Button) upgradeButton).y(300)
            .text("Sound/Music").end();
        layerMan.add(audioButton, Layer.UI);
        entityList.add(audioButton);
        
        // Create the audio group.
        audioGroup = new AudioGroup(settingsMan, layerMan);
        groupMan.register(audioGroup);
        
        // Create main menu button.
        mainMenuButton = new Button.Builder((Button) upgradeButton).y(354)
            .text("Main Menu").end();
        layerMan.add(mainMenuButton, Layer.UI);
        entityList.add(mainMenuButton);
        
        // Create back button.
        closeButton = new Button.Builder((Button) upgradeButton).y(420)
            .text("Close").end();
        layerMan.add(closeButton, Layer.UI);     
        entityList.add(closeButton);
        try
        {
            // Create the browser launcher.
            this.launcher = new BrowserLauncher();
        }
        catch (BrowserLaunchingInitializingException ex)
        {
            Logger.getLogger(OptionsGroup.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (UnsupportedOperatingSystemException ex)
        {
            Logger.getLogger(OptionsGroup.class.getName()).log(Level.SEVERE, null, ex);
        }
    }      
    
    public boolean isBackButtonClicked()
    {
        return closeButton.clicked();
    }  
    
    public boolean isBackButtonActivated()
    {
        return closeButton.isActivated();
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
        // See if Buy Now was pressed.
        if (upgradeButton.clicked() == true)
        {
            upgradeButton.setActivated(false);
            launcher.openURLinBrowser(Settings.getUpgradeUrl());
        }
        // Check if the back button was pressed.
        if (closeButton.isActivated() == true)
        {            
            // Hide all side triggered menues.
            closeButton.setActivated(false);
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
