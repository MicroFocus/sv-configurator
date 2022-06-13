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
package com.microfocus.sv.svconfigurator.processor;

import com.microfocus.sv.svconfigurator.core.IProject;
import com.microfocus.sv.svconfigurator.core.impl.jaxb.ServiceRuntimeConfiguration;

public class ChmodeProcessorInput {

    private boolean force;
    private IProject project;
    private String service;
    private String dataModel;
    private String perfModel;
    private ServiceRuntimeConfiguration.RuntimeMode serviceMode;
    private ServiceRuntimeConfiguration.RuntimeMode desiredServiceMode;
    private boolean defaultDataModel;
    private boolean defaultPerfModel;

    public ChmodeProcessorInput(boolean force, IProject project,
                                String service, String dataModel, String perfModel, ServiceRuntimeConfiguration.RuntimeMode serviceMode, 
                                boolean defaultDataModel, boolean defaultPerfModel) {
        this.force = force;
        this.project = project;
        this.service = service;
        this.dataModel = dataModel;
        this.perfModel = perfModel;
        this.desiredServiceMode = serviceMode;
        this.serviceMode = getTargetMode(serviceMode, perfModel, dataModel, defaultDataModel, defaultPerfModel);
        this.defaultDataModel = defaultDataModel;
        this.defaultPerfModel = defaultPerfModel;
    }

    private static ServiceRuntimeConfiguration.RuntimeMode getTargetMode(ServiceRuntimeConfiguration.RuntimeMode serviceMode,
                                                                         String perfModel, String dataModel,
                                                                         boolean defaultDataModel, boolean defaultPerfModel) {
        // translate Simulate(DM==null, PM!==null) to StandBy as required by rest API
        if (serviceMode == ServiceRuntimeConfiguration.RuntimeMode.SIMULATING && dataModel == null && !defaultDataModel
                && (perfModel != null || defaultPerfModel)) {
            return ServiceRuntimeConfiguration.RuntimeMode.STAND_BY;
        } else {
            return serviceMode;
        }
    }

    public boolean isForce() {
        return force;
    }

    public IProject getProject() {
        return project;
    }

    public String getService() {
        return service;
    }

    public String getDataModel() {
        return dataModel;
    }

    public String getPerfModel() {
        return perfModel;
    }

    public ServiceRuntimeConfiguration.RuntimeMode getServiceMode() {
        return serviceMode;
    }

    public boolean isDefaultDataModel() {
        return defaultDataModel;
    }

    public boolean isDefaultPerfModel() {
        return defaultPerfModel;
    }

    public ServiceRuntimeConfiguration.RuntimeMode getDesiredServiceMode() {
        return desiredServiceMode;
    }
}
