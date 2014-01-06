/**
 * ===========================================================================
 *    Copyright 2012 BIDS Holdings L.P. All rights reserved.
 * ---------------------------------------------------------------------------
 * Created on 12/27/13
 * By bidsjagu
 *
 */

package com.bids.bpm.jee.controller;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateful;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;


import com.bids.bpm.facts.model.BidsDay;
import com.bids.bpm.jee.cdi.BidsProcessEngine;
import com.bids.bpm.jee.data.BidsDayProducer;
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
    private BidsDeploymentsProducer deploymentsProducer;

    @Inject
    private BidsDayProducer bidsDayProducer;

    @PersistenceContext(unitName = "org.jbpm.domain")
    EntityManager em;

    public BidsDeployment deployModule(BidsDay bidsDay, String artifactId, String version)
    {

        // deploy the module identified by the GAV
        KModuleDeploymentUnit unit = new BidsDeploymentUnit(bidsDay, BidsBPMConstants.BIDS_MAVEN_GROUP, artifactId, version);
        engine.deployUnit(unit);

        // convert bids day to a persistent version
        if ( bidsDayProducer.getBidsDays().containsKey(bidsDay.getDate() ) )
            bidsDay = bidsDayProducer.getBidsDays().get(bidsDay.getDate());
        else
            em.persist(bidsDay);

        // add all facts
        engine.getRuntimeEngine(unit.getIdentifier()).getKieSession().insert(bidsDay);

        // remember our deployment
        BidsDeployment bd = new BidsDeployment();
        bd.setDeployIdentifier(unit.getIdentifier());
        bd.setArtifactId(artifactId);
        bd.setVersion(version);
        bd.setBidsDay(bidsDay);
        em.persist(bd);

        return bd;
    }

    public int createBidsDaySession(String unitIdentifier, BidsDay day)
    {
        return engine.getRuntimeEngine(unitIdentifier).getKieSession().getId();
    }

    public BidsDeployment findDeployment(Long bdId)
    {
        for (BidsDeployment bidsDeployment : deploymentsProducer.getDeployments())
            if ( bidsDeployment.getId().equals(bdId))
                return bidsDeployment;
        return null;
    }

    public boolean undeployModule(Long bdId)
    {
        Map<Date,BidsDay> bidsDays = bidsDayProducer.getBidsDays();
        BidsDeployment bd = findDeployment(bdId);
        if ( bd == null )
            throw new RuntimeException("Could not undeploy. Deployment does not exist: " + bdId);

        // reassemble the unit key and undeploy
        KModuleDeploymentUnit unit = new BidsDeploymentUnit(bd.getBidsDay(), BidsBPMConstants.BIDS_MAVEN_GROUP, bd.getArtifactId(), bd.getVersion());
        engine.undeployUnit(unit);

        // eliminate the deployment
        em.remove(bd);
        return false;
    }

    public List<BidsDeployment> getDeployments()
    {
        return deploymentsProducer.getDeployments();
    }

    private BidsDeploymentUnit fromBidsDeployment(BidsDeployment bd)
    {
        return new BidsDeploymentUnit(bd.getBidsDay(), BidsBPMConstants.BIDS_MAVEN_GROUP, bd.getArtifactId(), bd.getVersion());
    }
}
