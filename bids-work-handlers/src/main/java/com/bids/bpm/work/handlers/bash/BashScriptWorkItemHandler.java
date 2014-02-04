/**
 * ===========================================================================
 *    Copyright 2012 BIDS Holdings L.P. All rights reserved.
 * ---------------------------------------------------------------------------
 * Created on 10/5/13
 * By bidsjagu
 *
 */

package com.bids.bpm.work.handlers.bash;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;


import com.bids.bpm.facts.model.WorkDone;
import com.bids.bpm.work.handlers.BidsWorkItemHandler;
import com.bids.bpm.work.handlers.BidsWorkItemHandlerResults;
import org.apache.log4j.Logger;
import org.kie.api.runtime.process.WorkItem;

public class BashScriptWorkItemHandler
        extends BidsWorkItemHandler
{

    public static final String OUT_STD_OUT = "StdOut";
    public static final String OUT_STD_ERR = "StdErr";
    public static final String BASH_SCRIPT_ERROR_SIGNAL = "BashScriptError";
    private static final Logger log = Logger.getLogger(BashScriptWorkItemHandler.class);
    protected String targetHost;

    public BashScriptWorkItemHandler(File baseLogDir)
    {
        this(BASH_SCRIPT_ERROR_SIGNAL,baseLogDir);
    }

    public BashScriptWorkItemHandler(String errorSignalName, File baseLogDir)
    {
        super(errorSignalName, baseLogDir);
    }

    public String getTargetHost()
    {
        return targetHost;
    }

    public void setTargetHost(String targetHost)
    {
        this.targetHost = targetHost;
    }

    @Override
    protected BidsWorkItemWorker makeWorkItemWorker(WorkItem workItem)
    {
        BashScriptWorkerConfig config = new BashScriptWorkerConfig(workItem,getLogBaseDir());
        config.init(getKsession());
        return new BashScriptWorker(config);
    }

    protected class BashScriptWorker
            extends BidsWorkItemWorker
    {
        private final BashScriptWorkerConfig config;

        public BashScriptWorker(BashScriptWorkerConfig config)
        {
            super(config);
            this.config = config;
        }

        @Override
        public BidsWorkItemHandlerResults doWorkInThread()
                throws InterruptedException
        {
            BidsWorkItemHandlerResults rr = null;
            while (rr == null)
            {
                rr = executeInShell();
                if (rr.getReturnCode() != 0 && config.isWaitForSuccessfulExitStatus())
                {
                    pauseUntilReleased();
                    rr = null;
                    continue;
                }
            }
            rr.setWorkDone(new WorkDone(config.getWorkDoneId()));
            return rr;
        }

        private void pauseUntilReleased()
        {
            try
            {
                File waitFile = new File(config.getWorkItemLogDir(), config.getWorkDoneId() + ".paused");
                waitFile.createNewFile();
                int counter = 0;
                while (waitFile.exists())
                {
                    if (counter % 20 == 0)
                        log.warn("Script " + config.getWorkDoneId() + " is paused until you fix it.  Delete " + waitFile.getCanonicalPath() + " to retry execution");
                    counter++;
                    Thread.sleep(500L);
                }
                log.warn("Script " + config.getWorkDoneId() + " has resumed and will restart.");
            } catch (Exception e)
            {
                log.error(e);
            }
        }

        protected BidsWorkItemHandlerResults executeInShell()
                throws InterruptedException
        {
            log.info("BashScript will log to " + config.getWorkItemLogDir().toString());

            BidsWorkItemHandlerResults rr = new BidsWorkItemHandlerResults();
            BashShell script = makeBashShell();

            log.info("Executing BASH Script(WorkId:" + config.getWorkDoneId() + "): " + script.getScriptName() + " " + script.getScriptArgs());
            script.execute();

            // assemble the results of the BASH script run
            rr.setReturnCode(script.getExitValue());
            rr.addResult(OUT_STD_OUT, script.getStandardOutput().toString());
            rr.addResult(OUT_STD_ERR, script.getStandardError().toString());

            // echo script output to log files
            log.info("BASH Script Returns : " + script.getScriptName() + " rc: " + script.getExitValue());
            writeFile(".out", script.getStandardOutput());
            writeFile(".err", script.getStandardError());

            return rr;
        }

        protected BashShell makeBashShell()
        {
            BashShell script = new BashShell(config.getScriptName(), config.getScriptArgs());
            if (targetHost != null && targetHost.trim().length() > 0)
                script.setHost(targetHost);
            script.setLogOutput(config.isLogOutputToConsole());
            return script;
        }

        protected void writeFile(String suffix, StringBuilder contents)
        {
            try
            {
                FileWriter out = new FileWriter(new File(config.getWorkItemLogDir(), config.getWorkDoneId() + suffix));
                PrintWriter w = new PrintWriter(out);
                w.println(contents);
                w.close();
            } catch (IOException e)
            {
                log.error(e);
            }
        }
    }

}
