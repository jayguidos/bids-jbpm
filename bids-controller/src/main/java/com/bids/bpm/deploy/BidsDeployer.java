/**
 * ===========================================================================
 *    Copyright 2012 BIDS Holdings L.P. All rights reserved.
 * ---------------------------------------------------------------------------
 * Created on 12/23/13
 * By bidsjagu
 *
 */

package com.bids.bpm.deploy;

import java.io.File;
import java.util.Date;


import com.bids.bpm.facts.db.DBSession;
import com.bids.bpm.work.handlers.bash.BashScriptWorkItemHandler;
import com.bids.bpm.work.handlers.bash.BashScriptWorkItemHandlerFactory;
import org.apache.log4j.Logger;
import org.kie.api.KieServices;
import org.kie.api.builder.ReleaseId;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

public class BidsDeployer
{

    private static final Logger log = Logger.getLogger(BidsDeployer.class);

    public void deployBidsModule(String artifact, String version, Date bidsDay, File baseLoggingDir)
    {
        KieServices kieServices = KieServices.Factory.get();
        ReleaseId releaseId = kieServices.newReleaseId("bids-jbpm", artifact, version);
        log.info("Preparing to deploy: " + releaseId);
        KieContainer kieContainer = kieServices.newKieContainer(releaseId);
        log.info("Creating BidsDay Session for ");
        KieSession kieSession = kieContainer.newKieSession();
        kieSession.setGlobal("BBB", new DBSession(bidsDay));
        BashScriptWorkItemHandler bashWI = new BashScriptWorkItemHandlerFactory(kieSession, baseLoggingDir).makeWorkItem();
        kieSession.getWorkItemManager().registerWorkItemHandler("",bashWI);
    }

}
