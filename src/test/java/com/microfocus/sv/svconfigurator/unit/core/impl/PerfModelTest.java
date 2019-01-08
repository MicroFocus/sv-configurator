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
import com.microfocus.sv.svconfigurator.core.impl.AbstractPerfModel;
import com.microfocus.sv.svconfigurator.core.impl.PerfModel;
import com.microfocus.sv.svconfigurator.core.impl.Service;
import com.microfocus.sv.svconfigurator.core.impl.datasource.ArchiveProjectElementDataSource;

import net.lingala.zip4j.model.FileHeader;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class PerfModelTest extends AbstractCoreTest {

    public static final String ID = "DummyPerfModel";
    public static final String NAME = "DummyPerfModelName";

    //============================== STATIC ATTRIBUTES ========================================

    //============================== INSTANCE ATTRIBUTES ======================================
    private AbstractPerfModel pm;
    private FileHeader ze;
    private IProjectElementDataSource ds;

    //============================== STATIC METHODS ===========================================

    //============================== CONSTRUCTORS =============================================

    //============================== ABSTRACT METHODS =========================================

    //============================== OVERRIDEN METHODS ========================================

    //============================== INSTANCE METHODS =========================================

    @Before
    public void setUp() throws Exception {
        this.ze = this.zf.getFileHeader(AbstractCoreTest.PERF_MODEL_ENTRY);
        this.ds = new ArchiveProjectElementDataSource(zf, ze);
        this.pm = new PerfModel(ID, NAME, ds, null, null);
    }

    @Test
    public void testId() throws Exception {
        assertEquals("Id is not the same", ID, this.pm.getId());
    }

    @Test
    public void testName() throws Exception {
        assertEquals("Name is not the same", NAME, this.pm.getName());
    }

    @Test
    public void testService() throws Exception {
        Service s = new Service("DummySvc", "Dummy SVC Name");
        this.pm.setService(s);

        assertEquals("Service is not the same", s, this.pm.getService());

        try {
            this.pm.setService(new Service("Illegal Service", "Illegal SVC name"));
            fail("Should have thrown an exception.");
        } catch (IllegalStateException ex) {
            //This is ok, Perfmodel should belong only to one service.
        }
    }

    @Test
    public void testAccept() throws Exception {
        PerfModelTestVisitor v = new PerfModelTestVisitor();
        this.pm.accept(v);
        assertTrue("Visitor was not visited.", v.visited);
    }

    @Test
    public void testData() throws Exception {
        AbstractCoreTest.assertStreamEquals(this.zf.getInputStream(this.ze), this.pm.getData());
    }

    @Test
    public void testDataSize() throws Exception {
        assertEquals("Data sizes are not the same", this.ze.getUncompressedSize(), this.pm.getDataLength());
    }

    //============================== PRIVATE METHODS ==========================================

    //============================== GETTERS / SETTERS ========================================

    //============================== INNER CLASSES ============================================

    private static class PerfModelTestVisitor implements IProjectElementVisitor {

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
            fail("Should not be called");
        }

        @Override
        public void visit(IPerfModel pm) {
            visited = true;
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
