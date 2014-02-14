/**
 * ===========================================================================
 *    Copyright 2012 BIDS Holdings L.P. All rights reserved.
 * ---------------------------------------------------------------------------
 * Created on 2/13/14
 * By bidsjagu
 *
 */

package com.bids.bpm.jee.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.inject.Inject;


import com.bids.bpm.facts.model.BidsDay;
import com.bids.bpm.facts.model.BidsFact;
import com.bids.bpm.facts.model.WorkDone;
import com.bids.bpm.jee.data.BidsDayProducer;
import com.bids.bpm.jee.kie.BidsDeploymentUnit;
import com.bids.bpm.jee.model.BidsDeployment;
import com.bids.bpm.jee.model.BidsProcessInvocation;
import static com.bids.bpm.shared.BidsBPMConstants.BIDS_MAVEN_GROUP;
import static com.bids.bpm.shared.BidsBPMConstants.GLBL_FACT_MANAGER;
import static com.bids.bpm.shared.BidsBPMConstants.GLBL_KSESSION;
import static com.bids.bpm.shared.BidsBPMConstants.GLBL_LOG_DIR_HOME;
import com.bids.bpm.work.handlers.fact.KieSessionBidsFactManager;
import org.jbpm.kie.services.impl.KModuleDeploymentUnit;
import org.kie.api.event.process.DefaultProcessEventListener;
import org.kie.api.event.process.ProcessCompletedEvent;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.ProcessInstance;

@Stateless
public class BidsDayController
        extends BidsController
{
    @Inject
    private BidsDayProducer bidsDayProducer;
    @Inject
    private Logger log;
    @Inject
    BidsProcessController bpc;

    public String deleteWorkDoneItem(Long bdId, Long workId)
    {
        BidsDeployment bd = findDeployment(bdId);
        if (bd == null)
            throw new RuntimeException("Could not delete work. Deployment does not exist: " + bdId);
        KieSession kieSession = extractKieSession(bd);
        KieSessionBidsFactManager factManager = new KieSessionBidsFactManager(kieSession);
        WorkDone work = factManager.get(workId);
        if (work == null)
        {
            log.warning("Work Done not found for " + bd.getBidsDay() + ": " + workId);
            return null;
        }
        else
        {
            factManager.delete(work);
            log.info("Work Done deleted for " + bd.getBidsDay() + ": " + work.getName() + "(id=" + work.getId() + ")");
            return work.toString();
        }
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
        kieSession.setGlobal(GLBL_FACT_MANAGER, new KieSessionBidsFactManager(kieSession));
        kieSession.setGlobal(GLBL_LOG_DIR_HOME, config.getGlobalLogDir().toString());
        kieSession.addEventListener(new DefaultProcessEventListener()
        {
            @Override
            public void afterProcessCompleted(ProcessCompletedEvent event)
            {
                ProcessInstance processInstance = event.getProcessInstance();
                BidsProcessInvocation process = bpc.completeProcess(processInstance.getId());
                log.info("Completed process " + processInstance.getProcessName() + "[pId=" + processInstance.getId() + "] using module " + process.getDeployment());
            }
        });
        kieSession.addEventListener(new BidsDayEventLogger(bidsDay));

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

    public List<String> dumpAllFacts(Long bdId)
    {
        BidsDeployment bd = findDeployment(bdId);
        if (bd == null)
            throw new RuntimeException("Could not dump facts. Deployment does not exist: " + bdId);
        ArrayList<String> facts = new ArrayList<String>();
        log.info("Dumping facts for " + bd.getBidsDay());
        for (BidsFact bidsFact : new KieSessionBidsFactManager(extractKieSession(bd)).findAll())
            facts.add(bidsFact.toString());
        return facts;
    }

    // this is only called via bootstrapping
    public void redeployOnRestart(BidsDeployment bd)
    {
        // reassemble the unit key and redeploy to rebuild all runtimes
        KModuleDeploymentUnit unit = new BidsDeploymentUnit(bd.getBidsDay(), BIDS_MAVEN_GROUP, bd.getArtifactId(), bd.getVersion());
        log.info("Re-deploying BidsModule " + bd);
        kieManager.deployUnit(unit);
    }

    public boolean signal(Long bdId, String signalName)
    {
        BidsDeployment bd = findDeployment(bdId);
        if (bd == null)
            throw new RuntimeException("Could not signal. Deployment does not exist: " + bdId);
        log.info("Sending signal " + signalName + " to BidsDay: " + bd);
        extractKieSession(bd).signalEvent(signalName, null);
        return true;
    }

    public boolean undeployModule(Long bdId)
    {
        BidsDeployment bd = findDeployment(bdId);
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

}

