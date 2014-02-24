/*
 * ===========================================================================
 *    Copyright 2012 BIDS Holdings L.P. All rights reserved.
 * ---------------------------------------------------------------------------
 * Created on 2012-01-05
 * By sik.ngai
 * ===========================================================================
 */

package com.bids.bpm.work.handlers.implementations.bash.worker;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
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
    private AsyncStreamHandler stdOutStreamHandler;
    private AsyncStreamHandler stdErrStreamHandler;
    private AtomicInteger exitValue = new AtomicInteger(-1);
    private boolean logOutput = false;
    private File inputFile;
    private InputStream inputStream;

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
            final ProcessBuilder pb = new ProcessBuilder(commandArgs);
            final Process process = pb.start();
            AsyncInputStreamSpooler inputSpooler = null;

            if (inputFile != null)
                pb.redirectInput(inputFile);
            else if (inputStream != null)
            {
                // tie the stream directly to the stdin of the process
                inputSpooler = new AsyncInputStreamSpooler(inputStream,process.getOutputStream());
                inputSpooler.start();
            }

            InputStream stdOutStream = process.getInputStream();
            InputStream stdErrStream = process.getErrorStream();

            // these need to run as parallel java threads to drain in a timely fashion
            // the standard output and error from the command.
            stdOutStreamHandler = new AsyncStreamHandler(stdOutStream);
            stdErrStreamHandler = new AsyncStreamHandler(stdErrStream);

            stdOutStreamHandler.start();
            stdErrStreamHandler.start();

            exitValue.set(process.waitFor());

            stdOutStreamHandler.join();
            stdErrStreamHandler.join();
            if (inputSpooler != null)
            {
                inputSpooler.join(2000L);
                if (inputSpooler.isAlive())
                    inputSpooler.interrupt();
            }

            if (logOutput || exitValue.get() != 0)
                logOutput();
        } catch (InterruptedException e)
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

    public File getInputFile()
    {
        return inputFile;
    }

    public void setInputFile(File inputFile)
    {
        this.inputFile = inputFile;
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
        return stdOutStreamHandler.getOutputBuffer();
    }

    public StringBuilder getStandardError()
    {
        return stdErrStreamHandler.getOutputBuffer();
    }

    public String getScriptName()
    {
        return scriptName;
    }

    public void setScriptName(String scriptName)
    {
        this.scriptName = scriptName;
    }

    public InputStream getInputStream()
    {
        return inputStream;
    }

    public void setInputStream(InputStream inputStream)
    {
        this.inputStream = inputStream;
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

    private static class AsyncInputStreamSpooler
            extends Thread
    {
        private InputStream inputStream;
        private OutputStream outputStream;

        private AsyncInputStreamSpooler(InputStream inputStream, OutputStream outputStream)
        {
            this.inputStream = inputStream;
            this.outputStream = outputStream;
        }

        public void run()
        {
            try
            {
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(outputStream));
                BufferedReader inputFile = new BufferedReader(new InputStreamReader(inputStream));
                String currInputLine = null;
                while ((currInputLine = inputFile.readLine()) != null)
                {
                    bw.write(currInputLine);
                    bw.newLine();
                }
                bw.close();
            } catch (IOException e)
            {
                logger.error("Exception working with input stream", e);
            }
        }

    }

    private static class AsyncStreamHandler
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
