/**
 * ===========================================================================
 *    Copyright 2012 BIDS Holdings L.P. All rights reserved.
 * ---------------------------------------------------------------------------
 * Created on 1/1/14
 * By bidsjagu
 *
 */

package com.bids.bpm.rest.client;

import com.bids.bpm.rest.client.cmds.DeployCmd;

public class BidsServiceClient
{
    public static void main(String[] args)
    {
        try
        {
            DeployCmd deployCmd = new DeployCmd("http://localhost:8080/bids/rest/mgmt/deploy");
            deployCmd.formRequest(new String[]{});
            deployCmd.execute();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
