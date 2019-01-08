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
package com.microfocus.sv.svconfigurator.ant;

import java.io.File;
import java.net.URL;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import com.microfocus.sv.svconfigurator.LogConf;
import com.microfocus.sv.svconfigurator.core.IProject;
import com.microfocus.sv.svconfigurator.core.IService;
import com.microfocus.sv.svconfigurator.core.impl.exception.CommandExecutorException;
import com.microfocus.sv.svconfigurator.core.impl.exception.CommunicatorException;
import com.microfocus.sv.svconfigurator.core.impl.jaxb.ServiceRuntimeConfiguration;
import com.microfocus.sv.svconfigurator.core.impl.processor.Credentials;
import com.microfocus.sv.svconfigurator.serverclient.ICommandExecutor;
import com.microfocus.sv.svconfigurator.serverclient.impl.CommandExecutor;
import com.microfocus.sv.svconfigurator.util.AntTaskUtil;
import com.microfocus.sv.svconfigurator.util.HttpUtils;
import com.microfocus.sv.svconfigurator.util.ProjectUtils;
import com.microfocus.sv.svconfigurator.util.StringUtils;

/**
 * This ant task is used to change the mode of the service on Service Virtualization Server. The task can change the
 * mode, the data model and the performance model.
 */
public class ChmodeTask extends Task {
    //============================== STATIC ATTRIBUTES ========================================

    //============================== INSTANCE ATTRIBUTES ======================================

    private boolean force = false;
    private String dataModel;
    private String perfModel;
    private File projectFile;
    private String service;
    private String mode;
    private String username;
    private String password;
    private String url;
    private String projectPassword;

    //============================== STATIC METHODS ===========================================

    //============================== CONSTRUCTORS =============================================

    //============================== ABSTRACT METHODS =========================================

    //============================== OVERRIDEN METHODS ========================================

    @Override
    public void execute() throws BuildException {
        LogConf.configure();
        this.validateData();

        IProject proj = this.projectFile != null ? AntTaskUtil.createProject(this.projectFile, this.projectPassword) : null;
        Credentials credentials = ProjectUtils.createCredentials(this.username, this.password);
        URL uri = AntTaskUtil.createUri(this.url, proj);

        try {
            ICommandExecutor exec = new CommandExecutor(HttpUtils.createServerManagementEndpointClient(uri, credentials));
            exec.setForce(this.force);

            IService svc = proj == null
                    ? exec.findService(this.service, null)
                    : ProjectUtils.findProjElem(proj.getServices(), this.service, ProjectUtils.ENTITY_VIRTUAL_SERVICE);

            ServiceRuntimeConfiguration.RuntimeMode runtMode = this.translateMode(this.mode);

            ServiceRuntimeConfiguration conf = new ServiceRuntimeConfiguration(svc, runtMode, false,
                    ServiceRuntimeConfiguration.DeploymentState.READY);
            if (this.dataModel != null) {
                String dataModelId = ProjectUtils.findProjElem(svc.getDataModels(), this.dataModel,
                        ProjectUtils.ENTITY_DATA_MODEL).getId();
                conf.setDataModelId(dataModelId);
            }
            if (this.perfModel != null) {
                String perfModelId = ProjectUtils.findProjElem(svc.getPerfModels(), this.perfModel,
                        ProjectUtils.ENTITY_PERFORMANCE_MODEL).getId();
                conf.setPerfModelId(perfModelId);
            }

            exec.setServiceRuntime(svc, conf);
        } catch (CommunicatorException e) {
            throw new BuildException(e.getLocalizedMessage(), e);
        } catch (CommandExecutorException e) {
            throw new BuildException(e.getLocalizedMessage(), e);
        } catch (IllegalArgumentException e) {
            throw new BuildException(e.getLocalizedMessage(), e);
        }
    }


    //============================== INSTANCE METHODS =========================================

    //============================== PRIVATE METHODS ==========================================

    private void validateData() throws BuildException {
        if (this.service == null) {
            throw new BuildException("Service identification (service) has to be specified.");
        }
        if (this.mode == null) {
            throw new BuildException("Service mode (mode) has to be specified.");
        }
    }

    private ServiceRuntimeConfiguration.RuntimeMode translateMode(String mode) throws BuildException {
        try {
            return ServiceRuntimeConfiguration.RuntimeMode.valueOf(this.mode);
        } catch (IllegalArgumentException ex) {
            throw new BuildException("Mode " + mode + " is illegal. You have to use one of (" +
                    StringUtils.joinWithDelim(", ", ServiceRuntimeConfiguration.RuntimeMode.values()) + ")");
        }
    }

    //============================== GETTERS / SETTERS ========================================

    /**
     * Sets whether the force mode will be used. Force mode means that if a service whose mode we want to change is
     * locked by another username, the task will automatically unlock it. Use this mode only when you want what are you
     * doing because you can rewrite another username's data on the server.
     */
    public void setForce(boolean force) {
        this.force = force;
    }

    public void setDataModel(String dataModel) {
        this.dataModel = dataModel;
    }

    public void setPerfModel(String perfModel) {
        this.perfModel = perfModel;
    }

    public void setProjectFile(File projectFile) {
        this.projectFile = projectFile;
    }

    public void setService(String service) {
        this.service = service;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setProjectPassword(String projectPassword) {
        this.projectPassword = projectPassword;
    }
    //============================== INNER CLASSES ============================================

}
