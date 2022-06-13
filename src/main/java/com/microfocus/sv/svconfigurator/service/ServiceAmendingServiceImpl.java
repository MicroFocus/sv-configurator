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
package com.microfocus.sv.svconfigurator.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import com.microfocus.sv.svconfigurator.core.impl.exception.SVCParseException;
import com.microfocus.sv.svconfigurator.processor.utils.ServiceAgentsInfo;
import com.microfocus.sv.svconfigurator.util.XmlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;

import com.microfocus.sv.svconfigurator.core.IService;
import com.microfocus.sv.svconfigurator.core.impl.ServiceDataDecorator;
import com.microfocus.sv.svconfigurator.core.impl.datasource.InMemoryProjectElementDataSource;
import com.microfocus.sv.svconfigurator.core.impl.exception.CommandExecutorException;
import com.microfocus.sv.svconfigurator.core.impl.jaxb.AgentConfiguration;
import com.microfocus.sv.svconfigurator.core.impl.jaxb.AgentConfigurations;
import com.microfocus.sv.svconfigurator.core.impl.jaxb.ServiceRuntimeConfiguration;

interface ServiceAgentUpdater {
    String getMappedServerAgentId(String sourceAgentType, String sourceAgentId, String sourceAgentName) throws CommandExecutorException;
    String getServerAgentName(String desiredAgentType, String id);
}

abstract class ServiceAgentUpdaterBase implements ServiceAgentUpdater {
    protected Map<String, List<AgentConfiguration>> agentConfigurationsByType;
    protected Map<String, AgentConfiguration> agentConfigurationsById;
    protected Map<String, AgentConfiguration> agentConfigurationsByName;

    public ServiceAgentUpdaterBase(AgentConfigurations agentConfigurations) {
        agentConfigurationsByType = new HashMap<String, List<AgentConfiguration>>();
        agentConfigurationsById = new HashMap<String, AgentConfiguration>();
        agentConfigurationsByName = new HashMap<String, AgentConfiguration>();
        for (AgentConfiguration agentConfiguration : agentConfigurations.getConfigurations()) {
            if (!agentConfigurationsByType.containsKey(agentConfiguration.getType())) {
                List<AgentConfiguration> initialList = new ArrayList<AgentConfiguration>();
                initialList.add(agentConfiguration);
                agentConfigurationsByType.put(agentConfiguration.getType(), initialList);
            } else {
                agentConfigurationsByType.get(agentConfiguration.getType()).add(agentConfiguration);
            }
            agentConfigurationsById.put(agentConfiguration.getAgentId(), agentConfiguration);
            agentConfigurationsByName.put(agentConfiguration.getName(), agentConfiguration);
        }
    }

    public String getServerAgentName(String desiredAgentType, String id) {
        AgentConfiguration agentConfiguration = agentConfigurationsById.get(id);
        if (agentConfiguration != null && agentConfiguration.getType().equals(desiredAgentType)) {
            return agentConfiguration.getName();
        }
        return null;
    }
}

class AgentFallbackServiceUpdater extends ServiceAgentUpdaterBase {
    public AgentFallbackServiceUpdater(AgentConfigurations agentConfigurations) {
        super(agentConfigurations);
    }

    @Override
    public String getMappedServerAgentId(String sourceAgentType, String sourceAgentId, String sourceAgentName) throws CommandExecutorException {
        if (agentConfigurationsById.containsKey(sourceAgentId)) {
            return sourceAgentId;
        }

        List<AgentConfiguration> agentConfigurationsOfType = agentConfigurationsByType.get(sourceAgentType);
        if (agentConfigurationsOfType == null) {
            throw new CommandExecutorException(MessageFormat.format("No available agent of type [{0}] found on the server.", sourceAgentType));
        }
        // try to find any running instance of desired type
        for (AgentConfiguration conf : agentConfigurationsOfType) {
            if (conf.isRunning()) {
                return conf.getAgentId();
            }
        }
        // return first agent of that type if none is running
        return agentConfigurationsOfType.get(0).getAgentId();
    }
}

class AgentRemappingServiceUpdater extends ServiceAgentUpdaterBase {

    protected Map<String,String> agentRemapping;

    protected String getMappedAgentId(String sourceAgentId) {
        if(agentRemapping != null && agentRemapping.containsKey(sourceAgentId)) {
            return agentRemapping.get(sourceAgentId);
        } else {
            return null;
        }
    }

    public AgentRemappingServiceUpdater(AgentConfigurations agentConfigurations, Map<String,String> agentRemapping) {
        super(agentConfigurations);
        this.agentRemapping = agentRemapping;
    }

    @Override
    public String getMappedServerAgentId(String sourceAgentType, String sourceAgentId, String sourceAgentName) throws CommandExecutorException {
        String mappedAgentId = getMappedAgentId(sourceAgentId);
        if (mappedAgentId == null) {
            // agent not mapped
            return sourceAgentId;
        }

        if (!agentConfigurationsById.containsKey(mappedAgentId)
                || !agentConfigurationsById.get(mappedAgentId).getType().equals(sourceAgentType)) {
            throw new CommandExecutorException(
                    MessageFormat.format("No available agent of ID [{0}] and type [{1}] found on the server. " +
                                    "Please check the agent has the correct type and also note that Agent ID is case sensitive.",
                            mappedAgentId, sourceAgentType));
        }

        return mappedAgentId;
    }
}

/**
 * This Updated is supposed to be the last in the chain because it verifies that target agent exists on the server
 */
class AgentByNameRemappingServiceUpdater extends ServiceAgentUpdaterBase {

    public AgentByNameRemappingServiceUpdater(AgentConfigurations agentConfigurations) {
        super(agentConfigurations);
    }

    @Override
    public String getMappedServerAgentId(String sourceAgentType, String sourceAgentId, String sourceAgentName) throws CommandExecutorException {
        // try it by name
        if (sourceAgentName != null) {
            AgentConfiguration agentConfigByName = agentConfigurationsByName.get(sourceAgentName);
            if (agentConfigByName != null && agentConfigByName.getType().equals(sourceAgentType)) {
                return agentConfigByName.getAgentId();
            }
        }
        return null;
    }
}

class ValidateAndSetNameServiceUpdater extends ServiceAgentUpdaterBase {

    public ValidateAndSetNameServiceUpdater(AgentConfigurations agentConfigurations) {
        super(agentConfigurations);
    }

    @Override
    public String getMappedServerAgentId(String sourceAgentType, String sourceAgentId, String sourceAgentName) throws CommandExecutorException {
        AgentConfiguration agentConfiguration = agentConfigurationsById.get(sourceAgentId);
        if (agentConfiguration == null || !sourceAgentType.equals(agentConfiguration.getType())) {
            throw new CommandExecutorException(
                    MessageFormat.format("No available agent of ID [{0}] or name [{1}] and type [{2}] found on the server. "
                                    + "Please check the agent has the correct type and also note that Agent ID and name are case sensitive.",
                            sourceAgentId, sourceAgentName, sourceAgentType));
        }
        return sourceAgentId;
    }
}

public class ServiceAmendingServiceImpl implements ServiceAmendingService {

    private static final Logger LOG = LoggerFactory.getLogger(ServiceAmendingServiceImpl.class);
    private AgentConfigurations agentConfigurations;
    private Map<String, ServiceAgentsInfo> agentInfoMap;
    private List<IService> services;

    public ServiceAmendingServiceImpl(AgentConfigurations agentConfigurations, List<IService> services) throws CommandExecutorException {
        this.agentConfigurations = agentConfigurations;
        this.services = services;
        this.agentInfoMap = createAgentInfoMap(services);
    }

    private static Map<String, ServiceAgentsInfo> createAgentInfoMap(List<IService> services) throws CommandExecutorException {
        Map<String, ServiceAgentsInfo> agentInfoMap = new HashMap<String, ServiceAgentsInfo>();
        for (IService svc : services) {
            try {
                agentInfoMap.put(svc.getId(), new ServiceAgentsInfo(svc));
            } catch (Exception e) {
                throw new CommandExecutorException(String.format("Failed to parse agent references of service %s [%s]", svc.getName(), svc.getId()));
            }
        }
        return agentInfoMap;
    }

    @Override
    public void agentFallback() throws CommandExecutorException {
        amendServiceAgents(services, new AgentFallbackServiceUpdater(agentConfigurations));
    }

    @Override
    public void remapAgents(Map<String, String> agentRemapping) throws CommandExecutorException {
        amendServiceAgents(services, new AgentRemappingServiceUpdater(agentConfigurations, agentRemapping));
    }

    @Override
    public void remapAgentsByNames() throws CommandExecutorException {
        amendServiceAgents(services, new AgentByNameRemappingServiceUpdater(agentConfigurations));
    }

    @Override
    public void verifyAndSetNames() throws CommandExecutorException {
        amendServiceAgents(services, new ValidateAndSetNameServiceUpdater(agentConfigurations));
    }


    private void amendServiceAgents(List<IService> services, ServiceAgentUpdater serviceAgentUpdater) throws CommandExecutorException {
        for (IService svc : services) {
            ServiceAgentsInfo agentsInfo = agentInfoMap.get(svc.getId());
            for (ServiceAgentsInfo.AgentRef agentRef : agentsInfo.getAgentRefs().values()) {
                try {
                    String newId = serviceAgentUpdater.getMappedServerAgentId(agentsInfo.getAgentType(), agentRef.getId(), agentRef.getName());
                    if (newId != null) {
                        agentRef.setId(newId);

                        String newName = serviceAgentUpdater.getServerAgentName(agentsInfo.getAgentType(), newId);
                        if (newName != null) {
                            agentRef.setName(newName);
                        }
                    }
                } catch (CommandExecutorException e) {
                    throw new CommandExecutorException(String.format("Failed to process service '%s' [id: %s]. Detail: %s", svc.getName(), svc.getId(), e.getMessage()), e);
                }
            }
        }
    }

    public List<IService> applyAgentChanges() throws CommandExecutorException {
        List<IService> res = new ArrayList<IService>(services.size());
        for (IService svc : services) {
            ServiceAgentsInfo agentsInfo = agentInfoMap.get(svc.getId());
            if (!agentsInfo.isModified()) {
                res.add(svc);
            } else {
                try {
                    Document doc = XmlUtils.createDoc(svc.getData());
                    doc.getDocumentElement().normalize();

                    for (Map.Entry<String, ServiceAgentsInfo.AgentRef> entry : agentsInfo.getAgentRefs().entrySet()) {
                        NamedNodeMap agentAttributes = doc.getElementsByTagNameNS(ServiceRuntimeConfiguration.NAMESPACE, entry.getKey())
                                .item(0).getAttributes();

                        setAttribute(doc, agentAttributes, ServiceAgentsInfo.ATTR_REF, entry.getValue().getId());
                        setAttribute(doc, agentAttributes, ServiceAgentsInfo.ATTR_NAME, entry.getValue().getName());
                    }

                    IService updatedSvc = createModifiedService(svc, doc);
                    res.add(updatedSvc);
                } catch (Exception e) {
                    throw new CommandExecutorException(String.format("Failed to parse serialize service %s [%s]", svc.getName(), svc.getId()));
                }
            }
        }
        return res;
    }

    private IService createModifiedService(IService svc, Document doc) throws TransformerException, IOException, SVCParseException {
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        transformer.transform(new DOMSource(doc), new StreamResult(os));

        byte[] data = os.toByteArray();
        return new ServiceDataDecorator(svc, new InMemoryProjectElementDataSource(data));
    }

    private void setAttribute(Document doc, NamedNodeMap agentAttributes, String attrName, String attrValue) {
        Attr attr = doc.createAttribute(attrName);
        attr.setNodeValue(attrValue);
        agentAttributes.setNamedItem(attr);
    }
}
