/**
 * ===========================================================================
 *    Copyright 2012 BIDS Holdings L.P. All rights reserved.
 * ---------------------------------------------------------------------------
 * Created on 10/10/13
 * By bidsjagu
 *
 */

package com.bids.bpm.facts.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.validation.constraints.NotNull;


import com.bids.bpm.facts.model.validators.ValidBidsDate;
import static com.bids.bpm.shared.BidsBPMConstants.TRADING_DATE_FORMAT;

public class BidsDay
        extends BidsFact
        implements Comparable<BidsDay>
{
    public static final String BIDS_DAY = "BidsDay";
    private static final long serialVersionUID = 3108457365704606617L;
    private static final Date DEFAULT_START_TIME;
    private static final Date DEFAULT_END_TIME;
    @NotNull
    @ValidBidsDate
    private Date date;
    private Date systemStartTime;
    private Date systemEndTime;
    private boolean isTradingHalfDay;

    static
    {
        try
        {
            DEFAULT_START_TIME = new SimpleDateFormat("HH:mm:ss").parse("09:30:00");
            DEFAULT_END_TIME = new SimpleDateFormat("HH:mm:ss").parse("16:00:00");
        } catch (ParseException e)
        {
            throw new RuntimeException(e);
        }
    }

    // enforce time of day fields to be on the current bids date
    private static Date setTimeOfDayFieldsOnBidsDate(Date sourceDate, Date sourceTimeOfDay)
    {
        Calendar instance = Calendar.getInstance();
        instance.setTime(sourceDate);
        Calendar source = Calendar.getInstance();
        source.setTime(sourceTimeOfDay);

        instance.set(Calendar.HOUR, source.get(Calendar.HOUR));
        instance.set(Calendar.MINUTE, source.get(Calendar.MINUTE));
        instance.set(Calendar.SECOND, source.get(Calendar.SECOND));
        instance.set(Calendar.MILLISECOND, source.get(Calendar.MILLISECOND));
        return instance.getTime();
    }

    public BidsDay()
    {
        super(BIDS_DAY);
        this.date = new Date();
        this.systemStartTime = setTimeOfDayFieldsOnBidsDate(this.date, DEFAULT_START_TIME);
        this.systemEndTime = setTimeOfDayFieldsOnBidsDate(this.date, DEFAULT_END_TIME);

    }

    public BidsDay(String dateStr)
    {
        super(dateStr);
        try
        {
            this.date = new SimpleDateFormat(TRADING_DATE_FORMAT).parse(dateStr);
        } catch (ParseException e)
        {
            throw new RuntimeException(e);
        }
        this.systemStartTime = setTimeOfDayFieldsOnBidsDate(this.date, DEFAULT_START_TIME);
        this.systemEndTime = setTimeOfDayFieldsOnBidsDate(this.date, DEFAULT_END_TIME);
    }

    public int compareTo(BidsDay o)
    {
        if (o == null)
            return 1;
        String myDate = new SimpleDateFormat(TRADING_DATE_FORMAT).format(date);
        String otherDate = new SimpleDateFormat(TRADING_DATE_FORMAT).format(o.date);
        return myDate.compareTo(otherDate);
    }

    @Override
    public boolean equals(Object o)
    {
        if ( ! super.equals(o) )
            return false;

        if (!(o instanceof BidsDay)) return false;
        BidsDay bidsDay = (BidsDay) o;
        if (!date.equals(bidsDay.date)) return false;

        return true;
    }

    @Override
    public int hashCode()
    {
        return 31 * super.hashCode() + date.hashCode();
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
        // this is a special, undeletable fact, so I don't expose the ID anywhere
        return "BidsDay[" + new SimpleDateFormat(TRADING_DATE_FORMAT).format(date) + ']';
    }

    public Date getDate()
    {
        return date;
    }

    public void setDate(Date date)
    {
        // only the Date fields are relevant, zero out the time fields
        Calendar instance = Calendar.getInstance();
        instance.setTime(date);
        instance.set(Calendar.HOUR, 0);
        instance.set(Calendar.MINUTE, 0);
        instance.set(Calendar.SECOND, 0);
        instance.set(Calendar.MILLISECOND, 0);
        // just with Date fields set
        this.date = instance.getTime();
    }

    public Date getSystemStartTime()
    {
        return systemStartTime;
    }

    public void setSystemStartTime(Date systemStartTime)
    {
        this.systemStartTime = setTimeOfDayFieldsOnBidsDate(date, systemStartTime);
    }

    public Date getSystemEndTime()
    {
        return systemEndTime;
    }

    public void setSystemEndTime(Date systemEndTime)
    {
        this.systemEndTime = setTimeOfDayFieldsOnBidsDate(date, systemEndTime);
    }

}
