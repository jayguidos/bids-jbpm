/**
 * ===========================================================================
 *    Copyright 2012 BIDS Holdings L.P. All rights reserved.
 * ---------------------------------------------------------------------------
 * Created on 12/31/13
 * By bidsjagu
 *
 */

package com.bids.bpm.jee.kie;

import com.bids.bpm.facts.model.BidsDay;
import org.jbpm.kie.services.impl.KModuleDeploymentUnit;

public class BidsDeploymentUnit
        extends KModuleDeploymentUnit
{

    private final BidsDay bidsDay;

    public BidsDeploymentUnit(BidsDay bidsDay, String groupId, String artifactId, String version)
    {
        super(groupId, artifactId, version);
        this.bidsDay = bidsDay;
    }

    public BidsDeploymentUnit(BidsDay bidsDay, String groupId, String artifactId, String version, String kbaseName, String ksessionName)
    {
        super(groupId, artifactId, version, kbaseName, ksessionName);
        this.bidsDay = bidsDay;
    }

    public BidsDeploymentUnit(BidsDay bidsDay, String groupId, String artifactId, String version, String kbaseName, String ksessionName, String strategy)
    {
        super(groupId, artifactId, version, kbaseName, ksessionName, strategy);
        this.bidsDay = bidsDay;
    }

    @Override
    public String getIdentifier()
    {
        return bidsDay != null ? (super.getIdentifier() + ":" + bidsDay) : super.getIdentifier();
    }

    @Override
    public RuntimeStrategy getStrategy()
    {
        return RuntimeStrategy.SINGLETON;
    }

    public BidsDay getBidsDay()
    {
        return bidsDay;
    }
}


