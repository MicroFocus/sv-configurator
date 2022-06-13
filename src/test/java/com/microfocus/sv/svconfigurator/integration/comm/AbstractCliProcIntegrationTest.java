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
package com.microfocus.sv.svconfigurator.integration.comm;

import com.microfocus.sv.svconfigurator.core.IService;
import com.microfocus.sv.svconfigurator.core.impl.Service;
import com.microfocus.sv.svconfigurator.core.impl.datasource.InexistingProjectElementDataSource;
import com.microfocus.sv.svconfigurator.core.impl.jaxb.atom.ServiceListAtom;
import com.microfocus.sv.svconfigurator.core.impl.processor.Credentials;
import com.microfocus.sv.svconfigurator.core.impl.processor.ElementStatus;
import com.microfocus.sv.svconfigurator.integration.TestConst;
import com.microfocus.sv.svconfigurator.serverclient.IServerManagementEndpointClient;
import com.microfocus.sv.svconfigurator.util.HttpUtils;

import static org.junit.Assert.fail;

public class AbstractCliProcIntegrationTest {
    protected static IServerManagementEndpointClient spc;

    protected static void clearAll() throws Exception {
        ServiceListAtom sla = spc.getServiceList(TestConst.PROJ_ID);
        for (ServiceListAtom.ServiceEntry se : sla.getEntries()) {
            spc.undeployService(se.getId());
        }
        
        int count = 1;
        int tryCnt = 0;
        do {
            tryCnt++;
            count = spc.getServiceList(TestConst.PROJ_ID).getEntries().size();
            
            if (tryCnt > 10) {
                throw new IllegalStateException("Services should have been undeployed by now...");
            }
        } while (count != 0);
    }

    public static void setUpClass() throws Exception {
        spc = HttpUtils.createServerManagementEndpointClient(TestConst.MGMT_TST_URI, true, new Credentials(TestConst.MGMT_TST_USERNAME, TestConst.MGMT_TST_PASSWORD));
    }

    protected IService createDummyService(String id) {
        return this.createDummyService(id, null);
    }

    private IService createDummyService(String id, String name) {
        return new Service(id, name, new InexistingProjectElementDataSource(), null, null, null, false);
    }

    protected void waitForNotPresent(IService svc) throws Exception {
        int retries = 0;
        while (retries < 5) {
            ElementStatus stat = spc.getServiceStatus(svc);
            if (stat.equals(ElementStatus.NOT_PRESENT)) {
                return;
            }

            retries++;
        }
        fail("Service did not delete.");
    }

}
