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
package com.microfocus.sv.svconfigurator.integration.comm;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.microfocus.sv.svconfigurator.build.ProjectBuilder;
import com.microfocus.sv.svconfigurator.cli.impl.DeployCLICommandProcessor;
import com.microfocus.sv.svconfigurator.cli.impl.UndeployCLICommandProcessor;
import com.microfocus.sv.svconfigurator.integration.TestConst;
import com.microfocus.sv.svconfigurator.processor.DeployProcessor;
import com.microfocus.sv.svconfigurator.processor.UndeployProcessor;
import com.microfocus.sv.svconfigurator.serverclient.impl.CommandExecutorFactory;
import com.microfocus.sv.svconfigurator.service.ServiceAmendingServiceImpl;
import com.microfocus.sv.svconfigurator.util.TestUtils;

@Ignore(value="Obsolete project")
public class CliUndeploymentTest extends AbstractCliProcIntegrationTest {

    private UndeployCLICommandProcessor p;

    @BeforeClass
    public static void setUpClass() throws Exception {
        AbstractCliProcIntegrationTest.setUpClass();
    }

    @Before
    public void setUp() throws Exception {
        this.p = new UndeployCLICommandProcessor(new ProjectBuilder(), new UndeployProcessor(new CommandExecutorFactory()));
        
        clearAll();
    }

    @After
    public void tearDown() throws Exception {
        clearAll();
    }

    @Test
    public void testUndeploymentByName() throws Exception {
        // First we have to deploy
        this.deployClaimDemo();

        String[] cliParams = TestUtils.createParams(String.format("-usr %s -pwd %s -url %s -s \"%s\"",
                TestConst.MGMT_TST_USERNAME, TestConst.MGMT_TST_PASSWORD, TestConst.MGMT_TST_URI_STR, TestConst.SVC_NAME));

        p.process(cliParams);

        assertEquals(0, spc.getServiceList(null).getEntries().size());
    }
    
    @Test
    public void testUndeploymentByProject() throws Exception {
        // First we have to deploy
        this.deployClaimDemo();

        String[] cliParams = TestUtils.createParams(String.format("-usr %s -pwd %s -url %s -p \"%s\"",
                TestConst.MGMT_TST_USERNAME, TestConst.MGMT_TST_PASSWORD, TestConst.MGMT_TST_URI_STR, TestConst.getClaimDemoProject().getCanonicalPath()));

        p.process(cliParams);

        assertEquals(0, spc.getServiceList(null).getEntries().size());
    }

    @Test
    public void testUndeploymentManyArgs() throws Exception {
        // First we have to deploy
        this.deployClaimDemo();

        //Service name is not in "" and there is a space in the name
        String[] cliParams = TestUtils.createParams(String.format("-usr %s -pwd %s -url %s -s %s",
                TestConst.MGMT_TST_USERNAME, TestConst.MGMT_TST_PASSWORD, TestConst.MGMT_TST_URI_STR, TestConst.SVC_NAME));

        //parse error
        assertEquals(1000, p.process(cliParams));
        
        //service was not undeployed
        assertEquals(1, spc.getServiceList(null).getEntries().size());
    }

    private void deployClaimDemo() throws Exception {
        String[] cliParams = TestUtils.createParams(String.format("-usr %s -pwd %s -url %s %s",
                TestConst.MGMT_TST_USERNAME, TestConst.MGMT_TST_PASSWORD, TestConst.MGMT_TST_URI_STR, TestConst.getClaimDemoProject().getCanonicalPath()));

        DeployCLICommandProcessor p = new DeployCLICommandProcessor(new ProjectBuilder(), new DeployProcessor(new CommandExecutorFactory()));
        assertEquals(0, p.process(cliParams));

        assertEquals(1, spc.getServiceList(null).getEntries().size());
    }
}
