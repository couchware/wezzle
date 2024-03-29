package ca.couchware.wezzle2d.group;

import ca.couchware.wezzle2d.Game;
import ca.couchware.wezzle2d.IWindow;
import ca.couchware.wezzle2d.ManagerHub;
import ca.couchware.wezzle2d.manager.GroupManager;
import ca.couchware.wezzle2d.manager.BoardManager.AnimationType;
import ca.couchware.wezzle2d.manager.LayerManager.Layer;
import ca.couchware.wezzle2d.ResourceFactory.LabelBuilder;
import ca.couchware.wezzle2d.event.GameEvent;
import ca.couchware.wezzle2d.event.IGameListener;
import ca.couchware.wezzle2d.manager.Settings.Key;
import ca.couchware.wezzle2d.ui.Button;
import ca.couchware.wezzle2d.ui.IButton;
import ca.couchware.wezzle2d.ui.ITextLabel;
import java.util.EnumSet;

/**
 *
 * @author cdmckay
 */
public class GameOverGroup extends AbstractGroup implements IGameListener
{             
    private ITextLabel headerLabel;
    private ITextLabel scoreHeaderLabel;
    private ITextLabel scoreLabel;
    private IButton restartButton;
    private IButton continueButton;
    
    /**
     * The constructor.
     * @param layerMan
     */    
    public GameOverGroup(IWindow win, ManagerHub hub)
    {    
        // Sanity check.
        if (hub == null)
        {
            throw new IllegalArgumentException("Hub must not be null");
        }
        
        // Create the game over header.
        headerLabel = new LabelBuilder(400, 181)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                .color(hub.settingsMan.getColor(Key.GAME_COLOR_PRIMARY)).size(26).text("Game Over")
                .visible(false).build();
        hub.layerMan.add(headerLabel, Layer.UI);
        entityList.add(headerLabel);
        
        // Create the final score header.
        scoreHeaderLabel = new LabelBuilder(400, 234)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                .color(hub.settingsMan.getColor(Key.GAME_COLOR_PRIMARY)).size(14)
                .text("Your final score was")
                .visible(false).build();
        hub.layerMan.add(scoreHeaderLabel, Layer.UI); 
        entityList.add(scoreHeaderLabel);
        
        // Create the final score label.
        scoreLabel = new LabelBuilder(400, 270)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                .color(hub.settingsMan.getColor(Key.GAME_COLOR_PRIMARY)).size(30)
                .text("0").visible(false).build();
        hub.layerMan.add(scoreLabel, Layer.UI); 
        entityList.add(scoreLabel);
        
        // Create restart button.
        restartButton = new Button.Builder(win, 400, 345)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                //.type(SpriteButton.Type.THIN)
                .text("Restart").normalOpacity(70).visible(false).build();
        hub.layerMan.add(restartButton, Layer.UI);
        entityList.add(restartButton);
        
        // Create continue button, using the restart button as a template.
        continueButton = new Button.Builder((Button) restartButton)
                .y(405).text("Continue").build();
        hub.layerMan.add(continueButton, Layer.UI);
        entityList.add(continueButton);
    }        
    
    public void setScore(final int score)
    {
        this.scoreLabel.setText(String.format("%,d", score));
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

        // Hide the screen.
        hub.groupMan.hideGroup(
                GroupManager.Type.GAME_OVER,
                GroupManager.Layer.BOTTOM,
                !game.isCompletelyBusy());
                       
        // Reset the stat man.
        game.resetGame(this.restartButton.isActivated());

        // Create board and make it invisible.
        hub.boardMan.setVisible(false);
        hub.boardMan.generateBoard(hub.itemMan.getItemList(), hub.levelMan.getLevel());                    

        // Unpause the game.
        // Don't worry! The game won't pass updates to the 
        // timer unless the board is shown.  In hindsight, this
        // is kind of crappy, but whatever, we'll make it prettier
        // one day.
        hub.timerMan.setPaused(false);

        // Reset the timer.
        //hub.timerMan.resetCurrentTime();

        // Start the board show animation.  This will
        // make the board visible when it's done.
        game.startBoardShowAnimation(AnimationType.ROW_FADE);        
        
        // Deactivate button.
        this.restartButton.setActivated(false);
        this.continueButton.setActivated(false);

        // Clear the change setting.
        this.clearChanged();
    }

    public void gameStarted(GameEvent event)
    {
        // Intentionally blank.        
    }

    public void gameReset(GameEvent event)
    {
        // Intentionally blank.
    }

    public void gameOver(GameEvent event)
    {
        // Set the score to whatever the score manager has now.
        this.setScore(event.getTotalScore());
    }
            
}
