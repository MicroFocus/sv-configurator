/*
 *  Certain versions of software and/or documents ("Material") accessible here may contain branding from
 *  Hewlett-Packard Company (now HP Inc.) and Hewlett Packard Enterprise Company.  As of September 1, 2017,
 *  the Material is now offered by Micro Focus, a separately owned and operated company.  Any reference to the HP
 *  and Hewlett Packard Enterprise/HPE marks is historical in nature, and the HP and Hewlett Packard Enterprise/HPE
 *  marks are the property of their respective owners.
 * __________________________________________________________________
 * MIT License
 *
 * Copyright (c) 2012-2022 Micro Focus or one of its affiliates.
 *
 * The only warranties for products and services of Micro Focus and its affiliates
 * and licensors ("Micro Focus") are set forth in the express warranty statements
 * accompanying such products and services. Nothing herein should be construed as
 * constituting an additional warranty. Micro Focus shall not be liable for technical
 * or editorial errors or omissions contained herein.
 * The information contained herein is subject to change without notice.
 * __________________________________________________________________
 *
 */
package com.microfocus.sv.svconfigurator.serverclient.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.microfocus.sv.svconfigurator.core.impl.exception.CommunicatorException;
import com.microfocus.sv.svconfigurator.serverclient.IJaxbProcessor;

public class JaxbProcessor implements IJaxbProcessor {
    // ============================== STATIC ATTRIBUTES
    // ========================================

    private static Logger LOG = LoggerFactory.getLogger(JaxbProcessor.class);

    // ============================== INSTANCE ATTRIBUTES
    // ======================================
    private Map<Class<?>, JAXBContext> contexts = new HashMap<Class<?>, JAXBContext>();

    private DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

    // ============================== STATIC METHODS
    // ===========================================

    // ============================== CONSTRUCTORS
    // =============================================

    public JaxbProcessor() {
        this.dbf.setExpandEntityReferences(false);
        this.dbf.setNamespaceAware(true);
    }

    // ============================== ABSTRACT METHODS
    // =========================================

    // ============================== OVERRIDEN METHODS
    // ========================================

    // ============================== INSTANCE METHODS
    // =========================================

    @Override
    public String marshall(Object o) throws CommunicatorException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        this.marshall(o, baos);
        try {
            baos.close();
            return baos.toString();
        } catch (IOException e) {
            throw new CommunicatorException("Error during marshalling");
        }
    }

    @Override
    public void marshall(Object o, OutputStream os) throws CommunicatorException {
        try {
            Marshaller m = this.getContext(o.getClass()).createMarshaller();
            m.marshal(o, os);
        } catch (JAXBException e) {
            throw new CommunicatorException("Error during object marshalling", e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T unmasrhall(InputStream is, Class<T> cls) throws CommunicatorException {
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document document = db.parse(is);

            Unmarshaller u = this.getContext(cls).createUnmarshaller();
            return (T) u.unmarshal(document);
        } catch (JAXBException e) {
            throw new CommunicatorException("Error during stream unmarshalling.", e);
        } catch (IOException e) {
            throw new CommunicatorException("Error during stream unmarshalling.", e);
        } catch (ParserConfigurationException e) {
            throw new CommunicatorException("Error during stream unmarshalling.", e);
        } catch (SAXException e) {
            throw new CommunicatorException("Error during stream unmarshalling.", e);
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                LOG.error("Error during InputStream closing.", e);
            }
        }
    }

    // ============================== PRIVATE METHODS
    // ==========================================

    private JAXBContext getContext(Class<?> cls) throws CommunicatorException {
        if (!this.contexts.containsKey(cls)) {
            try {
                this.contexts.put(cls, JAXBContext.newInstance(cls));
            } catch (JAXBException e) {
                throw new CommunicatorException("Error during JaxbContext initialization: " + e.getLocalizedMessage(), e);
            }
        }

        return this.contexts.get(cls);
    }

    // ============================== GETTERS / SETTERS
    // ========================================

    // ============================== INNER CLASSES
    // ============================================

}
