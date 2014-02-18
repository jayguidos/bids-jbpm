/**
 * ===========================================================================
 *    Copyright 2012 BIDS Holdings L.P. All rights reserved.
 * ---------------------------------------------------------------------------
 * Created on 2/13/14
 * By bidsjagu
 *
 */

package com.bids.bpm.work.handlers.support.fact;

import java.io.Serializable;

public class SerializableFactIDFactory
    implements FactIDFactory,
               Serializable
{
    private Long nextFactId =0L;

    public synchronized Long getNextFactID(String key)
    {
        return ++nextFactId;
    }
}
