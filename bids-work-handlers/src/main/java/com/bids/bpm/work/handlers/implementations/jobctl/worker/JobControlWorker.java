/**
 * ===========================================================================
 *    Copyright 2012 BIDS Holdings L.P. All rights reserved.
 * ---------------------------------------------------------------------------
 * Created on 2/8/14
 * By bidsjagu
 *
 */

package com.bids.bpm.work.handlers.implementations.jobctl.worker;

import com.bids.bpm.facts.model.JobControlRecord;
import com.bids.bpm.work.handlers.support.BidsWorkItemHandlerResults;
import com.bids.bpm.work.handlers.implementations.bash.worker.BashScriptWorker;
import com.bids.bpm.work.handlers.implementations.jobctl.JobControlWorkItemHandler;
import org.apache.log4j.Logger;

public class JobControlWorker
        extends BashScriptWorker
{
    private static final Logger logger = Logger.getLogger(JobControlWorker.class);
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
        JobControlRecord jcr = factManager.get(config.getJcrHandle());
        jcr.transitionTo(rr.getReturnCode() == 0);
        factManager.update(jcr, config.getJcrHandle());
        logger.info("Job State Update: " + jcr);
        rr.addResult(JobControlWorkItemHandler.OUT_JOB_CONTROL_RECORD, jcr);

        return rr;
    }

}
