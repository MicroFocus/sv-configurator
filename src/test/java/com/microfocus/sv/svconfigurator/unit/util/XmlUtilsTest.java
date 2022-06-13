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
package com.microfocus.sv.svconfigurator.unit.util;

import com.microfocus.sv.svconfigurator.resources.Resources;
import com.microfocus.sv.svconfigurator.util.XmlUtils;
import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;

import java.io.InputStream;
import java.util.Collection;

public class XmlUtilsTest {


    //============================== STATIC ATTRIBUTES ========================================

    private static final String PROJ_FILE = "test/testProject.vproj";

    //============================== INSTANCE ATTRIBUTES ======================================

    //============================== STATIC METHODS ===========================================

    //============================== CONSTRUCTORS =============================================

    //============================== ABSTRACT METHODS =========================================

    //============================== OVERRIDEN METHODS ========================================

    //============================== INSTANCE METHODS =========================================

    @Test
    public void testEvalCollectionXpath() throws Exception {
        InputStream is = Resources.getResourceStream(PROJ_FILE);

        Document doc = XmlUtils.createDoc(is);
        Collection<String> col = XmlUtils.evalCollectionXpath("//*[local-name()='None']/@Include", doc);

        Assert.assertEquals(3, col.size());

        Assert.assertTrue(col.contains("IBM IMS TM Data Model Dataset 000.vsdataset"));
        Assert.assertTrue(col.contains("IBM IMS TM Data Model Dataset.vsdataset"));
        Assert.assertTrue(col.contains("IBM IMS TM Data Model.vsmodel"));
    }

    //============================== PRIVATE METHODS ==========================================

    //============================== GETTERS / SETTERS ========================================

    //============================== INNER CLASSES ============================================

}
