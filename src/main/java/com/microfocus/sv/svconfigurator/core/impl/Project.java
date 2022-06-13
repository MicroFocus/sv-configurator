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

import java.net.URL;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.microfocus.sv.svconfigurator.core.AbstractProjectElement;
import com.microfocus.sv.svconfigurator.core.IProject;
import com.microfocus.sv.svconfigurator.core.IProjectElementDataSource;
import com.microfocus.sv.svconfigurator.core.IProjectElementVisitor;
import com.microfocus.sv.svconfigurator.core.IService;

public class Project extends AbstractProjectElement implements IProject {

    private Set<IService> services;

    private URL serverUrl;

    public Project(String id, String name, String projectPassword, URL serverUrl, IProjectElementDataSource ds) {
        super(id, name, ds);

        this.services = new HashSet<IService>();
        this.serverUrl = serverUrl;
    }

    @Override
    public String toString() {
        return "Project["+ this.getName() +"]";
    }

    @Override
    public void accept(IProjectElementVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public Collection<IService> getServices() {
        return this.services;
    }

    @Override
    public void addService(IService svc) {
        this.services.add(svc);
        svc.setBaseProject(this);
    }

    @Override
    public URL getServerUrl() {
        return this.serverUrl;
    }

    @Override
    public String getProjectPassword() {
        return this.projectPassword;
    }
}
