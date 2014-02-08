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


import com.bids.bpm.work.handlers.worker.BidsWorkItemWorker;
import com.bids.bpm.work.handlers.worker.BpmnSignalThrower;
import org.apache.log4j.Logger;
import org.jbpm.runtime.manager.impl.SingletonRuntimeManager;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;
import org.kie.internal.runtime.manager.context.EmptyContext;

public abstract class BidsWorkItemHandler
        implements WorkItemHandler
{
    public static final String THREAD_NAME_PREFIX = "BidsWorkItemWorkerThread-";
    private static final Logger log = Logger.getLogger(BidsWorkItemHandler.class);
    private static int threadCounter = 1;
    private final File logBaseDir;
    private AtomicReference<Thread> workerThread = new AtomicReference<Thread>();
    private AtomicReference<BidsWorkItemWorker> worker = new AtomicReference<BidsWorkItemWorker>();
    private SingletonRuntimeManager runtimeManager;
    private BpmnSignalThrower signalThrower;

    protected BidsWorkItemHandler(String errorSignalName, File logBaseDir)
    {
        this.signalThrower = new BpmnSignalThrower(errorSignalName);
        this.logBaseDir = logBaseDir;
    }

    public void abortWorkItem(WorkItem workItem, WorkItemManager manager)
    {
        BidsWorkItemWorker w = worker.get();
        if (w != null)
            log.warn("Aborting work item: " + w.getConfig().getWorkItem());
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
        log.warn("Aborted work item: " + w.getConfig().getWorkItem());
    }

    public void executeWorkItem(WorkItem workItem, WorkItemManager manager)
    {

        // be sure to catch exceptions on this thread
        try
        {
            worker.set(makeWorkItemWorker(workItem));
        } catch (Exception e)
        {
            signalThrower.signalEvent(getKsession(), e);
            if (e instanceof RuntimeException)
                throw (RuntimeException) e;
            else
                throw new RuntimeException(e);
        }

        // exceptions on the worker thread will be caught by instances of BidsWorkItemWorker
        workerThread.set(new Thread(new InternalRunner(), THREAD_NAME_PREFIX + (threadCounter++)));
        workerThread.get().start();
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

    public void setRuntimeManager(SingletonRuntimeManager runtimeManager)
    {
        this.runtimeManager = runtimeManager;
    }

    public File getLogBaseDir()
    {
        return logBaseDir;
    }

    protected abstract BidsWorkItemWorker makeWorkItemWorker(WorkItem workItem);

    private class InternalRunner
        implements Runnable
    {

        public void run()
        {
            // we need a session that is for this thread only
            KieSession localKieSession = runtimeManager.getFactory().findKieSessionById(runtimeManager.getRuntimeEngine(EmptyContext.get()).getKieSession().getId());

            // now run
            try
            {
                BidsWorkItemWorker w = worker.get();
                w.setKieSession(localKieSession);
                if ( w != null )
                {
                    w.setSignalThrower(signalThrower);
                    w.run();
                }
            } catch (Exception e)
            {
                e.printStackTrace();
            } finally
            {
                worker.set(null);
                workerThread.set(null);
            }
        }
    }
}
