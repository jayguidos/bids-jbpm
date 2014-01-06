/**
 * ===========================================================================
 *    Copyright 2012 BIDS Holdings L.P. All rights reserved.
 * ---------------------------------------------------------------------------
 * Created on 12/31/13
 * By bidsjagu
 *
 */

package com.bids.bpm.jee.data;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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


import com.bids.bpm.facts.model.BidsDay;
import com.bids.bpm.jee.model.BidsDeployment;
import com.bids.bpm.jee.util.BidsEjbEM;

@RequestScoped
public class BidsDayProducer
{
    @Inject
    @BidsEjbEM
    private EntityManager em;

    private Map<Date,BidsDay> bidsDays = new HashMap<Date, BidsDay>();

    @Produces
    @Named
    public Map<Date,BidsDay> getBidsDays()
    {
        return Collections.unmodifiableMap(bidsDays);
    }

    public void onBidsDayListChanged(@Observes(notifyObserver = Reception.IF_EXISTS) final BidsDay bidsDay)
    {
        retrieveAllBidsDays();
    }

    @PostConstruct
    public void retrieveAllBidsDays()
    {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<BidsDay> criteria = cb.createQuery(BidsDay.class);
        criteria.select(criteria.from(BidsDay.class));
        List<BidsDay> days = em.createQuery(criteria).getResultList();
        bidsDays.clear();
        for (BidsDay day : days)
            bidsDays.put(day.getDate(),day);
    }

}
