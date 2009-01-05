package ca.couchware.wezzle2d.ui.group;

import ca.couchware.wezzle2d.manager.GroupManager;
import ca.couchware.wezzle2d.ui.IButton;
import ca.couchware.wezzle2d.ui.Button;
import ca.couchware.wezzle2d.manager.LayerManager;
import ca.couchware.wezzle2d.*;
import ca.couchware.wezzle2d.manager.BoardManager.AnimationType;
import ca.couchware.wezzle2d.manager.LayerManager.Layer;
import ca.couchware.wezzle2d.ResourceFactory.LabelBuilder;
import ca.couchware.wezzle2d.event.GameEvent;
import ca.couchware.wezzle2d.event.IGameListener;
import ca.couchware.wezzle2d.manager.ScoreManager;
import ca.couchware.wezzle2d.manager.Settings.Key;
import ca.couchware.wezzle2d.manager.SettingsManager;
import ca.couchware.wezzle2d.ui.*;
import java.util.EnumSet;

/**
 *
 * @author cdmckay
 */
public class GameOverGroup extends AbstractGroup implements IGameListener
{     
    
    /**
     * A reference to the layer manager.  This is used by groups to add
     * and remove things like buttons and sliders.
     */
    final private LayerManager layerMan;     
    
    /**
     * A reference to the score manager.  This is used by the group
     * to pull down the score when a game over event is fired.
     */
    final private ScoreManager scoreMan;
    
    /**
     * The header label.
     */
    private ITextLabel headerLabel;
    
    /**
     * The final score header label.
     */
    private ITextLabel scoreHeaderLabel;
    
    /**
     * The final score label.
     */
    private ITextLabel scoreLabel;
    
    /**
     * The restart button.
     */
    private IButton restartButton;
    
    /**
     * The continue button.
     */
    private IButton continueButton;
    
    /**
     * The constructor.
     * 
     * @param layerMan
     */    
    public GameOverGroup(
            final SettingsManager settingsMan,
            final LayerManager layerMan,
            final ScoreManager scoreMan)
    {    
        // Set the manager references.
        this.layerMan = layerMan;
        this.scoreMan = scoreMan;
        
        // Create the game over header.
        headerLabel = new LabelBuilder(400, 181)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                .color(settingsMan.getColor(Key.GAME_COLOR_PRIMARY)).size(26).text("Game over :(")
                .visible(false).end();
        layerMan.add(headerLabel, Layer.UI);
        entityList.add(headerLabel);
        
        // Create the final score header.
        scoreHeaderLabel = new LabelBuilder(400, 234)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                .color(settingsMan.getColor(Key.GAME_COLOR_PRIMARY)).size(14)
                .text("Your final score was")
                .visible(false).end();
        layerMan.add(scoreHeaderLabel, Layer.UI); 
        entityList.add(scoreHeaderLabel);
        
        // Create the final score label.
        scoreLabel = new LabelBuilder(400, 270)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                .color(settingsMan.getColor(Key.GAME_COLOR_PRIMARY)).size(30)
                .text("0").visible(false).end();
        layerMan.add(scoreLabel, Layer.UI); 
        entityList.add(scoreLabel);
        
        // Create restart button.
        restartButton = new Button.Builder(400, 345)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                //.type(SpriteButton.Type.THIN)
                .text("Restart").normalOpacity(70).visible(false).end();
        layerMan.add(restartButton, Layer.UI);
        entityList.add(restartButton);
        
        // Create continue button, using the restart button as a template.
        continueButton = new Button.Builder((Button) restartButton)
                .y(405).text("Continue").end();
        layerMan.add(continueButton, Layer.UI);
        entityList.add(continueButton);
    }        
    
    public void setScore(final int score)
    {
        layerMan.remove(scoreLabel, Layer.UI);
        entityList.remove(scoreLabel);
        scoreLabel = new LabelBuilder(scoreLabel)
                .text(String.valueOf(score)).end();
        layerMan.add(scoreLabel, Layer.UI);
        entityList.add(scoreLabel);
    }
    
    public int getScore()
    {
        return Integer.valueOf(scoreLabel.getText());
    }
    
    public boolean isRestartActivated()
    {
        return restartButton.isActivated();
    }
    
    public boolean isContinueActiavted()
    {
        return continueButton.isActivated();
    }       
    
    /**
     * Override the update logic method.
     * 
     * @param game The game state.
     */    
    public void updateLogic(Game game)
    {
        // Hide the screen.
        game.groupMan.hideGroup(
                GroupManager.Class.GAME_OVER,
                GroupManager.Layer.BOTTOM);
        
        // The level we reset to.
        int level = game.levelMan.getLevel();
        
        // Reset a bunch of stuff.
        if (isRestartActivated() == true)
        {
            // Reset the board manager.
            game.boardMan.resetState();
            
            // Reset the world manager.
            game.levelMan.resetState();  
            level = game.levelMan.getLevel();
            
            // Reset the item manager.
            game.itemMan.resetState();
            game.itemMan.evaluateRules(game);

            // Reset the timer to the initial.
            game.timerMan.resetInitialTime();
        }
        
        // Notify all listeners of reset.
        game.listenerMan.notifyGameReset(new GameEvent(this, level));
                       
        // Reset the stat man.
        game.statMan.resetState();

        // Create board and make it invisible.
        game.boardMan.setVisible(false);
        game.boardMan.generateBoard(game.itemMan.getItemList(), game.levelMan.getLevel());                    

        // Unpause the game.
        // Don't worry! The game won't pass updates to the 
        // timer unless the board is shown.  In hindsight, this
        // is kind of crappy, but whatever, we'll make it prettier
        // one day.
        game.timerMan.setPaused(false);

        // Reset the timer.
        game.timerMan.resetTimer();

        // Start the board show animation.  This will
        // make the board visible when it's done.
        game.startBoardShowAnimation(AnimationType.ROW_FADE);        
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
        this.setScore(this.scoreMan.getTotalScore());
    }
            
}
