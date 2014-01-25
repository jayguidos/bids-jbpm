/**
 * ===========================================================================
 *    Copyright 2012 BIDS Holdings L.P. All rights reserved.
 * ---------------------------------------------------------------------------
 * Created on 1/1/14
 * By bidsjagu
 *
 */

package com.bids.bpm.rest.client.cmds;

import com.bids.bpm.jee.model.BidsProcessInvocation;
import com.bids.bpm.rest.client.BSCommand;
import static com.bids.bpm.rest.client.BSCommand.CommandType.delete;
import com.bids.bpm.rest.client.RestEasyClientFactory;
import static javax.ws.rs.core.MediaType.TEXT_PLAIN;

public class KillProcessCmd
        extends BSCommand<BidsProcessInvocation>
{

    public static final String NAME = "killProcess";

    public KillProcessCmd(RestEasyClientFactory clientFactory,String uriTemplate)
    {
        super(NAME, clientFactory.makeRequest(assembleUri(uriTemplate, NAME)), delete, BidsProcessInvocation.class);
    }

    @Override
    public String getResultAsString()
            throws Exception
    {
        return getResult().toString();
    }

    @Override
    public BidsProcessInvocation getResult()
    {
        return response.getEntity(BidsProcessInvocation.class);
    }

    protected void prepareRequest(String[] args)
            throws Exception
    {
        if (args.length != 1)
            throw new RuntimeException("expected 1 args: bidsProcessId");
        request.body(TEXT_PLAIN, args[0].getBytes());
    }

}

