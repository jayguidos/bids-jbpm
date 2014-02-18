/**
 * ===========================================================================
 *    Copyright 2012 BIDS Holdings L.P. All rights reserved.
 * ---------------------------------------------------------------------------
 * Created on 2/3/14
 * By bidsjagu
 *
 */

package com.bids.bpm.work.handlers.implementations.bash.worker;

import java.io.File;


import com.bids.bpm.work.handlers.support.worker.BidsWorkItemWorkerConfig;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.WorkItem;

public class BashScriptWorkerConfig
        extends BidsWorkItemWorkerConfig
{
    public static final String IN_SCRIPT_NAME = "ScriptName";
    public static final String IN_SCRIPT_ARGS = "ScriptArgs";
    public static final String IN_WAIT_FOR_SUCCESS = "WaitForSuccess";
    public static final String IN_LOG_OUTPUT_TO_CONSOLE = "LogOutputToConsole";
    private final String targetHost;

    protected boolean waitForSuccessfulExitStatus;
    protected boolean logOutputToConsole;
    protected String scriptName;
    protected String scriptArgs;

    public BashScriptWorkerConfig(String targetHost, WorkItem workItem, File scriptLogDir)
    {
        super(workItem, scriptLogDir);
        this.targetHost = targetHost;
    }

    public void init(KieSession kieSession)
    {
        // these are mine
        this.scriptName = extractScriptNameFromParameters();
        this.scriptArgs = extractScriptArgsFromParameters();
        this.waitForSuccessfulExitStatus = getBooleanParameter(IN_WAIT_FOR_SUCCESS, false);
        this.logOutputToConsole = getBooleanParameter(IN_LOG_OUTPUT_TO_CONSOLE, true);

        // we can now safely call super and expect it to work
        super.init(kieSession);
    }

    public boolean isLogOutputToConsole()
    {
        return logOutputToConsole;
    }

    public void setLogOutputToConsole(boolean logOutputToConsole)
    {
        this.logOutputToConsole = logOutputToConsole;
    }

    public boolean isWaitForSuccessfulExitStatus()
    {
        return waitForSuccessfulExitStatus;
    }

    public void setWaitForSuccessfulExitStatus(boolean waitForSuccessfulExitStatus)
    {
        this.waitForSuccessfulExitStatus = waitForSuccessfulExitStatus;
    }

    public String getTargetHost()
    {
        return targetHost;
    }

    public String getScriptName()
    {
        return scriptName;
    }

    public void setScriptName(String scriptName)
    {
        this.scriptName = scriptName;
    }

    public String getScriptArgs()
    {
        return scriptArgs;
    }

    public void setScriptArgs(String scriptArgs)
    {
        this.scriptArgs = scriptArgs;
    }

    @Override
    protected String extractWorkDoneNameFromParameters()
    {
        return getStringParameter(IN_WORK_DONE_NAME, scriptName.replace(" ", "_"));
    }

    protected String extractScriptArgsFromParameters()
    {
        return getStringParameter(IN_SCRIPT_ARGS, null);
    }

    protected String extractScriptNameFromParameters()
    {
        return getStringParameter(IN_SCRIPT_NAME, "echo");
    }

}
