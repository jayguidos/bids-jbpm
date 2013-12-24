/**
 * ===========================================================================
 *    Copyright 2012 BIDS Holdings L.P. All rights reserved.
 * ---------------------------------------------------------------------------
 * Created on 10/10/13
 * By bidsjagu
 *
 */

package com.bids.bpm.facts.model;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


import org.apache.commons.io.FileUtils;

public class BidsTradingDay
        extends BidsFact
{
    public static final String BIDS_TRADING_DAY = "BidsTradingDay";
    private static final long serialVersionUID = -577558229893432533L;
    private final BidsDay day;
    private String baseLogDir;

    public BidsTradingDay()
    {
        this(new BidsDay(), "./log");
    }

    public BidsTradingDay(BidsDay day, String baseLogDir)
    {
        super(BIDS_TRADING_DAY);
        this.day = day;
        this.baseLogDir = baseLogDir;
    }

    public Date getSystemStartTime()
    {
        return day.getSystemStartTime();
    }

    public Date getSystemEndTime()
    {
        return day.getSystemEndTime();
    }

    public boolean isTradingHalfDay()
    {
        return day.isTradingHalfDay();
    }

    public boolean isBeforeOpen()
    {
        return day.getSystemStartTime().before(new Date());
    }

    public boolean isAfterClose()
    {
        return day.getSystemEndTime().after(new Date());
    }

    public boolean isInsideTradingHours()
    {
        return !isBeforeOpen() && !isAfterClose();
    }

    @Override
    public String toString()
    {
        return BIDS_TRADING_DAY + "{" +
                "day=" + day.getDate() +
                ", SystemStartTime=" + getSystemStartTime() +
                ", SystemEndTime=" + getSystemEndTime() +
                '}';
    }

    public String getDay()
    {
        return new SimpleDateFormat("yyyy-MM-dd").format(day.getDate());
    }

    public File getLogDir()
    {
        return new File(new File(baseLogDir), getDay());
    }

    public File makeLogDir()
    {
        File ld = getLogDir();
        if (getLogDir().exists())
            try
            {
                FileUtils.forceDelete(ld);
            } catch (IOException e)
            {
            }
        ld.mkdirs();
        return ld;
    }

    public String getBaseLogDir()
    {
        return baseLogDir;
    }

    public void setBaseLogDir(String baseLogDir)
    {
        this.baseLogDir = baseLogDir;
    }
}
