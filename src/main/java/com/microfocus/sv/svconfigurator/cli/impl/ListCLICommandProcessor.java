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
import com.microfocus.sv.svconfigurator.cli.ICLICommandProcessor;
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
import com.microfocus.sv.svconfigurator.processor.IListProcessor;
import com.microfocus.sv.svconfigurator.serverclient.ICommandExecutor;
import com.microfocus.sv.svconfigurator.util.CliUtils;

public class ListCLICommandProcessor extends AbstractProjectCommandProcessor implements ICLICommandProcessor {
    //============================== STATIC ATTRIBUTES ========================================

    public static final String COMMAND = "list";
    private static final String HELP_USAGE = COMMAND + " [parameters]";
    private static final Logger LOG = LoggerFactory.getLogger(ListCLICommandProcessor.class);
    //============================== INSTANCE ATTRIBUTES ======================================

    private IListProcessor processor;
    private Options opts;

    //============================== STATIC METHODS ===========================================

    //============================== CONSTRUCTORS =============================================

    public ListCLICommandProcessor(IProjectBuilder projectBuilder, IListProcessor processor) {
        super(projectBuilder);
        this.processor = processor;
        this.opts = this.createPropsOptions();
    }

    //============================== ABSTRACT METHODS =========================================

    //============================== OVERRIDEN METHODS ========================================

    //============================== INSTANCE METHODS =========================================

    @Override
    public int process(String[] args) {
        CommandLineParser parser = new BasicParser();

        try {
            CommandLine line = parser.parse(this.opts, args);

            final IProject proj = getProject(line);
            List<Server> servers = CliUtils.obtainServers(line, null, true);
            ServersCommandExecutor executor = new ServersCommandExecutor(
                    servers, processor.getCommandExecutorFactory());
            executor.execute(new IServerCommandRunner() {

                @Override
                public void runCommand(ICommandExecutor executor)
                        throws AbstractSVCException {
                    processor.process(proj, executor);
                }
            });
        } catch (ParseException e) {
            LOG.error(e.getLocalizedMessage(), e);
            CliUtils.printHelp(HELP_USAGE, opts, null);
            return EXIT_CODE_PARSE;
        } catch (SVCParseException e) {
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

    //============================== PRIVATE METHODS ==========================================

    private Options createPropsOptions() {
        Options opts = new Options();

        CliUtils.addConnectionOptions(opts);

        opts.addOption(CommandLineOptions.PROP_PROJ, CommandLineOptions.LONG_PROP_PROJ, true, "Specify this property (.vproj or .vproja file) either if you want to list only those services on SA server that are in the project (filtering) or if you want to use the server management URL from the project. " +
                "If you don't specify the project file all services on the server will be printed.");
        opts.addOption(CommandLineOptions.PROPERTY_PROJ_PASSWORD, CommandLineOptions.LONG_PROPERTY_PROJ_PASSWORD, true, "Project encryption password");
        return opts;
    }

    //============================== GETTERS / SETTERS ========================================

    //============================== INNER CLASSES ============================================

}
