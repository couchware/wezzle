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
    private RectangularBooleanButton restartButton;
    
    /**
     * The continue button.
     */
    private RectangularBooleanButton continueButton;
    
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
//        headerLabel = ResourceFactory.get().getLabel(400, 181);        
//        headerLabel.setSize(26);
//        headerLabel.setAlignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER));
//        headerLabel.setColor(Game.TEXT_COLOR);
//        headerLabel.setText("Game over :(");
//        headerLabel.setVisible(false);
        headerLabel = new LabelBuilder(400, 181)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                .color(Game.TEXT_COLOR).size(26).text("Game over :(")
                .visible(false).end();
        layerMan.add(headerLabel, Game.LAYER_UI);
        entityList.add(headerLabel);
        
        // Create the final score header.
//        scoreHeaderLabel = ResourceFactory.get().getLabel(400, 234);        
//        scoreHeaderLabel.setSize(14);
//        scoreHeaderLabel.setAlignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER));
//        scoreHeaderLabel.setColor(Game.TEXT_COLOR);
//        scoreHeaderLabel.setText("Your final score was");
//        scoreHeaderLabel.setVisible(false);
        scoreHeaderLabel = new LabelBuilder(400, 234)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                .color(Game.TEXT_COLOR).size(14)
                .text("Your final score was")
                .visible(false).end();
        layerMan.add(scoreHeaderLabel, Game.LAYER_UI); 
        entityList.add(scoreHeaderLabel);
        
        // Create the final score label.
//        scoreLabel = ResourceFactory.get().getLabel(400, 270);        
//        scoreLabel.setSize(30);
//        scoreLabel.setAlignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER));
//        scoreLabel.setColor(Game.TEXT_COLOR);
//        scoreLabel.setText("0");
//        scoreLabel.setVisible(false);
        scoreLabel = new LabelBuilder(400, 270)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                .color(Game.TEXT_COLOR).text("0").visible(false).end();
        layerMan.add(scoreLabel, Game.LAYER_UI); 
        entityList.add(scoreLabel);
        
        // Create restart button.
        restartButton = new RectangularBooleanButton(window, 400, 345, "Restart");        
        restartButton.setNormalOpacity(70);
//        restartButton.setText("Restart");        
        restartButton.setAlignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER));
        restartButton.setVisible(false);
        layerMan.add(restartButton, Game.LAYER_UI);
        entityList.add(restartButton);
        
        // Create continue button.
        continueButton = new RectangularBooleanButton(window, 400, 406, "Continue");
        continueButton.setNormalOpacity(70);
//        continueButton.setText("Continue");        
        continueButton.setAlignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER));
        continueButton.setVisible(false);
        layerMan.add(continueButton, Game.LAYER_UI);
        entityList.add(continueButton);
    }        
    
    public void setScore(final int score)
    {
        scoreLabel = new LabelBuilder(scoreLabel)
                .text(String.valueOf(score)).end();
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
