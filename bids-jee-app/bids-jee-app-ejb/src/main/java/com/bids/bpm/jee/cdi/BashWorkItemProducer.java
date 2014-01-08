/**
 * ===========================================================================
 *    Copyright 2012 BIDS Holdings L.P. All rights reserved.
 * ---------------------------------------------------------------------------
 * Created on 1/6/14
 * By bidsjagu
 *
 */

package com.bids.bpm.jee.cdi;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;


import com.bids.bpm.jee.util.GlobalLogDir;
import static com.bids.bpm.shared.BidsBPMConstants.BIDS_BASH_WORK_ITEM_HANDLER;
import com.bids.bpm.work.handlers.bash.BashScriptWorkItemHandlerFactory;
import org.jbpm.runtime.manager.api.WorkItemHandlerProducer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.task.TaskService;
import org.kie.internal.executor.api.ExecutorService;

public class BashWorkItemProducer
        implements WorkItemHandlerProducer
{

    // these constants are defined as KIE internals
    public static final String RUNTIME_MANAGER = "runtimeManager";
    public static final String KIE_SESSION = "ksession";
    public static final String EXECUTOR_SERVICE = "executorService";
    public static final String TASK_SERVICE = "taskService";

    @Inject
    @GlobalLogDir
    private File logBaseDir;

    public Map<String, WorkItemHandler> getWorkItemHandlers(String identifier, Map<String, Object> params)
    {
        KieSession kieSession = getKieSession(params);
        HashMap<String, WorkItemHandler> wihMap = new HashMap<String, WorkItemHandler>();
        BashScriptWorkItemHandlerFactory factory = new BashScriptWorkItemHandlerFactory(kieSession, logBaseDir);
        wihMap.put(BIDS_BASH_WORK_ITEM_HANDLER, factory.makeWorkItem());
        return wihMap;
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
