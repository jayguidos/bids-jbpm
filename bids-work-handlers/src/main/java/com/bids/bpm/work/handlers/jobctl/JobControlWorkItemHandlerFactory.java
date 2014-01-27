/**
 * ===========================================================================
 *    Copyright 2012 BIDS Holdings L.P. All rights reserved.
 * ---------------------------------------------------------------------------
 * Created on 12/23/13
 * By bidsjagu
 *
 */

package com.bids.bpm.work.handlers.jobctl;

import java.io.File;


import com.bids.bpm.work.handlers.AbstractBidsWorkItemHandlerFactoryImpl;
import com.bids.bpm.work.handlers.bash.BashScriptWorkItemHandler;
import org.kie.api.runtime.manager.RuntimeManager;

public class JobControlWorkItemHandlerFactory
        extends AbstractBidsWorkItemHandlerFactoryImpl<JobControlWorkItemHandler>
{

    private final String targetHost;

    public JobControlWorkItemHandlerFactory(RuntimeManager runtimeManager, File logBaseDir)
    {
        this(runtimeManager, logBaseDir, null);
    }

    public JobControlWorkItemHandlerFactory(RuntimeManager runtimeManager, File logBaseDir, String targetHost)
    {
        super(runtimeManager, logBaseDir);
        this.targetHost = targetHost;
    }

    public JobControlWorkItemHandler makeWorkItem()
    {
        JobControlWorkItemHandler jobControlWorkItemHandler = new JobControlWorkItemHandler();
        jobControlWorkItemHandler.setRuntimeManager(runtimeManager);
        jobControlWorkItemHandler.setLogBaseDir(logBaseDir);
        if ( targetHost != null && targetHost.trim().length() > 0 )
            jobControlWorkItemHandler.setTargetHost(targetHost);
        return jobControlWorkItemHandler;
    }
}
