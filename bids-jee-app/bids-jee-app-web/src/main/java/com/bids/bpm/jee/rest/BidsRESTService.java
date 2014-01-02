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
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;


import com.bids.bpm.facts.model.BidsDay;
import com.bids.bpm.jee.controller.BidsProcessController;
import com.bids.bpm.jee.model.BidsDeployment;
import com.bids.bpm.jee.rest.dto.DeployRequest;
import static javax.ws.rs.core.MediaType.APPLICATION_XML;
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

    @POST
    @Path("/deploy")
    @Produces(APPLICATION_XML)
    @Consumes(APPLICATION_XML)
    public BidsDeployment deploy(@Valid DeployRequest deploy)
    {
        return bpc.deployModule(new BidsDay(deploy.getBidsDate()), deploy.getArtifactId(), deploy.getVersion());
    }

    @GET
    @Path("/deployments")
    @Produces(APPLICATION_XML)
    public BidsDeployment[] deployments()
    {
        List<BidsDeployment> deployments = bpc.getDeployments();
        return deployments.toArray(new BidsDeployment[deployments.size()]);
    }

}
