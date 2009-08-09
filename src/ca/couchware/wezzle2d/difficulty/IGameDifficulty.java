

package ca.couchware.wezzle2d.difficulty;

import ca.couchware.wezzle2d.Refactorer.RefactorSpeed;

/**
 * Public interface for difficulty objects.
 * @author kgrad
 */
public interface IGameDifficulty
{
    public RefactorSpeed getRefactorSpeed();
    public int getTimeUpper();
    public int getTimeLower();
    public int getDropAmount(int numberOfTiles, int numberOfCells, int level,
            int pieceSize);
}
