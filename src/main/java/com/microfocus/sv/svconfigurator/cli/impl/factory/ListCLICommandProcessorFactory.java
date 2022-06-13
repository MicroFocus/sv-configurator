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
package com.microfocus.sv.svconfigurator.cli.impl.factory;

import com.microfocus.sv.svconfigurator.build.IProjectBuilder;
import com.microfocus.sv.svconfigurator.build.ProjectBuilder;
import com.microfocus.sv.svconfigurator.cli.ICLICommandProcessor;
import com.microfocus.sv.svconfigurator.cli.impl.ListCLICommandProcessor;
import com.microfocus.sv.svconfigurator.processor.IListProcessor;
import com.microfocus.sv.svconfigurator.processor.ListProcessor;
import com.microfocus.sv.svconfigurator.serverclient.ICommandExecutorFactory;

public class ListCLICommandProcessorFactory extends AbstractCLICommandProcessorFactory {

    private static final String DESCRIPTION = "Prints the list of deployed services on the server.";

    private ICommandExecutorFactory commandExecutorFactory = null;

    public ListCLICommandProcessorFactory(ICommandExecutorFactory commandExecutorFactory) {
        super(ListCLICommandProcessor.COMMAND, DESCRIPTION);

        this.commandExecutorFactory = commandExecutorFactory;
    }

    @Override
    public ICLICommandProcessor create() {
        IListProcessor proc = new ListProcessor(this.commandExecutorFactory);
        IProjectBuilder projectBuilder = new ProjectBuilder();
        return new ListCLICommandProcessor(projectBuilder, proc);
    }
}
