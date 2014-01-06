/**
 * ===========================================================================
 *    Copyright 2012 BIDS Holdings L.P. All rights reserved.
 * ---------------------------------------------------------------------------
 * Created on 12/31/13
 * By bidsjagu
 *
 */

package com.bids.bpm.jee.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;


import com.bids.bpm.facts.model.BidsDay;
import com.bids.bpm.facts.model.validators.ValidBidsDateString;
import static javax.persistence.GenerationType.IDENTITY;
import org.hibernate.validator.constraints.NotEmpty;

@Entity
@XmlRootElement
@Table(uniqueConstraints =
               {
                       @UniqueConstraint(columnNames = "deployIdentifier")
               }
)
public class BidsDeployment
{
    @NotNull
    @NotEmpty
    private String deployIdentifier;
    @NotNull
    @NotEmpty
    private String artifactId;
    @NotNull
    @NotEmpty
    private String version;
    @NotNull
    @ManyToOne
    @JoinColumn(name = "bids_day_id")
    private BidsDay bidsDay;
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

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

    public BidsDay getBidsDay()
    {
        return bidsDay;
    }

    public void setBidsDay(BidsDay bidsDay)
    {
        this.bidsDay = bidsDay;
    }

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getDeployIdentifier()
    {
        return deployIdentifier;
    }

    public void setDeployIdentifier(String deployIdentifier)
    {
        this.deployIdentifier = deployIdentifier;
    }

}
