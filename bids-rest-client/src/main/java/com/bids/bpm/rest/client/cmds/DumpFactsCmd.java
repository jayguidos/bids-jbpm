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
import com.bids.bpm.rest.client.BSCommand;
import static com.bids.bpm.rest.client.BSCommand.CommandType.get;

public class DumpFactsCmd
        extends BSCommand<BidsDeployment>
{

    public static final String NAME = "dumpFacts";
    public static final String DEPLOYMENT_ID = "bdId";

    public DumpFactsCmd(String uriTemplate)
    {
        super(NAME, assembleUri(uriTemplate, NAME, new String[] {DEPLOYMENT_ID} ), get, BidsDeployment.class);
    }

    @Override
    public String getResultAsString()
            throws Exception
    {
        return getResultAsXML();
    }

    @Override
    public BidsDeployment getResult()
    {
        return response.getEntity(BidsDeployment.class);
    }

    protected void prepareRequest(String[] args)
            throws Exception
    {
        if (args.length != 1)
            throw new RuntimeException("expected 1 args: deploymentId");
        request.pathParameter(DEPLOYMENT_ID, args[0]);
    }

}

