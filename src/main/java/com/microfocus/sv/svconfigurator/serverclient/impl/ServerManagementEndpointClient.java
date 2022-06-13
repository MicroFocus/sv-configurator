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
package com.microfocus.sv.svconfigurator.serverclient.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import com.microfocus.sv.svconfigurator.core.*;
import com.microfocus.sv.svconfigurator.core.impl.jaxb.*;
import com.microfocus.sv.svconfigurator.serverclient.FileInfo;
import org.apache.commons.io.IOUtils;
import org.apache.http.entity.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import com.microfocus.sv.svconfigurator.core.impl.exception.CommandExecutorException;
import com.microfocus.sv.svconfigurator.core.impl.exception.CommunicatorException;
import com.microfocus.sv.svconfigurator.core.impl.exception.SVCParseException;
import com.microfocus.sv.svconfigurator.core.impl.jaxb.atom.DataSetEntry;
import com.microfocus.sv.svconfigurator.core.impl.jaxb.atom.ElemModelListAtom;
import com.microfocus.sv.svconfigurator.core.impl.jaxb.atom.ServiceListAtom;
import com.microfocus.sv.svconfigurator.core.impl.processor.ElementStatus;
import com.microfocus.sv.svconfigurator.serverclient.IRestClient;
import com.microfocus.sv.svconfigurator.serverclient.IServerManagementEndpointClient;
import com.microfocus.sv.svconfigurator.serverclient.ServerCompatibility;
import com.microfocus.sv.svconfigurator.util.URIUtil;

/**
 * This class is responsible for communication with server management endpoint.
 * It executes single operation as e.g. call the /ping endpoint of put something
 * into services/{serviceId}.
 */
public class ServerManagementEndpointClient implements
        IServerManagementEndpointClient {

    // ============================== STATIC ATTRIBUTES
    // ========================================
    private static final String SV_VERSION = "3.0";

    private static final String REL_URL_PING = "ping";
    private static final String REL_URL_INFO = "info";
    private static final String REL_URL_AGENT_CONFIG = "agents/configurations";
    private static final String REL_URL_AGENT_CAPABILITIES = "agents/agentCapabilities";
    private static final String REL_URL_SERVICE = "services/%s?alt=xml";
    private static final String REL_URL_SERVICES = "services";
    private static final String REL_URL_SERVICE_LIST = "services?onlyActiveServices=false&projectIds=%s";
    private static final String REL_URL_SERVICE_ANALYSIS = "services/%s/analysis";
    private static final String REL_URL_3_01_SERVICE_REPORT = "services/%s/report";
    private static final String REL_URL_3_10_SERVICE_REPORT = "services/%s/runtimeReport";
    private static final String REL_URL_3_61_SERVICE_REPORT = "runtimeReports?virtualServiceIds=%s&onlyActiveServices=false";
    private static final String REL_URL_DATA_MODELS_LIST_ATOM = "dataModels?virtualServiceIds=%s&onlyActiveServices=false";
    private static final String REL_URL_DATA_MODELS = "services/%s/dataModel";
    private static final String REL_URL_GET_DATA_MODELS = "services/%s/dataModels?alt=xml";
    private static final String REL_URL_DATA_MODEL = "services/%s/dataModel/%s?alt=xml";
    private static final String REL_URL_PERF_MODELS = "services/%s/performanceModel";
    private static final String REL_URL_PERF_MODELS_LIST_ATOM = "performanceModels?virtualServiceIds=%s&onlyActiveServices=false";
    private static final String REL_URL_PERF_MODEL = "services/%s/performanceModel/%s?alt=xml";
    private static final String REL_URL_GET_PERF_MODELS = "services/%s/performanceModels?alt=xml";
    private static final String REL_URL_SVC_DESCS = "services/%s/serviceDescriptions";
    private static final String REL_URL_SVC_DESC = "services/%s/serviceDescriptions/%s?alt=xml";
    private static final String REL_URL_GET_SVC_DESCS = "serviceDescriptions?virtualServiceIds=%s";
    private static final String REL_URL_DATA_SETS = "services/%s/dataModel/%s/dataset";
    private static final String REL_URL_DATA_SET = "services/%s/dataModel/%s/dataset/%s?alt=xml";
    private static final String REL_URL_DATA_SET_ATOM = "services/%s/dataModel/%s/dataset/%s";
    private static final String REL_URL_GET_DATA_SETS = "services/%s/dataModel/%s/dataset";
    private static final String REL_URL_SVC_RUNTIME_CONF = "services/%s/runtimeConfiguration/";
    private static final String REL_URL_SVC_LOGGING_CONF = "services/%s/loggingConfiguration/";
    private static final String REL_URL_CONTENT_FILE = "services/%s/contentFiles";
    private static final String REL_URL_CONTENT_FILES = "services/%s/contentFiles/%s?alt=xml";
    private static final String REL_URL_CONTENT_FILES_INFO = "services/%s/contentFiles/%s";
    private static final String REL_URL_LOGGED_MESSAGES = "messageLogger/%s/impexp";
    private static final String REL_URL_LOGGED_MESSAGES_FROM_ID_LIMIT = REL_URL_LOGGED_MESSAGES + "?from_id=%s&limit=%s";

    private static final Logger LOG = LoggerFactory
            .getLogger(ServerManagementEndpointClient.class);

    private String REL_URL_SERVICE_REPORT = REL_URL_3_10_SERVICE_REPORT;
    // ============================== INSTANCE ATTRIBUTES
    // ======================================

    private final ServerCompatibility serverCompatibility;

    private final URL mgmtUri;
    private final IRestClient client;
    private final ServerInfo serverInfo;

    private final DocumentBuilderFactory docFactory;
    private final ThreadLocal<DocumentBuilder> threadLocalDocBuilder = new ThreadLocal<DocumentBuilder>();

    // ============================== STATIC METHODS
    // ===========================================

    public ServerManagementEndpointClient(URL mgmtUrl, IRestClient client)
            throws CommunicatorException {
        if (!"http".equalsIgnoreCase(mgmtUrl.getProtocol())
                && !"https".equalsIgnoreCase(mgmtUrl.getProtocol())) {
            throw new CommunicatorException(
                    "Management URL is not specified correctly (example: https://localhost:6085/management)");
        }

        this.mgmtUri = URIUtil.makeBase(mgmtUrl);
        this.client = client;

        this.pingServer();

        serverInfo = getServerInfoImpl();

        if (SV_VERSION.equals(serverInfo.getServerVersion())) {
            String[] prodVersionStr = serverInfo.getProductVersion().split("\\."); // prod
                                                                            // version
                                                                            // is
                                                                            // e.g.
                                                                            // 3.10.9.99
            int[] prodVersion = new int[prodVersionStr.length];
            for (int i = 0; i < prodVersion.length; i++) {
                prodVersion[i] = Integer.valueOf(prodVersionStr[i]);
            }

            if ((prodVersion[0] == 3 && prodVersion[1] >= 61) || prodVersion[0] >= 4) { // 3.61+
                this.REL_URL_SERVICE_REPORT = REL_URL_3_61_SERVICE_REPORT;
                this.serverCompatibility = ServerCompatibility.SV_3_61_PLUS;
            } else if (prodVersion[0] == 3 && prodVersion[1] >= 10) { // 3.10
                this.REL_URL_SERVICE_REPORT = REL_URL_3_10_SERVICE_REPORT;
                this.serverCompatibility = ServerCompatibility.SV_3_10;
            } else if (prodVersion[0] == 3 && prodVersion[1] < 10) {
                this.REL_URL_SERVICE_REPORT = REL_URL_3_01_SERVICE_REPORT;
                this.serverCompatibility = ServerCompatibility.SV_3_10_LESS;
            } else {
                throw new CommunicatorException(
                        "Server's product version you are using ("
                                + serverInfo.getProductVersion()
                                + ") is not supported by this tool.");
            }
        } else {
            throw new CommunicatorException("Server's version you are using ("
                    + serverInfo.getServerVersion()
                    + ") is not supported by this tool.");
        }

        docFactory = DocumentBuilderFactory.newInstance();
    }
    
    public URL getMgmtUri() {
        return mgmtUri;
    }
    
    public IRestClient getClient() {
        return client;
    }
    
    private DocumentBuilder getLocalBuilder() {
        try {
            DocumentBuilder builder = threadLocalDocBuilder.get();
            if (builder == null) {
                builder = docFactory.newDocumentBuilder();
                threadLocalDocBuilder.set(builder);
            }
            return builder;
        } catch (ParserConfigurationException e) {
            throw new IllegalStateException(e);
        }
    }

    public Document deserializeXml(byte[] data)
            throws ParserConfigurationException, SAXException, IOException {
        ByteArrayInputStream bis = new ByteArrayInputStream(data);
        try {
            return getLocalBuilder().parse(bis);
        } finally {
            IOUtils.closeQuietly(bis);
        }
    }

    // ============================== CONSTRUCTORS
    // =============================================

    // ============================== ABSTRACT METHODS
    // =========================================

    // ============================== OVERRIDEN METHODS
    // ========================================

    // ============================== INSTANCE METHODS
    // =========================================

    @Override
    public ServerCompatibility getServerCompatibility() {
        return this.serverCompatibility;
    }

    /**
     * Returns the server info - management URL: /info
     */
    @Override
    public ServerInfo getServerInfo() {
        return serverInfo;
    }

    private ServerInfo getServerInfoImpl() throws CommunicatorException {
        URI infoUri = this.resolveRelativeUri(REL_URL_INFO);
        return this.client.get(infoUri, ContentType.APPLICATION_ATOM_XML, ServerInfo.class);
    }

    /**
     * Returns the agent configurations
     */
    @Override
    public AgentConfigurations getAgentConfigurations()
            throws CommunicatorException {

        URI configUrl = this.resolveRelativeUri(REL_URL_AGENT_CONFIG);
        AgentConfigurations agentConfigurations = this.client.get(configUrl, ContentType.APPLICATION_XML, AgentConfigurations.class);

        // set isRunning property based on existing capability
        URI capabilityUrl = this.resolveRelativeUri(REL_URL_AGENT_CAPABILITIES);
        AgentCapabilities agentCapabilities =  this.client.get(capabilityUrl, ContentType.APPLICATION_XML, AgentCapabilities.class);

        Set<String> enabledAgents = new HashSet<String>();
        for (AgentCapability cap : agentCapabilities.getCapabilities()) {
            enabledAgents.add(cap.getAgentId());
        }

        for (AgentConfiguration conf : agentConfigurations.getConfigurations()) {
            conf.setRunning(enabledAgents.contains(conf.getAgentId()));
        }

        return agentConfigurations;
    }

    /**
     * Returns the service list.
     * 
     * @param projectId
     *            Project to search services in (can be null - all the services
     *            will be returned)
     * @throws CommunicatorException
     */
    @Override
    public ServiceListAtom getServiceList(String projectId)
            throws CommunicatorException {
        URI uri;
        try {
            uri = this.resolveRelativeUri(String.format(REL_URL_SERVICE_LIST,
                    projectId != null ? URLEncoder.encode(projectId, "UTF-8")
                            : ""));
        } catch (UnsupportedEncodingException e) {
            throw new CommunicatorException("URL encoding exception: "
                    + e.getLocalizedMessage(), e);
        }
        return this.client.get(uri, ContentType.APPLICATION_ATOM_XML,
                ServiceListAtom.class);
    }

    @Override
    public ServiceAnalysis getServiceAnalysis(IService svc)
            throws CommunicatorException {
        URI uri = this.resolveRelativeUri(String.format(
                REL_URL_SERVICE_ANALYSIS, svc.getId()));
        return this.client.get(uri, ServiceAnalysis.class);
    }

    @Override
    public void setServiceAnalysis(IService svc, ServiceAnalysis sa)
            throws CommunicatorException {
        URI uri = this.resolveRelativeUri(String.format(
                REL_URL_SERVICE_ANALYSIS, svc.getId()));
        this.client.put(uri, sa);
    }

    /**
     * Obtains the service status (if it exists on the server or not)
     * 
     * @throws CommunicatorException
     */
    @Override
    public ElementStatus getServiceStatus(IService svc)
            throws CommunicatorException {
        URI svcUri = this.resolveRelativeUri(String.format(REL_URL_SERVICE,
                svc.getId()));

        return this.client.getStatus(svcUri);
    }

    @Override
    public ServiceRuntimeReport getServiceRuntimeReport(IService svc)
            throws CommunicatorException {
        URI svcUri = this.resolveRelativeUri(String.format(
                REL_URL_SERVICE_REPORT, svc.getId()));
        if (REL_URL_SERVICE_REPORT.equals(REL_URL_3_61_SERVICE_REPORT)) {
            ServiceRuntimeReports reports = this.client.get(svcUri,
                    ContentType.APPLICATION_XML, ServiceRuntimeReports.class);
            if (reports != null && reports.getRuntimeReports() != null) {
                return reports.getRuntimeReports().get(0);
            } else {
                return new ServiceRuntimeReport();
            }
        } else {
            return this.client.get(svcUri, ContentType.APPLICATION_XML,
                    ServiceRuntimeReport.class);
        }
    }

    /**
     * Deploys the service (POST it) on the server
     * 
     * @throws CommunicatorException
     */
    @Override
    public void deployService(IService svc, String projectPassword)
            throws CommunicatorException {
        URI svcUri = this.resolveRelativeUri(REL_URL_SERVICES);
        this.deployElement(svc, svcUri);
    }

    @Override
    public void updateService(IService svc, String projectPassword)
            throws CommunicatorException {
        URI svcUri = this.resolveRelativeUri(String.format(REL_URL_SERVICE,
                svc.getId()));
        this.updateElement(svc, svcUri);
    }

    @Override
    public void undeployService(String serviceId) throws CommunicatorException {
        URI svcUri = this.resolveRelativeUri(String.format(REL_URL_SERVICE,
                serviceId));
        this.client.delete(svcUri);
    }

    @Override
    public ElementStatus getDataModelStatus(IDataModel dm)
            throws CommunicatorException {
        IService svc = dm.getService();
        URI dmUri = this.resolveRelativeUri(String.format(REL_URL_DATA_MODEL,
                svc.getId(), dm.getId()));

        return this.client.getStatus(dmUri);
    }

    @Override
    public ElemModelListAtom getSvcDataModelAtom(IService svc)
            throws CommunicatorException {
        URI u = this.resolveRelativeUri(String.format(
                REL_URL_DATA_MODELS_LIST_ATOM, svc.getId()));
        return this.client.get(u, ContentType.APPLICATION_ATOM_XML,
                ElemModelListAtom.class);
    }

    @Override
    public ElemModelListAtom getSvcPerfModelAtom(IService svc)
            throws CommunicatorException {
        URI u = this.resolveRelativeUri(String.format(
                REL_URL_PERF_MODELS_LIST_ATOM, svc.getId()));
        return this.client.get(u, ContentType.APPLICATION_ATOM_XML,
                ElemModelListAtom.class);
    }

    /**
     * Calls POST on service management endpoint
     * 
     * @throws CommunicatorException
     */
    @Override
    public void deployDataModel(IDataModel dm, String projectPassword)
            throws CommunicatorException {
        IService svc = dm.getService();
        URI uri = this.resolveRelativeUri(String.format(REL_URL_DATA_MODELS,
                svc.getId()));
        this.deployElement(dm, uri);
    }

    /**
     * Calls PUT on service management endpoint
     * 
     * @throws CommunicatorException
     */
    @Override
    public void updateDataModel(IDataModel dm, String projectPassword)
            throws CommunicatorException {
        IService svc = dm.getService();
        URI uri = this.resolveRelativeUri(String.format(REL_URL_DATA_MODEL,
                svc.getId(), dm.getId()));
        this.updateElement(dm, uri);
    }

    @Override
    public ElementStatus getPerfModelStatus(IPerfModel pm)
            throws CommunicatorException {
        IService svc = pm.getService();
        URI uri = this.resolveRelativeUri(String.format(REL_URL_PERF_MODEL,
                svc.getId(), pm.getId()));
        return this.client.getStatus(uri);
    }

    @Override
    public void deployPerfModel(IPerfModel pm, String projectPassword)
            throws CommunicatorException {
        IService svc = pm.getService();
        URI uri = this.resolveRelativeUri(String.format(REL_URL_PERF_MODELS,
                svc.getId()));

        this.deployElement(pm, uri);
    }

    @Override
    public void updatePerfModel(IPerfModel pm, String projectPassword)
            throws CommunicatorException {
        IService svc = pm.getService();
        URI uri = this.resolveRelativeUri(String.format(REL_URL_PERF_MODEL,
                svc.getId(), pm.getId()));
        this.updateElement(pm, uri);
    }

    @Override
    public ElementStatus getServiceDescriptionStatus(IServiceDescription sd, IService svc)
            throws CommunicatorException {
        URI uri = this.resolveRelativeUri(String.format(REL_URL_SVC_DESC,
                svc.getId(), sd.getId()));

        return this.client.getStatus(uri);
    }

    @Override
    public void deployServiceDescription(IServiceDescription sd, IService svc, String projectPassword) throws CommunicatorException {
        URI uri = this.resolveRelativeUri(String.format(REL_URL_SVC_DESCS,
                svc.getId()));

        this.deployElement(sd, uri);
    }

    @Override
    public void updateServiceDescription(IServiceDescription sd, IService svc, String projectPassword) throws CommunicatorException {
        URI uri = this.resolveRelativeUri(String.format(REL_URL_SVC_DESC,
                svc.getId(), sd.getId()));

        this.updateElement(sd, uri);
    }

    @Override
    public ElementStatus getContentFileStatus(IContentFile cf, IService svc)
            throws CommunicatorException {
        URI uri = this.resolveRelativeUri(String.format(REL_URL_CONTENT_FILES_INFO,
                svc.getId(), cf.getId()));

        return this.client.getStatus(uri);
    }

    @Override
    public void deployContentFile(IContentFile cf,
                                  String projectPassword, IService svc) throws CommunicatorException {
        URI uri = this.resolveRelativeUri(String.format(REL_URL_CONTENT_FILE,
                svc.getId()));

        this.deployElement(cf, uri);
    }

    @Override
    public void updateContentFile(IContentFile cf,
                                  String projectPassword, IService svc) throws CommunicatorException {
        URI uri = this.resolveRelativeUri(String.format(REL_URL_CONTENT_FILES,
                svc.getId(), cf.getId()));

        this.updateElement(cf, uri);
    }

    @Override
    public ElementStatus getDataSetStatus(IDataSet ds)
            throws CommunicatorException {
        IDataModel dm = ds.getDataModel();
        IService svc = dm.getService();
        URI uri = this.resolveRelativeUri(String.format(REL_URL_DATA_SET,
                svc.getId(), dm.getId(), ds.getId()));
        return this.client.getStatus(uri);
    }

    /**
     * Obtains the Data Set hashCode (Version)
     * 
     * @throws CommunicatorException
     */
    @Override
    public long getDataSetHashCode(IDataSet ds) throws CommunicatorException {
        IDataModel dm = ds.getDataModel();
        IService s = dm.getService();
        URI u = this.resolveRelativeUri(String.format(REL_URL_DATA_SET_ATOM,
                s.getId(), dm.getId(), ds.getId()));

        DataSetEntry dsEntry = this.client.get(u,
                ContentType.APPLICATION_ATOM_XML, DataSetEntry.class);
        return dsEntry.getVersion();
    }

    @Override
    public void deployDataSet(IDataSet ds, String projectPassword)
            throws CommunicatorException {
        IDataModel dm = ds.getDataModel();
        IService svc = dm.getService();
        URI uri = this.resolveRelativeUri(String.format(REL_URL_DATA_SETS,
                svc.getId(), dm.getId()));

        this.deployElement(ds, uri);
    }

    @Override
    public void updateDataSet(IDataSet ds, String projectPassword)
            throws CommunicatorException {
        IDataModel dm = ds.getDataModel();
        IService svc = dm.getService();
        URI uri = this.resolveRelativeUri(String.format(REL_URL_DATA_SET,
                svc.getId(), dm.getId(), ds.getId()));

        this.updateElement(ds, uri);
    }

    @Override
    public ServiceRuntimeConfiguration getServiceRuntimeConfiguration(
            IService svc) throws CommunicatorException {
        URI uri = this.resolveRelativeUri(String.format(
                REL_URL_SVC_RUNTIME_CONF, svc.getId()));
        return this.client.get(uri, ContentType.APPLICATION_XML,
                ServiceRuntimeConfiguration.class);
    }

    @Override
    public void setServiceRuntimeConfiguration(IService svc,
            ServiceRuntimeConfiguration configuration)
            throws CommunicatorException, CommandExecutorException {
        URI uri = this.resolveRelativeUri(String.format(
                REL_URL_SVC_RUNTIME_CONF, svc.getId()));
        this.client.put(uri, configuration, ContentType.APPLICATION_XML);
    }

    public VirtualServiceLoggingConfiguration getVirtualServiceLoggingConfiguration(IService svc) throws CommunicatorException{
        URI uri = this.resolveRelativeUri(String.format(REL_URL_SVC_LOGGING_CONF, svc.getId()));
        return this.client.get(uri, ContentType.APPLICATION_XML, VirtualServiceLoggingConfiguration.class);
    }

    public void setVirtualServiceLoggingConfiguration(IService svc, VirtualServiceLoggingConfiguration value) throws CommunicatorException {
        URI uri = this.resolveRelativeUri(String.format(REL_URL_SVC_LOGGING_CONF, svc.getId()));
        this.client.put(uri, value, ContentType.APPLICATION_XML);
    }

    // ============================== PRIVATE METHODS
    // ==========================================

    /**
     * Deploys an project element (service, dataModel, PerfModel, ...) into the
     * server on specified URI
     * 
     * @param el
     *            element to be deployed
     * @param uri
     *            URI where the post request will be send to
     * @throws com.microfocus.sv.svconfigurator.core.impl.exception.CommunicatorException
     * 
     */
    private void deployElement(IProjectElement el, URI uri)
            throws CommunicatorException {
        LOG.debug(String.format("Deploying element %s [%s] to %s", el.getName(), el.getId(), uri));
        try {
            this.client.post(uri, el.getData(), el.getDataLength(),
                    ContentType.APPLICATION_XML);
        } catch (IOException e) {
            throw new CommunicatorException(el + " data retrieve error: "
                    + e.getLocalizedMessage(), e);
        } catch (SVCParseException e) {
            throw new CommunicatorException(el + " data retrieve error: "
                    + e.getLocalizedMessage(), e);
        }
    }

    private void updateElement(IProjectElement el, URI uri)
            throws CommunicatorException {
        try {
            this.client.put(uri, el.getData(), el.getDataLength(),
                    ContentType.APPLICATION_XML);
        } catch (IOException e) {
            throw new CommunicatorException(el + " data retrieve error: "
                    + e.getLocalizedMessage(), e);
        } catch (SVCParseException e) {
            throw new CommunicatorException(el + " data retrieve error: "
                    + e.getLocalizedMessage(), e);
        }
    }

    private URI resolveRelativeUri(String relative) throws CommunicatorException {
        try {
            URL base = this.mgmtUri;
            return base.toURI().resolve(relative);
        } catch (URISyntaxException e) {
            throw new CommunicatorException("Failed to create URI from '"
                    + mgmtUri + "' and '" + relative + "'", e);
        }
    }

    /**
     * Calls the ping endpoint. If there is an error, exception is thrown. If
     * all is ok, nothing happens.
     * 
     * @throws com.microfocus.sv.svconfigurator.core.impl.exception.CommunicatorException
     * 
     */
    private void pingServer() throws CommunicatorException {
        URI pingUri = this.resolveRelativeUri(REL_URL_PING);
        this.client.pingServer(pingUri);
        LOG.debug("Server ping successful.");
    }

    @Override
    public byte[] fetchVirtualService(String vsId) throws CommunicatorException {
        URI svcUri = this.resolveRelativeUri(String.format(REL_URL_SERVICE,
                vsId));
        return this.client.getPayload(svcUri, ContentType.APPLICATION_XML);
    }

    @Override
    public byte[] fetchServiceDescription(String vsId, String sdId)
            throws CommunicatorException {
        URI svcUri = this.resolveRelativeUri(String.format(REL_URL_SVC_DESC,
                vsId, sdId));
        return this.client.getPayload(svcUri, ContentType.APPLICATION_XML);
    }

    @Override
    public byte[] fetchContentFile(String vsId, String cfId)
            throws CommunicatorException {
        URI svcUri = this.resolveRelativeUri(String.format(REL_URL_CONTENT_FILES,
                vsId, cfId));
        return this.client.getPayload(svcUri, ContentType.APPLICATION_XML);
    }

    @Override
    public byte[] fetchDataModel(String vsId, String dmId)
            throws CommunicatorException {
        URI svcUri = this.resolveRelativeUri(String.format(REL_URL_DATA_MODEL,
                vsId, dmId));
        return this.client.getPayload(svcUri, ContentType.APPLICATION_XML);
    }

    @Override
    public byte[] fetchDataSet(String vsId, String dmId, String dsId)
            throws CommunicatorException {
        URI svcUri = this.resolveRelativeUri(String.format(REL_URL_DATA_SET,
                vsId, dmId, dsId));
        return this.client.getPayload(svcUri, ContentType.APPLICATION_XML);
    }

    @Override
    public byte[] fetchPerformanceModel(String vsId, String pmId)
            throws CommunicatorException {
        URI svcUri = this.resolveRelativeUri(String.format(REL_URL_PERF_MODEL,
                vsId, pmId));
        return this.client.getPayload(svcUri, ContentType.APPLICATION_XML);
    }

    @Override
    public FileInfo fetchLoggedMessages(String vsId, long from, int limit) throws CommunicatorException {
        URI uri = this.resolveRelativeUri(String.format(REL_URL_LOGGED_MESSAGES_FROM_ID_LIMIT, vsId, from, limit));
        return this.client.getFileInfo(uri, ContentType.APPLICATION_XML);
    }

    @Override
    public void resetLoggedMessagesForService(String vsId) throws CommunicatorException {
        URI uri = this.resolveRelativeUri(String.format(REL_URL_LOGGED_MESSAGES, vsId));
        this.client.delete(uri);
    }

    @Override
    public void importLoggedMessages(ILoggedServiceCallList loggedServiceCallList) throws CommunicatorException {
        URI uri = this.resolveRelativeUri(String.format(REL_URL_LOGGED_MESSAGES, loggedServiceCallList.VsId()));
        try {
            this.client.post(uri, loggedServiceCallList.getData(), loggedServiceCallList.getDataLength(), ContentType.APPLICATION_XML);
        } catch (IOException e) {
            throw new CommunicatorException(loggedServiceCallList + " data retrieve error: "
                    + e.getLocalizedMessage(), e);
        } catch (SVCParseException e) {
            throw new CommunicatorException(loggedServiceCallList + " data retrieve error: "
                    + e.getLocalizedMessage(), e);
        }
    }

    private Collection<String> fetchItems(URI uri) throws CommunicatorException {
        try {
            byte[] data = this.client.getPayload(uri, null);
            Document doc = deserializeXml(data);
            List<String> items = new ArrayList<String>();
            Node feed = findNext(doc.getFirstChild(), "feed");
            if (feed == null) {
                throw new IllegalArgumentException("Failed to get feed element");
            }
            Node entry = feed.getFirstChild();
            while ((entry = findNext(entry, "entry")) != null) {
                Node id = findNext(entry.getFirstChild(), "id");
                items.add(id.getTextContent().trim());
                entry = entry.getNextSibling();
            }
            return items;
        } catch (CommunicatorException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalArgumentException(
                    "Cannot get fetch item list for '" + uri + "'", e);
        }
    }

    @Override
    public Collection<String> getServiceDescriptionIds(String vsId)
            throws CommunicatorException {
        return fetchItems(this.resolveRelativeUri(String.format(
                REL_URL_GET_SVC_DESCS, vsId)));
    }

    private Node findNext(Node node, String name) {
        if (node == null) {
            return null;
        }
        Node x = node;
        do {
            if (x.getNodeName().endsWith(name)) {
                return x;
            }
            x = x.getNextSibling();
        } while (x != null);
        return null;
    }

    @Override
    public Collection<String> getDataModelIds(String vsId)
            throws CommunicatorException {
        return fetchItems(this.resolveRelativeUri(String.format(
                REL_URL_GET_DATA_MODELS, vsId)));
    }

    @Override
    public Collection<String> getDataSetIds(String vsId, String dmId)
            throws CommunicatorException {
        return fetchItems(this.resolveRelativeUri(String.format(
                REL_URL_GET_DATA_SETS, vsId, dmId)));
    }

    @Override
    public Collection<String> getPerformanceModelIds(String vsId)
            throws CommunicatorException {
        return fetchItems(this.resolveRelativeUri(String.format(
                REL_URL_GET_PERF_MODELS, vsId)));
    }

    // ============================== GETTERS / SETTERS
    // ========================================

    // ============================== INNER CLASSES
    // ============================================

}
