/**
 * ===========================================================================
 *    Copyright 2012 BIDS Holdings L.P. All rights reserved.
 * ---------------------------------------------------------------------------
 * Created on 12/27/13
 * By bidsjagu
 *
 */

package com.bids.bpm.jee.cdi;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;


import com.bids.bpm.jee.kie.BidsDeploymentService;
import org.jbpm.kie.services.api.DeploymentService;
import org.jbpm.kie.services.api.IdentityProvider;
import org.jbpm.kie.services.api.Kjar;
import org.jbpm.runtime.manager.impl.cdi.InjectableRegisterableItemsFactory;
import org.jbpm.shared.services.cdi.Selectable;
import org.kie.api.runtime.manager.RuntimeEnvironment;
import org.kie.api.runtime.manager.RuntimeEnvironmentBuilder;
import org.kie.internal.runtime.manager.cdi.qualifier.PerProcessInstance;
import org.kie.internal.runtime.manager.cdi.qualifier.PerRequest;
import org.kie.internal.runtime.manager.cdi.qualifier.Singleton;
import org.kie.internal.task.api.UserGroupCallback;

public class BidsEnvironmentProducer
{
    @PersistenceUnit(unitName = "org.jbpm.domain")
    private EntityManagerFactory emf;

    @Inject
    @Selectable
    private UserGroupCallback userGroupCallback;

    @Inject
    private BeanManager beanManager;

    @Inject
    @Kjar
    public BidsDeploymentService deploymentService;

    @Produces
    @Default
    public DeploymentService produceDefaultDeploymentService()
    {
        return deploymentService;
    }

    @Produces
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
                return "bidsApp";
            }

            public List<String> getRoles()
            {
                return new ArrayList<String>();
            }
        };
    }

    @Produces
    @Singleton
    @PerRequest
    @PerProcessInstance
    public RuntimeEnvironment produceEnvironment(EntityManagerFactory emf)
    {

        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get()
                .newDefaultBuilder()
                .entityManagerFactory(emf)
                .userGroupCallback(userGroupCallback)
                .registerableItemsFactory(InjectableRegisterableItemsFactory.getFactory(beanManager, null))
                .get();
        return environment;

    }

}
