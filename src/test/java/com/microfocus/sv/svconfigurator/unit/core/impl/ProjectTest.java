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
package com.microfocus.sv.svconfigurator.unit.core.impl;

import com.microfocus.sv.svconfigurator.core.*;
import com.microfocus.sv.svconfigurator.core.impl.Project;
import com.microfocus.sv.svconfigurator.core.impl.Service;
import com.microfocus.sv.svconfigurator.core.impl.datasource.ArchiveProjectElementDataSource;
import com.microfocus.sv.svconfigurator.core.impl.encryption.EncryptedNode;
import net.lingala.zip4j.model.FileHeader;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;

import static org.junit.Assert.*;

public class ProjectTest extends AbstractCoreTest {

    public static final String PROJECT_ID = "dummyProjectId";
    public static final String PROJECT_NAME = "dummyProjectName";

    //============================== STATIC ATTRIBUTES ========================================
    //============================== INSTANCE ATTRIBUTES ======================================
    private IProject project;
    private FileHeader ze;
    private IProjectElementDataSource ds;

    //============================== STATIC METHODS ===========================================

    //============================== CONSTRUCTORS =============================================

    //============================== ABSTRACT METHODS =========================================

    //============================== OVERRIDEN METHODS ========================================

    @Before
    public void setUp() throws Exception {
        this.ze = this.zf.getFileHeader(AbstractCoreTest.PROJECT_ENTRY);
        this.ds = new ArchiveProjectElementDataSource(zf, ze);
        this.project = new Project(PROJECT_ID, PROJECT_NAME, null, null, ds);
    }

    //============================== INSTANCE METHODS =========================================

    @Test
    public void testId() throws Exception {
        assertEquals("Id is not the same", PROJECT_ID, this.project.getId());
    }

    public void testName() throws Exception {
        assertEquals("Name is not the same", PROJECT_NAME, this.project.getName());
    }

    @Test
    public void testAccept() throws Exception {
        ProjectVisitor v = new ProjectVisitor();
        this.project.accept(v);
        assertTrue("Visitor was not visited.", v.visited);
    }

    @Test
    public void testClose() throws Exception {
        try {
            this.project.close();
        } catch (Exception ex) {
            fail("Exception should not be thrown");
        }
    }

    @Test
    public void testAddGetService() throws Exception {
        IService dummySvc = new Service("dummyId", "dummy name");

        this.project.addService(dummySvc);
        Collection<IService> svcs = this.project.getServices();
        assertEquals("There should be one service", 1, svcs.size());

        IService obtained = svcs.iterator().next();
        assertEquals("Services are not the same", dummySvc, obtained);
    }

    @Test
    public void testData() throws Exception {
        AbstractCoreTest.assertStreamEquals(this.zf.getInputStream(this.ze), this.project.getData());
    }

    @Test
    public void testDataSize() throws Exception {
        assertEquals("Data sizes are not the same.", this.ze.getUncompressedSize(), this.project.getDataLength());
    }

    //============================== PRIVATE METHODS ==========================================

    //============================== GETTERS / SETTERS ========================================

    //============================== INNER CLASSES ============================================

    private static class ProjectVisitor implements IProjectElementVisitor {

        private boolean visited = false;

        @Override
        public void visit(IProject p) {
            visited = true;
        }

        @Override
        public void visit(IService s) {
            fail("Should not be called.");
        }

        @Override
        public void visit(IDataModel dm) {
            fail("Should not be called.");
        }

        @Override
        public void visit(IPerfModel pm) {
            fail("Should not be called.");
        }

        @Override
        public void visit(IServiceDescription sd) {
            fail("Should not be called.");
        }

        @Override
        public void visit(IDataSet ds) {
            fail("Should not be called.");
        }

        @Override
        public void visit(IManifest m) {
            fail("Should not be called.");
        }

        @Override
        public void visit(ITopology t) {
            fail("Should not be called.");
        }

        @Override
        public void visit(IContentFile t) {
            fail("Should not be called.");
        }

        @Override
        public void visit(ILoggedServiceCallList loggedServiceCallList) {
            fail("Should not be called.");
        }
    }

}
