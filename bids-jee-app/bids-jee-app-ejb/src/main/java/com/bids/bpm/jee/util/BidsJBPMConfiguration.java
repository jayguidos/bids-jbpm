/**
 * ===========================================================================
 *    Copyright 2012 BIDS Holdings L.P. All rights reserved.
 * ---------------------------------------------------------------------------
 * Created on 1/12/14
 * By bidsjagu
 *
 */

package com.bids.bpm.jee.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Properties;
import java.util.logging.Logger;


import static com.bids.bpm.shared.BidsBPMConstants.BIDS_BPM_BASH_HOST_PROP_NAME;
import static com.bids.bpm.shared.BidsBPMConstants.BIDS_BPM_LOG_DIR_PROP_NAME;

public class BidsJBPMConfiguration
    implements Serializable
{
    public static final String BIDS_JBPM_PROPERTIES = "bids-jbpm.properties";
    private File globalLogDir;
    private String bashHostName;

    public BidsJBPMConfiguration()
    {
        Properties p = new Properties();
        try
        {
            InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream(BIDS_JBPM_PROPERTIES);
            if ( resourceAsStream != null )
            {
                p.load(resourceAsStream);
                resourceAsStream.close();
            }
            this.globalLogDir = new File(loadProperty(BIDS_BPM_LOG_DIR_PROP_NAME, p, new File(System.getProperty("user.home"), "data/jbpm").toString()));
            this.globalLogDir.mkdirs();
            this.bashHostName = loadProperty(BIDS_BPM_BASH_HOST_PROP_NAME, p, "localhost");
        } catch (IOException e)
        {
            Logger.getLogger(this.getClass().getName()).severe("Cannot load properties: " + BIDS_JBPM_PROPERTIES);
        }
    }

    public File getGlobalLogDir()
    {
        return globalLogDir;
    }

    public void setGlobalLogDir(File globalLogDir)
    {
        this.globalLogDir = globalLogDir;
    }

    public String getBashHostName()
    {
        return bashHostName;
    }

    public void setBashHostName(String bashHostName)
    {
        this.bashHostName = bashHostName;
    }

    private String loadProperty(String name, Properties p, String defaultValue)
    {
        if (System.getProperty(name) != null)
            return System.getProperty(name);
        else
            return p.getProperty(name, defaultValue);
    }
}
