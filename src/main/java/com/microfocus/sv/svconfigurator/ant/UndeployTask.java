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
import com.microfocus.sv.svconfigurator.core.impl.exception.CommandExecutorException;
import com.microfocus.sv.svconfigurator.core.impl.exception.CommunicatorException;
import com.microfocus.sv.svconfigurator.core.impl.processor.Credentials;
import com.microfocus.sv.svconfigurator.processor.IUndeployProcessor;
import com.microfocus.sv.svconfigurator.processor.UndeployProcessor;
import com.microfocus.sv.svconfigurator.processor.UndeployProcessorInput;
import com.microfocus.sv.svconfigurator.serverclient.impl.CommandExecutorFactory;
import com.microfocus.sv.svconfigurator.util.AntTaskUtil;
import com.microfocus.sv.svconfigurator.util.ProjectUtils;

public class UndeployTask extends Task {

    private boolean force = false;
    private String service;
    private File projectFile;
    private String projectPassword;
    private String username;
    private String password;
    private String url;

    @Override
    public void execute() throws BuildException {
        this.validateData();

        IProject proj = this.projectFile == null ? null : AntTaskUtil.createProject(this.projectFile, this.projectPassword);
        Credentials credentials = ProjectUtils.createCredentials(this.username, this.password);
        URL uri = AntTaskUtil.createUri(this.url, proj);

        try {
            IUndeployProcessor proc = new UndeployProcessor(new CommandExecutorFactory());

            UndeployProcessorInput input = new UndeployProcessorInput(this.force, proj, service);
            proc.process(input, proc.getCommandExecutorFactory().createCommandExecutor(uri, credentials));
        } catch (CommunicatorException e) {
            throw new BuildException(e.getLocalizedMessage(), e);
        } catch (CommandExecutorException e) {
            throw new BuildException(e.getLocalizedMessage(), e);
        } catch (IllegalArgumentException e) {
            throw new BuildException(e.getLocalizedMessage(), e);
        }
    }

    private void validateData() throws BuildException {
        if (this.service == null && this.projectFile == null) {
            throw new BuildException("Either a service or a project has to be specified.");
        }

        if (this.url == null) {
            throw new BuildException("SV Server management URL have to be specified.");
        }
    }

    public void setForce(boolean force) {
        this.force = force;
    }

    public void setService(String service) {
        this.service = service;
    }

    public void setProjectFile(File projectFile) {
        this.projectFile = projectFile;
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

}
