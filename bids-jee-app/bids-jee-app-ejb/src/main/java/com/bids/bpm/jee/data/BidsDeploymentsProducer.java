/**
 * ===========================================================================
 *    Copyright 2012 BIDS Holdings L.P. All rights reserved.
 * ---------------------------------------------------------------------------
 * Created on 12/31/13
 * By bidsjagu
 *
 */

package com.bids.bpm.jee.data;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.event.Reception;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;


import com.bids.bpm.jee.model.BidsDeployment;
import com.bids.bpm.jee.util.BidsEjbEM;

@RequestScoped
public class BidsDeploymentsProducer
{
    @Inject
    @BidsEjbEM
    private EntityManager em;

    private List<BidsDeployment> deployments;

    @Produces
    @Named
    public List<BidsDeployment> getDeployments()
    {
        return deployments;
    }

    public void onBidsDeploymentListChanged(@Observes(notifyObserver = Reception.IF_EXISTS) final BidsDeployment deployment)
    {
        retrieveAllDeploymentsOrderedByName();
    }

    @PostConstruct
    public void retrieveAllDeploymentsOrderedByName()
    {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<BidsDeployment> criteria = cb.createQuery(BidsDeployment.class);
        Root<BidsDeployment> member = criteria.from(BidsDeployment.class);
        // Swap criteria statements if you would like to try out type-safe criteria queries, a new
        // feature in JPA 2.0
//        criteria.select(member).orderBy(cb.asc(member.get(BidsDeployment.identifier)));
        criteria.select(member).orderBy(cb.asc(member.get("deployIdentifier")));
        deployments = em.createQuery(criteria).getResultList();
    }

}
