/**
 * ===========================================================================
 *    Copyright 2012 BIDS Holdings L.P. All rights reserved.
 * ---------------------------------------------------------------------------
 * Created on 10/28/13
 * By bidsjagu
 *
 */

package com.bids.bpm.work.handlers.support;

import java.io.File;
import java.util.concurrent.atomic.AtomicReference;


import com.bids.bpm.work.handlers.support.fact.FactIDFactory;
import com.bids.bpm.work.handlers.support.fact.KieSessionBidsFactManager;
import com.bids.bpm.work.handlers.support.worker.AbstractBidsWorkItemWorker;
import com.bids.bpm.work.handlers.support.worker.BpmnSignalThrower;
import org.apache.log4j.Logger;
import org.jbpm.process.workitem.AbstractWorkItemHandler;
import org.jbpm.runtime.manager.impl.SingletonRuntimeManager;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemManager;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.kie.internal.runtime.manager.context.EmptyContext;

public abstract class AbstractBidsWorkItemHandler
        extends AbstractWorkItemHandler
{
    public static final String THREAD_NAME_PREFIX = "BidsWorkItemWorkerThread-";
    private static final Logger log = Logger.getLogger(AbstractBidsWorkItemHandler.class);
    private static int threadCounter = 1;
    private final String errorSignalName;
    private final File logBaseDir;
    private AtomicReference<Thread> workerThread = new AtomicReference<Thread>();
    private AtomicReference<AbstractBidsWorkItemWorker> worker = new AtomicReference<AbstractBidsWorkItemWorker>();
    private SingletonRuntimeManager runtimeManager;
    private FactIDFactory factIDFactory;

    public FactIDFactory getFactIDFactory()
    {
        return factIDFactory;
    }

    public void setFactIDFactory(FactIDFactory factIDFactory)
    {
        this.factIDFactory = factIDFactory;
    }

    protected AbstractBidsWorkItemHandler(KieSession kieSession, String errorSignalName, File logBaseDir)
    {
        super((StatefulKnowledgeSession) kieSession);
        this.errorSignalName = errorSignalName;
        this.logBaseDir = logBaseDir;
    }

    public void abortWorkItem(WorkItem workItem, WorkItemManager manager)
    {
        AbstractBidsWorkItemWorker w = worker.get();
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
        BpmnSignalThrower signalThrower = new BpmnSignalThrower(this, workItem, errorSignalName);

        // be sure to catch exceptions on this thread if something happens while I am building the worker
        try
        {
            AbstractBidsWorkItemWorker w = makeWorkItemWorker(workItem);
            w.setSignalThrower(signalThrower);
            w.setKieSession(getSession());
            this.worker.set(w);
        } catch (Exception e)
        {
            signalThrower.signalEvent(e);
            if (e instanceof RuntimeException)
                throw (RuntimeException) e;
            else
                throw new RuntimeException(e);
        }

        // exceptions on the worker thread will be caught by instances of AbstractBidsWorkItemWorker,
        // and InterruptedExceptions are allowed to propagate
        workerThread.set(new Thread(new WorkerRunner(), THREAD_NAME_PREFIX + (threadCounter++)));
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

    protected abstract AbstractBidsWorkItemWorker makeWorkItemWorker(WorkItem workItem);

    private class WorkerRunner
            implements Runnable
    {
        public void run()
        {
            AbstractBidsWorkItemWorker w = null;
            try
            {
                w = worker.get();
                w.setFactManager(new KieSessionBidsFactManager(getKsession()));
                if (w != null)
                    w.run();
            } finally
            {
                worker.set(null);
                workerThread.set(null);
                if ( w != null )
                    w.setFactManager(null);
            }
        }
    }
}
