/**
 * ===========================================================================
 *    Copyright 2012 BIDS Holdings L.P. All rights reserved.
 * ---------------------------------------------------------------------------
 * Created on 1/9/14
 * By bidsjagu
 *
 */

package com.bids.bpm.jee.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;


import static javax.persistence.GenerationType.IDENTITY;

// JPA Annotations
@Entity
@Table(uniqueConstraints =
               {
                       @UniqueConstraint(columnNames = "kieInstanceId")
               }
)

// JAXB Annotations
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)

public class BidsProcessInvocation
{
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;
    @NotNull
    @ManyToOne
    @JoinColumn(name = "BIDS_DEPLOY_ID")
    @XmlTransient
    private BidsDeployment deployment;
    @NotNull
    private long kieInstanceId;
    @NotNull
    private String kieProcessDescriptionId;
    // nullable
    private Date startTime;
    // nullable
    private Date endTime;

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof BidsProcessInvocation)) return false;

        BidsProcessInvocation that = (BidsProcessInvocation) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;

        return true;
    }

    // reset the transient marked by @XmlTransient
    public void afterUnmarshal(Unmarshaller u, Object parent) {
        this.deployment = (BidsDeployment)parent;
    }

    @Override
    public int hashCode()
    {
        return id != null ? id.hashCode() : 0;
    }

    public void setEndTime(Date endTime)
    {
        this.endTime = endTime;
    }

    public void setStartTime(Date startTime)
    {
        this.startTime = startTime;
    }

    @Override
    public String toString()
    {
        return "BidsProcessInvocation{" +
                "id=" + id +
                ", kieInstanceId=" + kieInstanceId +
                ", kieProcessDescriptionId='" + kieProcessDescriptionId + '\'' +
                ", deployment=" + deployment +
                '}';
    }

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public BidsDeployment getDeployment()
    {
        return deployment;
    }

    public void setDeployment(BidsDeployment deployment)
    {
        this.deployment = deployment;
    }

    public long getKieInstanceId()
    {
        return kieInstanceId;
    }

    public void setKieInstanceId(long kieInstanceId)
    {
        this.kieInstanceId = kieInstanceId;
    }

    public String getKieProcessDescriptionId()
    {
        return kieProcessDescriptionId;
    }

    public void setKieProcessDescriptionId(String kieProcessDescriptionId)
    {
        this.kieProcessDescriptionId = kieProcessDescriptionId;
    }

    public Date getStartTime()
    {
        return startTime;
    }

    public Date getEndTime()
    {
        return endTime;
    }
}
