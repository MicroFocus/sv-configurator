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

import com.microfocus.sv.svconfigurator.core.AbstractProjectElement;
import com.microfocus.sv.svconfigurator.core.IPerfModel;
import com.microfocus.sv.svconfigurator.core.IProjectElementDataSource;
import com.microfocus.sv.svconfigurator.core.IProjectElementVisitor;
import com.microfocus.sv.svconfigurator.core.IService;
import com.microfocus.sv.svconfigurator.core.encryption.EncryptionMetadata;
import com.microfocus.sv.svconfigurator.processor.printer.NonPrintable;

public abstract class AbstractPerfModel extends AbstractProjectElement
        implements IPerfModel {

    @NonPrintable
    private IService service;

    public AbstractPerfModel(String id, String name,
            IProjectElementDataSource ds) {
        super(id, name, ds);
    }

    public AbstractPerfModel(String id, String name,
            IProjectElementDataSource ds, EncryptionMetadata encryptionMetadata,
            String projectPassword) {
        super(id, name, ds, encryptionMetadata, projectPassword);
    }

    @Override
    public void accept(IProjectElementVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public IService getService() {
        if (this.service == null) {
            throw new IllegalStateException(
                    "PerformanceModel is not initialized - it does not belong to any service.");
        }
        return service;
    }

    public void setService(IService service) {
        if (this.service != null) {
            throw new IllegalStateException(
                    "PerformanceModel is already initialized. It can belong only to one service.");
        }
        this.service = service;
    }

    @Override
    public int compareTo(IPerfModel o) {
        if (this.getName() == null) {
            return o.getName() == null ? 0 : -1;
        } else {
            return this.getName().compareToIgnoreCase(o.getName());
        }
    }

}