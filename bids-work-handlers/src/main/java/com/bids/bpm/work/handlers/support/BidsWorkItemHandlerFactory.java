/**
 * ===========================================================================
 *    Copyright 2012 BIDS Holdings L.P. All rights reserved.
 * ---------------------------------------------------------------------------
 * Created on 10/31/13
 * By bidsjagu
 *
 */

package com.bids.bpm.work.handlers.support;

public interface BidsWorkItemHandlerFactory<T extends AbstractBidsWorkItemHandler>
{
    T makeWorkItem();
}
