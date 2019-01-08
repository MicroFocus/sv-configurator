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
import com.microfocus.sv.svconfigurator.core.impl.exception.ProjectBuilderException;
import com.microfocus.sv.svconfigurator.processor.IListProjectProcessor;
import com.microfocus.sv.svconfigurator.processor.ListProjectProcessorInput;
import com.microfocus.sv.svconfigurator.util.CliUtils;

public class ListProjectCLICommandProcessor extends AbstractProjectCommandProcessor implements ICLICommandProcessor {

    

    public static final String COMMAND = "listProject";
    private static final String MANDAT_PROP_PROJ = "project_file";
    
    //private static final String PARAM_LONG = "l";
    //private static final String LONG_PARAM_LONG = "long";

    private static final String HELP_USAGE = COMMAND + " [parameters] <" + MANDAT_PROP_PROJ + ">";
    
    private static final Logger LOG = LoggerFactory.getLogger(ListProjectCLICommandProcessor.class);
    
    private IListProjectProcessor processor;
    private Options opts;
    
    public ListProjectCLICommandProcessor(IProjectBuilder projectBuilder, IListProjectProcessor processor) {
        super(projectBuilder);
        this.processor = processor;
        this.opts = this.createPropsOptions();
    }

    @Override
    public int process(String[] args) {
        CommandLineParser parser = new BasicParser();

        try {
            CommandLine line = parser.parse(this.opts, args);

            IProject proj = getProject(line);
            if (proj == null) {
                throw new ParseException("You have to specify the project.");
            }
            
            this.processor.process(new ListProjectProcessorInput(proj, false));
        } catch (ParseException e) {
            LOG.error(e.getLocalizedMessage(), e);
            CliUtils.printHelp(HELP_USAGE, opts, this.createMandatParamOptions());
            return EXIT_CODE_PARSE;
        } catch (ProjectBuilderException e) {
            LOG.error(e.getLocalizedMessage(), e);
            return EXIT_CODE_PROJECT_BUILD;
        }

        return EXIT_CODE_OK;
    }
    

    private Options createPropsOptions() {
        Options opts = new Options();

        //opts.addOption(PARAM_LONG, LONG_PARAM_LONG, false, "Long output (will list not only service names but also its data and performance models.");
        opts.addOption(CommandLineOptions.PROPERTY_PROJ_PASSWORD, CommandLineOptions.LONG_PROPERTY_PROJ_PASSWORD, true, "Project encryption password");
        
        return opts;
    }
    
    private Options createMandatParamOptions() {
        Options opts = new Options();

        opts.addOption(MANDAT_PROP_PROJ, false, "Project file (.vproj or .vproja) to be listed.");

        return opts;
    }

}
