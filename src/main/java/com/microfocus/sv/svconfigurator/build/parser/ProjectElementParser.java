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

import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;

import com.microfocus.sv.svconfigurator.core.IProjectElement;
import com.microfocus.sv.svconfigurator.core.IProjectElementDataSource;
import com.microfocus.sv.svconfigurator.core.impl.Project;
import com.microfocus.sv.svconfigurator.core.impl.exception.SVCParseException;
import com.microfocus.sv.svconfigurator.util.StringUtils;

public class ProjectElementParser extends AbstractXMLElementParser {

    private static final String FILE_SUFFIX = ".vproj";

    private static final String ELEMENT_PROJ_GUID = "ProjectGuid";
    private static final String ELEMENT_PROJ_SERVER_URL = "ServerUrl";

    @Override
    public IProjectElement create(IProjectElementDataSource ds, XMLEventReader reader, String projectPassword) throws SVCParseException, XMLStreamException {
        AbstractXMLElementParser.skipToElement(reader, ELEMENT_PROJ_GUID);
        String id = reader.getElementText();
        String name = StringUtils.removeSuffix(ds.getName(), FILE_SUFFIX);

        AbstractXMLElementParser.skipToElement(reader, ELEMENT_PROJ_SERVER_URL);
        String uriStr = reader.getElementText();
        URL uri = null;
        if (uriStr != null && !uriStr.trim().isEmpty()) {
            try {
                uri = new URL(uriStr);
            } catch (MalformedURLException e) {
                throw new SVCParseException("Failed to parser project's server URL '" + uriStr + "'", e);
            }
        }
        return new Project(id, name, projectPassword, uri, ds);
    }

    @Override
    public boolean isParserForDataSource(IProjectElementDataSource ds) {
        return ds.getName().endsWith(FILE_SUFFIX);
    }
}
