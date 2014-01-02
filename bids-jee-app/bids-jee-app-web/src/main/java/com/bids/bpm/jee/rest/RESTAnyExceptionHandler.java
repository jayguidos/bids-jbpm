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


import com.bids.bpm.jee.rest.dto.ErrorResponse;
import static javax.ws.rs.core.MediaType.APPLICATION_XML;

@Provider
public class RESTAnyExceptionHandler
        implements ExceptionMapper<Exception>
{

    @Override
    public Response toResponse(Exception exception)
    {
        // Generic exception catcher
        ErrorResponse r = new ErrorResponse();
        r.setCode(100);
        r.setMessage("Unknown error : " + exception.getMessage());
        return Response.serverError().entity(r).type(APPLICATION_XML).build();
    }
}
