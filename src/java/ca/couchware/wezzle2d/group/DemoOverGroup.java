package ca.couchware.wezzle2d.group;

import ca.couchware.wezzle2d.Game;
import ca.couchware.wezzle2d.IWindow;
import ca.couchware.wezzle2d.ManagerHub;
import ca.couchware.wezzle2d.manager.GroupManager;
import ca.couchware.wezzle2d.manager.BoardManager.AnimationType;
import ca.couchware.wezzle2d.manager.LayerManager.Layer;
import ca.couchware.wezzle2d.ResourceFactory.LabelBuilder;
import ca.couchware.wezzle2d.manager.Settings;
import ca.couchware.wezzle2d.manager.Settings.Key;
import ca.couchware.wezzle2d.ui.Button;
import ca.couchware.wezzle2d.ui.ITextLabel;
import java.util.EnumSet;

/**
 * This screen is shown when the demo is over due to level contrainsts.
 * @author cdmckay
 */
public class DemoOverGroup extends AbstractGroup
{
    private Button restartButton;    
    private Button buyNowButton;
       
    public DemoOverGroup(IWindow win, ManagerHub hub)
    {            
        if (hub == null)
        {
            throw new IllegalArgumentException("Hub must not be null");
        }
                
        ITextLabel headerLabel = new LabelBuilder(400, 181)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                .color(hub.settingsMan.getColor(Key.GAME_COLOR_PRIMARY)).size(26).text("Demo Over")
                .visible(false).build();

        hub.layerMan.add(headerLabel, Layer.UI);        
        entityList.add(headerLabel);

        ITextLabel demoText0 = new LabelBuilder(400, 219)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                .color(hub.settingsMan.getColor(Key.GAME_COLOR_PRIMARY)).size(12)
                .text("The full version features:")
                .visible(false).build();

        hub.layerMan.add(demoText0, Layer.UI);
        entityList.add(demoText0);

        ITextLabel demoText1 = new LabelBuilder(400, 244)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                .color(hub.settingsMan.getColor(Key.GAME_COLOR_PRIMARY)).size(14)
                .text("-Four unique items")
                .visible(false).build();

        hub.layerMan.add(demoText1, Layer.UI);
        entityList.add(demoText1);

        ITextLabel demoText2 = new LabelBuilder(400, 264)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                .color(hub.settingsMan.getColor(Key.GAME_COLOR_PRIMARY)).size(14)
                .text("-Three difficuly modes")
                .visible(false).build();

        hub.layerMan.add(demoText2, Layer.UI);
        entityList.add(demoText2);

        ITextLabel demoText3 = new LabelBuilder(400, 284)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                .color(hub.settingsMan.getColor(Key.GAME_COLOR_PRIMARY)).size(14)
                .text("-10 exclusive songs")
                .visible(false).build();

        hub.layerMan.add(demoText3, Layer.UI);
        entityList.add(demoText3);

        ITextLabel demoText4 = new LabelBuilder(400, 304)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                .color(hub.settingsMan.getColor(Key.GAME_COLOR_PRIMARY)).size(14)
                .text("-Unlimited levels")
                .visible(false).build();

        hub.layerMan.add(demoText4, Layer.UI);
        entityList.add(demoText4);
                
        restartButton = new Button.Builder(win, 400, 405)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))                
                .text("Restart").normalOpacity(70).visible(false).build();
        hub.layerMan.add(restartButton, Layer.UI);

        entityList.add(restartButton);
        
        buyNowButton = new Button.Builder(restartButton).y(355)
                .text("Buy Now").build();
        hub.layerMan.add(buyNowButton, Layer.UI);

        entityList.add(buyNowButton);
    }                   
    
    /**
     * Override the update logic method.
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

        if (false) {}
        else if ( this.restartButton.clicked() )
        {
             // Hide the screen.
            hub.groupMan.hideGroup(
                    GroupManager.Type.GAME_OVER,
                    GroupManager.Layer.BOTTOM,
                    !game.isCompletelyBusy());

            // This action will change a lot of shit, so keep that in mind.
            game.resetGame(true);

            // Create board and make it invisible.
            hub.boardMan.setVisible(false);
            hub.boardMan.generateBoard(hub.itemMan.getItemList(), hub.levelMan.getLevel());

            // Unpause the game.
            // Don't worry! The game won't pass updates to the
            // timer unless the board is shown.  In hindsight, this
            // is kind of crappy, but whatever, we'll make it prettier
            // one day.
            hub.timerMan.setPaused(false);

            // Start the board show animation.  This will
            // make the board visible when it's done.
            game.startBoardShowAnimation(AnimationType.ROW_FADE);

            // Deactivate button.
            this.restartButton.setActivated(false);
        }
        else if ( this.buyNowButton.clicked() )
        {
            game.openURLinBrowser(Settings.getUpgradeUrl());
            this.buyNowButton.setActivated(false);
        }       
        
        // Clear the change setting.
        this.clearChanged();
    }    
            
}
