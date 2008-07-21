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
import ca.couchware.wezzle2d.tile.TileColor;
import ca.couchware.wezzle2d.piece.PieceDot;
import ca.couchware.wezzle2d.tile.TileEntity;
import ca.couchware.wezzle2d.ui.Label;
import ca.couchware.wezzle2d.ui.MultiLabel;
import ca.couchware.wezzle2d.ui.SpeechBubble;
import java.util.EnumSet;

/**
 * This is the basic tutorial.  It teaches the elementary rules of Wezzle.
 * 
 * @author cdmckay
 */
public class BasicTutorial extends Tutorial
{   
    
    /**
     * A speech bubble that indicates where the user should press.
     */
    SpeechBubble bubble;
    
    /**
     * The animation that makes the bubble blink.
     */
    BlinkAnimation bubbleAnimation;
    
    /**
     * The text that directs the user.
     */
    Label label;
    
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
                "colour.\n" +
                " \n" +
                "To make a line, click the\n" +
                "red tile to remove it."
                );
        game.layerMan.add(label, Game.LAYER_EFFECT);                 
        game.layerMan.toFront(label, Game.LAYER_EFFECT);
                
        // Stop the piece manager from dropping.
        game.pieceMan.setTileDropOnCommit(false);
        
        // Pause the timer.
        game.timerMan.setPaused(true);        
        
        // Now draw the fake board.
        // Clear it first.
        game.boardMan.clearBoard();
        
        // Create bottom row.        
        game.boardMan.createTile(0, game.boardMan.getRows() - 1, 
                TileEntity.class, TileColor.RED);                        
                
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

        // Change the piece to the dot.
        game.pieceMan.loadPiece(new PieceDot());
        
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
        bubbleAnimation = new BlinkAnimation(DurationType.CONTINUOUS, 
                4000, 300, bubble);        
        game.animationMan.add(bubbleAnimation);               
        
        // Reset move counter.
        game.statMan.resetMoveCount();
    }

    @Override
    protected boolean updateTutorial(Game game)
    {
        // If the move count is not 0, then the tutorial is over.
        if (game.statMan.getMoveCount() != 0)
        {            
            return false;
        }
        else
            return true;
    }

    @Override
    protected void finishTutorial(final Game game)
    {   
        // Load the score managers state.
        game.statMan.loadState();
        game.scoreMan.loadState();
        
        // Fade the board out.
        game.startBoardHideAnimation();
        game.pieceMan.stopAnimation();
        
        // Stop the piece animation.
        game.pieceMan.getPieceGrid().setVisible(false);
        
        // Remove the bubble animation.
        game.animationMan.remove(bubbleAnimation);
               
        // Fade out the bubble.
        game.animationMan.add(new FadeAnimation(FadeType.OUT, 0, 500, bubble)
        {
           // Remove the speech bubble.
           @Override
           public void onFinish()
           {
               // Remove the speech bubble.
               game.layerMan.remove(bubble, Game.LAYER_EFFECT);                              
           }
        });        
        
        // Fade out the label.
        game.animationMan.add(new FadeAnimation(FadeType.OUT, 0, 500, label)
        {
           // Remove the label.
           @Override
           public void onFinish()
           {
               // Remove the speech bubble.
               game.layerMan.remove(label, Game.LAYER_EFFECT);                              
           }
        });  
        
        // Remove the restriction board.
        game.pieceMan.clearRestrictionBoard();
        
        // Turn on tile drops.
        game.pieceMan.setTileDropOnCommit(true);                
    }

}
