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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.cli.*;
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
import com.microfocus.sv.svconfigurator.core.server.IServerCommandRunner;
import com.microfocus.sv.svconfigurator.core.server.ServersCommandExecutor;
import com.microfocus.sv.svconfigurator.processor.DeployProcessorInput;
import com.microfocus.sv.svconfigurator.processor.IDeployProcessor;
import com.microfocus.sv.svconfigurator.serverclient.ICommandExecutor;
import com.microfocus.sv.svconfigurator.util.CliUtils;

public class DeployCLICommandProcessor extends AbstractProjectCommandProcessor implements ICLICommandProcessor {
    //============================== STATIC ATTRIBUTES ========================================

    public static final String COMMAND = "deployProject";
    private static final String MANDAT_PROP_PROJ = "project_file";
    private static final String HELP_USAGE = COMMAND + " [parameters] <" + MANDAT_PROP_PROJ + ">";
    private static final String PARAM_FORCE = "f";
    private static final String LONG_PARAM_FORCE = "force";
    private static final String PARAM_UNDEPLOY = "u";
    private static final String LONG_PARAM_UNDEPLOY = "undeploy";
    private static final String PARAM_SERVICE = "s";
    private static final String LONG_PARAM_SERVICE = "service";
    private static final String LONG_PARAM_FIRST_AGENT_FALLBACK = "first-agent-fallback";
    private static final String LONG_PARAM_AGENT_REMAPPING = "remap";
    private static final Logger LOG = LoggerFactory.getLogger(DeployCLICommandProcessor.class);
    //============================== INSTANCE ATTRIBUTES ======================================

    private IDeployProcessor deployProcessor;
    private Options opts;

    //============================== STATIC METHODS ===========================================

    //============================== CONSTRUCTORS =============================================

    public DeployCLICommandProcessor(IProjectBuilder projectBuilder, IDeployProcessor deployProcessor) {
        super(projectBuilder);
        this.deployProcessor = deployProcessor;
        this.opts = this.createParamOptions();
    }

    //============================== ABSTRACT METHODS =========================================

    //============================== OVERRIDEN METHODS ========================================

    //============================== INSTANCE METHODS =========================================


    /**
     * @param args any other argument (first argument should not be the command itself)
     * @return
     */
    @Override
        public int process(String[] args) {
        CommandLineParser parser = new BasicParser();

        try {
            CommandLine line = parser.parse(this.opts, args);
            IProject proj = getProject(line);
            if (proj == null) {
                throw new ParseException("You have to specify the project.");
            }
            
            List<Server> servers = CliUtils.obtainServers(line, proj);
            
            boolean force = line.hasOption(PARAM_FORCE);
            boolean undeploy = line.hasOption(PARAM_UNDEPLOY);
            String svc = line.hasOption(PARAM_SERVICE) ? line.getOptionValue(PARAM_SERVICE) : null;

            Map<String,String> agentRemapping = getAgentRemapping(line);

            boolean importLoggedMessages = line.hasOption(CommandLineOptions.LONG_PARAM_WITH_LOGGED_MESSAGES);

            final DeployProcessorInput input = new DeployProcessorInput(force, undeploy, proj, svc, agentRemapping, importLoggedMessages);
            input.setFirstAgentFailover(line.hasOption(LONG_PARAM_FIRST_AGENT_FALLBACK));
            
            ServersCommandExecutor executor = new ServersCommandExecutor(servers, deployProcessor.getCommandExecutorFactory());
            executor.execute(new IServerCommandRunner() {
                
                @Override
                public void runCommand(ICommandExecutor executor)
                        throws AbstractSVCException {
                    deployProcessor.process(input, executor);    
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

    private Options createParamOptions() {
        Options opts = new Options();

        CliUtils.addConnectionOptions(opts);

        opts.addOption(PARAM_FORCE, LONG_PARAM_FORCE, false, "Force the deployment. This parameter is used when the service that we want " +
                "to deployProject is locked by another user. Force mode overrides the lock.");

        opts.addOption(PARAM_UNDEPLOY, LONG_PARAM_UNDEPLOY, false, "Undeploys all the services from the project.");

        opts.addOption(PARAM_SERVICE, LONG_PARAM_SERVICE, true, "If you specify this parameter, only the specified service will be deployed.");

        opts.addOption(CommandLineOptions.PROPERTY_PROJ_PASSWORD, CommandLineOptions.LONG_PROPERTY_PROJ_PASSWORD, true, "Project encryption password");
        
        opts.addOption(null, LONG_PARAM_FIRST_AGENT_FALLBACK, false, "When there is no such an agent on the server The first one of the same type will be automatically selected.");

        Option agentRemappingProperty  = OptionBuilder.withArgName( "vprojAgentID=serverAgentID" )
                .hasArgs(2)
                .withValueSeparator()
                .withDescription( "This optional parameter allows remapping your project agents to server agents having different IDs (but same type). Use the parameter once for each agent remapped. Note that Agent ID is case sensitive." )
                .create(LONG_PARAM_AGENT_REMAPPING);

        opts.addOption(agentRemappingProperty);

        opts.addOption(null, CommandLineOptions.LONG_PARAM_WITH_LOGGED_MESSAGES, false, "Import logged messages");

        return opts;
    }

    private Options createMandatParamOptions() {
        Options opts = new Options();

        opts.addOption(MANDAT_PROP_PROJ, false, "Project file (.vproj or .vproja) for the deployment.");

        return opts;
    }

    private Map<String,String> getAgentRemapping(CommandLine line) {
        Map<String,String> agentMapping = new HashMap<String, String>();
        if (line.hasOption(LONG_PARAM_AGENT_REMAPPING)) {
            Properties optionProperties = line.getOptionProperties(LONG_PARAM_AGENT_REMAPPING);
            for (String propertyName : optionProperties.stringPropertyNames()) {
                agentMapping.put(propertyName, optionProperties.getProperty(propertyName));
            }
        }
        return !agentMapping.isEmpty() ? agentMapping : null;
    }

    //============================== GETTERS / SETTERS ========================================

    //============================== INNER CLASSES ============================================

}
