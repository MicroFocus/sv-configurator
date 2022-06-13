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
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;

import com.microfocus.sv.svconfigurator.core.IProjectElement;
import com.microfocus.sv.svconfigurator.core.IProjectElementDataSource;
import com.microfocus.sv.svconfigurator.core.impl.Manifest;
import com.microfocus.sv.svconfigurator.core.impl.exception.SVCParseException;
import com.microfocus.sv.svconfigurator.util.StringUtils;

public class ManifestElementParser extends AbstractXMLElementParser {

    public static final String FILE_SUFFIX = ".vsmf";

    private static final String ELEMENT_MANIFEST = "deploymentManifest";

    private static final String ID_ATTR = "id";

    @Override
    public IProjectElement create(IProjectElementDataSource ds, XMLEventReader reader, String projectPassword) throws SVCParseException, XMLStreamException {
        StartElement elManifest = AbstractXMLElementParser.skipToElement(reader, ELEMENT_MANIFEST);
        Attribute idAttr = elManifest.getAttributeByName(QName.valueOf(ID_ATTR));
        String name = StringUtils.removeSuffix(ds.getName(), FILE_SUFFIX);

        return new Manifest(idAttr.getValue(), name, ds);
    }

    @Override
    public boolean isParserForDataSource(IProjectElementDataSource ds) {
        return ds.getName().endsWith(FILE_SUFFIX);
    }
}
