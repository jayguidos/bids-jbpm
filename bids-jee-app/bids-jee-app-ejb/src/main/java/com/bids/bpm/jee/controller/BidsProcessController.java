/**
 * ===========================================================================
 *    Copyright 2012 BIDS Holdings L.P. All rights reserved.
 * ---------------------------------------------------------------------------
 * Created on 12/27/13
 * By bidsjagu
 *
 */

package com.bids.bpm.jee.controller;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateful;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;


import com.bids.bpm.facts.model.BidsDay;
import com.bids.bpm.jee.cdi.BidsKieManager;
import com.bids.bpm.jee.data.BidsDayProducer;
import com.bids.bpm.jee.data.BidsDeploymentsProducer;
import com.bids.bpm.jee.kie.BidsDeploymentUnit;
import com.bids.bpm.jee.model.BidsDeployment;
import com.bids.bpm.jee.util.GlobalLogDir;
import com.bids.bpm.shared.BidsBPMConstants;
import static com.bids.bpm.shared.BidsBPMConstants.BIDS_MAVEN_GROUP;
import static com.bids.bpm.shared.BidsBPMConstants.GLBL_KSESSION;
import static com.bids.bpm.shared.BidsBPMConstants.GLBL_LOG_DIR_HOME;
import org.jbpm.kie.services.impl.KModuleDeploymentUnit;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.process.ProcessInstance;

@Stateful
public class BidsProcessController
{
    @Inject
    private BidsKieManager kieManager;

    @Inject
    private BidsDeploymentsProducer deploymentsProducer;

    @Inject
    private BidsDayProducer bidsDayProducer;

    @PersistenceContext(unitName = "org.jbpm.domain")
    EntityManager em;

    @Inject
    @GlobalLogDir
    private File logBaseDir;

    public BidsDeployment deployModule(BidsDay bidsDay, String artifactId, String version)
    {

        // deploy the module identified by the GAV
        KModuleDeploymentUnit unit = new BidsDeploymentUnit(bidsDay, BIDS_MAVEN_GROUP, artifactId, version);
        kieManager.deployUnit(unit);

        // convert bids day to a persistent version
        if ( bidsDayProducer.getBidsDays().containsKey(bidsDay.getDate() ) )
            bidsDay = bidsDayProducer.getBidsDays().get(bidsDay.getDate());
        else
            em.persist(bidsDay);

        // add all facts and globals
        KieSession kieSession = kieManager.getRuntimeEngine(unit.getIdentifier()).getKieSession();
        kieSession.insert(bidsDay);
        kieSession.setGlobal(GLBL_KSESSION, kieSession);
        kieSession.setGlobal(GLBL_LOG_DIR_HOME, logBaseDir);

        // remember our deployment
        BidsDeployment bd = new BidsDeployment();
        bd.setDeployIdentifier(unit.getIdentifier());
        bd.setArtifactId(artifactId);
        bd.setVersion(version);
        bd.setBidsDay(bidsDay);
        em.persist(bd);

        return bd;
    }

    public BidsDeployment findDeployment(Long bdId)
    {
        for (BidsDeployment bidsDeployment : deploymentsProducer.getDeployments())
            if ( bidsDeployment.getId().equals(bdId))
                return bidsDeployment;
        return null;
    }

    public boolean undeployModule(Long bdId)
    {
        Map<Date,BidsDay> bidsDays = bidsDayProducer.getBidsDays();
        BidsDeployment bd = findDeployment(bdId);
        if ( bd == null )
            throw new RuntimeException("Could not undeploy. Deployment does not exist: " + bdId);

        // reassemble the unit key and undeploy
        KModuleDeploymentUnit unit = new BidsDeploymentUnit(bd.getBidsDay(), BIDS_MAVEN_GROUP, bd.getArtifactId(), bd.getVersion());
        kieManager.undeployUnit(unit);

        // eliminate the deployment
        em.remove(bd);
        return false;
    }

    public List<BidsDeployment> getDeployments()
    {
        return deploymentsProducer.getDeployments();
    }

    public void startProcess(Long bdId, String processId)
    {
        BidsDeployment bd = findDeployment(bdId);
        RuntimeEngine runtimeEngine = kieManager.getRuntimeEngine(bd.getDeployIdentifier());
        KieSession kieSession = runtimeEngine.getKieSession();
        ProcessInstance processInstance = kieSession.startProcess(processId);
        processInstance.getId();
    }

    private BidsDeploymentUnit fromBidsDeployment(BidsDeployment bd)
    {
        return new BidsDeploymentUnit(bd.getBidsDay(), BIDS_MAVEN_GROUP, bd.getArtifactId(), bd.getVersion());
    }
}
