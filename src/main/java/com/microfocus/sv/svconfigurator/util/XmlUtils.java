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
package com.microfocus.sv.svconfigurator.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.microfocus.sv.svconfigurator.core.impl.exception.SVCParseException;

public class XmlUtils {
    // ============================== STATIC ATTRIBUTES
    // ========================================

    // ============================== INSTANCE ATTRIBUTES
    // ======================================

    // ============================== STATIC METHODS
    // ===========================================

    public static Document createDoc(InputStream is) throws IOException,
            SVCParseException {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory
                    .newInstance();

            factory.setFeature(
                    "http://apache.org/xml/features/disallow-doctype-decl",
                    true);
            factory.setNamespaceAware(true);
            factory.setExpandEntityReferences(false);
            
            DocumentBuilder builder = factory.newDocumentBuilder();

            return builder.parse(is);
        } catch (SAXException ex) {
            throw new SVCParseException("Error during stream parsing", ex);
        } catch (ParserConfigurationException ex) {
            throw new SVCParseException("Parser configuration exception", ex);
        } finally {
            is.close();
        }
    }

    public static void writeDoc(OutputStream os, Document doc)
            throws IOException, SVCParseException {
        try {
            TransformerFactory transformerFactory = TransformerFactory
                    .newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(os);
            transformer.transform(source, result);
        } catch (TransformerException ex) {
            throw new SVCParseException("Error during stream persisting", ex);
        } finally {
            os.close();
        }
    }

    public static Collection<String> evalCollectionXpath(String expr,
            Document doc) throws XPathExpressionException {
        NodeList nl = evalNodeListXpath(expr, doc);
        Collection<String> res = new ArrayList<String>(nl.getLength());
        for (int i = 0; i < nl.getLength(); i++) {
            res.add(ProjectUtils.decodeInclude(nl.item(i).getNodeValue()));
        }
        return res;
    }

    public static NodeList evalNodeListXpath(String expr,
            Document doc) throws XPathExpressionException {
        XPathFactory xPathfactory = XPathFactory.newInstance();
        XPath xpath = xPathfactory.newXPath();

        return (NodeList) xpath.evaluate(expr, doc,
                XPathConstants.NODESET);
    }

    public static Map<String, String> getNodeAsKeyValueMap(Node node, short nodeType) {
        Map<String,String> result = new HashMap<String, String>();
        if(nodeType == Document.ATTRIBUTE_NODE) {
            NamedNodeMap childNodes = node.getAttributes();
            for (int i = 0; i < childNodes.getLength(); i++) {
                Node childNode = childNodes.item(i);
                if (childNode.getNodeType() == nodeType) {
                    result.put(childNode.getLocalName(), childNode.getTextContent());
                }
            }
        } else {
            NodeList childNodes = node.getChildNodes();
            for (int i = 0; i < childNodes.getLength(); i++) {
                Node childNode = childNodes.item(i);
                if (childNode.getNodeType() == nodeType) {
                    result.put(childNode.getLocalName(), childNode.getTextContent());
                }
            }
        }
        return result;
    }

    // ============================== CONSTRUCTORS
    // =============================================

    // ============================== ABSTRACT METHODS
    // =========================================

    // ============================== OVERRIDEN METHODS
    // ========================================

    // ============================== INSTANCE METHODS
    // =========================================

    // ============================== PRIVATE METHODS
    // ==========================================

    // ============================== GETTERS / SETTERS
    // ========================================

    // ============================== INNER CLASSES
    // ============================================

}
