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
package com.microfocus.sv.svconfigurator.ant;

import java.net.URL;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import com.microfocus.sv.svconfigurator.core.impl.exception.CommandExecutorException;
import com.microfocus.sv.svconfigurator.core.impl.exception.CommunicatorException;
import com.microfocus.sv.svconfigurator.core.impl.processor.Credentials;
import com.microfocus.sv.svconfigurator.processor.HotSwapProcessor;
import com.microfocus.sv.svconfigurator.processor.HotSwapProcessorInput;
import com.microfocus.sv.svconfigurator.processor.IHotSwapProcessor;
import com.microfocus.sv.svconfigurator.serverclient.impl.CommandExecutorFactory;
import com.microfocus.sv.svconfigurator.util.AntTaskUtil;
import com.microfocus.sv.svconfigurator.util.ProjectUtils;

/**
 * This ant task is used to change the mode of the service on Service
 * Virtualization Server. The task can change the mode, the data model and the
 * performance model.
 */
public class HotSwapTask extends Task {
    // ============================== STATIC ATTRIBUTES
    // ========================================

    // ============================== INSTANCE ATTRIBUTES
    // ======================================

    private boolean force = false;
    private String pm;
    private String service;
    private String username;
    private String password;
    private String url;
    private boolean trustEveryone = false;

    // ============================== STATIC METHODS
    // ===========================================

    // ============================== CONSTRUCTORS
    // =============================================

    // ============================== ABSTRACT METHODS
    // =========================================

    // ============================== OVERRIDEN METHODS
    // ========================================

    @Override
    public void execute() throws BuildException {
        this.validateData();

        Credentials credentials = ProjectUtils.createCredentials(this.username, this.password);
        URL uri = AntTaskUtil.createUri(this.url, null);

        try {
            IHotSwapProcessor proc = new HotSwapProcessor(new CommandExecutorFactory());
            
            HotSwapProcessorInput input = new HotSwapProcessorInput(this.force, service, this.pm);
            proc.process(input, proc.getCommandExecutorFactory().createCommandExecutor(uri, trustEveryone, credentials));
        } catch (CommunicatorException e) {
            throw new BuildException(e.getLocalizedMessage(), e);
        } catch (CommandExecutorException e) {
            throw new BuildException(e.getLocalizedMessage(), e);
        } catch (IllegalArgumentException e) {
            throw new BuildException(e.getLocalizedMessage(), e);
        }
    }

    // ============================== INSTANCE METHODS
    // =========================================

    // ============================== PRIVATE METHODS
    // ==========================================

    private void validateData() throws BuildException {
        if (this.service == null) {
            throw new BuildException("Service identification (service) has to be specified.");
        }
        if (this.url == null) {
            throw new BuildException("SV Server management URL have to be specified.");
        }
    }

    // ============================== GETTERS / SETTERS
    // ========================================

    /**
     * Sets whether the force mode will be used. Force mode means that if a
     * service whose mode we want to change is locked by another username, the
     * task will automatically unlock it. Use this mode only when you want what
     * are you doing because you can rewrite another username's data on the
     * server.
     */
    public void setForce(boolean force) {
        this.force = force;
    }

    public void setPm(String pm) {
        this.pm = pm;
    }

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
    
    // ============================== INNER CLASSES
    // ============================================

}
