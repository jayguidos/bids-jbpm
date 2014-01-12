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
import org.kie.api.runtime.manager.RuntimeManager;

public class BashScriptWorkItemHandlerFactory
        extends AbstractBidsWorkItemHandlerFactoryImpl<BashScriptWorkItemHandler>
{

    public BashScriptWorkItemHandlerFactory(RuntimeManager runtimeManager, File logBaseDir)
    {
        super(runtimeManager, logBaseDir);
    }

    public BashScriptWorkItemHandler makeWorkItem()
    {
        BashScriptWorkItemHandler bashScriptWorkItemHandler = new BashScriptWorkItemHandler();
        bashScriptWorkItemHandler.setRuntimeManager(runtimeManager);
        bashScriptWorkItemHandler.setLogBaseDir(logBaseDir);
        return bashScriptWorkItemHandler;
    }
}
