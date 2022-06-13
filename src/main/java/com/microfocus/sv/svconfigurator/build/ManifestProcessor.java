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
package com.microfocus.sv.svconfigurator.build;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import com.microfocus.sv.svconfigurator.core.ILoggedServiceCallList;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.microfocus.sv.svconfigurator.core.IManifest;
import com.microfocus.sv.svconfigurator.core.IProjectElement;
import com.microfocus.sv.svconfigurator.core.IService;
import com.microfocus.sv.svconfigurator.core.impl.exception.ProjectBuilderException;
import com.microfocus.sv.svconfigurator.core.impl.exception.SVCParseException;

public class ManifestProcessor {
    //============================== STATIC ATTRIBUTES ========================================

    private static final String FIND_CHILDREN_BY_ID = "//*[local-name()='Item' and ./@id='%s']//*[local-name()='Ref']/@id";
    private static final String FIND_ROOT_ID = "/*[local-name() = 'deploymentManifest']/@rootItemId";
    //============================== INSTANCE ATTRIBUTES ======================================
    private Map<String, IProjectElement> elementMap;
    private IManifest manifest;
    private Document doc;
    private XPath xpath;

    //============================== CONSTRUCTORS =============================================

    public ManifestProcessor(Map<String, IProjectElement> elementMap, IManifest manifest) throws ProjectBuilderException {
        this.elementMap = elementMap;
        this.manifest = manifest;

        XPathFactory factory = XPathFactory.newInstance();
        this.xpath = factory.newXPath();

        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            
            docFactory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            docFactory.setNamespaceAware(true);
            
            DocumentBuilder builder = docFactory.newDocumentBuilder();
            this.doc = builder.parse(this.manifest.getData());
        } catch (IOException ex) {
            throw new ProjectBuilderException("Error during Manifest data read", ex);
        } catch (ParserConfigurationException ex) {
            throw new ProjectBuilderException("Error during Manifest data read", ex);
        } catch (SAXException ex) {
            throw new ProjectBuilderException("Error during Manifest data read", ex);
        } catch (SVCParseException ex) {
            throw new ProjectBuilderException("Error during Manifest data read", ex);
        }
    }


    //============================== ABSTRACT METHODS =========================================

    //============================== OVERRIDEN METHODS ========================================

    //============================== INSTANCE METHODS =========================================

    public IService getRoot() throws ProjectBuilderException {
        try {
            String rootId = xpath.evaluate(FIND_ROOT_ID, this.doc);

            IProjectElement root = this.elementMap.get(rootId);
            if (root == null) {
                throw new ProjectBuilderException("Root element (specified in the manifest) with ID '" + rootId + "' was not found in the project archive.");
            }
            return (IService) root;
        } catch (XPathExpressionException ex) {
            throw new ProjectBuilderException("Error during xpath evaluation.", ex);
        }
    }

    public Collection<IProjectElement> getChildrenForElement(IProjectElement el) throws ProjectBuilderException {
        try {
            String id = el.getId();
            NodeList nodes = (NodeList) xpath.evaluate(String.format(FIND_CHILDREN_BY_ID, id), this.doc, XPathConstants.NODESET);

            Set<IProjectElement> res = new HashSet<IProjectElement>();
            for (int i = 0; i < nodes.getLength(); i++) {
                String nodeId = nodes.item(i).getNodeValue();
                IProjectElement child = this.elementMap.get(nodeId);
                if (child == null) {
                    throw new ProjectBuilderException("Element (specified in the manifest '" + this.manifest.getId() + "') with ID '" + nodeId + "' was not found in the project archive.");
                }
                res.add(child);
            }
            if(el instanceof IService) {
                for (Map.Entry<String, IProjectElement> entry : elementMap.entrySet()) {
                    IProjectElement value = entry.getValue();
                    if (value instanceof ILoggedServiceCallList) {
                        ILoggedServiceCallList loggedServiceCallList = (ILoggedServiceCallList) value;
                        if (loggedServiceCallList.VsId().equals(el.getId())) {
                            res.add(value);
                        }
                    }
                }
            }
            return res;
        } catch (XPathExpressionException ex) {
            throw new ProjectBuilderException("Error during xpath evaluation.", ex);
        }
    }

    //============================== PRIVATE METHODS ==========================================

    private void init(String projectPassword) {
        XPathFactory factory = XPathFactory.newInstance();
        XPath xpath = factory.newXPath();

        try {
            String res = xpath.evaluate("/*[local-name() = 'deploymentManifest']/@rootItemId", new InputSource(this.manifest.getData()));
            System.out.println(res);
        } catch (XPathExpressionException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (SVCParseException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    //============================== GETTERS / SETTERS ========================================

    //============================== INNER CLASSES ============================================


}
