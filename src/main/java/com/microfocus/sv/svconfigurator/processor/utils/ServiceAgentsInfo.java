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
package com.microfocus.sv.svconfigurator.processor.utils;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.microfocus.sv.svconfigurator.core.IService;
import com.microfocus.sv.svconfigurator.core.impl.exception.SVCParseException;
import com.microfocus.sv.svconfigurator.core.impl.jaxb.ServiceRuntimeConfiguration;
import com.microfocus.sv.svconfigurator.util.XmlUtils;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class ServiceAgentsInfo {
    public class AgentRef {
        String id;
        String name;

        public AgentRef(String id, String name) {
            this.id = id;
            this.name = name;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public void setId(String id) {
            setModified(this.id, id);
            this.id = id;
        }

        public void setName(String name) {
            setModified(this.name, name);
            this.name = name;
        }

        private void setModified(String oldValue, String newValue) {
            if (oldValue == null ? newValue != null : !oldValue.equals(newValue)) {
                modified = true;
            }
        }
    }

    String agentType;
    Map<String, AgentRef> agentRefs = new HashMap<String, AgentRef>();
    boolean modified;

    public static final String ATTR_NAME = "name";
    public static final String ATTR_REF = "ref";

    public ServiceAgentsInfo(IService service) throws IOException, SVCParseException {
        Document doc = XmlUtils.createDoc(service.getData());
        doc.getDocumentElement().normalize();

        Node virtualEndpoint = doc.getElementsByTagNameNS(ServiceRuntimeConfiguration.NAMESPACE, "virtualEndpoint").item(0);
        agentType = virtualEndpoint.getAttributes().getNamedItem("type").getNodeValue();

        for (String nodeName : Arrays.asList("virtualInputAgent", "virtualOutputAgent", "realInputAgent",
                "realOutputAgent")) {

            NamedNodeMap agentAttributes = doc.getElementsByTagNameNS(ServiceRuntimeConfiguration.NAMESPACE, nodeName)
                    .item(0).getAttributes();

            Node idAttr = agentAttributes.getNamedItem(ATTR_REF);
            String id = idAttr.getNodeValue();
            Node nameAttr = agentAttributes.getNamedItem(ATTR_NAME);
            String name = (nameAttr != null) ? nameAttr.getNodeValue() : null;

            agentRefs.put(nodeName, new AgentRef(id, name));
        }
    }

    public boolean isModified() {
        return modified;
    }

    public String getAgentType() {
        return agentType;
    }

    public Map<String, AgentRef> getAgentRefs() {
        return agentRefs;
    }
}
