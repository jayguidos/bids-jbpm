/**
 * ===========================================================================
 *    Copyright 2012 BIDS Holdings L.P. All rights reserved.
 * ---------------------------------------------------------------------------
 * Created on 2/12/14
 * By bidsjagu
 *
 */

package com.bids.bpm.work.handlers.fact;

import java.util.Collection;


import com.bids.bpm.facts.FactSessionManager;
import com.bids.bpm.facts.model.BidsFact;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.ObjectFilter;
import org.kie.api.runtime.rule.FactHandle;

public class KieSessionBidsFactManager
        implements FactSessionManager
{
    // this object contains no local state, you can make many copies
    private transient final KieSession kieSession;
    private transient final FactIDFactory factIDFactory;

    public KieSessionBidsFactManager(KieSession kieSession)
    {
        this.kieSession = kieSession;

        // the fact ID factory's state is maintained within the session itself.
        for (Object o : kieSession.getObjects())
        {
            if (o instanceof SerializableFactIDFactory)
            {
                this.factIDFactory = (FactIDFactory) o;
                return;
            }
        }

        // no state yet, stick it in
        this.factIDFactory = new SerializableFactIDFactory();
        kieSession.insert(this.factIDFactory);
    }

    public void add(BidsFact fact)
    {
        addAndReturnHandle(fact);
    }

    public FactHandle addAndReturnHandle(BidsFact fact)
    {
        if (fact.getId() == 0)
            fact.setId(factIDFactory.getNextFactID(fact.getClass().getName()));
        else if (contains(fact))
            throw new RuntimeException("Fact already in agenda, use update or addOrUpdate: " + fact);
        return kieSession.insert(fact);
    }

    public void addOrUpdate(BidsFact fact)
    {
        if (contains(fact))
            update(fact);
        else
            add(fact);
    }

    public boolean contains(BidsFact fact)
    {
        return getHandle(fact) != null;
    }

    public void delete(final BidsFact fact)
    {
        FactHandle handle = getHandle(fact);
        if (handle == null)
            throw new RuntimeException("Fact is not in agenda: " + fact);
        kieSession.delete(handle);
    }

    @SuppressWarnings("unchecked")
    public <T extends BidsFact> T find(final Class<T> factClass, final String name)
    {
        org.kie.api.runtime.ObjectFilter doneTaskFilter = new org.kie.api.runtime.ObjectFilter()
        {
            public boolean accept(Object object)
            {
                return factClass.isAssignableFrom(object.getClass()) && ((BidsFact) object).getName().equals(name);
            }
        };
        Collection<FactHandle> workDoneHandles = kieSession.getFactHandles(doneTaskFilter);
        return workDoneHandles.size() == 0 ? null : (T) kieSession.getObject(workDoneHandles.iterator().next());
    }

    @SuppressWarnings("unchecked")
    public <T extends BidsFact> T find(final Class<T> factClass)
    {
        org.kie.api.runtime.ObjectFilter factClassFilter = new org.kie.api.runtime.ObjectFilter()
        {
            public boolean accept(Object object)
            {
                return factClass.isAssignableFrom(object.getClass());
            }
        };
        Collection<FactHandle> workDoneHandles = kieSession.getFactHandles(factClassFilter);
        return workDoneHandles.size() == 0 ? null : (T) kieSession.getObject(workDoneHandles.iterator().next());
    }

    public FactHandle find(ObjectFilter filter)
    {
        Collection<FactHandle> factHandles = kieSession.getFactHandles(filter);
        return factHandles.size() == 0 ? null : factHandles.iterator().next();
    }

    @SuppressWarnings("unchecked")
    public Collection<BidsFact> findAll()
    {
        return (Collection<BidsFact>) kieSession.getObjects(new org.drools.core.ObjectFilter()
        {
            public boolean accept(Object object)
            {
                return object instanceof BidsFact;
            }
        });
    }

    public void update(BidsFact fact)
    {
        FactHandle handle = getHandle(fact);
        if (handle == null)
            throw new RuntimeException("Fact is not in agenda: " + fact);
        update(fact, handle);
    }

    public void update(BidsFact fact, FactHandle handle)
    {
        kieSession.update(handle, fact);
    }

    @SuppressWarnings("unchecked")
    public <T extends BidsFact> T get(FactHandle handle)
    {
        return (T) kieSession.getObject(handle);
    }

    private FactHandle getHandle(BidsFact fact)
    {
        Collection<FactHandle> factHandles = kieSession.getFactHandles(makeFilter(fact));
        if (factHandles.size() == 0)
            return null;
        else if (factHandles.size() > 1)
            throw new RuntimeException("Unexpected result - more than one fact handle for the same fact.  Fact is: " + fact);
        else
            return factHandles.iterator().next();

    }

    private ObjectFilter makeFilter(final BidsFact fact)
    {
        return new ObjectFilter()
        {
            public boolean accept(Object object)
            {
                if (!(object instanceof BidsFact))
                    return false;
                else
                    return fact.equals(object);
            }
        };
    }
}
