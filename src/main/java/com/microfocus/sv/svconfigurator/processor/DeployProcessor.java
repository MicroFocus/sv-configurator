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
package com.microfocus.sv.svconfigurator.processor;

import java.util.ArrayList;
import java.util.List;

import com.microfocus.sv.svconfigurator.core.impl.jaxb.AgentConfigurations;
import com.microfocus.sv.svconfigurator.service.ServiceAmendingServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.microfocus.sv.svconfigurator.core.IProject;
import com.microfocus.sv.svconfigurator.core.IService;
import com.microfocus.sv.svconfigurator.core.impl.exception.CommandExecutorException;
import com.microfocus.sv.svconfigurator.core.impl.exception.CommunicatorException;
import com.microfocus.sv.svconfigurator.serverclient.ICommandExecutor;
import com.microfocus.sv.svconfigurator.serverclient.ICommandExecutorFactory;
import com.microfocus.sv.svconfigurator.util.ProjectUtils;

public class DeployProcessor implements IDeployProcessor {
    
    private static final Logger LOG = LoggerFactory.getLogger(DeployProcessor.class);

    private final ICommandExecutorFactory commandExecutorFactory;
    
    public DeployProcessor(ICommandExecutorFactory commandExecutorFactory) {
        this.commandExecutorFactory = commandExecutorFactory;
    }
    
    @Override
    public ICommandExecutorFactory getCommandExecutorFactory() {
        return commandExecutorFactory;
    }

    @Override
    public void process(DeployProcessorInput input, ICommandExecutor exec) throws CommunicatorException, CommandExecutorException {
        IProject proj = input.getProject();
        if (proj == null) {
            throw new CommandExecutorException("You have to specify the project.");
        }

        exec.setForce(input.isForce());

        IService svc = ProjectUtils.findProjElem(proj.getServices(), input.getService(), ProjectUtils.ENTITY_VIRTUAL_SERVICE);

        if (input.isUndeploy()) {
            this.undeploy(proj, exec, svc);
        } else {
            deploy(proj, exec, svc, input);
        }
    }

    private void deploy(IProject proj, ICommandExecutor exec, IService service, DeployProcessorInput input) throws CommunicatorException, CommandExecutorException {
        List<IService> services = new ArrayList<IService>();
        if (service == null) {
            for (IService svc : proj.getServices()) {
                services.add(svc);
            }
        } else {
            services.add(service);
        }

        List<IService> updatedServices = updateAndValidateServiceAgents(exec, input, services);

        for (IService svc : updatedServices) {
            exec.deployService(svc, proj.getProjectPassword(), input.isImportLoggedMessages());
        }
        if (service == null) {
            LOG.info(proj + " successfully deployed.");
        }
    }

    private List<IService> updateAndValidateServiceAgents(ICommandExecutor exec, DeployProcessorInput input, List<IService> services) throws CommunicatorException, CommandExecutorException {
        AgentConfigurations agentConfigurations = exec.getAgents();
        ServiceAmendingServiceImpl serviceAmendingService = new ServiceAmendingServiceImpl(agentConfigurations, services);

        if (input.isAgentRemappingRequired()) {
            serviceAmendingService.remapAgents(input.getAgentRemapping());
        }

        serviceAmendingService.remapAgentsByNames();

        if (input.isFirstAgentFailover()) {
            serviceAmendingService.agentFallback();
        }

        serviceAmendingService.verifyAndSetNames();

        return serviceAmendingService.applyAgentChanges();
    }

    private void undeploy(IProject proj, ICommandExecutor exec, IService svc) throws CommunicatorException, CommandExecutorException {
        if (svc == null) {
            exec.undeploy(proj);
        } else {
            exec.undeployService(svc);
        }
    }

}
