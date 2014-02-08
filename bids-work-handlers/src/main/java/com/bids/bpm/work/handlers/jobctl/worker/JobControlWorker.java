/**
 * ===========================================================================
 *    Copyright 2012 BIDS Holdings L.P. All rights reserved.
 * ---------------------------------------------------------------------------
 * Created on 2/8/14
 * By bidsjagu
 *
 */

package com.bids.bpm.work.handlers.jobctl.worker;

import com.bids.bpm.facts.model.JobControlRecord;
import com.bids.bpm.work.handlers.BidsWorkItemHandlerResults;
import com.bids.bpm.work.handlers.bash.worker.BashScriptWorker;
import com.bids.bpm.work.handlers.jobctl.JobControlWorkItemHandler;

public class JobControlWorker
        extends BashScriptWorker
{
    private final JobControlWorkerConfig config;

    public JobControlWorker(JobControlWorkerConfig config)
    {
        super(config);
        this.config = config;
    }

    @Override
    public BidsWorkItemHandlerResults doWorkInThread()
            throws InterruptedException
    {
        BidsWorkItemHandlerResults rr = super.doWorkInThread();

        // update the current status of the job
        JobControlRecord jcr = (JobControlRecord) kieSession.getObject(config.getJcrHandle());
        jcr.transitionTo(rr.getReturnCode() == 0);
        kieSession.update(config.getJcrHandle(), jcr);

        rr.addResult(JobControlWorkItemHandler.OUT_JOB_CONTROL_RECORD, jcr);

        return rr;
    }

}
