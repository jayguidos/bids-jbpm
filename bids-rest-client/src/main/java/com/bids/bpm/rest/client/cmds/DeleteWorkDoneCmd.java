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
import static com.bids.bpm.rest.client.BSCommand.CommandType.delete;
import com.bids.bpm.rest.client.RestEasyClientFactory;

public class DeleteWorkDoneCmd
        extends BSCommand<String>
{

    public static final String NAME = "deleteWorkDone";
    public static final String DEPLOYMENT_ID = "bdId";
    public static final String WORK_ID = "workId";

    public DeleteWorkDoneCmd(RestEasyClientFactory clientFactory, String uriTemplate)
    {
        super(NAME, clientFactory.makeRequest(assembleUri(uriTemplate, NAME, new String[]{DEPLOYMENT_ID, WORK_ID})), delete, String.class);
    }

    @Override
    public String getResultAsString()
            throws Exception
    {
        return getResult();
    }

    @Override
    public String getResult()
    {
        return response.getEntity(String.class);
    }

    protected void prepareRequest(String[] args)
            throws Exception
    {
        if (args.length != 2)
            throw new RuntimeException("expected 2 args: deploymentId workDoneId");
        request.pathParameter(DEPLOYMENT_ID, args[0]);
        request.pathParameter(WORK_ID, args[1]);
    }

}

