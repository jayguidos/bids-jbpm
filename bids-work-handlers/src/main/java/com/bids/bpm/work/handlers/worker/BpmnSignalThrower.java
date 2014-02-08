/**
 * ===========================================================================
 *    Copyright 2012 BIDS Holdings L.P. All rights reserved.
 * ---------------------------------------------------------------------------
 * Created on 2/8/14
 * By bidsjagu
 *
 */

package com.bids.bpm.work.handlers.worker;

import org.kie.api.runtime.KieSession;

public class BpmnSignalThrower
{
    private final String errorSignalName;

    public BpmnSignalThrower(String errorSignalName)
    {
        this.errorSignalName = errorSignalName;
    }

    public void signalEvent(KieSession ksession, Object data)
    {

    }
}
