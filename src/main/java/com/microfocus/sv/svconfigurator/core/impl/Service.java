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

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.microfocus.sv.svconfigurator.core.AbstractProjectElement;
import com.microfocus.sv.svconfigurator.core.IContentFile;
import com.microfocus.sv.svconfigurator.core.IDataModel;
import com.microfocus.sv.svconfigurator.core.ILoggedServiceCallList;
import com.microfocus.sv.svconfigurator.core.IPerfModel;
import com.microfocus.sv.svconfigurator.core.IProject;
import com.microfocus.sv.svconfigurator.core.IProjectElementDataSource;
import com.microfocus.sv.svconfigurator.core.IProjectElementVisitor;
import com.microfocus.sv.svconfigurator.core.IService;
import com.microfocus.sv.svconfigurator.core.IServiceDescription;
import com.microfocus.sv.svconfigurator.core.encryption.EncryptionMetadata;
import com.microfocus.sv.svconfigurator.processor.printer.NonPrintable;

public class Service extends AbstractProjectElement implements IService {

    private Set<IDataModel> dataModels;
    private Set<IPerfModel> perfModels;
    @NonPrintable
    private Set<IServiceDescription> svcDescs;
    @NonPrintable
    private Set<IContentFile> contentFiles;
    @NonPrintable
    private IProject baseProject;
    private String runtimeIssues;
    @NonPrintable
    private boolean nonExistentRealService;
    @NonPrintable
    private Set<ILoggedServiceCallList> loggedServiceCallLists;

    public Service(String id, String name, IProjectElementDataSource ds, EncryptionMetadata encryptionMetadata, String projectPassword, String runtimeIssues, boolean nonExistentRealService) {
        super(id, name, ds, encryptionMetadata, projectPassword);
        this.runtimeIssues = runtimeIssues;

        this.dataModels = new HashSet<IDataModel>();
        this.perfModels = new HashSet<IPerfModel>();
        this.svcDescs = new HashSet<IServiceDescription>();
        this.contentFiles = new HashSet<IContentFile>();
        this.loggedServiceCallLists = new HashSet<ILoggedServiceCallList>();
        this.baseProject = null;
        this.nonExistentRealService = nonExistentRealService;
    }

    public Service(String id, String name){
        this(id, name, null, null, null, null, false);
    }

    @Override
    public void accept(IProjectElementVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return "Service[" + this.getName() + "]";
    }

    @Override
    public Collection<IDataModel> getDataModels() {
        return this.dataModels;
    }

    @Override
    public Collection<IPerfModel> getPerfModels() {
        return this.perfModels;
    }

    @Override
    public Collection<IServiceDescription> getDescriptions() {
        return this.svcDescs;
    }

    @Override
    public Collection<IContentFile> getContentFiles() {
        return this.contentFiles;
    }

    @Override
    public Collection<ILoggedServiceCallList> getLoggedServiceCallLists() {
        return this.loggedServiceCallLists;
    }

    @Override
    public void addDescription(IServiceDescription sd) {
        this.svcDescs.add(sd);
    }

    @Override
    public void addContentFile(IContentFile cf) {
        this.contentFiles.add(cf);
    }

    @Override
    public void addLoggedServiceCallList(ILoggedServiceCallList loggedServiceCallList) {
        this.loggedServiceCallLists.add(loggedServiceCallList);
    }

    @Override
    public String getRuntimeIssues() {
        return runtimeIssues;
    }

    @Override
    public boolean NonExistentRealService() {
        return nonExistentRealService;
    }

    @Override
    public void addDataModel(IDataModel dm) {
        this.dataModels.add(dm);
        dm.setService(this);
    }

    @Override
    public void addPerfModel(IPerfModel pm) {
        this.perfModels.add(pm);
        pm.setService(this);
    }

    @Override
    public IProject getBaseProject() {
        if (this.baseProject == null) {
            throw new IllegalStateException("ReferenceElement is not initialized - it does not belong to any project.");
        }
        return baseProject;
    }

    @Override
    public void setBaseProject(IProject baseProject) {
        if (this.baseProject != null) {
            throw new IllegalStateException("ReferenceElement can belong to only one p");
        }
        this.baseProject = baseProject;
    }
}
