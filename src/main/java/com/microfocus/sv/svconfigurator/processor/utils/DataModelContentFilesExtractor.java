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
package com.microfocus.sv.svconfigurator.processor.utils;

import com.microfocus.sv.svconfigurator.core.impl.exception.SVCParseException;
import com.microfocus.sv.svconfigurator.util.XmlUtils;
import org.apache.commons.io.IOUtils;
import org.w3c.dom.Document;

import javax.xml.xpath.XPathExpressionException;
import java.io.ByteArrayInputStream;
import java.util.Set;

public class DataModelContentFilesExtractor {

    public static void extractContentFileIds(byte[] dataModelData, Set<String> includedFileIds) throws SVCParseException {
        Document doc;
        ByteArrayInputStream bis = null;
        try {
            bis = new ByteArrayInputStream(dataModelData);
            doc = XmlUtils.createDoc(bis);
        } catch (Exception e) {
            throw new SVCParseException("Failed to parse data model", e);
        } finally {
            IOUtils.closeQuietly(bis);
        }

        if (doc != null) {
            try {
                extract(doc, includedFileIds);
            } catch (Exception e) {
                throw new SVCParseException("Content File IDs extraction failed", e);
            }
        }
    }

    private static void extract(Document doc, Set<String> includedFileIds) throws XPathExpressionException {
        for (String includedFileId : XmlUtils.evalCollectionXpath("//*[local-name()='includedFile']/@includedFileId", doc)) {
            includedFileIds.add(includedFileId);
        }
    }
}