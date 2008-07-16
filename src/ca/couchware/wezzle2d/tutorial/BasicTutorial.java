/*
 *  Wezzle
 *  Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.tutorial;

import ca.couchware.wezzle2d.Game;
import ca.couchware.wezzle2d.Rule;
import ca.couchware.wezzle2d.tile.TileColor;
import ca.couchware.wezzle2d.piece.PieceDot;
import ca.couchware.wezzle2d.tile.TileEntity;
import ca.couchware.wezzle2d.ui.SpeechBubble;

/**
 * This is the basic tutorial.  It teaches the elementary rules of Wezzle.
 * 
 * @author cdmckay
 */
public class BasicTutorial extends Tutorial
{

    /**
     * The speech bubble that instructs the player where to click.
     */
    private SpeechBubble bubble;
    
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
        
        // Create the speech bubble and add it to the layer manaager.
        // The speech bubble will be positioned over the button right
        // corner of the board.
        bubble = new SpeechBubble(
                    game.boardMan.getX() + game.boardMan.getCellWidth() / 2,
                    game.boardMan.getY() + game.boardMan.getHeight()
                        - game.boardMan.getCellHeight() / 2,
                    SpeechBubble.TYPE_NORMAL,
                    "Click here"                    
                );
        game.layerMan.add(bubble, Game.LAYER_UI);
        
        // Now draw the fake board.
        // Create bottom row.
        game.boardMan.createTile(0, game.boardMan.getRows() - 1, 
                TileEntity.class, TileColor.GREEN);
        
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
    }

    @Override
    protected boolean updateTutorial(Game game)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected void finishTutorial(Game game)
    {                
        // Remove the bubble.
        game.layerMan.remove(bubble, Game.LAYER_UI);
        bubble = null;
    }

}
