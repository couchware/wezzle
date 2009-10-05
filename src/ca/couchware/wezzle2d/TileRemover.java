/*
 *  Wezzle
 *  Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d;

import ca.couchware.wezzle2d.tracker.Line;
import ca.couchware.wezzle2d.tracker.Move;
import ca.couchware.wezzle2d.Refactorer.RefactorSpeed;
import ca.couchware.wezzle2d.ResourceFactory.LabelBuilder;
import ca.couchware.wezzle2d.animation.AnimationAdapter;
import ca.couchware.wezzle2d.animation.AnimationHelper;
import ca.couchware.wezzle2d.animation.FadeAnimation;
import ca.couchware.wezzle2d.animation.FadeAnimation.Builder;
import ca.couchware.wezzle2d.event.LevelEvent;
import ca.couchware.wezzle2d.manager.ListenerManager.GameType;
import ca.couchware.wezzle2d.animation.IAnimation;
import ca.couchware.wezzle2d.animation.MetaAnimation;
import ca.couchware.wezzle2d.animation.MetaAnimation.FinishRule;
import ca.couchware.wezzle2d.animation.MetaAnimation.RunRule;
import ca.couchware.wezzle2d.animation.MoveAnimation;
import ca.couchware.wezzle2d.animation.WaitAnimation;
import ca.couchware.wezzle2d.animation.ZoomAnimation;
import ca.couchware.wezzle2d.audio.Sound;
import ca.couchware.wezzle2d.difficulty.IDifficultyStrategy;
import ca.couchware.wezzle2d.event.ILevelListener;
import ca.couchware.wezzle2d.event.LineEvent;
import ca.couchware.wezzle2d.event.MoveEvent;
import ca.couchware.wezzle2d.graphics.GraphicEntity;
import ca.couchware.wezzle2d.graphics.IPositionable.Alignment;
import ca.couchware.wezzle2d.manager.AnimationManager;
import ca.couchware.wezzle2d.manager.BoardManager;
import ca.couchware.wezzle2d.manager.IResettable;
import ca.couchware.wezzle2d.manager.LayerManager;
import ca.couchware.wezzle2d.manager.LayerManager.Layer;
import ca.couchware.wezzle2d.manager.ListenerManager;
import ca.couchware.wezzle2d.manager.PieceManager;
import ca.couchware.wezzle2d.manager.ScoreManager;
import ca.couchware.wezzle2d.manager.Settings;
import ca.couchware.wezzle2d.manager.Settings.Key;
import ca.couchware.wezzle2d.manager.SettingsManager;
import ca.couchware.wezzle2d.manager.SoundManager;
import ca.couchware.wezzle2d.manager.StatManager;
import ca.couchware.wezzle2d.manager.TimerManager;
import ca.couchware.wezzle2d.manager.TutorialManager;
import ca.couchware.wezzle2d.ui.ITextLabel;
import ca.couchware.wezzle2d.util.ImmutablePosition;
import ca.couchware.wezzle2d.tile.RocketTile;
import ca.couchware.wezzle2d.tile.StarTile;
import ca.couchware.wezzle2d.tile.Tile;
import ca.couchware.wezzle2d.tile.TileHelper;
import ca.couchware.wezzle2d.tile.TileType;
import ca.couchware.wezzle2d.tracker.TileEffect;
import ca.couchware.wezzle2d.util.CouchLogger;
import ca.couchware.wezzle2d.util.IBuilder;
import java.awt.Color;
import java.util.ArrayList;
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
public class TileRemover implements IResettable, ILevelListener
{

    /** The only instance of this class to ever exist. */
    private static final TileRemover single = new TileRemover();
       
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
    
    /** If true, a line removal will be activated next loop. */    
    private boolean activateLineRemoval;    
    
    /** If true, level up. */
    private boolean activateLevelUp;
    
    /** If true, a line removal is in progress. */
    private boolean tileRemovalInProgress;     
    
    /** The set of tile indices that will be removed. */
    private Set<Integer> tileRemovalSet;
    
    /** If true, uses jump animation instead of zoom. */
    private boolean levelUpInProgress;
    
    /** If true, award no points for this tile removal. */
    private boolean doNotAwardScore;
    
    /** If true, do not activate items on this removal. */
    private boolean doNotActivateItems;
      
    /** If true, a bomb removal will be activated next loop. */
    private boolean activateBombRemoval;    
    
    /** If true, a star removal will be activated next loop. */
    private boolean activateStarRemoval;   
    
    /** If true, a rocket removal will be activated next loop. */
    private boolean activateRocketRemoval;

    /** If true, the gravity shift will be activated next loop. */
    private boolean gravityShiftInProgress;
      
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
        
        // Now create all the item sets.
        for (TileType t : tileTypeSet)
        {
            itemSetMap.put(t, new HashSet<Integer>());
        }
        
        // Reset the state.
        this.resetState();
    }      
    
    /**
     * Reset the tile remover state.
     */
    public void resetState()
    {
        this.tileRemovalSet.clear();
        this.lastMatchSet.clear();
        
        for (Set<Integer> set : itemSetMap.values())
        {
            set.clear();
        }
        
        this.activateLineRemoval    = false;
        this.activateLevelUp        = false;
        this.tileRemovalInProgress  = false;
        this.levelUpInProgress      = false;
        this.activateBombRemoval    = false;            
        this.activateStarRemoval    = false;
        this.activateRocketRemoval  = false;
        this.gravityShiftInProgress = false;
        this.doNotAwardScore        = false;
        this.doNotActivateItems     = false;
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
    public void updateLogic(final Game game, ManagerHub hub)
    {
        // Sanity check.
        if (game == null)
            throw new IllegalArgumentException("Hub cannot be null.");
        
        if (hub == null)
            throw new IllegalArgumentException("Game cannot be null.");
        
        final ListenerManager listenerMan = hub.listenerMan;
        final PieceManager pieceMan       = hub.pieceMan;
        final TimerManager timerMan       = hub.timerMan;           
        
        if ( activateLevelUp )
        {
            activateLevelUp = false;
            levelUp( hub.boardMan );
        }

        // See if it just finished.
        if ( game.getRefactorer().isFinished() && areItemSetsEmpty() )
        {
            // Keep track of chains.
            game.getTracker().record( findMatches(hub) );
            
            // If there are matches, score them, remove 
            // them and then refactor again.
            if ( !tileRemovalSet.isEmpty() )
            {
                startLineRemoval();
            }
            else
            {
                // Make sure the tiles are not still dropping.
                if ( !game.getTileDropper().isTileDropping() )
                {      
                    // Don't fire a move completed event if we're just
                    // doing the level up line removal.
                    if (!this.levelUpInProgress)
                    {
                        // Fire the move completed event.
                        listenerMan.notifyMoveCompleted(new MoveEvent(this, 1));
                        
                        // The move is completed. Build the move.                        
                        Move move = game.getTracker().finishMove();
                        CouchLogger.get().recordMessage(this.getClass(),
                                "\n" + move.toString());

                        // Evaluate the achievements.
                        hub.achievementMan.evaluate(game, hub);                        
                    }
                    
                    // Start the next move.
                    boolean getNextPiece = !this.levelUpInProgress
                            && !hub.tutorialMan.isTutorialRunning();
                    startNextMove(pieceMan, timerMan, getNextPiece);
                    CouchLogger.get().recordWarning(this.getClass(), "getNextPiece = " + getNextPiece);
                    
                    // Clear the level up in progress flag.
                    this.levelUpInProgress = false;                                             
                                                                                                  
                } // end if                                                                               
            } // end if
        } // end if                       

        // If a line removal was activated.
        if (this.activateLineRemoval)
        {            
            removeLines(game, hub);
        }                 
        // If the star removal is in progress.
        else if (this.activateRocketRemoval)
        {            
            game.getTracker().record( removeRockets(game, hub) );
        }
        // If the star removal is in progress.
        else if (this.activateStarRemoval)
        {
            game.getTracker().record( removeStars(game, hub) );
        }
        // If a bomb removal is in progress.
        else if (this.activateBombRemoval)
        {
            game.getTracker().record( removeBombs(game, hub) );
        }
        
        // If a line removal is in progress.        
        if (this.tileRemovalInProgress)
        {            
            processRemoval(game, hub);
        }

        if (this.gravityShiftInProgress)
        {
            // Turn off the shift.
            this.gravityShiftInProgress = false;

            // Makes sure to reset refactor speed here.
            Refactorer refactorer = game.getRefactorer();
            refactorer.setRefactorSpeed(this.refactorSpeed);
        }
    }   
         
    private void startLineRemoval()
    {
        // Activate the line removal.
        this.activateLineRemoval = true;
    }   
    
    private void startNextMove(
            PieceManager pieceMan, 
            TimerManager timerMan,
            boolean newPiece)
    {
        // Load new piece and make it visible.
        if (newPiece) pieceMan.nextPiece();

        pieceMan.showPieceGrid();
        pieceMan.startAnimation(timerMan);

        // Reset the mouse.
        pieceMan.clearMouseButtonSet();
        timerMan.setPaused(false);
    }
    
    /**
     * Checks the emptiness of all the item sets.
     * 
     * @return
     */
    private boolean areItemSetsEmpty()
    {
        if (this.itemSetMap == null)
            throw new IllegalStateException("itemSetMap is null.");
                    
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
                || this.activateLineRemoval 
                || this.activateBombRemoval 
                || this.activateStarRemoval 
                || this.activateRocketRemoval;
    }

    private void levelUp(BoardManager boardMan)
    {
        // Set some flags for the level up.
        this.activateLineRemoval = true;
        this.levelUpInProgress   = true;
        this.doNotAwardScore = true;
        this.doNotActivateItems = true;
        
        // Clear the tile removal set.        
        this.tileRemovalSet.clear();
        
        int j;
        if (boardMan.getGravity().contains(BoardManager.Direction.UP))
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

    public List<Line> findMatches(ManagerHub hub)
    {        
        // Shortcuts to the managers.
        BoardManager boardMan       = hub.boardMan;
        StatManager  statMan        = hub.statMan;
        
        // Look for matches.
        tileRemovalSet.clear();

        // The lines in the chain
        final List<Line> lineList = new ArrayList<Line>();
        
        int cycleX = boardMan.findXMatch(tileRemovalSet, lineList);
        int cycleY = boardMan.findYMatch(tileRemovalSet, lineList);                
        
        statMan.incrementCycleLineCount(cycleX);
        statMan.incrementCycleLineCount(cycleY);
        
        //  Handle any lines we may have had.       
        if (hub.tutorialMan.isTutorialRunning() == true)
        {
            hub.listenerMan.notifyLineConsumed(new LineEvent(
                    hub.statMan.getCycleLineCount(), this),
                    GameType.TUTORIAL);
        }
        else
        {
            hub.listenerMan.notifyLineConsumed(new LineEvent(
                    hub.statMan.getCycleLineCount(), this),
                    GameType.GAME);
        }                

        // Copy the match into the last line match holder.
        lastMatchSet.clear();
        lastMatchSet.addAll(tileRemovalSet);

        // Return the chain list.        
        return lineList;
    }    
    
    private void processRemoval(Game game, ManagerHub hub)
    {        
        // Animation completed flag.
        boolean animationInProgress = false;

        // Check to see if they're all done.
        for (Iterator it = tileRemovalSet.iterator(); it.hasNext();)
        {
            Tile tile = hub.boardMan.getTile((Integer) it.next());
            if (tile != null && !tile.getAnimation().isFinished())
            {
                animationInProgress = true;
                break;
            }
        } // end for

        if (!animationInProgress)
        {
            // Remove the tiles from the board.
            hub.boardMan.removeTiles(tileRemovalSet);

            // Bomb removal is completed.
            this.tileRemovalInProgress = false;

            // See if there are any bombs in the bomb set.
            // If there are, activate the bomb removal.              
            if (!this.itemSetMap.get(TileType.ROCKET).isEmpty())
            {
                this.activateRocketRemoval = true;
            }
            else if (!this.itemSetMap.get(TileType.STAR).isEmpty())
            {
                this.activateStarRemoval = true;
            }
            else if (!this.itemSetMap.get(TileType.BOMB).isEmpty())
            {
                this.activateBombRemoval = true;
            }
            else if (!this.itemSetMap.get(TileType.GRAVITY).isEmpty())
            {                
                game.getTracker().finishChain(game, hub);

                shiftGravity(hub.boardMan);

                Refactorer refactorer = game.getRefactorer();
                this.refactorSpeed = refactorer.getRefactorSpeed();
                refactorer.setRefactorSpeed(RefactorSpeed.SHIFT);
                refactorer.startRefactor();

                // Check for achievements.
                hub.achievementMan.evaluate(game, hub);
            }
            // Otherwise, start a new refactor.
            else
            {               
                game.getTracker().finishChain(game, hub);
                
                Refactorer refactorer = game.getRefactorer();
                //refactorer.setRefactorSpeed(this.refactorSpeed);
                refactorer.startRefactor();

                // Check for achievements.
                hub.achievementMan.evaluate(game, hub);
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

    private void removeLines(final Game game, ManagerHub hub)
    {
        // Sanity check.
        if (game == null)
            throw new IllegalArgumentException("Game cannot be null.");

        if (hub == null)
            throw new IllegalArgumentException("Hub cannot be null.");
        
        // Shortcuts to managers.
        final AnimationManager animationMan = hub.animationMan;
        final BoardManager boardMan         = hub.boardMan;
        final LayerManager layerMan         = hub.layerMan;
        final ScoreManager scoreMan         = hub.scoreMan;
        final SettingsManager settingsMan   = hub.settingsMan;
        final SoundManager soundMan         = hub.soundMan;
        final StatManager statMan           = hub.statMan;     
        final TutorialManager tutorialMan   = hub.tutorialMan;

        // Clear flag.
        activateLineRemoval = false;

        // Increment chain count.
        statMan.incrementChainCount();
        statMan.incrementLineChainCount();

        // Calculate score, unless no-score flag is set.
        if (!doNotAwardScore)
        {
            final int deltaScore = scoreMan.calculateLineScore(
                    tileRemovalSet,
                    ScoreManager.ScoreType.LINE,
                    statMan.getChainCount());           
            
            // Increment the score.
            if (!tutorialMan.isTutorialRunning())       
            {
                scoreMan.incrementScore(deltaScore);        
            }

            // Show the SCT.
            final ImmutablePosition pos = boardMan.determineCenterPoint(tileRemovalSet);
            final Color color = settingsMan.getColor(Key.SCT_COLOR_LINE);
            animationMan.add(AnimationHelper.animateItemSct(hub, pos, deltaScore, color));
        }
        else
        {
            // Turn off the flag now that it has been used.
            doNotAwardScore = false;
        }

        // Play the sound.
        int chainNumber = Math.min(7, statMan.getLineChainCount());
        soundMan.play(Sound.valueOf("LINE_" + chainNumber));

        // Make sure bombs aren't removed (they get removed
        // in a different step).  However, if the no-items
        // flag is set, then ignore bombs.
        if (!this.doNotActivateItems)
        {
            scanFor(boardMan, EnumSet.allOf(TileType.class), true);
        }
        else
        {
            // Turn off the flag now that it has been used.
            this.doNotActivateItems = false;
        }

        // Start the line removal animations if there are any
        // non-bomb tiles.
        if (!tileRemovalSet.isEmpty())
        {            
            for (Integer index : tileRemovalSet)
            {
                Tile tile = boardMan.getTile(index);

                if (this.levelUpInProgress)
                {
                    IAnimation anim = AnimationHelper.animateLevelUp(hub, index);
                    tile.setAnimation(anim);
                    animationMan.add(anim);
                }
                else
                {                    
                    IAnimation anim = AnimationHelper.animateRemove(hub, tile);
                    tile.setAnimation(anim);
                    animationMan.add(anim);
                }                                
                
                // Get a set of all the items activated.
                Set<Integer> allItemSet = new HashSet<Integer>();
                for (Set<Integer> itemSet : itemSetMap.values())
                {
                    allItemSet.addAll(itemSet);
                }

                // Animate their activation.
                for (Integer itemIndex : allItemSet)
                {
                    // Get the tile and make a copy.
                    Tile itemTile = boardMan.getTile(itemIndex);
                   
                    // Create and add the animation.                    
                    animationMan.add(AnimationHelper.animateItemActivation(hub, itemTile));
                }
            } // end for
        } // end if    
        
        Set<Integer> gravityRemovalSet = itemSetMap.get(TileType.GRAVITY);
        if (!gravityRemovalSet.isEmpty())
        {
            for (Integer i : gravityRemovalSet)
            {
                Tile t = boardMan.getTile(i);
                IAnimation a = new FadeAnimation.Builder(FadeAnimation.Type.OUT, t)
                        .duration(50)
                        .build();
                t.setAnimation(a);
                animationMan.add(a);
            }            
        }                    

        // Set the flag.
        tileRemovalInProgress = true;
    }    
        
    private List<TileEffect> removeRockets(final Game game, ManagerHub hub)
    {
        // Sanity check.
        if (game == null)
            throw new IllegalArgumentException("Game cannot be null.");

        if (hub == null)
            throw new IllegalArgumentException("Hub cannot be null.");
        
        final AnimationManager animationMan = hub.animationMan;
        final BoardManager     boardMan     = hub.boardMan;
        final LayerManager     layerMan     = hub.layerMan;
        final ScoreManager     scoreMan     = hub.scoreMan;
        final SettingsManager  settingsMan  = hub.settingsMan;
        final SoundManager     soundMan     = hub.soundMan;
        final StatManager      statMan      = hub.statMan;                        
        final TutorialManager  tutorialMan  = hub.tutorialMan;
        
        // Shortcut to the set.
        Set<Integer> rocketRemovalSet = this.itemSetMap.get(TileType.ROCKET);
        
        // Clear the flag.
        activateRocketRemoval = false;

        // Increment cascade.
        statMan.incrementChainCount();

        // Used below.
        int deltaScore = 0;       

        // Get the tiles the rockets would affect.
        List<TileEffect> effectList = new ArrayList<TileEffect>();
        boardMan.processRockets(rocketRemovalSet, tileRemovalSet, effectList);

        // Add those to

        for (Integer index : lastMatchSet)
        {
            if (boardMan.getTile(index) == null)
            {
                continue;
            }

            if (boardMan.getTile(index).getClass() != RocketTile.class)
            {
                tileRemovalSet.remove(index);
            }
        } // end for

        deltaScore = scoreMan.calculateLineScore(
                tileRemovalSet,
                ScoreManager.ScoreType.ROCKET,
                statMan.getChainCount());       
        
        // Increment the score.
        if (tutorialMan.isTutorialRunning() == false)       
        {
            scoreMan.incrementScore(deltaScore);        
        }

        // Show the SCT.
        final ImmutablePosition pos = boardMan.determineCenterPoint(tileRemovalSet);
        final Color color = hub.settingsMan.getColor(Key.SCT_COLOR_ITEM);
        animationMan.add(AnimationHelper.animateItemSct(hub, pos, deltaScore, color));
                       
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
            Tile t = boardMan.getTile(i);
            
            if (t == null) continue;
            
            t.setAnimation(AnimationHelper.animateItemActivation(hub, t));
            animationMan.add(t.getAnimation());
        }

        // Start the line removal animations.
        int i = 0;
        for (Iterator it = tileRemovalSet.iterator(); it.hasNext();)
        {
            Tile tile = boardMan.getTile((Integer) it.next());

            // Bring the tile to the front.
            layerMan.toFront(tile, Layer.TILE);

            IAnimation anim;
            if (tile.getType() == TileType.ROCKET)
            {                
                anim = AnimationHelper.animateRocket(hub, tile);
                
            }
            else
            {
                int angle = ++i % 2 == 0 ? 70 : 180 - 70;
                anim = AnimationHelper.animateRocketJump(hub, tile, angle);
            }
            tile.setAnimation(anim);
            animationMan.add(anim);
        }

        // If other things were were hit, they will be dealt with in another
        // removal cycle.
        this.itemSetMap.put(TileType.ROCKET, nextRocketRemovalSet);

        // Set the flag.
        tileRemovalInProgress = true;

        // Return the effect list.
        return effectList;
    }
    
    private List<TileEffect> removeBombs(final Game game, final ManagerHub hub)
    {        
        // Sanity check.
        if (game == null)
            throw new IllegalArgumentException("Game cannot be null.");

        if (hub == null)
            throw new IllegalArgumentException("Hub cannot be null.");
        
        // Create shortcuts to all the managers.
        final AnimationManager animationMan = hub.animationMan;
        final BoardManager     boardMan     = hub.boardMan;
        final LayerManager     layerMan     = hub.layerMan;
        final ScoreManager     scoreMan     = hub.scoreMan;
        final SettingsManager  settingsMan  = hub.settingsMan;
        final SoundManager     soundMan     = hub.soundMan;
        final StatManager      statMan      = hub.statMan;                        
        final TutorialManager  tutorialMan  = hub.tutorialMan;                                              

        // Shortcut to the set.
        Set<Integer> bombRemovalSet = this.itemSetMap.get(TileType.BOMB);
        
        // Clear the flag.
        activateBombRemoval = false;

        // Increment cascade.
        statMan.incrementChainCount();

        // Used below.
        int deltaScore = 0;

        // Get the tiles the bombs would affect.
        List<TileEffect> effectList = new ArrayList<TileEffect>();
        boardMan.processBombs(bombRemovalSet, tileRemovalSet, effectList);
        deltaScore = scoreMan.calculateLineScore(
                tileRemovalSet,
                ScoreManager.ScoreType.BOMB,
                statMan.getChainCount());       

        // Increment the score.
        if (tutorialMan.isTutorialRunning() == false)       
        {
            scoreMan.incrementScore(deltaScore);        
        }

        // Show the SCT.
        ImmutablePosition pos = boardMan.determineCenterPoint(tileRemovalSet);
        final Color color = hub.settingsMan.getColor(Key.SCT_COLOR_ITEM);
        animationMan.add(AnimationHelper.animateItemSct(hub, pos, deltaScore, color));

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
            Tile t = boardMan.getTile(i);
            
            // Make sure the item has not already been removed.
            if (t == null) continue;
            
            t.setAnimation(AnimationHelper.animateItemActivation(hub, t));
            animationMan.add(t.getAnimation());
        }

        // Get the bombs from the set.
        int bombIndex = -1;
        for (int i : tileRemovalSet)
        {                       
            if (boardMan.getTile(i).getType() == TileType.BOMB)
            {
                bombIndex = i;
                break;
            }
        }

        // If bomb index is still -1, then there's no bomb, uh-oh :(
        if (bombIndex < 0)
            throw new IllegalStateException("No bomb found!");
        
        // Start the line removal animations.
        for (int index : tileRemovalSet)
        {
            Tile tile = boardMan.getTile(index);
            layerMan.toFront(tile, Layer.TILE);
            
            if (tile.getType() == TileType.BOMB)
            {
                IAnimation anim = AnimationHelper.animateExplosion(hub, tile);
                tile.setAnimation(anim);
                animationMan.add(anim);
            }
            else
            {           
                IAnimation anim = AnimationHelper.animateShrapnel(hub, tile, index, bombIndex);
                tile.setAnimation(anim);
                animationMan.add(anim);
            }
        }

        // If other bombs were hit, they will be dealt with in another
        // bomb removal cycle.
        this.itemSetMap.put(TileType.BOMB, nextBombRemovalSet);        

        // Set the flag.
        this.tileRemovalInProgress = true;

        // Return the effect list.
        return effectList;
    }   

    private List<TileEffect> removeStars(final Game game, final ManagerHub hub)
    {        
        // Sanity check.
        if (game == null)
            throw new IllegalArgumentException("Game cannot be null.");

        if (hub == null)
            throw new IllegalArgumentException("Hub cannot be null.");
        
        // Create shortcuts to all the managers.
        final AnimationManager animationMan = hub.animationMan;
        final BoardManager boardMan         = hub.boardMan;
        final LayerManager layerMan         = hub.layerMan;
        final ScoreManager scoreMan         = hub.scoreMan;
        final SoundManager soundMan         = hub.soundMan;
        final StatManager statMan           = hub.statMan;
        final TutorialManager tutorialMan   = hub.tutorialMan;
        
        // Shortcut to the set.
        Set<Integer> starRemovalSet = this.itemSetMap.get(TileType.STAR);
        
        // Clear the flag.
        activateStarRemoval = false;

        // Increment cascade.
        statMan.incrementChainCount();

        // Used below.
        int deltaScore = 0;

        // Get the tiles the bombs would affect.
        List<TileEffect> effectList = new ArrayList<TileEffect>();
        boardMan.processStars(starRemovalSet, tileRemovalSet, effectList);

        for (Integer index : lastMatchSet)
        {
            if (boardMan.getTile(index) == null)
            {
                continue;
            }

            if (boardMan.getTile(index).getClass() != StarTile.class)
            {
                tileRemovalSet.remove(index);
            }
        }

        deltaScore = scoreMan.calculateLineScore(
                tileRemovalSet,
                ScoreManager.ScoreType.STAR,
                statMan.getChainCount());       
        
        // Increment the score.
        if (tutorialMan.isTutorialRunning() == false)       
        {
            scoreMan.incrementScore(deltaScore);        
        }

        // Show the SCT.
        final ImmutablePosition pos = boardMan.determineCenterPoint(tileRemovalSet);
        final Color color = hub.settingsMan.getColor(Key.SCT_COLOR_ITEM);
        animationMan.add(AnimationHelper.animateItemSct(hub, pos, deltaScore, color));

        // Play the sound.
        soundMan.play(Sound.STAR);
        
        // Find any additional item tiles.
        EnumSet<TileType> tileTypeSet = EnumSet.allOf(TileType.class);
        tileTypeSet.remove(TileType.STAR);        
        scanFor(boardMan, tileTypeSet);  

        // Start the line removal animations.        
        int i = 0;
        for (Integer index : tileRemovalSet)
        {
            Tile tile = boardMan.getTile(index);           
            layerMan.toFront(tile, Layer.TILE);
            
            int angle = ++i % 2 == 0 ? 70 : 180 - 70;
            IAnimation anim = AnimationHelper.animateJump(hub, tile, angle);

            tile.setAnimation(anim);
            animationMan.add(anim);
        }

        // Clear the star removal set.
        starRemovalSet.clear();

        // Set the flag.
        tileRemovalInProgress = true;

        // Return tile effect set.
        return effectList;
    }
    
    private void shiftGravity(BoardManager boardMan)
    {
        // Set the flag.
        this.gravityShiftInProgress = true;

        // Determine the new gravity.
        EnumSet<BoardManager.Direction> gravity = null;
        if (boardMan.getGravity().contains(BoardManager.Direction.LEFT))
        {
            gravity = EnumSet.of(BoardManager.Direction.DOWN,
                    BoardManager.Direction.RIGHT);
        }
        else
        {
            gravity = EnumSet.of(BoardManager.Direction.DOWN,
                    BoardManager.Direction.LEFT);
        }

        // Set the new gravity.
        boardMan.setGravity(gravity);
        
        // Clear the gravity tiles.
        this.itemSetMap.get(TileType.GRAVITY).clear();
    }

    

    public void levelChanged(LevelEvent event)
    {                
        this.activateLevelUp = event.isLevelUp();
    }
   
}
