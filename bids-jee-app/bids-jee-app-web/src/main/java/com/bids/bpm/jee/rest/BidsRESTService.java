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

import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;


import com.bids.bpm.facts.model.BidsDay;
import com.bids.bpm.jee.controller.BidsProcessController;
import com.bids.bpm.jee.model.BidsDeployment;
import com.bids.bpm.jee.model.BidsProcessInvocation;
import com.bids.bpm.jee.rest.dto.BidsFactsResponse;
import com.bids.bpm.jee.rest.dto.DeployRequest;
import com.bids.bpm.jee.rest.dto.StartProcessRequest;
import static javax.ws.rs.core.MediaType.APPLICATION_XML;
import static javax.ws.rs.core.MediaType.TEXT_PLAIN;
import org.hibernate.validator.constraints.NotEmpty;
import org.jboss.resteasy.annotations.providers.jaxb.Wrapped;
import org.jboss.resteasy.spi.validation.ValidateRequest;

@Path("/mgmt")
@RequestScoped
@ValidateRequest
public class BidsRESTService
{

    @Inject
    private BidsProcessController bpc;

    @GET
    @Path("/deployments")
    @Produces(APPLICATION_XML)
    @Wrapped(element = "list", prefix = "deployments")
    public List<BidsDeployment> deployments()
    {
        return bpc.getDeployments();
    }

    @GET
    @Path("/dumpFacts/{bdId}")
    @Produces(APPLICATION_XML)
    public BidsFactsResponse dumpFacts(@NotNull @NotEmpty @DecimalMin("1") @PathParam("bdId") String bdIdString)
    {
        BidsDeployment bidsDeployment = findBidsDeployment(bdIdString);
        BidsFactsResponse response = new BidsFactsResponse();
        response.setBidsDeploymentId(bidsDeployment.getId());
        response.setFacts(bpc.dumpAllFacts(bidsDeployment.getId()));
        return response;
    }

    @DELETE
    @Path("/killProcess")
    @Produces(APPLICATION_XML)
    @Consumes(TEXT_PLAIN)
    public BidsProcessInvocation killProcess(@NotNull @DecimalMin("1") Long bidsProcessId)
    {
        return bpc.killProcess(bidsProcessId);
    }

    @POST
    @Path("/startDay")
    @Produces(APPLICATION_XML)
    @Consumes(APPLICATION_XML)
    public BidsDeployment startDay(@Valid DeployRequest deploy)
    {
        return bpc.deployModule(new BidsDay(deploy.getBidsDate()), deploy.getArtifactId(), deploy.getVersion());
    }

    @POST
    @Path("/startProcess")
    @Produces(APPLICATION_XML)
    @Consumes(APPLICATION_XML)
    public BidsProcessInvocation startProcess(@Valid StartProcessRequest sp)
    {
        return bpc.startProcess(findBidsDeployment(sp.getDeploymentId()).getId(), sp.getKieProcessId());
    }

    @DELETE
    @Path("/stopDay")
    @Produces(TEXT_PLAIN)
    @Consumes(TEXT_PLAIN)
    public boolean stopDay(@NotNull @NotEmpty @DecimalMin("1") String bdIdString)
    {
        return bpc.undeployModule(findBidsDeployment(bdIdString).getId());
    }

    private BidsDeployment findBidsDeployment(String bdIdStr)
    {
        Long bdId = Long.parseLong(bdIdStr);
        BidsDeployment bd = bpc.findDeployment(bdId);
        if (bd == null)
            throw new RuntimeException("BidsDeployment not found with Id: " + bdId);
        else
            return bd;
    }

}
