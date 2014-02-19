/**
 * ===========================================================================
 *    Copyright 2012 BIDS Holdings L.P. All rights reserved.
 * ---------------------------------------------------------------------------
 * Created on 2/18/14
 * By bidsjagu
 *
 */

package com.bids.bpm.work.handlers.implementations.sql;

import java.io.File;


import com.bids.bpm.work.handlers.support.AbstractBidsWorkItemHandlerFactoryImpl;
import org.jbpm.runtime.manager.impl.SingletonRuntimeManager;

public class SQLRunnerWorkItemHandlerFactory
        extends AbstractBidsWorkItemHandlerFactoryImpl<SQLRunnerWorkItemHandler>

{
    private final String targetHost;

    public SQLRunnerWorkItemHandlerFactory(SingletonRuntimeManager runtimeManager, File logBaseDir, String targetHost)
    {
        super(runtimeManager, logBaseDir);
        this.targetHost = targetHost;
    }

    public SQLRunnerWorkItemHandler makeWorkItem()
    {
        SQLRunnerWorkItemHandler sqlRunnerWorkItemHandler = new SQLRunnerWorkItemHandler(getKieSession(), logBaseDir);
        if (targetHost != null && targetHost.trim().length() > 0)
            sqlRunnerWorkItemHandler.setTargetHost(targetHost);
        return configureItem(sqlRunnerWorkItemHandler);
    }
}
