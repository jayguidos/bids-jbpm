/**
 * ===========================================================================
 *    Copyright 2012 BIDS Holdings L.P. All rights reserved.
 * ---------------------------------------------------------------------------
 * Created on 12/31/13
 * By bidsjagu
 *
 */

package com.bids.bpm.jee.rest.validators;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;


import com.bids.bpm.shared.BidsBPMConstants;
import static com.bids.bpm.shared.BidsBPMConstants.TRADING_DATE_FORMAT;

public class BidsDayValidator
        implements ConstraintValidator<ValidBidsDay, String>
{
    @Override
    public void initialize(ValidBidsDay constraintAnnotation)
    {

    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context)
    {
        if ( value == null || value.length() == 0 )
            return false;

        try
        {
            new SimpleDateFormat(TRADING_DATE_FORMAT).parse(value);
            return true;
        } catch (ParseException ignored)
        {
            return false;
        }
    }
}
