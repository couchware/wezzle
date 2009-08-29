package ca.couchware.wezzle2d.difficulty;

import ca.couchware.wezzle2d.Refactorer.RefactorSpeed;

/**
 * Public interface for difficulty objects.
 * @author kgrad
 */
public interface IDifficultyStrategy
{
    public RefactorSpeed getRefactorSpeed();
    public int getMaxTime();
    public int determineTimeForLevel(int level);
    public int getDropAmount(int numberOfTiles, int numberOfCells, int level,
            int pieceSize);
}
