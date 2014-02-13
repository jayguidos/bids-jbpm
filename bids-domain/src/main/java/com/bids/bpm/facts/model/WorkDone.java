/**
 * ===========================================================================
 *    Copyright 2012 BIDS Holdings L.P. All rights reserved.
 * ---------------------------------------------------------------------------
 * Created on 10/16/13
 * By bidsjagu
 *
 */

package com.bids.bpm.facts.model;

import java.util.Date;

public class WorkDone
        extends BidsFact
{
    private static final long serialVersionUID = -9101928234778914389L;
    private Date doneTime = new Date();

    public WorkDone(String name)
    {
        super(name);
    }

    @Override
    public String toString()
    {
        return printNameAndId() + "{" +
                "name='" + getName() + '\'' +
                "doneTime='" + doneTime + '\'' +
                '}';
    }

    public Date getDoneTime()
    {
        return doneTime;
    }
}
