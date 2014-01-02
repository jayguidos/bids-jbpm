/**
 * ===========================================================================
 *    Copyright 2012 BIDS Holdings L.P. All rights reserved.
 * ---------------------------------------------------------------------------
 * Created on 1/2/14
 * By bidsjagu
 *
 */

package com.bids.bpm.rest.client;

import com.bids.bpm.jee.rest.dto.ErrorResponse;

public class ErrorResponseException
        extends Exception
{
    private final ErrorResponse errorInfo;

    public ErrorResponseException(ErrorResponse errorInfo)
    {
        super(errorInfo.getMessage());
        this.errorInfo = errorInfo;
    }

    public ErrorResponse getErrorInfo()
    {
        return errorInfo;
    }
}
