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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;

import com.microfocus.sv.svconfigurator.cli.ICLICommandProcessor;
import com.microfocus.sv.svconfigurator.cli.ICLICommandProcessorFactory;
import com.microfocus.sv.svconfigurator.cli.ICLICommandRegistry;
import com.microfocus.sv.svconfigurator.core.impl.exception.SVCParseException;

/**
 * Registry for all the supported CLICommandFactories
 */
public class CLICommandRegistry implements ICLICommandRegistry {

    private static final String USAGE = "Usage: <COMMAND> [parameters]\nYou must select one of the following command:\n";

    private Map<String, ICLICommandProcessorFactory> factoryMap = null;

    public CLICommandRegistry(Collection<ICLICommandProcessorFactory> commandProcessorFactories) {
        this.factoryMap = new HashMap<String, ICLICommandProcessorFactory>(commandProcessorFactories.size());
        for (ICLICommandProcessorFactory factory : commandProcessorFactories) {
            this.factoryMap.put(factory.getCommand().toUpperCase(), factory);
        }
    }

    @Override
    public ICLICommandProcessor lookupCommandProcessor(String command) throws SVCParseException {
        String upperCommand = command.toUpperCase();

        if (! this.factoryMap.containsKey(upperCommand)) {
            throw new SVCParseException("Command "+ command + " was not found in the CLICommandProcessor registry.");
        }

        ICLICommandProcessorFactory factory = this.factoryMap.get(upperCommand);
        return factory.create();
    }

    @Override
    public String getCLICommandHelp() {
        HelpFormatter f = new HelpFormatter();
        f.setOptPrefix("");
        f.setLongOptPrefix("");

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        f.printHelp(pw, f.getWidth(), USAGE, "", this.createOptions(), f.getLeftPadding(), f.getDescPadding(), "");

        pw.close();
        return sw.toString();
    }

    /**
     * Creates the options for our command factories (used for help printing)
     *
     * @return
     */
    private Options createOptions() {
        Options opts = new Options();

        for (ICLICommandProcessorFactory factory : this.factoryMap.values()) {
            opts.addOption(OptionBuilder
                    .withLongOpt(factory.getCommand())
                    .withDescription(factory.getDescription())
                    .create()
            );
        }

        return opts;
    }
}
