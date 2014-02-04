/**
 * ===========================================================================
 *    Copyright 2012 BIDS Holdings L.P. All rights reserved.
 * ---------------------------------------------------------------------------
 * Created on 2/3/14
 * By bidsjagu
 *
 */

package com.bids.bpm.work.handlers.jobctl;

import java.io.File;


import com.bids.bpm.facts.model.JobControlRecord;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.WorkItem;

public class EndJobControlWorkerConfig
        extends JobControlWorkerConfig
{
    public static final String IN_JOB_FAILED = "JobFailed";

    private boolean jobFailed;

    public EndJobControlWorkerConfig(WorkItem workItem, File scriptLogDir)
    {
        super(workItem, scriptLogDir);
    }

    @Override
    public void init(KieSession kieSession)
    {
        // retrieve the current job control record we stored in the process when
        // the start job was called
        JobControlRecord jcr = (JobControlRecord) getProcessInstance(kieSession).getVariable(JCR_RECORD_VARIABLE);
        if (jcr == null)
            throw new RuntimeException("End Job cannot find Job Control Record in the process");

        // use that record to configure me
        this.jobCtlId = jcr.getJobId();

        this.jcrHandle = getJobControlRecordHandle(kieSession, this.jobCtlId, 1);
        if (!jcr.equals(kieSession.getObject(this.jcrHandle)))
            throw new RuntimeException("Job control record in agenda does not match the one stored in the process");
        this.jobFailed = getBooleanParameter(IN_JOB_FAILED, false);

        // I can now safely call the super and expect it to work
        super.init(kieSession);
    }

    @Override
    protected String extractWorkDoneIdFromParamters()
    {
        return JOB_CONTROL_WORK_PREFIX + this.jobCtlId + "_end";
    }

    @Override
    protected String extractScriptArgsFromParameter()
    {
        return (this.jobFailed ? "failJob" : "completeJob") + " " + this.jobCtlId;
    }
}
