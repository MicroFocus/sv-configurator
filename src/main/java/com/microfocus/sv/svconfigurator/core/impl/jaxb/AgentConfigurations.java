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

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "agentConfigurations", namespace = AgentConfigurations.NAMESPACE)
public class AgentConfigurations {

    public static final String NAMESPACE = "http://hp.com/SOAQ/ServiceVirtualization/2010/agents";

    private List<AgentConfiguration> configurations;

    @XmlElement(name="configuration", namespace = AgentConfigurations.NAMESPACE)
    public final List<AgentConfiguration> getConfigurations() {
        return configurations;
    }

    public final void setConfigurations(List<AgentConfiguration> configurations) {
        this.configurations = configurations;
    }
}
