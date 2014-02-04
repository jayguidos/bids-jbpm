/**
 * ===========================================================================
 *    Copyright 2012 BIDS Holdings L.P. All rights reserved.
 * ---------------------------------------------------------------------------
 * Created on 10/28/13
 * By bidsjagu
 *
 */

package com.bids.bpm.work.handlers;

import java.io.File;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicReference;


import com.bids.bpm.facts.model.WorkDone;
import org.apache.log4j.Logger;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.ObjectFilter;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.runtime.manager.context.EmptyContext;

public abstract class BidsWorkItemHandler
        implements WorkItemHandler
{
    public static final String THREAD_NAME_PREFIX = "BidsWorkItemWorkerThread-";
    private static final Logger log = Logger.getLogger(BidsWorkItemHandler.class);
    private static int threadCounter = 1;
    private final String errorSignalName;
    private final File logBaseDir;
    private AtomicReference<Thread> workerThread = new AtomicReference<Thread>();
    private AtomicReference<BidsWorkItemWorker> worker = new AtomicReference<BidsWorkItemWorker>();
    private RuntimeManager runtimeManager;

    protected BidsWorkItemHandler(String errorSignalName, File logBaseDir)
    {
        this.errorSignalName = errorSignalName;
        this.logBaseDir = logBaseDir;
    }

    public void abortWorkItem(WorkItem workItem, WorkItemManager manager)
    {
        BidsWorkItemWorker w = worker.get();
        if (w != null)
            log.warn("Aborting work item: " + w.config.getWorkItem());
        Thread t = workerThread.getAndSet(null);
        if (t != null)
        {
            t.interrupt();
            try
            {
                t.join();
            } catch (InterruptedException ignored)
            {
            }
        }
        log.warn("Aborted work item: " + w.config.getWorkItem());
    }

    public void executeWorkItem(WorkItem workItem, WorkItemManager manager)
    {

        // be sure to catch exceptions on this thread
        try
        {
            worker.set(makeWorkItemWorker(workItem));
        } catch (Exception e)
        {
            getKsession().signalEvent(errorSignalName, e);
            if (e instanceof RuntimeException)
                throw (RuntimeException) e;
            else
                throw new RuntimeException(e);
        }

        // exceptions on the worker thread will be caught by instances of BidsWorkItemWorker
        workerThread.set(new Thread(worker.get(), THREAD_NAME_PREFIX + (threadCounter++)));
        workerThread.get().start();
    }

    public String getErrorSignalName()
    {
        return errorSignalName;
    }

    public KieSession getKsession()
    {
        // presumes a global singleton engine for this runtime, otherwise I would have to provide
        // something in the context
        return runtimeManager.getRuntimeEngine(EmptyContext.get()).getKieSession();
    }

    public RuntimeManager getRuntimeManager()
    {
        return runtimeManager;
    }

    public void setRuntimeManager(RuntimeManager runtimeManager)
    {
        this.runtimeManager = runtimeManager;
    }

    public File getLogBaseDir()
    {
        return logBaseDir;
    }

    protected abstract BidsWorkItemWorker makeWorkItemWorker(WorkItem workItem);

    public abstract class BidsWorkItemWorker
            implements Runnable
    {
        private final BidsWorkItemConfig config;

        public BidsWorkItemWorker(BidsWorkItemConfig config)
        {
            this.config = config;
        }

        public abstract BidsWorkItemHandlerResults doWorkInThread()
                throws InterruptedException;

        public void run()
        {

            BidsWorkItemHandlerResults rr;
            FactHandle oldWorkHandle = findOldWorkDone();
            WorkDone oldWork = oldWorkHandle == null ? null : (WorkDone) getKsession().getObject(oldWorkHandle);

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
                    getKsession().signalEvent(errorSignalName, e);
                    if (e instanceof RuntimeException)
                        throw (RuntimeException) e;
                    else
                        throw new RuntimeException(e);
                }

                // mark the work as done (or redone)
                if (oldWork == null)
                    getKsession().insert(rr.getWorkDone());
                else
                    getKsession().update(oldWorkHandle, rr.getWorkDone());

                // if the work task has been configured to signal if there was an error result then do it now
                if (rr.getReturnCode() != 0 && config.isSignalOnErrorResult())
                    getKsession().signalEvent(errorSignalName, config.getWorkDoneId());
            }

            // notify manager that work item has been completed.  We cannot keep a handle to
            // the WorkItemManager around - the transaction it is within will have been
            // closed by the time we get here.  Instead get it directly from the session when
            // we need it
            getKsession().getWorkItemManager().completeWorkItem(config.getWorkItemId(), rr.getResults());

            workerThread.set(null);
            worker.set(null);
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
            Collection<FactHandle> workDoneHandles = getKsession().getFactHandles(doneTaskFilter);
            return workDoneHandles.size() == 0 ? null : workDoneHandles.iterator().next();
        }

    }
}
