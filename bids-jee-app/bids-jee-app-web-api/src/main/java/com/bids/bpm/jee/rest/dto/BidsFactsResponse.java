/**
 * ===========================================================================
 *    Copyright 2012 BIDS Holdings L.P. All rights reserved.
 * ---------------------------------------------------------------------------
 * Created on 1/21/14
 * By bidsjagu
 *
 */

package com.bids.bpm.jee.rest.dto;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;


import static javax.xml.bind.annotation.XmlAccessType.NONE;

@XmlAccessorType(NONE)
@XmlRootElement(name = "deploymentFacts")
public class BidsFactsResponse
    implements Serializable
{

    @XmlElement
    private Long bidsDeploymentId;

    @XmlElement( name="fact")
    @XmlElementWrapper( name="factList")
    private List<String> facts;

    public Long getBidsDeploymentId()
    {
        return bidsDeploymentId;
    }

    public void setBidsDeploymentId(Long bidsDeploymentId)
    {
        this.bidsDeploymentId = bidsDeploymentId;
    }

    public List<String> getFacts()
    {
        return facts;
    }

    public void setFacts(List<String> facts)
    {
        this.facts = facts;
    }
}
