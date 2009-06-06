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
public class CouchDate implements IXMLizable
{
    private GregorianCalendar calendar;

    private CouchDate()
    {
        this.calendar = new GregorianCalendar();
    }
    
    private CouchDate(int year, int month, int day)
    {
        this.calendar = new GregorianCalendar(year, month, day);
    }
    
    public static CouchDate newInstance()
    {
        return new CouchDate();
    }
    
    public static CouchDate newInstanceFromXml(Element element)
    {
        int day = Integer.parseInt(element.getAttributeValue("day").toString());
        int month = Integer.parseInt(element.getAttributeValue("month").toString());            
        int year = Integer.parseInt(element.getAttributeValue("year").toString());
        
        return new CouchDate(year, month, day);
    }

    public long getTime()
    {
        return calendar.getTimeInMillis();
    }
    
    public Element toXmlElement()
    {
        Element element = new Element("date");
        element.setAttribute("day",   String.valueOf(calendar.get(Calendar.DATE)));
        element.setAttribute("month", String.valueOf(calendar.get(Calendar.MONTH)));
        element.setAttribute("year",  String.valueOf(calendar.get(Calendar.YEAR)));
        return element;
    }
    
}
