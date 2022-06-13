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
package com.microfocus.sv.svconfigurator.core.impl.datasource;

import java.io.IOException;
import java.io.InputStream;

import com.microfocus.sv.svconfigurator.core.IProjectElementDataSource;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.FileHeader;

/**
 * Project element data source that obtains data from a single entry in a project archive (zip archive with suffix
 * .vproja).
 */
public class ArchiveProjectElementDataSource implements IProjectElementDataSource {
    //============================== STATIC ATTRIBUTES ========================================

    //============================== INSTANCE ATTRIBUTES ======================================

    private ZipFile zipFile;
    private FileHeader fileHeader;

    //============================== STATIC METHODS ===========================================

    //============================== CONSTRUCTORS =============================================

    public ArchiveProjectElementDataSource(ZipFile zipFile, FileHeader fileHeader) {
        this.zipFile = zipFile;
        this.fileHeader = fileHeader;
    }

    //============================== ABSTRACT METHODS =========================================

    //============================== OVERRIDEN METHODS ========================================

    @Override
    public long getDataSize() {
        return this.fileHeader.getUncompressedSize();
    }

    @Override
    public InputStream getData() throws IOException {
        try {
            return this.zipFile.getInputStream(this.fileHeader);
        } catch (ZipException e) {
            throw new IOException(e.getMessage(), e);
        }
    }

    @Override
    public String getName() {
        return this.fileHeader.getFileName();
    }

    @Override
    public void close() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ArchiveProjectElementDataSource)) return false;

        ArchiveProjectElementDataSource that = (ArchiveProjectElementDataSource) o;

        if (fileHeader != null ? !fileHeader.equals(that.fileHeader) : that.fileHeader != null) return false;
        return !(zipFile != null ? !zipFile.equals(that.zipFile) : that.zipFile != null);

    }

    @Override
    public int hashCode() {
        int result = zipFile != null ? zipFile.hashCode() : 0;
        result = 31 * result + (fileHeader != null ? fileHeader.hashCode() : 0);
        return result;
    }
    //============================== INSTANCE METHODS =========================================

    //============================== PRIVATE METHODS ==========================================

    //============================== GETTERS / SETTERS ========================================

    //============================== INNER CLASSES ============================================

}
