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
package com.microfocus.sv.svconfigurator;

import java.util.Arrays;
import java.util.Collection;

import com.microfocus.sv.svconfigurator.cli.impl.factory.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.microfocus.sv.svconfigurator.cli.ICLICommandProcessor;
import com.microfocus.sv.svconfigurator.cli.ICLICommandProcessorFactory;
import com.microfocus.sv.svconfigurator.cli.ICLICommandRegistry;
import com.microfocus.sv.svconfigurator.cli.impl.CLICommandRegistry;
import com.microfocus.sv.svconfigurator.core.impl.exception.SVCParseException;
import com.microfocus.sv.svconfigurator.serverclient.ICommandExecutorFactory;
import com.microfocus.sv.svconfigurator.serverclient.impl.CommandExecutorFactory;

public class Main {

    static {
        LogConf.configure();
    }

    private static final Logger LOG = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws Exception {
        LOG.debug("Initializing the SVConfigurator instance.");
        ICommandExecutorFactory executorFactory = new CommandExecutorFactory();

        Collection<ICLICommandProcessorFactory> factories = Arrays.asList(
                (ICLICommandProcessorFactory) new ChmodeCLICommandProcessorFactory(executorFactory),
                new DeployCLICommandProcessorFactory(executorFactory),
                new UndeployCLICommandProcessorFactory(executorFactory),
                new ListCLICommandProcessorFactory(executorFactory),
                new UnlockCLICommandProcessorFactory(executorFactory),
                new ViewCLICommandProcessorFactory(executorFactory),
                new HotSwapCLICommandProcessorFactory(executorFactory),
                new UpdateCommandProcessorFactory(executorFactory),
                new ExportCommandProcessorFactory(executorFactory),
                new ListProjectCLICommandProcessorFactory(),
                new SetLoggingCliCommandProcessorFactory(executorFactory)
            );

        ICLICommandRegistry registry = new CLICommandRegistry(factories);

        LOG.debug("Command Registry initialized.");

        try {
            if (args.length == 0) {
                throw new SVCParseException("You have to specify a command you want to execute.");
            }
            String command = args[0];
            String[] commandArgs = new String[args.length - 1];
            System.arraycopy(args, 1, commandArgs, 0, args.length - 1);

            ICLICommandProcessor cliProc = registry.lookupCommandProcessor(command);
            int res = cliProc.process(commandArgs);
            System.exit(res);
        } catch (SVCParseException e) {
            LOG.error(e.getLocalizedMessage());
            LOG.info(registry.getCLICommandHelp());
            System.exit(ICLICommandProcessor.EXIT_CODE_PARSE);
        }

    }

}
