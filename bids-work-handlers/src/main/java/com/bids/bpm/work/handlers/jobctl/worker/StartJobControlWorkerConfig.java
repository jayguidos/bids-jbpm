/**
 * ===========================================================================
 *    Copyright 2012 BIDS Holdings L.P. All rights reserved.
 * ---------------------------------------------------------------------------
 * Created on 2/3/14
 * By bidsjagu
 *
 */

package com.bids.bpm.work.handlers.jobctl.worker;

import java.io.File;


import com.bids.bpm.facts.model.JobControlRecord;
import com.bids.bpm.work.handlers.jobctl.worker.JobControlWorkerConfig;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.WorkItem;

public class StartJobControlWorkerConfig
        extends JobControlWorkerConfig
{
    public static final String IN_JOB_CONTROL_ID = "JobControlId";
    public static final String IN_JOB_IS_A_RESTART = "IsARestart";
    protected boolean restart;

    public StartJobControlWorkerConfig(String targetHost, WorkItem workItem, File scriptLogDir)
    {
        super(targetHost, workItem,scriptLogDir);
    }

    public void init(KieSession kieSession)
    {
        // these are mine
        this.jobCtlId = getStringParameter(IN_JOB_CONTROL_ID, "0000");
        this.restart = getBooleanParameter(IN_JOB_IS_A_RESTART, false);

        if (this.restart)
            this.jcrHandle = getJobControlRecordHandle(kieSession, this.jobCtlId, 1);
        else
        {
            getJobControlRecordHandle(kieSession, this.jobCtlId, 0);
            this.jcrHandle = kieSession.insert(new JobControlRecord(this.jobCtlId));
        }

        // store the job control record in the process for later use
        JobControlRecord jcr = (JobControlRecord) kieSession.getObject(this.jcrHandle);
        getProcessInstance(kieSession).setVariable(JCR_RECORD_VARIABLE, jcr);

        // now I can safely call super and expect overridden methods to work
        super.init(kieSession);
    }

    @Override
    protected String extractWorkDoneIdFromParamters()
    {
        return JOB_CONTROL_WORK_PREFIX + this.jobCtlId + "_start";
    }

    @Override
    protected String extractScriptArgsFromParameter()
    {
        if (this.restart)
            return "restartJob" + " " + this.jobCtlId;
        else
            return "startJob" + " " + this.jobCtlId;
    }
}
