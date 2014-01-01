/**
 * ===========================================================================
 *    Copyright 2012 BIDS Holdings L.P. All rights reserved.
 * ---------------------------------------------------------------------------
 * Created on 12/27/13
 * By bidsjagu
 *
 */

package com.bids.bpm.jee.controller;

import java.util.List;

import javax.ejb.Stateful;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;


import com.bids.bpm.facts.model.BidsDay;
import com.bids.bpm.jee.cdi.BidsProcessEngine;
import com.bids.bpm.jee.data.BidsDeploymentsProducer;
import com.bids.bpm.jee.kie.BidsDeploymentUnit;
import com.bids.bpm.jee.model.BidsDeployment;
import com.bids.bpm.shared.BidsBPMConstants;
import org.jbpm.kie.services.impl.KModuleDeploymentUnit;

@Stateful
public class BidsProcessController
{
    @Inject
    private BidsProcessEngine engine;

    @Inject
    private BidsDeploymentsProducer bdProducer;

    @PersistenceContext(unitName = "org.jbpm.domain")
    EntityManager em;

    public String deployModule(BidsDay bidsDay, String artifactId, String version)
    {
        KModuleDeploymentUnit unit = new BidsDeploymentUnit(bidsDay, BidsBPMConstants.BIDS_MAVEN_GROUP, artifactId, version);
        engine.deployUnit(unit);

        engine.getRuntimeEngine(unit.getIdentifier()).getKieSession().insert(bidsDay);

        BidsDeployment bidsDeployment = new BidsDeployment();
        bidsDeployment.setDeployIdentifier(unit.getIdentifier());
        em.persist(bidsDeployment);

        return unit.getIdentifier();
    }

    public int createBidsDaySession(String unitIdentifier, BidsDay day)
    {
        return engine.getRuntimeEngine(unitIdentifier).getKieSession().getId();
    }

    public List<BidsDeployment> getDeployments()
    {
        return bdProducer.getDeployments();
    }

}
