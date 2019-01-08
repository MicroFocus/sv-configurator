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
package com.microfocus.sv.svconfigurator.unit.util;

import com.microfocus.sv.svconfigurator.core.IPerfModel;
import com.microfocus.sv.svconfigurator.core.IService;
import com.microfocus.sv.svconfigurator.core.impl.PerfModel;
import com.microfocus.sv.svconfigurator.core.impl.encryption.EncryptedNode;
import com.microfocus.sv.svconfigurator.util.ProjectUtils;
import org.apache.commons.cli.ParseException;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class ProjectUtilsTest {


    //============================== STATIC ATTRIBUTES ========================================

    //============================== INSTANCE ATTRIBUTES ======================================

    //============================== STATIC METHODS ===========================================

    //============================== CONSTRUCTORS =============================================

    //============================== ABSTRACT METHODS =========================================

    //============================== OVERRIDEN METHODS ========================================

    //============================== INSTANCE METHODS =========================================

    @Test
    public void testFindProjElem() throws Exception {
        Collection<IPerfModel> pms = new HashSet<IPerfModel>();
        PerfModel pm1 = new PerfModel("PM1_ID", "PM1_name", null, null, null);
        PerfModel pm2 = new PerfModel("PM2_ID", "PM2_name", null, null, null);
        PerfModel pm3 = new PerfModel("PM3_ID", "PM3_name", null, null, null);
        PerfModel pm4 = new PerfModel("PM4_ID", "PM4_name", null, null, null);
        PerfModel pm5 = new PerfModel("PM5_ID", "PM5_name", null, null, null);
        pms.addAll(Arrays.asList(pm1, pm2, pm3, pm4, pm5));

        assertEquals("Find by id error", pm1, ProjectUtils.findProjElem(pms, "PM1_ID"));
        assertEquals("Find by name error", pm2, ProjectUtils.findProjElem(pms, "PM2_name"));

        try {
            ProjectUtils.findProjElem(pms, "PM1_undefined");
            fail("Exception should have been thrown");
        }    catch (IllegalArgumentException ex) {
            //PRESENT
        }
    }

    //============================== PRIVATE METHODS ==========================================

    //============================== GETTERS / SETTERS ========================================

    //============================== INNER CLASSES ============================================

}
