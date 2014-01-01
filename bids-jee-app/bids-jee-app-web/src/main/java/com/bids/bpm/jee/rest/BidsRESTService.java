/**
 * ===========================================================================
 *    Copyright 2012 BIDS Holdings L.P. All rights reserved.
 * ---------------------------------------------------------------------------
 * Created on 12/28/13
 * By bidsjagu
 *
 */

package com.bids.bpm.jee.rest;

import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.validation.constraints.NotNull;
import javax.ws.rs.GET;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;


import com.bids.bpm.facts.model.BidsDay;
import com.bids.bpm.jee.controller.BidsProcessController;
import com.bids.bpm.jee.data.BidsDeploymentsProducer;
import com.bids.bpm.jee.model.BidsDeployment;
import com.bids.bpm.jee.rest.validators.ValidBidsDay;
import static com.bids.bpm.shared.BidsBPMConstants.TRADING_DATE_FORMAT;
import org.jboss.resteasy.spi.validation.ValidateRequest;

@Path("/mgmt")
@RequestScoped
@ValidateRequest
public class BidsRESTService
{

    @Inject
    private BidsProcessController bpc;

    @Inject
    private EntityManager em;

    @GET
    @Path("/deploy")
    @Produces("text/plain")
    public String deploy(

            @MatrixParam("artifactId")
            @NotNull(message = "artifactId is required")
            String artifactId,

            @MatrixParam("version")
            @NotNull(message = "version (i.e. artifact version) is required")
            String version,

            @MatrixParam("bidsDay")
            @NotNull(message = "bidsDay is required in format " + TRADING_DATE_FORMAT)
            @ValidBidsDay
            String dateStr
    )
    {
        return bpc.deployModule(new BidsDay(dateStr), artifactId, version);
    }

    @GET
    @Path("/deployments")
    @Produces("application/xml")
    public BidsDeployment[] deployments()
    {
        List<BidsDeployment> deployments = bpc.getDeployments();
        return deployments.toArray(new BidsDeployment[deployments.size()]);
    }

}
