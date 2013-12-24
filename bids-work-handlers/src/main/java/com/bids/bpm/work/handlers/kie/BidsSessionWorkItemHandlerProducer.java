/**
 * ===========================================================================
 *    Copyright 2012 BIDS Holdings L.P. All rights reserved.
 * ---------------------------------------------------------------------------
 * Created on 12/19/13
 * By bidsjagu
 *
 */

package com.bids.bpm.work.handlers.kie;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Produces;


import org.jbpm.runtime.manager.api.WorkItemHandlerProducer;
import org.kie.api.runtime.process.WorkItemHandler;

@SessionScoped
public class BidsSessionWorkItemHandlerProducer
    implements WorkItemHandlerProducer,
               Serializable
{

    public Map<String, WorkItemHandler> getWorkItemHandlers(String s, Map<String, Object> stringObjectMap)
    {
        return new HashMap<String, WorkItemHandler>();
    }

}
