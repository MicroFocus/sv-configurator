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
package com.microfocus.sv.svconfigurator.unit.build;

import com.microfocus.sv.svconfigurator.build.ProjectBuilder;
import com.microfocus.sv.svconfigurator.core.*;
import com.microfocus.sv.svconfigurator.core.impl.exception.ProjectBuilderException;
import com.microfocus.sv.svconfigurator.resources.Resources;
import com.microfocus.sv.svconfigurator.unit.core.impl.AbstractCoreTest;
import org.junit.Test;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ProjectBuilderTest {

    //============================== STATIC ATTRIBUTES ========================================

    private static final Map<String, String> projIdNames = new HashMap<String, String>();
    private static final Map<String, String> serviceIdNames = new HashMap<String, String>();
    private static final Map<String, String> dmIdNames = new HashMap<String, String>();
    private static final Map<String, String> pmIdNames = new HashMap<String, String>();
    private static final Map<String, String> sdIdNames = new HashMap<String, String>();
    private static final Map<String, String> dsIdNames = new HashMap<String, String>();

    static {
        //proj
        projIdNames.put("{783F07FB-9C80-4D45-91D3-DFC9406BF288}", "Rest Management");

        //services
        serviceIdNames.put("18d93068-cdcb-424f-9eb0-62d359a22a69", "Example Org Service");

        //data models
        dmIdNames.put("8757c788-744b-4e55-9322-e3c2e893080e", "Example Org Data Model");

        //performance models
        pmIdNames.put("07837fb5-2921-4ea7-af99-67b48c27c3d6", "Example Org Performance Model");
        pmIdNames.put("cfe4b70b-2783-4f34-9f37-9e23d29cb86f", "Offline");

        //service descriptions
        sdIdNames.put("90f284ed-583c-4ae6-a53d-5e97cb85e5c7", "Example Org Service Description");

        //data sets
        dsIdNames.put("fecb9cb6-e0ed-41a2-a004-aa2489388e4b", "Example Org Data Model Dataset");
        dsIdNames.put("26f16479-9d43-423e-a4bc-1b2c0551328f", "Example Org Data Model Dataset 001");
    }

    //============================== INSTANCE ATTRIBUTES ======================================

    //============================== STATIC METHODS ===========================================

    //============================== CONSTRUCTORS =============================================

    //============================== ABSTRACT METHODS =========================================

    //============================== OVERRIDEN METHODS ========================================

    //============================== INSTANCE METHODS =========================================

    @Test
    public void testBuildProject() throws Exception {
        File projFile = Resources.getResource(AbstractCoreTest.EXAMPLE_PROJECT_ARCHIVE);
        IProject project = new ProjectBuilder().buildProject(projFile, null);

        assertTrue("Project id is not correct", projIdNames.containsKey(project.getId()));
        assertEquals("Project name is not correct", projIdNames.get(project.getId()), project.getName());
        projIdNames.remove(project.getId());

        for (IService s : project.getServices()) {
            assertTrue("Service ID is not correct", serviceIdNames.containsKey(s.getId()));
            assertEquals("Service name is not correct", serviceIdNames.get(s.getId()), s.getName());
            serviceIdNames.remove(s.getId());

            for (IDataModel dm : s.getDataModels()) {
                assertTrue("DataModel id is not correct", dmIdNames.containsKey(dm.getId()));
                assertEquals("DataModel name is not correect", dmIdNames.get(dm.getId()), dm.getName());
                dmIdNames.remove(dm.getId());

                for (IDataSet ds : dm.getDataSets()) {
                    assertTrue("DataSet id is not correct", dsIdNames.containsKey(ds.getId()));
                    assertEquals("DataSet name is not correct", dsIdNames.get(ds.getId()), ds.getName());
                    dsIdNames.remove(ds.getId());

                    assertEquals("Parent dataModel is not correct", ds.getDataModel(), dm);
                }

                assertEquals("Parent Service (of a data model) is not correct", dm.getService(), s);
            }

            for (IPerfModel pm : s.getPerfModels()) {
                assertTrue("PerfModel id is not correct", pmIdNames.containsKey(pm.getId()));
                assertEquals("PerfModel name is not correct", pmIdNames.get(pm.getId()), pm.getName());
                pmIdNames.remove(pm.getId());

                assertEquals("Service of performanceModel is not correct", s, pm.getService());
            }

            for (IServiceDescription sd : s.getDescriptions()) {
                assertTrue("ServiceDescription id is not correct", sdIdNames.containsKey(sd.getId()));
                assertEquals("ServiceDescription name is not correct", sdIdNames.get(sd.getId()), sd.getName());
                sdIdNames.remove(sd.getId());
            }

            assertEquals("Project of the service is not correct", project, s.getBaseProject());
        }
    }

    @Test(expected = ProjectBuilderException.class)
    public void testEncryptedProjectArchiveWithoutPassword() throws Exception {
        File projFile = Resources.getResource(AbstractCoreTest.ENCRYPTED_PROJECT_ARCHIVE);
        new ProjectBuilder().buildProject(projFile, null);
    }

    @Test
    public void testEncryptedProjectArchive() throws Exception {
        File projFile = Resources.getResource(AbstractCoreTest.ENCRYPTED_PROJECT_ARCHIVE);
        IProject project = new ProjectBuilder().buildProject(projFile, AbstractCoreTest.ENCRYPTED_PROJECT_PASSWORD);
        assertEquals("Project id is not correct", "{9B3A0C94-3965-4342-90F4-995D626F5A39}", project.getId());
    }

//============================== PRIVATE METHODS ==========================================

    //============================== GETTERS / SETTERS ========================================

    //============================== INNER CLASSES ============================================

}
