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
import static com.bids.bpm.shared.BidsBPMConstants.BIDS_JOB_CONTROL_END_ITEM_HANDLER;
import static com.bids.bpm.shared.BidsBPMConstants.BIDS_JOB_CONTROL_START_ITEM_HANDLER;
import static com.bids.bpm.work.handlers.jobctl.JobControlType.endJob;
import static com.bids.bpm.work.handlers.jobctl.JobControlType.startJob;
import com.bids.bpm.work.handlers.jobctl.JobControlWorkItemHandlerFactory;
import org.jbpm.runtime.manager.impl.SingletonRuntimeManager;
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
        SingletonRuntimeManager runtimeManager = getRuntimeManager(params);
        wihMap.put(
                BIDS_JOB_CONTROL_START_ITEM_HANDLER,
                new JobControlWorkItemHandlerFactory(startJob, runtimeManager, config.getGlobalLogDir(), config.getBashHostName()).makeWorkItem()
        );
        wihMap.put(
                BIDS_JOB_CONTROL_END_ITEM_HANDLER,
                new JobControlWorkItemHandlerFactory(endJob, runtimeManager, config.getGlobalLogDir(), config.getBashHostName()).makeWorkItem()
        );
        return wihMap;
    }
}
