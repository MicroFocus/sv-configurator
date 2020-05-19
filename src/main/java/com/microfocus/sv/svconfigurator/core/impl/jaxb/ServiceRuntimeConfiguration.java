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
package com.microfocus.sv.svconfigurator.core.impl.jaxb;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.microfocus.sv.svconfigurator.core.IService;
import com.microfocus.sv.svconfigurator.core.impl.jaxb.helper.ReferenceElement;
import com.microfocus.sv.svconfigurator.processor.printer.NonPrintable;

@XmlRootElement(name = "virtualServiceRuntimeConfiguration", namespace = ServiceRuntimeConfiguration.NAMESPACE)
@XmlType(propOrder = { "service", "dataModel", "perfModel", "clientId", "runtimeMode", "logMessages", "deploymentErrorMessage", "deploymentState" })
public class ServiceRuntimeConfiguration implements Cloneable {
    // ============================== STATIC ATTRIBUTES
    // ========================================

    public static final String NAMESPACE = "http://hp.com/SOAQ/ServiceVirtualization/2010/";

    // ============================== INSTANCE ATTRIBUTES
    // ======================================
    private String id;
    @NonPrintable
    private ReferenceElement service;
    private ReferenceElement dataModel;
    private ReferenceElement perfModel;
    private String clientId;
    private RuntimeMode runtimeMode;
    @NonPrintable
    private LogMessages logMessages;
    private String deploymentErrorMessage;
    private DeploymentState deploymentState;

    // ============================== STATIC METHODS
    // ===========================================

    // ============================== CONSTRUCTORS
    // =============================================

    public ServiceRuntimeConfiguration() {
    }

    public ServiceRuntimeConfiguration(IService svc, RuntimeMode runtimeMode, boolean logging, DeploymentState deploymentState) {
        this.runtimeMode = runtimeMode;
        this.deploymentState = deploymentState;

        this.setServiceId(svc.getId());
        this.setLogging(logging);
    }

    // ============================== ABSTRACT METHODS
    // =========================================

    // ============================== OVERRIDEN METHODS
    // ========================================

    @Override
    public String toString() {
        return "ServiceRuntimeConfiguration[" +
                "\truntimeId: " + this.getId() + "\n" +
                "\tserviceId: " + (this.service == null ? "" : this.service.getRef()) + "\n" +
                "\tdataModelId: " + this.getDataModelId() + "\n" +
                "\tperfModelId: " + this.getPerfModelId() + "\n" +
                "\tclientId: " + this.getClientId() + "\n" +
                "\truntimeMode: " + this.runtimeMode + "\n" +
                "\tlogMessages: " + this.logMessages + "\n" +
                "\tdeploymentErrorMessage: " + this.deploymentErrorMessage + "\n]" +
                "\tdeploymentState: " + this.deploymentState + "\n]";
    }

    @Override
    @SuppressWarnings("MethodDoesntCallSuperMethod")
    public ServiceRuntimeConfiguration clone() {
        ServiceRuntimeConfiguration res = new ServiceRuntimeConfiguration();
        res.setId(this.getId());
        res.setServiceId(this.getServiceId());
        res.setDataModelId(this.getDataModelId());
        res.setPerfModelId(this.getPerfModelId());
        res.setClientId(this.getClientId());
        res.setRuntimeMode(this.getRuntimeMode());
        res.setLogging(this.isLogging());
        res.setDeploymentState(this.getDeploymentState());

        return res;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof ServiceRuntimeConfiguration))
            return false;

        ServiceRuntimeConfiguration that = (ServiceRuntimeConfiguration) o;

        if (clientId != null ? !clientId.equals(that.clientId) : that.clientId != null)
            return false;
        if (dataModel != null ? !dataModel.equals(that.dataModel) : that.dataModel != null)
            return false;
        if (deploymentState != that.deploymentState)
            return false;
        if (id != null ? !id.equals(that.id) : that.id != null)
            return false;
        if (logMessages != that.logMessages)
            return false;
        if (perfModel != null ? !perfModel.equals(that.perfModel) : that.perfModel != null)
            return false;
        if (runtimeMode != that.runtimeMode)
            return false;
        if (deploymentErrorMessage != null ? !deploymentErrorMessage.equals(that.deploymentErrorMessage) : that.deploymentErrorMessage != null)
            return false;
        return !(service != null ? !service.equals(that.service) : that.service != null);

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (service != null ? service.hashCode() : 0);
        result = 31 * result + (dataModel != null ? dataModel.hashCode() : 0);
        result = 31 * result + (perfModel != null ? perfModel.hashCode() : 0);
        result = 31 * result + (clientId != null ? clientId.hashCode() : 0);
        result = 31 * result + (runtimeMode != null ? runtimeMode.hashCode() : 0);
        result = 31 * result + (logMessages != null ? logMessages.hashCode() : 0);
        result = 31 * result + (deploymentErrorMessage != null ? deploymentErrorMessage.hashCode() : 0);
        result = 31 * result + (deploymentState != null ? deploymentState.hashCode() : 0);
        return result;
    }

// ============================== INSTANCE METHODS
    // =========================================

    @XmlTransient
    public String getServiceId() {
        return this.service == null ? null : this.service.getRef();
    }

    public void setServiceId(String serviceId) {
        this.service = new ReferenceElement(serviceId);
    }

    @XmlTransient
    public boolean isLogging() {
        return LogMessages.TRUE.equals(this.logMessages);
    }

    public void setLogging(boolean logging) {
        this.logMessages = logging ? LogMessages.TRUE : LogMessages.FALSE;
    }

    @XmlTransient
    public String getDataModelId() {
        return this.dataModel == null ? null : this.dataModel.getRef();
    }

    public void setDataModelId(String id) {
        this.dataModel = id == null ? null : new ReferenceElement(id);
    }

    @XmlTransient
    public String getPerfModelId() {
        return this.perfModel == null ? null : this.perfModel.getRef();
    }

    public void setPerfModelId(String id) {
        this.perfModel = id == null ? null : new ReferenceElement(id);
    }

    public boolean isLocked() {
        return this.clientId != null && !this.clientId.isEmpty();
    }

    public boolean isLockedForMe(String myId) {
        return this.isLocked() && !this.clientId.equals(myId);
    }

    @XmlTransient
    public RuntimeMode getDisplayRuntimeMode() {
        if (deploymentState != DeploymentState.READY) {
            return RuntimeMode.OFFLINE;
        }
        return (runtimeMode == ServiceRuntimeConfiguration.RuntimeMode.STAND_BY && getPerfModelId() != null && getDataModelId() == null)
                ? ServiceRuntimeConfiguration.RuntimeMode.SIMULATING
                : runtimeMode;
    }


    // ============================== PRIVATE METHODS
    // ==========================================

    // ============================== GETTERS / SETTERS
    // ========================================

    @XmlAttribute(name = "id")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @XmlElement(name = "service", namespace = NAMESPACE)
    public ReferenceElement getService() {
        return service;
    }

    public void setService(ReferenceElement service) {
        this.service = service;
    }

    @XmlElement(name = "dataModel", namespace = NAMESPACE, nillable = false, required = false)
    public ReferenceElement getDataModel() {
        return dataModel;
    }

    public void setDataModel(ReferenceElement dataModel) {
        this.dataModel = dataModel;
    }

    @XmlElement(name = "performanceModel", namespace = NAMESPACE, nillable = false, required = false)
    public ReferenceElement getPerfModel() {
        return perfModel;
    }

    public void setPerfModel(ReferenceElement perfModel) {
        this.perfModel = perfModel;
    }

    @XmlElement(name = "clientId", namespace = NAMESPACE, nillable = false, required = false)
    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    @XmlElement(name = "serviceRuntimeMode", namespace = NAMESPACE)
    public RuntimeMode getRuntimeMode() {
        return runtimeMode;
    }

    public void setRuntimeMode(RuntimeMode runtimeMode) {
        this.runtimeMode = runtimeMode;
    }

    @XmlElement(name = "logMessages", namespace = NAMESPACE)
    public LogMessages getLogMessages() {
        return logMessages;
    }

    public void setLogMessages(LogMessages logMessages) {
        this.logMessages = logMessages;
    }

    @XmlElement(name = "virtualServiceDeploymentState", namespace = NAMESPACE)
    public DeploymentState getDeploymentState() {
        return deploymentState;
    }

    public void setDeploymentState(DeploymentState deploymentState) {
        this.deploymentState = deploymentState;
    }

    @XmlElement(name = "deploymentErrorMessage", namespace = NAMESPACE)
    public String getDeploymentErrorMessage() {
        return deploymentErrorMessage;
    }

    public void setDeploymentErrorMessage(String deploymentErrorMessage) {
        this.deploymentErrorMessage = deploymentErrorMessage;
    }

    // ============================== INNER CLASSES
    // ============================================

    public static class XmlConstants {
        public static final String RUNTIME_OFFLINE = "Offline";
        public static final String RUNTIME_SIMULATING = "Simulating";
        public static final String RUNTIME_PASS_THROUGH = "PassThrough";
        public static final String RUNTIME_RECORDING = "Recording";

        public static final String STATE_READY = "Ready";
        public static final String STATE_DOWN = "Down";
        public static final String STATE_DOWNING = "Downing";
        public static final String STATE_READYING = "Readying";
    }

    @XmlEnum
    public enum RuntimeMode {
        @XmlEnumValue(XmlConstants.RUNTIME_OFFLINE)
        OFFLINE,

        @XmlEnumValue(XmlConstants.RUNTIME_SIMULATING)
        SIMULATING,

        @XmlEnumValue(XmlConstants.RUNTIME_PASS_THROUGH)
        STAND_BY,

        @XmlEnumValue(XmlConstants.RUNTIME_RECORDING)
        LEARNING
    }

    @XmlEnum
    public enum DeploymentState {
        @XmlEnumValue(XmlConstants.STATE_READY)
        READY(false),

        @XmlEnumValue(XmlConstants.STATE_DOWN)
        DOWN(false),

        @XmlEnumValue(XmlConstants.STATE_DOWNING)
        DOWNING(true),

        @XmlEnumValue(XmlConstants.STATE_READYING)
        READYING(true);

        private boolean inProgress;

        DeploymentState(boolean inProgress) {
            this.inProgress = inProgress;
        }

        public boolean isInProgress() {
            return inProgress;
        }
    }

    @XmlEnum
    public enum LogMessages {
        @XmlEnumValue("true")
        TRUE,

        @XmlEnumValue("false")
        FALSE
    }

}
