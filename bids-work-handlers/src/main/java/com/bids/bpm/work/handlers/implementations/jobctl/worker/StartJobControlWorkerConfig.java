/**
 * ===========================================================================
 *    Copyright 2012 BIDS Holdings L.P. All rights reserved.
 * ---------------------------------------------------------------------------
 * Created on 2/3/14
 * By bidsjagu
 *
 */

package com.bids.bpm.work.handlers.implementations.jobctl.worker;

import java.io.File;


import com.bids.bpm.facts.model.JobControlRecord;
import com.bids.bpm.facts.model.WorkDone;
import com.bids.bpm.work.handlers.support.fact.KieSessionBidsFactManager;
import org.apache.log4j.Logger;
import org.drools.core.ObjectFilter;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.rule.FactHandle;

public class StartJobControlWorkerConfig
        extends JobControlWorkerConfig
{
    public static final String IN_JOB_CONTROL_ID = "JobControlId";
    public static final String IN_JOB_IS_A_RESTART = "IsARestart";
    protected boolean restart;
    private static final Logger logger = Logger.getLogger(StartJobControlWorkerConfig.class);
    public StartJobControlWorkerConfig(String targetHost, WorkItem workItem, File scriptLogDir)
    {
        super(targetHost, workItem,scriptLogDir);
    }

    public void init(KieSession kieSession)
    {
        KieSessionBidsFactManager factManager = new KieSessionBidsFactManager(kieSession);

        // these are mine
        this.jobCtlId = getStringParameter(IN_JOB_CONTROL_ID, "0000");
        this.restart = getBooleanParameter(IN_JOB_IS_A_RESTART, false);

        // see if a Job Control Record is already lurking
        this.jcrHandle = getJobControlRecordHandle(kieSession, this.jobCtlId);
        JobControlRecord jcr;

        // if this is a restart we can just reset the state to unstarted and try again
        if ( this.jcrHandle != null )
        {
            jcr = factManager.get(this.jcrHandle);
            jcr.setState(JobControlRecord.State.unstarted);
            factManager.update(jcr,this.jcrHandle);
            removeWorkDones(kieSession);
        }
        else
        {
            jcr = new JobControlRecord(this.jobCtlId);
            this.jcrHandle = factManager.addAndReturnHandle(jcr);
        }

        // store the job control record in the process for later use
        getProcessInstance(kieSession).setVariable(JCR_RECORD_VARIABLE, jcr);

        // now I can safely call super and expect overridden methods to work
        super.init(jcr,kieSession);
    }

    private void removeWorkDones(KieSession kieSession)
    {
        ObjectFilter workDoneFilter = new ObjectFilter()
        {
            public boolean accept(Object object)
            {
                if (!(object instanceof WorkDone))
                    return false;
                WorkDone wd = (WorkDone) object;
                return wd.getName().startsWith(JOB_CONTROL_WORK_PREFIX + jobCtlId + "_");
            }
        };
        for (FactHandle fh : kieSession.getFactHandles(workDoneFilter))
        {
            logger.info("JobControl Restart: removed work done record: " + kieSession.getObject(fh));
            kieSession.delete(fh);
        }
    }

    @Override
    protected String extractWorkDoneNameFromParameters()
    {
        return JOB_CONTROL_WORK_PREFIX + this.jobCtlId + "_start";
    }

    @Override
    protected String extractScriptArgsFromParameters()
    {
        if (this.restart)
            return "restartJob" + " " + this.jobCtlId;
        else
            return "startJob" + " " + this.jobCtlId;
    }
}
