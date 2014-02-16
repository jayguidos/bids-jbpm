/**
 * ===========================================================================
 *    Copyright 2012 BIDS Holdings L.P. All rights reserved.
 * ---------------------------------------------------------------------------
 * Created on 1/1/14
 * By bidsjagu
 *
 */

package com.bids.bpm.rest.client.cmds;

import com.bids.bpm.jee.model.DeployedBidsDayDesc;
import com.bids.bpm.rest.client.BSCommand;
import static com.bids.bpm.rest.client.BSCommand.CommandType.get;
import com.bids.bpm.rest.client.RestEasyClientFactory;

public class ReportProcessStatusCmd
        extends BSCommand<DeployedBidsDayDesc>
{

    public static final String NAME = "reportProcessStatus";
    public static final String DEPLOYMENT_ID = "bdId";
    public static final String PROCESS_ID = "processId";
    public static final String USE_HISTORY = "history";

    public ReportProcessStatusCmd(RestEasyClientFactory clientFactory, String uriTemplate)
    {
        super(
                NAME,
                clientFactory.makeRequest(assembleUri(uriTemplate, NAME, new String[]{DEPLOYMENT_ID, PROCESS_ID, USE_HISTORY})),
                get,
                DeployedBidsDayDesc.class
        );
    }

    @Override
    public String getResultAsString()
            throws Exception
    {
        return getResultAsXML();
    }

    @Override
    public DeployedBidsDayDesc getResult()
    {
        return response.getEntity(DeployedBidsDayDesc.class);
    }

    protected void prepareRequest(String[] args)
            throws Exception
    {
        if (args.length == 3)
        {
            request.pathParameter(DEPLOYMENT_ID, args[0]);
            request.pathParameter(PROCESS_ID, args[1]);
            request.pathParameter(USE_HISTORY, args[2]);
        }
        else
            throw new RuntimeException("expected 3 args: deploymentId processId [true|false] where you use true to get full process execution history");
    }

}

