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
package com.microfocus.sv.svconfigurator.unit.cli.impl;

import com.microfocus.sv.svconfigurator.build.IProjectBuilder;
import com.microfocus.sv.svconfigurator.cli.impl.DeployCLICommandProcessor;
import com.microfocus.sv.svconfigurator.core.IProject;
import com.microfocus.sv.svconfigurator.core.impl.Project;
import com.microfocus.sv.svconfigurator.core.impl.processor.Credentials;
import com.microfocus.sv.svconfigurator.processor.DeployProcessorInput;
import com.microfocus.sv.svconfigurator.processor.IDeployProcessor;
import com.microfocus.sv.svconfigurator.serverclient.ICommandExecutor;
import com.microfocus.sv.svconfigurator.serverclient.ICommandExecutorFactory;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.io.File;
import java.net.URL;

import static com.microfocus.sv.svconfigurator.unit.cli.CliTestConst.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

public class DeployCLICommandProcessorTest {

    private IDeployProcessor mockProc;
    private IProjectBuilder mockBuild;
    private ICommandExecutorFactory mockFactory;
    private ICommandExecutor mockExecutor;
    private DeployCLICommandProcessor cliProc;

    private IProject dummyProject;

    @Before
    public void setUp() throws Exception {
        this.mockProc = Mockito.mock(IDeployProcessor.class);
        this.mockBuild = Mockito.mock(IProjectBuilder.class);
        this.mockFactory = Mockito.mock(ICommandExecutorFactory.class);
        this.mockExecutor = Mockito.mock(ICommandExecutor.class);
        this.cliProc = new DeployCLICommandProcessor(this.mockBuild, this.mockProc);
        when(this.mockProc.getCommandExecutorFactory()).thenReturn(mockFactory);
        when(this.mockFactory.createCommandExecutor(new URL(STR4_HTTPS), new Credentials(STR1, STR3))).thenReturn(mockExecutor);
        this.dummyProject = new Project(STR10, STR9, null, null, null);
        when(this.mockBuild.buildProject(any(File.class), anyString())).thenReturn(this.dummyProject);
    }

    @Test
    public void testNoParams() throws Exception {
        int res = this.cliProc.process(new String[]{});
        Assert.assertNotSame("result", 0, res);

        Mockito.verifyNoMoreInteractions(this.mockBuild);
        Mockito.verifyNoMoreInteractions(this.mockProc);
    }

    @Test
    public void testDeploy() throws Exception {
        String[] params = new String[] {URL, STR4_HTTPS, USR, STR1, PWD, STR3, STR5};
        assertSame("result", 0, this.cliProc.process(params));

        verify(this.mockBuild, times(1)).buildProject(any(File.class), (String)isNull());

        ArgumentCaptor<DeployProcessorInput> captor = ArgumentCaptor.forClass(DeployProcessorInput.class);
        ArgumentCaptor<ICommandExecutor> captorExec = ArgumentCaptor.forClass(ICommandExecutor.class);
        verify(this.mockProc, times(1)).process(captor.capture(), captorExec.capture());
        DeployProcessorInput input = captor.getValue();
        ICommandExecutor exec = captorExec.getValue();

        assertFalse("force", input.isForce());
        assertFalse("undeploy", input.isUndeploy());
        assertSame("project", this.dummyProject, input.getProject());
        Assert.assertNotNull("ICommandExecutor", exec);
    }
}
