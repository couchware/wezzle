/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ca.couchware.wezzle2d.ui.group;

import ca.couchware.wezzle2d.ui.Label;
import ca.couchware.wezzle2d.*;
import ca.couchware.wezzle2d.ui.button.*;

/**
 *
 * @author cdmckay
 */
public class PauseGroup extends Group
{       
    /**
     * The main label showing the paused text.
     */
    private Label mainLabel;
    
    /**
     * The moves label.
     */
    private Label movesLabel;
    
    /**
     * The lines label.
     */
    private Label linesLabel;
    
    /**
     * The lines per move label.
     */
    private Label linesPerMoveLabel;
    
    /**
     * The constructor.
     * 
     * @param window
     * @param layerMan
     */    
    public PauseGroup(final GameWindow window, 
            final LayerManager layerMan, final GroupManager groupMan)
    {
        // Invoke super.
        super(window, layerMan, groupMan);
               
        // Create the "Paused" text.
        mainLabel = ResourceFactory.get().getLabel(400, 245);        
        mainLabel.setSize(30);
        mainLabel.setAlignment(Label.HCENTER | Label.VCENTER);
        mainLabel.setColor(Game.TEXT_COLOR);
        mainLabel.setText("Paused");
        mainLabel.setVisible(false);
        layerMan.add(mainLabel, Game.LAYER_UI);
        entityList.add(mainLabel);

        movesLabel = ResourceFactory.get().getLabel(400, 310);        
        movesLabel.setSize(18);
        movesLabel.setAlignment(Label.HCENTER | Label.VCENTER);
        movesLabel.setColor(Game.TEXT_COLOR);
        movesLabel.setText("0 moves taken");        
        movesLabel.setVisible(false);
        layerMan.add(movesLabel, Game.LAYER_UI);
        entityList.add(movesLabel);

        linesLabel = ResourceFactory.get().getLabel(400, 340);        
        linesLabel.setSize(18);
        linesLabel.setAlignment(Label.HCENTER | Label.VCENTER);
        linesLabel.setColor(Game.TEXT_COLOR);
        linesLabel.setText("0 lines cleared");
        linesLabel.setVisible(false);
        layerMan.add(linesLabel, Game.LAYER_UI);
        entityList.add(linesLabel);

        linesPerMoveLabel = ResourceFactory.get().getLabel(400, 370);        
        linesPerMoveLabel.setSize(18);
        linesPerMoveLabel.setAlignment(Label.HCENTER | Label.VCENTER);
        linesPerMoveLabel.setColor(Game.TEXT_COLOR);
        linesPerMoveLabel.setText("0.0 lines per move");
        linesPerMoveLabel.setVisible(false);
        layerMan.add(linesPerMoveLabel, Game.LAYER_UI);
        entityList.add(linesPerMoveLabel);
    }
    
    public void setMoves(int moves)
    {
        // Set the moves label.
        if (moves == 1)
            movesLabel.setText("1 move taken");
        else
            movesLabel.setText(moves + " moves taken");
    }
    
    public void setLines(int lines)
    {
        // Set the lines label.
        if (lines == 1)                    
            linesLabel.setText("1 line cleared");
        else
            linesLabel.setText(lines + " lines cleared");       
    }
    
    public void setLinesPerMove(double lpm)
    {
        // Set the lines per move label.
        linesPerMoveLabel.setText(lpm + " lines per move");
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
        
        // Make it so pause button still shows when the rest of the group is
        // not visible.
//        if (visible == true)
//        {
//            pauseButton.setVisible(true);
//            pauseButton.setText("Resume");
//        }
//        else
//        {
//            pauseButton.setVisible(true);            
//            pauseButton.setText("Pause");
//        }
    }
    
    @Override
    public void setActivated(final boolean activated)
    {
        // Invoke super.
        super.setActivated(activated);               
    }        
            
}
