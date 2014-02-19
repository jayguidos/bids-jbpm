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


import com.bids.bpm.work.handlers.implementations.bash.BashScriptWorkItemHandler;
import com.bids.bpm.work.handlers.implementations.sql.worker.SQLRunnerWorker;
import com.bids.bpm.work.handlers.implementations.sql.worker.SQLRunnerWorkerConfig;
import com.bids.bpm.work.handlers.support.worker.AbstractBidsWorkItemWorker;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.WorkItem;

public class SQLRunnerWorkItemHandler
        extends BashScriptWorkItemHandler
{

    public static final String SQL_RUNNER_ERROR_SIGNAL = "SQLRunnerError";

    public SQLRunnerWorkItemHandler(KieSession kieSession, File baseLogDir)
    {
        super(kieSession, SQL_RUNNER_ERROR_SIGNAL, baseLogDir);
    }

    @Override
    protected AbstractBidsWorkItemWorker makeWorkItemWorker(WorkItem workItem)
    {
        SQLRunnerWorkerConfig config = new SQLRunnerWorkerConfig(getTargetHost(), workItem, getLogBaseDir());
        config.init(getKsession());
        return new SQLRunnerWorker(config);
    }
}
