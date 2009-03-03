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
import ca.couchware.wezzle2d.animation.FadeAnimation;
import ca.couchware.wezzle2d.event.LevelEvent;
import ca.couchware.wezzle2d.manager.ListenerManager.GameType;
import ca.couchware.wezzle2d.animation.IAnimation;
import ca.couchware.wezzle2d.animation.MetaAnimation;
import ca.couchware.wezzle2d.animation.MetaAnimation.FinishRule;
import ca.couchware.wezzle2d.animation.MoveAnimation;
import ca.couchware.wezzle2d.animation.ZoomAnimation;
import ca.couchware.wezzle2d.audio.Sound;
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
    private boolean noScore;
    
    /** If true, do not activate items on this removal. */
    private boolean noItems;
      
    /** If true, a bomb removal will be activated next loop. */
    private boolean activateBombRemoval;    
    
    /** If true, a star removal will be activated next loop. */
    private boolean activateStarRemoval;   
    
    /** If true, a rocket removal will be activated next loop. */
    private boolean activateRocketRemoval;    
      
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
        
        this.activateLineRemoval   = false;        
        this.activateLevelUp       = false;        
        this.tileRemovalInProgress = false;             
        this.levelUpInProgress     = false;                     
        this.activateBombRemoval   = false;            
        this.activateStarRemoval   = false;           
        this.activateRocketRemoval = false;    
        this.noScore = false;        
        this.noItems = false;     
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
        
        if (activateLevelUp == true)
        {
            activateLevelUp = false;
            levelUp(hub.boardMan);
        }

        // See if it just finished.
        if (game.getRefactorer().isFinished() && areItemSetsEmpty())
        {
            // Keep track of chains.
            game.getTracker().record( findMatches(hub) );
            
            // If there are matches, score them, remove 
            // them and then refactor again.
            if (!tileRemovalSet.isEmpty())
            {
                startLineRemoval(game.getRefactorer());
            }
            else
            {
                // Make sure the tiles are not still dropping.
                if (!game.getTileDropper().isTileDropping())
                {      
                    // Don't fire a move completed event if we're just
                    // doing the level up line removal.
                    if (!this.levelUpInProgress)
                    {
                        // Fire the move completed event.
                        listenerMan.notifyMoveCompleted(new MoveEvent(this, 1));
                        
                        // The move is completed. Build the move.
                        //Move move = Move.newInstance(game.getChainList());
                        Move move = game.getTracker().finishMove();
                        CouchLogger.get().recordMessage(this.getClass(),
                                "\n" + move.toString());                                               
                    }
                    
                    // Start the next move.
                    startNextMove(pieceMan, timerMan, !this.levelUpInProgress);
                    
                    // Clear the level up in progress flag.
                    this.levelUpInProgress = false;                                             
                                                                                                  
                } // end if                                                                               
            } // end if
        } // end if                       

        // If a line removal was activated.
        if (this.activateLineRemoval == true)
        {            
            removeLines(game, hub);
        }                 
        // If the star removal is in progress.
        else if (this.activateRocketRemoval == true)
        {            
            game.getTracker().record( removeRockets(game, hub) );
        }
        // If the star removal is in progress.
        else if (this.activateStarRemoval == true)
        {
            removeStars(game, hub);
        }
        // If a bomb removal is in progress.
        else if (this.activateBombRemoval == true)
        {
            game.getTracker().record( removeBombs(game, hub) );
        }
        
        // If a line removal is in progress.        
        if (this.tileRemovalInProgress == true)
        {            
            processRemoval(game, hub);
        }
    }   
         
    private void startLineRemoval(Refactorer refactorer)
    {
        // Activate the line removal.
        this.activateLineRemoval = true;

        // Record the refactor speed.
        this.refactorSpeed = refactorer.getRefactorSpeed();
    }   
    
    private void startNextMove(
            PieceManager pieceMan, 
            TimerManager timerMan,
            boolean newPiece)
    {
        // Load new piece and make it visible.
        if (newPiece) { pieceMan.loadPiece(); }
        pieceMan.showPieceGrid();
        pieceMan.startAnimation(timerMan);

        // Reset the mouse.
        pieceMan.clearMouseButtonSet();

        // Unpause the timer.
        //timerMan.resetCurrentTime();
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
        this.noScore = true;
        this.noItems = true;
        
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
            if (hub.boardMan.getTile((Integer) it.next()).getAnimation().isFinished() == false)
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
                game.getTracker().finishChain();
                shiftGravity(hub.boardMan);                
                game.getRefactorer()
                        .setRefactorSpeed(RefactorSpeed.SHIFT)
                        .startRefactor();
            }
            // Otherwise, start a new refactor.
            else
            {
                game.getTracker().finishChain();
                game.getRefactorer()
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
        if (noScore == false)
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
            animationMan.add(animateItemSct(hub, pos, deltaScore, color));
        }
        else
        {
            // Turn off the flag now that it has been used.
            noScore = false;
        }

        // Play the sound.
        int chainNumber = Math.min(7, statMan.getLineChainCount());
        soundMan.play(Sound.valueOf("LINE_" + chainNumber));

        // Make sure bombs aren't removed (they get removed
        // in a different step).  However, if the no-items
        // flag is set, then ignore bombs.
        if (!this.noItems)
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
        if (!tileRemovalSet.isEmpty())
        {            
            for (Integer index : tileRemovalSet)
            {
                Tile tile = boardMan.getTile(index);

                if (this.levelUpInProgress)
                {
                    IAnimation anim = animateLevelUp(hub, index);
                    tile.setAnimation(anim);
                    animationMan.add(anim);
                }
                else
                {                    
                    IAnimation anim = animateRemove(hub, tile);
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
                    animationMan.add(animateItemActivation(hub, itemTile));
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
                        .end();
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
        animationMan.add(animateItemSct(hub, pos, deltaScore, color));
                       
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
            
            t.setAnimation(animateItemActivation(hub, t));
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
                anim = animateRocket(hub, tile);
                
            }
            else
            {
                int angle = ++i % 2 == 0 ? 70 : 180 - 70;
                anim = animateRocketJump(hub, tile, angle);              
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
        animationMan.add(animateItemSct(hub, pos, deltaScore, color));

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
            
            t.setAnimation(animateItemActivation(hub, t));
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
                IAnimation anim = animateExplosion(hub, tile);
                tile.setAnimation(anim);
                animationMan.add(anim);
            }
            else
            {           
                IAnimation anim = animateShrapnel(hub, tile, index, bombIndex);
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

    private void removeStars(final Game game, final ManagerHub hub)
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
        boardMan.processStars(starRemovalSet, tileRemovalSet);

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
        animationMan.add(animateItemSct(hub, pos, deltaScore, color));

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
            IAnimation anim = animateJump(hub, tile, angle);

            tile.setAnimation(anim);
            animationMan.add(anim);
        }

        // Clear the star removal set.
        starRemovalSet.clear();

        // Set the flag.
        tileRemovalInProgress = true;
    }
    
    private void shiftGravity(BoardManager boardMan)
    {      
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

    private IAnimation animateLevelUp(final ManagerHub hub, int index)
    {
        final int column = hub.boardMan.asColumn(index);
        final int angle = column >= hub.boardMan.getColumns() / 2 ? 0 : 180;
        final int wait = column >= hub.boardMan.getColumns() / 2
                ? (hub.boardMan.getColumns() - 1 - column) * 100
                : column * 100;

        final Tile tile = hub.boardMan.getTile(index);

        IAnimation move = new MoveAnimation.Builder(tile)
                .wait(wait)
                .duration(hub.settingsMan.getInt(Key.ANIMATION_LEVEL_MOVE_DURATION))
                .theta(angle).speed(hub.settingsMan.getInt(Key.ANIMATION_LEVEL_MOVE_SPEED))
                .gravity(hub.settingsMan.getInt(Key.ANIMATION_LEVEL_MOVE_GRAVITY))
                .end();

        IAnimation fade = new FadeAnimation.Builder(FadeAnimation.Type.OUT, tile)
                .wait(wait)
                .duration(hub.settingsMan.getInt(Key.ANIMATION_LEVEL_FADE_DURATION))
                .end();

        IAnimation meta = new MetaAnimation.Builder()
                .finishRule(MetaAnimation.FinishRule.ALL)
                .add(move).add(fade).end();
        
        return meta;
    }

    private IAnimation animateRemove(final ManagerHub hub, final Tile t)
    {
        IAnimation a1 = new ZoomAnimation.Builder(ZoomAnimation.Type.IN, t)
                .speed(hub.settingsMan.getInt(Key.ANIMATION_LINE_REMOVE_ZOOM_SPEED))
                .end();

        IAnimation a2 = new FadeAnimation.Builder(FadeAnimation.Type.OUT, t)
                .wait(hub.settingsMan.getInt(Key.ANIMATION_LINE_REMOVE_FADE_WAIT))
                .duration(hub.settingsMan.getInt(Key.ANIMATION_LINE_REMOVE_FADE_DURATION))
                .end();

        IAnimation meta = new MetaAnimation.Builder().finishRule(FinishRule.ALL)
                .add(a1).add(a2).end();

        return meta;
    }

    private IAnimation animateItemSct(
            final ManagerHub hub,
            final ImmutablePosition pos,
            final int deltaScore,
            final Color color)
    {
        final ITextLabel label = new LabelBuilder(pos.getX(), pos.getY())
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                .color(color)
                .size(hub.scoreMan.determineFontSize(deltaScore))
                .text(String.valueOf(deltaScore))
                .end();

        IAnimation move = new MoveAnimation.Builder(label)
                .duration(hub.settingsMan.getInt(Key.SCT_SCORE_MOVE_DURATION))
                .speed(hub.settingsMan.getInt(Key.SCT_SCORE_MOVE_SPEED))
                .theta(hub.settingsMan.getInt(Key.SCT_SCORE_MOVE_THETA))
                .end();

        IAnimation fade = new FadeAnimation.Builder(FadeAnimation.Type.OUT, label)
                .wait(hub.settingsMan.getInt(Key.SCT_SCORE_FADE_WAIT))
                .duration(hub.settingsMan.getInt(Key.SCT_SCORE_FADE_DURATION))
                .minOpacity(hub.settingsMan.getInt(Key.SCT_SCORE_FADE_MIN_OPACITY))
                .maxOpacity(hub.settingsMan.getInt(Key.SCT_SCORE_FADE_MAX_OPACITY))
                .end();
        
        move.addAnimationListener(new AnimationAdapter()
        {
            @Override
            public void animationStarted()
            {
                hub.layerMan.add(label, Layer.EFFECT);
            }

            @Override
            public void animationFinished()
            {
                hub.layerMan.remove(label, Layer.EFFECT);
            }
        });

        return new MetaAnimation.Builder()
                .finishRule(MetaAnimation.FinishRule.ALL)
                .add(move).add(fade).end();
    }

    private IAnimation animateItemActivation(final ManagerHub hub, final Tile tile)
    {
        // Sanity check.
        if (hub == null)
            throw new IllegalArgumentException("Hub cannot be null.");

        if (tile == null)
            throw new IllegalArgumentException("Tile cannot be null.");

        // The clone of tile, used to make the effect.
        final Tile clone = TileHelper.cloneTile(tile);

        // Add the clone to the layer man.
        hub.layerMan.add(clone, Layer.EFFECT);

        // Make the animation.
        IAnimation anim1 = new ZoomAnimation.Builder(ZoomAnimation.Type.OUT, clone)
                .minWidth(clone.getWidth())
                .maxWidth(Integer.MAX_VALUE)
                .speed(hub.settingsMan.getInt(Key.ANIMATION_ITEM_ACTIVATE_ZOOM_SPEED))
                .duration(hub.settingsMan.getInt(Key.ANIMATION_ITEM_ACTIVATE_ZOOM_DURATION))
                .end();

        IAnimation anim2 = new FadeAnimation.Builder(FadeAnimation.Type.OUT, clone)
                .wait(hub.settingsMan.getInt(Key.ANIMATION_ITEM_ACTIVATE_FADE_WAIT))
                .duration(hub.settingsMan.getInt(Key.ANIMATION_ITEM_ACTIVATE_FADE_DURATION))
                .end();

        MetaAnimation meta = new MetaAnimation.Builder()
                .add(anim1)
                .add(anim2)
                .end();

        meta.addAnimationListener(new AnimationAdapter()
        {
            @Override
            public void animationFinished()
            { hub.layerMan.remove(clone, Layer.EFFECT); }
        });

        clone.setAnimation(meta);

        return meta;
    }

    private IAnimation animateJump(final ManagerHub hub, final Tile tile, int angle)
    {
        IAnimation move = new MoveAnimation.Builder(tile)
                .duration(hub.settingsMan.getInt(Key.ANIMATION_JUMP_MOVE_DURATION))
                .theta(angle)
                .speed(hub.settingsMan.getInt(Key.ANIMATION_JUMP_MOVE_SPEED))
                .gravity(hub.settingsMan.getInt(Key.ANIMATION_JUMP_MOVE_GRAVITY))
                .end();

        IAnimation fade = new FadeAnimation.Builder(FadeAnimation.Type.OUT, tile)
                .wait(hub.settingsMan.getInt(Key.ANIMATION_JUMP_FADE_WAIT))
                .duration(hub.settingsMan.getInt(Key.ANIMATION_JUMP_MOVE_DURATION))
                .end();

        return new MetaAnimation.Builder()
                .finishRule(MetaAnimation.FinishRule.ALL)
                .add(move).add(fade).end();
    }

    private IAnimation animateRocket(final ManagerHub hub, final Tile tile)
    {
        // Cast it.
        RocketTile rocket = (RocketTile) tile;

        IAnimation move = new MoveAnimation.Builder(rocket)
                .duration(hub.settingsMan.getInt(Key.ANIMATION_ROCKET_MOVE_DURATION))
                .theta(rocket.getDirection().toDegrees())
                .speed(hub.settingsMan.getInt(Key.ANIMATION_ROCKET_MOVE_SPEED))
                .gravity(hub.settingsMan.getInt(Key.ANIMATION_ROCKET_MOVE_GRAVITY))
                .end();

        IAnimation fade = new FadeAnimation.Builder(FadeAnimation.Type.OUT, tile)
                .wait(hub.settingsMan.getInt(Key.ANIMATION_ROCKET_FADE_WAIT))
                .duration(hub.settingsMan.getInt(Key.ANIMATION_ROCKET_FADE_DURATION))
                .end();

        MetaAnimation meta = new MetaAnimation.Builder()
                .finishRule(FinishRule.ALL)
                .add(move).add(fade).end();

        return meta;
    }

    private IAnimation animateRocketJump(final ManagerHub hub, final Tile tile, int angle)
    {
        return animateJump(hub, tile, angle);
    }

    private IAnimation animateExplosion(final ManagerHub hub, final Tile t)
    {
        final GraphicEntity explosion = new GraphicEntity.Builder(
                t.getCenterX() - 1, t.getCenterY() - 1,
                Settings.getSpriteResourcesPath() + "/Explosion.png")
                .end();

        explosion.setWidth(2);
        explosion.setHeight(2);

        // Add the clone to the layer man.
        hub.layerMan.add(explosion, Layer.EFFECT);

        // Make the animation.
        IAnimation boomZoom = new ZoomAnimation.Builder(ZoomAnimation.Type.OUT, explosion)
                .minWidth(2).maxWidth(Integer.MAX_VALUE)
                .speed(hub.settingsMan.getInt(Key.ANIMATION_BOMB_EXPLODE_ZOOM_SPEED))
                .duration(hub.settingsMan.getInt(Key.ANIMATION_BOMB_EXPLODE_ZOOM_DURATION))
                .end();

        IAnimation boomFade = new FadeAnimation.Builder(FadeAnimation.Type.OUT, explosion)
                .wait(hub.settingsMan.getInt(Key.ANIMATION_BOMB_EXPLODE_FADE_WAIT))
                .duration(hub.settingsMan.getInt(Key.ANIMATION_BOMB_EXPLODE_FADE_DURATION))
                .end();

        IAnimation tileFade = new FadeAnimation.Builder(FadeAnimation.Type.OUT, t)
                .wait(hub.settingsMan.getInt(Key.ANIMATION_BOMB_TILE_FADE_WAIT))
                .duration(hub.settingsMan.getInt(Key.ANIMATION_BOMB_TILE_FADE_DURATION))
                .end();

        MetaAnimation meta = new MetaAnimation.Builder()
                .add(boomZoom).add(boomFade).add(tileFade).end();

        meta.addAnimationListener(new AnimationAdapter()
        {
            @Override
            public void animationFinished()
            {
                hub.layerMan.remove(explosion, Layer.EFFECT);
            }
        });

        return meta;
    }

    private IAnimation animateShrapnel(final ManagerHub hub, final Tile tile, int index, int bombIndex)
    {
        IAnimation fade = new FadeAnimation.Builder(FadeAnimation.Type.OUT, tile)
                .wait(hub.settingsMan.getInt(Key.ANIMATION_BOMB_SHRAPNEL_FADE_WAIT))
                .duration(hub.settingsMan.getInt(Key.ANIMATION_BOMB_SHRAPNEL_FADE_DURATION))
                .end();

        int h = hub.boardMan.relativeColumnPosition(index, bombIndex).asInteger();
        int v = hub.boardMan.relativeRowPosition(index, bombIndex).asInteger() * -1;
        int theta = 0;

        if (h == 0)
        {
            theta = 90 * v;
        }
        else if (v == 0)
        {
            theta = h == 1 ? 0 : 180;
        }
        else
        {
            theta = (int) Math.toDegrees(Math.atan(h / v));
            if (h == -1)
            {
                theta -= 180;
            }
        }

        tile.setRotationAnchor(tile.getWidth() / 2, tile.getHeight() / 2);
        IAnimation move = new MoveAnimation.Builder(tile)
                .wait(hub.settingsMan.getInt(Key.ANIMATION_BOMB_SHRAPNEL_MOVE_WAIT))
                .duration(hub.settingsMan.getInt(Key.ANIMATION_BOMB_SHRAPNEL_MOVE_DURATION))
                .speed(hub.settingsMan.getInt(Key.ANIMATION_BOMB_SHRAPNEL_MOVE_SPEED))
                .gravity(hub.settingsMan.getInt(Key.ANIMATION_BOMB_SHRAPNEL_MOVE_GRAVITY))
                .theta(theta)
                .omega(hub.settingsMan.getDouble(Key.ANIMATION_BOMB_SHRAPNEL_MOVE_OMEGA))
                .end();

        IAnimation meta = new MetaAnimation.Builder()
                .finishRule(MetaAnimation.FinishRule.ALL)
                .add(fade).add(move).end();

        return meta;
    }

    public void levelChanged(LevelEvent event)
    {                
        this.activateLevelUp = event.isLevelUp();
    }
   
}
