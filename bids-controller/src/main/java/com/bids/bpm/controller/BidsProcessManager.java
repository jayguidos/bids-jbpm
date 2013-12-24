/**
 * ===========================================================================
 *    Copyright 2012 BIDS Holdings L.P. All rights reserved.
 * ---------------------------------------------------------------------------
 * Created on 12/19/13
 * By bidsjagu
 *
 */

package com.bids.bpm.controller;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;


import org.jbpm.kie.services.impl.KModuleDeploymentUnit;
import org.jbpm.kie.services.impl.model.ProcessDesc;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.internal.runtime.manager.context.EmptyContext;

@ApplicationScoped
public class BidsProcessManager
{
     @Inject
    BidsProcessEngineService bidsEngine;

    public void deployUnit(String groupId, String artifactId, String version)
    {
        KModuleDeploymentUnit unit = new KModuleDeploymentUnit(groupId,artifactId,version);
        bidsEngine.deployUnit(unit);
        KieSession kieSession = bidsEngine.getRuntimeManager(unit.getIdentifier()).getRuntimeEngine(EmptyContext.get()).getKieSession();
    }

    public void startProcess(String groupId, String artifactId, String version, String processId)
    {
        KModuleDeploymentUnit unit = new KModuleDeploymentUnit(groupId,artifactId,version);
        RuntimeManager manager = bidsEngine.getRuntimeManager(unit.getIdentifier());
        for (ProcessDesc processDesc : bidsEngine.getProcesses(unit.getIdentifier()))
        {
            if ( processDesc.getId().equals(processId))
                manager.getRuntimeEngine(EmptyContext.get()).getKieSession().startProcess(processId);
        }
    }
}
