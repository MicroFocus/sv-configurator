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

import java.util.List;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.microfocus.sv.svconfigurator.build.IProjectBuilder;
import com.microfocus.sv.svconfigurator.build.ProjectBuilder;
import com.microfocus.sv.svconfigurator.cli.ICLICommandProcessor;
import com.microfocus.sv.svconfigurator.cli.impl.factory.CommandLineOptions;
import com.microfocus.sv.svconfigurator.core.IProject;
import com.microfocus.sv.svconfigurator.core.impl.Server;
import com.microfocus.sv.svconfigurator.core.impl.exception.AbstractSVCException;
import com.microfocus.sv.svconfigurator.core.impl.exception.CommunicatorException;
import com.microfocus.sv.svconfigurator.core.impl.exception.ProjectBuilderException;
import com.microfocus.sv.svconfigurator.core.server.IServerCommandRunner;
import com.microfocus.sv.svconfigurator.core.server.ServersCommandExecutor;
import com.microfocus.sv.svconfigurator.processor.UpdateProcessor;
import com.microfocus.sv.svconfigurator.serverclient.ICommandExecutor;
import com.microfocus.sv.svconfigurator.util.CliUtils;

public class UpdateCommandProcessor extends AbstractProjectCommandProcessor
        implements ICLICommandProcessor {

    private static final Logger LOG = LoggerFactory
            .getLogger(UpdateCommandProcessor.class);

    public static final String COMMAND = "update";

    private static final String PARAM_SERVICE = "s";
    private static final String LONG_PARAM_SERVICE = "service";
    private static final String MANDAT_PROP_PROJ = "project_file";
    private static final String HELP_USAGE = COMMAND + " [parameters] <"
            + MANDAT_PROP_PROJ + ">";

    private final Options opts;
    private final UpdateProcessor updateProcessor;

    public UpdateCommandProcessor(IProjectBuilder projectBuilder,
            UpdateProcessor proc) {
        super(projectBuilder);
        this.updateProcessor = proc;
        this.opts = this.createParamOptions();
    }

    @Override
    public int process(String[] args) {
        CommandLineParser parser = new BasicParser();

        try {
            CommandLine line = parser.parse(this.opts, args);
            final String projectPath = getProjectPath(line);
            if (projectPath == null) {
                throw new ParseException("You have to specify the project.");
            }
            final String projectPassword = getProjectPassword(line);

            final IProject proj = getProject(projectPath, projectPassword);
            if (proj == null) {
                throw new ParseException("You have to specify the project.");
            }

            List<Server> servers = CliUtils.obtainServers(line, null, true);
            if (!line.hasOption(PARAM_SERVICE)) {
                throw new ParseException(
                        "You must specify a service that will be updated.");
            }
            final String service = line.getOptionValue(PARAM_SERVICE);

            ServersCommandExecutor executor = new ServersCommandExecutor(
                    servers, updateProcessor.getCommandExecutorFactory());
            executor.execute(new IServerCommandRunner() {

                @Override
                public void runCommand(ICommandExecutor executor)
                        throws AbstractSVCException {
                    updateProcessor.process(executor, projectPath,
                            projectPassword, proj, service);
                }
            });
        } catch (ParseException e) {
            LOG.error(e.getLocalizedMessage(), e);
            CliUtils.printHelp(HELP_USAGE, opts, this.createMandatParamOptions());
            return EXIT_CODE_PARSE;
        } catch (ProjectBuilderException e) {
            LOG.error(e.getLocalizedMessage(), e);
            return EXIT_CODE_PROJECT_BUILD;
        } catch (CommunicatorException e) {
            LOG.error(e.getLocalizedMessage(), e);
            return EXIT_CODE_COMMUNICATION;
        } catch (AbstractSVCException e) {
            LOG.error(e.getLocalizedMessage(), e);
            return EXIT_CODE_CONDITIONS;
        }

        return EXIT_CODE_OK;
    }

    @Override
    protected IProject getProject(String filePath, String password)
            throws ProjectBuilderException {
        if (!filePath.endsWith(ProjectBuilder.VPROJ_SUFFIX)) {
            throw new ProjectBuilderException(
                    "Cannot update SV project archive. Only unpacked project archives can be updated.");
        }
        return super.getProject(filePath, password);
    }

    private Options createParamOptions() {
        Options opts = new Options();

        CliUtils.addConnectionOptions(opts);

        opts.addOption(PARAM_SERVICE, LONG_PARAM_SERVICE, true,
                "A service that will be updated (id or name).");

        opts.addOption(CommandLineOptions.PROPERTY_PROJ_PASSWORD,
                CommandLineOptions.LONG_PROPERTY_PROJ_PASSWORD, true,
                "Project encryption password");

        return opts;
    }

    private Options createMandatParamOptions() {
        Options opts = new Options();

        opts.addOption(MANDAT_PROP_PROJ, false,
                "Project file (.vproj) for the update.");

        return opts;
    }
}
