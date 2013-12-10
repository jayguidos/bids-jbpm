/**
 * ===========================================================================
 *    Copyright 2012 BIDS Holdings L.P. All rights reserved.
 * ---------------------------------------------------------------------------
 * Created on 10/10/13
 * By bidsjagu
 *
 */

package com.bids.bpm.facts.model;

import java.util.Date;

public class CalendarDay
        extends BidsFact
{
    private static final long serialVersionUID = 3108457365704606617L;
    public static final String CALENDAR_DAY = "CalendarDay";
    private Date date;
    private Date systemStartTime;
    private Date systemEndTime;
    private boolean isTradingHalfDay;

    public CalendarDay()
    {
        super(CALENDAR_DAY);
    }

    public Date getDate()
    {
        return date;
    }

    public void setDate(Date date)
    {
        this.date = date;
    }

    public Date getSystemStartTime()
    {
        return systemStartTime;
    }

    public void setSystemStartTime(Date systemStartTime)
    {
        this.systemStartTime = systemStartTime;
    }

    public Date getSystemEndTime()
    {
        return systemEndTime;
    }

    public void setSystemEndTime(Date systemEndTime)
    {
        this.systemEndTime = systemEndTime;
    }

    public boolean isTradingHalfDay()
    {
        return isTradingHalfDay;
    }

    public void setTradingHalfDay(boolean tradingHalfDay)
    {
        isTradingHalfDay = tradingHalfDay;
    }

    @Override
    public String toString()
    {
        return "CalendarDay{" +
                "date=" + date +
                ", isTradingHalfDay=" + isTradingHalfDay +
                '}';
    }
}
