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
package com.microfocus.sv.svconfigurator.core.impl.jaxb.atom;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "feed", namespace = AbstractFeed.NAMESPACE)
public class ServiceListAtom extends AbstractFeed<ServiceListAtom.ServiceEntry> {
    //============================== STATIC ATTRIBUTES ========================================

    //============================== INSTANCE ATTRIBUTES ======================================

    private List<ServiceEntry> entries = new ArrayList<ServiceEntry>();

    //============================== STATIC METHODS ===========================================

    //============================== CONSTRUCTORS =============================================

    //============================== ABSTRACT METHODS =========================================

    //============================== OVERRIDEN METHODS ========================================

    @Override
    @XmlElement(name = AbstractEntry.EL_NAME, namespace = NAMESPACE)
    public List<ServiceEntry> getEntries() {
        return this.entries;
    }

    @Override
    public void setEntries(List<ServiceEntry> entry) {
        this.entries = entry;
    }


    //============================== INSTANCE METHODS =========================================

    //============================== PRIVATE METHODS ==========================================

    //============================== GETTERS / SETTERS ========================================

    //============================== INNER CLASSES ============================================

    public static class ServiceEntry extends AbstractEntry {

        private String serviceMode;
        private String deployState;
        private String deploymentError;
        private String projectName;
        private String clientId;

        private String dataModel;
        private String perfModel;
        private String projectId;
        private String runtimeIssues;
        private String runtimeIssuesParsed;
        private String nonExistentRealService;

        @XmlElement(name = "ServiceMode")
        public String getServiceMode() {
            return serviceMode;
        }

        public void setServiceMode(String serviceMode) {
            this.serviceMode = serviceMode;
        }

        @XmlElement(name = "VirtualServiceDeploymentState")
        public String getDeployState() {
            return deployState;
        }

        public void setDeployState(String deployState) {
            this.deployState = deployState;
        }

        @XmlElement(name = "VirtualServiceDeploymentErrorMessage")
        public String getDeploymentError() {
            return deploymentError;
        }

        public void setDeploymentError(String deploymentError) {
            this.deploymentError = deploymentError;
        }

        @XmlElement(name = "ProjectName")
        public String getProjectName() {
            return projectName;
        }

        public void setProjectName(String projectName) {
            this.projectName = projectName;
        }

        @XmlElement(name = "SessionClientId")
        public String getClientId() {
            return clientId;
        }

        public void setClientId(String clientId) {
            this.clientId = clientId;
        }

        @XmlElement(name = "DataModelId")
        public String getDataModel() {
            return dataModel;
        }

        public void setDataModel(String dataModel) {
            this.dataModel = dataModel;
        }

        @XmlElement(name = "PerformanceModelId")
        public String getPerfModel() {
            return perfModel;
        }

        public void setPerfModel(String perfModel) {
            this.perfModel = perfModel;
        }

        @XmlElement(name = "ProjectId")
        public String getProjectId() {
            return projectId;
        }

        public void setProjectId(String projectId) {
            this.projectId = projectId;
        }

        @XmlElement(name = "RuntimeIssues")
        public String getRuntimeIssues() {
            return runtimeIssues;
        }

        public void setRuntimeIssues(String runtimeIssues) {

            this.runtimeIssues = runtimeIssues;
            runtimeIssuesParsed = ParseRuntimeIssues();
        }

        private String ParseRuntimeIssues() {
            JsonParser jsonParser = new JsonParser();
            try {
                String result = "";
                JsonObject issuesJson = jsonParser.parse(runtimeIssues).getAsJsonObject();
                JsonArray issuesArr = issuesJson.getAsJsonArray("issues");
                for (JsonElement issue : issuesArr) {
                    JsonObject issueJson = issue.getAsJsonObject();
                    RuntimeIssue i = new RuntimeIssue();
                    i.type = issueJson.get("type").getAsString();
                    i.agentName = issueJson.get("agentName").getAsString();
                    if(i.type.equals("endpointDown")) {
                        i.endpointName = issueJson.get("endpointName").getAsString();
                    }
                    result+= i.Format() + ", ";
                }
                return result.length()==0 ? "No Issues" : result.substring(0, result.length()-2);
            } catch(Exception e){
                return "Cannot parse";
            }
        }

        public String getRuntimeIssuesParsed() {
            return runtimeIssuesParsed;
        }

        @XmlElement(name = "NonExistentRealService")
        public String getNonExistentRealService() {
            return nonExistentRealService;
        }

        public void setNonExistentRealService(String nonExistentRealService) {
            this.nonExistentRealService = nonExistentRealService;
        }

        public static class HashCodeObj {

            private String hashCode;
            private String value;

            @XmlElement(name = "hashCode", namespace = "http://schemas.datacontract.org/2004/07/HP.SOAQ.ServiceVirtualization.Model.Api.Common")
            public String getHashCode() {
                return hashCode;
            }

            public void setHashCode(String hashCode) {
                this.hashCode = hashCode;
            }

            @XmlElement(name = "value", namespace = "http://schemas.datacontract.org/2004/07/HP.SOAQ.ServiceVirtualization.Model.Api.Common")
            public String getValue() {
                return value;
            }

            public void setValue(String value) {
                this.value = value;
            }
        }

        private class RuntimeIssue{
            public String type;
            public String agentName;
            public String endpointName;

            public String Format(){
                return (type.equals("agentDown") ? "Agent " + agentName : "Endpoint " + endpointName) + " is down";
            }
        }
    }
}
