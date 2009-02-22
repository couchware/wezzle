
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ca.couchware.wezzle2d.ui.group;

import ca.couchware.wezzle2d.Game;
import ca.couchware.wezzle2d.ManagerHub;
import ca.couchware.wezzle2d.manager.LayerManager;
import ca.couchware.wezzle2d.manager.LayerManager.Layer;
import ca.couchware.wezzle2d.ResourceFactory.LabelBuilder;
import ca.couchware.wezzle2d.event.GameEvent;
import ca.couchware.wezzle2d.event.IGameListener;
import ca.couchware.wezzle2d.event.ILineListener;
import ca.couchware.wezzle2d.event.IMoveListener;
import ca.couchware.wezzle2d.event.LineEvent;
import ca.couchware.wezzle2d.event.MoveEvent;
import ca.couchware.wezzle2d.graphics.IEntity;
import ca.couchware.wezzle2d.ui.ITextLabel;
import ca.couchware.wezzle2d.manager.ListenerManager.GameType;
import ca.couchware.wezzle2d.manager.Settings.Key;
import ca.couchware.wezzle2d.manager.StatManager;
import ca.couchware.wezzle2d.util.NumUtil;
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
    
    /** A reference to the manager hub. */
    final private ManagerHub hub;
       
    /** The main label showing the paused text. */
    private ITextLabel mainLabel;
    
    /** The moves label. */
    private ITextLabel movesLabel;
    
    /** The lines label. */
    private ITextLabel linesLabel;
    
    /** The lines per move label. */
    private ITextLabel linesPerMoveLabel;
    
    private int    moves = 0;
    private int    lines = 0;
    private double linesPerMove = 0.0;
       
    /**
     * The constructor.    
     */    
    public PauseGroup(ManagerHub hub)
    {
        // Sanity check and assignment.
        if(hub == null)
            throw new IllegalArgumentException("hub must not be null.");
        this.hub = hub;
               
        // Create the "Paused" text.
        mainLabel = new LabelBuilder(400, 245)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                .color(hub.settingsMan.getColor(Key.GAME_COLOR_PRIMARY)).size(38)
                .text("Paused")
                .visible(false).end();       
        entityList.add(mainLabel);

        movesLabel = new LabelBuilder(400, 310)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                .color(hub.settingsMan.getColor(Key.GAME_COLOR_PRIMARY)).size(18)
                .text("0 moves")
                .visible(false).end();        
        entityList.add(movesLabel);

        linesLabel = new LabelBuilder(400, 340)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                .color(hub.settingsMan.getColor(Key.GAME_COLOR_PRIMARY)).size(18)
                .text("0 lines")
                .visible(false).end();        
        entityList.add(linesLabel);

        linesPerMoveLabel = new LabelBuilder(400, 370)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))                
                .color(hub.settingsMan.getColor(Key.GAME_COLOR_PRIMARY)).size(18)
                .text("0.0 lines/move")
                .visible(false).end();        
        entityList.add(linesPerMoveLabel);

        // Add all entities to the layer manager.
        for (IEntity entity : entityList)
        {
            hub.layerMan.add(entity, Layer.UI);
        }
    }
        
    private void setMoves(int moves)
    {
        if (this.moves == moves) return;
        
        // Record the current number of moves.
        this.moves = moves;
        
        // Set the moves label.       
        if (moves == 1) movesLabel.setText("1 move");           
        else movesLabel.setText(moves + " moves");
    }       
    
    private void setLines(int lines)
    {
        if (this.lines == lines) return;
        
        // Record the current number of moves.
        this.lines = lines;
        
        // Set the lines label.        
        if (lines == 1) linesLabel.setText("1 line");     
        else linesLabel.setText(lines + " lines");     
    }
    
    private void setLinesPerMove(double linesPerMove)
    {
        if (NumUtil.equalsDouble(this.linesPerMove, linesPerMove)) return;
        
        // Record the current lpm.
        this.linesPerMove = linesPerMove;
        
        // Set the lines per move label.        
        linesPerMoveLabel.setText(linesPerMove + " lines/move");        
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
    
    public void updateLogic(Game game, ManagerHub hub)
    {
        // Intentionally left blank.
    }
            
    public void moveCommitted(MoveEvent e, GameType gameType)
    {        
        if (gameType == GameType.GAME)
        {                     
            this.setMoves(hub.statMan.getMoveCount());
            this.setLinesPerMove(hub.statMan.getLinesPerMove());
        }
    }
    
    public void moveCompleted(MoveEvent event)
    {
        // Don't need this.
    }
       
    public void lineConsumed(LineEvent e, GameType gameType)
    {                
        if (gameType == GameType.GAME)
        {            
            this.setLines(hub.statMan.getLineCount());
            this.setLinesPerMove(hub.statMan.getLinesPerMove());
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
