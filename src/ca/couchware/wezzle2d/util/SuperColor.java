/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ca.couchware.wezzle2d.util;

import java.awt.Color;
import org.jdom.Element;

/**
 *
 * @author cdmckay
 */
public class SuperColor extends Color implements IXMLizable
{

    private SuperColor(int red, int green, int blue, int alpha)
    {
        super(red, green, blue, alpha);
    }
    
    public static SuperColor newInstance(int red, int green, int blue, int alpha)
    {
        return new SuperColor(red, green, blue, alpha);
    }
    
    public static SuperColor newInstance(Color color, int alpha)
    {
        return new SuperColor(color.getRed(), color.getGreen(), color.getBlue(), alpha);
    }
    
    public static SuperColor newInstanceFromXML(Element element)
    {
        int r = Integer.parseInt(element.getAttributeValue("red"));
        int g = Integer.parseInt(element.getAttributeValue("green"));
        int b = Integer.parseInt(element.getAttributeValue("blue"));
        int a = Integer.parseInt(element.getAttributeValue("alpha"));
        return new SuperColor(r, g, b, a);
    }
    
    public Element toXMLElement()
    {
        Element element = new Element("color");
        element.setAttribute("red", String.valueOf(getRed()));
        element.setAttribute("green", String.valueOf(getGreen()));
        element.setAttribute("blue", String.valueOf(getBlue()));                    
        element.setAttribute("alpha", String.valueOf(getAlpha()));
        return element; 
    }
    
    public static int scaleOpacity(int val)
    {        
        return Util.scaleInt(0, 100, 0, 255, val);
    }
    
    @Override
    public String toString()
    {
        return String.format("SuperColor[r=%d, g=%d, b=%d, a=%d]", 
                getRed(), getGreen(), getBlue(), getAlpha());
    }
            
}
