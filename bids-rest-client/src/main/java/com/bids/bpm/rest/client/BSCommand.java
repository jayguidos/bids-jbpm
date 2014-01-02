/**
 * ===========================================================================
 *    Copyright 2012 BIDS Holdings L.P. All rights reserved.
 * ---------------------------------------------------------------------------
 * Created on 1/1/14
 * By bidsjagu
 *
 */

package com.bids.bpm.rest.client;

import org.jboss.resteasy.client.ClientRequest;

public abstract class BSCommand
{
    protected ClientRequest request;

    public BSCommand(String uriTemplate)
    {
        this.request = new ClientRequest(uriTemplate);
    }

    public abstract void execute() throws Exception;
}
