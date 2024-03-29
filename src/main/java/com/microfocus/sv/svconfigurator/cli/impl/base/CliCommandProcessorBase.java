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
package com.microfocus.sv.svconfigurator.cli.impl.base;

import com.microfocus.sv.svconfigurator.cli.ICLICommandProcessor;
import com.microfocus.sv.svconfigurator.util.CliUtils;
import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class CliCommandProcessorBase implements ICLICommandProcessor {

    public String command;
    public Options options;
    protected Logger log;

    public CliCommandProcessorBase(String command){
        this.log = LoggerFactory.getLogger(this.getClass());
        this.command = command;
        this.options = createCommandLineOptions();
    }

    @Override
    public final int process(String[] args) {
        CommandLineParser parser = new BasicParser();

        try {
            CommandLine parameters = parser.parse(this.options, args);
            return execute(parameters);
        }
        catch(ParseException e){
            log.error(e.getLocalizedMessage(), e);
            printHelp();
            return EXIT_CODE_PARSE;
        }
    }

    protected abstract int execute(CommandLine parameters);

    /**
     * Returns command usage help
     * @return
     */
    public String getHelpUsage(){
        return this.command + " [parameters]";
    }

    /**
     * Prints command usage help
     */
    public void printHelp() {
        CliUtils.printHelp(getHelpUsage(), this.options, this.createMandatoryCommandLineOptions());
    }

    private Options createCommandLineOptions() {
        Options result = new Options();
        addCommandLineOptions(result);
        return result;
    }

    private Options createMandatoryCommandLineOptions(){
        Options result = new Options();
        addMandatoryCommandLineOptions(result);
        return result;
    }

    /**
     * Used to fill command optional parameters
     * @param options
     */
    protected void addCommandLineOptions(Options options){

    }

    /**
     * Used to fill command mandatory parameters
     * @param options
     */
    protected void addMandatoryCommandLineOptions(Options options){

    }

}
