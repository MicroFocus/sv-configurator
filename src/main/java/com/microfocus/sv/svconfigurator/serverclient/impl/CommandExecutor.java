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
package com.microfocus.sv.svconfigurator.serverclient.impl;

import java.util.ArrayList;
import java.util.Collection;

import com.microfocus.sv.svconfigurator.Global;
import com.microfocus.sv.svconfigurator.core.*;
import com.microfocus.sv.svconfigurator.core.impl.DataModel;
import com.microfocus.sv.svconfigurator.core.impl.OfflinePerfModel;
import com.microfocus.sv.svconfigurator.core.impl.PerfModel;
import com.microfocus.sv.svconfigurator.core.impl.Service;
import com.microfocus.sv.svconfigurator.core.impl.datasource.InexistingProjectElementDataSource;
import com.microfocus.sv.svconfigurator.core.impl.exception.CommandExecutorException;
import com.microfocus.sv.svconfigurator.core.impl.exception.CommunicatorException;
import com.microfocus.sv.svconfigurator.core.impl.jaxb.AgentConfigurations;
import com.microfocus.sv.svconfigurator.core.impl.jaxb.ServiceAnalysis;
import com.microfocus.sv.svconfigurator.core.impl.jaxb.ServiceRuntimeConfiguration;
import com.microfocus.sv.svconfigurator.core.impl.jaxb.ServiceRuntimeConfiguration.RuntimeMode;
import com.microfocus.sv.svconfigurator.core.impl.jaxb.ServiceRuntimeReport;
import com.microfocus.sv.svconfigurator.core.impl.jaxb.VirtualServiceLoggingConfiguration;
import com.microfocus.sv.svconfigurator.core.impl.jaxb.atom.ElemModelListAtom;
import com.microfocus.sv.svconfigurator.core.impl.jaxb.atom.ServiceListAtom;
import com.microfocus.sv.svconfigurator.core.impl.processor.ElementStatus;
import com.microfocus.sv.svconfigurator.serverclient.ICommandExecutor;
import com.microfocus.sv.svconfigurator.serverclient.IServerManagementEndpointClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Every logical command is composed of several steps (e.g. to put several times
 * something into /runtimeConfiguration/ endpoint). This class is responsible
 * for execution of these steps (by calling appropriate methods in
 * ServerManagementEndpointClient).
 */
public class CommandExecutor implements ICommandExecutor {
    // ============================== STATIC ATTRIBUTES
    // ========================================

    public static final String PROP_WAIT_MS = "svconf_wait_ms";
    private static final int RETRY_INTERVAL_MS = 250;
    private static final Logger LOG = LoggerFactory.getLogger(CommandExecutor.class);
    private static int WAIT_MILISECONDS = 30000;

    static {
        try {
            WAIT_MILISECONDS = Integer.parseInt(System.getProperty(PROP_WAIT_MS, "30000"));
        } catch (NumberFormatException ex) {
            LOG.error("Wait time configuration error. ", ex);
        }
    }
    
    // ============================== INSTANCE ATTRIBUTES
    // ======================================
    private IServerManagementEndpointClient smeClient;
    private boolean force = false;

    // ============================== STATIC METHODS
    // ===========================================

    // ============================== CONSTRUCTORS
    // =============================================

    public CommandExecutor(IServerManagementEndpointClient smeClient) throws CommunicatorException {
        this.smeClient = smeClient;
    }

    // ============================== ABSTRACT METHODS
    // =========================================

    // ============================== OVERRIDEN METHODS
    // ========================================

    // ============================== INSTANCE METHODS
    // =========================================

    /**
     * Deploys the project onto the server
     * 
     * @throws com.microfocus.sv.svconfigurator.core.impl.exception.CommunicatorException
     * 
     */
    @Override
    public void deployProject(IProject project) throws CommunicatorException, CommandExecutorException {
        for (IService svc : project.getServices()) {
            this.deployService(svc, project.getProjectPassword());
        }
        LOG.info(project + " successfully deployed.");
    }
    
    public void deployService(IService svc, String projectPassword) throws CommunicatorException, CommandExecutorException {
        ElementStatus svcStat = this.smeClient.getServiceStatus(svc);
        if (ElementStatus.NOT_PRESENT.equals(svcStat)) { // Deploy service
            this.smeClient.deployService(svc, projectPassword);
        } else { // Update the service
            this.changeServiceDeploymentState(svc, ServiceRuntimeConfiguration.DeploymentState.DOWN);
            this.smeClient.updateService(svc, projectPassword);
        }
        this.smeClient.getServiceStatus(svc);

        for (IDataModel dm : svc.getDataModels()) {
            ElementStatus dmStat = this.smeClient.getDataModelStatus(dm);
            if (ElementStatus.NOT_PRESENT.equals(dmStat)) { // deployProject data model
                this.smeClient.deployDataModel(dm, projectPassword);
            } else { // update data model
                this.smeClient.updateDataModel(dm, projectPassword);
            }
            this.smeClient.getDataModelStatus(dm);

            for (IDataSet ds : dm.getDataSets()) {
                ElementStatus dsStat = this.smeClient.getDataSetStatus(ds);
                if (ElementStatus.NOT_PRESENT.equals(dsStat)) { // deployProject data set
                    this.smeClient.deployDataSet(ds, projectPassword);
                } else {
                    long version = this.smeClient.getDataSetHashCode(ds);
                    if (version != ds.getDataHashCode()) { // update the data set
                        this.smeClient.updateDataSet(ds, projectPassword);
                    }
                }
                this.smeClient.getDataSetStatus(ds);
            }
        }

        for (IPerfModel pm : svc.getPerfModels()) {
            ElementStatus pmStat = this.smeClient.getPerfModelStatus(pm);
            if (ElementStatus.NOT_PRESENT.equals(pmStat)) { // deployProject performance model
                this.smeClient.deployPerfModel(pm, projectPassword);
            } else { // update performance model
                this.smeClient.updatePerfModel(pm, projectPassword);
            }
            this.smeClient.getPerfModelStatus(pm);
        }

        for (IServiceDescription sd : svc.getDescriptions()) {
            ElementStatus sdStat = this.smeClient.getServiceDescriptionStatus(sd, svc);
            if (ElementStatus.NOT_PRESENT.equals(sdStat)) {
                this.smeClient.deployServiceDescription(sd, svc, projectPassword);
            } else {
                this.smeClient.updateServiceDescription(sd, svc, projectPassword);
            }
            this.smeClient.getServiceDescriptionStatus(sd, svc);
        }

        for (IContentFile cf : svc.getContentFiles()) {
            ElementStatus cfStat = this.smeClient.getContentFileStatus(cf, svc);
            if (ElementStatus.NOT_PRESENT.equals(cfStat)) {
                this.smeClient.deployContentFile(cf, projectPassword, svc);
            } else {
                this.smeClient.updateContentFile(cf, projectPassword, svc);
            }
            this.smeClient.getContentFileStatus(cf, svc);
        }

        this.smeClient.getServiceRuntimeConfiguration(svc);
        this.changeServiceDeploymentState(svc, ServiceRuntimeConfiguration.DeploymentState.READY);
        LOG.info(svc + " successfully deployed.");
    }

    /**
     * Undeploys all the services from the server
     * 
     * @throws CommunicatorException
     */
    @Override
    public void undeploy(IProject project) throws CommunicatorException, CommandExecutorException {
        for (IService svc : project.getServices()) {
            this.undeployService(svc);
        }
        LOG.info(project + " successfully undeployed.");
    }

    @Override
    public void undeployService(IService svc) throws CommunicatorException, CommandExecutorException {
        ElementStatus s = this.smeClient.getServiceStatus(svc);
        if (s.equals(ElementStatus.PRESENT)) {
            ServiceRuntimeConfiguration conf = this.smeClient.getServiceRuntimeConfiguration(svc);
            this.verifyLock(conf);

            this.smeClient.undeployService(svc.getId());

            // wait for undeploying
            long start = System.currentTimeMillis();
            while ((System.currentTimeMillis() - start) < WAIT_MILISECONDS) {
                ElementStatus stat = this.smeClient.getServiceStatus(svc);
                if (ElementStatus.NOT_PRESENT.equals(stat)) {
                    LOG.info(svc + " successfully undeployed.");
                    return;
                }
            }
            throw new CommandExecutorException("Service did not undeployed in " + WAIT_MILISECONDS + " milliseconds.");
        }
    }

    @Override
    public IService findService(String svcIdent, IProject proj) throws CommunicatorException, CommandExecutorException {
        Collection<IService> svcs = this.findServices(svcIdent, proj);
        if (svcs.isEmpty()) {
            throw new CommandExecutorException("Desired service '" + svcIdent + "' was not found on the server.");
        }
        if (svcs.size() > 1) {
            throw new CommandExecutorException("There was found more than one service with the specified " +
                    "identification. Please, specify its ID or provide a project to search in.");
        }
        IService svc = svcs.iterator().next();

        // add data models
        for (IDataModel dm : this.findDataModels(svc)) {
            svc.addDataModel(dm);
        }

        // add perf models
        for (IPerfModel pm : this.findPerfModels(svc)) {
            svc.addPerfModel(pm);
        }

        return svc;
    }

    @Override
    public Collection<IService> findServices(String svcIdent, IProject proj) throws CommunicatorException {
        ServiceListAtom atom = this.getServiceList(proj == null ? null : proj.getId());
        Collection<IService> svcs = new ArrayList<IService>(atom.getEntries().size());
        for (ServiceListAtom.ServiceEntry se : atom.getEntries()) {
            if (svcIdent.equals(se.getTitle()) || svcIdent.equals(se.getId())) {
                svcs.add(new Service(se.getId(), se.getTitle(), new InexistingProjectElementDataSource(), null, null, se.getRuntimeIssuesParsed(), "true".equalsIgnoreCase(se.getNonExistentRealService())));
            }
        }
        return svcs;
    }

    @Override
    public Collection<IDataModel> findDataModels(IService svc) throws CommunicatorException {
        ElemModelListAtom a = this.smeClient.getSvcDataModelAtom(svc);
        Collection<IDataModel> dms = new ArrayList<IDataModel>(a.getEntries().size());
        for (ElemModelListAtom.ElemModelEntry e : a.getEntries()) {
            dms.add(new DataModel(e.getId(), e.getTitle(), new InexistingProjectElementDataSource(), null, null));
        }
        return dms;
    }

    @Override
    public Collection<IPerfModel> findPerfModels(IService svc) throws CommunicatorException {
        ElemModelListAtom a = this.smeClient.getSvcPerfModelAtom(svc);
        Collection<IPerfModel> dms = new ArrayList<IPerfModel>(a.getEntries().size());
        for (ElemModelListAtom.ElemModelEntry e : a.getEntries()) {
            if (e.getIsOffline()) {
                dms.add(new OfflinePerfModel(e.getId(), e.getTitle(), new InexistingProjectElementDataSource(), null, null));
            } else {
                dms.add(new PerfModel(e.getId(), e.getTitle(), new InexistingProjectElementDataSource(), null, null));
            }
        }
        return dms;
    }

    /**
     * Locks the service (or unlock if clientId is null)
     *
     * @throws CommunicatorException
     * @throws CommandExecutorException
     */
    @Override
    public void lockService(IService svc, String clientId) throws CommunicatorException, CommandExecutorException {
        ServiceRuntimeConfiguration conf = this.smeClient.getServiceRuntimeConfiguration(svc);
        conf.setClientId(null);
        this.smeClient.setServiceRuntimeConfiguration(svc, conf);
        if (clientId != null) {
            conf.setClientId(clientId);
            this.smeClient.setServiceRuntimeConfiguration(svc, conf);
        }
    }

    @Override
    public void changeVirtualServiceLoggingConfiguration(IService svc, boolean isEnabled) throws CommunicatorException, CommandExecutorException{
        VirtualServiceLoggingConfiguration value = new VirtualServiceLoggingConfiguration(svc.getId(), isEnabled);
        this.smeClient.setVirtualServiceLoggingConfiguration(svc, value);
    }

    /**
     * Sets the desired runtime configuration of a virtual service. In the
     * runtime configuration you don't have to specify neither the ID of the
     * runtime nor the ID of the service nor the clientId. These data will be
     * set before transmitting to the server.
     * 
     * @param svc
     *            Service whose runtimeConfiguration will be amended
     * @param runtConf
     *            Desired runtime configuration.
     * @throws CommunicatorException
     *             If there is an unexpected situation during the communication
     *             with the server
     * @throws CommandExecutorException
     *             If goal cant be accomplished due to some condition
     */
    @Override
    public void setServiceRuntime(IService svc, ServiceRuntimeConfiguration runtConf) throws CommunicatorException, CommandExecutorException {
        this.validateServiceRuntime(runtConf);

        ServiceRuntimeConfiguration conf = this.smeClient.getServiceRuntimeConfiguration(svc);
        this.verifyLock(conf);

        if (conf.getRuntimeMode().equals(ServiceRuntimeConfiguration.RuntimeMode.LEARNING)
                && conf.getDeploymentState().equals(ServiceRuntimeConfiguration.DeploymentState.READY)) {
            this.stopLearningAnalysis(svc);
        }

        String clientId = Global.getClientId(this);
        
        // copy static fields
        runtConf.setServiceId(conf.getServiceId());
        runtConf.setId(conf.getId());
        runtConf.setClientId(clientId);
        
        conf.setClientId(clientId);

        LOG.debug("Changing the {} mode to {}", svc, runtConf);
        if (!runtConf.equals(conf)) {
            // take it down
            conf.setDeploymentState(ServiceRuntimeConfiguration.DeploymentState.DOWN);
            this.smeClient.setServiceRuntimeConfiguration(svc, conf);
            ServiceRuntimeConfiguration resConf = this.waitForServiceRuntimeChange(svc);
            if (!ServiceRuntimeConfiguration.DeploymentState.DOWN.equals(resConf.getDeploymentState())) {
                throw new CommandExecutorException("Service should take down but it's mode is " + resConf.getDeploymentState()
                        + ". " + getDeploymentErrorInfo(resConf));
            }

            // change the mode
            this.smeClient.setServiceRuntimeConfiguration(svc, runtConf);
            resConf = this.waitForServiceRuntimeChange(svc);
            if (!ServiceRuntimeConfiguration.DeploymentState.READY.equals(resConf.getDeploymentState())) {
                this.lockService(svc, null); //unlock the service
                
                throw new CommandExecutorException("Service should get ready but it's mode is " + resConf.getDeploymentState()
                        + ". " + getDeploymentErrorInfo(resConf));
            }
            
            if (! this.isSimulatingFromUserPerspective(runtConf) && runtConf.getRuntimeMode() != RuntimeMode.LEARNING) {
                this.lockService(svc, null); //unlock the service
            }
            
            //unlock it if it is not
            LOG.info(svc + " runtime mode is changed to " + runtConf.getDisplayRuntimeMode());
        } else {
            LOG.info(svc + " has already mode " + runtConf.getDisplayRuntimeMode());
        }
    }
    
    /**
     * Sets the desired runtime configuration of a virtual service. In the
     * runtime configuration you don't have to specify neither the ID of the
     * runtime nor the ID of the service nor the clientId. These data will be
     * set before transmitting to the server.
     * 
     * @param svc
     *            Service whose runtimeConfiguration will be amended
     * @param runtConf
     *            Desired runtime configuration.
     * @throws CommunicatorException
     *             If there is an unexpected situation during the communication
     *             with the server
     * @throws CommandExecutorException
     *             If goal cant be accomplished due to some condition
     */
    @Override
    public void hotSwapServiceRuntime(IService svc, ServiceRuntimeConfiguration runtConf) throws CommunicatorException, CommandExecutorException {
        this.validateServiceRuntime(runtConf);

        ServiceRuntimeConfiguration conf = this.smeClient.getServiceRuntimeConfiguration(svc);
        this.verifyLock(conf);

        // copy static fields
        runtConf.setServiceId(conf.getServiceId());
        runtConf.setId(conf.getId());
        runtConf.setClientId(Global.getClientId(this));

        LOG.debug("Changing the {} mode to {}", svc, runtConf);
        if (!runtConf.equals(conf)) {
            // change the mode
            this.smeClient.setServiceRuntimeConfiguration(svc, runtConf);
            ServiceRuntimeConfiguration resConf = this.waitForServiceRuntimeChange(svc);
            if (!ServiceRuntimeConfiguration.DeploymentState.READY.equals(resConf.getDeploymentState())) {
                throw new CommandExecutorException("Service should get ready but it's mode is " + resConf.getDeploymentState()
                        + ". " + getDeploymentErrorInfo(resConf));
            }
            LOG.info(svc + " runtime mode is changed.");
        } else {
            LOG.info(svc + " has already the mode.");
        }
    }
    
    @Override
    public AgentConfigurations getAgents() throws CommunicatorException, CommandExecutorException {
        return this.smeClient.getAgentConfigurations();
    }

    /**
     * Returns the service runtime configuration
     * 
     * @throws CommunicatorException
     */
    @Override
    public ServiceRuntimeConfiguration getServiceRuntimeInfo(IService svc) throws CommunicatorException {
        return this.smeClient.getServiceRuntimeConfiguration(svc);
    }

    @Override
    public ServiceRuntimeReport getServiceRuntimeReport(IService svc) throws CommunicatorException {
        return this.smeClient.getServiceRuntimeReport(svc);
    }

    /**
     * Returns the list of services
     * 
     * @param projectId
     *            services will be searched only for the project (can be set to
     *            null - all services will be listed)
     * @throws CommunicatorException
     */
    @Override
    public ServiceListAtom getServiceList(String projectId) throws CommunicatorException {
        return this.smeClient.getServiceList(projectId);
    }

    // ============================== PRIVATE METHODS
    // ==========================================
    
    private boolean isSimulatingFromUserPerspective(ServiceRuntimeConfiguration conf) {
        return RuntimeMode.SIMULATING == conf.getDisplayRuntimeMode();
    }

    /**
     * Validates the data combination in the ServiceRuntimeConfiguration
     * instance.
     * 
     * @throws CommandExecutorException
     * 
     */
    private void validateServiceRuntime(ServiceRuntimeConfiguration conf) throws CommandExecutorException {
        switch (conf.getRuntimeMode()) {
            case OFFLINE: {
                throw new CommandExecutorException("You can't switch the service into the Offline mode.");
            }
            case STAND_BY: {
                if (conf.getDataModel() != null) {
                    throw new CommandExecutorException(
                            "Data Model can't be set in STAND_BY mode.");
                }
                break;
            }
            case SIMULATING: {
                if (conf.getDataModel() == null) {
                    throw new CommandExecutorException(
                            "Data Model has to be set in SIMULATING mode. If you want to use only Performance Model, use the STAND_BY mode.");
                }
                break;
            }
            case LEARNING: {
                if (conf.getDataModel() == null && conf.getPerfModel() == null) {
                    throw new CommandExecutorException(
                            "Either a Data Model or a Performance Model has to be set in the LEARNING mode.");
                }
            }
        }
    }

    private void verifyLock(ServiceRuntimeConfiguration conf) throws CommandExecutorException {
        String clientId = Global.getClientId(this);
        if (!this.force && conf.isLockedForMe(clientId)) {
            throw new CommandExecutorException(
                    String.format("Service [%s] is locked by '%s' but you are connecting as '%s'. If you want to proceed, use the force mode.",
                            conf.getServiceId(), conf.getClientId(), clientId));
        }
    }

    /**
     * When service is in learning mode and when we want to change its mode we
     * have to stop the lerning analysis. Skipping this phase would lead to all
     * model erasure (from the server)
     * 
     * @throws CommunicatorException
     */
    private void stopLearningAnalysis(IService svc) throws CommunicatorException {
        ServiceAnalysis sa = this.smeClient.getServiceAnalysis(svc);

        if (sa.getState().equals(ServiceAnalysis.State.IN_PROGRESS)) {
            sa.setState(ServiceAnalysis.State.FINISHING);
            this.smeClient.setServiceAnalysis(svc, sa);

            sa = this.smeClient.getServiceAnalysis(svc);
            while (sa.getState().isInProgress()) {
                try {
                    Thread.sleep(RETRY_INTERVAL_MS);
                } catch (InterruptedException e) {
                    throw new CommunicatorException("Error during sleep for service change mode.");
                }
                sa = this.smeClient.getServiceAnalysis(svc);
            }

            if (!sa.getState().equals(ServiceAnalysis.State.FINISHED)) {
                throw new CommunicatorException("Stop Service Analysis resulted into " + sa.getState() + " state.");
            }
        }
    }

    /**
     * Takes the service down
     * 
     * @throws CommunicatorException
     */
    private void changeServiceDeploymentState(IService svc, ServiceRuntimeConfiguration.DeploymentState deploymentState) throws CommunicatorException, CommandExecutorException {
        ServiceRuntimeConfiguration conf = this.smeClient.getServiceRuntimeConfiguration(svc);
        this.verifyLock(conf);

        if (deploymentState.equals(conf.getDeploymentState())) {
            return;
        }

        conf.setDeploymentState(deploymentState);
        conf.setClientId(Global.getClientId(this));

        try {
            this.smeClient.setServiceRuntimeConfiguration(svc, conf);
        } catch (CommandExecutorException ex) {
            throw new IllegalStateException("This should not happen", ex);
        }

        ServiceRuntimeConfiguration resConf = this.waitForServiceRuntimeChange(svc);
        if (!deploymentState.equals(resConf.getDeploymentState())) {
            throw new CommandExecutorException(String.format("Service [%s] should be %s but is %s. %s",
                    svc.getName(), deploymentState.toString(), resConf.getDeploymentState().toString(), getDeploymentErrorInfo(resConf)));
        }
    }

    private String getDeploymentErrorInfo(ServiceRuntimeConfiguration resConf) {
        return "See server log file for more details. Deployment error: " + resConf.getDeploymentErrorMessage();
    }

    private ServiceRuntimeConfiguration waitForServiceRuntimeChange(IService svc) throws CommunicatorException {
        long start = System.currentTimeMillis();
        while ((System.currentTimeMillis() - start) < WAIT_MILISECONDS) {
            ServiceRuntimeConfiguration conf = this.smeClient.getServiceRuntimeConfiguration(svc);
            if (!conf.getDeploymentState().isInProgress()) {
                return conf;
            }

            try {
                Thread.sleep(RETRY_INTERVAL_MS);
            } catch (InterruptedException e) {
                throw new CommunicatorException("Error during sleep for service change mode.");
            }
        }
        throw new CommunicatorException("Service did not change the runtime mode in " + WAIT_MILISECONDS + " ms");
    }

    // ============================== GETTERS / SETTERS
    // ========================================

    /**
     * returns if the command executor is in the force mode. If it is, it
     * unlocks an locked service
     */
    @Override
    public boolean isForce() {
        return force;
    }

    /**
     * Sets if the command executor is in the force mode.
     */
    @Override
    public void setForce(boolean force) {
        this.force = force;
    }
    
    @Override
    public IServerManagementEndpointClient getClient() {
        return smeClient;
    }

    // ============================== INNER CLASSES
    // ============================================

}
