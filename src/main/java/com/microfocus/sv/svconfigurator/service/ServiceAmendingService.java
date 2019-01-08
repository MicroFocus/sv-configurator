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
package com.microfocus.sv.svconfigurator.service;

import java.util.List;
import java.util.Map;

import com.microfocus.sv.svconfigurator.core.IService;
import com.microfocus.sv.svconfigurator.core.impl.exception.CommandExecutorException;
import com.microfocus.sv.svconfigurator.core.impl.jaxb.AgentConfigurations;

public interface ServiceAmendingService {

    /**
     * Verifies that all the services have an existing agent and if not it tries to replace it by another agent of the
     * same type.
     * 
     * @throws CommandExecutorException
     */
    void agentFallback() throws CommandExecutorException;

    /**
     * Remaps source Agent IDs (usually from vproj) to destination Agent IDs (usually from the server)
     *
     * @param agentRemapping
     *            Key-value collection of Agent IDs where key is source and value is destination Agent ID.
     * @throws CommandExecutorException
     */
    void remapAgents(Map<String,String> agentRemapping) throws CommandExecutorException;

    /**
     * Replaces missing source agents by existing agents of the same type and name
     *
     * @throws CommandExecutorException
     */
    void remapAgentsByNames() throws CommandExecutorException;

    void verifyAndSetNames() throws CommandExecutorException;
}
