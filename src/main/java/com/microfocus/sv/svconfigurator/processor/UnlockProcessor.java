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
package com.microfocus.sv.svconfigurator.processor;

import com.microfocus.sv.svconfigurator.Global;
import com.microfocus.sv.svconfigurator.core.IService;
import com.microfocus.sv.svconfigurator.core.impl.exception.CommandExecutorException;
import com.microfocus.sv.svconfigurator.core.impl.exception.CommunicatorException;
import com.microfocus.sv.svconfigurator.serverclient.ICommandExecutor;
import com.microfocus.sv.svconfigurator.serverclient.ICommandExecutorFactory;

public class UnlockProcessor implements IUnlockProcessor {

    private ICommandExecutorFactory commandExecutorFactory;

    public UnlockProcessor(ICommandExecutorFactory commandExecutorFactory) {
        this.commandExecutorFactory = commandExecutorFactory;
    }
    
    @Override
    public ICommandExecutorFactory getCommandExecutorFactory() {
        return commandExecutorFactory;
    }

    @Override
    public void process(UnlockProcessorInput input, ICommandExecutor exec) throws CommunicatorException, CommandExecutorException {
        String svcStr = input.getService();
        if (svcStr == null) {
            throw new CommandExecutorException("Service have to be specified");
        }

        IService svc = exec.findService(svcStr, input.getProject());
        String clientId = input.isLock() ? Global.getClientId(exec) : null;
        exec.lockService(svc, clientId);
    }
}
