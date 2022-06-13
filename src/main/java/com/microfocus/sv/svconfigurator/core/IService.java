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
package com.microfocus.sv.svconfigurator.core;

import java.util.Collection;

public interface IService extends IProjectElement {

    public Collection<IDataModel> getDataModels();

    public Collection<IPerfModel> getPerfModels();

    public Collection<IServiceDescription> getDescriptions();

    public Collection<IContentFile> getContentFiles();

    public Collection<ILoggedServiceCallList> getLoggedServiceCallLists();

    public IProject getBaseProject();

    public void addDataModel(IDataModel dm);

    public void addPerfModel(IPerfModel pm);

    public void setBaseProject(IProject baseProject);

    public void addDescription(IServiceDescription sd);

    public void addContentFile(IContentFile cf);

    public void addLoggedServiceCallList(ILoggedServiceCallList loggedServiceCallList);

    public String getRuntimeIssues();

    public boolean NonExistentRealService();
}
