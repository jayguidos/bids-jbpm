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


import com.bids.bpm.work.handlers.bash.BashScriptWorkItemHandler;
import com.bids.bpm.work.handlers.jobctl.worker.EndJobControlWorkerConfig;
import com.bids.bpm.work.handlers.jobctl.worker.JobControlWorker;
import com.bids.bpm.work.handlers.jobctl.worker.JobControlWorkerConfig;
import com.bids.bpm.work.handlers.jobctl.worker.StartJobControlWorkerConfig;
import com.bids.bpm.work.handlers.worker.BidsWorkItemWorker;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.WorkItem;

public class JobControlWorkItemHandler
        extends BashScriptWorkItemHandler
{

    public static final String OUT_JOB_CONTROL_RECORD = "JobControlRecord";
    public static final String JOB_CONTROL_ERROR_SIGNAL = "JobControlError";
    private JobControlType jobControlType;

    public JobControlWorkItemHandler(KieSession kieSession, File logBaseDir)
    {
        super(kieSession,JOB_CONTROL_ERROR_SIGNAL, logBaseDir);
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
                config = new StartJobControlWorkerConfig(getTargetHost(), workItem, getLogBaseDir());
                break;
            case endJob:
                config = new EndJobControlWorkerConfig(getTargetHost(), workItem, getLogBaseDir());
                break;
            default:
                throw new RuntimeException("Unhandled Job Control type");
        }
        config.init(getKsession());
        return new JobControlWorker(config);
    }

}
