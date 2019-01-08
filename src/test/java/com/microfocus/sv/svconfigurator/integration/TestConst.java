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
package com.microfocus.sv.svconfigurator.integration;

import java.io.File;
import java.net.URL;

import com.microfocus.sv.svconfigurator.resources.Resources;

public class TestConst {
    //============================== STATIC ATTRIBUTES ========================================

    public static final String MGMT_TST_URI_STR = "https://mpavm0276.hpeswlab.net:6085/management";
    public static final URL MGMT_TST_URI = createUrl(MGMT_TST_URI_STR);
    public static final String MGMT_TST_USERNAME = "SvServerAdmin";
    public static final String MGMT_TST_PASSWORD = "changeit";

    public static final String CLAIM_DEMO_REL_PATH = "test/claimDemo/Simulation.vproj";
    public static final String ENCRYPTED_PROJ_REL_PATH = "test/encryption/EncryptedServiceVirtualizationProject.vproj";
    public static final String SERVERS_PROPERTIES = "test/servers.properties";

    private static File CLAIM_DEMO_PROJECT_FILE = null;

    public static final String PROJ_NAME = "Simulation";
    public static final String PROJ_ID = "{666BAD4C-2790-4BC7-A2E4-A817F44223EF}";
    public static final String SVC_NAME = "Member Accounts";
    public static final String SVC_ID = "67242b01-3ed5-4127-aab1-639c953ca86c";
    public static final String SVC_DATA_MODEL = "Data Model";
    public static final String SVC_DATA_MODEL_ID = "7e84f205-806f-4fa5-9ce6-11b8ac44cfa1";
    public static final String SVC_PERF_MODEL = "Performance Model";
    public static final String SVC_PERF_MODEL_ID = "e80b8432-b21e-4398-b724-dcbcd35f94e5";


    public static final String SVC_MODE_SIMULATING = "Simulating";
    public static final String SVC_MODE_LEARNING = "Learning";
    public static final String SVC_MODE_STAND_BY = "StandBy";
    public static final String SVC_DEPLOY_READY = "Ready";

    public static final String CLI_MODE_SIMULATING = "SIMULATING";
    public static final String CLI_MODE_LEARNING = "LEARNING";
    public static final String CLI_MODE_STAND_BY = "STAND_BY";


    //============================== INSTANCE ATTRIBUTES ======================================

    //============================== STATIC METHODS ===========================================

    public static File getServersProperties() throws Exception {
        return Resources.getResource(SERVERS_PROPERTIES);
    }
    
    public static File getClaimDemoProject() throws Exception {
        return Resources.getResource(CLAIM_DEMO_REL_PATH);
    }
    
    private static URL createUrl(String mgmtTstUriStr) {
        try {
            return new URL(mgmtTstUriStr);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static File getEncryptionProject() {
        return Resources.getResource(ENCRYPTED_PROJ_REL_PATH);
    }

    //============================== CONSTRUCTORS =============================================

    //============================== ABSTRACT METHODS =========================================

    //============================== OVERRIDEN METHODS ========================================

    //============================== INSTANCE METHODS =========================================

    //============================== PRIVATE METHODS ==========================================

    //============================== GETTERS / SETTERS ========================================

    //============================== INNER CLASSES ============================================

}
