package com.bids.bpm.jee.test;

import java.util.logging.Logger;

import javax.inject.Inject;


import com.bids.bpm.facts.model.BidsDay;
import com.bids.bpm.jee.data.BidsDayProducer;
import com.bids.bpm.jee.util.Resources;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class BidsDayRegistrationTest
{
    @Inject
    BidsDayProducer memberRegistration;
    @Inject
    Logger log;

    @Deployment
    public static Archive<?> createTestArchive()
    {
        return ShrinkWrap.create(WebArchive.class, "test.war")
                .addClasses(BidsDay.class, BidsDayProducer.class, Resources.class)
                .addAsResource("META-INF/persistence.xml", "META-INF/persistence.xml")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    @Test
    public void testRegister()
            throws Exception
    {
//      BidsDay newMember = memberRegistration.getBidsDays();
//      newMember.setName("Jane Doe");
//      newMember.setEmail("jane@mailinator.com");
//      newMember.setPhoneNumber("2125551234");
//      memberRegistration.register();
//      assertNotNull(newMember.getId());
//      log.info(newMember.getName() + " was persisted with id " + newMember.getId());
    }

}
