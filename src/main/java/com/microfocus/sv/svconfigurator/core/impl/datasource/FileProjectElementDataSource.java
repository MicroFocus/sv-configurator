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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.microfocus.sv.svconfigurator.core.IProjectElementDataSource;

public class FileProjectElementDataSource implements IProjectElementDataSource {
    //============================== STATIC ATTRIBUTES ========================================

    //============================== INSTANCE ATTRIBUTES ======================================

    private File f;

    //============================== STATIC METHODS ===========================================

    //============================== CONSTRUCTORS =============================================

    public FileProjectElementDataSource(File f) {
        this.f = f;
    }


    //============================== ABSTRACT METHODS =========================================

    //============================== OVERRIDEN METHODS ========================================

    //============================== INSTANCE METHODS =========================================

    //============================== PRIVATE METHODS ==========================================

    //============================== GETTERS / SETTERS ========================================

    @Override
    public long getDataSize() {
        return this.f.length();
    }

    @Override
    public InputStream getData() throws IOException {
        return new FileInputStream(this.f);
    }

    @Override
    public String getName() {
        return this.f.getName();
    }

    @Override
    public void close() throws IOException {
        //do nothing
    }


    //============================== INNER CLASSES ============================================

}
