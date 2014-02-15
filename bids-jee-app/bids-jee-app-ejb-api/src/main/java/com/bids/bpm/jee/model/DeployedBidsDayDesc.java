/**
 * ===========================================================================
 *    Copyright 2012 BIDS Holdings L.P. All rights reserved.
 * ---------------------------------------------------------------------------
 * Created on 2/14/14
 * By bidsjagu
 *
 */

package com.bids.bpm.jee.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;


import com.bids.bpm.facts.model.BidsDay;

@XmlRootElement( name = "deployment" )
@XmlAccessorType(XmlAccessType.FIELD)
public class DeployedBidsDayDesc
{
    private Long id;
    private BidsDay bidsDay;
    @XmlElementWrapper(name = "processes")
    @XmlElement(name = "process")
    private List<DeployedProcessDesc> processes = new ArrayList<DeployedProcessDesc>();
    private String deploymentId;

    public DeployedBidsDayDesc()
    {
    }

    public DeployedBidsDayDesc(BidsDeployment bd)
    {
        this.id = bd.getId();
        this.bidsDay = bd.getBidsDay();
        this.deploymentId = bd.getDeployIdentifier();
    }

    public void addProcess(DeployedProcessDesc dp)
    {
        dp.setBidsDay(this);
        processes.add(dp);
    }

    public String getDeploymentId()
    {
        return deploymentId;
    }

    public Long getId()
    {
        return id;
    }

    public BidsDay getBidsDay()
    {
        return bidsDay;
    }

    public List<DeployedProcessDesc> getProcesses()
    {
        return processes;
    }
}
