package ca.couchware.wezzle2d.piece;

import ca.couchware.wezzle2d.util.ArrayUtil;

/**
 * A piece class representing the Line piece.
 * @author cdmckay
 *
 */
public class PieceLine extends Piece
{

    public PieceLine()
    {
        super("Line", PieceType.LINE, 3);

        // Set the name.
        this.structure = new Boolean[][]
                {
                    {
                        false, false, false
                    },
                    {
                        true, true, true
                    },
                    {
                        false, false, false
                    }
                };

        // Set the dimensions.
        this.columns = 1;
        this.rows = 3;
    }

    @Override
    public void rotateRight()
    {
        // Transpose the structure.
        ArrayUtil.transpose2d(structure);

        // Update the dimensions.
        int swap = this.columns;
        this.columns = this.rows;
        this.rows = swap;
    }

    @Override
    public void rotateLeft()
    {
        this.rotateRight();
    }
}
