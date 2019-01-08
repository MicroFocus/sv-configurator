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
package com.microfocus.sv.svconfigurator.unit.processor;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import java.net.URL;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.microfocus.sv.svconfigurator.build.ProjectBuilder;
import com.microfocus.sv.svconfigurator.core.IProject;
import com.microfocus.sv.svconfigurator.core.IService;
import com.microfocus.sv.svconfigurator.core.impl.processor.Credentials;
import com.microfocus.sv.svconfigurator.integration.TestConst;
import com.microfocus.sv.svconfigurator.processor.IUndeployProcessor;
import com.microfocus.sv.svconfigurator.processor.UndeployProcessor;
import com.microfocus.sv.svconfigurator.processor.UndeployProcessorInput;
import com.microfocus.sv.svconfigurator.serverclient.ICommandExecutor;
import com.microfocus.sv.svconfigurator.serverclient.ICommandExecutorFactory;

public class UndeployProcessorTest {

    private IUndeployProcessor proc;
    
    private IService svc;
    private ICommandExecutor executor;
    private ICommandExecutorFactory factory;
    
    @Before
    public void setUp() throws Exception {
        this.svc = mock(IService.class);
        
        this.executor = mock(ICommandExecutor.class);
        Mockito.when(this.executor.findService("My Super", null)).thenReturn(this.svc);
        
        this.factory = Mockito.mock(ICommandExecutorFactory.class);
        Mockito.when(this.factory.createCommandExecutor(Mockito.any(URL.class), Mockito.any(Credentials.class))).thenReturn(this.executor);
        
        this.proc = new UndeployProcessor(factory);
    }

    @Test
    public void testSimpleProcess() throws Exception {
        UndeployProcessorInput input = new UndeployProcessorInput(false, null, "My Super");
        
        this.proc.process(input, proc.getCommandExecutorFactory().createCommandExecutor(TestConst.MGMT_TST_URI, new Credentials("xyz", "password")));
        
        this.verifyFactory(TestConst.MGMT_TST_URI, "xyz", "password");
        
        Mockito.verify(this.executor).findService("My Super", null);
        Mockito.verify(this.executor).undeployService(Mockito.any(IService.class));
        Mockito.verify(this.executor).setForce(false);
        
        Mockito.verifyNoMoreInteractions(this.executor);
    }
    
    public void testUndeployProject() throws Exception {
        
        IProject proj = new ProjectBuilder().buildProject(TestConst.getClaimDemoProject(), null);
        UndeployProcessorInput input = new UndeployProcessorInput(false, proj, null);
        
        this.proc.process(input, proc.getCommandExecutorFactory().createCommandExecutor(TestConst.MGMT_TST_URI, new Credentials("abcd", "qwertz")));
        
        this.verifyFactory(TestConst.MGMT_TST_URI, "abcd", "qwertz");
        
        ArgumentCaptor<IProject> projCaptor = ArgumentCaptor.forClass(IProject.class);
        
        Mockito.verify(this.executor).undeploy(projCaptor.capture());
        assertEquals(TestConst.PROJ_ID, projCaptor.getValue().getId());
        
        Mockito.verify(this.executor).setForce(false);
        
        Mockito.verifyNoMoreInteractions(this.executor);
    }
    
    private void verifyFactory(URL url, String username, String password) throws Exception {
        ArgumentCaptor<Credentials> credArgCaptor = ArgumentCaptor.forClass(Credentials.class);
        ArgumentCaptor<URL> uriArgCaptor = ArgumentCaptor.forClass(URL.class);
        Mockito.verify(this.factory).createCommandExecutor(uriArgCaptor.capture(), credArgCaptor.capture());
        Mockito.verifyNoMoreInteractions(this.factory);
        
        assertEquals(username, credArgCaptor.getValue().getUsername());
        assertEquals(password, credArgCaptor.getValue().getPassword());
        
        assertEquals(url, uriArgCaptor.getValue());
    }

}
