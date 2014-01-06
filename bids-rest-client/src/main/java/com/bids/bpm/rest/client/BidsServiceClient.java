/**
 * ===========================================================================
 *    Copyright 2012 BIDS Holdings L.P. All rights reserved.
 * ---------------------------------------------------------------------------
 * Created on 1/1/14
 * By bidsjagu
 *
 */

package com.bids.bpm.rest.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


import com.bids.bpm.rest.client.cmds.DeployCmd;
import com.bids.bpm.rest.client.cmds.GetDeploymentsCmd;
import com.bids.bpm.rest.client.cmds.UndeployCmd;
import org.apache.log4j.Logger;

public class BidsServiceClient
{
    private static final Logger log = Logger.getLogger(BidsServiceClient.class);

    public static void main(String[] args)
    {
        try
        {
            String uriTemplate = "http://localhost:8080/bids/rest";
            BidsServiceClient bidsServiceClient = new BidsServiceClient();
            bidsServiceClient.runCommand(uriTemplate, args);
        } catch (Exception e)
        {
            log.error(e);
        }
    }

    public void runCommand(String uriTemplate, String[] args)
            throws Exception
    {
        Map<String, BSCommand<?>> cmds = makeCmds(uriTemplate);
        if (args.length == 0 || !cmds.containsKey(args[0]))
            throw new RuntimeException("Specify command to run");

        BSCommand<?> cmd = cmds.get(args[0]);
        log.trace("Preparing command: " + cmd);
        cmd.prepareRequest(getCmdArgs(args));
        log.trace("Running command: " + cmd);
        cmd.runCmd();
        log.info("Result: \n" + cmd.getResultAsString());

    }

    private Map<String, BSCommand<?>> makeCmds(String uriTemplate)
    {
        HashMap<String, BSCommand<?>> cmds = new HashMap<String, BSCommand<?>>();
        cmds.put(GetDeploymentsCmd.NAME, new GetDeploymentsCmd(uriTemplate));
        cmds.put(DeployCmd.NAME, new DeployCmd(uriTemplate));
        cmds.put(UndeployCmd.NAME, new UndeployCmd(uriTemplate));
        return cmds;
    }

    protected String[] getCmdArgs(String[] allArgs)
    {
        ArrayList<String> cmdArgs = new ArrayList<String>();
        for (int i = 1; i < allArgs.length; i++)
            cmdArgs.add(allArgs[i]);
        return cmdArgs.toArray(new String[cmdArgs.size()]);
    }


}
