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
    public static final String IN_ONCE_ONLY = "OnceOnly";
    public static final String IN_WAIT_FOR_SUCCESS = "WaitForSuccess";
    public static final String OUT_STD_OUT = "StdOut";
    public static final String OUT_STD_ERR = "StdErr";
    private static final Logger log = Logger.getLogger(BashScriptWorkItemHandler.class);

    @Override
    protected BidsWorkItemWorker makeWorkItemWorker(WorkItem workItem)
    {
        return new BashScriptWorker(workItem);
    }

    private class BashScriptWorker
            extends BidsWorkItemWorker
    {
        private final ObjectFilter doneTaskFilter = new ObjectFilter()
        {
            public boolean accept(Object object)
            {
                return object instanceof WorkDone && ((WorkDone) object).getName().equals(workDoneId);
            }
        };
        private final String workDoneId;
        private final String scriptName;
        private final boolean onceOnly;
        private final boolean waitForSuccessfulExitStatus;
        private final File scriptLogDir;

        public BashScriptWorker(WorkItem workItem)
        {
            super(workItem);
            scriptName = getStringParameter(IN_SCRIPT_NAME, "echo");
            workDoneId = getStringParameter(IN_WORK_ID, scriptName.replace(" ", "_"));
            onceOnly = getBooleanParameter(IN_ONCE_ONLY, false);
            waitForSuccessfulExitStatus = getBooleanParameter(IN_WAIT_FOR_SUCCESS, false);
            this.scriptLogDir = new File(workItemLogDir, scriptName);
            log.info("BashScript will log to " + scriptLogDir.toString());
        }

        @Override
        public BidsWorkItemHandlerResults doWorkInThread()
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
            } catch (Exception e)
            {
                log.error(e);
                return ERROR_RESULTS;
            }
        }

        private BidsWorkItemHandlerResults executeUntilSuccessful()
        {
            BidsWorkItemHandlerResults rr = null;
            while (rr == null || rr.getReturnCode() != 0)
            {
                rr = executeInShell();
                if (rr.getReturnCode() != 0 && waitForSuccessfulExitStatus)
                    pauseUntilReleased();
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

        private BidsWorkItemHandlerResults executeInShell()
        {
            BidsWorkItemHandlerResults rr = new BidsWorkItemHandlerResults();
            BashShell script = new BashShell(scriptName);

            try
            {

                log.info("Executing BASH Script(WorkId:" + workDoneId + "): " + script.getScriptName());
                script.execute();

                // assemble the results of the BASH script run
                rr.setReturnCode(script.getExitValue());
                rr.addResult(OUT_STD_OUT, script.getStandardOutput().toString());
                rr.addResult(OUT_STD_ERR, script.getStandardError().toString());

                // echo script output to log files and the console
                log.info("BASH Script Returns : " + script.getScriptName() + " rc: " + script.getExitValue());
                writeFile(".out", script.getStandardOutput());
                log.info(OUT_STD_OUT + ":\n" + script.getStandardOutput());
                writeFile(".err", script.getStandardError());
                log.info(OUT_STD_ERR + ":\n" + script.getStandardError());

            } catch (Throwable e)
            {
                log.error(e);
            }
            return rr;
        }

        private void writeFile(String suffix, StringBuilder contents)
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
