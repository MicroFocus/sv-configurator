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
package com.microfocus.sv.svconfigurator.cli.impl.base;


import com.microfocus.sv.svconfigurator.core.impl.Server;
import com.microfocus.sv.svconfigurator.core.impl.exception.AbstractSVCException;
import com.microfocus.sv.svconfigurator.core.server.IServerCommandRunner;
import com.microfocus.sv.svconfigurator.core.server.ServersCommandExecutor;
import com.microfocus.sv.svconfigurator.serverclient.ICommandExecutor;
import com.microfocus.sv.svconfigurator.serverclient.ICommandExecutorFactory;
import com.microfocus.sv.svconfigurator.serverclient.impl.CommandExecutorFactory;
import com.microfocus.sv.svconfigurator.util.CliUtils;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;

import java.util.List;

public abstract class CliServerCommandProcessorBase extends CliCommandProcessorBase {

    public CliServerCommandProcessorBase(String command) {
        super(command);
    }

    @Override
    protected void addCommandLineOptions(Options options) {
        super.addCommandLineOptions(options);
        CliUtils.addConnectionOptions(options);
    }

    @Override
    protected final int execute(final CommandLine parameters) {
        try {
            if (!initParameters(parameters)){
                printHelp();
                return EXIT_CODE_PARSE;
            }

            List<Server> servers = CliUtils.obtainServers(parameters, null, true);
            ServersCommandExecutor executor = new ServersCommandExecutor(servers, this.createCommandExecutorFactory());

            executor.execute(new IServerCommandRunner() {
                @Override
                public void runCommand(ICommandExecutor executor) throws AbstractSVCException {
                    executeWithServerExecutor(parameters, executor);
                }
            });
        }
        catch (AbstractSVCException e) {
            log.error(e.getLocalizedMessage(), e);
            return EXIT_CODE_ABSTRACT_SV;
        }
        return EXIT_CODE_OK;
    }

    /**
     * Checks command parameters and initialize command
     * @param parameters
     * @return true if passed, false to indicate parameters error
     */
    protected boolean initParameters(CommandLine parameters){
        return true;
    }

    protected abstract int executeWithServerExecutor(CommandLine parameters, ICommandExecutor executor) throws AbstractSVCException;

    protected ICommandExecutorFactory createCommandExecutorFactory(){
        return new CommandExecutorFactory();
    }

}
