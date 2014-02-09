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
import java.util.Collection;


import com.bids.bpm.facts.model.JobControlRecord;
import com.bids.bpm.work.handlers.bash.worker.BashScriptWorkerConfig;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.ObjectFilter;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.rule.FactHandle;

public class JobControlWorkerConfig
        extends BashScriptWorkerConfig
{
    public static final String OPS_JOB_CONTROL_SH = "ops_job_control.sh";
    public static final String JOB_CONTROL_WORK_PREFIX = "JobControl_";
    public static final String JCR_RECORD_VARIABLE = "jcrRecord";
    protected String jobCtlId;
    protected FactHandle jcrHandle;
    private JobControlRecord jcr;

    public JobControlWorkerConfig(String targetHost, WorkItem workItem, File scriptLogDir)
    {
        super(targetHost, workItem,scriptLogDir);
    }

    public void init(JobControlRecord jcr, KieSession kieSession)
    {
        super.init(kieSession);
        this.jcr = jcr;
        // flip the default to false for logging job control
        this.logOutputToConsole = getBooleanParameter(IN_LOG_OUTPUT_TO_CONSOLE, false);
    }

    @Override
    protected String extractScriptNameFromParameters()
    {
        // the BASH script I run is hard coded
        return OPS_JOB_CONTROL_SH;
    }

    public JobControlRecord getJcr()
    {
        return jcr;
    }

    public String getJobCtlId()
    {
        return jobCtlId;
    }

    public void setJobCtlId(String jobCtlId)
    {
        this.jobCtlId = jobCtlId;
    }

    public FactHandle getJcrHandle()
    {
        return jcrHandle;
    }

    public void setJcrHandle(FactHandle jcrHandle)
    {
        this.jcrHandle = jcrHandle;
    }

    protected FactHandle getJobControlRecordHandle(KieSession kieSession, String jcId, int expectedCount)
    {
        Collection<FactHandle> factHandles = kieSession.getFactHandles(makeJobControlRecordQuery(jcId));
        if (factHandles.size() != expectedCount)
            throw new RuntimeException("JobControlRecord count mismatch.  Expected count: " + expectedCount + " but found: " + factHandles.size());
        if (factHandles.size() > 0)
            return factHandles.iterator().next();
        else
            return null;
    }

    protected ObjectFilter makeJobControlRecordQuery(final String jobControlId)
    {
        return new ObjectFilter()
        {
            public boolean accept(Object object)
            {
                return object instanceof JobControlRecord && ((JobControlRecord) object).getJobId().equals(jobControlId);
            }
        };
    }
}
