/*
 *  Wezzle
 *  Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.tutorial;

import ca.couchware.wezzle2d.Game;
import ca.couchware.wezzle2d.ManagerHub;
import ca.couchware.wezzle2d.Refactorer;
import ca.couchware.wezzle2d.Refactorer.RefactorSpeed;
import ca.couchware.wezzle2d.manager.LayerManager.Layer;
import ca.couchware.wezzle2d.ResourceFactory.LabelBuilder;
import ca.couchware.wezzle2d.Rule;
import ca.couchware.wezzle2d.animation.IAnimation;
import ca.couchware.wezzle2d.animation.FadeAnimation;
import ca.couchware.wezzle2d.graphics.IPositionable.Alignment;
import ca.couchware.wezzle2d.manager.AnimationManager;
import ca.couchware.wezzle2d.manager.BoardManager;
import ca.couchware.wezzle2d.manager.LayerManager;
import ca.couchware.wezzle2d.manager.PieceManager;
import ca.couchware.wezzle2d.manager.Settings.Key;
import ca.couchware.wezzle2d.manager.SettingsManager;
import ca.couchware.wezzle2d.tile.TileColor;
import ca.couchware.wezzle2d.tile.Tile;
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
public class BombTutorial extends AbstractTutorial
{                 
    
    /**
     * The constructor.
     */
    public BombTutorial(Refactorer refactorer)
    {
        // Set the name.
        super(refactorer, "Bomb Tutorial");
        
        // Activate the tutorial on a specific level.      
        addRule(new Rule(Rule.Type.LEVEL, Rule.Operation.EQ, 5));
    }
    
    @Override
    public void initialize(final Game game, ManagerHub hub)
    {
        // Invoke the super.
        super.initialize(game, hub);

        // Make convenience variables for the managers used.
        final BoardManager boardMan = hub.boardMan;
        final LayerManager layerMan = hub.layerMan;
        final SettingsManager settingsMan = hub.settingsMan;

        // Add this item to the world manager.
        //game.levelMan.addItem(new Item.Builder(TileType.BOMB)
        //        .initialAmount(0).maximumOnBoard(1).weight(0).end());
        
        // Slow down refactor so the user can see more clearly what happens.
        refactorer.setRefactorSpeed(RefactorSpeed.SLOW);                
        
        // Create the text that instructs the user to click the block.        
        this.labelList = new ArrayList<ITextLabel>();
        ITextLabel label = null;
        
        // Line 1.
        label = new LabelBuilder(280, 166)
                .alignment(EnumSet.of(Alignment.BOTTOM, Alignment.LEFT))                
                .color(settingsMan.getColor(Key.GAME_COLOR_PRIMARY))
                .size(16).text("Bombs destroy all tiles").build();
        layerMan.add(label, Layer.EFFECT);   
        this.labelList.add(label);
        
        // Line 2.
        label = new LabelBuilder(label).y(166 + 24)
                .text("surrounding them.").build();
        layerMan.add(label, Layer.EFFECT);                 
        this.labelList.add(label);
        
        // Line 3.
        label = new LabelBuilder(label).y(166 + 24 + 24 + 24)
                .text("Get one in a line to").build();
        layerMan.add(label, Layer.EFFECT);                                 
        this.labelList.add(label);
        
        label = new LabelBuilder(label).y(166 + 24 + 24 + 24 + 24)
                .text("explode it.").build();
        layerMan.add(label, Layer.EFFECT);                                 
        this.labelList.add(label);
                
        // Create the speech bubble and add it to the layer manaager.
        // The speech bubble will be positioned over the button right
        // corner of the board.
        this.bubble = new SpeechBubble.Builder(
                    boardMan.getX() + boardMan.getCellWidth()
                        + boardMan.getCellWidth() / 2,
                    boardMan.getY() + boardMan.getHeight() 
                        - boardMan.getCellHeight() * 3)
                .type(BubbleType.VERTICAL).text("Click here").build();
        layerMan.add(bubble, Layer.EFFECT);   
        layerMan.toFront(bubble, Layer.EFFECT);                        
        
        // Run the repeat tutorial method, that sets up the things that must
        // be reset each time the tutorial is run.
        repeat(game, hub);
    }
       
    protected void createBoard(final Game game, ManagerHub hub)
    {
        // Make convenience variables for the managers used.
        final AnimationManager animationMan = hub.animationMan;
        final BoardManager boardMan = hub.boardMan;
        
        // Clear it first.
        boardMan.clearBoard();
         
        // Set a click action.
        Tile.ITileListener listener = new Tile.ITileListener()
        {           
           public void tileClicked()
           {               
               // Fade out the bubble.            
               IAnimation f = new FadeAnimation.Builder(FadeAnimation.Type.OUT, bubble)
                       .wait(0).duration(500).build();
               animationMan.add(f);       
           }
        };                   
        
        // Create bottom row.        
        boardMan.createTile(0, boardMan.getRows() - 1, 
                TileType.NORMAL, TileColor.GREEN);                                 
                
        boardMan.createTile(1, boardMan.getRows() - 1, 
                TileType.NORMAL, TileColor.YELLOW);
        
        Tile t2 = boardMan.createTile(2, boardMan.getRows() - 1, 
                TileType.NORMAL, TileColor.RED);
        t2.addTileListener(listener);
        
        boardMan.createTile(3, boardMan.getRows() - 1, 
                TileType.NORMAL, TileColor.YELLOW);
        
        boardMan.createTile(4, boardMan.getRows() - 1, 
                TileType.NORMAL, TileColor.GREEN);
        
        boardMan.createTile(5, boardMan.getRows() - 1, 
                TileType.NORMAL, TileColor.YELLOW);
        
        // Create second-from-bottom row.
        boardMan.createTile(0, boardMan.getRows() - 2, 
                TileType.NORMAL, TileColor.YELLOW);
        
        // Create the bomb.     
        boardMan.createTile(1, boardMan.getRows() - 2, 
                TileType.BOMB, TileColor.YELLOW);        
        
        Tile t3 = boardMan.createTile(2, boardMan.getRows() - 2, 
                TileType.NORMAL, TileColor.RED);
        t3.addTileListener(listener);
        
        boardMan.createTile(3, boardMan.getRows() - 2, 
                TileType.NORMAL, TileColor.YELLOW);
        
        boardMan.createTile(4, boardMan.getRows() - 2, 
                TileType.NORMAL, TileColor.GREEN);
        
        boardMan.createTile(5, boardMan.getRows() - 2, 
                TileType.NORMAL, TileColor.BLUE);
        
        // Create third-from-bottom row.
        boardMan.createTile(0, boardMan.getRows() - 3, 
                TileType.NORMAL, TileColor.YELLOW);     
        
        Tile t4 = boardMan.createTile(1, boardMan.getRows() - 3, 
                TileType.NORMAL, TileColor.RED);
        t4.addTileListener(listener);
        
        boardMan.createTile(2, boardMan.getRows() - 3, 
                TileType.NORMAL, TileColor.YELLOW);
        
        boardMan.createTile(3, boardMan.getRows() - 3, 
                TileType.NORMAL, TileColor.GREEN);
        
        // Create fourth-from-bottom row.
        boardMan.createTile(0, boardMan.getRows() - 4, 
                TileType.NORMAL, TileColor.BLUE); 
        
        boardMan.setVisible(true);
    }   
    
    @Override
    public void finish(Game game, ManagerHub hub)
    {
        super.finish(game, hub);
        
        // Add this item to the world manager.
        //game.levelMan.removeItem(TileType.BOMB);
    }
    
    @Override
    protected void repeat(Game game, ManagerHub hub)
    {
        super.repeat(game, hub);

        // Make convenience variables for the managers used.
        final BoardManager boardMan = hub.boardMan;
        final PieceManager pieceMan = hub.pieceMan;

        // Set restriction board so that only the bottom left corner is
        // clickable.
        pieceMan.clearRestrictionBoard();
        pieceMan.reverseRestrictionBoard();       
        pieceMan.setRestrictionCell(1, boardMan.getRows() - 3, true);
        pieceMan.setRestrictionCell(2, boardMan.getRows() - 1, true);
        pieceMan.setRestrictionCell(2, boardMan.getRows() - 2, true);
    }

    public Key getHasRunSettingsKey()
    {
        return Key.USER_TUTORIAL_BOMB_RAN;
    }

}
