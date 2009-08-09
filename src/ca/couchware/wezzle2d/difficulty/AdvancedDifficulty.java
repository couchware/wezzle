/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ca.couchware.wezzle2d.difficulty;

import ca.couchware.wezzle2d.Refactorer.RefactorSpeed;

/**
 *
 * @author kgrad
 */
public class AdvancedDifficulty implements IGameDifficulty
{

    public RefactorSpeed getRefactorSpeed() {
        throw new UnsupportedOperationException("Not supported yet.");
    }


    public int getDropAmount(int numberOfTiles, int numberOfCells, int level,
            int pieceSize)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int getMaxTime() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int determineTimeForLevel(int level) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    

}
