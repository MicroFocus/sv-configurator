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
package com.microfocus.sv.svconfigurator.processor.export;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

import com.microfocus.sv.svconfigurator.core.impl.exception.CommandExecutorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FolderWriter extends AbstractWriter {
    private static final Logger LOG = LoggerFactory.getLogger(FolderWriter.class);

    File projectFolder;

    public FolderWriter(File root, String projectName) throws CommandExecutorException {
        String folderName = entityNameToFileName(projectName);
        projectFolder = new File(root, folderName);
        if (projectFolder.exists()) {
            projectFolder = new File(root, folderName + "-" + UUID.randomUUID());
        }
        if (!projectFolder.exists()) {
            if (!projectFolder.mkdirs()) {
                throw new CommandExecutorException("Cannot create directory: " + projectFolder);
            }
        } else {
            throw new CommandExecutorException("Directory already exists: " + projectFolder);
        }
        LOG.info("Saving project '" + projectName + "' to '" + projectFolder + "'...");
    }

    @Override
    protected void writeDataImpl(byte[] data, String filePath) throws IOException {
        File f = new File(projectFolder, filePath);
        FileOutputStream fos = new FileOutputStream(f);
        try {
            fos.write(data);
        } finally {
            fos.close();
        }
    }
}
