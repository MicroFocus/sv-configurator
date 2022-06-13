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
import com.microfocus.sv.svconfigurator.core.impl.Server;
import com.microfocus.sv.svconfigurator.core.impl.exception.AbstractSVCException;
import com.microfocus.sv.svconfigurator.core.impl.exception.CommandExecutorException;
import com.microfocus.sv.svconfigurator.core.impl.exception.CommunicatorException;
import com.microfocus.sv.svconfigurator.core.impl.exception.SVCParseException;
import com.microfocus.sv.svconfigurator.core.server.IServerCommandRunner;
import com.microfocus.sv.svconfigurator.core.server.ServersCommandExecutor;
import com.microfocus.sv.svconfigurator.processor.HotSwapProcessorInput;
import com.microfocus.sv.svconfigurator.processor.IHotSwapProcessor;
import com.microfocus.sv.svconfigurator.serverclient.ICommandExecutor;
import com.microfocus.sv.svconfigurator.util.CliUtils;

public class HotSwapCLICommandProcessor extends AbstractProjectCommandProcessor implements ICLICommandProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(HotSwapCLICommandProcessor.class);
    
    public static final String COMMAND = "hotswap";

    private static final String PARAM_SVC_PERF_MODEL = "pm";
    private static final String LONG_PARAM_SVC_PERF_MODEL = "perf-model";
    private static final String PARAM_FORCE = "f";
    private static final String LONG_PARAM_FORCE = "force";
    
    private static final String MANDAT_PARAM_SVC = "service_ident";
    
    private static final String HELP_USAGE = String.format("%s [parameters] <%s>", COMMAND, MANDAT_PARAM_SVC);

    private Options opts;
    private IHotSwapProcessor processor;

    public HotSwapCLICommandProcessor(IProjectBuilder projectBuilder, IHotSwapProcessor processor) {
        super(projectBuilder);

        this.processor = processor;
        this.opts = this.createParamOptions();
    }
    
    @Override
    public int process(String[] args) {
        CommandLineParser parser = new BasicParser();
        
        try {
            CommandLine line = parser.parse(this.opts, args);
            List<Server> servers = CliUtils.obtainServers(line, null);
            
            String lineArgs[] = line.getArgs();
            if (lineArgs.length != 1) {
                throw new ParseException("Expected 1 argument(<service_identification>).");
            }

            boolean force = line.hasOption(PARAM_FORCE);
            String svcIdent = lineArgs[0];
            String perfModel = line.getOptionValue(PARAM_SVC_PERF_MODEL);

            final HotSwapProcessorInput input = new HotSwapProcessorInput(force, svcIdent, perfModel);
            ServersCommandExecutor executor = new ServersCommandExecutor(servers, processor.getCommandExecutorFactory());
            executor.execute(new IServerCommandRunner() {
                
                @Override
                public void runCommand(ICommandExecutor executor)
                        throws AbstractSVCException {
                    processor.process(input, executor);    
                }
            });
        } catch (ParseException e) {
            LOG.error(e.getLocalizedMessage(), e);
            CliUtils.printHelp(HELP_USAGE, opts, this.createMandatParamOptions());
            return EXIT_CODE_PARSE;
        } catch (SVCParseException e) {
            LOG.error(e.getLocalizedMessage(), e);
            CliUtils.printHelp(HELP_USAGE, opts, this.createMandatParamOptions());
            return EXIT_CODE_PARSE;
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
    
    private Options createParamOptions() {
        Options opts = new Options();
        
        CliUtils.addConnectionOptions(opts);
        
        opts.addOption(PARAM_FORCE, LONG_PARAM_FORCE, false, "Force mode (if the service is locked, it will unlock it)");
        opts.addOption(PARAM_SVC_PERF_MODEL, LONG_PARAM_SVC_PERF_MODEL, true, "Performance model identification (id or name)");

        return opts;
    }

    private Options createMandatParamOptions() {
        Options opts = new Options();

        opts.addOption(MANDAT_PARAM_SVC, false, "Identification of the service (ID or the name) whose performance model we want to change. There can be more services with the same name in the server. In this case you will be asked to use the ID of the service.");

        return opts;
    }

}
