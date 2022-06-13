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
package com.microfocus.sv.svconfigurator.integration.comm;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.microfocus.sv.svconfigurator.build.ProjectBuilder;
import com.microfocus.sv.svconfigurator.core.IDataModel;
import com.microfocus.sv.svconfigurator.core.IPerfModel;
import com.microfocus.sv.svconfigurator.core.IProject;
import com.microfocus.sv.svconfigurator.core.IService;
import com.microfocus.sv.svconfigurator.core.impl.jaxb.ServiceRuntimeConfiguration;
import com.microfocus.sv.svconfigurator.core.impl.jaxb.ServiceRuntimeReport;
import com.microfocus.sv.svconfigurator.core.impl.processor.Credentials;
import com.microfocus.sv.svconfigurator.integration.IntegrationTest;
import com.microfocus.sv.svconfigurator.integration.TestConst;
import com.microfocus.sv.svconfigurator.serverclient.ICommandExecutor;
import com.microfocus.sv.svconfigurator.serverclient.impl.CommandExecutor;
import com.microfocus.sv.svconfigurator.util.HttpUtils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@Category(IntegrationTest.class)
@Ignore(value="Obsolete project")
public class CommandExecutorIntegrationTest {
    //============================== STATIC ATTRIBUTES ========================================

    //============================== INSTANCE ATTRIBUTES ======================================

    private File projectFile;
    private IProject proj;
    private ICommandExecutor ce;

    //============================== STATIC METHODS ===========================================

    //============================== CONSTRUCTORS =============================================

    @Before
    public void setUp() throws Exception {
        this.projectFile = TestConst.getClaimDemoProject();
        this.proj = new ProjectBuilder().buildProject(this.projectFile, null);
        this.ce = new CommandExecutor(HttpUtils.createServerManagementEndpointClient(TestConst.MGMT_TST_URI, true, new Credentials(TestConst.MGMT_TST_USERNAME, TestConst.MGMT_TST_PASSWORD)));

        this.ce.deployProject(this.proj);
    }

    @After
    public void tearDown() throws Exception {
        this.ce.undeploy(this.proj);
    }

    //============================== ABSTRACT METHODS =========================================

    //============================== OVERRIDEN METHODS ========================================

    //============================== INSTANCE METHODS =========================================
    @Test
    public void testFindService() throws Exception {
        IService svc = this.ce.findService(TestConst.SVC_NAME, null);
        IService svc2 = this.ce.findService(TestConst.SVC_NAME, this.proj);
        IService svc3 = this.ce.findService(TestConst.SVC_ID, this.proj);
        IService svc4 = this.ce.findService(TestConst.SVC_ID, null);

        List<IService> svcs = Arrays.asList(svc, svc2, svc3, svc4);
        for (IService s : svcs) {
            assertEquals(TestConst.SVC_ID, s.getId());
            assertEquals(TestConst.SVC_NAME, s.getName());

            Collection<IDataModel> dms = s.getDataModels();
            assertEquals(1, dms.size());
            IDataModel dm = dms.iterator().next();
            assertEquals(TestConst.SVC_DATA_MODEL, dm.getName());
            assertEquals(TestConst.SVC_DATA_MODEL_ID, dm.getId());

            Collection<IPerfModel> pms = s.getPerfModels();
            assertEquals(2, pms.size());
        }
    }

    @Test
    public void testFindDataModel() throws Exception {
        IService s = this.ce.findService(TestConst.SVC_ID, this.proj);
        Collection<IDataModel> dms = this.ce.findDataModels(s);
        assertEquals(1, dms.size());

        IDataModel dm = dms.iterator().next();
        assertEquals(TestConst.SVC_DATA_MODEL, dm.getName());
        assertEquals(TestConst.SVC_DATA_MODEL_ID, dm.getId());
    }

    @Test
    public void testFindPerfModel() throws Exception {
        IService s = this.ce.findService(TestConst.SVC_ID, this.proj);
        Collection<IPerfModel> pms = this.ce.findPerfModels(s);
        assertEquals(2, pms.size());

        boolean isThere = false;
        for (IPerfModel pm : pms) {
            if (pm.getId().equals(TestConst.SVC_PERF_MODEL_ID) && pm.getName().equals(TestConst.SVC_PERF_MODEL)) {
                if (isThere) {
                    fail("Performance model can be there only once.");
                } else {
                    isThere = true;
                }
            }
        }
        assertTrue(isThere);
    }

    @Test
    public void testRuntimeReportInSimulation() throws Exception {
        IService s = this.ce.findService(TestConst.SVC_ID, this.proj);
        ServiceRuntimeConfiguration conf = new ServiceRuntimeConfiguration(s, ServiceRuntimeConfiguration.RuntimeMode.SIMULATING, true, ServiceRuntimeConfiguration.DeploymentState.READY);
        conf.setDataModelId(s.getDataModels().iterator().next().getId());
        this.ce.setServiceRuntime(s, conf);

        ServiceRuntimeReport rep = this.ce.getServiceRuntimeReport(s);
        assertNotNull(rep);
    }

    @Test
    public void testRuntimeReportInStandBy() throws Exception {
        IService s = this.ce.findService(TestConst.SVC_ID, this.proj);
        ServiceRuntimeConfiguration conf = new ServiceRuntimeConfiguration(s, ServiceRuntimeConfiguration.RuntimeMode.STAND_BY, true, ServiceRuntimeConfiguration.DeploymentState.READY);
        this.ce.setServiceRuntime(s, conf);

        ServiceRuntimeReport rep = this.ce.getServiceRuntimeReport(s);
        assertNotNull(rep);
    }

    /*@Test //VS cant set service to learning mode
    public void testChangeFromLearningToSimulating() throws Exception {
        //Take it to learning mode
        IService s = this.ce.findService(TestConst.SVC_ID, this.proj);
        ServiceRuntimeConfiguration conf = new ServiceRuntimeConfiguration(s, ServiceRuntimeConfiguration.RuntimeMode.LEARNING, true, ServiceRuntimeConfiguration.DeploymentState.READY);
        conf.setDataModelId(s.getDataModels().iterator().next().getId());
        this.ce.setServiceRuntime(s, conf);

        conf = this.ce.getServiceRuntimeInfo(s);
        assertEquals(ServiceRuntimeConfiguration.RuntimeMode.LEARNING, conf.getRuntimeMode());
        assertEquals(ServiceRuntimeConfiguration.DeploymentState.READY, conf.getDeploymentState());

        //Take it to simulating mode
        conf.setRuntimeMode(ServiceRuntimeConfiguration.RuntimeMode.SIMULATING);
        this.ce.setServiceRuntime(s, conf);

        conf = this.ce.getServiceRuntimeInfo(s);
        assertEquals(ServiceRuntimeConfiguration.RuntimeMode.SIMULATING, conf.getRuntimeMode());
        assertEquals(ServiceRuntimeConfiguration.DeploymentState.READY, conf.getDeploymentState());
    }*/
    
    @Test
    public void testSimulatingLocked() throws Exception {
        IService s = this.ce.findService(TestConst.SVC_ID, this.proj);
        ServiceRuntimeConfiguration conf = new ServiceRuntimeConfiguration(s, ServiceRuntimeConfiguration.RuntimeMode.SIMULATING, true, ServiceRuntimeConfiguration.DeploymentState.READY);
        conf.setDataModelId(TestConst.SVC_DATA_MODEL_ID);
        
        this.ce.setServiceRuntime(s, conf);
        
        conf = this.ce.getServiceRuntimeInfo(s);
        assertEquals(true, conf.isLocked());
    }
    
    @Test
    public void testStandByUnlocked() throws Exception {
        IService s = this.ce.findService(TestConst.SVC_ID, this.proj);
        ServiceRuntimeConfiguration conf = new ServiceRuntimeConfiguration(s, ServiceRuntimeConfiguration.RuntimeMode.STAND_BY, true, ServiceRuntimeConfiguration.DeploymentState.READY);
        
        this.ce.setServiceRuntime(s, conf);
        
        conf = this.ce.getServiceRuntimeInfo(s);
        assertEquals(false, conf.isLocked());
    }

    //============================== PRIVATE METHODS ==========================================

    //============================== GETTERS / SETTERS ========================================

    //============================== INNER CLASSES ============================================

}
