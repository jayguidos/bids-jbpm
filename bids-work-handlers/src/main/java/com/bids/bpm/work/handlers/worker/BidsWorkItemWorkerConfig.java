/**
 * ===========================================================================
 *    Copyright 2012 BIDS Holdings L.P. All rights reserved.
 * ---------------------------------------------------------------------------
 * Created on 2/3/14
 * By bidsjagu
 *
 */

package com.bids.bpm.work.handlers.worker;

import java.io.File;
import java.util.Collection;


import com.bids.bpm.facts.model.BidsDay;
import org.jbpm.ruleflow.instance.RuleFlowProcessInstance;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.ObjectFilter;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.process.WorkItem;

public class BidsWorkItemWorkerConfig
{
    public static final String IN_WORK_ID = "WorkId";
    public static final String IN_SIGNAL_ON_ERROR = "SignalOnError";
    public static final String IN_ONCE_ONLY = "OnceOnly";
    protected final WorkItem workItem;
    protected final File logBaseDir;
    protected File workItemLogDir;
    protected Long workItemId;
    protected String workDoneId;
    protected boolean signalOnErrorResult;
    protected boolean onceOnly;

    public BidsWorkItemWorkerConfig(WorkItem workItem, File logBaseDir)
    {
        this.workItem = workItem;
        this.workItemId = workItem.getId();
        this.logBaseDir = logBaseDir;
    }

    public void init(KieSession kieSession)
    {
        this.signalOnErrorResult = getBooleanParameter(IN_SIGNAL_ON_ERROR, true);
        this.onceOnly = getBooleanParameter(IN_ONCE_ONLY, false);
        this.workDoneId = extractWorkDoneIdFromParamters();

        // derive a working log directory based on the Bids Day and the current process
        ProcessInstance process = kieSession.getProcessInstance(workItem.getProcessInstanceId());
        BidsDay bidsDay = findBidsDay(kieSession);

        File workItemLogBaseDir = bidsDay == null ? logBaseDir : new File(logBaseDir, bidsDay.getName());
        workItemLogBaseDir = new File(workItemLogBaseDir, process.getProcessName());
        this.workItemLogDir = new File(workItemLogBaseDir, workDoneId);
        this.workItemLogDir.mkdirs();
    }

    public boolean isOnceOnly()
    {
        return onceOnly;
    }

    public void setOnceOnly(boolean onceOnly)
    {
        this.onceOnly = onceOnly;
    }

    public boolean isSignalOnErrorResult()
    {
        return signalOnErrorResult;
    }

    public void setSignalOnErrorResult(boolean signalOnErrorResult)
    {
        this.signalOnErrorResult = signalOnErrorResult;
    }

    public String getWorkDoneId()
    {
        return workDoneId;
    }

    public void setWorkDoneId(String workDoneId)
    {
        this.workDoneId = workDoneId;
    }

    public File getWorkItemLogDir()
    {
        return workItemLogDir;
    }

    public void setWorkItemLogDir(File workItemLogDir)
    {
        this.workItemLogDir = workItemLogDir;
    }

    public Long getWorkItemId()
    {
        return workItemId;
    }

    public void setWorkItemId(Long workItemId)
    {
        this.workItemId = workItemId;
    }

    public WorkItem getWorkItem()
    {
        return workItem;
    }

    protected String extractWorkDoneIdFromParamters()
    {
        return getStringParameter(IN_WORK_ID, this.workItemId.toString());
    }

    @SuppressWarnings("unchecked")
    private BidsDay findBidsDay(KieSession kieSession)
    {
        Collection<BidsDay> bidsDays = (Collection<BidsDay>) kieSession.getObjects(new ObjectFilter()
        {
            public boolean accept(Object object)
            {
                return object instanceof BidsDay;
            }
        });
        return bidsDays.size() == 0 ? null : bidsDays.iterator().next();
    }

    protected Object getObjectParameter(String name, Object def)
    {
        Object val = def;
        if (workItem.getParameter(name) != null && ((String) workItem.getParameter(name)).length() > 0)
            return workItem.getParameter(name);
        else
            return val;
    }

    protected boolean getBooleanParameter(String name, boolean def)
    {
        boolean val = def;
        if (workItem.getParameter(name) != null && ((String) workItem.getParameter(name)).length() > 0)
            return Boolean.valueOf((String) workItem.getParameter(name));
        else
            return val;
    }

    protected String getStringParameter(String name, String def)
    {
        String val = def;
        if (workItem.getParameter(name) != null && ((String) workItem.getParameter(name)).length() > 0)
            return (String) workItem.getParameter(name);
        else
            return val;
    }

    protected RuleFlowProcessInstance getProcessInstance(KieSession kieSession)
    {
        return (RuleFlowProcessInstance) kieSession.getProcessInstance(workItem.getProcessInstanceId());
    }


}
