/**
 * ===========================================================================
 *    Copyright 2012 BIDS Holdings L.P. All rights reserved.
 * ---------------------------------------------------------------------------
 * Created on 1/1/14
 * By bidsjagu
 *
 */

package com.bids.bpm.jee.rest.dto;


import java.io.Serializable;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


import com.bids.bpm.jee.rest.validators.ValidBidsDay;
import static com.bids.bpm.shared.BidsBPMConstants.TRADING_DATE_FORMAT;
import static javax.xml.bind.annotation.XmlAccessType.NONE;

@XmlAccessorType(NONE)
@XmlRootElement(name = "deploy")
public class DeployRequest
        implements Serializable
{
    @XmlElement
    @NotNull(message = "artifactId is required")
    private String artifactId;

    @XmlElement
    @NotNull(message = "version (i.e. artifact version) is required")
    private String version;

    @XmlElement
    @NotNull(message = "bidsDay is required in format " + TRADING_DATE_FORMAT)
    @ValidBidsDay
    private String bidsDate;

    public String getArtifactId()
    {
        return artifactId;
    }

    public void setArtifactId(String artifactId)
    {
        this.artifactId = artifactId;
    }

    public String getVersion()
    {
        return version;
    }

    public void setVersion(String version)
    {
        this.version = version;
    }

    public String getBidsDate()
    {
        return bidsDate;
    }

    public void setBidsDate(String bidsDate)
    {
        this.bidsDate = bidsDate;
    }
}
