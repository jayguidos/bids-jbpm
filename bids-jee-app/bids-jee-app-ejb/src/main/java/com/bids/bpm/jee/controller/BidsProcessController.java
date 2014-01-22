/**
 * ===========================================================================
 *    Copyright 2012 BIDS Holdings L.P. All rights reserved.
 * ---------------------------------------------------------------------------
 * Created on 12/27/13
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
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Root;


import com.bids.bpm.facts.model.BidsDay;
import com.bids.bpm.jee.cdi.BidsKieManager;
import com.bids.bpm.jee.data.BidsDayProducer;
import com.bids.bpm.jee.data.BidsDeploymentsProducer;
import com.bids.bpm.jee.kie.BidsDeploymentUnit;
import com.bids.bpm.jee.model.BidsDeployment;
import com.bids.bpm.jee.model.BidsProcessInvocation;
import com.bids.bpm.jee.util.BidsJBPMConfiguration;
import static com.bids.bpm.shared.BidsBPMConstants.BIDS_MAVEN_GROUP;
import static com.bids.bpm.shared.BidsBPMConstants.GLBL_KSESSION;
import static com.bids.bpm.shared.BidsBPMConstants.GLBL_LOG_DIR_HOME;
import org.jbpm.kie.services.impl.KModuleDeploymentUnit;
import org.kie.api.event.process.DefaultProcessEventListener;
import org.kie.api.event.process.ProcessCompletedEvent;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.api.runtime.rule.QueryResultsRow;

@Stateless
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
    private BidsJBPMConfiguration config;
    @Inject
    private Logger log;
    @Resource
    private SessionContext context;

    public BidsProcessInvocation completeProcess(long kieProcessInstanceId)
    {
        // query to convert to ID of persistent object
        BidsProcessInvocation process = bidsProcessFromKieProcessInstance(kieProcessInstanceId);
        if (process == null)
            throw new RuntimeException("Could not find Bids Process. Kie process instance is not recorded: " + kieProcessInstanceId);
        process.getDeployment().completeProcess(process);
        return process;
    }

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
        kieSession.setGlobal(GLBL_LOG_DIR_HOME, config.getGlobalLogDir().toString());
        kieSession.addEventListener(new DefaultProcessEventListener()
        {
            @Override
            public void afterProcessCompleted(ProcessCompletedEvent event)
            {
                ProcessInstance processInstance = event.getProcessInstance();
                BidsProcessInvocation process = context.getBusinessObject(BidsProcessController.class).completeProcess(processInstance.getId());
                log.info("Completed process " + processInstance.getProcessName() + "[pId=" + processInstance.getId() + "] using module " + process.getDeployment());
            }
        });

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
        BidsDeployment bd = em.find(BidsDeployment.class, bdId);
        if (bd == null)
            throw new RuntimeException("Could not dump facts. Deployment does not exist: " + bdId);
        ArrayList<String> facts = new ArrayList<String>();
        KieSession kieSession = extractKieSession(bd);
        log.info("Dumping facts for " + bd.getBidsDay());
        for (FactHandle fh : kieSession.getFactHandles())
            facts.add(kieSession.getObject(fh).toString());
        return facts;
    }

    public BidsDeployment findDeployment(Long bdId)
    {
        return em.find(BidsDeployment.class, bdId);
    }

    // this is only called via bootstrapping
    public void redeployOnRestart(BidsDeployment bd)
    {
        // reassemble the unit key and redeploy to rebuild all runtimes
        KModuleDeploymentUnit unit = new BidsDeploymentUnit(bd.getBidsDay(), BIDS_MAVEN_GROUP, bd.getArtifactId(), bd.getVersion());
        log.info("Re-deploying BidsModule " + bd);
        kieManager.deployUnit(unit);
    }

    public BidsProcessInvocation startProcess(Long bdId, String processId)
    {
        BidsDeployment bd = em.find(BidsDeployment.class, bdId);
        if (bd == null)
            throw new RuntimeException("Could not start process. Deployment does not exist: " + bdId);
        KieSession kieSession = extractKieSession(bd);
        ProcessInstance processInstance = kieSession.startProcess(processId);

        log.info("Launched process " + processInstance.getProcessName() + "[pId=" + processInstance.getId() + "] using module " + bd);

        BidsProcessInvocation activeProcess = new BidsProcessInvocation();
        activeProcess.setDeployment(bd);
        activeProcess.setKieProcessDescriptionId(processId);
        activeProcess.setKieInstanceId(processInstance.getId());
        bd.startProcess(activeProcess);

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

    private BidsProcessInvocation bidsProcessFromKieProcessInstance(long kieProcessInstanceId)
    {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<BidsProcessInvocation> query = cb.createQuery(BidsProcessInvocation.class);
        Root<BidsProcessInvocation> bpi = query.from(BidsProcessInvocation.class);

        ParameterExpression<Long> instanceParameter = cb.parameter(Long.class);
        query.select(bpi).where(
                cb.equal(
                        bpi.get("kieInstanceId"), instanceParameter
                )
        );

        return em.createQuery(query).setParameter(instanceParameter, kieProcessInstanceId).getSingleResult();
    }

    private KieSession extractKieSession(BidsDeployment bd)
    {
        return kieManager.getRuntimeEngine(bd.getDeployIdentifier()).getKieSession();
    }

}
