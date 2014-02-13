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


import com.bids.bpm.rest.client.cmds.DeleteWorkDoneCmd;
import com.bids.bpm.rest.client.cmds.DumpFactsCmd;
import com.bids.bpm.rest.client.cmds.GetDeploymentsCmd;
import com.bids.bpm.rest.client.cmds.KillProcessCmd;
import com.bids.bpm.rest.client.cmds.StartDayCmd;
import com.bids.bpm.rest.client.cmds.StartProcess;
import com.bids.bpm.rest.client.cmds.StopDayCmd;
import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.SystemConfiguration;
import org.apache.log4j.Logger;

public class BidsServiceClient
{
    private static final Logger log = Logger.getLogger(BidsServiceClient.class);
    private final RestEasyClientFactory clientFactory;

    public static void main(String[] args)
    {
        try
        {
            CompositeConfiguration config = new CompositeConfiguration();
            config.addConfiguration(new SystemConfiguration());
            try
            {
                config.addConfiguration(new PropertiesConfiguration("bids.jbpm.properties"));
            } catch (ConfigurationException ignored)
            {

            }
            String host = config.getString("bids.jbpm.rest.host", "localhost");
            int port = config.getInt("bids.jbpm.rest.port", 8080);
            String user = config.getString("bids.jbpm.rest.user", "andi");
            String password = config.getString("bids.jbpm.rest.password", "password");
            String uriTemplate = "http://" + host + ":" + port + "/bids/rest";
            BidsServiceClient bidsServiceClient = new BidsServiceClient(host,port,user,password);
            bidsServiceClient.runCommand(uriTemplate, args);
        } catch (Exception e)
        {
            log.error(e);
        }
    }

    public BidsServiceClient(String host, int port, String user, String password)
    {
        clientFactory = new RestEasyClientFactory(host, port, user, password);
    }

    public void runCommand(String uriTemplate, String[] args)
            throws Exception
    {
        Map<String, BSCommand<?>> cmds = makeCmds(uriTemplate + "/mgmt");
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
        cmds.put(GetDeploymentsCmd.NAME, new GetDeploymentsCmd(clientFactory,uriTemplate));
        cmds.put(StartDayCmd.NAME, new StartDayCmd(clientFactory,uriTemplate));
        cmds.put(StopDayCmd.NAME, new StopDayCmd(clientFactory,uriTemplate));
        cmds.put(StartProcess.NAME, new StartProcess(clientFactory,uriTemplate));
        cmds.put(DumpFactsCmd.NAME, new DumpFactsCmd(clientFactory,uriTemplate));
        cmds.put(DeleteWorkDoneCmd.NAME, new DeleteWorkDoneCmd(clientFactory,uriTemplate));
        cmds.put(KillProcessCmd.NAME, new KillProcessCmd(clientFactory,uriTemplate));
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
