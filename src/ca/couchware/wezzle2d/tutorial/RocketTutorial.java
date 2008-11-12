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
import ca.couchware.wezzle2d.tile.RocketTileEntity;
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
public class RocketTutorial extends AbstractTutorial
{                
    
    /**
     * The constructor.
     */
    public RocketTutorial()
    {
        // Set the name.
        super("Rocket Tutorial");
        
        // Activate the tutorial on a specific level.
        addRule(new Rule(Rule.Type.LEVEL, Rule.Operation.EQ, 3));
    }
    
    @Override
    public void initialize(final Game game)
    {
        // Invoke the super.
        super.initialize(game);   
        
        // Slow down refactor so the user can see more clearly what happens.
        Refactorer.get().setRefactorSpeed(RefactorSpeed.SLOW);
        
        // Set restriction board so that only the bottom left corner is
        // clickable.
        game.pieceMan.clearRestrictionBoard();
        game.pieceMan.reverseRestrictionBoard();
        game.pieceMan.setRestrictionCell(0, game.boardMan.getRows() - 1, true);
        game.pieceMan.setRestrictionCell(1, game.boardMan.getRows() - 3, true);
        game.pieceMan.setRestrictionCell(2, game.boardMan.getRows() - 1, true);
        game.pieceMan.setRestrictionCell(2, game.boardMan.getRows() - 2, true);
        
        // Create the text that instructs the user to click the block.        
        this.labelList = new ArrayList<ILabel>();
        ILabel label = null;
        
        // Line 1.
        label = new LabelBuilder(280, 166)
                .alignment(EnumSet.of(Alignment.BOTTOM, Alignment.LEFT))
                .cached(false)
                .color(Game.TEXT_COLOR1).size(16)
                .text("Rockets destroy all tiles").end();
        game.layerMan.add(label, Layer.EFFECT);   
        this.labelList.add(label);
        
        // Line 2.
        label = new LabelBuilder(label).y(166 + 24)
                .text("in their path. Get one in a").end();
        game.layerMan.add(label, Layer.EFFECT);                 
        this.labelList.add(label);
        
        // Line 3.
        label = new LabelBuilder(label).y(166 + 24 + 24)
                .text("line to fire it.").end();
        game.layerMan.add(label, Layer.EFFECT);                                 
        this.labelList.add(label);
       
        // Create the speech bubble and add it to the layer manaager.
        // The speech bubble will be positioned over the button right
        // corner of the board.
        this.bubble = new SpeechBubble.Builder(
                    game.boardMan.getX() + game.boardMan.getCellWidth()
                        + game.boardMan.getCellWidth() / 2,
                    game.boardMan.getY() + game.boardMan.getHeight() 
                        - game.boardMan.getCellHeight() * 3)
                .type(BubbleType.VERTICAL).text("Click here").end();                
        game.layerMan.add(this.bubble, Layer.EFFECT);   
        game.layerMan.toFront(this.bubble, Layer.EFFECT);                             
        
        // Run the repeat tutorial method, that sets up the things that must
        // be reset each time the tutorial is run.
        repeat(game);                                                                                                                                     
    } 
    
    protected void createBoard(final Game game)
    {        
        // Clear it first.
        game.boardMan.clearBoard();
         
        // Set a click action.
        Runnable r = new Runnable()
        {           
           public void run()
           {               
               // Fade out the bubble.            
               IAnimation f = new FadeAnimation.Builder(FadeAnimation.Type.OUT, bubble)
                       .wait(0).duration(500).end();
               game.animationMan.add(f);       
           }
        };                   
        
        // Create bottom row.        
        TileEntity t1 = game.boardMan.createTile(0, game.boardMan.getRows() - 1, 
                TileType.NORMAL, TileColor.RED);                         
        t1.setClickRunnable(r);
                
        game.boardMan.createTile(1, game.boardMan.getRows() - 1, 
                TileType.NORMAL, TileColor.BLUE);
        
        TileEntity t2 = game.boardMan.createTile(2, game.boardMan.getRows() - 1, 
                TileType.NORMAL, TileColor.RED);
        t2.setClickRunnable(r);
        
        game.boardMan.createTile(3, game.boardMan.getRows() - 1, 
                TileType.NORMAL, TileColor.BLUE);
        
        game.boardMan.createTile(4, game.boardMan.getRows() - 1, 
                TileType.NORMAL, TileColor.GREEN);
        
        game.boardMan.createTile(5, game.boardMan.getRows() - 1, 
                TileType.NORMAL, TileColor.BLUE);
        
        // Create second-from-bottom row.
        game.boardMan.createTile(0, game.boardMan.getRows() - 2, 
                TileType.NORMAL, TileColor.BLUE);
        
        // Create the rocket.     
        RocketTileEntity rocket = (RocketTileEntity) game.boardMan.createTile(
                1, game.boardMan.getRows() - 2, 
                TileType.ROCKET, TileColor.BLUE);
        rocket.setDirection(RocketTileEntity.Direction.RIGHT);
        
        TileEntity t3 = game.boardMan.createTile(2, game.boardMan.getRows() - 2, 
                TileType.NORMAL, TileColor.RED);
        t3.setClickRunnable(r);
        
        game.boardMan.createTile(3, game.boardMan.getRows() - 2, 
                TileType.NORMAL, TileColor.BLUE);
        
        game.boardMan.createTile(4, game.boardMan.getRows() - 2, 
                TileType.NORMAL, TileColor.GREEN);
        
        game.boardMan.createTile(5, game.boardMan.getRows() - 2, 
                TileType.NORMAL, TileColor.YELLOW);
        
        // Create third-from-bottom row.
        game.boardMan.createTile(0, game.boardMan.getRows() - 3, 
                TileType.NORMAL, TileColor.BLUE);     
        
        TileEntity t4 = game.boardMan.createTile(1, game.boardMan.getRows() - 3, 
                TileType.NORMAL, TileColor.RED);
        t4.setClickRunnable(r);
        
        game.boardMan.createTile(2, game.boardMan.getRows() - 3, 
                TileType.NORMAL, TileColor.BLUE);
        
        game.boardMan.createTile(3, game.boardMan.getRows() - 3, 
                TileType.NORMAL, TileColor.GREEN);
        
        game.boardMan.setVisible(true);
    }   

}
