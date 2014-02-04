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
import org.kie.api.runtime.manager.RuntimeManager;

public class JobControlWorkItemHandlerFactory
        extends AbstractBidsWorkItemHandlerFactoryImpl<JobControlWorkItemHandler>
{

    private final String targetHost;
    private final JobControlType jobControlType;


    public JobControlWorkItemHandlerFactory(JobControlType jobControlType, RuntimeManager runtimeManager, File logBaseDir, String targetHost)
    {
        super(runtimeManager, logBaseDir);
        this.jobControlType = jobControlType;
        this.targetHost = targetHost;
    }

    public JobControlWorkItemHandler makeWorkItem()
    {
        JobControlWorkItemHandler jobControlWorkItemHandler = new JobControlWorkItemHandler(logBaseDir);
        jobControlWorkItemHandler.setRuntimeManager(runtimeManager);
        jobControlWorkItemHandler.setJobControlType(jobControlType);
        if ( targetHost != null && targetHost.trim().length() > 0 )
            jobControlWorkItemHandler.setTargetHost(targetHost);
        return jobControlWorkItemHandler;
    }
}
