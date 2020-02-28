package com.microfocus.sv.svconfigurator.build.parser;

import com.microfocus.sv.svconfigurator.core.IProjectElement;
import com.microfocus.sv.svconfigurator.core.IProjectElementDataSource;
import com.microfocus.sv.svconfigurator.core.impl.LoggedServiceCallList;
import com.microfocus.sv.svconfigurator.core.impl.exception.SVCParseException;
import com.microfocus.sv.svconfigurator.util.StringUtils;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import java.util.UUID;

public class LoggedServiceCallListParser extends AbstractXMLElementParser {

    //============================== STATIC ATTRIBUTES ========================================

    public static final String FILE_EXTENSION = ".msglog";
    private static final String ROOT_EL = "loggedServiceCalls";
    private static final String ATTR_VS_ID = "virtualServiceId";

    @Override
    public IProjectElement create(IProjectElementDataSource ds, XMLEventReader reader, String projectPassword) throws SVCParseException, XMLStreamException {
        StartElement el = AbstractXMLElementParser.skipToElement(reader, ROOT_EL);
        String vsId = AbstractXMLElementParser.getAttributeValue(el, ATTR_VS_ID, false);
        String name = StringUtils.removeSuffix(ds.getName(), FILE_EXTENSION);

        return new LoggedServiceCallList(UUID.randomUUID().toString(), name, vsId, ds, readEncryptionMetadata(reader), projectPassword);
    }

    @Override
    public boolean isParserForDataSource(IProjectElementDataSource ds) {
        return ds.getName().endsWith(FILE_EXTENSION);
    }
}