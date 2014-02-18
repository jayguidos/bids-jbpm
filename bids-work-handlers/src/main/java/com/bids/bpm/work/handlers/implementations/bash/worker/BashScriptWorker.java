/**
 * ===========================================================================
 *    Copyright 2012 BIDS Holdings L.P. All rights reserved.
 * ---------------------------------------------------------------------------
 * Created on 2/8/14
 * By bidsjagu
 *
 */

package com.bids.bpm.work.handlers.implementations.bash.worker;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;


import com.bids.bpm.facts.model.WorkDone;
import com.bids.bpm.work.handlers.support.BidsWorkItemHandlerResults;
import com.bids.bpm.work.handlers.implementations.bash.BashScriptWorkItemHandler;
import com.bids.bpm.work.handlers.support.worker.AbstractBidsWorkItemWorker;
import org.apache.log4j.Logger;

public class BashScriptWorker
        extends AbstractBidsWorkItemWorker
{
    private final BashScriptWorkerConfig config;
    private static final Logger log = Logger.getLogger(BashScriptWorker.class);

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
        rr.setWorkDone(new WorkDone(config.getWorkDoneName()));
        return rr;
    }

    private void pauseUntilReleased()
    {
        try
        {
            File waitFile = new File(config.getWorkItemLogDir(), config.getWorkDoneName() + ".paused");
            waitFile.createNewFile();
            int counter = 0;
            while (waitFile.exists())
            {
                if (counter % 20 == 0)
                    log.warn("Script " + config.getWorkDoneName() + " is paused until you fix it.  Delete " + waitFile.getCanonicalPath() + " to retry execution");
                counter++;
                Thread.sleep(500L);
            }
            log.warn("Script " + config.getWorkDoneName() + " has resumed and will restart.");
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

        log.info("Executing BASH Script(WorkId:" + config.getWorkDoneName() + "): " + script.getScriptName() + " " + script.getScriptArgs());
        script.execute();

        // assemble the results of the BASH script run
        rr.setReturnCode(script.getExitValue());
        rr.addResult(BashScriptWorkItemHandler.OUT_STD_OUT, script.getStandardOutput().toString());
        rr.addResult(BashScriptWorkItemHandler.OUT_STD_ERR, script.getStandardError().toString());

        // echo script output to log files
        log.info("BASH Script Returns : " + script.getScriptName() + " rc: " + script.getExitValue());
        writeFile(".out", script.getStandardOutput());
        writeFile(".err", script.getStandardError());

        return rr;
    }

    protected BashShell makeBashShell()
    {
        BashShell script = new BashShell(config.getScriptName(), config.getScriptArgs());
        if (config.getTargetHost() != null && config.getTargetHost().trim().length() > 0)
            script.setHost(config.getTargetHost());
        script.setLogOutput(config.isLogOutputToConsole());
        return script;
    }

    protected void writeFile(String suffix, StringBuilder contents)
    {
        try
        {
            FileWriter out = new FileWriter(new File(config.getWorkItemLogDir(), config.getWorkDoneName() + suffix));
            PrintWriter w = new PrintWriter(out);
            w.println(contents);
            w.close();
        } catch (IOException e)
        {
            log.error(e);
        }
    }
}
