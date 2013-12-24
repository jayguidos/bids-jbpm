/**
 * ===========================================================================
 *    Copyright 2012 BIDS Holdings L.P. All rights reserved.
 * ---------------------------------------------------------------------------
 * Created on 10/11/13
 * By bidsjagu
 *
 */

package com.bids.bpm.facts.db;

import java.util.Calendar;
import java.util.Date;


import com.bids.bpm.facts.model.BidsDay;

public class DBSession
{

    private final Date activeDate;

    public DBSession(Date activeDate)
    {
        this.activeDate = activeDate;
    }

    public BidsDay getCalendarDay()
    {
        BidsDay bidsDay = new BidsDay();
        Calendar c = Calendar.getInstance();
        c.setTime(activeDate);

        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 1);
        bidsDay.setDate(c.getTime());

        c.set(Calendar.HOUR_OF_DAY, 9);
        c.set(Calendar.MINUTE, 30);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        bidsDay.setSystemStartTime(c.getTime());

        c.set(Calendar.HOUR_OF_DAY, 4);
        c.set(Calendar.MINUTE, 00);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        bidsDay.setSystemEndTime(c.getTime());

        return bidsDay;
    }
}
