/*
 *  Wezzle
 *  Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.tutorial;

import static ca.couchware.wezzle2d.animation.BlinkAnimation.DurationType;
import static ca.couchware.wezzle2d.animation.FadeAnimation.FadeType;
import static ca.couchware.wezzle2d.graphics.Positionable.Alignment;
import static ca.couchware.wezzle2d.ui.SpeechBubble.BubbleType;
import ca.couchware.wezzle2d.Game;
import ca.couchware.wezzle2d.Rule;
import ca.couchware.wezzle2d.animation.BlinkAnimation;
import ca.couchware.wezzle2d.animation.FadeAnimation;
import ca.couchware.wezzle2d.graphics.EntityGroup;
import ca.couchware.wezzle2d.tile.TileColor;
import ca.couchware.wezzle2d.piece.PieceDot;
import ca.couchware.wezzle2d.tile.TileEntity;
import ca.couchware.wezzle2d.ui.Label;
import ca.couchware.wezzle2d.ui.MultiLabel;
import ca.couchware.wezzle2d.ui.SpeechBubble;
import ca.couchware.wezzle2d.ui.button.RectangularBooleanButton;
import java.util.EnumSet;

/**
 * This is the basic tutorial.  It teaches the elementary rules of Wezzle.
 * 
 * @author cdmckay
 */
public class BasicTutorial extends Tutorial
{   
    
    /**
     * Is the menu visible?
     */
    private boolean menuShown = false;
    
    /**
     * The text that directs the user.
     */
    private Label label;
    
    /**
     * A speech bubble that indicates where the user should press.
     */
    private SpeechBubble bubble;       
    
    /**
     * The repeat button.
     */
    private RectangularBooleanButton repeatButton;
    
    /**
     * The continue button.
     */
    private RectangularBooleanButton continueButton;
    
    /**
     * The constructor.
     */
    public BasicTutorial()
    {
        // Set the name.
        super("Basic Tutorial");
        
        // This tutorial has a single rule.  It activates on level one.
        Rule[] rules = new Rule[1];
        rules[0] = new Rule(Rule.Type.LEVEL, Rule.Operation.EQ, 1);     
        setRules(rules);
    }
    
    @Override
    protected void initializeTutorial(Game game)
    {
        // Save the manager states.
        game.statMan.saveState();
        game.scoreMan.saveState();
        
        // Turn off the game in progress variable in the world manager.
        game.worldMan.setGameInProgress(false);
        
        // Slow down refactor so the user can see more clearly what happens.
        game.setRefactorSpeed(50);
        
         // Set restriction board so that only the bottom left corner is
        // clickable.
        game.pieceMan.clearRestrictionBoard();
        game.pieceMan.reverseRestrictionBoard();
        game.pieceMan.setRestrictionCell(0, game.boardMan.getRows() - 1, true);
        
        // Create the text that instructs the user to click the block.
        label = new MultiLabel(280, 166, 22);
        label.setColor(Game.TEXT_COLOR);
        label.setSize(16);
        label.setAlignment(EnumSet.of(Alignment.BOTTOM, Alignment.LEFT));
        label.setText(
                "Lines are made by lining\n" +
                "up 3 tiles of the same\n" +
                "colour."               
                );
        game.layerMan.add(label, Game.LAYER_EFFECT);                 
        game.layerMan.toFront(label, Game.LAYER_EFFECT);
        
        // Stop the piece manager from dropping.
        game.pieceMan.setTileDropOnCommit(false);
        
        // Stop the timer.
        game.timerMan.setStopped(true); 
        
        // Create the speech bubble and add it to the layer manaager.
        // The speech bubble will be positioned over the button right
        // corner of the board.
        bubble = new SpeechBubble(
                    game.boardMan.getX() + game.boardMan.getCellWidth() / 2,
                    game.boardMan.getY() + game.boardMan.getHeight() 
                        - game.boardMan.getCellHeight(),
                    BubbleType.VERTICAL,
                    "Click here"                    
                );        
        game.layerMan.add(bubble, Game.LAYER_EFFECT);   
        game.layerMan.toFront(bubble, Game.LAYER_EFFECT);           
        
        // Create repeat button.
        repeatButton = new RectangularBooleanButton(
                game.getGameWindow(), 400, 310);        
        repeatButton.setNormalOpacity(70);
        repeatButton.setText("Repeat");
        repeatButton.getLabel().setSize(18);
        repeatButton.setAlignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER));
        repeatButton.setVisible(false);
        game.layerMan.add(repeatButton, Game.LAYER_EFFECT);
        
         // Create continue button.
        continueButton = new RectangularBooleanButton(
                game.getGameWindow(), 400, 370);
        continueButton.setNormalOpacity(70);
        continueButton.setText("Continue");
        continueButton.getLabel().setSize(18);
        continueButton.setAlignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER));
        continueButton.setVisible(false);
        game.layerMan.add(continueButton, Game.LAYER_EFFECT);    
        
        // Run the repeat tutorial method, that sets up the things that must
        // be reset each time the tutorial is run.
        repeatTutorial(game);                                                                                                                                     
    }

    @Override
    protected boolean updateTutorial(final Game game)
    {
        // If the move count is not 0, then the tutorial is over.
        if (game.statMan.getMoveCount() != 0 && menuShown == false)
        {               
            // Stop the piece animation.
            game.pieceMan.getPieceGrid().setVisible(false); 
            game.pieceMan.stopAnimation();                                            
            
            // Fade the board out.
            //game.startBoardHideAnimation();
            EntityGroup e = game.boardMan.getTiles(game.boardMan.getCells() / 2, 
                    game.boardMan.getCells() - 1);
            game.animationMan.add(new FadeAnimation(FadeType.OUT, 0, 500, e));
                        
            // Fade in two new buttons.
            FadeAnimation f;                        
             
            f = new FadeAnimation(FadeType.IN, 100, 500, repeatButton);
            f.setMaxOpacity(70);
            game.animationMan.add(f);
                         
            f = new FadeAnimation(FadeType.IN, 100, 500, continueButton);
            f.setMaxOpacity(70);
            game.animationMan.add(f);
            
            // Menu is now shown.
            menuShown = true;
            
            // Create the confirm window.
//            confirmWindow = new Window(game.getGameWindow(), 
//                    400, 300, 
//                    310, 150);
//            confirmWindow.setAlignment(
//                    EnumSet.of(Alignment.MIDDLE, Alignment.CENTER));            
//            game.layerMan.add(confirmWindow, Game.LAYER_EFFECT);            
            
            return true;
        }
        else if (menuShown == true)
        {
            if (repeatButton.isActivated() == true)
            {
                repeatTutorial(game);                
            }
            else if (continueButton.isActivated() == true)
            {
                return false;
            }
        }      
        
        return true;
    }

    @Override
    protected void finishTutorial(final Game game)
    {   
        // Load the score managers state.
        game.statMan.loadState();
        game.scoreMan.loadState();                                                
        
        // Remove the repeat button.
        repeatButton.setVisible(false);
        game.layerMan.remove(repeatButton, Game.LAYER_EFFECT);
        repeatButton = null;
        
        // Remove the continue button.
        continueButton.setVisible(false);
        game.layerMan.remove(continueButton, Game.LAYER_EFFECT);
        continueButton = null;
        
        // Remove the speech bubble.
        game.layerMan.remove(bubble, Game.LAYER_EFFECT);
        bubble = null;
        
        // Remove the text label.
        game.layerMan.remove(label, Game.LAYER_EFFECT);
        label = null;
        
        // Remove the restriction board.
        game.pieceMan.clearRestrictionBoard();
        
        // Turn on tile drops.
        game.pieceMan.setTileDropOnCommit(true);   
        
        // Start the timer.        
        game.timerMan.setStopped(false);  
        
        // Reset the refactor speed.
        game.resetRefactorSpeed();
    }
    
    @Override
    protected void repeatTutorial(final Game game)
    {
        // Reset move counter.
        game.statMan.resetMoveCount();      
        
        // Reset the scores.
        game.scoreMan.resetScore();
        
        // Create the fake board.
        createBoard(game);
        
        // Change the piece to the dot.
        game.pieceMan.loadPiece(new PieceDot());    
        
        // Make sure buttons aren't visible.
        repeatButton.setVisible(false);
        if (repeatButton.isActivated() == true)
            repeatButton.setActivated(false);
        
        continueButton.setVisible(false);
        if (continueButton.isActivated() == true)
            continueButton.setActivated(false);
        
        // Menu is not shown.
        menuShown = false;
        
        // Make sure the bubble opacity is full.
        bubble.setOpacity(100);            
        
        // Piece is visible.
        game.pieceMan.getPieceGrid().setVisible(true);
    }
    
    private void createBoard(final Game game)
    {        
        // Clear it first.
        game.boardMan.clearBoard();
                
        // Create bottom row.        
        TileEntity t = new TileEntity(game.boardMan, TileColor.RED, 0, 0)
        {
           @Override
           public void onClick()
           {
                // Fade out the bubble.
                game.animationMan.add(new FadeAnimation(FadeType.OUT, 0, 500, bubble));       
           }
        };        
        game.boardMan.addTile(0, game.boardMan.getRows() - 1, t);        
                
        game.boardMan.createTile(1, game.boardMan.getRows() - 1, 
                TileEntity.class, TileColor.BLUE);
        
        game.boardMan.createTile(2, game.boardMan.getRows() - 1, 
                TileEntity.class, TileColor.BLUE);
        
        game.boardMan.createTile(3, game.boardMan.getRows() - 1, 
                TileEntity.class, TileColor.YELLOW);
        
        // Create second-from-bottom row.
        game.boardMan.createTile(0, game.boardMan.getRows() - 2, 
                TileEntity.class, TileColor.BLUE);
        
        game.boardMan.createTile(1, game.boardMan.getRows() - 2, 
                TileEntity.class, TileColor.BLUE);
        
        game.boardMan.createTile(2, game.boardMan.getRows() - 2, 
                TileEntity.class, TileColor.PURPLE);
        
        game.boardMan.createTile(3, game.boardMan.getRows() - 2, 
                TileEntity.class, TileColor.YELLOW);
    }

}
