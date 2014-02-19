/**
 * ===========================================================================
 *    Copyright 2012 BIDS Holdings L.P. All rights reserved.
 * ---------------------------------------------------------------------------
 * Created on 2/18/14
 * By bidsjagu
 *
 */

package com.bids.bpm.work.handlers.implementations.sql.worker;

import java.io.File;


import com.bids.bpm.work.handlers.implementations.bash.worker.BashScriptWorkerConfig;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.WorkItem;

public class SQLRunnerWorkerConfig
        extends BashScriptWorkerConfig
{
    public static final String OPS_RUN_SQLPLUS_SH = "ops_run_sqlplus.sh";
    public static final String IN_SQL_SCRIPT_FILE_NAME = "SQLScriptFileName";
    public static final String IN_SQL_SCRIPT_ARGS = "SQLScriptArgs";
    private String sqlScriptFile;
    private String sqlScriptArgs;

    public SQLRunnerWorkerConfig(String targetHost, WorkItem workItem, File scriptLogDir)
    {
        super(targetHost, workItem, scriptLogDir);
    }

    @Override
    public void init(KieSession kieSession)
    {
        // these are mine
        this.sqlScriptFile = getStringParameter(IN_SQL_SCRIPT_FILE_NAME, "SelectFromDual.sql");
        this.sqlScriptArgs = getStringParameter(IN_SQL_SCRIPT_ARGS, "");

        // we can now safely call super and expect it to work
        super.init(kieSession);
    }

    public String getSqlScriptFile()
    {
        return sqlScriptFile;
    }

    public String getSqlScriptArgs()
    {
        return sqlScriptArgs;
    }

    @Override
    protected String extractScriptNameFromParameters()
    {
        // the BASH script I run is hard coded
        return OPS_RUN_SQLPLUS_SH;
    }

    @Override
    protected String extractWorkDoneNameFromParameters()
    {
        // default to the name of the script
        return getStringParameter(IN_WORK_DONE_NAME, this.sqlScriptFile);
    }

}
