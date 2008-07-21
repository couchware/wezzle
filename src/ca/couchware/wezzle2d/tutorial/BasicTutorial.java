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
     * The text that directs the user.
     */
    Label label;
    
    /**
     * The constructor.
     */
    public BasicTutorial()
    {
        // This tutorial has a single rule.  It activates on level one.
        Rule[] rules = new Rule[1];
        rules[0] = new Rule(Rule.Type.LEVEL, Rule.Operation.EQ, 1);     
        setRules(rules);
    }
    
    @Override
    protected void initializeTutorial(Game game)
    {
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
        game.layerMan.add(label, Game.LAYER_TILE);
        
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
        game.layerMan.add(bubble, Game.LAYER_UI);    
        
        BlinkAnimation a = new BlinkAnimation(DurationType.CONTINUOUS, 
                4000, 300, bubble);        
//        FadeAnimation a = new FadeAnimation(FadeType.LOOP_OUT, bubble);
        game.animationMan.add(a);
        
        // Stop the piece manager from dropping.
        game.pieceMan.setTileDropOnCommit(false);
        
        // Pause the timer.
        game.timerMan.setPaused(true);        
        
        // Now draw the fake board.
        // Clear it first.
        game.boardMan.clearBoard();
        
        // Create bottom row.        
        TileEntity t = game.boardMan.createTile(0, game.boardMan.getRows() - 1, 
                TileEntity.class, TileColor.RED);        
        //FadeAnimation a = new FadeAnimation(FadeType.LOOP_OUT, 0, 1200, t);
        //a.setMinOpacity(30);               
                
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
        
        // Reset move counter.
        game.moveMan.resetMoveCount();
    }

    @Override
    protected boolean updateTutorial(Game game)
    {
        // If the move count is not 0, then the tutorial is over.
        if (game.moveMan.getMoveCount() != 0)
        {
            finishTutorial(game);
            return false;
        }
        else
            return true;
    }

    @Override
    protected void finishTutorial(Game game)
    {                
                
    }

}
