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
import com.microfocus.sv.svconfigurator.core.impl.Manifest;
import com.microfocus.sv.svconfigurator.core.impl.datasource.ArchiveProjectElementDataSource;
import net.lingala.zip4j.model.FileHeader;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ManifestTest extends AbstractCoreTest {

    public static final String ID = "DummyManifest";
    public static final String NAME = "DummyManifestName";
    //============================== STATIC ATTRIBUTES ========================================
    //============================== INSTANCE ATTRIBUTES ======================================
    private FileHeader ze;
    private Manifest m;
    private IProjectElementDataSource ds;

    //============================== STATIC METHODS ===========================================

    //============================== CONSTRUCTORS =============================================

    //============================== ABSTRACT METHODS =========================================

    //============================== OVERRIDEN METHODS ========================================

    //============================== INSTANCE METHODS =========================================

    @Before
    public void setUp() throws Exception {
        this.ze = this.zf.getFileHeader(AbstractCoreTest.MANIFEST_ENTRY);
        this.ds = new ArchiveProjectElementDataSource(zf, ze);
        this.m = new Manifest(ID, NAME, this.ds);
    }

    @Test
    public void testId() throws Exception {
        assertEquals("Ids are not the same", ID, this.m.getId());
    }

    @Test
    public void testAccept() throws Exception {
        ManifestTestVisitor v = new ManifestTestVisitor();
        this.m.accept(v);
        assertTrue("Visitor was not called", v.visited);
    }

    @Test
    public void testData() throws Exception {
        AbstractCoreTest.assertStreamEquals(this.zf.getInputStream(this.ze), this.m.getData());
    }

    @Test
    public void testDataSize() throws Exception {
        assertEquals("DataSizes are not the same", this.ze.getUncompressedSize(), this.m.getDataLength());
    }

    //============================== PRIVATE METHODS ==========================================

    //============================== GETTERS / SETTERS ========================================

    //============================== INNER CLASSES ============================================

    private static class ManifestTestVisitor implements IProjectElementVisitor {

        private boolean visited = false;

        @Override
        public void visit(IProject p) {
            fail("Should not be called.");
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
            this.visited = true;
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
