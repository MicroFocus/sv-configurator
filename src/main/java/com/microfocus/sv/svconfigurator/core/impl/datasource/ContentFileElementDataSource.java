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

package com.microfocus.sv.svconfigurator.core.impl.datasource;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import com.microfocus.sv.svconfigurator.core.IProjectElementDataSource;
import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/**
 * Project element data source that obtains data from a single content file (like a .cs or .js file
 * and encloses it in SV-compatible XML representation along with file metadata read from the project file.
 */
public class ContentFileElementDataSource implements IProjectElementDataSource {
    //============================== STATIC ATTRIBUTES ========================================

    public static final String CONTENTFILE_ROOT_ELEMENT_NAME = "contentFile";
    public static final String NAMESPACE = "http://hp.com/SOAQ/ServiceVirtualization/2010/";

    //============================== INSTANCE ATTRIBUTES ======================================

    private IProjectElementDataSource rawDataSource;
    private Map<String, String> metadata;
    private byte[] data;

    //============================== STATIC METHODS ===========================================

    private static byte[] generateDataRepresentation(Map<String, String> metadata,
                                                     InputStream rawFileContentStream,
                                                     long rawFileContentLength) throws ParserConfigurationException, IOException, TransformerException {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

        // root elements
        Document doc = docBuilder.newDocument();
        Element rootElement = doc.createElementNS(NAMESPACE, CONTENTFILE_ROOT_ELEMENT_NAME);
        doc.appendChild(rootElement);

        for (Map.Entry<String, String> entry : metadata.entrySet()) {
            Attr attr = doc.createAttribute(entry.getKey());
            attr.setValue(entry.getValue());
            rootElement.setAttributeNode(attr);
        }

        byte[] rawContentBytes = new byte[(int) rawFileContentLength];
        rawFileContentStream.read(rawContentBytes, 0, rawContentBytes.length);
        rawFileContentStream.close();
        String rawContentBase64 = Base64.encode(rawContentBytes);
        rootElement.setTextContent(rawContentBase64);

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(doc);
        ByteArrayOutputStream bos=new ByteArrayOutputStream();
        StreamResult result=new StreamResult(bos);
        transformer.transform(source, result);

        byte[] resultBytes = bos.toByteArray();
        bos.close();

        return resultBytes;
    }

    //============================== CONSTRUCTORS =============================================

    public ContentFileElementDataSource(IProjectElementDataSource rawDataSource,
                                        Map<String, String> metadata) throws IOException, TransformerException, ParserConfigurationException {
        this.rawDataSource = rawDataSource;
        this.metadata = metadata;
        //TODO this can be done in a more lazy manner
        this.data = generateDataRepresentation(metadata, rawDataSource.getData(), rawDataSource.getDataSize());
    }

    //============================== ABSTRACT METHODS =========================================

    //============================== OVERRIDEN METHODS ========================================

    @Override
    public long getDataSize() {
        return this.data.length;
    }

    @Override
    public InputStream getData() {
        return new ByteArrayInputStream(this.data);
    }

    @Override
    public String getName() {
        return this.rawDataSource.getName();
    }

    @Override
    public void close() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ContentFileElementDataSource)) return false;

        ContentFileElementDataSource that = (ContentFileElementDataSource) o;

        if (data != null ? !data.equals(that.data) : that.data != null) return false;
        return true;
    }

    @Override
    public int hashCode() {
        return rawDataSource != null ? rawDataSource.hashCode() : 0;
    }
    //============================== INSTANCE METHODS =========================================

    //============================== PRIVATE METHODS ==========================================

    //============================== GETTERS / SETTERS ========================================

    //============================== INNER CLASSES ============================================

}
