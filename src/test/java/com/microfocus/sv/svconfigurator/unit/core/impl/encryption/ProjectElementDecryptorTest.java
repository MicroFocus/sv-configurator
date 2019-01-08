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
package com.microfocus.sv.svconfigurator.unit.core.impl.encryption;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

import com.microfocus.sv.svconfigurator.core.encryption.EncryptionMetadata;
import org.junit.Before;
import org.junit.Test;

import com.microfocus.sv.svconfigurator.build.parser.AbstractXMLElementParser;
import com.microfocus.sv.svconfigurator.core.IProjectElement;
import com.microfocus.sv.svconfigurator.core.IProjectElementDataSource;
import com.microfocus.sv.svconfigurator.core.impl.encryption.EncryptedNode;
import com.microfocus.sv.svconfigurator.core.impl.encryption.ProjectElementDecryptor;
import com.microfocus.sv.svconfigurator.core.impl.encryption.ProjectElementDecryptorImpl;
import com.microfocus.sv.svconfigurator.core.impl.exception.SVCParseException;
import com.microfocus.sv.svconfigurator.resources.Resources;
import com.microfocus.sv.svconfigurator.unit.core.impl.AbstractCoreTest;

public class ProjectElementDecryptorTest extends AbstractCoreTest {

    ProjectElementDecryptor decryptor;

    public static final String ENCRYPTED_SERVICE_RESOURCE_NAME = "test/encryption/EncryptedService.vs";

    @Before
    public void setUp() throws Exception {
        TestXMLElementParser parser = new TestXMLElementParser();
        File encServiceFile = Resources.getResource(ENCRYPTED_SERVICE_RESOURCE_NAME);
        FileInputStream fis = new FileInputStream(encServiceFile);
        EncryptionMetadata encryptionMetadata = null;
        try {
            XMLEventReader reader = XMLInputFactory.newInstance().createXMLEventReader(fis);
            XMLEvent xmlEvent = null;
            while (reader.hasNext() && (xmlEvent == null || !xmlEvent.isStartElement())) {
                xmlEvent = reader.nextEvent();
            }
            encryptionMetadata = parser.readEncryptionMetadataInTest(reader);
        } finally {
            if (fis != null) {
                fis.close();
            }
        }
        decryptor = new ProjectElementDecryptorImpl(encryptionMetadata);
    }

    @Test
    public void testDecodeString() throws Exception {
        assertEquals("aaaaa", decryptor.decryptString("kbHtDCLCUEX/08XSZXFL72LTi1HVmSR4tKQcP7Zh8fs=", "aaa"));
        assertEquals("b", decryptor.decryptString("E0r2T9VZrQZOrAzUSq/EG4NDzU8F9tPntCX8jhv8Qi8=", "aaa"));
    }

    @Test
    public void testDecodeString_370() throws Exception {
        String version = "2";
        String encryptionPassword = "password";
        ProjectElementDecryptor decryptor = new ProjectElementDecryptorImpl(new EncryptionMetadata(version, null));

        assertEquals("a", decryptor.decryptString("K+9a8C+Mh964WW+XmXMQk485/41P3bkDlWl4/XLAvEiFvXnN/yJ7Ikrki9H5u2/S", encryptionPassword));
        assertEquals("password", decryptor.decryptString("ZPTS6tupb7vtdq+Ve3AERCfcpP0S69AdCkw0JsdIY9MH0x+epb1GpXTsok7l4qZq", encryptionPassword));
        assertEquals("passwordpassword", decryptor.decryptString("n+9WfA0HPa7WtCrxAPl9hwn5SCIyZCGCZGaoD3ekpTMTXliJfB3z71DitL52DCwKTXYOTGjrI78ydCYTbHHsWA==", encryptionPassword));
    }

    @Test
    public void testDecodeStream() throws Exception {
        File encServiceFile = Resources.getResource(ENCRYPTED_SERVICE_RESOURCE_NAME);
        FileInputStream fis = new FileInputStream(encServiceFile);
        try {
            String decodedXmlStr = new java.util.Scanner(fis).useDelimiter("\\A").next();
            assertFalse(decodedXmlStr.contains("\"aaaaa\""));
            assertFalse(decodedXmlStr.contains("\"b\""));
            assertTrue(decodedXmlStr.contains("\"kbHtDCLCUEX/08XSZXFL72LTi1HVmSR4tKQcP7Zh8fs=\""));
            assertTrue(decodedXmlStr.contains("\"E0r2T9VZrQZOrAzUSq/EG4NDzU8F9tPntCX8jhv8Qi8=\""));
        } finally {
            if (fis != null) {
                fis.close();
            }
        }

        fis = new FileInputStream(encServiceFile);
        try {
            InputStream is = new ByteArrayInputStream(decryptor.decodeStream(fis, "aaa"));
            String decodedXmlStr = new java.util.Scanner(is).useDelimiter("\\A").next();
            assertTrue(decodedXmlStr.contains("\"aaaaa\""));
            assertTrue(decodedXmlStr.contains("\"b\""));
            assertFalse(decodedXmlStr.contains("kbHtDCLCUEX/08XSZXFL72LTi1HVmSR4tKQcP7Zh8fs="));
            assertFalse(decodedXmlStr.contains("E0r2T9VZrQZOrAzUSq/EG4NDzU8F9tPntCX8jhv8Qi8="));
        } finally {
            if (fis != null) {
                fis.close();
            }
        }
    }

    class TestXMLElementParser extends AbstractXMLElementParser {

        @Override
        public IProjectElement create(IProjectElementDataSource ds, XMLEventReader reader, String projectPassword) throws SVCParseException, XMLStreamException {
            return null;
        }

        @Override
        public boolean isParserForDataSource(IProjectElementDataSource ds) {
            return false;
        }

        EncryptionMetadata readEncryptionMetadataInTest(XMLEventReader reader) throws XMLStreamException, SVCParseException {
            return this.readEncryptionMetadata(reader);
        }
    }
}
