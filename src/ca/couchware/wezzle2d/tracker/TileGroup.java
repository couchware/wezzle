/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ca.couchware.wezzle2d.tracker;


import ca.couchware.wezzle2d.tile.Tile;
import java.util.Set;

/**
 * A tile group is a collection of tiles that the player has removed.
 * @author Cameron McKay
 */
public interface TileGroup
{
    Set<Tile> getTiles();
}
