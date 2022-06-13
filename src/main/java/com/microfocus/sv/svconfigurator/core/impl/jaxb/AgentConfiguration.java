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

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "configuration", namespace = AgentConfigurations.NAMESPACE)
public class AgentConfiguration {

    private String agentId;

    private AgentEnabled enabled;

    private String name;

    private String type;

    private boolean running;

    @XmlAttribute(name = "agentId")
    public final String getAgentId() {
        return agentId;
    }

    public final void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    @XmlAttribute(name="enabled")
    public final AgentEnabled getEnabled() {
        return enabled;
    }

    public final void setEnabled(AgentEnabled enabled) {
        this.enabled = enabled;
    }

    @XmlAttribute(name = "name")
    public final String getName() {
        return name;
    }

    public final void setName(String name) {
        this.name = name;
    }

    @XmlAttribute(name = "type")
    public final String getType() {
        return type;
    }

    public final void setType(String type) {
        this.type = type;
    }

    @XmlEnum
    public enum AgentEnabled {
        @XmlEnumValue("True")
        TRUE,

        @XmlEnumValue("False")
        FALSE
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }
}
