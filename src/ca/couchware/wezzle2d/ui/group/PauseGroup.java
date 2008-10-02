
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
import java.util.EnumSet;

/**
 *
 * @author cdmckay
 */
public class PauseGroup extends AbstractGroup implements IMoveListener, ILineListener
{      
    
    /**
     * A reference to the layer manager.  This is used by groups to add
     * and remove things like buttons and sliders.
     */
    final protected LayerManager layerMan;       
    
    /**
     * The main label showing the paused text.
     */
    private ILabel mainLabel;
    
    /**
     * The moves label.
     */
    private ILabel movesLabel;
    
    /**
     * The lines label.
     */
    private ILabel linesLabel;
    
    /**
     * The lines per move label.
     */
    private ILabel linesPerMoveLabel;
    
    /**
     * The running line count.
     */
    
    private int lines;
    
    /**
     * The running move count.
     */
    
    private int moves;
    
    /**
     * The constructor.    
     */    
    public PauseGroup(final LayerManager layerMan)
    {
        // Set the layer manager.
        this.layerMan = layerMan;
               
        // Create the "Paused" text.
        mainLabel = new LabelBuilder(400, 245)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                .color(Game.TEXT_COLOR1).size(30).text("Paused")
                .visible(false).end();
        layerMan.add(mainLabel, Layer.UI);
        entityList.add(mainLabel);

        movesLabel = new LabelBuilder(400, 310)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                .color(Game.TEXT_COLOR1).size(18).text("0 moves taken")
                .visible(false).end();
        layerMan.add(movesLabel, Layer.UI);
        entityList.add(movesLabel);

        linesLabel = new LabelBuilder(400, 340)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                .color(Game.TEXT_COLOR1).size(18).text("0 lines cleared")
                .visible(false).end();
        layerMan.add(linesLabel, Layer.UI);
        entityList.add(linesLabel);

        linesPerMoveLabel = new LabelBuilder(400, 370)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))                
                .color(Game.TEXT_COLOR1).size(18).text("0.0 lines per move")
                .visible(false).end();
        layerMan.add(linesPerMoveLabel, Layer.UI);
        entityList.add(linesPerMoveLabel);
    }
    
    public void setMoves(int moves)
    {
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
    
    public void setLines(int lines)
    {
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
    
    public void setLinesPerMove(double lpm)
    {
        // Set the lines per move label.
        //linesPerMoveLabel.setText(lpm + " lines per move");
        layerMan.remove(linesPerMoveLabel, Layer.UI);
        entityList.remove(linesPerMoveLabel);
        linesPerMoveLabel = new LabelBuilder(linesPerMoveLabel)
                .text(lpm + " lines per move").end();
        layerMan.add(linesPerMoveLabel, Layer.UI);
        entityList.add(linesPerMoveLabel);
    }      
    
    
      @Override
    public void handleMoveEvent(MoveEvent e, IListenerComponent.GameType gameType)
    {
        if (gameType == IListenerComponent.GameType.GAME)
        {
            this.moves += e.getMoveCount();
            this.setMoves(moves);
        }
    }
        
    @Override
    public void handleLineEvent(LineEvent e, IListenerComponent.GameType gameType)
    {
        if (gameType == IListenerComponent.GameType.GAME)
        {
            this.lines += e.getLineCount();
            this.setLines(lines);
        }
    }
    
    @Override
    public void setVisible(final boolean visible)
    {
        // This is more important than you think.  Basically, since we might
        // be adding or removing listeners, we want to make sure we only add
        // a listener once, and that we only remove it once.  This ensures that.
        if (isVisible() == visible)
            return;            
        
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
    
}
