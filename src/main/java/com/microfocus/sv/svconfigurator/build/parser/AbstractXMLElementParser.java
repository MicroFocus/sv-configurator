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
package com.microfocus.sv.svconfigurator.build.parser;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.microfocus.sv.svconfigurator.core.IProjectElement;
import com.microfocus.sv.svconfigurator.core.IProjectElementDataSource;
import com.microfocus.sv.svconfigurator.core.encryption.EncryptionMetadata;
import com.microfocus.sv.svconfigurator.core.impl.encryption.EncryptedNode;
import com.microfocus.sv.svconfigurator.core.impl.exception.SVCParseException;

/**
 * Abstract class for XML parser - it creates an XMLEventReader and so concrete implementation does not have to create it
 * on its own.
 */
public abstract class AbstractXMLElementParser extends AbstractProjectElementParser {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractXMLElementParser.class);
    
    public static String EncryptionMetadataElementName = "EncryptionMetadata";
    public static String EncryptedNodeElementName = "EncryptedNode";
    public static String EncryptionVersionAttributeName = "EncryptionVersion";
    public static String XPointerAttributeName = "xpointer";
    public static String TargetNameAttributeName = "targetName";

    static QName EncryptionMetadataElementQName = QName.valueOf(EncryptionMetadataElementName);

    /**
     * Reads encrypted nodes from the source reader. The reader must be in a position when the next element
     * is EncryptionMetadata element.
     */
    protected EncryptionMetadata readEncryptionMetadata(XMLEventReader reader) throws XMLStreamException, SVCParseException {
        XMLEvent xmlEvent = null;
        // move to the first element
        while (reader.hasNext() && (xmlEvent == null || !xmlEvent.isStartElement())) {
            xmlEvent = reader.nextEvent();
        }
        if (xmlEvent == null || !xmlEvent.isStartElement()) {
            return null;
        }
        StartElement xmlStartElement = xmlEvent.asStartElement();
        QName encryptedDataStartElementName = xmlStartElement.getName();
        if (encryptedDataStartElementName == null || !EncryptionMetadataElementName.equals(encryptedDataStartElementName.getLocalPart())) {
            return null;
        }

        String encryptionVersion = "";
        Attribute attr = xmlStartElement.getAttributeByName(new QName(null, EncryptionVersionAttributeName));
        if (attr != null) {
            encryptionVersion = attr.getValue();
        }

        List<EncryptedNode> retVal = new ArrayList<EncryptedNode>();
        while (reader.hasNext() && !isEndElement(xmlEvent, EncryptionMetadataElementQName)) {
            xmlEvent = reader.nextEvent();
            if (xmlEvent.isStartElement()) {
                xmlStartElement = xmlEvent.asStartElement();
                QName qName = xmlStartElement.getName();
                if (qName != null && EncryptedNodeElementName.equals(qName.getLocalPart())) {
                    String encryptedNodeXPath = getAttributeValue(xmlStartElement, XPointerAttributeName, false);
                    String targetName = getAttributeValue(xmlStartElement, TargetNameAttributeName, false);
                    if (encryptedNodeXPath != null && targetName != null) {
                        retVal.add(new EncryptedNode(encryptedNodeXPath, targetName));
                    }
                }
            }
        }
        return new EncryptionMetadata(encryptionVersion, retVal);
    }

    private boolean isEndElement(XMLEvent xmlEvent, QName elementQName) {
        if (!xmlEvent.isEndElement()) {
            return false;
        }
        EndElement endElement = xmlEvent.asEndElement();
        return elementQName.equals(endElement.getName());
    }

    /**
     * Skips in the XML stream to the element with specified name. If there is no such a element, an exception is thrown
     */
    public static StartElement skipToElement(XMLEventReader reader, String element) throws SVCParseException, XMLStreamException {
        while (reader.hasNext()) {
            XMLEvent e = reader.nextEvent();
            if (e.isStartElement()) {
                StartElement startEl = e.asStartElement();
                String localName = startEl.getName().getLocalPart();
                if (localName.equals(element)) {
                    return startEl;
                }
            }
        }
        throw new SVCParseException("There was not found an element with name " + element);
    }

    /**
     * Returns the element's attribute name
     * @param element StartElement to be processed
     * @param attrName Desired attribute name
     * @return value of the attribute
     * @throws com.microfocus.sv.svconfigurator.core.impl.exception.SVCParseException If there is not such an attribute
     */
    public static String getAttributeValue(StartElement element, String attrName, boolean optional) throws SVCParseException {
        Attribute atr = element.getAttributeByName(QName.valueOf(attrName));
        if (atr == null) {
            if (optional) {
                LOG.debug("There is no attribute with name '" + attrName + "' in the element "+ element.toString());
                return "";
            } else {
                throw new SVCParseException("There is no attribute with name '" + attrName + "' in the element "+ element.toString());
            }
        }
        return atr.getValue();
    }



    public abstract IProjectElement create(IProjectElementDataSource ds, XMLEventReader reader, String projectPassword) throws SVCParseException, XMLStreamException;

    @Override
    public IProjectElement create(IProjectElementDataSource ds, String projectPassword) throws SVCParseException {
        InputStream is = null;
        try {
            is = ds.getData();
            XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
            
            xmlInputFactory.setProperty("javax.xml.stream.supportDTD", false);
            xmlInputFactory.setProperty("javax.xml.stream.isSupportingExternalEntities", false);
            xmlInputFactory.setProperty("javax.xml.stream.isReplacingEntityReferences", false);
            
            XMLEventReader reader = xmlInputFactory.createXMLEventReader(is);
            return this.create(ds, reader, projectPassword);
        } catch (XMLStreamException ex) {
            throw new SVCParseException("Error during file parsing, did not you use stream from non verified file?", ex);
        } catch (IOException ex) {
            throw new SVCParseException("Zip Entry IO error.", ex);
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                //TODO: logger
            }
        }
    }
}
