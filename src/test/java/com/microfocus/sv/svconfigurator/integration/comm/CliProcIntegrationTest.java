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
import static org.junit.Assert.assertNull;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.microfocus.sv.svconfigurator.build.ProjectBuilder;
import com.microfocus.sv.svconfigurator.cli.impl.ChmodeCLICommandProcessor;
import com.microfocus.sv.svconfigurator.cli.impl.DeployCLICommandProcessor;
import com.microfocus.sv.svconfigurator.cli.impl.ListCLICommandProcessor;
import com.microfocus.sv.svconfigurator.cli.impl.UnlockCLICommandCliProcessor;
import com.microfocus.sv.svconfigurator.cli.impl.ViewCLICommandProcessor;
import com.microfocus.sv.svconfigurator.cli.impl.factory.ChmodeCLICommandProcessorFactory;
import com.microfocus.sv.svconfigurator.core.impl.jaxb.atom.ServiceListAtom;
import com.microfocus.sv.svconfigurator.integration.IntegrationTest;
import com.microfocus.sv.svconfigurator.integration.TestConst;
import com.microfocus.sv.svconfigurator.processor.DeployProcessor;
import com.microfocus.sv.svconfigurator.processor.ListProcessor;
import com.microfocus.sv.svconfigurator.processor.UnlockProcessor;
import com.microfocus.sv.svconfigurator.processor.ViewProcessor;
import com.microfocus.sv.svconfigurator.serverclient.impl.CommandExecutorFactory;
import com.microfocus.sv.svconfigurator.service.ServiceAmendingServiceImpl;
import com.microfocus.sv.svconfigurator.util.TestUtils;

@Category(IntegrationTest.class)
@Ignore(value="Obsolete project")
public class CliProcIntegrationTest extends AbstractCliProcIntegrationTest {
    //============================== STATIC ATTRIBUTES ========================================

    //============================== INSTANCE ATTRIBUTES ======================================

    private File projectFile;

    //============================== STATIC METHODS ===========================================

    @BeforeClass
    public static void setUpClass() throws Exception {
        AbstractCliProcIntegrationTest.setUpClass();
        clearAll();
    }

    //============================== CONSTRUCTORS =============================================

    @Before
    public void setUp() throws Exception {
        this.projectFile = TestConst.getClaimDemoProject();

        //------- DEPLOY -----
        String[] cliParams = TestUtils.createParams(String.format("-usr %s -pwd %s -url %s -w aaa %s", 
                TestConst.MGMT_TST_USERNAME, TestConst.MGMT_TST_PASSWORD, TestConst.MGMT_TST_URI_STR, projectFile.getCanonicalPath()));  
        DeployCLICommandProcessor p = new DeployCLICommandProcessor(new ProjectBuilder(), new DeployProcessor(new CommandExecutorFactory()));
        assertEquals(0, p.process(cliParams));
    }

    @After
    public void tearDown() throws Exception {
        clearAll();
    }

    @Test
    public void testModeSwitching() throws Exception {
        //=========Simulating============
        String[] cliParams = TestUtils.createParams(String.format("-usr %s -pwd %s -url %s -dm \"%s\" -pm \"%s\" \"%s\" %s", 
                TestConst.MGMT_TST_USERNAME, TestConst.MGMT_TST_PASSWORD, TestConst.MGMT_TST_URI_STR, TestConst.SVC_DATA_MODEL, 
                TestConst.SVC_PERF_MODEL, TestConst.SVC_NAME, TestConst.CLI_MODE_SIMULATING));  

        ChmodeCLICommandProcessorFactory procFactory = new ChmodeCLICommandProcessorFactory(new CommandExecutorFactory());
        ChmodeCLICommandProcessor chp = procFactory.create();
        assertEquals(0, chp.process(cliParams));

        ServiceListAtom.ServiceEntry se = spc.getServiceList(TestConst.PROJ_ID).getEntries().get(0);
        assertEquals(TestConst.SVC_DATA_MODEL_ID, se.getDataModel());
        assertEquals(TestConst.SVC_PERF_MODEL_ID, se.getPerfModel());
        assertEquals(TestConst.SVC_MODE_SIMULATING, se.getServiceMode());
        assertEquals(TestConst.SVC_DEPLOY_READY, se.getDeployState());
        assertNull(se.getDeploymentError());

        //================STAND_BY===============
        cliParams = TestUtils.createParams(String.format("-usr %s -pwd %s -url %s \"%s\" %s", 
                TestConst.MGMT_TST_USERNAME, TestConst.MGMT_TST_PASSWORD, TestConst.MGMT_TST_URI_STR, TestConst.SVC_NAME, TestConst.CLI_MODE_STAND_BY));   
        assertEquals(0, chp.process(cliParams));

        se = spc.getServiceList(TestConst.PROJ_ID).getEntries().get(0);
        assertNull(se.getDataModel());
        assertNull(se.getPerfModel());
        assertEquals(TestConst.SVC_MODE_STAND_BY, se.getServiceMode());
        assertEquals(TestConst.SVC_DEPLOY_READY, se.getDeployState());
        assertNull(se.getDeploymentError());
    }

    @Test
    public void testModelSwitching() throws Exception {
        //----- Simulating without perf model ----
        String[] cliParams = TestUtils.createParams(String.format("-usr %s -pwd %s -url %s -dm \"%s\" \"%s\" %s", 
                TestConst.MGMT_TST_USERNAME, TestConst.MGMT_TST_PASSWORD, TestConst.MGMT_TST_URI_STR, 
                TestConst.SVC_DATA_MODEL, TestConst.SVC_NAME, TestConst.CLI_MODE_SIMULATING));   
        
        ChmodeCLICommandProcessorFactory procFactory = new ChmodeCLICommandProcessorFactory(new CommandExecutorFactory());
        ChmodeCLICommandProcessor chp = procFactory.create();
        assertEquals(0, chp.process(cliParams));

        ServiceListAtom.ServiceEntry se = spc.getServiceList(TestConst.PROJ_ID).getEntries().get(0);
        assertNull(se.getPerfModel());
        assertEquals(TestConst.SVC_DATA_MODEL_ID, se.getDataModel());
        assertEquals(TestConst.SVC_MODE_SIMULATING, se.getServiceMode());
        assertEquals(TestConst.SVC_DEPLOY_READY, se.getDeployState());

        //----- Simulating with perf model ----
        cliParams =  TestUtils.createParams(String.format("-usr %s -pwd %s -url %s -pm \"%s\" -dm \"%s\" \"%s\" %s", 
                TestConst.MGMT_TST_USERNAME, TestConst.MGMT_TST_PASSWORD, TestConst.MGMT_TST_URI_STR, 
                TestConst.SVC_PERF_MODEL, TestConst.SVC_DATA_MODEL, TestConst.SVC_NAME, TestConst.CLI_MODE_SIMULATING));
                
        assertEquals(0, chp.process(cliParams));

        se = spc.getServiceList(TestConst.PROJ_ID).getEntries().get(0);
        assertEquals(TestConst.SVC_PERF_MODEL_ID, se.getPerfModel());
        assertEquals(TestConst.SVC_DATA_MODEL_ID, se.getDataModel());
        assertEquals(TestConst.SVC_MODE_SIMULATING, se.getServiceMode());
        assertEquals(TestConst.SVC_DEPLOY_READY, se.getDeployState());
    }

    @Test
    public void testList() throws Exception {
        String[] cliParams = TestUtils.createParams(String.format("-usr %s -pwd %s -url %s", 
                TestConst.MGMT_TST_USERNAME, TestConst.MGMT_TST_PASSWORD, TestConst.MGMT_TST_URI_STR)); 
        
        ListCLICommandProcessor proc = new ListCLICommandProcessor(new ProjectBuilder(), new ListProcessor(new CommandExecutorFactory()));
        assertEquals(0, proc.process(cliParams));
    }

    @Test
    public void testView() throws Exception {
        String[] cliParams = TestUtils.createParams(String.format("-usr %s -pwd %s -url %s \"%s\"", 
                TestConst.MGMT_TST_USERNAME, TestConst.MGMT_TST_PASSWORD, TestConst.MGMT_TST_URI_STR, TestConst.SVC_NAME));
        
        String[] cliParams2 = TestUtils.createParams(String.format("-usr %s -pwd %s -url %s \"%s\"", 
                TestConst.MGMT_TST_USERNAME, TestConst.MGMT_TST_PASSWORD, TestConst.MGMT_TST_URI_STR, TestConst.SVC_ID)); 
        
        ViewCLICommandProcessor proc = new ViewCLICommandProcessor(new ProjectBuilder(), new ViewProcessor(new CommandExecutorFactory()));
        assertEquals(0, proc.process(cliParams));
        assertEquals(0, proc.process(cliParams2));
    }

    @Test
    public void testUnlock() throws Exception {
        String[] cliParams = TestUtils.createParams(String.format("-usr %s -pwd %s -url %s \"%s\"", 
                TestConst.MGMT_TST_USERNAME, TestConst.MGMT_TST_PASSWORD, TestConst.MGMT_TST_URI_STR, TestConst.SVC_NAME)); 
        UnlockCLICommandCliProcessor proc = new UnlockCLICommandCliProcessor(new ProjectBuilder(), new UnlockProcessor(new CommandExecutorFactory()));
        assertEquals(0, proc.process(cliParams));

        ServiceListAtom.ServiceEntry se = spc.getServiceList(TestConst.PROJ_ID).getEntries().get(0);
        assertNull("Service was not unlocked", se.getClientId());

        String[] cliParams2 = TestUtils.createParams(String.format("-usr %s -pwd %s -url %s -l \"%s\"", 
                TestConst.MGMT_TST_USERNAME, TestConst.MGMT_TST_PASSWORD, TestConst.MGMT_TST_URI_STR, TestConst.SVC_NAME));

        assertEquals(0, proc.process(cliParams2));
        se = spc.getServiceList(TestConst.PROJ_ID).getEntries().get(0);
        assertEquals("Service was not locked", 0, TestConst.MGMT_TST_USERNAME.compareToIgnoreCase(se.getClientId()));

        assertEquals(0, proc.process(cliParams));
        se = spc.getServiceList(TestConst.PROJ_ID).getEntries().get(0);
        assertNull("Service was not unlocked again", se.getClientId());
    }
}
