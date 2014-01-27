/**
 * ===========================================================================
 *    Copyright 2012 BIDS Holdings L.P. All rights reserved.
 * ---------------------------------------------------------------------------
 * Created on 1/26/14
 * By bidsjagu
 *
 */

package com.bids.bpm.jee.cdi;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;


import com.bids.bpm.jee.util.BidsJBPMConfiguration;
import static com.bids.bpm.shared.BidsBPMConstants.BIDS_JOB_CONTROL_WORK_ITEM_HANDLER;
import com.bids.bpm.work.handlers.jobctl.JobControlWorkItemHandlerFactory;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.process.WorkItemHandler;

public class JobControlWorkItemProducer
        extends BidsWorkItemProducer
{

    @Inject
    private BidsJBPMConfiguration config;

    @Override
    public Map<String, WorkItemHandler> getWorkItemHandlers(String identifier, Map<String, Object> params)
    {
        HashMap<String, WorkItemHandler> wihMap = new HashMap<String, WorkItemHandler>();
        RuntimeManager runtimeManager = getRuntimeManager(params);
        JobControlWorkItemHandlerFactory factory = new JobControlWorkItemHandlerFactory(runtimeManager, config.getGlobalLogDir(), config.getBashHostName());
        wihMap.put(BIDS_JOB_CONTROL_WORK_ITEM_HANDLER, factory.makeWorkItem());
        return wihMap;
    }
}
