/**
 * ===========================================================================
 *    Copyright 2012 BIDS Holdings L.P. All rights reserved.
 * ---------------------------------------------------------------------------
 * Created on 1/2/14
 * By bidsjagu
 *
 */

package com.bids.bpm.rest.client;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.namespace.QName;


import static javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT;

public class JAXBHelper
{
    public static <V> String marshallIntoXML(Class<V> jaxbClass, V instance)
            throws Exception
    {
        StringWriter sw = new StringWriter();
        JAXBContext jbc = JAXBContext.newInstance(jaxbClass);
        jbc.createMarshaller().marshal(instance, sw);
        return sw.toString();
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static <V> String marshallIntoXML(String name, Class<V> jaxbClass, List<?> instances)
            throws JAXBException
    {
        StringWriter sw = new StringWriter();
        QName qName = new QName(name);
        JAXBContext jbc = JAXBContext.newInstance(jaxbClass,Wrapper.class);
        Wrapper wrapper = new Wrapper(instances);
        JAXBElement<Wrapper> jaxbElement = new JAXBElement<Wrapper>(qName, Wrapper.class, wrapper);
        Marshaller marshaller = jbc.createMarshaller();
        marshaller.setProperty(JAXB_FORMATTED_OUTPUT,true);
        marshaller.marshal(jaxbElement, sw);
        return sw.toString();
    }

    public static class Wrapper<T> {

        private List<T> items;

        public Wrapper() {
            items = new ArrayList<T>();
        }

        public Wrapper(List<T> items) {
            this.items = items;
        }

        @XmlAnyElement(lax=true)
        public List<T> getItems() {
            return items;
        }

    }
}
