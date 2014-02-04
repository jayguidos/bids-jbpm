/**
 * ===========================================================================
 *    Copyright 2012 BIDS Holdings L.P. All rights reserved.
 * ---------------------------------------------------------------------------
 * Created on 1/25/14
 * By bidsjagu
 *
 */

package com.bids.bpm.work.handlers.jobctl;

import java.io.File;


import com.bids.bpm.facts.model.JobControlRecord;
import com.bids.bpm.work.handlers.BidsWorkItemHandlerResults;
import com.bids.bpm.work.handlers.bash.BashScriptWorkItemHandler;
import org.kie.api.runtime.process.WorkItem;

public class JobControlWorkItemHandler
        extends BashScriptWorkItemHandler
{

    public static final String OUT_JOB_CONTROL_RECORD = "JobControlRecord";
    public static final String JOB_CONTROL_ERROR_SIGNAL = "JobControlError";
    private JobControlType jobControlType;

    public JobControlWorkItemHandler(File logBaseDir)
    {
        super(JOB_CONTROL_ERROR_SIGNAL,logBaseDir);
    }

    public JobControlType getJobControlType()
    {
        return jobControlType;
    }

    public void setJobControlType(JobControlType jobControlType)
    {
        this.jobControlType = jobControlType;
    }

    @Override
    protected BidsWorkItemWorker makeWorkItemWorker(WorkItem workItem)
    {
        // just call the job control script here
        JobControlWorkerConfig config;
        switch (jobControlType)
        {
            case startJob:
                config = new StartJobControlWorkerConfig(workItem,getLogBaseDir());
                break;
            case endJob:
                config = new EndJobControlWorkerConfig(workItem,getLogBaseDir());
                break;
            default:
                throw new RuntimeException("Unhandled Job Control type");
        }
        config.init(getKsession());
        return new JobControlWorker(config);
    }

    protected class JobControlWorker
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
            JobControlRecord jcr = (JobControlRecord) getKsession().getObject(config.getJcrHandle());
            jcr.transitionTo(rr.getReturnCode() == 0);
            getKsession().update(config.getJcrHandle(), jcr);

            rr.addResult(OUT_JOB_CONTROL_RECORD, jcr);

            return rr;
        }

    }
}
