/**
 * ===========================================================================
 *    Copyright 2012 BIDS Holdings L.P. All rights reserved.
 * ---------------------------------------------------------------------------
 * Created on 1/9/14
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
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;


import static javax.persistence.GenerationType.IDENTITY;

@Entity
@XmlRootElement
@Table()
public class BidsActiveProcess
{
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "BIDS_DEPLOY_ID")
    private BidsDeployment deployment;


    @NotNull
    private long processInstanceId;

    @NotNull
    private String processId;

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

    public long getProcessInstanceId()
    {
        return processInstanceId;
    }

    public void setProcessInstanceId(long processInstanceId)
    {
        this.processInstanceId = processInstanceId;
    }

    public String getProcessId()
    {
        return processId;
    }

    public void setProcessId(String processId)
    {
        this.processId = processId;
    }

    @Override
    public String toString()
    {
        return "BidsActiveProcess{" +
                "id=" + id +
                ", processInstanceId=" + processInstanceId +
                ", processId='" + processId + '\'' +
                ", deployment=" + deployment +
                '}';
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof BidsActiveProcess)) return false;

        BidsActiveProcess that = (BidsActiveProcess) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;

        return true;
    }

    @Override
    public int hashCode()
    {
        return id != null ? id.hashCode() : 0;
    }
}
