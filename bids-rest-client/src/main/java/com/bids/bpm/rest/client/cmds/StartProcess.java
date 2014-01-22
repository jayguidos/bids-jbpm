/**
 * ===========================================================================
 *    Copyright 2012 BIDS Holdings L.P. All rights reserved.
 * ---------------------------------------------------------------------------
 * Created on 1/9/14
 * By bidsjagu
 *
 */

package com.bids.bpm.rest.client.cmds;

import com.bids.bpm.jee.model.BidsProcessInvocation;
import com.bids.bpm.jee.rest.dto.StartProcessRequest;
import com.bids.bpm.rest.client.BSCommand;
import static com.bids.bpm.rest.client.BSCommand.CommandType.post;
import com.bids.bpm.rest.client.JAXBHelper;
import static javax.ws.rs.core.MediaType.APPLICATION_XML;

public class StartProcess
        extends BSCommand<BidsProcessInvocation>
{
    public static final String NAME = "startProcess";
    private final JAXBHelper jaxbHelper = new JAXBHelper(StartProcessRequest.class);

    public StartProcess(String uriTemplate)
    {
        super(NAME, assembleUri(uriTemplate, NAME), post, BidsProcessInvocation.class);
    }

    @Override
    public String getResultAsString()
            throws Exception
    {
        return getResultAsXML();
    }

    @Override
    public BidsProcessInvocation getResult()
    {
        return response.getEntity(BidsProcessInvocation.class);
    }

    protected void prepareRequest(String[] args)
            throws Exception
    {

        if (args.length != 2)
            throw new RuntimeException("expected 2 args: processId deploymentId");

        StartProcessRequest dr = new StartProcessRequest();
        dr.setProcessId(args[0]);
        dr.setDeploymentId(args[1]);
        request.body(APPLICATION_XML, jaxbHelper.marshallIntoXML(dr));
    }

}
