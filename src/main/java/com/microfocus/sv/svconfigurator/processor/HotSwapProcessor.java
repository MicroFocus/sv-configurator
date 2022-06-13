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

import com.microfocus.sv.svconfigurator.core.IPerfModel;
import com.microfocus.sv.svconfigurator.core.IService;
import com.microfocus.sv.svconfigurator.core.impl.exception.CommandExecutorException;
import com.microfocus.sv.svconfigurator.core.impl.exception.CommunicatorException;
import com.microfocus.sv.svconfigurator.core.impl.jaxb.ServiceRuntimeConfiguration;
import com.microfocus.sv.svconfigurator.core.impl.jaxb.ServiceRuntimeConfiguration.DeploymentState;
import com.microfocus.sv.svconfigurator.core.impl.jaxb.ServiceRuntimeConfiguration.RuntimeMode;
import com.microfocus.sv.svconfigurator.serverclient.ICommandExecutor;
import com.microfocus.sv.svconfigurator.serverclient.ICommandExecutorFactory;
import com.microfocus.sv.svconfigurator.util.ProjectUtils;

public class HotSwapProcessor implements IHotSwapProcessor {

    private ICommandExecutorFactory commandExecutorFactory;

    public HotSwapProcessor(ICommandExecutorFactory commandExecutorFactory) {
        super();
        this.commandExecutorFactory = commandExecutorFactory;
    }

    @Override
    public ICommandExecutorFactory getCommandExecutorFactory() {
        return commandExecutorFactory;
    }
    
    @Override
    public void process(HotSwapProcessorInput input, ICommandExecutor exec) throws CommunicatorException, CommandExecutorException {
        exec.setForce(input.isForce());

        IService svc = exec.findService(input.getService(), null);
        String perfModelId = getPerformanceModelId(input, svc);
        ServiceRuntimeConfiguration rc = exec.getServiceRuntimeInfo(svc);

        this.validate(svc, rc, perfModelId);

        rc.setPerfModelId(perfModelId);

        exec.hotSwapServiceRuntime(svc, rc);
    }

    private String getPerformanceModelId(HotSwapProcessorInput input, IService svc) throws CommandExecutorException {
        return (input.getPerfModel() == null) ? null : ProjectUtils.findProjElem(svc.getPerfModels(), input.getPerfModel(),
                ProjectUtils.ENTITY_PERFORMANCE_MODEL).getId();
    }

    private void validate(IService svc, ServiceRuntimeConfiguration rc, String pmId) throws CommandExecutorException {
        if (!DeploymentState.READY.equals(rc.getDeploymentState()))
            throw new CommandExecutorException("Service is not deployed.");

        if (!this.isInSimulation(rc))
            throw new CommandExecutorException("Service is not in Simulation.");

        if (this.isPMOffline(svc, rc.getPerfModelId()))
            throw new CommandExecutorException("Services' Actual Performance Model is Offline.");
        if (this.isPMOffline(svc, pmId))
            throw new CommandExecutorException("Actual Services' Performance Model is Offline.");
    }

    private boolean isPMOffline(IService svc, String dataModelId) {
        if (dataModelId == null)
            return false; // null performance ID signalizes
                            // "maximal performance model"

        for (IPerfModel pm : svc.getPerfModels()) {
            if (pm.getId().equals(dataModelId))
                return pm.isOffline();
        }

        return true;
    }

    /**
     * Returns true if the service is in Simulation mode (from the User
     * perspective - like in Designer)
     */
    private boolean isInSimulation(ServiceRuntimeConfiguration rc) {
        return RuntimeMode.SIMULATING == rc.getDisplayRuntimeMode();
    }
}
