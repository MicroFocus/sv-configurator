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
package com.microfocus.sv.svconfigurator.unit.core.impl;

import com.microfocus.sv.svconfigurator.core.*;
import com.microfocus.sv.svconfigurator.core.impl.*;
import com.microfocus.sv.svconfigurator.core.impl.datasource.ArchiveProjectElementDataSource;
import net.lingala.zip4j.model.FileHeader;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

public class ServiceTest extends AbstractCoreTest {

    public static final String ID = "dummyId";

    public static final String NAME = "dummy Name";


    //============================== STATIC ATTRIBUTES ========================================

    //============================== INSTANCE ATTRIBUTES ======================================

    private IService service;
    private FileHeader ze;
    private IProjectElementDataSource ds;

    //============================== STATIC METHODS ===========================================

    //============================== CONSTRUCTORS =============================================

    //============================== ABSTRACT METHODS =========================================

    //============================== OVERRIDEN METHODS ========================================

    //============================== INSTANCE METHODS =========================================

    @Before
    public void setUp() throws Exception {
        this.ze = this.zf.getFileHeader(AbstractCoreTest.SERVICE_ENTRY);
        this.ds = new ArchiveProjectElementDataSource(zf, ze);
        this.service = new Service(ID, NAME, ds, null, null, null, false);
    }

    @Test
    public void testAccept() throws Exception {
        ServiceTestVisitor v = new ServiceTestVisitor();
        this.service.accept(v);
        assertTrue("Visitor was not visited.", v.visited);
    }

    @Test
    public void testDataModels() throws Exception {
        Set<DataModel> dms = new HashSet<DataModel>();
        dms.add(new DataModel("dummyModel_1", "name1", null, null, null));
        dms.add(new DataModel("dummyModel_2", "name2", null, null, null));
        dms.add(new DataModel("dummyModel_3", "name3", null, null, null));

        for (DataModel dm : dms) {
            this.service.addDataModel(dm);
        }

        assertEquals("There should be "+ dms.size() + " data models", dms.size(), this.service.getDataModels().size());

        for (IDataModel dm : this.service.getDataModels()) {
            if (! dms.contains(dm)) {
                fail("The data model was not added, shouldn't be there.");
            }
        }
    }

    @Test
    public void testPerfModels() throws Exception {
        Set<PerfModel> pms = new HashSet<PerfModel>();
        pms.add(new PerfModel("dummyPerfModel_1", "dmpm1", null, null, null));
        pms.add(new PerfModel("dummyPerfModel_2", "dmpm2", null, null, null));
        pms.add(new PerfModel("dummyPerfModel_3", "dmpm3", null, null, null));

        for (PerfModel pm : pms) {
            this.service.addPerfModel(pm);
        }

        assertEquals("There should be "+ pms.size() + " perf models", pms.size(), this.service.getPerfModels().size());

        for (IPerfModel pm : this.service.getPerfModels()) {
            if (! pms.contains(pm)) {
                fail("The data model was not added, shouldn't be there.");
            }
        }
    }

    @Test
    public void testDescription() throws Exception {
        ServiceDescription sd = new ServiceDescription("Dummy Descr 1", "DDSCR 1", null, null, null);
        this.service.addDescription(sd);

        assertEquals("Description count error", 1, this.service.getDescriptions().size());
        assertEquals("Description is not the same", sd, this.service.getDescriptions().iterator().next());

        this.service.addDescription(new ServiceDescription("should fail description", "illegal name", null, null, null));
        assertEquals("Description count error", 2, this.service.getDescriptions().size());
    }

    @Test
    public void testBaseProject() throws Exception {
        Project p = new Project("DummyBaseProject", "XYZ", null, null, null);
        this.service.setBaseProject(p);

        assertEquals("Project is not the same", p, this.service.getBaseProject());
    }

    @Test
    public void testId() throws Exception {
        assertEquals("Id is not the same", ID, this.service.getId());
    }

    @Test
    public void testData() throws Exception {
        AbstractCoreTest.assertStreamEquals(zf.getInputStream(ze), this.service.getData());
    }

    @Test
    public void testDataSize() throws Exception {
        assertEquals("Data size is not the same", ze.getUncompressedSize(), this.service.getDataLength());
    }

    //============================== PRIVATE METHODS ==========================================

    //============================== GETTERS / SETTERS ========================================

    //============================== INNER CLASSES ============================================

    private static class ServiceTestVisitor implements IProjectElementVisitor {

        private boolean visited = false;

        @Override
        public void visit(IProject p) {
            fail("Should not be called.");
        }

        @Override
        public void visit(IService s) {
            this.visited = true;
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
