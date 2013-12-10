/**
 * ===========================================================================
 *    Copyright 2012 BIDS Holdings L.P. All rights reserved.
 * ---------------------------------------------------------------------------
 * Created on 10/15/13
 * By bidsjagu
 *
 */

package com.bids.bpm.facts.model;

import java.util.Date;

public class TimeBell
        extends BidsFact
{
    private static final long serialVersionUID = 6515199337954823288L;
    public static final String TIME_BELL = "TimeBell";
    private Date timeRung = new Date();

    public TimeBell(String name)
    {
        super(name);
    }

    public Date getTimeRung()
    {
        return timeRung;
    }

    @Override
    public String toString()
    {
        return TIME_BELL + "{" +
                "name='" + name + '\'' +
                '}';
    }
}
