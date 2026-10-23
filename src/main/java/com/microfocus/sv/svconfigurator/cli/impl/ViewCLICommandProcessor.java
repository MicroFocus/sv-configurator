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
import com.microfocus.sv.svconfigurator.processor.IViewProcessor;
import com.microfocus.sv.svconfigurator.processor.ViewProcessorInput;
import com.microfocus.sv.svconfigurator.serverclient.ICommandExecutor;
import com.microfocus.sv.svconfigurator.util.CliUtils;

public class ViewCLICommandProcessor extends AbstractProjectCommandProcessor {
    //============================== STATIC ATTRIBUTES ========================================

    public static final String COMMAND = "view";
    private static final Logger LOG = LoggerFactory.getLogger(ViewCLICommandProcessor.class);
    private static final String PROPERTY_REPORT = "r";
    private static final String LONG_PROPERTY_REPORT = "report";
    private static final String PARAM_SERVICE = "service";
    private static final String HELP_USAGE = COMMAND + " [parameters] <" + PARAM_SERVICE + ">";
    //============================== INSTANCE ATTRIBUTES ======================================
    private IViewProcessor proc;
    private Options opts;

    //============================== STATIC METHODS ===========================================

    public ViewCLICommandProcessor(IProjectBuilder projectBuilder, IViewProcessor proc) {
        super(projectBuilder);
        this.proc = proc;
        this.opts = this.createPropertyOptions();
    }

    //============================== CONSTRUCTORS =============================================

    //============================== ABSTRACT METHODS =========================================

    //============================== OVERRIDEN METHODS ========================================


    @Override
    public int process(String[] args) {
        CommandLineParser parser = new BasicParser();

        try {
            CommandLine line = parser.parse(this.opts, args);
            String[] lineArgs = line.getArgs();

            if (lineArgs.length != 1) {
                throw new ParseException("Bad arguments count, please take a look into the help.");
            }

            IProject proj = null;
            if (line.hasOption(CommandLineOptions.PROP_PROJ)) { //project file specified
                String projPass = line.hasOption(CommandLineOptions.PROPERTY_PROJ_PASSWORD) ? line.getOptionValue(CommandLineOptions.PROPERTY_PROJ_PASSWORD) : null;
                proj = this.getProject(line.getOptionValue(CommandLineOptions.PROP_PROJ), projPass);
            }

            List<Server> servers = CliUtils.obtainServers(line, null, true);
            final String outputFormat = CliUtils.obtainOutputFormat(line);
            final ViewProcessorInput input = new ViewProcessorInput(line.hasOption(PROPERTY_REPORT), proj, lineArgs[0], outputFormat);
            ServersCommandExecutor executor = new ServersCommandExecutor(servers, proc.getCommandExecutorFactory());
            executor.execute(new IServerCommandRunner() {
                @Override
                public void runCommand(ICommandExecutor executor) throws AbstractSVCException {
                    proc.process(input, executor);
                }
            });
        } catch (ParseException e) {
            LOG.error(e.getLocalizedMessage(), e);
            this.printHelp();
            return EXIT_CODE_PARSE;
        } catch (SVCParseException e) {
            LOG.error(e.getLocalizedMessage(), e);
            this.printHelp();
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
        } catch (IllegalArgumentException e) {
            LOG.error(e.getLocalizedMessage(), e);
            return EXIT_CODE_PARSE;
        } catch (AbstractSVCException e) {
            LOG.error(e.getLocalizedMessage(), e);
            return EXIT_CODE_ABSTRACT_SV;
        }

        return EXIT_CODE_OK;
    }


    //============================== INSTANCE METHODS =========================================

    //============================== PRIVATE METHODS ==========================================

    /**
     * Property options like -r, -f or -uri
     */
    private Options createPropertyOptions() {
        Options opts = new Options();

        CliUtils.addConnectionOptions(opts);

        opts.addOption(PROPERTY_REPORT, LONG_PROPERTY_REPORT, false, "Prints the service runtime report.");
        opts.addOption(CommandLineOptions.PROP_PROJ, CommandLineOptions.LONG_PROP_PROJ, true, "Processes the project file (.vproja or .vproj). You have to specify " +
                "the project file either if you want to identify the service by its name or if you want to obtain the " +
                "server management URL from the project.");
        opts.addOption(CommandLineOptions.PROPERTY_PROJ_PASSWORD, CommandLineOptions.LONG_PROPERTY_PROJ_PASSWORD, true, "Project encryption password");

        CliUtils.addOutputFormatOptions(opts);
        return opts;
    }

    /**
     * Parameters like <project_file> or <service_ident>
     */
    private Options createMandatParamsOptions() {
        Options opts = new Options();

        opts.addOption(PARAM_SERVICE, false, "Service identification (ID or name). There can be more services with the same name in the server. In this case you will be asked either to use the ID of the service or specify the project where the service is. Unfortunately there can also be more services with the same name in the project and if they are, you have to use the service ID.");

        return opts;
    }

    private void printHelp() {
        CliUtils.printHelp(HELP_USAGE, this.opts, this.createMandatParamsOptions());
    }

    //============================== GETTERS / SETTERS ========================================

    //============================== INNER CLASSES ============================================

}
