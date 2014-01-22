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
import static com.bids.bpm.rest.client.BSCommand.CommandType.get;
import org.jboss.resteasy.util.GenericType;

public class GetDeploymentsCmd
        extends BSCommand<List<BidsDeployment>>
{

    public static final String NAME = "deployments";
    public static final GenericType<List<BidsDeployment>> BD_LIST_TYPE = new GenericType<List<BidsDeployment>>()
    {
    };

    public GetDeploymentsCmd(String uriTemplate)
    {
        super(NAME, assembleUri(uriTemplate, NAME), get, BidsDeployment.class);
    }

    @Override
    public String getResultAsString()
            throws Exception
    {
        return jaxbHelper.marshallIntoXML(NAME, getResult());
    }

    @Override
    public List<BidsDeployment> getResult()
    {
        return response.getEntity(BD_LIST_TYPE);
    }

    protected void prepareRequest(String[] args)
            throws Exception
    {
    }

}

