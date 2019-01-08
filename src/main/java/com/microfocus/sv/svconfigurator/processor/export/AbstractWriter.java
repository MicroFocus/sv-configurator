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
package com.microfocus.sv.svconfigurator.processor.export;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractWriter {
    Map<String, byte[]> entries = new LinkedHashMap<String, byte[]>();
    private static final Logger LOG = LoggerFactory.getLogger(ZipWriter.class);

    public static String entityNameToFileName(String entityName) {
        return entityName.replaceAll("[^a-zA-Z0-9 .-]", "_").trim();
    }

    public void addData(String filePath, byte[] data) {
        LOG.info("    writing '" + filePath + "'...");
        entries.put(filePath, data);
    }

    public void commit() throws IOException {
        for (Map.Entry<String, byte[]> e:entries.entrySet()) {
            writeDataImpl(e.getValue(), e.getKey());
        }
        entries.clear();
    }

    public void rollback() {
        entries.clear();
    }

    protected abstract void writeDataImpl(byte[] data, String filePath) throws IOException;

    public void close() throws IOException {}
}
