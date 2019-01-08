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
import com.microfocus.sv.svconfigurator.core.impl.datasource.ArchiveProjectElementDataSource;
import net.lingala.zip4j.model.FileHeader;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class DataSetTest extends AbstractCoreTest {

    public static final String ID = "DummyDataSet";
    public static final String NAME = "DummyDataSet name";
    public static final long HASH_CODE = 1234;

    //============================== STATIC ATTRIBUTES ========================================

    //============================== INSTANCE ATTRIBUTES ======================================
    private DataSet ds;
    private FileHeader ze;
    private IProjectElementDataSource dsource;

    //============================== STATIC METHODS ===========================================

    //============================== CONSTRUCTORS =============================================

    //============================== ABSTRACT METHODS =========================================

    //============================== OVERRIDEN METHODS ========================================

    //============================== INSTANCE METHODS =========================================

    @Before
    public void setUp() throws Exception {
        this.ze = this.zf.getFileHeader(AbstractCoreTest.DATA_SET_ENTRY);
        this.dsource = new ArchiveProjectElementDataSource(zf, ze);
        this.ds = new DataSet(ID, NAME, HASH_CODE, this.dsource, null, null);
    }

    @Test
    public void testId() throws Exception {
        assertEquals("Ids are not the same", ID, this.ds.getId());
    }

    @Test
    public void testName() throws Exception {
        assertEquals("Names are not the same", NAME, this.ds.getName());
    }

    @Test
    public void testHashCode() throws Exception {
        assertEquals("HashCode is not ok", HASH_CODE, this.ds.getDataHashCode());
    }

    @Test
    public void testDataModel() throws Exception {
        DataModel dm = new DataModel("ParentModel", "parent name", null, null, null);

        this.ds.setDataModel(dm);
        assertEquals("Parent Data Model is not the same", dm, this.ds.getDataModel());

        try {
            this.ds.setDataModel(new DataModel("Illegal Data Model", "Illegal name", null, null, null));
            fail("Illegal State Exception should be thrown.");
        } catch (IllegalStateException ex) {
            //this is ok, there should be only one parent data model.
        }
    }

    @Test
    public void testData() throws Exception {
        AbstractCoreTest.assertStreamEquals(this.zf.getInputStream(this.ze), this.ds.getData());
    }

    @Test
    public void testDataSize() throws Exception {
        assertEquals("Data sizes are not the same.", this.ze.getUncompressedSize(), this.ds.getDataLength());
    }

    @Test
    public void testAccept() throws Exception {
        DataSetTestVisitor v = new DataSetTestVisitor();
        this.ds.accept(v);

        assertTrue("Visitor was not visited.", v.visited);
    }

    //============================== PRIVATE METHODS ==========================================

    //============================== GETTERS / SETTERS ========================================

    //============================== INNER CLASSES ============================================
    private static class DataSetTestVisitor implements IProjectElementVisitor {

        private boolean visited = false;

        @Override
        public void visit(IProject p) {
            fail("should not be called");
        }

        @Override
        public void visit(IService s) {
            fail("should not be called");
        }

        @Override
        public void visit(IDataModel dm) {
            fail("should not be called");
        }

        @Override
        public void visit(IPerfModel pm) {
            fail("should not be called");
        }

        @Override
        public void visit(IServiceDescription sd) {
            fail("should not be called");
        }

        @Override
        public void visit(IDataSet ds) {
            this.visited = true;
        }

        @Override
        public void visit(IManifest m) {
            fail("should not be called");
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
