/**
 * ===========================================================================
 *    Copyright 2012 BIDS Holdings L.P. All rights reserved.
 * ---------------------------------------------------------------------------
 * Created on 2/13/14
 * By bidsjagu
 *
 */

package com.bids.bpm.jee.controller;

import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;


import com.bids.bpm.jee.cdi.BidsKieManager;
import com.bids.bpm.jee.data.BidsDeploymentsProducer;
import com.bids.bpm.jee.model.BidsDeployment;
import com.bids.bpm.jee.util.BidsJBPMConfiguration;
import org.kie.api.runtime.KieSession;

public class BidsController
{
    @Inject
    protected BidsKieManager kieManager;
    @PersistenceContext(unitName = "org.jbpm.domain")
    EntityManager em;
    @Inject
    private BidsDeploymentsProducer deploymentsProducer;
    @Inject
    protected BidsJBPMConfiguration config;

    public BidsDeployment findDeployment(Long bdId)
    {
        return em.find(BidsDeployment.class, bdId);
    }

    public List<BidsDeployment> getDeployments()
    {
        return deploymentsProducer.getDeployments();
    }

    protected KieSession extractKieSession(BidsDeployment bd)
    {
        return kieManager.getRuntimeEngine(bd.getDeployIdentifier()).getKieSession();
    }
}
