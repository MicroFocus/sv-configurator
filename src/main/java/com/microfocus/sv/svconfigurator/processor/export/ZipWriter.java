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
import java.nio.charset.Charset;
import javax.xml.bind.DatatypeConverter;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.microfocus.sv.svconfigurator.core.impl.exception.CommandExecutorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZipWriter extends AbstractWriter {
    private static final Logger LOG = LoggerFactory.getLogger(ZipWriter.class);
    public static final String FILE_EXTENSION = ".vproja";

    ZipOutputStream zipOutputStream;

    public ZipWriter(File root, String projectName) throws CommandExecutorException, IOException {
        String archiveName = entityNameToFileName(projectName) + FILE_EXTENSION;

        File archiveFile = new File(root, archiveName);

        if (archiveFile.exists()) {
            archiveFile = new File(root, archiveName + "-" + UUID.randomUUID() + FILE_EXTENSION);
        }
        if (archiveFile.exists()) {
            throw new CommandExecutorException("File already exists: " + archiveFile);
        }
        LOG.info("Saving project '" + projectName + "' to '" + archiveFile + "'...");
        zipOutputStream = new ZipOutputStream(new FileOutputStream(archiveFile));

        // write project name as comment
        byte[] bytes = projectName.getBytes(Charset.forName("utf-16LE"));
        zipOutputStream.setComment(DatatypeConverter.printBase64Binary(bytes));
    }

    @Override
    public void writeDataImpl(byte[] data, String filePath) throws IOException {
        ZipEntry entry = new ZipEntry(filePath);

        zipOutputStream.putNextEntry(entry);
        zipOutputStream.write(data);
        zipOutputStream.closeEntry();
    }

    @Override
    public void close() throws IOException {
        zipOutputStream.finish();
        zipOutputStream.close();
    }
}
