/*
 * ===========================================================================
 *    Copyright 2012 BIDS Holdings L.P. All rights reserved.
 * ---------------------------------------------------------------------------
 * Created on 2012-01-05
 * By sik.ngai
 * ===========================================================================
 */

package com.bids.bpm.work.handlers.bash;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;


import org.apache.log4j.Logger;

public class BashShell
{
    private String[] commandArgs;
    private String scriptName;
    private AsyncStreamHandler inputStreamHandler;
    private AsyncStreamHandler errorStreamHandler;
    private AtomicInteger exitValue = new AtomicInteger(-1);
    private boolean logOutput = false;

    private static final Logger logger = Logger.getLogger(BashShell.class);

    public BashShell()
    {
    }

    public BashShell(String scriptName)
    {
        this.scriptName = scriptName;
    }

    public void setScriptName(String scriptName)
    {
        this.scriptName = scriptName;
    }

    private String inheritEnvironmentVariable(String environmentVar)
    {
        return environmentVar + "=" + System.getenv(environmentVar);
    }

    public int execute()
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
        allArgs.add("ssh bidsapp@localhost \"" + scriptName + "\"");

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
        } catch (Exception e)
        {
            // do nothing
            exitValue.set(2);
        }

        return exitValue.get();
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

    public boolean isLogOutput()
    {
        return logOutput;
    }

    public void setLogOutput(boolean logOutput)
    {
        this.logOutput = logOutput;
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

    public String getScriptName()
    {
        return scriptName;
    }

}
