/**
 * ===========================================================================
 *    Copyright 2012 BIDS Holdings L.P. All rights reserved.
 * ---------------------------------------------------------------------------
 * Created on 1/26/14
 * By bidsjagu
 *
 */

package com.bids.bpm.jee.cdi;

import java.util.Map;


import org.jbpm.runtime.manager.api.WorkItemHandlerProducer;
import org.jbpm.runtime.manager.impl.SingletonRuntimeManager;
import org.kie.api.runtime.KieSession;
import org.kie.api.task.TaskService;
import org.kie.internal.executor.api.ExecutorService;

public abstract class BidsWorkItemProducer
        implements WorkItemHandlerProducer
{
    // these constants are defined as KIE internals
    public static final String RUNTIME_MANAGER = "runtimeManager";
    public static final String KIE_SESSION = "ksession";
    public static final String EXECUTOR_SERVICE = "executorService";
    public static final String TASK_SERVICE = "taskService";

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
