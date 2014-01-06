/**
 * ===========================================================================
 *    Copyright 2012 BIDS Holdings L.P. All rights reserved.
 * ---------------------------------------------------------------------------
 * Created on 12/31/13
 * By bidsjagu
 *
 */

package com.bids.bpm.facts.model.validators;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;


import static com.bids.bpm.shared.BidsBPMConstants.TRADING_DATE_FORMAT;

public class BidsDateStringValidator
        implements ConstraintValidator<ValidBidsDateString, String>
{

    public void initialize(ValidBidsDateString constraintAnnotation)
    {

    }

    public boolean isValid(String candidateBidsDateAsString, ConstraintValidatorContext context)
    {
        if (candidateBidsDateAsString == null || candidateBidsDateAsString.length() == 0)
            return false;

        try
        {
            new SimpleDateFormat(TRADING_DATE_FORMAT).parse(candidateBidsDateAsString);
            return true;
        } catch (ParseException ignored)
        {
            return false;
        }
    }
}
