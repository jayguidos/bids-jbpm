/**
 * ===========================================================================
 *    Copyright 2012 BIDS Holdings L.P. All rights reserved.
 * ---------------------------------------------------------------------------
 * Created on 10/24/13
 * By bidsjagu
 *
 */

package com.bids.bpm.facts.model;

import java.io.Serializable;

public abstract class BidsFact
        implements Serializable
{
    protected String name;

    public BidsFact(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }
}
