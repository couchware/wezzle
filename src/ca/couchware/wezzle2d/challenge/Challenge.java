package ca.couchware.wezzle2d.challenge;

import ca.couchware.wezzle2d.*;
import ca.couchware.wezzle2d.animation.Animation;
import ca.couchware.wezzle2d.util.Util;

/**
 * A class for:
 * 1) popping up a challenge notifier,
 * 2) keeping track of said challenge,
 * 3) removing the notifier.
 * 
 * @author cdmckay
 */
public abstract class Challenge extends Entity
{

    //--------------------------------------------------------------------------
    // Static Attributes
    //--------------------------------------------------------------------------
    
    final protected static String SPRITE_NORMAL = Game.SPRITES_PATH 
            + "/ChallengeCircle.png";
    
    final protected static String SPRITE_FAIL = Game.SPRITES_PATH 
            + "/ChallengeCircleFail.png";
    
    final protected static String SPRITE_PASS = Game.SPRITES_PATH 
            + "/ChallengeCirclePass.png";
    
    /**
     * The amount of time to show the fail phase, in ms.
     */
    final protected static long FAIL_PERIOD = 2000;
    
    /**
     * The amount of time to show the pass phase, in ms.
     */
    final protected static long PASS_PERIOD = 2000;
    
    //--------------------------------------------------------------------------
    // Instance Attributes
    //--------------------------------------------------------------------------            
        
    /**
     * The animation.
     */
    protected Animation animation;
    
    /**
     * The challenge background sprite, whee normal.
     */
    protected Sprite spriteNormal;
    
    /**
     * The challenge background sprite, when failed.
     */
    protected Sprite spriteFail;
    
    /**
     * The challenge background sprite, when passed.
     */
    protected Sprite spritePass;
    
    /**
     * The challenge header text.
     */
    protected Label headerLabel;
    
    /**
     * The challenge body text, line 1.
     */
    protected Label bodyLabel1;
    
    /**
     * The challenge body text, line 2.
     */
    protected Label bodyLabel2;
    
    /**
     * The reward, in points.
     */
    protected int reward;
    
    /**
     * The reward label. This is used for both failing and passing.
     */
    protected Label rewardLabel;        
    
    /**
     * The passed status.
     */
    protected boolean passed;
    
    /**
     * How long the pass phase has been running.
     */
    protected long passCounter;
    
    /**
     * The failed status.
     */
    protected boolean failed;
    
    /**
     * How long the fail phase has been running.
     */
    protected long failCounter;
    
    /**
     * Are we done?
     */
    protected boolean done;
        
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a new challenge with its notifier's top-left corner placed
     * at (x,y).
     * 
     * @param x
     * @param y
     */
    public Challenge(final int x, final int y)
    {
        // Load the the sprites.
        spriteNormal = ResourceFactory.get().getSprite(SPRITE_NORMAL);
        spriteFail = ResourceFactory.get().getSprite(SPRITE_FAIL);
        spritePass = ResourceFactory.get().getSprite(SPRITE_PASS);
        
        // Set the (x,y) position and the previous ones too.
        this.x = x;
        this.y = y;
        this.x_ = x;
        this.y_ = y;
        
        // Set the dimensions.
        this.width = spriteNormal.getWidth();
        this.height = spriteNormal.getHeight();
        this.width_ = width;
        this.height_ = height;                
        
        // Create the header text.
        headerLabel = ResourceFactory.get().getText();                
        headerLabel.setSize(18);
        headerLabel.setColor(Game.TEXT_COLOR);
        headerLabel.setAlignment(Label.HCENTER | Label.VCENTER);
        headerLabel.setXYPosition(x + width / 2, y + 62);
        headerLabel.setText("Challenge!");      
        
        // Create the body text.
        bodyLabel1 = ResourceFactory.get().getText();
        bodyLabel1.setSize(12);
        bodyLabel1.setColor(Game.TEXT_COLOR);
        bodyLabel1.setAlignment(Label.HCENTER | Label.VCENTER);
        bodyLabel1.setXYPosition(x + width / 2, y + 90);        
        
        bodyLabel2 = ResourceFactory.get().getText();
        bodyLabel2.setSize(12);
        bodyLabel2.setColor(Game.TEXT_COLOR);
        bodyLabel2.setAlignment(Label.HCENTER | Label.VCENTER);
        bodyLabel2.setXYPosition(x + width / 2, y + 105);    
        
        // Create reward label.
        rewardLabel = ResourceFactory.get().getText();
        rewardLabel.setSize(24);
        rewardLabel.setColor(Game.TEXT_COLOR);
        rewardLabel.setAlignment(Label.HCENTER | Label.VCENTER);
        rewardLabel.setXYPosition(x + width / 2, y + height / 2);
        
        // Set both pass and fail to false (meaning that it's in
        // progress).
        failed = false;
        passed = false;
        
        // Initialize their counters to sane defaults.
        failCounter = 0;
        passCounter = 0;
    }
    
    //--------------------------------------------------------------------------
    // Instance Methods
    //--------------------------------------------------------------------------
    
    public void fail()
    {
        failed = true;     
        rewardLabel.setText("Failed!");
    }
    
    public void pass()
    {
        passed = true;
        rewardLabel.setText("+" + reward);
    }          

    public boolean isDone()
    {
        return done;
    }        
    
    public Animation getAnimation()
    {
        return animation;
    }

    public void setAnimation(Animation animation)
    {
        this.animation = animation;
        
        // Set dirty so it will be drawn.        
        setDirty(true);
    }        
    
    /**
     * Advances the animation depending on the amount of time that has passed.
     * 
     * @param delta The amount of time that has passed.
     */
    public void animate(long delta)
    {
        // Ignore if we have no animation.
        if (animation == null || animation.isDone() == true)
            return;
        
        // Pass through to the animation.
        animation.nextFrame(delta);  
        
        // Set dirty so it will be drawn.        
        setDirty(true);
    }
    
    //--------------------------------------------------------------------------
    // Logic
    //--------------------------------------------------------------------------
    
    public void updateLogic(final Game game, final long delta)
    {
        // If we're done, do nothing.
        if (done == true)
        {
            // Do nothing.
        }
        // If the player hasn't failed or passed, pass the logic handling
        // to the sub-class.
        else if (failed == false && passed == false)
        {
            // Run sub-class logic.
            challengeLogic(game);        
        }
        // If they failed, handle that.
        else if (failed == true)
        {
            failCounter += delta;
            if (failCounter > FAIL_PERIOD)
            {                
                done = true;
            }
        }
        // If they passed, handle that.
        else if (passed == true)
        {
            passCounter += delta;
            if (passCounter > PASS_PERIOD)
            {                
                done = true;
            }
        }
        // This shouldn't happen.
        else
        {
            throw new IllegalStateException("Challenge problem.");
        }
    }        
        
    public abstract void challengeLogic(final Game game);
    
    //--------------------------------------------------------------------------
    // Draw
    //--------------------------------------------------------------------------
    
    public void draw()
    {
        if (isVisible() == false)
            return;
        
        if (failed == true)
        {
            spriteFail.draw(x, y, width, height, 0.0, opacity);
            rewardLabel.setOpacity(opacity);
            rewardLabel.draw();
        }        
        else if (passed == true)
        {
            spritePass.draw(x, y, width, height, 0.0, opacity);
            rewardLabel.setOpacity(Util.scaleInt(0, 85, 0, 100, opacity));
            rewardLabel.draw();
        }
        else
        {
            spriteNormal.draw(x, y, width, height, 0.0, opacity);                       
            headerLabel.draw();          
            bodyLabel1.draw();            
            bodyLabel2.draw();
        }
        
        
    }  

}
