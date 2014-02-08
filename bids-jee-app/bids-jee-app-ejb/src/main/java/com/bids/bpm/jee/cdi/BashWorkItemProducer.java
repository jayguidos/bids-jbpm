/**
 * ===========================================================================
 *    Copyright 2012 BIDS Holdings L.P. All rights reserved.
 * ---------------------------------------------------------------------------
 * Created on 1/6/14
 * By bidsjagu
 *
 */

package com.bids.bpm.jee.cdi;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;


import com.bids.bpm.jee.util.BidsJBPMConfiguration;
import static com.bids.bpm.shared.BidsBPMConstants.BIDS_BASH_WORK_ITEM_HANDLER;
import com.bids.bpm.work.handlers.bash.BashScriptWorkItemHandlerFactory;
import org.jbpm.runtime.manager.impl.SingletonRuntimeManager;
import org.kie.api.runtime.process.WorkItemHandler;

public class BashWorkItemProducer
        extends BidsWorkItemProducer
{

    @Inject
    private BidsJBPMConfiguration config;

    public Map<String, WorkItemHandler> getWorkItemHandlers(String identifier, Map<String, Object> params)
    {
        HashMap<String, WorkItemHandler> wihMap = new HashMap<String, WorkItemHandler>();
        SingletonRuntimeManager runtimeManager = getRuntimeManager(params);
        BashScriptWorkItemHandlerFactory factory = new BashScriptWorkItemHandlerFactory(runtimeManager, config.getGlobalLogDir(), config.getBashHostName());
        wihMap.put(BIDS_BASH_WORK_ITEM_HANDLER, factory.makeWorkItem());
        return wihMap;
    }

}
