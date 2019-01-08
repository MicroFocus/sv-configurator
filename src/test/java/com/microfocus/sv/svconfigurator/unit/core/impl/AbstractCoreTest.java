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

import com.microfocus.sv.svconfigurator.resources.Resources;

import net.lingala.zip4j.core.ZipFile;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class AbstractCoreTest {
    //============================== STATIC ATTRIBUTES ========================================

    public static final String EXAMPLE_PROJECT_ARCHIVE = "test/exampleSV.vproja";
    public static final String ENCRYPTED_PROJECT_ARCHIVE = "test/encryptedArchive.vproja";
    public static final String ENCRYPTED_PROJECT_PASSWORD = "noname";
    public static final String PROJECT_ENTRY = "Rest Management.vproj";
    public static final String SERVICE_ENTRY = "Example Org Service.vs";
    public static final String DATA_MODEL_ENTRY = "Example Org Data Model.vsmodel";
    public static final String PERF_MODEL_ENTRY = "Example Org Service Performance Model.vspfmodel";
    public static final String DATA_SET_ENTRY = "Example Org Data Model Dataset 001.vsdataset";
    public static final String SERVICE_DESCRIPTION_ENTRY = "Example Org Service Description.vsdsc";
    public static final String MANIFEST_ENTRY = "Example Org Service.vsmf";

    //============================== INSTANCE ATTRIBUTES ======================================

    protected ZipFile zf;

    //============================== STATIC METHODS ===========================================

    public static void assertStreamEquals(InputStream stream1, InputStream stream2) throws Exception {
        int bfrSize = 1024; //1kB
        byte[] dmBfr = new byte[bfrSize];
        byte[] entryBfr = new byte[bfrSize];

        int available = Math.min(Math.min(stream1.available(), stream2.available()), bfrSize);
        while (available != 0) {
            stream1.read(dmBfr, 0, available);
            stream2.read(entryBfr, 0, available);
            assertTrue("Streams does not contain the same data!", Arrays.equals(dmBfr, entryBfr));

            available = Math.min(Math.min(stream1.available(), stream2.available()), bfrSize);
        }

        assertEquals("Stream1 is not in the end.", 0, stream1.available());
        assertEquals("Stream2 is not in the end.", 0, stream2.available());

        stream1.close();
        stream2.close();
    }

    //============================== CONSTRUCTORS =============================================

    //============================== ABSTRACT METHODS =========================================

    //============================== OVERRIDEN METHODS ========================================

    //============================== INSTANCE METHODS =========================================


    @Test
    public void testName() throws Exception {

    }

    @Before
    public void abstractSetUp() throws Exception {
        this.zf = new ZipFile(Resources.getResource(AbstractCoreTest.EXAMPLE_PROJECT_ARCHIVE));
    }

    //============================== PRIVATE METHODS ==========================================

    //============================== GETTERS / SETTERS ========================================

    //============================== INNER CLASSES ============================================

}
