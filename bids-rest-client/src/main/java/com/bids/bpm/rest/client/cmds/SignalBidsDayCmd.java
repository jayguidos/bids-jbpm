/**
 * ===========================================================================
 *    Copyright 2012 BIDS Holdings L.P. All rights reserved.
 * ---------------------------------------------------------------------------
 * Created on 1/1/14
 * By bidsjagu
 *
 */

package com.bids.bpm.rest.client.cmds;

import com.bids.bpm.rest.client.BSCommand;
import static com.bids.bpm.rest.client.BSCommand.CommandType.put;
import com.bids.bpm.rest.client.RestEasyClientFactory;

public class SignalBidsDayCmd
        extends BSCommand<Boolean>
{

    public static final String NAME = "signalBidsdDay";
    public static final String DEPLOYMENT_ID = "bdId";
    public static final String SIGNAL_NAME = "signalName";

    public SignalBidsDayCmd(RestEasyClientFactory clientFactory, String uriTemplate)
    {
        super(NAME, clientFactory.makeRequest(assembleUri(uriTemplate, NAME, new String[]{DEPLOYMENT_ID, SIGNAL_NAME})), put, Boolean.class);
    }

    @Override
    public String getResultAsString()
            throws Exception
    {
        return getResult().toString();
    }

    @Override
    public Boolean getResult()
    {
        return response.getEntity(Boolean.class);
    }

    protected void prepareRequest(String[] args)
            throws Exception
    {
        if (args.length != 2)
            throw new RuntimeException("expected 2 args: deploymentId signalName");
        request.pathParameter(DEPLOYMENT_ID, args[0]);
        request.pathParameter(SIGNAL_NAME, args[1]);
    }

}

