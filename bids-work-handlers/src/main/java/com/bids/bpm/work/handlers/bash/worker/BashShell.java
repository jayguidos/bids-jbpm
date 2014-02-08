/*
 * ===========================================================================
 *    Copyright 2012 BIDS Holdings L.P. All rights reserved.
 * ---------------------------------------------------------------------------
 * Created on 2012-01-05
 * By sik.ngai
 * ===========================================================================
 */

package com.bids.bpm.work.handlers.bash.worker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;


import org.apache.log4j.Logger;

public class BashShell
{
    private static final Logger logger = Logger.getLogger(BashShell.class);
    private String host = "localhost";
    private String[] commandArgs;
    private String scriptName;
    private String scriptArgs;
    private AsyncStreamHandler inputStreamHandler;
    private AsyncStreamHandler errorStreamHandler;
    private AtomicInteger exitValue = new AtomicInteger(-1);
    private boolean logOutput = false;

    public BashShell()
    {
    }

    public BashShell(String scriptName)
    {
        this(scriptName, "");
    }

    public BashShell(String scriptName, String scriptArgs)
    {
        this.scriptName = scriptName;
        this.scriptArgs = scriptArgs;
    }

    public int execute()
            throws InterruptedException
    {
        if (scriptName == null || scriptName.length() == 0)
            throw new IllegalStateException("No script name set");

        ArrayList<String> allArgs = new ArrayList<String>();
        allArgs.add("env");
        allArgs.add("-i");
        allArgs.add(inheritEnvironmentVariable("HOME"));
        allArgs.add(inheritEnvironmentVariable("TERM"));
        allArgs.add("/bin/bash");
        allArgs.add("--login");
        allArgs.add("-c");
        if (scriptArgs == null || scriptArgs.trim().length() == 0)
            allArgs.add("ssh bidsapp@" + host + " \"" + scriptName + "\"");
        else
            allArgs.add("ssh bidsapp@" + host + " \"" + scriptName + " " + scriptArgs + "\"");

        this.commandArgs = allArgs.toArray(new String[allArgs.size()]);
        exitValue.set(-1);

        try
        {
            ProcessBuilder pb = new ProcessBuilder(commandArgs);
            Process process = pb.start();

            InputStream inputStream = process.getInputStream();
            InputStream errorStream = process.getErrorStream();

            // these need to run as parallel java threads to drain in a timely fashion
            // the standard output and error from the command.
            inputStreamHandler = new AsyncStreamHandler(inputStream);
            errorStreamHandler = new AsyncStreamHandler(errorStream);

            inputStreamHandler.start();
            errorStreamHandler.start();

            exitValue.set(process.waitFor());

            inputStreamHandler.join();
            errorStreamHandler.join();

            if (logOutput || exitValue.get() != 0)
                logOutput();
        } catch ( InterruptedException e )
        {
            throw e;
        } catch (Exception e)
        {
            // do nothing
            exitValue.set(2);
        }

        return exitValue.get();
    }

    public boolean isLogOutput()
    {
        return logOutput;
    }

    public void setLogOutput(boolean logOutput)
    {
        this.logOutput = logOutput;
    }

    public String getScriptArgs()
    {
        return scriptArgs;
    }

    public int getExitValue()
    {
        return exitValue.get();
    }

    public StringBuilder getStandardOutput()
    {
        return inputStreamHandler.getOutputBuffer();
    }

    public StringBuilder getStandardError()
    {
        return errorStreamHandler.getOutputBuffer();
    }

    public String getScriptName()
    {
        return scriptName;
    }

    public void setScriptName(String scriptName)
    {
        this.scriptName = scriptName;
    }

    public String getHost()
    {
        return host;
    }

    public void setHost(String host)
    {
        this.host = host;
    }

    private String inheritEnvironmentVariable(String environmentVar)
    {
        return environmentVar + "=" + System.getenv(environmentVar);
    }

    private void logOutput()
    {
        StringBuilder sb = new StringBuilder();
        for (String arg : commandArgs)
            sb.append(arg).append(" ");
        sb.append("\nExit code: ").append(exitValue);
        sb.append("\nSTDOUT:\n").append(getStandardOutput());
        sb.append("\nSTDERR:\n").append(getStandardError());
        if (exitValue.get() == 0)
            logger.info(sb.toString());
        else
            logger.error(sb.toString());
    }

    private class AsyncStreamHandler
            extends Thread
    {
        InputStream inputStream;
        StringBuilder outputBuffer = new StringBuilder();

        AsyncStreamHandler(InputStream inputStream)
        {
            this.inputStream = inputStream;
        }

        public void run()
        {
            BufferedReader bufferedReader = null;
            try
            {
                bufferedReader = new BufferedReader(new InputStreamReader(
                        inputStream));
                String line;
                while ((line = bufferedReader.readLine()) != null)
                {
                    outputBuffer.append(line).append("\n");
                }
            } catch (Throwable t)
            {
                logger.error(t);
            } finally
            {
                try
                {
                    bufferedReader.close();
                } catch (IOException ignored)
                {
                }
            }
        }

        public StringBuilder getOutputBuffer()
        {
            return outputBuffer;
        }

    }
}
