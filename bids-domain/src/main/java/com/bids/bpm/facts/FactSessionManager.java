/**
 * ===========================================================================
 *    Copyright 2012 BIDS Holdings L.P. All rights reserved.
 * ---------------------------------------------------------------------------
 * Created on 2/12/14
 * By bidsjagu
 *
 */

package com.bids.bpm.facts;

import com.bids.bpm.facts.model.BidsFact;

public interface FactSessionManager
{
    public void add(BidsFact fact);

    public void addOrUpdate(BidsFact fact);

    public boolean contains(BidsFact fact);

    public void delete(BidsFact fact);

    public <T extends BidsFact> T find(Class<T> factClass);

    public <T extends BidsFact> T find(Class<T> factClass, String name);

    public void update(BidsFact fact);
}
