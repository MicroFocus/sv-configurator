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
package com.microfocus.sv.svconfigurator.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.microfocus.sv.svconfigurator.core.IProject;
import com.microfocus.sv.svconfigurator.core.impl.exception.CommunicatorException;
import com.microfocus.sv.svconfigurator.core.impl.jaxb.atom.ServiceListAtom;
import com.microfocus.sv.svconfigurator.processor.printer.IPrinter;
import com.microfocus.sv.svconfigurator.processor.printer.PrinterFactory;
import com.microfocus.sv.svconfigurator.serverclient.ICommandExecutor;
import com.microfocus.sv.svconfigurator.serverclient.ICommandExecutorFactory;

/**
 * Processor for the list command.
 */
public class ListProcessor implements IListProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(ListProcessor.class);

    private ICommandExecutorFactory commandExecutorFactory;

    public ListProcessor(ICommandExecutorFactory commandExecutorFactory) {
        this.commandExecutorFactory = commandExecutorFactory;
    }
    
    @Override
    public ICommandExecutorFactory getCommandExecutorFactory() {
        return commandExecutorFactory;
    }

    @Override
    public void process(IProject proj, String outputFormat, ICommandExecutor exec) throws CommunicatorException {
        ServiceListAtom atom = exec.getServiceList(proj == null ? null : proj.getId());
        IPrinter printer = PrinterFactory.create(outputFormat);
        LOG.info(printer.createServiceListOutput(atom));
    }
}
