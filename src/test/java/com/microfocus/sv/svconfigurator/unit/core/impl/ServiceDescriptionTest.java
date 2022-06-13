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

import com.microfocus.sv.svconfigurator.core.IContentFile;
import com.microfocus.sv.svconfigurator.core.IDataModel;
import com.microfocus.sv.svconfigurator.core.IDataSet;
import com.microfocus.sv.svconfigurator.core.ILoggedServiceCallList;
import com.microfocus.sv.svconfigurator.core.IManifest;
import com.microfocus.sv.svconfigurator.core.IPerfModel;
import com.microfocus.sv.svconfigurator.core.IProject;
import com.microfocus.sv.svconfigurator.core.IProjectElementDataSource;
import com.microfocus.sv.svconfigurator.core.IProjectElementVisitor;
import com.microfocus.sv.svconfigurator.core.IService;
import com.microfocus.sv.svconfigurator.core.IServiceDescription;
import com.microfocus.sv.svconfigurator.core.ITopology;
import com.microfocus.sv.svconfigurator.core.impl.ServiceDescription;
import com.microfocus.sv.svconfigurator.core.impl.datasource.ArchiveProjectElementDataSource;
import net.lingala.zip4j.model.FileHeader;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class ServiceDescriptionTest extends AbstractCoreTest {

    public static final String ID = "DummySD";
    public static final String NAME = "DummySDName";

    //============================== STATIC ATTRIBUTES ========================================

    //============================== INSTANCE ATTRIBUTES ======================================

    private ServiceDescription sd;
    private FileHeader ze;
    private IProjectElementDataSource ds;

    //============================== STATIC METHODS ===========================================

    //============================== CONSTRUCTORS =============================================

    //============================== ABSTRACT METHODS =========================================

    //============================== OVERRIDEN METHODS ========================================

    //============================== INSTANCE METHODS =========================================

    @Before
    public void setUp() throws Exception {
        this.ze = this.zf.getFileHeader(AbstractCoreTest.SERVICE_DESCRIPTION_ENTRY);
        this.ds = new ArchiveProjectElementDataSource(zf, ze);
        this.sd = new ServiceDescription(ID, NAME, ds, null, null);
    }

    @Test
    public void testId() throws Exception {
        assertEquals("Ids are not the same.", ID, this.sd.getId());
    }

    @Test
    public void testName() throws Exception {
        assertEquals("Names are not the same.", NAME, this.sd.getName());
    }

    @Test
    public void testAccept() throws Exception {
        SDTestVisitor v = new SDTestVisitor();
        this.sd.accept(v);
        assertTrue("Visitor wan't visited.", v.visited);
    }

    @Test
    public void testData() throws Exception {
        AbstractCoreTest.assertStreamEquals(this.zf.getInputStream(this.ze), this.sd.getData());
    }

    @Test
    public void testDataSize() throws Exception {
        assertEquals("Data sizes are not the same.", this.ze.getUncompressedSize(), this.sd.getDataLength());
    }

    //============================== PRIVATE METHODS ==========================================

    //============================== GETTERS / SETTERS ========================================

    //============================== INNER CLASSES ============================================

    private static class SDTestVisitor implements IProjectElementVisitor {

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
            fail("Should not be called");
        }

        @Override
        public void visit(IServiceDescription sd) {
            this.visited = true;
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

        @Override
        public void visit(ILoggedServiceCallList loggedServiceCallList) {
            fail("Should not be called.");
        }
    }

}
