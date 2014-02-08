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


import org.jbpm.runtime.manager.impl.SingletonRuntimeManager;

public abstract class AbstractBidsWorkItemHandlerFactoryImpl<T extends BidsWorkItemHandler>
        implements BidsWorkItemHandlerFactory<T>
{

    public SingletonRuntimeManager runtimeManager;
    protected File logBaseDir;

    public AbstractBidsWorkItemHandlerFactoryImpl(SingletonRuntimeManager runtimeManager, File logBaseDir)
    {
        this.runtimeManager = runtimeManager;
        this.logBaseDir = logBaseDir;
    }
}
