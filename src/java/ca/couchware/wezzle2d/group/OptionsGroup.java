package ca.couchware.wezzle2d.group;

import ca.couchware.wezzle2d.Game;
import ca.couchware.wezzle2d.manager.GroupManager;
import ca.couchware.wezzle2d.ManagerHub;
import ca.couchware.wezzle2d.manager.LayerManager.Layer;
import ca.couchware.wezzle2d.ResourceFactory.LabelBuilder;
import ca.couchware.wezzle2d.manager.Settings.Key;
import ca.couchware.wezzle2d.ui.Button;
import ca.couchware.wezzle2d.ui.IButton;
import ca.couchware.wezzle2d.ui.ITextLabel;
import ca.couchware.wezzle2d.util.CouchLogger;
import edu.stanford.ejalbert.BrowserLauncher;
import edu.stanford.ejalbert.exception.BrowserLaunchingInitializingException;
import edu.stanford.ejalbert.exception.UnsupportedOperatingSystemException;
import java.util.EnumSet;

/**
 *
 * @author cdmckay
 */
public class OptionsGroup extends AbstractGroup
{
    final private ManagerHub hub;

    private BrowserLauncher launcher;       
    
    private ITextLabel headerLabel;
    private IButton audioButton;
    private IButton mainMenuButton;
    private IButton exitGameButton;
    private IButton closeButton;

    private AudioGroup audio;
    private MainMenuGroup mainMenu;
    private ExitGameGroup exitGame;

    /**
     * The constructor.
     * 
     * @param window
     * @param layerMan
     */    
    public OptionsGroup(ManagerHub hub)
    {
        // Check the hub instance and save it.
        if(hub == null)
            throw new IllegalArgumentException("Hub must not be null");

        this.hub = hub;
        
        // Create the options header.
        headerLabel = new LabelBuilder(400, 171)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                .color(hub.settingsMan.getColor(Key.GAME_COLOR_PRIMARY))
                .size(26).text("Options")
                .visible(false).build();
        hub.layerMan.add(headerLabel, Layer.UI);
        entityList.add(headerLabel);

        audio = new AudioGroup( hub );
        hub.groupMan.register( audio );

        mainMenu = new MainMenuGroup( hub );
        hub.groupMan.register( mainMenu );

        exitGame = new ExitGameGroup( hub );
        hub.groupMan.register( exitGame );

        // Old Y-values...
        // Buy Now:   246
        // Audio:     300
        // Main Menu: 354

        // Create upgrade button.
        Button templateButton = new Button.Builder(400, 0)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))                
                .text("").normalOpacity(80).visible(false).build();

        audioButton = new Button.Builder(templateButton).y(246)
            .text("Audio").build();
        hub.layerMan.add(audioButton, Layer.UI);
        entityList.add(audioButton);                
                
        mainMenuButton = new Button.Builder(templateButton).y(300)
            .text("Main Menu").build();
        hub.layerMan.add(mainMenuButton, Layer.UI);
        entityList.add(mainMenuButton);

        exitGameButton = new Button.Builder(templateButton).y(354)
            .text("Exit Game").build();
        hub.layerMan.add(exitGameButton, Layer.UI);
        entityList.add(exitGameButton);

        closeButton = new Button.Builder(templateButton).y(420)
            .text("Back to Game").build();
        hub.layerMan.add(closeButton, Layer.UI);     
        entityList.add(closeButton);
        
        try
        {
            // Create the browser launcher.
            this.launcher = new BrowserLauncher();
        }
        catch (BrowserLaunchingInitializingException ex)
        {
            CouchLogger.get().recordException(this.getClass(), ex);
        }
        catch (UnsupportedOperatingSystemException ex)
        {
            CouchLogger.get().recordException(this.getClass(), ex);
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
    @Override
    public void updateLogic(Game game, ManagerHub hub)
    {
        super.updateLogic( game, hub );

        // Sanity check.
        if (game == null)
            throw new IllegalArgumentException("Game must not be null");

        if (hub == null)
            throw new IllegalArgumentException("Hub must not be null");

        // Make sure something changed.
        if ( !this.controlChanged() ) return;     
                
        if (closeButton.isActivated())
        {                        
            closeButton.setActivated(false);
            hub.groupMan.hideGroup(
                        GroupManager.Type.OPTIONS,
                        GroupManager.Layer.MIDDLE,
                        !game.isCompletelyBusy());
        }       
        else if (audioButton.isActivated())
        {                                               
            hub.groupMan.showGroup(audioButton, audio,
                GroupManager.Type.OPTIONS,
                GroupManager.Layer.MIDDLE);
        }
        else if (mainMenuButton.isActivated())
        {
            hub.groupMan.showGroup(mainMenuButton, mainMenu,
                GroupManager.Type.OPTIONS,
                GroupManager.Layer.MIDDLE);
        }
        else if (exitGameButton.isActivated())
        {            
            hub.groupMan.showGroup(exitGameButton, exitGame,
                GroupManager.Type.OPTIONS,
                GroupManager.Layer.MIDDLE);
        }        

        // Clear the change setting.
        this.clearChanged();
    }
    
}
