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
import java.util.List;

import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;


import com.bids.bpm.jee.rest.dto.ErrorResponse;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;

public abstract class BSCommand<T>
{
    public enum CommandType
    {
        get, put, post, delete
    }

    protected final ClientRequest request;
    private final String name;
    private final CommandType cmdType;
    protected ClientResponse<?> response;

    public BSCommand(String name, String uriTemplate, CommandType cmdType)
    {
        this.name = name;
        this.cmdType = cmdType;
        this.request = new ClientRequest(uriTemplate);
    }

    public T runCmd()
            throws Exception
    {
        sendRequest();
        return getResult();
    }

    @Override
    public String toString()
    {
        return "BSCommand{" +
                "name='" + name + '\'' +
                '}';
    }

    abstract public String getResultAsString()
            throws Exception;

    abstract public T getResult();

    public ClientRequest getRequest()
    {
        return request;
    }

    public String getName()
    {
        return name;
    }

    public CommandType getCmdType()
    {
        return cmdType;
    }

    private T sendRequest()
            throws Exception
    {
        switch (cmdType)
        {
            case get:
                return processServerResponse(request.get());
            case put:
                return processServerResponse(request.put());
            case post:
                return processServerResponse(request.post());
            case delete:
                return processServerResponse(request.delete());
        }
        return null;
    }

    protected abstract void prepareRequest(String[] args)
            throws Exception;

    protected T processServerResponse(ClientResponse<?> response)
            throws Exception
    {
        this.response = response;
        if (response.getResponseStatus().getFamily() == Response.Status.Family.SUCCESSFUL)
            return getResult();
        else if (response.getResponseStatus().getFamily() == Response.Status.Family.SERVER_ERROR)
            throw new ErrorResponseException(response.getEntity(ErrorResponse.class));
        else
            throw new RuntimeException("Unknown error : " + response.getResponseStatus().getStatusCode());
    }

}
