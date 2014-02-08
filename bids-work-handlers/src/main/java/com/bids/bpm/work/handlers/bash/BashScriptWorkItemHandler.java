/**
 * ===========================================================================
 *    Copyright 2012 BIDS Holdings L.P. All rights reserved.
 * ---------------------------------------------------------------------------
 * Created on 10/5/13
 * By bidsjagu
 *
 */

package com.bids.bpm.work.handlers.bash;

import java.io.File;


import com.bids.bpm.work.handlers.BidsWorkItemHandler;
import com.bids.bpm.work.handlers.bash.worker.BashScriptWorker;
import com.bids.bpm.work.handlers.bash.worker.BashScriptWorkerConfig;
import com.bids.bpm.work.handlers.worker.BidsWorkItemWorker;
import org.apache.log4j.Logger;
import org.kie.api.runtime.process.WorkItem;

public class BashScriptWorkItemHandler
        extends BidsWorkItemHandler
{

    public static final String OUT_STD_OUT = "StdOut";
    public static final String OUT_STD_ERR = "StdErr";
    public static final String BASH_SCRIPT_ERROR_SIGNAL = "BashScriptError";
    private static final Logger log = Logger.getLogger(BashScriptWorkItemHandler.class);
    protected String targetHost;

    public BashScriptWorkItemHandler(File baseLogDir)
    {
        this(BASH_SCRIPT_ERROR_SIGNAL,baseLogDir);
    }

    public BashScriptWorkItemHandler(String errorSignalName, File baseLogDir)
    {
        super(errorSignalName, baseLogDir);
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
    protected BidsWorkItemWorker makeWorkItemWorker(WorkItem workItem)
    {
        BashScriptWorkerConfig config = new BashScriptWorkerConfig(this.targetHost,workItem,getLogBaseDir());
        config.init(getKsession());
        return new BashScriptWorker(config);
    }

}
