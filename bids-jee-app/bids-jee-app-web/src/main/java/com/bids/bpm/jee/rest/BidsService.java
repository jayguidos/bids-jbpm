/**
 * ===========================================================================
 *    Copyright 2012 BIDS Holdings L.P. All rights reserved.
 * ---------------------------------------------------------------------------
 * Created on 12/28/13
 * By bidsjagu
 *
 */

package com.bids.bpm.jee.rest;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;


import com.bids.bpm.jee.controller.BidsProcessController;

@Path("/bids")
@RequestScoped
public class BidsService
{

    @Inject
    private BidsProcessController bpc;

    @GET
    @Path("/deploy")
    @Produces("text/xml")
    public String deploy(@MatrixParam("artifactId") String artifactId, @MatrixParam("version") String version)
    {
        return bpc.deployModule(artifactId, version);
    }
}
