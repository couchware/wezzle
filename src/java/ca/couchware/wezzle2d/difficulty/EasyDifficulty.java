package ca.couchware.wezzle2d.difficulty;

import ca.couchware.wezzle2d.Refactorer.RefactorSpeed;
import ca.couchware.wezzle2d.util.Rational;

/**
 *
 * @author kgrad
 */
public class EasyDifficulty implements IDifficultyStrategy
{
    /** The score modifier. */
    final private Rational scoreModifier = new Rational(1, 1);

    /** The minimum drop. */
    final private int minimumDrop = 1;

    /** The level at which the difficulty begins to increase. */
    final private int minimumLevel = 3;

    /** The number of levels before the difficulty level increases. */
    final private int levelInterval = 2;

    /** The percentage of tiles to maintain. */
    final private int tileRatio = 80;

    /** The maximum number of tiles to drop in. */
    final private int maximumTotalDropAmount = 8;

    /**
     * The timer upper bound, in ms.
     */
    final private int timeUpper = 10000;

    /**
     * The timer lower bound, im ms.
     */
    final private int timeLower = 1000;

    /**
     * Package private constructor.
     */
    EasyDifficulty() { };

    public RefactorSpeed getRefactorSpeed()
    {
       return RefactorSpeed.NORMAL;
    }

    public int getDropAmount(int numberOfTiles, int numberOfCells, int level,
            int pieceSize)
    {

        // The number of tiles for the current level.
        int levelDrop = (level / this.levelInterval);

        // Check for difficulty ramp up.
        if (level > this.minimumLevel)
        {
            levelDrop = (this.minimumLevel / this.levelInterval);
        }

        // The percent of the board to readd.
        int boardPercentage = (numberOfCells - numberOfTiles) / 10;

        // The drop amount.
        int dropAmount = -1;

        // We are low. drop in a percentage of the tiles, increasing if there
        // are fewer tiles.
        if ((numberOfTiles / numberOfCells) * 100 < this.tileRatio)
        {
            // If we are past the level ramp up point, drop in more.
            if (level > this.minimumLevel)
            {
                dropAmount = pieceSize + levelDrop
                        + (level - this.minimumLevel)
                        + boardPercentage + this.minimumDrop;

                if (dropAmount > this.maximumTotalDropAmount + pieceSize)
                {
                    dropAmount = this.maximumTotalDropAmount + pieceSize;
                }
            }
            else
            {
                dropAmount = pieceSize + levelDrop + boardPercentage
                        + this.minimumDrop;

                if (dropAmount > this.maximumTotalDropAmount + pieceSize)
                {
                    dropAmount = this.maximumTotalDropAmount + pieceSize;
                }
            }
        }
        else
        {
            // If we are past the level ramp up point, drop in more.
            if (level > this.minimumLevel)
            {
                dropAmount = pieceSize + levelDrop
                        + (level - this.minimumLevel)
                        + this.minimumDrop;

                if (dropAmount > this.maximumTotalDropAmount + pieceSize)
                {
                    dropAmount = this.maximumTotalDropAmount + pieceSize;
                }
            }
            else
            {
                dropAmount = pieceSize + levelDrop + this.minimumDrop;

                if (dropAmount > this.maximumTotalDropAmount + pieceSize)
                {
                    dropAmount = this.maximumTotalDropAmount + pieceSize;
                }
            }
        } // end if

        // See if the drop amount is -1.  If it is, then something broke.
        if (dropAmount == -1)
        {
            throw new IllegalStateException("The drop amount was not set properly");
        }

        // Otherwise, update the total drop amount.
        return dropAmount;

    }

    public int getMaxTime()
    {
        return this.timeUpper;
    }

    /**
     * Determine the time limit for the given level.
     * @param level
     * @return
     */
    public int determineTimeForLevel(int level)
    {
        return Math.max(timeUpper - (level/2) * 1000, timeLower);
    }

    public Rational getScoreModifier()
    {
        return scoreModifier;
    }
}
