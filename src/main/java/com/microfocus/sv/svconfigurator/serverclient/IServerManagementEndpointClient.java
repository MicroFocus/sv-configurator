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
package com.microfocus.sv.svconfigurator.serverclient;

import java.net.URL;
import java.util.Collection;

import com.microfocus.sv.svconfigurator.core.*;
import com.microfocus.sv.svconfigurator.core.impl.exception.CommandExecutorException;
import com.microfocus.sv.svconfigurator.core.impl.exception.CommunicatorException;
import com.microfocus.sv.svconfigurator.core.impl.jaxb.*;
import com.microfocus.sv.svconfigurator.core.impl.jaxb.atom.ElemModelListAtom;
import com.microfocus.sv.svconfigurator.core.impl.jaxb.atom.ServiceListAtom;
import com.microfocus.sv.svconfigurator.core.impl.processor.ElementStatus;

public interface IServerManagementEndpointClient {
    
    ServerCompatibility getServerCompatibility();

    ServerInfo getServerInfo() throws CommunicatorException;
    
    AgentConfigurations getAgentConfigurations() throws CommunicatorException;

    ServiceListAtom getServiceList(String projectId) throws CommunicatorException;

    ServiceAnalysis getServiceAnalysis(IService svc) throws CommunicatorException;

    void setServiceAnalysis(IService svc, ServiceAnalysis sa) throws CommunicatorException;

    ElementStatus getServiceStatus(IService svc) throws CommunicatorException;

    ServiceRuntimeReport getServiceRuntimeReport(IService svc) throws CommunicatorException;

    void deployService(IService svc, String projectPassword) throws CommunicatorException;

    void updateService(IService svc, String projectPassword) throws CommunicatorException;

    void undeployService(String serviceId) throws CommunicatorException;

    ElementStatus getDataModelStatus(IDataModel dm) throws CommunicatorException;

    ElemModelListAtom getSvcDataModelAtom(IService svc) throws CommunicatorException;

    ElemModelListAtom getSvcPerfModelAtom(IService svc) throws CommunicatorException;

    void deployDataModel(IDataModel dm, String projectPassword) throws CommunicatorException;

    void updateDataModel(IDataModel dm, String projectPassword) throws CommunicatorException;

    ElementStatus getPerfModelStatus(IPerfModel pm) throws CommunicatorException;

    void deployPerfModel(IPerfModel pm, String projectPassword) throws CommunicatorException;

    void updatePerfModel(IPerfModel pm, String projectPassword) throws CommunicatorException;

    ElementStatus getServiceDescriptionStatus(IServiceDescription sd, IService svc) throws CommunicatorException;

    void deployServiceDescription(IServiceDescription sd, IService svc, String projectPassword) throws CommunicatorException;

    ElementStatus getContentFileStatus(IContentFile cf, IService svc) throws CommunicatorException;

    void deployContentFile(IContentFile cf, String projectPassword, IService svc) throws CommunicatorException;

    ElementStatus getDataSetStatus(IDataSet ds) throws CommunicatorException;

    long getDataSetHashCode(IDataSet ds) throws CommunicatorException;

    void deployDataSet(IDataSet ds, String projectPassword) throws CommunicatorException;

    void updateDataSet(IDataSet ds, String projectPassword) throws CommunicatorException;

    ServiceRuntimeConfiguration getServiceRuntimeConfiguration(IService svc) throws CommunicatorException;

    void setServiceRuntimeConfiguration(IService svc, ServiceRuntimeConfiguration configuration) throws CommunicatorException, CommandExecutorException;

    VirtualServiceLoggingConfiguration getVirtualServiceLoggingConfiguration(IService svc) throws CommunicatorException;

    void setVirtualServiceLoggingConfiguration(IService svc, VirtualServiceLoggingConfiguration value) throws CommunicatorException;

    void updateServiceDescription(IServiceDescription sd, IService svc, String projectPassword) throws CommunicatorException;

    void updateContentFile(IContentFile sd, String projectPassword, IService svc) throws CommunicatorException;

    byte[] fetchVirtualService(String vsId) throws CommunicatorException;
    byte[] fetchServiceDescription(String vsId, String sdId) throws CommunicatorException;
    byte[] fetchContentFile(String vsId, String sdId) throws CommunicatorException;
    byte[] fetchDataModel(String vsId, String dmId) throws CommunicatorException;
    byte[] fetchDataSet(String vsId, String dmId, String dsId) throws CommunicatorException;
    byte[] fetchPerformanceModel(String vsId, String pmId) throws CommunicatorException;
    
    Collection<String> getServiceDescriptionIds(String vsId) throws CommunicatorException;
    Collection<String> getDataModelIds(String vsId) throws CommunicatorException;
    Collection<String> getDataSetIds(String vsId, String dmId) throws CommunicatorException;
    Collection<String> getPerformanceModelIds(String vsId) throws CommunicatorException;
    
    URL getMgmtUri();
    IRestClient getClient();
}
