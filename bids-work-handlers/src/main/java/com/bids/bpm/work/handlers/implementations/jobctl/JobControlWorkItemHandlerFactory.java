/**
 * ===========================================================================
 *    Copyright 2012 BIDS Holdings L.P. All rights reserved.
 * ---------------------------------------------------------------------------
 * Created on 12/23/13
 * By bidsjagu
 *
 */

package com.bids.bpm.work.handlers.implementations.jobctl;

import java.io.File;


import com.bids.bpm.work.handlers.support.AbstractBidsWorkItemHandlerFactoryImpl;
import org.jbpm.runtime.manager.impl.SingletonRuntimeManager;

public class JobControlWorkItemHandlerFactory
        extends AbstractBidsWorkItemHandlerFactoryImpl<JobControlWorkItemHandler>
{

    private final String targetHost;
    private final JobControlType jobControlType;


    public JobControlWorkItemHandlerFactory(JobControlType jobControlType, SingletonRuntimeManager runtimeManager, File logBaseDir, String targetHost)
    {
        super(runtimeManager, logBaseDir);
        this.jobControlType = jobControlType;
        this.targetHost = targetHost;
    }

    public JobControlWorkItemHandler makeWorkItem()
    {
        JobControlWorkItemHandler jobControlWorkItemHandler = new JobControlWorkItemHandler(getKieSession(),logBaseDir);
        jobControlWorkItemHandler.setJobControlType(jobControlType);
        if (targetHost != null && targetHost.trim().length() > 0)
            jobControlWorkItemHandler.setTargetHost(targetHost);
        return configureItem(jobControlWorkItemHandler);
    }
}
