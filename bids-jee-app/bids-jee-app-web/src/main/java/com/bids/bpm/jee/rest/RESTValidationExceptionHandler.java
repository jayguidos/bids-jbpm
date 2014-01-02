/**
 * ===========================================================================
 *    Copyright 2012 BIDS Holdings L.P. All rights reserved.
 * ---------------------------------------------------------------------------
 * Created on 1/1/14
 * By bidsjagu
 *
 */

package com.bids.bpm.jee.rest;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;


import static javax.ws.rs.core.MediaType.APPLICATION_XML;

@Provider
public class RESTValidationExceptionHandler
        implements ExceptionMapper<Exception>
{

    @Override
    public Response toResponse(Exception exception)
    {
        // For simplicity I am preparing error xml by hand.
        // Ideally we should create an ErrorResponse class to hold the error info.
        StringBuilder response = new StringBuilder("<response>");
        response.append("<status>ERROR</status>");
        response.append("<message>" + exception.getMessage() + "</message>");
        response.append("</response>");
        return Response.serverError().entity(response.toString()).type(APPLICATION_XML).build();
    }
}
