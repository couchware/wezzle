/*
 *  Wezzle
 *  Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d;

import ca.couchware.wezzle2d.animation.IAnimation;
import ca.couchware.wezzle2d.animation.ZoomAnimation;
import ca.couchware.wezzle2d.audio.Sound;
import ca.couchware.wezzle2d.manager.AnimationManager;
import ca.couchware.wezzle2d.manager.BoardManager;
import ca.couchware.wezzle2d.manager.IResettable;
import ca.couchware.wezzle2d.manager.ItemManager;
import ca.couchware.wezzle2d.manager.LogManager;
import ca.couchware.wezzle2d.manager.Settings.Key;
import ca.couchware.wezzle2d.manager.SettingsManager;
import ca.couchware.wezzle2d.tile.TileColor;
import ca.couchware.wezzle2d.tile.Tile;
import ca.couchware.wezzle2d.tile.TileType;
import ca.couchware.wezzle2d.util.Util;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * A class for handling the tile drops that occur after a move.
 * 
 * @author cdmckay
 */
public class TileDropper implements IResettable
{

    /** The only instance of the tile dropper. */
    final private static TileDropper SINGLE = new TileDropper();
    
    /** The percentage of tiles to maintain. */
    final private int TILE_RATIO = 80;
    
    /** The maximum number of tiles to drop in. */
    final private int MAXIMUM_TOTAL_DROP_AMOUNT = 8;      
    
    /** The number of pieces to drop in concurrently. */
    final private int MAXIMUM_PARALLEL_DROP_AMOUNT = 4;
    
    /** The minimum drop. */
    final private int MINIMUM_DROP = 1;           
            
    /** The level at which the difficulty begins to increase. */
    final private int MINIMUM_LEVEL = 3;
    
    /** The number of levels before the difficulty level increases. */
    final private int LEVEL_INTERVAL = 2;
    
    /**
     * Should the piece manager drop automatically drop tiles after a 
     * commit?
     */
    private boolean dropOnCommit;
    
    /** Is the board dropping in a tile. */
    private boolean tileDropping;
    
    /** Is the board animating the tile dropped? */
    private boolean animating;        
    
    /** The number of tiles to drop this turn. */
    private int totalDropAmount = 0;        
              
    /** The tile currently being dropped. */
    private List<Tile> tileDropList = new ArrayList<Tile>();
            
    private TileDropper()
    { resetState(); }               
    
    public void resetState()
    {
        // Reset all the flags.
        this.dropOnCommit = true;
        this.tileDropping = false;
        this.animating = false;
        
        // Set the total drop amount to 0.
        this.totalDropAmount = 0;        
        
        // Clear the tile drop list.
        tileDropList.clear();       
    }
    
    final public static TileDropper get()
    {
        return SINGLE;
    }  
    
    public void updateLogic(Game game)
    {
        // If the board is refactoring, do not logicify.
        if (game.isContextManipulating()
                || game.refactorer.isRefactoring()
                || game.tileRemover.isTileRemoving())
        {
            return;
        }
        
        // Some convenience variables.
        final AnimationManager animationMan = game.animationMan;
        final BoardManager     boardMan     = game.boardMan;
        final ItemManager      itemMan      = game.itemMan;
        final SettingsManager  settingsMan  = game.settingsMan;      
        final Refactorer       refactorer   = game.refactorer;       
                 
        // Which row should we be dropping tiles into?
        int row = boardMan.getGravity().contains(BoardManager.Direction.UP)
                ? boardMan.getRows() - 1
                : 0;       
        
        // Drop in any tiles that need to be dropped, one at a time. 
        //
        // The if statement encompasses the entire function in order to ensure
        // that the board is locked while tiles are dropping.
        if (tileDropping == true)
        {
            // Is the tile dropped currently being animated?
            // If not, that means we need to drop a new one.            
            if (animating == false)
            {                      
                // Get the number of parallel tiles to drop this turn.
                int parallelDropAmount = MAXIMUM_PARALLEL_DROP_AMOUNT;
                
                // Adjust for the pieces left to drop in.
                parallelDropAmount = Math.min(parallelDropAmount, totalDropAmount);
                
                // Count the open columns and build a list of all open indices.
                LinkedList<Integer> openIndexList = new LinkedList<Integer>();               
                
                for (int i = 0; i < boardMan.getColumns(); i++)
                {
                    if (boardMan.getTile(i, row) == null)
                    {                       
                        openIndexList.add(i);
                    }
                }                         
                              
                // Automatically adjust the number of pieces to fall in based
                // on the number of open columns.
                parallelDropAmount = Math.min(parallelDropAmount, openIndexList.size());               
                
                // Create a queue holding all the open columns in a randomized
                // order.                
                Collections.shuffle(openIndexList, Util.random);
                              
                // The current number of items and multipliers.
                int items          = boardMan.getNumberOfItems();
                int multipliers    = boardMan.getNumberOfMultipliers();
                int maxItems       = itemMan.getMaximumItems();
                int maxMultipliers = itemMan.getMaximumMultipliers();
                
                // If there are less items left (to ensure only 1 drop per drop 
                // in) and we have less than the max number of items...
                // drop an item in. Otherwise drop a normal.
                if (openIndexList.size() == 0 && totalDropAmount > 0)                
                {                    
                    // The tile drop is no longer in progress.
                    tileDropping = false;
                    
                    // Start the game over routine.
                    game.startGameOver();
                }
                else if (
                          totalDropAmount <= openIndexList.size()
                       && totalDropAmount <= MAXIMUM_PARALLEL_DROP_AMOUNT
                       && (items < maxItems || multipliers < maxMultipliers))                            
                {
                    //LogManager.recordWarning("B, totalDropAmount = " + totalDropAmount);                    
                    
                    // This must be true.
                    assert totalDropAmount <= openIndexList.size();
                    
                    // Drop in the first amount, minus 1 for the item.
                    for (int i = 0; i < totalDropAmount - 1; i++)
                    {
                        int randomIndex = openIndexList.remove();
                        //initialIndexList.add(randomIndex);
                        
                        tileDropList.add(boardMan.createTile(
                                randomIndex, 
                                row, 
                                TileType.NORMAL)); 
                    }
                    
                    TileType type = itemMan.getItem(items, multipliers).getTileType();
                    
                    // Drop in the item tile.
                    int randomIndex = openIndexList.remove();
                    //initialIndexList.add(randomIndex);
                    
                    tileDropList.add(boardMan.createTile(randomIndex, row, type));                     
                }
                else
                {
                    //LogManager.recordWarning("C, totalDropAmount = " + totalDropAmount);
                    
                    // They are all normals.
                    for (int i = 0; i < parallelDropAmount; i++)
                    {
                        int randomIndex = openIndexList.remove();                        
                        tileDropList.add(boardMan.createTile(randomIndex, row, TileType.NORMAL));
                    }                 
                }                                
                
                // At this point, the tile drop list is built.
                // simulate the drops and change colours if necessary.
                // The point of this is to ensure that no lines come from the dropped in tiles.
                // The formula is as follows: instant refactor the board without removing lines.
                // find all the xmatches. change the colors of the drop in tiles until 
                // there are no more x matches. do the same for the y matches.
                // delete the tiles from the board. Add the new tiles back in the
                // original positions with the new colours. Continue.                          
                List<Integer> initialIndexList = new ArrayList<Integer>();
                
                // Get the initial indices.
                for (Tile t : tileDropList)
                {
                    initialIndexList.add(boardMan.getIndex(t));
                }

                // Refactor the board.
                boardMan.instantRefactorBoard();

                // Check for lines and switch the colors of the appropriate tiles.
                while (true)
                {
                    // The number of found matches. Also puts the matches into match set.
                    Set<Integer> set = new HashSet<Integer>();
                    Set<Integer> matchSet = new HashSet<Integer>();
                    boardMan.findXMatch(set, null);
                    boardMan.findYMatch(matchSet, null);
                    matchSet.addAll(set);
                   
                    Set<Tile> tileMatchSet = new HashSet<Tile>();
                    for (Integer i : matchSet)
                    {
                        tileMatchSet.add(boardMan.getTile(i));
                    }
                    
                    // Intersect the tile match set with the tile drop list.
                    tileMatchSet.retainAll(tileDropList);

                    // We found lines, change the color of the appropriate tiles.
                    if (tileMatchSet.isEmpty() == true)
                    {
                        break;
                    }
                    else
                    {
                        for (Tile matchedTile : tileMatchSet)
                        {
                            TileColor oldColor = matchedTile.getColor();                         
                            TileColor newColor = TileColor.getRandomColor(
                                    boardMan.getNumberOfColors(),
                                    EnumSet.of(oldColor));
                                
                            int index = tileDropList.indexOf(matchedTile);
                            assert index != -1;
                            Tile newTile = boardMan.replaceTile(boardMan.getIndex(matchedTile), newColor);
                            tileDropList.set(index, newTile);
                        }                                               
                    } // end if
                } // end while                                                
                
                // Remove all the old tiles. this will prevent tiles from
                // potentially blocking the new tiles.
                for (Tile t : tileDropList)
                {                    
                    //LogManager.recordMessage("" + tileDropList);

                    // Remove the old tile.
                    boardMan.removeTile(t);
                    
                    //LogManager.recordMessage("Removed tile " + (count + 1) + "/" + tileDropList.size());
                }

                // Create a new tile drop list from the old one.
                List<Tile> newTileDropList = new ArrayList<Tile>();
                
                // Add the new tiles in the initial indexes.
                int count = 0;
                for (Tile t : tileDropList)
                {
                    // This gives the drop a small chance of getting a line.
                    TileColor color;
                    if (count == 0) color = TileColor.getRandomColor(boardMan.getNumberOfColors());
                    else color = t.getColor();
                    
                    // Add the new.
                    newTileDropList.add(
                            boardMan.createTile(
                            initialIndexList.get(count),
                            t.getType(),
                            color));                                        
                    count++;
                }

                tileDropList.clear();
                tileDropList.addAll(newTileDropList);
                    
                // See if the tile drop is still in progress.  If it's not
                // (which would be in the case of a game over), don't play
                // the sound or set the animation flag.
                if (tileDropping == true)
                {        
                    // Start the animation.
                    game.soundMan.play(Sound.BLEEP);
                    
                    for (Tile tile : tileDropList)
                    {
                        if (tile == null)
                        {
                            throw new NullPointerException(
                                    "A tile was null in the tile drop list.");
                        }
                        else
                        {                                                                   
                            IAnimation a = new ZoomAnimation.Builder(ZoomAnimation.Type.OUT, tile)
                                    .speed(settingsMan.getInt(Key.ANIMATION_DROP_ZOOM_OUT_SPEED))
                                    .end();
                            
                            tile.setAnimation(a);
                            animationMan.add(a);
                        }
                    }
                 
                    // Set the animation flag.
                    animating = true;
                }                                                     
            } 
            // If we got here, the animating is in progress, so we need to check
            // if it's done.  If it is, de-reference it and refactor.
            else if (areAllAnimationsDone(tileDropList))
            {
                // Clear the flag.
                animating = false;
                               
                // Run refactor.
                refactorer.startRefactor();
                
                // Remove the amount just removed from the total.
                totalDropAmount -= tileDropList.size();
                
                // De-reference the tile dropped.
                tileDropList.clear();
                
                // Check to see if we have more tiles to drop. 
                // If not, stop tile dropping.
                if (totalDropAmount == 0)  
                {
                    tileDropping = false;
                }                
                // Defensive.
                else if (totalDropAmount < 0)
                {
                    throw new IllegalStateException("Tile drop count is: "
                            + totalDropAmount);
                }                
            } // end if
        }
    }
    
    /**
     * Calculates the number of tiles to drop.
     * 
     * @param game The game.
     * @param pieceSize The size of the piece consumed.
     * @return The number of tiles do drop.
     */
    public void updateDropAmount(final Game game, int pieceSize)
    {
        int numberOfTiles = game.boardMan.getNumberOfTiles();
        int numberOfCells = game.boardMan.getCells();
        int level = game.levelMan.getLevel();
        
        // The number of tiles for the current level.
        int levelDrop = (level / this.LEVEL_INTERVAL);
        
        // Check for difficulty ramp up.
        if (level > this.MINIMUM_LEVEL)
        {
            levelDrop = (this.MINIMUM_LEVEL / this.LEVEL_INTERVAL);
        }
        
        // The percent of the board to readd.
        int  boardPercentage = (int) ((numberOfCells - numberOfTiles) * 0.1f); 
        
        // The drop amount.
        int dropAmount = -1;
        
        // We are low. drop in a percentage of the tiles, increasing if there 
        // are fewer tiles.
        if ((numberOfTiles / numberOfCells) * 100 < this.TILE_RATIO)
        {
            // If we are past the level ramp up point, drop in more.
            if (level > this.MINIMUM_LEVEL)
            {
                dropAmount = pieceSize + levelDrop 
                        + (level - this.MINIMUM_LEVEL) 
                        + boardPercentage + this.MINIMUM_DROP;

                if (dropAmount > this.MAXIMUM_TOTAL_DROP_AMOUNT + pieceSize)
                {
                    dropAmount = this.MAXIMUM_TOTAL_DROP_AMOUNT + pieceSize;
                }         
            }
            else
            {
                dropAmount = pieceSize + levelDrop + boardPercentage 
                        + this.MINIMUM_DROP;
                
                if (dropAmount > this.MAXIMUM_TOTAL_DROP_AMOUNT + pieceSize)
                {
                    dropAmount = this.MAXIMUM_TOTAL_DROP_AMOUNT + pieceSize;
                }
            }
        }
        else
        {
            // If we are past the level ramp up point, drop in more.
            if (level > this.MINIMUM_LEVEL)
            {
                dropAmount = pieceSize + levelDrop 
                        + (level - this.MINIMUM_LEVEL) 
                        + this.MINIMUM_DROP;

                if (dropAmount > this.MAXIMUM_TOTAL_DROP_AMOUNT + pieceSize)
                {
                    dropAmount = this.MAXIMUM_TOTAL_DROP_AMOUNT + pieceSize;
                }
            }
            else
            {
                dropAmount = pieceSize + levelDrop + this.MINIMUM_DROP;

                if (dropAmount > this.MAXIMUM_TOTAL_DROP_AMOUNT + pieceSize)
                {
                    dropAmount = this.MAXIMUM_TOTAL_DROP_AMOUNT + pieceSize;
                }
            }
        } // end if
        
        // See if the drop amount is -1.  If it is, then something broke.
        if (dropAmount == -1)
        {
            throw new IllegalStateException("The drop amount was not set properly.");
        }
        
        // Otherwise, update the total drop amount.
        this.totalDropAmount = dropAmount;
    }
    
    /**
     * Check if an array of animations is done.
     * @param tiles
     * @return
     */
    private boolean areAllAnimationsDone(List<Tile> tileList)
    {   
        // Cycle through all the tiles, looking for one that is not done
        // so we can short circuit it.
        for (Tile tile : tileList)
        {            
            if (tile != null && tile.getAnimation().isFinished() == false)
            {
                return false;  
            }
        } // end for
                        
        return true;
    }  
       
    public void startDrop()
    {
        this.tileDropping = true;
    }
      
    public boolean isDropOnCommit()
    {
        return dropOnCommit;
    }
        
    public void setDropOnCommit(boolean val)
    {
        this.dropOnCommit = val;
    }
    
    public boolean isTileDropping()
    {
        return tileDropping;
    }              
    
}
