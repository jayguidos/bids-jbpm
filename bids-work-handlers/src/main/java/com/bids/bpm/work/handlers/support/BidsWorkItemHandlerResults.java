/**
 * ===========================================================================
 *    Copyright 2012 BIDS Holdings L.P. All rights reserved.
 * ---------------------------------------------------------------------------
 * Created on 11/10/13
 * By bidsjagu
 *
 */

package com.bids.bpm.work.handlers.support;

import java.util.HashMap;


import com.bids.bpm.facts.model.WorkDone;

public class BidsWorkItemHandlerResults
{
    public static final String RETURN_CODE = "ReturnCode";
    public static BidsWorkItemHandlerResults ERROR_RESULTS = new BidsWorkItemHandlerResults(-1);
    public static BidsWorkItemHandlerResults OK_RESULTS = new BidsWorkItemHandlerResults(1);
    private final HashMap<String, Object> results = new HashMap<String, Object>();
    private int returnCode = -1;
    private WorkDone workDone;

    public BidsWorkItemHandlerResults(int returnCode)
    {
        setReturnCode(returnCode);
    }

    public BidsWorkItemHandlerResults(int returnCode, WorkDone workDone)
    {
        setReturnCode(returnCode);
        setWorkDone(workDone);
    }

    public BidsWorkItemHandlerResults()
    {
    }

    public void addResult(String name, Object val)
    {
        results.put(name, val);
    }

    public HashMap<String, Object> getResults()
    {
        return results;
    }

    public int getReturnCode()
    {
        return returnCode;
    }

    public void setReturnCode(int returnCode)
    {
        this.returnCode = returnCode;
        addResult(RETURN_CODE, returnCode);
    }

    public WorkDone getWorkDone()
    {
        return workDone;
    }

    public void setWorkDone(WorkDone workDone)
    {
        this.workDone = workDone;
    }
}
