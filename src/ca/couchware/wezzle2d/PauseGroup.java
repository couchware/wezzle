/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ca.couchware.wezzle2d;

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
    public PauseGroup(final GameWindow window, final LayerManager layerMan)
    {
        // Invoke super.
        super(window, layerMan);
        
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
            
}
