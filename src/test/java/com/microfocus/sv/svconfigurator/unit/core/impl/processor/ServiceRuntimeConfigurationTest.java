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
package com.microfocus.sv.svconfigurator.unit.core.impl.processor;

import com.microfocus.sv.svconfigurator.core.impl.Service;
import com.microfocus.sv.svconfigurator.core.impl.jaxb.ServiceRuntimeConfiguration;
import org.junit.Assert;
import org.junit.Test;

public class ServiceRuntimeConfigurationTest {


    //============================== STATIC ATTRIBUTES ========================================

    //============================== INSTANCE ATTRIBUTES ======================================

    //============================== STATIC METHODS ===========================================

    //============================== CONSTRUCTORS =============================================

    //============================== ABSTRACT METHODS =========================================

    //============================== OVERRIDEN METHODS ========================================

    //============================== INSTANCE METHODS =========================================

    @Test
    public void testCloneEquals() throws Exception {
        ServiceRuntimeConfiguration conf = new ServiceRuntimeConfiguration(new Service("xxx", "yyy"), null, false, null);
        ServiceRuntimeConfiguration conf2 = new ServiceRuntimeConfiguration(new Service("xxx", "yyy"), null, false, null);

        Assert.assertEquals(conf, conf2);
        //Todo: not done
    }

    @Test
    public void testGetDisplayRuntimeMode() throws Exception {
        ServiceRuntimeConfiguration sc = new ServiceRuntimeConfiguration(new Service("xxx", "yyy"),
                ServiceRuntimeConfiguration.RuntimeMode.LEARNING, false, ServiceRuntimeConfiguration.DeploymentState.READY);

        Assert.assertEquals(ServiceRuntimeConfiguration.RuntimeMode.LEARNING, sc.getDisplayRuntimeMode());

        sc.setRuntimeMode(ServiceRuntimeConfiguration.RuntimeMode.STAND_BY);
        Assert.assertEquals(ServiceRuntimeConfiguration.RuntimeMode.STAND_BY, sc.getDisplayRuntimeMode());

        sc.setPerfModelId("perfModel");
        Assert.assertEquals(ServiceRuntimeConfiguration.RuntimeMode.SIMULATING, sc.getDisplayRuntimeMode());

        sc.setPerfModelId(null);
        sc.setDataModelId("dataModel");
        Assert.assertEquals(ServiceRuntimeConfiguration.RuntimeMode.STAND_BY, sc.getDisplayRuntimeMode());
    }

    //============================== PRIVATE METHODS ==========================================

    //============================== GETTERS / SETTERS ========================================

    //============================== INNER CLASSES ============================================

}
