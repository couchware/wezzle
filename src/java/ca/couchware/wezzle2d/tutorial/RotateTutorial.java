/*
 *  Wezzle
 *  Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.tutorial;

import ca.couchware.wezzle2d.Game;
import ca.couchware.wezzle2d.IWindow;
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
import ca.couchware.wezzle2d.manager.Settings.Key;
import ca.couchware.wezzle2d.manager.SettingsManager;
import ca.couchware.wezzle2d.piece.PieceType;
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
public class RotateTutorial extends AbstractTutorial
{                 
    
    /**
     * The constructor.
     */
    public RotateTutorial(IWindow win, Refactorer refactorer)
    {
        // Set the name.
        super(win, refactorer, "Rotate Tutorial");
        
        // This tutorial has a single rule.  It activates on level one.        
        addRule(new Rule(Rule.Type.LEVEL, Rule.Operation.EQ, 1));
    }
        
    public void tutorialInitialize(final Game game, ManagerHub hub)
    {        
        // Make convenience variables for the managers used.
        final BoardManager boardMan = hub.boardMan;
        final SettingsManager settingsMan = hub.settingsMan;
        final LayerManager layerMan = hub.layerMan;

        // Slow down refactor so the user can see more clearly what happens.
        refactorer.setRefactorSpeed(RefactorSpeed.SLOWER);                
        
        // Create the text that instructs the user to click the block.        
        this.labelList = new ArrayList<ITextLabel>();
        ITextLabel label = null;
        
        // Line 1.
        label = new LabelBuilder(280, 166)
                .alignment(EnumSet.of(Alignment.BOTTOM, Alignment.LEFT))                
                .color(settingsMan.getColor(Key.GAME_COLOR_PRIMARY))
                .size(16).text("Rotate pieces by clicking").build();
        layerMan.add(label, Layer.EFFECT);   
        this.labelList.add(label);
        
        // Line 2.
        label = new LabelBuilder(label).y(166 + 24)
                .text("the RIGHT MOUSE BUTTON").build();
        layerMan.add(label, Layer.EFFECT);                 
        this.labelList.add(label);
        
        // Line 3.
        label = new LabelBuilder(label).y(166 + 24 + 24)
                .text("or the CTRL KEY.").build();
        layerMan.add(label, Layer.EFFECT);                                 
        this.labelList.add(label);              
        
        // Create the speech bubble and add it to the layer manaager.
        // The speech bubble will be positioned over the button right
        // corner of the board.
        this.bubble = new SpeechBubble.Builder(
                    boardMan.getX() + (boardMan.getCellWidth() * 3) / 2,
                    boardMan.getY() + boardMan.getHeight() - boardMan.getCellHeight())
                .type(BubbleType.VERTICAL).text("Click here").build();
        layerMan.add(bubble, Layer.EFFECT);   
        layerMan.toFront(bubble, Layer.EFFECT);
    }  
        
    protected void createBoard(final Game game, ManagerHub hub)
    {
        // Make convenience variables for the managers used.
        final AnimationManager animationMan = hub.gameAnimationMan;
        final BoardManager boardMan = hub.boardMan;

        // Clear it first.
        boardMan.clearBoard();
                
        // Create bottom row.        
        boardMan.createTile(0, boardMan.getRows() - 1, 
                TileType.NORMAL, TileColor.RED);                               
                
        Tile t = boardMan.createTile(1, boardMan.getRows() - 1,
                TileType.NORMAL, TileColor.BLUE);

        // Set a click action.
        t.addTileListener(new Tile.ITileListener()
        {
           public void tileClicked()
           {
               // Fade out the bubble.
               IAnimation f = new FadeAnimation.Builder(FadeAnimation.Type.OUT, bubble)
                       .wait(0).duration(500).build();
               animationMan.add(f);
           }
        });
        
        boardMan.createTile(2, boardMan.getRows() - 1, 
                TileType.NORMAL, TileColor.RED);
        
        boardMan.createTile(3, boardMan.getRows() - 1, 
                TileType.NORMAL, TileColor.BLUE);
        
        // Create second-from-bottom row.
        boardMan.createTile(0, boardMan.getRows() - 2, 
                TileType.NORMAL, TileColor.PURPLE);
        
        boardMan.createTile(1, boardMan.getRows() - 2, 
                TileType.NORMAL, TileColor.BLUE);
        
        boardMan.createTile(2, boardMan.getRows() - 2, 
                TileType.NORMAL, TileColor.BLUE);
        
        boardMan.createTile(3, boardMan.getRows() - 2, 
                TileType.NORMAL, TileColor.YELLOW);    
        
        boardMan.setVisible(true);
    }   
    
    @Override
    protected void repeat(Game game, ManagerHub hub)
    {
        super.repeat(game, hub);

        // Set piece to line piece.
        hub.pieceMan.setPiece(PieceType.LINE.getPiece());

        // Set restriction board so that only the bottom left corner is
        // clickable.
        hub.pieceMan.clearRestrictionBoard();
        hub.pieceMan.reverseRestrictionBoard();
        hub.pieceMan.setRestrictionCell(0, hub.boardMan.getRows() - 1, true);
        hub.pieceMan.setRestrictionCell(1, hub.boardMan.getRows() - 1, true);
        hub.pieceMan.setRestrictionCell(2, hub.boardMan.getRows() - 1, true);
    }

    public Key getHasRunSettingsKey()
    {
        return Key.USER_TUTORIAL_ROTATE_RAN;
    }
}
