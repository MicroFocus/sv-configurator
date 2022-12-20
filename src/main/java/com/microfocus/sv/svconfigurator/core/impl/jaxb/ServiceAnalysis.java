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

import javax.xml.datatype.Duration;

import com.microfocus.sv.svconfigurator.core.impl.jaxb.helper.ReferenceElement;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlEnumValue;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "analysisResult", namespace = ServiceAnalysis.NAMESPACE)
public class ServiceAnalysis {
    //============================== STATIC ATTRIBUTES ========================================

    public static final String NAMESPACE = "http://hp.com/SOAQ/ServiceVirtualization/2010/";

    //============================== INSTANCE ATTRIBUTES ======================================

    private ReferenceElement service;

    private ReferenceElement aggregateModifications;

    private String httpAuthentication;

    private int recordedMsgs;

    private int analyzedMsgs;

    private Duration estimatedTimeLeft;

    private State state;

    //============================== STATIC METHODS ===========================================

    //============================== CONSTRUCTORS =============================================

    //============================== ABSTRACT METHODS =========================================

    //============================== OVERRIDEN METHODS ========================================

    //============================== INSTANCE METHODS =========================================

    //============================== PRIVATE METHODS ==========================================

    //============================== GETTERS / SETTERS ========================================

    @XmlElement(name = "virtualService", namespace = NAMESPACE)
    public ReferenceElement getService() {
        return service;
    }

    public void setService(ReferenceElement service) {
        this.service = service;
    }

    @XmlElement(name = "aggregateModifications", namespace = NAMESPACE)
    public ReferenceElement getAggregateModifications() {
        return aggregateModifications;
    }

    public void setAggregateModifications(ReferenceElement aggregateModifications) {
        this.aggregateModifications = aggregateModifications;
    }

    @XmlElement(name = "httpAuthentication", namespace = NAMESPACE)
    public String getHttpAuthentication() {
        return httpAuthentication;
    }

    public void setHttpAuthentication(String httpAuthentication) {
        this.httpAuthentication = httpAuthentication;
    }

    @XmlElement(name = "recordedMessages", namespace = NAMESPACE)
    public int getRecordedMsgs() {
        return recordedMsgs;
    }

    public void setRecordedMsgs(int recordedMsgs) {
        this.recordedMsgs = recordedMsgs;
    }

    @XmlElement(name = "analyzedMessages", namespace = NAMESPACE)
    public int getAnalyzedMsgs() {
        return analyzedMsgs;
    }

    public void setAnalyzedMsgs(int analyzedMsgs) {
        this.analyzedMsgs = analyzedMsgs;
    }

    @XmlElement(name = "estimatedTimeLeft", namespace = NAMESPACE)
    public Duration getEstimatedTimeLeft() {
        return estimatedTimeLeft;
    }

    public void setEstimatedTimeLeft(Duration estimatedTimeLeft) {
        this.estimatedTimeLeft = estimatedTimeLeft;
    }

    @XmlElement(name = "state", namespace = NAMESPACE)
    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }


    //============================== INNER CLASSES ============================================

    @XmlEnum
    public static enum State {

        @XmlEnumValue("NotStarted")
        NOT_STARTED(false),

        @XmlEnumValue("InProgress")
        IN_PROGRESS(false),

        @XmlEnumValue("Finishing")
        FINISHING(true),

        @XmlEnumValue("Finished")
        FINISHED(false),

        @XmlEnumValue("Failing")
        FAILING(true),

        @XmlEnumValue("Failed")
        FAILED(false),

        @XmlEnumValue("Cancelled")
        CANCELLED(false),

        @XmlEnumValue("Cancelling")
        CANCELLING(true);

        private boolean inProgress;

        private State(boolean inProgress) {
            this.inProgress = inProgress;
        }

        public boolean isInProgress() {
            return this.inProgress;
        }

    }

}
