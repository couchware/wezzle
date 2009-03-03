/*
 * Wezzle
 * Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.util;

import java.util.Calendar;
import java.util.GregorianCalendar;
import org.jdom.Element;

/**
 *
 * @author cdmckay
 */
public class SuperCalendar extends GregorianCalendar implements IXMLizable
{

    private SuperCalendar()
    {
        super();
    }
    
    private SuperCalendar(int year, int month, int day)
    {
        super(year, month, day);
    }
    
    public static SuperCalendar newInstance()
    {
        return new SuperCalendar();
    }
    
    public static SuperCalendar newInstanceFromXml(Element element)
    {
        int day = Integer.parseInt(element.getAttributeValue("day").toString());
        int month = Integer.parseInt(element.getAttributeValue("month").toString());            
        int year = Integer.parseInt(element.getAttributeValue("year").toString());
        
        return new SuperCalendar(year, month, day);
    }
    
    public Element toXmlElement()
    {
        Element element = new Element("date");
        element.setAttribute("day",   String.valueOf(get(Calendar.DATE)));
        element.setAttribute("month", String.valueOf(get(Calendar.MONTH)));
        element.setAttribute("year",  String.valueOf(get(Calendar.YEAR)));
        return element;
    }
    
}
