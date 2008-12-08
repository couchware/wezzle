/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ca.couchware.wezzle2d.util;

import org.jdom.Element;

/**
 *
 * @author cdmckay
 */
public class Color extends java.awt.Color implements IXMLizable
{

    private Color(int red, int green, int blue, int alpha)
    {
        super(red, green, blue, alpha);
    }
    
    public static Color newInstance(int red, int green, int blue, int alpha)
    {
        return new Color(red, green, blue, alpha);
    }
    
    public static Color newInstanceFromXML(Element element)
    {
        int r = Integer.parseInt(element.getAttributeValue("red"));
        int g = Integer.parseInt(element.getAttributeValue("green"));
        int b = Integer.parseInt(element.getAttributeValue("blue"));
        int a = Integer.parseInt(element.getAttributeValue("alpha"));
        return new Color(r, g, b, a);
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
            
}
