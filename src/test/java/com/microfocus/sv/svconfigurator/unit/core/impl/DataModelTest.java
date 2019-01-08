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
import com.microfocus.sv.svconfigurator.core.impl.DataModel;
import com.microfocus.sv.svconfigurator.core.impl.DataSet;
import com.microfocus.sv.svconfigurator.core.impl.Service;
import com.microfocus.sv.svconfigurator.core.impl.datasource.ArchiveProjectElementDataSource;
import com.microfocus.sv.svconfigurator.core.impl.encryption.EncryptedNode;
import net.lingala.zip4j.model.FileHeader;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

public class DataModelTest extends AbstractCoreTest {


    //============================== STATIC ATTRIBUTES ========================================

    private static final String DM_ID = "TestDataModel";
    private static final String DM_NAME = "TestDataModelName";

    //============================== INSTANCE ATTRIBUTES ======================================
    private FileHeader ze;
    private DataModel dm;
    private IProjectElementDataSource ds;

    //============================== STATIC METHODS ===========================================

    //============================== CONSTRUCTORS =============================================

    //============================== ABSTRACT METHODS =========================================

    //============================== OVERRIDEN METHODS ========================================

    //============================== INSTANCE METHODS =========================================

    @Before
    public void setUp() throws Exception {
        this.ze = this.zf.getFileHeader(AbstractCoreTest.DATA_MODEL_ENTRY);
        this.ds = new ArchiveProjectElementDataSource(zf, ze);
        this.dm = new DataModel(DM_ID, DM_NAME, ds, null, null);
    }

    @Test
    public void testId() throws Exception {
        assertEquals("Id is not the same", DM_ID, this.dm.getId());
    }

    @Test
    public void testName() throws Exception {
        assertEquals("Name is not the same", DM_NAME, this.dm.getName());
    }

    @Test
    public void testAccept() throws Exception {
        DataModelTestVisitor v = new DataModelTestVisitor();
        this.dm.accept(v);

        assertTrue("Visitor was not visited.", v.visited);
    }

    @Test
    public void testDataSets() throws Exception {
        Set<DataSet> dss = new HashSet<DataSet>();
        for (int i = 1; i <= 5; i++) {
            DataSet ds = new DataSet("DataSet_" + i, "DS " + i + " name", 1234, null, null, null);
            dss.add(ds);
            this.dm.addDataSet(ds);
        }

        for (IDataSet ds : this.dm.getDataSets()) {
            if (!dss.contains(ds)) {
                fail("The data set was not added so it should not be there.");
            }
        }
    }

    @Test
    public void testService() throws Exception {
        Service svc = new Service("DummyService", "SVC Name");

        this.dm.setService(svc);
        assertEquals("Services are not the same", svc, this.dm.getService());

        try {
            this.dm.setService(new Service("IllegalService", "Illegal service name"));
            fail("Another set service call should throw an exception.");
        } catch (IllegalStateException ex) {
            //this is ok, another call to ser service should throw an illegal state exception because DataModel should belong to only one Service.
        }
    }

    @Test
    public void testData() throws Exception {
        AbstractCoreTest.assertStreamEquals(this.dm.getData(), this.zf.getInputStream(this.ze));
    }

    @Test
    public void testDataSize() throws Exception {
        assertEquals("Data size is not the same", ze.getUncompressedSize(), this.dm.getDataLength());
    }

    //============================== PRIVATE METHODS ==========================================

    //============================== GETTERS / SETTERS ========================================

    //============================== INNER CLASSES ============================================

    private static class DataModelTestVisitor implements IProjectElementVisitor {

        private boolean visited = false;

        @Override
        public void visit(IProject p) {
            fail("Should not be called");
        }

        @Override
        public void visit(IService s) {
            fail("Should not be called");
        }

        @Override
        public void visit(IDataModel dm) {
            this.visited = true;
        }

        @Override
        public void visit(IPerfModel pm) {
            fail("Should not be called");
        }

        @Override
        public void visit(IServiceDescription sd) {
            fail("Should not be called");
        }

        @Override
        public void visit(IDataSet ds) {
            fail("Should not be called");
        }

        @Override
        public void visit(IManifest m) {
            fail("Should not be called");
        }

        @Override
        public void visit(ITopology t) {
            fail("Should not be called.");
        }

        @Override
        public void visit(IContentFile t) {
            fail("Should not be called.");
        }
    }

}
