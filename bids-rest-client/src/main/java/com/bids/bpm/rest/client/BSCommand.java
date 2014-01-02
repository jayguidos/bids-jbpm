/**
 * ===========================================================================
 *    Copyright 2012 BIDS Holdings L.P. All rights reserved.
 * ---------------------------------------------------------------------------
 * Created on 1/1/14
 * By bidsjagu
 *
 */

package com.bids.bpm.rest.client;

import java.io.StringWriter;

import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBContext;


import com.bids.bpm.jee.rest.dto.ErrorResponse;
import com.bids.bpm.rest.client.cmds.DeployCmd;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;

public abstract class BSCommand
{
    private final ClientRequest request;
    private final CommandType cmdType;
    private final String[] args;

    public enum CommandType
    {
        get, put, post, delete
    }

    public BSCommand(String uriTemplate, CommandType cmdType, String[] args)
    {
        this.args = args;
        this.request = new ClientRequest(uriTemplate);
        this.cmdType = cmdType;
    }

    public <T> T runCmd(Class<T> returnType)
            throws Exception
    {
        prepareRequest(request, args);
        switch (cmdType)
        {
            case get:
                return processServerResponse(returnType, request.get());
            case put:
                return processServerResponse(returnType, request.put());
            case post:
                return processServerResponse(returnType, request.post());
            case delete:
                return processServerResponse(returnType, request.delete());
        }
        return null;
    }

    protected abstract void prepareRequest(ClientRequest request, String[] args)
            throws Exception;

    protected <V> String marshallIntoXML(Class<V> jaxbClass, V instance)
            throws Exception
    {
        StringWriter sw = new StringWriter();
        JAXBContext jbc = JAXBContext.newInstance(jaxbClass);
        jbc.createMarshaller().marshal(instance, sw);
        return sw.toString();
    }

    protected <T> T processServerResponse(Class<T> expectedResponseEntityType, ClientResponse<?> response)
            throws Exception
    {
        if (response.getResponseStatus().getFamily() == Response.Status.Family.SUCCESSFUL)
            return response.getEntity(expectedResponseEntityType);
        else if (response.getResponseStatus().getFamily() == Response.Status.Family.SERVER_ERROR)
            throw new ErrorResponseException(response.getEntity(ErrorResponse.class));
        else
            throw new RuntimeException("Unknown error : " + response.getResponseStatus().getStatusCode());
    }
}
