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
package com.microfocus.sv.svconfigurator.integration.ant;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.microfocus.sv.svconfigurator.ant.UndeployTask;
import com.microfocus.sv.svconfigurator.build.ProjectBuilder;
import com.microfocus.sv.svconfigurator.core.IProject;
import com.microfocus.sv.svconfigurator.core.impl.processor.Credentials;
import com.microfocus.sv.svconfigurator.integration.TestConst;
import com.microfocus.sv.svconfigurator.serverclient.ICommandExecutor;
import com.microfocus.sv.svconfigurator.serverclient.impl.CommandExecutor;
import com.microfocus.sv.svconfigurator.util.HttpUtils;

@Ignore(value="Obsolete project")
public class UndeployTaskTest {

    private File projectFile;
    private IProject proj;
    private ICommandExecutor ce;

    @Before
    public void setUp() throws Exception {
        this.projectFile = TestConst.getClaimDemoProject();
        this.proj = new ProjectBuilder().buildProject(this.projectFile, null);
        this.ce = new CommandExecutor(HttpUtils.createServerManagementEndpointClient(TestConst.MGMT_TST_URI, new Credentials(TestConst.MGMT_TST_USERNAME, TestConst.MGMT_TST_PASSWORD)));

        this.ce.deployProject(this.proj);
    }

    @After
    public void tearDown() throws Exception {
        this.ce.undeploy(this.proj);
    }

    @Test
    public void test() throws Exception {
        UndeployTask task = new UndeployTask();
        
        task.setUrl(TestConst.MGMT_TST_URI.toString());
        task.setUsername(TestConst.MGMT_TST_USERNAME);
        task.setPassword(TestConst.MGMT_TST_PASSWORD);
        task.setService(TestConst.SVC_NAME);
        
        assertEquals(1, this.ce.getServiceList(this.proj.getId()).getEntries().size());
        
        task.execute();
        
        assertEquals(0, this.ce.getServiceList(this.proj.getId()).getEntries().size());
    }

}
