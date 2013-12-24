/**
 * ===========================================================================
 *    Copyright 2012 BIDS Holdings L.P. All rights reserved.
 * ---------------------------------------------------------------------------
 * Created on 10/28/13
 * By bidsjagu
 *
 */

package com.bids.bpm.work.handlers;

import org.kie.api.runtime.KieSession;

public interface KieSessionAware
{
    public void setKsession(KieSession ksession);
}
