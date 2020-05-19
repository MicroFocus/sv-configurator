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

import com.microfocus.sv.svconfigurator.core.IProject;
import com.microfocus.sv.svconfigurator.core.IService;
import com.microfocus.sv.svconfigurator.core.impl.exception.CommandExecutorException;
import com.microfocus.sv.svconfigurator.core.impl.exception.CommunicatorException;
import com.microfocus.sv.svconfigurator.core.impl.processor.Credentials;
import com.microfocus.sv.svconfigurator.serverclient.ICommandExecutor;
import com.microfocus.sv.svconfigurator.serverclient.impl.CommandExecutor;
import com.microfocus.sv.svconfigurator.util.AntTaskUtil;
import com.microfocus.sv.svconfigurator.util.HttpUtils;
import com.microfocus.sv.svconfigurator.util.ProjectUtils;

/**
 * ANT Task to deployProject HPSV project
 */
public class DeployTask extends Task {
    
    //============================== STATIC ATTRIBUTES ========================================

    //============================== INSTANCE ATTRIBUTES ======================================

    private File projectFile;
    private String projectPassword;
    private boolean force;
    private boolean undeploy = false;
    private String url;
    private String username;
    private String password;
    private String service;

    //============================== STATIC METHODS ===========================================

    //============================== CONSTRUCTORS =============================================

    //============================== ABSTRACT METHODS =========================================

    //============================== OVERRIDEN METHODS ========================================

    @Override
    public void execute() throws BuildException {
        this.validateData();

        IProject proj = AntTaskUtil.createProject(this.projectFile, this.projectPassword);
        Credentials credentials = ProjectUtils.createCredentials(this.username, this.password);
        URL uri = AntTaskUtil.createUri(this.url, proj);

        try {
            ICommandExecutor exec = new CommandExecutor(HttpUtils.createServerManagementEndpointClient(uri, credentials));
            exec.setForce(this.force);

            IService svc = null;
            if (this.service != null) {
                svc = ProjectUtils.findProjElem(proj.getServices(), this.service, ProjectUtils.ENTITY_VIRTUAL_SERVICE);
            }

            if (this.undeploy) {
                if (svc == null) {
                    exec.undeploy(proj);
                } else {
                    exec.undeployService(svc);
                }
            } else {
                if (svc == null) {
                    exec.deployProject(proj);
                } else {
                    exec.deployService(svc, proj.getProjectPassword());
                }
            }
        } catch (CommunicatorException e) {
            throw new BuildException(e.getLocalizedMessage(), e);
        } catch (CommandExecutorException e) {
            throw new BuildException(e.getLocalizedMessage(), e);
        }
    }

    //============================== INSTANCE METHODS =========================================

    //============================== PRIVATE METHODS ==========================================

    private void validateData() throws BuildException {
        if (this.projectFile == null) {
            throw new BuildException("Project File (projectFile) has to be specified.");
        }
    }

    //============================== GETTERS / SETTERS ========================================

    public void setProjectFile(File projectFile) {
        this.projectFile = projectFile;
    }

    public void setForce(boolean force) {
        this.force = force;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUndeploy(boolean undeploy) {
        this.undeploy = undeploy;
    }

    public void setService(String service) {
        this.service = service;
    }

    public void setProjectPassword(String projectPassword) {
        this.projectPassword = projectPassword;
    }
    //============================== INNER CLASSES ============================================

}
