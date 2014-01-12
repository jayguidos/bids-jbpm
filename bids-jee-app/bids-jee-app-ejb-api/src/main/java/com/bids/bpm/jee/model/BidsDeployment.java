/**
 * ===========================================================================
 *    Copyright 2012 BIDS Holdings L.P. All rights reserved.
 * ---------------------------------------------------------------------------
 * Created on 12/31/13
 * By bidsjagu
 *
 */

package com.bids.bpm.jee.model;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;


import com.bids.bpm.facts.model.BidsDay;
import com.bids.bpm.facts.model.validators.ValidBidsDateString;
import static javax.persistence.CascadeType.ALL;
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

    @OneToMany(cascade = ALL, mappedBy = "deployment")
    private Set<BidsActiveProcess> processes;

    public BidsDeployment()
    {
    }

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

    public Set<BidsActiveProcess> getProcesses()
    {
        return processes;
    }

    public void setProcesses(Set<BidsActiveProcess> processes)
    {
        this.processes = processes;
    }

    public void addProcess(BidsActiveProcess p)
    {
        p.setDeployment(this);
        this.processes.add(p);
    }

    @Override
    public String toString()
    {
        return "BidsDeployment{" +
                "id=" + id +
                ", deployIdentifier='" + deployIdentifier + '\'' +
                ", artifactId='" + artifactId + '\'' +
                ", version='" + version + '\'' +
                ", bidsDay=" + bidsDay +
                '}';
    }

    public String getDeployIdentifier()
    {
        return deployIdentifier;
    }

    public void setDeployIdentifier(String deployIdentifier)
    {
        this.deployIdentifier = deployIdentifier;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof BidsDeployment)) return false;

        BidsDeployment that = (BidsDeployment) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;

        return true;
    }

    @Override
    public int hashCode()
    {
        return id != null ? id.hashCode() : 0;
    }
}
