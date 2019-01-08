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
package com.microfocus.sv.svconfigurator.unit.core.impl.jaxb.atom;

import com.microfocus.sv.svconfigurator.core.impl.jaxb.atom.ElemModelListAtom;
import com.microfocus.sv.svconfigurator.serverclient.IJaxbProcessor;
import com.microfocus.sv.svconfigurator.serverclient.impl.JaxbProcessor;
import com.microfocus.sv.svconfigurator.resources.Resources;
import org.junit.Test;

import java.io.InputStream;

import static org.junit.Assert.assertEquals;

public class ElemModelListAtomTest {
    //============================== STATIC ATTRIBUTES ========================================

    //============================== INSTANCE ATTRIBUTES ======================================

    //============================== STATIC METHODS ===========================================

    //============================== CONSTRUCTORS =============================================

    //============================== ABSTRACT METHODS =========================================

    //============================== OVERRIDEN METHODS ========================================

    //============================== INSTANCE METHODS =========================================
    @Test
    public void testJaxb() throws Exception {
        InputStream is = Resources.getResourceStream("test/jaxb/perfModelListAtom.xml");
        IJaxbProcessor proc = new JaxbProcessor();
        ElemModelListAtom atom = proc.unmasrhall(is, ElemModelListAtom.class);

        assertEquals(2, atom.getEntries().size());
    }



    //============================== PRIVATE METHODS ==========================================

    //============================== GETTERS / SETTERS ========================================

    //============================== INNER CLASSES ============================================

}
