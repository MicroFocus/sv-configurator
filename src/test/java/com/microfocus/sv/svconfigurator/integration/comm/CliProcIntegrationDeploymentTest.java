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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import com.microfocus.sv.svconfigurator.build.ProjectBuilder;
import com.microfocus.sv.svconfigurator.cli.impl.DeployCLICommandProcessor;
import com.microfocus.sv.svconfigurator.core.impl.jaxb.atom.ServiceListAtom;
import com.microfocus.sv.svconfigurator.core.impl.processor.ElementStatus;
import com.microfocus.sv.svconfigurator.integration.IntegrationTest;
import com.microfocus.sv.svconfigurator.integration.TestConst;
import com.microfocus.sv.svconfigurator.processor.DeployProcessor;
import com.microfocus.sv.svconfigurator.resources.Resources;
import com.microfocus.sv.svconfigurator.serverclient.impl.CommandExecutorFactory;
import com.microfocus.sv.svconfigurator.service.ServiceAmendingServiceImpl;
import com.microfocus.sv.svconfigurator.util.TestUtils;

@RunWith(Parameterized.class)
@Category(IntegrationTest.class)
@Ignore(value="Obsolete project")
public class CliProcIntegrationDeploymentTest extends AbstractCliProcIntegrationTest {

    private static final String ENCRYPTED_SERVICE_PROJECT_FILE_NAME = "test/encryption/EncryptedServiceVirtualizationProject.vproj";

    private File projectFile;
    private String projectId;
    private String projectName;
    private String serviceId;
    private String serviceName;

    @BeforeClass
    public static void setUpClass() throws Exception {
        AbstractCliProcIntegrationTest.setUpClass();
    }

    public CliProcIntegrationDeploymentTest(File projectFile, String projectId, String projectName, String serviceId, String serviceName) {
        this.projectFile = projectFile;
        this.projectId = projectId;
        this.projectName = projectName;
        this.serviceId = serviceId;
        this.serviceName = serviceName;
    }

    @Parameterized.Parameters
    public static java.util.Collection data() {
        File claimDemoProject = null;
        File encryptedServiceProject = null;
        try {
            claimDemoProject = TestConst.getClaimDemoProject();
            encryptedServiceProject = Resources.getResource(ENCRYPTED_SERVICE_PROJECT_FILE_NAME);
        } catch (Exception e) {
            fail(e.getMessage());
        }
        Object[][] data = new Object[][] {
                {claimDemoProject, TestConst.PROJ_ID , TestConst.PROJ_NAME , TestConst.SVC_ID, TestConst.SVC_NAME },
                {encryptedServiceProject, "{366ABE23-60CD-4DD7-808E-8AEA19EA1DB8}" , "EncryptedServiceVirtualizationProject" , "2b3e6023-d0e7-49dd-b6bf-24ee9f39e2d4", "Enc2 Service" }
        };
        return Arrays.asList(data);
    }

    @Test
    /**
     * Tests to deployProject the project onto the server (whole project, single service) + its undeployment
     */
    public void testDeployment() throws Exception {
        clearAll();
        //------- DEPLOY -----
        String[] cliParams = TestUtils.createParams(String.format("-usr %s -pwd %s -url %s -w aaa %s", 
                TestConst.MGMT_TST_USERNAME, TestConst.MGMT_TST_PASSWORD, TestConst.MGMT_TST_URI_STR, projectFile.getCanonicalPath()));
        
        DeployCLICommandProcessor p = new DeployCLICommandProcessor(new ProjectBuilder(), new DeployProcessor(new CommandExecutorFactory()));
        assertEquals(0, p.process(cliParams));

        ServiceListAtom sla = spc.getServiceList(projectId);
        List<ServiceListAtom.ServiceEntry> ses = sla.getEntries();
        assertEquals(1, ses.size());

        ServiceListAtom.ServiceEntry se = ses.get(0);
        assertEquals(serviceId, se.getId());
        assertEquals(serviceName, se.getTitle());
        assertEquals(projectName, se.getProjectName());

        //------- UNDEPLOY -----
        assertEquals(ElementStatus.PRESENT, spc.getServiceStatus(this.createDummyService(serviceId)));
        cliParams = TestUtils.createParams(String.format("-usr %s -pwd %s -url %s -u -w aaa %s", 
                TestConst.MGMT_TST_USERNAME, TestConst.MGMT_TST_PASSWORD, TestConst.MGMT_TST_URI_STR, projectFile.getCanonicalPath()));
        assertEquals(0, p.process(cliParams));
        this.waitForNotPresent(this.createDummyService(serviceId));

        //------ DEPLOY SVC ONLY-------
        cliParams =TestUtils.createParams(String.format("-usr %s -pwd %s -url %s -s %s -w aaa %s", 
                TestConst.MGMT_TST_USERNAME, TestConst.MGMT_TST_PASSWORD, TestConst.MGMT_TST_URI_STR, serviceId, projectFile.getCanonicalPath())); 
        assertEquals(0, p.process(cliParams));
        assertEquals(ElementStatus.PRESENT, spc.getServiceStatus(this.createDummyService(serviceId)));

        //----- undeploy Svc only ----
        cliParams =TestUtils.createParams(String.format("-usr %s -pwd %s -url %s -u -s %s -w aaa %s", 
                TestConst.MGMT_TST_USERNAME, TestConst.MGMT_TST_PASSWORD, TestConst.MGMT_TST_URI_STR, serviceId, projectFile.getCanonicalPath())); 
        assertEquals(0, p.process(cliParams));
        this.waitForNotPresent(this.createDummyService(serviceId));
    }
    
    @Test
    public void testDeploymentWithBadSvcName() throws Exception {
        clearAll();
        //------- DEPLOY -----
        String[] cliParams = new String[]{
                "-usr", TestConst.MGMT_TST_USERNAME,
                "-pwd", TestConst.MGMT_TST_PASSWORD,
                "-url", TestConst.MGMT_TST_URI_STR,
                "-w", "aaa",
                "-s", "BadServiceNameOhYeah",
                projectFile.getCanonicalPath()
        };
        DeployCLICommandProcessor p = new DeployCLICommandProcessor(new ProjectBuilder(), new DeployProcessor(new CommandExecutorFactory()));
        assertEquals(1300, p.process(cliParams));
    }
}
