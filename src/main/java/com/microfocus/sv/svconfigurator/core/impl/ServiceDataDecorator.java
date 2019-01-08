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
package com.microfocus.sv.svconfigurator.core.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

import com.microfocus.sv.svconfigurator.core.*;
import com.microfocus.sv.svconfigurator.core.impl.exception.SVCParseException;

public class ServiceDataDecorator implements IService {

    private final IService service;

    private final IProjectElementDataSource ds;

    public ServiceDataDecorator(IService service, IProjectElementDataSource ds) {
        this.service = service;
        this.ds = ds;
    }

    @Override
    public String toString() {
        return this.service.toString();
    }

    @Override
    public String getId() {
        return this.service.getId();
    }

    @Override
    public String getName() {
        return this.service.getName();
    }

    @Override
    public InputStream getData() throws IOException, SVCParseException {
        return this.ds.getData();
    }

    @Override
    public long getDataLength() throws IOException, SVCParseException {
        return this.ds.getDataSize();
    }

    @Override
    public void accept(IProjectElementVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public void close() throws IOException {
        this.ds.close();
        this.service.close();
    }

    @Override
    public Collection<IDataModel> getDataModels() {
        return this.service.getDataModels();
    }

    @Override
    public Collection<IPerfModel> getPerfModels() {
        return this.service.getPerfModels();
    }

    @Override
    public Collection<IServiceDescription> getDescriptions() {
        return this.service.getDescriptions();
    }

    @Override
    public Collection<IContentFile> getContentFiles() {
        return this.service.getContentFiles();
    }

    @Override
    public IProject getBaseProject() {
        return this.service.getBaseProject();
    }

    @Override
    public void addDataModel(IDataModel dm) {
        this.service.addDataModel(dm);
    }

    @Override
    public void addPerfModel(IPerfModel pm) {
        this.service.addPerfModel(pm);
    }

    @Override
    public void setBaseProject(IProject baseProject) {
        this.service.setBaseProject(baseProject);
    }

    @Override
    public void addDescription(IServiceDescription sd) {
        this.service.addDescription(sd);
    }

    @Override
    public void addContentFile(IContentFile cf) {
        this.service.addContentFile(cf);
    }

    @Override
    public String getRuntimeIssues() {
        return this.service.getRuntimeIssues();
    }

}
