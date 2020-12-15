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
import com.microfocus.sv.svconfigurator.core.impl.jaxb.ServiceRuntimeConfiguration;
import com.microfocus.sv.svconfigurator.core.impl.jaxb.ServiceRuntimeReport;
import com.microfocus.sv.svconfigurator.core.impl.processor.Credentials;
import com.microfocus.sv.svconfigurator.processor.printer.IPrinter;
import com.microfocus.sv.svconfigurator.processor.printer.PrinterFactory;
import com.microfocus.sv.svconfigurator.serverclient.ICommandExecutor;
import com.microfocus.sv.svconfigurator.serverclient.impl.CommandExecutor;
import com.microfocus.sv.svconfigurator.util.AntTaskUtil;
import com.microfocus.sv.svconfigurator.util.HttpUtils;
import com.microfocus.sv.svconfigurator.util.ProjectUtils;

public class ViewTask extends Task {
    //============================== STATIC ATTRIBUTES ========================================

    //============================== INSTANCE ATTRIBUTES ======================================

    private String service;
    private String username;
    private String password;
    private String url;

    private File projectFile;

    private boolean report;
    private String projectPassword;
    private String outputFormat = PrinterFactory.getDefaultFormat();
    private boolean trustEveryone = false;

    //============================== STATIC METHODS ===========================================

    //============================== CONSTRUCTORS =============================================

    //============================== ABSTRACT METHODS =========================================

    //============================== OVERRIDEN METHODS ========================================

    @Override
    public void execute() throws BuildException {
        this.validate();

        URL mgmtUri = AntTaskUtil.createUri(this.url, null);
        Credentials cred = ProjectUtils.createCredentials(this.username, this.password);

        try {
            ICommandExecutor exec = new CommandExecutor(HttpUtils.createServerManagementEndpointClient(mgmtUri, trustEveryone, cred));

            IProject proj = this.projectFile != null ? AntTaskUtil.createProject(this.projectFile, this.projectPassword) : null;
            IService svc = exec.findService(this.service, proj);
            IPrinter printer = PrinterFactory.create(outputFormat);

            ServiceRuntimeConfiguration conf = exec.getServiceRuntimeInfo(svc);
            ServiceRuntimeReport rep = (report) ? exec.getServiceRuntimeReport(svc) : null;
            getProject().log(printer.createServiceInfoOutput(svc, conf, rep));

        } catch (CommunicatorException e) {
            throw new BuildException(e.getLocalizedMessage(), e);
        } catch (CommandExecutorException e) {
            throw new BuildException(e.getLocalizedMessage(), e);
        }
    }


    //============================== INSTANCE METHODS =========================================

    //============================== PRIVATE METHODS ==========================================

    private void validate() throws BuildException {
        if (this.service == null) {
            throw new BuildException("Service identification (service) has to be set.");
        }
        if (url == null) {
            throw new BuildException("Server management URL (url) have to be set.");
        }
    }

    //============================== GETTERS / SETTERS ========================================

    public void setService(String service) {
        this.service = service;
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

    public void setTrustEveryone(boolean trustEveryone) { this.trustEveryone = trustEveryone; }

    public void setReport(boolean report) {
        this.report = report;
    }

    public void setProjectFile(File projectFile) {
        this.projectFile = projectFile;
    }

    public void setProjectPassword(String projectPassword) {
        this.projectPassword = projectPassword;
    }

    public void setOutputFormat(String outputFormat) {
        this.outputFormat = outputFormat;
    }
    //============================== INNER CLASSES ============================================

}
