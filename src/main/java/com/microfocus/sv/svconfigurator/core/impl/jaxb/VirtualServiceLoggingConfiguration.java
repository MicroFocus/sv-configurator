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

import com.microfocus.sv.svconfigurator.core.impl.jaxb.helper.ReferenceElement;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;
import jakarta.xml.bind.annotation.XmlType;

@XmlRootElement(name = "virtualServiceLoggingConfiguration", namespace = VirtualServiceLoggingConfiguration.NAMESPACE)
@XmlType(propOrder = { "virtualService", "isEnabled" })
public class VirtualServiceLoggingConfiguration {

    public static final String NAMESPACE = "http://hp.com/SOAQ/ServiceVirtualization/2010/";

    private ReferenceElement virtualService;
    private boolean isEnabled;

    public VirtualServiceLoggingConfiguration() {

    }

    public VirtualServiceLoggingConfiguration(String virtualServiceId, boolean isEnabled) {
        this.virtualService = new ReferenceElement(virtualServiceId);
        this.isEnabled = isEnabled;
    }

    @XmlTransient
    public String getVirtualServiceId() {
        return this.virtualService.getRef();
    }
    public void setVirtualServiceId(String virtualServiceId) { this.virtualService = new ReferenceElement(virtualServiceId); }

    @XmlElement(name = "service", namespace = NAMESPACE)
    public ReferenceElement getVirtualService() {
        return this.virtualService;
    }
    public void setVirtualService(ReferenceElement virtualService) { this.virtualService = virtualService; }

    @XmlElement(name = "isEnabled", namespace = NAMESPACE)
    public boolean getIsEnabled() {
        return this.isEnabled;
    }
    public void setIsEnabled(boolean isEnabled) {
        this.isEnabled = isEnabled;
    }


}
