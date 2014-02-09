/**
 * ===========================================================================
 *    Copyright 2012 BIDS Holdings L.P. All rights reserved.
 * ---------------------------------------------------------------------------
 * Created on 12/23/13
 * By bidsjagu
 *
 */

package com.bids.bpm.work.handlers.bash;

import java.io.File;


import com.bids.bpm.work.handlers.AbstractBidsWorkItemHandlerFactoryImpl;
import org.jbpm.runtime.manager.impl.SingletonRuntimeManager;

public class BashScriptWorkItemHandlerFactory
        extends AbstractBidsWorkItemHandlerFactoryImpl<BashScriptWorkItemHandler>
{

    private final String targetHost;

    public BashScriptWorkItemHandlerFactory(SingletonRuntimeManager runtimeManager, File logBaseDir)
    {
        this(runtimeManager, logBaseDir, null);
    }

    public BashScriptWorkItemHandlerFactory(SingletonRuntimeManager runtimeManager, File logBaseDir, String targetHost)
    {
        super(runtimeManager, logBaseDir);
        this.targetHost = targetHost;
    }

    public BashScriptWorkItemHandler makeWorkItem()
    {
        BashScriptWorkItemHandler bashScriptWorkItemHandler = new BashScriptWorkItemHandler(getKieSession(),logBaseDir);
        bashScriptWorkItemHandler.setRuntimeManager(runtimeManager);
        if (targetHost != null && targetHost.trim().length() > 0)
            bashScriptWorkItemHandler.setTargetHost(targetHost);
        return bashScriptWorkItemHandler;
    }
}
