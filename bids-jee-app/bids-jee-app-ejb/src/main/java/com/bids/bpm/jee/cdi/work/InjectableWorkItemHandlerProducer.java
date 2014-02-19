/**
 * ===========================================================================
 *    Copyright 2012 BIDS Holdings L.P. All rights reserved.
 * ---------------------------------------------------------------------------
 * Created on 2/19/14
 * By bidsjagu
 *
 */

package com.bids.bpm.jee.cdi.work;

import java.util.HashMap;
import java.util.Map;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;


import com.bids.bpm.jee.cdi.work.implementations.AbstractBidsWorkItemHandlerProducer;
import com.bids.bpm.jee.util.TransactionLockInterceptor;
import org.drools.core.command.impl.CommandBasedStatefulKnowledgeSession;
import org.drools.persistence.SingleSessionCommandService;
import org.jbpm.runtime.manager.api.WorkItemHandlerProducer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.WorkItemHandler;

public class InjectableWorkItemHandlerProducer
        implements WorkItemHandlerProducer

{
    // these are the map parameter keys that are in the handler 'params' map
    public static final String RUNTIME_MANAGER = "runtimeManager";
    public static final String KIE_SESSION = "ksession";
    public static final String EXECUTOR_SERVICE = "executorService";
    public static final String TASK_SERVICE = "taskService";

    @Inject
    Instance<AbstractBidsWorkItemHandlerProducer> allProducers;

    @Override
    public Map<String, WorkItemHandler> getWorkItemHandlers(String identifier, Map<String, Object> params)
    {
        HashMap<String, WorkItemHandler> wihMap = new HashMap<String, WorkItemHandler>();
        for (AbstractBidsWorkItemHandlerProducer producer : allProducers)
            wihMap.putAll(producer.getWorkItemHandlers(identifier,params));

        // hack to register interceptor
        KieSession kieSession = getKieSession(params);
        SingleSessionCommandService sscs = (SingleSessionCommandService)
                ((CommandBasedStatefulKnowledgeSession) kieSession).getCommandService();

        sscs.addInterceptor(new TransactionLockInterceptor(kieSession.getEnvironment()));

        return wihMap;
    }

    private KieSession getKieSession(Map<String, Object> injectedMap)
    {
        return (KieSession) injectedMap.get(KIE_SESSION);
    }


}
