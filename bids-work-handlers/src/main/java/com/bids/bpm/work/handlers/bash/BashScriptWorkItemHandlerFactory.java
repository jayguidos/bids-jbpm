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


import com.bids.bpm.work.handlers.BidsWorkItemHandlerFactory;
import org.kie.api.runtime.KieSession;

public class BashScriptWorkItemHandlerFactory
        implements BidsWorkItemHandlerFactory<BashScriptWorkItemHandler>
{
    private KieSession ksession;
    private File logBaseDir;

    public BashScriptWorkItemHandlerFactory(KieSession ksession, File logBaseDir)
    {
        this.ksession = ksession;
        this.logBaseDir = logBaseDir;
    }

    public BashScriptWorkItemHandler makeWorkItem()
    {
        BashScriptWorkItemHandler bashScriptWorkItemHandler = new BashScriptWorkItemHandler();
        bashScriptWorkItemHandler.setKsession(ksession);
        bashScriptWorkItemHandler.setLogBaseDir(logBaseDir);
        return bashScriptWorkItemHandler;
    }
}
