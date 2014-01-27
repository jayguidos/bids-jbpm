/**
 * ===========================================================================
 *    Copyright 2012 BIDS Holdings L.P. All rights reserved.
 * ---------------------------------------------------------------------------
 * Created on 1/25/14
 * By bidsjagu
 *
 */

package com.bids.bpm.facts.model;

import static com.bids.bpm.facts.model.JobControlRecord.State.complete;
import static com.bids.bpm.facts.model.JobControlRecord.State.failed;
import static com.bids.bpm.facts.model.JobControlRecord.State.started;
import static com.bids.bpm.facts.model.JobControlRecord.State.unstarted;

public class JobControlRecord
        extends BidsFact
{
    public enum State
    {
        unstarted, started, complete, failed
    }

    private final String jobId;
    private State state = unstarted;
    private int status;

    public JobControlRecord(String jobId)
    {
        super("JobCtl_" + jobId);
        this.jobId = jobId;
    }

    public void transitionTo(boolean stepIsSuccessful)
    {
        if (!stepIsSuccessful)
        {
            this.state = failed;
            return;
        }

        switch (state)
        {
            case unstarted:
                this.state = started;
                break;
            case started:
                this.state = complete;
                break;
            case failed:
            case complete:
                this.state = started;
                break;
        }
    }

    public State getState()
    {
        return state;
    }

    public void setState(State state)
    {
        this.state = state;
    }

    public int getStatus()
    {
        return status;
    }

    public void setStatus(int status)
    {
        this.status = status;
    }

}
