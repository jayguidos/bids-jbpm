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
import org.kie.api.runtime.KieSession;
import org.kie.internal.runtime.manager.context.EmptyContext;

public abstract class AbstractBidsWorkItemHandlerFactoryImpl<T extends BidsWorkItemHandler>
        implements BidsWorkItemHandlerFactory<T>
{

    protected final SingletonRuntimeManager runtimeManager;
    protected final File logBaseDir;

    public AbstractBidsWorkItemHandlerFactoryImpl(SingletonRuntimeManager runtimeManager, File logBaseDir)
    {
        this.runtimeManager = runtimeManager;
        this.logBaseDir = logBaseDir;
    }

    protected KieSession getKieSession()
    {
        return this.runtimeManager.getRuntimeEngine(EmptyContext.get()).getKieSession();
    }

}
