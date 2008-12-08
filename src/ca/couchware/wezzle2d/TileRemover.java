/*
 *  Wezzle
 *  Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d;

import ca.couchware.wezzle2d.Refactorer.RefactorSpeed;
import ca.couchware.wezzle2d.ResourceFactory.LabelBuilder;
import ca.couchware.wezzle2d.animation.FadeAnimation;
import ca.couchware.wezzle2d.animation.FinishedAnimation;
import ca.couchware.wezzle2d.manager.ListenerManager.GameType;
import ca.couchware.wezzle2d.animation.IAnimation;
import ca.couchware.wezzle2d.animation.MetaAnimation;
import ca.couchware.wezzle2d.animation.MetaAnimation.FinishRule;
import ca.couchware.wezzle2d.animation.MetaAnimation.RunRule;
import ca.couchware.wezzle2d.animation.MoveAnimation;
import ca.couchware.wezzle2d.animation.ZoomAnimation;
import ca.couchware.wezzle2d.audio.Sound;
import ca.couchware.wezzle2d.event.CollisionEvent;
import ca.couchware.wezzle2d.event.LineEvent;
import ca.couchware.wezzle2d.event.MoveEvent;
import ca.couchware.wezzle2d.graphics.EntityGroup;
import ca.couchware.wezzle2d.graphics.GraphicEntity;
import ca.couchware.wezzle2d.graphics.IPositionable.Alignment;
import ca.couchware.wezzle2d.manager.*;
import ca.couchware.wezzle2d.manager.BoardManager.Direction;
import ca.couchware.wezzle2d.manager.LayerManager.Layer;
import ca.couchware.wezzle2d.manager.ScoreManager.ScoreType;
import ca.couchware.wezzle2d.manager.Settings.Key;
import ca.couchware.wezzle2d.ui.ILabel;
import ca.couchware.wezzle2d.util.ImmutablePosition;
import ca.couchware.wezzle2d.tile.RocketTileEntity;
import ca.couchware.wezzle2d.tile.StarTileEntity;
import ca.couchware.wezzle2d.tile.TileColor;
import ca.couchware.wezzle2d.tile.TileEntity;
import ca.couchware.wezzle2d.tile.TileType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A class for handling tile removing from the board.  This is a singleton class.
 * 
 * @author kgrad
 */
public class TileRemover
{

    /** The only instance of this class to ever exist. */
    private static final TileRemover single = new TileRemover();
    
    /** If true, a line removal will be activated next loop. */    
    private boolean activateLineRemoval = false;
    
    /**
     * The last line that was matched.  This is used by the "star" item
     * to determine whether a bomb is "in the wild" (i.e. should be removed)
     * or part of the line (i.e. should be left to explode).
     */
    private Set<Integer> lastMatchSet;
    
    /** The item set map.  Contains all the item sets. */
    private Map<TileType, Set<Integer>> itemSetMap;
    
    /** The last refactor speed used. */
    private RefactorSpeed refactorSpeed;
    
    /** If true, level up. */
    private boolean activateLevelUp = false;
    
    /** If true, a line removal is in progress. */
    private boolean tileRemovalInProgress = false;     
    
    /** The set of tile indices that will be removed. */
    private Set<Integer> tileRemovalSet;
    
    /** If true, uses jump animation instead of zoom. */
    private boolean levelUpInProgress = false;
    
    /** If true, award no points for this tile removal. */
    private boolean noScore = false;
    
    /** If true, do not activate items on this removal. */
    private boolean noItems = false;
    
    /** If true, a wezzle tile infection is in progress. */
    private boolean tileInfectionInProgress = false;
    
    /** The tile infection animation. */
    private IAnimation tileInfectionAnimation = FinishedAnimation.get();
        
    /** If true, a bomb removal will be activated next loop. */
    private boolean activateBombRemoval = false;    
    
    /** If true, a star removal will be activated next loop. */
    private boolean activateStarRemoval = false;   
    
    /** If true, a rocket removal will be activated next loop. */
    private boolean activateRocketRemoval = false;    
      
    /**
     * The private constructor.
     */
    private TileRemover()
    { 
        // Initialize the sets.
        this.tileRemovalSet = new HashSet<Integer>();
        this.lastMatchSet   = new HashSet<Integer>();
        
        // Create the item set map.
        this.itemSetMap = new EnumMap<TileType, Set<Integer>>(TileType.class);
        
        // Create a set for each item tile type.
        EnumSet<TileType> tileTypeSet = EnumSet.allOf(TileType.class);        
        tileTypeSet.remove(TileType.NORMAL);
        tileTypeSet.remove(TileType.X2);
        tileTypeSet.remove(TileType.X3);
        tileTypeSet.remove(TileType.X4);
        tileTypeSet.remove(TileType.WEZZLE);
        
        // Now create all the item sets.
        for (TileType t : tileTypeSet)
            itemSetMap.put(t, new HashSet<Integer>());        
    }      

    /**
     * Retrieve the single instance of this class.
     * 
     * @return The single instance of this class.
     */
    static TileRemover get()
    {
        return single;
    }

    public boolean isTileRemovalInProgress()
    {
        return tileRemovalInProgress;
    }

    /**
     * The main chunk of the remover. This is where the logic occurs.
     * 
     * @param game The game instance.
     */
    public void updateLogic(final Game game)
    {
        final AnimationManager animationMan = game.animationMan;
        final BoardManager boardMan         = game.boardMan;
        final LayerManager layerMan         = game.layerMan;
        final ListenerManager listenerMan   = game.listenerMan;
        final PieceManager pieceMan         = game.pieceMan;
        final SettingsManager settingsMan   = game.settingsMan;
        final TimerManager timerMan         = game.timerMan;
        final WorldManager worldMan         = game.worldMan;
        
        if (activateLevelUp == true)
        {
            activateLevelUp = false;
            levelUp(game);
        }

        // See if it just finished.
        if (game.refactorer.isFinished() == true && areItemSetsEmpty() == true)
        {
            findMatches(game);
            
            // If there are matches, score them, remove 
            // them and then refactor again.
            if (!tileRemovalSet.isEmpty())
            {
                startLineRemoval(game.refactorer);
            }
            else
            {
                // Make sure the tiles are not still dropping.
                if (!pieceMan.isTileDropInProgress())
                {      
                    // Don't fire a move completed event if we're just
                    // doing the level up line removal.
                    if (!levelUpInProgress)
                    {
                        // Fire the move completed event.
                        listenerMan.notifyMoveCompleted(new MoveEvent(this, 1));  
                    }
                    
                    // Clear the level up in progress flag.
                    levelUpInProgress = false;      
                                                                               
                    //See if the wezzle timer has run out.  If it has, start
                    // the tile infection.
                    if (worldMan.getWezzleTime() == 0 && boardMan.getNumberOfTiles() > 0)
                    {                                        
                        // Look for any item that is not a wezzle tile or multiplier.
                        List<Integer> indexList = boardMan.getTileIndices(
                                EnumSet.of(
                                    TileType.ROCKET,
                                    TileType.GRAVITY,
                                    TileType.BOMB,
                                    TileType.STAR
                                ),
                                EnumSet.allOf(TileColor.class));

                        // Look for any item that is not a wezzle tile.
                        if (indexList.isEmpty() == true)
                        {
                            indexList = boardMan.getTileIndices(
                                EnumSet.of(
                                    TileType.X2,
                                    TileType.X3,
                                    TileType.X4
                                ),
                                EnumSet.allOf(TileColor.class));
                        }
                        
                        // Look for any tile that is not a wezzle tile.
                        if (indexList.isEmpty() == true)
                        {                            
                            indexList = boardMan.getTileIndices(
                                    EnumSet.of(TileType.NORMAL),
                                    EnumSet.allOf(TileColor.class));
                        }

                        // Get a random one from that list and replace it.                        
                        Collections.shuffle(indexList);
                        final int index = indexList.get(0);
                        final TileEntity oldTile = boardMan.getTile(index);                       
                        
                        // Create the new wezzle tile.
                        final TileEntity newTile = boardMan.makeTile(
                                TileType.WEZZLE, 
                                oldTile.getColor(), 
                                oldTile.getX(), oldTile.getY());
                        
                        // Start an animation.                        
                        IAnimation wezzle1 = new ZoomAnimation.Builder(ZoomAnimation.Type.IN, oldTile)
                                .speed(settingsMan.getInt(Key.ANIMATION_WEZZLE_ZOOM_IN_SPEED))
                                .end();
                        
                        wezzle1.setFinishHook(new Runnable()
                        {
                            public void run()
                            { boardMan.removeTile(index); }
                        });
                        
                        IAnimation wezzle2 = new ZoomAnimation.Builder(ZoomAnimation.Type.OUT, newTile)                                
                                .speed(settingsMan.getInt(Key.ANIMATION_WEZZLE_ZOOM_OUT_SPEED))
                                .end();
                        
                        // Add tile to layer manager for animation purposes.
                        wezzle2.setStartHook(new Runnable()
                        {
                            public void run()
                            { layerMan.add(newTile, Layer.TILE); }
                        });
                        
                        // Remove tile for layer manager and add it to the 
                        // board manager.
                        wezzle2.setFinishHook(new Runnable()
                        {
                            public void run()
                            { 
                                layerMan.remove(newTile, Layer.TILE); 
                                boardMan.addTile(index, newTile);
                            }
                        });                                                                        
                        
                        // If the color is already locked, no need to fade it.
                        IAnimation fade = FinishedAnimation.get();
                        if (!boardMan.isColorLocked(newTile.getColor()))
                        {
                            EntityGroup e = boardMan.getTiles(
                                boardMan.getTileIndices(
                                    EnumSet.complementOf(EnumSet.of(TileType.WEZZLE)), 
                                    EnumSet.of(newTile.getColor())));                                                
                        
                            fade = new FadeAnimation.Builder(FadeAnimation.Type.OUT, e)
                                    .minOpacity(settingsMan.getInt(Key.ANIMATION_WEZZLE_FADE_MIN_OPACITY))
                                    .wait(settingsMan.getInt(Key.ANIMATION_WEZZLE_FADE_WAIT))
                                    .duration(settingsMan.getInt(Key.ANIMATION_WEZZLE_FADE_DURATION))
                                    .end();
                        } // end if                                                                                             
                        
                        IAnimation wezzleMeta = new MetaAnimation.Builder()
                                .runRule(RunRule.SEQUENCE)
                                .add(wezzle1)
                                .add(wezzle2)
                                .end();
                        
                        IAnimation meta = new MetaAnimation.Builder()
                                .runRule(RunRule.SIMULTANEOUS)
                                .finishRule(FinishRule.ALL)
                                .add(wezzleMeta)
                                .add(fade)
                                .end();
                                                                              
                        // Set the animation.
                        this.tileInfectionAnimation = meta;
                        animationMan.add(meta);
                        
                        // Lock the color.
                        boardMan.setColorLocked(newTile.getColor(), true);
                        
                        // The infection is now in progress.
                        this.tileInfectionInProgress = true;                                                
                    }    
                    else
                    {
                        // Start the next move.
                        startNextMove(pieceMan, timerMan);
                    }
                } // end if                                
            } // end if
        } // end if       
        
        // If there's an infection in progress, check to see if it's done.
        if (this.tileInfectionInProgress == true)       
        {
            if (tileInfectionAnimation.isFinished() == true)
            {
                // Reset the wezzle timer.
                worldMan.resetWezzleTime();

                // Start the next move.
                startNextMove(pieceMan, timerMan);

                // Turn off the infection flag.
                this.tileInfectionInProgress = false;            
            }
        } // end if       

        // If a line removal was activated.
        if (this.activateLineRemoval == true)
        {            
            removeLines(game);
        }                 

        // If the star removal is in progress.
        if (this.activateRocketRemoval == true)
        {
            removeRockets(game);
        }

        // If the star removal is in progress.
        if (this.activateStarRemoval == true)
        {
            removeStars(game);
        }

        // If a bomb removal is in progress.
        if (this.activateBombRemoval == true)
        {
            removeBombs(game);
        }
                       
        // If a line removal is in progress.        
        if (this.tileRemovalInProgress == true)
        {            
            processRemoval(game);
        }
    }
    
    private void startLineRemoval(Refactorer refactorer)
    {
        // Activate the line removal.
        this.activateLineRemoval = true;

        // Record the refactor speed.
        this.refactorSpeed = refactorer.getRefactorSpeed();
    }   
    
    private void startNextMove(PieceManager pieceMan, TimerManager timerMan)
    {
        // Load new piece and make it visible.
        pieceMan.loadPiece();
        pieceMan.getPieceGrid().setVisible(true);               

        // Reset the mouse.
        pieceMan.clearMouseButtonSet();

        // Unpause the timer.
        timerMan.resetTimer();
        timerMan.setPaused(false);
    }
    
    /**
     * Checks the emptiness of all the item sets.
     * 
     * @return
     */
    private boolean areItemSetsEmpty()
    {
        assert this.itemSetMap != null;
                    
        for (TileType t : this.itemSetMap.keySet())
        {
            if (this.itemSetMap.containsKey(t) 
                    && this.itemSetMap.get(t).isEmpty() == false)
            {
                return false;
            }
        }
        
        return true;
    }       

    public boolean isTileRemoving()
    {
        return this.tileRemovalInProgress
                || this.tileInfectionInProgress
                || this.activateLineRemoval 
                || this.activateBombRemoval 
                || this.activateStarRemoval 
                || this.activateRocketRemoval;
    }

    public void levelUp(final Game game)
    {
        // Set some flags for the level up.
        this.activateLineRemoval = true;
        this.levelUpInProgress = true;
        this.noScore = true;
        this.noItems = true;
        
        // Clear the tile removal set.        
        this.tileRemovalSet.clear();

        // Make a shortcut to the board manager.
        BoardManager boardMan = game.boardMan;

        int j;
        if (boardMan.getGravity().contains(Direction.UP))
        {
            j = 0;
        }
        else
        {
            j = boardMan.getRows() - 1;
        }

        for (int i = 0; i < boardMan.getColumns(); i++)
        {
            int index = i + (j * boardMan.getColumns());
            if (boardMan.getTile(index) != null)
            {
                tileRemovalSet.add(new Integer(index));
            }
        }
    }

    public void findMatches(final Game game)
    {
        // Shortcuts to the managers.
        BoardManager boardMan       = game.boardMan;        
        StatManager  statMan        = game.statMan;                       
        
        // Look for matches.
        tileRemovalSet.clear();

        int cycleX = boardMan.findXMatch(tileRemovalSet);
        int cycleY = boardMan.findYMatch(tileRemovalSet);
        
        statMan.incrementCycleLineCount(cycleX);
        statMan.incrementCycleLineCount(cycleY);
        
        //  Handle any lines we may have had.       
        if (game.tutorialMan.isTutorialInProgress() == true)
        {
            game.listenerMan.notifyLineConsumed(new LineEvent(
                    game.statMan.getCycleLineCount(), this),
                    GameType.TUTORIAL);
        }
        else
        {
            game.listenerMan.notifyLineConsumed(new LineEvent(
                    game.statMan.getCycleLineCount(), this),
                    GameType.GAME);
        }                

        // Copy the match into the last line match holder.
        lastMatchSet.clear();
        lastMatchSet.addAll(tileRemovalSet);                
    }

    private IAnimation animateItemActivation(
            final SettingsManager settingsMan,
            final LayerManager layerMan,
            final BoardManager boardMan, 
            final TileEntity tile)
    {
        assert settingsMan != null;
        assert layerMan    != null;
        assert boardMan    != null;
        assert tile        != null;
        
        // The clone of tile, used to make the effect.
        final TileEntity clone = boardMan.cloneTile(tile);
        
        // Add the clone to the layer man.
        layerMan.add(clone, Layer.EFFECT);
        
        // Make the animation.
        IAnimation anim1 = new ZoomAnimation.Builder(ZoomAnimation.Type.OUT, clone)
                .minWidth(clone.getWidth())                                
                .maxWidth(Integer.MAX_VALUE)
                .speed(settingsMan.getInt(Key.ANIMATION_ITEM_ACTIVATE_ZOOM_SPEED))
                .duration(settingsMan.getInt(Key.ANIMATION_ITEM_ACTIVATE_ZOOM_DURATION))
                .end();

        IAnimation anim2 = new FadeAnimation.Builder(FadeAnimation.Type.OUT, clone)
                .wait(settingsMan.getInt(Key.ANIMATION_ITEM_ACTIVATE_FADE_WAIT))
                .duration(settingsMan.getInt(Key.ANIMATION_ITEM_ACTIVATE_FADE_DURATION))
                .end();

        MetaAnimation meta = new MetaAnimation.Builder()
                .add(anim1)
                .add(anim2)
                .end();

        meta.setFinishHook(new Runnable()
        {
           public void run() 
           { layerMan.remove(clone, Layer.EFFECT); }
        });

        clone.setAnimation(meta);
        
        return meta;
    }


    private void followThrough(
            Game game,
            Integer lastItem, 
            List<TileEntity> itemsSeenList,              
            List<TileEntity> allSeenList) 
    {
        // The set to hold the tiles affected by the item.
        Set<Integer> tilesAffected = new HashSet<Integer>();
       
        // Build a set with the current item as its only member.
        Set<Integer> currentItem = new HashSet<Integer>();
        currentItem.add(lastItem);                      
        
        // Determine the type and get the affected tiles accordingly.
        switch (game.boardMan.getTile(lastItem).getType())
        {
            case ROCKET:
                game.boardMan.processRockets(currentItem, tilesAffected);
                break;
            
            case BOMB:
                game.boardMan.processBombs(currentItem, tilesAffected);
                break;
            
            case STAR:
                game.boardMan.processStars(currentItem, tilesAffected);
                break;
            
            default:
                break;               
        }
        
        // Go through the affected tiles set looking for an item.
        for (Integer index : tilesAffected)
        {           
            // Get the tile entity.
            TileEntity t = game.boardMan.getTile(index);
            
            // If we have found another item. Recurse.
            if (t.getType() != TileType.NORMAL)
            {
                // We've found another item. Add it to the list and recurse.
                if (allSeenList.contains(t) == true)
                    continue;
                
                // Add to the list of all things seen.
                allSeenList.add(t);
                
                // Add to the temp list.
                List<TileEntity> list = new ArrayList<TileEntity>(itemsSeenList);
                list.add(t);
                followThrough(game, index, list, allSeenList);
            }
        }
        
        // When we are done.  Return the event.
        if (itemsSeenList.isEmpty() == false)
        {
            game.listenerMan.notifyCollision(new CollisionEvent(this, itemsSeenList));
        }
    }
    
    private void processRemoval(final Game game)
    {
        // Shortcut to board manager.
        BoardManager boardMan = game.boardMan;
        
        // Animation completed flag.
        boolean animationInProgress = false;

        // Check to see if they're all done.
        for (Iterator it = tileRemovalSet.iterator(); it.hasNext();)
        {
            if (boardMan.getTile((Integer) it.next()).getAnimation().isFinished() == false)
            {
                animationInProgress = true;
                break;
            }
        } // end for

        if (animationInProgress == false)
        {
            // Remove the tiles from the board.
            boardMan.removeTiles(tileRemovalSet);

            // Bomb removal is completed.
            this.tileRemovalInProgress = false;

            // See if there are any bombs in the bomb set.
            // If there are, activate the bomb removal.  
            
            if (this.itemSetMap.get(TileType.ROCKET).isEmpty() == false)
            {
                this.activateRocketRemoval = true;
            }
            else if (this.itemSetMap.get(TileType.STAR).isEmpty() == false)
            {
                this.activateStarRemoval = true;
            }
            else if (this.itemSetMap.get(TileType.BOMB).isEmpty() == false)
            {
                this.activateBombRemoval = true;
            }
            else if (this.itemSetMap.get(TileType.GRAVITY).isEmpty() == false)
            {                
                shiftGravity(game); 
                game.refactorer
                        .setRefactorSpeed(RefactorSpeed.SHIFT)
                        .startRefactor();
            }
            // Otherwise, start a new refactor.
            else
            {
                game.refactorer
                        .setRefactorSpeed(refactorSpeed)
                        .startRefactor();
            }
        } // end if
    }
    
    private void scanFor(
            BoardManager boardMan, 
            EnumSet<TileType> tileTypeSet, 
            boolean clear)    
    {                
        for (TileType t : tileTypeSet)
        {
            if (itemSetMap.containsKey(t) == false)
                continue;
            
            Set<Integer> set = this.itemSetMap.get(t);
            
            if (clear == true) set.clear();
            boardMan.scanFor(t, this.tileRemovalSet, set);                       
            
            if (t != TileType.GRAVITY)
                this.tileRemovalSet.removeAll(set);                                    
            
        } // end for                                
    }    
    
    private void scanFor(
        BoardManager boardMan, 
        EnumSet<TileType> tileTypeSet)
    {
        scanFor(boardMan, tileTypeSet, false);
    }

    private void removeLines(final Game game)
    {
        // Shortcuts to managers.
        AnimationManager animationMan = game.animationMan;
        BoardManager boardMan = game.boardMan;
        //ListenerManager listenerMan = game.listenerMan;
        ScoreManager scoreMan = game.scoreMan;
        SettingsManager settingsMan = game.settingsMan;
        SoundManager soundMan = game.soundMan;
        StatManager statMan = game.statMan;                        
        //TutorialManager tutorialMan = game.tutorialMan;                

        // Clear flag.
        activateLineRemoval = false;

        // Increment chain count.
        statMan.incrementChainCount();

        // Calculate score, unless no-score flag is set.
        if (noScore == false)
        {
            final int deltaScore = scoreMan.calculateLineScore(
                    tileRemovalSet,
                    ScoreType.LINE,
                    statMan.getChainCount());

            // Fire a score event.
//            if (tutorialMan.isTutorialInProgress() == true)
//            {
//                listenerMan.notifyScoreChanged(new ScoreEvent(this, deltaScore, -1),
//                        IListenerManager.GameType.TUTORIAL);
//            }
//            else
//            {
//                listenerMan.notifyScoreChanged(new ScoreEvent(this, deltaScore, -1),
//                        IListenerManager.GameType.GAME);
//            }
            
            // Increment the score.
            if (game.tutorialMan.isTutorialInProgress() == false)       
            {
                game.scoreMan.incrementScore(deltaScore);        
            }

            // Show the SCT.
            ImmutablePosition p = boardMan.determineCenterPoint(tileRemovalSet);

            final ILabel label = new LabelBuilder(p.getX(), p.getY())
                    .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                    .color(settingsMan.getColor(Key.SCT_COLOR_LINE)).size(scoreMan.determineFontSize(deltaScore))
                    .text(String.valueOf(deltaScore)).end();
            
            IAnimation a1 = new FadeAnimation.Builder(FadeAnimation.Type.OUT, label)
                .wait(settingsMan.getInt(Key.SCT_SCORE_FADE_WAIT))
                .duration(settingsMan.getInt(Key.SCT_SCORE_FADE_DURATION))
                .minOpacity(settingsMan.getInt(Key.SCT_SCORE_FADE_MIN_OPACITY))
                .maxOpacity(settingsMan.getInt(Key.SCT_SCORE_FADE_MAX_OPACITY))
                .end();
            
            IAnimation a2 = new MoveAnimation.Builder(label)
                .duration(settingsMan.getInt(Key.SCT_SCORE_MOVE_DURATION))
                .speed(settingsMan.getInt(Key.SCT_SCORE_MOVE_SPEED))
                .theta(settingsMan.getInt(Key.SCT_SCORE_MOVE_THETA)).end();

            a2.setStartHook(new Runnable()
            {
                public void run()
                {
                    game.layerMan.add(label, Layer.EFFECT);
                }
            });

            a2.setFinishHook(new Runnable()
            {
                public void run()
                {
                    game.layerMan.remove(label, Layer.EFFECT);
                }
            });

            animationMan.add(a1);
            animationMan.add(a2);
            a1 = null;
            a2 = null;

            // Release references.
            p = null;
        }
        else
        {
            // Turn off the flag now that it has been used.
            noScore = false;
        }

        // Play the sound.
        soundMan.play(Sound.LINE);

        // Make sure bombs aren't removed (they get removed
        // in a different step).  However, if the no-items
        // flag is set, then ignore bombs.
        if (this.noItems == false)
        {
            scanFor(boardMan, EnumSet.allOf(TileType.class), true);
        }
        else
        {
            // Turn off the flag now that it has been used.
            this.noItems = false;
        }

        // Start the line removal animations if there are any
        // non-bomb tiles.
        if (tileRemovalSet.isEmpty() == false)
        {
            int i = 0;
            for (Iterator it = tileRemovalSet.iterator(); it.hasNext();)
            {
                TileEntity t = boardMan.getTile((Integer) it.next());

                if (levelUpInProgress == true)
                {
                    i++;
                    int angle = i % 2 == 0 ? 70 : 180 - 70;

                    // Bring this tile to the top.
                    game.layerMan.toFront(t, Layer.TILE);

                    IAnimation a1 = new MoveAnimation.Builder(t)
                            .duration(settingsMan.getInt(Key.ANIMATION_JUMP_MOVE_DURATION))
                            .theta(angle)
                            .speed(settingsMan.getInt(Key.ANIMATION_JUMP_MOVE_SPEED))
                            .gravity(settingsMan.getInt(Key.ANIMATION_JUMP_MOVE_GRAVITY))
                            .end();
                    
                    IAnimation a2 = new FadeAnimation.Builder(FadeAnimation.Type.OUT, t)
                            .wait(settingsMan.getInt(Key.ANIMATION_JUMP_FADE_WAIT))
                            .duration(settingsMan.getInt(Key.ANIMATION_JUMP_FADE_DURATION))
                            .end();
                    
                    t.setAnimation(a1);
                    
                    animationMan.add(a1);
                    animationMan.add(a2);
                    
                    a1 = null;
                    a2 = null;
                }
                else
                {
                    IAnimation a1 = new ZoomAnimation.Builder(ZoomAnimation.Type.IN, t)
                            .speed(settingsMan.getInt(Key.ANIMATION_LINE_REMOVE_ZOOM_SPEED))
                            .end();
                    
                    IAnimation a2 = new FadeAnimation.Builder(FadeAnimation.Type.OUT, t)
                            .wait(settingsMan.getInt(Key.ANIMATION_LINE_REMOVE_FADE_WAIT))
                            .duration(settingsMan.getInt(Key.ANIMATION_LINE_REMOVE_FADE_DURATION))
                            .end();
                    
                    IAnimation meta = new MetaAnimation.Builder()
                            .finishRule(FinishRule.ALL)
                            .add(a1)
                            .add(a2)
                            .end();
                    
                    t.setAnimation(meta);
                    animationMan.add(t.getAnimation());                                        
                }                                
                
                // Get a set of all the items activated.
                Set<Integer> allItemSet = new HashSet<Integer>();
                for (Set<Integer> itemSet : itemSetMap.values())
                {
                    allItemSet.addAll(itemSet);
                }

                // Animate their activation.
                for (Integer index : allItemSet)
                {
                    // Get the tile and make a copy.
                    final TileEntity tile = boardMan.getTile(index);
                   
                    // Create and add the animation.                    
                    animationMan.add(animateItemActivation(
                            game.settingsMan,
                            game.layerMan,
                            game.boardMan,
                            tile));
                }
            }           
        } // end if    
        
        Set<Integer> gravityRemovalSet = itemSetMap.get(TileType.GRAVITY);
        if (gravityRemovalSet.isEmpty() == false)
        {
            for (Integer i : gravityRemovalSet)
            {
                TileEntity t = boardMan.getTile(i);
                IAnimation a = new FadeAnimation.Builder(FadeAnimation.Type.OUT, t)
                        .duration(50)
                        .end();
                t.setAnimation(a);
                animationMan.add(a);
            }            
        }                    

        // Set the flag.
        tileRemovalInProgress = true;
    }    
        
    private void removeRockets(final Game game)
    {        
        // Shortcuts to managers.
        AnimationManager animationMan = game.animationMan;
        BoardManager boardMan = game.boardMan;
        LayerManager layerMan = game.layerMan;
        //ListenerManager listenerMan = game.listenerMan;
        ScoreManager scoreMan = game.scoreMan;
        SettingsManager settingsMan = game.settingsMan;
        SoundManager soundMan = game.soundMan;
        StatManager statMan = game.statMan;                        
        //TutorialManager tutorialMan = game.tutorialMan;  

        // Shortcut to the set.
        Set<Integer> rocketRemovalSet = this.itemSetMap.get(TileType.ROCKET);
        
        // Clear the flag.
        activateRocketRemoval = false;

        // Increment cascade.
        statMan.incrementChainCount();

        // Used below.
        int deltaScore = 0;

        // Also used below.
        IAnimation a1, a2, a3;
        
        List<TileEntity> allSeenSet = new ArrayList<TileEntity>();
          
        // Load the items into the allseen initially.
        for (Integer index : rocketRemovalSet)
        {            
            TileEntity t = game.boardMan.getTile(index);
            
            if (t.getType() != TileType.NORMAL)
            {
                allSeenSet.add(t);
            }
         }
                
        // Simulate all collisions that these rockets would achieve one at a time.
        for (Integer index : rocketRemovalSet)
        {            
            // Create a set to hold the current item.
            List<TileEntity> itemsSeen = new ArrayList<TileEntity>();
            itemsSeen.add(boardMan.getTile(index));
            
            followThrough(game, index, itemsSeen, allSeenSet);
        }

        // Get the tiles the rockets would affect.
        boardMan.processRockets(rocketRemovalSet, tileRemovalSet);

        for (Integer index : lastMatchSet)
        {
            if (boardMan.getTile(index) == null)
            {
                continue;
            }

            if (boardMan.getTile(index).getClass() != RocketTileEntity.class)
            {
                tileRemovalSet.remove(index);
            }
        } // end for

        deltaScore = scoreMan.calculateLineScore(
                tileRemovalSet,
                ScoreType.STAR,
                statMan.getChainCount());

        // Fire a score event.
//        if (tutorialMan.isTutorialInProgress() == true)
//        {
//            listenerMan.notifyScoreChanged(new ScoreEvent(this, deltaScore, -1),
//                    IListenerManager.GameType.TUTORIAL);
//        }
//        else
//        {
//            listenerMan.notifyScoreChanged(new ScoreEvent(this, deltaScore, -1),
//                    IListenerManager.GameType.GAME);
//        }
        
        // Increment the score.
        if (game.tutorialMan.isTutorialInProgress() == false)       
        {
            game.scoreMan.incrementScore(deltaScore);        
        }

        // Show the SCT.
        ImmutablePosition p = boardMan.determineCenterPoint(tileRemovalSet);

        final ILabel label = new LabelBuilder(p.getX(), p.getY())
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                .color(settingsMan.getColor(Key.SCT_COLOR_ITEM))
                .size(scoreMan.determineFontSize(deltaScore))
                .text(String.valueOf(deltaScore))
                .end();
        
        a1 = new FadeAnimation.Builder(FadeAnimation.Type.OUT, label)
                .wait(settingsMan.getInt(Key.SCT_SCORE_FADE_WAIT))
                .duration(settingsMan.getInt(Key.SCT_SCORE_FADE_DURATION))
                .minOpacity(settingsMan.getInt(Key.SCT_SCORE_FADE_MIN_OPACITY))
                .maxOpacity(settingsMan.getInt(Key.SCT_SCORE_FADE_MAX_OPACITY))
                .end();
        
        a2 = new MoveAnimation.Builder(label)
                .duration(settingsMan.getInt(Key.SCT_SCORE_MOVE_DURATION))
                .speed(settingsMan.getInt(Key.SCT_SCORE_MOVE_SPEED))
                .theta(settingsMan.getInt(Key.SCT_SCORE_MOVE_THETA))
                .end();

        a2.setStartHook(new Runnable()
        {
            public void run()
            {
                game.layerMan.add(label, Layer.EFFECT);
            }
        });

        a2.setFinishHook(new Runnable()
        {
            public void run()
            {
                game.layerMan.remove(label, Layer.EFFECT);
            }
        });

        animationMan.add(a1);
        animationMan.add(a2);
        a1 = null;
        a2 = null;

        // Release references.
        p = null;

        // Play the sound.
        soundMan.play(Sound.ROCKET);
        
        // Find all the new rockets.
        Set<Integer> nextRocketRemovalSet = new HashSet<Integer>();
        boardMan.scanFor(TileType.ROCKET, tileRemovalSet, nextRocketRemovalSet);
        nextRocketRemovalSet.removeAll(rocketRemovalSet);

        // Remove all new rockets from the tile removal set.
        // They will be processed separately.
        tileRemovalSet.removeAll(nextRocketRemovalSet);               

        // Find the rest of the pieces.
        EnumSet<TileType> tileTypeSet = EnumSet.allOf(TileType.class);
        tileTypeSet.remove(TileType.ROCKET);        
        scanFor(boardMan, tileTypeSet);                  
        
        // Now animate the activation of the next removal set.
        Set<Integer> allItemSet = new HashSet<Integer>();
        allItemSet.addAll(nextRocketRemovalSet);
        
        for (TileType t : tileTypeSet)
        {
            if (itemSetMap.containsKey(t))            
                allItemSet.addAll(itemSetMap.get(t));                
        }              
        
        for (Integer i : allItemSet)
        {
            TileEntity t = boardMan.getTile(i);
            
            if (t == null) continue;
            
            t.setAnimation(animateItemActivation(settingsMan, layerMan, boardMan, t));
            animationMan.add(t.getAnimation());
        }

        // Start the line removal animations.
        int i = 0;
        for (Iterator it = tileRemovalSet.iterator(); it.hasNext();)
        {
            TileEntity t = boardMan.getTile((Integer) it.next());

            // Bring the tile to the front.
            game.layerMan.toFront(t, Layer.TILE);

            if (t.getType() == TileType.ROCKET)
            {                
                // Cast it.
                RocketTileEntity r = (RocketTileEntity) t;                                
                
                a1 = new MoveAnimation.Builder(r)
                        .duration(settingsMan.getInt(Key.ANIMATION_ROCKET_MOVE_DURATION))
                        .theta(r.getDirection().toDegrees())
                        .speed(settingsMan.getInt(Key.ANIMATION_ROCKET_MOVE_SPEED))
                        .gravity(settingsMan.getInt(Key.ANIMATION_ROCKET_MOVE_GRAVITY))
                        .end();
                
                a2 = new FadeAnimation.Builder(FadeAnimation.Type.OUT, t)
                        .wait(settingsMan.getInt(Key.ANIMATION_ROCKET_FADE_WAIT))
                        .duration(settingsMan.getInt(Key.ANIMATION_ROCKET_FADE_DURATION))
                        .end();
                                
                MetaAnimation meta = new MetaAnimation.Builder()
                        .finishRule(FinishRule.ALL)
                        .add(a1)
                        .add(a2)                        
                        .end();
                
                t.setAnimation(meta);                
                animationMan.add(meta);                
                
                a1 = null;
                a2 = null;
            }
            else
            {
                i++;
                int angle = i % 2 == 0 ? 70 : 180 - 70;                
               
                a1 = new MoveAnimation.Builder(t)
                        .duration(settingsMan.getInt(Key.ANIMATION_JUMP_MOVE_DURATION))
                        .theta(angle)
                        .speed(settingsMan.getInt(Key.ANIMATION_JUMP_MOVE_SPEED))
                        .gravity(settingsMan.getInt(Key.ANIMATION_JUMP_MOVE_GRAVITY)).end();
                    
                a2 = new FadeAnimation.Builder(FadeAnimation.Type.OUT, t)
                        .wait(settingsMan.getInt(Key.ANIMATION_JUMP_FADE_WAIT))
                        .duration(settingsMan.getInt(Key.ANIMATION_JUMP_FADE_DURATION)).end();
                
                t.setAnimation(a1);
                
                animationMan.add(a1);
                animationMan.add(a2);
                
                a1 = null;
                a2 = null;
            }
        }

        // If other things were were hit, they will be dealt with in another
        // removal cycle.
        this.itemSetMap.put(TileType.ROCKET, nextRocketRemovalSet);

        // Set the flag.
        tileRemovalInProgress = true;
    }
    
    private void removeBombs(final Game game)
    {
        // Create shortcuts to all the managers.
        final AnimationManager animationMan = game.animationMan;
        final BoardManager boardMan = game.boardMan;
        final LayerManager layerMan = game.layerMan;
        //final ListenerManager listenerMan = game.listenerMan;
        final ScoreManager scoreMan = game.scoreMan;
        final SettingsManager settingsMan = game.settingsMan;
        final SoundManager soundMan = game.soundMan;
        final StatManager statMan = game.statMan;                        
        //final TutorialManager tutorialMan = game.tutorialMan;                

        // Shortcut to the set.
        Set<Integer> bombRemovalSet = this.itemSetMap.get(TileType.BOMB);
        
        // Clear the flag.
        activateBombRemoval = false;

        // Increment cascade.
        statMan.incrementChainCount();

        // Used below.
        int deltaScore = 0;

        // Also used below.
        IAnimation a1, a2, a3;

        List<TileEntity> allSeen = new ArrayList<TileEntity>();
          
        // Load the items into the allseen initially.
        for (Integer index : bombRemovalSet)
        {          
            TileEntity t = game.boardMan.getTile(index);
            
            if (t.getType() != TileType.NORMAL)
            {
                allSeen.add(t);
            }
        }
        
        // Simulate all collisions that these bombs would achieve one at a time.
        for (Integer index : bombRemovalSet)
        {           
            // Create a set to hold the current item.
            List<TileEntity> itemsSeenList = new ArrayList<TileEntity>();            
            itemsSeenList.add(boardMan.getTile(index));                        
            
            followThrough(game, index, itemsSeenList, allSeen);            
        }
        
        // Get the tiles the bombs would affect.
        boardMan.processBombs(bombRemovalSet, tileRemovalSet);
        deltaScore = scoreMan.calculateLineScore(
                tileRemovalSet,
                ScoreType.BOMB,
                statMan.getChainCount());

        // Fire a score event.
//        if (tutorialMan.isTutorialInProgress() == true)
//        {
//            listenerMan.notifyScoreChanged(new ScoreEvent(this, deltaScore, -1),
//                    IListenerManager.GameType.TUTORIAL);
//        }
//        else
//        {
//            listenerMan.notifyScoreChanged(new ScoreEvent(this, deltaScore, -1),
//                    IListenerManager.GameType.GAME);
//        }

        // Increment the score.
        if (game.tutorialMan.isTutorialInProgress() == false)       
        {
            game.scoreMan.incrementScore(deltaScore);        
        }

        // Show the SCT.
        ImmutablePosition p = boardMan.determineCenterPoint(tileRemovalSet);

        final ILabel label = new LabelBuilder(p.getX(), p.getY())
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                .color(settingsMan.getColor(Key.SCT_COLOR_ITEM)).size(scoreMan.determineFontSize(deltaScore))
                .text(String.valueOf(deltaScore)).end();

        a1 = new FadeAnimation.Builder(FadeAnimation.Type.OUT, label)
                .wait(settingsMan.getInt(Key.SCT_SCORE_FADE_WAIT))
                .duration(settingsMan.getInt(Key.SCT_SCORE_FADE_DURATION))
                .minOpacity(settingsMan.getInt(Key.SCT_SCORE_FADE_MIN_OPACITY))
                .maxOpacity(settingsMan.getInt(Key.SCT_SCORE_FADE_MAX_OPACITY))
                .end();
        
        a2 = new MoveAnimation.Builder(label)
                .duration(settingsMan.getInt(Key.SCT_SCORE_MOVE_DURATION))
                .speed(settingsMan.getInt(Key.SCT_SCORE_MOVE_SPEED))
                .theta(settingsMan.getInt(Key.SCT_SCORE_MOVE_THETA)).end();

        a2.setStartHook(new Runnable()
        {
            public void run()
            {
                game.layerMan.add(label, Layer.EFFECT);
            }
        });

        a2.setFinishHook(new Runnable()
        {
            public void run()
            {
                game.layerMan.remove(label, Layer.EFFECT);
            }
        });

        animationMan.add(a1);
        animationMan.add(a2);
        a1 = null;
        a2 = null;

        // Release references.
        p = null;

        // Play the sound.
        soundMan.play(Sound.BOMB);

        // Find all the new bombs.
        Set<Integer> nextBombRemovalSet = new HashSet<Integer>();
        boardMan.scanFor(TileType.BOMB, tileRemovalSet, nextBombRemovalSet);
        nextBombRemovalSet.removeAll(bombRemovalSet);

        // Remove all new bombs from the tile removal set.
        // They will be processed separately.
        tileRemovalSet.removeAll(nextBombRemovalSet);        
        
        // Find the rest of the item tiles.
        EnumSet<TileType> tileTypeSet = EnumSet.allOf(TileType.class);
        tileTypeSet.remove(TileType.BOMB);        
        scanFor(boardMan, tileTypeSet);  
        
        // Now animate the activation of the next removal set.
        Set<Integer> allItemSet = new HashSet<Integer>();
        allItemSet.addAll(nextBombRemovalSet);
        
        for (TileType t : tileTypeSet)
        {
            if (itemSetMap.containsKey(t))            
                allItemSet.addAll(itemSetMap.get(t));                
        }
        
        for (Integer i : allItemSet)
        {
            TileEntity t = boardMan.getTile(i);
            t.setAnimation(animateItemActivation(settingsMan, layerMan, boardMan, t));
            animationMan.add(t.getAnimation());
        }

        // Get the bombs from the set.
        Integer bombIndex = null;
        for (Integer i : tileRemovalSet)
        {                       
            if (boardMan.getTile(i).getType() == TileType.BOMB)
            {
                bombIndex = i;
                break;
            }
        }                
        
        // Start the line removal animations.
        for (Integer i : tileRemovalSet)
        {
            TileEntity t = boardMan.getTile(i);
            layerMan.toFront(t, Layer.TILE);
            
            if (boardMan.getTile(i).getType() == TileType.BOMB)
            {
                final GraphicEntity explosion = new GraphicEntity.Builder(
                        t.getCenterX() - 1,
                        t.getCenterY() - 1,
                        Settings.getSpriteResourcesPath() + "/Explosion.png")
                        .end();
                
                explosion.setWidth(2);
                explosion.setHeight(2);
                
                // Add the clone to the layer man.
                layerMan.add(explosion, Layer.EFFECT);

                // Make the animation.
                IAnimation anim1 = new ZoomAnimation.Builder(ZoomAnimation.Type.OUT, explosion)
                        .minWidth(2)                                
                        .maxWidth(Integer.MAX_VALUE)
                        .speed(settingsMan.getInt(Key.ANIMATION_BOMB_EXPLODE_ZOOM_SPEED))
                        .duration(settingsMan.getInt(Key.ANIMATION_BOMB_EXPLODE_ZOOM_DURATION))
                        .end();

                IAnimation anim2 = new FadeAnimation.Builder(FadeAnimation.Type.OUT, explosion)
                        .wait(settingsMan.getInt(Key.ANIMATION_BOMB_EXPLODE_FADE_WAIT))
                        .duration(settingsMan.getInt(Key.ANIMATION_BOMB_EXPLODE_FADE_DURATION))
                        .end();
                
                IAnimation anim3 = new FadeAnimation.Builder(FadeAnimation.Type.OUT, t)
                        .wait(settingsMan.getInt(Key.ANIMATION_BOMB_TILE_FADE_WAIT))
                        .duration(settingsMan.getInt(Key.ANIMATION_BOMB_TILE_FADE_DURATION))
                        .end();

                MetaAnimation meta = new MetaAnimation.Builder()
                        .add(anim1)
                        .add(anim2)
                        .add(anim3)
                        .end();

                meta.setFinishHook(new Runnable()
                {
                   public void run() 
                   { layerMan.remove(explosion, Layer.EFFECT); }
                });

                t.setAnimation(meta);
                animationMan.add(meta);                
            }
            else
            {           
                a1 = new FadeAnimation.Builder(FadeAnimation.Type.OUT, t)
                        .wait(settingsMan.getInt(Key.ANIMATION_BOMB_SHRAPNEL_FADE_WAIT))
                        .duration(settingsMan.getInt(Key.ANIMATION_BOMB_SHRAPNEL_FADE_DURATION))
                        .end();
                                       
                int h = boardMan.relativeColumnPosition(i, bombIndex).asInteger();
                int v = boardMan.relativeRowPosition(i, bombIndex).asInteger() * -1;                                
                
                int theta = 0;
                
                if      (h == 0) theta = 90 * v;                    
                else if (v == 0) theta = h == 1 ? 0 : 180;
                else
                {
                    theta = (int) Math.toDegrees(Math.atan(h / v));
                    if (h == -1) theta -= 180;                    
                }                                              
                                                                    
                a2 = new MoveAnimation.Builder(t)
                        .wait(settingsMan.getInt(Key.ANIMATION_BOMB_SHRAPNEL_MOVE_WAIT))
                        .duration(settingsMan.getInt(Key.ANIMATION_BOMB_SHRAPNEL_MOVE_DURATION))
                        .speed(settingsMan.getInt(Key.ANIMATION_BOMB_SHRAPNEL_MOVE_SPEED))
                        .gravity(settingsMan.getInt(Key.ANIMATION_BOMB_SHRAPNEL_MOVE_GRAVITY))
                        .theta(theta)                         
                        .omega(settingsMan.getDouble(Key.ANIMATION_BOMB_SHRAPNEL_MOVE_OMEGA))
                        .end();                                     
                
                t.setAnimation(a1);
                animationMan.add(a1);
                animationMan.add(a2);
                a1 = null;
                a2 = null;
            }
        }

        // If other bombs were hit, they will be dealt with in another
        // bomb removal cycle.
        this.itemSetMap.put(TileType.BOMB, nextBombRemovalSet);        

        // Set the flag.
        tileRemovalInProgress = true;
    }   

    private void removeStars(final Game game)
    {
        // Shortcuts to managers.
        AnimationManager animationMan = game.animationMan;
        BoardManager boardMan = game.boardMan;
        //ListenerManager listenerMan = game.listenerMan;
        ScoreManager scoreMan = game.scoreMan;
        SettingsManager settingsMan = game.settingsMan;
        SoundManager soundMan = game.soundMan;
        StatManager statMan = game.statMan;                        
        //TutorialManager tutorialMan = game.tutorialMan;  
        
        // Shortcut to the set.
        Set<Integer> starRemovalSet = this.itemSetMap.get(TileType.STAR);
        
        // Clear the flag.
        activateStarRemoval = false;

        // Increment cascade.
        statMan.incrementChainCount();

        // Used below.
        int deltaScore = 0;

        // Also used below.
        IAnimation a1, a2;
        
        
        List<TileEntity> allSeenSet = new ArrayList<TileEntity>();
          
        // Load the items into the allseen initially.
        for (Integer index : starRemovalSet)
        {            
            TileEntity t = game.boardMan.getTile(index);
            
            if (t.getType() != TileType.NORMAL)
            {
                allSeenSet.add(t);
            }
        }
        
        // Simulate all collisions that these stars would achieve one at a time.
        for (Integer index : starRemovalSet)
        {           
            // Create a set to hold the current item.
            List<TileEntity> itemsSeenList = new ArrayList<TileEntity>();            
            itemsSeenList.add(boardMan.getTile(index));                        
            
            followThrough(game, index, itemsSeenList, allSeenSet);            
        }

        // Get the tiles the bombs would affect.
        boardMan.processStars(starRemovalSet, tileRemovalSet);

        for (Integer index : lastMatchSet)
        {
            if (boardMan.getTile(index) == null)
            {
                continue;
            }

            if (boardMan.getTile(index).getClass() != StarTileEntity.class)
            {
                tileRemovalSet.remove(index);
            }
        }

        deltaScore = scoreMan.calculateLineScore(
                tileRemovalSet,
                ScoreType.STAR,
                statMan.getChainCount());       
        
        // Increment the score.
        if (game.tutorialMan.isTutorialInProgress() == false)       
        {
            game.scoreMan.incrementScore(deltaScore);        
        }

        // Show the SCT.
        ImmutablePosition p = boardMan.determineCenterPoint(tileRemovalSet);

        final ILabel label = new LabelBuilder(p.getX(), p.getY())
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                .color(settingsMan.getColor(Key.SCT_COLOR_ITEM))
                .size(scoreMan.determineFontSize(deltaScore))
                .text(String.valueOf(deltaScore))
                .end();

        a1 = new FadeAnimation.Builder(FadeAnimation.Type.OUT, label)
                .wait(settingsMan.getInt(Key.SCT_SCORE_FADE_WAIT))
                .duration(settingsMan.getInt(Key.SCT_SCORE_FADE_DURATION))
                .minOpacity(settingsMan.getInt(Key.SCT_SCORE_FADE_MIN_OPACITY))
                .maxOpacity(settingsMan.getInt(Key.SCT_SCORE_FADE_MAX_OPACITY))
                .end();
        
        a2 = new MoveAnimation.Builder(label)
                .duration(settingsMan.getInt(Key.SCT_SCORE_MOVE_DURATION))
                .speed(settingsMan.getInt(Key.SCT_SCORE_MOVE_SPEED))
                .theta(settingsMan.getInt(Key.SCT_SCORE_MOVE_THETA)).end();

        a2.setStartHook(new Runnable()
        {
            public void run()
            {
                game.layerMan.add(label, Layer.EFFECT);
            }
        });

        a2.setFinishHook(new Runnable()
        {
            public void run()
            {
                game.layerMan.remove(label, Layer.EFFECT);
            }
        });

        animationMan.add(a1);
        animationMan.add(a2);
        a1 = null;
        a2 = null;

        // Release references.
        p = null;

        // Play the sound.
        soundMan.play(Sound.STAR);
        
        // Find any additional item tiles.
        EnumSet<TileType> tileTypeSet = EnumSet.allOf(TileType.class);
        tileTypeSet.remove(TileType.STAR);        
        scanFor(boardMan, tileTypeSet);  

        // Start the line removal animations.
        int i = 0;
        for (Iterator it = tileRemovalSet.iterator(); it.hasNext();)
        {
            TileEntity t = boardMan.getTile((Integer) it.next());

            // Bring the entity to the front.
            game.layerMan.toFront(t, Layer.TILE);

            // Increment counter.
            i++;

            int angle = i % 2 == 0 ? 70 : 180 - 70;
                                          
            a1 = new MoveAnimation.Builder(t)
                    .duration(settingsMan.getInt(Key.ANIMATION_JUMP_MOVE_DURATION))
                    .theta(angle)
                    .speed(settingsMan.getInt(Key.ANIMATION_JUMP_MOVE_SPEED))
                    .gravity(settingsMan.getInt(Key.ANIMATION_JUMP_MOVE_GRAVITY))
                    .end();
            a2 = new FadeAnimation.Builder(FadeAnimation.Type.OUT, t)
                    .wait(settingsMan.getInt(Key.ANIMATION_JUMP_FADE_WAIT))
                    .duration(settingsMan.getInt(Key.ANIMATION_JUMP_MOVE_DURATION))
                    .end();
            
            t.setAnimation(a1);
            animationMan.add(a1);
            animationMan.add(a2);
            
            a1 = null;
            a2 = null;
        }

        // Clear the star removal set.
        starRemovalSet.clear();

        // Set the flag.
        tileRemovalInProgress = true;
    }
    
    private void shiftGravity(Game game)
    {
        // Shortcut to the set.
        Set<Integer> gravityRemovalSet = this.itemSetMap.get(TileType.GRAVITY);
        
        // Determine the new gravity.
        EnumSet<Direction> gravity = null;
        if (game.boardMan.getGravity().contains(Direction.LEFT))
        {
            gravity = EnumSet.of(Direction.DOWN, Direction.RIGHT);
        }
        else
        {
            gravity = EnumSet.of(Direction.DOWN, Direction.LEFT);
        }

        // Set the new gravity.
        game.boardMan.setGravity(gravity);
        
        // Clear the gravity tiles.
        gravityRemovalSet.clear();
    }

    /**
     * Notify the tile remover of a level-up.
     */
    public void notifyLevelUp()
    {
        this.activateLevelUp = true;
    }
}