/**
 * ===========================================================================
 *    Copyright 2012 BIDS Holdings L.P. All rights reserved.
 * ---------------------------------------------------------------------------
 * Created on 10/15/13
 * By bidsjagu
 *
 */

package com.bids.bpm.facts.model;

public class TradingEngine
        extends BidsFact
{
    private static final long serialVersionUID = 9038990869738168473L;

    public enum TradingEngineAccess
    {
        restricted, limited, open, closed
    }

    private TradingEngineAccess access;

    public TradingEngine()
    {
        super("TradingEngine");
    }

    public TradingEngineAccess getAccess()
    {
        return access;
    }

    public void setAccess(TradingEngineAccess access)
    {
        this.access = access;
    }

    public void setOpen()
    {
        setAccess(TradingEngineAccess.open);
    }

    public void setClosed()
    {
        setAccess(TradingEngineAccess.closed);
    }

    public boolean isOpen()
    {
        return access == TradingEngineAccess.open;
    }

    public boolean isClosed()
    {
        return access == TradingEngineAccess.closed;
    }

    @Override
    public String toString()
    {
        return "TradingEngine{" +
                "access=" + access +
                '}';
    }
}
