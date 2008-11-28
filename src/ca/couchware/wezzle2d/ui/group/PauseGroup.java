
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ca.couchware.wezzle2d.ui.group;

import ca.couchware.wezzle2d.manager.LayerManager;
import ca.couchware.wezzle2d.*;
import ca.couchware.wezzle2d.manager.LayerManager.Layer;
import ca.couchware.wezzle2d.ResourceFactory.LabelBuilder;
import ca.couchware.wezzle2d.ui.ILabel;
import ca.couchware.wezzle2d.event.*;
import ca.couchware.wezzle2d.manager.ListenerManager.GameType;
import ca.couchware.wezzle2d.manager.LogManager;
import ca.couchware.wezzle2d.manager.Settings.Key;
import ca.couchware.wezzle2d.manager.SettingsManager;
import ca.couchware.wezzle2d.manager.StatManager;
import ca.couchware.wezzle2d.util.Util;
import java.util.EnumSet;

/**
 *
 * @author cdmckay
 */
public class PauseGroup extends AbstractGroup implements 
        IGameListener, 
        IMoveListener, 
        ILineListener
{      
    
    /**
     * A reference to the layer manager.  This is used by groups to add
     * and remove things like buttons and sliders.
     */
    final private LayerManager layerMan;      
    
    /** A reference to the stats manager.  Used to grab the latest stats. */
    final private StatManager statMan;
    
    /** The main label showing the paused text. */
    private ILabel mainLabel;
    
    /** The moves label. */
    private ILabel movesLabel;
    
    /** The lines label. */
    private ILabel linesLabel;
    
    /** The lines per move label. */
    private ILabel linesPerMoveLabel;
    
    private int    moves = 0;
    private int    lines = 0;
    private double linesPerMove = 0.0;
       
    /**
     * The constructor.    
     */    
    public PauseGroup(
            SettingsManager settingsMan,
            LayerManager layerMan, 
            StatManager statMan)
    {
        // Remember the managers.
        this.layerMan = layerMan;
        this.statMan  = statMan;
               
        // Create the "Paused" text.
        mainLabel = new LabelBuilder(400, 245)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                .color(settingsMan.getColor(Key.GAME_COLOR_PRIMARY)).size(30)
                .text("Paused")
                .visible(false).end();
        layerMan.add(mainLabel, Layer.UI);
        entityList.add(mainLabel);

        movesLabel = new LabelBuilder(400, 310)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                .color(settingsMan.getColor(Key.GAME_COLOR_PRIMARY)).size(18)
                .text("0 moves taken")
                .visible(false).end();
        layerMan.add(movesLabel, Layer.UI);
        entityList.add(movesLabel);

        linesLabel = new LabelBuilder(400, 340)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                .color(settingsMan.getColor(Key.GAME_COLOR_PRIMARY)).size(18)
                .text("0 lines cleared")
                .visible(false).end();
        layerMan.add(linesLabel, Layer.UI);
        entityList.add(linesLabel);

        linesPerMoveLabel = new LabelBuilder(400, 370)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))                
                .color(settingsMan.getColor(Key.GAME_COLOR_PRIMARY)).size(18)
                .text("0.0 lines per move")
                .visible(false).end();
        layerMan.add(linesPerMoveLabel, Layer.UI);
        entityList.add(linesPerMoveLabel);
    }
        
    private void setMoves(int moves)
    {
        if (this.moves == moves) return;
        
        // Record the current number of moves.
        this.moves = moves;
        
        // Set the moves label.
        layerMan.remove(movesLabel, Layer.UI);
        entityList.remove(movesLabel);
        if (moves == 1)
        {
            movesLabel = new LabelBuilder(movesLabel)
                    .text("1 move taken").end();            
        }
        else
        {
            movesLabel = new LabelBuilder(movesLabel)
                    .text(moves + " moves taken").end(); 
        }
        layerMan.add(movesLabel, Layer.UI);
        entityList.add(movesLabel);
    }       
    
    private void setLines(int lines)
    {
        if (this.lines == lines) return;
        
        // Record the current number of moves.
        this.lines = lines;
        
        // Set the lines label.
        layerMan.remove(linesLabel, Layer.UI);
        entityList.remove(linesLabel);
        if (lines == 1)
        {
            linesLabel = new LabelBuilder(linesLabel)
                    .text("1 line cleared").end();            
        }
        else
        {
            linesLabel = new LabelBuilder(linesLabel)
                    .text(lines + " lines cleared").end(); 
        }
        layerMan.add(linesLabel, Layer.UI);  
        entityList.add(linesLabel);
    }
    
    private void setLinesPerMove(double linesPerMove)
    {
        if (Util.equals(this.linesPerMove, linesPerMove)) return;
        
        // Record the current lpm.
        this.linesPerMove = linesPerMove;
        
        // Set the lines per move label.
        //linesPerMoveLabel.setText(lpm + " lines per move");
        layerMan.remove(linesPerMoveLabel, Layer.UI);
        entityList.remove(linesPerMoveLabel);
        linesPerMoveLabel = new LabelBuilder(linesPerMoveLabel)
                .text(linesPerMove + " lines per move").end();
        layerMan.add(linesPerMoveLabel, Layer.UI);
        entityList.add(linesPerMoveLabel);
    }      
    
     @Override
    public void setVisible(final boolean visible)
    {
        // This is more important than you think.  Basically, since we might
        // be adding or removing listeners, we want to make sure we only add
        // a listener once, and that we only remove it once.  This ensures that.
        if (this.visible == visible) return;            
        
        // Invoke super.  This will remove the listener from pause which
        // we will re-add below.
        super.setVisible(visible);       
    }
    
    @Override
    public void setActivated(final boolean activated)
    {
        // Invoke super.
        super.setActivated(activated);               
    }
    
    public void updateLogic(Game game)
    {
        // Intentionally left blank.
    }
            
    public void moveCommitted(MoveEvent e, GameType gameType)
    {        
        if (gameType == GameType.GAME)
        {                     
            this.setMoves(statMan.getMoveCount());
            this.setLinesPerMove(statMan.getLinesPerMove());
        }
    }
       
    public void lineConsumed(LineEvent e, GameType gameType)
    {                
        if (gameType == GameType.GAME)
        {            
            this.setLines(statMan.getLineCount());
            this.setLinesPerMove(statMan.getLinesPerMove());
        }
    }       

    public void gameStarted(GameEvent event)
    {
        // Intentionally left blank.
    }

    public void gameReset(GameEvent event)
    {
        setMoves(0);
        setLines(0);
        setLinesPerMove(0.0);
    }

    public void gameOver(GameEvent event)
    {
        // Intentionally left blank.
    }
    
}
