/**
 * ===========================================================================
 *    Copyright 2012 BIDS Holdings L.P. All rights reserved.
 * ---------------------------------------------------------------------------
 * Created on 2/8/14
 * By bidsjagu
 *
 */

package com.bids.bpm.work.handlers.worker;

import java.util.Collection;


import com.bids.bpm.facts.model.WorkDone;
import com.bids.bpm.work.handlers.BidsWorkItemHandlerResults;
import org.apache.log4j.Logger;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.ObjectFilter;
import org.kie.api.runtime.rule.FactHandle;

public abstract class BidsWorkItemWorker
        implements Runnable
{
    private static final Logger log = Logger.getLogger(BidsWorkItemWorker.class);
    private final BidsWorkItemWorkerConfig config;
    protected KieSession kieSession;
    private BpmnSignalThrower signalThrower;

    public BidsWorkItemWorker(BidsWorkItemWorkerConfig config)
    {
        this.config = config;
    }

    public abstract BidsWorkItemHandlerResults doWorkInThread()
            throws InterruptedException;

    public void run()
    {

        BidsWorkItemHandlerResults rr;
        FactHandle oldWorkHandle = findOldWorkDone();
        WorkDone oldWork = oldWorkHandle == null ? null : (WorkDone) kieSession.getObject(oldWorkHandle);

        if (config.isOnceOnly() && oldWork != null)
        {
            log.warn("Work Id: " + config.getWorkDoneId() + " already completed - skipping this invocation");
            rr = new BidsWorkItemHandlerResults(0, oldWork);
        }
        else
        {
            try
            {
                rr = doWorkInThread();
            } catch (InterruptedException e)
            {
                log.warn("Work item thread was killed for : " + config.getWorkItem());
                //quietly exit, someone killed us intentionally
                return;
            } catch (Exception e)
            {
                // signal an exception occurred to anyone who may be listening
                signalThrower.signalEvent(kieSession, e);
                if (e instanceof RuntimeException)
                    throw (RuntimeException) e;
                else
                    throw new RuntimeException(e);
            }

            // mark the work as done (or redone)
            if (oldWork == null)
                kieSession.insert(rr.getWorkDone());
            else
                kieSession.update(oldWorkHandle, rr.getWorkDone());

            // if the work task has been configured to signal if there was an error result then do it now
            if (rr.getReturnCode() != 0 && config.isSignalOnErrorResult())
                signalThrower.signalEvent(kieSession, config.getWorkDoneId());
        }

        // notify manager that work item has been completed.  We cannot keep a handle to
        // the WorkItemManager around - the transaction it is within will have been
        // closed by the time we get here.  Instead get it directly from the session when
        // we need it
        kieSession.getWorkItemManager().completeWorkItem(config.getWorkItemId(), rr.getResults());

    }

    public void setKieSession(KieSession kieSession)
    {
        this.kieSession = kieSession;
    }

    public BpmnSignalThrower getSignalThrower()
    {
        return signalThrower;
    }

    public void setSignalThrower(BpmnSignalThrower signalThrower)
    {
        this.signalThrower = signalThrower;
    }

    public BidsWorkItemWorkerConfig getConfig()
    {
        return config;
    }

    private FactHandle findOldWorkDone()
    {
        // look for old work in the agenda
        ObjectFilter doneTaskFilter = new ObjectFilter()
        {
            public boolean accept(Object object)
            {
                return object instanceof WorkDone && ((WorkDone) object).getName().equals(config.getWorkDoneId());
            }
        };
        Collection<FactHandle> workDoneHandles = kieSession.getFactHandles(doneTaskFilter);
        return workDoneHandles.size() == 0 ? null : workDoneHandles.iterator().next();
    }

}
