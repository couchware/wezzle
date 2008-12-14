/*
 *  Wezzle
 *  Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.tutorial;

import ca.couchware.wezzle2d.Game;
import ca.couchware.wezzle2d.manager.Item;
import ca.couchware.wezzle2d.Refactorer;
import ca.couchware.wezzle2d.Refactorer.RefactorSpeed;
import ca.couchware.wezzle2d.manager.LayerManager.Layer;
import ca.couchware.wezzle2d.ResourceFactory.LabelBuilder;
import ca.couchware.wezzle2d.Rule;
import ca.couchware.wezzle2d.animation.IAnimation;
import ca.couchware.wezzle2d.animation.FadeAnimation;
import ca.couchware.wezzle2d.graphics.IPositionable.Alignment;
import ca.couchware.wezzle2d.manager.BoardManager.Direction;
import ca.couchware.wezzle2d.manager.Settings.Key;
import ca.couchware.wezzle2d.tile.TileColor;
import ca.couchware.wezzle2d.tile.TileEntity;
import ca.couchware.wezzle2d.tile.TileType;
import ca.couchware.wezzle2d.ui.ITextLabel;
import ca.couchware.wezzle2d.ui.SpeechBubble;
import ca.couchware.wezzle2d.ui.SpeechBubble.BubbleType;
import java.util.ArrayList;
import java.util.EnumSet;

/**
 * This is the basic tutorial.  It teaches the elementary rules of Wezzle.
 * 
 * @author cdmckay
 */
public class GravityTutorial extends AbstractTutorial
{                 
    
    /**
     * The constructor.
     */
    public GravityTutorial(Refactorer refactorer)
    {
        // Set the name.
        super(refactorer, "Gravity Tutorial");
        
        // Activate the tutorial on a specific level.
        addRule(new Rule(Rule.Type.LEVEL, Rule.Operation.EQ, 4));
    }
    
    @Override
    public void initialize(final Game game)
    {
        // Invoke the super.
        super.initialize(game);                            
        
        // Add this item to the world manager.
        //game.levelMan.addItem(new Item.Builder(TileType.GRAVITY)
        //        .initialAmount(0).maximumOnBoard(1).weight(0).end());
        
        // Slow down refactor so the user can see more clearly what happens.
        refactorer.setRefactorSpeed(RefactorSpeed.SLOW);                
        
        // Create the text that instructs the user to click the block.        
        this.labelList = new ArrayList<ITextLabel>();
        ITextLabel label = null;
        
        // Line 1.
        label = new LabelBuilder(280, 166)
                .alignment(EnumSet.of(Alignment.BOTTOM, Alignment.LEFT))
                .cached(false)
                .color(game.settingsMan.getColor(Key.GAME_COLOR_PRIMARY)).size(16)
                .text("Gravity tiles change the").end();
        game.layerMan.add(label, Layer.EFFECT);   
        this.labelList.add(label);
        
        // Line 2.
        label = new LabelBuilder(label).y(166 + 24)
                .text("direction that tiles slide.").end();
        game.layerMan.add(label, Layer.EFFECT);                 
        this.labelList.add(label);
        
        // Line 3.
        label = new LabelBuilder(label).y(166 + 24 + 24 + 24)
                .text("Get one in a line to").end();
        game.layerMan.add(label, Layer.EFFECT);                                 
        this.labelList.add(label);              
        
        label = new LabelBuilder(label).y(166 + 24 + 24 + 24 + 24)
                .text("try it out.").end();
        game.layerMan.add(label, Layer.EFFECT);                                 
        this.labelList.add(label);              
        
        // Create the speech bubble and add it to the layer manaager.
        // The speech bubble will be positioned over the button right
        // corner of the board.
        this.bubble = new SpeechBubble.Builder(
                    game.boardMan.getX() 
                    + 3 * game.boardMan.getCellWidth() 
                    + game.boardMan.getCellWidth() / 2,
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
        game.boardMan.createTile(0, game.boardMan.getRows() - 1, 
                TileType.NORMAL, TileColor.GREEN);
        
        game.boardMan.createTile(1, game.boardMan.getRows() - 1, 
                TileType.NORMAL, TileColor.BLUE);                                                      
        
        game.boardMan.createTile(2, game.boardMan.getRows() - 1, 
                TileType.GRAVITY, TileColor.BLUE);
        
        TileEntity t = game.boardMan.createTile(3, game.boardMan.getRows() - 1, 
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
        
        game.boardMan.createTile(4, game.boardMan.getRows() - 1, 
                TileType.NORMAL, TileColor.BLUE);
        
        game.boardMan.createTile(5, game.boardMan.getRows() - 1, 
                TileType.NORMAL, TileColor.PURPLE);
        
        game.boardMan.createTile(6, game.boardMan.getRows() - 1, 
                TileType.NORMAL, TileColor.YELLOW);     
        
        // Create second-from-bottom row.
        game.boardMan.createTile(0, game.boardMan.getRows() - 2, 
                TileType.NORMAL, TileColor.RED);
        
        game.boardMan.createTile(1, game.boardMan.getRows() - 2, 
                TileType.NORMAL, TileColor.BLUE);     
        
        game.boardMan.createTile(2, game.boardMan.getRows() - 2, 
                TileType.NORMAL, TileColor.GREEN);     
        
        // Create third-from-bottom row.
        game.boardMan.createTile(0, game.boardMan.getRows() - 3, 
                TileType.NORMAL, TileColor.GREEN);
        
        game.boardMan.createTile(1, game.boardMan.getRows() - 3, 
                TileType.NORMAL, TileColor.YELLOW);   
        
        // Create fourth-from-bottom row.
        game.boardMan.createTile(0, game.boardMan.getRows() - 4, 
                TileType.NORMAL, TileColor.YELLOW);
        
        game.boardMan.createTile(1, game.boardMan.getRows() - 4, 
                TileType.NORMAL, TileColor.YELLOW);
        
        game.boardMan.setVisible(true);
    }   

    @Override
    protected void finish(Game game)
    {
        super.finish(game);
        
        // Add this item to the world manager.
        //game.levelMan.removeItem(TileType.GRAVITY);
    }
    
    @Override
    protected void repeat(Game game)
    {
        super.repeat(game);
        
        // Set restriction board so that only the bottom left corner is
        // clickable.
        game.pieceMan.clearRestrictionBoard();
        game.pieceMan.reverseRestrictionBoard();
        game.pieceMan.setRestrictionCell(3, game.boardMan.getRows() - 1, true);
    }
    
}
