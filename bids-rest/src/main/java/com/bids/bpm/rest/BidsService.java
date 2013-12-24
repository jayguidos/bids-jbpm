/**
 * ===========================================================================
 *    Copyright 2012 BIDS Holdings L.P. All rights reserved.
 * ---------------------------------------------------------------------------
 * Created on 12/19/13
 * By bidsjagu
 *
 */

package com.bids.bpm.rest;


import com.bids.bpm.controller.BidsProcessManager;


import javax.inject.Inject;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

@Path("/bids")
public class BidsService
{
    @Inject
    private BidsProcessManager bpm;

    public void startProcess(@PathParam("processId") String pid)
    {
        bpm.deployUnit("bids-jbmp","EndOfDay","1.0.0-SNAPSHOT");
        bpm.startProcess("bids-jbmp","EndOfDay","1.0.0-SNAPSHOT",pid);
    }
}
