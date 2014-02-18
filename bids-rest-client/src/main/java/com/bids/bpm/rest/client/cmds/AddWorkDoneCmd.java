/**
 * ===========================================================================
 *    Copyright 2012 BIDS Holdings L.P. All rights reserved.
 * ---------------------------------------------------------------------------
 * Created on 1/1/14
 * By bidsjagu
 *
 */

package com.bids.bpm.rest.client.cmds;

import com.bids.bpm.facts.model.WorkDone;
import com.bids.bpm.rest.client.BSCommand;
import static com.bids.bpm.rest.client.BSCommand.CommandType.put;
import com.bids.bpm.rest.client.RestEasyClientFactory;

public class AddWorkDoneCmd
        extends BSCommand<WorkDone>
{

    public static final String NAME = "addWorkDone";
    public static final String DEPLOYMENT_ID = "bdId";
    public static final String WORK_DONE_NAME = "workDoneName";

    public AddWorkDoneCmd(RestEasyClientFactory clientFactory, String uriTemplate)
    {
        super(NAME, clientFactory.makeRequest(assembleUri(uriTemplate, NAME, new String[]{DEPLOYMENT_ID, WORK_DONE_NAME})), put, WorkDone.class);
    }

    @Override
    public String getResultAsString()
            throws Exception
    {
        return getResultAsXML();
    }

    @Override
    public WorkDone getResult()
    {
        return response.getEntity(WorkDone.class);
    }

    protected void prepareRequest(String[] args)
            throws Exception
    {
        if (args.length != 2)
            throw new RuntimeException("expected 2 args: deploymentId workDoneName");
        request.pathParameter(DEPLOYMENT_ID, args[0]);
        request.pathParameter(WORK_DONE_NAME, args[1]);
    }

}

