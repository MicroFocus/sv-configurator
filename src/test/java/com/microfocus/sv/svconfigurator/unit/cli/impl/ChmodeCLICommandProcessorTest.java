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

import static com.microfocus.sv.svconfigurator.unit.cli.CliTestConst.STR4_HTTP;
import static com.microfocus.sv.svconfigurator.unit.cli.CliTestConst.STR4_HTTPS;
import static com.microfocus.sv.svconfigurator.unit.cli.CliTestConst.STR5;
import static com.microfocus.sv.svconfigurator.unit.cli.CliTestConst.STR6;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.io.File;
import java.net.URL;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.microfocus.sv.svconfigurator.build.IProjectBuilder;
import com.microfocus.sv.svconfigurator.cli.impl.ChmodeCLICommandProcessor;
import com.microfocus.sv.svconfigurator.core.IProject;
import com.microfocus.sv.svconfigurator.core.impl.Project;
import com.microfocus.sv.svconfigurator.core.impl.exception.ProjectBuilderException;
import com.microfocus.sv.svconfigurator.core.impl.jaxb.ServiceRuntimeConfiguration;
import com.microfocus.sv.svconfigurator.core.impl.processor.Credentials;
import com.microfocus.sv.svconfigurator.processor.ChmodeProcessorInput;
import com.microfocus.sv.svconfigurator.processor.IChmodeProcessor;
import com.microfocus.sv.svconfigurator.serverclient.ICommandExecutor;
import com.microfocus.sv.svconfigurator.serverclient.ICommandExecutorFactory;
import com.microfocus.sv.svconfigurator.unit.cli.CliTestConst;

public class ChmodeCLICommandProcessorTest {

    private IChmodeProcessor mockProc;
    private IProjectBuilder mockBuild;
    private ICommandExecutorFactory mockFactory;
    private ICommandExecutor mockExecutor;
    private ChmodeCLICommandProcessor cliProc;

    @Before
    public void setUp() throws Exception {
        this.mockProc = Mockito.mock(IChmodeProcessor.class);
        this.mockBuild = Mockito.mock(IProjectBuilder.class);
        this.mockFactory = Mockito.mock(ICommandExecutorFactory.class);
        this.mockExecutor = Mockito.mock(ICommandExecutor.class);
        this.cliProc = new ChmodeCLICommandProcessor(this.mockBuild, this.mockProc);
        when(this.mockProc.getCommandExecutorFactory()).thenReturn(mockFactory);
        when(this.mockFactory.createCommandExecutor(new URL(STR4_HTTPS), new Credentials(STR5, STR6))).thenReturn(mockExecutor);
        when(this.mockFactory.createCommandExecutor(new URL(STR4_HTTP), null)).thenReturn(mockExecutor);
    }

    @Test
    public void testWithoutParams() throws Exception {
        int res = this.cliProc.process(new String[]{});
        assertNotEquals("chmode with no parameters returned code 0", 0, res);

        Mockito.verifyNoMoreInteractions(this.mockProc);
        Mockito.verifyNoMoreInteractions(this.mockBuild);
    }

    @Test
    public void testSimulate_DM_PM_URL_USER_PASS_PROJ_FORCE() throws Exception {
        String[] args = new String[]{CliTestConst.PM, CliTestConst.STR1, CliTestConst.DM, CliTestConst.STR3, CliTestConst.URL, CliTestConst.STR4_HTTPS, CliTestConst.USR, CliTestConst.STR5, CliTestConst.PWD, CliTestConst.STR6, CliTestConst.PROJECT, CliTestConst.STR2, CliTestConst.FORCE, CliTestConst.STR2, "SIMULATING"};
        IProject proj = new Project(CliTestConst.STR7, null, null, null, null);
        Mockito.when(this.mockBuild.buildProject(Mockito.any(File.class), Mockito.anyString())).thenReturn(proj);

        assertSame(0, this.cliProc.process(args));

        //verify project building
        ArgumentCaptor<File> projFileCaptor = ArgumentCaptor.forClass(File.class);
        ArgumentCaptor<String> projPassCaptor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(this.mockBuild, Mockito.times(1)).buildProject(projFileCaptor.capture(), projPassCaptor.capture());

        File projF = projFileCaptor.getValue();
        String projPass = projPassCaptor.getValue();
        assertSame("proj file", CliTestConst.STR2, projF.toString());
        assertNull("proj pass", projPass);

        //verify executor
        ArgumentCaptor<ChmodeProcessorInput> captor = ArgumentCaptor.forClass(ChmodeProcessorInput.class);
        ArgumentCaptor<ICommandExecutor> captorExec = ArgumentCaptor.forClass(ICommandExecutor.class);
        Mockito.verify(this.mockProc, Mockito.times(1)).process(captor.capture(), captorExec.capture());

        ICommandExecutor exec = captorExec.getValue();
        ChmodeProcessorInput chmodeInput = captor.getValue();
        assertSame("perf model is not same", CliTestConst.STR1, chmodeInput.getPerfModel());
        assertSame("data model is not same", CliTestConst.STR3, chmodeInput.getDataModel());
        assertSame("proj", proj, chmodeInput.getProject());
        assertTrue("force", chmodeInput.isForce());
        assertSame("svc name", CliTestConst.STR2, chmodeInput.getService());
        assertSame("svc status", ServiceRuntimeConfiguration.RuntimeMode.SIMULATING, chmodeInput.getServiceMode());
        Assert.assertNotNull("ICommandExecutor", exec);
    }
    
    @Test
    public void testBadSimulMode() throws Exception {
        String[] args = new String[]{CliTestConst.URL, CliTestConst.STR4_HTTPS, CliTestConst.STR2, "SIMULA"};
        assertEquals(1000, this.cliProc.process(args));
    }

    @Test
    public void testLearn_BadProject() throws Exception {
        Mockito.when(this.mockBuild.buildProject(Mockito.any(File.class), Mockito.anyString())).thenThrow(ProjectBuilderException.class);

        String[] args = new String[]{CliTestConst.PROJECT, CliTestConst.STR6, CliTestConst.FORCE, CliTestConst.STR2, "SIMULATING"};
        assertNotSame(0, this.cliProc.process(args));

        //verify project building
        ArgumentCaptor<File> projFileCaptor = ArgumentCaptor.forClass(File.class);
        ArgumentCaptor<String> projPassCaptor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(this.mockBuild, Mockito.times(1)).buildProject(projFileCaptor.capture(), projPassCaptor.capture());

        File projF = projFileCaptor.getValue();
        String projPass = projPassCaptor.getValue();
        assertSame("proj file", CliTestConst.STR6, projF.toString());
        assertNull("proj pass", projPass);

        Mockito.verifyNoMoreInteractions(this.mockBuild);
        Mockito.verifyNoMoreInteractions(this.mockProc);
    }

    @Test
    public void testStandBy_NoProject() throws Exception {
        String[] args = new String[]{CliTestConst.URL, CliTestConst.STR4_HTTP, CliTestConst.STR7, "STAND_BY"};
        assertSame(0, this.cliProc.process(args));

        ArgumentCaptor<ChmodeProcessorInput> captor = ArgumentCaptor.forClass(ChmodeProcessorInput.class);
        ArgumentCaptor<ICommandExecutor> captorExec = ArgumentCaptor.forClass(ICommandExecutor.class);
        Mockito.verify(this.mockProc, Mockito.times(1)).process(captor.capture(), captorExec.capture());

        ICommandExecutor exec = captorExec.getValue();
        ChmodeProcessorInput chmodeInput = captor.getValue();
        assertNull("PM", chmodeInput.getPerfModel());
        assertNull("DM", chmodeInput.getDataModel());
        assertNull("proj", chmodeInput.getProject());
        assertFalse("force", chmodeInput.isForce());
        assertSame("svc name", CliTestConst.STR7, chmodeInput.getService());
        assertSame("svc status", ServiceRuntimeConfiguration.RuntimeMode.STAND_BY, chmodeInput.getServiceMode());
        Assert.assertNotNull("ICommandExecutor", exec);
    }

}
