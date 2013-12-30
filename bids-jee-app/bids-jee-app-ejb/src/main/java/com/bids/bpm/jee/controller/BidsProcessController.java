/**
 * ===========================================================================
 *    Copyright 2012 BIDS Holdings L.P. All rights reserved.
 * ---------------------------------------------------------------------------
 * Created on 12/27/13
 * By bidsjagu
 *
 */

package com.bids.bpm.jee.controller;

import javax.ejb.Stateful;
import javax.inject.Inject;


import com.bids.bpm.facts.model.BidsDay;
import com.bids.bpm.jee.cdi.BidsProcessEngine;
import com.bids.bpm.shared.BidsBPMConstants;
import org.jbpm.kie.services.impl.KModuleDeploymentUnit;
import org.kie.internal.runtime.manager.context.EmptyContext;

@Stateful
public class BidsProcessController
{
    @Inject
    private BidsProcessEngine engine;

    public String deployModule(String artifactId, String version)
    {
        KModuleDeploymentUnit unit = new KModuleDeploymentUnit(BidsBPMConstants.BIDS_MAVEN_GROUP, artifactId, version);
        engine.deployUnit(unit);
        engine.getRuntimeManager(unit.getIdentifier()).getRuntimeEngine(EmptyContext.get());
        return unit.getIdentifier();
    }

    public int createBidsDaySession(String unitIdentifier, BidsDay day)
    {
        return engine.getRuntimeEngine(unitIdentifier).getKieSession().getId();
    }
}
