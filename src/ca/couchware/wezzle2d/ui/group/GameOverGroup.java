package ca.couchware.wezzle2d.ui.group;

import ca.couchware.wezzle2d.LayerManager;
import ca.couchware.wezzle2d.*;
import ca.couchware.wezzle2d.ResourceFactory.LabelBuilder;
import ca.couchware.wezzle2d.ui.*;
import ca.couchware.wezzle2d.ui.button.*;
import java.util.EnumSet;

/**
 *
 * @author cdmckay
 */
public class GameOverGroup extends Group
{              
    /**
     * The header label.
     */
    private ILabel headerLabel;
    
    /**
     * The final score header label.
     */
    private ILabel scoreHeaderLabel;
    
    /**
     * The final score label.
     */
    private ILabel scoreLabel;
    
    /**
     * The restart button.
     */
    private SpriteButton restartButton;
    
    /**
     * The continue button.
     */
    private SpriteButton continueButton;
    
    /**
     * The constructor.
     * 
     * @param layerMan
     */    
    public GameOverGroup(final GameWindow window, 
            final LayerManager layerMan, final GroupManager groupMan)
    {        
        // Invoke super.
        super(window, layerMan, groupMan);
        
        // Create the game over header.
        headerLabel = new LabelBuilder(400, 181)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                .color(Game.TEXT_COLOR).size(26).text("Game over :(")
                .visible(false).end();
        layerMan.add(headerLabel, Game.LAYER_UI);
        entityList.add(headerLabel);
        
        // Create the final score header.
        scoreHeaderLabel = new LabelBuilder(400, 234)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                .color(Game.TEXT_COLOR).size(14)
                .text("Your final score was")
                .visible(false).end();
        layerMan.add(scoreHeaderLabel, Game.LAYER_UI); 
        entityList.add(scoreHeaderLabel);
        
        // Create the final score label.
        scoreLabel = new LabelBuilder(400, 270)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                .color(Game.TEXT_COLOR).size(30)
                .text("0").visible(false).end();
        layerMan.add(scoreLabel, Game.LAYER_UI); 
        entityList.add(scoreLabel);
        
        // Create restart button.
        restartButton = new SpriteButton.Builder(window, 400, 345)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                .text("Restart").normalOpacity(70).visible(false).end();
        layerMan.add(restartButton, Game.LAYER_UI);
        entityList.add(restartButton);
        
        // Create continue button, using the restart button as a template.
        continueButton = new SpriteButton.Builder(restartButton).y(406)
                .text("Continue").end();
        layerMan.add(continueButton, Game.LAYER_UI);
        entityList.add(continueButton);
    }        
    
    public void setScore(final int score)
    {
        layerMan.remove(scoreLabel, Game.LAYER_UI);
        entityList.remove(scoreLabel);
        scoreLabel = new LabelBuilder(scoreLabel)
                .text(String.valueOf(score)).end();
        layerMan.add(scoreLabel, Game.LAYER_UI);
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
    @Override
    public void updateLogic(Game game)
    {
        // Hide the screen.
        game.groupMan.hideGroup(GroupManager.CLASS_GAME_OVER,
                GroupManager.LAYER_BOTTOM);

        // Reset a bunch of stuff.
        if (isRestartActivated() == true)
        {
            // Reset the board manager.
            game.boardMan.restart();
            
            // Reset the world manager.
            game.worldMan.restart();                        

            // Reset the timer to the initial.
            game.timerMan.setInitialTime(game.worldMan.getInitialTimer());
        }

        game.scoreMan.setLevelScore(0);
        game.scoreMan.setTotalScore(0); 
        game.scoreMan.setTargetLevelScore(
                game.worldMan.generateTargetLevelScore(
                game.worldMan.getLevel()));    
        
        game.progressBar.setProgressMax(game.scoreMan.getTargetLevelScore());
                       
        game.statMan.resetMoveCount();
        game.statMan.resetLineCount();

        // Create board and make it invisible.
        game.boardMan.setVisible(false);
        game.boardMan.generateBoard(game.worldMan.getItemList());                    

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
        game.startBoardShowAnimation();
    }
            
}
