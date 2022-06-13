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

import com.microfocus.sv.svconfigurator.core.impl.exception.*;
import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.microfocus.sv.svconfigurator.build.IProjectBuilder;
import com.microfocus.sv.svconfigurator.cli.ICLICommandProcessor;
import com.microfocus.sv.svconfigurator.cli.impl.factory.CommandLineOptions;
import com.microfocus.sv.svconfigurator.core.IProject;
import com.microfocus.sv.svconfigurator.core.impl.Server;
import com.microfocus.sv.svconfigurator.core.server.IServerCommandRunner;
import com.microfocus.sv.svconfigurator.core.server.ServersCommandExecutor;
import com.microfocus.sv.svconfigurator.processor.IUndeployProcessor;
import com.microfocus.sv.svconfigurator.processor.UndeployProcessorInput;
import com.microfocus.sv.svconfigurator.serverclient.ICommandExecutor;
import com.microfocus.sv.svconfigurator.util.CliUtils;
import com.microfocus.sv.svconfigurator.util.StringUtils;

public class UndeployCLICommandProcessor extends
        AbstractProjectCommandProcessor implements ICLICommandProcessor {

    public static final String COMMAND = "undeploy";
    private static final String HELP_USAGE = COMMAND + " [parameters]";
    private static final String PARAM_FORCE = "f";
    private static final String LONG_PARAM_FORCE = "force";
    private static final String PARAM_SERVICE = "s";
    private static final String LONG_PARAM_SERVICE = "service";
    private static final Logger LOG = LoggerFactory
            .getLogger(DeployCLICommandProcessor.class);

    private IUndeployProcessor proc;
    private Options opts;

    public UndeployCLICommandProcessor(IProjectBuilder projectBuilder,
            IUndeployProcessor proc) {
        super(projectBuilder);

        this.proc = proc;
        this.opts = this.createParamOptions();
    }

    @Override
    public int process(String[] args) {
        CommandLineParser parser = new BasicParser();

        try {
            CommandLine line = parser.parse(this.opts, args);
            if (line.getArgList().size() > 0) {
                throw new ParseException("Unknown parameters: "
                        + StringUtils.joinWithDelim("", line.getArgList()));
            }

            IProject proj = getProject(line);
            List<Server> servers = CliUtils.obtainServers(line, proj);

            boolean force = line.hasOption(PARAM_FORCE);
            String svc = line.hasOption(PARAM_SERVICE) ? line
                    .getOptionValue(PARAM_SERVICE) : null;

            if (svc == null && proj == null) {
                throw new ParseException(
                        "Either project or service have to be specified.");
            }

            final UndeployProcessorInput input = new UndeployProcessorInput(
                    force, proj, svc);
            ServersCommandExecutor executor = new ServersCommandExecutor(servers, proc.getCommandExecutorFactory());
            executor.execute(new IServerCommandRunner() {
                
                @Override
                public void runCommand(ICommandExecutor executor)
                        throws AbstractSVCException {
                    proc.process(input, executor);    
                }
            });
        } catch (ParseException e) {
            LOG.error(e.getLocalizedMessage(), e);
            CliUtils.printHelp(HELP_USAGE, opts, null);
            return EXIT_CODE_PARSE;
        }catch (SVCParseException e) {
            LOG.error(e.getLocalizedMessage(), e);
            CliUtils.printHelp(HELP_USAGE, opts, null);
            return EXIT_CODE_PARSE;
        } catch (ProjectBuilderException e) {
            LOG.error(e.getLocalizedMessage(), e);
            return EXIT_CODE_PROJECT_BUILD;
        } catch (CommunicatorException e) {
            LOG.error(e.getLocalizedMessage(), e);
            return EXIT_CODE_COMMUNICATION;
        } catch (CommandExecutorException e) {
            LOG.error(e.getLocalizedMessage(), e);
            return EXIT_CODE_CONDITIONS;
        } catch (AbstractSVCException e) {
            LOG.error(e.getLocalizedMessage(), e);
            return EXIT_CODE_ABSTRACT_SV;
        }

        return EXIT_CODE_OK;
    }

    private Options createParamOptions() {
        Options opts = new Options();

        CliUtils.addConnectionOptions(opts);

        opts.addOption(
                PARAM_FORCE,
                LONG_PARAM_FORCE,
                false,
                "Force the undeployment. This parameter is used when the service that we want "
                        + "to undeploy is locked by another user. Force mode overrides the lock. Be carefull with this options.");

        opts.addOption(PARAM_SERVICE, LONG_PARAM_SERVICE, true,
                "If you specify this parameter, only the specified service will be undeployed.");

        opts.addOption(CommandLineOptions.PROP_PROJ,
                CommandLineOptions.LONG_PROP_PROJ, true,
                "Project to be undeployed.");

        opts.addOption(CommandLineOptions.PROPERTY_PROJ_PASSWORD,
                CommandLineOptions.LONG_PROPERTY_PROJ_PASSWORD, true,
                "Project encryption password.");
        return opts;
    }

}
