/**
 * ===========================================================================
 *    Copyright 2012 BIDS Holdings L.P. All rights reserved.
 * ---------------------------------------------------------------------------
 * Created on 10/10/13
 * By bidsjagu
 *
 */

package com.bids.bpm.facts.model;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


import com.bids.bpm.shared.BidsBPMConstants;
import static com.bids.bpm.shared.BidsBPMConstants.TRADING_DATE_FORMAT;

public class BidsDay
        extends BidsFact
        implements Comparable<BidsDay>
{
    private static final long serialVersionUID = 3108457365704606617L;
    public static final String BIDS_DAY = "BidsDay";
    private Date date;
    private Date systemStartTime;
    private Date systemEndTime;
    private boolean isTradingHalfDay;

    public BidsDay()
    {
        super(BIDS_DAY);
        this.date = new Date();
    }

    public BidsDay(String dateStr)
    {
        super(BIDS_DAY);
        try
        {
            this.date = new SimpleDateFormat(TRADING_DATE_FORMAT).parse(dateStr);
        } catch (ParseException e)
        {
            throw new RuntimeException(e);
        }
    }

    public int compareTo(BidsDay o)
    {
        if ( o == null )
            return 1;
        String myDate = new SimpleDateFormat(TRADING_DATE_FORMAT).format(date);
        String otherDate = new SimpleDateFormat(TRADING_DATE_FORMAT).format(o.date);
        return myDate.compareTo(otherDate);
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
        return "BidsDay[" + new SimpleDateFormat(TRADING_DATE_FORMAT).format(date) + ']';
    }
}
