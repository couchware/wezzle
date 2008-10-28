/*
 *  Wezzle
 *  Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d;


import ca.couchware.wezzle2d.Refactorer.RefactorType;
import ca.couchware.wezzle2d.ResourceFactory.LabelBuilder;
import ca.couchware.wezzle2d.animation.ExplosionAnimation;
import ca.couchware.wezzle2d.animation.FadeAnimation;
import ca.couchware.wezzle2d.animation.IAnimation;
import ca.couchware.wezzle2d.animation.JiggleAnimation;
import ca.couchware.wezzle2d.animation.MoveAnimation;
import ca.couchware.wezzle2d.animation.ZoomAnimation;
import ca.couchware.wezzle2d.audio.Sound;
import ca.couchware.wezzle2d.event.IListenerComponent;
import ca.couchware.wezzle2d.event.ScoreEvent;
import java.util.HashSet;
import java.util.Set;
import ca.couchware.wezzle2d.manager.*;
import ca.couchware.wezzle2d.manager.BoardManager.Direction;
import ca.couchware.wezzle2d.manager.ScoreManager.ScoreType;
import ca.couchware.wezzle2d.ui.ILabel;
import ca.couchware.wezzle2d.util.ImmutablePosition;
import java.util.EnumSet;
import ca.couchware.wezzle2d.graphics.IPositionable.Alignment;
import ca.couchware.wezzle2d.manager.LayerManager.Layer;
import ca.couchware.wezzle2d.tile.BombTileEntity;
import ca.couchware.wezzle2d.tile.RocketTileEntity;
import ca.couchware.wezzle2d.tile.StarTileEntity;
import ca.couchware.wezzle2d.tile.TileEntity;
import java.util.Iterator;


/**
 * A class for handling tile removing from the board.  This is a singleton class.
 * @author kgrad
 */
public class TileRemover 
{
     /**
     * If true, a line removal will be activated next loop.
     */
    private boolean activateLineRemoval = false;       
    
    /**
     * The last line that was matched.  This is used by the "star" item
     * to determine whether a bomb is "in the wild" (i.e. should be removed)
     * or part of the line (i.e. should be left to explode).
     */
    private Set<Integer> lastMatchSet;
    
    /**
     * If true, a line removal is in progress.
     */
    private boolean tileRemovalInProgress = false;
    
    /**
     * The set of tile indices that will be removed.
     */
    private Set<Integer> tileRemovalSet;
    
    /**
     * If true, uses jump animation instead of zoom.
     */
    private boolean tileRemovalUseJumpAnimation = false;
    
    /**
     * If true, award no points for this tile removal.
     */
    private boolean tileRemovalNoScore = false;
    
    /**
     * If true, do not activate items on this removal.
     */
    private boolean tileRemovalNoItems = false;
    
    /**
     * If true, a bomb removal will be activated next loop.
     */
    private boolean activateBombRemoval = false;
    
    /**
     * The set of bomb tile indices that will be removed.
     */
    private Set<Integer> bombRemovalSet;       
    
    /**
     * If true, a star removal will be activated next loop.
     */
    private boolean activateStarRemoval = false;
    
    /**
     * The set of star tile indices that will be removed.
     */
    private Set<Integer> starRemovalSet;     
    
    /**
     * If true, a rocket removal will be activated next loop.
     */
    private boolean activateRocketRemoval = false;
    
    /**
     * The set of star tile indices that will be removed.
     */
    private Set<Integer> rocketRemovalSet;           
    

    
    
    /** 
     * The single instance of this class to ever exist. 
     */
    private static final TileRemover single = new TileRemover();
    
  
    /**
     * The private constructor.
     */
    private TileRemover()
    {
      
    }
    
    /**
     * Retrieve the single instance of this class.
     * 
     * @return The single instance of this class.
     */
    public static TileRemover get()
    {
            return single;
    }

    // Getters.

    public boolean isActivateBombRemoval() {
        return activateBombRemoval;
    }

    public boolean isActivateLineRemoval() {
        return activateLineRemoval;
    }

    public boolean isActivateRocketRemoval() {
        return activateRocketRemoval;
    }
     public boolean isActivateStarRemoval() {
        return activateStarRemoval;
    }

    public Set<Integer> getBombRemovalSet() {
        return bombRemovalSet;
    }

    public Set<Integer> getLastMatchSet() {
        return lastMatchSet;
    }

    public Set<Integer> getRocketRemovalSet() {
        return rocketRemovalSet;
    }

    public static TileRemover getSingle() {
        return single;
    }

    public Set<Integer> getStarRemovalSet() {
        return starRemovalSet;
    }

    public boolean isTileRemovalInProgress() {
        return tileRemovalInProgress;
    }

    public boolean isTileRemovalNoItems() {
        return tileRemovalNoItems;
    }

    public boolean isTileRemovalNoScore() {
        return tileRemovalNoScore;
    }

    public boolean isTileRemovalUseJumpAnimation() {
        return tileRemovalUseJumpAnimation;
    }
    
    // Setters.

    public void setActivateBombRemoval(boolean activateBombRemoval) {
        this.activateBombRemoval = activateBombRemoval;
    }

    public void setActivateLineRemoval(boolean activateLineRemoval) {
        this.activateLineRemoval = activateLineRemoval;
    }

    public void setActivateRocketRemoval(boolean activateRocketRemoval) {
        this.activateRocketRemoval = activateRocketRemoval;
    }

    public void setActivateStarRemoval(boolean activateStarRemoval) {
        this.activateStarRemoval = activateStarRemoval;
    }

    public void setTileRemovalInProgress(boolean tileRemovalInProgress) {
        this.tileRemovalInProgress = tileRemovalInProgress;
    }

    public void setTileRemovalNoItems(boolean tileRemovalNoItems) {
        this.tileRemovalNoItems = tileRemovalNoItems;
    }

    public void setTileRemovalNoScore(boolean tileRemovalNoScore) {
        this.tileRemovalNoScore = tileRemovalNoScore;
    }

    public void setTileRemovalUseJumpAnimation(boolean tileRemovalUseJumpAnimation) {
        this.tileRemovalUseJumpAnimation = tileRemovalUseJumpAnimation;
    }


  
    
    /**
     * The main chunk of the remover. This is where the logic occurs.
     * @param game The game.
     */
    public void updateLogic(final Game game)
    {
         // See if it just finished.
        if (Refactorer.get().isFinished() == true)
        {
            TileRemover.get().refactorFinished(game);
          
        } // end if

        // If a line removal was activated.
        if (TileRemover.get().isActivateLineRemoval() == true)
        {                
           TileRemover.get().removeLines(game);
        }
        
        // If the star removal is in progress.
        if (TileRemover.get().isActivateRocketRemoval() == true)
        {
           TileRemover.get().removeRockets(game);
        }

        // If the star removal is in progress.
        if (TileRemover.get().isActivateStarRemoval() == true)
        {
            TileRemover.get().removeStars(game);
        }

        // If a bomb removal is in progress.
        if (TileRemover.get().isActivateBombRemoval() == true)
        {
            TileRemover.get().removeBombs(game);
        }
        
        // If a line removal is in progress.
        if (TileRemover.get().isTileRemovalInProgress() == true)
        {
            TileRemover.get().removalInProgress(game);
        }

    }

    
    
    void clearTileRemovalSet() 
    {
       tileRemovalSet.clear();
    }

    void initialize() 
    {
         // Initialize the last line match.
        lastMatchSet = new HashSet<Integer>();
        
        // Initialize line index set.
        tileRemovalSet = new HashSet<Integer>();
        
        // Initialize bomb index set.
        bombRemovalSet = new HashSet<Integer>();
        
        // Initialize star index set.
        starRemovalSet = new HashSet<Integer>();
        
        // Initialize rocket index set.
        rocketRemovalSet = new HashSet<Integer>();       
    }

    boolean isTileRemoving() 
    {
         return this.activateLineRemoval               
               || this.activateBombRemoval
               || this.activateStarRemoval
               || this.activateRocketRemoval
               || this.tileRemovalInProgress;
    }

    void levelUp(final Game game) 
    {
        BoardManager boardMan = game.boardMan;
        
        int j;
        if (boardMan.getGravity().contains(Direction.UP))                        
            j = 0;
        else
            j = boardMan.getRows() - 1;

        for (int i = 0; i < boardMan.getColumns(); i++)
        {                         
            int index = i + (j * boardMan.getColumns());
            if (boardMan.getTile(index) != null)
                tileRemovalSet.add(new Integer(index));
        }      
    }

    void refactorFinished(final Game game) 
    {
        StatManager statMan = game.statMan;
        BoardManager boardMan = game.boardMan;
        PieceManager pieceMan = game.pieceMan;
        TimerManager timerMan = game.timerMan;
          // Look for matches.
            tileRemovalSet.clear();

            statMan.incrementCycleLineCount(
                    boardMan.findXMatch(tileRemovalSet));

            statMan.incrementCycleLineCount(
                    boardMan.findYMatch(tileRemovalSet));

            // Copy the match into the last line match holder.
            lastMatchSet.clear();
            lastMatchSet.addAll(tileRemovalSet);

            // If there are matches, score them, remove 
            // them and then refactor again.
            if (tileRemovalSet.size() > 0)
            {
                // Activate the line removal.
                activateLineRemoval = true;
            }
            else
            {
                // Make sure the tiles are not still dropping.
                if (pieceMan.isTileDropInProgress() == false)
                {
                    pieceMan.loadPiece();
                    pieceMan.getPieceGrid().setVisible(true);

                    // Unpause the timer.
                    timerMan.resetTimer();
                    timerMan.setPaused(false);

                    // Reset the mouse.
                    pieceMan.clearMouseButtonSet();
                }
            }
    }

    void removalInProgress(final Game game) 
    {
        BoardManager boardMan = game.boardMan;
        // Animation completed flag.
            boolean animationInProgress = false;

            // Check to see if they're all done.
            for (Iterator it = tileRemovalSet.iterator(); it.hasNext(); )
            {
                if (boardMan.getTile((Integer) it.next()).getAnimation()
                        .isFinished() == false)
                {
                    animationInProgress = true;
                }
            }

            if (animationInProgress == false)
            {
                // Remove the tiles from the board.
                boardMan.removeTiles(tileRemovalSet);

                // Bomb removal is completed.
                tileRemovalInProgress = false;

                // See if there are any bombs in the bomb set.
                // If there are, activate the bomb removal.
                if (rocketRemovalSet.size() > 0)
                    activateRocketRemoval = true;
                else if (starRemovalSet.size() > 0)
                    activateStarRemoval = true;
                else if (bombRemovalSet.size() > 0)
                    activateBombRemoval = true;
                // Otherwise, start a new refactor.
                else                
                    Refactorer.get().startRefactor(RefactorType.NORMAL);
            }  
    }

    void removeBombs(final Game game) 
    {
         StatManager statMan = game.statMan;
        BoardManager boardMan = game.boardMan;
        AnimationManager animationMan = game.animationMan;
        ScoreManager scoreMan = game.scoreMan;
        TutorialManager tutorialMan = game.tutorialMan;
        ListenerManager listenerMan = game.listenerMan;
        SoundManager soundMan = game.soundMan;
 
        // Clear the flag.
            activateBombRemoval = false;

            // Increment cascade.
            statMan.incrementChainCount();

            // Used below.
            int deltaScore = 0;

            // Also used below.
            IAnimation a1, a2;

            // Get the tiles the bombs would affect.
            boardMan.processBombs(bombRemovalSet, tileRemovalSet);
            deltaScore = scoreMan.calculateLineScore(
                    tileRemovalSet, 
                    ScoreType.BOMB, 
                    statMan.getChainCount());

              // Fire a score event.
              if (tutorialMan.isTutorialInProgress() == true)
              {
                  listenerMan.notifyScoreListener(new ScoreEvent(deltaScore, this),
                            IListenerComponent.GameType.TUTORIAL);
              }
              else
              {
                listenerMan.notifyScoreListener(new ScoreEvent(deltaScore, this),
                            IListenerComponent.GameType.GAME);
              }


            // Show the SCT.
            ImmutablePosition p = boardMan.determineCenterPoint(tileRemovalSet);

            final ILabel label = new LabelBuilder(p.getX(), p.getY())
                    .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                    .color(game.SCORE_BOMB_COLOR)
                    .size(scoreMan.determineFontSize(deltaScore))
                    .text(String.valueOf(deltaScore)).end();

            a1 = new FadeAnimation.Builder(FadeAnimation.Type.OUT, label).end();
            a2 = new MoveAnimation.Builder(label)
                    .duration(1150).v(0.03).theta(90).end();

            a2.setStartRunnable(new Runnable()
            {
                public void run()
                { game.layerMan.add(label, Layer.EFFECT); }
            });

            a2.setFinishRunnable(new Runnable()
            {
                public void run()
                { game.layerMan.remove(label, Layer.EFFECT); }
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
            Set<Integer> newBombRemovalSet = new HashSet<Integer>();
            boardMan.scanFor(BombTileEntity.class, tileRemovalSet,
                    newBombRemovalSet);                                                
            newBombRemovalSet.removeAll(bombRemovalSet);

            // Remove all new bombs from the tile removal set.
            // They will be processed separately.
            tileRemovalSet.removeAll(newBombRemovalSet);

            // Find all rockets.                
            boardMan.scanFor(RocketTileEntity.class, tileRemovalSet,
                    rocketRemovalSet);                

            // Remove all rockets from the tile removal set.
            // They will be processed separately.
            tileRemovalSet.removeAll(rocketRemovalSet);

            // Start the line removal animations.
            for (Iterator it = tileRemovalSet.iterator(); it.hasNext(); )
            {
                TileEntity t = boardMan.getTile((Integer) it.next());

                if (t instanceof BombTileEntity)                    
                {
                    t.setAnimation(new ExplosionAnimation(t, game.layerMan));                                            
                    animationMan.add(t.getAnimation());
                }                    
                else
                {          
                    a1 = new JiggleAnimation(600, 50, t);
                    //a2 = new FadeAnimation(FadeType.OUT, 0, 600, t);                                        
                    a2 = new FadeAnimation.Builder(FadeAnimation.Type.OUT, t)
                            .wait(0).duration(600).end();

                    t.setAnimation(a1);
                    animationMan.add(a1);                            
                    animationMan.add(a2);      
                    a1 = null;
                    a2 = null;                                               
                }
            }

            // If other bombs were hit, they will be dealt with in another
            // bomb removal cycle.
            bombRemovalSet = newBombRemovalSet;

            // Set the flag.
            tileRemovalInProgress = true;
    }

    void removeLines(final Game game) 
    {
        StatManager statMan = game.statMan;
        BoardManager boardMan = game.boardMan;
        AnimationManager animationMan = game.animationMan;
        ScoreManager scoreMan = game.scoreMan;
        TutorialManager tutorialMan = game.tutorialMan;
        ListenerManager listenerMan = game.listenerMan;
        SoundManager soundMan = game.soundMan;
        
        // Clear flag.
            activateLineRemoval = false;

            // Increment cascade.
            statMan.incrementChainCount();

            // Calculate score, unless no-score flag is set.
            if (tileRemovalNoScore == false)
            {
                final int deltaScore = scoreMan.calculateLineScore(
                        tileRemovalSet, 
                        ScoreType.LINE,
                        statMan.getChainCount());                               

                // Fire a score event.
                if (tutorialMan.isTutorialInProgress() == true)
                {
                    listenerMan.notifyScoreListener(new ScoreEvent(deltaScore, this),
                            IListenerComponent.GameType.TUTORIAL);
                }
                else
                {
                    listenerMan.notifyScoreListener(new ScoreEvent(deltaScore, this),
                            IListenerComponent.GameType.GAME);
                }

                // Show the SCT.
                ImmutablePosition p = boardMan.determineCenterPoint(tileRemovalSet);

                final ILabel label = new LabelBuilder(p.getX(), p.getY())
                        .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                        .color(game.SCORE_LINE_COLOR)
                        .size(scoreMan.determineFontSize(deltaScore))
                        .text(String.valueOf(deltaScore)).end();

                IAnimation a1 = new FadeAnimation.Builder(FadeAnimation.Type.OUT, label).end();
                //IAnimation a2 = new FloatAnimation(0, -1, layerMan, label);                    
                IAnimation a2 = new MoveAnimation.Builder(label)
                        .duration(1150).v(0.03).theta(90).end();

                a2.setStartRunnable(new Runnable()
                {
                    public void run()
                    { game.layerMan.add(label, Layer.EFFECT); }
                });

                a2.setFinishRunnable(new Runnable()
                {
                    public void run()
                    { game.layerMan.remove(label, Layer.EFFECT); }
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
                tileRemovalNoScore = false;
            }

            // Play the sound.
            soundMan.play(Sound.LINE);

            // Make sure bombs aren't removed (they get removed
            // in a different step).  However, if the no-items
            // flag is set, then ignore bombs.
            if (tileRemovalNoItems == false)
            {
                //Set<Integer> allSet = new HashSet<Integer>();

                bombRemovalSet.clear();
                boardMan.scanFor(BombTileEntity.class, tileRemovalSet, 
                        bombRemovalSet);

                starRemovalSet.clear();
                boardMan.scanFor(StarTileEntity.class, tileRemovalSet, 
                        starRemovalSet);

                rocketRemovalSet.clear();
                boardMan.scanFor(RocketTileEntity.class, tileRemovalSet, 
                        rocketRemovalSet);                                       

                tileRemovalSet.removeAll(bombRemovalSet);
                tileRemovalSet.removeAll(starRemovalSet);
                tileRemovalSet.removeAll(rocketRemovalSet);                                            
            }
            else
            {
                // Turn off the flag now that it has been used.
                tileRemovalNoItems = false;
            }

            // Start the line removal animations if there are any
            // non-bomb tiles.
            if (tileRemovalSet.size() > 0)
            {
                int i = 0;
                for (Iterator it = tileRemovalSet.iterator(); it.hasNext(); )
                {
                    TileEntity t = boardMan.getTile((Integer) it.next());

                    if (tileRemovalUseJumpAnimation == true)
                    {
                        i++;
                        int angle = i % 2 == 0 ? 70 : 180 - 70;                             

                        // Bring this tile to the top.
                        game.layerMan.toFront(t, Layer.TILE);

                        IAnimation a1 = new MoveAnimation.Builder(t)
                                .duration(750).theta(angle).v(0.3)
                                .g(0.001).end();                                    
                        IAnimation a2 = new FadeAnimation.Builder(FadeAnimation.Type.OUT, t)
                                .wait(0).duration(750).end();
                        t.setAnimation(a1);
                        animationMan.add(a1);                            
                        animationMan.add(a2);      
                        a1 = null;
                        a2 = null;
                    }
                    else                        
                    {
                        t.setAnimation(new ZoomAnimation.Builder(ZoomAnimation.Type.IN, t)
                                .v(0.05).end());
                        animationMan.add(t.getAnimation());
                    }
                }

                // Clear the animation flag.
                tileRemovalUseJumpAnimation = false;

                // Set the flag.
                tileRemovalInProgress = true;
            }
            // Otherwise, start the bomb processing.
            else
            {
                //activateBombRemoval = true;
                activateStarRemoval = true;
            }
    }

    void removeRockets(final Game game) 
    {
        StatManager statMan = game.statMan;
        BoardManager boardMan = game.boardMan;
        AnimationManager animationMan = game.animationMan;
        ScoreManager scoreMan = game.scoreMan;
        TutorialManager tutorialMan = game.tutorialMan;
        ListenerManager listenerMan = game.listenerMan;
        SoundManager soundMan = game.soundMan;
         // Clear the flag.
            activateRocketRemoval = false;

            // Increment cascade.
            statMan.incrementChainCount();

            // Used below.
            int deltaScore = 0;

            // Also used below.
            IAnimation a1, a2;

            // Get the tiles the bombs would affect.
            boardMan.processRockets(rocketRemovalSet, tileRemovalSet);

            for (Integer index : lastMatchSet)
            {
                if (boardMan.getTile(index) == null)
                    continue;

                if (boardMan.getTile(index).getClass() 
                    != RocketTileEntity.class)
                {
                    tileRemovalSet.remove(index);
                }
            }       

            deltaScore = scoreMan.calculateLineScore(
                    tileRemovalSet, 
                    ScoreType.STAR, 
                    statMan.getChainCount());

              // Fire a score event.
            if (tutorialMan.isTutorialInProgress() == true)
            {
                listenerMan.notifyScoreListener(new ScoreEvent(deltaScore, this),
                            IListenerComponent.GameType.TUTORIAL);
            }
            else
            {
                listenerMan.notifyScoreListener(new ScoreEvent(deltaScore, this),
                            IListenerComponent.GameType.GAME);
            }


            // Show the SCT.
            ImmutablePosition p = boardMan.determineCenterPoint(tileRemovalSet);

            final ILabel label = new LabelBuilder(p.getX(), p.getY())
                    .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                    .color(game.SCORE_BOMB_COLOR)
                    .size(scoreMan.determineFontSize(deltaScore))
                    .text(String.valueOf(deltaScore)).end();                        

            //a1 = new FadeAnimation(FadeType.OUT, label);
            a1 = new FadeAnimation.Builder(FadeAnimation.Type.OUT, label).end();
            //a2 = new FloatAnimation(0, -1, layerMan, label);    
            a2 = new MoveAnimation.Builder(label)
                        .duration(1150).v(0.03).theta(90).end();

            a2.setStartRunnable(new Runnable()
            {
                public void run()
                { game.layerMan.add(label, Layer.EFFECT); }
            });

            a2.setFinishRunnable(new Runnable()
            {
                public void run()
                { game.layerMan.remove(label, Layer.EFFECT); }
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
            Set<Integer> newRocketRemovalSet = new HashSet<Integer>();
            boardMan.scanFor(RocketTileEntity.class, tileRemovalSet,
                    newRocketRemovalSet);                                                
            newRocketRemovalSet.removeAll(rocketRemovalSet);

            // Remove all new rockets from the tile removal set.
            // They will be processed separately.
            tileRemovalSet.removeAll(newRocketRemovalSet);

            // Find all the bombs.
            boardMan.scanFor(BombTileEntity.class, tileRemovalSet,
                    bombRemovalSet);

            // Remove all bombs from the tile removal set.
            // They will be processed separately.
            tileRemovalSet.removeAll(bombRemovalSet);

            // Start the line removal animations.
            int i = 0;
            for (Iterator it = tileRemovalSet.iterator(); it.hasNext(); )
            {
                TileEntity t = boardMan.getTile((Integer) it.next());

                // Bring the tile to the front.
                game.layerMan.toFront(t, Layer.TILE);

                if (t.getClass() == RocketTileEntity.class)
                {
                    // Cast it.
                    RocketTileEntity r = (RocketTileEntity) t;                                                                                           

                    //a1 = new JumpAnimation(0.3, r.getDirection() + 90, 0, 750, r);
                    //a2 = new FadeAnimation(FadeType.OUT, 0, 750, t); 
                    a1 = new MoveAnimation.Builder(r).duration(750)
                            .theta(r.getDirection().toDegrees())
                            .v(0.3).g(0).end();
                    a2 = new FadeAnimation.Builder(FadeAnimation.Type.OUT, t)
                            .wait(0).duration(750).end();
                    t.setAnimation(a1);
                    animationMan.add(a1);                            
                    animationMan.add(a2);      
                    a1 = null;
                    a2 = null;
                }
                else
                {
                    i++;
                    int angle = i % 2 == 0 ? 70 : 180 - 70;                        
                    //int angle = 360 - 180 + 70;
                    a1 = new MoveAnimation.Builder(t).duration(750)
                            .theta(angle).v(0.3).g(0.001).end();     
                    //a2 = new FadeAnimation(FadeType.OUT, 0, 750, t);                                        
                    a2 = new FadeAnimation.Builder(FadeAnimation.Type.OUT, t)
                            .wait(0).duration(750).end();
                    t.setAnimation(a1);
                    animationMan.add(a1);                            
                    animationMan.add(a2);      
                    a1 = null;
                    a2 = null;
                }                    
            }

            // If other bombs were hit, they will be dealt with in another
            // bomb removal cycle.
            rocketRemovalSet = newRocketRemovalSet;

            // Set the flag.
            tileRemovalInProgress = true;
    }

    void removeStars(final Game game) 
    {
        StatManager statMan = game.statMan;
        BoardManager boardMan = game.boardMan;
        AnimationManager animationMan = game.animationMan;
        ScoreManager scoreMan = game.scoreMan;
        TutorialManager tutorialMan = game.tutorialMan;
        ListenerManager listenerMan = game.listenerMan;
        SoundManager soundMan = game.soundMan;
      // Clear the flag.
            activateStarRemoval = false;

            // Increment cascade.
            statMan.incrementChainCount();

            // Used below.
            int deltaScore = 0;

            // Also used below.
            IAnimation a1, a2;

            // Get the tiles the bombs would affect.
            boardMan.processStars(starRemovalSet, tileRemovalSet);

            for (Integer index : lastMatchSet)
            {
                if (boardMan.getTile(index) == null)
                    continue;

                if (boardMan.getTile(index).getClass() 
                    != StarTileEntity.class)
                {
                    tileRemovalSet.remove(index);
                }
            }       

            deltaScore = scoreMan.calculateLineScore(
                    tileRemovalSet, 
                    ScoreType.STAR, 
                    statMan.getChainCount());

              // Fire a score event.
            if (tutorialMan.isTutorialInProgress() == true)
            {
                listenerMan.notifyScoreListener(new ScoreEvent(deltaScore, this),
                            IListenerComponent.GameType.TUTORIAL);
            }
            else
            {
                listenerMan.notifyScoreListener(new ScoreEvent(deltaScore, this),
                            IListenerComponent.GameType.GAME);
            }


            // Show the SCT.
            ImmutablePosition p = boardMan.determineCenterPoint(tileRemovalSet);

            final ILabel label = new LabelBuilder(p.getX(), p.getY())
                    .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                    .color(game.SCORE_BOMB_COLOR)
                    .size(scoreMan.determineFontSize(deltaScore))
                    .text(String.valueOf(deltaScore)).end();                        

            a1 = new FadeAnimation.Builder(FadeAnimation.Type.OUT, label).end();
            //a2 = new FloatAnimation(0, -1, layerMan, label);                    
            a2 = new MoveAnimation.Builder(label)
                    .duration(1150).v(0.03).theta(90).end();

            a2.setStartRunnable(new Runnable()
            {
                public void run()
                { game.layerMan.add(label, Layer.EFFECT); }
            });

            a2.setFinishRunnable(new Runnable()
            {
                public void run()
                { game.layerMan.remove(label, Layer.EFFECT); }
            });

            animationMan.add(a1);
            animationMan.add(a2);
            a1 = null;
            a2 = null;

            // Release references.
            p = null;

            // Play the sound.
            soundMan.play(Sound.STAR);

            // Start the line removal animations.
            int i = 0;
            for (Iterator it = tileRemovalSet.iterator(); it.hasNext(); )
            {
                TileEntity t = boardMan.getTile((Integer) it.next());

                // Bring the entity to the front.
                game.layerMan.toFront(t, Layer.TILE);

                // Increment counter.
                i++;

                int angle = i % 2 == 0 ? 70 : 180 - 70;                                                                
                //a1 = new JumpAnimation(0.3, angle, 0.001, 750, t);
                //a2 = new FadeAnimation(FadeType.OUT, 0, 750, t);                                        
                a1 = new MoveAnimation.Builder(t).duration(750)
                        .theta(angle).v(0.3).g(0.001).end();                            
                a2 = new FadeAnimation.Builder(FadeAnimation.Type.OUT, t)
                        .wait(0).duration(750).end();
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
    
  
    
}