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
import java.io.InputStream;


import com.bids.bpm.work.handlers.implementations.bash.worker.BashScriptWorker;
import com.bids.bpm.work.handlers.implementations.bash.worker.BashShell;

public class SQLRunnerWorker
        extends BashScriptWorker
{
    public static final File SQL_SCRIPT_DIR = new File("sqlscripts");
    private final SQLRunnerWorkerConfig config;

    public SQLRunnerWorker(SQLRunnerWorkerConfig config)
    {
        super(config);
        this.config = config;
    }

    @Override
    protected BashShell makeBashShell()
    {
        // make the shell as normal
        BashShell bashShell = super.makeBashShell();

        // attach the script file to the stdin of the shell
        File scriptRelDir = new File(SQL_SCRIPT_DIR, config.getSqlScriptFile());
        final InputStream inputStream = getClass().getClassLoader().getResourceAsStream(scriptRelDir.toString());
        if (inputStream == null)
            throw new RuntimeException("No script file found with name " + config.getSqlScriptFile());
        bashShell.setInputStream(inputStream);

        return bashShell;
    }

}
