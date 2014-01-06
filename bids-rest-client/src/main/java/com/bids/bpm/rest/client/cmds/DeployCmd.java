/**
 * ===========================================================================
 *    Copyright 2012 BIDS Holdings L.P. All rights reserved.
 * ---------------------------------------------------------------------------
 * Created on 1/1/14
 * By bidsjagu
 *
 */

package com.bids.bpm.rest.client.cmds;

import com.bids.bpm.jee.model.BidsDeployment;
import com.bids.bpm.jee.rest.dto.DeployRequest;
import com.bids.bpm.rest.client.BSCommand;
import com.bids.bpm.rest.client.JAXBHelper;
import static javax.ws.rs.core.MediaType.APPLICATION_XML;

public class DeployCmd
        extends BSCommand<BidsDeployment>
{

    public static final String NAME = "deploy";

    public DeployCmd(String uriTemplate)
    {
        super(NAME, uriTemplate.concat("/mgmt/" + NAME), CommandType.post);
    }

    @Override
    public String getResultAsString()
            throws Exception
    {
        return JAXBHelper.marshallIntoXML(BidsDeployment.class, getResult());
    }

    @Override
    public BidsDeployment getResult()
    {
        return response.getEntity(BidsDeployment.class);
    }

    protected void prepareRequest(String[] args)
            throws Exception
    {

        if (args.length != 3)
            throw new RuntimeException("expected 3 args: artifactId version bidsDate");

        DeployRequest dr = new DeployRequest();
        dr.setArtifactId(args[0]);
        dr.setVersion(args[1]);
        dr.setBidsDate(args[2]);
        request.body(APPLICATION_XML, JAXBHelper.marshallIntoXML(DeployRequest.class, dr));
    }

}

