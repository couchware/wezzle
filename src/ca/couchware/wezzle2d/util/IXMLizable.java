/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ca.couchware.wezzle2d.util;

import org.jdom.Element;

/**
 * An interface for defining objects that may imported and exported
 * to and from and XML element.
 * 
 * @author cdmckay
 */
public interface IXMLizable 
{
    // public static <object> newInstanceFromXml(Element element);
    public Element toXmlElement();
}
