/**
 * ===========================================================================
 *    Copyright 2012 BIDS Holdings L.P. All rights reserved.
 * ---------------------------------------------------------------------------
 * Created on 2/14/14
 * By bidsjagu
 *
 */

package com.bids.bpm.jee.kie;

import java.util.Collection;
import java.util.List;


import com.bids.bpm.jee.model.DeployedBidsDayDesc;
import com.bids.bpm.jee.model.BidsDeployment;
import com.bids.bpm.jee.model.DeployedProcessActivity;
import com.bids.bpm.jee.model.DeployedProcessDesc;
import com.bids.bpm.jee.model.DeployedProcessNodeActivity;
import org.jbpm.kie.services.api.RuntimeDataService;
import org.jbpm.kie.services.impl.model.NodeInstanceDesc;
import org.jbpm.kie.services.impl.model.ProcessAssetDesc;
import org.jbpm.kie.services.impl.model.ProcessInstanceDesc;

public class BidsDayActivityReporter
{

    private final RuntimeDataService rds;
    private final boolean withHistory;
    private final List<Integer> allStates = DeployedProcessActivity.State.asIntegers();

    public BidsDayActivityReporter(RuntimeDataService rds, boolean withHistory)
    {
        this.rds = rds;
        this.withHistory = withHistory;
    }

    public DeployedBidsDayDesc reportActivity(BidsDeployment bd)
    {
        Collection<ProcessAssetDesc> processes = rds.getProcessesByDeploymentId(bd.getDeployIdentifier());
        DeployedBidsDayDesc dd = new DeployedBidsDayDesc(bd);
        for (ProcessAssetDesc pd : processes)
            addProcessAssets(dd, pd);
        return dd;
    }

    protected void addProcessAssets(DeployedBidsDayDesc dd,ProcessAssetDesc p)
    {
        DeployedProcessDesc dp = new DeployedProcessDesc(p.getId());
        dd.addProcess(dp);
        for (ProcessInstanceDesc pid : rds.getProcessInstancesByProcessId(allStates, dp.getProcessId(), null))
            addProcessInstance(dp, pid);
    }

    private void addProcessInstance(DeployedProcessDesc dp, ProcessInstanceDesc pid)
    {
        DeployedProcessActivity dpa = new DeployedProcessActivity(pid.getId(), pid.getState(), pid.getDataTimeStamp());
        String deploymentId = dp.getBidsDay().getDeploymentId();
        Collection<NodeInstanceDesc> nodeHistory;
        if ( withHistory )
            nodeHistory = rds.getProcessInstanceFullHistory(deploymentId,pid.getId());
        else
            nodeHistory = rds.getProcessInstanceActiveNodes(deploymentId, pid.getId());
        for ( NodeInstanceDesc nid : nodeHistory)
            dpa.add(new DeployedProcessNodeActivity(
                    dpa,
                    nid.getId(),
                    nid.getNodeId(),
                    nid.getNodeType(),
                    nid.getName(),
                    nid.isCompleted(),
                    nid.getDataTimeStamp())
            );
        dp.addActivity(dpa);
    }
}

