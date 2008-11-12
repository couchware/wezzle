/*
 *  Wezzle
 *  Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.tutorial;

import ca.couchware.wezzle2d.Game;
import ca.couchware.wezzle2d.Refactorer;
import ca.couchware.wezzle2d.Refactorer.RefactorSpeed;
import ca.couchware.wezzle2d.manager.LayerManager.Layer;
import ca.couchware.wezzle2d.ResourceFactory.LabelBuilder;
import ca.couchware.wezzle2d.Rule;
import ca.couchware.wezzle2d.animation.IAnimation;
import ca.couchware.wezzle2d.animation.FadeAnimation;
import ca.couchware.wezzle2d.graphics.IPositionable.Alignment;
import ca.couchware.wezzle2d.tile.TileColor;
import ca.couchware.wezzle2d.tile.TileEntity;
import ca.couchware.wezzle2d.tile.TileType;
import ca.couchware.wezzle2d.ui.ILabel;
import ca.couchware.wezzle2d.ui.SpeechBubble;
import ca.couchware.wezzle2d.ui.SpeechBubble.BubbleType;
import java.util.ArrayList;
import java.util.EnumSet;

/**
 * This is the basic tutorial.  It teaches the elementary rules of Wezzle.
 * 
 * @author cdmckay
 */
public class BasicTutorial extends AbstractTutorial
{                 
    
    /**
     * The constructor.
     */
    public BasicTutorial()
    {
        // Set the name.
        super("Basic Tutorial");
        
        // This tutorial has a single rule.  It activates on level one.        
        addRule(new Rule(Rule.Type.LEVEL, Rule.Operation.EQ, 1));
    }
    
    @Override
    public void initialize(final Game game)
    {
        // Invoke the super.
        super.initialize(game);            
        
        // Slow down refactor so the user can see more clearly what happens.
        Refactorer.get().setRefactorSpeed(RefactorSpeed.SLOWER);
        
         // Set restriction board so that only the bottom left corner is
        // clickable.
        game.pieceMan.clearRestrictionBoard();
        game.pieceMan.reverseRestrictionBoard();
        game.pieceMan.setRestrictionCell(0, game.boardMan.getRows() - 1, true);
        
        // Create the text that instructs the user to click the block.        
        this.labelList = new ArrayList<ILabel>();
        ILabel label = null;
        
        // Line 1.
        label = new LabelBuilder(280, 166)
                .alignment(EnumSet.of(Alignment.BOTTOM, Alignment.LEFT))
                .cached(false)
                .color(Game.TEXT_COLOR1).size(16)
                .text("Lines are made by lining").end();
        game.layerMan.add(label, Layer.EFFECT);   
        this.labelList.add(label);
        
        // Line 2.
        label = new LabelBuilder(label).y(166 + 24)
                .text("up 3 tiles of the same").end();
        game.layerMan.add(label, Layer.EFFECT);                 
        this.labelList.add(label);
        
        // Line 3.
        label = new LabelBuilder(label).y(166 + 24 + 24)
                .text("colour.").end();
        game.layerMan.add(label, Layer.EFFECT);                                 
        this.labelList.add(label);              
        
        // Create the speech bubble and add it to the layer manaager.
        // The speech bubble will be positioned over the button right
        // corner of the board.
        this.bubble = new SpeechBubble.Builder(
                    game.boardMan.getX() + game.boardMan.getCellWidth() / 2,
                    game.boardMan.getY() + game.boardMan.getHeight() 
                        - game.boardMan.getCellHeight())
                .type(BubbleType.VERTICAL).text("Click here").end();                
        game.layerMan.add(bubble, Layer.EFFECT);   
        game.layerMan.toFront(bubble, Layer.EFFECT);                         
        
        // Run the repeat tutorial method, that sets up the things that must
        // be reset each time the tutorial is run.
        repeat(game);                                                                                                                                     
    }  
        
    protected void createBoard(final Game game)
    {        
        // Clear it first.
        game.boardMan.clearBoard();
                
        // Create bottom row.        
        TileEntity t = game.boardMan.createTile(0, game.boardMan.getRows() - 1, 
                TileType.NORMAL, TileColor.RED);
        
        // Set a click action.
        t.setClickRunnable(new Runnable()
        {           
           public void run()
           {               
               // Fade out the bubble.            
               IAnimation f = new FadeAnimation.Builder(FadeAnimation.Type.OUT, bubble)
                       .wait(0).duration(500).end();
               game.animationMan.add(f);       
           }
        });                
                
        game.boardMan.createTile(1, game.boardMan.getRows() - 1, 
                TileType.NORMAL, TileColor.BLUE);
        
        game.boardMan.createTile(2, game.boardMan.getRows() - 1, 
                TileType.NORMAL, TileColor.BLUE);
        
        game.boardMan.createTile(3, game.boardMan.getRows() - 1, 
                TileType.NORMAL, TileColor.YELLOW);
        
        // Create second-from-bottom row.
        game.boardMan.createTile(0, game.boardMan.getRows() - 2, 
                TileType.NORMAL, TileColor.BLUE);
        
        game.boardMan.createTile(1, game.boardMan.getRows() - 2, 
                TileType.NORMAL, TileColor.BLUE);
        
        game.boardMan.createTile(2, game.boardMan.getRows() - 2, 
                TileType.NORMAL, TileColor.PURPLE);
        
        game.boardMan.createTile(3, game.boardMan.getRows() - 2, 
                TileType.NORMAL, TileColor.YELLOW);
        
        game.boardMan.setVisible(true);
    }   

}
