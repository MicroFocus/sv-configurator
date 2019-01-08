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

import com.microfocus.sv.svconfigurator.cli.impl.base.CliServerCommandProcessorBase;
import com.microfocus.sv.svconfigurator.core.IService;
import com.microfocus.sv.svconfigurator.core.impl.exception.AbstractSVCException;
import com.microfocus.sv.svconfigurator.serverclient.ICommandExecutor;
import com.microfocus.sv.svconfigurator.util.StringUtils;
import org.apache.commons.cli.*;

public class SetLoggingCliCommandProcessor extends CliServerCommandProcessorBase {

    public static final String COMMAND = "setLogging";
    public static final String ENABLED_ARG = "enabled";
    public static final String DISABLED_ARG = "disabled";

    public static final String SERVICE_ARG = "service_ident";
    public static final String LOGGING_ARG = "logging_value";

    public SetLoggingCliCommandProcessor(){
        super(COMMAND);
    }

    String serviceIdentArg;
    boolean enableLoggingArg;


    @Override
    protected boolean initParameters(CommandLine parameters) {
        String[] args = parameters.getArgs();
        if (args.length != 2){
            return false;
        }

        this.serviceIdentArg = args[0];

        if (ENABLED_ARG.equals(args[1])) {
            this.enableLoggingArg = true;
        }
        else if (DISABLED_ARG.equals(args[1])){
            this.enableLoggingArg = false;
        }
        else{
            // invalid value
            return false;
        }


        return true;
    }

    @Override
    protected int executeWithServerExecutor(CommandLine parameters, ICommandExecutor executor) throws AbstractSVCException {

        IService service = executor.findService(this.serviceIdentArg, null);
        executor.changeVirtualServiceLoggingConfiguration(service, this.enableLoggingArg);

        return EXIT_CODE_OK;
    }

    @Override
    public String getHelpUsage() {
        return this.command + " [parameters] <" + SERVICE_ARG + "> <" + LOGGING_ARG + ">";
    }

    @Override
    protected void addMandatoryCommandLineOptions(Options options) {
        options.addOption(SERVICE_ARG, false, "Identification of the service (ID or the name).");
        options.addOption(LOGGING_ARG, false, "Logging setting. Value have to be one of these values: " +
                StringUtils.joinWithDelim(", ", ENABLED_ARG, DISABLED_ARG) + ".");
    }
}
