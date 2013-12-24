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
import java.util.concurrent.atomic.AtomicReference;


import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;

public abstract class BidsWorkItemHandler
        implements WorkItemHandler,
                   KieSessionAware,
                   LogBaseDirAware
{
    public static final String THREAD_NAME_PREFIX = "BidsWorkItemWorkerThread-";
    private static int threadCounter = 1;
    protected KieSession ksession;
    protected File logBaseDir;
    private AtomicReference<Thread> workerThread = new AtomicReference<Thread>();
    private AtomicReference<BidsWorkItemWorker> worker = new AtomicReference<BidsWorkItemWorker>();

    public void abortWorkItem(WorkItem workItem, WorkItemManager manager)
    {
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
    }

    public void executeWorkItem(WorkItem workItem, WorkItemManager manager)
    {
        worker.set(makeWorkItemWorker(workItem));
        workerThread.set(new Thread(worker.get(), THREAD_NAME_PREFIX + (threadCounter++)));
        workerThread.get().start();
    }

    public File getLogBaseDir()
    {
        return logBaseDir;
    }

    public void setLogBaseDir(File logBaseDir)
    {
        this.logBaseDir = logBaseDir;
    }

    public KieSession getKsession()
    {
        return ksession;
    }

    public void setKsession(KieSession ksession)
    {
        this.ksession = ksession;
    }

    protected abstract BidsWorkItemWorker makeWorkItemWorker(WorkItem workItem);

    public abstract class BidsWorkItemWorker
            implements Runnable
    {
        protected final long workItemId;
        protected final File workItemLogDir;
        protected final WorkItem workItem;

        public BidsWorkItemWorker(WorkItem workItem)
        {
            this.workItem = workItem;
            ProcessInstance process = ksession.getProcessInstance(workItem.getProcessInstanceId());
            this.workItemLogDir = new File(logBaseDir, process.getProcessName());
            this.workItemId = workItem.getId();
        }

        public abstract BidsWorkItemHandlerResults doWorkInThread();

        public void run()
        {

            BidsWorkItemHandlerResults rr = doWorkInThread();

            // mark the work as done
            ksession.insert(rr.getWorkDone());

            // notify manager that work item has been completed.  We cannot keep a handle to
            // the WorkItemManager around - the transaction it is within will have been
            // closed by the time we get here.  Instead get it directly from the session when
            // we need it
            ksession.getWorkItemManager().completeWorkItem(workItemId, rr.getResults());
            workerThread.set(null);
            worker.set(null);
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

    }
}
