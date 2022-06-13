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

import java.util.Map;

public class DeployProcessorInput {

    private boolean force;
    private boolean undeploy;
    private IProject project;
    private String service;
    private Map<String, String> agentRemapping;
    private final boolean importLoggedMessages;

    private boolean firstAgentFailover;

    public DeployProcessorInput(boolean force, boolean undeploy, IProject project, String service, Map<String, String> agentRemapping, boolean importLoggedMessages) {
        this.force = force;
        this.undeploy = undeploy;
        this.project = project;
        this.service = service;
        this.agentRemapping = agentRemapping;
        this.importLoggedMessages = importLoggedMessages;

        this.firstAgentFailover = false;
    }

    public boolean isForce() {
        return force;
    }

    public boolean isUndeploy() {
        return undeploy;
    }

    public IProject getProject() {
        return project;
    }

    public String getService() {
        return service;
    }

    public Map<String, String> getAgentRemapping() {
        return agentRemapping;
    }

    public boolean isFirstAgentFailover() {
        return firstAgentFailover;
    }

    public boolean isAgentRemappingRequired() {
        return getAgentRemapping() != null && !getAgentRemapping().isEmpty();
    }

    public final void setFirstAgentFailover(boolean firstAgentFailover) {
        this.firstAgentFailover = firstAgentFailover;
    }

    public boolean isImportLoggedMessages() {
        return importLoggedMessages;
    }
}
