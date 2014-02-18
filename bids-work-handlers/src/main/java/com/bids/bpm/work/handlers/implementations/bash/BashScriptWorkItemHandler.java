/**
 * ===========================================================================
 *    Copyright 2012 BIDS Holdings L.P. All rights reserved.
 * ---------------------------------------------------------------------------
 * Created on 10/5/13
 * By bidsjagu
 *
 */

package com.bids.bpm.work.handlers.implementations.bash;

import java.io.File;


import com.bids.bpm.work.handlers.implementations.bash.worker.BashScriptWorker;
import com.bids.bpm.work.handlers.implementations.bash.worker.BashScriptWorkerConfig;
import com.bids.bpm.work.handlers.support.AbstractBidsWorkItemHandler;
import com.bids.bpm.work.handlers.support.worker.AbstractBidsWorkItemWorker;
import org.apache.log4j.Logger;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.WorkItem;

public class BashScriptWorkItemHandler
        extends AbstractBidsWorkItemHandler
{

    public static final String OUT_STD_OUT = "StdOut";
    public static final String OUT_STD_ERR = "StdErr";
    public static final String BASH_SCRIPT_ERROR_SIGNAL = "BashScriptError";
    private static final Logger log = Logger.getLogger(BashScriptWorkItemHandler.class);
    protected String targetHost;

    public BashScriptWorkItemHandler(KieSession kieSession, File baseLogDir)
    {
        this(kieSession, BASH_SCRIPT_ERROR_SIGNAL, baseLogDir);
    }

    public BashScriptWorkItemHandler(KieSession kieSession, String errorSignalName, File baseLogDir)
    {
        super(kieSession, errorSignalName, baseLogDir);
    }

    public String getTargetHost()
    {
        return targetHost;
    }

    public void setTargetHost(String targetHost)
    {
        this.targetHost = targetHost;
    }

    @Override
    protected AbstractBidsWorkItemWorker makeWorkItemWorker(WorkItem workItem)
    {
        BashScriptWorkerConfig config = new BashScriptWorkerConfig(this.targetHost, workItem, getLogBaseDir());
        config.init(getKsession());
        return new BashScriptWorker(config);
    }

}
