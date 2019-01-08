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
package com.microfocus.sv.svconfigurator.cli.impl;

import java.util.List;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.microfocus.sv.svconfigurator.build.IProjectBuilder;
import com.microfocus.sv.svconfigurator.cli.impl.factory.CommandLineOptions;
import com.microfocus.sv.svconfigurator.core.IProject;
import com.microfocus.sv.svconfigurator.core.impl.Server;
import com.microfocus.sv.svconfigurator.core.impl.exception.AbstractSVCException;
import com.microfocus.sv.svconfigurator.core.impl.exception.CommandExecutorException;
import com.microfocus.sv.svconfigurator.core.impl.exception.CommunicatorException;
import com.microfocus.sv.svconfigurator.core.impl.exception.ProjectBuilderException;
import com.microfocus.sv.svconfigurator.core.impl.exception.SVCParseException;
import com.microfocus.sv.svconfigurator.core.server.IServerCommandRunner;
import com.microfocus.sv.svconfigurator.core.server.ServersCommandExecutor;
import com.microfocus.sv.svconfigurator.processor.IUnlockProcessor;
import com.microfocus.sv.svconfigurator.processor.UnlockProcessorInput;
import com.microfocus.sv.svconfigurator.serverclient.ICommandExecutor;
import com.microfocus.sv.svconfigurator.util.CliUtils;

public class UnlockCLICommandCliProcessor extends
        AbstractProjectCommandProcessor {
    // ============================== STATIC ATTRIBUTES
    // ========================================

    public static final String COMMAND = "unlock";
    private static final String PROP_LOCK = "l";
    private static final String LONG_PROP_LOCK = "lock";
    private static final String MAND_PROP_SERVICE = "service";
    private static final String HELP_USAGE = COMMAND + " [parameters] <"
            + MAND_PROP_SERVICE + ">";
    private static final Logger LOG = LoggerFactory
            .getLogger(UnlockCLICommandCliProcessor.class);

    // ============================== INSTANCE ATTRIBUTES
    // ======================================
    private IUnlockProcessor processor;
    private Options opts;

    // ============================== STATIC METHODS
    // ===========================================

    public UnlockCLICommandCliProcessor(IProjectBuilder projectBuilder,
            IUnlockProcessor processor) {
        super(projectBuilder);
        this.processor = processor;
        this.opts = this.createPropsOptions();
    }

    // ============================== CONSTRUCTORS
    // =============================================

    // ============================== ABSTRACT METHODS
    // =========================================

    // ============================== OVERRIDEN METHODS
    // ========================================

    @Override
    public int process(String[] args) {
        CommandLineParser parser = new BasicParser();

        try {
            CommandLine line = parser.parse(this.opts, args);

            String[] lineArgs = line.getArgs();
            if (lineArgs.length != 1) {
                throw new ParseException(
                        "There should be one mandatory parameter. Please, take a look into the help.");
            }

            IProject proj = null;
            if (line.hasOption(CommandLineOptions.PROP_PROJ)) { // project file
                                                                // specified
                String projPass = line
                        .hasOption(CommandLineOptions.PROPERTY_PROJ_PASSWORD) ? line
                        .getOptionValue(CommandLineOptions.PROPERTY_PROJ_PASSWORD)
                        : null;
                proj = this.getProject(
                        line.getOptionValue(CommandLineOptions.PROP_PROJ),
                        projPass);
            }

            List<Server> servers = CliUtils.obtainServers(line, proj);

            final UnlockProcessorInput input = new UnlockProcessorInput(
                    line.hasOption(PROP_LOCK), proj, lineArgs[0]);
            

            ServersCommandExecutor executor = new ServersCommandExecutor(servers, processor.getCommandExecutorFactory());
            executor.execute(new IServerCommandRunner() {
                
                @Override
                public void runCommand(ICommandExecutor executor)
                        throws AbstractSVCException {
                    processor.process(input, executor);
                }
            });
            
            LOG.info("Service lock change successful.");
        } catch (ParseException e) {
            LOG.error(e.getLocalizedMessage(), e);
            CliUtils.printHelp(HELP_USAGE, opts, this.createMandPropsOptions());
            return EXIT_CODE_PARSE;
        } catch (SVCParseException e) {
            LOG.error(e.getLocalizedMessage(), e);
            CliUtils.printHelp(HELP_USAGE, opts, this.createMandPropsOptions());
            return EXIT_CODE_PARSE;
        } catch (CommunicatorException e) {
            LOG.error(e.getLocalizedMessage(), e);
            return EXIT_CODE_COMMUNICATION;
        } catch (CommandExecutorException e) {
            LOG.error(e.getLocalizedMessage(), e);
            return EXIT_CODE_CONDITIONS;
        } catch (ProjectBuilderException e) {
            LOG.error(e.getLocalizedMessage(), e);
            return EXIT_CODE_PROJECT_BUILD;
        } catch (AbstractSVCException e) {
            LOG.error(e.getLocalizedMessage(), e);
            return EXIT_CODE_ABSTRACT_SV;
        }

        return EXIT_CODE_OK;
    }

    // ============================== INSTANCE METHODS
    // =========================================

    // ============================== PRIVATE METHODS
    // ==========================================

    private Options createPropsOptions() {
        Options opts = new Options();

        CliUtils.addConnectionOptions(opts);

        opts.addOption(PROP_LOCK, LONG_PROP_LOCK, false,
                "Locks the service with your ID after the unlock.");

        opts.addOption(
                CommandLineOptions.PROP_PROJ,
                CommandLineOptions.LONG_PROP_PROJ,
                true,
                "Processes the project file (.vproja or .vproj). You have to specify "
                        + "the project file either if you want to identify the service by its name or if you want to obtain the "
                        + "server management URL from the project.");

        opts.addOption(CommandLineOptions.PROPERTY_PROJ_PASSWORD,
                CommandLineOptions.LONG_PROPERTY_PROJ_PASSWORD, true,
                "Project encryption password");

        return opts;
    }

    private Options createMandPropsOptions() {
        Options opts = new Options();

        opts.addOption(MAND_PROP_SERVICE, false,
                "Service identification (name or ID) to be unlocked.");

        return opts;
    }

    // ============================== GETTERS / SETTERS
    // ========================================

    // ============================== INNER CLASSES
    // ============================================

}
