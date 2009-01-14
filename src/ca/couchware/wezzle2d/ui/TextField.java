/*
 * Wezzle
 * Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.ui;

import ca.couchware.wezzle2d.IBuilder;
import ca.couchware.wezzle2d.ResourceFactory;
import ca.couchware.wezzle2d.ResourceFactory.LabelBuilder;
import ca.couchware.wezzle2d.event.IKeyListener;
import ca.couchware.wezzle2d.event.KeyEvent;
import ca.couchware.wezzle2d.event.MouseEvent;
import ca.couchware.wezzle2d.graphics.ISprite;
import ca.couchware.wezzle2d.manager.Settings;
import ca.couchware.wezzle2d.util.Ascii;
import ca.couchware.wezzle2d.util.ImmutableRectangle;
import ca.couchware.wezzle2d.util.SuperColor;
import ca.couchware.wezzle2d.util.Util;
import java.awt.Color;
import java.util.EnumSet;
import java.util.Set;

/**
 * A class for creating text boxes that allow the user to enter text
 * when active and stop taking text when not active.
 * 
 * @author cdmckay
 */
public class TextField extends AbstractButton implements ITextField, IKeyListener
{    
    
    /** The default color. */
    protected static Color defaultColor = Color.RED;
    
    /**
     * Change the default color for all sprite buttons.
     * 
     * @param The new color.
     */
    public static void setDefaultColor(Color color)
    { defaultColor = color; }            
            
    /** 
     * The character appended to the end of the label text when it is being
     * edited.
     */
    final private static char EDIT_CHARACTER = '|';       
    
    /** The graphic file type. */
    final protected static String FILE_TYPE = ".png";
    
    /** The left sprite. */
    final private static String LEFT_SPRITE_PATH = Settings.getSpriteResourcesPath() 
            + "/Button_Thin_Left" 
            + FILE_TYPE;
    
    /** The middle sprite. */
    final private static String MIDDLE_SPRITE_PATH = Settings.getSpriteResourcesPath() 
            + "/Button_Thin_Middle" 
            + FILE_TYPE;
    
    /** The right sprite. */
    final private static String RIGHT_SPRITE_PATH = Settings.getSpriteResourcesPath() 
            + "/Button_Thin_Right" 
            + FILE_TYPE;
    
    final protected Set<Ascii> allowedAsciiSet = createAllowedAsciiSet();
    
    protected Set<Ascii> createAllowedAsciiSet()
    {
        Set<Ascii> set = EnumSet.range(Ascii.UPPER_A, Ascii.UPPER_Z);       
        set.addAll(EnumSet.range(Ascii.LOWER_A, Ascii.LOWER_Z));
        return set;
    }
           
    /** The normal text. */
    final protected String defaultText;  
    
    /** The color of the button. */
    final protected Color normalColor;   
    
     /** The color of the button. */
    final protected Color activeColor;   

    /** The left sprite of the button. */
    protected ISprite leftSprite;
    
    /** The middle sprite of the button. */
    protected ISprite middleSprite;
    
    /** The right sprite of the button. */
    protected ISprite rightSprite;
    
    /** The normal label. */
    final protected ITextLabel normalLabel;   
    
    /** The active label. */
    final protected ITextLabel activeLabel;       
    
    /** The size of the text on the button. */
    final protected int textSize;
       
    /** The normal opacity when the button is off. */
    protected int normalOpacity;
    
    /** The hover opacity. */
    protected int hoverOpacity;
    
    /** The pressed opacity. */
    protected int pressedOpacity;
    
    /** The active opacity. */
    protected int activeOpacity;               
    
    /** The maximum length of the text field. */
    protected final int maximumLength;
    
    /**
     * Creates a button at the coordinates provided.
     * @param x
     * @param y
     */
    protected TextField(Builder builder)
    {      
        // Invoke the super on the required arguments.
        // This will set their variables as well.
        super(builder.x, builder.y);
        
        // Assign values from builder.      
        this.textSize       = builder.textSize;
        this.normalColor    = builder.normalColor;
        this.activeColor     = builder.activeColor;
        this.defaultText    = builder.defaultText;       
        this.normalOpacity  = limitOpacity(builder.normalOpacity);
        this.hoverOpacity   = limitOpacity(builder.hoverOpacity);
        this.pressedOpacity = limitOpacity(builder.pressedOpacity);
        this.activeOpacity  = limitOpacity(builder.activeOpacity);
        this.opacity        = limitOpacity(builder.opacity);
                                
        this.maximumLength = builder.maximumLength > 0
                ? builder.maximumLength
                : 1;
                
        // Set the visibility.
        this.visible = builder.visible;
        this.disabled = builder.disabled;
                                                                   
        // Load the sprites.
        leftSprite   = ResourceFactory.get().getSprite(LEFT_SPRITE_PATH);      
        middleSprite = ResourceFactory.get().getSprite(MIDDLE_SPRITE_PATH);      
        rightSprite  = ResourceFactory.get().getSprite(RIGHT_SPRITE_PATH);      
        
        // Assign values based on the values from builder.        
        this.width  = builder.width;
        this.height = middleSprite.getHeight();
               
        if (!validateWidth())
            throw new RuntimeException("The button width is too narrow.");
                        
        // Determine the offsets.
        this.alignment = builder.alignment;
        this.offsetX = determineOffsetX(alignment, width);
        this.offsetY = determineOffsetY(alignment, height);
        
        // Set the shape.
        this.shape = new ImmutableRectangle(x + offsetX, y + offsetY, width, height);                                      
        
        // Create the normal label.
        this.normalLabel = new LabelBuilder(x + offsetX + width / 2, y + offsetY + height / 2)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                .color(normalColor).size(textSize).text(defaultText).end(); 
        
        // Create the active label, which is just the normal label with
        // an underscore at the end.
        this.activeLabel = new LabelBuilder(normalLabel)
                .color(activeColor)
                .text(Util.padString("", EDIT_CHARACTER, maximumLength)).end();
    }
    
    public static class Builder implements IBuilder<TextField>
    {
        // Required values.       
        protected int x;
        protected int y;        
        
        // Optional values.
        protected EnumSet<Alignment> alignment = EnumSet.of(Alignment.TOP, Alignment.LEFT);
        protected Color normalColor = defaultColor;
        protected Color activeColor  = defaultColor;
        protected String defaultText = "Button";        
        protected int width = 220;
        protected int textSize = 20;        
        protected int pressedOpacity = 100;
        protected int hoverOpacity   = 100;        
        protected int activeOpacity  = 100;
        protected int normalOpacity  = 80;
        protected int opacity        = 100;
        protected boolean visible = true;
        protected boolean disabled = false;        
        protected int maximumLength = 8;
        
        public Builder(int x, int y)
        {            
            this.x = x;
            this.y = y;
        }
        
        public Builder(TextField textBox)
        {            
            this.x                = textBox.x;
            this.y                = textBox.y;
            this.alignment        = textBox.alignment.clone();
            this.normalColor      = textBox.normalColor;
            this.activeColor       = textBox.activeColor;
            this.defaultText      = textBox.defaultText;           
            this.width            = textBox.width;
            this.textSize         = textBox.textSize;            
            this.hoverOpacity     = textBox.hoverOpacity;
            this.pressedOpacity   = textBox.pressedOpacity;
            this.activeOpacity    = textBox.activeOpacity;
            this.normalOpacity    = textBox.normalOpacity;       
            this.opacity          = textBox.opacity;            
            this.visible          = textBox.visible;
            this.disabled         = textBox.disabled;
            this.maximumLength    = textBox.maximumLength;
        }
        
        public Builder x(int val) { x = val; return this; }        
        public Builder y(int val) { y = val; return this; }
        
        public Builder alignment(EnumSet<Alignment> val) 
        { alignment = val; return this; }
        
        public Builder normalColor(Color val)
        { normalColor = val; return this; }
        
        public Builder activeColor(Color val)
        { activeColor = val; return this; }
        
        public Builder text(String val)
        { defaultText(val); return this; }
        
        public Builder defaultText(String val) 
        { defaultText = val; return this; }                
        
        public Builder width(int val)
        { width = val; return this; }
        
        public Builder textSize(int val)
        { textSize = val; return this; }
        
        public Builder normalOpacity(int val)                 
        { normalOpacity = val; return this; }
        
        public Builder hoverOpacity(int val) 
        { hoverOpacity = val; return this; }
        
        public Builder pressedOpacity(int val)
        { pressedOpacity = val; return this; }
        
        public Builder activeOpacity(int val) 
        { activeOpacity = val; return this; }
        
        public Builder opacity(int val)
        { opacity = val; return this; }
                
        public Builder visible(boolean val) 
        { visible = val; return this; }
        
        public Builder disabled(boolean val)
        { disabled = val; return this; }
        
        public Builder maximumLength(int val)
        { maximumLength = val; return this; }
        
        public TextField end()
        {
            TextField textBox = new TextField(this);
            
            if (visible == true && disabled == false)
            {
                textBox.window.addMouseListener(textBox);           
                textBox.window.addKeyListener(textBox);
            }
            
            return textBox;
        }                
    }            
    
    final protected boolean validateWidth()
    {
        // Make sure the width is acceptable.
        int w1 = leftSprite.getWidth();        
        int w2 = rightSprite.getWidth();                             
        
        if (w1 + w2 > width)
            return false;
        else
            return true;
    }
        
    protected void drawButton(int o)
    {
        final int X = x + offsetX;
        final int Y = y + offsetY;
        
        leftSprite.draw(X, Y)
                .width(leftSprite.getWidth()).height(height).opacity(o).end();
                
        middleSprite.draw(X + leftSprite.getWidth(), Y)
                .width(width - leftSprite.getWidth() - rightSprite.getWidth())
                .height(height).opacity(o).end();
        
        rightSprite.draw(X + width - leftSprite.getWidth(), Y)
                .width(rightSprite.getWidth()).height(height).opacity(o).end();                
    }
    
    private void drawUnderline(int x, int y, int width, int height, int border)
    {                
        gfx.setColor(SuperColor.newInstance( 
                Color.BLACK, 
                SuperColor.scaleOpacity(opacity)));
        gfx.fillRect(x, y, width + border * 2, height + border * 2);
        
        gfx.setColor(SuperColor.newInstance(
                Color.DARK_GRAY, 
                SuperColor.scaleOpacity(opacity)));
        gfx.fillRect(x + border, y + border, width, height);
    }
    
    protected void drawNormal()
    {                
//        sprite.draw(
//                x + offsetX, y + offsetY, 
//                width, height,                 
//                0.0, opacitize(normalOpacity));   
        //drawButton(opacitize(normalOpacity));  
        drawUnderline(this.x + 6 + offsetX, this.y + height - 6 + offsetY, this.width - 12, 1, 1);
        
        normalLabel.setOpacity(opacity);
        normalLabel.draw();
    }
    
    protected void drawActivated()
    {        
//        sprite.draw(
//                x + offsetX, y + offsetY, 
//                width, height, 
//                0.0, opacitize(activeOpacity));        
        //drawButton(opacitize(activeOpacity));   
        drawUnderline(this.x + 6 + offsetX, this.y + height - 6 + offsetY, this.width - 12, 1, 1);
        
        if (activeLabel != null)
        {
            activeLabel.setOpacity(opacity);
            activeLabel.draw();            
        }
        else 
        {
            normalLabel.setOpacity(opacity);
            normalLabel.draw();
        }
    }
    
    protected void drawHovered()
    {
        //sprite.draw(x + offsetX, y + offsetY, width, height, 0.0, opacitize(hoverOpacity));
        //drawButton(opacitize(hoverOpacity));
        drawUnderline(this.x + 6 + offsetX, this.y + height - 6 + offsetY, this.width - 12, 1, 1);
             
        if (activeLabel != null && isActivated() == true)
        {
            activeLabel.setOpacity(opacity);            
            activeLabel.draw();            
        }
        else
        {
            normalLabel.setOpacity(opacity);
            normalLabel.setColor(activeColor);
            normalLabel.draw();
            normalLabel.setColor(normalColor);
        }        
    }
    
    protected void drawPressed()
    {
        //sprite.draw(x + offsetX, y + offsetY, width, height, 0.0, opacitize(pressedOpacity));
        //drawButton(opacitize(pressedOpacity));
        drawUnderline(this.x + 6 + offsetX, this.y + height - 6 + offsetY, this.width - 12, 1, 1);
        normalLabel.setOpacity(opacity);
        normalLabel.translate(0, 1);
        normalLabel.draw();
        normalLabel.translate(0, -1);
    }   
    
    
    private int opacitize(int targetOpacity)
    {
        return Util.scaleInt(0, 100, 0, targetOpacity, this.opacity);
    }

    public int getActiveOpacity()
    {
        return activeOpacity;
    }

    public void setActiveOpacity(int activeOpacity)
    {
        this.activeOpacity = limitOpacity(activeOpacity);
        setDirty(true);
    }

    public int getHoverOpacity()
    {
        return hoverOpacity;
    }

    public void setHoverOpacity(int hoverOpacity)
    {
        this.hoverOpacity = limitOpacity(hoverOpacity);
        setDirty(true);
    }     
    
     public int getPressedOpacity()
    {
        return pressedOpacity;
    }

    public void setPressedOpacity(int pressedOpacity)
    {
        this.pressedOpacity = limitOpacity(pressedOpacity);
        setDirty(true);
    }    

    public int getNormalOpacity()
    {
        return getOpacity();
    }

    public void setNormalOpacity(int offOpacity)
    {
        setOpacity(offOpacity);
    }             

    public String getNormalText()
    {
        return defaultText;
    }
        
    // This is overridden so that the text box will
    // stop taking input when the user clicks elsewhere.
    @Override    
    public void mousePressed(MouseEvent e)
	{      		
        // Ignore click if we're outside the button.
        if (state.contains(State.HOVERED) == false)
        {            
            state.remove(State.ACTIVATED);
            
            if (normalLabel.getText().length() == 0)
            {
                normalLabel.setText(defaultText);
            }
            
            this.dirty = true;
            return;                    
        }
                        
		// Check which button.
        switch (e.getButton())
        {
            // Left mouse clicked.
            case LEFT:
                handlePressed();
                break;                        
                
            default:
                // Intentionally left blank.
        }
	}
    
    @Override
    public void setX(int x)
    {
        super.setX(x);
        
        normalLabel.setX(x);       
        if (activeLabel != null) activeLabel.setX(x);
    }
    
    @Override
    public void setY(int y)
    {
        super.setY(y);
        
        normalLabel.setY(y);        
        if (activeLabel != null) activeLabel.setY(y);
    }

    /**
     * Gets the maximum allowable length for this text field.
     * 
     * @return
     */
    public int getMaximumLength()
    {
        return maximumLength;
    }   
    
    public boolean draw()
    {
        x_ = x + offsetX;
        y_ = y + offsetY;
        
        // Don't draw if not visible.
        if (visible == false)
            return false;

        // See what state we're in.
        if (state.contains(State.PRESSED))        
            drawPressed();        
        else if (state.contains(State.HOVERED))
            drawHovered();
        else if (state.contains(State.ACTIVATED))
            drawActivated();
        else
            drawNormal();     
        
        // Uncomment for debugging the draw rect box.
        //window.drawRect(shape.getX(), shape.getY(), shape.getWidth(), shape.getHeight());
        
        return true;
    }
        
    @Override
    protected void handlePressed()
    {   
        // Invoke super.
        super.handlePressed();
        
        // Blank out the normal label.
        normalLabel.setText("");
        
        // Change the string to be *'s.
        activeLabel.setText("" + EDIT_CHARACTER);
    }    
    
    @Override
    protected void handleReleased()
    {                                             
        if (state.containsAll(EnumSet.of(State.PRESSED, State.HOVERED)))
        {
           clicked = true;                                                          
           state.add(State.ACTIVATED);           
        }   
        
        state.remove(State.PRESSED);
                            
        setDirty(true);
    }
    
    @Override
    public void setVisible(boolean visible)
    {
        // Ignore if visibility not changed.
        if (this.visible == visible || this.disabled == true)
        {
            this.visible = visible;
            return;
        }
        
        // Invoke super.
        super.setVisible(visible);
        
        // Add or remove listener based on visibility.
        swapKeyListener(visible);     
    }
    
    /**
     * Adds or removes this instance from the key listener list.
     * 
     * @param add
     */
    private void swapKeyListener(boolean add)
    {
        // If we're adding the listener.
        if (add == true)
        {            
            window.addKeyListener(this);            
        }
        // If we're removing it.
        else
        {                              
            window.removeKeyListener(this);            
        }  
    }
    
    public void keyPressed(KeyEvent event)
    {    
        //LogManager.recordMessage(event.getChar() + " (" + (int) event.getChar() + ")");
        
        // Get the key that was pressed.
        Ascii a = Ascii.valueOf(event.getChar());                
        
        if (state.contains(State.ACTIVATED))
        {       
            String text = normalLabel.getText();
            
            switch (a)
            {
                case BACKSPACE:
                    
                    if (text.length() > 0)                    
                        normalLabel.setText(text.substring(0, text.length() - 1));
                    
                    break;
                    
                case CR:
                case ESC:
                    
                    state.remove(State.ACTIVATED);
                
                    if (normalLabel.getText().length() == 0)                    
                        normalLabel.setText(defaultText);
                    
                    break;
                                        
                default:
                    
                    if (text.length() < maximumLength 
                            && allowedAsciiSet.contains(a))     
                    {
                        normalLabel.setText(text + a);                                       
                    }
            }
                        
            // Set the active label.
            activeLabel.setText(normalLabel.getText() + EDIT_CHARACTER);
        }        
    }

    public void keyReleased(KeyEvent event)
    {
        // Doesn't work right now.
    }

    public String getText()
    {
        return normalLabel.getText();
    }

    public void setText(String text)
    {
        normalLabel.setText(text);
        //activeLabel.setText(text + EDIT_CHARACTER);
    }

}    