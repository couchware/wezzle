package ca.couchware.wezzle2d.challenge;

import ca.couchware.wezzle2d.Game;

/**
 *
 * @author cdmckay
 */
public class GetXLinesInYMovesChallenge extends ChallengeEntity
{
    /**
     * The total number of lines.
     */
    protected final int totalLines;
    
    /**
     * The current number of lines.
     */
    protected int lines;
    
    /**
     * The total number of moves.
     */
    protected final int totalMoves;
    
    /**
     * The number of moves remaining.
     */
    protected int moves;
    
    /**
     * Used to keep track of moves.
     */
    protected int lastMoveSeen;
    
    /**
     * The reward.
     */
    protected int reward;
    
    /**
     * Creates a challenge that will require the user to
     * get X lines in Y moves.
     * 
     * @param x
     * @param y
     * @param totalLines The X in the description.
     * @param totalMoves The Y in the description.
     */
    public GetXLinesInYMovesChallenge(final int x, final int y,
            final int totalLines, final int totalMoves)
    {
        // Invoke super.
        super(x, y);
        
        // Check values.
        assert(totalLines > 0);
        assert(totalMoves > 0);                
        
        // Set the total values.
        this.totalLines = totalLines;
        this.totalMoves = totalMoves;
        
        // Initialize current values.
        this.lines = 0;
        this.moves = 0;
        
        // Set the last move seen to -1.
        // This is done so that the challenge can calibrate itself on 
        // the first logic update.
        this.lastMoveSeen = -1;
        
        // Determine the reward.
        int linesReward = lines * 100;
        int movesReward = (10 - moves) * 100;
        
        if (movesReward < 0)
            movesReward = 0;
        
        reward = linesReward + movesReward;
        
        // Set the body text.
        bodyLabel1.setText("Get " + totalLines + " lines in");
        bodyLabel2.setText(totalMoves + " moves");          
    }
    
    protected void updateLines(int lineDelta)
    {
        // Add the line increase.
        lines += lineDelta;
        
        // Cap the lines at total lines.
        if (lines > totalLines)
            lines = totalLines;
        
        // Update the label.
        if (lines != 0 || moves != 0)
        {
            bodyLabel1.setText(lines + "/" + totalLines + " lines");
            setDirty(true);
        }        
    }
    
    protected void updateMoves(int moveDelta)
    {
        // Add the move increase.
        moves += moveDelta;
        
        // Cap the moves at the total moves.
        if (moves > totalMoves)
            moves = totalMoves;
        
        // Update the label.
        if (lines != 0 || moves != 0)  
        {
            bodyLabel2.setText((totalMoves - moves) + " moves left");            
            setDirty(true);
        }
    }
    
    protected void updateProgress(int lines, int moves)
    {
        
    }
    
    public void updateLogic(final Game game)
    {
        // See if we've run yet.
        // If we haven't, record the current move (the 0 move) and then
        // return.
        if (lastMoveSeen == -1)        
            lastMoveSeen = game.moveMan.getMoveCount();                
        
        // Determine the move delta.
        int moveDelta = game.moveMan.getMoveCount() - lastMoveSeen;
        lastMoveSeen = game.moveMan.getMoveCount();
        
        // See if there were any lines found.
        updateLines(game.getLineCount());
        updateMoves(moveDelta);
    }
    
}
