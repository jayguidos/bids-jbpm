/**
 * ===========================================================================
 *    Copyright 2012 BIDS Holdings L.P. All rights reserved.
 * ---------------------------------------------------------------------------
 * Created on 1/6/14
 * By bidsjagu
 *
 */

package com.bids.bpm.jee.cdi.work.implementations;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;


import com.bids.bpm.jee.util.BidsJBPMConfiguration;
import static com.bids.bpm.shared.BidsBPMConstants.BIDS_SQL_RUNNER_ITEM_HANDLER;
import com.bids.bpm.work.handlers.implementations.sql.SQLRunnerWorkItemHandlerFactory;
import org.jbpm.runtime.manager.impl.SingletonRuntimeManager;
import org.kie.api.runtime.process.WorkItemHandler;

public class SQLRunnerWorkItemHandlerProducer
        extends AbstractBidsWorkItemHandlerProducer
{

    @Inject
    private BidsJBPMConfiguration config;

    public Map<String, WorkItemHandler> getWorkItemHandlers(String identifier, Map<String, Object> params)
    {
        HashMap<String, WorkItemHandler> wihMap = new HashMap<String, WorkItemHandler>();
        SingletonRuntimeManager runtimeManager = getRuntimeManager(params);
        SQLRunnerWorkItemHandlerFactory factory = new SQLRunnerWorkItemHandlerFactory(runtimeManager, config.getGlobalLogDir(), config.getBashHostName());
        wihMap.put(BIDS_SQL_RUNNER_ITEM_HANDLER, factory.makeWorkItem());
        return wihMap;
    }

}
