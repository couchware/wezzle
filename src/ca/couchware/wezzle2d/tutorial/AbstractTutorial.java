/*
 *  Wezzle
 *  Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.tutorial;

import ca.couchware.wezzle2d.Game;
import ca.couchware.wezzle2d.ManagerHub;
import ca.couchware.wezzle2d.Refactorer;
import ca.couchware.wezzle2d.Refactorer.RefactorSpeed;
import ca.couchware.wezzle2d.Rule;
import ca.couchware.wezzle2d.animation.AnimationAdapter;
import ca.couchware.wezzle2d.animation.FadeAnimation;
import ca.couchware.wezzle2d.animation.IAnimation;
import ca.couchware.wezzle2d.graphics.EntityGroup;
import ca.couchware.wezzle2d.graphics.IPositionable.Alignment;
import ca.couchware.wezzle2d.manager.AnimationManager;
import ca.couchware.wezzle2d.manager.BoardManager;
import ca.couchware.wezzle2d.manager.BoardManager.Direction;
import ca.couchware.wezzle2d.manager.LayerManager;
import ca.couchware.wezzle2d.manager.LayerManager.Layer;
import ca.couchware.wezzle2d.manager.PieceManager;
import ca.couchware.wezzle2d.manager.ScoreManager;
import ca.couchware.wezzle2d.manager.Settings.Key;
import ca.couchware.wezzle2d.manager.SettingsManager;
import ca.couchware.wezzle2d.manager.StatManager;
import ca.couchware.wezzle2d.manager.TimerManager;
import ca.couchware.wezzle2d.piece.Piece;
import ca.couchware.wezzle2d.piece.PieceType;
import ca.couchware.wezzle2d.ui.IButton;
import ca.couchware.wezzle2d.ui.ITextLabel;
import ca.couchware.wezzle2d.ui.SpeechBubble;
import ca.couchware.wezzle2d.ui.Button;
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
    protected List<ITextLabel> labelList;
    
    /** A speech bubble that indicates where the user should press. */
    protected SpeechBubble bubble;       
    
    /** The repeat button. */
    protected IButton repeatButton;
    
    /** The continue button. */
    protected IButton continueButton;

    /** The piece the user started the tutorial with. */
    protected Piece pieceBeforeInitialization;
    
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
    
    public void initialize(final Game game, final ManagerHub hub)
    {
        // Set the activataed variable.
        this.initialized = true;
        
        // Make convenience variables for the managers used.
        final BoardManager boardMan = hub.boardMan;
        final LayerManager layerMan = hub.layerMan;
        final ScoreManager scoreMan = hub.scoreMan;
        final StatManager statMan = hub.statMan;        
        final TimerManager timerMan = hub.timerMan;

        // Save the manager states.
        boardMan.saveState();
        statMan.saveState();
        scoreMan.saveState();
             
        // Stop the piece manager from dropping.
        game.getTileDropper().setDropOnCommit(false);
        
        // Stop the timer.
        timerMan.setStopped(true);

        // Hide the piece preview.
        game.getUI().setTraditionalPiecePreviewVisible(false);
        game.getUI().setOverlayPiecePreviewVisible(false);

        // Remember the piece we entered with.
        this.pieceBeforeInitialization = hub.pieceMan.getPiece();

        // Create repeat button.
        repeatButton = new Button.Builder(400, 330)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))                
                .text("Repeat").visible(false).build();
        layerMan.add(repeatButton, Layer.EFFECT);
        
         // Create continue button, using the repeat button as a template.
        continueButton = new Button.Builder((Button) repeatButton)
                .y(390).text("Continue").build();
        layerMan.add(continueButton, Layer.EFFECT);          
    }
       
    protected boolean update(final Game game, final ManagerHub hub)
    {
        // Make convenience variables for the managers used.
        final AnimationManager animationMan = hub.animationMan;
        final BoardManager boardMan = hub.boardMan;
        final PieceManager pieceMan = hub.pieceMan;
        final StatManager statMan = hub.statMan;

        // If the move count is not 0, then the tutorial is over.
        if (statMan.getMoveCount() != 0 && menuShown == false)
        {               
            // Stop the piece animation.
            pieceMan.hidePieceGrid();
            pieceMan.stopAnimation();                                            
            
            // Lock the whole board.
            pieceMan.clearRestrictionBoard();
            pieceMan.reverseRestrictionBoard();
            
            // Fade the board out.            
            final EntityGroup entityGroup = boardMan.getTileRange(
                    boardMan.getNumberOfCells() / 2,
                    boardMan.getNumberOfCells() - 1);
            
            IAnimation anim = new FadeAnimation.Builder(FadeAnimation.Type.OUT, entityGroup)
                    .wait(0).duration(500).build();
            
            anim.addAnimationListener(new AnimationAdapter()
            {                
                @Override
                public void animationFinished()
                { entityGroup.setVisible(false); }
            });           
            
            animationMan.add(anim);            
                                    
            // Fade in two new buttons.
            FadeAnimation fade;                        
             
            //f = new FadeAnimation(FadeType.IN, 100, 500, repeatButton);
            //f.setMaxOpacity(70);
            fade = new FadeAnimation.Builder(FadeAnimation.Type.IN, repeatButton)
                    .wait(100).duration(500).maxOpacity(70).build();
            animationMan.add(fade);
                         
            //f = new FadeAnimation(FadeType.IN, 100, 500, continueButton);
            //f.setMaxOpacity(70);
            fade = new FadeAnimation.Builder(FadeAnimation.Type.IN, continueButton)
                    .wait(100).duration(500).maxOpacity(70).build();
            animationMan.add(fade);
            
            // Menu is now shown.
            menuShown = true;                  
            
            return true;
        }
        else if (menuShown == true)
        {
            if (repeatButton.isActivated() == true)
            {
                repeat(game, hub);
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
    public boolean evaluateRules(Game game, ManagerHub hub)
    {
        // Check all the rules.  If any of them are false, return false.
        for (Rule rule : ruleList)
            if (rule.evaluate(game, hub) == false)
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
    public void updateLogic(Game game, ManagerHub hub)
    {                
        // If we're activated, run the tutorial logic.
        if (initialized == true && update(game, hub) == false)
        {
            initialized = false;
            finish(game, hub);
            done = true;
        }
    }       
    
    public void finish(final Game game, ManagerHub hub)
    {
        // Make convenience variables for the managers used.
        final BoardManager boardMan = hub.boardMan;
        final LayerManager layerMan = hub.layerMan;
        final PieceManager pieceMan = hub.pieceMan;
        final ScoreManager scoreMan = hub.scoreMan;
        final StatManager statMan = hub.statMan;
        final TimerManager timerMan = hub.timerMan;
        final SettingsManager settingsMan = hub.settingsMan;

        // Write out completion.
        settingsMan.setBoolean(this.getSettingsKey(), true);

        // Load the score managers state.
        boardMan.loadState();
        statMan.loadState();
        scoreMan.loadState();                                                
        
        // Remove the repeat button.
        repeatButton.setVisible(false);
        layerMan.remove(repeatButton, Layer.EFFECT);
        repeatButton.dispose();
        repeatButton = null;
        
        // Remove the continue button.
        continueButton.setVisible(false);
        layerMan.remove(continueButton, Layer.EFFECT);
        continueButton.dispose();
        continueButton = null;
        
        // Remove the speech bubble.
        layerMan.remove(bubble, Layer.EFFECT);
        bubble.dispose();
        bubble = null;
        
        // Remove the text label.
        for (ITextLabel label : labelList)
        {
            layerMan.remove(label, Layer.EFFECT);       
            label.dispose();
        }
        labelList = null;
        
        // Remove the restriction board.
        pieceMan.clearRestrictionBoard();
        
        // Turn on tile drops.
        game.getTileDropper().setDropOnCommit(true);
        
        // Start the timer.        
        timerMan.setStopped(false);  
        
        // Reset the refactor speed.
        refactorer.setRefactorSpeed(RefactorSpeed.NORMAL);

        // Reset the piece preview.
        boolean showTraditional = hub.settingsMan.getBool(Key.USER_PIECE_PREVIEW_TRADITIONAL);
        boolean showOverlay = hub.settingsMan.getBool(Key.USER_PIECE_PREVIEW_OVERLAY);
        game.getUI().setTraditionalPiecePreviewVisible(showTraditional);
        game.getUI().setOverlayPiecePreviewVisible(showOverlay);

        // Reset the piece we had before the tutorial ran.
        hub.pieceMan.setPiece(pieceBeforeInitialization);
    }
        
    protected void repeat(final Game game, ManagerHub hub)
    {
        // Make convenience variables for the managers used.
        final AnimationManager animationMan = hub.animationMan;
        final BoardManager boardMan = hub.boardMan;
        final PieceManager pieceMan = hub.pieceMan;
        final ScoreManager scoreMan = hub.scoreMan;
        final StatManager statMan = hub.statMan;
        final TimerManager timerMan = hub.timerMan;

        // Make sure to reset the gravity.
        boardMan.setGravity(EnumSet.of(Direction.DOWN, Direction.LEFT));
        
        // Reset move counter.
        statMan.resetMoveCount();      
        
        // Reset the scores.
        scoreMan.resetScore();
        
        // Create the fake board.
        createBoard(game, hub);
        
        // Fade board in.
        final EntityGroup e = boardMan.getTileRange(
                boardMan.getNumberOfCells() / 2,
                boardMan.getNumberOfCells() - 1);
        
        e.setVisible(false);
        
        IAnimation a = new FadeAnimation.Builder(FadeAnimation.Type.IN, e)
                .wait(0).duration(300).build();
        
        animationMan.add(a);
        
        // Change the piece to the dot.
        pieceMan.setPiece(PieceType.DOT.getPiece());
        
        // Make sure buttons aren't visible.
        repeatButton.setVisible(false);
        if (repeatButton.isActivated())
        {
            repeatButton.setActivated(false);
        }
        
        continueButton.setVisible(false);
        if (continueButton.isActivated())
        {
            continueButton.setActivated(false);
        }
        
        // Menu is not shown.
        menuShown = false;
        
        // Make sure the bubble opacity is full.
        bubble.setOpacity(100);            
        
        // Piece is visible.
        pieceMan.showPieceGrid();
        pieceMan.startAnimation(timerMan);
    }    
    
    protected abstract void createBoard(final Game game, final ManagerHub hub);
    
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

    @Override
    public boolean hasRun(final ManagerHub hub)
    {
        return hub.settingsMan.getBool(this.getSettingsKey());
    }
    
}
