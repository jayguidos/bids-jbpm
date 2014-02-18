/**
 * ===========================================================================
 *    Copyright 2012 BIDS Holdings L.P. All rights reserved.
 * ---------------------------------------------------------------------------
 * Created on 1/10/14
 * By bidsjagu
 *
 */

package com.bids.bpm.work.handlers.support;

import java.io.File;


import com.bids.bpm.work.handlers.support.fact.FactIDFactory;
import org.jbpm.runtime.manager.impl.SingletonRuntimeManager;
import org.kie.api.runtime.KieSession;
import org.kie.internal.runtime.manager.context.EmptyContext;

public abstract class AbstractBidsWorkItemHandlerFactoryImpl<T extends AbstractBidsWorkItemHandler>
        implements BidsWorkItemHandlerFactory<T>
{

    protected final SingletonRuntimeManager runtimeManager;
    protected final File logBaseDir;
    protected FactIDFactory factIDFactory;

    public AbstractBidsWorkItemHandlerFactoryImpl(SingletonRuntimeManager runtimeManager, File logBaseDir)
    {
        this.runtimeManager = runtimeManager;
        this.logBaseDir = logBaseDir;
    }

    protected KieSession getKieSession()
    {
        return this.runtimeManager.getRuntimeEngine(EmptyContext.get()).getKieSession();
    }

    public FactIDFactory getFactIDFactory()
    {
        return factIDFactory;
    }

    public void setFactIDFactory(FactIDFactory factIDFactory)
    {
        this.factIDFactory = factIDFactory;
    }

    protected T  configureItem(T item)
    {
        item.setRuntimeManager(runtimeManager);
        item.setFactIDFactory(factIDFactory);
        return item;
    }
}
