/**
 * ===========================================================================
 *    Copyright 2012 BIDS Holdings L.P. All rights reserved.
 * ---------------------------------------------------------------------------
 * Created on 1/10/14
 * By bidsjagu
 *
 */

package com.bids.bpm.work.handlers;

import java.io.File;


import org.kie.api.runtime.manager.RuntimeManager;

public abstract class AbstractBidsWorkItemHandlerFactoryImpl<T extends BidsWorkItemHandler>
    implements BidsWorkItemHandlerFactory<T>
{

    public RuntimeManager runtimeManager;
    protected File logBaseDir;

    public AbstractBidsWorkItemHandlerFactoryImpl(RuntimeManager runtimeManager, File logBaseDir)
    {
        this.runtimeManager = runtimeManager;
        this.logBaseDir = logBaseDir;
    }
}
