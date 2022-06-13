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
package com.microfocus.sv.svconfigurator.serverclient;

import java.io.IOException;
import java.util.Collection;

import com.microfocus.sv.svconfigurator.core.IDataModel;
import com.microfocus.sv.svconfigurator.core.IPerfModel;
import com.microfocus.sv.svconfigurator.core.IProject;
import com.microfocus.sv.svconfigurator.core.IService;
import com.microfocus.sv.svconfigurator.core.impl.exception.CommandExecutorException;
import com.microfocus.sv.svconfigurator.core.impl.exception.CommunicatorException;
import com.microfocus.sv.svconfigurator.core.impl.jaxb.AgentConfigurations;
import com.microfocus.sv.svconfigurator.core.impl.jaxb.ServiceRuntimeConfiguration;
import com.microfocus.sv.svconfigurator.core.impl.jaxb.ServiceRuntimeReport;
import com.microfocus.sv.svconfigurator.core.impl.jaxb.atom.ServiceListAtom;

public interface ICommandExecutor {

    void deployProject(IProject project) throws CommunicatorException, CommandExecutorException;

    void deployService(IService svc, String projectPassword) throws CommunicatorException, CommandExecutorException;

    void deployService(IService svc, String projectPassword, boolean importLoggedMessages) throws CommunicatorException, CommandExecutorException;

    void undeploy(IProject project) throws CommunicatorException, CommandExecutorException;

    void undeployService(IService svc) throws CommunicatorException, CommandExecutorException;

    IService findService(String svcIdent, IProject proj) throws CommunicatorException, CommandExecutorException;

    Collection<IService> findServices(String svcIdent, IProject proj) throws CommunicatorException;

    Collection<IDataModel> findDataModels(IService svc) throws CommunicatorException;

    Collection<IPerfModel> findPerfModels(IService svc) throws CommunicatorException;

    void lockService(IService svc, String clientId) throws CommunicatorException, CommandExecutorException;

    void changeVirtualServiceLoggingConfiguration(IService svc, boolean isEnabled) throws CommunicatorException, CommandExecutorException;

    void setServiceRuntime(IService svc, ServiceRuntimeConfiguration runtConf) throws CommunicatorException, CommandExecutorException;
    
    void hotSwapServiceRuntime(IService svc, ServiceRuntimeConfiguration runtConf) throws CommunicatorException, CommandExecutorException;
    
    AgentConfigurations getAgents() throws CommunicatorException, CommandExecutorException;

    ServiceRuntimeConfiguration getServiceRuntimeInfo(IService svc) throws CommunicatorException;

    ServiceRuntimeReport getServiceRuntimeReport(IService svc) throws CommunicatorException;

    ServiceListAtom getServiceList(String projectId) throws CommunicatorException;

    boolean isForce();

    void setForce(boolean force);
    
    IServerManagementEndpointClient getClient();
}
