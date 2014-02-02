/**
 * ===========================================================================
 *    Copyright 2012 BIDS Holdings L.P. All rights reserved.
 * ---------------------------------------------------------------------------
 * Created on 1/25/14
 * By bidsjagu
 *
 */

package com.bids.bpm.work.handlers.jobctl;

import com.bids.bpm.facts.model.JobControlRecord;
import com.bids.bpm.work.handlers.BidsWorkItemHandlerResults;
import com.bids.bpm.work.handlers.bash.BashScriptWorkItemHandler;
import com.bids.bpm.work.handlers.bash.BashShell;
import static com.bids.bpm.work.handlers.jobctl.JobControlWorkItemHandler.JobControlType.startJob;
import org.apache.log4j.Logger;
import org.kie.api.runtime.process.WorkItem;

public class JobControlWorkItemHandler
        extends BashScriptWorkItemHandler
{
    enum JobControlType
    {
        startJob, restartJob, completeJob, failJob,
    }

    public static final String IN_JOB_CONTROL_TYPE = "JobControlType";
    public static final String IN_JOB_CONTROL_ID = "JobControlId";
    public static final String OUT_JOB_CONTROL_RECORD = "JobControlRecord";
    public static final String OPS_JOB_CONTROL_SH = "ops_job_control.sh";
    private static final Logger log = Logger.getLogger(JobControlWorkItemHandler.class);

    @Override
    protected BidsWorkItemWorker makeWorkItemWorker(WorkItem workItem)
    {
        // just call the job control script here

        return new JobControlWorker(workItem);
    }

    protected class JobControlWorker
            extends BashScriptWorker
    {

        private final String jobCtlId;
        private final JobControlType jobCtlType;

        public JobControlWorker(WorkItem workItem)
        {
            super(workItem);
            this.jobCtlId = getStringParameter(IN_JOB_CONTROL_ID, "0000");
            this.jobCtlType = JobControlType.valueOf(getStringParameter(IN_JOB_CONTROL_TYPE, startJob.toString()));
            this.scriptName = OPS_JOB_CONTROL_SH;
            this.scriptArgs = jobCtlType + " " + this.jobCtlId;
        }

        protected BidsWorkItemHandlerResults executeInShell()
                throws InterruptedException
        {
            BidsWorkItemHandlerResults rr = new BidsWorkItemHandlerResults();
            BashShell script = makeBashShell();

            try
            {
                log.info("Running Job Control(WorkId:" + workDoneId + "): " + script.getScriptName() + " " + script.getScriptArgs());
                script.execute();

                JobControlRecord jcr = new JobControlRecord(jobCtlId);
                jcr.transitionTo(script.getExitValue() == 0);

                // assemble the results of the BASH script run
                rr.setReturnCode(script.getExitValue());
                rr.addResult(OUT_STD_OUT, script.getStandardOutput().toString());
                rr.addResult(OUT_STD_ERR, script.getStandardError().toString());
                rr.addResult(OUT_JOB_CONTROL_RECORD, jcr);

                // echo script output to log files (if it failed) and the console
                log.info("Job Control Returns : " + script.getScriptName() + " rc: " + script.getExitValue());
                if (script.getExitValue() != 0)
                {
                    writeFile(".out", script.getStandardOutput());
                    log.info(OUT_STD_OUT + ":\n" + script.getStandardOutput());
                    writeFile(".err", script.getStandardError());
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
    }
}
