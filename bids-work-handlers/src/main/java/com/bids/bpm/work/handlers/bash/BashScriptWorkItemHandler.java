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
import java.util.Collection;


import com.bids.bpm.facts.model.WorkDone;
import com.bids.bpm.work.handlers.BidsWorkItemHandler;
import com.bids.bpm.work.handlers.BidsWorkItemHandlerResults;
import static com.bids.bpm.work.handlers.BidsWorkItemHandlerResults.ERROR_RESULTS;
import org.apache.log4j.Logger;
import org.kie.api.runtime.ObjectFilter;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.rule.FactHandle;

public class BashScriptWorkItemHandler
        extends BidsWorkItemHandler
{

    public static final String IN_WORK_ID = "WorkId";
    public static final String IN_SCRIPT_NAME = "ScriptName";
    public static final String IN_SCRIPT_ARGS = "ScriptArgs";
    public static final String IN_ONCE_ONLY = "OnceOnly";
    public static final String IN_WAIT_FOR_SUCCESS = "WaitForSuccess";
    public static final String IN_SIGNAL_ON_ERROR = "SignalOnError";
    public static final String IN_LOG_OUTPUT_TO_CONSOLE = "LogOutputToConsole";
    public static final String OUT_STD_OUT = "StdOut";
    public static final String OUT_STD_ERR = "StdErr";
    private static final Logger log = Logger.getLogger(BashScriptWorkItemHandler.class);
    protected String targetHost;

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
        return new BashScriptWorker(workItem);
    }

    protected class BashScriptWorker
            extends BidsWorkItemWorker
    {
        protected final String workDoneId;
        private final ObjectFilter doneTaskFilter = new ObjectFilter()
        {
            public boolean accept(Object object)
            {
                return object instanceof WorkDone && ((WorkDone) object).getName().equals(workDoneId);
            }
        };
        private final boolean onceOnly;
        private final boolean waitForSuccessfulExitStatus;
        private final boolean signalOnError;
        private final boolean logOutputToConsole;
        private final File scriptLogDir;
        protected String scriptName;
        protected String scriptArgs;

        public BashScriptWorker(WorkItem workItem)
        {
            super(workItem);
            scriptName = getStringParameter(IN_SCRIPT_NAME, "echo");
            scriptArgs = getStringParameter(IN_SCRIPT_ARGS, null);
            workDoneId = getStringParameter(IN_WORK_ID, scriptName.replace(" ", "_"));
            onceOnly = getBooleanParameter(IN_ONCE_ONLY, false);
            waitForSuccessfulExitStatus = getBooleanParameter(IN_WAIT_FOR_SUCCESS, false);
            signalOnError = getBooleanParameter(IN_SIGNAL_ON_ERROR, false);
            logOutputToConsole = getBooleanParameter(IN_LOG_OUTPUT_TO_CONSOLE, true);
            this.scriptLogDir = new File(workItemLogDir, scriptName);
            log.info("BashScript will log to " + scriptLogDir.toString());
        }

        @Override
        public BidsWorkItemHandlerResults doWorkInThread()
                throws InterruptedException
        {

            Collection<FactHandle> workDoneHandles = getKsession().getFactHandles(doneTaskFilter);
            if (!onceOnly && workDoneHandles.size() > 0)
            {
                log.warn("Work Id: " + workDoneId + " already completed - skipping this invocation");
                WorkDone workDone = (WorkDone) getKsession().getObject(workDoneHandles.iterator().next());
                return new BidsWorkItemHandlerResults(0, workDone);
            }

            try
            {
                this.scriptLogDir.mkdirs();
                return executeUntilSuccessful();
            } catch (InterruptedException e) {
                throw e;
            } catch (Exception e)
            {
                log.error(e);
                return ERROR_RESULTS;
            }
        }

        private BidsWorkItemHandlerResults executeUntilSuccessful()
                throws InterruptedException
        {
            BidsWorkItemHandlerResults rr = null;
            while (rr == null)
            {
                rr = executeInShell();
                if (rr.getReturnCode() != 0 && waitForSuccessfulExitStatus)
                {
                    pauseUntilReleased();
                    rr = null;
                    continue;
                }
                else if (rr.getReturnCode() != 0 && signalOnError)
                    getKsession().signalEvent("BashScriptError", workDoneId);
            }
            rr.setWorkDone(new WorkDone(workDoneId));
            return rr;
        }

        private void pauseUntilReleased()
        {
            try
            {
                File waitFile = new File(workItemLogDir, workDoneId + ".paused");
                waitFile.createNewFile();
                int counter = 0;
                while (waitFile.exists())
                {
                    if (counter % 20 == 0)
                        log.warn("Script " + workDoneId + " is paused until you fix it.  Delete " + waitFile.getCanonicalPath() + " to retry execution");
                    counter++;
                    Thread.sleep(500L);
                }
                log.warn("Script " + workDoneId + " has resumed and will restart.");
            } catch (Exception e)
            {
                log.error(e);
            }
        }

        protected BidsWorkItemHandlerResults executeInShell()
                throws InterruptedException
        {
            BidsWorkItemHandlerResults rr = new BidsWorkItemHandlerResults();
            BashShell script = makeBashShell();

            try
            {

                log.info("Executing BASH Script(WorkId:" + workDoneId + "): " + script.getScriptName() + " " + script.getScriptArgs());
                script.execute();

                // assemble the results of the BASH script run
                rr.setReturnCode(script.getExitValue());
                rr.addResult(OUT_STD_OUT, script.getStandardOutput().toString());
                rr.addResult(OUT_STD_ERR, script.getStandardError().toString());

                // echo script output to log files and the console
                log.info("BASH Script Returns : " + script.getScriptName() + " rc: " + script.getExitValue());
                writeFile(".out", script.getStandardOutput());
                writeFile (".err", script.getStandardError());
                if (!logOutputToConsole && rr.getReturnCode() != 0)
                {
                    log.info(OUT_STD_OUT + ":\n" + script.getStandardOutput());
                    log.info(OUT_STD_ERR + ":\n" + script.getStandardError());
                }

            } catch ( InterruptedException e ) {
                throw e;
            } catch (Throwable e)
            {
                log.error(e);
            }
            return rr;
        }

        protected BashShell makeBashShell()
        {
            BashShell script = new BashShell(scriptName, scriptArgs);
            if (targetHost != null && targetHost.trim().length() > 0)
                script.setHost(targetHost);
            script.setLogOutput(logOutputToConsole);
            return script;
        }

        protected void writeFile(String suffix, StringBuilder contents)
        {
            try
            {
                PrintWriter w = new PrintWriter(new FileWriter(new File(scriptLogDir, workDoneId + suffix)));
                w.println(contents);
                w.close();
            } catch (IOException e)
            {
                log.error(e);
            }
        }
    }

}
