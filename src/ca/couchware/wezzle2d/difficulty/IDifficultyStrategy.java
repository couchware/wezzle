package ca.couchware.wezzle2d.difficulty;

import ca.couchware.wezzle2d.Refactorer.RefactorSpeed;
import ca.couchware.wezzle2d.util.Rational;

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
    public Rational getScoreModifier();
}
