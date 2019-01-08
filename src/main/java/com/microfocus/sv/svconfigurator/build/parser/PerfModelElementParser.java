/*
 *  Certain versions of software and/or documents ("Material") accessible here may contain branding from
 *  Hewlett-Packard Company (now HP Inc.) and Hewlett Packard Enterprise Company.  As of September 1, 2017,
 *  the Material is now offered by Micro Focus, a separately owned and operated company.  Any reference to the HP
 *  and Hewlett Packard Enterprise/HPE marks is historical in nature, and the HP and Hewlett Packard Enterprise/HPE
 *  marks are the property of their respective owners.
 * __________________________________________________________________
 * MIT License
 *
 * Copyright (c) 2012-2018 Micro Focus or one of its affiliates.
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

import java.util.Arrays;
import java.util.Collection;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import com.microfocus.sv.svconfigurator.core.IProjectElement;
import com.microfocus.sv.svconfigurator.core.IProjectElementDataSource;
import com.microfocus.sv.svconfigurator.core.impl.PerfModel;
import com.microfocus.sv.svconfigurator.core.impl.exception.SVCParseException;

public class PerfModelElementParser extends AbstractXMLElementParser {

    //============================== STATIC ATTRIBUTES ========================================

    private static final String ATTR_ID = "id";
    private static final String ATTRIBUTE_NAME = "name";

    public static final String FILE_EXTENSION = ".vspfmodel";

    private static final Collection<String> rootElements = Arrays.asList("offlinePerformanceModel", "performanceModel");

    //============================== INSTANCE ATTRIBUTES ======================================

    //============================== CONSTRUCTORS =============================================

    //============================== ABSTRACT METHODS =========================================

    //============================== OVERRIDEN METHODS ========================================

    @Override
    public IProjectElement create(IProjectElementDataSource ds, XMLEventReader reader, String projectPassword) throws SVCParseException, XMLStreamException {
        XMLEvent event = null;
        while (reader.hasNext() && (event == null || !event.isStartElement())) {
            event = reader.nextEvent();
        }
        if (event == null) {
            throw new SVCParseException(String.format("Unable to parse content of the file '%s'", ds.getName()));
        }
        StartElement el = event.asStartElement();
        if (! rootElements.contains(el.getName().getLocalPart())) {
            throw new SVCParseException("Parser does not recognize root element '"+ el.getName() +"'");
        }
        String id = AbstractXMLElementParser.getAttributeValue(el, ATTR_ID, false);
        String name = AbstractXMLElementParser.getAttributeValue(el, ATTRIBUTE_NAME, true);
        return new PerfModel(id, name, ds, readEncryptionMetadata(reader), projectPassword);
    }

    @Override
    public boolean isParserForDataSource(IProjectElementDataSource ds) {
        return ds.getName().endsWith(FILE_EXTENSION);
    }

    //============================== INSTANCE METHODS =========================================

    //============================== GETTERS / SETTERS ========================================

    //============================== INNER CLASSES ============================================

}
