/**
 * ===========================================================================
 *    Copyright 2012 BIDS Holdings L.P. All rights reserved.
 * ---------------------------------------------------------------------------
 * Created on 1/1/14
 * By bidsjagu
 *
 */

package com.bids.bpm.rest.client.cmds;

import java.util.List;


import com.bids.bpm.jee.model.BidsDeployment;
import com.bids.bpm.rest.client.BSCommand;
import com.bids.bpm.rest.client.JAXBHelper;
import org.jboss.resteasy.util.GenericType;

public class GetDeploymentsCmd
        extends BSCommand<List<BidsDeployment>>
{

    public static final String NAME = "deployments";

    public GetDeploymentsCmd(String uriTemplate)
    {
        super(NAME, uriTemplate.concat("/mgmt/deployments"), CommandType.get);
    }

    @Override
    public String getResultAsXML()
            throws Exception
    {
        return JAXBHelper.marshallIntoXML("deployments", BidsDeployment.class, getResult());
    }

    @Override
    public List<BidsDeployment> getResult()
    {
        return response.getEntity(new GenericType<List<BidsDeployment>>()
        {
        });
    }

    protected void prepareRequest(String[] args)
            throws Exception
    {
    }

}

