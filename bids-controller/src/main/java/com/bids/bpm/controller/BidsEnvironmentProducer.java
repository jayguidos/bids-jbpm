/**
 * ===========================================================================
 *    Copyright 2012 BIDS Holdings L.P. All rights reserved.
 * ---------------------------------------------------------------------------
 * Created on 12/19/13
 * By bidsjagu
 *
 */

package com.bids.bpm.controller;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;


import com.bids.bpm.work.handlers.kie.BidsWorkItemHandlerProducer;
import org.jbpm.kie.services.api.IdentityProvider;
import org.jbpm.runtime.manager.api.WorkItemHandlerProducer;
import org.jbpm.runtime.manager.impl.RuntimeEnvironmentBuilder;
import org.jbpm.runtime.manager.impl.cdi.InjectableRegisterableItemsFactory;
import org.jbpm.shared.services.cdi.Selectable;
import org.kie.api.runtime.manager.RuntimeEnvironment;
import org.kie.api.task.UserGroupCallback;
import org.kie.internal.runtime.manager.cdi.qualifier.PerProcessInstance;
import org.kie.internal.runtime.manager.cdi.qualifier.PerRequest;

@ApplicationScoped
public class BidsEnvironmentProducer
{

    @PersistenceUnit(unitName = "org.jbpm.domain")
    private EntityManagerFactory emf;

    @Inject
    private BeanManager beanManager;

    @Inject
    @Selectable
    private UserGroupCallback userGroupCallback;

//    @Produces
    public EntityManagerFactory getEntityManagerFactory()
    {
        return this.emf;
    }

    @Produces
    @RequestScoped
    public EntityManager getEntityManager()
    {
        EntityManager em = emf.createEntityManager();
        return em;
    }

    public void close(@Disposes EntityManager em)
    {
        em.close();
    }

    @Produces
    public UserGroupCallback produceSelectedUserGroupCalback()
    {
        return userGroupCallback;
    }

    @Produces
    public IdentityProvider produceIdentityProvider()
    {
        return new IdentityProvider()
        {
            public String getName()
            {
                return "Jay";
            }

            public List<String> getRoles()
            {
                return new ArrayList<String>();
            }
        };
    }

    @Produces
    public WorkItemHandlerProducer produceWorkItemHandlers()
    {
        return new BidsWorkItemHandlerProducer();
    }

    @Produces
    @Singleton
    @PerRequest
    @PerProcessInstance
    public RuntimeEnvironment produceEnvironment(EntityManagerFactory emf) {
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get()
                .newDefaultBuilder()
                .entityManagerFactory(emf)
                .userGroupCallback(userGroupCallback)
                .registerableItemsFactory(InjectableRegisterableItemsFactory.getFactory(beanManager, null))
                .get();

        return environment;
    }

}
