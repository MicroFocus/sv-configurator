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
import java.util.Collections;
import java.util.List;

import com.microfocus.sv.svconfigurator.core.IDataModel;
import com.microfocus.sv.svconfigurator.core.IPerfModel;
import com.microfocus.sv.svconfigurator.core.IProject;
import com.microfocus.sv.svconfigurator.core.IService;
import com.microfocus.sv.svconfigurator.core.impl.exception.CommandExecutorException;
import com.microfocus.sv.svconfigurator.core.impl.exception.CommunicatorException;
import com.microfocus.sv.svconfigurator.core.impl.exception.SVCParseException;
import com.microfocus.sv.svconfigurator.core.impl.jaxb.ServiceRuntimeConfiguration;
import com.microfocus.sv.svconfigurator.core.impl.jaxb.ServiceRuntimeConfiguration.RuntimeMode;
import com.microfocus.sv.svconfigurator.serverclient.ICommandExecutor;
import com.microfocus.sv.svconfigurator.serverclient.ICommandExecutorFactory;
import com.microfocus.sv.svconfigurator.util.ProjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChmodeProcessor implements IChmodeProcessor {
    private static final Logger LOG = LoggerFactory.getLogger(ChmodeProcessor.class);

    private ICommandExecutorFactory commandExecutorFactory;

    public ChmodeProcessor(ICommandExecutorFactory commandExecutorFactory) {
        this.commandExecutorFactory = commandExecutorFactory;
    }

    public ICommandExecutorFactory getCommandExecutorFactory() {
        return commandExecutorFactory;
    }

    @Override
    public void process(ChmodeProcessorInput input, ICommandExecutor exec)
            throws CommandExecutorException, SVCParseException, CommunicatorException {
        exec.setForce(input.isForce());

        IProject proj = input.getProject();
        IService svc = (proj == null) ? exec.findService(input.getService(), null)
                : ProjectUtils.findProjElem(proj.getServices(), input.getService(), ProjectUtils.ENTITY_VIRTUAL_SERVICE);

        ServiceRuntimeConfiguration conf = new ServiceRuntimeConfiguration(svc,
                input.getServiceMode(), false,
                ServiceRuntimeConfiguration.DeploymentState.READY);
        String dataModel = input.getDataModel();
        String perfModel = input.getPerfModel();

        if (dataModel != null) {
            String dataModelId = ProjectUtils.findProjElem(svc.getDataModels(), dataModel, ProjectUtils.ENTITY_DATA_MODEL).getId();
            conf.setDataModelId(dataModelId);
        }

        if (perfModel != null) {
            IPerfModel model = ProjectUtils.findProjElem(svc.getPerfModels(), perfModel, ProjectUtils.ENTITY_DATA_MODEL);
            if (model.isOffline() && input.getServiceMode() == RuntimeMode.LEARNING) {
                throw new CommandExecutorException("Offline performance model cannot be selected in the LEARNING mode.");
            }
            conf.setPerfModelId(model.getId());
        }

        // Simulating or learning and no data model selected --> select the first DM (sorted
        // by name) automatically
        if (dataModel == null && input.isDefaultDataModel()
                && (input.getServiceMode() == RuntimeMode.SIMULATING
                || input.getServiceMode() == RuntimeMode.LEARNING)) {
            setDefaultDataModel(svc, conf);
        }

        if (perfModel == null && input.isDefaultPerfModel()
                && (input.getServiceMode() == RuntimeMode.SIMULATING
                || input.getServiceMode() == RuntimeMode.STAND_BY
                || input.getServiceMode() == RuntimeMode.LEARNING)) {
            setDefaultPerfModel(svc, conf);
        }

        if (conf.getDisplayRuntimeMode() != input.getDesiredServiceMode()) {
            LOG.warn("Target service mode " + input.getDesiredServiceMode() + " has been translated to " + conf.getDisplayRuntimeMode()
                    + " to be in accord with Service Virtualization Designer. Feel free to use " + conf.getDisplayRuntimeMode() + " mode directly.");
        }

        exec.setServiceRuntime(svc, conf);
    }

    private void setDefaultDataModel(IService svc, ServiceRuntimeConfiguration conf) {
        List<IDataModel> dms = new ArrayList<IDataModel>(svc.getDataModels());
        if (!dms.isEmpty()) {
            Collections.sort(dms);
            String dataModelId = dms.get(0).getId();
            conf.setDataModelId(dataModelId);
        }
    }

    private void setDefaultPerfModel(IService svc, ServiceRuntimeConfiguration conf) {
        List<IPerfModel> pms = new ArrayList<IPerfModel>(svc.getPerfModels());

        if (!pms.isEmpty()) {
            Collections.sort(pms);
            for (IPerfModel pm : pms) {
                if (!pm.isOffline()) {
                    conf.setPerfModelId(pm.getId());
                    break;
                }
            }
        }
    }
}
