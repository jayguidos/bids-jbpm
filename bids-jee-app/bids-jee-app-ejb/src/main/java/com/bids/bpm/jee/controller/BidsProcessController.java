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
import java.util.List;
import java.util.logging.Logger;

import javax.ejb.Stateful;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;


import com.bids.bpm.facts.model.BidsDay;
import com.bids.bpm.jee.cdi.BidsKieManager;
import com.bids.bpm.jee.data.BidsDayProducer;
import com.bids.bpm.jee.data.BidsDeploymentsProducer;
import com.bids.bpm.jee.kie.BidsDeploymentUnit;
import com.bids.bpm.jee.model.BidsActiveProcess;
import com.bids.bpm.jee.model.BidsDeployment;
import com.bids.bpm.jee.util.GlobalLogDir;
import static com.bids.bpm.shared.BidsBPMConstants.BIDS_MAVEN_GROUP;
import static com.bids.bpm.shared.BidsBPMConstants.GLBL_KSESSION;
import static com.bids.bpm.shared.BidsBPMConstants.GLBL_LOG_DIR_HOME;
import org.jbpm.kie.services.impl.KModuleDeploymentUnit;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.api.runtime.rule.QueryResultsRow;

@Stateful
public class BidsProcessController
{
    @PersistenceContext(unitName = "org.jbpm.domain")
    EntityManager em;
    @Inject
    private BidsKieManager kieManager;
    @Inject
    private BidsDeploymentsProducer deploymentsProducer;
    @Inject
    private BidsDayProducer bidsDayProducer;
    @Inject
    @GlobalLogDir
    private File logBaseDir;
    @Inject
    private Logger log;

    public boolean deleteWorkDoneItem(String workDoneName, Long bdId)
    {
        BidsDeployment bd = em.find(BidsDeployment.class, bdId);
        KieSession kieSession = extractKieSession(bd);
        QueryResults queryResults = kieSession.getQueryResults("Is this work done", workDoneName);
        for (QueryResultsRow result : queryResults)
        {
            kieSession.delete(result.getFactHandle("work"));
            log.info("Work Done deleted for " + bd.getBidsDay() + ": " + workDoneName);
            return true;
        }
        log.warning("Work Done not found for " + bd.getBidsDay() + ": " + workDoneName);
        return false;
    }

    public BidsDeployment deployModule(BidsDay bidsDay, String artifactId, String version)
    {
        // deploy the module identified by the GAV
        KModuleDeploymentUnit unit = new BidsDeploymentUnit(bidsDay, BIDS_MAVEN_GROUP, artifactId, version);
        kieManager.deployUnit(unit);

        // convert bids day to a persistent version
        if (bidsDayProducer.getBidsDays().containsKey(bidsDay.getDate()))
            bidsDay = bidsDayProducer.getBidsDays().get(bidsDay.getDate());
        else
            em.persist(bidsDay);

        // add all facts and globals
        KieSession kieSession = kieManager.getRuntimeEngine(unit.getIdentifier()).getKieSession();
        kieSession.insert(bidsDay);
        kieSession.setGlobal(GLBL_KSESSION, kieSession);
        kieSession.setGlobal(GLBL_LOG_DIR_HOME, logBaseDir.toString());

        // remember our deployment
        BidsDeployment bd = new BidsDeployment();
        bd.setDeployIdentifier(unit.getIdentifier());
        bd.setArtifactId(artifactId);
        bd.setVersion(version);
        bd.setBidsDay(bidsDay);
        em.persist(bd);

        log.info("Deployed BidsModule " + bd);
        return bd;
    }

    public void dumpAllFacts(Long bdId)
    {
        BidsDeployment bd = em.find(BidsDeployment.class, bdId);
        if (bd == null)
            throw new RuntimeException("Could not dump facts. Deployment does not exist: " + bdId);
        KieSession kieSession = extractKieSession(bd);
        log.info("Dumping facts for " + bd.getBidsDay());
        for (FactHandle fh : kieSession.getFactHandles())
            log.info("Fact: " + kieSession.getObject(fh).toString());
    }

    public BidsDeployment findDeployment(Long bdId)
    {
        return em.find(BidsDeployment.class, bdId);
    }

    public BidsActiveProcess startProcess(Long bdId, String processId)
    {
        BidsDeployment bd = em.find(BidsDeployment.class, bdId);
        if (bd == null)
            throw new RuntimeException("Could not start process. Deployment does not exist: " + bdId);
        KieSession kieSession = extractKieSession(bd);
        ProcessInstance processInstance = kieSession.startProcess(processId);

        log.info("Launched process " + processInstance.getProcessName() + "[pId=" + processInstance.getId() + "] using module " + bd);

        BidsActiveProcess activeProcess = new BidsActiveProcess();
        activeProcess.setDeployment(bd);
        activeProcess.setProcessId(processId);
        activeProcess.setProcessInstanceId(processInstance.getId());
        bd.addProcess(activeProcess);

        return activeProcess;
    }

    public boolean undeployModule(Long bdId)
    {
        BidsDeployment bd = em.find(BidsDeployment.class, bdId);
        if (bd == null)
            throw new RuntimeException("Could not undeploy. Deployment does not exist: " + bdId);

        // reassemble the unit key and undeploy
        KModuleDeploymentUnit unit = new BidsDeploymentUnit(bd.getBidsDay(), BIDS_MAVEN_GROUP, bd.getArtifactId(), bd.getVersion());
        kieManager.undeployUnit(unit);

        // eliminate the deployment
        em.remove(bd);
        log.info("Undeployed BidsModule " + bd);
        return true;
    }

    public List<BidsDeployment> getDeployments()
    {
        return deploymentsProducer.getDeployments();
    }

    private KieSession extractKieSession(BidsDeployment bd)
    {
        return kieManager.getRuntimeEngine(bd.getDeployIdentifier()).getKieSession();
    }

}
