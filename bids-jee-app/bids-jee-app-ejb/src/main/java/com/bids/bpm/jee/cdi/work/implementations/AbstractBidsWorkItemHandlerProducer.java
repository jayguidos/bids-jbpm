/**
 * ===========================================================================
 *    Copyright 2012 BIDS Holdings L.P. All rights reserved.
 * ---------------------------------------------------------------------------
 * Created on 1/26/14
 * By bidsjagu
 *
 */

package com.bids.bpm.jee.cdi.work.implementations;

import java.util.Map;


import static com.bids.bpm.jee.cdi.work.InjectableWorkItemHandlerProducer.EXECUTOR_SERVICE;
import static com.bids.bpm.jee.cdi.work.InjectableWorkItemHandlerProducer.KIE_SESSION;
import static com.bids.bpm.jee.cdi.work.InjectableWorkItemHandlerProducer.RUNTIME_MANAGER;
import static com.bids.bpm.jee.cdi.work.InjectableWorkItemHandlerProducer.TASK_SERVICE;
import org.jbpm.runtime.manager.impl.SingletonRuntimeManager;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.task.TaskService;
import org.kie.internal.executor.api.ExecutorService;

public abstract class AbstractBidsWorkItemHandlerProducer
{
    // these constants are defined as KIE internals

    abstract public Map<String, WorkItemHandler> getWorkItemHandlers(String identifier, Map<String, Object> params);

    protected SingletonRuntimeManager getRuntimeManager(Map<String, Object> injectedMap)
    {
        return (SingletonRuntimeManager) injectedMap.get(RUNTIME_MANAGER);
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
