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
package com.microfocus.sv.svconfigurator.cli.impl;

import java.io.IOException;
import java.util.List;

import com.microfocus.sv.svconfigurator.build.IProjectBuilder;
import com.microfocus.sv.svconfigurator.cli.impl.factory.CommandLineOptions;
import com.microfocus.sv.svconfigurator.core.IProject;
import com.microfocus.sv.svconfigurator.core.impl.exception.CommandExecutorException;
import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.microfocus.sv.svconfigurator.cli.ICLICommandProcessor;
import com.microfocus.sv.svconfigurator.core.impl.Server;
import com.microfocus.sv.svconfigurator.core.impl.exception.AbstractSVCException;
import com.microfocus.sv.svconfigurator.core.impl.exception.CommunicatorException;
import com.microfocus.sv.svconfigurator.core.impl.exception.SVCParseException;
import com.microfocus.sv.svconfigurator.core.server.IServerCommandRunner;
import com.microfocus.sv.svconfigurator.core.server.ServersCommandExecutor;
import com.microfocus.sv.svconfigurator.processor.ExportProcessor;
import com.microfocus.sv.svconfigurator.serverclient.ICommandExecutor;
import com.microfocus.sv.svconfigurator.util.CliUtils;

public class ExportCommandProcessor extends AbstractProjectCommandProcessor implements ICLICommandProcessor {

    public static final String COMMAND = "export";
    private static final String PARAM_SERVICE = "s";
    private static final String LONG_PARAM_SERVICE = "service";
    private static final String PARAM_DIR = "d";
    private static final String LONG_PARAM_DIR = "directory";
    private static final String PARAM_ARCHIVE = "a";
    private static final String LONG_PARAM_ARCHIVE = "archive";
    private static final String PARAM_CONTINUE_ON_ERROR = "f";
    private static final String LONG_PARAM_CONTINUE_ON_ERROR = "continue-on-error";
    private static final String HELP_USAGE = COMMAND + " [parameters]";

    private static final Logger LOG = LoggerFactory.getLogger(DeployCLICommandProcessor.class);

    private ExportProcessor exportProcessor;
    private Options opts;

    public ExportCommandProcessor(ExportProcessor exportProcessor, IProjectBuilder projectBuilder) {
        super(projectBuilder);
        this.exportProcessor = exportProcessor;
        this.opts = this.createParamOptions();
    }

    @Override
    public int process(String[] args) {
        CommandLineParser parser = new BasicParser();

        try {
            CommandLine line = parser.parse(this.opts, args);
            List<Server> servers = CliUtils.obtainServers(line, null, true);
            final String directory =  line.getOptionValue(PARAM_DIR);
            if (directory == null) {
                throw new ParseException("You have to specify the output directory.");
            }

            final boolean ignoreErrors = line.hasOption(PARAM_CONTINUE_ON_ERROR);
            final boolean exportAsArchive = line.hasOption(PARAM_ARCHIVE);
            final boolean includeLoggedMessages = line.hasOption(CommandLineOptions.LONG_PARAM_WITH_LOGGED_MESSAGES);

            final String svc = line.hasOption(PARAM_SERVICE) ? line.getOptionValue(PARAM_SERVICE) : null;
            final IProject project = getProject(line);

            ServersCommandExecutor executor = new ServersCommandExecutor(servers, exportProcessor.getCommandExecutorFactory());
            executor.execute(new IServerCommandRunner() {

                @Override
                public void runCommand(ICommandExecutor executor) throws AbstractSVCException {
                    try {
                        exportProcessor.process(executor, directory, svc, project, ignoreErrors, exportAsArchive, includeLoggedMessages);
                    } catch (CommunicatorException e) {
                        throw e;
                    } catch (IOException e) {
                        throw new CommunicatorException(e.getMessage(), e);
                    } catch (Exception e) {
                        throw new CommandExecutorException(e.getMessage(), e);
                    }
                }
            });
        } catch (ParseException e) {
            LOG.error(e.getLocalizedMessage(), e);
            CliUtils.printHelp(HELP_USAGE, opts, new Options());
            return EXIT_CODE_PARSE;
        } catch (SVCParseException e) {
            LOG.error(e.getLocalizedMessage(), e);
            CliUtils.printHelp(HELP_USAGE, opts, new Options());
            return EXIT_CODE_PARSE;
        } catch (AbstractSVCException e) {
            LOG.error(e.getLocalizedMessage(), e);
            return EXIT_CODE_COMMUNICATION;
        }

        return EXIT_CODE_OK;
    }

    private Options createParamOptions() {
        Options opts = new Options();

        CliUtils.addConnectionOptions(opts);

        opts.addOption(PARAM_SERVICE, LONG_PARAM_SERVICE, true,
                "If you specify this parameter, only the specified service will be exported.");
        opts.addOption(PARAM_DIR, LONG_PARAM_DIR, true, "Output directory.");
        opts.addOption(PARAM_CONTINUE_ON_ERROR, LONG_PARAM_CONTINUE_ON_ERROR, false, "Continue without error if export of any service failed.");
        opts.addOption(PARAM_ARCHIVE, LONG_PARAM_ARCHIVE, false, "Export as project archives (.vproja).");
        opts.addOption(null, CommandLineOptions.LONG_PARAM_WITH_LOGGED_MESSAGES, false, "Include logged messages");
        opts.addOption(CommandLineOptions.PROP_PROJ, CommandLineOptions.LONG_PROP_PROJ, true, "Project file (.vproj or .vproja) to define set of services to be exported.");
        opts.addOption(CommandLineOptions.PROPERTY_PROJ_PASSWORD, CommandLineOptions.LONG_PROPERTY_PROJ_PASSWORD, true, "Project encryption password");
        return opts;
    }
}
