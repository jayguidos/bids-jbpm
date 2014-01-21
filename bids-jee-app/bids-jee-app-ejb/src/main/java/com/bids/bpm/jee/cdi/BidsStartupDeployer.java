/**
 * ===========================================================================
 *    Copyright 2012 BIDS Holdings L.P. All rights reserved.
 * ---------------------------------------------------------------------------
 * Created on 1/21/14
 * By bidsjagu
 *
 */

package com.bids.bpm.jee.cdi;

import java.util.List;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;


import com.bids.bpm.jee.controller.BidsProcessController;
import com.bids.bpm.jee.model.BidsDeployment;
import com.bids.bpm.jee.util.BidsEjbEM;


@Singleton
@Startup
public class BidsStartupDeployer
{
    @Inject
    BidsProcessController bpc;
    @Inject
    @BidsEjbEM
    private EntityManager em;
    @Inject
    private Logger log;

    @PostConstruct
    public void redeployExistingBidsDaySessions()
    {
        List<BidsDeployment> deployments = retrieveAllDeployments();
        if (deployments.size() > 0)
        {

            log.info("Detected " + deployments.size() + " redeployments.");
            for (BidsDeployment bd : deployments)
                bpc.redeployOnRestart(bd);
            log.info("Redeployment phase complete");
        }
    }

    private List<BidsDeployment> retrieveAllDeployments()
    {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<BidsDeployment> criteria = cb.createQuery(BidsDeployment.class);
        Root<BidsDeployment> deploymentRoot = criteria.from(BidsDeployment.class);
        criteria.select(deploymentRoot).orderBy(cb.asc(deploymentRoot.get("deployIdentifier")));
        return em.createQuery(criteria).getResultList();
    }

}

