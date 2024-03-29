/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ca.couchware.wezzle2d.util;

import java.awt.Color;
import org.jdom.Element;
import org.lwjgl.opengl.GL11;

/**
 *
 * @author cdmckay
 */
public class CouchColor implements IXMLizable
{

    /** The instance of color this instance is wrapping. */
    private Color color;    
    
    private CouchColor(Color color)
    {
        this.color = color;
    }
    
    private CouchColor(int red, int green, int blue)
    {
        this.color = new Color(red, green, blue);
    }
    
    private CouchColor(int red, int green, int blue, int alpha)
    {
        this.color = new Color(red, green, blue, alpha);
    }
    
    public static CouchColor newInstance(Color color)
    {
        return new CouchColor(color);
    }
    
    public static CouchColor newInstance(int red, int green, int blue)
    {
        return new CouchColor(red, green, blue);
    }
    
    public static CouchColor newInstance(int red, int green, int blue, int alpha)
    {
        return new CouchColor(red, green, blue, alpha);
    }
    
    public static CouchColor newInstance(Color color, int alpha)
    {
        return new CouchColor(color.getRed(), color.getGreen(), color.getBlue(), alpha);
    }
    
    public static CouchColor newInstanceFromXml(Element element)
    {
        int r = Integer.parseInt(element.getAttributeValue("red"));
        int g = Integer.parseInt(element.getAttributeValue("green"));
        int b = Integer.parseInt(element.getAttributeValue("blue"));
        int a = Integer.parseInt(element.getAttributeValue("alpha"));
        return new CouchColor(r, g, b, a);
    }
    
    public Element toXmlElement()
    {
        Element element = new Element("color");
        element.setAttribute("red", String.valueOf(color.getRed()));
        element.setAttribute("green", String.valueOf(color.getGreen()));
        element.setAttribute("blue", String.valueOf(color.getBlue()));                    
        element.setAttribute("alpha", String.valueOf(color.getAlpha()));
        return element; 
    }
    
    public static int scaleOpacity(int val)
    {        
        return NumUtil.scaleInt(0, 100, 0, 255, val);
    }
    
    /**
     * Bind the colour to the current GL context.
     */
    public void bind()
    {
        GL11.glColor4f((float) color.getRed()   / 255f,
                       (float) color.getGreen() / 255f,
                       (float) color.getBlue()  / 255f,
                       (float) color.getAlpha() / 255f);        
    }   
    
    /**
     * Returns the underlying color instance.
     * 
     * @return
     */
    public Color toColor()
    {
        return color;
    }
    
    @Override
    public String toString()
    {
        return String.format("SuperColor[r=%d, g=%d, b=%d, a=%d]", 
                color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
    }
            
}
