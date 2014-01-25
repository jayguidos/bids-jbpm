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
import java.io.Serializable;
import java.util.logging.Logger;


import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;


import static com.bids.bpm.shared.BidsBPMConstants.BIDS_BPM_BASH_HOST_PROP_NAME;
import static com.bids.bpm.shared.BidsBPMConstants.BIDS_BPM_LOG_DIR_PROP_NAME;
import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.SystemConfiguration;

@ApplicationScoped
public class BidsJBPMConfiguration
        implements Serializable
{
    public static final String BIDS_JBPM_PROPERTIES = "bids-jbpm.properties";
    public static final String USER_HOME = "user.home";
    public static final String DEFAULT_DATA_DIR = new File(System.getProperty(USER_HOME), "data/jbpm").toString();
    public static final String DEFAULT_BASH_HOST = "localhost";
    private File globalLogDir;
    private String bashHostName;

    @Inject
    private Logger log;

    @PostConstruct
    public void doPostConstruct()
    {
        CompositeConfiguration config = new CompositeConfiguration();
        config.addConfiguration(new SystemConfiguration());
        try
        {
            config.addConfiguration(new PropertiesConfiguration(BIDS_JBPM_PROPERTIES));
        } catch (ConfigurationException ignored)
        {

        }

        this.globalLogDir = new File(config.getString(BIDS_BPM_LOG_DIR_PROP_NAME, DEFAULT_DATA_DIR));
        log.info("Global Logging Directory Home: " + this.globalLogDir);
        this.globalLogDir.mkdirs();
        this.bashHostName = config.getString(BIDS_BPM_BASH_HOST_PROP_NAME, DEFAULT_BASH_HOST);
        log.info("BASH Host Server Name: " + this.bashHostName );
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

}
