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
import static com.bids.bpm.facts.model.JobControlRecord.State.error;
import static com.bids.bpm.facts.model.JobControlRecord.State.failed;
import static com.bids.bpm.facts.model.JobControlRecord.State.started;
import static com.bids.bpm.facts.model.JobControlRecord.State.unstarted;

public class JobControlRecord
        extends BidsFact
{
    public enum State
    {
        unstarted, started, complete, failed, error
    }

    public static final String JOB_CTL = "JobCtl_";

    private final String jobId;
    private State state = unstarted;

    public JobControlRecord(String jobId)
    {
        super(JOB_CTL + jobId);
        this.jobId = jobId;
    }

    public void transitionTo(boolean stepIsSuccessful)
    {
        if (!stepIsSuccessful)
        {
            this.state = error;
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
            case error:
                this.state = failed;
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

    public String getJobId()
    {
        return jobId;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof JobControlRecord)) return false;

        JobControlRecord that = (JobControlRecord) o;

        if (!jobId.equals(that.jobId)) return false;

        return true;
    }

    @Override
    public int hashCode()
    {
        return jobId.hashCode();
    }

    @Override
    public String toString()
    {
        return "JobControlRecord{" +
                "jobId='" + jobId + '\'' +
                ", state=" + state +
                '}';
    }
}
