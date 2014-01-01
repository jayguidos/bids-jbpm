/**
 * ===========================================================================
 *    Copyright 2012 BIDS Holdings L.P. All rights reserved.
 * ---------------------------------------------------------------------------
 * Created on 12/26/13
 * By bidsjagu
 *
 */

package com.bids.bpm.jee.cdi;

import java.io.File;
import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.enterprise.event.Observes;


import com.bids.bpm.facts.db.DBSession;
import static com.bids.bpm.shared.BidsBPMConstants.BIDS_BASH_WORK_ITEM_HANDLER;
import static com.bids.bpm.shared.BidsBPMConstants.BIDS_BPM_LOG_DIR_PROP_NAME;
import static com.bids.bpm.shared.BidsBPMConstants.BIDS_MAVEN_GROUP;
import static com.bids.bpm.shared.BidsBPMConstants.GLBL_DB_SESSION;
import static com.bids.bpm.shared.BidsBPMConstants.GLBL_KSESSION;
import static com.bids.bpm.shared.BidsBPMConstants.GLBL_LOG_DIR_HOME;
import com.bids.bpm.work.handlers.bash.BashScriptWorkItemHandler;
import com.bids.bpm.work.handlers.bash.BashScriptWorkItemHandlerFactory;
import org.apache.log4j.Logger;
import org.jbpm.kie.services.api.DeployedUnit;
import org.jbpm.kie.services.impl.event.Deploy;
import org.jbpm.kie.services.impl.event.DeploymentEvent;
import org.kie.api.runtime.KieSession;
import org.kie.internal.runtime.manager.context.EmptyContext;

public class BidsSingletonScopedRuntimeConfigurationService
{
    private static final Logger log = Logger.getLogger(BidsSingletonScopedRuntimeConfigurationService.class);

    private AtomicBoolean runtimeConfigured = new AtomicBoolean(false);

    public void onDeployedUnit(@Observes @Deploy DeploymentEvent event)
    {

        // store deployed unit info for further needs

        DeployedUnit deployedUnit = event.getDeployedUnit();
        log.info("Heard deployment: " + deployedUnit.getDeploymentUnit().getIdentifier());
        String groupId = deployedUnit.getDeploymentUnit().getIdentifier().split(":")[0];
        if (BIDS_MAVEN_GROUP.equals(groupId))
        {
            log.info("Checking runtime for BIDS configuration");
            KieSession kieSession = deployedUnit.getRuntimeManager().getRuntimeEngine(EmptyContext.get()).getKieSession();
            if ( runtimeConfigured.compareAndSet(false,true))
                configureKieSession(kieSession,new Date(), getBaseLoggingDir());
        }
    }

    private void configureKieSession(KieSession kieSession, Date bidsDay, File baseLoggingDir)
    {
//        kieSession.setGlobal(GLBL_DB_SESSION, new DBSession(bidsDay));
//        kieSession.setGlobal(GLBL_KSESSION, kieSession);
//        kieSession.setGlobal(GLBL_LOG_DIR_HOME, baseLoggingDir);

        BashScriptWorkItemHandler bashWI = new BashScriptWorkItemHandlerFactory(kieSession, baseLoggingDir).makeWorkItem();
        kieSession.getWorkItemManager().registerWorkItemHandler(BIDS_BASH_WORK_ITEM_HANDLER,bashWI);
    }

    private File getBaseLoggingDir()
    {
        String propVal = System.getProperty(BIDS_BPM_LOG_DIR_PROP_NAME);
        if ( propVal == null || propVal.length() == 0 )
            propVal="./bids-logs";
        return new File(propVal);
    }
}
