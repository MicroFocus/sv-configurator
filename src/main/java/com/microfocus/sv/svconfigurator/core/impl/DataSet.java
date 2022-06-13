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
package com.microfocus.sv.svconfigurator.core.impl;

import java.util.List;

import com.microfocus.sv.svconfigurator.core.AbstractProjectElement;
import com.microfocus.sv.svconfigurator.core.IDataModel;
import com.microfocus.sv.svconfigurator.core.IDataSet;
import com.microfocus.sv.svconfigurator.core.IProjectElementDataSource;
import com.microfocus.sv.svconfigurator.core.IProjectElementVisitor;
import com.microfocus.sv.svconfigurator.core.encryption.EncryptionMetadata;
import com.microfocus.sv.svconfigurator.core.impl.encryption.EncryptedNode;

public class DataSet extends AbstractProjectElement implements IDataSet {

    //============================== STATIC ATTRIBUTES ========================================

    //============================== INSTANCE ATTRIBUTES ======================================

    private IDataModel dataModel;

    private long hashCode;

    //============================== CONSTRUCTORS =============================================

    public DataSet(String id, String name, long hashCode, IProjectElementDataSource ds, EncryptionMetadata encryptionMetadata, String projectPassword) {
        super(id, name, ds, encryptionMetadata, projectPassword);

        this.hashCode = hashCode;
    }

    //============================== ABSTRACT METHODS =========================================

    //============================== OVERRIDEN METHODS ========================================

    @Override
    public void accept(IProjectElementVisitor visitor) {
        visitor.visit(this);
    }

    //============================== INSTANCE METHODS =========================================

    //============================== GETTERS / SETTERS ========================================

    @Override
    public IDataModel getDataModel() {
        if (this.dataModel == null) {
            throw new IllegalStateException("DataSet is not initialized. It does not belong to any DataModel.");
        }
        return dataModel;
    }

    @Override
    public void setDataModel(IDataModel dataModel) {
        if (this.dataModel != null) {
            throw new IllegalStateException("DataSet has been already initialized. It can belong to only one DataModel.");
        }
        this.dataModel = dataModel;
    }

    @Override
    public long getDataHashCode() {
        return hashCode;
    }

    public void setDataHashCode(long hashCode) {
        this.hashCode = hashCode;
    }
    //============================== INNER CLASSES ============================================

}
