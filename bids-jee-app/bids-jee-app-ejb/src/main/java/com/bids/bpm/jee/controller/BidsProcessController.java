/**
 * ===========================================================================
 *    Copyright 2012 BIDS Holdings L.P. All rights reserved.
 * ---------------------------------------------------------------------------
 * Created on 12/27/13
 * By bidsjagu
 *
 */

package com.bids.bpm.jee.controller;

import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.inject.Inject;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Root;


import com.bids.bpm.jee.kie.BidsDayActivityReporter;
import com.bids.bpm.jee.model.BidsDeployment;
import com.bids.bpm.jee.model.BidsProcessInvocation;
import com.bids.bpm.jee.model.DeployedBidsDayDesc;
import static javax.ejb.TransactionAttributeType.REQUIRES_NEW;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.ProcessInstance;

@Stateless
public class BidsProcessController
        extends BidsController
{
    @Inject
    private Logger log;

    public BidsProcessInvocation completeProcess(long kieProcessInstanceId)
    {
        // query to convert to ID of persistent object
        BidsProcessInvocation process = bidsProcessFromKieProcessInstance(kieProcessInstanceId);
        if (process == null)
            throw new RuntimeException("Could not find Bids Process. Kie process instance is not recorded: " + kieProcessInstanceId);
        process.getDeployment().completeProcess(process);
        return process;
    }

    // this transaction must complete BEFORE the process is started, because processes
    // can run to completion in the KIE startProcess() call and the end process listeners
    // require the process record to be in the DB
    @TransactionAttribute(REQUIRES_NEW)
    public BidsProcessInvocation createProcessInvocation(Long bdId, String kieProcesssId)
    {
        BidsDeployment bd = findDeployment(bdId);
        if (bd == null)
            throw new RuntimeException("Could not start process. Deployment does not exist: " + bdId);
        KieSession kieSession = extractKieSession(bd);
        ProcessInstance processInstance = kieSession.createProcessInstance(kieProcesssId, null);

        log.info("Created process " + processInstance.getProcessName() + "[pId=" + processInstance.getId() + "] using module " + bd);

        BidsProcessInvocation bidsProcess = new BidsProcessInvocation();
        bidsProcess.setDeployment(bd);
        bidsProcess.setKieProcessDescriptionId(kieProcesssId);
        bidsProcess.setKieInstanceId(processInstance.getId());
        bd.startProcess(bidsProcess);

        return bidsProcess;
    }

    public BidsProcessInvocation killProcess(long bidsProcessId)
    {
        BidsProcessInvocation process = em.find(BidsProcessInvocation.class, bidsProcessId);
        if (process == null)
            throw new RuntimeException("Could not find Bids Process. Cannot abort: " + bidsProcessId);
        KieSession kieSession = extractKieSession(process.getDeployment());
        ProcessInstance processInstance = kieSession.getProcessInstance(process.getKieInstanceId());
        if (processInstance != null)
            try
            {
                kieSession.abortProcessInstance(process.getKieInstanceId());
                log.warning("Killed process " + processInstance.getProcessName() + "[pId=" + processInstance.getId() + "] using module " + process.getDeployment());
            } catch (Exception ignored)
            {
                log.warning("Could not kill process " + processInstance.getProcessName() + "[pId=" + processInstance.getId() + "] using module " + process.getDeployment());
            }
        else
            log.warning("Process gone, no kill done for " + processInstance.getProcessName() + "[pId=" + processInstance.getId() + "] using module " + process.getDeployment());

        process.getDeployment().completeProcess(process);
        return process;
    }

    public DeployedBidsDayDesc reportProcessActivity(Long bdId, String processId, boolean withHistory)
    {
        BidsDeployment bd = findDeployment(bdId);
        if (bd == null)
            throw new RuntimeException("Could not report on process activity. Deployment does not exist: " + bdId);
        return new BidsDayActivityReporter(kieManager.getRuntimeDataService(), withHistory).reportProcessActivity(bd, processId);
    }

    public void startProcess(BidsProcessInvocation processInvocation)
    {
        BidsDeployment bd = processInvocation.getDeployment();
        KieSession kieSession = extractKieSession(bd);
        ProcessInstance processInstance = kieSession.startProcessInstance(processInvocation.getKieInstanceId());
        log.info("Started process " + processInstance.getProcessName() + "[pId=" + processInstance.getId() + "] using module " + bd);
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

}
