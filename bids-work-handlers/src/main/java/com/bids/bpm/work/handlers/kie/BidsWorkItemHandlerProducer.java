/**
 * ===========================================================================
 *    Copyright 2012 BIDS Holdings L.P. All rights reserved.
 * ---------------------------------------------------------------------------
 * Created on 12/19/13
 * By bidsjagu
 *
 */

package com.bids.bpm.work.handlers.kie;

import java.util.HashMap;
import java.util.Map;


import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;


import org.jbpm.runtime.manager.api.WorkItemHandlerProducer;
import org.kie.api.runtime.process.WorkItemHandler;

public class BidsWorkItemHandlerProducer
    implements WorkItemHandlerProducer
{

    @ApplicationScoped
    public Map<String, WorkItemHandler> getWorkItemHandlers(String s, Map<String, Object> stringObjectMap)
    {
        return new HashMap<String, WorkItemHandler>();
    }

    @ApplicationScoped
    @Produces
    public WorkItemHandlerProducer getOtherGlobalWorkItemHandlerProducer()
    {
        return new WorkItemHandlerProducer()
        {
            public Map<String, WorkItemHandler> getWorkItemHandlers(String s, Map<String, Object> stringObjectMap)
            {
                return new HashMap<String, WorkItemHandler>();
            }
        };
    }

}
