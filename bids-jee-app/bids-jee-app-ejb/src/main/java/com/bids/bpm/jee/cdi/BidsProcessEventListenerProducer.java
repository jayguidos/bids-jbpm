/**
 * ===========================================================================
 *    Copyright 2012 BIDS Holdings L.P. All rights reserved.
 * ---------------------------------------------------------------------------
 * Created on 1/21/14
 * By bidsjagu
 *
 */

package com.bids.bpm.jee.cdi;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


import org.jbpm.runtime.manager.api.EventListenerProducer;
import org.kie.api.event.process.ProcessEventListener;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.task.TaskService;
import org.kie.internal.executor.api.ExecutorService;

public class BidsProcessEventListenerProducer
        implements EventListenerProducer<ProcessEventListener>
{

    // these constants are defined as KIE internals
    public static final String RUNTIME_MANAGER = "runtimeManager";
    public static final String KIE_SESSION = "ksession";
    public static final String EXECUTOR_SERVICE = "executorService";
    public static final String TASK_SERVICE = "taskService";

    @Override
    public List<ProcessEventListener> getEventListeners(String identifier, Map<String, Object> params)
    {
        ArrayList<ProcessEventListener> listeners = new ArrayList<ProcessEventListener>();

        return listeners;
    }

    protected RuntimeManager getRuntimeManager(Map<String, Object> injectedMap)
    {
        return (RuntimeManager) injectedMap.get(RUNTIME_MANAGER);
    }

    protected KieSession getKieSession(Map<String, Object> injectedMap)
    {
        return (KieSession) injectedMap.get(KIE_SESSION);
    }

    protected ExecutorService getExecutorService(Map<String, Object> injectedMap)
    {
        return (ExecutorService) injectedMap.get(EXECUTOR_SERVICE);
    }

    protected TaskService getTaskService(Map<String, Object> injectedMap)
    {
        return (TaskService) injectedMap.get(TASK_SERVICE);
    }

}
