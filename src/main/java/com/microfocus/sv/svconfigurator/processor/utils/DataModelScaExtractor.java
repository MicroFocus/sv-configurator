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
package com.microfocus.sv.svconfigurator.processor.utils;

import java.io.ByteArrayInputStream;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.microfocus.sv.svconfigurator.core.impl.exception.AbstractSVCException;
import com.microfocus.sv.svconfigurator.core.impl.exception.SVCParseException;
import com.microfocus.sv.svconfigurator.util.XmlUtils;

public final class DataModelScaExtractor {

    public static Set<String> extractScaServiceDescriptionIds(byte[] dataModelData) throws AbstractSVCException {
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
                return extract(doc);
            } catch (Exception e) {
                throw new SVCParseException("SCA Service Description IDs extraction failed", e);
            }
        } else {
            return null;
        }
    }

    private static Set<String> extract(Document doc) {
        Element root = doc.getDocumentElement();
        if (root == null) {
            throw new IllegalStateException("No dataModel element found");
        }

        NodeList list = root.getElementsByTagName("serviceOperations");
        if (list == null) {
            throw new IllegalStateException(
                    "No serviceOperations element found");
        } else if (list.getLength() == 0) {
            return null;
        }

        Element child = (Element) list.item(0);
        list = child.getElementsByTagName("serviceOperation");
        if (list == null) {
            throw new IllegalStateException(
                    "No serviceOperations element found");
        } else if (list.getLength() == 0) {
            return null;
        }

        Set<String> sdIds = new HashSet<String>();
        for (int i = 0; i < list.getLength(); i++) {
            child = (Element) list.item(i);
            NodeList activitiesList = child.getElementsByTagName("activities");
            if (activitiesList == null || activitiesList.getLength() != 1) {
                throw new IllegalStateException("No activities element found");
            }
            Element activities = (Element) activitiesList.item(0);

            NodeList scaList = activities
                    .getElementsByTagName("serviceCallActivity");
            if (scaList != null) {
                for (int j = 0; j < scaList.getLength(); j++) {
                    Element sca = (Element)scaList.item(j);
                    String sdId = sca.getAttribute("serviceDescriptionId");
                    if (sdId != null) {
                        sdIds.add(sdId);
                    }
                }
            }

            scaList = activities
                    .getElementsByTagName("postResponseServiceCallActivity");
            if (scaList != null) {
                for (int j = 0; j < scaList.getLength(); j++) {
                    Element sca = (Element)scaList.item(j);
                    String sdId = sca.getAttribute("serviceDescriptionId");
                    if (sdId != null) {
                        sdIds.add(sdId);
                    }
                }
            }
        }

        return sdIds;
    }

}
