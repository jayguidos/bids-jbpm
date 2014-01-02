/**
 * ===========================================================================
 *    Copyright 2012 BIDS Holdings L.P. All rights reserved.
 * ---------------------------------------------------------------------------
 * Created on 1/1/14
 * By bidsjagu
 *
 */

package com.bids.bpm.rest.client;

import com.bids.bpm.jee.model.BidsDeployment;
import com.bids.bpm.rest.client.cmds.DeployCmd;

public class BidsServiceClient
{
    public static void main(String[] args)
    {
        try
        {
            String uriTemplate = "http://localhost:8080/bids/rest/mgmt";
            DeployCmd deployCmd = new DeployCmd(uriTemplate.concat("/deploy"), new String[] {});
            BidsDeployment bd = deployCmd.runCmd(BidsDeployment.class);
            System.out.println(bd);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
