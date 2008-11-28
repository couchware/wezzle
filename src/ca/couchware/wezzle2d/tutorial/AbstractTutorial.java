/*
 *  Wezzle
 *  Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.tutorial;

import ca.couchware.wezzle2d.*;
import ca.couchware.wezzle2d.Refactorer.RefactorSpeed;
import ca.couchware.wezzle2d.animation.FadeAnimation;
import ca.couchware.wezzle2d.animation.IAnimation;
import ca.couchware.wezzle2d.graphics.EntityGroup;
import ca.couchware.wezzle2d.graphics.IPositionable.Alignment;
import ca.couchware.wezzle2d.manager.BoardManager.Direction;
import ca.couchware.wezzle2d.manager.LayerManager.Layer;
import ca.couchware.wezzle2d.piece.PieceType;
import ca.couchware.wezzle2d.ui.IButton;
import ca.couchware.wezzle2d.ui.ILabel;
import ca.couchware.wezzle2d.ui.SpeechBubble;
import ca.couchware.wezzle2d.ui.SpriteButton;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

/**
 *
 * @author cdmckay
 */
public abstract class AbstractTutorial implements ITutorial 
{
        
    /** Has this tutorial been initalized? Initially false. */
    protected boolean initialized = false;
    
    /** Has the tutorial been completed? Initially false. */
    protected boolean done = false;
    
    /** The refactorer. */
    protected Refactorer refactorer;
    
    /** The name of the tutorial. */
    protected String name;      
    
    /** Is the menu visible? */
    protected boolean menuShown = false;
    
    /** The text that directs the user. */
    protected List<ILabel> labelList;
    
    /** A speech bubble that indicates where the user should press. */
    protected SpeechBubble bubble;       
    
    /** The repeat button. */
    protected IButton repeatButton;
    
    /** The continue button. */
    protected IButton continueButton;
    
    /** The list of rules. */
    private List<Rule> ruleList = new ArrayList<Rule>();
    
    /**
     * Create a new tutorial that is activated when the associated rule is
     * true.
     * 
     * @param rule
     */    
    public AbstractTutorial(Refactorer refactorer, String name) 
    { 
        // Set the refactorer.
        this.refactorer = refactorer;
        
        // Set the tutorial name.
        this.name = name;
    }
    
    public void initialize(final Game game)
    {
        // Set the activataed variable.
        this.initialized = true;
        
        // Save the manager states.
        game.boardMan.saveState();
        game.statMan.saveState();
        game.scoreMan.saveState();
        
        // Turn off the game in progress variable in the world manager.
        game.worldMan.setGameInProgress(false);
              
        // Stop the piece manager from dropping.
        game.pieceMan.setTileDropOnCommit(false);
        
        // Stop the timer.
        game.timerMan.setStopped(true);       
        
        // Create repeat button.
        repeatButton = new SpriteButton.Builder(400, 330)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                .type(SpriteButton.Type.THIN)
                .text("Repeat").offOpacity(70).visible(false).end();
        game.layerMan.add(repeatButton, Layer.EFFECT);
        
         // Create continue button, using the repeat button as a template.
        continueButton = new SpriteButton.Builder((SpriteButton) repeatButton)
                .y(390).text("Continue").end();
        game.layerMan.add(continueButton, Layer.EFFECT);          
    }
       
    protected boolean update(final Game game)
    {        
        // If the move count is not 0, then the tutorial is over.
        if (game.statMan.getMoveCount() != 0 && menuShown == false)
        {               
            // Stop the piece animation.
            game.pieceMan.getPieceGrid().setVisible(false); 
            game.pieceMan.stopAnimation();                                            
            
            // Fade the board out.            
            final EntityGroup e = game.boardMan.getTiles(game.boardMan.getCells() / 2, 
                    game.boardMan.getCells() - 1);  
            
            IAnimation a = new FadeAnimation.Builder(FadeAnimation.Type.OUT, e)
                    .wait(0).duration(500).end();
            
            a.setFinishRunnable(new Runnable()
            {
                public void run()
                { e.setVisible(false); }
            });
            
            game.animationMan.add(a);            
                                    
            // Fade in two new buttons.
            FadeAnimation f;                        
             
            //f = new FadeAnimation(FadeType.IN, 100, 500, repeatButton);
            //f.setMaxOpacity(70);
            f = new FadeAnimation.Builder(FadeAnimation.Type.IN, repeatButton)
                    .wait(100).duration(500).maxOpacity(70).end();            
            game.animationMan.add(f);
                         
            //f = new FadeAnimation(FadeType.IN, 100, 500, continueButton);
            //f.setMaxOpacity(70);
            f = new FadeAnimation.Builder(FadeAnimation.Type.IN, continueButton)
                    .wait(100).duration(500).maxOpacity(70).end();
            game.animationMan.add(f);
            
            // Menu is now shown.
            menuShown = true;                  
            
            return true;
        }
        else if (menuShown == true)
        {
            if (repeatButton.isActivated() == true)
            {
                repeat(game);                
            }
            else if (continueButton.isActivated() == true)
            {
                return false;
            }
        }      
        
        return true;
    }
    
    public void addRule(Rule rule)
    {
        ruleList.add(rule);
    }

    public void removeRule(Rule rule)
    {
        ruleList.remove(rule);
    }
    
    /**
     * Evaluates the tutorial activation rules.  Returns true if they are
     * met, false otherwise.
     * 
     * @param game
     * @return
     */
    public boolean evaluateRules(Game game)
    {
        // Check all the rules.  If any of them are false, return false.
        for (Rule rule : ruleList)
            if (rule.evaluate(game) == false)
                return false;
        
        // If all the rules are true, then return true.        
        return true;
    }
    
    /**
     * Updates the tutorial's internal logic.  If the tutorial is activated,
     * it will continue with the tutorial.  If the tutorial is not activated,
     * it will evaluate the activation rules to see if it should be activated.
     * 
     * @param game
     */
    public void updateLogic(Game game)
    {                
        // If we're activated, run the tutorial logic.
        if (initialized == true && update(game) == false)
        {
            initialized = false;
            finish(game);
            done = true;
        }
    }       
    
    protected void finish(final Game game)
    {   
        // Load the score managers state.
        game.boardMan.loadState();
        game.statMan.loadState();
        game.scoreMan.loadState();                                                
        
        // Remove the repeat button.
        repeatButton.setVisible(false);
        game.layerMan.remove(repeatButton, Layer.EFFECT);
        repeatButton = null;
        
        // Remove the continue button.
        continueButton.setVisible(false);
        game.layerMan.remove(continueButton, Layer.EFFECT);
        continueButton = null;
        
        // Remove the speech bubble.
        game.layerMan.remove(bubble, Layer.EFFECT);
        bubble = null;
        
        // Remove the text label.
        for (ILabel label : labelList)
            game.layerMan.remove(label, Layer.EFFECT);       
        labelList = null;
        
        // Remove the restriction board.
        game.pieceMan.clearRestrictionBoard();
        
        // Turn on tile drops.
        game.pieceMan.setTileDropOnCommit(true);   
        
        // Start the timer.        
        game.timerMan.setStopped(false);  
        
        // Reset the refactor speed.
        refactorer.setRefactorSpeed(RefactorSpeed.NORMAL);
    }
        
    protected void repeat(final Game game)
    {
        // Make sure to reset the gravity.
        game.boardMan.setGravity(EnumSet.of(Direction.DOWN, Direction.LEFT));
        
        // Reset move counter.
        game.statMan.resetMoveCount();      
        
        // Reset the scores.
        game.scoreMan.resetScore();
        
        // Create the fake board.
        createBoard(game);
        
        // Fade board in.
        final EntityGroup e = game.boardMan.getTiles(game.boardMan.getCells() / 2, 
                game.boardMan.getCells() - 1);        
        
        e.setVisible(false);
        
        IAnimation a = new FadeAnimation.Builder(FadeAnimation.Type.IN, e)
                .wait(0).duration(300).end();               
        
        game.animationMan.add(a);
        
        // Change the piece to the dot.
        game.pieceMan.loadPiece(PieceType.DOT);    
        
        // Make sure buttons aren't visible.
        repeatButton.setVisible(false);
        if (repeatButton.isActivated() == true)
            repeatButton.setActivated(false);
        
        continueButton.setVisible(false);
        if (continueButton.isActivated() == true)
            continueButton.setActivated(false);
        
        // Menu is not shown.
        menuShown = false;
        
        // Make sure the bubble opacity is full.
        bubble.setOpacity(100);            
        
        // Piece is visible.
        game.pieceMan.getPieceGrid().setVisible(true);
    }    
    
    protected abstract void createBoard(final Game game);
    
//    /**
//     * The tutorial logic goes here.
//     * 
//     * @param game
//     * @return True if the tutorial is still running, false otherwise.
//     */
//    protected abstract boolean update(Game game);

//    /**
//     * Is run when the tutorial is completed.  Typically deals with resetting 
//     * the managers to their previous states before the tutorial started.
//     * 
//     * @param game
//     */
//    protected abstract void finish(Game game);
    
//    /**
//     * Restarts the tutorial at the beginning.  This method can be overwritten
//     * to optimize it.
//     * 
//     * @return
//     */
//    protected void repeat(Game game)
//    {
//        finish(game);
//        initialize(game);
//    }              
    
    public boolean isInitialized()
    {
        return initialized;
    }   

    public boolean isDone()
    {
        return done;
    }

    public String getName()
    {
        return name;
    }        
    
}
