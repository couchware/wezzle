/*
 *  Wezzle
 *  Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.event;

//import ca.couchware.wezzle2d.IBuilder;
import ca.couchware.wezzle2d.util.ImmutablePosition;
import java.util.EnumSet;

/**
 *
 * @author cdmckay
 */
public class MouseEvent implements IMouseEvent
{

    private Object source;    
    
    private Button button;    
    
    private EnumSet<Modifier> modifierState;
    
    private ImmutablePosition position;
    
    private Type type;
    
    public MouseEvent(Object source,
            Button buttonState, 
            EnumSet<Modifier> modifierState,
            ImmutablePosition position,
            Type type) 
    {        
        // Check the arguments.
        if (buttonState == null)
            throw new NullPointerException("Button state cannot be null.");
        
        if (modifierState == null)
            throw new NullPointerException("Modifier state cannot be null.");
        
        if (position == null)
            throw new NullPointerException("Position cannot be null.");
        
        if (type == null)
            throw new NullPointerException("Type cannot be null.");
        
        this.source = source;
        this.button = buttonState;
        this.modifierState = modifierState;
        this.position = position;
        this.type = type;
    }        
    
    public MouseEvent(java.awt.event.MouseEvent event, Type type)
    {
        // Check argument.
        if (event == null)
            throw new NullPointerException();
        
        // Extract the source.
        this.source = event.getSource();
        
        // Extract the button.
        switch (event.getButton())
        {
            case java.awt.event.MouseEvent.BUTTON1:
                this.button = Button.LEFT;
                break;
                
            case java.awt.event.MouseEvent.BUTTON2:
                this.button = Button.MIDDLE;
                break;
                
            case java.awt.event.MouseEvent.BUTTON3:
                this.button = Button.RIGHT;
                break;
                
            case java.awt.event.MouseEvent.NOBUTTON:
                this.button = Button.NONE;
                break;
                                                
            default: throw new IllegalStateException("Unknown button.");
        }
        
        // Deal with modifiers here in the future if we need them.
        this.modifierState = EnumSet.noneOf(Modifier.class);
        
        // Extract the position.
        this.position = new ImmutablePosition(event.getX(), event.getY());
        
        // Set the type.
        this.type = type;
    }
    
    public Button getButton()
    {
        return button;
    }

    public EnumSet<Modifier> getModifierState()
    {
        return modifierState;
    }

    public ImmutablePosition getPosition()
    {
        return position;
    }

    public int getX()
    {
        return position.getX();
    }

    public int getY()
    {
        return position.getY();
    }

    public Object getSource()
    {
        return source;
    }

    public Type getType()
    {
        return type;
    }

}
