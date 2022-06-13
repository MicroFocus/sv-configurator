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
package com.microfocus.sv.svconfigurator.core.server;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.microfocus.sv.svconfigurator.core.impl.Server;
import com.microfocus.sv.svconfigurator.core.impl.exception.AbstractSVCException;
import com.microfocus.sv.svconfigurator.core.impl.exception.CommandExecutorException;
import com.microfocus.sv.svconfigurator.serverclient.ICommandExecutor;
import com.microfocus.sv.svconfigurator.serverclient.ICommandExecutorFactory;
import com.microfocus.sv.svconfigurator.util.CliUtils;

public class ServersCommandExecutor {

    private static final Logger LOG = LoggerFactory.getLogger(ServersCommandExecutor.class);
    
    private final List<Server> servers;
    private final ICommandExecutorFactory commandExecutorFactory;

    public ServersCommandExecutor(List<Server> servers, ICommandExecutorFactory commandExecutorFactory) throws CommandExecutorException {
        if (servers == null) {
            throw new CommandExecutorException("No servers defined");
        }
        if (commandExecutorFactory == null) {
            throw new CommandExecutorException("No command executor factory defined.");
        }
        this.servers = new ArrayList<Server>(servers);
        this.commandExecutorFactory = commandExecutorFactory;
    }

    public void execute(IServerCommandRunner runner) throws AbstractSVCException {
        if (runner == null) {
            throw new CommandExecutorException("No runner defined");
        }
        
        for (Server server : servers) {
            ICommandExecutor executor;
            try {    
                executor = commandExecutorFactory.createCommandExecutor(server.getURL(), server.isTrustEveryone(), server.getCredentials());
            } catch (AbstractSVCException e) {
                throw new CommandExecutorException("Failed to initiate connection to " + getServerIdentifier(server) + ": " + e.getMessage(), e);
            } catch (Exception e) {
                throw new CommandExecutorException("Failed to initiate connection to " + getServerIdentifier(server), e);
            }
            
            if (executor == null) {
                throw new CommandExecutorException("No command executor found for " + getServerIdentifier(server));
            }
            
            try {
                LOG.debug("Running command for " + getServerIdentifier(server) + "'...");
                runner.runCommand(executor);
                LOG.info("Command successfully finished for " + getServerIdentifier(server));
            } catch (AbstractSVCException e) {
                throw new CommandExecutorException("Failed to run command for " + getServerIdentifier(server) + ": " + e.getMessage(), e);
            } catch (Exception e) {
                throw new CommandExecutorException("Failed to run command for " + getServerIdentifier(server), e);
            }
        }
    }

    private String getServerIdentifier(Server server) {
        if (CliUtils.DEFAULT_SERVER_ID.equals(server.getId())) {
            return "server " + server.getURL();
        }
        return "server " + server.getURL() + " (id: " + server.getId() + ")";
    }
}
