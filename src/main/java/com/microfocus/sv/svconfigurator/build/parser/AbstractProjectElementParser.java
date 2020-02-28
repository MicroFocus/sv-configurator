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

import java.util.ArrayList;
import java.util.List;

import com.microfocus.sv.svconfigurator.core.IProjectElement;
import com.microfocus.sv.svconfigurator.core.IProjectElementDataSource;
import com.microfocus.sv.svconfigurator.core.impl.exception.SVCParseException;

/**
 * Abstract Class that defines the interface for parsing of single project file types (service file, data model file,
 * ...)
 */
public abstract class AbstractProjectElementParser {

    private static List<AbstractProjectElementParser> parsers = new ArrayList<AbstractProjectElementParser>();

    static {
        parsers.add(new ProjectElementParser());
        parsers.add(new ManifestElementParser());
        parsers.add(new DataModelElementParser());
        parsers.add(new PerfModelElementParser());
        parsers.add(new DataSetElementParser());
        parsers.add(new ServiceElementParser());
        parsers.add(new ServiceDescriptionElementParser());
        parsers.add(new TopologyElementParser());
        parsers.add(new ContentFileElementParser());
        parsers.add(new LoggedServiceCallListParser());
    }

    public static AbstractProjectElementParser getParserForDataSource(IProjectElementDataSource ds) throws SVCParseException {
        for (AbstractProjectElementParser parser : parsers) {
            if (parser.isParserForDataSource(ds)) {
                return parser;
            }
        }
        throw new SVCParseException("There was not found a parser for zip entry: "+ ds.getName());
    }

    /**
     * Decides if the parser is designed for specified project file
     *
     * @param ds
     * @return
     */
    public abstract boolean isParserForDataSource(IProjectElementDataSource ds);

    /**
     * Creates an implementation of IProjectElement according to the content of the project file
     *
     * @param ds Zip project archive
     * @return
     * @throws com.microfocus.sv.svconfigurator.core.impl.exception.SVCParseException
     */
    public abstract IProjectElement create(IProjectElementDataSource ds, String projectPassword) throws SVCParseException;

}
