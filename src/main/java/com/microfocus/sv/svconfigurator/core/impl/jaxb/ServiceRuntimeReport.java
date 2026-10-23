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
package com.microfocus.sv.svconfigurator.core.impl.jaxb;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;

import static com.microfocus.sv.svconfigurator.core.impl.jaxb.ServiceRuntimeReport.NAMESPACE;

import java.util.List;

@XmlRootElement(name = "runtimeReport", namespace = NAMESPACE)
public class ServiceRuntimeReport {
    //============================== STATIC ATTRIBUTES ========================================

    public static final String NAMESPACE = "http://hp.com/SOAQ/ServiceVirtualization/2010/";

    //============================== INSTANCE ATTRIBUTES ======================================

    private int messageCount;

    private int messageSize;

    private int errorCount;

    private int warningCount;

    private int perfModelAccuracy;

    private String serviceId;

    private long lastResponseTime;
    private long minResponseTime;
    private long maxResponseTime;
    private double standardDeviation;

    private int uniqueMsgCount;
    private SimulationStatistics simulationStats;

    private List<String> clientIds;

    private List<String> hostnames;

    //============================== STATIC METHODS ===========================================

    //============================== CONSTRUCTORS =============================================

    //============================== ABSTRACT METHODS =========================================

    //============================== OVERRIDEN METHODS ========================================

    //============================== INSTANCE METHODS =========================================

    //============================== PRIVATE METHODS ==========================================

    //============================== GETTERS / SETTERS ========================================


    @XmlAttribute(name = "lastResponseTime")
    public long getLastResponseTime() {
        return lastResponseTime;
    }

    public void setLastResponseTime(long lastResponseTime) {
        this.lastResponseTime = lastResponseTime;
    }

    @XmlAttribute(name = "standardDeviation")
    public double getStandardDeviation() {
        return standardDeviation;
    }

    public void setStandardDeviation(double standardDeviation) {
        this.standardDeviation = standardDeviation;
    }

    @XmlAttribute(name = "minResponseTime")
    public long getMinResponseTime() {
        return minResponseTime;
    }

    public void setMinResponseTime(long minResponseTime) {
        this.minResponseTime = minResponseTime;
    }

    @XmlAttribute(name = "maxResponseTime")
    public long getMaxResponseTime() {
        return maxResponseTime;
    }

    public void setMaxResponseTime(long maxResponseTime) {
        this.maxResponseTime = maxResponseTime;
    }

    @XmlAttribute(name = "messageCount")
    public int getMessageCount() {
        return messageCount;
    }

    public void setMessageCount(int messageCount) {
        this.messageCount = messageCount;
    }

    @XmlAttribute(name = "messageSize")
    public int getMessageSize() {
        return messageSize;
    }

    public void setMessageSize(int messageSize) {
        this.messageSize = messageSize;
    }

    @XmlAttribute(name = "numberOfErrors")
    public int getErrorCount() {
        return errorCount;
    }

    public void setErrorCount(int errorCount) {
        this.errorCount = errorCount;
    }

    @XmlAttribute(name = "numberOfWarnings")
    public int getWarningCount() {
        return warningCount;
    }

    public void setWarningCount(int warningCount) {
        this.warningCount = warningCount;
    }

    @XmlAttribute(name = "performanceModelAccuracy")
    public int getPerfModelAccuracy() {
        return perfModelAccuracy;
    }

    public void setPerfModelAccuracy(int perfModelAccuracy) {
        this.perfModelAccuracy = perfModelAccuracy;
    }

    @XmlAttribute(name = "serviceId")
    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    @XmlAttribute(name = "uniqueMessageCount")
    public int getUniqueMsgCount() {
        return uniqueMsgCount;
    }

    public void setUniqueMsgCount(int uniqueMsgCount) {
        this.uniqueMsgCount = uniqueMsgCount;
    }

    @XmlElement(name = "simulationStatistics", namespace = NAMESPACE)
    public SimulationStatistics getSimulationStats() {
        return simulationStats;
    }

    public void setSimulationStats(SimulationStatistics simulationStats) {
        this.simulationStats = simulationStats;
    }

    @XmlElementWrapper(name = "clientIds", namespace = NAMESPACE)
    @XmlElement(name = "clientId", namespace = NAMESPACE)
    public List<String> getClientIds() {
        return clientIds;
    }

    public void setClientIds(List<String> clientIds) {
        this.clientIds = clientIds;
    }

    @XmlElementWrapper(name = "hostnames", namespace = NAMESPACE)
    @XmlElement(name = "hostname", namespace = NAMESPACE)
    public List<String> getHostnames() {
        return hostnames;
    }

    public void setHostnames(List<String> hostnames) {
        this.hostnames = hostnames;
    }


    //============================== INNER CLASSES ============================================

    public static class SimulationStatistics {

        private int defaultRuleUsedCount;

        private int requestsCount;

        private int requestsNotIncreasingCostCount;

        private int simulationQualityPercentage;

        private int statefulResponsesReturnedCount;

        private double totalStatelessSimulationAccuracy;

        @XmlAttribute(name = "defaultRuleUsedCount")
        public int getDefaultRuleUsedCount() {
            return defaultRuleUsedCount;
        }

        public void setDefaultRuleUsedCount(int defaultRuleUsedCount) {
            this.defaultRuleUsedCount = defaultRuleUsedCount;
        }

        @XmlAttribute(name = "requestsCount")
        public int getRequestsCount() {
            return requestsCount;
        }

        public void setRequestsCount(int requestsCount) {
            this.requestsCount = requestsCount;
        }

        @XmlAttribute(name = "requestsNotIncreasingCostCount")
        public int getRequestsNotIncreasingCostCount() {
            return requestsNotIncreasingCostCount;
        }

        public void setRequestsNotIncreasingCostCount(int requestsNotIncreasingCostCount) {
            this.requestsNotIncreasingCostCount = requestsNotIncreasingCostCount;
        }

        @XmlAttribute(name = "simulationQualityPercentage")
        public int getSimulationQualityPercentage() {
            return simulationQualityPercentage;
        }

        public void setSimulationQualityPercentage(int simulationQualityPercentage) {
            this.simulationQualityPercentage = simulationQualityPercentage;
        }

        @XmlAttribute(name = "statefulResponsesReturnedCount")
        public int getStatefulResponsesReturnedCount() {
            return statefulResponsesReturnedCount;
        }

        public void setStatefulResponsesReturnedCount(int statefulResponsesReturnedCount) {
            this.statefulResponsesReturnedCount = statefulResponsesReturnedCount;
        }

        @XmlAttribute(name = "totalStatelessSimulationAccuracy")
        public double getTotalStatelessSimulationAccuracy() {
            return totalStatelessSimulationAccuracy;
        }

        public void setTotalStatelessSimulationAccuracy(double totalStatelessSimulationAccuracy) {
            this.totalStatelessSimulationAccuracy = totalStatelessSimulationAccuracy;
        }
    }

}
