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
import com.microfocus.sv.svconfigurator.cli.ICLICommandProcessor;
import com.microfocus.sv.svconfigurator.cli.impl.factory.CommandLineOptions;
import com.microfocus.sv.svconfigurator.core.IProject;
import com.microfocus.sv.svconfigurator.core.impl.Server;
import com.microfocus.sv.svconfigurator.core.impl.exception.AbstractSVCException;
import com.microfocus.sv.svconfigurator.core.impl.exception.CommandExecutorException;
import com.microfocus.sv.svconfigurator.core.impl.exception.CommunicatorException;
import com.microfocus.sv.svconfigurator.core.impl.exception.ProjectBuilderException;
import com.microfocus.sv.svconfigurator.core.impl.exception.SVCParseException;
import com.microfocus.sv.svconfigurator.core.impl.jaxb.ServiceRuntimeConfiguration;
import com.microfocus.sv.svconfigurator.core.server.IServerCommandRunner;
import com.microfocus.sv.svconfigurator.core.server.ServersCommandExecutor;
import com.microfocus.sv.svconfigurator.processor.ChmodeProcessorInput;
import com.microfocus.sv.svconfigurator.processor.IChmodeProcessor;
import com.microfocus.sv.svconfigurator.serverclient.ICommandExecutor;
import com.microfocus.sv.svconfigurator.util.CliUtils;
import com.microfocus.sv.svconfigurator.util.StringUtils;

public class ChmodeCLICommandProcessor extends AbstractProjectCommandProcessor implements ICLICommandProcessor {
    //============================== STATIC ATTRIBUTES ========================================

    public static final String COMMAND = "changemode";

    private static final Logger LOG = LoggerFactory.getLogger(ChmodeCLICommandProcessor.class);

    private static final String PARAM_FORCE = "f";
    private static final String LONG_PARAM_FORCE = "force";
    private static final String PARAM_SVC_DATA_MODEL = "dm";
    private static final String LONG_PARAM_SVC_DATA_MODEL = "data-model";
    private static final String PARAM_SVC_PERF_MODEL = "pm";
    private static final String LONG_PARAM_SVC_PERF_MODEL = "perf-model";
    private static final String LONG_PARAM_DEFAULT_DM = "dm-default";
    private static final String LONG_PARAM_DEFAULT_PM = "pm-default";
    private static final String MANDAT_PARAM_SVC = "service_ident";
    private static final String MANDAT_PARAM_MODE = "service_mode";
    private static final String HELP_USAGE = COMMAND + " [parameters] <" + MANDAT_PARAM_SVC + "> <" + MANDAT_PARAM_MODE + ">";
    private static final String STAND_BY_ALTERNATIVE_VALUE = "StandBy";
    //============================== INSTANCE ATTRIBUTES ======================================
    private Options opts;

    private IChmodeProcessor chmodeProcessor;

    //============================== STATIC METHODS ===========================================

    public ChmodeCLICommandProcessor(IProjectBuilder projectBuilder, IChmodeProcessor chmodeProcessor) {
        super(projectBuilder);
        this.opts = this.createParamOptions();
        this.chmodeProcessor = chmodeProcessor;
    }

    //============================== CONSTRUCTORS =============================================


    //============================== ABSTRACT METHODS =========================================

    //============================== OVERRIDEN METHODS ========================================

    @Override
    public int process(String[] args) {
        CommandLineParser parser = new BasicParser();

        try {
            CommandLine line = parser.parse(this.opts, args);
            IProject proj = this.getProject(line);
            List<Server> servers = CliUtils.obtainServers(line, proj);

            String[] lineArgs = line.getArgs();
            if (lineArgs.length != 2) {
                throw new ParseException("Expected 2 arguments (<service_ident> <service_mode>).");
            }

            boolean force = line.hasOption(PARAM_FORCE);
            String svcIdent = lineArgs[0];
            String dataModel = line.getOptionValue(PARAM_SVC_DATA_MODEL);
            String perfModel = line.getOptionValue(PARAM_SVC_PERF_MODEL);
            boolean defaultDataModel = line.hasOption(LONG_PARAM_DEFAULT_DM);
            boolean defaultPerfModel = line.hasOption(LONG_PARAM_DEFAULT_PM);

            ServiceRuntimeConfiguration.RuntimeMode mode;
            String modeParamValue = lineArgs[1];
            try {
                if (STAND_BY_ALTERNATIVE_VALUE.equalsIgnoreCase(modeParamValue)) {
                    mode = ServiceRuntimeConfiguration.RuntimeMode.STAND_BY;
                } else {
                    mode = ServiceRuntimeConfiguration.RuntimeMode.valueOf(modeParamValue.toUpperCase());
                }
            } catch (IllegalArgumentException ex) {
                throw new ParseException("Service mode '" + modeParamValue + "' is illegal, take a look into the help.");
            }
            
            final ChmodeProcessorInput input = new ChmodeProcessorInput(force, proj, svcIdent, dataModel, perfModel, mode, defaultDataModel, defaultPerfModel);
            ServersCommandExecutor executor = new ServersCommandExecutor(servers, chmodeProcessor.getCommandExecutorFactory());
            executor.execute(new IServerCommandRunner() {
                
                @Override
                public void runCommand(ICommandExecutor executor)
                        throws AbstractSVCException {
                    chmodeProcessor.process(input, executor);    
                }
            });
        } catch (ParseException e) {
            LOG.error(e.getLocalizedMessage());
            CliUtils.printHelp(HELP_USAGE, opts, this.createMandatParamOptions());
            return EXIT_CODE_PARSE;
        } catch (SVCParseException e) {
            LOG.error(e.getLocalizedMessage());
            CliUtils.printHelp(HELP_USAGE, opts, this.createMandatParamOptions());
            return EXIT_CODE_PARSE;
        } catch (ProjectBuilderException e) {
            LOG.error(e.getLocalizedMessage());
            return EXIT_CODE_PROJECT_BUILD;
        } catch (CommunicatorException e) {
            LOG.error(e.getLocalizedMessage());
            return EXIT_CODE_COMMUNICATION;
        } catch (CommandExecutorException e) {
            LOG.error(e.getLocalizedMessage());
            return EXIT_CODE_CONDITIONS;
        } catch (AbstractSVCException e) {
            LOG.error(e.getLocalizedMessage());
            CliUtils.printHelp(HELP_USAGE, opts, this.createMandatParamOptions());
            return EXIT_CODE_ABSTRACT_SV;
        } catch (IllegalArgumentException e) {
            LOG.error(e.getLocalizedMessage());
            return EXIT_CODE_PARSE;
        }

        return EXIT_CODE_OK;
    }

    //============================== INSTANCE METHODS =========================================

    //============================== PRIVATE METHODS ==========================================

    private Options createParamOptions() {
        Options opts = new Options();
        CliUtils.addConnectionOptions(opts);

        opts.addOption(PARAM_FORCE, LONG_PARAM_FORCE, false, "Force mode (if the service is locked, it will unlock it)");
        opts.addOption(PARAM_SVC_DATA_MODEL, LONG_PARAM_SVC_DATA_MODEL, true, "Data model identification (id or name)");
        opts.addOption(null, LONG_PARAM_DEFAULT_DM, false, "Default data model fail-over (the first data model will be selected)");
        opts.addOption(PARAM_SVC_PERF_MODEL, LONG_PARAM_SVC_PERF_MODEL, true, "Performance model identification (id or name)");
        opts.addOption(null, LONG_PARAM_DEFAULT_PM, false, "Default performance model fail-over (the first performance model will be selected)");
        opts.addOption(CommandLineOptions.PROP_PROJ, CommandLineOptions.LONG_PROP_PROJ, true, "Project file (.vproj or .vproja) to search the service in.");
        opts.addOption(CommandLineOptions.PROPERTY_PROJ_PASSWORD, CommandLineOptions.LONG_PROPERTY_PROJ_PASSWORD, true, "Project encryption password");

        return opts;
    }

    private Options createMandatParamOptions() {
        Options opts = new Options();

        opts.addOption(MANDAT_PARAM_SVC, false, "Identification of the service (ID or the name) whose mode we want to change. There can be more services with the same name in the server. In this case you will be asked either to use the ID of the service or specify the project where the service is. Unfortunately there can also be more services with the same name in the project and if they are, you have to use the service ID.");
        opts.addOption(MANDAT_PARAM_MODE, false, "New service mode. Mode have to be one of these values: " +
                StringUtils.joinWithDelim(", ", ServiceRuntimeConfiguration.RuntimeMode.STAND_BY, ServiceRuntimeConfiguration.RuntimeMode.SIMULATING, ServiceRuntimeConfiguration.RuntimeMode.LEARNING, ServiceRuntimeConfiguration.RuntimeMode.OFFLINE)
                + ". Service mode values are case in-sensitive and '" + STAND_BY_ALTERNATIVE_VALUE + "' stands for '" + ServiceRuntimeConfiguration.RuntimeMode.STAND_BY +"' to keep compatibility with the LIST command.");

        return opts;
    }

    //============================== GETTERS / SETTERS ========================================

    public void setChmodeProcessor(IChmodeProcessor chmodeProcessor) {
        this.chmodeProcessor = chmodeProcessor;
    }

    //============================== INNER CLASSES ============================================

}
