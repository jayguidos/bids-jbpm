/**
 * ===========================================================================
 *    Copyright 2012 BIDS Holdings L.P. All rights reserved.
 * ---------------------------------------------------------------------------
 * Created on 1/1/14
 * By bidsjagu
 *
 */

package com.bids.bpm.rest.client.cmds;

import java.io.StringWriter;

import javax.xml.bind.JAXBContext;


import com.bids.bpm.jee.model.BidsDeployment;
import com.bids.bpm.jee.rest.dto.DeployRequest;
import com.bids.bpm.rest.client.BSCommand;
import static javax.ws.rs.core.MediaType.APPLICATION_XML;
import org.jboss.resteasy.client.ClientResponse;

public class DeployCmd
        extends BSCommand
{

    private DeployRequest dr;

    public DeployCmd(String uriTemplate)
    {
        super(uriTemplate);
    }

    public void formRequest(String[] args)
    {
        dr = new DeployRequest();
        dr.setArtifactId("EndOfDay");
        dr.setVersion("1.0.0-SNAPSHOT");
        dr.setBidsDate("2013-12-22");
    }

    @Override
    public void execute()
            throws Exception
    {
        request.body(APPLICATION_XML, marshallIntoXML(DeployRequest.class, dr));
        ClientResponse<BidsDeployment> response = request.post(BidsDeployment.class);
        BidsDeployment bd = response.getEntity();
        System.out.println(bd);
    }

    protected <V> String marshallIntoXML(Class<V> jaxbClass, V instance)
            throws Exception
    {
        StringWriter sw = new StringWriter();
        JAXBContext jbc = JAXBContext.newInstance(jaxbClass);
        jbc.createMarshaller().marshal(instance, sw);
        return sw.toString();
    }

}
