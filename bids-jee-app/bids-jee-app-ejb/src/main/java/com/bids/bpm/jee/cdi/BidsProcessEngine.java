/**
 * ===========================================================================
 *    Copyright 2012 BIDS Holdings L.P. All rights reserved.
 * ---------------------------------------------------------------------------
 * Created on 12/19/13
 * By bidsjagu
 *
 */

package com.bids.bpm.jee.cdi;

import java.util.Collection;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;


import org.jbpm.kie.services.api.DeployedUnit;
import org.jbpm.kie.services.api.DeploymentService;
import org.jbpm.kie.services.api.Kjar;
import org.jbpm.kie.services.api.RuntimeDataService;
import org.jbpm.kie.services.impl.KModuleDeploymentUnit;
import org.jbpm.kie.services.impl.model.ProcessDesc;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.internal.runtime.manager.context.EmptyContext;

@ApplicationScoped
public class BidsProcessEngine
{

    @Inject
    private RuntimeDataService runtimeDataService;

    @Inject
    @Kjar
    private DeploymentService deploymentService;

    /**
     * Deploys given unit into the process engine
     *
     * @param unit unit that represents kjar and runtime strategy
     */
    public void deployUnit(KModuleDeploymentUnit unit)
    {
        deploymentService.deploy(unit);
    }

    /**
     * Undeploys given unit from the process engine
     *
     * @param unit unit that represents kjar
     */
    public void undeployUnit(KModuleDeploymentUnit unit)
    {
        deploymentService.undeploy(unit);
    }

    /**
     * Returns all available process definitions
     *
     * @return
     */
    public Collection<ProcessDesc> getProcesses()
    {
        return runtimeDataService.getProcesses();
    }

    /**
     * Returns all process definitions for given deployment unit (kjar)
     *
     * @param deploymentId unique identifier of unit (kjar)
     * @return
     */
    public Collection<ProcessDesc> getProcesses(String deploymentId)
    {
        return runtimeDataService.getProcessesByDeploymentId(deploymentId);
    }

    /**
     * Returns <code>RuntimeManager</code> instance for given deployment unit (kjar)
     *
     * @param deploymentId unique identifier of unit (kjar)
     * @return null if no RuntimeManager available for given id
     */
    public RuntimeManager getRuntimeManager(String deploymentId)
    {
        DeployedUnit deployedUnit = deploymentService.getDeployedUnit(deploymentId);
        if (deployedUnit == null)
        {
            return null;
        }
        return deployedUnit.getRuntimeManager();
    }

    public RuntimeEngine getRuntimeEngine(String deploymentId)
    {
        // this presumes a Singleton, as per-session lookups require a context
        return getRuntimeManager(deploymentId).getRuntimeEngine(EmptyContext.get());
    }
}
