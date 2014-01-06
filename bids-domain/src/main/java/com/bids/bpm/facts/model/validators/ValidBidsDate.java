package com.bids.bpm.facts.model.validators;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;


import static com.bids.bpm.shared.BidsBPMConstants.TRADING_DATE_FORMAT;

/**
 * ===========================================================================
 * Copyright 2012 BIDS Holdings L.P. All rights reserved.
 * ---------------------------------------------------------------------------
 * Created on 12/31/13
 * By bidsjagu
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Constraint(validatedBy = BidsDateValidator.class)
public @interface ValidBidsDate
{
    String message() default "BidsDay dates must be from 2013 on";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
