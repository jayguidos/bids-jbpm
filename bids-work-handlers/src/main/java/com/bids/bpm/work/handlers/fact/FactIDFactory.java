/**
 * ===========================================================================
 *    Copyright 2012 BIDS Holdings L.P. All rights reserved.
 * ---------------------------------------------------------------------------
 * Created on 2/10/14
 * By bidsjagu
 *
 */

package com.bids.bpm.work.handlers.fact;

public interface FactIDFactory
{
    public Long getNextFactID(String key);
}
