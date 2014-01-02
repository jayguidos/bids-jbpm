/**
 * ===========================================================================
 *    Copyright 2012 BIDS Holdings L.P. All rights reserved.
 * ---------------------------------------------------------------------------
 * Created on 1/1/14
 * By bidsjagu
 *
 */

package com.bids.bpm.rest.client.cmds;

import com.bids.bpm.jee.rest.dto.DeployRequest;
import com.bids.bpm.rest.client.BSCommand;
import static javax.ws.rs.core.MediaType.APPLICATION_XML;
import org.jboss.resteasy.client.ClientRequest;

public class DeployCmd
        extends BSCommand
{

    private DeployRequest dr;

    public DeployCmd(String uriTemplate, String[] args)
    {
        super(uriTemplate, CommandType.post, args);
    }

    protected void prepareRequest(ClientRequest request, String[] args)
            throws Exception
    {
        dr = new DeployRequest();
        dr.setArtifactId("EndOfDay");
        dr.setVersion("1.0.0-SNAPSHOT");
        dr.setBidsDate("XX-12-22");
        request.body(APPLICATION_XML, marshallIntoXML(DeployRequest.class, dr));
    }

}
